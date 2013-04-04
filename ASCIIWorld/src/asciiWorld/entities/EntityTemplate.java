package asciiWorld.entities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import asciiWorld.tiles.TileFactory;

public class EntityTemplate {
	private String _name;
	private String _tileResourceName;
	private List<EntityComponentTemplate> _components;
	private List<String> _inventory;
	private Map<String, String> _properties;
	
	public EntityTemplate(String path) throws Exception {
		this((Element)new SAXBuilder().build(new File(path)).getRootElement());
	}
	
	public EntityTemplate(Element elem) throws Exception {
		_components = new ArrayList<EntityComponentTemplate>();
		_inventory = new ArrayList<String>();
		_properties = new HashMap<String, String>();
		
		_name = elem.getAttributeValue("name");
		_tileResourceName = elem.getAttributeValue("tile");
		
		loadComponents(elem.getChild("Components"));
		loadInventory(elem.getChild("Inventory"));
		loadProperties(elem.getChild("Properties"));
	}
	
	public String getName() {
		return _name;
	}
	
	public String getPropertyValue(String propertyName) {
		return _properties.get(propertyName);
	}
	
	public Entity createInstance() throws Exception {
		Entity entity = new Entity();
		entity.setName(getName());
		entity.setTile(TileFactory.get().getResource(_tileResourceName));
		
		for (EntityComponentTemplate component : _components) {
			entity.getComponents().add(component.createInstance(entity));
		}
		
		for (String itemName : _inventory) {
			entity.getInventory().add(EntityFactory.get().getResource(itemName));
		}
		
		for (String propertyName : _properties.keySet()) {
			entity.setProperty(propertyName, getPropertyValue(propertyName));
		}
		
		return entity;
	}
	
	private void loadComponents(Element componentsElem) throws Exception {
		if (componentsElem != null) {
			List<Element> componentElems = componentsElem.getChildren("Component");
			for (Element componentElem : componentElems) {
				_components.add(new EntityComponentTemplate(componentElem));
			}
		}
	}
	
	private void loadInventory(Element inventoryElem) {
		if (inventoryElem != null) {
			List<Element> itemElems = inventoryElem.getChildren("Item");
			for (Element itemElem : itemElems) {
				_inventory.add(itemElem.getAttributeValue("name"));
			}
		}
	}
	
	private void loadProperties(Element propertiesElem) {
		if (propertiesElem != null) {
			List<Element> propertyElems = propertiesElem.getChildren("Property");
			for (Element propertyElem : propertyElems) {
				String propertyName = propertyElem.getAttributeValue("name");
				String propertyValue = propertyElem.getAttributeValue("value");
				_properties.put(propertyName, propertyValue);
			}
		}
	}

}
