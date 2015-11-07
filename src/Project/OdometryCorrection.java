package Project;

import lejos.hardware.sensor.EV3ColorSensor;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private static final double d = 1.5; //less than actual distance between the color sensor and the middle of the wheelbase, to account for the delay in correction 
	private static Odometer odometer;  // odometer object
    private static EV3ColorSensor lightSensor;  // color sensor (light sensor) object
	
	// variables for storing readings from the light sensor (need two to compare)
	private static float last_reading;  
	private static float now_reading; 
	
	
	public OdometryCorrection(EV3ColorSensor lSensor) {
		// Assign odometer and color sensor objects.
		odometer = o;
		lightSensor=lSensor; 

	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd; // variables for ensuring the odometry correction occurs only once every period
		
		double new_position; //record robot's new coordinate that is being corrected (based on square).
		
		//initializing color sensor 

		float[] lsData = {0}; 	// Need an array to get the reading from the light sensor. 	
		
		lightSensor.getRedMode().fetchSample(lsData, 0);   //get initial sensor reading 
		last_reading=lsData[0];  // Store the initial reading as last_reading for comparison
		
		double[] now_coord=new double[4]; //stores the position returned by the odometer
	
		while (true) {
			correctionStart = System.currentTimeMillis();
			
			// put your correction code here
			
				lightSensor.getRedMode().fetchSample(lsData, 0); // Get reading from the light sensor.
				now_reading=lsData[0];                           // Store the reading.
				odometer.getPosition(now_coord);                 //retrieve the odometer reading

				
			if (now_reading <= 0.9*last_reading ){ //grid line detected (0.9 seems too high, but it was the best ratio for detecting faded lines)
				Sound.beep();  
				// Check which orientation (dir) to determine coordinate needs to be updated
				switch ((int)now_coord[3]){

				case 0: //positive y direction 
					new_position = (2*Math.floor( (now_coord[1]-d)/30.48 ) + 1)*15.24; 
					odometer.setY(new_position);
					break;
				
				case 1: //positive x direction 
					new_position = (2*Math.floor( (now_coord[0]-d)/30.48 ) + 1)*15.24;
					odometer.setX(new_position);
				    break; 
				
				case 2: //negative y direction 
					new_position = (2*Math.floor( (now_coord[1]+d)/30.48 ) + 1)*15.24;
				    odometer.setY(new_position);
				    break; 
				
				case 3: //negative x direction
					new_position = (2*Math.floor( (now_coord[0]+d)/30.48 ) + 1)*15.24;
					odometer.setX(new_position);   
					break;
				}//end of switch
				
			}//end of if dark line 
				
			last_reading=now_reading;  // Replace the last sensor reading by the current one (for the next turn).
				
			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}//end of if
		}//end of while 
	}//end of run
	
	//returns the sensor reading, to display on the LCD screen
	//helpful for debugging
	public static double getSensorReading(){ 
		double result; 
			result=(double) now_reading;
		return result; 
	}
	
}
