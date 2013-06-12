package asciiWorld;

import java.io.File;
import java.io.IOException;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class XmlHelper {
	
	public static Element load(String path) throws JDOMException, IOException {
		return (Element)new SAXBuilder().build(new File(path)).getRootElement();
	}
	
	public static void assertName(Element elem, String name) throws Exception {
		if (elem.getName() != name) {
			throw new Exception(String.format("The input element is not a %s.", name));
		}
	}
	
	public static String getAttributeValueOrDefault(Element elem, String attributeName, String defaultValue) {
		Attribute attr = elem.getAttribute(attributeName);
		if (attr == null) {
			return defaultValue;
		} else {
			return attr.getValue();
		}
	}
	
	public static String getChildTextOrDefault(Element elem, String childName, String defaultValue) {
		Element child = elem.getChild(childName);
		if (child == null) {
			return defaultValue;
		} else {
			return child.getText();
		}
	}

	public static String getPropertyValueOrDefault(Element elem, String propertyName, String defaultValue) {
		String value = getAttributeValueOrDefault(elem, propertyName, null);
		if (value != null) {
			return value;
		}
		
		return getChildTextOrDefault(elem, String.format("%s.%s", elem.getName(), propertyName), defaultValue);
	}
}