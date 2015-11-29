

import lejos.hardware.Sound;

//not a thread because it is called by the main method, and it should not move on until the destination is reached 
//it handles obstacle avoidance by itself. No interrupts needed

/**
 * 
 * @author DPM TEAM18
 * @version 2.0, 25 Nov 2015 
 * Path planning class with obstacle avoidance capability
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

	double[] getEntryPoints() {
	
		int left, right, top, bottom; 
		left = Robot.MyHome_lowerLeft[0]; 
		bottom = Robot.MyHome_lowerLeft[1]; 
		right = Robot.MyHome_upperRight[0]; 
		top = Robot.MyHome_upperRight[1]; 
		
		//add another field
		double[] entry_point = new double[4]; 
		if (Math.abs(left - Robot.start_coord[0]) < Math.abs(right - Robot.start_coord[0])){
			entry_point[0] = left*Robot.tile + (double)Robot.tile/2; 
			entry_point[2] = 1; 
		}
		else {
			entry_point[0] = right*Robot.tile - (double)Robot.tile/2; 
			entry_point[2] = 0; 
		}
		
		if (Math.abs(top - Robot.start_coord[1]) < Math.abs(bottom - Robot.start_coord[1])){
			entry_point[1] = top*Robot.tile + (double)Robot.tile/2;  
			entry_point[3] = 1; 
		}
		else {
			entry_point[1] = bottom*Robot.tile - (double)Robot.tile/2; 
			entry_point[3] = 0; 
		}
		
		
		System.out.println(entry_point[0] + "   " + entry_point[1]);
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

//		System.out.println("destx: " + dest_x);
//		System.out.println("desty: " + dest_y);
		
		while (!isReached) {

			current_x = Robot.odometer.getX(); 
			current_y = Robot.odometer.getY(); 

//			System.out.println("current x" + current_x);
//			System.out.println("current y" + current_y);
			
			//if they are within half a tile, we consider it reached

			if (current_x - dest_x <= 5 && current_y - dest_y <= 5 ){
				isReached = true; 
				break; 
			}
			
			//*****Evaluation of tile values cannot overwrite non-positive values*****////
			// ***Evaluate values of east and west tiles***//
			// if closer tile is on the east side
			if (Math.round(2*dest_x/(Robot.tile)) - Math.round(2*current_x/Robot.tile) > 0 && tile_values[0] > 0) {
				tile_values[0] = 2;
				tile_values[2] = (tile_values[2] > 0) ? 1 : tile_values[2]; 
			}
			// if closer tile is on the west side
			else if (Math.round(2*dest_x/Robot.tile) - Math.round(2*current_x/Robot.tile) < 0 && tile_values[2] > 0) {
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
			if (Math.round(2*dest_y/Robot.tile) - Math.round(2*current_y/Robot.tile) > 0 && tile_values[1] > 0) {
				tile_values[1] = 2;
				tile_values[3] = (tile_values[3] > 0) ? 1 : tile_values[3]; 
			}
			// if closer tile is to the south
			else if (Math.round(2*dest_y/Robot.tile) - Math.round(2*current_y/Robot.tile) < 0 && tile_values[3] > 0) {
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
			//at this point we turned to the best heading but did not travel forward yet
			// if obstacle ahead, repeat the while loop.
			if (isObstacle()) { 
				tile_values[new_H.ordinal()] = -1; 
				
				continue; //proceeds with while loop
			}

		
			// at this point, clear tile was found, and robot was turned towards it
			current_H = new_H; 
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

			//reset the tile values except for the last tile we were on
			for (int i = 0; i<4; i++){
				if (tile_values[i]!=0) {
					tile_values[i] = 1; 
				}
			}
			
			Robot.navigator.travelTo(Robot.odometer.getX() + displacement[0] * Robot.tile,
					Robot.odometer.getY() + displacement[1] * Robot.tile);
		}
			
		Sound.beepSequenceUp();
		Sound.beepSequenceUp(); 
	}

	private boolean isObstacle() {
		if ((Robot.usPoller_left.getDistance() < Robot.tile  ||  Robot.usPoller_right.getDistance() < Robot.tile)) {
			Sound.beepSequence();
//			System.out.println("left: " + Robot.usPoller_left.getDistance());
//			System.out.println("right: " + Robot.usPoller_right.getDistance());
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
	}

	 HEADING getHeading() {
		return current_H;
	}
}
