package asciiWorld.lighting;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.entities.Entity;

public class TargetedLight extends Light {

	private Entity _target;
	
	public TargetedLight(Color color, float range, Entity target) {
		super(Vector2f.zero(), range, 1.0f, color);
		_target = target;
	}
	
	@Override
	public Vector2f getPosition() {
		return _target.getCenterPosition();
	}
}
