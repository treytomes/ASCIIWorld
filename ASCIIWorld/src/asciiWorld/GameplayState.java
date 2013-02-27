package asciiWorld;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.chunks.Chunk;
import asciiWorld.chunks.ChunkFactory;
import asciiWorld.entities.Entity;
import asciiWorld.entities.PlayerControlComponent;
import asciiWorld.tiles.TileFactory;
import asciiWorld.tiles.TileSet;
import asciiWorld.ui.HUDView;
import asciiWorld.ui.RootVisualPanel;

public class GameplayState extends BasicGameState implements IHasBounds {
	
	private static final String TILESET_NAME = "resources/tileSets/OEM437.xml";
	
	private int _stateID = -1;
	
	private UnicodeFont _font = null;
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
			_tiles = TileSet.load(TILESET_NAME);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to load the tileset resource.");
		}
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
		
		try {
			_chunk = ChunkFactory.generateOverworld();
		} catch (Exception e) {
			throw new SlickException("Unable to generate the chunk.", e);
		}
		
		try {
			_player = new Entity();
			_camera = new Camera(this, _player, 4.0f);
			_player.getComponents().add(new PlayerControlComponent(_player, _camera));
			_player.moveTo(_chunk.findRandomSpawnPoint(Chunk.LAYER_OBJECT));
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
		
		try {
			generateUI(container, game);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to generate the user interface.");
		}
		
		try {
			RootVisualPanel.get().loadMessageBox("resources/ui/welcomeMessageBox.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.leave(container, game);
		
		try {
			RootVisualPanel.get().clear();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to clear the RootVisualPanel.");
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if (container.hasFocus()) {
			if (!isPaused()) {
				try {
					RootVisualPanel.get().update(container, delta);
					if (!RootVisualPanel.get().isModalWindowOpen()) {
						_chunk.update(container, game, delta);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		_camera.apply(g);
		
		_chunk.render(_camera, _tiles);
		
		_camera.reset(g);
		
		try {
			RootVisualPanel.get().render(g);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
	
	private void generateUI(final GameContainer container, final StateBasedGame game) throws Exception {
		HUDView hud = new HUDView(container, game);
		hud.setCamera(_camera);
		hud.setPlayer(_player);
		RootVisualPanel.get().addChild(hud);
	}
}