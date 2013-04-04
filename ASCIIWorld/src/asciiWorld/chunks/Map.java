package asciiWorld.chunks;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.Direction;

public class Map {

    private Cell[][] _cells;
    private Rectangle _bounds;
    private int _rows;
    private int _columns;

    protected Map(int rows, int columns) {
		_cells = new Cell[rows][columns];
		_bounds = new Rectangle(0, 0, columns, rows);
		_rows = rows;
		_columns = columns;
    }

    public Cell get(int row, int column) {
    	return _cells[row][column];
    }
    
    public void set(int row, int column, Cell value) {
    	_cells[row][column] = value;
    }

    public Cell get(Vector2f location) {
    	return _cells[(int)location.y][(int)location.x];
    }
    
    public void set(Vector2f location, Cell value) {
    	_cells[(int)location.y][(int)location.x] = value;
    }
    
    public int getRows() {
    	return _rows;
    }
    
    public int getColumns() {
    	return _columns;
    }

    public Rectangle getBounds() {
    	return _bounds;
    }

    protected Vector2f getTargetLocation(Vector2f location, Direction direction) {
        if (!hasAdjacentCellInDirection(location, direction)) {
        	throw new UnsupportedOperationException("No adjacent cell exists for the location and direction provided.");
        }

        switch (direction) {
            case North:
                return new Vector2f(location.x, location.y - 1);
            case South:
                return new Vector2f(location.x, location.y + 1);
            case West:
                return new Vector2f(location.x - 1, location.y);
            case East:
                return new Vector2f(location.x + 1, location.y);
            default:
                throw new UnsupportedOperationException("No adjacent cell exists for the location and direction provided.");
        }
    }

    public boolean hasAdjacentCellInDirection(Vector2f location, Direction direction) {
        // Check that the location falls within the bounds of the map:
        if ((location.x < 0) || (location.x >= getColumns()) || (location.y < 0) || (location.y >= getRows())) {
            return false;
        }

        // Check if there is an adjacent cell in the direction:
        switch (direction) {
            case North:
                return location.y > 0;
            case South:
                return location.y < (getRows() - 1);
            case West:
                return location.x > 0;
            case East:
                return location.x < (getColumns() - 1);
            default:
                return false;
        }
    }
}
