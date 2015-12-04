
package Project; 
import lejos.hardware.Button;
import lejos.hardware.Sound;


/**
 * 
 * This class decides the robot's next waypoint based on obstacles and odometry data
 * @author DPM TEAM18
 * @version 2.0, 25 Nov 2015 Path planning class with obstacle avoidance
 *          capability
 */
public class PathPlanner {

	// private static final int partition_size = 4;
	/**
	 * boolean flag to indicate whether destination is reached
	 */
	private boolean isReached;
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
	private HEADING current_H = HEADING.EAST;
	/**
	 * next heading
	 */
	private HEADING new_H;

	enum HEADING {
		EAST, NORTH, WEST, SOUTH
	}

	/**
	 * computes nearest entry point to search area
	 * @return coordinates of entry point in array of type double
	 */
	double[] getEntryPoints() {

		int left, right, top, bottom;
		left = Robot.MyHome_lowerLeft[0];
		bottom = Robot.MyHome_lowerLeft[1];
		right = Robot.MyHome_upperRight[0];
		top = Robot.MyHome_upperRight[1];

		// add another field
		double[] entry_point = new double[4];
		if (Math.abs(left - Robot.start_coord[0]) < Math.abs(right - Robot.start_coord[0])) {
			entry_point[0] = left * Robot.tile + (double) Robot.tile / 2;
			entry_point[2] = 1;
		} else {
			entry_point[0] = right * Robot.tile - (double) Robot.tile / 2;
			entry_point[2] = 0;
		}

		if (Math.abs(top - Robot.start_coord[1]) < Math.abs(bottom - Robot.start_coord[1])) {
			entry_point[1] = top * Robot.tile + (double) Robot.tile / 2;
			entry_point[3] = 1;
		} else {
			entry_point[1] = bottom * Robot.tile - (double) Robot.tile / 2;
			entry_point[3] = 0;
		}

//		System.out.println(entry_point[0] + "   " + entry_point[1]);
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

	void setDestination(double[] x_y) {
		isReached = false;
		dest_x = x_y[0];
		dest_y = x_y[1];
	}

	/**
	 * path planning method which handles obstacle avoidance
	 */
	void travel() {
		// 0 is for east tile, 1 for north tile, 2 for west tile, 3 for south
		// tile
		// -1 -> obstacle, 0 -> previous tile, 1-> clear farther, 2 -> clear
		// closer.
		int[] tile_values = { 1, 1, 1, 1 };
		int best_heading = 0;

//		System.out.println("destx: " + dest_x + "\n");
//		System.out.println("desty: " + dest_y + "\n");
		
		//move away from the walls
//		Robot.correction.start(); 
		double deltaX = (Robot.corner.getId() == 1 || Robot.corner.getId() == 4) ? Robot.tile : -Robot.tile; 
		double deltaY = (Robot.corner.getId() == 1 || Robot.corner.getId() == 2) ? Robot.tile : -Robot.tile; 
//		
		current_H = (Robot.corner.getId() == 1 || Robot.corner.getId() == 4) ? HEADING.EAST : HEADING.WEST; 
		Robot.navigator.travelTo(Robot.odometer.getX() + deltaX, Robot.odometer.getY()); 
		
		current_H = (Robot.corner.getId() == 1 || Robot.corner.getId() == 2) ? HEADING.NORTH : HEADING.SOUTH; 
		Robot.navigator.travelTo(Robot.odometer.getX(), Robot.odometer.getY() + deltaY); 
//		Robot.correction.disable(); 
//		
//		Button.waitForAnyEvent(); 
		
		while (!isReached) {

			current_x = Robot.odometer.getX();
			current_y = Robot.odometer.getY();

//			System.out.println("current x" + current_x + "\n");
//			System.out.println("current y" + current_y + "\n");

			// if they are within half a tile, we consider it reached

			if (Math.abs(current_x - dest_x) <= Robot.tile/2
					&& Math.abs(current_y - dest_y) <= Robot.tile/2) {
				isReached = true;
				//Sound.buzz();
				
				//Sound.buzz();
				//Sound.buzz();
				Robot.navigator.stopMotors();
				break;
			}

			if (dest_x - current_x > Robot.navigator.dist_tolerance && tile_values[0] > 0) {
				//Sound.twoBeeps();
				tile_values[0] = 2;
				tile_values[2] = (tile_values[2] > 0) ? 1 : tile_values[2];
			}
			// if closer tile is on the west side
			else if (dest_x - current_x < Robot.navigator.dist_tolerance && tile_values[2] > 0) {
				tile_values[2] = 2;
				tile_values[0] = (tile_values[0] > 0) ? 1 : tile_values[0];
			}
			// if purely vertical motion, assign horizontal tiles a value of 1
			else {
				tile_values[0] = (tile_values[0] > 0) ? 1 : tile_values[0];
				tile_values[2] = (tile_values[2] > 0) ? 1 : tile_values[2];
			}

			// ***Evaluate values of north and south tile***//
			// if closer tile is to the north
			if (dest_y - current_y > Robot.navigator.dist_tolerance && tile_values[1] > 0) {
				tile_values[1] = 2;
				tile_values[3] = (tile_values[3] > 0) ? 1 : tile_values[3];
			}
			// if closer tile is to the south
			else if (dest_y - current_y < Robot.navigator.dist_tolerance && tile_values[3] > 0) {
				tile_values[3] = 2;
				tile_values[1] = (tile_values[1] > 0) ? 1 : tile_values[1];
			}
			// if purely horizontal motion, assign vertical tiles a value of 1
			else {
				tile_values[1] = (tile_values[1] > 0) ? 1 : tile_values[1];
				tile_values[3] = (tile_values[3] > 0) ? 1 : tile_values[3];
			}

			// find best heading, look for max value
			best_heading = findBestHeading(tile_values);

			new_H = getHeadingfromInt(best_heading);

			// turn to this "best heading"
			changeHeading();

			if (isObstacle()) {
				tile_values[new_H.ordinal()] = -1;

				continue; // proceeds with while loop
			}

			// at this point, clear tile was found, and robot was turned towards
			// it
			
			
			//System.out.println(current_H);
			// reset the tile values 
			for (int i = 0; i < 4; i++) {
				//System.out.println(tile_values[i]);
					tile_values[i] = 1;
			}
			
			current_H = new_H;
			
			//evaluate displacement vector, and set previous tile value to 0 
			double[] displacement = { 0, 0 }; 
			switch (current_H) {
			case NORTH:
				displacement[1] = 1;
				tile_values[HEADING.SOUTH.ordinal()] = 0;
				break;
			case SOUTH:
				displacement[1] = -1;
				tile_values[HEADING.NORTH.ordinal()] = 0;
				break;
			case WEST:
				displacement[0] = -1;
				tile_values[HEADING.EAST.ordinal()] = 0;
				break;
			case EAST:
				displacement[0] = 1;
				tile_values[HEADING.WEST.ordinal()] = 0;
				break;
			}


			Robot.navigator.travelTo(Robot.odometer.getX() + displacement[0] * Robot.tile,
					Robot.odometer.getY() + displacement[1] * Robot.tile);
		}

		//Sound.beepSequenceUp();
		//Sound.beepSequenceUp();
	}

	/**
	 * determines whether there is an obstacle ahead
	 * @return returns true if obstacle ahead, false if otherwise
	 */
	private boolean isObstacle() {
		if ((Robot.usPoller_left.getDistance() < Robot.tile || Robot.usPoller_right.getDistance() < Robot.tile)) {
			//Sound.beepSequence();
			return true;
		}

		else
			return false;
	}

	/**
	 * Finds the tile with the maximum value
	 * @param tile_values 
	 * @return ordinal of best heading
	 */
	private int findBestHeading(int[] tile_values) {
		int max_index = 0;
		for (int i = 1; i < 4; i++) {
			if (tile_values[max_index] < tile_values[i]) {
				max_index = i;
			}
		}
		return max_index;
	}

/**
 * returns the heading corresponding to index
 * @param index the ordianal of the enum HEADING
 * @return the HEADING corresponding to index
 */
	private HEADING getHeadingfromInt(int index) {
		HEADING heading;
		switch (index) {
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
	}

	/**
	 * returns the robot's current heading
	 * @return HEADING
	 */
	HEADING getHeading() {
		return current_H;
	}
}
