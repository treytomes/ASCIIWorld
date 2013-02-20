package asciiWorld;

import org.mozilla.javascript.Undefined;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
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
import asciiWorld.ui.StackPanel;

public class ConsoleState extends BasicGameState implements KeyListener {
	
	private static final int CURSOR_BLINK_INTERVAL = 500;
	private static final String TEXT_CURSOR = "_";
	private static final Color TEXT_COLOR = Color.yellow;
	private static final int TEXT_SIZE = 12;
	private static String TEXT_PROMPT = "> ";
	
	private int _stateID;
	private RootVisualPanel _ui;
	private JavascriptContext _context;
	private String _allText;
	private String _currentLine;
	private UnicodeFont _textFont;
	private int _cursorIndex;
	private int _totalTime;
	
	public ConsoleState(int stateID) {
		_stateID = stateID;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		_textFont = FontFactory.get().getResource(TEXT_SIZE);

		try {
			_ui = generateUI(container, game);
		} catch (Exception e) {
			System.err.println("Unable to generate the user interface.");
		}

		_context = new JavascriptContext();
		_context.addObjectToContext(this, "console");
		try {
			_context.executeScript("function clear() {console.clear();}");
		} catch (Exception e) {
			_ui.showMessageBox(true, e.getMessage(), "Error!");
		}
		
		_allText = TEXT_PROMPT;
		_currentLine = "";
		_cursorIndex = 0;
		_totalTime = 0;
	}
	
