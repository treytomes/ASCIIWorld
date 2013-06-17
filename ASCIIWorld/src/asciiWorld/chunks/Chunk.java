package asciiWorld.chunks;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.Camera;
import asciiWorld.entities.Entity;
import asciiWorld.entities.EntityCamera;
import asciiWorld.lighting.FrameBufferObject;
import asciiWorld.lighting.Light;
import asciiWorld.math.RandomFactory;
import asciiWorld.math.Vector3f;

public class Chunk {
	
	public static final int LAYER_GROUND = 0;
	public static final int LAYER_OBJECT = 1;
	public static final int LAYER_SKY = 2;
	public static final int LAYERS_COUNT = 3;
	
	public static final int WIDTH = 128;
	public static final int HEIGHT = 128;

	private static final Color AMBIENT_LIGHT_COLOR = new Color(0.0f, 0.0f, 0.0f, 1.0f);

	private List<Entity> _entities;
	private List<Light> _lights;
	private Color _ambientLightColor;
	private FrameBufferObject _framebuffer;
	
	private Entity[][][] _searchIndex;
	
	public Chunk() {
		_entities = new ArrayList<Entity>();
		_lights = new ArrayList<Light>();
		_ambientLightColor = AMBIENT_LIGHT_COLOR;
		
		_framebuffer = null;
		_lights.add(new Light(Vector2f.zero(), 200.0f, 1.0f, new Color(1.0f, 1.0f, 1.0f, 1.0f)));
		//_lights.add(new Light(new Vector2f(200, 200), 200.0f, 1.0f, new Color(0.0f, 0.0f, 1.0f, 0.5f)));
		
		resetSearchIndex();
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
	
	public List<Entity> getEntities(int layer) {
		List<Entity> zEntities = new ArrayList<Entity>();
		for (Entity entity : _entities) {
			if (entity.getPosition().z == layer) {
				zEntities.add(entity);
			}
		}
		return zEntities;
	}

	public Entity getEntityAt(Vector2f chunkPoint, int layer) {
		/*
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
		*/
		
		if (containsPoint(chunkPoint, layer)) {
			return _searchIndex[layer][(int)chunkPoint.y][(int)chunkPoint.x];
		} else {
			return null;
		}
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
			
			_searchIndex[entity.getLayer()][(int)entity.getOccupiedChunkPoint().y][(int)entity.getOccupiedChunkPoint().x] = entity;
		}
	}
	
	public void removeEntity(Entity entity) {
		if (containsEntity(entity)) {
			_entities.remove(entity);
			entity.setChunk(null);
			
			_searchIndex[entity.getLayer()][(int)entity.getOccupiedChunkPoint().y][(int)entity.getOccupiedChunkPoint().x] = null;
		}
	}

	public Boolean isSpaceOccupied(Vector3f chunkPoint) {
		return getEntityAt(chunkPoint.toVector2f(), (int)chunkPoint.z) != null;
	}

	public Boolean isSpaceOccupied(Vector2f chunkPoint, int layer) {
		return getEntityAt(chunkPoint, layer) != null;
	}

	public Entity findClosestEntity(Vector3f chunkPoint, double range) {
		Entity targetEntity = null;
		double closestRange = range;
		Vector2f targetChunkPoint = chunkPoint.toVector2f();
		for (Entity entity : getEntities((int)chunkPoint.z)) {
			double distance = entity.getDistanceFromPoint(targetChunkPoint);
			if (distance <= closestRange) {
				targetEntity = entity;
				closestRange = distance;
				if (closestRange == 0) {
					break;
				}
			}
		}
		return targetEntity;
	}
	
	public Vector3f findSpawnPoint(int layer) throws Exception {
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
		List<Entity> entities = getEntities();
		
		for (int index = 0; index < entities.size(); index++) {
			Entity entity = entities.get(index);
			entity.update(container, game, deltaTime);
		}
		
		updateSearchIndex();
	}
	
	private void updateSearchIndex() {
		List<Entity> entities = getEntities();
		resetSearchIndex();
		
		for (int index = 0; index < entities.size(); index++) {
			Entity entity = entities.get(index);
			Vector2f chunkPoint = entity.getOccupiedChunkPoint();
			int layer = entity.getLayer();
			if (containsPoint(chunkPoint, layer)) {
				_searchIndex[layer][(int)chunkPoint.y][(int)chunkPoint.x] = entity;
			}
		}
	}
	
