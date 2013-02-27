package asciiWorld.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.MouseListener;
import org.newdawn.slick.state.StateBasedGame;

public abstract class InputAwareComponent extends EntityComponent implements KeyListener, MouseListener {
	
	//private Boolean _addedToKeyListeners;
	private Input _input;

	public InputAwareComponent(Entity owner) {
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
			_input.removeMouseListener(this);
		}
		_input = input;
		_input.addKeyListener(this);
		_input.addMouseListener(this);
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

	@Override
	public void mouseWheelMoved(int change) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		// TODO Auto-generated method stub
		
	}
}
