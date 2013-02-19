package asciiWorld.ui;

import org.newdawn.slick.geom.Rectangle;

public class StackPanel extends Panel {
	
	private Orientation _orientation;

	public StackPanel(Rectangle bounds, Orientation orientation) {
		super(bounds);
		
		_orientation = orientation;
	}

	public StackPanel(Rectangle bounds) {
		this(bounds, Orientation.Horizontal);
	}
	
	public Orientation getOrientation() {
		return _orientation;
	}
	
	public void setOrientation(Orientation value) {
		_orientation = value;
	}

	@Override
	protected void setChildBounds(FrameworkElement child) {
		if (child == null) {
			return;
		}
		
		switch (getOrientation()) {
		case Horizontal:
			setHorizontalOrientationBounds();
			break;
		case Vertical:
			setVerticalOrientationBounds();
			break;
		}
		
		child.resetBounds();
	}
	
	private void setHorizontalOrientationBounds() {
		int numberOfChildren = getChildren().size();
		float myWidth = getBounds().getWidth();
		float myHeight = getBounds().getHeight();
		float childWidth = myWidth / numberOfChildren;
		
		float x = getBounds().getX();
		float y = getBounds().getY();
		for (FrameworkElement child : getChildren()) {
			child.getBounds().setX(x + child.getMargin().getLeftMargin());
			child.getBounds().setY(y + child.getMargin().getTopMargin());
			child.getBounds().setWidth(childWidth - child.getMargin().getLeftMargin() - child.getMargin().getRightMargin());
			child.getBounds().setHeight(myHeight - child.getMargin().getTopMargin() - child.getMargin().getBottomMargin());
			
			x += childWidth;
		}
	}
	
	private void setVerticalOrientationBounds() {
		int numberOfChildren = getChildren().size();
		float myWidth = getBounds().getWidth();
		float myHeight = getBounds().getHeight();
		float childHeight = myHeight / numberOfChildren;
		
		float x = getBounds().getX();
		float y = getBounds().getY();
		for (FrameworkElement child : getChildren()) {
			child.getBounds().setX(x + child.getMargin().getLeftMargin());
			child.getBounds().setY(y + child.getMargin().getTopMargin());
			child.getBounds().setWidth(myWidth - child.getMargin().getLeftMargin() - child.getMargin().getRightMargin());
			child.getBounds().setHeight(childHeight - child.getMargin().getTopMargin() - child.getMargin().getBottomMargin());
			
			y += childHeight;
		}
	}
}
