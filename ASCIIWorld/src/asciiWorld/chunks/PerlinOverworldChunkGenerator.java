package asciiWorld.chunks;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.geom.Vector2f;

import asciiWorld.RandomFactory;
import asciiWorld.entities.Entity;
import asciiWorld.entities.EntityFactory;

public class PerlinOverworldChunkGenerator implements IChunkGenerator {
	
	private static final int SEEDTREE_MAX = 80;
	private static final int SEEDTREE_MIN = 20;
	private static final int TREES_MIN = 80;
	private static final int TREES_MAX = 640;
	private static final int CAVES_COUNT = 40;
	
	private static final double PERSISTENCE = 0.25;
	private static final double FREQUENCY = 0.0625; // lower frequency to stretch out the terrain
	private static final double AMPLITUDE = 10;
	private static final int OCTAVES = 10;
	
	private static final int ATTEMPTS_FINDWATER = 1024;
	private static final int ATTEMPTS_FINDCAVEENTRANCE = 1024;
	
	public Chunk generate(Chunk chunk, long seed) throws Exception {
		RandomFactory.get().reseed(seed);
		
		chunk = generatePerlinNoise(chunk);
		chunk = generateRiver(chunk, findWater(chunk), findWater(chunk));
		chunk = generateTrees(chunk);
		chunk = generateCaveEntrances(chunk);
		
		return chunk;
	}

    private Chunk generatePerlinNoise(Chunk chunk) throws Exception
	{
    	System.out.print("Generating perlin noise...");
    	
		// This will generate weird grainy landscapes with random seeds > 25000.  Don't know why.
		ITerrainGenerator generator = new PerlinTerrainGenerator(PERSISTENCE, FREQUENCY, AMPLITUDE, OCTAVES, RandomFactory.get().nextInt(0, 25000));
		double[][] terrain = generator.generate(Chunk.WIDTH, Chunk.HEIGHT);
		for (int y = 0; y < Chunk.HEIGHT; y++) {
			for (int x = 0; x < Chunk.WIDTH; x++) {
				int value = (int)terrain[y][x];
				if (value > 1) {
					EntityFactory.createDirtEntity(chunk, x, y);
					EntityFactory.createStoneEntity(chunk, x, y);
				}
				else if (value > -3) {
					EntityFactory.createGrassEntity(chunk, x, y);
				}
				else if (value > -4) {
					EntityFactory.createSandEntity(chunk, x, y);
				}
				else {
					EntityFactory.createWaterEntity(chunk, x, y);
				}
			}
		}
		
		System.out.println(" done!");
		return chunk;
	}
    
    private Vector2f findWater(Chunk chunk) throws Exception {
    	System.out.print("Locating water...");
    	
    	Vector2f point = new Vector2f(
    			RandomFactory.get().nextInt(0, Chunk.WIDTH),
    			RandomFactory.get().nextInt(0, Chunk.HEIGHT));
    	int attempt = 0;
    	while (chunk.getEntityAt(point, Chunk.LAYER_GROUND).getName() != "Water") {
        	point.x = RandomFactory.get().nextInt(0, Chunk.WIDTH);
        	point.y = RandomFactory.get().nextInt(0, Chunk.HEIGHT);
        	
        	System.out.print(".");
        	
        	attempt++;
        	if (attempt >= ATTEMPTS_FINDWATER) {
        		System.out.println(" failed to find water.");
        		return null;
        	}
    	}
    	
    	System.out.println(" done!");
    	return point;
    }

	private Chunk generateRiver(Chunk chunk, Vector2f startingChunkPoint, Vector2f endingChunkPoint) throws Exception
	{
		System.out.print("Generating a river...");
		
		if ((startingChunkPoint == null) || (endingChunkPoint == null)) {
			System.out.println(" failed to generate a river.");
			return chunk;
		}
		
		Vector2f chunkPoint = startingChunkPoint.copy();
		while (!((chunkPoint.x == endingChunkPoint.x) && (chunkPoint.y == endingChunkPoint.y))) {
			switch (RandomFactory.get().nextInt(0, 2)) {
				case 0:
					if (chunkPoint.x < endingChunkPoint.x) {
						chunkPoint.x++;
					} else if (chunkPoint.x > endingChunkPoint.x) {
						chunkPoint.x--;
					}
					break;
				case 1:
					if (chunkPoint.y < endingChunkPoint.y) {
						chunkPoint.y++;
					} else if (chunkPoint.y > endingChunkPoint.y) {
						chunkPoint.y--;
					}
					break;
			}

			// Rivers cut through mountains; lakes do not.
			Entity groundEntity = chunk.getEntityAt(chunkPoint, Chunk.LAYER_GROUND);
			if (groundEntity != null) {
				chunk.removeEntity(groundEntity);
			}
			Entity objectEntity = chunk.getEntityAt(chunkPoint, Chunk.LAYER_OBJECT);
			if (objectEntity != null) {
				chunk.removeEntity(objectEntity);
			}
			EntityFactory.createWaterEntity(chunk, (int)chunkPoint.x, (int)chunkPoint.y);
			
			System.out.print(".");
		}
		
		System.out.println(" done!");
		return chunk;
	}

