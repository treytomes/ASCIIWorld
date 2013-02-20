package asciiWorld;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.ui.Button;
import asciiWorld.ui.ImmediateWindow;
import asciiWorld.ui.Label;
import asciiWorld.ui.MethodBinding;
import asciiWorld.ui.Orientation;
import asciiWorld.ui.RootVisualPanel;
import asciiWorld.ui.StackPanel;

public class ConsoleState extends BasicGameState implements KeyListener {
		
	private int _stateID;
	private RootVisualPanel _ui;
	
	public ConsoleState(int stateID) {
		_stateID = stateID;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		try {
			_ui = generateUI(container, game);
		} catch (Exception e) {
			System.err.println("Unable to generate the user interface.");
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		_ui.update(container, delta);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.clear();
		_ui.render(g);
	}

	@Override
	public int getID() {
		return _stateID;
	}

	private RootVisualPanel generateUI(final GameContainer container, final StateBasedGame game) throws Exception {
		int buttonWidth = 202;
		int buttonHeight = 42;
		int margin = 5;
		int topMargin = 30 + margin;
		int bottomMargin = topMargin + margin;
		int rightMargin = buttonWidth + margin + margin + margin;
		int numberOfMenuOptions = 2;
		StackPanel menuButtonPanel = new StackPanel(new Rectangle(container.getWidth() - buttonWidth - margin, margin, buttonWidth, buttonHeight * numberOfMenuOptions), Orientation.Vertical);
		menuButtonPanel.addChild(Button.createStateTransitionButton("Main Menu", game, ASCIIWorldGame.STATE_MAINMENU));
		menuButtonPanel.addChild(Button.createActionButton("Exit :-(", new MethodBinding(container, "exit")));
		
		RootVisualPanel root = new RootVisualPanel(container);
		root.addChild(new Label(new Vector2f(10, 10), "Script Console", Color.red)); // create the title label
		root.addChild(menuButtonPanel);

		root.addChild(new ImmediateWindow(container, new Rectangle(margin, topMargin, container.getWidth() - rightMargin, container.getHeight() - bottomMargin)));
		return root;
	}
}
