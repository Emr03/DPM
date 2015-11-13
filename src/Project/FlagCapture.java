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
	
	public static final EV3LargeRegulatedMotor armMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	public static final EV3MediumRegulatedMotor grabberMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B")); 
	//private int now_armTacho, last_armTacho; 
	private int ARM_SPEED = 80, GRAB_SPEED = 120;
	//angle in radians toward which to turn to throw non flags
	private double PHI ; 
	//entry tile in search routine
	private int [] search_start; 
	
	/**
	 * Creates an Object of type FlagCapture
	 * @param flagColor float representing color of opponents flag
	 */
	
	public FlagCapture(float flagColor){
		this.flagColor=flagColor;
		this.isCaptured=false;
		armMotor.setAcceleration(3000);
		
	}
	
	
	/**
	 * This method searches for the Flag
	 * 
	 */
	public boolean Search(){
		
		
		return isCaptured;	
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
	}
	
	public void GetOutTheWay() throws InterruptedException {
		captureFlag(); 
		Robot.navigator.turnTo(PHI ); 
		armMotor.rotate(120);
		grabberMotor.flt();
		armMotor.rotate(-120);
	}
	
	
	

}
