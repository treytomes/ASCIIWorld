package asciiWorld.entities;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.CreateRectangle;
import asciiWorld.chunks.Chunk;
import asciiWorld.ui.Button;
import asciiWorld.ui.InventoryView;
import asciiWorld.ui.RootVisualPanel;
import asciiWorld.ui.TreasureChestView;
import asciiWorld.ui.WindowPanel;

public class TreasureChestComponent extends EntityComponent {

	private InventoryContainer _inventory;
	private WindowPanel _entityUI;
	
	public TreasureChestComponent(Entity owner) {
		super(owner);
		_inventory = new InventoryContainer(owner);
	}
	
	@Override
	public void touched(Entity touchedByEntity) {
		if (!touchedByEntity.getName().equals("Player")) {
			return;
		}
		
		try {
			createWindow(touchedByEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void afterRemovedFromChunk(Chunk chunk) {
		// Drop inventory into the chunk.
		Vector2f chunkPoint = getOwner().getOccupiedChunkPoint();
		while (_inventory.getItemCount() > 0) {
			Entity item = _inventory.getItemAt(0);
			try {
				_inventory.remove(item);
				item.moveTo(chunk.findSpawnPoint(chunkPoint, Chunk.LAYER_OBJECT));
				chunk.addEntity(item);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	public void addItemToChest(Entity item) {
		try {
			_inventory.add(item);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createWindow(Entity player) throws Exception {
		RootVisualPanel root = RootVisualPanel.get();
		
		Rectangle bounds = CreateRectangle
				.from(root.getBounds())
				.scale(4f / 5f)
				.centerOn(root.getBounds())
				.getRectangle();
		
		_entityUI = new WindowPanel(bounds, "Treasure Chest");
		_entityUI.setWindowContent(new TreasureChestView(_inventory, player.getInventory()));
		
		root.addModalChild(_entityUI);
	}
}
