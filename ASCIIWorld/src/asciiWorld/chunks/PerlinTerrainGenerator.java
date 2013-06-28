package asciiWorld.chunks;

public class PerlinTerrainGenerator implements ITerrainGenerator {

	private double _persistence;
	private double _frequency;
	private double _amplitude;
	private int _octaves;
	private long _randomSeed;

	public PerlinTerrainGenerator(double persistence, double frequency, double amplitude, int octaves, long randomSeed)
	{
		setPersistence(persistence);
		setFrequency(frequency);
		setAmplitude(amplitude);
		setOctaves(octaves);
		setRandomSeed(randomSeed);
	}
	
	public PerlinTerrainGenerator(double persistence, double frequency, double amplitude, int octaves) {
		this(persistence, frequency, amplitude, octaves, 0);
	}
	
	public PerlinTerrainGenerator(double persistence, double frequency, double amplitude) {
		this(persistence, frequency, amplitude, 0);
	}
	
	public PerlinTerrainGenerator(double persistence, double frequency) {
		this(persistence, frequency, 0);
	}
	
	public PerlinTerrainGenerator(double persistence) {
		this(persistence, 0);
	}
	
	public PerlinTerrainGenerator() {
		this(0);
	}
	
	public double getPersistence() {
		return _persistence;
	}
	
	public void setPersistence(double value) {
		_persistence = value;
	}
	
	public double getFrequency() {
		return _frequency;
	}
	
	public void setFrequency(double value) {
		_frequency = value;
	}
	
	public double getAmplitude() {
		return _amplitude;
	}
	
	public void setAmplitude(double value) {
		_amplitude = value;
	}
	
	public int getOctaves() {
		return _octaves;
	}
	
	public void setOctaves(int value) {
		_octaves = value;
	}
	
	public long getRandomSeed() {
		return _randomSeed;
	}
	
	public void setRandomSeed(long value) {
		_randomSeed = 2 + value * value;
	}

	@Override
	public double[][] generate(int width, int height, int xPage, int yPage) throws Exception {
		int xOffset = width * xPage;
		int yOffset = height * yPage;
		
		double[][] terrain = new double[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				terrain[y][x] = getHeight(x + xOffset, y + yOffset);
			}
		}
		return terrain;
	}

	@Override
	public double getHeight(double x, double y)
			throws Exception {
		return getAmplitude() * total(x, y);
	}

	private double total(double i, double j) throws Exception {
		// Properties of 1 octave (changing each loop).
		double t = 0.0;
		double amplitude = 1.0;
		double freq = getFrequency();

		for (int k = 0; k < _octaves; k++) {
			t += getValue(j * freq + getRandomSeed(), i * freq + getRandomSeed()) * amplitude;
			amplitude *= getPersistence();
			freq *= 2;
		}

		return t;
	}

	private double getValue(double x, double y)
			throws Exception {
		int Xint = (int)x;
		int Yint = (int)y;
		double Xfrac = x - Xint;
		double Yfrac = y - Yint;

		// Noise values.
		double n01 = noise(Xint - 1, Yint - 1);
		double n02 = noise(Xint + 1, Yint - 1);
		double n03 = noise(Xint - 1, Yint + 1);
		double n04 = noise(Xint + 1, Yint + 1);
		double n05 = noise(Xint - 1, Yint);
		double n06 = noise(Xint + 1, Yint);
		double n07 = noise(Xint, Yint - 1);
		double n08 = noise(Xint, Yint + 1);
		double n09 = noise(Xint, Yint);

		double n12 = noise(Xint + 2, Yint - 1);
		double n14 = noise(Xint + 2, Yint + 1);
		double n16 = noise(Xint + 2, Yint);

		double n23 = noise(Xint - 1, Yint + 2);
		double n24 = noise(Xint + 1, Yint + 2);
		double n28 = noise(Xint, Yint + 2);

		double n34 = noise(Xint + 2, Yint + 2);

		// Find the noise values of the four corners.
		double x0y0 = 0.0625 * (n01 + n02 + n03 + n04) + 0.125 * (n05 + n06 + n07 + n08) + 0.25 * (n09);
		double x1y0 = 0.0625 * (n07 + n12 + n08 + n14) + 0.125 * (n09 + n16 + n02 + n04) + 0.25 * (n06);
		double x0y1 = 0.0625 * (n05 + n06 + n23 + n24) + 0.125 * (n03 + n04 + n09 + n28) + 0.25 * (n08);
		double x1y1 = 0.0625 * (n09 + n16 + n28 + n34) + 0.125 * (n08 + n14 + n06 + n24) + 0.25 * (n04);

		// Interpolate between those values according to the x and y fractions.
		double v1 = interpolate(x0y0, x1y0, Xfrac); // interpolate in x direction (y)
		double v2 = interpolate(x0y1, x1y1, Xfrac); // interpolate in x direction (y+1)
		double fin = interpolate(v1, v2, Yfrac);    // interpolate in y direction

		return fin;
	}

	private double noise(int x, int y)
			throws Exception {
		int n = x + y * 57;
		n = (n << 13) ^ n;
		int t = (n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff;
		return 1.0 - (double)t * 0.931322574615478515625e-9; /// 1073741824.0);
	}

	private double interpolate(double x, double y, double a) {
		double negA = 1.0 - a;
		double negASqr = negA * negA;
		double fac1 = 3.0 * (negASqr) - 2.0 * (negASqr * negA);
		double aSqr = a * a;
		double fac2 = 3.0 * aSqr - 2.0 * (aSqr * a);

		return x * fac1 + y * fac2; // add the weighted factors
	}
}
