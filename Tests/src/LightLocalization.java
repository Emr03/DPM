
import java.util.concurrent.TimeUnit;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class LightLocalization {
	private Odometer odometer = Robot.odometer;
	private Navigation navigator = Robot.navigator;
	EV3LargeRegulatedMotor motorLeft = Robot.leftMotor;
	EV3LargeRegulatedMotor motorRight = Robot.rightMotor;
	private static final int SPEED = 100;
	private static final int ROTATE_SPEED = 150;
	private static final double distance_to_center = 14.5; 

	LightLocalization(){
		navigator = Robot.navigator; 
		odometer = Robot.odometer; 
		motorLeft = Robot.leftMotor; 
		motorRight = Robot.rightMotor; 
	}
	
	public void doLocalization() throws InterruptedException {
		double background;                   //last light sensor reading
		int grid_lines = 0;                  //counts the number of grid lines detected, index for angles[]
		double theta_y, theta_x;             //used to correct heading like in tutorial
		double[] angles =  new double[4];    //stores the odometer heading at every grid line
		float[] lightData = {0}; 
		
	    Robot.lightSensor.getRedMode().fetchSample(lightData, 0); 
		background= lightData[0]; 
	
		//position the robot near the corner
		
		motorLeft.setSpeed(SPEED);
		motorRight.setSpeed(SPEED);
		
		navigator.turnTo(Math.PI);
		Sound.beepSequence();
		motorLeft.backward(); 
		motorRight.backward(); 
		while (Robot.usPoller_right.getDistance() < 18); 
		navigator.stopMotors(); 
		
		navigator.turnTo(3*Math.PI/2); 
		Sound.beepSequenceUp();
		motorLeft.backward(); 
		motorRight.backward(); 
		while (Robot.usPoller_right.getDistance() < 18); 
		navigator.stopMotors(); 
		
		//ready to turn and correct heading 
		motorLeft.setSpeed(ROTATE_SPEED);
		motorRight.setSpeed(ROTATE_SPEED);
		
		//rotate until all 4 grid lines are detected 
		while (grid_lines < 4){
			//rotate clockwise
			motorLeft.forward(); 
			motorRight.backward(); 
			//get new sensor reading 
			Robot.lightSensor.getRedMode().fetchSample(lightData, 0); 
			//if grid line detected 
			if (lightData[0] <= 0.75*background){
				Sound.beep(); 
				angles[grid_lines] = odometer.getTheta(); 
				grid_lines++;  
				TimeUnit.MILLISECONDS.sleep(250);
			}
		}
			
		navigator.stopMotors(); 
		
		
		// do trig to compute (0,0) and 0 degrees
		theta_x = (angles[3] - angles[1] + 2*Math.PI); 
		theta_x = theta_x - theta_x/(2*Math.PI); 
		theta_y =  angles[0] - angles[2]; 
		
		odometer.setX(-distance_to_center*Math.cos(theta_y/2)); 
		odometer.setY(-distance_to_center*Math.cos(theta_x/2));
		
		double delta_theta = Math.PI - angles[2] - theta_y/2; 
		updateHeading(delta_theta + odometer.getTheta()); 

	}

	public void updateHeading(double deltaT) {
		System.out.println(Robot.corner);
		Sound.beepSequence();
		
		switch (Robot.corner) {

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


}
