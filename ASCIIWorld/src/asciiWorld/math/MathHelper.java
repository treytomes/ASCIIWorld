package asciiWorld;

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
}
