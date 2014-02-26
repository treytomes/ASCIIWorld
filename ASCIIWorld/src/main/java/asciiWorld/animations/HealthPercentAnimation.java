package asciiWorld.animations;

import org.newdawn.slick.Graphics;

import asciiWorld.entities.Entity;
import asciiWorld.math.Vector3f;

public class HealthPercentAnimation implements IAnimation {
	
	private static final float MAX_LIVE_TIME = 3000;
	
	private Entity _owner;
	private float _totalTime;
	
	public HealthPercentAnimation(Entity owner) {
		_owner = owner;
		_totalTime = 0;
	}

	@Override
	public boolean isAlive() {
		return _totalTime <= MAX_LIVE_TIME;
	}

	@Override
	public void update(double deltaTime) {
		_totalTime += deltaTime;
	}

	@Override
	public void render(Graphics g) {
		g.pushTransform();
		
		Vector3f position = _owner.getPosition();
		g.translate(position.x, position.y);
		_owner.renderHealth(g, 1.0f - (_totalTime / MAX_LIVE_TIME));
		
		g.popTransform();
	}

}
