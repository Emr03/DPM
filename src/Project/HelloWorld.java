package Project;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import lejos.hardware.Audio;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.BrickInfo;
import lejos.hardware.ev3.EV3;
import lejos.remote.ev3.RemoteEV3;
import lejos.hardware.LED; 

public class HelloWorld {

	/**
	 * @param args
	 * @throws NotBoundException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws NotBoundException, IOException {
		BrickInfo[] bricks = BrickFinder.discover();
		for(BrickInfo info: bricks) {
			   Brick brick = new RemoteEV3(info.getIPAddress());
			   brick.getAudio().systemSound(0);
				LED led= brick.getLED();
		        led.setPattern(1); 
			}
	}

}
