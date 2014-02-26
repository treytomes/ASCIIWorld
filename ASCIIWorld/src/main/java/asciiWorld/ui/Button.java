package asciiWorld.ui;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.CreateColor;
import asciiWorld.FontFactory;
import asciiWorld.JavascriptContext;
import asciiWorld.TextHelper;
import asciiWorld.io.XmlHelper;

public class Button extends ContentControl {
	
	private static final Color COLOR_LABEL = Color.white;
	private static final Color COLOR_BORDER = Color.gray;
	private static final float CORNER_RADIUS = 8;

	private static UnicodeFont _defaultFont = null;
	
	private List<ButtonClickedEvent> _buttonClickedListeners;
	
	private Boolean _buttonPressed;
	private Object _textBinding;
	
	private Border _fill;
	private Border _border;
	private Label _label;
	
	public static Button load(String path) throws Exception {
		return fromXml(XmlHelper.load(path));
	}
	
	public static Button fromXml(Element elem) throws Exception {
		XmlHelper.assertName(elem, "Button");
		String text = XmlHelper.getAttributeValueOrDefault(elem, "text", "");
		int margin = Integer.parseInt(XmlHelper.getAttributeValueOrDefault(elem, "margin", "0"));
		
		final String scriptText = XmlHelper.getPropertyValueOrDefault(elem, "click", null);
		
		Button btn = new Button(text);
		btn.getMargin().setValue(margin);
		
		if (!TextHelper.isNullOrWhiteSpace(scriptText)) {
			btn.addClickListener(new ButtonClickedEvent() {
				JavascriptContext _context;
				
				{
					_context = new JavascriptContext();
				}
				
				@Override
				public void click(Button button) {
					_context.addObjectToContext(button, "me");
					try {
						_context.executeScript(scriptText);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		
		return btn;
	}
	
	public static Button createStateTransitionButton(String text, final StateBasedGame game, final int stateID) throws Exception {
		return new Button(text) {{
			getMargin().setValue(5);
			addClickListener(new ButtonClickedEvent() {
				@Override
				public void click(Button button) {
					game.enterState(stateID);
				}
			});
		}};
	}
	
	public static Button createActionButton(String text, final MethodBinding methodBinding) throws Exception {
		return new Button(text) {{
			getMargin().setValue(5);
			addClickListener(new ButtonClickedEvent() {
				@Override
				public void click(Button button) {
					methodBinding.getValue();
				}
			});
		}};
	}
	
	private static UnicodeFont getDefaultFont() throws SlickException {
		if (_defaultFont == null) {
			_defaultFont = FontFactory.get().getDefaultFont();
		}
		return _defaultFont;
	}
	
 	public Button(UnicodeFont font, Object textBinding, Rectangle bounds) throws Exception {
		_buttonPressed = false;
		_textBinding = textBinding;
		
		_buttonClickedListeners = new ArrayList<ButtonClickedEvent>();
		
		if (bounds.getWidth() == 0) {
			bounds.setWidth(CORNER_RADIUS * 4);
		}
		if (bounds.getHeight() == 0) {
			bounds.setHeight(CORNER_RADIUS * 4);
		}
		
		Color fillColor = CreateColor.from(COLOR_BORDER).changeAlphaTo(0.25f).getColor();
		_fill = new Border(new RoundedRectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), CORNER_RADIUS), fillColor, true) {
			@Override
			public void update(GameContainer container, int delta) {
				//super.update(container);
			}
		};
		
		_border = new Border(new RoundedRectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 8), COLOR_BORDER, false) {
			@Override
			public void update(GameContainer container, int delta) {
				//super.update(container);
			}
		};
		
		_label = new Label(font, textBinding, COLOR_LABEL) {
			@Override
			public void update(GameContainer container, int delta) {
				//super.update(container);
			}
		};
		
		addMouseOverListener(new MousePositionEvent() {
			@Override
			public void mousePositionChanged(FrameworkElement sender, Vector2f mousePosition) {
				_fill.getColor().a = 0.5f;
			}
		});
		addMouseOutListener(new MousePositionEvent() {
			@Override
			public void mousePositionChanged(FrameworkElement sender, Vector2f mousePosition) {
				_fill.getColor().a = 0.25f;
			}
		});
		addMouseDownListener(new MouseButtonEvent() {
			@Override
			public void mouseButtonChanged(FrameworkElement sender, MouseButton button, Vector2f mousePosition) {
				if (button == MouseButton.Left) {
					_fill.getColor().a = 0.75f;
					_buttonPressed = true;
				}
			}
		});
		addMouseUpListener(new MouseButtonEvent() {
			@Override
			public void mouseButtonChanged(FrameworkElement sender, MouseButton button, Vector2f mousePosition) {
				if ((button == MouseButton.Left) && _buttonPressed) {
					_fill.getColor().a = 0.5f;
					handleClick();
				}
			}
		});
		
		_border.setContent(_label);
		_fill.setContent(_border);
		setContent(_fill);
	}
	
	public Button(UnicodeFont font, Object textBinding) throws Exception {
		this(font, textBinding, new Rectangle(0, 0, 0, 0));
	}
	
	public Button(Object textBinding) throws Exception {
		this(getDefaultFont(), textBinding, new Rectangle(0, 0, 0, 0));
	}
	
	public Button(Object textBinding, Rectangle bounds) throws Exception {
		this(getDefaultFont(), textBinding, bounds);
	}
	
	public void addClickListener(ButtonClickedEvent listener) {
		_buttonClickedListeners.add(listener);
	}
	
	public void removeClickListener(ButtonClickedEvent listener) {
		_buttonClickedListeners.remove(listener);
	}
	
	public Color getForegroundColor() {
		return _label.getColor();
	}
	
	public void setForegroundColor(Color value) {
		_label.setColor(value);
	}
	
	public Color getBackgroundColor() {
		return _border.getColor();
	}
	
	public void setBackgroundColor(Color value) {
		_border.setColor(value);
		_fill.setColor(CreateColor.from(value).changeAlphaTo(0.25f).getColor());
	}
	
	@Override
	public void setHorizontalContentAlignment(HorizontalAlignment value) {
		super.setHorizontalContentAlignment(value);
		if (_label != null) {
			_label.setHorizontalContentAlignment(value);
		}
	}
	
	public String getText() {
		return _textBinding.toString();
	}
	
	@Override
	public Rectangle getBounds() {
		return _fill.getBounds();
	}

	public float getCornerRadius() {
		Rectangle bounds = getBounds();
		if (bounds instanceof RoundedRectangle) {
			return ((RoundedRectangle)bounds).getCornerRadius();
		} else {
			return 0;
		}
	}
	
	public void setCornerRadius(float value) {
		Rectangle oldBounds = getBounds();
		if (value == 0) {
			_border.setBounds(new Rectangle(oldBounds.getX(), oldBounds.getY(), oldBounds.getWidth(), oldBounds.getHeight()));
			_fill.setBounds(new Rectangle(oldBounds.getX(), oldBounds.getY(), oldBounds.getWidth(), oldBounds.getHeight()));
		} else {
			_border.setBounds(new RoundedRectangle(oldBounds.getX(), oldBounds.getY(), oldBounds.getWidth(), oldBounds.getHeight(), value));
			_fill.setBounds(new RoundedRectangle(oldBounds.getX(), oldBounds.getY(), oldBounds.getWidth(), oldBounds.getHeight(), value));
		}
	}
	
	@Override
	public void moveTo(Vector2f position) {
		getContent().moveTo(position);
	}

	@Override
	protected Boolean contains(Vector2f point) {
		return getContent().getBounds().contains(point.x, point.y);
	}
	
	private void handleClick() {
		for (ButtonClickedEvent l : _buttonClickedListeners) {
			l.click(this);
		}		
	}
}
