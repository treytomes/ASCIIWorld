package asciiWorld.entities;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.Direction;
import asciiWorld.IHasPosition;
import asciiWorld.IHasRangeOfVision;
import asciiWorld.MathHelper;
import asciiWorld.Vector3f;
import asciiWorld.chunks.Chunk;
import asciiWorld.tiles.Tile;
import asciiWorld.tiles.TileSet;

public class Entity implements IHasPosition, IHasRangeOfVision {
	
	public static final float MOVEMENT_STEP = 8.0f;
	
	private static final int DEFAULT_AGILITY = 1;
	private static final int DEFAULT_PERCEPTION = 10;
	private static final float DEFAULT_WEIGHT = 0;
	
	private static final float MODIFIER_AGILITY = 15.0f;
	
	private Chunk _chunk;
	
	private Tile _tile;
	
	private Direction _direction;
	private Vector3f _position;
	private Vector3f _moveFromPosition;
	private Vector3f _moveToPosition;
	private float _movementWeight;
	
	private String _name;
	
	private int _agility;
	private int _perception;
	private float _weight;
	
	/**
	 * The container that contains this entity.
	 */
	private InventoryContainer _container;
	
	/**
	 * The inventory contained by this entity.
	 */
	private InventoryContainer _inventory;
	
	private List<EntityComponent> _components;
	
