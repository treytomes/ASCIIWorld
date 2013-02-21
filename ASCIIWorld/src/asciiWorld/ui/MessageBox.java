package asciiWorld.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.FontFactory;
import asciiWorld.TextFactory;
import asciiWorld.TextHelper;

public class MessageBox {

	private static final Color COLOR_MODAL_BACKGROUND = new Color(0, 0, 0, 0.5f);
	private static final Color COLOR_BORDER_WINDOW = new Color(0.5f, 0.5f, 1.0f);
	private static final Color COLOR_BORDER_MESSAGE = new Color(0.0f, 0.75f, 0.5f);
	private static final Color COLOR_TEXT_TITLE = Color.white;
	private static final Color COLOR_TEXT_MESSAGE = Color.yellow;
	
	private static final String DEFAULT_TITLE = "Message Box";
	private static final String DEFAULT_MESSAGE_ACCEPT = "Okay";
	private static final String DEFAULT_MESSAGE_CANCEL = "";

	private List<MessageBoxClosedEvent> _closedListeners;

	private Boolean _isModal;
	private String _message;
	private String _title;
	private String _acceptButtonText;
	private String _cancelButtonText;
	private float _width;
	private float _height;
	private RoundedRectangle _bounds;
	private FrameworkElement _ui;
	
	private Boolean _result;
	
	private MessageBox(Rectangle containerBounds, Boolean isModal, String message, String title, String acceptButtonText, String cancelButtonText) throws Exception {
		_closedListeners = new ArrayList<MessageBoxClosedEvent>();
		
		_isModal = isModal;
		_title = title;
		_acceptButtonText = acceptButtonText;
		_cancelButtonText = cancelButtonText;
		_message = message;
		_width = containerBounds.getWidth() / 3;
		_height = containerBounds.getWidth() / 3;
		_bounds = new RoundedRectangle((containerBounds.getWidth() - _width) / 2, (containerBounds.getHeight() - _height) / 2, _width, _height, 8);
		_result = false;
		
		generateUI();
		if (_isModal) {
			makeUIModal(containerBounds);
		}
	}
	
	public FrameworkElement getUI() {
		return _ui;
	}
	
	public void addClosedListener(MessageBoxClosedEvent listener) {
		_closedListeners.add(listener);
	}
	
	public void removeClosedListener(MessageBoxClosedEvent listener) {
		_closedListeners.remove(listener);
	}
	
	public Boolean isOpen() {
		return _ui.getParent() != null;
	}
	
	public Boolean getResult() {
		return _result;
	}
	
	public static MessageBox create(Rectangle containerBounds, Boolean isModal, String message, String title, String acceptButtonText, String cancelButtonText) throws Exception {
		return new MessageBox(containerBounds, isModal, message, title, acceptButtonText, cancelButtonText);
	}
	
	public static MessageBox create(Rectangle containerBounds, Boolean isModal, String message, String title, String acceptButtonText) throws Exception {
		return create(containerBounds, isModal, message, title, acceptButtonText, DEFAULT_MESSAGE_CANCEL);
	}
	
	public static MessageBox create(Rectangle containerBounds, Boolean isModal, String message, String title) throws Exception {
		return create(containerBounds, isModal, message, title, DEFAULT_MESSAGE_ACCEPT);
	}
	
	public static MessageBox create(Rectangle containerBounds, Boolean isModal, String message) throws Exception {
		return create(containerBounds, isModal, message, DEFAULT_TITLE);
	}
	
	public static MessageBox load(Rectangle containerBounds, String path) throws Exception {
		return fromXml(containerBounds, (Element)new SAXBuilder().build(new File(path)).getRootElement());
	}
	
	public static MessageBox fromXml(Rectangle containerBounds, Element elem) throws Exception {
		org.jdom2.Attribute isModalAttribute = elem.getAttribute("isModal");
		
		Boolean isModal = (isModalAttribute == null) ? false : elem.getAttribute("isModal").getBooleanValue();
		String title = elem.getAttributeValue("title");
		String message = parseMessage(elem.getChild("Message"));
		return create(containerBounds, isModal, message, title);
	}
	
	private static String parseMessage(Element messageElement) throws Exception {
		List<Element> children = messageElement.getChildren();
		if (children.size() < 1) {
			return messageElement.getText();
		} else if (children.size() > 1) {
			throw new Exception("The 'Message' element can only have 1 child.");
		} else {
			Element messageSourceElement = children.get(0);
			switch (messageSourceElement.getName()) {
			case "TextResource":
				return TextFactory.get().fromXml(messageSourceElement);
			default:
				throw new Exception("Invalid message source type.");
			}
		}
	}
	
