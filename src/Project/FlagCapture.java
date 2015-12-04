package Project; 

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;

/**
 * 
 * FlagCapture Class includes search algorithm and grabber control
 * @author DPM TEAM18
 * @version 2.0, 25 Nov 2015
 *
 */
public class FlagCapture {

	public boolean isCaptured = false;
	private UltrasonicPoller usPoller_left;
	private UltrasonicPoller usPoller_right;
	public static final EV3LargeRegulatedMotor armMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	public static final EV3MediumRegulatedMotor grabberMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("C"));

	private int FLAG_SPEED = 50;


	private double[] search_start; // should be set to entry_point
	private CORNER search_corner;
	private double dist_error = 15;
	private double[][] Waypoint = new double[5][2]; // array of waypoints


	/**
	 * constructor sets the search area entry point
	 * calls getWaypoints() to set waypoints for search algorithm 
	 * @param entry_points
	 */
	public FlagCapture (double[] entry_points) {
		this.search_start = entry_points;
		setCorner(); // set entry corner type in constructor
		getWaypoints(); // get waypoints in constructor
		armMotor.setAcceleration(1000);
		grabberMotor.setAcceleration(1000);
		this.usPoller_right = Robot.usPoller_right;
		this.usPoller_left = Robot.usPoller_left;
	}

	/**
	 * 
	 * @author DPM TEAM18
	 * @version 2.0, 25 Nov 2015
	 *
	 */
	public enum CORNER {
		TL, TR, BL, BR
	}

/**
 * sets entry corner type to generate corresponding search path waypoints
 */
	private void setCorner() {
		// An array of each waypoint and its x&y position
		// if left
		if (search_start[2] == 1) {
			// if top
			if (search_start[3] == 1) {
				search_corner = CORNER.TL;
			} else
				search_corner = CORNER.BL;
			// if right
		} else if (search_start[2] == 0) {
			// if top
			if (search_start[3] == 1) {
				search_corner = CORNER.TR;
			} else
				search_corner = CORNER.BR;
		}
	}

