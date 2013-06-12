package asciiWorld.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.IHasBounds;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.Camera;
import asciiWorld.chunks.Chunk;
import asciiWorld.entities.Entity;
import asciiWorld.entities.EntityCamera;
import asciiWorld.entities.EntityFactory;
import asciiWorld.entities.HotKeyManager;
import asciiWorld.entities.PlayerControlComponent;
import asciiWorld.math.Vector3f;
import asciiWorld.stateManager.GameState;
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
	private static final String DEFAULT_PLAYER_RESOURCE = "player";
	
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
		
		if (isComplete()) {
			try {
				getManager().switchTo(new GameplayState(getChunk(), getCamera()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			RootVisualPanel.get().update(container, delta);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		try {
			RootVisualPanel.get().render(g);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generateGreeting() {
		try {
			RootVisualPanel.get().loadMessageBox("resources/ui/welcomeMessageBox.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createPlayer(GameContainer container) {
		try {
			_player = EntityFactory.get().getResource(DEFAULT_PLAYER_RESOURCE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		createCamera(container);
		_playerControl = new PlayerControlComponent(_player, _camera);
		_player.getComponents().add(_playerControl);
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
		_camera = new EntityCamera(new IHasBounds() {
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
