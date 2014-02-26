package asciiWorld.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.ASCIIWorldGame;
import asciiWorld.ui.Button;
import asciiWorld.ui.Label;
import asciiWorld.ui.MethodBinding;
import asciiWorld.ui.Orientation;
import asciiWorld.ui.RootVisualPanel;
import asciiWorld.ui.StackPanel;
import asciiWorld.ui.UIFactory;

public class MainMenuState extends BasicGameState {
	
	private int _stateID = -1;
	
	public MainMenuState(int stateID) {
		_stateID = stateID;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.enter(container, game);
		
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
		super.leave(container, game);
		
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

	private void generateUI(GameContainer container, StateBasedGame game) throws Exception {
		Rectangle containerBounds = new Rectangle(0, 0, container.getWidth(), container.getHeight());
		
		int margin = 5;
		int buttonWidth = 202;
		int buttonHeight = 42;
		int numberOfMenuOptions = 5;
		StackPanel mainMenuButtonPanel = new StackPanel(new Rectangle(containerBounds.getWidth() - buttonWidth - margin, margin, buttonWidth, buttonHeight * numberOfMenuOptions), Orientation.Vertical);
		mainMenuButtonPanel.addChild(Button.createStateTransitionButton("New Game! :-D", game, ASCIIWorldGame.STATE_GAMEPLAY));
		mainMenuButtonPanel.addChild(Button.createStateTransitionButton("Script Console", game, ASCIIWorldGame.STATE_CONSOLE));
		mainMenuButtonPanel.addChild(Button.createStateTransitionButton("Text Editor", game, ASCIIWorldGame.STATE_TEXTEDITOR));
		mainMenuButtonPanel.addChild(Button.createStateTransitionButton("Audio Tests", game, ASCIIWorldGame.STATE_AUDIOTESTS));
		mainMenuButtonPanel.addChild(Button.createActionButton("Exit :-(", new MethodBinding(container, "exit")));
		
		RootVisualPanel root = RootVisualPanel.get();
		root.addChild(new Label(new Vector2f(10, 10), "ASCII World", Color.red));
		root.addChild(new Button("A", new Rectangle(50, 50, 50, 50)));
		root.addChild(new Button("B", new Rectangle(75, 75, 50, 50)));
		root.addChild(mainMenuButtonPanel);
		root.addChild(UIFactory.get().getResource("optionButtonPanel"));
	}
}