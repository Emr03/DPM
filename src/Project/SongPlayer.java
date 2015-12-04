package Project;
import java.io.File;

import lejos.hardware.Sound;

public class SongPlayer extends Thread {

	boolean enabled = true;

	public void run() {
		while (enabled) {
			File Song = new File("Song.wav");
			Sound.playSample(Song, 100);
		}
	}
}
