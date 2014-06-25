package asciiWorld;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.states.AudioTestsState;
import asciiWorld.states.ConsoleState;
import asciiWorld.states.MainMenuState;
import asciiWorld.states.SlickGameplayState;
import asciiWorld.states.TextEditorState;
import asciiWorld.ui.RootVisualPanel;

public class ASCIIWorldGame extends StateBasedGame {
	
	public static final int STATE_MAINMENU = 0;
	public static final int STATE_GAMEPLAY = 1;
	public static final int STATE_TEXTEDITOR = 2;
	public static final int STATE_CONSOLE = 3;
	public static final int STATE_AUDIOTESTS = 4;
	
	private ConfigurationProperties config;

	public ASCIIWorldGame() {
		super("ASCII World");
		
		this.config = new ConfigurationProperties();
	}
	
	public int getScreenWidth() {
		return config.getScreenWidth();
	}
	
	public int getScreenHeight() {
		return config.getScreenHeight();
	}
	
	public Boolean getShowFPS() {
		return config.getShowFPS();
	}
	
	public Boolean getFullScreen() {
		return config.getFullscreen();
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		container.setShowFPS(getShowFPS());
		container.setVSync(true);
		container.setAlwaysRender(true);
		
		try {
			RootVisualPanel.initialize(container);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to initialize the RootVisualPanel.");
		}
		
		addState(new MainMenuState(STATE_MAINMENU));
		addState(new SlickGameplayState(STATE_GAMEPLAY));
		addState(new TextEditorState(STATE_TEXTEDITOR));
		addState(new ConsoleState(STATE_CONSOLE));
		addState(new AudioTestsState(STATE_AUDIOTESTS));
	}
	
	public static void main(String[] args) {
		try {
			ASCIIWorldGame game = new ASCIIWorldGame();
			AppGameContainer app = new AppGameContainer(game);
			app.setDisplayMode(game.getScreenWidth(), game.getScreenHeight(), game.getFullScreen());
			app.setIcon("resources/gfx/icon.png");
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
