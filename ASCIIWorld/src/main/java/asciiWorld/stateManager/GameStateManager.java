package asciiWorld.stateManager;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.Require;

public class GameStateManager {
	
	private List<GameState> _states;
	
	public GameStateManager() {
		_states = new ArrayList<GameState>();
	}
	
	public Boolean isEmpty() {
		return _states.isEmpty();
	}
	
	public void enter(GameState state) throws Exception {
		Require.that(state, "state").isNotNull();
		Require.that(_states, "states").doesNotContain(state);
		_states.add(state);
		state.setManager(this);
		state.enter();
	}
	
	public void leave(GameState state) throws Exception {
		Require.that(state, "state").isNotNull();
		Require.that(_states, "states").contains(state);
		state.leave();
		state.setManager(null);
		_states.remove(state);
	}
	
	public void switchTo(GameState state) throws Exception {
		Require.that(state, "state").isNotNull();
		Require.that(_states, "states").doesNotContain(state);
		leave(getActiveState());
		enter(state);
	}
	
	public GameState getActiveState() {
		return _states.get(_states.size() - 1);
	}
	
	public Boolean isActive(GameState state) throws Exception {
		Require.that(state, "state").isNotNull();
		return getActiveState().equals(state);
	}
	
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		for (int index = 0; index < _states.size(); index++) {
			_states.get(index).render(container, game, g);
		}
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta) {
		if (!isEmpty()) {
			getActiveState().update(container, game, delta);
		}
		//for (int index = _states.size() - 1; index >= 0; index--) {
		//	_states.get(index).update(container, game, delta);
		//}
	}
}
