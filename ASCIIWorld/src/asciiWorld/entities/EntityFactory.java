package asciiWorld.entities;

import org.newdawn.slick.geom.Vector2f;

import asciiWorld.chunks.Chunk;
import asciiWorld.tiles.TileFactory;

public class EntityFactory {

	public static Entity createWaterEntity(Chunk chunk, int x, int y)
			throws Exception {
		Entity entity = new Entity();
		entity.setName("Water");
		entity.setTile(TileFactory.get().getResource("water"));
		entity.moveTo(new Vector2f(x, y), Chunk.LAYER_GROUND);
		chunk.addEntity(entity);
		return entity;
	}

	public static Entity createSandEntity(Chunk chunk, int x, int y)
			throws Exception {
		Entity entity = new Entity();
		entity.setName("Sand");
		entity.setTile(TileFactory.get().getResource("sand"));
		entity.moveTo(new Vector2f(x, y), Chunk.LAYER_GROUND);
		chunk.addEntity(entity);
		return entity;
	}

	public static Entity createDirtEntity(Chunk chunk, int x, int y)
			throws Exception {
		Entity entity = new Entity();
		entity.setName("Dirt");
		entity.setTile(TileFactory.get().getResource("dirt"));
		entity.moveTo(new Vector2f(x, y), Chunk.LAYER_GROUND);
		chunk.addEntity(entity);
		return entity;
	}

	public static Entity createGrassEntity(Chunk chunk, int x, int y)
			throws Exception {
		Entity entity = new Entity();
		entity.setName("Grass");
		entity.setTile(TileFactory.get().getResource("grass"));
		entity.moveTo(new Vector2f(x, y), Chunk.LAYER_GROUND);
		chunk.addEntity(entity);
		return entity;
	}

	public static Entity createStoneEntity(Chunk chunk, int x, int y)
			throws Exception {
		Entity entity = new Entity();
		entity.setName("Stone");
		entity.setTile(TileFactory.get().getResource("stone"));
		entity.moveTo(new Vector2f(x, y), Chunk.LAYER_OBJECT);
		chunk.addEntity(entity);
		return entity;
	}

	public static Entity createCaveEntranceEntity(Chunk chunk, int x, int y)
			throws Exception {
		Entity entity = new Entity();
		entity.setName("Cave Entrance");
		entity.setTile(TileFactory.get().getResource("caveEntrance"));
		entity.moveTo(new Vector2f(x, y), Chunk.LAYER_OBJECT);
		chunk.addEntity(entity);
		return entity;
	}

	public static Entity createTreeEntity(Chunk chunk, int x, int y)
			throws Exception {
		Entity entity = new Entity();
		entity.setName("Tree");
		entity.setTile(TileFactory.get().getResource("tree"));
		entity.getComponents().add(new CanBePickedUpComponent(entity));
		entity.getComponents().add(new PlaceableComponent(entity));
		entity.moveTo(new Vector2f(x, y), Chunk.LAYER_OBJECT);
		chunk.addEntity(entity);
		return entity;
	}
}
