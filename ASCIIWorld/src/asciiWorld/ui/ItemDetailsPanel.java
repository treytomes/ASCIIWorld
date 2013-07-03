package asciiWorld.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

import asciiWorld.FontFactory;
import asciiWorld.entities.Entity;
import asciiWorld.entities.HotKeyInfo;
import asciiWorld.entities.HotKeyManager;

public class ItemDetailsPanel extends GridViewPanel {

	private static final Color COLOR_TEXT_DESCRIPTION = Color.white;
	private static final Color COLOR_TEXT_DETAILS = Color.yellow;
	
	private Object _itemBinding;
	
	public ItemDetailsPanel(Object itemBinding, final HotKeyManager hotkeys) {
		super(8, 2);
		
		_itemBinding = itemBinding;
		
		setColumnWidth(0, 0.4f);
		setColumnWidth(1, 0.6f);
		
		try {
			UnicodeFont largeFont = FontFactory.get().getResource(30);
		
			addChild(new Label(largeFont, "Details for:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 0, 0);
			addChild(new Label(largeFont, new MethodBinding(_itemBinding, "getName"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 0, 1);
			
			addChild(new Label("Health:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 1, 0);
			addChild(new Label(new MethodBinding(this, "getHealthText", _itemBinding), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 1, 1);
			
			addChild(new Label("Weight:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 2, 0);
			addChild(new Label(new MethodBinding(_itemBinding, "getTotalWeight"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 2, 1);
	
			addChild(new Label("Agility:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 3, 0);
			addChild(new Label(new MethodBinding(_itemBinding, "getAgility"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 3, 1);
			
			addChild(new Label("Perception:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 4, 0);
			addChild(new Label(new MethodBinding(_itemBinding, "getPerception"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 4, 1);
			
			addChild(new Label("Movement speed:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 5, 0);
			addChild(new Label(new MethodBinding(_itemBinding, "getMovementSpeed"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 5, 1);
			
			addChild(new Label("Range of vision:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 6, 0);
			addChild(new Label(new MethodBinding(_itemBinding, "getRangeOfVision"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 6, 1);
			
			addChild(new Label("Hot key assignment:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, 7, 0);
			HotKeyPanel hotkeyPanel = new HotKeyPanel(hotkeys);
			hotkeyPanel.addItemSelectedListener(new HotKeySelectedEvent() {
				@Override
				public void selected(HotKeyPanel sender, HotKeyInfo info) {
					assignItemToHotKey(hotkeys, info);
				}
			});
			addChild(hotkeyPanel, 7, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Entity getItem() {
		if (_itemBinding instanceof Entity) {
			return Entity.class.cast(_itemBinding);
		} else if (_itemBinding instanceof MethodBinding) {
			return Entity.class.cast(MethodBinding.class.cast(_itemBinding).getValue());
		} else {
			return null;
		}
	}
	
	public String getHealthText(Entity item) {
		return String.format("%d / %d", item.getHealth(), item.getMaxHealth());
	}
	
	private void assignItemToHotKey(HotKeyManager hotkeys, HotKeyInfo info) {
		Entity item = getItem();
		
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
}
