package asciiWorld;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.entities.Entity;
import asciiWorld.tiles.TileSet;

public class Chunk {
	
	public static final int LAYER_GROUND = 0;
	public static final int LAYER_OBJECT = 1;
	public static final int LAYER_SKY = 2;
	
	private List<Entity> _entities;
	
	public Chunk() {
		_entities = new ArrayList<Entity>();
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
		for (Entity entity : _entities) {
			if (entity.getPosition().z == layer) {
				Vector2f entityChunkPoint = entity.getChunkPoint();
				if ((entityChunkPoint.x == chunkPoint.x) && (entityChunkPoint.y == chunkPoint.y)) {
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
	
	public void update(GameContainer container, StateBasedGame game, int deltaTime) {
		for (Entity entity : _entities) {
			entity.update(container, game, deltaTime);
		}
	}
	
	public void render(IHasPosition focus, TileSet tiles) {
		Vector2f position = focus.getPosition().toVector2f();
		
		tiles.startBatchDraw();
		render(position, tiles, LAYER_GROUND);
		render(position, tiles, LAYER_OBJECT);
		render(position, tiles, LAYER_SKY);
		tiles.endBatchDraw();
	}
	
	private void render(Vector2f center, TileSet tiles, int layerIndex) {
		for (Entity entity : getEntities()) {
			Vector3f position = entity.getPosition();
			if (position.z == layerIndex) {
				if (position.getDistance(center) <= Entity.MOVEMENT_STEP * 32) {
					entity.render(tiles);
				}
			}
		}
	}
}