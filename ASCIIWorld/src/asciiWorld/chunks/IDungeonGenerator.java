package asciiWorld.chunks;

public interface IDungeonGenerator {

	int getRows();
	void setRows(int value);
	
	int getColumns();
	void setColumns(int value);
	
	/**
	 * 
	 * @return Value from 0.0 to 1.0.  Percentage chance of changing direction.
	 */
	double getChangeDirectionModifier();
	void setChangeDirectionModifier(double value);
	
	/**
	 * 
	 * @return Percentage of the map (0.0 to 1.0) turned to rock.
	 */
	double getSparsenessFactor();
	void setSparsenessFactor(double value);
	
	/**
	 * 
	 * @return Percentage value (0.0 - 1.0) of dead ends to convert into loops.
	 */
	double getDeadEndRemovalModifier();
	void setDeadEndRemovalModifier(double value);
	
	Dungeon generate();
}
