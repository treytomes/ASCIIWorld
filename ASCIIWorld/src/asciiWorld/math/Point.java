package asciiWorld.math;

import org.newdawn.slick.geom.Vector2f;

public class Point {

	public int x;
	public int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Point() {
		this(0, 0);
	}
	
	public Vector2f toVector() {
		return new Vector2f(x, y);
	}
	
	public Point copy() {
		return new Point(x, y);
	}
	
	public Point scale(int xScale, int yScale) {
		x *= xScale;
		y *= yScale;
		return this;
	}
	
	public Point add(int xAmount, int yAmount) {
		x += xAmount;
		y += yAmount;
		return this;
	}
	
	public Point add(Point pnt) {
		return add(pnt.x, pnt.y);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Point) {
			Point otherPoint = Point.class.cast(obj);
			return (otherPoint.x == x) && (otherPoint.y == y);
		} else {
			return false;
		}
	}
}