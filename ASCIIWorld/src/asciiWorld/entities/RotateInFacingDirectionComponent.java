package asciiWorld.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.tiles.TileTransform;
import asciiWorld.tiles.TransformEffect;

public class RotateInFacingDirectionComponent extends EntityComponent {

	private static final float DEFAULT_ANGLE_OFFSET = 90.0f;
	
	private float _angleOffset;
	private TileTransform _transform;
	private Boolean _isInitialized;
	
	public RotateInFacingDirectionComponent(Entity owner) {
		super(owner);
		
		_transform = new TileTransform(0, TransformEffect.None);
		_transform.setRotation(180.0f);
		_isInitialized = false;
		setAngleOffset(DEFAULT_ANGLE_OFFSET);
	}
	
	public float getAngleOffset() {
		return _angleOffset;
	}
	
	public void setAngleOffset(float value) {
		_angleOffset = value;
	}

	@Override
	public void collided(Entity collidedWithEntity) {
		getOwner().move(collidedWithEntity.getDirection());
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int deltaTime) {
		if (!_isInitialized) {
			getOwner().getTile().getTransformations().add(_transform);
			_isInitialized = true;
		}
		_transform.setRotation(getAngleOffset() + (float)Math.toDegrees(getOwner().getDirection().toVector2f().getAngle()));
		super.update(container, game, deltaTime);
	}
}
