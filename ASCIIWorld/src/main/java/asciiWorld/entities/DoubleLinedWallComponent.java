package asciiWorld.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.chunks.Chunk;
import asciiWorld.tiles.Frame;
import asciiWorld.tiles.IRenderable;

public class DoubleLinedWallComponent extends EntityComponent {

	private static final int ALL = 206;
	private static final int NONE = 249;
	private static final int TOP_BOTTOM_LEFT = 185;
	private static final int TOP_BOTTOM_RIGHT = 204;
	private static final int TOP_LEFT_RIGHT = 202;
	private static final int BOTTOM_LEFT_RIGHT = 203;
	private static final int TOP_BOTTOM = 186;
	private static final int LEFT_RIGHT = 205;
	private static final int TOP_LEFT = 188;
	private static final int TOP_RIGHT = 200;
	private static final int BOTTOM_LEFT = 187;
	private static final int BOTTOM_RIGHT = 201;
	private static final int TOP = 208;
	private static final int BOTTOM = 210;
	private static final int LEFT = 181;
	private static final int RIGHT = 198;
	
	public DoubleLinedWallComponent(Entity owner) {
		super(owner);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int deltaTime) {
		int row = (int)getOwner().getOccupiedChunkPoint().y;
		int column = (int)getOwner().getOccupiedChunkPoint().x;
		
		boolean top = isValidNeighbor(row - 1, column);
		boolean bottom = isValidNeighbor(row + 1, column);
		boolean left = isValidNeighbor(row, column - 1);
		boolean right = isValidNeighbor(row, column + 1);
		
		int tileIndex = NONE;
		if (top && bottom && left && right) {
			tileIndex = ALL;
		} else if (top && bottom && left) {
			tileIndex = TOP_BOTTOM_LEFT;
		} else if (top && bottom && right) {
			tileIndex = TOP_BOTTOM_RIGHT;
		} else if (top && left && right) {
			tileIndex = TOP_LEFT_RIGHT;
		} else if (bottom && left && right) {
			tileIndex = BOTTOM_LEFT_RIGHT;
		} else if (top && bottom) {
			tileIndex = TOP_BOTTOM;
		} else if (left && right) {
			tileIndex = LEFT_RIGHT;
		} else if (top && left) {
			tileIndex = TOP_LEFT;
		} else if (top && right) {
			tileIndex = TOP_RIGHT;
		} else if (bottom && left) {
			tileIndex = BOTTOM_LEFT;
		} else if (bottom && right) {
			tileIndex = BOTTOM_RIGHT;
		} else if (top) {
			tileIndex = TOP;
		} else if (bottom) {
			tileIndex = BOTTOM;
		} else if (left) {
			tileIndex = LEFT;
		} else if (right) {
			tileIndex = RIGHT;
		}
		
		IRenderable renderable = getOwner().getTile().getCurrentFrame();
		if (renderable instanceof Frame) {
			Frame.class.cast(renderable).setTileIndex(tileIndex);
		}
	}

	private boolean isValidNeighbor(int row, int column) {
		Chunk chunk = getOwner().getChunk();
		Entity entity = chunk.getEntityAt(new Vector2f(column, row), Chunk.LAYER_OBJECT);
		return (entity != null) && entity.getName().equals(getOwner().getName());
	}
}
