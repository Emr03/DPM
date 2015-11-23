package Project;

import java.util.concurrent.TimeUnit;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;

/**
 * Flag Capture CLass
 * 
 * This class is called to search and capture the Flag in the Opponent's Zone
 * 
 * @author DPM TEAM18
 * @version 1.0, 7 Nov 2015
 * 
 */


public class FlagCapture {
	
	private float flagColor;
	private boolean isCaptured;
	private int maxDist=15;
	private boolean mainPath= true;
	private UltrasonicPoller usPoller_left;
	private UltrasonicPoller usPoller_right;
	
	public static final EV3LargeRegulatedMotor armMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	public static final EV3MediumRegulatedMotor grabberMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B")); 
	//private int now_armTacho, last_armTacho; 
	private int ARM_SPEED = 80, GRAB_SPEED = 120;
	//angle in radians toward which to turn to throw non flags
	private double PHI ; 
	//entry tile in search routine
	private double [] search_start;
	// outer index of 2D Waypoint array
	private int x=0;
	//tolerated distance error from  waypoint
	private double dist_error= 1.5;
	
	
	
	/**
	 * Creates an Object of type FlagCapture
	 * @param flagColor float representing color of opponents flag
	 */
	
	public FlagCapture(float flagColor, double[] entry_points){
		this.flagColor=flagColor;
		this.isCaptured=false;
		this.search_start=entry_points;
		armMotor.setAcceleration(3000);

//		this.usPoller_left=Robot.usPoller_left;
		this.usPoller_right = Robot.usPoller_right;
		this.usPoller_left = Robot.usPoller_left; 
	}
	
	double[][] getWaypoints(){
		
		//An array of each waypoint and its x&y position
		double [][] Waypoint= new double[5][2];
		
		if(search_start)
		
		
		
	}
	/**
	 * This method searches for the Flag
	 * @return 
	 * 
	 */
	public void Search() throws InterruptedException{
	
		/*
		 * Remember to add a boolean to the odomertry correction thread and set it false here
		 * 
		 * Need to find a better way of returning to the initial pos on the Main search branch
		 * 
		 * may need to change the max dist for the check from the center line
		 * 
		 * 
		 */
		 
		while(!isCaptured){
			
			while(!atWaypoint(x))	{
					forward();
					while(!isObstacle());
					Investigate();				
			}					
										
		}
			
	}		
				
	private boolean isObstacle() {
		if ((Robot.usPoller_left.getDistance() < 10 ||  Robot.usPoller_right.getDistance() < 10)) {
			return true;
		}

		else
			return false;
	}
	
	private boolean atWaypoint(int x){
		double deltaX= Waypoint[x][0]-Robot.odometer.getX();
		double deltaY= Waypoint[x][1]-Robot.odometer.getY();
		if(Math.abs(deltaX)<dist_error && Math.abs(deltaY)<dist_error){
			this.x++;
			if(x%2==0){
				
				
			}
			return true;
		}
		else
			return false;
		
	}
	
	
	/*
	 * Notes on investigate: 
	 * 
	 * what to do if it is the wrong flag: throw it West or east depending
	 */
	 public void Investigate() throws InterruptedException{
		
		forward();
		while(Robot.colorPoller.getColor()>=14);
		if(Robot.colorPoller.getColor() == (Robot.Opp_Color)){
			captureFlag();					
		}
		else{
			if(mainPath){
				GetOutTheWay();		
				mainPath=false;
				}
				else{
					Robot.navigator.turnTo(180);
					forward(15);
				}
			}
	 }
	

	
	
	/**
	 * This method captures the Flag once found
	 * @return isCaptured Boolean that is true when Flag has been captured.
	 * @throws InterruptedException 
	 */
	public void captureFlag() throws InterruptedException{
		//if setAcceleration is not smooth enough, P-control of arm speed goes here (uses tachocounts)
		//rotate forward 120 degrees?
		armMotor.rotate(120); 
		grabberMotor.forward(); //keep applying torque
		TimeUnit.SECONDS.sleep(3); //give 3 seconds to grab before lifting
		armMotor.rotate(-120); 
		this.isCaptured=true;
	}
	
	public void GetOutTheWay() throws InterruptedException {
		captureFlag(); 
		Robot.navigator.turnTo(PHI ); 
		armMotor.rotate(120);
		grabberMotor.flt();
		armMotor.rotate(-120);
	}
	

	private void forward(){
		
		Robot.leftMotor.setSpeed(Robot.FORWARD_SPEED);
		Robot.rightMotor.setSpeed(Robot.FORWARD_SPEED);
		
		Robot.leftMotor.forward();
		Robot.rightMotor.forward();
		
	}

	
//	public  int convertDistance(double radius, double distance) {
//		return (int) ((180.0 * distance) / (Math.PI * radius));
//	}
//
//	public  int convertAngle(double radius, double width, double angle) {
//		return convertDistance(radius, Math.PI * width * angle / 360.0);
//	}
	

}
