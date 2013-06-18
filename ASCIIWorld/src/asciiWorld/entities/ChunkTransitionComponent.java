package asciiWorld.entities;

import org.newdawn.slick.geom.Vector2f;

import asciiWorld.chunks.Chunk;
import asciiWorld.chunks.ChunkFactory;
import asciiWorld.math.RandomFactory;
import asciiWorld.math.Vector3f;

public class ChunkTransitionComponent extends EntityComponent {

	private int _seed;
	private Chunk _chunk;
	
	public ChunkTransitionComponent(Entity owner) {
		super(owner);
	}
	
	@Override
	public void afterAddedToChunk(Chunk chunk) {
		Vector2f chunkPoint = getOwner().getOccupiedChunkPoint();
		_seed = ((int)chunkPoint.y << 6 + (int)chunkPoint.x);
	}

	@Override
	public void touched(Entity touchedByEntity) {
		// Generate the new chunk.
		Chunk oldChunk = touchedByEntity.getChunk();
		Chunk newChunk = generateChunk();
		
		// Is there a way back to the old chunk?
		Entity transitionBackEntity = null;
		for (Entity entity : newChunk.getEntities()) {
			for (EntityComponent component : entity.getComponents()) {
				if (component instanceof ChunkTransitionComponent) {
					ChunkTransitionComponent transitionComponent = ChunkTransitionComponent.class.cast(component);
					if (transitionComponent._chunk == oldChunk) {
						transitionBackEntity = entity;
					}
					break;
				}
			}
			if (transitionBackEntity != null) {
				break;
			}
		}
		
		// No, so place a return entity.
		if (transitionBackEntity == null) {
			transitionBackEntity = generateCaveEntrance(oldChunk, newChunk);
		}
		
		// Place the touchedByEntity in the new chunk.
		try {
			touchedByEntity.moveTo(newChunk.findSpawnPoint(transitionBackEntity.getOccupiedChunkPoint(), Chunk.LAYER_OBJECT));
			newChunk.addEntity(touchedByEntity);
		} catch (Exception e) {
			System.err.println("Unable to spawn in the new chunk.");
			e.printStackTrace();
		}
	}
	
	private Chunk generateChunk() {
		if (_chunk == null) {
			try {
				_chunk = ChunkFactory.generateDungeon(System.out, _seed);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return _chunk;
	}
	
	private Entity generateCaveEntrance(Chunk oldChunk, Chunk newChunk) {
		// Create the entrance.
		Entity transitionBackEntity = EntityFactory.get().getResource("caveEntrance");
		for (EntityComponent component : transitionBackEntity.getComponents()) {
			if (component instanceof ChunkTransitionComponent) {
				ChunkTransitionComponent transitionComponent = ChunkTransitionComponent.class.cast(component);
				transitionComponent._chunk = oldChunk;
			}
		}

		// Find a place to put the cave.
		Vector2f chunkPoint = findPossibleCaveEntrance(newChunk);

		// Clear out some space for the entrance.
		Entity entity = newChunk.getEntityAt(chunkPoint, Chunk.LAYER_OBJECT);
		if (entity != null) {
			newChunk.removeEntity(entity);
		}

		// Place the entrance.
		transitionBackEntity.moveTo(chunkPoint, Chunk.LAYER_OBJECT);
		newChunk.addEntity(transitionBackEntity);

		return transitionBackEntity;
	}

	private Vector2f findPossibleCaveEntrance(Chunk chunk) {
		while (true) {
			int x = RandomFactory.get().nextInt(0, Chunk.COLUMNS);
			int y = RandomFactory.get().nextInt(0, Chunk.ROWS);

            Entity entity = chunk.getEntityAt(new Vector2f(x, y), Chunk.LAYER_OBJECT);

			if (entity != null) {
                if (!chunk.isSpaceOccupied(new Vector3f(x - 1, y, Chunk.LAYER_OBJECT)) ||
            		!chunk.isSpaceOccupied(new Vector3f(x + 1, y, Chunk.LAYER_OBJECT)) ||
            		!chunk.isSpaceOccupied(new Vector3f(x, y - 1, Chunk.LAYER_OBJECT)) ||
            		!chunk.isSpaceOccupied(new Vector3f(x, y + 1, Chunk.LAYER_OBJECT))) {
					return new Vector2f(x, y);
				}
			}
		}
	}
}
