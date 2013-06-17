package asciiWorld.tiles;

public interface IRenderable {

	void render(TileSet tiles);
	
	IRenderable clone();
}