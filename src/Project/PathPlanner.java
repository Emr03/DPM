package Project;

//not a thread because it is called by the main method, and it should not move on until the destination is reached 
//it handles obstacle avoidance by itself. No interrupts needed

/**
 * 
 * @author DPM TEAM18
 *@version 1.0, 7 Nov 2015
 * Path planning class with obstacle avoidance capability
 */
class PathPlanner {

	// private static final int partition_size = 4;
	/**
	 * boolean flag to indicate whether destination is reached
	 */
	private static boolean isReached = false;
	/**
	 * destination coordinate in cm 
	 */
	private static double dest_x, dest_y;
	/**
	 * current coordinate in cm 
	 */
	private static double current_x, current_y;
	/**
	 * current heading 
	 */
	private static HEADING current_H;
	/**
	 * next heading
	 */
	private static HEADING new_H;

	enum HEADING {
		NORTH, SOUTH, WEST, EAST
	}

	/**
	 * sets the destination coordinates
	 * 
	 * @param x
	 *            x coordinate in cm
	 * @param y
	 *            y coordinate in cm
	 */
	public static void setDestination(double x, double y) {
		isReached = false;
		dest_x = x;
		dest_y = y;
	}

	/**
	 * path planning method which handles obstacle avoidance
	 */
	void travel() {
		while (!isReached) {
			current_x = Robot.odometer.getX();
			current_y = Robot.odometer.getY();

			if (dest_x - current_x > Robot.tile) {
				new_H = HEADING.EAST;
			} else if (dest_x - current_x < Robot.tile)
				new_H = HEADING.WEST;

			else {
				if (dest_y - current_y > Robot.tile) {
					new_H = HEADING.NORTH;
				} else if (dest_y - current_y < Robot.tile) {
					new_H = HEADING.SOUTH;
				} else
					isReached = true;
			}

			changeHeading();

			if (Robot.usPoller_left.getDistance() < 2 * Robot.tile
					|| Robot.usPoller_right.getDistance() < 2 * Robot.tile) {
				if (dest_y - current_y > Robot.tile) {
					new_H = HEADING.NORTH;
				}
				// *** to be completed, rest of choose heading routine
				// We might want to write the PathPlanner as a state machine
			}

			
			// at this point, final heading is chosen
			changeHeading();
			double[] displacement = { 0, 0 };
			switch (current_H) {
			case NORTH:
				displacement[1] = 1;
				break;
			case SOUTH:
				displacement[1] = -1;
				break;
			case WEST:
				displacement[0] = -1;
				break;
			case EAST:
				displacement[0] = 1;
				break;
			}

			Robot.navigator.travelTo(current_x + displacement[0] * Robot.tile,
					current_y + displacement[1] * Robot.tile);
		}
	}

	/**
	 * Changes the robot's heading depending on planned path
	 */
	private static void changeHeading() {
		double theta;
		switch (new_H) {
		case NORTH:
			theta = Math.PI / 2;
			break;
		case SOUTH:
			theta = 3 * Math.PI / 2;
			break;
		case WEST:
			theta = Math.PI;
			break;
		case EAST:
			theta = 0;
			break;
		default:
			return;
		}
		Robot.navigator.turnTo(theta);
		current_H = new_H;
	}

	
	public HEADING getHeading() {
		return current_H;
	}
}
