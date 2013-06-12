package asciiWorld.ui;

import org.jdom2.Element;
import org.newdawn.slick.geom.Rectangle;

import asciiWorld.io.XmlHelper;

public class StackPanel extends Panel {
	
	private Orientation _orientation;
	
	public static StackPanel load(String path) throws Exception {
		return fromXml(XmlHelper.load(path));
	}
	
	public static StackPanel fromXml(Element elem) throws Exception {
		XmlHelper.assertName(elem, "StackPanel");
		
		int x = Integer.parseInt(XmlHelper.getAttributeValueOrDefault(elem, "x", "0"));
		int y = Integer.parseInt(XmlHelper.getAttributeValueOrDefault(elem, "y", "0"));
		int width = Integer.parseInt(XmlHelper.getAttributeValueOrDefault(elem, "width", "0"));
		int height = Integer.parseInt(XmlHelper.getAttributeValueOrDefault(elem, "height", "0"));
		Orientation orientation = Orientation.valueOf(XmlHelper.getAttributeValueOrDefault(elem, "orientation", "Horizontal"));
		
		StackPanel sp = new StackPanel(new Rectangle(x, y, width, height), orientation);
		
		Element childrenElem = elem.getChild("Children");
		if (childrenElem != null) {
			for (Element childElem : childrenElem.getChildren()) {
				sp.addChild(UIFactory.get().fromXml(childElem));
			}
		}
		
		return sp;
	}

	public StackPanel(Rectangle bounds, Orientation orientation) {
		super(bounds);
		
		_orientation = orientation;
	}

	public StackPanel(Rectangle bounds) {
		this(bounds, Orientation.Horizontal);
	}

	public StackPanel(Orientation orientation) {
		this(new Rectangle(0, 0, 0, 0), orientation);
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
	
	protected void setVerticalOrientationBounds() {
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
