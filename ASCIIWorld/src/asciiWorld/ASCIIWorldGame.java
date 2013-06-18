package asciiWorld;

import java.io.File;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
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
	
	private static final String PATH_SETTINGS = "resources/settings.xml";
	
	private int _screenWidth;
	private int _screenHeight;
	private Boolean _showFPS;
	private Boolean _fullScreen;

	public ASCIIWorldGame() {
		super("ASCII World");
		
		_screenWidth = 800;
		_screenHeight = 600;
		_showFPS = false;
		_fullScreen = false;
		
		loadSettings();
	}
	
	public int getScreenWidth() {
		return _screenWidth;
	}
	
	public int getScreenHeight() {
		return _screenHeight;
	}
	
	public Boolean getShowFPS() {
		return _showFPS;
	}
	
	public Boolean getFullScreen() {
		return _fullScreen;
	}
	
	private void loadSettings() {
		try {
			Element settingsElement = (Element)new SAXBuilder().build(new File(PATH_SETTINGS)).getRootElement();
			
			List<Element> propertyElements = settingsElement.getChildren("Property");
			if (propertyElements != null) {
				for (Element propertyElement : propertyElements) {
					parseProperty(propertyElement);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseProperty(Element propertyElement) throws Exception {
		String key = propertyElement.getAttribute("key").getValue();
		String value = propertyElement.getAttribute("value").getValue();
		
		if ((key == null) || (value == null)) {
			throw new Exception("Incomplete property definition.");
		}
		
		switch (key) {
		case "ScreenWidth":
			_screenWidth = Integer.parseInt(value);
			break;
		case "ScreenHeight":
			_screenHeight = Integer.parseInt(value);
			break;
		case "ShowFPS":
			_showFPS = Boolean.parseBoolean(value);
			break;
		case "FullScreen":
			_fullScreen = Boolean.parseBoolean(value);
			break;
		default:
			throw new Exception(String.format("Unknown property key: %s", key));
		}
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
