package asciiWorld.chunks;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.geom.Vector2f;

import asciiWorld.Direction;
import asciiWorld.math.IRandom;

public class Dungeon extends Map {

    private List<Vector2f> _visitedCells;
    private List<Room> _rooms;

    public Dungeon(int rows, int columns) {
    	super(rows, columns);
        _visitedCells = new ArrayList<Vector2f>();
        _rooms = new ArrayList<Room>();

        markCellsUnvisited();
    }

    public boolean allCellsVisited() {
    	//System.err.println(String.format("Visiting %d of %d...", _visitedCells.size(), getRows() * getColumns()));
    	return _visitedCells.size() == (getRows() * getColumns());
    }

    public List<Vector2f> getDeadEndCellLocations() {
    	List<Vector2f> points = new ArrayList<Vector2f>();
        for (int row = 0; row < getRows(); row++) {
            for (int column = 0; column < getColumns(); column++) {
                if (get(row, column).isDeadEnd()) {
                    points.add(new Vector2f(column, row));
                }
            }
        }
        return points;
    }

    public List<Vector2f> getCorridorCellLocations() {
    	List<Vector2f> points = new ArrayList<Vector2f>();
        for (int row = 0; row < getRows(); row++) {
            for (int column = 0; column < getColumns(); column++) {
                if (get(row, column).isCorridor()) {
                    points.add(new Vector2f(column, row));
                }
            }
        }
        return points;
    }

    public List<Room> getRooms() {
    	return _rooms;
    }

    public void markCellsUnvisited() {
        for (int row = 0; row < getRows(); row++) {
            for (int column = 0; column < getColumns(); column++) {
            	set(row, column, new Cell());
            }
        }
        _visitedCells.clear();
    }

    public Vector2f pickRandomCellAndMarkItVisited(IRandom random) throws Exception {
        Vector2f pnt = new Vector2f(random.nextInt(getColumns()), random.nextInt(getRows()));
        flagCellAsVisited(pnt);
        return pnt;
    }

    public boolean adjacentCellInDirectionIsVisited(Vector2f location, Direction direction) {
        if (!hasAdjacentCellInDirection(location, direction)) {
            throw new UnsupportedOperationException("No adjacent cell exists for the location and direction provided.");
        }

        switch (direction) {
            case North:
                return get((int)location.y - 1, (int)location.x).getVisited();
            case South:
                return get((int)location.y + 1, (int)location.x).getVisited();
            case West:
                return get((int)location.y, (int)location.x - 1).getVisited();
            case East:
                return get((int)location.y, (int)location.x + 1).getVisited();
            default:
                throw new UnsupportedOperationException("No adjacent cell exists for the location and direction provided.");
        }
    }

    public boolean adjacentCellInDirectionIsCorridor(Vector2f location, Direction direction) {
        if (!hasAdjacentCellInDirection(location, direction)) {
            return false;
        }

        Vector2f target = getTargetLocation(location, direction);

        switch (direction) {
            case North:
                return get(target).isCorridor();
            case West:
                return get(target).isCorridor();
            case South:
                return get(target).isCorridor();
            case East:
                return get(target).isCorridor();
            default:
            	throw new UnsupportedOperationException();
        }
    }

    public void flagCellAsVisited(Vector2f location)
    		throws Exception {
        if (locationIsOutsideBounds(location)) {
            throw new Exception("Location is outside of map bounds.");
        }
        if (get(location).getVisited()) {
            throw new Exception("Location is already visited.");
        } else {
	        get(location).setVisited(true);
	        _visitedCells.add(location);
        }
    }

    private boolean locationIsOutsideBounds(Vector2f location) {
        return ((location.x < 0) || (location.x >= getColumns()) || (location.y < 0) || (location.y >= getRows()));
    }

    public Vector2f getRandomVisitedCell(Vector2f location, IRandom random) {
        if (_visitedCells.isEmpty()) {
        	throw new UnsupportedOperationException("There are no visited cells to return.");
        }

        int index = random.nextInt(_visitedCells.size());

        // Loop while the current cell is the visited cell:
        while (_visitedCells.get(index) == location) {
            index = random.nextInt(_visitedCells.size());
        }

        return _visitedCells.get(index);
    }

    public Vector2f createCorridor(Vector2f location, Direction direction) {
        return createSide(location, direction, SideType.Empty);
    }

    public Vector2f createWall(Vector2f location, Direction direction) {
        return createSide(location, direction, SideType.Wall);
    }

    public Vector2f createDoor(Vector2f location, Direction direction) {
        return createSide(location, direction, SideType.Door);
    }

    private Vector2f createSide(Vector2f location, Direction direction, SideType sideType) {
    	Vector2f target = getTargetLocation(location, direction);

        switch (direction) {
            case North:
                get(location).setNorthSide(sideType);
                get(target).setSouthSide(sideType);
                break;
            case South:
            	get(location).setSouthSide(sideType);
            	get(target).setNorthSide(sideType);
                break;
            case West:
            	get(location).setWestSide(sideType);
            	get(target).setEastSide(sideType);
                break;
            case East:
            	get(location).setEastSide(sideType);
            	get(target).setWestSide(sideType);
                break;
        }

        return target;
    }
}