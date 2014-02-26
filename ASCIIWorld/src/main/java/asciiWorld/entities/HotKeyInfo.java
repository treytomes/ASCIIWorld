package asciiWorld.entities;

import org.newdawn.slick.Input;

import asciiWorld.ui.MethodBinding;

public class HotKeyInfo {
	
	private Object _ownerBinding;
	private int _keyboardKey;
	private Entity _item;
	
	public HotKeyInfo(Object ownerBinding, int keyboardKey) {
		_ownerBinding = ownerBinding;
		_keyboardKey = keyboardKey;
	}
	
	public Entity getOwner() throws Exception {
		if (_ownerBinding instanceof Entity) {
			return (Entity)_ownerBinding;
		} else if (_ownerBinding instanceof MethodBinding) {
			return (Entity)((MethodBinding)_ownerBinding).getValue();
		} else {
			throw new Exception("Invalid binding.");
		}
	}
	
	public int getKeyboardKey() {
		return _keyboardKey;
	}
	
	public String getKeyboardKeyName() {
		return Input.getKeyName(getKeyboardKey());
	}
	
	public Entity getItem() throws Exception {
		if (_item != null) {
			if (!getOwner().getInventory().contains(_item)) {
				_item = null;
			}
		}
		return _item;
	}
	
	public void setItem(Entity value) {
		_item = value;
	}
	
	public Boolean isActive() {
		try {
			return (getItem() != null) && (getOwner().getActiveItem() == getItem());
		} catch (Exception e) {
			return false;
		}
	}
}