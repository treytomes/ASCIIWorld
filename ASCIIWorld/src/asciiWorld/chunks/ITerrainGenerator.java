package asciiWorld.chunks;

public interface ITerrainGenerator {

	double[][] generate(int width, int height) throws Exception;
	
	double getHeight(double x, double y) throws Exception;
}
