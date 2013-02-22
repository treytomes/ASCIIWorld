package asciiWorld.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.CreateColor;
import asciiWorld.CreateRectangle;
import asciiWorld.FontFactory;

public class WindowPanel extends Border {

	private static final int CORNER_RADIUS = 8;
	private static final Color COLOR_BORDER_WINDOW = new Color(0.5f, 0.5f, 1.0f);
	private static final Color COLOR_TEXT_TITLE = Color.white;
	private static final Color COLOR_CONTENT_BORDER = new Color(0.0f, 0.75f, 0.5f);
	
	private static final int BUTTON_WIDTH = 106;
	private static final int BUTTON_HEIGHT = 42;
	private static final int MARGIN = 5;

	private Border _contentBackground;

	public WindowPanel(Rectangle bounds, String title) throws Exception {
		super(CreateRectangle.from(bounds).setCornerRadius(CORNER_RADIUS).getRectangle(), CreateColor.from(COLOR_BORDER_WINDOW).changeAlphaTo(0.25f).getColor(), true);
		
		UnicodeFont font = FontFactory.get().getDefaultFont();
		
		int buttonHeight = BUTTON_HEIGHT;
		
		_contentBackground = new Border(new Rectangle(bounds.getMinX() + 10, bounds.getMinY() + 40, bounds.getWidth() - 20, bounds.getHeight() - 50 - buttonHeight), COLOR_CONTENT_BORDER, false);
		
		Color contentFillColor = CreateColor.from(COLOR_CONTENT_BORDER).changeAlphaTo(0.25f).getColor();
		Border contentBorder = new Border(new Rectangle(bounds.getMinX() + 10, bounds.getMinY() + 40, bounds.getWidth() - 20, bounds.getHeight() - 50 - buttonHeight), contentFillColor, true);
		contentBorder.setContent(_contentBackground);
		
		CanvasPanel windowCanvas = new CanvasPanel();
		Vector2f titlePosition = new Vector2f(
				bounds.getMinX() + (bounds.getWidth() - font.getWidth(title)) / 2,
				bounds.getMinY() + 10);
		windowCanvas.addChild(new Label(titlePosition, font, title, COLOR_TEXT_TITLE));
		windowCanvas.addChild(getButtons(getBounds()));
		windowCanvas.addChild(contentBorder);
		
		Border windowBorder = new Border(getBounds(), COLOR_BORDER_WINDOW, false);
		windowBorder.setContent(windowCanvas);

		setContent(windowBorder);
	}
	
	public Boolean isClosed() {
		return getParent() == null;
	}
	
	/**
	 * This sets what will be displayed within the inner content frame,
	 * as opposed to setting the content of the outer border, which is
	 * what calling setContent will do.
	 * 
	 * @param content
	 * @throws Exception
	 */
	public void setWindowContent(FrameworkElement content) throws Exception {
		_contentBackground.setContent(content);
	}
	
	private StackPanel getButtons(Rectangle dialogBounds) throws Exception {
		int numberOfButtons = 1;
		int myWidth = BUTTON_WIDTH * numberOfButtons;
		
		StackPanel buttonPanel = new StackPanel(
				new Rectangle(
						dialogBounds.getMinX() + (dialogBounds.getWidth() - myWidth) / 2,
						dialogBounds.getMaxY() - BUTTON_HEIGHT - MARGIN,
						myWidth,
						BUTTON_HEIGHT));
		
		buttonPanel.addChild(Button.createActionButton("Close", new MethodBinding(this, "closeWindow")));

		return buttonPanel;
	}
	
	public void closeWindow() {
		try {
			getRoot().modalWindowIsClosing();
			getParent().setParent(null); // close the modal panel
			setParent(null); // close the inventory window
			//_inventoryUI = null;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error while attempting to close the window.");
		}
	}
}
