package asciiWorld.ui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.tiles.Tile;
import asciiWorld.tiles.TileSet;
import asciiWorld.tiles.TileSetFactory;

public class TileView extends FrameworkElement {
	
	private static final float DEFAULT_SCALE = 2.0f;
	
	private Vector2f _position;
	private Object _tileBinding;
	private float _scale;
	
	public TileView(Vector2f position, Object tileBinding) {
		_position = position;
		setTile(tileBinding);
		setScale(DEFAULT_SCALE);
	}
	
	public TileView(Object tileBinding) {
		this(new Vector2f(0, 0), tileBinding);
	}
	
	public Tile getTile() throws Exception {
		if (_tileBinding instanceof Tile) {
			return (Tile)_tileBinding;
		} else if (_tileBinding instanceof MethodBinding) {
			return (Tile)((MethodBinding)_tileBinding).getValue();
		} else {
			throw new Exception("Invalid tile binding.");
		}
	}
	
	public void setTile(Object value) {
		_tileBinding = value;
	}
	
	public Vector2f getPosition() {
		return _position;
	}
	
	public float getScale() {
		return _scale;
	}
	
	public void setScale(float value) {
		_scale = value;
	}
	
	private TileSet getTileSet() {
		try {
			if (getTile() == null) {
				return TileSetFactory.get().getDefaultTileSet();
			} else {
				return getTile().getTileSet();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void render(Graphics g) {
		if (_tileBinding != null) {
			try {
				Tile tile = getTile();
				if (tile != null) {
					float scale = getScale();
					
					float parentWidth = getParent().getBounds().getWidth();
					float parentHeight = getParent().getBounds().getHeight();
					float myWidth = getBounds().getWidth();
					float myHeight = getBounds().getHeight();
					float centerX = _position.x + (parentWidth - myWidth) / 2;
					float centerY = _position.y + (parentHeight - myHeight) / 2;

					g.scale(scale, scale);
					
					g.translate(centerX / scale - scale, centerY / scale - scale);
					tile.render();
					
					g.resetTransform();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Rectangle getBounds() {
		try {
			Vector2f tileSize = getTileSet().getSize();
			return new Rectangle(_position.x, _position.y, tileSize.x, tileSize.y);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void moveTo(Vector2f position) {
		_position = position.copy();
	}

	@Override
	protected Boolean contains(Vector2f point) {
		return getBounds().contains(point.x, point.y);
	}
}