    private Chunk generateTrees(Chunk chunk) throws Exception {
    	System.out.print("Generating trees...");
    	
		// Generate seed trees.
		int seedTreeCount = RandomFactory.get().nextInt(SEEDTREE_MIN, SEEDTREE_MAX);
		List<Vector2f> seedTrees = new ArrayList<Vector2f>();
		for (int n = 0; n < seedTreeCount; n++) {
			Vector2f treePoint = chunk.findRandomSpawnPoint();
			while (chunk.getEntityAt(treePoint, Chunk.LAYER_GROUND).getName() != "Grass") {
				// Trees can only grow on grass.
				treePoint = chunk.findRandomSpawnPoint();
			}

			seedTrees.add(treePoint);

			EntityFactory.createTreeEntity(chunk, (int)treePoint.x, (int)treePoint.y);
			
			System.out.print(".");
		}

		// Generate forests.
		int totalTrees = RandomFactory.get().nextInt(TREES_MIN, TREES_MAX) - seedTreeCount;
		for (int n = 0; n < totalTrees; n++) {
			// Pick a seed position.
			Vector2f seedPoint = seedTrees.get(RandomFactory.get().nextInt(0, seedTreeCount));
			generateTree(chunk, seedPoint);
			
			System.out.print(".");
		}
		
		System.out.println(" done!");
		return chunk;
    }

	private Entity generateTree(Chunk chunk, Vector2f seedPoint) throws Exception
	{
		// Pick a random angle.
		float angle = (float)(RandomFactory.get().nextInt(0, 360) * Math.PI / 180.0f);

		// Keep searching outward until a suitable spawn point is found.
		int distance = 1;
		while (true) {
			Vector2f chunkPoint = new Vector2f(seedPoint.x + distance * (float)Math.cos(angle), seedPoint.y + distance * (float)Math.sin(angle));
			Entity groundEntity = chunk.getEntityAt(chunkPoint, Chunk.LAYER_GROUND); 
			if ((groundEntity == null) || (groundEntity.getName() != "Grass")) {
				// We're moving out into the desert; start over.
				return generateTree(chunk, seedPoint);
			}
			if (!chunk.isSpaceOccupied(chunkPoint)) {
				return EntityFactory.createTreeEntity(chunk, (int)chunkPoint.x, (int)chunkPoint.y);
			} else {
				distance++;
			}
		}
	}
	
	private Chunk generateCaveEntrances(Chunk chunk) throws Exception {
		System.out.print("Generating cave entrances...");
		
		for (int n = 0; n < CAVES_COUNT; n++) {
			if (generateCaveEntrance(chunk) == null) {
				System.out.println(" failed to find a place to put a cave.");
				return chunk;
			}
			System.out.print(".");
		}
		
		System.out.println(" done!");
		return chunk;
	}
	
	private Entity generateCaveEntrance(Chunk chunk) throws Exception {
		// Find a place to put the cave.
		Vector2f chunkPoint = findPossibleCaveEntrance(chunk);
		if (chunkPoint == null) {
			// Unable to find a place to put the cave.
			return null;
		}

		// Clear out some space for the entrance.
		Entity objectEntity = chunk.getEntityAt(chunkPoint, Chunk.LAYER_OBJECT);
		if (objectEntity != null) {
			chunk.removeEntity(objectEntity);
		}

		// Create the entrance.
		return EntityFactory.createCaveEntranceEntity(chunk, (int)chunkPoint.x, (int)chunkPoint.y);
	}

	private Vector2f findPossibleCaveEntrance(Chunk chunk) throws Exception
	{
		int attempt = 0;
		while (true) {
			attempt++;
			if (attempt >= ATTEMPTS_FINDCAVEENTRANCE) {
				return null;
			}

			int x = RandomFactory.get().nextInt(0, Chunk.WIDTH);
			int y = RandomFactory.get().nextInt(0, Chunk.HEIGHT);
			
			Entity entity = chunk.getEntityAt(new Vector2f(x, y), Chunk.LAYER_OBJECT);
			if (entity == null) {
				continue; // we need to find a mountain to dig into
			}
			if (entity.getName() == "Stone") {
				chunk.removeEntity(entity);
				return new Vector2f(x, y);
			}
		}
	}
}
