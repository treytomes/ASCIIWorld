package asciiWorld.chunks;

import java.util.List;

import org.newdawn.slick.geom.Vector2f;

import asciiWorld.Direction;
import asciiWorld.math.IRandom;

public class DungeonGenerator implements IDungeonGenerator {

	private static final boolean SPARSIFY_MAZE = true;
	private static final boolean REMOVE_DEAD_ENDS = true;
	private static final boolean PLACE_ROOMS = true;

	private IRandom _random;
	private int _rows;
	private int _columns;
	private double _changeDirectionModifier;
	private double _sparsenessFactor;
	private double _deadEndRemovalModifier;
	private IRoomGenerator _roomGenerator;

	public DungeonGenerator(IRandom random, IRoomGenerator roomGenerator) {
		_random = random;
		_roomGenerator = roomGenerator;
	}

	public int getRows() {
		return _rows;
	}
	
	public void setRows(int value) {
		_rows = value;
	}

	public int getColumns() {
		return _columns;
	}
	
	public void setColumns(int value) {
		_columns = value;
	}

	/**
	 * Value from 0.0 to 1.0.  Percentage chance of changing direction.
	 */
	public double getChangeDirectionModifier() {
		return _changeDirectionModifier;
	}
	
	public void setChangeDirectionModifier(double value) {
		_changeDirectionModifier = value;
	}

	/**
	 * Percentage of the map (0.0 to 1.0) turned to rock.
	 */
	public double getSparsenessFactor() {
		return _sparsenessFactor;
	}
	
	public void setSparsenessFactor(double value) {
		_sparsenessFactor = value;
	}

	/**
	 * Percentage value (0.0 - 1.0) of dead ends to convert into loops.
	 */
	public double getDeadEndRemovalModifier() {
		return _deadEndRemovalModifier;
	}
	
	public void setDeadEndRemovalModifier(double value) {
		_deadEndRemovalModifier = value;
	}

	public Dungeon generate() {
		try {
			Dungeon dungeon = createDenseMaze(_rows, _columns, _changeDirectionModifier);

			if (SPARSIFY_MAZE) {
				sparsifyMaze(dungeon, _sparsenessFactor);
			}

			if (REMOVE_DEAD_ENDS) {
				removeDeadEnds(dungeon, _deadEndRemovalModifier);
			}

			if (PLACE_ROOMS) {
				_roomGenerator.placeRooms(dungeon);
			}

			return dungeon;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Dungeon createDenseMaze(int rows, int columns, double changeDirectionModifier) throws Exception {
		Dungeon map = new Dungeon(rows, columns);
		map.markCellsUnvisited();
		Vector2f currentLocation = map.pickRandomCellAndMarkItVisited(_random);
		Direction previousDirection = Direction.North;

		while (!map.allCellsVisited()) {
			//System.err.println(String.format("Current location (B): %d, %d", (int)currentLocation.x, (int)currentLocation.y));
			
			DirectionPicker directionPicker = new DirectionPicker(_random, previousDirection, changeDirectionModifier);
			Direction direction = directionPicker.getNextDirection();

			while (!map.hasAdjacentCellInDirection(currentLocation, direction) || map.adjacentCellInDirectionIsVisited(currentLocation, direction)) {
				if (directionPicker.hasNextDirection()) {
					direction = directionPicker.getNextDirection();
				} else {
					currentLocation = map.getRandomVisitedCell(currentLocation, _random); // get a new previously visited location
					directionPicker = new DirectionPicker(_random, previousDirection, changeDirectionModifier); // reset the direction picker
					direction = directionPicker.getNextDirection(); // get a new direction.
				}
			}

			//System.err.println(String.format("Was it visited? %b", map.adjacentCellInDirectionIsVisited(currentLocation, direction)));

			currentLocation = map.createCorridor(currentLocation, direction);

			//System.err.println(String.format("Current location (N): %d, %d", (int)currentLocation.x, (int)currentLocation.y));

			map.flagCellAsVisited(currentLocation);
			previousDirection = direction;

			//System.err.println(String.format("Current location (E): %d, %d", (int)currentLocation.x, (int)currentLocation.y));
		}

		return map;
	}

	/**
	 * 
	 * @param map
	 * @param sparsenessFactor Percentage of the map (0.0 to 1.0) turned to rock.
	 */
	private void sparsifyMaze(Dungeon map, double sparsenessFactor) {
		// Calculate the number of cells to remove as a percentage of the total number of cells in the map:
		int noOfDeadEndCellsToRemove = (int)Math.ceil(sparsenessFactor * map.getRows() * map.getColumns());
		List<Vector2f> points = map.getDeadEndCellLocations();

		for (int i = 0; i < noOfDeadEndCellsToRemove; i++) {
			if (points.size() == 0) { // check if there is another item in our enumerator
				points = map.getDeadEndCellLocations(); // get a new list
				if (points.size() == 0) {
					break; // no new items exist so break out of loop
				}
			}

			int index = _random.nextInt(0, points.size());
			Vector2f point = points.get(index);
			points.remove(index);
			if (map.get(point).isDeadEnd()) { // make sure the status of the cell hasn't change
				map.createWall(point, map.get(point).calculateDeadEndCorridorDirection());
			}
		}
	}

	/**
	 * 
	 * @param map
	 * @param deadEndRemovalModifier Percentage value (0.0 - 1.0) of dead ends to convert into loops.
	 */
	private void removeDeadEnds(Dungeon map, double deadEndRemovalModifier) {
		int noOfDeadEndCellsToRemove = (int)Math.ceil(deadEndRemovalModifier * map.getRows() * map.getColumns());
		List<Vector2f> deadEndLocations = map.getDeadEndCellLocations();

		for (int i = 0; i < noOfDeadEndCellsToRemove; i++) {
			if (deadEndLocations.size() == 0) {
				break;
			}

			int index = _random.nextInt(0, deadEndLocations.size());
			Vector2f deadEndLocation = deadEndLocations.get(index);
			deadEndLocations.remove(index);
			if (map.get(deadEndLocation).isDeadEnd()) {
				Vector2f currentLocation = deadEndLocation;

				do {
					// Initialize the direction picker not to select the dead-end corridor direction.
					DirectionPicker directionPicker = new DirectionPicker(_random, map.get(currentLocation).calculateDeadEndCorridorDirection(), 1);
					Direction direction = directionPicker.getNextDirection();

					while (!map.hasAdjacentCellInDirection(currentLocation, direction)) {
						if (directionPicker.hasNextDirection()) {
							direction = directionPicker.getNextDirection();
						} else {
							throw new UnsupportedOperationException("This should not happen.");
						}
					}

					// Create a corridor in the selected direction:
					currentLocation = map.createCorridor(currentLocation, direction);
				} while (map.get(currentLocation).isDeadEnd()); // stop when you intersect an existing corridor
			}
		}
	}
}
