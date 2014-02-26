package asciiWorld.math;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

public class MathHelper {

	public static float lerp(float from, float to, float weight) {
		return from + ((to - from) * weight);
	}

	public static Color lerp(Color from, Color to, float weight) {
		Color color = new Color(0, 0, 0, 0);
		color.a = lerp(from.a, to.a, weight);
		color.r = lerp(from.r, to.r, weight);
		color.g = lerp(from.g, to.g, weight);
		color.b = lerp(from.b, to.b, weight);
		return color;
	}
	
	public static float smoothStep(final float from, final float to, float weight) {
		weight = (weight > 1) ? 1 : ((weight < 0) ? 0 : weight);
		weight = (weight * weight) * (3 - (2 * weight));
		return lerp(from, to, weight);
	}
	
	public static Vector2f smoothStep(final Vector2f from, final Vector2f to, float weight) {
		Vector2f vector = new Vector2f(0, 0);
		weight = (weight > 1) ? 1 : ((weight < 0) ? 0 : weight);
		weight = (weight * weight) * (3 - (2 * weight));
		vector.x = lerp(from.x, to.x, weight);
		vector.y = lerp(from.y, to.y, weight);
		return vector;
	}
	
	public static Vector3f smoothStep(final Vector3f from, final Vector3f to, float weight) {
		Vector3f vector = new Vector3f();
		weight = (weight > 1) ? 1 : ((weight < 0) ? 0 : weight);
		weight = (weight * weight) * (3 - (2 * weight));
		vector.x = lerp(from.x, to.x, weight);
		vector.y = lerp(from.y, to.y, weight);
		vector.z = lerp(from.z, to.z, weight);
		return vector;
	}
	
	public static Color smoothStep(final Color from, final Color to, float weight) {
		Color color = new Color(0, 0, 0, 0);
		weight = (weight > 1) ? 1 : ((weight < 0) ? 0 : weight);
		weight = (weight * weight) * (3 - (2 * weight));
		color.r = lerp(from.r, to.r, weight);
		color.g = lerp(from.g, to.g, weight);
		color.b = lerp(from.b, to.b, weight);
		color.a = lerp(from.a, to.a, weight);
		return color;
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
}
