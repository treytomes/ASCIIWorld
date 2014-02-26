package asciiWorld.ui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import asciiWorld.entities.Entity;
import asciiWorld.entities.HotKeyInfo;
import asciiWorld.entities.HotKeyManager;
import asciiWorld.entities.InventoryContainer;

public class InventoryView extends GridViewPanel {
	
	private ListView _itemList;
	private GridViewPanel _itemDetails;
	private HotKeyManager _hotkeys;

	public InventoryView(InventoryContainer inventory, HotKeyManager hotkeys) throws Exception {
		super(1, 2);
		
		_hotkeys = hotkeys;
		
		setColumnWidth(0, 0.25f);
		setColumnWidth(1, 0.75f);
		
		_itemList = createItemList(inventory);
		addChild(new ScrollableListView(_itemList), 0, 0);
		
		_itemDetails = createDetailsPanel(hotkeys);
		addChild(_itemDetails, 0, 1);
	}
	
	public Entity getSelectedItem() {
		return Entity.class.cast(_itemList.getSelectedItem());
	}
	
	@Override
	public void update(GameContainer container, int delta) {
		super.update(container, delta);
		assignItemToHotKey(container);
	}
	
	private void assignItemToHotKey(GameContainer container) {
		Input input = container.getInput();
		for (HotKeyInfo info : _hotkeys) {
			if (input.isKeyDown(info.getKeyboardKey())) {
				clearOldSelection();
				info.setItem(getSelectedItem());
			}
		}
	}
	
	private void clearOldSelection() {
		Entity item = getSelectedItem();

		for (HotKeyInfo hk : _hotkeys) {
			try {
				if (hk.getItem() == item) {
					hk.setItem(null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private ListView createItemList(InventoryContainer inventory) {
		ListView itemsList = new ListView();
		itemsList.setItemsSource(inventory);
		return itemsList;
	}
	
	private GridViewPanel createDetailsPanel(HotKeyManager hotkeys) throws Exception {
		MethodBinding selectedItemBinding = new MethodBinding(_itemList, "getSelectedItem");
		return new ItemDetailsPanel(selectedItemBinding, hotkeys);
	}
}
