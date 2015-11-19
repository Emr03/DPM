package Project;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import Project.Odometer;
import Project.UltrasonicPoller;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Localization CLass
 * 
 * This class is called to localize the robot on a grid.
 * 
 * 
 * @author DPM TEAM18
 * @version 1.0, 7 Nov 2015
 * 
 */

public class Localization {

	public enum LocalizationType {
		FALLING_EDGE, RISING_EDGE
	};

	public static int ROTATION_SPEED = 100;

	private Odometer odometer;
	private Navigation navigator;
	private LocalizationType locType;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private UltrasonicPoller usPoller_left, usPoller_right;

	double d = 34, k = 2;

	private int corner;

	public Localization() {

		this.corner = Robot.start_corner;
		this.odometer = Robot.odometer;
		this.navigator = Robot.navigator;
		this.locType = LocalizationType.FALLING_EDGE;
		this.leftMotor = Robot.leftMotor;
		this.rightMotor = Robot.rightMotor;
		this.usPoller_right = Robot.usPoller_right;

	}

	public void doLocalization() {

		double angleA, angleB;
		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);

		if (locType == LocalizationType.FALLING_EDGE) {

			// set robot to rotate clockwise for first falling edge
			leftMotor.forward();
			rightMotor.backward();
			angleA = detectFallingEdge();

			leftMotor.startSynchronization();
			leftMotor.stop();
			rightMotor.stop();
			leftMotor.endSynchronization();

			// Switch direction to counterclockwise
			leftMotor.backward();
			rightMotor.forward();
			angleB = detectFallingEdge();

			leftMotor.startSynchronization();
			leftMotor.stop();
			rightMotor.stop();
			leftMotor.endSynchronization();

			double deltaT = (angleA > angleB) ? 5 * Math.PI / 4 : Math.PI / 4;
			deltaT = deltaT - ((angleA + angleB) / 2);

			updateHeading(deltaT);
		}

		else {
			leftMotor.forward();
			rightMotor.backward();

			angleA = detectRisingEdge();

			leftMotor.startSynchronization();
			leftMotor.stop();
			rightMotor.stop();
			leftMotor.endSynchronization();

			leftMotor.backward();
			rightMotor.forward();

			angleB = detectRisingEdge();

			leftMotor.startSynchronization();
			leftMotor.stop();
			rightMotor.stop();
			leftMotor.endSynchronization();

			double deltaT = (angleA < angleB) ? 5 * Math.PI / 4 : Math.PI / 4;
			deltaT = deltaT - ((angleA + angleB) / 2);
			updateHeading(deltaT); 	
		}

		navigator.turnTo(0);

	}

	private void updateHeading(double deltaT) {
		switch (this.corner) {

		// Corner 1
		case 1:
			odometer.setPosition(new double[] { 0.0, 0.0, odometer.getTheta() + deltaT },
					new boolean[] { false, false, true });
			break;

		// depends if started facing or away
		// -90
		case 2:
			odometer.setPosition(new double[] { 0.0, 0.0, odometer.getTheta() + deltaT + Math.PI / 2 },
					new boolean[] { false, false, true });
			break;

		// Corner 3, same as 1 but minus 180
		case 3:
			odometer.setPosition(new double[] { 0.0, 0.0, odometer.getTheta() + deltaT + Math.PI },
					new boolean[] { false, false, true });
			break;

		// Corner 4 same as case 2 but minus 180
		// +90
		case 4:

			odometer.setPosition(new double[] { 0.0, 0.0, odometer.getTheta() + deltaT - Math.PI / 2 },
					new boolean[] { false, false, true });
			break;

		}
	}

	private double detectFallingEdge() {

		double dist; // variable to store reading of the ultrasonic sensor
		double angle = 0;
		double edge1 = 0, edge2 = 0;
		boolean falling_edge = false;
		dist = getFilteredData(usPoller_right);

		// rotate until it sees no wall
		while (dist < d + k)
			dist = getFilteredData(usPoller_right); // Get ultrasonic sensor
													// reading.

		Sound.beep();
		// keep rotating until it sees a wall
		while (dist >= d + k)
			dist = getFilteredData(usPoller_right); // Get ultrasonic sensor
													// reading.

		Sound.beep();
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
			if (dist <= d - k) {
				edge2 = odometer.getTheta();
				angle = (edge1 + edge2) / 2;
				falling_edge = true;
			}

		}

		return angle;

	}

	private double detectRisingEdge() {
		double dist; // variable to store reading of the ultrasonic sensor
		double angle = 0;
		double edge1 = 0, edge2 = 0;
		boolean rising_edge = false;
		dist = getFilteredData(usPoller_right);

		// rotate until it sees a wall
		while (dist > d + k)
			dist = getFilteredData(usPoller_right); // Get ultrasonic sensor
													// reading.

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

		Sound.beep();
		Sound.beep();
		Sound.beep();

		return angle;

	}

	public float getFilteredData(UltrasonicPoller usPoller) {
		// use filter control!!
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
