package asciiWorld.ui;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.entities.HotKeyInfo;
import asciiWorld.entities.HotKeyManager;

public class HotKeyPanel extends StackPanel {

	private static final int PANEL_SIZE = 32;
	private static final int MARGIN = 5;
	
	private List<HotKeySelectedEvent> _itemSelectedListeners;
	
	private HotKeyManager _hotkeys;
	
	public HotKeyPanel(HotKeyManager hotkeys) throws Exception {
		super(new Rectangle(0, 0, (PANEL_SIZE + MARGIN * 2) * hotkeys.size(), PANEL_SIZE + MARGIN * 2), Orientation.Horizontal);
		
		_itemSelectedListeners = new ArrayList<HotKeySelectedEvent>();
		
		_hotkeys = hotkeys;
		
		for (final HotKeyInfo info : _hotkeys) {
			HotKeyView itemPanel = new HotKeyView(info);
			itemPanel.addMouseDownListener(new MouseButtonEvent() {
				@Override
				public void mouseButtonChanged(FrameworkElement sender, MouseButton button, Vector2f mousePosition) {
					handleItemSelected(info);
				}
			});
			addChild(itemPanel);
		}
	}
	
	public void addItemSelectedListener(HotKeySelectedEvent value) {
		_itemSelectedListeners.add(value);
	}
	
	public void removeItemSelectedListener(HotKeySelectedEvent value) {
		_itemSelectedListeners.remove(value);
	}
	
	/*private int getKeyboardKeyForIndex(int index) throws Exception {
		switch (index) {
		case 0:
			return Input.KEY_0;
		case 1:
			return Input.KEY_1;
		case 2:
			return Input.KEY_2;
		case 3:
			return Input.KEY_3;
		case 4:
			return Input.KEY_4;
		case 5:
			return Input.KEY_5;
		case 6:
			return Input.KEY_6;
		case 7:
			return Input.KEY_7;
		case 8:
			return Input.KEY_8;
		case 9:
			return Input.KEY_9;
		default:
			throw new Exception("Index out of range.");	
		}
	}*/

	private void handleItemSelected(HotKeyInfo info) {
		for (HotKeySelectedEvent listener : _itemSelectedListeners) {
			listener.selected(this, info);
		}
	}
}
