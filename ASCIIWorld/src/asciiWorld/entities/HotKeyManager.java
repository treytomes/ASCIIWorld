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