package asciiWorld.ui;

import org.newdawn.slick.geom.Rectangle;

public class CanvasPanel extends Panel {
	
	public CanvasPanel(Rectangle bounds) {
		super(bounds);
	}
	
	public CanvasPanel() {
		super(new Rectangle(0, 0, 0, 0));
	}

	@Override
	protected void setChildBounds(FrameworkElement child) {
		// The canvas panel lets it's children be.
	}
}
