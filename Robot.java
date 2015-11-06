package Project;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class Robot {

	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3MediumRegulatedMotor armMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("C"));
	public static final EV3UltrasonicSensor usSensor_left = new EV3UltrasonicSensor(SensorPort.S2);
	public static final EV3UltrasonicSensor usSensor_right = new EV3UltrasonicSensor(SensorPort.S3);
	public static final EV3UltrasonicSensor usSensor_middle = new EV3UltrasonicSensor(SensorPort.S4);
	public static final EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S1);

	public static final UltrasonicPoller usPoller_left = new UltrasonicPoller(usSensor_left.getDistanceMode());
	public static final UltrasonicPoller usPoller_right = new UltrasonicPoller(usSensor_right.getDistanceMode());
	public static final UltrasonicPoller usPoller_middle = new UltrasonicPoller(usSensor_middle.getDistanceMode());
	public static final ColorSensorPoller colorPoller = new ColorSensorPoller(colorSensor);

	public static final double left_radius = 2.1;
	public static final double right_radius = 2.1;
	public static final double wheel_base = 15.0;

	private enum State {
		LOCALIZE, NAVIGATE, AVOID, CAPTURE, FINISHED
	}

	public static final int START = 4;
	public static final double MyHome[][] = new double[2][2];
	public static final double OppHome[][] = new double[2][2];
	public static final double finaldest[][] = new double[2][2]; 
	
	public static final Localization localizer = new Localization();
	public static final Odometer odometer = new Odometer();
	public static final Navigation navigator = new Navigation();
	public static final ObstacleAvoidance avoider = new ObstacleAvoidance();
	private static State state = State.LOCALIZE;

	void main() {
		// get info from wifi class

		// state machine loop
		while (state != State.FINISHED) {

		}
		
		//Notes: while in the navigate state, main checks distance from ultrasonic poller and interrupts navigation thread which calls avoid() 
		//sets state to AVOID (since avoid() is not a thread, it has an internal check on whether the coast is clear, if it is it returns true to main, and navigation resumes
		//to avoid as much complications as possible, we should think of main as the mediator between all the threads
			
		//Questions: 
		//what happens after interrupt code is executed? does the thread resume, or should we recall navigator.run() from main?
		//should avoid() be called from main? and have a flag isInterrupted for Navigator?
	}
}
