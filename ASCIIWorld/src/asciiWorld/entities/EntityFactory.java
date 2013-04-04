package asciiWorld.entities;

import java.util.HashMap;
import java.util.Map;

//import org.jdom2.Element;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.chunks.Chunk;

public class EntityFactory {
	
	private static EntityFactory _instance = null;
	
	private Map<String, EntityTemplate> _cachedResources;
	
	private EntityFactory() {
		_cachedResources = new HashMap<String, EntityTemplate>();
	}
	
	public static EntityFactory get() {
		if (_instance == null) {
			_instance = new EntityFactory();
		}
		return _instance;
	}

	public Entity createWaterEntity(Chunk chunk, int x, int y)
			throws Exception {
		return createEntity("water", chunk, x, y, Chunk.LAYER_GROUND);
	}

	public Entity createSandEntity(Chunk chunk, int x, int y)
			throws Exception {
		return createEntity("sand", chunk, x, y, Chunk.LAYER_GROUND);
	}

	public Entity createDirtEntity(Chunk chunk, int x, int y)
			throws Exception {
		return createEntity("dirt", chunk, x, y, Chunk.LAYER_GROUND);
	}

	public Entity createGrassEntity(Chunk chunk, int x, int y)
			throws Exception {
		return createEntity("grass", chunk, x, y, Chunk.LAYER_GROUND);
	}

	public Entity createStoneEntity(Chunk chunk, int x, int y)
			throws Exception {
		return createEntity("stone", chunk, x, y);
	}

	public Entity createCaveEntranceEntity(Chunk chunk, int x, int y)
			throws Exception {
		return createEntity("caveEntrance", chunk, x, y);
	}

	public Entity createTreeEntity(Chunk chunk, int x, int y)
			throws Exception {
		return createEntity("tree", chunk, x, y);
	}
	
	public Entity createEntity(String name, Chunk chunk, int x, int y) {
		return createEntity(name, chunk, x, y, Chunk.LAYER_OBJECT);
	}
	
	public Entity createEntity(String name, Chunk chunk, int x, int y, int z) {
		Entity entity = getResource(name);
		entity.moveTo(new Vector2f(x, y), z);
		chunk.addEntity(entity);
		return entity;
	}
	
	/**
	 * 
	 * @param name
	 * @return A fresh copy of the requested resource.
	 * @throws Exception
	 */
	public Entity getResource(String name) {
		try {
			if (!_cachedResources.containsKey(name)) {
				_cachedResources.put(name, new EntityTemplate(getPathForResource(name)));
			}
			
			return _cachedResources.get(name).createInstance();
			//return Entity.load(getPathForResource(name));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	public Entity fromXml(Element elem) throws Exception {
		return Entity.fromXml(elem);
	}
	*/
	
	private String getPathForResource(String name) {
		return String.format("resources/entities/%s.xml", name);
	}
}
