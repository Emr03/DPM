package Project;

//not a thread because it is called by the main method, and it should not move on until the destination is reached 
//it handles obstacle avoidance by itself. No interrupts needed

/**
 * 
 * @author DPM TEAM18
 * @version 1.0, 10 Nov 2015 Path planning class with obstacle avoidance
 *          capability
 */
class PathPlanner {

	// private static final int partition_size = 4;
	/**
	 * boolean flag to indicate whether destination is reached
	 */
	private boolean isReached = false;
	/**
	 * destination coordinate in cm
	 */
	private double dest_x, dest_y;
	/**
	 * current coordinate in cm
	 */
	private double current_x, current_y;
	/**
	 * current heading
	 */
	private HEADING current_H;
	/**
	 * next heading
	 */
	private HEADING new_H;

	enum HEADING {
		EAST, NORTH, WEST, SOUTH
	}

	int[] getEntryPoints() {
		// we already have the upper right and lower left corners stored in the
		// main class
		// the nearby corners would be upper left, and lower right
		int UR, LL, UL, LR;
		UR = Robot.OppHome_upperRight[0] - Robot.start_corner[0] + Robot.OppHome_upperRight[1] - Robot.start_corner[1];
		LL = Robot.OppHome_lowerLeft[0] - Robot.start_corner[0] + Robot.OppHome_lowerLeft[1] - Robot.start_corner[1];
		UL = Robot.OppHome_lowerLeft[0] - Robot.start_corner[0] + Robot.OppHome_upperRight[1] - Robot.start_corner[1];

		int entry_point[] = new int[2];

		// if wall on the right side
		if (UR < LL && Robot.OppHome_upperRight[0] == 11) {
			// entry point is upper left
			entry_point[0] = Robot.OppHome_lowerLeft[0];
			entry_point[1] = Robot.OppHome_upperRight[1];
		}

		// if wall on the top side
		else if (UR < LL && Robot.OppHome_upperRight[1] == 11) {
			// entry point is lower right
			entry_point[0] = Robot.OppHome_upperRight[0];
			entry_point[1] = Robot.OppHome_lowerLeft[1];
		}

		// if wall is on the left side
		else if (LL < UR && Robot.OppHome_lowerLeft[0] == -1) {
			// entry point is lower right
			entry_point[0] = Robot.OppHome_upperRight[0];
			entry_point[1] = Robot.OppHome_lowerLeft[1];
		}

		// if wall is on the bottom side
		else if (LL < UR && Robot.OppHome_lowerLeft[1] == -1) {
			// entry point is upper left
			entry_point[0] = Robot.OppHome_lowerLeft[0];
			entry_point[1] = Robot.OppHome_upperRight[1];
		}
		return entry_point;
	}

	/**
	 * sets the destination coordinates
	 * 
	 * @param x
	 *            x coordinate in cm
	 * @param y
	 *            y coordinate in cm
	 */

	void setDestination(double x, double y) {
		isReached = false;
		dest_x = x;
		dest_y = y;
	}

	/**
	 * path planning method which handles obstacle avoidance
	 */

	public void travel() {
		//0 is for east tile, 1 for north tile, 2 for west tile, 3 for south tile  
		// -1 -> obstacle, 0 -> previous tile, 1-> clear farther, 2 -> clear closer. 
		int[] tile_values = {1, 1, 1, 1};
		int best_heading = 0; 
		
		while (!isReached) { 
			
			//***Evaluate values of east and west tiles***//
			//if closer tile is on the east side
			if (dest_x - current_x > Robot.tile/2 && tile_values[0]>0) {
				tile_values[0] = 2; 

			}
			// if closer tile is on the west side
			else if (dest_x - current_x < -Robot.tile / 2 && tile_values[2] > 0) {
				tile_values[2] = 2;
			}
			// if purely vertical motion, assign horizontal tiles a value of 1
			else {
				tile_values[0] = 1;
				tile_values[2] = 1;
			}

			// ***Evaluate values of north and south tile***//
			// if closer tile is to the north
			if (dest_y - current_y > Robot.tile / 2 && tile_values[1] > 0) {
				tile_values[1] = 2;
			}
			// if closer tile is to the south
			else if (dest_y - current_y < -Robot.tile / 2 && tile_values[3] > 0) {
				tile_values[3] = 2;
			}
			// if purely horizontal motion, assign vertical tiles a value of 1
			else {
				tile_values[1] = 1;
				tile_values[3] = 1;
			}

			// find best heading, look for max value
			best_heading = findBestHeading(tile_values);
			new_H = getHeadingfromInt(best_heading);
			// turn to this "best heading"
			changeHeading();

			// if obstacle ahead, repeat the while loop.
			if (isObstacle()) {
				tile_values[new_H.ordinal()] = -1;
				continue;
			}

			// at this point, clear tile was found, and robot was turned towards
			// it
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

	private boolean isObstacle() {
		if (Robot.usPoller_left.getDistance() < Robot.tile || Robot.usPoller_right.getDistance() < Robot.tile) {
			return true;
		}

		else
			return false;
	}

	private int findBestHeading(int[] tile_values) {
		int max_index = 0;
		for (int i = 1; i < 4; i++) {
			if (tile_values[max_index] < tile_values[i]) {
				max_index = i;
			}
		}
		return max_index;
	}

	private HEADING getHeadingfromInt(int max_index) {
		HEADING heading;
		switch (max_index) {
		case 0:
			heading = HEADING.EAST;
			break;
		case 1:
			heading = HEADING.NORTH;
			break;
		case 2:
			heading = HEADING.WEST;
			break;
		case 3:
			heading = HEADING.SOUTH;
			break;

		default:
			heading = HEADING.EAST;
			break;
		}

		return heading;
	}

	/**
	 * Changes the robot's heading depending on planned path
	 */
	private void changeHeading() {
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

	 HEADING getHeading() {
		return current_H;
	}
	
	
}