	public void clear() {
		_allText = "";
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		container.getInput().enableKeyRepeat();
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException {
		container.getInput().disableKeyRepeat();
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		_totalTime += delta;
		_ui.update(container, delta);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.clear();
		
		String renderingText = _allText.concat(_currentLine);
		
		Boolean cursorWasRendered = false;
		Vector2f tileSize = new Vector2f(_textFont.getWidth("*"), _textFont.getLineHeight());
		int textMarginTop = 30;
		int textMarginLeft = 5;
		Vector2f position = new Vector2f(textMarginLeft, textMarginTop);
		for (int index = 0; index < renderingText.length(); index++) {
			if ((index == (_allText.length() + _cursorIndex)) && (((_totalTime / CURSOR_BLINK_INTERVAL) % 2) == 1)) {
				_textFont.drawString(position.x, position.y, TEXT_CURSOR, TEXT_COLOR);
				cursorWasRendered = true;
			}
			
			switch (renderingText.charAt(index)) {
			case '\n':
				position.y += tileSize.y;
				position.x = textMarginLeft;
				break;
			case '\t':
				float tabSize = 4 * tileSize.x;
				position.x += (tabSize - ((position.x + tileSize.x) % tabSize));
				break;
			default:
				_textFont.drawString(position.x, position.y, Character.toString(renderingText.charAt(index)), TEXT_COLOR);
				position.x += tileSize.x;
				break;
			}
			
			if (position.x > container.getWidth() - tileSize.x - textMarginLeft) {
				position.x = textMarginLeft;
				position.y += tileSize.y;
			}
			if (position.y > container.getHeight() - tileSize.y - textMarginTop) {
				removeFirstLineOfText();
				break;
			}
		}
		
		if (!cursorWasRendered) {
			if (((_totalTime / CURSOR_BLINK_INTERVAL) % 2) == 1) {
				if (position.y <= container.getHeight() - tileSize.y - textMarginTop) {
					_textFont.drawString(position.x, position.y, TEXT_CURSOR, TEXT_COLOR);
					cursorWasRendered = true;
				}
			}
		}
		
		_ui.render(g);
	}
	
	private void removeFirstLineOfText() {
		int index = _allText.indexOf('\n');
		_allText = _allText.substring(index + 1, _allText.length());
	}
	
	@Override
	public void keyPressed(int key, char c) {
		if ((' ' <= c) && (c <= '~')) {
			_currentLine = _currentLine.substring(0, _cursorIndex).concat(Character.toString(c)).concat((_cursorIndex == _currentLine.length()) ? "" : _currentLine.substring(_cursorIndex, _currentLine.length()));
			_cursorIndex++;
		} else {
			switch (key) {
			case Input.KEY_ENTER:
				//_currentLine = _currentLine.substring(0, _cursorIndex).concat("\n").concat((_cursorIndex == _currentLine.length()) ? "" : _currentLine.substring(_cursorIndex, _currentLine.length()));
				//_cursorIndex++;
				_currentLine = _currentLine.concat("\n");
				executeScript();
				break;
			case Input.KEY_TAB:
				_currentLine = _currentLine.substring(0, _cursorIndex).concat("\t").concat((_cursorIndex == _currentLine.length()) ? "" : _currentLine.substring(_cursorIndex, _currentLine.length()));
				_cursorIndex++;
				break;
			case Input.KEY_HOME:
				moveCursorToBeginningOfLine();
				break;
			case Input.KEY_END:
				moveCursorToEndOfLine();
				break;
			case Input.KEY_BACK:
				if (_cursorIndex > 0) {
					_currentLine = _currentLine.substring(0, _cursorIndex - 1).concat((_cursorIndex == _currentLine.length()) ? "" : _currentLine.substring(_cursorIndex, _currentLine.length()));
					_cursorIndex--;
				}
				break;
			case Input.KEY_DELETE:
				if (_cursorIndex > 0) {
					_currentLine = _currentLine.substring(0, _cursorIndex).concat(((_cursorIndex + 1) == _currentLine.length()) ? "" : _currentLine.substring(_cursorIndex + 1, _currentLine.length()));
				}
				break;
			case Input.KEY_UP:
				moveCursorToBeginningOfLine();
				if (_cursorIndex > 0) {
					_cursorIndex--;
					moveCursorToBeginningOfLine();
				}
				break;
			case Input.KEY_DOWN:
				moveCursorToEndOfLine();
				if (_cursorIndex < _currentLine.length()) {
					_cursorIndex++;
					moveCursorToBeginningOfLine();
				}
				break;
			case Input.KEY_LEFT:
				_cursorIndex--;
				if (_cursorIndex < 0) {
					_cursorIndex = 0;
				}
				break;
			case Input.KEY_RIGHT: 
				_cursorIndex++;
				if (_cursorIndex > _currentLine.length()) {
					_cursorIndex = _currentLine.length();
				}
				break;
			}
		}
	}
	
	private void executeScript() {
		_allText = _allText.concat(_currentLine);
		StringBuilder sb = new StringBuilder();
		try {
			Object result = _context.executeScript(_currentLine);
			if (result != Undefined.instance) {
				sb.append(JavascriptContext.toString(result)).append("\n");
			}
		} catch (Exception e) {
			sb.append(e.getMessage()).append("\n");
		}
		sb.append(TEXT_PROMPT);
		_allText = _allText.concat(sb.toString());
		_currentLine = "";
		_cursorIndex = 0;
	}
	
	private void moveCursorToBeginningOfLine() {
		if (!cursorIsAtBeginningOfLine()) {
			while (_cursorIndex > 0) {
				_cursorIndex--;
				if (_currentLine.charAt(_cursorIndex) == '\n') {
					_cursorIndex++;
					break;
				}
			}
		}
	}
	
	private void moveCursorToEndOfLine() {
		if (!cursorIsAtEndOfLine()) {
			while (true) {
				_cursorIndex++;
				if ((_cursorIndex == _currentLine.length()) || (_currentLine.charAt(_cursorIndex) == '\n')) {
					break;
				}
			}
		}
	}
	
	private Boolean cursorIsAtBeginningOfLine() {
		return (_cursorIndex == 0) || (_currentLine.charAt(_cursorIndex - 1) == '\n');
	}
	
	private Boolean cursorIsAtEndOfLine() {
		return (_cursorIndex == _currentLine.length()) || (_currentLine.charAt(_cursorIndex) == '\n');
	}

	@Override
	public int getID() {
		return _stateID;
	}

	private RootVisualPanel generateUI(final GameContainer container, final StateBasedGame game) throws Exception {
		int numberOfMenuOptions = 2;
		StackPanel menuButtonPanel = new StackPanel(new Rectangle(container.getWidth() - 202 - 5, 5, 202, 42 * numberOfMenuOptions), Orientation.Vertical);
		menuButtonPanel.addChild(Button.createStateTransitionButton("Main Menu", game, ASCIIWorldGame.STATE_MAINMENU));
		menuButtonPanel.addChild(Button.createActionButton("Exit :-(", new MethodBinding(container, "exit")));
		
		RootVisualPanel root = new RootVisualPanel(container);
		root.addChild(new Label(new Vector2f(10, 10), "Console", Color.red)); // create the title label
		root.addChild(menuButtonPanel);
		return root;
	}
}
