package asciiWorld;

import org.newdawn.slick.geom.Vector2f;

import asciiWorld.math.RandomFactory;
import asciiWorld.math.Vector3f;

public enum Direction {
	
	North,
	South,
	East,
	West;
	
	public static Direction[] all() {
		return new Direction[] {
			North,
			South,
			East,
			West
		};
	}
	
	public static Direction fromVector2f(Vector2f position) {
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
			return null;
		}
	}
	
	public static Direction fromVector3f(Vector3f position) throws Exception {
		return fromVector2f(position.toVector2f());
	}
	
	public static Direction random() {
		switch (RandomFactory.get().nextInt(0, 4)) {
		case 0:
			return North;
		case 1:
			return South;
		case 2:
			return East;
		case 3:
			return West;
		default:
			return null;	
		}
	}
	
	public Direction opposite() {
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
			return null;	
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
}