package asciiWorld.audio;

import java.io.ByteArrayInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Inner class to play back the data that was saved.
 * @author Trey
 *
 */
public class AudioPlaybackThread extends Thread {
	
	// Allowable 8000, 11025, 16000, 22050, 44100.
	private static final int SAMPLE_SIZE_INT_BITS = 16;
	
	// Allowable 1, 2.
	private static final boolean SIGNED = true;
	
	// Allowable true, false.
	private static final boolean BIG_ENDIAN = true;

	//This is a working buffer used to transfer the data between the AudioInputStream and the SourceDataLine.  The size is rather arbitrary.
	byte playBuffer[] = new byte[16384];
	
	private SourceDataLine sourceDataLine;
	private AudioFormat audioFormat;
	private AudioInputStream audioInputStream;
	
	public AudioPlaybackThread(SignalGenerator signalGenerator) {
		// Get the required audio format.
		this.audioFormat = new AudioFormat(signalGenerator.getSampleRate(), SAMPLE_SIZE_INT_BITS, signalGenerator.getChannels(), SIGNED, BIG_ENDIAN);

		// Get an audio input stream from the ByteArrayInputStream.
		this.audioInputStream = new AudioInputStream(new ByteArrayInputStream(signalGenerator.getAudioData()), audioFormat, signalGenerator.getAudioData().length / audioFormat.getFrameSize());
		
		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat); // get info on the required data line

		// Get a SourceDataLine object.
		try {
			sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
      	} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		int cnt;

		try {
			// Open and start the SourceDataLine
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();
			
			//long startTime = new Date().getTime(); // get beginning of elapsed time for playback

			// Transfer the audio data to the speakers
			while ((cnt = audioInputStream.read(playBuffer, 0, playBuffer.length)) != -1) {
				// Keep looping until the input read method returns -1 for empty stream.
				if (cnt > 0) {
					// Write data to the internal buffer of the data line where it will be delivered to the speakers in real time
					sourceDataLine.write(playBuffer, 0, cnt);
				}
			}
			
			// Block and wait for internal buffer of the SourceDataLine to become empty.
			sourceDataLine.drain();
	
			//int elapsedTime = (int)(new Date().getTime() - startTime); // get and display the elapsed time for the previous playback
			
			//Finish with the SourceDataLine
			sourceDataLine.stop();
			sourceDataLine.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
