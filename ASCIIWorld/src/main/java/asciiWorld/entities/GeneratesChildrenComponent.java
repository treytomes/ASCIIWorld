package asciiWorld.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.Direction;
import asciiWorld.chunks.Chunk;
import asciiWorld.math.RandomFactory;

public class GeneratesChildrenComponent extends EntityComponent {

	private double _spawnRate;
	private String _childType;
	
	public GeneratesChildrenComponent(Entity owner) {
		super(owner);
		_spawnRate = 0;
		_childType = "";
	}

	public Double getSpawnRate() {
		return _spawnRate;
	}
	
	public void setSpawnRate(Double value) {
		if (value < 0) {
			_spawnRate = 0;
		} else if (value > 1) {
			_spawnRate = 1;
		} else {
			_spawnRate = value;
		}
	}
	
	public String getChildType() {
		return _childType;
	}
	
	public void setChildType(String value) {
		_childType = value;
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int deltaTime) {
		if (RandomFactory.get().nextDouble() <= _spawnRate) {
			Vector2f startingPoint = getOwner().getOccupiedChunkPoint();
			
			for (Direction d : Direction.all()) {
				Vector2f spawnPoint = d.toVector2f().add(startingPoint);
				if (!getOwner().getChunk().isSpaceOccupied(spawnPoint, Chunk.LAYER_OBJECT)) {
					Entity spawnedEntity = EntityFactory.get().getResource(_childType);
					spawnedEntity.moveTo(spawnPoint, Chunk.LAYER_OBJECT);
					getOwner().getChunk().addEntity(spawnedEntity);
					break;
				}
			}
		}
	}
}
