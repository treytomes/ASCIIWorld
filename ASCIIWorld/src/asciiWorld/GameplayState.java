package asciiWorld;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.chunks.Chunk;
import asciiWorld.stateManager.GameState;
import asciiWorld.ui.RootVisualPanel;

public class GameplayState extends GameState {
	
	private Chunk _chunk;
	private Camera _camera;
	
	public GameplayState(Chunk chunk, Camera camera) {
		_chunk = chunk;
		_camera = camera;
	}
	
	@Override
	public void leave() {
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
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		if (container.hasFocus()) {
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
		} else {
			try {
				getManager().enter(new PauseGameState());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		_camera.apply(g);
		_chunk.render(g, _camera);
		_camera.reset(g);
		
		try {
			RootVisualPanel.get().render(g);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}