	private void makeUIModal(Rectangle containerBounds) throws Exception {
		CanvasPanel panel = new CanvasPanel(new Rectangle(0, 0, containerBounds.getWidth(), containerBounds.getHeight()));
		panel.addChild(new Border(new Rectangle(0, 0, containerBounds.getWidth(), containerBounds.getHeight()), COLOR_MODAL_BACKGROUND, true));
		panel.addChild(_ui);
		_ui = panel;
	}
	
	private void generateUI() throws Exception {
		UnicodeFont font = FontFactory.get().getDefaultFont();
		Color windowFillColor = new Color(COLOR_BORDER_WINDOW);
		windowFillColor.a = 0.25f;
		
		Label messageLabel = new Label(new Vector2f(0, 0), font, _message, COLOR_TEXT_MESSAGE);
		messageLabel.getMargin().setValue(5);
		messageLabel.setHorizontalContentAlignment(HorizontalAlignment.Left);
		messageLabel.setVerticalContentAlignment(VerticalAlignment.Top);
		
		Border messageBackground = new Border(new Rectangle(_bounds.getMinX() + 10, _bounds.getMinY() + 40, _bounds.getWidth() - 20, _bounds.getHeight() - 50 - 42), COLOR_BORDER_MESSAGE, false);
		messageBackground.setContent(messageLabel);
		
		Color messageFillColor = new Color(COLOR_BORDER_MESSAGE);
		messageFillColor.a = 0.25f;

		Border messageBorder = new Border(new Rectangle(_bounds.getMinX() + 10, _bounds.getMinY() + 40, _bounds.getWidth() - 20, _bounds.getHeight() - 50 - 42), messageFillColor, true);
		messageBorder.setContent(messageBackground);
		
		CanvasPanel windowCanvas = new CanvasPanel();
		windowCanvas.addChild(new Label(new Vector2f(_bounds.getMinX() + (_bounds.getWidth() - font.getWidth(_title)) / 2, _bounds.getMinY() + 10), font, _title, COLOR_TEXT_TITLE));
		windowCanvas.addChild(messageBorder);
		windowCanvas.addChild(getButtons(_bounds, _acceptButtonText, _cancelButtonText));
		
		final Border windowBackground = new Border(_bounds, COLOR_BORDER_WINDOW, false);
		windowBackground.setContent(windowCanvas);
		
		Border windowBorder = new Border(_bounds, windowFillColor, true);
		windowBorder.setContent(windowBackground);
		
		_ui = windowBorder;
	}
	
	private StackPanel getButtons(RoundedRectangle dialogBounds, final String firstButtonText, final String secondButtonText) throws Exception {
		int numberOfButtons = TextHelper.isNullOrWhiteSpace(secondButtonText) ? 1 : 2;
		int myWidth = 106 * numberOfButtons;
		
		StackPanel buttonPanel = new StackPanel(new Rectangle(dialogBounds.getMinX() + (dialogBounds.getWidth() - myWidth) / 2, dialogBounds.getMaxY() - 42 - 5, myWidth, 42));
		
		Button acceptButton = new Button(TextHelper.isNullOrWhiteSpace(firstButtonText) ? DEFAULT_MESSAGE_ACCEPT : firstButtonText);
		acceptButton.getMargin().setValue(5);
		acceptButton.addClickListener(new ButtonClickedEvent() {
			@Override
			public void click(Button button) {
				_result = true;
				closeWindow();
			}
		});
		buttonPanel.addChild(acceptButton);

		if (numberOfButtons > 1) {
			Button cancelButton = new Button(TextHelper.isNullOrWhiteSpace(secondButtonText) ? DEFAULT_MESSAGE_CANCEL : secondButtonText);
			cancelButton.getMargin().setValue(5);
			cancelButton.addClickListener(new ButtonClickedEvent() {
				@Override
				public void click(Button button) {
					_result = false;
					closeWindow();
				}
			});
			buttonPanel.addChild(cancelButton);
		}

		return buttonPanel;
	}
	
	private void closeWindow() {
		try {
			for (MessageBoxClosedEvent l : _closedListeners) {
				l.closed(_ui, _result);
			}		
			_ui.setParent(null);
		} catch (Exception e) {
			System.err.println("Error while attempting to close the dialog window.");
		}
	}
}
