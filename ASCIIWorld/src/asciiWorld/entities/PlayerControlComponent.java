package asciiWorld.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.CreateColor;
import asciiWorld.Direction;
import asciiWorld.FontFactory;
import asciiWorld.ui.Border;
import asciiWorld.ui.Button;
import asciiWorld.ui.CanvasPanel;
import asciiWorld.ui.Label;
import asciiWorld.ui.ListView;
import asciiWorld.ui.ListViewItemSelectedEvent;
import asciiWorld.ui.MethodBinding;
import asciiWorld.ui.RootVisualPanel;
import asciiWorld.ui.StackPanel;

public class PlayerControlComponent extends KeyboardAwareComponent {
	
	private static final int KEY_MOVE_NORTH = Input.KEY_W;
	private static final int KEY_MOVE_SOUTH = Input.KEY_S;
	private static final int KEY_MOVE_WEST = Input.KEY_A;
	private static final int KEY_MOVE_EAST = Input.KEY_D;
	private static final int KEY_TOUCH = Input.KEY_SPACE;
	private static final int KEY_INVENTORY = Input.KEY_ESCAPE;
	
	private RootVisualPanel _ui;
	private Border _inventoryUI;
	private Direction _movingDirection;

	public PlayerControlComponent(Entity owner, RootVisualPanel ui) {
		super(owner);
		_ui = ui;
		_inventoryUI = null;
		_movingDirection = null;
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int deltaTime) {
		super.update(container, game, deltaTime);
		
		if (_movingDirection != null) {
			getOwner().move(_movingDirection);
		}
	}
	
	@Override
	public boolean isAcceptingInput() {
		return !isInventoryUIOpen();
	}
	
	@Override
	public void keyPressed(int key, char c) {
		switch (key) {
		case KEY_MOVE_NORTH:
			_movingDirection = Direction.North;
			break;
		case KEY_MOVE_SOUTH:
			_movingDirection = Direction.South;
			break;
		case KEY_MOVE_EAST:
			_movingDirection = Direction.East;
			break;
		case KEY_MOVE_WEST:
			_movingDirection = Direction.West;
			break;
		case KEY_TOUCH:
			getOwner().touch();
			break;
		case KEY_INVENTORY:
			openInventoryInterface();
			break;
		}
	}
	
	@Override
	public void keyReleased(int key, char c) {
		switch (key) {
		case KEY_MOVE_NORTH:
		case KEY_MOVE_SOUTH:
		case KEY_MOVE_EAST:
		case KEY_MOVE_WEST:
			_movingDirection = null;
			break;
		}
	}
	
	private Boolean isInventoryUIOpen() {
		return _inventoryUI != null;
	}
	
	private void openInventoryInterface() {
		try {
			InventoryContainer inventory = getOwner().getInventory();
			createInventoryWindow(inventory);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to open the inventory interface.");
		}
	}
	
	private void createInventoryWindow(InventoryContainer inventory) throws Exception {
		_inventoryUI = generateUI(inventory);
		_ui.addModalChild(_inventoryUI);
	}
	
	private static final Color COLOR_BORDER_WINDOW = new Color(0.5f, 0.5f, 1.0f);
	private static final Color COLOR_CONTENT_BORDER = new Color(0.0f, 0.75f, 0.5f);
	private static final Color COLOR_TEXT_TITLE = Color.white;
	
	private Border generateUI(InventoryContainer inventory) throws Exception {
		Rectangle containerBounds = _ui.getBounds();
		float width = containerBounds.getWidth() / 3;
		float height = containerBounds.getWidth() / 3;
		RoundedRectangle _bounds = new RoundedRectangle((containerBounds.getWidth() - width) / 2, (containerBounds.getHeight() - height) / 2, width, height, 8);
		UnicodeFont font = FontFactory.get().getDefaultFont();
		Color windowFillColor = CreateColor.from(COLOR_BORDER_WINDOW).changeAlphaTo(0.25f).getColor();
		int buttonHeight = 42;
		
		ListView itemsList = new ListView();
		itemsList.setItemsSource(inventory);
		itemsList.addItemSelectedListener(new ListViewItemSelectedEvent() {
			@Override
			public void itemSelected(ListView listView, Object selectedItem) {
				_ui.showMessageBox(true, String.format("Selected an item: %s", selectedItem.toString()), "You selected an item!");
			}
		});
		
		Border contentBackground = new Border(new Rectangle(_bounds.getMinX() + 10, _bounds.getMinY() + 40, _bounds.getWidth() - 20, _bounds.getHeight() - 50 - buttonHeight), COLOR_CONTENT_BORDER, false);
		contentBackground.setContent(itemsList);
		
		Color contentFillColor = CreateColor.from(COLOR_CONTENT_BORDER).changeAlphaTo(0.25f).getColor();

		Border contentBorder = new Border(new Rectangle(_bounds.getMinX() + 10, _bounds.getMinY() + 40, _bounds.getWidth() - 20, _bounds.getHeight() - 50 - buttonHeight), contentFillColor, true);
		contentBorder.setContent(contentBackground);
		
		CanvasPanel windowCanvas = new CanvasPanel();
		String titleText = "Inventory";
		windowCanvas.addChild(new Label(new Vector2f(_bounds.getMinX() + (_bounds.getWidth() - font.getWidth(titleText)) / 2, _bounds.getMinY() + 10), font, titleText, COLOR_TEXT_TITLE));
		windowCanvas.addChild(contentBorder);
		windowCanvas.addChild(getButtons(_bounds));
		
		Border windowBackground = new Border(_bounds, COLOR_BORDER_WINDOW, false);
		windowBackground.setContent(windowCanvas);
		
		Border windowBorder = new Border(_bounds, windowFillColor, true);
		windowBorder.setContent(windowBackground);
		
		return windowBorder;
	}
	
	private StackPanel getButtons(RoundedRectangle dialogBounds) throws Exception {
		int numberOfButtons = 1;
		int margin = 5;
		int buttonWidth = 106;
		int buttonHeight = 42;
		int myWidth = buttonWidth * numberOfButtons;
		
		StackPanel buttonPanel = new StackPanel(
				new Rectangle(
						dialogBounds.getMinX() + (dialogBounds.getWidth() - myWidth) / 2,
						dialogBounds.getMaxY() - buttonHeight - margin,
						myWidth,
						buttonHeight));
		
		buttonPanel.addChild(Button.createActionButton("Close", new MethodBinding(this, "closeInventoryUI")));

		return buttonPanel;
	}
	
	public void closeInventoryUI() {
		try {
			_inventoryUI.getParent().setParent(null); // close the  modal panel
			_inventoryUI.setParent(null); // close the inventory window
			_inventoryUI = null;
			_ui.modalWindowIsClosing();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error while attempting to close the inventory interface window.");
		}
	}
}
