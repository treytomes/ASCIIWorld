package asciiWorld.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.Direction;
import asciiWorld.tiles.TileTransform;
import asciiWorld.tiles.TransformEffect;

public class RotateInFacingDirectionComponent extends EntityComponent {

	private static final float DEFAULT_ANGLE_OFFSET = 90.0f;
	
	private float _angleOffset;
	private TileTransform _transform;
	private Boolean _isInitialized;
	private Direction _lastDirection;
	
	public RotateInFacingDirectionComponent(Entity owner) {
		super(owner);
		
		_transform = new TileTransform(0, TransformEffect.None);
		_transform.setRotation(180.0f);
		_isInitialized = false;
		setAngleOffset(DEFAULT_ANGLE_OFFSET);
		_lastDirection = getOwner().getDirection();
	}
	
	public float getAngleOffset() {
		return _angleOffset;
	}
	
	public void setAngleOffset(float value) {
		_angleOffset = value;
	}
	
	private boolean hasDirectionChanged() {
		return _lastDirection != getOwner().getDirection();
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int deltaTime) {
		if (!_isInitialized) {
			getOwner().getTile().getTransformations().add(_transform);
			_isInitialized = true;
		}
		if (hasDirectionChanged()) {
			_transform.setRotation(getAngleOffset() + (float)Math.toDegrees(getOwner().getDirection().toVector2f().getAngle()));
			_lastDirection = getOwner().getDirection(); 
		}
		super.update(container, game, deltaTime);
	}
}