	public boolean containsPoint(Vector2f chunkPoint, int layer) {
		return (layer >= 0) && (layer < LAYERS_COUNT) && containsPoint(chunkPoint);
	}
	
	public boolean containsPoint(Vector2f chunkPoint) {
		return (chunkPoint.x >= 0) && (chunkPoint.x < WIDTH) && (chunkPoint.y >= 0) && (chunkPoint.y < HEIGHT);
	}
	
	public void render(Graphics g, Camera camera) {
		if (_framebuffer == null) {
			try {
				_framebuffer = new FrameBufferObject((int)camera.getViewport().getBounds().getWidth(), (int)camera.getViewport().getBounds().getHeight());
			    GL11.glDepthFunc(GL11.GL_LEQUAL); // use less-than or equal depth testing
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Position our light.
		Vector2f lightPosition = camera.getPosition().toVector2f();
		Vector2f tileSize = getEntities().get(0).getTile().getTileSet().getSize();
		lightPosition.x += tileSize.x / 2.0f;
		lightPosition.y += tileSize.y / 2.0f;
		_lights.get(0).setPosition(lightPosition);
		
		Vector2f focusChunkPoint = Camera.translatePositionToPoint(lightPosition);
		Entity focusEntity = (camera instanceof EntityCamera) ? ((EntityCamera)camera).getFocusEntity() : getEntityAt(focusChunkPoint, Chunk.LAYER_OBJECT);
		
		List<Entity> entitiesInRange = getEntitiesInRange(camera);
		
		//camera.reset(g);
		_framebuffer.enable();
		//camera.apply(g);
		
		// Clear the display.
		GL11.glClearDepth(1.1);
		
		// Render the ambient lighting.
		GL11.glClearColor(_ambientLightColor.r, _ambientLightColor.g, _ambientLightColor.b, _ambientLightColor.a);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
		
		// Clear the alpha channel of the framebuffer to 0.
		GL11.glColorMask(false, false, false, true);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	    
		for (Light light : _lights) {
			// Write new framebuffer alpha.
			GL11.glColorMask(false, false, false, true);
			GL11.glDisable(GL11.GL_BLEND);
		    GL11.glEnable(GL11.GL_DEPTH_TEST);
			light.render(g);
			
		    // Draw shadow geometry.
			GL11.glEnable(GL11.GL_BLEND);
		    GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ZERO);
		    drawShadowGeometry(focusEntity, light, entitiesInRange, LAYER_OBJECT);
			
		    // Draw light color.
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		    GL11.glEnable(GL11.GL_BLEND);
		    GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE);
		    GL11.glColorMask(true, true, true, false);
			
			//render(g, entitiesInRange, LAYER_GROUND);
			//render(g, entitiesInRange, LAYER_OBJECT);
			//render(g, entitiesInRange, LAYER_SKY);
		    
		    //for (Light light2 : _lights) {
			//	light2.render(g);
			//}
		    light.render(g);
		}
		
		//camera.reset(g);
		_framebuffer.disable();
		//camera.apply(g);
		
		// Render the scene.
		GL11.glColorMask(true, true, true, true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	    GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//tiles.startBatchDraw();
		render(g, entitiesInRange, LAYER_GROUND);
		render(g, entitiesInRange, LAYER_OBJECT);
		render(g, entitiesInRange, LAYER_SKY);
		//tiles.endBatchDraw();

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		
		//GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_DST_COLOR);
		
		//GL20.glBlendEquationSeparate(GL14.GL_FUNC_ADD, GL14.GL_FUNC_ADD);
		//GL14.glBlendFuncSeparate(GL11.GL_SRC_COLOR, GL11.GL_DST_COLOR, GL11.GL_ONE, GL11.GL_ZERO);
		
		GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ZERO);
		
		camera.reset(g);
		_framebuffer.render(g);
		camera.apply(g);
		
		//render(g, entitiesInRange, LAYER_GROUND);
		//render(g, entitiesInRange, LAYER_OBJECT);
		//render(g, entitiesInRange, LAYER_SKY);

		//_framebuffer.disable();
		//GL11.glDisable(GL11.GL_DEPTH_TEST);
		//GL11.glDisable(GL11.GL_BLEND);
		//_framebuffer.render(g);
		
		// Reset OpenGL settings for Slick.
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	    GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	private void drawShadowGeometry(Entity focusEntity, Light light, List<Entity> entities, int layerIndex) {
		for (Entity entity : entities) {
			if (entity.getLayer() == layerIndex) {
				if (entity == focusEntity) {
					continue;
				}
				if (!entity.getOccupiedChunkPoint().equals(Camera.translatePositionToPoint(light.getPosition()))) {
					entity.drawShadowGeometry(light);
				}
			}
		}
	}
	
	private void render(Graphics g, List<Entity> entities, int layerIndex) {
		for (Entity entity : entities) {
			Vector3f position = entity.getPosition();
			if (position.z == layerIndex) {
				entity.render(g);
			}
		}
	}
	
	/*
	private float getMaxZ(List<Entity> entities) {
		float maxZ = 0;
		for (Entity entity : entities) {
			float z = entity.getPosition().z;
			if (z > maxZ) {
				maxZ = z;
			}
		}
		return maxZ;
	}
	
	private List<Entity> whereChunkPointEquals(List<Entity> entities, Vector2f chunkPoint) {
		List<Entity> results = new ArrayList<Entity>();
		for (Entity entity : entities) {
			Vector2f occupiedPoint = entity.getOccupiedChunkPoint();
			if (chunkPoint.equals(occupiedPoint)) {
				results.add(entity);
			}
		}
		return results;
	}
	*/
	
	/*
	private Entity getPlayer() {
		for (Entity entity : getEntities()) {
			if (entity.findComponent(asciiWorld.entities.PlayerControlComponent.class) != null) {
				return entity;
			}
		}
		return null;
	}
	*/
	
	private List<Entity> getEntitiesInRange(Camera camera) {
		float rangeOfVision = camera.getRangeOfVision();

		Vector2f focusPosition = camera.getPosition().toVector2f();
		Vector2f tileSize = getEntities().get(0).getTile().getTileSet().getSize();
		focusPosition.x += tileSize.x / 2.0f;
		focusPosition.y += tileSize.y / 2.0f;
		
		Vector2f focusChunkPoint = Camera.translatePositionToPoint(focusPosition);
		//focusChunkPoint.x += 0.5f;
		//focusChunkPoint.y += 0.5f;

		Entity focusEntity = (camera instanceof EntityCamera) ? ((EntityCamera)camera).getFocusEntity() : getEntityAt(focusChunkPoint, Chunk.LAYER_OBJECT);
		
		List<Entity> entities = new ArrayList<Entity>();
		
		for (float angle = 0; angle < 360; angle++) {
			for (float distance = 0; distance <= rangeOfVision; distance++) {
				Vector2f chunkPoint = new Vector2f(
						(float)Math.floor(focusChunkPoint.x + distance * Math.cos(angle * Math.PI / 180)),
						(float)Math.floor(focusChunkPoint.y + distance * Math.sin(angle * Math.PI / 180))
						);
				
				if (!containsPoint(chunkPoint)) {
					break;
				}
				
				Entity groundEntity = getEntityAt(chunkPoint, LAYER_GROUND);
				Entity objectEntity = getEntityAt(chunkPoint, LAYER_OBJECT);
				Entity skyEntity = getEntityAt(chunkPoint, LAYER_SKY);
				
				if ((groundEntity != null) && !entities.contains(groundEntity)) {
					entities.add(groundEntity);
				}
				if (objectEntity != null) {
					if (!entities.contains(objectEntity)) {
						entities.add(objectEntity);
					}
					if (objectEntity != focusEntity) { // the entity at the camera focus cannot obstruct it's own vision
						break; // break on obstruction of vision
					}
				}
				if ((skyEntity != null) && !entities.contains(skyEntity)) {
					entities.add(skyEntity);
				}
			}
		}
		
		/*
		for (Entity entity : getEntities()) {
			Vector3f position = entity.getPosition();
			if (position.getDistance(focus) <= Entity.MOVEMENT_STEP * rangeOfVision) {
				entities.add(entity);
			}
		}
		*/
		
		return entities;
	}
	
	private void resetSearchIndex() {
		_searchIndex = new Entity[LAYERS_COUNT][HEIGHT][WIDTH];
	}
}