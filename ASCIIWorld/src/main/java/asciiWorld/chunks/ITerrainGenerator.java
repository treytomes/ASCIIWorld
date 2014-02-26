package asciiWorld.chunks;

public interface ITerrainGenerator {

	double[][] generate(int width, int height, int xPage, int yPage) throws Exception;
	
	double getHeight(double x, double y) throws Exception;
}
