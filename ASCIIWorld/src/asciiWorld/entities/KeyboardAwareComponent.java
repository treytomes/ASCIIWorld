package asciiWorld.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.state.StateBasedGame;

public class KeyboardAwareComponent extends EntityComponent implements KeyListener {
	
	//private Boolean _addedToKeyListeners;
	private Input _input;

	public KeyboardAwareComponent(Entity owner) {
		super(owner);
		//_addedToKeyListeners = false;
		_input = null;
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int deltaTime) {
		if (_input == null) {
			setInput(container.getInput());
		}
		super.update(container, game, deltaTime);
	}

	@Override
	public void setInput(Input input) {
		if (_input != null) {
			_input.removeKeyListener(this);
		}
		_input = input;
		_input.addKeyListener(this);
	}
	
	public Input getInput() {
		return _input;
	}

	@Override
	public boolean isAcceptingInput() {
		return true;
	}

	@Override
	public void inputEnded() {
	}

	@Override
	public void inputStarted() {
	}

	@Override
	public void keyPressed(int key, char c) {
	}

	@Override
	public void keyReleased(int key, char c) {
	}
}
