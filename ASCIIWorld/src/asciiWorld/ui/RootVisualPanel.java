package asciiWorld.ui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Rectangle;

public class RootVisualPanel extends CanvasPanel {

	private Boolean _isModalWindowOpen;

	public RootVisualPanel(GameContainer container) throws Exception {
		super(new Rectangle(0, 0, container.getWidth(), container.getHeight()));
		_isModalWindowOpen = false;
	}
	
	public Boolean isModalWindowOpen() {
		return _isModalWindowOpen;
	}
	
	public void modalWindowIsOpening() {
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
				public void closed(FrameworkElement sender, Boolean dialogResult) {
					sender.getRoot().modalWindowIsClosing();
				}
			});
			modalWindowIsOpening();
			return mb;
		} catch (Exception e) {
			System.err.println("Unable to show the message box.");
			return null;
		}
	}
	
	public MessageBox showMessageBox(Boolean modal, String message, String title) {
		try {
			MessageBox mb = MessageBox.show(this, modal, message, title);
			mb.addClosedListener(new MessageBoxClosedEvent() {
				@Override
				public void closed(FrameworkElement sender, Boolean dialogResult) {
					sender.getRoot().modalWindowIsClosing();
				}
			});
			modalWindowIsOpening();
			return mb;
		} catch (Exception e) {
			System.err.println("Unable to show the message box.");
			return null;
		}
	}
}
