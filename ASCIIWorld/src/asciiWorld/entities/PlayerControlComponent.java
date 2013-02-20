package asciiWorld.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.Direction;
import asciiWorld.ui.RootVisualPanel;

public class PlayerControlComponent extends EntityComponent {
	
	private static final int KEY_MOVE_NORTH = Input.KEY_W;
	private static final int KEY_MOVE_SOUTH = Input.KEY_S;
	private static final int KEY_MOVE_WEST = Input.KEY_A;
	private static final int KEY_MOVE_EAST = Input.KEY_D;
	private static final int KEY_TOUCH = Input.KEY_SPACE;
	
	private RootVisualPanel _ui;

	public PlayerControlComponent(Entity owner, RootVisualPanel ui) {
		super(owner);
		_ui = ui;
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int deltaTime) {
		Input input = container.getInput();
		
		if (input.isKeyDown(KEY_MOVE_NORTH)) {
			getOwner().move(Direction.North);
		} else if (input.isKeyDown(KEY_MOVE_SOUTH)) {
			getOwner().move(Direction.South);
		} else if (input.isKeyDown(KEY_MOVE_WEST)) {
			getOwner().move(Direction.West);
		} else if (input.isKeyDown(KEY_MOVE_EAST)) {
			getOwner().move(Direction.East);
		}
		
		if (input.isKeyPressed(KEY_TOUCH)) {
			getOwner().touch();
		}
		if (input.isKeyPressed(Input.KEY_ESCAPE)) {
			StringBuilder sb = new StringBuilder();
			InventoryContainer inventory = getOwner().getInventory();
			for (int index = 0; index < inventory.getItemCount(); index++) {
				sb.append(inventory.getItemAt(index).getName()).append("\n");
			}
			_ui.showMessageBox(true, sb.toString(), "Inventory");
		}
	}
}
