package asciiWorld.math;

import java.util.Random;

public class DefaultRandom implements IRandom {

	private Random _random;
	private long _seed;
	
	public DefaultRandom(Long seed) {
		_seed = seed;
		_random = new Random(_seed);
	}
	
	public DefaultRandom() {
		this(System.currentTimeMillis());
	}
	
	public long getSeed() {
		return _seed;
	}
	
	public int nextInt() {
		return _random.nextInt();
	}
	
	public int nextInt(int exclusiveMaxValue) {
		return _random.nextInt(exclusiveMaxValue);
	}
	
	public int nextInt(int inclusiveMinValue, int exclusiveMaxValue) {
		return inclusiveMinValue + nextInt(exclusiveMaxValue - inclusiveMinValue);
	}
	
	public double nextDouble() {
		return _random.nextDouble();
	}
}