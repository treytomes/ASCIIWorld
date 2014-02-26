package asciiWorld.math;


public class ManhattanDistanceAlgorithm implements IDistanceAlgorithm {

	@Override
	public double getDistance(Point point, Point goal) {
		return Math.abs(point.x - goal.x) - Math.abs(point.y - goal.y);
	}
}