	public Entity() {
		_chunk = null;
		_tile = null;
		_position = new Vector3f();
		_moveFromPosition = new Vector3f();
		_moveToPosition = new Vector3f();
		_movementWeight = 0.0f;
		_direction = Direction.South;
		
		setName("");
		
		setAgility(DEFAULT_AGILITY);
		setPerception(DEFAULT_PERCEPTION);
		setWeight(DEFAULT_WEIGHT);
		
		setContainer(null);
		_inventory = new InventoryContainer(this);

		setComponents(new ArrayList<EntityComponent>());
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public InventoryContainer getContainer() {
		return _container;
	}
	
	public void setContainer(InventoryContainer value) {
		_container = value;
	}
	
	public InventoryContainer getInventory() {
		return _inventory;
	}
	
	public List<EntityComponent> getComponents() {
		return _components;
	}
	
	public void setComponents(List<EntityComponent> value) {
		_components = value;
	}
	
	public Chunk getChunk() {
		return _chunk;
	}
	
	public void setChunk(Chunk chunk) {
		if ((_chunk != null) && (_chunk != chunk)) {
			Chunk removedFromChunk = _chunk;
			for (EntityComponent component : getComponents()) {
				component.beforeRemovedFromChunk(removedFromChunk);
			}
			
			if (_chunk.containsEntity(this)) {
				_chunk.removeEntity(this);
			}
			_chunk = null;
			
			for (EntityComponent component : getComponents()) {
				component.afterRemovedFromChunk(removedFromChunk);
			}
		}
		
		if ((_chunk == null) && (chunk != null)) {
			for (EntityComponent component : getComponents()) {
				component.beforeAddedToChunk(chunk);
			}
			
			_chunk = chunk;
			if (!_chunk.containsEntity(this)) {
				_chunk.addEntity(this);
			}

			for (EntityComponent component : getComponents()) {
				component.afterAddedToChunk(chunk);
			}
		}
	}
	
	public Tile getTile() {
		return _tile;
	}
	
	public void setTile(Tile value) {
		_tile = value;
	}
	
	public Vector3f getPosition() {
		return _position;
	}
	
	public Vector2f getOccupiedChunkPoint() {
		return translatePositionToPoint(_moveToPosition); // getPosition());
	}
	
	public Boolean isMoving() {
		return !_moveFromPosition.equals(_moveToPosition);
	}
	
	public Direction getDirection() {
		return _direction;
	}
	
	public String getName() {
		return _name;
	}
	
	public void setName(String value) {
		_name = value;
	}
	
	public int getAgility() {
		return _agility;
	}
	
	public void setAgility(int value) {
		_agility = value;
	}
	
	public int getPerception() {
		return _perception;
	}
	
	public void setPerception(int value) {
		_perception = value;
	}
	
	/**
	 * 
	 * @return The weight of this entity by itself, i.e. without counting it's inventory.
	 */
	public float getBaseWeight() {
		return _weight;
	}
	
	/**
	 * 
	 * @return The weight of this entity, including it's inventory.
	 */
	public float getTotalWeight() {
		float weight = getBaseWeight();
		for (Entity item : getInventory()) {
			weight += item.getTotalWeight();
		}
		return weight;
	}
	
	public void setWeight(float value) {
		_weight = value;
	}
	
	public float getBaseMovementSpeed() {
		return getAgility() / MODIFIER_AGILITY;
	}
	
	public float getMovementSpeed() {
		float movementSpeed = getBaseMovementSpeed();
		Chunk myChunk = getChunk();
		float myLayer = getPosition().z;
		
		if (myChunk != null) {
			Vector2f chunkPoint = getOccupiedChunkPoint();
			Entity entity = myChunk.getEntityAt(chunkPoint, myLayer - 1);
			if (entity != null) {
				movementSpeed *= entity.getTile().getFriction();
			}
		}
		
		return movementSpeed;
	}
	
	public float getBaseRangeOfVision() {
		return getPerception();
	}
	
	public float getRangeOfVision() {
		return getBaseRangeOfVision();
	}
	
	public void moveTo(Vector2f chunkPoint, float layer) {
		_position = translatePointToPosition(chunkPoint, layer);
		_moveFromPosition = _position.clone();
		_moveToPosition = _position.clone();
	}
	
	public void moveTo(Vector3f chunkPoint) {
		moveTo(chunkPoint.toVector2f(), chunkPoint.z);
	}
	
	public void move(Direction direction) {
		if (!isMoving()) {
			Chunk cachedChunk = getChunk();
			
			_direction = direction;
			
			Vector3f pendingMoveToPosition = _moveToPosition.clone().add(_direction.toVector3f().multiply(MOVEMENT_STEP));
			
			// Check for a collision:
			if (cachedChunk != null) {
				Vector2f pendingChunkPoint = translatePositionToPoint(pendingMoveToPosition);
				
				Entity entity = getChunk().getEntityAt(pendingChunkPoint, getPosition().z);
				if (entity != null) {
					entity.collidedWith(this);
					pendingMoveToPosition = _moveToPosition;
				}
			}
			
			_moveToPosition = pendingMoveToPosition;
		}
	}
	
	public void use(Vector3f targetChunkPoint) {
		for (EntityComponent component : getComponents()) {
			component.use(getContainer().getOwner(), targetChunkPoint);
		}
	}
	
	public void collidedWith(Entity collidedWithEntity) {
		for (EntityComponent component : getComponents()) {
			component.collided(collidedWithEntity);
		}
	}
	
	public void touch() {
		if (getChunk() == null) {
			return;
		}
		
		Vector2f chunkPoint = getOccupiedChunkPoint();
		Vector2f directionVector = getDirection().toVector2f();
		chunkPoint.x += directionVector.x;
		chunkPoint.y += directionVector.y;
		
		Entity entity = getChunk().getEntityAt(chunkPoint, getPosition().z);
		if (entity != null) {
			entity.touched(this);
		}
	}
	
	public void touched(Entity touchedByEntity) {
		for (EntityComponent component : getComponents()) {
			component.touched(touchedByEntity);
		}
	}
	
	public void update(GameContainer container, StateBasedGame game, int deltaTime) {
		if (getContainer() == null) {
			if (isMoving()) {
				_position = MathHelper.smoothStep(_moveFromPosition, _moveToPosition, _movementWeight);
				_movementWeight += (deltaTime / 20.0f) * getMovementSpeed();
				if (_movementWeight > 1.0f) {
					_position = _moveToPosition.clone();
					_moveFromPosition = _moveToPosition.clone();
					_movementWeight = 0.0f;
				}
			}
		}
		
		_tile.update(deltaTime);
		
		for (EntityComponent component : getComponents()) {
			component.update(container, game, deltaTime);
		}
	}
	
	public void render(TileSet tiles) {
		getTile().render(tiles, getPosition().toVector2f());
	}

	public static Vector2f translatePositionToPoint(Vector3f position) {
		return translatePositionToPoint(position.toVector2f());
	}

	public static Vector2f translatePositionToPoint(Vector2f position) {
		return new Vector2f((float)Math.floor(position.x / MOVEMENT_STEP), (float)Math.floor(position.y / MOVEMENT_STEP));
	}

	public static Vector3f translatePointToPosition(Vector2f chunkPoint, float layer) {
		return new Vector3f(chunkPoint.x * MOVEMENT_STEP, chunkPoint.y * MOVEMENT_STEP, layer);
	}
}