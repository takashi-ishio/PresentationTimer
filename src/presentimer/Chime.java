/*
 * Copyright (C) 2010 Takashi Ishio All Rights Reserved.
 * Licensed under the MIT license: 
 * http://www.opensource.org/licenses/mit-license.php
 */
package presentimer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * A class file to play sound data.
 * @author ishio
 */
public class Chime {

	private static int MAX_FILE_SIZE = 1024 * 1024;

	private boolean initialized;
	
	private String[] files = new String[] { "chime1.wav", "chime2.wav", "chime3.wav" };
	private AudioFormat[] audioFormat = new AudioFormat[files.length];
	private byte[][] alarmData = new byte[files.length][]; 
	private int[] alarmDataLength = new int[files.length];
	
	public Chime() {
		initialized = false;
		try {
			for (int i=0; i<files.length; ++i) {
				// BufferedInputStream is necessary to use getAudioInputStream
				BufferedInputStream stream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(files[i]));
			    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(stream);
			    audioFormat[i] = audioInputStream.getFormat();
	
			    alarmData[i] = new byte[MAX_FILE_SIZE]; 
		        alarmDataLength[i] = audioInputStream.read(alarmData[i], 0, alarmData[i].length);

		        audioInputStream.close();
			}		
			initialized = true;
		} catch (IOException e)  {
			System.err.println("Cannot load a audio file. " + e.getMessage());
		} catch (UnsupportedAudioFileException e) {
			System.err.println("Cannot load a audio file. " + e.getMessage());
		}
	}
	
	public boolean isAvailable() {
		return initialized;
	}

	public void playSound(final int alarmIndex) {
		if ((0 <= alarmIndex) && (alarmIndex < files.length)) {
			// Use a thread to play a chime because sound blocks a thread.
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
					    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat[alarmIndex]);
					    SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
					    line.open(audioFormat[alarmIndex]);
					    line.start();
					    line.write(alarmData[alarmIndex], 0, alarmDataLength[alarmIndex]);
					    line.drain();
					    line.close();
					} catch (LineUnavailableException e) {
						System.err.println("Cannot play a wave data. AlarmIndex = " + Integer.toString(alarmIndex));
					}
				}
			});
			th.start();
		}
	}

}
