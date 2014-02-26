package asciiWorld.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.FontFactory;
import asciiWorld.entities.Entity;
import asciiWorld.tiles.TileSet;
import asciiWorld.tiles.TileSetFactory;

public class HealthMeter extends FrameworkElement {

	private Object _playerBinding;
	private Rectangle _bounds;
	private UnicodeFont _font;
	private TileSet _tiles;
	
	public HealthMeter(Rectangle bounds, Object playerBinding) {
		super();
		
		_bounds = bounds;
		_playerBinding = playerBinding;
		
		try {
			_font = FontFactory.get().getDefaultFont();
			_tiles = TileSetFactory.get().getDefaultTileSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Entity getPlayer() {
		if (_playerBinding instanceof Entity) {
			return Entity.class.cast(_playerBinding);
		} else if (_playerBinding instanceof MethodBinding) {
			return Entity.class.cast(MethodBinding.class.cast(_playerBinding).getValue());
		} else {
			return null;
		}
	}

	@Override
	public void render(Graphics g) {
		Entity player = getPlayer();
		int health = player.getHealth();
		int maxHealth = player.getMaxHealth();
		
		Rectangle previousWorldClip = setTransform(g);
		
		g.pushTransform();
		g.translate(_bounds.getX(), _bounds.getY());
		
		g.scale(2, 2);

		for (int value = 10; value <= maxHealth; value += 10) {
			if (value <= health) {
				_tiles.draw(3, new Color(1, 0, 0, 1.0f));
			} else if (value > (health + 10 - (health % 10))) {
				_tiles.draw(3, new Color(1, 0, 0, 0.25f));
			} else {
				_tiles.draw(3, new Color(1, 0, 0, 0.25f + ((float)(health % 10) / 10.0f) * 0.75f));
			}
			g.translate(_tiles.getSize().x, 0);
		}
		
		g.scale(0.5f, 0.5f);
		
		_font.drawString(0, 0, String.format("%d / %d", health, maxHealth));
		
		g.popTransform();
		
		clearTransform(g, previousWorldClip);
	}

	@Override
	public Rectangle getBounds() {
		return _bounds;
	}

	@Override
	public void moveTo(Vector2f position) {
		_bounds.setX(position.x);
		_bounds.setY(position.y);
	}
}
