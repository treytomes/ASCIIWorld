package asciiWorld.tileLocator;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.tiles.SpriteBatch;
import asciiWorld.tiles.TileSet;
import asciiWorld.tiles.TileSetFactory;

public class TileLocatorGame extends BasicGame {

	private static final Vector2f SCALE = new Vector2f(2, 2);
	private static final int MARGIN_LEFT = 64;
	private static final int MARGIN_TOP = 64;
	private static final int PADDING = 4;

	private TileSet _tiles;
	
	private int _mouseRow;
	private int _mouseColumn;
	private int _selectedTile;

	public TileLocatorGame() {
		super("Tile Locator");
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		try {
			_tiles = TileSetFactory.get().getDefaultTileSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		super.mouseMoved(oldx, oldy, newx, newy);
		
		_mouseRow = (int)((newy - MARGIN_TOP) / (_tiles.getSize().y * SCALE.y + PADDING));
		_mouseColumn = (int)((newx - MARGIN_LEFT) / (_tiles.getSize().x * SCALE.x + PADDING));
		
		_selectedTile = _mouseRow * _tiles.getColumns() + _mouseColumn;
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.setColor(Color.white);

		for (int row = 0, y = MARGIN_TOP; row < _tiles.getRows(); row++, y += _tiles.getSize().y * SCALE.y + PADDING) {
			g.drawString(String.format("%02X", row * _tiles.getColumns()), MARGIN_LEFT - 32, y);
		}
		
		for (int column = 0, x = MARGIN_LEFT; column < _tiles.getColumns(); column++, x += _tiles.getSize().x * SCALE.x + PADDING) {
			g.drawString(String.format("%02X", column), x, MARGIN_TOP - 32);
		}
		
		SpriteBatch spriteBatch = new SpriteBatch();
		for (int row = 0, y = MARGIN_TOP, tileIndex = 0; row < _tiles.getRows(); row++, y += _tiles.getSize().y * SCALE.y + PADDING) {
			for (int column = 0, x = MARGIN_LEFT; column < _tiles.getColumns(); column++, tileIndex++, x += _tiles.getSize().x * SCALE.x + PADDING) {
				_tiles.drawBatched(spriteBatch, x, y, SCALE, 0.0f, tileIndex, Color.white);
			}
		}
		spriteBatch.flush();
		
		if ((_mouseRow >= 0) && (_mouseRow < _tiles.getRows()) && (_mouseColumn >= 0) && (_mouseColumn < _tiles.getColumns())) {
			g.setColor(new Color(1.0f, 0.0f, 0.0f, 0.4f));
			g.fillRect(
					MARGIN_LEFT - 32,
					MARGIN_TOP + _mouseRow * (_tiles.getSize().y * SCALE.y + PADDING),
					_tiles.getColumns() * (_tiles.getSize().x * SCALE.x + PADDING) + 32,
					_tiles.getSize().y * SCALE.y + PADDING);
	
			g.setColor(new Color(0.0f, 1.0f, 0.0f, 0.4f));
			g.fillRect(
					MARGIN_LEFT + _mouseColumn * (_tiles.getSize().x * SCALE.x + PADDING),
					MARGIN_TOP - 32,
					_tiles.getSize().x * SCALE.x + PADDING,
					_tiles.getRows() * (_tiles.getSize().y * SCALE.y + PADDING) + 32);
			
			g.setColor(Color.white);
			g.drawString(Integer.toString(_selectedTile), MARGIN_LEFT + _tiles.getColumns() * (_tiles.getSize().x * SCALE.x + PADDING) + MARGIN_LEFT, MARGIN_TOP - 32);
			Vector2f scale = new Vector2f(8, 8);
			int padding = 4;
			g.drawRect(MARGIN_LEFT + _tiles.getColumns() * (_tiles.getSize().x * SCALE.x + PADDING) + MARGIN_LEFT, MARGIN_TOP, _tiles.getSize().x * scale.x + padding * 2, _tiles.getSize().y * scale.y + padding * 2);
			_tiles.drawBatched(spriteBatch, MARGIN_LEFT + _tiles.getColumns() * (_tiles.getSize().x * SCALE.x + PADDING) + MARGIN_LEFT + padding, MARGIN_TOP + padding, scale, 0.0f, _selectedTile, Color.white);
			spriteBatch.flush();
		}
	}
	
	public static void main(String[] args) {
		try {
			TileLocatorGame game = new TileLocatorGame();
			AppGameContainer app = new AppGameContainer(game);
			//app.setDisplayMode(game.getScreenWidth(), game.getScreenHeight(), game.getFullScreen());
			//app.setIcon("resources/gfx/icon.png");
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
