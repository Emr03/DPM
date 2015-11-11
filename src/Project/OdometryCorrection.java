package Project;

import Project.PathPlanner.HEADING;

/**
 * 
  * @author DPM TEAM18
 *@version 1.0, 7 Nov 2015
 * implements odometry correction by detecting grid lines
 */

class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	
	//private static final double d = 1.5; 
	private static final double detection_ratio = 0.85;

	/**
	 * detects grid lines and updates the odometer's appropriate coordinate
	 */
	public void run() {
		float last_reading;
		float now_reading;
		long correctionStart, correctionEnd;

		double new_position;

		float[] lsData = { 0 };

		Robot.lightSensor.getRedMode().fetchSample(lsData, 0);
		last_reading = lsData[0];

		double[] current_pos = new double[3];

		while (true) {
			correctionStart = System.currentTimeMillis();

			Robot.lightSensor.getRedMode().fetchSample(lsData, 0);
			now_reading = lsData[0];
			
			Robot.odometer.getPosition(current_pos);
			HEADING heading = Robot.planner.getHeading();

			if (now_reading <= detection_ratio  * last_reading) {
				switch (heading) {

				case NORTH:
					new_position = (2 * Math.floor((current_pos[1]) / 30.48)) * 15.24;
					Robot.odometer.setY(new_position);
					break;

				case EAST:
					new_position = (2 * Math.floor((current_pos[0]) / 30.48)) * 15.24;
					Robot.odometer.setX(new_position);
					break;

				case SOUTH:
					new_position = (2 * Math.floor((current_pos[1]) / 30.48)) * 15.24;
					Robot.odometer.setY(new_position);
					break;

				case WEST:
					new_position = (2 * Math.floor((current_pos[0]) / 30.48)) * 15.24;
					Robot.odometer.setX(new_position);
					break;
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

	/*
	public static double getSensorReading() {
		double result;
		result = (double) now_reading;
		return result;
	}
*/
}
