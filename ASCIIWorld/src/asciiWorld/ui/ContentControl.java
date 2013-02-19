package asciiWorld.ui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public abstract class ContentControl extends FrameworkElement {
	
	FrameworkElement _content;
	HorizontalAlignment _horizontalContentAlignment;
	VerticalAlignment _verticalContentAlignment;

	public ContentControl() throws Exception {
		super();
		
		setContent(null);
		setHorizontalContentAlignment(HorizontalAlignment.Center);
		setVerticalContentAlignment(VerticalAlignment.Center);
	}
	
	public FrameworkElement getContent() {
		return _content;
	}
	
	public void setContent(FrameworkElement content) throws Exception {
		if (getContent() != content) {
			_content = content;
			_content.setParent(this);
			setContentBounds();
		}
	}
	
	public HorizontalAlignment getHorizontalContentAlignment() {
		return _horizontalContentAlignment;
	}
	
	public void setHorizontalContentAlignment(HorizontalAlignment alignment) {
		_horizontalContentAlignment = alignment;
	}
	
	public VerticalAlignment getVerticalContentAlignment() {
		return _verticalContentAlignment;
	}
	
	public void setVerticalContentAlignment(VerticalAlignment alignment) {
		_verticalContentAlignment = alignment;
	}
	
	@Override
	public void render(Graphics g) {
		if (getContent() != null) {
			//setContentBounds();
			getContent().render(g);
		}
	}
	
	@Override
	public void update(GameContainer container, int delta) {
		setInputHandled(false);
		
		if (shouldHandleNewInput()) {
			if (getContent() != null) {
				getContent().update(container, delta);
				if (getContent().getInputHandled()) {
					setInputHandled(true);
				}
			}
			
			if (!getInputHandled()) {
				super.update(container, delta);
			}
		} else if (containsMouse()) {
			releaseMouseHover(container.getInput());
		}
	}
	
	@Override
	public void resetBounds() {
		setContentBounds();
	}
	
	protected void setContentBounds() {
		if (_content == null) {
			return;
		}
		
		float x = 0;
		float y = 0;
		float contentWidth = getBounds().getWidth() - _content.getMargin().getLeftMargin() - _content.getMargin().getRightMargin();
		float contentHeight = getBounds().getHeight() - _content.getMargin().getTopMargin() - _content.getMargin().getBottomMargin();

		_content.getBounds().setWidth(contentWidth);
		_content.getBounds().setHeight(contentHeight);

		switch (getHorizontalContentAlignment()) {
		case Left:
			x = getBounds().getX() + _content.getMargin().getLeftMargin();
			break;
		case Center:
			x = getBounds().getX() + (getBounds().getWidth() - _content.getBounds().getWidth()) / 2.0f;
			break;
		case Right:
			x = getBounds().getX() + getBounds().getWidth() - _content.getBounds().getWidth() - _content.getMargin().getRightMargin();
			break;
		}
		
		switch (getVerticalContentAlignment()) {
		case Top:
			y = getBounds().getY() + _content.getMargin().getTopMargin();
			break;
		case Center:
			y = getBounds().getY() + (getBounds().getHeight() - _content.getBounds().getHeight()) / 2.0f;
			break;
		case Bottom:
			y = getBounds().getY() + getBounds().getHeight() - _content.getBounds().getHeight() - _content.getMargin().getBottomMargin();
			break;
		}
		
		_content.moveTo(new Vector2f(x, y));
		
		_content.resetBounds();
	}
}
