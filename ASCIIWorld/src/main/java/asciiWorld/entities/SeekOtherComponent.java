package asciiWorld.entities;

import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.Direction;
import asciiWorld.ai.AStarOriginal;
import asciiWorld.chunks.Chunk;
import asciiWorld.math.Point;

public class SeekOtherComponent extends EntityComponent {
	
	private static final String DEFAULT_TARGET_ENTITY_NAME = "Player";

	private String _targetEntityName;
	private Vector2f _lastTargetPosition;
	
	private List<Point> _currentPath;
	private int _pathIndex;
	
	public SeekOtherComponent(Entity owner) {
		super(owner);
		setTargetEntityName(DEFAULT_TARGET_ENTITY_NAME);
		_lastTargetPosition = null;
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
		Entity targetEntity = findNearestVisibleTarget();
		if (targetEntity == null) {
			return Direction.random();
		} else {
			Vector2f sourcePosition = getOwner().getOccupiedChunkPoint();
			Vector2f targetPosition = targetEntity.getOccupiedChunkPoint();
			
			if (!targetPosition.equals(_lastTargetPosition)) {
				// Recalculate path.
				_currentPath = AStarOriginal.findPath(getOwner().getChunk(), new Point((int)sourcePosition.x, (int)sourcePosition.y), new Point((int)targetPosition.x, (int)targetPosition.y));
				_pathIndex = 0;
				_lastTargetPosition = targetPosition.copy();
			}
			
			if ((_currentPath != null) && (_currentPath.size() > 0) && (_pathIndex < _currentPath.size())) {
				Point nextPoint = _currentPath.get(_pathIndex);
				Direction nextDirection;
				if (nextPoint.x < sourcePosition.x) {
					nextDirection = Direction.West;
				} else if (nextPoint.x > sourcePosition.x) {
					nextDirection = Direction.East;
				} else {
					if (nextPoint.y < sourcePosition.y) {
						nextDirection = Direction.North;
					} else if (nextPoint.y > sourcePosition.y) {
						nextDirection = Direction.South;
					} else {
						nextDirection = null;
					}
				}
				
				_pathIndex++;
				return nextDirection;
			} else {
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
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int deltaTime) {
		if (!getOwner().isMoving()) {
			getOwner().move(getNextDirection());
		}
	}
}
