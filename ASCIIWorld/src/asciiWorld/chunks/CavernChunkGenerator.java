package asciiWorld.chunks;

import java.io.PrintStream;

import asciiWorld.entities.EntityFactory;
import asciiWorld.math.RandomFactory;

public class CavernChunkGenerator implements IChunkGenerator {
	
	private static final double PERSISTENCE = 0.5;
	private static final double FREQUENCY = 0.25; // lower frequency to stretch out the terrain
	private static final double AMPLITUDE = 10;
	private static final int OCTAVES = 5;
	
	private PrintStream _logStream;
	
	public Chunk generate(Chunk chunk, long seed, PrintStream logStream)
			throws Exception {
		
		_logStream = logStream;
		
		RandomFactory.get().reseed(seed);
		
		chunk = generatePerlinNoise(chunk);
		
		chunk.getComponents().add(new DayNightCycleComponent(chunk));
		chunk.getComponents().add(new SpawnEntitiesComponent(chunk) {{
			setEntityType("slime");
			setSpawnFrequency(10000);
			setSpawnChance(0.75);
			setDistanceFromPlayer(10);
			setUpperLimit(15);
		}});
		
		return chunk;
	}

    private Chunk generatePerlinNoise(Chunk chunk)
    		throws Exception {
    	_logStream.print("Generating perlin noise...");
    	
		// This will generate weird grainy landscapes with random seeds > 25000.  Don't know why.
		ITerrainGenerator generator = new PerlinTerrainGenerator(PERSISTENCE, FREQUENCY, AMPLITUDE, OCTAVES, RandomFactory.get().nextInt(0, 25000));
		double[][] terrain = generator.generate(Chunk.COLUMNS, Chunk.ROWS);
		for (int y = 0; y < Chunk.ROWS; y++) {
			for (int x = 0; x < Chunk.COLUMNS; x++) {
				int value = (int)terrain[y][x];
				if (value > 1) {
					EntityFactory.get().createEntity("clay", chunk, x, y, Chunk.LAYER_GROUND);
				} else if (value > -3) {
					EntityFactory.get().createEntity("clay", chunk, x, y, Chunk.LAYER_GROUND);
					EntityFactory.get().createEntity("stone", chunk, x, y, Chunk.LAYER_OBJECT);
				} else if (value > -4) {
					EntityFactory.get().createEntity("clay", chunk, x, y, Chunk.LAYER_GROUND);
					EntityFactory.get().createEntity("coalOre", chunk, x, y, Chunk.LAYER_OBJECT);
				} else {
					EntityFactory.get().createEntity("clay", chunk, x, y, Chunk.LAYER_GROUND);
					EntityFactory.get().createEntity("ironOre", chunk, x, y, Chunk.LAYER_OBJECT);
				}
			}
		}
		
		_logStream.println(" done!");
		return chunk;
	}
}
