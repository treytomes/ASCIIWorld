package asciiWorld.entities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.Camera;
import asciiWorld.Direction;
import asciiWorld.IHasPosition;
import asciiWorld.IHasRangeOfVision;
import asciiWorld.animations.TileSwingAnimation;
import asciiWorld.chunks.Chunk;
import asciiWorld.math.MathHelper;
import asciiWorld.math.Vector3f;
import asciiWorld.tiles.Tile;
import asciiWorld.tiles.TileFactory;

public class Entity implements IHasPosition, IHasRangeOfVision {
	
	public static final float MOVEMENT_STEP = 8.0f;
	
	private static final int DEFAULT_AGILITY = 1;
	private static final int DEFAULT_STRENGTH = 2;
	private static final int DEFAULT_PERCEPTION = 10;
	private static final float DEFAULT_WEIGHT = 1;
	
	private static final float MODIFIER_AGILITY = 15.0f;
	private static final float SPEED_TIME_DIVISOR = 20.0f;
	
	private Chunk _chunk;
	
	private Tile _tile;
	private List<TileSwingAnimation> _animations;
	
	private Direction _direction;
	private Vector3f _position;
	private Vector3f _moveFromPosition;
	private Vector3f _moveToPosition;
	private float _movementWeight;
	
	private String _name;
	
	private int _agility;
	private int _perception;
	private int _strength;
	private float _weight;
	
	/**
	 * The container that contains this entity.
	 */
	private InventoryContainer _container;
	
	/**
	 * The inventory contained by this entity.
	 */
	private InventoryContainer _inventory;
	
	/**
	 * This is the item that this entity is currently using.
	 */
	private Entity _activeItem;
	
	private List<EntityComponent> _components;
	
	public static Entity load(String path) throws Exception {
		return fromXml((Element)new SAXBuilder().build(new File(path)).getRootElement());
	}
	
	public static Entity fromXml(Element elem) throws Exception {
		Entity newEntity = new Entity();
		newEntity.setName(elem.getAttributeValue("name"));
		newEntity.setTile(TileFactory.get().getResource(elem.getAttributeValue("tile")));
		
		Element componentsElem = elem.getChild("Components");
		if (componentsElem != null) {
			List<Element> componentElems = componentsElem.getChildren("Component");
			for (Element componentElem : componentElems) {
				newEntity.getComponents().add(EntityComponent.fromXml(newEntity, componentElem));
			}
		}
		
		return newEntity;
	}
	
	public Entity() {
		_chunk = null;
		_tile = null;
		_animations = new ArrayList<TileSwingAnimation>();
		_position = new Vector3f();
		_moveFromPosition = new Vector3f();
		_moveToPosition = new Vector3f();
		_movementWeight = 0.0f;
		_direction = Direction.South;
		
		setName("");
		
		setAgility(DEFAULT_AGILITY);
		setStrength(DEFAULT_STRENGTH);
		setPerception(DEFAULT_PERCEPTION);
		setWeight(DEFAULT_WEIGHT);
		
		setContainer(null);
		try {
			_inventory = new InventoryContainer(this);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to initialize the inventory container.");
		}
		setActiveItem(null);

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
	
	public void addAnimation(TileSwingAnimation value) {
		_animations.add(value);
	}
	
	public Vector3f getPosition() {
		return _position;
	}
	
	public Vector2f getOccupiedChunkPoint() {
		return Camera.translatePositionToPoint(_moveToPosition); // getPosition());
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
	
	public int getStrength() {
		return _strength;
	}
	
	public void setStrength(int value) {
		_strength = value;
	}
	
	public int getAttackSpeed() {
		return getStrength() * getAgility();
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
	
	public Entity getActiveItem() {
		if (!getInventory().contains(_activeItem)) {
			_activeItem = null;
		}
		return _activeItem;
	}
	
	/**
	 * The item will be added to this entity's inventory if it isn't already there.
	 * 
	 * @param item
	 */
	public void setActiveItem(Entity item) {
		if (item != null) {
			if (!getInventory().contains(item)) {
				try {
					getInventory().add(item);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Unable to set the active item.");
				}
			}
		}
		_activeItem = item;
	}
	
	public Boolean hasActiveItem() {
		return getActiveItem() != null;
	}
	
	public void useActiveItem(Vector3f targetChunkPoint) {
		if (hasActiveItem()) {
			addAnimation(TileSwingAnimation.createUseActiveItemAnimation(this, targetChunkPoint));
			getActiveItem().use(targetChunkPoint);
		}
	}
	
	public void moveTo(Vector2f chunkPoint, float layer) {
		_position = Camera.translatePointToPosition(chunkPoint, layer);
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
				Vector2f pendingChunkPoint = Camera.translatePositionToPoint(pendingMoveToPosition);
				
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
				_movementWeight += (deltaTime / SPEED_TIME_DIVISOR) * getMovementSpeed();
				if (_movementWeight > 1.0f) {
					_position = _moveToPosition.clone();
					_moveFromPosition = _moveToPosition.clone();
					_movementWeight = 0.0f;
				}
			}
		}
		
		_tile.update(deltaTime);
		
		List<TileSwingAnimation> deadAnimations = new ArrayList<TileSwingAnimation>();
		for (TileSwingAnimation animation : _animations) {
			animation.update(deltaTime);
			if (!animation.isAlive()) {
				deadAnimations.add(animation);
			}
		}
		while (!deadAnimations.isEmpty()) {
			_animations.remove(deadAnimations.remove(0));
		}
		
		for (EntityComponent component : getComponents()) {
			component.update(container, game, deltaTime);
		}
	}
	
	public void render(Graphics g) {
		getTile().render(g, getPosition().toVector2f());
		
		for (TileSwingAnimation animation : _animations) {
			animation.render(g);
		}
	}
}