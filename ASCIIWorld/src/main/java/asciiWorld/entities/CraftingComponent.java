package asciiWorld.entities;

import org.newdawn.slick.geom.Rectangle;

import asciiWorld.CreateRectangle;
import asciiWorld.ui.Button;
import asciiWorld.ui.CraftingView;
import asciiWorld.ui.MethodBinding;
import asciiWorld.ui.RootVisualPanel;
import asciiWorld.ui.WindowPanel;

public class CraftingComponent extends EntityComponent {
	
	private WindowPanel _entityUI;

	public CraftingComponent(Entity owner) {
		super(owner);
		// TODO Auto-generated constructor stub
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
	
	private void createWindow(Entity player) throws Exception {
		RootVisualPanel root = RootVisualPanel.get();
		
		Rectangle bounds = CreateRectangle
				.from(root.getBounds())
				.scale(4f / 5f)
				.centerOn(root.getBounds())
				.getRectangle();
		
		CraftingView craftingView = new CraftingView(player.getInventory(), 1);
		
		_entityUI = new WindowPanel(bounds, "Crafting Table");
		_entityUI.setWindowContent(craftingView);
		_entityUI.addButton(Button.createActionButton("Pick Up", new MethodBinding(this, "pickUpCraftingTable", player)));
		_entityUI.addButton(Button.createActionButton("Deconstruct", new MethodBinding(this, "deconstructItem", player, craftingView)));
		
		root.addModalChild(_entityUI);
	}

	public void pickUpCraftingTable(Entity player) {
		try {
			player.getInventory().add(getOwner());
			_entityUI.closeWindow();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deconstructItem(Entity player, CraftingView craftingView) {
		Entity selectedItem = craftingView.getSelectedInventoryItem();
		if (selectedItem == null) {
			return;
		}
		try {
			InventoryContainer itemInventory = selectedItem.getInventory();
			InventoryContainer playerInventory = player.getInventory();
			while (itemInventory.getItemCount() > 0) {
				playerInventory.add(itemInventory.getItemAt(0));
			}
			getOwner().takeDamage(player, selectedItem.getHealth());
			playerInventory.remove(selectedItem);
			craftingView.resetBinding();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
