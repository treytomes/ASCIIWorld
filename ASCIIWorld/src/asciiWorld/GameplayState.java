package asciiWorld;

import java.awt.Font;
import java.io.IOException;

import org.jdom2.JDOMException;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.entities.Entity;
import asciiWorld.entities.PlayerControlComponent;
import asciiWorld.tiles.Tile;
import asciiWorld.tiles.TileFactory;
import asciiWorld.tiles.TileSet;
import asciiWorld.ui.Button;
import asciiWorld.ui.Label;
import asciiWorld.ui.MethodBinding;
import asciiWorld.ui.Orientation;
import asciiWorld.ui.RootVisualPanel;
import asciiWorld.ui.StackPanel;

public class GameplayState extends BasicGameState implements IHasBounds {

	private static final float ZOOM_INCREMENT = 0.1f;
	
	private int _stateID = -1;
	
	private UnicodeFont _font = null;
	private RootVisualPanel _ui = null;
	private TileSet _tiles = null;
	private Chunk _chunk = null;
	private Entity _player = null;
	private Tile _waterTile = null;
	private Tile _woodLogTile = null;
	private Tile _woodenSwordTile = null;
	private Rectangle _bounds;
	private Camera _camera;
	
	private Boolean _isPaused;
	
	public GameplayState(int stateID) {
		_stateID = stateID;
		_isPaused = false;
	}
	
	public Rectangle getBounds() {
		return _bounds;
	}
	
	public Entity getPlayer() {
		return _player;
	}

	public Boolean isPaused() {
		return _isPaused;
	}
	
	public void pause() {
		_isPaused = true;
	}
	
	public void resume() {
		_isPaused = false;
	}
	
	@Override
	public void init(final GameContainer container, final StateBasedGame game) throws SlickException {
		_bounds = new Rectangle(0, 0, container.getWidth(), container.getHeight());
		
		// Create the font.
		_font = FontFactory.get().getResource("Courier New", Font.BOLD, 20);
		
		try {
			_ui = generateUI(container, game);
		} catch (Exception e) {
			System.err.println("Unable to generate the user interface.");
		}
		
		try {
			_tiles = TileSet.load("resources/tileSets/OEM437.xml");
		} catch (JDOMException | IOException e) {
			System.err.println("Unable to load the tileset resources.");
		}
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
		
		try {
			//_chunk = ChunkFactory.generateGrassyPlain();
			//_chunk = ChunkFactory.generateDesert();
			_chunk = ChunkFactory.generateCollisionTest(_ui);
		} catch (Exception e) {
			throw new SlickException("Unable to generate the chunk.");
		}
		
		_player = new Entity();
		_player.getComponents().add(new PlayerControlComponent(_player));
		_player.moveTo(new Vector2f(0, 0), Chunk.LAYER_OBJECT);
		_chunk.addEntity(_player);
		
		try {
			_player.setTile(TileFactory.get().getResource("player"));
			_woodLogTile = TileFactory.get().getResource("woodLog");
			_waterTile = TileFactory.get().getResource("water");
			_woodenSwordTile = TileFactory.get().getResource("woodenSword");
		} catch (Exception e) {
			throw new SlickException("Unable to load the tile resources.", e);
		}
		
		_camera = new Camera(this, _player, 4.0f);
		
		//_ui.showMessageBox(true, "Welcome to ASCII World!  This is but a simple demo, but it will become a full-fledged game world in the due course of events.", "Welcome!");
		//_ui.showMessageBox(true, text, "Welcome!");
		try {
			_ui.loadMessageBox("resources/ui/welcomeMessageBox.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if (container.hasFocus()) {
			if (!isPaused()) {
				_ui.update(container, delta);
				if (!_ui.isModalWindowOpen()) {
					_waterTile.update(delta);
					_chunk.update(container, game, delta);
				}
			}
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		_camera.apply(g);
		
		_chunk.render(_camera, _tiles);
		_waterTile.render(_tiles, new Vector2f(32, 32));
		_woodLogTile.render(_tiles, new Vector2f(40, 32));
		_woodenSwordTile.render(_tiles, new Vector2f(48, 32));
		
		_camera.reset(g);
		
		_ui.render(g);
		
		if (!container.hasFocus() || isPaused()) {
			renderPauseScreen(container, game, g);
		}
	}
	
	private void renderPauseScreen(GameContainer container, StateBasedGame game, Graphics g) {
		g.setColor(new Color(0, 0, 0, 0.5f));
		g.fillRect(0, 0, container.getWidth(), container.getHeight());
		
		String text = "Paused";
		float x = (container.getWidth() - _font.getWidth(text)) / 2.0f;
		float y = (container.getHeight() - _font.getHeight(text)) / 2.0f;
		_font.drawString(x, y, text);
		
		g.flush();
	}
	
	@Override
	public int getID() {
		return _stateID;
	}
	
	private RootVisualPanel generateUI(final GameContainer container, final StateBasedGame game) throws Exception {
		final MethodBinding getPlayerPositionBinding = new MethodBinding(new MethodBinding(this, "getPlayer"), "getPosition");
		
		final Button mainMenuButton = Button.createStateTransitionButton("Main Menu", game, ASCIIWorldGame.STATE_MAINMENU);
		final Button exitButton = Button.createActionButton("Exit", new MethodBinding(container, "exit"));
		
		final Button zoomInButton = Button.createActionButton("Zoom +", new MethodBinding(this, "zoomIn"));
		final Button zoomOutButton = Button.createActionButton("Zoom -", new MethodBinding(this, "zoomOut"));
		
		return new RootVisualPanel(container) {{
			addChild(new Label(new Vector2f(10, 10), _font, "Gameplay State", Color.red));
			
			addChild(new StackPanel(new Rectangle(getBounds().getWidth() - 202 - 5, 5, 202, 42 * 2), Orientation.Vertical) {{
				addChild(mainMenuButton);
				addChild(exitButton);
			}});
			
			addChild(new StackPanel(new Rectangle(getBounds().getWidth() - 106 * 2 - 5, getBounds().getHeight() - 42 - 5, 106 * 2, 42)) {{
				addChild(zoomOutButton);
				addChild(zoomInButton);
			}});
			
			addChild(new Label(new Vector2f(10, 30), _font, getPlayerPositionBinding, Color.blue));
		}};
	}
	
	public void zoomIn() {
		_camera.setScale(_camera.getScale() + ZOOM_INCREMENT);
	}
	
	public void zoomOut() {
		_camera.setScale(_camera.getScale() - ZOOM_INCREMENT);
	}
}