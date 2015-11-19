package Project;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class Robot {

	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));

	//public static final EV3UltrasonicSensor usSensor_left = new EV3UltrasonicSensor(SensorPort.S2);
	public static final EV3UltrasonicSensor usSensor_right = new EV3UltrasonicSensor(SensorPort.S3);
	public static final EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S1);
	public static final EV3ColorSensor lightSensor = new EV3ColorSensor(SensorPort.S4); 

	//public static final UltrasonicPoller usPoller_left = new UltrasonicPoller(usSensor_left.getDistanceMode());
	public static final UltrasonicPoller usPoller_right=new UltrasonicPoller(usSensor_right.getDistanceMode());
	public static final ColorSensorPoller colorPoller = new ColorSensorPoller(colorSensor);


	public static final Odometer odometer = new Odometer();
	public static final Navigation navigator = new Navigation();
	public static final Localization localizer = new Localization();
	public static final PathPlanner planner = new PathPlanner();
	public static final OdometryCorrection odoCorrector = new OdometryCorrection(); 
	
	public static final double left_radius = 2.01;
	public static final double right_radius = 2.01;
	public static final double wheel_base = 15.7;
	public static final double tile = 30.48; 
	public static final int ROTATE_SPEED = 100;           
	public static final int FORWARD_SPEED = 150;
	
	public static  int start_corner;
	public static final int MyHome_lowerLeft[] = new int[2];
	public static final int MyHome_upperRight[] = new int[2];
	public static final int OppHome_lowerLeft[] = new int[2];
	public static final int OppHome_upperRight[] = new int[2];
	public static final int Opp_Color=1;
	public static final int Home_Color=1;
	
	
	private enum State {
		LOCALIZE, NAVIGATE, AVOID, CAPTURE, FINISHED
	}
	
	private static State state = State.LOCALIZE;
	public static int flagColor;

	public static void main(String[] args) {
		// get info from wifi class
		//adjust coordinates from wifi class to point to middle of the tiles
		
		start_corner = 2; 
		
		int[] start_coord = {0, 0};  
		//usPoller_left.start(); 
		usPoller_right.start(); 
		odometer.start(); 
		LCDInfo lcd = new LCDInfo(odometer);
		//odoCorrector.start(); 
//		navigator.turnTo(0);
		localizer.doLocalization(); 
		
		planner.setDestination(new int[]{4, 6}); 
		planner.travel(); 
		
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE); 
		System.exit(0); 
		//Hello World!
	}
	
	

}

