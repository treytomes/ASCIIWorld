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
	private TileSet _tiles;
	private float _scale;
	
	public TileView(Vector2f position, Object tileBinding) {
		_position = position;
		setTile(tileBinding);
		setScale(DEFAULT_SCALE);
		
		try {
			_tiles = TileSetFactory.get().getDefaultTileSet();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to load the tileset resource.");
		}
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
	
	public TileSet getTileSet() {
		return _tiles;
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
					tile.render(getTileSet());
					
					g.resetTransform();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Rectangle getBounds() {
		Vector2f tileSize = _tiles.getSize();
		return new Rectangle(_position.x, _position.y, tileSize.x, tileSize.y);
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
