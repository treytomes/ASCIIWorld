package asciiWorld;

import org.newdawn.slick.geom.Vector2f;

import asciiWorld.entities.CanSpeakComponent;
import asciiWorld.entities.Entity;
import asciiWorld.tiles.TileFactory;
import asciiWorld.ui.RootVisualPanel;

public class ChunkFactory {
	
	public static Chunk generateVoid() {
		return new Chunk();
	}
	
	public static Chunk generateGrassyPlain() throws Exception {
		Chunk chunk = generateVoid();
		
		for (int x = 0; x < 128; x++) {
			for (int y = 0; y < 128; y++) {
				Entity entity = new Entity();
				entity.setTile(TileFactory.get().getResource("grass"));
				entity.moveTo(new Vector2f(x, y), Chunk.LAYER_GROUND);
				chunk.addEntity(entity);
			}
		}
		
		return chunk;
	}
	
	public static Chunk generateDesert() throws Exception {
		Chunk chunk = generateVoid();
		for (int x = 0; x < 128; x++) {
			for (int y = 0; y < 128; y++) {
				Entity entity = new Entity();
				entity.setTile(TileFactory.get().getResource("sand"));
				entity.moveTo(new Vector2f(x, y), Chunk.LAYER_GROUND);
				chunk.addEntity(entity);
				
			}
		}
		
		return chunk;
	}
	
	public static Chunk generateCollisionTest(RootVisualPanel uiRoot) throws Exception {
		Chunk chunk = generateGrassyPlain();
		
		Entity entity = new Entity();
		entity.setTile(TileFactory.get().getResource("tree"));
		entity.moveTo(new Vector2f(10, 10), Chunk.LAYER_OBJECT);
		entity.getComponents().add(new CanSpeakComponent(entity, uiRoot, "I've been touched!"));
		chunk.addEntity(entity);
		
		return chunk;
	}
}