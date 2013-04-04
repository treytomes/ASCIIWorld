package asciiWorld.chunks;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.Direction;
import asciiWorld.math.IRandom;

public class RoomGenerator implements IRoomGenerator {

	private IRandom _random;
	private int _numRooms;
	private int _minRoomRows;
	private int _maxRoomRows;
	private int _minRoomColumns;
	private int _maxRoomColumns;

	public RoomGenerator(IRandom random) {
		_random = random;
	}

	@Override
	public int getNumRooms() {
		return _numRooms;
	}

	@Override
	public void setNumRooms(int value) {
		_numRooms = value;
	}

	@Override
	public int getMinRoomRows() {
		return _minRoomRows;
	}

	@Override
	public void setMinRoomRows(int value) {
		_minRoomRows = value;
	}

	@Override
	public int getMaxRoomRows() {
		return _maxRoomRows;
	}

	@Override
	public void setMaxRoomRows(int value) {
		_maxRoomRows = value;
	}

	@Override
	public int getMinRoomColumns() {
		return _minRoomColumns;
	}

	@Override
	public void setMinRoomColumns(int value) {
		_minRoomColumns = value;
	}

	@Override
	public int getMaxRoomColumns() {
		return _maxRoomColumns;
	}

	@Override
	public void setMaxRoomColumns(int value) {
		_maxRoomColumns = value;
	}

	@Override
	public void placeRooms(Dungeon dungeon) {
		if ((_numRooms <= 0) || (_minRoomRows <= 0) || (_maxRoomRows <= 0) || (_minRoomColumns <= 0) || (_maxRoomColumns <= 0)) {
			throw new UnsupportedOperationException("Invalid object state; all properties must have positive values.");
		}

		// Loop for the number of rooms to place:
		for (int roomCounter = 0; roomCounter < _numRooms; roomCounter++) {
			Room room = createRoom();
			int bestRoomPlacementScore = Integer.MAX_VALUE;
			Vector2f bestRoomPlacementLocation = null;

			for (Vector2f currentRoomPlacementLocation : dungeon.getCorridorCellLocations()) {
				int currentRoomPlacementScore = calculateRoomPlacementScore(currentRoomPlacementLocation, dungeon, room);

				if (currentRoomPlacementScore < bestRoomPlacementScore) {
					bestRoomPlacementScore = currentRoomPlacementScore;
					bestRoomPlacementLocation = currentRoomPlacementLocation;
				}
			}

			// Create room at best room placement cell.
			if (bestRoomPlacementLocation != null) {
				placeRoom(bestRoomPlacementLocation, dungeon, room);
			}
		}

		placeDoors(dungeon);
	}

	private void placeDoors(Dungeon dungeon) {
		for (Room room : dungeon.getRooms())
		{
			boolean hasNorthDoor = false;
			boolean hasSouthDoor = false;
			boolean hasWestDoor = false;
			boolean hasEastDoor = false;

			for (int row = 0; row < room.getRows(); row++) {
				for (int column = 0; column < room.getColumns(); column++) {
					Vector2f cellLocation = new Vector2f(column, row);

					// Translate the room cell location to its location in the dungeon:
					Vector2f dungeonLocation = new Vector2f(room.getBounds().getX() + cellLocation.x, room.getBounds().getY() + cellLocation.y);

					// Check if we are on the west boundary of our roomand if there is a corridor to the west:
					if (!hasWestDoor && (cellLocation.x == 0) &&
						(dungeon.adjacentCellInDirectionIsCorridor(dungeonLocation, Direction.West))) {
						dungeon.createDoor(dungeonLocation, Direction.West);
						hasWestDoor = true;
					}

					// Check if we are on the east boundary of our room and if there is a corridor to the east
					if (!hasEastDoor && (cellLocation.x == room.getColumns() - 1) &&
						(dungeon.adjacentCellInDirectionIsCorridor(dungeonLocation, Direction.East))) {
						dungeon.createDoor(dungeonLocation, Direction.East);
						hasEastDoor = true;
					}

					// Check if we are on the north boundary of our room and if there is a corridor to the north
					if (!hasNorthDoor && (cellLocation.y == 0) &&
						(dungeon.adjacentCellInDirectionIsCorridor(dungeonLocation, Direction.North))) {
						dungeon.createDoor(dungeonLocation, Direction.North);
						hasNorthDoor = true;
					}

					// Check if we are on the south boundary of our room and if there is a corridor to the south
					if (!hasSouthDoor && (cellLocation.y == room.getRows() - 1) &&
						(dungeon.adjacentCellInDirectionIsCorridor(dungeonLocation, Direction.South))) {
						dungeon.createDoor(dungeonLocation, Direction.South);
						hasSouthDoor = true;
					}
				}
			}
		}
	}

