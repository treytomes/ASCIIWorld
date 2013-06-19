package asciiWorld.ai;

import asciiWorld.math.Point;

public class AStarNode extends Point {

	/**
	 * The parent node object.
	 */
	public AStarNode parent;
	
	/**
	 * The array index of this node in the world linear array.
	 */
	public int value;
	
	/**
	 * The distanceFunction cost to get TO this node from the START.
	 */
	public double f;
	
	/**
	 * The distanceFunction cost to get from this node to the GOAL.
	 */
	public double g;
	
	public AStarNode(int mapWidth, AStarNode parent, Point point) {
		this.parent = parent;
		value = (point.x + (point.y * mapWidth));
		x = point.x;
		y = point.y;
		f = 0;
		g = 0;
	}
}
