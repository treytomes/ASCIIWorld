package asciiWorld.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import asciiWorld.math.IDistanceAlgorithm;
import asciiWorld.math.ManhattanDistanceAlgorithm;
import asciiWorld.math.Point;

public class AStarImproved {
	
	enum FindNeighborsFunction {
		NONE,
		DIAGONAL_NEIGHBORS, // no squeezing through cracks
		DIAGONAL_NEIGHBORS_FREE // squeezing through cracks allowed
	}
	
	/**
	 * Path function, executes AStar algorithm operations.
	 * @return
	 */
	public static List<Point> findPath(IAStarMap map, Point pathStart, Point pathEnd) {
		//System.out.println(String.format("PS (%d, %d)", pathStart.x, pathStart.y));
		//System.out.println(String.format("PE (%d, %d)", pathEnd.x, pathEnd.y));

		// Which heuristic should we use?  Default: no diagonals (Manhattan)
		Class<ManhattanDistanceAlgorithm> distanceFn = ManhattanDistanceAlgorithm.class;
		FindNeighborsFunction neighborFn = FindNeighborsFunction.NONE;
	 
		/*
		// Diagonals allowed but no sqeezing through cracks:
		var distanceFunction = DiagonalDistance;
		var findNeighbours = DiagonalNeighbours;
	 
		// diagonals and squeezing through cracks allowed:
		var distanceFunction = DiagonalDistance;
		var findNeighbours = DiagonalNeighboursFree;
	 
		// euclidean but no squeezing through cracks:
		var distanceFunction = EuclideanDistance;
		var findNeighbours = DiagonalNeighbours;
	 
		// euclidean and squeezing through cracks allowed:
		var distanceFunction = EuclideanDistance;
		var findNeighbours = DiagonalNeighboursFree;
		*/
		
		// Keep track of world dimensions.
		// Note that this A* implementation expectes the world array to be square:
		// it must have equal width and height.  If your game world is rectangular,
		// Just fill the array with dummy values to pad the empty space.
		int mapSize = map.getRows() * map.getColumns();

		// Create Nodes from the Start and End x, y coordinates.
		AStarNode mypathStart = new AStarNode(map.getColumns(), null, pathStart.copy());
		AStarNode myPathEnd = new AStarNode(map.getColumns(), null, pathEnd.copy());
		
		boolean[] visitedNodes = new boolean[mapSize]; // create an array that will contain all world cells
		
		List<AStarNode> openNodes = new ArrayList<AStarNode>(); // list of currently open Nodes
		openNodes.add(mypathStart);
		
		List<Point> result = new ArrayList<Point>(); // list of the final output array
		
		List<Point> myNeighbours; // reference to a Node (that is nearby)
		AStarNode myNode; // reference to a Node (that we are considering now)
		AStarNode myPath; // reference to a Node (that starts a path in question)
		
		int min;
		double length, max;
		
		// Iterate through the open list until none are left.
		while ((length = openNodes.size()) > 0) {
			max = mapSize;
			min = -1;
			for (int i = 0; i < length; i++) {
				if (openNodes.get(i).f < max) {
					max = openNodes.get(i).f;
					min = i;
				}
			}
			
			// Grab the next node and remove it from Open array.
			myNode = openNodes.get(min);
			openNodes.remove(min); 
			
			if (myNode.value == myPathEnd.value) { // is it the destination node?
				myPath = myNode;
				do {
					result.add(new Point(myPath.x, myPath.y));
				} while ((myPath = myPath.parent) != null);
				
				Collections.reverse(result); // we want to return start to finish
			} else { // not the destination
				myNeighbours = neighbors(neighborFn, map, myNode.x, myNode.y); // find which nearby nodes are walkable
				
				// Test each one that hasn't been tried already
				for (int i = 0, j = myNeighbours.size(); i < j; i++) {
					myPath = new AStarNode(map.getColumns(), myNode, myNeighbours.get(i));
					if (!visitedNodes[myPath.value]) {
						// Switching it to add myPath.f instead of myPath.g makes it more dependable in real-time.  I don't know why it works, but it does.
						myPath.f = myPath.f + distance(distanceFn, myNeighbours.get(i), myPathEnd); // estimated cost of entire guessed route to the destination
						
						openNodes.add(myPath); // remember this new path for testing above
						visitedNodes[myPath.value] = true; // mark this node in the world graph as visited
					}
				}
			}
		} // keep iterating until until the openNodes list is empty
		return result;
	}
	
