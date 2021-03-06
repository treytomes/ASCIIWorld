package asciiWorld.ui;

import java.io.File;
//import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.CreateRectangle;
import asciiWorld.TextFactory;

public class MessageBox extends WindowPanel {

	private static final Color COLOR_TEXT_MESSAGE = Color.yellow;
	
	private static final String DEFAULT_TITLE = "Message Box";
	private static final String DEFAULT_MESSAGE_ACCEPT = "Okay";
	private static final String DEFAULT_MESSAGE_CANCEL = "";

	//private List<MessageBoxClosedEvent> _closedListeners;

	private Boolean _isModal;
	private Label _messageLabel;
	//private String _acceptButtonText;
	//private String _cancelButtonText;
	
	private Boolean _result;
	
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
	
	private MessageBox(RootVisualPanel rootUI, Boolean isModal, String message, String title, String acceptButtonText, String cancelButtonText) throws Exception {
		super(createBounds(rootUI), title);
		
		//_closedListeners = new ArrayList<MessageBoxClosedEvent>();
		
		_isModal = isModal;
		//_acceptButtonText = acceptButtonText;
		//_cancelButtonText = cancelButtonText;
		_result = false;
		
		_messageLabel = new Label(new Vector2f(0, 0), message, COLOR_TEXT_MESSAGE);
		_messageLabel.getMargin().setValue(5);
		_messageLabel.setHorizontalContentAlignment(HorizontalAlignment.Left);
		_messageLabel.setVerticalContentAlignment(VerticalAlignment.Top);
		
		setWindowContent(_messageLabel);
	}
	
	private static RoundedRectangle createBounds(RootVisualPanel rootUI) {
		return CreateRectangle
				.from(rootUI.getBounds())
				.scale(1.0f / 3.0f, 2.0f / 3.0f)
				.centerOn(rootUI.getBounds())
				.setCornerRadius(8)
				.getRectangle();
	}
	
	public Label getMessageLabel() {
		return _messageLabel;
	}
	
	public Boolean isModal() {
		return _isModal;
	}
	
	/*
	public void addClosedListener(MessageBoxClosedEvent listener) {
		_closedListeners.add(listener);
	}
	
	public void removeClosedListener(MessageBoxClosedEvent listener) {
		_closedListeners.remove(listener);
	}
	*/
	
	public Boolean isOpen() {
		return getParent() != null;
	}
	
	public Boolean getResult() {
		return _result;
	}
	
	/*private StackPanel getButtons(Rectangle dialogBounds, final String firstButtonText, final String secondButtonText) throws Exception {
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
				closeMessageWindow();
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
					closeMessageWindow();
				}
			});
			buttonPanel.addChild(cancelButton);
		}

		return buttonPanel;
	}
	
	private void closeMessageWindow() {
		try {
			for (MessageBoxClosedEvent l : _closedListeners) {
				l.closed(this, _result);
			}		
			this.setParent(null);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error while attempting to close the dialog window.");
		}
	}*/
}
