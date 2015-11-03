package Project;

import lejos.robotics.SampleProvider;

public class UltrasonicPoller extends Thread {
	private SampleProvider us;
	private float[] usData;
	public int distance;
	private Object lock = new Object();

	public UltrasonicPoller(SampleProvider us) {
		this.us = us;
		this.usData = new float[us.sampleSize()];
	}

	// Sensors now return floats using a uniform protocol.
	// Need to convert US result to an integer [0,255]

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

	public int getDistance() {
		int result = this.distance; 
		synchronized (lock){
			return result;
		}
	}
}

