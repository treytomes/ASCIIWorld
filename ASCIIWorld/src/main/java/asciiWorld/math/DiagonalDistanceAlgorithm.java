package asciiWorld.math;


public class DiagonalDistanceAlgorithm implements IDistanceAlgorithm {

	@Override
	public double getDistance(Point point, Point goal) {
		return Math.max(Math.abs(point.x - goal.x), Math.abs(point.y - goal.y));
	}
}