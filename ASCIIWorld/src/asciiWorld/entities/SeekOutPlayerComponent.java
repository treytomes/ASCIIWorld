package asciiWorld.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.Direction;
import asciiWorld.chunks.Chunk;

public class SeekOutPlayerComponent extends EntityComponent {
	
	private static final String DEFAULT_TARGET_ENTITY_NAME = "Player";

	private String _targetEntityName;
	
	public SeekOutPlayerComponent(Entity owner) {
		super(owner);
		setTargetEntityName(DEFAULT_TARGET_ENTITY_NAME);
	}
	
	public String getTargetEntityName() {
		return _targetEntityName;
	}
	
	public void setTargetEntityName(String value) {
		_targetEntityName = value;
	}
	
	private Entity findNearestVisibleTarget(String targetEntityName) {
		for (Entity entity : getOwner().getChunk().getEntities()) {
			if (entity.getName().equals(targetEntityName)) {
				if (getOwner().getOccupiedChunkPoint().distance(entity.getOccupiedChunkPoint()) <= getOwner().getRangeOfVision()) {
					// Only return this entity if it can be seen.
					return entity;
				}
			}
		}
		return null;
	}
	
	private Entity findNearestVisibleTarget() {
		Entity targetEntity = findNearestVisibleTarget(getTargetEntityName());
		if (targetEntity == null) {
			// Can't find the target?  Go find some friends instead.
			targetEntity = findNearestVisibleTarget(getOwner().getName());
		}
		return targetEntity;
	}
	
	private Direction getNextDirection() {
		Vector2f sourcePosition = getOwner().getOccupiedChunkPoint();
		Vector2f targetPosition = findNearestVisibleTarget().getOccupiedChunkPoint();
		
		Direction nextDirection;
		if (targetPosition.x < sourcePosition.x) {
			nextDirection = Direction.West;
		} else if (targetPosition.x > sourcePosition.x) {
			nextDirection = Direction.East;
		} else {
			if (targetPosition.y < sourcePosition.y) {
				nextDirection = Direction.North;
			} else if (targetPosition.y > sourcePosition.y) {
				nextDirection = Direction.South;
			} else {
				nextDirection = null;
			}
		}
		
		if (nextDirection != null) {
			Vector2f nextPosition = nextDirection.toVector2f().add(sourcePosition);
			if (getOwner().getChunk().isSpaceOccupied(nextPosition, Chunk.LAYER_OBJECT)) {
				return Direction.random();
			}
		}
		
		return nextDirection;
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int deltaTime) {
		getOwner().move(getNextDirection());
	}
}
