package Project;



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
		while (true) {
			us.fetchSample(usData, 0); // acquire data
			synchronized (lock){
				this.distance = (int) (usData[0] * 100); // extract from buffer,
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
	

}
