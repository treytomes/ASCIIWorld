package asciiWorld;

public interface IRandom {
	
	long getSeed();
	
	int nextInt();
	
	int nextInt(int exclusiveMaxValue);
	
	int nextInt(int inclusiveMinValue, int exclusiveMaxValue);
	
	double nextDouble();
}