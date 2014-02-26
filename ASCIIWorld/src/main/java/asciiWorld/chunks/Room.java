package asciiWorld.chunks;

import org.newdawn.slick.geom.Vector2f;

public class Room extends Map {

    public Room(int rows, int columns) {
    	super(rows, columns);
    	initializeRoomCells();
    }
    
    public void initializeRoomCells() {
        for (int row = 0; row < getRows(); row++) {
            for (int column = 0; column < getColumns(); column++) {
                Cell cell = new Cell();

                cell.setNorthSide((row == 0) ? SideType.Wall : SideType.Empty);
                cell.setSouthSide((row == getRows() - 1) ? SideType.Wall : SideType.Empty);
                cell.setWestSide((column == 0) ? SideType.Wall : SideType.Empty);
                cell.setEastSide((column == getColumns() - 1) ? SideType.Wall : SideType.Empty);

                set(row, column, cell);
            }
        }
    }

    public void setLocation(Vector2f location) {
        getBounds().setX(location.x);
        getBounds().setY(location.y);
    }
}
