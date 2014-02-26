package asciiWorld.chunks;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.geom.Rectangle;
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
	
	private List<Rectangle> _houses;
	
	public VillageChunkGenerator() {
		_houses = new ArrayList<Rectangle>();
	}

	@Override
	public Chunk generate(Chunk chunk, long seed, PrintStream logStream) throws Exception {
		RandomFactory.get().reseed(seed);
		
		_houses.clear();
		
		chunk.getComponents().add(new DayNightCycleComponent(chunk));
		
		chunk = generateGrassyPlain(logStream, chunk);
		chunk = generateHouses(logStream, chunk);
		
		return chunk;
	}
	
	private Chunk generateGrassyPlain(PrintStream logStream, Chunk chunk) throws Exception {
		logStream.print("Generating grassy plain...");
		
		for (int x = 0; x < Chunk.COLUMNS; x++) {
			for (int y = 0; y < Chunk.ROWS; y++) {
				Entity entity = EntityFactory.get().getResource("grass");
				entity.moveTo(new Vector2f(x, y), Chunk.LAYER_GROUND);
				chunk.addEntity(entity);
			}
		}
		
		logStream.println(" done!");
		return chunk;
	}
	
	private Chunk generateHouses(PrintStream logStream, Chunk chunk) {
		int numHouses = RandomFactory.get().nextInt(COUNT_HOUSE_MIN, COUNT_HOUSE_MAX + 1);
		logStream.println(String.format("Generating %d houses.", numHouses));
		
		for (int index = 0; index < numHouses; index++) {
			logStream.print(String.format("Generating house #%d...", index));
			chunk = generateHouse(chunk);
			logStream.println(" done!");
		}
		
		return chunk;
	}
	
	private Chunk generateHouse(Chunk chunk) {
		Rectangle area = getHouseArea();
		
		chunk = generateWalls(chunk, area);
		chunk = generateFloor(chunk, area);
		chunk = generateDoor(chunk, area);
		chunk = generateTorches(chunk, area);
		chunk = generateVillager(chunk, area);
		
		return chunk;
	}
	
	private Rectangle getHouseArea() {
		Rectangle area = null;
		while (true) {
			area = new Rectangle(
					RandomFactory.get().nextInt(0, Chunk.ROWS - HOUSE_HEIGHT),
					RandomFactory.get().nextInt(0, Chunk.COLUMNS - HOUSE_WIDTH),
					HOUSE_WIDTH - 1, HOUSE_HEIGHT - 1);
			
			boolean intersects = false;
			for (Rectangle house : _houses) {
				if (house.intersects(area) || house.contains(area) || area.contains(house)) {
					intersects = true;
					break;
				}
			}
			if (!intersects) {
				break;
			}
		}
		if (area != null) {
			_houses.add(area);
		}
		return area;
	}
	
	private Chunk generateWalls(Chunk chunk, Rectangle area) {
		for (float row = area.getMinY(); row <= area.getMaxY(); row++) {
			chunk = generateWallTile(chunk, row, area.getMinX());
			chunk = generateWallTile(chunk, row, area.getMaxX());
		}
		
		for (float column = area.getMinX(); column <= area.getMaxX(); column++) {
			chunk = generateWallTile(chunk, area.getMinY(), column);
			chunk = generateWallTile(chunk, area.getMaxY(), column);
		}
		
		return chunk;
	}
	
	private Chunk generateWallTile(Chunk chunk, float row, float column) {
		Vector2f chunkPoint = new Vector2f(column, row);
		chunk.removeEntity(chunk.getEntityAt(chunkPoint, Chunk.LAYER_OBJECT));
		
		Entity wall = EntityFactory.get().getResource(ENTITY_WALL);
		wall.moveTo(chunkPoint, Chunk.LAYER_OBJECT);
		chunk.addEntity(wall);
		return chunk;
	}
	
	private Chunk generateFloor(Chunk chunk, Rectangle area) {
		for (float row = area.getMinY(); row <= area.getMaxY(); row++) {
			for (float column = area.getMinX(); column <= area.getMaxX(); column++) {
				chunk = generateFloorTile(chunk, row, column);
			}
		}
		return chunk;
	}
		
	private Chunk generateFloorTile(Chunk chunk, float row, float column) {
		Vector2f chunkPoint = new Vector2f(column, row);
		chunk.removeEntity(chunk.getEntityAt(chunkPoint, Chunk.LAYER_GROUND));
		
		Entity floor = EntityFactory.get().getResource(ENTITY_FLOOR);
		floor.moveTo(chunkPoint, Chunk.LAYER_GROUND);
		chunk.addEntity(floor);
		
		return chunk;
	}
	
	private Chunk generateDoor(Chunk chunk, Rectangle area) {
		Direction doorSide = Direction.random();
		switch (doorSide) {
		case North:
			chunk = generateDoorTile(chunk, area.getMinY(), area.getCenterX()); 
			break;
		case South:
			chunk = generateDoorTile(chunk, area.getMaxY(), area.getCenterX()); 
			break;
		case East:
			chunk = generateDoorTile(chunk, area.getCenterY(), area.getMaxX()); 
			break;
		case West:
			chunk = generateDoorTile(chunk, area.getCenterY(), area.getMinX()); 
			break;
		}
		return chunk;
	}
	
	private Chunk generateDoorTile(Chunk chunk, float row, float column) {
		Vector2f chunkPoint = new Vector2f(column, row);
		chunk.removeEntity(chunk.getEntityAt(chunkPoint, Chunk.LAYER_OBJECT));
		
		Entity door = EntityFactory.get().getResource(ENTITY_DOOR);
		door.moveTo(chunkPoint, Chunk.LAYER_OBJECT);
		chunk.addEntity(door);
		return chunk;
	}
	
	private Chunk generateTorches(Chunk chunk, Rectangle area) {
		chunk = generateTorchTile(chunk, area.getMinY() + 1, area.getMinX() + 1);
		chunk = generateTorchTile(chunk, area.getMinY() + 1, area.getMaxX() - 1);
		chunk = generateTorchTile(chunk, area.getMaxY() - 1, area.getMinX() + 1);
		chunk = generateTorchTile(chunk, area.getMaxY() - 1, area.getMaxX() - 1);
		return chunk;
	}
	
	private Chunk generateTorchTile(Chunk chunk, float row, float column) {
		Vector2f chunkPoint = new Vector2f(column, row);
		chunk.removeEntity(chunk.getEntityAt(chunkPoint, Chunk.LAYER_OBJECT));
		
		Entity door = EntityFactory.get().getResource("torch");
		door.moveTo(chunkPoint, Chunk.LAYER_OBJECT);
		chunk.addEntity(door);
		return chunk;
	}
	
	private Chunk generateVillager(Chunk chunk, Rectangle area) {
		Vector2f chunkPoint = new Vector2f(area.getCenterX(), area.getCenterY());
		chunk.removeEntity(chunk.getEntityAt(chunkPoint, Chunk.LAYER_OBJECT));
		
		Entity villager = EntityFactory.get().getResource("villager");
		villager.moveTo(chunkPoint, Chunk.LAYER_OBJECT);
		chunk.addEntity(villager);
		return chunk;
	}
}