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
import asciiWorld.entities.Entity;
import asciiWorld.tiles.TileSet;
import asciiWorld.ui.RootVisualPanel;

public class GameplayState extends BasicGameState implements IHasBounds {
	
	private enum RunState {
		GeneratingChunk,
		LoadGraphics,
		Play,
		Pause
	}
	private RunState _state;
	
	private static final String TILESET_NAME = "resources/tileSets/OEM437.xml";
	
	private int _stateID = -1;
	
	private UnicodeFont _font = null;
	private TileSet _tiles = null;
	private Chunk _chunk = null;
	private Entity _player = null;
	private Rectangle _bounds;
	private Camera _camera;
	
	private Boolean _isPaused;
	
	private GenerateChunkGameState _chunkGenerationState;
	private LoadGraphicsGameState _loadGraphicsState;
	
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
	public void enter(final GameContainer container, final StateBasedGame game) throws SlickException {
		super.enter(container, game);
		
		_state = RunState.GeneratingChunk;
		_chunkGenerationState = new GenerateChunkGameState();
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.leave(container, game);
		
		// Destroy the chunk.
		_chunk.clearEntities();
		
		// Clear the user interface.
		try {
			RootVisualPanel.get().clear();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to clear the RootVisualPanel.");
		}
	}
	
	private void updateLoggingWindow(GameContainer container, StateBasedGame game, int delta) {
		_chunkGenerationState.update(container, game, delta);
		if (_chunkGenerationState.isComplete()) {
			_loadGraphicsState = new LoadGraphicsGameState(_chunkGenerationState.getChunk());
			_state = RunState.LoadGraphics;
		}
	}
	
	private void loadGraphics(GameContainer container, StateBasedGame game, int delta) {
		_loadGraphicsState.update(container, game, delta);
		if (_loadGraphicsState.isComplete()) {
			_chunk = _loadGraphicsState.getChunk();
			_player = _loadGraphicsState.getPlayer();
			_camera = _loadGraphicsState.getCamera();
			_state = RunState.Play;
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		switch (_state) {
		case GeneratingChunk:
			updateLoggingWindow(container, game, delta);
			break;
		case LoadGraphics:
			loadGraphics(container, game, delta);
			break;
		case Play:
			if (container.hasFocus()) {
				if (!isPaused()) {
					try {
						RootVisualPanel.get().update(container, delta);
						try {
							if (!RootVisualPanel.get().isModalWindowOpen()) {
								_chunk.update(container, game, delta);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			if (!container.hasFocus() || isPaused()) {
				_state = RunState.Pause;
			}
			break;
		case Pause:
			if (container.hasFocus() && !isPaused()) {
				_state = RunState.Play;
			}
			break;
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		switch (_state) {
		case GeneratingChunk:
			_chunkGenerationState.render(container, game, g);
			break;
		case LoadGraphics:
			_loadGraphicsState.render(container, game, g);
			break;
		case Play:
			renderPlayScreen(container, game, g);
			break;
		case Pause:
			renderPlayScreen(container, game, g);
			renderPauseScreen(container, game, g);
			break;
		}
	}
	
	private void renderPlayScreen(GameContainer container, StateBasedGame game, Graphics g) {
		_camera.apply(g);
		_chunk.render(_camera, _tiles);
		_camera.reset(g);
		
		try {
			RootVisualPanel.get().render(g);
		} catch (Exception e) {
			e.printStackTrace();
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
}