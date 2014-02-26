package asciiWorld.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.tiles.TileTransform;

public class SpinComponent extends EntityComponent {

	private float _speed;
	private TileTransform _transform;
	private boolean _isInitialized;
	
	public SpinComponent(Entity owner) {
		super(owner);
		_speed = 0.0f;
		_transform = new TileTransform();
		_isInitialized = false;
	}

	public Float getSpeed() {
		return _speed;
	}
	
	public void setSpeed(Float value) {
		_speed = value;
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int deltaTime) {
		if (!_isInitialized) {
			getOwner().getTile().getTransformations().add(_transform);
			_isInitialized = true;
		}
		_transform.setRotation(_transform.getRotation() + _speed);
	}
}
