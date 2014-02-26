package asciiWorld.chunks;

import java.util.ArrayList;
import java.util.List;

import asciiWorld.Direction;
import asciiWorld.math.IRandom;

public class DirectionPicker {
	
    private IRandom _random;
    private List<Direction> _directionsPicked;
    private Direction _previousDirection;
    private double _changeDirectionModifer;

    /// <param name="changeDirectionModifier">Value from 0.0 to 1.0.  Percentage chance of changing direction.</param>
    public DirectionPicker(IRandom random, Direction previousDirection, double changeDirectionModifier) {
        _random = random;
        _directionsPicked = new ArrayList<Direction>();
        _previousDirection = previousDirection;
        _changeDirectionModifer = changeDirectionModifier;
    }

    public boolean hasNextDirection() {
    	return _directionsPicked.size() < Direction.values().length;
    }

    public Direction getNextDirection()
    {
        if (!hasNextDirection()) {
        	throw new UnsupportedOperationException("No directions available.");
        }

        Direction directionPicked;

        do {
            directionPicked = mustChangeDirection(_changeDirectionModifer) ? pickDifferentDirection() : _previousDirection;
        } while (_directionsPicked.contains(directionPicked));

        _directionsPicked.add(directionPicked);
        return directionPicked;
    }

    private Direction pickDifferentDirection() {
    	Direction directionPicked;
        do {
            directionPicked = Direction.random();
        } while ((directionPicked == _previousDirection) && (_directionsPicked.size() < 3));
        return directionPicked;
    }

    /// <param name="changeDirectionModifier">Value from 0.0 to 1.0.  Percentage chance of changing direction.</param>
    public boolean mustChangeDirection(double changeDirectionModifier) {
        return (_directionsPicked.size() > 0) || (changeDirectionModifier > _random.nextDouble());
    }
}
