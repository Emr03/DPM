package project;

/**
 * 
 * @author Shia LaBlock implements simple odometry using the motor's tachometers
 */
class Odometer extends Thread {

	private static double x = 0, y = 0, theta = Math.PI / 2;

	/**
	 * distance covered by left wheel in cm 
	 */
	private static double distL;

	/**
	 * distance covered by right wheel in cm 
	 */
	private static double distR;
	
	/**
	 * displacement of center of rotation in cm 
	 */
	private static double deltaD;
	
	/**
	 * change in heading in radians
	 */
	private static double deltaT;
	
	/**
	 * displacement in x-direction in cm 
	 */
	private static double dx;
	
	/**
	 * displacement in y-direction in cm 
	 */
	private static double dy;
	
	private static int nowTachoL, nowTachoR, lastTachoL = 0, lastTachoR = 0;

	private static final long ODOMETER_PERIOD = 20;

	/** lock object for synchronization **/
	private Object lock = new Object();

	/**
	 * updates the robot's position and heading using the motor's tachometers
	 */
	public void run() {

		long updateStart, updateEnd;

		while (true) {

			updateStart = System.currentTimeMillis();

			// Get the current tacho values of the motors.
			nowTachoL = Robot.leftMotor.getTachoCount();
			nowTachoR = Robot.rightMotor.getTachoCount();

			// Calculate the distance traveled by each wheel.
			distL = Math.PI * Robot.left_radius * (nowTachoL - lastTachoL) / 180;
			distR = Math.PI * Robot.right_radius * (nowTachoR - lastTachoR) / 180;

			// Replace the last tacho values by current tacho values (for the
			// next turn).
			lastTachoL = nowTachoL;
			lastTachoR = nowTachoR;

			deltaD = (distL + distR) / 2;
			deltaT = (distR - distL) / Robot.wheel_base;

			synchronized (lock) {

				theta = theta + deltaT; // Update current orientation in radians
				// wrap around
				theta = theta - Math.floor(theta / (2 * Math.PI)) * 2 * Math.PI;

				// Calculate the displacement in each axis (trigonometry).
				dx = deltaD * Math.cos(theta);
				dy = deltaD * Math.sin(theta);

				// Update current position in each axis.
				x = x + dx;
				y = y + dy;

			}

			// This ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();

			if (updateEnd - updateStart < ODOMETER_PERIOD) {

				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {

					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread

				}

			}

		}

	}

	/**
	 * returns the coordinates in cm and the heading in radians
	 * 
	 * @param position
	 *            array of type double that stores coordinates and heading
	 */
	public void getPosition(double[] position) {

		// Ensure that the values don't change while the odometer is running
		synchronized (lock) {
			position[0] = x;
			position[1] = y;
			position[2] = theta;
		}

	}

	/**
	 * 
	 * @return current x-coordinate in cm
	 */
	public double getX() {
		double result;
		synchronized (lock) {
			result = x;
		}
		return result;
	}

	/**
	 * 
	 * @return current y-coordinate in cm
	 */
	public double getY() {

		double result;

		synchronized (lock) {
			result = y;
		}

		return result;

	}

	/**
	 * 
	 * @return current heading in radians
	 */
	public double getTheta() {

		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;

	}

	/**
	 * 
	 * @param position
	 *            array of type double that stores new coordinates and heading
	 * @param update
	 *            array of type boolean that indicates which values from
	 *            position[] to update
	 */
	public void setPosition(double[] position, boolean[] update) {

		// Ensure that the values don't change while the odometer is running
		synchronized (lock) {

			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];

		}

	}

	public void setX(double x) {

		synchronized (lock) {
			this.x = x;
		}

	}

	public void setY(double y) {

		synchronized (lock) {
			this.y = y;
		}

	}

	public void setTheta(double theta) {

		synchronized (lock) {
			this.theta = theta;
		}

	}

}
