package project;

import Project.Odometer;
import Project.UltrasonicPoller;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;




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
	public static double ROTATION_SPEED = 30;

	private Odometer odo;
	private int corner;
	private UltrasonicPoller usPoller_left;
	private UltrasonicPoller usPoller_right;


	private final int maxDist = 50;
	private final int d = 30;
	private final int k = 7;
	private final int width = 15;
	
	/**
	 * Creates an Object of type Localization
	 * @param odo Odometer
	 * @param usPoller_left UltrasonicPoller
	 * @param usPoller_right UltrasonicPoller
	 * @param corner int
	 */
	
	public Localization(Odometer odo, UltrasonicPoller usPoller_left, UltrasonicPoller usPoller_right, int corner) {
		this.odo = odo;
		this.corner=corner;
		this.usPoller_left=usPoller_left;
		this.usPoller_right=usPoller_right;
	}
	
	
	/**
	 * Localizes the Robot using Rising edge method
	 *
	 * 
	 */
	
	public void Localization(){
		
		double angleA, angleB;
		double deltaTheta;
		
		
		// rotate the robot until it sees wall then stop
		navigate.turn(false);
		while(getFilteredData()>d-k);
		navigate.halt();
		
		
		//keep rotating till the wall disappears, then store the angle
		navigate.turn(false);
		while(getFilteredData()<d+k);
		navigate.halt();
		angleA=odo.getAng();
		t.drawString("angleA=" +angleA, 0, 5);
		Sound.twoBeeps();
		
		
		//switch directions and look for a wall
		navigate.turn(true);
		while(getFilteredData()>d-k);
		navigate.halt();
		
		
		//rotate clockwise till the wall disappears then store the angle
		turn(true);
		while(getFilteredData()<d+k);
		navigate.halt();
		angleB=odo.getAng();
		
	
		if (angleA > angleB) {
			deltaTheta = 225 - (angleA + angleB)/2;
		} else {
			deltaTheta = 45 - (angleA + angleB)/2;
			}
		
		odo.setPosition(new double [] {0.0, 0.0, (odo.getAng()+deltaTheta)}, new boolean [] {false, false, true});
		navigate.turnTo(0.0,false);
		
		
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
	
	
	
	
	
	

}

