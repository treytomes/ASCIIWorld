package asciiWorld.chunks;

import java.io.PrintStream;
import java.util.Map;
import java.util.HashMap;

import org.newdawn.slick.geom.Vector2f;

import asciiWorld.entities.Entity;
import asciiWorld.entities.EntityFactory;
import asciiWorld.math.IRandom;
import asciiWorld.math.RandomFactory;

public class DungeonChunkGenerator implements IChunkGenerator {

    private static final double ODDS_OF_GENERATING_TREASURE_CHEST = 0.2;
    private static final int MIN_ITEMS_IN_TREASURE_CHEST = 1;
    private static final int MAX_ITEMS_IN_TREASURE_CHEST = 16;

	private static final String TILETYPE_FLOOR = "clay";
	private static final String TILETYPE_WALL = "stone";
	private static final String ENTITY_DOOR = "woodenDoor";
	private static final String ENTITY_TREASURECHEST = "treasureChest";

	@Override
	public Chunk generate(Chunk blankChunk, long seed, PrintStream logStream)
			throws Exception {
		RandomFactory.get().reseed(seed);

		logStream.print("Generating dungeon...");
		IDungeonGenerator generator = createDungeonGenerator(RandomFactory.get().getRandom());
		Dungeon dungeon = generator.generate();
		logStream.println(" done!");
		
		return convertToChunk(blankChunk, dungeon, logStream);
	}

	private Chunk convertToChunk(Chunk chunk, Dungeon dungeon, PrintStream logStream) {
		logStream.println("Converting dungeon into chunk...");
		
		logStream.print("Generating a rocky chunk...");
        chunk = generateRockyChunk(chunk);
        logStream.println(" done!");
        
		logStream.print("Excavating rooms...");
		chunk = excavateRooms(chunk, dungeon);
        logStream.println(" done!");
        
		logStream.print("Excavating corridors...");
        chunk = excavateCorridors(chunk, dungeon);
        logStream.println(" done!");

		logStream.print("Filling rooms with treasure...");
        chunk = fillRoomsWithTreasure(chunk, dungeon);
        logStream.println(" done!");

        return chunk;
	}

	private Chunk generateRockyChunk(Chunk chunk) {
		// Initialize the tile array to rock.
		for (int row = 0; row < Chunk.ROWS; row++) {
			for (int column = 0; column < Chunk.COLUMNS; column++) {
                EntityFactory.get().createEntity(TILETYPE_WALL, chunk, column, row, Chunk.LAYER_OBJECT);
                EntityFactory.get().createEntity(TILETYPE_FLOOR, chunk, column, row, Chunk.LAYER_GROUND);
			}
		}

		return chunk;
	}

	private Chunk excavateRooms(Chunk chunk, Dungeon dungeon) {
		System.out.println("Excavating room...");
		System.out.println(dungeon.getRooms().size());
		// Fill tiles with corridor values for each room in dungeon.
		for (Room room : dungeon.getRooms()) {
			// Get the room min and max location in tile coordinates.
			Vector2f minPoint = new Vector2f(room.getBounds().getMinX() * 2 + 1, room.getBounds().getMinY() * 2 + 1);
			Vector2f maxPoint = new Vector2f(room.getBounds().getMaxX() * 2, room.getBounds().getMaxY() * 2);

			// Fill the room in tile space with an empty value.
			for (int row = (int)minPoint.y; row < maxPoint.y; row++) {
				for (int column = (int)minPoint.x; column < maxPoint.x; column++) {
					excavateChunkPoint(chunk, new Vector2f(column, row));
				}
			}
		}
		System.out.println("Room complete!");

		return chunk;
	}

