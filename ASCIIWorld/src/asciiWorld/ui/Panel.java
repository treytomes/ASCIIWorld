package asciiWorld.ui;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public abstract class Panel extends FrameworkElement {
	
	private Rectangle _bounds;
	private List<FrameworkElement> _children;
	
	public Panel(Rectangle bounds) {
		_bounds = bounds;
		_children = new ArrayList<FrameworkElement>();
	}
	
	public Boolean containsChild(FrameworkElement child) {
		return getChildren().contains(child);
	}
	
	public void addChild(FrameworkElement child) throws Exception {
		if (!containsChild(child)) {
			getChildren().add(child);
			child.setParent(this);
			//setChildBounds(child);
			resetBounds();
		}
	}
	
	public void removeChild(FrameworkElement child) throws Exception {
		if (containsChild(child)) {
			getChildren().remove(child);
			child.setParent(null);
			resetBounds();
		}
	}
	
	protected List<FrameworkElement> getChildren() {
		return _children;
	}
	
	protected void setBounds(Rectangle value) {
		_bounds = value;
	}

	@Override
	public Rectangle getBounds() {
		return _bounds;
	}

	@Override
	public void moveTo(Vector2f position) {
		_bounds.setLocation(position);
	}
	
	@Override
	public FrameworkElement findMouseHover() {
		FrameworkElement mouseHover = null;
		for (FrameworkElement elem : getChildren()) {
			mouseHover = elem.findMouseHover();
			if (mouseHover != null) {
				return mouseHover;
			}
		}
		return super.findMouseHover();
	}
	
	@Override
	public void render(Graphics g) {
		Rectangle previousWorldClip = setTransform(g);
		
		for (FrameworkElement child : _children) {
			//setChildBounds(child);
			child.render(g);
		}
		
		clearTransform(g, previousWorldClip);
	}
	
	@Override
	public void update(GameContainer container, int delta) {
		setInputHandled(false);
		
		if (shouldHandleNewInput()) {
			/*if (_inputFocus != null) {
				_inputFocus.update(container, delta);
				if (_inputFocus.getInputHandled()) {
					setInputHandled(true);
				}
				if (!getChildren().contains(_inputFocus)) {
					_inputFocus = null;
				}
			}
			*/
			
			if (!getInputHandled()) {
				//List<FrameworkElement> reverseChildCopy = new ArrayList<FrameworkElement>(_children);
				//Collections.reverse(reverseChildCopy);
				
				for (int index = _children.size() - 1; index >= 0; index--) {
					FrameworkElement child = _children.get(index);
					//if (getInputHandled()) {
					//	continue;
					//}
					/*if (child == _inputFocus) {
						continue;
					}*/
					
					child.update(container, delta);
					if (child.getInputHandled()) {
						setInputHandled(true);
					//	_inputFocus = child;
					//	break;
					}
				}
			}
			
			if (!getInputHandled()) {
				super.update(container, delta);
			}
		} else if (containsMouse()) {
			releaseMouseHover(container.getInput());
		}
	}
	
	/**
	 * Re-calculate the position of all child elements.
	 */
	@Override
	public void resetBounds() {
		for (FrameworkElement child : _children) {
			setChildBounds(child);
		}
	}

	@Override
	protected Boolean contains(Vector2f point) {
		return _bounds.contains(point.x, point.y);
	}
	
	protected abstract void setChildBounds(FrameworkElement child);
}