package asciiWorld.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Rectangle;

public class RootVisualPanel extends CanvasPanel {

	private static final Color COLOR_MODAL_BACKGROUND = new Color(0, 0, 0, 0.5f);

	private Boolean _isModalWindowOpen;

	public RootVisualPanel(GameContainer container) throws Exception {
		super(new Rectangle(0, 0, container.getWidth(), container.getHeight()));
		_isModalWindowOpen = false;
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
			mb.addClosedListener(new MessageBoxClosedEvent() {
				@Override
				public void closed(MessageBox sender, Boolean dialogResult) {
					if (sender.isModal()) {
						try {
							sender.getRoot().modalWindowIsClosing();
							sender.getParent().setParent(null);
						} catch (Exception e) {
							e.printStackTrace();
							System.err.println("Error while closing the modal background.");
						}
					}
				}
			});
			
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
			mb.addClosedListener(new MessageBoxClosedEvent() {
				@Override
				public void closed(MessageBox sender, Boolean dialogResult) {
					if (sender.isModal()) {
						try {
							sender.getRoot().modalWindowIsClosing();
							sender.getParent().setParent(null);
						} catch (Exception e) {
							e.printStackTrace();
							System.err.println("Error while closing the modal background.");
						}
					}
				}
			});
			
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
}
