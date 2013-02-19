package asciiWorld.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.FontFactory;
import asciiWorld.tiles.StaticText;

public class Label extends FrameworkElement {

	private static UnicodeFont _defaultFont = null;
	
	private Rectangle _bounds;
	private UnicodeFont _font = null;
	private Object _textBinding = null;
	private Color _color;
	
	private HorizontalAlignment _horizontalContentAlignment;
	private VerticalAlignment _verticalContentAlignment;

	private String _lastFrameText;
	
	private static UnicodeFont getDefaultFont() throws SlickException {
		if (_defaultFont == null) {
			_defaultFont = FontFactory.get().getDefaultFont();
		}
		return _defaultFont;
	}

	public Label(Vector2f position, UnicodeFont font, Object textBinding, Color color) {
		_font = font;
		_color = color;
		_bounds = new Rectangle(position.x, position.y, _font.getWidth(getText()), _font.getHeight(getText()));
		
		setHorizontalContentAlignment(HorizontalAlignment.Center);
		setVerticalContentAlignment(VerticalAlignment.Center);
		setTextBinding(textBinding);
	}
	
	public Label(Vector2f position, UnicodeFont font, String text, Color color) {
		this(position, font, new StaticText(text), color);
	}
	
	public Label(UnicodeFont font, String text, Color color) {
		this(new Vector2f(0, 0), font, new StaticText(text), color);
	}
	
	public Label(Vector2f position, String text, Color color) throws SlickException {
		this(position, getDefaultFont(), new StaticText(text), color);
	}
	
	public HorizontalAlignment getHorizontalContentAlignment() {
		return _horizontalContentAlignment;
	}
	
	public void setHorizontalContentAlignment(HorizontalAlignment value) {
		_horizontalContentAlignment = value;
	}
	
	public VerticalAlignment getVerticalContentAlignment() {
		return _verticalContentAlignment;
	}
	
	public void setVerticalContentAlignment(VerticalAlignment value) {
		_verticalContentAlignment = value;
	}
	
	public Rectangle getBounds() {
		return _bounds;
	}
	
	public UnicodeFont getFont() {
		return _font;
	}
	
	public void setFont(UnicodeFont font) {
		_font = font;
	}
	
	public Object getTextBinding() {
		return _textBinding;
	}
	
	public void setTextBinding(Object value) {
		_textBinding = value;
		resetTextBounds();
	}
	
	public String getText() {
		if (_textBinding == null) {
			return "";
		} else {
			return _textBinding.toString();
		}
	}
	
	public void setText(String text) {
		setTextBinding(new StaticText(text));
	}
	
	public Color getColor() {
		return _color;
	}
	
	public void setColor(Color color) {
		_color = color;
	}
	
	@Override
	public void render(Graphics g) {
		if (_lastFrameText != getText()) {
			resetTextBounds();
		}
		
		Vector2f textPosition = getTextPosition();
		
		String remainingText = getText();
		while (remainingText.length() > 0) {
			switch (remainingText.charAt(0)) {
			case '\n': // System.lineSeparator();
				remainingText = remainingText.substring(1, remainingText.length());
				textPosition.y += _font.getLineHeight();
				continue;
			case ' ':
				remainingText = remainingText.substring(1, remainingText.length());
				continue;
			case '\r':
				continue;
			}
			
			// Get the length of text that will fit on a single line:
			int lengthToChop = 1;
			while (true) {
				if (lengthToChop >= remainingText.length()) {
					break;
				}
				
				String substring = remainingText.substring(0, lengthToChop);
				if (substring.endsWith("\n")) { // System.lineSeparator();
					break;
				}
				if (_font.getWidth(substring) < getBounds().getWidth()) {
					lengthToChop++;
				} else {
					break;
				}
			}
			if (lengthToChop < remainingText.length()) {
				lengthToChop--;
			}
			
			// Prevent words being chopped in half:
			if (lengthToChop < remainingText.length()) {
				String subCheck = remainingText.substring(0, lengthToChop + 1);
				String lineSeparator = "\n"; // System.lineSeparator();
				if (!subCheck.endsWith(lineSeparator)) {
					while ((lengthToChop > 0) && !Character.isWhitespace(remainingText.charAt(lengthToChop - 1))) {
						if (Character.isWhitespace(remainingText.charAt(lengthToChop))) {
							break;
						}
						lengthToChop--;
					}
				}
			}
			// This will also prevent the final space from being rendered on the next line.
			
			// Make sure that the substring is no longer than the length of the remaining text:
			if (lengthToChop > remainingText.length()) {
				lengthToChop = remainingText.length();
			}
			
			// Remove the current line of text from the remaining text:
			String thisLine = (lengthToChop == 0) ? remainingText : remainingText.substring(0, lengthToChop);
			remainingText = (lengthToChop == 0) ? "" : remainingText.substring(lengthToChop, remainingText.length());
			if (remainingText.startsWith("\n")) {
				remainingText = remainingText.substring(1, remainingText.length());
			}
			
			// Render the current line of text:
			_font.drawString(textPosition.x, textPosition.y, thisLine, _color);
			textPosition.y += _font.getHeight(thisLine);
			
			// Ensure that we don't draw outside the lines:
			if ((textPosition.y + _font.getHeight(remainingText)) >= getBounds().getMaxY()) {
				break;
			}
		}
	}

	@Override
	public void moveTo(Vector2f position) {
		_bounds.setLocation(position);
	}

	@Override
	protected Boolean contains(Vector2f point) {
		return getBounds().contains(point.x, point.y);
	}
	
	private void resetTextBounds() {
		_bounds.setWidth(_font.getWidth(getText()));
		_bounds.setHeight(_font.getHeight(getText()));
		_lastFrameText = getText();
	}
	
	private Vector2f getTextPosition() {
		float x = 0;
		float y = 0;

		switch (getHorizontalContentAlignment()) {
		case Left:
			x = getBounds().getX();
			break;
		case Center:
			x = getBounds().getX() + (getBounds().getWidth() - _font.getWidth(getText())) / 2.0f;
			break;
		case Right:
			x = getBounds().getX() + getBounds().getWidth() - _font.getWidth(getText());
			break;
		}
		
		switch (getVerticalContentAlignment()) {
		case Top:
			y = getBounds().getY();
			break;
		case Center:
			y = getBounds().getY() + (getBounds().getHeight() - _font.getHeight(getText())) / 2.0f;
			break;
		case Bottom:
			y = getBounds().getY() + getBounds().getHeight() - _font.getHeight(getText());
			break;
		}
		
		return new Vector2f(x, y);
	}
}