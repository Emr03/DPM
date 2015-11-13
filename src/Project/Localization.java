package Project;

import Project.Odometer;
import Project.UltrasonicPoller;
import lejos.hardware.Sound;






/**
 * Localization CLass
 * 
 * This class is called to localize the robot on a grid.
 * 
 * 
 * @author DPM TEAM18
 * @version 1.0, 7 Nov 2015
 * 
 */


public class Localization{
	public static int ROTATION_SPEED = 30;
	public enum LocalizationType { FACING_AWAY, FACING_WALL};

	private Odometer odo;
	private int corner=0 ;
	private UltrasonicPoller usPoller_left;
	private UltrasonicPoller usPoller_right;

	private LocalizationType locType;
	private final int maxDist = 50;
	private final int d = 30;
	private final int k = 7;
	
	/**
	 * Creates an Object of type Localization
	 * @param odo Odometer
	 * @param usPoller_left UltrasonicPoller
	 * @param usPoller_right UltrasonicPoller
	 * @param corner int
	 */
	
	public Localization() {
		this.odo = Robot.odometer;
		this.corner=Robot.start_corner;
		this.usPoller_left=Robot.usPoller_left;
		this.usPoller_right=Robot.usPoller_right;
	}
	
	
	
	public void begin(){
		
		if(getFilteredData(usPoller_left)<d-k && getFilteredData(usPoller_right )< d-k){
			locType=LocalizationType.FACING_WALL;
			doLocalization();
		}
		else{
			locType=LocalizationType.FACING_AWAY;
			doLocalization();
		}
		
	}
	
	
	/**
	 * Localizes the Robot using Rising edge method
	 *
	 * 
	 */
	
	public void doLocalization(){
		
		
		if(locType==LocalizationType.FACING_WALL){
			
			double angleA, angleB;
			
			// rotate the robot until it sees no wall then stop and latch
			//rising edge
			turn(false);
			while(getFilteredData(usPoller_left)<maxDist);
			halt();	
			angleA= Math.toDegrees(odo.getTheta());
			Sound.twoBeeps();
			
	
			// switch direction and wait until it sees no wall then stop and latch
			//falling edge
			turn(false);
			while(getFilteredData(usPoller_left)> d-k);
			halt();
			angleB=Math.toDegrees(odo.getTheta());
			Sound.twoBeeps();	
			
			
			computeAngle(angleA, angleB);
			
		}
		else{
		double angleA, angleB;
		
		
		// rotate the robot until it sees wall then stop and latch angle
		//Falling edge
		turn(true);
		while(getFilteredData(usPoller_right)>d-k);
		halt();
		angleA= Math.toDegrees(odo.getTheta());
		Sound.twoBeeps();
		
		
		//keep rotating till the wall disappears, then store the angle
		//Rising edge
		turn(true);
		while(getFilteredData(usPoller_right)<d+k);
		halt();
		angleB=Math.toDegrees(odo.getTheta());
		Sound.twoBeeps();
		
		
		computeAngle(angleA, angleB);
		
		}	
		
	}
	

	/**
	 * This method returns filtered data on the distances recored by the US Poller Class.
	 * @param usPoller UltrasonicPoller
	 * @return distance int
	 */
	private int getFilteredData(UltrasonicPoller usPoller) {
		
		int distance = usPoller.getDistance();
		
		if (distance> 30){
			distance=maxDist;
		}
	
					
		return distance;
	}
	
	private void computeAngle(double angleA, double angleB){
		
		double deltaTheta=0;
		deltaTheta=computeDeltaTheta(angleA,angleB);
		switch(this.corner){
		
		//Corner 1
		case 1:
			odo.setPosition(new double [] {0.0, 0.0, (Math.toDegrees(odo.getTheta())+deltaTheta)}, new boolean [] {false, false, true});
			break;
			
		//depends if started facing or away	
		//-90
		case 2:
			odo.setPosition(new double [] {0.0, 0.0, ((Math.toDegrees(odo.getTheta())+deltaTheta)+90)}, new boolean [] {false, false, true});
			break;
			
			
		//Corner 3, same as 1 but minus 180
		case 3:
			odo.setPosition(new double [] {0.0, 0.0, ((Math.toDegrees(odo.getTheta())+deltaTheta)+180)}, new boolean [] {false, false, true});
			break;
			
		//Corner 4 same as case 2 but minus 180
		//+90
		case 4:
			
			odo.setPosition(new double [] {0.0, 0.0, ((Math.toDegrees(odo.getTheta())+deltaTheta)-90)}, new boolean [] {false, false, true});
			break;
				
		}
		Robot.navigator.turnTo(0.0);
	}
	
	
	private double computeDeltaTheta(double angleA, double angleB){
		
		double deltaTheta;
		
		if (angleA > angleB) {
			deltaTheta = 225 - (angleA + angleB)/2;
		} else {
			deltaTheta = 45 - (angleA + angleB)/2;
			}
		
		return deltaTheta;
		
	}
	
	// turns the robot in a circle until stopped
	public void turn (boolean clockwise) {
		
		Robot.leftMotor.setSpeed(ROTATION_SPEED);
		Robot.rightMotor.setSpeed(ROTATION_SPEED);
			
		if (clockwise) {	
			Robot.leftMotor.forward();
			Robot.rightMotor.backward();
		} else {
			Robot.leftMotor.backward();
			Robot.rightMotor.forward();
		}
		
	}
	
	public void halt(){
		Robot.leftMotor.stop();
		Robot.rightMotor.stop();
	}
	
	
	

}

