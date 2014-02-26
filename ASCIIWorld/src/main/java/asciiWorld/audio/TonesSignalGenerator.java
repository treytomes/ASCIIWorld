package asciiWorld.audio;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * This method generates a monaural tone consisting of the sum of three sinusoids.
 * 
 * @author Trey
 *
 */
public class TonesSignalGenerator {
	
	/**
	 * Each channel requires two 8-bit bytes per 16-bit sample.
	 */
	private static final int BYTES_PER_SAMPLE = 2;
	
	/**
	 * Arbitrary default frequency.
	 */
	private static final double DEFAULT_FREQUENCY = 950.0;
	private static final double TWO_PI = 2.0 * Math.PI;

	private byte[] _audioData;
	private double _frequency;
	private ShortBuffer _shortBuffer;
	
	public TonesSignalGenerator(byte[] audioData, double frequency) {
		_audioData = audioData;
		_frequency = frequency;
		_shortBuffer = ByteBuffer.wrap(_audioData).asShortBuffer();
	}
	
	public TonesSignalGenerator(byte[] synDataBuffer) {
		this(synDataBuffer, DEFAULT_FREQUENCY);
	}
	
	public double getFrequency() {
		return _frequency;
	}
	
	/**
	 * Java allows 1 or 2 channels.
	 * 
	 * @return
	 */
	public int getChannels() {
		return 1;
	}
	
	public float getSampleRate() {
		return 16000.0f;
	}
	
	public void generate() {
		for (int cnt = 0; cnt < getSampleLength(); cnt++){
			double time = cnt / getSampleRate();
			getBuffer().put((short)(getSampleRate() * signalFunction(time)));
		}
	}
	
	private double signalFunction(double time) {
		double freq = getFrequency();
		double sine1Value = Math.sin(TWO_PI * freq * time);
		double sine2Value = Math.sin(TWO_PI * (freq / 1.8) * time);
		double sine3Value = Math.sin(TWO_PI * (freq / 1.5) * time);
		return  (sine1Value + sine2Value + sine3Value) / 3.0;
	}
	
	private ShortBuffer getBuffer() {
		return _shortBuffer;
	}
	
	/**
	 * Allowable 8000,11025,16000,22050,44100.
	 * 
	 * @return
	 */
	private int getSampleLength() {
		return _audioData.length / BYTES_PER_SAMPLE;
	}
}
