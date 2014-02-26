package asciiWorld.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.stateManager.GameStateManager;

public class SlickGameplayState extends BasicGameState {
	
	private int _stateID;
	
	private GameStateManager _manager;
	
	public SlickGameplayState(int stateID) {
		_stateID = stateID;
		_manager = new GameStateManager();
	}
	
	@Override
	public void init(final GameContainer container, final StateBasedGame game) throws SlickException {
	}
	
	@Override
	public void enter(final GameContainer container, final StateBasedGame game) throws SlickException {
		super.enter(container, game);
		
		try {
			_manager.enter(new GenerateChunkGameState());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.leave(container, game);
		
		// Clear the state manager.
		try {
			while (!_manager.isEmpty()) {
				_manager.leave(_manager.getActiveState());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		_manager.update(container, game, delta);
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		_manager.render(container, game, g);
	}
	
	@Override
	public int getID() {
		return _stateID;
	}
}