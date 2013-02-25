package asciiWorld;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.entities.Entity;

public class Camera implements IHasPosition, IHasBounds, IHasRangeOfVision {
	
	private static final float DEFAULT_RANGE_OF_VISION = 32;

	private IHasBounds _viewport;
	private IHasPosition _focus;
	private float _scale;
	private Vector2f _scaledCenter;
	
	public Camera(IHasBounds viewport, IHasPosition focus, float scale) {
		_viewport = viewport;
		_focus = focus;
		_scale = scale;
		setScaledCenter();
	}
	
	public Camera(IHasBounds viewport, IHasPosition focus) {
		this(viewport, focus, 1.0f);
	}
	
	public IHasBounds getViewport() {
		return _viewport;
	}
	
	public void setViewport(IHasBounds value) {
		_viewport = value;
		setScaledCenter();
	}
	
	public IHasPosition getFocus() {
		return _focus;
	}
	
	public void setFocus(IHasPosition value) {
		_focus = value;
	}
	
	public float getScale() {
		return _scale;
	}
	
	public void setScale(float scale) {
		_scale = scale;
		setScaledCenter();
	}
	
	public Rectangle getBounds() {
		return getViewport().getBounds();
	}
	
	private void setScaledCenter() {
		float scale = getScale();
		Rectangle bounds = getBounds();
		_scaledCenter = new Vector2f(bounds.getCenterX() / scale, bounds.getCenterY() / scale);
	}
	
	public Vector3f getPosition() {
		Vector3f focusPosition = getFocus().getPosition();
		return new Vector3f(focusPosition.x, focusPosition.y, focusPosition.z);
	}
	
	public float getRangeOfVision() {
		if (_focus instanceof IHasRangeOfVision) {
			return ((IHasRangeOfVision)_focus).getRangeOfVision();
		} else {
			return DEFAULT_RANGE_OF_VISION;
		}
	}
	
	public void apply(Graphics g) {
		float scale = getScale();
		Vector3f position = getPosition();
		
		g.scale(scale, scale);
		g.translate(_scaledCenter.x - position.x, _scaledCenter.y - position.y);
	}
	
	public void reset(Graphics g) {
		g.resetTransform();
	}

	public Vector2f screenPositionToChunkPosition(Vector2f screenPosition) {
		// TODO: Figure out why the "- 2" is necessary.  If it's not there, the bounds are always 2 pixels off.
		float x = (screenPosition.x - getBounds().getCenterX() + Entity.MOVEMENT_STEP) / _scale + _focus.getPosition().x - 2;
		float y = (screenPosition.y - getBounds().getCenterY() + Entity.MOVEMENT_STEP) / _scale + _focus.getPosition().y - 2;
		return new Vector2f(x, y);
	}

	/*public Vector2f ChunkPositionToScreenPosition(Vector3f chunkPosition)
	{
		return (chunkPosition - _focus.Position) * _scale - Entity.MOVEMENT_STEP * Vector2.One + _viewportCenter;
	}*/
}
