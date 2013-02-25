package asciiWorld.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

import asciiWorld.FontFactory;
import asciiWorld.entities.InventoryContainer;

public class InventoryView extends GridViewPanel {
	
	private static final Color COLOR_TEXT_DESCRIPTION = Color.white;
	private static final Color COLOR_TEXT_DETAILS = Color.yellow;
	
	private ListView _itemList;
	private GridViewPanel _itemDetails;

	public InventoryView(InventoryContainer inventory) throws Exception {
		super(1, 2);
		
		_itemList = createItemList(inventory);
		addChild(_itemList, 0, 0);
		
		_itemDetails = createDetailsPanel();
		addChild(_itemDetails, 0, 1);
	}
	
	private ListView createItemList(InventoryContainer inventory) {
		ListView itemsList = new ListView();
		itemsList.setItemsSource(inventory);
		return itemsList;
	}
	
	private GridViewPanel createDetailsPanel() throws Exception {
		MethodBinding selectedItemBinding = new MethodBinding(_itemList, "getSelectedItem");
		
		GridViewPanel details = new GridViewPanel(6, 2);
		
		UnicodeFont largeFont = FontFactory.get().getResource(30);
		
		details.addChild(new Label(largeFont, "Details for:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 0, 0);
		details.addChild(new Label(largeFont, new MethodBinding(selectedItemBinding, "getName"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 0, 1);
		
		details.addChild(new Label("Weight:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 1, 0);
		details.addChild(new Label(new MethodBinding(selectedItemBinding, "getTotalWeight"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 1, 1);

		details.addChild(new Label("Agility:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 2, 0);
		details.addChild(new Label(new MethodBinding(selectedItemBinding, "getAgility"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 2, 1);
		
		details.addChild(new Label("Perception:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 3, 0);
		details.addChild(new Label(new MethodBinding(selectedItemBinding, "getPerception"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 3, 1);
		
		details.addChild(new Label("Movement speed:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 4, 0);
		details.addChild(new Label(new MethodBinding(selectedItemBinding, "getMovementSpeed"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 4, 1);
		
		details.addChild(new Label("Range of vision:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 5, 0);
		details.addChild(new Label(new MethodBinding(selectedItemBinding, "getRangeOfVision"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 5, 1);
		
		return details;
	}
}
