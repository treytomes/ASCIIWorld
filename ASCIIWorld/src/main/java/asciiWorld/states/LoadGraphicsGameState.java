package asciiWorld.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.IHasBounds;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.World;
import asciiWorld.chunks.Chunk;
import asciiWorld.entities.Entity;
import asciiWorld.entities.EntityCamera;
import asciiWorld.entities.EntityFactory;
import asciiWorld.entities.HotKeyManager;
import asciiWorld.entities.PlayerControlComponent;
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
	
	private World _world;
	private Chunk _chunk;
	private EntityCamera _camera;
	private PlayerControlComponent _playerControl;
	private Boolean _isComplete;
	
	public LoadGraphicsGameState(Chunk chunk) {
		_chunk = chunk;
		_world = _chunk.getWorld();
		_isComplete = false;
	}
	
	public Boolean isComplete() {
		return _isComplete;
	}
	
	public EntityCamera getCamera() {
		return _camera;
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		createPlayer(container);
		generateUI(container, game, _playerControl.getHotKeyManager());
		generateGreeting();
		_isComplete = true;
		
		if (isComplete()) {
			try {
				getManager().switchTo(new GameplayState(_world, getCamera()));
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
			Entity player = EntityFactory.get().getResource(DEFAULT_PLAYER_RESOURCE);
			player.moveTo(_chunk.findRandomSpawnPoint(Chunk.LAYER_OBJECT));
			_chunk.addEntity(player);
			_world.setPlayer(player);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		createCamera(container);
		_playerControl = new PlayerControlComponent(_world.getPlayer(), _camera);
		_world.getPlayer().getComponents().add(_playerControl);
	}
	
	private void createCamera(final GameContainer container) {
		_camera = new EntityCamera(new IHasBounds() {
			@Override
			public Rectangle getBounds() {
				return new Rectangle(0, 0, container.getWidth(), container.getHeight());
			}
		}, _world.getPlayer(), DEFAULT_ZOOM);
	}
	
	private void generateUI(GameContainer container, StateBasedGame game, HotKeyManager hotkeys) {
		try {
		HUDView hud = new HUDView(container, game, hotkeys);
		hud.setCamera(_camera);
		hud.setWorld(_world);
		RootVisualPanel.get().addChild(hud);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to generate the user interface.");
		}
	}
}
