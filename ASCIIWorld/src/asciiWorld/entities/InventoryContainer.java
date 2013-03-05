package asciiWorld.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import asciiWorld.Require;

public class InventoryContainer implements Iterable<Entity> {
	
	private Entity _owner;
	private List<Entity> _items;
	
	public InventoryContainer(Entity owner) throws Exception {
		Require.that(owner, "owner").isNotNull();
		
		_owner = owner;
		_items = new ArrayList<Entity>();
	}
	
	public Entity getOwner() {
		return _owner;
	}
	
	public int getItemCount() {
		return _items.size();
	}
	
	public Entity getItemAt(Integer index) {
		if ((index < 0) || (index > _items.size())) {
			return null;
		} else {
			return _items.get(index);
		}
	}
	
	public Boolean contains(Entity item) {
		return _items.contains(item);
	}
	
	public void remove(Entity item) throws Exception {
		if (item == null) {
			throw new Exception("Please specify an item to remove from the inventory.");
		}
		else if (!contains(item)) {
			throw new Exception("That item is not in the inventory.");
		} else {
			for (EntityComponent component : item.getComponents()) {
				component.beforeRemovedFromInventory(this);
			}
			_items.remove(item);
			item.setContainer(null);
			for (EntityComponent component : item.getComponents()) {
				component.afterRemovedFromInventory(this);
			}
			for (EntityComponent component : getOwner().getComponents()) {
				component.itemWasLost(item);
			}
		}
	}
	
	public void add(Entity item) throws Exception {
		if (item == null) {
			throw new Exception("Please specify an item to add to the inventory.");
		}
		else if (contains(item)) {
			throw new Exception("That item is already in the inventory.");
		} else {
			if (item.getChunk() != null) {
				item.getChunk().removeEntity(item);
			}
			if (item.getContainer() != null) {
				item.getContainer().remove(item);
			}
			for (EntityComponent component : item.getComponents()) {
				component.beforeAddedToInventory(this);
			}
			_items.add(item);
			item.setContainer(this);
			for (EntityComponent component : item.getComponents()) {
				component.afterAddedToInventory(this);
			}
			for (EntityComponent component : getOwner().getComponents()) {
				component.itemWasGained(item);
			}
		}
	}
	@Override
	public Iterator<Entity> iterator() {
		return _items.iterator();
	}
}
