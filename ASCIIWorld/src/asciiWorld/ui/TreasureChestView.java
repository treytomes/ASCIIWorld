package asciiWorld.ui;

import org.newdawn.slick.Color;

import asciiWorld.entities.Entity;
import asciiWorld.entities.InventoryContainer;

public class TreasureChestView extends GridViewPanel {

	private InventoryContainer _treasureChestInventory;
	private InventoryContainer _playerInventoryContainer;
	
	private ListView _treasureChestList;
	private ListView _playerList;
	
	public TreasureChestView(InventoryContainer treasureChestInventory, InventoryContainer playerInventory) {
		super(2, 3);
		
		_treasureChestInventory = treasureChestInventory;
		_playerInventoryContainer = playerInventory;
		
		setRowHeight(0, 0.1f);
		setRowHeight(1, 0.9f);
		
		try {
			// Treasure Chest Inventory
			addChild(new Label("Items to Store", Color.yellow), 0, 0);
			_treasureChestList = createItemList(_treasureChestInventory);
			addChild(new ScrollableListView(_treasureChestList), 1, 0);
			
			addChild(createButtonPanel(), 1, 1);
			
			// Player Inventory
			addChild(new Label("Items to Take", Color.yellow), 0, 2);
			_playerList = createItemList(_playerInventoryContainer);
			addChild(new ScrollableListView(_playerList), 1, 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private ListView createItemList(InventoryContainer inventory) {
		ListView itemsList = new ListView();
		itemsList.setItemsSource(inventory);
		return itemsList;
	}
	
	private StackPanel createButtonPanel() throws Exception {
		StackPanel buttonPanel = new StackPanel(Orientation.Vertical);
		
		buttonPanel.addChild(new Button("<< Store") {{
			addClickListener(new ButtonClickedEvent() {
				@Override
				public void click(Button button) {
					try {
						storeItem();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}});
		buttonPanel.addChild(new Button("Take >>") {{
			addClickListener(new ButtonClickedEvent() {
				@Override
				public void click(Button button) {
					try {
						takeItem();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}});
		
		return buttonPanel;
	}
	
	private void storeItem() throws Exception {
		if (_playerList.isItemSelected()) {
			Entity itemToStore = Entity.class.cast(_playerList.getSelectedItem());
			_playerInventoryContainer.remove(itemToStore);
			_treasureChestInventory.add(itemToStore);
			_playerList.resetBinding();
			_treasureChestList.resetBinding();
		}
	}
	
	private void takeItem() throws Exception {
		if (_treasureChestList.isItemSelected()) {
			Entity itemToTake = Entity.class.cast(_treasureChestList.getSelectedItem());
			_treasureChestInventory.remove(itemToTake);
			_playerInventoryContainer.add(itemToTake);
			_treasureChestList.resetBinding();
			_playerList.resetBinding();
		}
	}
}
