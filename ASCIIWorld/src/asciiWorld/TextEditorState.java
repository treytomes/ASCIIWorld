package asciiWorld;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
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
	
	private UnicodeFont _font;
	private RootVisualPanel _ui;
	
	public TextEditorState(int stateID) {
		_stateID = stateID;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		_font = FontFactory.get().getDefaultFont();
		
		try {
			_ui = generateUI(container, game);
		} catch (Exception e) {
			System.err.println("Unable to generate the user interface.");
		}
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException {
		_scriptingWindow.close(container);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.clear();
		_ui.render(g);
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		_ui.update(container, delta);
	}

	@Override
	public int getID() {
		return _stateID;
	}

	public RootVisualPanel generateUI(final GameContainer container, final StateBasedGame game) throws Exception {
		_scriptingWindow = new ScriptingWindow(container, new Rectangle(90, 90, 660, 500 + 42 + 10));
		
		int numberOfMenuOptions = 2;
		StackPanel menuButtonPanel = new StackPanel(new Rectangle(container.getWidth() - 202 - 5, 5, 202, 42 * numberOfMenuOptions), Orientation.Vertical);
		menuButtonPanel.addChild(Button.createStateTransitionButton("Main Menu", game, ASCIIWorldGame.STATE_MAINMENU));
		menuButtonPanel.addChild(Button.createActionButton("Exit :-(", new MethodBinding(container, "exit")));
		
		RootVisualPanel root = new RootVisualPanel(container);
		root.addChild(new Label(new Vector2f(10, 10), _font, "Text Editor", Color.red)); // create the title label
		root.addChild(new Label(new Vector2f(10, 30), _font, "Press F11 to execute the Javascript.", Color.red)); // create the title label
		root.addChild(_scriptingWindow);
		root.addChild(menuButtonPanel);
		return root;
	}
}
