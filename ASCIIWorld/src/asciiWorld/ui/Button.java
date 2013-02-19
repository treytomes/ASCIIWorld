package asciiWorld.ui;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.FontFactory;

public class Button extends ContentControl {
	
	private static final Color COLOR_LABEL = Color.white;
	private static final Color COLOR_FILL = new Color(0.25f, 0.25f, 0.25f, 0.25f);
	private static final Color COLOR_BORDER = Color.gray;
	private static final float CORNER_RADIUS = 8;

	private static UnicodeFont _defaultFont = null;
	
	private List<ButtonClickedEvent> _buttonClickedListeners;
	
	private Boolean _buttonPressed;
	private String _text;
	
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
	
 	public Button(UnicodeFont font, String text, Rectangle bounds) throws Exception {
		_buttonPressed = false;
		_text = text;
		
		_buttonClickedListeners = new ArrayList<ButtonClickedEvent>();
		
		if (bounds.getWidth() == 0) {
			bounds.setWidth(CORNER_RADIUS * 4);
			//bounds.grow(1, 1);
		}
		if (bounds.getHeight() == 0) {
			bounds.setHeight(CORNER_RADIUS * 4);
		}
		//bounds.setWidth((bounds.getWidth() == 0) ? 1 : bounds.getWidth());
		//bounds.setHeight((bounds.getHeight() == 0) ? 1 : bounds.getWidth());
		
		final Border fill = new Border(new RoundedRectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), CORNER_RADIUS), new Color(COLOR_FILL), true) {
			@Override
			public void update(GameContainer container, int delta) {
				//super.update(container);
			}
		};
		
		Border border = new Border(new RoundedRectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 8), COLOR_BORDER, false) {
			@Override
			public void update(GameContainer container, int delta) {
				//super.update(container);
				//setInputHandled(false);
			}
		};
		
		Label label = new Label(font, text, COLOR_LABEL) {
			@Override
			public void update(GameContainer container, int delta) {
				//super.update(container);
				//setInputHandled(false);
			}
		};
		
		addMouseOverListener(new MousePositionEvent() {
			@Override
			public void mousePositionChanged(FrameworkElement sender, Vector2f mousePosition) {
				fill.getColor().a = 0.5f;
			}
		});
		addMouseOutListener(new MousePositionEvent() {
			@Override
			public void mousePositionChanged(FrameworkElement sender, Vector2f mousePosition) {
				fill.getColor().a = 0.25f;
			}
		});
		addMouseDownListener(new MouseButtonEvent() {
			@Override
			public void mouseButtonChanged(FrameworkElement sender, MouseButton button, Vector2f mousePosition) {
				if (button == MouseButton.Left) {
					fill.getColor().a = 0.75f;
					_buttonPressed = true;
				}
			}
		});
		addMouseUpListener(new MouseButtonEvent() {
			@Override
			public void mouseButtonChanged(FrameworkElement sender, MouseButton button, Vector2f mousePosition) {
				if ((button == MouseButton.Left) && _buttonPressed) {
					fill.getColor().a = 0.5f;
					handleClick();
				}
			}
		});
		
		border.setContent(label);
		fill.setContent(border);
		setContent(fill);
	}
	
	public Button(UnicodeFont font, String text) throws Exception {
		this(font, text, new Rectangle(0, 0, 0, 0));
	}
	
	public Button(String text) throws Exception {
		this(getDefaultFont(), text, new Rectangle(0, 0, 0, 0));
	}
	
	public Button(String text, Rectangle bounds) throws Exception {
		this(getDefaultFont(), text, bounds);
	}
	
	public void addClickListener(ButtonClickedEvent listener) {
		_buttonClickedListeners.add(listener);
	}
	
	public void removeClickListener(ButtonClickedEvent listener) {
		_buttonClickedListeners.remove(listener);
	}
	
	public String getText() {
		return _text;
	}
	
	@Override
	public Rectangle getBounds() {
		return getContent().getBounds();
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
