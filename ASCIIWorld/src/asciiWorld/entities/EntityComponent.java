package asciiWorld.entities;

import java.io.File;
import java.lang.reflect.Method;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.Convert;
import asciiWorld.MethodIterator;
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
		
		Element propertiesElem = elem.getChild("Properties");
		if (propertiesElem != null) {
			for (Element propertyElem : propertiesElem.getChildren("Property")) {
				String propertyName = propertyElem.getAttributeValue("name");
				String propertyValue = propertyElem.getAttributeValue("value");
				component.setProperty(propertyName, propertyValue);
			}
		}
		
		return component;
	}
	
	public EntityComponent(Entity owner) {
		_owner = owner;
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws Exception {
		Method method = MethodIterator.getMethods(getClass()).withName(String.format("set%s", propertyName)).first();
		
		if (method != null) {
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length != 1) {
				throw new Exception("The property setter must have 1, and only 1, parameter.");
			} else {
				method.invoke(this, Convert.changeType(propertyValue, parameterTypes[0]));
				return;
			}
		} else {
			throw new Exception(String.format("I do not understand this property name: %s", propertyName));
		}
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