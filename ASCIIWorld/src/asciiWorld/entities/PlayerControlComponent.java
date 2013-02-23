package asciiWorld.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.CreateRectangle;
import asciiWorld.Direction;
import asciiWorld.ui.GridViewPanel;
import asciiWorld.ui.Label;
import asciiWorld.ui.ListView;
import asciiWorld.ui.ListViewItemSelectedEvent;
import asciiWorld.ui.RootVisualPanel;
import asciiWorld.ui.WindowPanel;

public class PlayerControlComponent extends KeyboardAwareComponent {
	
	private static final int KEY_MOVE_NORTH = Input.KEY_W;
	private static final int KEY_MOVE_SOUTH = Input.KEY_S;
	private static final int KEY_MOVE_WEST = Input.KEY_A;
	private static final int KEY_MOVE_EAST = Input.KEY_D;
	private static final int KEY_TOUCH = Input.KEY_SPACE;
	private static final int KEY_INVENTORY = Input.KEY_ESCAPE;
	
	private RootVisualPanel _ui;
	private WindowPanel _inventoryUI;
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
		return (_inventoryUI != null) && !_inventoryUI.isClosed();
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
	
	private WindowPanel generateUI(InventoryContainer inventory) throws Exception {
		Rectangle bounds = CreateRectangle
				.from(_ui.getBounds())
				.scale(2.0f / 3.0f)
				.centerOn(_ui.getBounds())
				.getRectangle();
		
		ListView itemsList = createItemList(inventory);
		Label detailsLabel = new Label("Details", Color.red);
		
		GridViewPanel gridView = new GridViewPanel(1, 2);
		gridView.addChild(itemsList, 0, 0);
		gridView.addChild(detailsLabel, 0, 1);
		
		WindowPanel window = new WindowPanel(bounds, "Inventory");
		window.setWindowContent(gridView);

		gridView.resetBounds();

		return window;
	}
	
	private ListView createItemList(InventoryContainer inventory) {
		ListView itemsList = new ListView();
		itemsList.setItemsSource(inventory);
		itemsList.addItemSelectedListener(new ListViewItemSelectedEvent() {
			@Override
			public void itemSelected(ListView listView, Object selectedItem) {
				// TODO: Change _ui to listView.getRoot()
				_ui.showMessageBox(true, String.format("Selected an item: %s", selectedItem.toString()), "You selected an item!");
			}
		});
		return itemsList;
	}
}
