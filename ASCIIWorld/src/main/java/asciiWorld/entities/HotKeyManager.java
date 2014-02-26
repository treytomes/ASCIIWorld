package asciiWorld.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;

/**
 * Entity service that manages the hot-key/inventory index association.
 * 
 * @author ttomes
 *
 */
public class HotKeyManager implements Iterable<HotKeyInfo>, KeyListener {
	
	private Entity _owner;
	private List<HotKeyInfo> _list;
	
	public HotKeyManager(Entity owner) {
		_owner = owner;
		
		_list = new ArrayList<HotKeyInfo>();
		_list.add(new HotKeyInfo(getOwner(), Input.KEY_1));
		_list.add(new HotKeyInfo(getOwner(), Input.KEY_2));
		_list.add(new HotKeyInfo(getOwner(), Input.KEY_3));
		_list.add(new HotKeyInfo(getOwner(), Input.KEY_4));
		_list.add(new HotKeyInfo(getOwner(), Input.KEY_5));
		_list.add(new HotKeyInfo(getOwner(), Input.KEY_6));
		_list.add(new HotKeyInfo(getOwner(), Input.KEY_7));
		_list.add(new HotKeyInfo(getOwner(), Input.KEY_8));
		_list.add(new HotKeyInfo(getOwner(), Input.KEY_9));
		_list.add(new HotKeyInfo(getOwner(), Input.KEY_0));
	}
	
	public int size() {
		return _list.size();
	}
	
	public Entity getOwner() {
		return _owner;
	}
	
	public int indexOf(Entity item) {
		for (int index = 0; index < _list.size(); index++) {
			HotKeyInfo info = _list.get(index);
			try {
				if (info.getItem() == item) {
					return index;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	public int indexOf(HotKeyInfo info) {
		return _list.indexOf(info);
	}
	
	public HotKeyInfo get(int index) {
		return _list.get(index);
	}

	@Override
	public Iterator<HotKeyInfo> iterator() {
		return _list.iterator();
	}

	@Override
	public void keyPressed(int key, char c) {
		for (HotKeyInfo info : this) {
			if (info.getKeyboardKey() == key) {
				activateItem(info);
				break;
			}
		}
	}
	
	public void activatePreviousItem() {
		int startingIndex = indexOf(getOwner().getActiveItem());
		if (startingIndex < 0) {
			startingIndex = 0;
		}
		
		int index = startingIndex - 1;
		while (true) {
			if (index < 0) {
				index = size() - 1;
			}
			if (index == startingIndex) {
				break;
			}
			
			try {
				HotKeyInfo info = get(index);
				if (info.getItem() != null) {
					activateItem(info);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			index--;
		}
	}
	
	public void activateNextItem() {
		int startingIndex = indexOf(getOwner().getActiveItem());
		if (startingIndex >= size()) {
			startingIndex = size() - 1;
		}
		
		int index = startingIndex + 1;
		while (true) {
			if (index >= size()) {
				index = 0;
			}
			if (index == startingIndex) {
				break;
			}
			
			try {
				HotKeyInfo info = get(index);
				if (info.getItem() != null) {
					activateItem(info);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			index++;
		}
	}
	
	private void activateItem(HotKeyInfo info) {
		try {
			getOwner().setActiveItem(info.getItem());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to active the item.");
		}
	}

	@Override
	public boolean isAcceptingInput() {
		return true;
	}

	@Override
	public void setInput(Input input) {
	}

	@Override
	public void inputEnded() {
	}

	@Override
	public void inputStarted() {
	}

	@Override
	public void keyReleased(int key, char c) {
	}
}