	private Chunk excavateCorridors(Chunk chunk, Dungeon dungeon) {
		// Loop for each corridor cell and expand it.
		for (Vector2f cellLocation : dungeon.getCorridorCellLocations()) {
			Vector2f tileLocation = new Vector2f(cellLocation.x * 2 + 1, cellLocation.y * 2 + 1);
            excavateChunkPoint(chunk, new Vector2f(tileLocation.x, tileLocation.y));

			if (dungeon.get(cellLocation).getNorthSide() == SideType.Empty) {
				excavateChunkPoint(chunk, new Vector2f(tileLocation.x, tileLocation.y - 1));
			} else if (dungeon.get(cellLocation).getNorthSide() == SideType.Door) {
				excavateChunkPoint(chunk, new Vector2f(tileLocation.x, tileLocation.y - 1));
				EntityFactory.get().createEntity(ENTITY_DOOR, chunk, (int)tileLocation.x, (int)tileLocation.y - 1);
			}

			if (dungeon.get(cellLocation).getSouthSide() == SideType.Empty) {
				excavateChunkPoint(chunk, new Vector2f(tileLocation.x, tileLocation.y + 1));
			} else if (dungeon.get(cellLocation).getSouthSide() == SideType.Door) {
				excavateChunkPoint(chunk, new Vector2f(tileLocation.x, tileLocation.y + 1));
				EntityFactory.get().createEntity(ENTITY_DOOR, chunk, (int)tileLocation.x, (int)tileLocation.y + 1);
			}

			if (dungeon.get(cellLocation).getWestSide() == SideType.Empty) {
				excavateChunkPoint(chunk, new Vector2f(tileLocation.x - 1, tileLocation.y));
			} else if (dungeon.get(cellLocation).getWestSide() == SideType.Door) {
				excavateChunkPoint(chunk, new Vector2f(tileLocation.x - 1, tileLocation.y));
				EntityFactory.get().createEntity(ENTITY_DOOR, chunk, (int)tileLocation.x - 1, (int)tileLocation.y);
			}

			if (dungeon.get(cellLocation).getEastSide() == SideType.Empty) {
				excavateChunkPoint(chunk, new Vector2f(tileLocation.x + 1, tileLocation.y));
			} else if (dungeon.get(cellLocation).getEastSide() == SideType.Door) {
				excavateChunkPoint(chunk, new Vector2f(tileLocation.x + 1, tileLocation.y));
				EntityFactory.get().createEntity(ENTITY_DOOR, chunk, (int)tileLocation.x + 1, (int)tileLocation.y);
			}
		}

		return chunk;
	}

    private void excavateChunkPoint(Chunk chunk, Vector2f chunkPoint) {
    	Entity entity = chunk.getEntityAt(chunkPoint, Chunk.LAYER_OBJECT);
    	if (entity != null) {
    		System.out.println(String.format("Removing %s at %d,%d.", entity.getName(), (int)chunkPoint.x, (int)chunkPoint.y));
    		chunk.removeEntity(entity);
    	}
    }

	private Chunk fillRoomsWithTreasure(Chunk chunk, Dungeon dungeon) {
		for (Room room : dungeon.getRooms()) {
			// Get the room min and max location in tile coordinates.
			Vector2f minPoint = new Vector2f(room.getBounds().getMinX() * 2 + 1, room.getBounds().getMinY() * 2 + 1);
			Vector2f maxPoint = new Vector2f(room.getBounds().getMaxX() * 2, room.getBounds().getMaxY() * 2);

            double odds = RandomFactory.get().nextDouble();
            if (odds <= ODDS_OF_GENERATING_TREASURE_CHEST) {
                // Pick a spot to put the treasure chest.
                while (true) {
                	Vector2f chunkPoint = new Vector2f(RandomFactory.get().nextInt((int)minPoint.x, (int)maxPoint.x), RandomFactory.get().nextInt((int)minPoint.y, (int)maxPoint.y));
                    if (!isDoorAdjacent(chunk, chunkPoint)) {
                        // Now we know that the treasure chest won't be blocking a door.

                        if (!chunk.isSpaceOccupied(chunkPoint, Chunk.LAYER_OBJECT)) {
                            // The space isn't occupied, and we're not blocking a door, so we're good for placing some treasure.

                            chunk = generateTreasure(chunk, chunkPoint);
                            break;
                        }
                    }
                }
            }
		}

		return chunk;
	}

