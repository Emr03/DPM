



import lejos.robotics.SampleProvider;

/**
 * Ultrasonic Poller Class
 * This class polls the ultrasonic sensor for distances.
 * This class is implemented as a thread.
 * 
 * @author DPM TEAM 18
 * @version 1.0, 7 Nov 2015
 * 
 *
 */


public class UltrasonicPoller extends Thread {
	
	
	private SampleProvider us;
	private float[] usData;
	public int distance;
	private Object lock = new Object();
	private int filterControl = 0;
	private int FILTER_OUT = 2;
	

	/**
	 * Creates an Object of type UltrasonicPoller
	 * Creates and assigns an array of floats used to store the distance values.
	 * @param us is a SampleProvider
	 * 
	 */
	public UltrasonicPoller(SampleProvider us) {
		this.us = us;
		this.usData = new float[us.sampleSize()];
	}

	// Sensors now return floats using a uniform protocol.
	// Need to convert US result to an integer [0,255]

	/**
	 * This method is called when the thread is started 
	 * It acquires data from the sensor.
	 * 
	 * 
	 */
	
	
	public void run() {
		int d; 
		while (true) {
			us.fetchSample(usData, 0); // acquire data
			synchronized (lock){
				d = (int) (usData[0] * 100); // extract from buffer,
				this.distance = getFilteredData(d); 
			}	
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			} // Poor man's timed sampling
		}
	}

	
	/**
	 * This method updates the distances recognized by the UltraSonic sensor.
	 * @return int assigned the value of the distance seen by the US sensor.
	 */
	public int getDistance() {
		int result = this.distance; 
		synchronized (lock){
			return result;
		}
	}
	
	public int getFilteredData(int d) {
		// use filter control!!
		if(d >= 100 && filterControl < FILTER_OUT) {
			filterControl ++;
		} 
		
		// actual value, the distance is indeed greater than 100, so set the distance variable
		else if(d >= 100){
			this.distance = d;
		}
		
		// distance went below 100, therefore reset everything.
		else{
			filterControl = 0;
			this.distance = d;
		}
		
		
		return this.distance;
	}

}
