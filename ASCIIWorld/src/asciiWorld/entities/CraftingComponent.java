package asciiWorld.entities;

import org.newdawn.slick.geom.Rectangle;

import asciiWorld.CreateRectangle;
import asciiWorld.ui.CraftingView;
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
		
		_entityUI = new WindowPanel(bounds, "Crafting Table");
		_entityUI.setWindowContent(new CraftingView(player.getInventory()));
		
		root.addModalChild(_entityUI);
	}

}
