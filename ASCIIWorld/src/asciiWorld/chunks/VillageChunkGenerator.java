package asciiWorld.chunks;

import java.io.PrintStream;

import org.newdawn.slick.geom.Vector2f;

import asciiWorld.Direction;
import asciiWorld.entities.Entity;
import asciiWorld.entities.EntityFactory;
import asciiWorld.math.RandomFactory;

public class VillageChunkGenerator implements IChunkGenerator {
	
	private static final int COUNT_HOUSE_MIN = 3;
	private static final int COUNT_HOUSE_MAX = 10;
	
	private static final int HOUSE_WIDTH = 7;
	private static final int HOUSE_HEIGHT = 7;

	private static final String ENTITY_WALL = "woodWall";
	private static final String ENTITY_FLOOR = "dirt";
	private static final String ENTITY_DOOR = "woodenDoor";

	@Override
	public Chunk generate(Chunk chunk, long seed, PrintStream logStream) throws Exception {
		RandomFactory.get().reseed(seed);
		
		chunk.getComponents().add(new DayNightCycleComponent(chunk));
		
		chunk = generateGrassyPlain(chunk);
		
		int numHouses = RandomFactory.get().nextInt(COUNT_HOUSE_MIN, COUNT_HOUSE_MAX + 1);
		for (int index = 0; index < numHouses; index++) {
			chunk = generateHouse(chunk);
		}
		
		Entity entity = EntityFactory.get().getResource("woodenDoor");
		entity.moveTo(new Vector2f(10, 10), Chunk.LAYER_OBJECT);
		chunk.addEntity(entity);
		
		return chunk;
	}
	
	private Chunk generateGrassyPlain(Chunk chunk) throws Exception {
		for (int x = 0; x < Chunk.COLUMNS; x++) {
			for (int y = 0; y < Chunk.ROWS; y++) {
				Entity entity = EntityFactory.get().getResource("grass");
				entity.moveTo(new Vector2f(x, y), Chunk.LAYER_GROUND);
				chunk.addEntity(entity);
			}
		}
		
		return chunk;
	}
	
	private Chunk generateHouse(Chunk chunk) {
		int top = RandomFactory.get().nextInt(0, Chunk.ROWS - HOUSE_HEIGHT);
		int bottom = top + HOUSE_HEIGHT - 1;
		int left = RandomFactory.get().nextInt(0, Chunk.COLUMNS - HOUSE_WIDTH);
		int right = left + HOUSE_WIDTH - 1;
		
		chunk = generateWalls(chunk, top, bottom, left, right);
		chunk = generateFloor(chunk, top, bottom, left, right);
		
		Direction doorSide = Direction.random();
		switch (doorSide) {
		case North:
			chunk = generateDoorTile(chunk, top, (left + right) / 2); 
			break;
		case South:
			chunk = generateDoorTile(chunk, bottom, (left + right) / 2); 
			break;
		case East:
			chunk = generateDoorTile(chunk, (top + bottom) / 2, right); 
			break;
		case West:
			chunk = generateDoorTile(chunk, (top + bottom) / 2, left); 
			break;
		}
		
		return chunk;
	}
	
	private Chunk generateWalls(Chunk chunk, int top, int bottom, int left, int right) {
		for (int row = top; row <= bottom; row++) {
			chunk = generateWallTile(chunk, row, left);
			chunk = generateWallTile(chunk, row, right);
		}
		
		for (int column = left; column <= right; column++) {
			chunk = generateWallTile(chunk, top, column);
			chunk = generateWallTile(chunk, bottom, column);
		}
		
		return chunk;
	}
	
	private Chunk generateWallTile(Chunk chunk, int row, int column) {
		Vector2f chunkPoint = new Vector2f(column, row);
		chunk.removeEntity(chunk.getEntityAt(chunkPoint, Chunk.LAYER_OBJECT));
		
		Entity wall = EntityFactory.get().getResource(ENTITY_WALL);
		wall.moveTo(chunkPoint, Chunk.LAYER_OBJECT);
		chunk.addEntity(wall);
		return chunk;
	}
	
	private Chunk generateFloor(Chunk chunk, int top, int bottom, int left, int right) {
		for (int row = top; row <= bottom; row++) {
			for (int column = left; column <= right; column++) {
				chunk = generateFloorTile(chunk, row, column);
			}
		}
		return chunk;
	}
		
	private Chunk generateFloorTile(Chunk chunk, int row, int column) {
		Vector2f chunkPoint = new Vector2f(column, row);
		chunk.removeEntity(chunk.getEntityAt(chunkPoint, Chunk.LAYER_GROUND));
		
		Entity floor = EntityFactory.get().getResource(ENTITY_FLOOR);
		floor.moveTo(chunkPoint, Chunk.LAYER_GROUND);
		chunk.addEntity(floor);
		
		return chunk;
	}
	
	private Chunk generateDoorTile(Chunk chunk, int row, int column) {
		Vector2f chunkPoint = new Vector2f(column, row);
		chunk.removeEntity(chunk.getEntityAt(chunkPoint, Chunk.LAYER_OBJECT));
		
		Entity door = EntityFactory.get().getResource(ENTITY_DOOR);
		door.moveTo(chunkPoint, Chunk.LAYER_OBJECT);
		chunk.addEntity(door);
		return chunk;
	}
}