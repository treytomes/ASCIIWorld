package asciiWorld;

import org.newdawn.slick.geom.Vector2f;

public enum Direction {
	
	North,
	South,
	East,
	West;
	
	public Direction opposite() throws Exception {
		switch (this) {
		case North:
			return South;
		case South:
			return North;
		case East:
			return West;
		case West:
			return East;
		default:
			throw new Exception("This shouldn't be possible.");
		}
	}
	
	public Vector2f toVector2f() {
		switch (this) {
		case North:
			return new Vector2f(0, -1);
		case South:
			return new Vector2f(0, 1);
		case East:
			return new Vector2f(1, 0);
		case West:
			return new Vector2f(-1, 0);
		default:
			return new Vector2f(0, 0);
		}
	}
	
	public Vector3f toVector3f() {
		return new Vector3f(toVector2f());
	}
	
	public static Direction fromVector2f(Vector2f position) throws Exception {
		position = position.normalise();
		if ((position.x == 0) && (position.y == -1)) {
			return Direction.North;
		} else if ((position.x == 0) && (position.y == 1)) {
			return Direction.South;
		} else if ((position.x == 1) && (position.y == 0)) {
			return Direction.East;
		} else if ((position.x == -1) && (position.y == 0)) {
			return Direction.West;
		} else {
			throw new Exception("The input vector is not a cardinal direction.");
		}
	}
	
	public static Direction fromVector3f(Vector3f position) throws Exception {
		return fromVector2f(position.toVector2f());
	}
}