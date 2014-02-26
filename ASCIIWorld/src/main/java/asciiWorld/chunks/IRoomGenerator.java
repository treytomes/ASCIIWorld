package asciiWorld.chunks;

public interface IRoomGenerator
{
	int getNumRooms();
	void setNumRooms(int value);
	
	int getMinRoomRows();
	void setMinRoomRows(int value);
	
	int getMaxRoomRows();
	void setMaxRoomRows(int value);
	
	int getMinRoomColumns();
	void setMinRoomColumns(int value);
	
	int getMaxRoomColumns();
	void setMaxRoomColumns(int value);

	void placeRooms(Dungeon dungeon);
}
