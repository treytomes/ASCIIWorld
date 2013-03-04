package asciiWorld;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

public class GameState {
	
	private GameStateManager _manager;
	
	public GameState() {
		_manager = null;
	}
	
	public GameStateManager getManager() {
		return _manager;
	}
	
	public void setManager(GameStateManager value) {
		_manager = value;
	}
	
	public void enter() {
	}
	
	public void leave() {
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta) {
	}

	public void render(GameContainer container, StateBasedGame game, Graphics g) {
	}
}