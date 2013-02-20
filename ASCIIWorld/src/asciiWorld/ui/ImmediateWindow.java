package asciiWorld.ui;

import java.util.Stack;

import org.mozilla.javascript.Undefined;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.FontFactory;
import asciiWorld.JavascriptContext;

/**
 * For immediate Javascript-executions.
 * 
 * @author ttomes
 *
 */
public class ImmediateWindow extends Border implements KeyListener {

	private static final int CURSOR_BLINK_INTERVAL = 500;
	private static final Color COLOR_BORDER_TEXT = new Color(0.0f, 0.75f, 0.5f);
	private static final Color COLOR_BORDER_WINDOW_FILL = new Color(0.5f, 0.5f, 1.0f, 0.25f);
	private static final String TEXT_CURSOR = "_";
	private static final int TEXT_SIZE = 12;
	private static final Color TEXT_COLOR = Color.yellow;
	private static String TEXT_PROMPT = "> ";
	
	private Border _textContainer;
	
	private String _allText;
	private String _currentLine;
	private UnicodeFont _textFont;
	private int _cursorIndex;

	private JavascriptContext _context;
	private Stack<String> _history;

	/**
	 * This is used to manage the cursor blink.
	 */
	private int _totalTime;
	
	public ImmediateWindow(GameContainer container, Rectangle bounds)
			throws Exception {
		super(new RoundedRectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 8), COLOR_BORDER_WINDOW_FILL, true);
		
		createUI();
		
		container.getInput().enableKeyRepeat();
		container.getInput().addKeyListener(this);

		_textFont = FontFactory.get().getResource(TEXT_SIZE);

		_context = new JavascriptContext();
		_context.addObjectToContext(this, "console");
		_context.executeScript("function clear() {console.clear();}");
		
		_history = new Stack<String>();
		
		_allText = TEXT_PROMPT;
		_currentLine = "";
		_cursorIndex = 0;
		_totalTime = 0;
	}
	
	public ImmediateWindow(GameContainer container)
			throws Exception {
		this(container, new Rectangle(0, 0, container.getWidth(), container.getHeight()));
	}
	
	public void clear() {
		_allText = "";
	}
	
	public void clearAllText() {
		_allText = TEXT_PROMPT;
	}
	
	public void close(GameContainer container) {
		container.getInput().disableKeyRepeat();
		container.getInput().removeKeyListener(this);
	}
	
	@Override
	public void update(GameContainer container, int delta) {
		_totalTime += delta;
		super.update(container, delta);
	}
	
	@Override
	public void render(Graphics g) {
		super.render(g);
		
		String renderingText = _allText.concat(_currentLine);
		
		Boolean cursorWasRendered = false;
		Vector2f tileSize = new Vector2f(_textFont.getWidth("*"), _textFont.getLineHeight());
		int textMargin = 5;
		Vector2f position = new Vector2f(_textContainer.getBounds().getMinX() + textMargin, _textContainer.getBounds().getMinY() + textMargin);
		for (int index = 0; index < renderingText.length(); index++) {
			if ((index == (_allText.length() + _cursorIndex)) && (((_totalTime / CURSOR_BLINK_INTERVAL) % 2) == 1)) {
				_textFont.drawString(position.x, position.y, TEXT_CURSOR, TEXT_COLOR);
				cursorWasRendered = true;
			}
			
			switch (renderingText.charAt(index)) {
			case '\n':
				position.y += tileSize.y;
				position.x = _textContainer.getBounds().getMinX() + textMargin;
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
			
			if (position.x > _textContainer.getBounds().getMaxX() - tileSize.x - textMargin) {
				position.x = _textContainer.getBounds().getMinX() + textMargin;
				position.y += tileSize.y;
			}
			if (position.y > _textContainer.getBounds().getMaxY() - tileSize.y - textMargin) {
				removeFirstLineOfText();
				break;
			}
		}
		
		if (!cursorWasRendered) {
			if (((_totalTime / CURSOR_BLINK_INTERVAL) % 2) == 1) {
				if (position.y <= _textContainer.getBounds().getMaxY() - tileSize.y - textMargin) {
					_textFont.drawString(position.x, position.y, TEXT_CURSOR, TEXT_COLOR);
					cursorWasRendered = true;
				}
			}
		}
	}
	
	private void removeFirstLineOfText() {
		int index = _allText.indexOf('\n');
		_allText = _allText.substring(index + 1, _allText.length());
	}
	
	private void createUI() throws Exception {
		final RoundedRectangle windowBounds = (RoundedRectangle)getBounds();
		
		Color textContainerFillColor = new Color(COLOR_BORDER_TEXT);
		textContainerFillColor.a = 0.25f;
		_textContainer = new Border(new Rectangle(windowBounds.getX() + 10, windowBounds.getY() + 10, windowBounds.getWidth() - 20, windowBounds.getHeight() - 42 - 20), textContainerFillColor, true);
		_textContainer.setContent(new Border(COLOR_BORDER_TEXT, false));
		
		CanvasPanel windowCanvas = new CanvasPanel();
		windowCanvas.addChild(_textContainer);
		windowCanvas.addChild(getButtons(windowBounds));
		
		Color windowBorderColor = new Color(COLOR_BORDER_WINDOW_FILL);
		windowBorderColor.a = 1.0f;
		Border windowBorder = new Border(getBounds(), windowBorderColor, false);
		windowBorder.setContent(windowCanvas);
		setContent(windowBorder);
	}
	
	protected StackPanel getButtons(RoundedRectangle dialogBounds) throws Exception {
		float myWidth = dialogBounds.getWidth();
		StackPanel buttonPanel = new StackPanel(new Rectangle(dialogBounds.getMinX() + (dialogBounds.getWidth() - myWidth) / 2, dialogBounds.getMaxY() - 42 - 5, myWidth, 42));
		buttonPanel.addChild(Button.createActionButton("Clear", new MethodBinding(this, "clearAllText")));
		return buttonPanel;
	}
	
	@Override
	public void keyPressed(int key, char c) {
		if ((' ' <= c) && (c <= '~')) {
			_currentLine = _currentLine.substring(0, _cursorIndex).concat(Character.toString(c)).concat((_cursorIndex == _currentLine.length()) ? "" : _currentLine.substring(_cursorIndex, _currentLine.length()));
			_cursorIndex++;
		} else {
			switch (key) {
			case Input.KEY_ENTER:
				//_currentLine = _currentLine.substring(0, _cursorIndex).concat("\n").concat((_cursorIndex == _text.length()) ? "" : _text.substring(_cursorIndex, _text.length()));
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
				if (_cursorIndex < _currentLine.length()) {
					_currentLine = _currentLine.substring(0, _cursorIndex).concat(((_cursorIndex + 1) == _currentLine.length()) ? "" : _currentLine.substring(_cursorIndex + 1, _currentLine.length()));
				}
				break;
			case Input.KEY_UP:
				_currentLine = _history.pop();
				_history.insertElementAt(_currentLine, 0);
				break;
			case Input.KEY_DOWN:
				_currentLine = _history.elementAt(0);
				_history.remove(0);
				_history.push(_currentLine);
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
				_context.addObjectToContext(result, "_");
			}
			_history.push(_currentLine.trim());
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
	public void setInput(Input input) {
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
	public void keyReleased(int key, char c) {
	}
}
