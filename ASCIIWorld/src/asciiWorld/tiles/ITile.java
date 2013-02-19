package asciiWorld.tiles;

import org.newdawn.slick.Color;

public interface ITile {
	
	Color getBackgroundColor();
	Color getForegroundColor();
	int getTileIndex();
}