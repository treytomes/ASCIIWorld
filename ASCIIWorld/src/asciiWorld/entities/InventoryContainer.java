package asciiWorld.entities;

import java.util.ArrayList;
import java.util.List;

public class InventoryContainer {
	
	private Entity _owner;
	private List<Entity> _items;
	
	public InventoryContainer(Entity owner) {
		_owner = owner;
		_items = new ArrayList<Entity>();
	}
	
	public Entity getOwner() {
		return _owner;
	}
	
	public int getItemCount() {
		return _items.size();
	}
	
	public Entity getItemAt(int index) {
		return _items.get(index);
	}
	
	public void remove(Entity item) throws Exception {
		if (item == null) {
			throw new Exception("Please specify an item to remove from the inventory.");
		}
		else if (!_items.contains(item)) {
			throw new Exception("That item is not in the inventory.");
		} else {
			_items.remove(item);
			item.setContainer(null);
		}
	}
	
	public void add(Entity item) throws Exception {
		if (item == null) {
			throw new Exception("Please specify an item to add to the inventory.");
		}
		else if (_items.contains(item)) {
			throw new Exception("That item is already in the inventory.");
		} else {
			if (item.getChunk() != null) {
				item.getChunk().removeEntity(item);
			}
			if (item.getContainer() != null) {
				item.getContainer().remove(item);
			}
			_items.add(item);
			item.setContainer(this);
		}
	}
}
