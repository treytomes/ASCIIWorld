package asciiWorld.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.Direction;
import asciiWorld.FontFactory;
import asciiWorld.ui.Border;
import asciiWorld.ui.Button;
import asciiWorld.ui.ButtonClickedEvent;
import asciiWorld.ui.CanvasPanel;
import asciiWorld.ui.FrameworkElement;
import asciiWorld.ui.HorizontalAlignment;
import asciiWorld.ui.Label;
import asciiWorld.ui.RootVisualPanel;
import asciiWorld.ui.StackPanel;
import asciiWorld.ui.VerticalAlignment;

public class PlayerControlComponent extends EntityComponent {
	
	private static final int KEY_MOVE_NORTH = Input.KEY_W;
	private static final int KEY_MOVE_SOUTH = Input.KEY_S;
	private static final int KEY_MOVE_WEST = Input.KEY_A;
	private static final int KEY_MOVE_EAST = Input.KEY_D;
	private static final int KEY_TOUCH = Input.KEY_SPACE;
	
	private RootVisualPanel _ui;
	private FrameworkElement _inventoryUI;

	public PlayerControlComponent(Entity owner, RootVisualPanel ui) {
		super(owner);
		_ui = ui;
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int deltaTime) {
		Input input = container.getInput();
		
		if (input.isKeyDown(KEY_MOVE_NORTH)) {
			getOwner().move(Direction.North);
		} else if (input.isKeyDown(KEY_MOVE_SOUTH)) {
			getOwner().move(Direction.South);
		} else if (input.isKeyDown(KEY_MOVE_WEST)) {
			getOwner().move(Direction.West);
		} else if (input.isKeyDown(KEY_MOVE_EAST)) {
			getOwner().move(Direction.East);
		}
		
		if (input.isKeyPressed(KEY_TOUCH)) {
			getOwner().touch();
		}
		if (input.isKeyPressed(Input.KEY_ESCAPE)) {
			openInventoryInterface();
		}
	}
	
	private void openInventoryInterface() {
		StringBuilder sb = new StringBuilder();
		InventoryContainer inventory = getOwner().getInventory();
		for (int index = 0; index < inventory.getItemCount(); index++) {
			sb.append(inventory.getItemAt(index).getName()).append("\n");
		}
		
		try {
			createInventoryWindow(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to open the inventory interface.");
		}
	}
	
	private void createInventoryWindow(String text) throws Exception {
		_inventoryUI = makeUIModal(generateUI(text));
		_ui.addChild(_inventoryUI);
	}
	
	private static final Color COLOR_MODAL_BACKGROUND = new Color(0, 0, 0, 0.5f);
	private static final Color COLOR_BORDER_WINDOW = new Color(0.5f, 0.5f, 1.0f);
	private static final Color COLOR_BORDER_MESSAGE = new Color(0.0f, 0.75f, 0.5f);
	private static final Color COLOR_TEXT_TITLE = Color.white;
	private static final Color COLOR_TEXT_MESSAGE = Color.yellow;

	private CanvasPanel makeUIModal(FrameworkElement ui) throws Exception {
		Rectangle containerBounds = ui.getBounds();
		CanvasPanel panel = new CanvasPanel(new Rectangle(0, 0, containerBounds.getWidth(), containerBounds.getHeight()));
		panel.addChild(new Border(new Rectangle(0, 0, containerBounds.getWidth(), containerBounds.getHeight()), COLOR_MODAL_BACKGROUND, true));
		panel.addChild(ui);
		return panel;
	}
	
	private Border generateUI(String text) throws Exception {
		Rectangle containerBounds = _ui.getBounds();
		float width = containerBounds.getWidth() / 3;
		float height = containerBounds.getWidth() / 3;
		RoundedRectangle _bounds = new RoundedRectangle((containerBounds.getWidth() - width) / 2, (containerBounds.getHeight() - height) / 2, width, height, 8);
		UnicodeFont font = FontFactory.get().getDefaultFont();
		Color windowFillColor = new Color(COLOR_BORDER_WINDOW);
		windowFillColor.a = 0.25f;
		
		Label messageLabel = new Label(new Vector2f(0, 0), font, text, COLOR_TEXT_MESSAGE);
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
		String titleText = "Inventory";
		windowCanvas.addChild(new Label(new Vector2f(_bounds.getMinX() + (_bounds.getWidth() - font.getWidth(titleText)) / 2, _bounds.getMinY() + 10), font, titleText, COLOR_TEXT_TITLE));
		windowCanvas.addChild(messageBorder);
		windowCanvas.addChild(getButtons(_bounds));
		
		Border windowBackground = new Border(_bounds, COLOR_BORDER_WINDOW, false);
		windowBackground.setContent(windowCanvas);
		
		Border windowBorder = new Border(_bounds, windowFillColor, true);
		windowBorder.setContent(windowBackground);
		
		return windowBorder;
	}
	
	private StackPanel getButtons(RoundedRectangle dialogBounds) throws Exception {
		int numberOfButtons = 1;
		int myWidth = 106 * numberOfButtons;
		
		StackPanel buttonPanel = new StackPanel(new Rectangle(dialogBounds.getMinX() + (dialogBounds.getWidth() - myWidth) / 2, dialogBounds.getMaxY() - 42 - 5, myWidth, 42));
		
		Button closeButton = new Button("Close");
		closeButton.getMargin().setValue(5);
		closeButton.addClickListener(new ButtonClickedEvent() {
			@Override
			public void click(Button button) {
				try {
					_inventoryUI.setParent(null);
				} catch (Exception e) {
					System.err.println("Error while attempting to close the inventory interface window.");
				}
			}
		});
		buttonPanel.addChild(closeButton);

		return buttonPanel;
	}
}
