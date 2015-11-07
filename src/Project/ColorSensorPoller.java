package Project;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

public class ColorSensorPoller extends Thread{

	private SampleProvider colorValue;
	private float[] colorData;
	public float colorID;
	private Object lock;
	
	
	
	public ColorSensorPoller(EV3ColorSensor colorsensor) {
		this.colorValue=colorsensor.getMode("ColorID");
		this.colorData = new float[colorValue.sampleSize()];
		
	}
	
	
	public void run(){
		while(true){
			colorValue.fetchSample(colorData,0);
			synchronized(lock){
				this.colorID=(colorData[0]);
			}
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			} // Poor man's timed sampling
		}
	}
	
	public float getColor(){
		float result= this.colorID;
		synchronized(lock){
			return result;
		}
	}
	

}
