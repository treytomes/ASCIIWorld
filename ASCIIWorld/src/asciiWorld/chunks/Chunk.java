package asciiWorld.chunks;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.Camera;
import asciiWorld.entities.Entity;
import asciiWorld.math.RandomFactory;
import asciiWorld.math.Vector3f;
import asciiWorld.tiles.TileSet;

public class Chunk {
	
	public static final int LAYER_GROUND = 0;
	public static final int LAYER_OBJECT = 1;
	public static final int LAYER_SKY = 2;
	
	public static final int WIDTH = 128;
	public static final int HEIGHT = 128;
	
	private List<Entity> _entities;
	
	public Chunk() {
		_entities = new ArrayList<Entity>();
	}

	public void clearEntities() {
		List<Entity> children = getEntities();
		while (children.size() != 0) {
			removeEntity(children.get(0));
		}
	}
	
	public List<Entity> getEntities() {
		return _entities;
	}
	
	public List<Entity> getEntities(float layer) {
		layer = (float)Math.floor(layer);
		List<Entity> zEntities = new ArrayList<Entity>();
		for (Entity entity : _entities) {
			if (entity.getPosition().z == layer) {
				zEntities.add(entity);
			}
		}
		return zEntities;
	}

	public Entity getEntityAt(Vector2f chunkPoint, float layer) {
		Vector2f searchPoint = new Vector2f((int)chunkPoint.x, (int)chunkPoint.y);
		for (Entity entity : _entities) {
			if (entity.getPosition().z == layer) {
				Vector2f entityChunkPoint = entity.getOccupiedChunkPoint();
				if ((entityChunkPoint.x == searchPoint.x) && (entityChunkPoint.y == searchPoint.y)) {
					return entity;
				}
			}
		}
		return null;
	}
	
	public Boolean containsEntity(Entity entity) {
		return _entities.contains(entity);
	}
	
	public void addEntity(Entity entity) {
		if (!containsEntity(entity)) {
			if (entity.getChunk() != null) {
				entity.getChunk().removeEntity(entity);
			}
			
			_entities.add(entity);
			entity.setChunk(this);
		}
	}
	
	public void removeEntity(Entity entity) {
		if (containsEntity(entity)) {
			_entities.remove(entity);
			entity.setChunk(null);
		}
	}

	public Boolean isSpaceOccupied(Vector3f chunkPoint)
	{
		return getEntityAt(chunkPoint.toVector2f(), chunkPoint.z) != null;
	}

	public Vector3f findSpawnPoint(int layer) throws Exception
	{
		for (int y = 0; y < Chunk.WIDTH; y++)
		{
			for (int x = 0; x < Chunk.HEIGHT; x++)
			{
				Vector3f chunkPoint = new Vector3f(x, y, layer);
				if (!isSpaceOccupied(chunkPoint))
				{
					return chunkPoint;
				}
			}
		}
		throw new Exception("Unable to find a valid spawn point.");
	}

	public Vector3f findRandomSpawnPoint(int layer) throws Exception
	{
		while (true)
		{
			Vector3f chunkPoint = new Vector3f(RandomFactory.get().nextInt(0, WIDTH), RandomFactory.get().nextInt(0, HEIGHT), layer);
			if (!isSpaceOccupied(chunkPoint))
			{
				return chunkPoint;
			}
		}
	}
	
	public void update(GameContainer container, StateBasedGame game, int deltaTime) {
		for (int index = 0; index < getEntities().size(); index++) {
			getEntities().get(index).update(container, game, deltaTime);
		}
	}
	
	public void render(Graphics g, Camera camera, TileSet tiles) {
		Vector2f position = camera.getPosition().toVector2f();
		float rangeOfVision = camera.getRangeOfVision();
		
		//tiles.startBatchDraw();
		render(g, position, tiles, LAYER_GROUND, rangeOfVision);
		render(g, position, tiles, LAYER_OBJECT, rangeOfVision);
		render(g, position, tiles, LAYER_SKY, rangeOfVision);
		//tiles.endBatchDraw();
	}
	
	private void render(Graphics g,Vector2f center, TileSet tiles, int layerIndex, float rangeOfVision) {
		for (Entity entity : getEntities()) {
			Vector3f position = entity.getPosition();
			if (position.z == layerIndex) {
				if (position.getDistance(center) <= Entity.MOVEMENT_STEP * rangeOfVision) {
					entity.render(g, tiles);
				}
			}
		}
	}
}