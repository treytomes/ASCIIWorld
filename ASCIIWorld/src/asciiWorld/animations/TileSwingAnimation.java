package asciiWorld.animations;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.IHasPosition;
import asciiWorld.entities.Entity;
import asciiWorld.math.MathHelper;
import asciiWorld.math.Vector3f;
import asciiWorld.tiles.Tile;
import asciiWorld.tiles.TileSet;

public class TileSwingAnimation {

	private static final float ANGULAR_VELOCITY_MULTIPLIER = 0.01f;
	private static final float DEFAULT_ANGULAR_VELOCITY = 1.0f;
	private static final float DEFAULT_ROTATIONAL_OFFSET = 90;
	private static final float SPEED_TIME_DIVISOR = 20.0f;
	private static final float ANGLE_MIN = 0.0f;
	private static final float ANGLE_MAX = (float)Math.PI * 2.0f;

	private Tile _tile;
	private IHasPosition _owner;
	private float _angularVelocity;
	private float _rotationalOffset;
	
	private float _animationWeight;
	
	private boolean _repeat;
	
	public static TileSwingAnimation createUseActiveItemAnimation(Entity owner, Vector3f targetChunkPoint) {
		return createUseItemAnimation(owner, owner.getActiveItem(), targetChunkPoint);
	}
	
	public static TileSwingAnimation createUseItemAnimation(Entity owner, Entity item, Vector3f targetChunkPoint) {
		Vector2f sourceChunkPoint = owner.getOccupiedChunkPoint();
		float rotationalOffset = getAngle(sourceChunkPoint, targetChunkPoint);
		return new TileSwingAnimation(item.getTile(), owner, rotationalOffset, owner.getAttackSpeed());
	}
	
	private static float getAngle(Vector2f fromPoint, Vector3f toPoint) {
		return (float)Math.atan2(toPoint.y - fromPoint.y, toPoint.x - fromPoint.x);
	}
	
	public TileSwingAnimation(Tile tile, IHasPosition owner, float rotationalOffset, float angularVelocity) {
		_tile = tile;
		_owner = owner;
		
		setAngularVelocity(angularVelocity);
		setRotationalOffset(rotationalOffset);
		
		_animationWeight = ANGLE_MIN;
		
		_repeat = false;
	}
	
	public TileSwingAnimation(Tile tile, IHasPosition owner, float rotationalOffset) {
		this(tile, owner, rotationalOffset, DEFAULT_ANGULAR_VELOCITY);
	}
	
	public TileSwingAnimation(Tile tile, IHasPosition owner) {
		this(tile, owner, DEFAULT_ROTATIONAL_OFFSET);
	}
	
	public boolean getRepeat() {
		return _repeat;
	}
	
	public void setRepeat(boolean value) {
		_repeat = value;
	}
	
	public boolean isAlive() {
		return _repeat || (!_repeat && (getAngleWeight() <= ANGLE_MAX));
	}
	
	public float getAngularVelocity() {
		return _angularVelocity;
	}
	
	public void setAngularVelocity(float value) {
		_angularVelocity = value;
	}
	
	public float getRotationalOffset() {
		return _rotationalOffset;
	}
	
	public void setRotationalOffset(float value) {
		_rotationalOffset = value;
	}
	
	public float getTileRotationAngle() {
		return 90.0f + (float)Math.toDegrees(getAngle());
	}
	
	private float getAngleWeight() {
		return MathHelper.lerp(ANGLE_MIN, ANGLE_MAX, _animationWeight);
	}
	
	private float getAngle() {
		return getRotationalOffset() + (float)Math.cos(getAngleWeight());
	}

	private Vector2f getOffset() {
		return new Vector2f((float)Math.cos(getAngle()), (float)Math.sin(getAngle()));
	}
	
	private Vector2f getPosition(TileSet tiles) 
	{
		Vector3f position = _owner.getPosition();
		Vector2f size = tiles.getSize();
		Vector2f offset = getOffset();
		return new Vector2f(
				position.x + size.x * offset.x,
				position.y + size.y * offset.y);
	}
	
	public void update(double deltaTime) {
		if (isAlive()) {
			_animationWeight += (deltaTime / SPEED_TIME_DIVISOR) * (getAngularVelocity() * ANGULAR_VELOCITY_MULTIPLIER);
		}
	}
	
	private void translate(Graphics g, TileSet tiles) {
		Vector2f position = getPosition(tiles);
		g.translate(position.x, position.y);
	}
	
	private void rotate(Graphics g, TileSet tiles) {
		Vector2f size = tiles.getSize();
		g.rotate(size.x / 2, size.y / 2, getTileRotationAngle());
	}
	
	public void render(Graphics g, TileSet tiles) {
		g.pushTransform();
		translate(g, tiles);
		rotate(g, tiles);
		_tile.render(tiles);
		g.popTransform();
	}
}
