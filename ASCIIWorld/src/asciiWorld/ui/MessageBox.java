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

import asciiWorld.CreateColor;
import asciiWorld.FontFactory;
import asciiWorld.TextFactory;
import asciiWorld.TextHelper;

public class MessageBox extends Border {

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
	
	private Boolean _result;
	
	private MessageBox(RootVisualPanel rootUI, Boolean isModal, String message, String title, String acceptButtonText, String cancelButtonText) throws Exception {
		super(createBounds(rootUI), CreateColor.from(COLOR_BORDER_WINDOW).changeAlphaTo(0.25f).getColor(), true);
		
		_closedListeners = new ArrayList<MessageBoxClosedEvent>();
		
		_isModal = isModal;
		_title = title;
		_acceptButtonText = acceptButtonText;
		_cancelButtonText = cancelButtonText;
		_message = message;
		_result = false;
		
		setContent(generateContent());
	}
	
	private static RoundedRectangle createBounds(RootVisualPanel rootUI) {
		Rectangle containerBounds = rootUI.getBounds();
		float width = containerBounds.getWidth() / 3;
		float height = containerBounds.getWidth() / 3;
		return new RoundedRectangle((containerBounds.getWidth() - width) / 2, (containerBounds.getHeight() - height) / 2, width, height, 8);
	}
	
	public Boolean isModal() {
		return _isModal;
	}
	
	public void addClosedListener(MessageBoxClosedEvent listener) {
		_closedListeners.add(listener);
	}
	
	public void removeClosedListener(MessageBoxClosedEvent listener) {
		_closedListeners.remove(listener);
	}
	
	public Boolean isOpen() {
		return getParent() != null;
	}
	
	public Boolean getResult() {
		return _result;
	}
	
	public static MessageBox create(RootVisualPanel rootUI, Boolean isModal, String message, String title, String acceptButtonText, String cancelButtonText) throws Exception {
		return new MessageBox(rootUI, isModal, message, title, acceptButtonText, cancelButtonText);
	}
	
	public static MessageBox create(RootVisualPanel rootUI, Boolean isModal, String message, String title, String acceptButtonText) throws Exception {
		return create(rootUI, isModal, message, title, acceptButtonText, DEFAULT_MESSAGE_CANCEL);
	}
	
	public static MessageBox create(RootVisualPanel rootUI, Boolean isModal, String message, String title) throws Exception {
		return create(rootUI, isModal, message, title, DEFAULT_MESSAGE_ACCEPT);
	}
	
	public static MessageBox create(RootVisualPanel rootUI, Boolean isModal, String message) throws Exception {
		return create(rootUI, isModal, message, DEFAULT_TITLE);
	}
	
	public static MessageBox load(RootVisualPanel rootUI, String path) throws Exception {
		return fromXml(rootUI, (Element)new SAXBuilder().build(new File(path)).getRootElement());
	}
	
	public static MessageBox fromXml(RootVisualPanel rootUI, Element elem) throws Exception {
		org.jdom2.Attribute isModalAttribute = elem.getAttribute("isModal");
		
		Boolean isModal = (isModalAttribute == null) ? false : elem.getAttribute("isModal").getBooleanValue();
		String title = elem.getAttributeValue("title");
		String message = parseMessage(elem.getChild("Message"));
		return create(rootUI, isModal, message, title);
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
	
	private Border generateContent() throws Exception {
		Rectangle bounds = getBounds();
		UnicodeFont font = FontFactory.get().getDefaultFont();
		int buttonHeight = 42;
		
		Label messageLabel = new Label(new Vector2f(0, 0), font, _message, COLOR_TEXT_MESSAGE);
		messageLabel.getMargin().setValue(5);
		messageLabel.setHorizontalContentAlignment(HorizontalAlignment.Left);
		messageLabel.setVerticalContentAlignment(VerticalAlignment.Top);
		
		Border messageBackground = new Border(new Rectangle(bounds.getMinX() + 10, bounds.getMinY() + 40, bounds.getWidth() - 20, bounds.getHeight() - 50 - buttonHeight), COLOR_BORDER_MESSAGE, false);
		messageBackground.setContent(messageLabel);
		
		Color messageFillColor = CreateColor.from(COLOR_BORDER_MESSAGE).changeAlphaTo(0.25f).getColor();

		Border messageBorder = new Border(new Rectangle(bounds.getMinX() + 10, bounds.getMinY() + 40, bounds.getWidth() - 20, bounds.getHeight() - 50 - buttonHeight), messageFillColor, true);
		messageBorder.setContent(messageBackground);
		
		CanvasPanel windowCanvas = new CanvasPanel();
		windowCanvas.addChild(new Label(new Vector2f(bounds.getMinX() + (bounds.getWidth() - font.getWidth(_title)) / 2, bounds.getMinY() + 10), font, _title, COLOR_TEXT_TITLE));
		windowCanvas.addChild(messageBorder);
		windowCanvas.addChild(getButtons(bounds, _acceptButtonText, _cancelButtonText));
		
		Border windowBackground = new Border(bounds, COLOR_BORDER_WINDOW, false);
		windowBackground.setContent(windowCanvas);
		
		return windowBackground;
	}
	
	private StackPanel getButtons(Rectangle dialogBounds, final String firstButtonText, final String secondButtonText) throws Exception {
		int numberOfButtons = TextHelper.isNullOrWhiteSpace(secondButtonText) ? 1 : 2;
		int buttonWidth = 106;
		int myWidth = buttonWidth * numberOfButtons;
		
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
				l.closed(this, _result);
			}		
			this.setParent(null);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error while attempting to close the dialog window.");
		}
	}
}
