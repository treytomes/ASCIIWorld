package asciiWorld.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Rectangle;

public class RootVisualPanel extends CanvasPanel {

	private static final Color COLOR_MODAL_BACKGROUND = new Color(0, 0, 0, 0.75f);

	private static RootVisualPanel _instance;
	
	private Boolean _isModalWindowOpen;

	private RootVisualPanel(GameContainer container) throws Exception {
		super(new Rectangle(0, 0, container.getWidth(), container.getHeight()));
		_isModalWindowOpen = false;
	}
	
	public static void initialize(GameContainer container) throws Exception {
		_instance = new RootVisualPanel(container);
	}
	
	public static RootVisualPanel get() throws Exception {
		if (_instance == null) {
			throw new Exception("You must initialize the RootVisualPanel before you can use it.");
		}
		return _instance;
	}
	
	public void clear() {
		getChildren().clear();
	}
	
	public Boolean isModalWindowOpen() {
		return _isModalWindowOpen;
	}
	
	private void modalWindowIsOpening() {
		_isModalWindowOpen = true;
	}
	
	public void modalWindowIsClosing() {
		_isModalWindowOpen = false;
	}
	
	public MessageBox loadMessageBox(String path) throws Exception {
		try {
			MessageBox mb = MessageBox.load(this, path);
			
			if (mb.isModal()) {
				addModalChild(mb);
			} else {
				addChild(mb);
			}
			return mb;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to show the message box.");
			return null;
		}
	}
	
	public MessageBox showMessageBox(Boolean modal, String message, String title) {
		try {
			MessageBox mb = MessageBox.create(this, modal, message, title);
			
			if (mb.isModal()) {
				addModalChild(mb);
			} else {
				addChild(mb);
			}
			return mb;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to show the message box.");
			return null;
		}
	}
	
	public FrameworkElement addModalChild(FrameworkElement ui) throws Exception {
		Rectangle bounds = new Rectangle(0, 0, getBounds().getWidth(), getBounds().getHeight());
		CanvasPanel panel = new CanvasPanel(bounds);
		panel.addChild(new Border(bounds, COLOR_MODAL_BACKGROUND, true));
		panel.addChild(ui);
		addChild(panel);
		modalWindowIsOpening();
		return panel;
	}
	
	@Override
	public void update(GameContainer container, int delta) {
		setInputFocus(null);
		super.update(container, delta);
	}
}
