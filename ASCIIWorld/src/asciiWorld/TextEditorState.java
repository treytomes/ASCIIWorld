package asciiWorld;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.ui.Button;
import asciiWorld.ui.Label;
import asciiWorld.ui.MethodBinding;
import asciiWorld.ui.Orientation;
import asciiWorld.ui.RootVisualPanel;
import asciiWorld.ui.ScriptingWindow;
import asciiWorld.ui.StackPanel;

public class TextEditorState extends BasicGameState {
	
	private int _stateID;
	private ScriptingWindow _scriptingWindow;
	
	public TextEditorState(int stateID) {
		_stateID = stateID;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		try {
			generateUI(container, game);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to generate the user interface.");
		}
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException {
		_scriptingWindow.close(container);
		try {
			RootVisualPanel.get().clear();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to clear the RootVisualPanel.");
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.clear();
		try {
			RootVisualPanel.get().render(g);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		try {
			RootVisualPanel.get().update(container, delta);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getID() {
		return _stateID;
	}

	public void generateUI(final GameContainer container, final StateBasedGame game) throws Exception {
		_scriptingWindow = new ScriptingWindow(container, new Rectangle(90, 90, 660, 500 + 42 + 10));
		
		int numberOfMenuOptions = 2;
		StackPanel menuButtonPanel = new StackPanel(new Rectangle(container.getWidth() - 202 - 5, 5, 202, 42 * numberOfMenuOptions), Orientation.Vertical);
		menuButtonPanel.addChild(Button.createStateTransitionButton("Main Menu", game, ASCIIWorldGame.STATE_MAINMENU));
		menuButtonPanel.addChild(Button.createActionButton("Exit :-(", new MethodBinding(container, "exit")));
		
		RootVisualPanel root = RootVisualPanel.get();
		root.addChild(new Label(new Vector2f(10, 10), "Text Editor", Color.red)); // create the title label
		root.addChild(new Label(new Vector2f(10, 30), "Press F11 to execute the Javascript.", Color.red)); // create the title label
		root.addChild(_scriptingWindow);
		root.addChild(menuButtonPanel);
	}
}
