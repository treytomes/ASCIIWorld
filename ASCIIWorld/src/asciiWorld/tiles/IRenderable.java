package asciiWorld.tiles;

import org.newdawn.slick.geom.Vector2f;

public interface IRenderable {

	void render(TileSet tiles);
	void renderBatched(TileSet tiles, SpriteBatch spriteBatch, float x, float y, Vector2f scale, float rotation);
	
	IRenderable clone();
}