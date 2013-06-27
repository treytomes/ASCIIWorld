package asciiWorld.tiles;

public interface IRenderable {

	void render(TileSet tiles);
	void renderBatched(TileSet tiles, SpriteBatch spriteBatch, float x, float y);
	
	IRenderable clone();
}