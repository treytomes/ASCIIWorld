package asciiWorld.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;

import asciiWorld.entities.InventoryContainer;

public class InventoryView extends GridViewPanel {
	
	private static final Color COLOR_TEXT_DETAILS = Color.yellow;
	
	private Label _itemDetails;

	public InventoryView(InventoryContainer inventory) throws Exception {
		super(1, 2);
		
		addChild(createItemList(inventory), 0, 0);
		
		_itemDetails = createDetailsPanel();
		addChild(_itemDetails, 0, 1);
	}
	
	private ListView createItemList(InventoryContainer inventory) {
		ListView itemsList = new ListView();
		itemsList.setItemsSource(inventory);
		itemsList.addItemSelectedListener(new ListViewItemSelectedEvent() {
			@Override
			public void itemSelected(ListView listView, Object selectedItem) {
				_itemDetails.setText(String.format("Selected an item: %s", selectedItem.toString()));
			}
		});
		return itemsList;
	}
	
	private Label createDetailsPanel() throws SlickException {
		return new Label("Details", COLOR_TEXT_DETAILS);
	}
}
