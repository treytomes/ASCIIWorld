package asciiWorld.math;

import asciiWorld.math.IRandom;

public class RandomFactory {
	
	private static RandomFactory _instance = null;

	private Class<?> _randomType;
	private IRandom _random;
	private long _seed;

	private RandomFactory()
			throws Exception {
		_randomType = DefaultRandom.class;
		try {
			_random = (IRandom)_randomType.newInstance();
		} catch (Exception e) {
			throw new Exception("Unable to create the random number generator.", e);
		}
		
	}
	
	public static RandomFactory get() {
		if (_instance == null) {
			try {
				_instance = new RandomFactory();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return _instance;
	}
	
	public IRandom getRandom() {
		return _random;
	}
	
	public long getSeed() {
		return _seed;
	}
	
	public void reseed(long seed)
			throws Exception {
		_seed = seed;
		try {
			_random = (IRandom)_randomType.getConstructor(Long.class).newInstance(_seed);
		} catch (Exception e) {
			throw new Exception("Unable to create the random number generator.", e);
		}
	}
	
	public <TRandom extends IRandom> void use(Class<TRandom> randomType)
			throws Exception {
		_randomType = randomType;
		reseed(_seed);
	}
	
	public int nextInt(int inclusiveMinValue, int exclusiveMaxValue) {
		return _random.nextInt(inclusiveMinValue, exclusiveMaxValue);
	}
	
	public double nextDouble() {
		return _random.nextDouble();
	}
}
