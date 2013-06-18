package asciiWorld.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.UnicodeFont;

import asciiWorld.FontFactory;
import asciiWorld.entities.Entity;
import asciiWorld.entities.HotKeyInfo;
import asciiWorld.entities.HotKeyManager;
import asciiWorld.entities.InventoryContainer;

public class InventoryView extends GridViewPanel {
	
	private static final Color COLOR_TEXT_DESCRIPTION = Color.white;
	private static final Color COLOR_TEXT_DETAILS = Color.yellow;
	
	//private InventoryContainer _inventory;
	private ListView _itemList;
	private GridViewPanel _itemDetails;
	private HotKeyManager _hotkeys;

	public InventoryView(InventoryContainer inventory, HotKeyManager hotkeys) throws Exception {
		super(1, 2);
		
		_hotkeys = hotkeys;
		
		setColumnWidth(0, 0.25f);
		setColumnWidth(1, 0.75f);
		
		//_inventory = inventory;
		
		_itemList = createItemList(inventory);
		
		StackPanel verticalScrollButtons = new StackPanel(Orientation.Vertical);
		verticalScrollButtons.addChild(new Button("^") {{
			addClickListener(new ButtonClickedEvent() {
				@Override
				public void click(Button button) {
					_itemList.setTopIndex(_itemList.getTopIndex() - 1);
				}
			});
		}});
		verticalScrollButtons.addChild(new Button("V") {{
			addClickListener(new ButtonClickedEvent() {
				@Override
				public void click(Button button) {
					_itemList.setTopIndex(_itemList.getTopIndex() + 1);
				}
			});
		}});
		
		GridViewPanel verticalScrollView = new GridViewPanel(1, 2);
		verticalScrollView.setColumnWidth(0, 0.9f);
		verticalScrollView.setColumnWidth(1, 0.1f);
		verticalScrollView.addChild(_itemList, 0, 0);
		verticalScrollView.addChild(verticalScrollButtons, 0, 1);
		
		addChild(verticalScrollView, 0, 0);
		
		_itemDetails = createDetailsPanel(hotkeys);
		addChild(_itemDetails, 0, 1);
	}
	
	@Override
	public void update(GameContainer container, int delta) {
		super.update(container, delta);
		
		Input input = container.getInput();
		for (HotKeyInfo info : _hotkeys) {
			if (input.isKeyDown(info.getKeyboardKey())) {
				info.setItem(Entity.class.cast(_itemList.getSelectedItem()));
			}
		}
	}
	
	private ListView createItemList(InventoryContainer inventory) {
		ListView itemsList = new ListView();
		itemsList.setItemsSource(inventory);
		return itemsList;
	}
	
	private GridViewPanel createDetailsPanel(final HotKeyManager hotkeys) throws Exception {
		MethodBinding selectedItemBinding = new MethodBinding(_itemList, "getSelectedItem");
		
		GridViewPanel details = new GridViewPanel(7, 2);
		details.setColumnWidth(0, 0.4f);
		details.setColumnWidth(1, 0.6f);
		
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
		
		details.addChild(new Label("Hot key assignment:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 6, 0);
		HotKeyPanel hotkeyPanel = new HotKeyPanel(hotkeys);
		hotkeyPanel.addItemSelectedListener(new HotKeySelectedEvent() {
			@Override
			public void selected(HotKeyPanel sender, HotKeyInfo info) {
				Entity item = (Entity)_itemList.getSelectedItem();
				
				for (HotKeyInfo hk : hotkeys) {
					try {
						if (hk.getItem() == item) {
							hk.setItem(null);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				info.setItem(item);
			}
		});
		details.addChild(hotkeyPanel, 6, 1);
		
		return details;
	}
}
