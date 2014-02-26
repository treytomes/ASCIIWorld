package asciiWorld.chunks;

import org.newdawn.slick.geom.Vector2f;

import asciiWorld.entities.Entity;
import asciiWorld.entities.EntityFactory;
import asciiWorld.math.RandomFactory;
import asciiWorld.math.Vector3f;

public class SpawnEntitiesComponent extends ChunkComponent {

	private static final String ENTITYTYPE_PLAYER = "player";
	
	private String _entityType;
	private int _spawnFrequency;
	private double _spawnChance;
	private int _distanceFromPlayer;
	private int _totalTime;
	private int _lastSpawnTime;
	private int _upperLimit;
	
	public SpawnEntitiesComponent(Chunk owner) {
		super(owner);
		_entityType = "ruffian";
		_spawnFrequency = 1000;
		_spawnChance = 0.5;
		_distanceFromPlayer = 15;
		_totalTime = 0;
		_lastSpawnTime = 0;
		_upperLimit = -1;
	}

	public String getEntityType() {
		return _entityType;
	}
	
	public void setEntityType(String value) {
		_entityType = value;
	}
	
	/**
	 * 
	 * @return The number of milliseconds between spawn attempts.
	 */
	public Integer getSpawnFrequency() {
		return _spawnFrequency;
	}
	
	public void setSpawnFrequency(Integer value) {
		_spawnFrequency = value;
	}
	
	/**
	 * 
	 * @return Odds of an entity being spawned during this period.  Valued from 0 to 1.
	 */
	public Double getSpawnChance() {
		return _spawnChance;
	}
	
	public void setSpawnChance(Double value) {
		_spawnChance = value;
	}
	
	/**
	 * 
	 * @return How far must a point be from the player in order to spawn an entity?
	 */
	public Integer getDistanceFromPlayer() {
		return _distanceFromPlayer;
	}
	
	public void setDistanceFromPlayer(Integer value) {
		_distanceFromPlayer = value;
	}
	
	/**
	 * 
	 * @return The total number of this type of entity allowed in the chunk.
	 */
	public Integer getUpperLimit() {
		return _upperLimit;
	}
	
	public void setUpperLimit(Integer value) {
		_upperLimit = value;
	}
	
	@Override
	public void update(int deltaTime) {
		_totalTime += deltaTime;
		if ((_totalTime - _lastSpawnTime) >= _spawnFrequency) {
			_lastSpawnTime = _totalTime;

			int entityCount = getCountOfEntityType(_entityType);
			if ((_upperLimit < 0) || (entityCount < _upperLimit)) {
				double odds = RandomFactory.get().nextDouble();
				if (odds <= _spawnChance) {
					Entity player = findPlayer();
					Vector2f playerPoint = player.getOccupiedChunkPoint();

					while (true) {
						Vector3f chunkPoint = getOwner().findRandomSpawnPoint(Chunk.LAYER_OBJECT);
						if (Math.abs(chunkPoint.toVector2f().distance(playerPoint)) >= _distanceFromPlayer) {
							Entity entity = EntityFactory.get().getResource(_entityType);
							entity.moveTo(chunkPoint);
							getOwner().addEntity(entity);
							break;
						}
					}
				}
			}
		}
	}
	
	private int getCountOfEntityType(String entityType) {
		int count = 0;
		for (Entity entity : getOwner().getEntities()) {
			if (entity.getType().equals(entityType)) {
				count++;
			}
		}
		return count;
	}
	
	private Entity findPlayer() {
		for (Entity entity : getOwner().getEntities()) {
			if (entity.getType().equals(ENTITYTYPE_PLAYER)) {
				return entity;
			}
		}
		return null;
	}
}
