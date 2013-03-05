package asciiWorld.chunks;

import java.io.PrintStream;

import org.newdawn.slick.geom.Vector2f;

import asciiWorld.entities.CanBePickedUpComponent;
import asciiWorld.entities.CanBePushedComponent;
import asciiWorld.entities.CanSpeakComponent;
import asciiWorld.entities.Entity;
import asciiWorld.math.RandomFactory;
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
		
		Entity entity = null;
		
		chunk.removeEntity(chunk.getEntityAt(new Vector2f(5, 5), Chunk.LAYER_GROUND));
		chunk.removeEntity(chunk.getEntityAt(new Vector2f(6, 5), Chunk.LAYER_OBJECT));
		chunk.removeEntity(chunk.getEntityAt(new Vector2f(7, 5), Chunk.LAYER_OBJECT));
		
		entity = new Entity();
		entity.setName("Water");
		entity.setTile(TileFactory.get().getResource("water"));
		entity.moveTo(new Vector2f(5, 5), Chunk.LAYER_GROUND);
		chunk.addEntity(entity);
		
		entity = new Entity();
		entity.setName("Wood Log");
		entity.setTile(TileFactory.get().getResource("woodLog"));
		entity.moveTo(new Vector2f(6, 5), Chunk.LAYER_OBJECT);
		chunk.addEntity(entity);
		
		entity = new Entity();
		entity.setName("Wooden Sword");
		entity.setTile(TileFactory.get().getResource("woodenSword"));
		entity.moveTo(new Vector2f(7, 5), Chunk.LAYER_OBJECT);
		chunk.addEntity(entity);
		
		
		entity = new Entity();
		entity.setName("Tree");
		entity.setTile(TileFactory.get().getResource("tree"));
		entity.moveTo(new Vector2f(10, 10), Chunk.LAYER_OBJECT);
		entity.getComponents().add(new CanSpeakComponent(entity, uiRoot, "I've been touched!"));
		chunk.addEntity(entity);
		
		entity = new Entity();
		entity.setName("Boulder");
		entity.setTile(TileFactory.get().getResource("boulder"));
		entity.moveTo(new Vector2f(15, 15), Chunk.LAYER_OBJECT);
		entity.getComponents().add(new CanBePushedComponent(entity));
		entity.getComponents().add(new CanBePickedUpComponent(entity));
		chunk.addEntity(entity);
		
		return chunk;
	}
	
	public static Chunk generateOverworld(PrintStream logStream) throws Exception {
		Chunk chunk = new Chunk();
		long seed = RandomFactory.get().nextInt(0, Integer.MAX_VALUE);
		return new PerlinOverworldChunkGenerator().generate(chunk, seed, logStream);
	}
}