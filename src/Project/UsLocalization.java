package Project;


import java.io.PrintWriter;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Localization CLass
 * 
 * This class is called to localize the robot on a grid.
 * 
 * 
 * @author DPM TEAM18
 * @version 2.0, 25 Nov 2015
 * 
 */
public class UsLocalization {

	public enum LocalizationType {
		FALLING_EDGE, RISING_EDGE
	};

	public static int ROTATION_SPEED = 80;

	private Odometer odometer;
	private Navigation navigator;
	private LocalizationType locType;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private UltrasonicPoller usPoller_right;
	 


	double d = 28, k = 2, offset = Math.toRadians(-7);

	private int corner;

	/**
	 * constructor assigns odometer, navigator, motors, ultrasonic poller and locType
	 */
	public UsLocalization() {

		this.odometer = Robot.odometer;
		this.navigator = Robot.navigator;
		this.locType = LocalizationType.FALLING_EDGE;
		this.leftMotor = Robot.leftMotor;
		this.rightMotor = Robot.rightMotor;
		this.usPoller_right = Robot.usPoller_right;
//
//		try {
//			writer = new PrintWriter(String.format("data%d.csv", System.currentTimeMillis()), "UTF-8");
//		} catch (FileNotFoundException | UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
	}

	/**
	 * performs ultrasonic localization using either the falling edge or rising edge method
	 */
	public void doLocalization() {

		this.corner = Robot.corner.getId();
		double angleA, angleB;
		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);

		if (locType == LocalizationType.FALLING_EDGE) {

			// set robot to rotate clockwise for first falling edge
			leftMotor.forward();
			rightMotor.backward();
			angleA = detectFallingEdge();

			navigator.stopMotors();

			// Switch direction to counterclockwise
			leftMotor.backward();
			rightMotor.forward();
			angleB = detectFallingEdge();

			navigator.stopMotors();

			double deltaT = (angleA > angleB) ? 5 * Math.PI / 4 : Math.PI / 4;
			deltaT = deltaT - ((angleA + angleB) / 2);
			deltaT = odometer.getTheta() + deltaT + offset;
			updateHeading(deltaT); // Update the current orientation.
		}

		else {
			leftMotor.forward();
			rightMotor.backward();

			angleA = detectRisingEdge();

			navigator.stopMotors();

			leftMotor.backward();
			rightMotor.forward();

			angleB = detectRisingEdge();

			navigator.stopMotors();

			double deltaT = (angleA < angleB) ? 5 * Math.PI / 4 : Math.PI / 4;
			deltaT = deltaT - ((angleA + angleB) / 2);
			deltaT = odometer.getTheta() + deltaT + offset;
			updateHeading(deltaT);
		}

//		writer.close(); 

	}

/**
 * detects a falling edge by analyzing ultrasonic sensor readings
 * @return heading in radians at which the falling edge was detected
 */
	private double detectFallingEdge() {

		double dist; // variable to store reading of the ultrasonic sensor
		double angle = 0;
		double edge1 = 0, edge2 = 0;
		boolean falling_edge = false;
		dist = getFilteredData(usPoller_right);

		// rotate until it sees no wall
		while (dist < d + k){
			Sound.buzz();
			dist = getFilteredData(usPoller_right); 
//			writer.write("seeing a wall , " + dist + "," + " \n");
		}										
 
		// keep rotating until it sees a wall
		while (dist >= d + k){
			dist = getFilteredData(usPoller_right); 
//			writer.write("looking for falling edge , " + dist + "," + " \n");
		}										

		Sound.beep();
//		writer.write("entered noise margin , " + dist + ", " + " \n");
		// it entered the noise margin
		if (dist < d + k && dist > d - k)
			edge1 = odometer.getTheta();

		// if sudden drop below noise margin
		else {
			falling_edge = true;
			angle = odometer.getTheta();
		}

		// if falling edge not yet detected
		while (!falling_edge) {
			dist = getFilteredData(usPoller_right);
//			writer.write("looking for falling edge , " + dist + "," + " \n");
			if (dist <= d - k) {
				edge2 = odometer.getTheta();
				angle = (edge1 + edge2) / 2;
				falling_edge = true;
			}

		}
//		writer.write("found falling edge , " + dist + ", " + " \n");
		Sound.twoBeeps();
		return angle;

	}

	/**
	 * detects a rising edge by analyzing ultrasonic sensor readings
	 * @return heading in radians at which the rising edge was detected
	 */
	private double detectRisingEdge() {
		double dist; // variable to store reading of the ultrasonic sensor
		double angle = 0;
		double edge1 = 0, edge2 = 0;
		boolean rising_edge = false;
		dist = getFilteredData(usPoller_right);

		// rotate until it sees a wall
		while (dist > d + k) 
			dist = getFilteredData(usPoller_right); 
												

		Sound.beep();
		// keep rotating until it sees a rising edge
		while (dist <= d + k)
			dist = getFilteredData(usPoller_right); // Get ultrasonic sensor
													// reading.

		// it entered the noise margin
		if (Math.abs(dist - d) < k)
			edge1 = odometer.getTheta();

		// if sudden rise above noise margin
		else {
			rising_edge = true;
			angle = odometer.getTheta();
		}

		Sound.beep();
		// if rising edge not yet detected
		while (!rising_edge) {
			dist = getFilteredData(usPoller_right);
			if (dist >= d + k) {
				edge2 = odometer.getTheta();
				angle = (edge1 + edge2) / 2;
				rising_edge = true;
			}

		}

		return angle;

	}

/**
 * updates the heading used by the odometer depending on the starting corner
 * @param deltaT
 */
	public void updateHeading(double deltaT) {
		System.out.println(Robot.corner);
		Sound.beepSequence();
		
		switch (this.corner) {

		// Corner 1
		case 1:
			odometer.setTheta(deltaT);
			break;

		// +90
		case 2:
			odometer.setTheta(deltaT + Math.PI / 2);
			break;

		// Corner 3, same as 1 but minus 180
		case 3:
			Sound.beepSequence();
			odometer.setTheta(deltaT + Math.PI);
			break;

		// Corner 4 same as case 2 but minus 180
		// +90
		case 4:
			odometer.setTheta(deltaT - Math.PI / 2);
			break;

		}
	}
	
	/**
	 * Filter that caps the distance read by the ultrasonic sensor to 50 cm
	 * @param usPoller
	 * @return capped ultrasonic sensor reading in cm 
	 */
	public float getFilteredData(UltrasonicPoller usPoller) {
		boolean largeDistanceDetected = false;
		int distance = usPoller.getDistance();
		if (distance > 50 && !largeDistanceDetected) {
			largeDistanceDetected = true;
			distance = usPoller.getDistance();
		}
		if (distance > 50 && largeDistanceDetected)
			distance = 50;

		return distance;
	}

}