/**
 * generates sequence of waypoints based on search area parameters and entry corner
 */
	private void getWaypoints() {
		switch (search_corner) {
		case BL:
			this.Waypoint[0][0] = search_start[0];
			this.Waypoint[0][1] = (search_start[1] + (3 * Robot.tile));
			this.Waypoint[1][0] = Waypoint[0][0] + Robot.tile / 2;
			this.Waypoint[1][1] = Waypoint[0][1];
			this.Waypoint[2][0] = Waypoint[1][0];
			this.Waypoint[2][1] = Waypoint[1][1] - (2.5 * Robot.tile);
			this.Waypoint[3][0] = Waypoint[2][0] + Robot.tile / 2;
			this.Waypoint[3][1] = Waypoint[2][1];
			this.Waypoint[4][0] = Waypoint[3][0];
			this.Waypoint[4][1] = Waypoint[3][1] + (2.5 * Robot.tile);
			break;
		case BR:
			this.Waypoint[0][0] = search_start[0];
			this.Waypoint[0][1] = (search_start[1] + (3 * Robot.tile));
			this.Waypoint[1][0] = Waypoint[0][0] - Robot.tile / 2;
			this.Waypoint[1][1] = Waypoint[0][1];
			this.Waypoint[2][0] = Waypoint[1][0];
			this.Waypoint[2][1] = Waypoint[1][1] - (2.5 * Robot.tile);
			this.Waypoint[3][0] = Waypoint[2][0] - Robot.tile / 2;
			this.Waypoint[3][1] = Waypoint[2][1];
			this.Waypoint[4][0] = Waypoint[3][0];
			this.Waypoint[4][1] = Waypoint[3][1] + (2.5 * Robot.tile);
			break;
		case TR:
			this.Waypoint[0][0] = search_start[0];
			this.Waypoint[0][1] = (search_start[1] - (3 * Robot.tile));
			this.Waypoint[1][0] = Waypoint[0][0] - Robot.tile / 2;
			this.Waypoint[1][1] = Waypoint[0][1];
			this.Waypoint[2][0] = Waypoint[1][0];
			this.Waypoint[2][1] = Waypoint[1][1] + (2.5 * Robot.tile);
			this.Waypoint[3][0] = Waypoint[2][0] - Robot.tile / 2;
			this.Waypoint[3][1] = Waypoint[2][1];
			this.Waypoint[4][0] = Waypoint[3][0];
			this.Waypoint[4][1] = Waypoint[3][1] - (2.5 * Robot.tile);
			break;

		case TL:
			this.Waypoint[0][0] = search_start[0];
			this.Waypoint[0][1] = (search_start[1] - (3 * Robot.tile));
			this.Waypoint[1][0] = Waypoint[0][0] + Robot.tile / 2;
			this.Waypoint[1][1] = Waypoint[0][1];
			this.Waypoint[2][0] = Waypoint[1][0];
			this.Waypoint[2][1] = Waypoint[1][1] + (2.5 * Robot.tile);
			this.Waypoint[3][0] = Waypoint[2][0] + Robot.tile / 2;
			this.Waypoint[3][1] = Waypoint[2][1];
			this.Waypoint[4][0] = Waypoint[3][0];
			this.Waypoint[4][1] = Waypoint[3][1] - (2.5 * Robot.tile);
			break;

		}
	}
	

	@SuppressWarnings("unused")
	private boolean isObstacle() {
		if ((usPoller_left.getDistance() <= 10 || usPoller_right.getDistance() <= 10)) {
			return true;
		} else
			return false;
	}

	/**
	 * Checks whether the waypoint referenced by index is reached
	 * @param index index of waypoint in Waypoint array
	 * @return returns true if waypoint denoted by paramter index is reached, false if otherwise
	 */
	private boolean atWaypoint(int index) {
//		System.out.println("X: " + Robot.odometer.getX() + " Y: " + Robot.odometer.getY() + "\n");
//		System.out.println(Waypoint[index][0] + "  " + Waypoint[index][1]);
		double deltaX = Waypoint[index][0] - Robot.odometer.getX();
		double deltaY = Waypoint[index][1] - Robot.odometer.getY();
//		System.out.println(" deltaX: " + deltaX + " deltaY: " + deltaY + "\n");
		if (Math.abs(deltaX) < dist_error && Math.abs(deltaY) < dist_error) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Search algorithm for finding and capturing the flag
	 * @return true if flag was captured, false if search was completed without finding the flag
	 */
	public boolean Search() {
		Robot.colorPoller.start(); 
	
		for (int i = 0; i < Waypoint.length; i++) {

			changeHeading(i);

			while (!atWaypoint(i) && !isCaptured) {
				forward(1); 
				while (Robot.colorPoller.getColor() >= 13 || Robot.colorPoller.getColor() == -1 || Robot.colorPoller.getColor() == 7)
					System.out.println(Robot.colorPoller.getColor());
				if (Robot.colorPoller.getColor() == (Robot.Opp_Color)) {
					isCaptured = true; 
					captureFlag();
					//Sound.beepSequence();
					break; 
				} else {
					System.out.println(Robot.colorPoller.getColor());
					GetOutTheWay();
				}
			}

			Robot.navigator.stopMotors();
			
			if (isCaptured)
				return true;

		}

		return false;
	}

	private void forward(int speed_flag) {
		if (speed_flag == 1) {
			Robot.leftMotor.setSpeed(Robot.FORWARD_SPEED);
			Robot.rightMotor.setSpeed(Robot.FORWARD_SPEED);

		} else if (speed_flag == 2) {
			Robot.leftMotor.setSpeed(FLAG_SPEED);
			Robot.rightMotor.setSpeed(FLAG_SPEED);
		}

		Robot.leftMotor.forward();
		Robot.rightMotor.forward();
	}

	/**
	 * determines Robot's next heading based on next waypoint. 
	 * @param index index of next waypoint in array Waypoint
	 */
	
	private void changeHeading(int index) {
		double delta_x = Waypoint[index][0] - Robot.odometer.getX();
		double delta_y = Waypoint[index][1] - Robot.odometer.getY();
		double theta = Robot.navigator.getArcTan(delta_x, delta_y);
		Robot.navigator.turnTo(theta);
	}

//	private void Investigate() throws InterruptedException {
//		//Sound.beepSequenceUp();
//		//if (Math.abs(odometer.getX() - Robot.grid_edge)  ==  || odometer.getY() )
//		forward(2);
//		while (Robot.colorPoller.getColor() >= 13 || Robot.colorPoller.getColor() == -1)
//			;
//		if (Robot.colorPoller.getColor() == (Robot.Opp_Color)) {
//			captureFlag();
//		} else {
//			GetOutTheWay();
//		}
//	}

	/**
	 * captures the flag with grabberMotor and armMotor
	 */
	private void captureFlag() {
		Robot.leftMotor.rotate(-100, true);
		Robot.rightMotor.rotate(-100, false);
		
		Robot.navigator.stopMotors();

		armMotor.setSpeed(120);
		grabberMotor.setSpeed(80);

		//open claw
		grabberMotor.rotate(-80);
		//lower arm
		armMotor.rotate(140);
		//close claw
		grabberMotor.setSpeed(100);
		grabberMotor.rotate(120); 
		
		//lift arm 
		armMotor.rotate(-120);
		
	}

	/**
	 * Moves non-flags out of the way
	 */
	private void GetOutTheWay()  {
		captureFlag();
		double heading = Robot.odometer.getTheta();
		if (search_corner == CORNER.BL || search_corner == CORNER.TL) {
			Robot.navigator.turnTo(Math.PI);
		} else {
			Robot.navigator.turnTo(0);
		}
	
		//Drop 
		armMotor.rotate(120); 
		//Open grabber
		grabberMotor.rotate(-120);
		//lift arm 
		armMotor.rotate(-120);
		//close grabber
		grabberMotor.rotate(80); 
		Robot.navigator.turnTo(heading);
	}

	
	/**
	 * Drops the flag at the drop zone
	 */
	private void DropTheMic() {
		
	}
}
