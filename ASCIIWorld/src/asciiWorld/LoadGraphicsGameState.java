package asciiWorld;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.chunks.Chunk;
import asciiWorld.entities.Entity;
import asciiWorld.entities.HotKeyManager;
import asciiWorld.entities.PlayerControlComponent;
import asciiWorld.tiles.TileFactory;
import asciiWorld.ui.HUDView;
import asciiWorld.ui.RootVisualPanel;

/**
 * After generating the chunk, load the graphics prior to starting the game.
 * 
 * @author ttomes
 *
 */
public class LoadGraphicsGameState extends GameState {
	
	private static final float DEFAULT_ZOOM = 4.0f;
	private static final String DEFAULT_PLAYER_TILE = "player";
	
	private Entity _player;
	private Camera _camera;
	private Chunk _chunk;
	private PlayerControlComponent _playerControl;
	private Boolean _isComplete;
	
	public LoadGraphicsGameState(Chunk chunk) {
		_chunk = chunk;
		_isComplete = false;
	}
	
	public Boolean isComplete() {
		return _isComplete;
	}
	
	public Entity getPlayer() {
		return _player;
	}
	
	public Camera getCamera() {
		return _camera;
	}
	
	public Chunk getChunk() {
		return _chunk;
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		createPlayer(container);
		spawnPlayer();
		generateUI(container, game, _playerControl.getHotKeyManager());
		generateGreeting();
		_isComplete = true;
	}
	
	private void generateGreeting() {
		try {
			RootVisualPanel.get().loadMessageBox("resources/ui/welcomeMessageBox.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createPlayer(GameContainer container) {
		_player = new Entity();
		createCamera(container);
		_playerControl = new PlayerControlComponent(_player, _camera);
		_player.getComponents().add(_playerControl);

		try {
			_player.setTile(TileFactory.get().getResource(DEFAULT_PLAYER_TILE));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to load the player tile resource.");
		}
	}
	
	private Vector3f getSpawnPoint() {
		try {
			return _chunk.findRandomSpawnPoint(Chunk.LAYER_OBJECT);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to find a spawn point for the player.");
			return new Vector3f();
		}
	}
	
	private void spawnPlayer() {
		_player.moveTo(getSpawnPoint());
		_chunk.addEntity(_player);
	}
	
	private void createCamera(final GameContainer container) {
		_camera = new Camera(new IHasBounds() {
			@Override
			public Rectangle getBounds() {
				return new Rectangle(0, 0, container.getWidth(), container.getHeight());
			}
		}, _player, DEFAULT_ZOOM);
	}
	
	private void generateUI(GameContainer container, StateBasedGame game, HotKeyManager hotkeys) {
		try {
		HUDView hud = new HUDView(container, game, hotkeys);
		hud.setCamera(_camera);
		hud.setPlayer(_player);
		RootVisualPanel.get().addChild(hud);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to generate the user interface.");
		}
	}
}
