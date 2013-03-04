package asciiWorld.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public abstract class FrameworkElement {

	private static final int DEFAULT_MARGIN = 0;
	
	private List<MousePositionEvent> _mouseOverListeners;
	private List<MousePositionEvent> _mouseOutListeners;
	private List<MouseButtonEvent> _mouseDownListeners;
	private List<MouseButtonEvent> _mouseUpListeners;
	
	private Boolean _containsMouse = false;
	private Map<MouseButton, Boolean> _mouseButtonDown = null;
	
	private Margin _margin;
	
	private FrameworkElement _parent;
	private FrameworkElement _inputFocus;
	
	private Boolean _inputHandled;

	public FrameworkElement() {
		_mouseOverListeners = new ArrayList<MousePositionEvent>();
		_mouseOutListeners = new ArrayList<MousePositionEvent>();
		_mouseDownListeners = new ArrayList<MouseButtonEvent>();
		_mouseUpListeners = new ArrayList<MouseButtonEvent>();

		_mouseButtonDown = new HashMap<MouseButton, Boolean>();
		_mouseButtonDown.put(MouseButton.Left, false);
		_mouseButtonDown.put(MouseButton.Middle, false);
		_mouseButtonDown.put(MouseButton.Right, false);
		
		_margin = new Margin(DEFAULT_MARGIN);
		setInputHandled(false);
	}
	
	public void addMouseOverListener(MousePositionEvent listener) {
		_mouseOverListeners.add(listener);
	}
	
	public void removeMouseOverListener(MousePositionEvent listener) {
		_mouseOverListeners.remove(listener);
	}
	
	public void addMouseOutListener(MousePositionEvent listener) {
		_mouseOutListeners.add(listener);
	}
	
	public void removeMouseOutListener(MousePositionEvent listener) {
		_mouseOutListeners.remove(listener);
	}
	
	public void addMouseDownListener(MouseButtonEvent listener) {
		_mouseDownListeners.add(listener);
	}
	
	public void removeMouseDownListener(MouseButtonEvent listener) {
		_mouseDownListeners.remove(listener);
	}
	
	public void addMouseUpListener(MouseButtonEvent listener) {
		_mouseUpListeners.add(listener);
	}
	
	public void removeMouseUpListener(MouseButtonEvent listener) {
		_mouseUpListeners.remove(listener);
	}
	
	public FrameworkElement findMouseHover() {
		if (containsMouse()) {
			return this;
		} else {
			return null;
		}
	}
	
	public FrameworkElement getInputFocus() {
		if (getParent() != null) {
			return getParent().getInputFocus();
		}
		return _inputFocus;
	}
	
	public void setInputFocus(FrameworkElement value) {
		_inputFocus = value;
		if (getParent() != null) {
			getParent().setInputFocus(value);
		}
	}
	
	/**
	 * Was input handled in the last frame?  If so, don't let siblings and parent's process the input. 
	 */
	public Boolean getInputHandled() {
		return _inputHandled;
	}
	
	protected void setInputHandled(Boolean value) {
		_inputHandled = value;
	}
	
	public Boolean containsMouse() {
		return _containsMouse;
	}
	
	public Margin getMargin() {
		return _margin;
	}
	
	public void setMargin(Margin value) {
		_margin = value;
	}
	
	public FrameworkElement getParent() {
		return _parent;
	}
	
	/**
	 * 
	 * @return The RootVisualPanel, or null if it is not present.
	 */
	public RootVisualPanel getRoot() {
		FrameworkElement parent = getParent();
		if (parent == null) {
			if (this instanceof RootVisualPanel) {
				return (RootVisualPanel)this;
			} else {
				return null;
			}
		}
		while (parent.getParent() != null) {
			parent = parent.getParent();
		}
		return (RootVisualPanel)parent;
	}
	
	public void setParent(FrameworkElement parent) throws Exception {
		if ((getParent() != null) && (getParent() != parent)) {
			removeOldParent();
		}

		if ((getParent() == null) && (parent != null)) {
			if (parent instanceof ContentControl) {
				setNewParent((ContentControl)parent);
			} else if (parent instanceof Panel) {
				setNewParent((Panel)parent);
			} else {
				throw new Exception(String.format("Unable to set the input element type '%s' as a parent.", parent.getClass().getName()));
			}
		}
	}
	
	private void removeOldParent() throws Exception {
		if (getParent() instanceof ContentControl) {
			ContentControl contentParent = (ContentControl)getParent();
			if (contentParent.getContent() == this) {
				contentParent.setContent(null);
			}
		} else if (getParent() instanceof Panel) {
			Panel panelParent = (Panel)getParent();
			if (panelParent.containsChild(this)) {
				panelParent.removeChild(this);
			}
		}
		_parent = null;
	}
	
	private void setNewParent(ContentControl parent) throws Exception {
		_parent = parent;
		if (parent.getContent() != this) {
			parent.setContent(this);
		}
	}
	
	private void setNewParent(Panel parent) throws Exception {
		_parent = parent;
		if (!parent.containsChild(this)) {
			parent.addChild(this);
		}
	}
	
	/**
	 * 
	 * @param g
	 * @return The previous world clip, to be restored as input to clearTransform.
	 */
	protected Rectangle setTransform(Graphics g) {
		//Rectangle previousWorldClip = g.getWorldClip();
		Rectangle previousWorldClip = g.getClip();
		//g.pushTransform();
		
		Rectangle bounds = getBounds();
		//g.translate(bounds.getX(), bounds.getY());
		
		//g.setWorldClip(getBounds());
		Rectangle newClip = new Rectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		newClip.grow(2, 2);
		g.setClip(newClip);
		
		if (previousWorldClip == null) {
			return null;
		} else {
			return new Rectangle(previousWorldClip.getX(), previousWorldClip.getY(), previousWorldClip.getWidth(), previousWorldClip.getHeight());
		}
	}
	
	protected void clearTransform(Graphics g, Rectangle previousWorldClip) {
		//g.popTransform();
		//g.setWorldClip(previousWorldClip);
		g.setClip(previousWorldClip);
	}
	
	public abstract void render(Graphics g);
	
	public void update(GameContainer container, int delta) {
		setInputHandled(false);
		
		Input input = container.getInput();
		if (shouldHandleNewInput()) {
			updateMouseEvents(input);
		} else if (containsMouse()) {
			releaseMouseHover(input);
		}
	}
	
	public abstract Rectangle getBounds();
	
	public void resetBounds() {
	}
	
	public abstract void moveTo(Vector2f position);
	
	protected Boolean shouldHandleNewInput() {
		return (getParent() == null) || !getParent().getInputHandled();
	}
	
	protected abstract Boolean contains(Vector2f point);
	
	private void updateMouseEvents(Input input) {
		Vector2f mousePosition = new Vector2f(input.getMouseX(), input.getMouseY());

		Boolean lastFrameContainedMouse = _containsMouse;
		_containsMouse = contains(mousePosition);

		if (_containsMouse && !lastFrameContainedMouse) {
			for (MousePositionEvent l : _mouseOverListeners) {
				l.mousePositionChanged(this, mousePosition);
			}
		}
		else if (lastFrameContainedMouse && !_containsMouse) {
			releaseMouseHover(input);
		}

		if (_containsMouse) {
			checkMouseButtonStates(input, mousePosition);

			setInputHandled(true);
		} else {
			clearMouseButtonStates();
		}
	}
	
	protected void releaseMouseHover(Input input) {
		Vector2f mousePosition = new Vector2f(input.getMouseX(), input.getMouseY());
		for (MousePositionEvent l : _mouseOutListeners) {
			l.mousePositionChanged(this, mousePosition);
		}
	}
	
	/**
	 * If the state of the mouse button has changed since the last frame,
	 * invoke the relevant event handler.
	 * 
	 * @param input
	 * @param mousePosition
	 */
	private void checkMouseButtonStates(Input input, Vector2f mousePosition) {
		checkMouseButtonState(input, MouseButton.Left, mousePosition);
		checkMouseButtonState(input, MouseButton.Middle, mousePosition);
		checkMouseButtonState(input, MouseButton.Right, mousePosition);
	}
	
	private void checkMouseButtonState(Input input, MouseButton button, Vector2f mousePosition) {
		Boolean lastState = _mouseButtonDown.get(button);
		Boolean currentState = input.isMouseButtonDown(button.index());
		_mouseButtonDown.put(button, currentState);
		if (currentState && !lastState) {
			for (MouseButtonEvent l : _mouseDownListeners) {
				l.mouseButtonChanged(this, button, mousePosition);
			}
			setInputFocus(this);
		}
		else if (lastState && !currentState) {
			for (MouseButtonEvent l : _mouseUpListeners) {
				l.mouseButtonChanged(this, button, mousePosition);
			}
			setInputFocus(this);
		}
	}
	
	private void clearMouseButtonStates() {
		_mouseButtonDown.put(MouseButton.Left, false);
		_mouseButtonDown.put(MouseButton.Middle, false);
		_mouseButtonDown.put(MouseButton.Right, false);
	}
}