	private static <TDistance extends IDistanceAlgorithm> double distance(Class<TDistance> distanceClass, Point point, AStarNode goal) {
		try {
			return distanceClass.newInstance().getDistance(point, goal);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			System.err.println("Defaulting to Manhattan distance.");
			return new ManhattanDistanceAlgorithm().getDistance(point, goal);
		}
	}
	
	/**
	 * Returns every available north, south, east, or west cell that is empty.
	 * No diagonals, unless distance function is not Manhattan.
	 * @param x
	 * @param y
	 * @return
	 */
	private static List<Point> neighbors(FindNeighborsFunction fn, IAStarMap map, int x, int y) {
		int	N = y - 1;
		int S = y + 1;
		int E = x + 1;
		int W = x - 1;
		boolean myN = (N > -1) && map.canWalkHere(N, x);
		boolean myS = (S < map.getRows()) && map.canWalkHere(S, x);
		boolean myE = (E < map.getColumns()) && map.canWalkHere(y, E);
		boolean myW = (W > -1) && map.canWalkHere(y, W);
		List<Point> result = new ArrayList<Point>();
		
		if (myN) {
			result.add(new Point(x, N));
		}
		if (myE) {
			result.add(new Point(E, y));
		}
		if (myS) {
			result.add(new Point(x, S));
		}
		if (myW) {
			result.add(new Point(W, y));
		}
		
		switch (fn) {
		case DIAGONAL_NEIGHBORS:
			result = diagonalNeighbours(map, myN, myS, myE, myW, N, S, E, W, result);
			break;
		case DIAGONAL_NEIGHBORS_FREE:
			result = diagonalNeighboursFree(map, myN, myS, myE, myW, N, S, E, W, result);
			break;
		default:
			break;
		}
		return result;
	}

	/**
	 * Returns every available North East, South East, South West or North West cell - no squeezing through"cracks" between two diagonals.
	 * @param map
	 * @param maxWalkableTileNum
	 * @param myN
	 * @param myS
	 * @param myE
	 * @param myW
	 * @param N
	 * @param S
	 * @param E
	 * @param W
	 * @param result
	 * @return
	 */
	private static List<Point> diagonalNeighbours(IAStarMap map, boolean myN, boolean myS, boolean myE, boolean myW, int N, int S, int E, int W, List<Point> result) {
		if (myN) {
			if (myE && map.canWalkHere(N, E)) {
				result.add(new Point(E, N));
			}
			if (myW && map.canWalkHere(N, W)) {
				result.add(new Point(W, N));
			}
		}
		if (myS) {
			if (myE && map.canWalkHere(S, E)) {
				result.add(new Point(E, S));
			}
			if (myW && map.canWalkHere(S, W)) {
				result.add(new Point(W, S));
			}
		}
		return result;
	}
	
	/**
	 * Returns every available North East, South East, South West or North West cell including the times that you would be squeezing through a "crack".
	 * @param map
	 * @param maxWalkableTileNum
	 * @param mapWidth
	 * @param mapHeight
	 * @param myN
	 * @param myS
	 * @param myE
	 * @param myW
	 * @param N
	 * @param S
	 * @param E
	 * @param W
	 * @param result
	 * @return
	 */
	private static List<Point> diagonalNeighboursFree(IAStarMap map, boolean myN, boolean myS, boolean myE, boolean myW, int N, int S, int E, int W, List<Point> result) {
		myN = N > -1;
		myS = S < map.getRows();
		myE = E < map.getColumns();
		myW = W > -1;
		if (myE) {
			if (myN && map.canWalkHere(N, E)) {
				result.add(new Point(E, N));
			}
			if (myS && map.canWalkHere(S, E)) {
				result.add(new Point(E, S));
			}
		}
		if (myW) {
			if (myN && map.canWalkHere(N, W)) {
				result.add(new Point(W, N));
			}
			if (myS && map.canWalkHere(W, S)) {
				result.add(new Point(W, S));
			}
		}
		return result;
	}
}
