package asciiWorld.math;


public class EuclideanDistanceAlgorithm implements IDistanceAlgorithm {

	@Override
	public double getDistance(Point point, Point goal) {
		return Math.sqrt(Math.pow(point.x - goal.x, 2) + Math.pow(point.y - goal.y, 2));
	}
}