	private void placeRoom(Vector2f location, Dungeon dungeon, Room room) {
		// Offset the room origin to the new location.
		room.setLocation(location);

		// Loop for each cell in the room
		for (int row = 0; row < room.getRows(); row++) {
			for (int column = 0; column < room.getColumns(); column++) {
				// Translate the room cell location to its location in the dungeon.
				Vector2f dungeonLocation = new Vector2f(location.x + column, location.y + row);
				dungeon.get(dungeonLocation).setNorthSide(room.get(row, column).getNorthSide());
				dungeon.get(dungeonLocation).setSouthSide(room.get(row, column).getSouthSide());
				dungeon.get(dungeonLocation).setWestSide(room.get(row, column).getWestSide());
				dungeon.get(dungeonLocation).setEastSide(room.get(row, column).getEastSide());

				// Create room walls on map (either side of the wall)
				if ((column == 0) && (dungeon.hasAdjacentCellInDirection(dungeonLocation, Direction.West))) {
					dungeon.createWall(dungeonLocation, Direction.West);
				}
				if ((column == room.getColumns() - 1) && (dungeon.hasAdjacentCellInDirection(dungeonLocation, Direction.East))) {
					dungeon.createWall(dungeonLocation, Direction.East);
				}
				if ((row == 0) && (dungeon.hasAdjacentCellInDirection(dungeonLocation, Direction.North))) {
					dungeon.createWall(dungeonLocation, Direction.North);
				}
				if ((row == room.getRows() - 1) && (dungeon.hasAdjacentCellInDirection(dungeonLocation, Direction.South))) {
					dungeon.createWall(dungeonLocation, Direction.South);
				}
			}
		}

		dungeon.getRooms().add(room);
	}

	private Room createRoom() {
		Room room = new Room(_random.nextInt(_minRoomRows, _maxRoomRows + 1), _random.nextInt(_minRoomColumns, _maxRoomColumns + 1));
		room.initializeRoomCells();
		return room;
	}

	private int calculateRoomPlacementScore(Vector2f location, Dungeon dungeon, Room room) {
		// Check if the room at the given location will fit inside the bounds of the map.
		if (dungeon.getBounds().contains(new Rectangle(location.x, location.y, room.getColumns(), room.getRows()))) {
			int roomPlacementScore = 0;

			// Loop for each cell in the room.
			for (int column = 0; column < room.getColumns(); column++) {
				for (int row = 0; row < room.getRows(); row++) {
					// Translate the room cell location to its location in the dungeon.
					Vector2f dungeonLocation = new Vector2f(location.x + column, location.y + row);

					// Add 1 point for each adjacent corridor to the cell.
					if (dungeon.adjacentCellInDirectionIsCorridor(dungeonLocation, Direction.North)) {
						roomPlacementScore++;
					}
					if (dungeon.adjacentCellInDirectionIsCorridor(dungeonLocation, Direction.South)) {
						roomPlacementScore++;
					}
					if (dungeon.adjacentCellInDirectionIsCorridor(dungeonLocation, Direction.West)) {
						roomPlacementScore++;
					}
					if (dungeon.adjacentCellInDirectionIsCorridor(dungeonLocation, Direction.East)) {
						roomPlacementScore++;
					}

					// Add 3 points if the cell overlaps an existing corridor.
					if (dungeon.get(dungeonLocation).isCorridor()) {
						roomPlacementScore += 3;
					}

					// Add 100 points if the cell overlaps any existing room cells.
					for (Room dungeonRoom : dungeon.getRooms()) {
						if (dungeonRoom.getBounds().contains(dungeonLocation)) {
							roomPlacementScore += 100;
						}
					}
				}
			}

			return roomPlacementScore;
		} else {
			return Integer.MAX_VALUE;
		}
	}
}
