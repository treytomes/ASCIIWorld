package asciiWorld.chunks;

import asciiWorld.Direction;

public class Cell {
	
    private boolean _visited;
    private SideType _northSide;
    private SideType _southSide;
    private SideType _eastSide;
    private SideType _westSide;

    public Cell() {
        _visited = false;
        _northSide = SideType.Wall;
        _southSide = SideType.Wall;
        _eastSide = SideType.Wall;
        _westSide = SideType.Wall;
    }
    
    public boolean getVisited() {
    	return _visited;
    }
    
    public void setVisited(boolean value) {
    	_visited = value;
    }
    
    public SideType getNorthSide() {
    	return _northSide;
    }
    
    public void setNorthSide(SideType value) {
    	_northSide = value;
    }
    
    public SideType getSouthSide() {
    	return _southSide;
    }
    
    public void setSouthSide(SideType value) {
    	_southSide = value;
    }
    
    public SideType getEastSide() {
    	return _eastSide;
    }
    
    public void setEastSide(SideType value) {
    	_eastSide = value;
    }
    
    public SideType getWestSide() {
    	return _westSide;
    }
    
    public void setWestSide(SideType value) {
    	_westSide = value;
    }

    public boolean isDeadEnd() {
    	return getWallCount() == 3;
    }
    
    public boolean isCorridor() {
    	return getWallCount() < 4;
    }
    
    public int getWallCount() {
    	int wallCount = 0;
		if (getNorthSide() == SideType.Wall) {
			wallCount++;
		}
		if (getSouthSide() == SideType.Wall) {
			wallCount++;
		}
		if (getEastSide() == SideType.Wall) {
			wallCount++;
		}
		if (getWestSide() == SideType.Wall) {
			wallCount++;
		}
		return wallCount;
    }

    public Direction calculateDeadEndCorridorDirection() {
        if (!isDeadEnd())
        {
        	throw new UnsupportedOperationException();
        }

        if (getNorthSide() == SideType.Empty)
        {
            return Direction.North;
        }
        if (getSouthSide() == SideType.Empty)
        {
            return Direction.South;
        }
        if (getEastSide() == SideType.Empty)
        {
            return Direction.East;
        }
        if (getWestSide() == SideType.Empty)
        {
            return Direction.West;
        }

        throw new UnsupportedOperationException();
    }
}
