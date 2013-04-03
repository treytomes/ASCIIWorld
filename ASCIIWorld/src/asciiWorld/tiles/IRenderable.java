package asciiWorld.tiles;

import org.newdawn.slick.geom.Vector2f;

public interface IRenderable {

	void render(TileSet tiles, Vector2f position, float rotation, TransformEffect transform);
	void render(TileSet tiles, Vector2f position, float rotation);
	void render(TileSet tiles, Vector2f position);
	void render(TileSet tiles);
	
	IRenderable clone();
}