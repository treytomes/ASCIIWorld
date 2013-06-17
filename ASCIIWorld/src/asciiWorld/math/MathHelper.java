package asciiWorld.math;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;


public class MathHelper {
	
	public static Vector3f smoothStep(final Vector3f from, final Vector3f to, float weight) {
		Vector3f vector = new Vector3f();
		weight = (weight > 1) ? 1 : ((weight < 0) ? 0 : weight);
		weight = (weight * weight) * (3 - (2 * weight));
		vector.x = from.x + ((to.x - from.x) * weight);
		vector.y = from.y + ((to.y - from.y) * weight);
		vector.z = from.z + ((to.z - from.z) * weight);
		return vector;
	}
	
	public static Vector2f smoothStep(final Vector2f from, final Vector2f to, float weight) {
		Vector2f vector = new Vector2f(0, 0);
		weight = (weight > 1) ? 1 : ((weight < 0) ? 0 : weight);
		weight = (weight * weight) * (3 - (2 * weight));
		vector.x = from.x + ((to.x - from.x) * weight);
		vector.y = from.y + ((to.y - from.y) * weight);
		return vector;
	}

	public static float clamp(float value, float lowerBound, float upperBound) {
		if (value < lowerBound) {
			return lowerBound;
		} else if (value > upperBound) {
			return upperBound;
		} else {
			return value;
		}
	}

	public static float lerp(float from, float to, float weight) {
		return from + ((to - from) * weight);
	}

	public static Color lerp(Color from, Color to, float weight) {
		Color color = new Color(0, 0, 0, 0);
		color.a = from.a + ((to.a - from.a) * weight);
		color.r = from.r + ((to.r - from.r) * weight);
		color.g = from.g + ((to.g - from.g) * weight);
		color.b = from.b + ((to.b - from.b) * weight);
		return color;
	}
}
