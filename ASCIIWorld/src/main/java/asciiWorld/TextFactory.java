package asciiWorld;

import java.util.HashMap;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Element;

import asciiWorld.io.FileHelper;

/**
 * Use this to load text resources.
 * 
 * @author ttomes
 *
 */
public class TextFactory {
	
	private static TextFactory _instance = null;
	
	private Map<String, String> _resourceCache;
	
	private TextFactory() {
		_resourceCache = new HashMap<String, String>();
	}
	
	public static TextFactory get() {
		if (_instance == null) {
			_instance = new TextFactory();
		}
		return _instance;
	}
	
	/**
	 * 
	 * @param name
	 * @return A fresh copy of the requested resource.
	 * @throws Exception
	 */
	public String getResource(String name) throws Exception {
		try {
			if (!_resourceCache.containsKey(name)) {
				_resourceCache.put(name, FileHelper.readToEnd(getPathForResource(name)));
			}
		} catch (Exception e) {
			throw new Exception(String.format("The text resource '%s' does not exist.", name), e);
		}
		return new String(_resourceCache.get(name));
	}

	public String fromXml(Element elem) throws Exception {
		if (elem.getName() != "TextResource") {
			throw new Exception("The input element is not a TextResource.");
		}
		Attribute nameAttribute = elem.getAttribute("name");
		if (nameAttribute == null) {
			throw new Exception("The 'name' attribute is missing.");
		}
		return getResource(nameAttribute.getValue());
	}
	
	private String getPathForResource(String name) {
		return String.format("resources/text/%s.txt", name);
	}
}
