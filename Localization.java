
package Project;

import lejos.hardware.lcd.TextLCD;
import lejos.robotics.SampleProvider;
import localization.Navigation;
import localization.Odometer;
import localization.USLocalizer.LocalizationType;

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

/**
 * 
 * @author Alina Mambo
 *
 */


public class Localization  extends Thread{
	
	public USLocalizer(Odometer odo,Navigation navigate, UltrasonicPoller usPoller_left, UltrasonicPoller usPoller_right) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.navigate=navigate;
		this.usPoller_left=usPoller_left;
		this.usPoller_right=usPoller_right;
	}
	
	
	/**
	 * 
	 */
	
	public void run(){
		
		double angleA, angleB;
		double deltaTheta;
		
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	

}
