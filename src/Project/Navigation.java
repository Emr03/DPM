package Project; 

/**
 * provides methods for controlling the robot's motion
 * @author DPM TEAM18
 * @version 2.0, 25 Nov 2015 
 */

public class Navigation {

	private static double currentX, currentY, currentT;

	private static double deltaX, deltaY, deltaT;

	/** indicates whether robot is navigating **/
	private boolean isNavigating = false; // boolean that is set to true while
											// the robot navigate s

	/** tolerated error in heading in radians **/
	public double theta_tolerance = 0.067;

	/** tolerated error in position in cm **/
	public int dist_tolerance = 1;

	/**
	 * moves the robot to coordinate (x, y)
	 * 
	 * @param x
	 *            x-coordinate in cm
	 * @param y
	 *            y-coordinate in cm
	 */
	public void travelTo(double x, double y) {

		currentX = Robot.odometer.getX();
		currentY = Robot.odometer.getY();

		// calculate the change required for each coordinate
		deltaX = x - currentX;
		deltaY = y - currentY;
		double targetT;

		Robot.leftMotor.setSpeed(Robot.FORWARD_SPEED);
		Robot.rightMotor.setSpeed(Robot.FORWARD_SPEED);

		while ((Math.abs(deltaX) > dist_tolerance || Math.abs(deltaY) > dist_tolerance)) {

			targetT = getArcTan(deltaX, deltaY);
			deltaT = getMinAngle(targetT - Robot.odometer.getTheta());
			if (Math.abs(deltaT) > Math.toRadians(5))
				adjustHeading(targetT); // Do the turning.

			double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			Robot.leftMotor.rotate(convertDistance(Robot.left_radius, distance), true);
			Robot.rightMotor.rotate(convertDistance(Robot.right_radius, distance), false);

			currentX = Robot.odometer.getX();
			currentY = Robot.odometer.getY();
			// System.out.println(currentX + "\t" + currentY + "\n");
			// calculate the change required for each coordinate
			deltaX = x - currentX;
			deltaY = y - currentY;

		}

		stopMotors();
		isNavigating = false;

	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double angle) {
		return convertDistance(radius, Math.PI * Robot.wheel_base * angle / 360.0);
	}

	/**
	 * returns the binary arctan by looking at dX and dY
	 * 
	 * @param dX
	 *            x displacement in cm
	 * @param dY
	 *            y displacement in cm
	 * @return binary arctan
	 */
	double getArcTan(double dX, double dY) {
		double T;
		if ((int) dX == 0)
			T = ((int) dY >= 0) ? Math.PI / 2 : -Math.PI / 2;

		else if ((int) dX < 0)
			T = Math.atan(dY / dX) + Math.PI;

		else
			T = Math.atan(dY / dX);

		return T;
	}

	/**
	 * turns the robot to an absolute heading
	 * 
	 * @param theta
	 *            in radians
	 */
	public void turnTo(double theta) {
		adjustHeading(theta);
		stopMotors();
	}

	/**
	 * adjusts heading while traveling a straight path
	 * 
	 * @param theta
	 *            in radians
	 */
	private void adjustHeading(double theta) {
		currentT = Robot.odometer.getTheta(); // get current heading
		deltaT = theta - currentT;

		deltaT = getMinAngle(deltaT);

		Robot.leftMotor.setSpeed(Robot.ROTATE_SPEED);
		Robot.rightMotor.setSpeed(Robot.ROTATE_SPEED);

		while (Math.abs(deltaT) > theta_tolerance) {

			currentT = Robot.odometer.getTheta(); // get current heading
			deltaT = theta - currentT;
			deltaT = getMinAngle(deltaT);
			//System.out.println("deltaT: " + deltaT);
			if (deltaT < 0) {
				Robot.leftMotor.rotate(convertAngle(Robot.left_radius, Math.toDegrees(Math.abs(deltaT))), true);
				Robot.rightMotor.rotate(-convertAngle(Robot.right_radius, Math.toDegrees(Math.abs(deltaT))), false);

			} else {
				Robot.leftMotor.rotate(-convertAngle(Robot.left_radius, Math.toDegrees(Math.abs(deltaT))), true);
				Robot.rightMotor.rotate(convertAngle(Robot.right_radius, Math.toDegrees(Math.abs(deltaT))), false);
			}

		}

	}

	/**
	 * stops motors synchronously
	 */
	public void stopMotors() {
		Robot.leftMotor.stop(true);
		Robot.rightMotor.stop(false);
	}

	/**
	 * computes the minimal angle needed for turning
	 * 
	 * @param angle
	 *            angular displacement in radians
	 * @return minimal angle in radians
	 */
	private double getMinAngle(double angle) {
		// set minimal theta
		if (angle > 0) {
			if (Math.abs(angle) > Math.abs(angle - 2 * Math.PI))
				angle = angle - 2 * Math.PI;
		}
		if (angle < 0) {
			if (Math.abs(angle) > Math.abs(angle + 2 * Math.PI))
				angle = angle + 2 * Math.PI;
		}

		return angle;
	}

	/**
	 * indicates whether the robot is navigating
	 * 
	 * @return boolean isNavigating
	 */
	public boolean isNavigating() {
		if (isNavigating)
			return true;
		else
			return false;
	}

}
