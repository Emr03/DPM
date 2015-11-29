

import java.io.IOException;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;


public class Robot {

	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));

    public static final EV3UltrasonicSensor usSensor_left = new EV3UltrasonicSensor(SensorPort.S2);
	public static final EV3UltrasonicSensor usSensor_right = new EV3UltrasonicSensor(SensorPort.S3);
	public static final EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S1);
	public static final EV3ColorSensor lightSensor = new EV3ColorSensor(SensorPort.S4); 
	
	public static final UltrasonicPoller usPoller_left = new UltrasonicPoller(usSensor_left.getDistanceMode());
	public static final UltrasonicPoller usPoller_right=new UltrasonicPoller(usSensor_right.getDistanceMode());
	public static final ColorSensorPoller colorPoller = new ColorSensorPoller(colorSensor); 


	public static final Odometer odometer = new Odometer();
	public static final Navigation navigator = new Navigation();
	public static final UsLocalization localizer = new UsLocalization();
	public static final PathPlanner planner = new PathPlanner();
	public static final OdometryCorrection correction = new OdometryCorrection(); 
	
	public static final double left_radius = 2.01;
	public static final double right_radius = 2.01;
	public static final double wheel_base = 15.0;
	public static final double tile = 30.48; 
	public static final int ROTATE_SPEED = 150;           
	public static final int FORWARD_SPEED = 200;
	
	//public static StartCorner corner;
	public static int MyHome_lowerLeft[] = new int[2];
	public static int MyHome_upperRight[] = new int[2];
	public static int OppHome_lowerLeft[] = new int[2];
	public static int OppHome_upperRight[] = new int[2];
	public static int Dropzone[]=new int[2];
	public static int Opp_Color=1;
	public static int Home_Color=2;
	static int start_coord[] = {0, 6};
	public static int corner = 4; 
	
	private enum State {
		LOCALIZE, NAVIGATE, AVOID, CAPTURE, FINISHED
	}
	
	private static State state = State.LOCALIZE;
	public static int flagColor;
	private static final String SERVER_IP = "192.168.10.200";
	private static final int TEAM_NUMBER = 18;

	public static void main(String[] args) throws InterruptedException {
	
		usPoller_left.start(); 
		usPoller_right.start(); 
		odometer.start(); 
		LCDInfo lcd = new LCDInfo(odometer);
//		
		leftMotor.setAcceleration(5000);
		rightMotor.setAcceleration(5000);
		
		MyHome_lowerLeft[0] = 2; 
		MyHome_lowerLeft[1] = -1; 
		MyHome_upperRight[0] = 4; 
		MyHome_upperRight[1] = 2; 
		
		
		//***Localization***//
//		localizer.doLocalization(); 	
//		LightLocalization lightLocalizer = new LightLocalization(); 
//		lightLocalizer.doLocalization(); 
//		navigator.turnTo(0);
	
		//***PathPlanner***//
//		correction.start(); 
		odometer.setX(start_coord[0]*tile - tile/2);
		odometer.setY(start_coord[1]*tile + tile/2);
		double[] entry_point = planner.getEntryPoints(); 
		planner.setDestination(entry_point); 
		planner.travel(); 
		
		//***FlagCapture***//
//		double[] entry_point = planner.getEntryPoints(); 
//		odometer.setTheta(3*Math.PI/2);
//		odometer.setX(entry_point[0]);
//		odometer.setY(entry_point[1]);
//		
//		Stuff IwannaWin = new Stuff(entry_point); 
//		try {
//			IwannaWin.Search();
//		} catch (InterruptedException e) {
//		
//			e.printStackTrace();
//		} 
	
		while (Button.waitForAnyPress() != Button.ID_ESCAPE); 
		System.exit(0); 
		//Hello World!
	}
	
	

}
