package asciiWorld;

import java.awt.Font;

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
import asciiWorld.ui.ButtonClickedEvent;
import asciiWorld.ui.Label;
import asciiWorld.ui.MethodBinding;
import asciiWorld.ui.Orientation;
import asciiWorld.ui.RootVisualPanel;
import asciiWorld.ui.StackPanel;

public class MainMenuState extends BasicGameState {
	
	enum MenuOption {
		None,
		NewGame,
		Exit
	}
	
	int _stateID = -1;
	
	UnicodeFont _font = null;
	RootVisualPanel _ui = null;
	
	MainMenuState(int stateID) {
		_stateID = stateID;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		_font = FontFactory.get().getResource("Courier New", Font.BOLD, 20);
		
		try {
			_ui = generateUI(container, game);
		} catch (Exception e) {
			System.err.println("Unable to generate the user interface.");
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.clear();
		_ui.render(g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		_ui.update(container, delta);
	}

	@Override
	public int getID() {
		return _stateID;
	}

	public RootVisualPanel generateUI(GameContainer container, StateBasedGame game)
			throws Exception {
		Rectangle containerBounds = new Rectangle(0, 0, container.getWidth(), container.getHeight());
		
		int numberOfMenuOptions = 4;
		StackPanel mainMenuButtonPanel = new StackPanel(new Rectangle(containerBounds.getWidth() - 202 - 5, 5, 202, 42 * numberOfMenuOptions), Orientation.Vertical);
		mainMenuButtonPanel.addChild(Button.createStateTransitionButton("New Game! :-D", game, ASCIIWorldGame.STATE_GAMEPLAY));
		mainMenuButtonPanel.addChild(Button.createStateTransitionButton("Script Console", game, ASCIIWorldGame.STATE_CONSOLE));
		mainMenuButtonPanel.addChild(Button.createStateTransitionButton("Text Editor", game, ASCIIWorldGame.STATE_TEXTEDITOR));
		mainMenuButtonPanel.addChild(Button.createActionButton("Exit :-(", new MethodBinding(container, "exit")));
		
		StackPanel optionButtonPanel = new StackPanel(new Rectangle(100, 100, 350, 500), Orientation.Vertical);
		optionButtonPanel.addChild(createDialogTestButton());
		optionButtonPanel.addChild(createOptionButton("Option 2"));
		optionButtonPanel.addChild(createOptionButton("Option 3"));
		optionButtonPanel.addChild(createOptionButton("Option 4"));
		optionButtonPanel.addChild(createOptionButton("Option 5"));
		
		RootVisualPanel root = new RootVisualPanel(container);
		root.addChild(new Label(new Vector2f(10, 10), _font, "ASCII World", Color.red));
		root.addChild(new Button("A", new Rectangle(50, 50, 50, 50)));
		root.addChild(new Button("B", new Rectangle(75, 75, 50, 50)));
		root.addChild(mainMenuButtonPanel);
		root.addChild(optionButtonPanel);
		
		return root;
	}
	
	private Button createDialogTestButton()
			throws Exception {
		Button btn = createOptionButton("Dialog Test");
		btn.addClickListener(new ButtonClickedEvent() {
			@Override
			public void click(Button button) {
				try {
					button.getRoot().showMessageBox(true, TextFactory.get().getResource("longWinded"), "Warning!");
				} catch (Exception e) {
					System.err.println("Unable to open the dialog window.");
				}
			}
		});
		return btn;
	}
	
	private Button createOptionButton(String text)
			throws Exception {
		Button btn = new Button(text);
		btn.getMargin().setValue(10);
		return btn;
	}
}