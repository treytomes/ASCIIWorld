package asciiWorld.math;

import org.newdawn.slick.geom.Vector2f;

public class Vector3f {
	public float x;
	public float y;
	public float z;
	
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3f(Vector3f cloneThis) {
		this(cloneThis.x, cloneThis.y, cloneThis.z);
	}
	
	public Vector3f(float x, float y) {
		this(x, y, 0);
	}
	
	public Vector3f(Vector2f vector, float z) {
		this(vector.x, vector.y, z);
	}
	
	public Vector3f(Vector2f vector) {
		this(vector.x, vector.y, 0.0f);
	}
	
	public Vector3f() {
		this(0, 0);
	}
	
	public Vector3f add(Vector3f vector) {
		x += vector.x;
		y += vector.y;
		z += vector.z;
		return this;
	}
	
	public Vector3f add(Vector2f vector) {
		x += vector.x;
		y += vector.y;
		return this;
	}
	
	public Vector3f subtract(Vector3f vector) {
		x -= vector.x;
		y -= vector.y;
		z -= vector.z;
		return this;
	}
	
	public Vector3f subtract(Vector2f vector) {
		x -= vector.x;
		y -= vector.y;
		return this;
	}
	
	public Vector3f multiply(float scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
		return this;
	}
	
	public double getRoughDistance(Vector3f other) {
		float dx = (x - other.x);
		float dy = (y - other.y);
		float dz = (z - other.z);
		return dx * dx + dy * dy + dz * dz;
	}
	
	public double getDistance(Vector3f vector) {
		return Math.sqrt(getRoughDistance(vector));
	}
	
	public double getRoughDistance(Vector2f vector) {
		float dx = (x - vector.x);
		float dy = (y - vector.y);
		return dx * dx + dy * dy;
	}
	
	public double getDistance(Vector2f vector) {
		return Math.sqrt(getRoughDistance(vector));
	}
	
	public Vector2f toVector2f() {
		return new Vector2f(x, y);
	}
	
	public Vector3f clone() {
		return new Vector3f(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vector3f) {
			Vector3f otherVector = Vector3f.class.cast(obj);
			return
					(otherVector.x == x) &&
					(otherVector.y == y) &&
					(otherVector.z == z);
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return String.format("(%f, %f, %f)", x, y, z);
	}
}