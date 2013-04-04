package asciiWorld.entities;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Element;

public class EntityComponentTemplate {
	
	private static final String PACKAGE_PATH = "asciiWorld.entities.%sComponent";
	
	private static Map<String, Constructor<?>> _cachedConstructors; 
	
	private String _className;
	private Map<String, String> _properties;
	
	public EntityComponentTemplate(Element elem) throws Exception {
		_properties = new HashMap<>();
		_className = elem.getAttributeValue("name");
		loadProperties(elem.getChild("Properties"));
	}
	
	private void loadProperties(Element propertiesElem) {
		if (propertiesElem != null) {
			for (Element propertyElem : propertiesElem.getChildren("Property")) {
				String propertyName = propertyElem.getAttributeValue("name");
				String propertyValue = propertyElem.getAttributeValue("value");
				_properties.put(propertyName, propertyValue);
			}
		}
	}
	
	public String getPropertyValue(String propertyName) {
		return _properties.get(propertyName);
	}
	
	public EntityComponent createInstance(Entity entity) {
		try {
			EntityComponent component = EntityComponent.class.cast(getConstructorFor(_className).newInstance(entity));
			for (String propertyName : _properties.keySet()) {
				component.setProperty(propertyName, getPropertyValue(propertyName));
			}
			return component;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static Constructor<?> getConstructorFor(String className) throws Exception {
		if (_cachedConstructors == null) {
			_cachedConstructors = new HashMap<String, Constructor<?>>();
		}
		if (!_cachedConstructors.containsKey(className)) {
			_cachedConstructors.put(className, Class.forName(String.format(PACKAGE_PATH, className)).getConstructor(Entity.class));
		}
		return _cachedConstructors.get(className);
	}
}
