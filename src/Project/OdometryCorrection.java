package Project; 

import lejos.hardware.Sound;

/**
 * 
  * @author DPM TEAM18
 *@version 2.0, 25 Nov 2015
 * implements odometry correction by detecting grid lines
 */

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private double Line = 30;				// establishes the threshold of the sensor to delineate what values indicate crossing a line.
	private double deltaX, deltaY;
	private double Sensor_Dist = 13;				// establishes the distance of the sensor from the midpoint of the wheel axis.
	private int distLine = 15;
	
	
	private static final double d = 13.0; 
	private static final double detection_ratio = 0.9;

	private boolean enabled = true;

	/**
	 * detects grid lines and updates the odometer's appropriate coordinate
	 */
	public void run() {
		float last_reading;
		float now_reading;
		long correctionStart, correctionEnd;
		double new_position;
		float[] lightData = {0};

		Robot.lightSensor.getRedMode().fetchSample(lightData, 0);
		last_reading = lightData[0];
		double[] current_pos = new double[3];

		while (enabled) {
			correctionStart = System.currentTimeMillis();

			Robot.lightSensor.getRedMode().fetchSample(lightData, 0);
			now_reading = lightData[0];
			
			Robot.odometer.getPosition(current_pos);
			PathPlanner.HEADING heading = Robot.planner.getHeading();

			if (now_reading < detection_ratio *last_reading) {
				Sound.beep(); 
				deltaX = distLine - ((Robot.odometer.getX()-Sensor_Dist*Math.sin(Math.toRadians(Robot.odometer.getTheta()))))%Line;
				deltaY = distLine - ((Robot.odometer.getY()-Sensor_Dist*Math.cos(Math.toRadians(Robot.odometer.getTheta()))))%Line;
				
				
				if (Math.abs(deltaX) < Math.abs(deltaY)) {
					Robot.odometer.setX(Robot.odometer.getX() + deltaX);
				} else {
					Robot.odometer.setY(Robot.odometer.getY() + deltaY);
				}
				

			}

			last_reading = now_reading;
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD - (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}

	public void disable() {
		// TODO Auto-generated method stub
		enabled = false; 
	}

	/*
	public static double getSensorReading() {
		double result;
		result = (double) now_reading;
		return result;
	}
*/
}
