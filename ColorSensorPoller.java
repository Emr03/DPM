package Project;

import lejos.hardware.sensor.EV3ColorSensor;

public class ColorSensorPoller extends Thread{

	private SampleProvider colorValue;
	private float[] colorData;
	public float colorID;
	
	
	
	public ColorSensorPoller(EV3ColorSensor colorsensor) {
		this.colorValue=colorsensor.getMode("ColorID");
		this.colorData = new float[colorValue.sampleSize()];
		
	}
	
	
	public void run(){
		while(true){
			colorValue.fetchsample(colorData,0);
			synchronized(lock){
				this.colorID=(colorData[0]);
			}
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			} // Poor man's timed sampling
		}
	}
	
	public int getColor(){
		float result= this.colorID;
		synchronized(lock){
			return result();
		}
	}
	

}
