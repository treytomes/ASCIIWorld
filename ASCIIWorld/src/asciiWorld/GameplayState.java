package asciiWorld;

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

import asciiWorld.chunks.Chunk;
import asciiWorld.chunks.ChunkFactory;
import asciiWorld.entities.Entity;
import asciiWorld.entities.PlayerControlComponent;
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
		_font = FontFactory.get().getDefaultFont();
		
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
			_chunk = ChunkFactory.generateOverworld();
		} catch (Exception e) {
			throw new SlickException("Unable to generate the chunk.");
		}
		
		try {
			_player = new Entity();
			_player.getComponents().add(new PlayerControlComponent(_player, _ui));
			_player.moveTo(_chunk.findRandomSpawnPoint(), Chunk.LAYER_OBJECT);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to create the player.");
		}
		_chunk.addEntity(_player);
		
		try {
			_player.setTile(TileFactory.get().getResource("player"));
		} catch (Exception e) {
			throw new SlickException("Unable to load the tile resources.", e);
		}
		
		_camera = new Camera(this, _player, 4.0f);
		
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
					_chunk.update(container, game, delta);
				}
			}
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		_camera.apply(g);
		
		_chunk.render(_camera, _tiles);
		
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
		MethodBinding getPlayerPositionBinding = new MethodBinding(new MethodBinding(this, "getPlayer"), "getPosition");
		
		Button mainMenuButton = Button.createStateTransitionButton("Main Menu", game, ASCIIWorldGame.STATE_MAINMENU);
		Button exitButton = Button.createActionButton("Exit", new MethodBinding(container, "exit"));
		
		Button zoomInButton = Button.createActionButton("Zoom +", new MethodBinding(this, "zoomIn"));
		Button zoomOutButton = Button.createActionButton("Zoom -", new MethodBinding(this, "zoomOut"));
		
		StackPanel menuButtonPanel = new StackPanel(new Rectangle(getBounds().getWidth() - 202 - 5, 5, 202, 42 * 2), Orientation.Vertical);
		menuButtonPanel.addChild(mainMenuButton);
		menuButtonPanel.addChild(exitButton);
		
		StackPanel zoomButtonPanel = new StackPanel(new Rectangle(getBounds().getWidth() - 106 * 2 - 5, getBounds().getHeight() - 42 - 5, 106 * 2, 42));
		zoomButtonPanel.addChild(zoomOutButton);
		zoomButtonPanel.addChild(zoomInButton);
		
		RootVisualPanel root = new RootVisualPanel(container);
		root.addChild(new Label(new Vector2f(10, 10), _font, "Gameplay State", Color.red));
		root.addChild(menuButtonPanel);
		root.addChild(zoomButtonPanel);
		root.addChild(new Label(new Vector2f(10, 30), _font, getPlayerPositionBinding, Color.blue));
		
		return root;
	}
	
	public void zoomIn() {
		_camera.setScale(_camera.getScale() + ZOOM_INCREMENT);
	}
	
	public void zoomOut() {
		_camera.setScale(_camera.getScale() - ZOOM_INCREMENT);
	}
}