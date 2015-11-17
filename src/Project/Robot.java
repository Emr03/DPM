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

	public static final EV3UltrasonicSensor usSensor_left = new EV3UltrasonicSensor(SensorPort.S2);
	public static final EV3UltrasonicSensor usSensor_right = new EV3UltrasonicSensor(SensorPort.S3);
	public static final EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S1);
	public static final EV3ColorSensor lightSensor = new EV3ColorSensor(SensorPort.S4); 

	public static final UltrasonicPoller usPoller_left = new UltrasonicPoller(usSensor_left.getDistanceMode());
	public static final UltrasonicPoller usPoller_right = new UltrasonicPoller(usSensor_right.getDistanceMode());
	public static final ColorSensorPoller colorPoller = new ColorSensorPoller(colorSensor);


	public static final Odometer odometer = new Odometer();
	public static final Navigation navigator = new Navigation();
	public static final Localization localizer = new Localization();
	public static final PathPlanner planner = new PathPlanner();
	public static final OdometryCorrection odoCorrector = new OdometryCorrection(); 
	
	public static final double left_radius = 2.1;
	public static final double right_radius = 2.1;
	public static final double wheel_base = 15.0;
	public static final double tile = 30.48; 
	public static final int ROTATE_SPEED = 80;           
	public static final int FORWARD_SPEED = 150;
<<<<<<< HEAD
=======

	public static final int start_corner =1;
	public static final double MyHome_lowerLeft[][] = new double[1][1];
	public static final double MyHome_upperRight[][] = new double[1][1];
	public static final double OppHome_lowerLeft[][] = new double[1][1];
	public static final double OppHome_upperRight[][] = new double[1][1];
	public static final int Opp_Color= 0;
	public static final int Home_Color=0;
>>>>>>> flagcapture-ver1
	
	public static  int start_corner;
	public static final int MyHome_lowerLeft[] = new int[2];
	public static final int MyHome_upperRight[] = new int[2];
	public static final int OppHome_lowerLeft[] = new int[2];
	public static final int OppHome_upperRight[] = new int[2];

	private enum State {
		LOCALIZE, NAVIGATE, AVOID, CAPTURE, FINISHED
	}
	
	private static State state = State.LOCALIZE;
	public static int flagColor;

	public static void main(String[] args) {
		// get info from wifi class
<<<<<<< HEAD
		//adjust coordinates from wifi class to point to middle of the tiles
		start_corner = 1; 
		
		usPoller_left.start(); 
		usPoller_right.start(); 
		odometer.start(); 
		odoCorrector.start(); 
=======

		// state machine loop
			localizer.begin();

		
>>>>>>> flagcapture-ver1
		
		localizer.begin(); 
		
		planner.setDestination(planner.getEntryPoints()); 
		planner.travel(); 
		
	}
	

}
