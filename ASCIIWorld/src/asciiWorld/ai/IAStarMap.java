package asciiWorld.ai;

public interface IAStarMap {

	int getRows();
	int getColumns();
	boolean canWalkHere(int row, int column);
}
