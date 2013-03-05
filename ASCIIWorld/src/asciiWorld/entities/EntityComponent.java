package asciiWorld.entities;

import java.io.File;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.chunks.Chunk;
import asciiWorld.math.Vector3f;

public class EntityComponent {
	
	private static final String PACKAGE_PATH = "asciiWorld.entities.%sComponent";
	private Entity _owner;
	
	public static EntityComponent load(Entity entity, String path) throws Exception {
		return fromXml(entity, (Element)new SAXBuilder().build(new File(path)).getRootElement());
	}
	
	public static EntityComponent fromXml(Entity entity, Element elem) throws Exception {
		Class<?> componentClass = Class.forName(String.format(PACKAGE_PATH, elem.getAttributeValue("name")));
		EntityComponent component = (EntityComponent)componentClass.getConstructor(Entity.class).newInstance(entity);
		return component;
	}
	
	public EntityComponent(Entity owner) {
		_owner = owner;
	}
	
	public Entity getOwner() {
		return _owner;
	}
	
	public void beforeAddedToChunk(Chunk chunk) { }
	
	public void afterAddedToChunk(Chunk chunk) { }

	public void beforeRemovedFromChunk(Chunk chunk) { }
	
	public void afterRemovedFromChunk(Chunk chunk) { }
	
	/**
	 * Called before this entity is added to an inventory.
	 * @param chunk
	 */
	public void beforeAddedToInventory(InventoryContainer container) { }
	
	public void afterAddedToInventory(InventoryContainer container) { }
	
	/**
	 * Called before this entity is removed from an inventory.
	 * @param chunk
	 */
	public void beforeRemovedFromInventory(InventoryContainer container) { }
	
	public void afterRemovedFromInventory(InventoryContainer container) { }
	
	public void update(GameContainer container, StateBasedGame game, int deltaTime) { }
	
	public void use(Entity source, Vector3f targetChunkPoint) { }
	
	public void touched(Entity touchedByEntity) { }
	
	public void collided(Entity collidedWithEntity) { }
	
	/**
	 * Called when an item is added to this entity's inventory.
	 *  
	 * @param item
	 */
	public void itemWasGained(Entity item) { }
	
	/**
	 * Called when an item is removed from this entity's inventory.
	 * @param item
	 */
	public void itemWasLost(Entity item) { }
}