	private boolean isDoorAdjacent(Chunk chunk, Vector2f chunkPoint) {
		Entity north = chunk.getEntityAt(new Vector2f(chunkPoint.x, chunkPoint.y), Chunk.LAYER_OBJECT);
		Entity south = chunk.getEntityAt(new Vector2f(chunkPoint.x, chunkPoint.y + 1), Chunk.LAYER_OBJECT);
		Entity east = chunk.getEntityAt(new Vector2f(chunkPoint.x - 1, chunkPoint.y), Chunk.LAYER_OBJECT);
		Entity west = chunk.getEntityAt(new Vector2f(chunkPoint.x + 1, chunkPoint.y), Chunk.LAYER_OBJECT);
		
		return
				((north != null) && (north.getName().equals(ENTITY_DOOR))) ||
				((south != null) && (south.getName().equals(ENTITY_DOOR))) ||
				((east != null) && (east.getName().equals(ENTITY_DOOR))) ||
				((west != null) && (west.getName().equals(ENTITY_DOOR)));
	}

	private Chunk generateTreasure(Chunk chunk, Vector2f chunkPoint) {
		// Create the treasure chest.
		Entity treasureChest = EntityFactory.get().createEntity(ENTITY_TREASURECHEST, chunk, (int)chunkPoint.x, (int)chunkPoint.y);

		// TODO: Implement the TreasureChestComponent.
		/*TreasureChestComponent inventory = treasureChest.findComponent(TreasureChestComponent.class);

		// Each treasure chest will have up to 16 pieces of random treasure.
        for (int n = 0; n < RandomFactory.get().nextInt(MIN_ITEMS_IN_TREASURE_CHEST, MAX_ITEMS_IN_TREASURE_CHEST + 1); n++) {
			inventory.addItemToChest(EntityFactory.get().getResource(getRandomTreasure()));
		}*/

		return chunk;
	}

	private String getRandomTreasure() {
		Map<String, Double> treasureTypes = new HashMap<String, Double>();
		treasureTypes.put("bauble", 0.01);
		treasureTypes.put("boulder", 0.45);
		treasureTypes.put("craftingTable", 0.15);
		treasureTypes.put("healingPotion", 0.05);
		treasureTypes.put("stoneRubble", 0.4);
		treasureTypes.put("stoneSword", 0.3);
		treasureTypes.put("torch", 0.5);
		treasureTypes.put("treasureChest", 0.1);
		treasureTypes.put("woodenClub", 0.4);
		treasureTypes.put("woodenSword", 0.4);
		treasureTypes.put("woodLog", 0.8);
		treasureTypes.put("woodPlank", 0.7);
		treasureTypes.put("woodStick", 0.6);
		treasureTypes.put("woodWall", 0.5);

		String[] keys = treasureTypes.keySet().toArray(new String[0]);
		
        while (true) {
        	String itemKey = keys[RandomFactory.get().nextInt(0, treasureTypes.size())];
        	double odds = RandomFactory.get().nextDouble();
            if (odds <= treasureTypes.get(itemKey)) {
                return itemKey;
            }
        }
	}
	
	private IDungeonGenerator createDungeonGenerator(IRandom random) {
		DungeonGenerator generator = new DungeonGenerator(random, createRoomGenerator(random));
		generator.setRows((Chunk.ROWS - 1) / 2);
		generator.setColumns((Chunk.COLUMNS - 1) / 2);
		generator.setChangeDirectionModifier(random.nextDouble());
		generator.setSparsenessFactor(random.nextDouble());
		generator.setDeadEndRemovalModifier(random.nextDouble());

		return generator;
	}

	private IRoomGenerator createRoomGenerator(IRandom random) {
		IRoomGenerator roomGenerator = new RoomGenerator(random);
		roomGenerator.setNumRooms(5);
		roomGenerator.setMinRoomRows(2);
		roomGenerator.setMaxRoomRows(5);
		roomGenerator.setMinRoomColumns(2);
		roomGenerator.setMaxRoomColumns(5);
		return roomGenerator;
	}

}
