
package Project;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;




/**
 * 
 * @author Alina Mambo
 *
 */


public class Localization{
	public static double ROTATION_SPEED = 30;

	private Odometer odo;
	private SampleProvider usSensor;
	private float[] usData;
	private int corner;
	private Navigation navigate;
	private UltrasonicPoller usPoller_left;
	private UltrasonicPoller usPoller_right;


	private final int maxDist = 50;
	private final int d = 30;
	private final int k = 7;
	private final int width = 15;
	
	public Localization(Odometer odo,Navigation navigate, UltrasonicPoller usPoller_left, UltrasonicPoller usPoller_right) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.navigate=navigate;
		this.usPoller_left=usPoller_left;
		this.usPoller_right=usPoller_right;
	}
	
	
	/**
	 * @param
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
		navigate.turn(true);
		while(getFilteredData()<d+k);
		navigate.halt();
		angleB=odo.getAng();
		t.drawString("angleB=" +angleB, 0, 5);
		Sound.twoBeeps();
		
	
		if (angleA > angleB) {
			deltaTheta = 225 - (angleA + angleB)/2;
		} else {
			deltaTheta = 45 - (angleA + angleB)/2;
			}
		t.drawString("deltaTheta=" +deltaTheta, 0, 5);
		
		odo.setPosition(new double [] {0.0, 0.0, (odo.getAng()+deltaTheta)}, new boolean [] {false, false, true});
		navigate.turnTo(0.0,false);
		Sound.beep();
		
		
		
		
		
	}
	
	
	/**
	 * 
	 */
	
private float getFilteredData(UltrasonicPoller usPoller) {
		
		int distance = usPoller.getDistance();
		
		if (distance> 30){
			distance=maxDist;
		}
	
					
		return distance;
	}
	
	
	
	
	
	

}
