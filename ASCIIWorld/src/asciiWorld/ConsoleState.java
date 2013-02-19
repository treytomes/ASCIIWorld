package asciiWorld;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.tiles.TileSet;

public class ConsoleState extends BasicGameState implements KeyListener {
	
	private static final int CURSOR_BLINK_INTERVAL = 500;
	private static final String CURSOR_TEXT = "_";
	
	private int _stateID;
	private Context _context;
	private String _text;
	private String _scriptOutput;
	private TileSet _font;
	private int _cursorIndex;
	private int _totalTime;
	
	public ConsoleState(int stateID) {
		_stateID = stateID;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		try {
			_font = TileSet.load("resources/tileSets/OEM437.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		// Create and enter a Context.  The Context stores information about the execution environment of a script.
		_context = Context.enter();
		
		_scriptOutput = "";
		try {
			// Initialize the standard objects (Object, Function, etc.).
			// This must be done before scripts can be executed.
			// Returns a scope object that we use in later calls.
			Scriptable scope = _context.initStandardObjects();
			
			//String script = "Math.cos(Math.PI)";
			String script = "function f(x) { return x + 1 } f(7)";
			
			// Now evaluate the script string.
			Object result = _context.evaluateString(scope, script, "<cmd>", 1, null);
			
			// Convert the result to a string and print it.
			_scriptOutput = Context.toString(result);
		} finally {
			
		}
		
		container.getInput().enableKeyRepeat();
		_text = "";
		_cursorIndex = 0;
		_totalTime = 0;
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException {
		Context.exit();
		container.getInput().disableKeyRepeat();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		
		Boolean cursorWasRendered = false;
		Vector2f tileSize = _font.getTileSize();
		Vector2f position = new Vector2f(0, 0);
		for (int index = 0; index < _text.length(); index++) {
			if ((index == _cursorIndex) && (((_totalTime / CURSOR_BLINK_INTERVAL) % 2) == 1)) {
				_font.drawString(CURSOR_TEXT, position, Color.green);
				cursorWasRendered = true;
			}
			
			switch (_text.charAt(index)) {
			case '\n':
				position.y += tileSize.y;
				position.x = 0;
				break;
			case '\t':
				float tabSize = 4 * tileSize.x;
				position.x += (tabSize - (position.x % tabSize));
				break;
			default:
				_font.draw((int)_text.charAt(index), position, Color.green);
				position.x += _font.getTileSize().x;
				break;
			}
			
			if (position.x > container.getWidth() - tileSize.x) {
				position.x = 0;
				position.y += tileSize.y;
			}
		}
		
		if (!cursorWasRendered) {
			if (((_totalTime / CURSOR_BLINK_INTERVAL) % 2) == 1) {
				_font.drawString(CURSOR_TEXT, position, Color.green);
				cursorWasRendered = true;
			}
		}
		
		_font.drawString(_scriptOutput, new Vector2f(100, 100), Color.magenta);
		
		_font.drawString(Integer.toString(_totalTime), new Vector2f(200, 200));
	}
	
	@Override
	public void keyPressed(int key, char c) {
		if ((' ' <= c) && (c <= '~')) {
			_text = _text.substring(0, _cursorIndex).concat(Character.toString(c)).concat((_cursorIndex == _text.length()) ? "" : _text.substring(_cursorIndex, _text.length()));
			_cursorIndex++;
		} else {
			switch (key) {
			case Input.KEY_ENTER:
				_text = _text.substring(0, _cursorIndex).concat("\n").concat((_cursorIndex == _text.length()) ? "" : _text.substring(_cursorIndex, _text.length()));
				_cursorIndex++;
				break;
			case Input.KEY_TAB:
				_text = _text.substring(0, _cursorIndex).concat("\t").concat((_cursorIndex == _text.length()) ? "" : _text.substring(_cursorIndex, _text.length()));
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
					_text = _text.substring(0, _cursorIndex - 1).concat((_cursorIndex == _text.length()) ? "" : _text.substring(_cursorIndex, _text.length()));
					_cursorIndex--;
				}
				break;
			case Input.KEY_DELETE:
				if (_cursorIndex > 0) {
					_text = _text.substring(0, _cursorIndex).concat(((_cursorIndex + 1) == _text.length()) ? "" : _text.substring(_cursorIndex + 1, _text.length()));
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
				if (_cursorIndex < _text.length()) {
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
				if (_cursorIndex > _text.length()) {
					_cursorIndex = _text.length();
				}
				break;
			}
		}
	}
	
	private void moveCursorToBeginningOfLine() {
		if (!cursorIsAtBeginningOfLine()) {
			while (_cursorIndex > 0) {
				_cursorIndex--;
				if (_text.charAt(_cursorIndex) == '\n') {
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
				if ((_cursorIndex == _text.length()) || (_text.charAt(_cursorIndex) == '\n')) {
					break;
				}
			}
		}
	}
	
	private Boolean cursorIsAtBeginningOfLine() {
		return (_cursorIndex == 0) || (_text.charAt(_cursorIndex - 1) == '\n');
	}
	
	private Boolean cursorIsAtEndOfLine() {
		return (_cursorIndex == _text.length()) || (_text.charAt(_cursorIndex) == '\n');
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		_totalTime += delta;
	}

	@Override
	public int getID() {
		return _stateID;
	}
}
