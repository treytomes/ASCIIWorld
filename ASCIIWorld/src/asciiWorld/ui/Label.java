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

	private static final HorizontalAlignment DEFAULT_HORIZONTAL_ALIGNMENT = HorizontalAlignment.Center;
	private static final VerticalAlignment DEFAULT_VERTICAL_ALIGNMENT = VerticalAlignment.Center;
	private static final int DEFAULT_MARGIN = 5;
	private static final TextWrappingMode DEFAULT_TEXT_WRAPPING_MODE = TextWrappingMode.WordWrap;
	
	private static UnicodeFont _defaultFont = null;
	
	private Rectangle _bounds;
	private UnicodeFont _font = null;
	private Object _textBinding = null;
	private Color _color;
	private TextWrappingMode _textWrappingMode;
	
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
		
		setHorizontalContentAlignment(DEFAULT_HORIZONTAL_ALIGNMENT);
		setVerticalContentAlignment(DEFAULT_VERTICAL_ALIGNMENT);
		setTextBinding(textBinding);
		getMargin().setValue(DEFAULT_MARGIN);
		setTextWrappingMode(DEFAULT_TEXT_WRAPPING_MODE);
		
		_bounds = new Rectangle(position.x, position.y, _font.getWidth(getText()), _font.getHeight(getText()));
	}
	
	public Label(UnicodeFont font, Object textBinding, Color color) {
		this(new Vector2f(0, 0), font, textBinding, color);
	}
	
	public Label(Vector2f position, Object textBinding, Color color) throws SlickException {
		this(position, getDefaultFont(), textBinding, color);
	}
	
	public Label(Object textBinding, Color color) throws SlickException {
		this(new Vector2f(0, 0), getDefaultFont(), textBinding, color);
	}
	
	public TextWrappingMode getTextWrappingMode() {
		return _textWrappingMode;
	}
	
	public void setTextWrappingMode(TextWrappingMode value) {
		_textWrappingMode = value;
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
		_lastFrameText = getText();
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
		Rectangle previousWorldClip = setTransform(g);
		//g.setWorldClip(getBounds());
		
		g.pushTransform();
		Vector2f translationPosition = getTextPosition();
		g.translate(translationPosition.x, translationPosition.y);
		
		if (_lastFrameText != getText()) {
			if (getParent() != null) {
				getParent().resetBounds();
			} else {
				resetBounds();
			}
		}
		
		float contentWidth = getBounds().getWidth() - getMargin().getLeftMargin() - getMargin().getRightMargin();
		Vector2f textPosition = new Vector2f(0, 0);
		TextWrappingMode textWrappingMode = getTextWrappingMode();
		
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
				if ((_font.getWidth(substring) < contentWidth) || (textWrappingMode == TextWrappingMode.NoWrap)) {
					lengthToChop++;
				} else {
					break;
				}
			}
			if (lengthToChop < remainingText.length()) {
				lengthToChop--;
			}
			
			if (textWrappingMode == TextWrappingMode.WordWrap) {
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
			}
			
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
		
		g.popTransform();
		
		//g.setWorldClip(null);
		clearTransform(g, previousWorldClip);
	}

	@Override
	public void moveTo(Vector2f position) {
		_bounds.setLocation(position);
	}

	@Override
	protected Boolean contains(Vector2f point) {
		return getBounds().contains(point.x, point.y);
	}
	
	private Vector2f getTextPosition() {
		float x = getBounds().getMinX();
		float y = getBounds().getMinY();

		switch (getHorizontalContentAlignment()) {
		case Left:
			x = x + getMargin().getLeftMargin();
			break;
		case Center:
			x = x + (getBounds().getWidth() - _font.getWidth(getText())) / 2.0f;
			break;
		case Right:
			x = x + getBounds().getWidth() - getMargin().getRightMargin() - _font.getWidth(getText());
			break;
		}
		
		switch (getVerticalContentAlignment()) {
		case Top:
			y = y + getMargin().getTopMargin();
			break;
		case Center:
			y = y + (getBounds().getHeight() - _font.getHeight(getText())) / 2.0f;
			break;
		case Bottom:
			y = y + getBounds().getHeight() - getMargin().getBottomMargin() - _font.getHeight(getText());
			break;
		}
		
		return new Vector2f(x, y);
	}
}