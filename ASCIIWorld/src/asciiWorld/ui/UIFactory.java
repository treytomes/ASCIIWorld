package asciiWorld.ui;

import org.jdom2.Element;

import asciiWorld.XmlHelper;

public class UIFactory {
	
	private static UIFactory _instance;
	
	private UIFactory() {
	}
	
	public static UIFactory get() {
		if (_instance == null) {
			_instance = new UIFactory();
		}
		return _instance;
	}
	
	/**
	 * 
	 * @param name
	 * @return A fresh copy of the requested resource.
	 * @throws Exception
	 */
	public FrameworkElement getResource(String name) throws Exception {
		Element elem = XmlHelper.load(getPathForResource(name));
		return fromXml(elem);
	}

	public FrameworkElement fromXml(Element elem) throws Exception {
		switch (elem.getName()) {
		case "Button":
			return Button.fromXml(elem);
		case "StackPanel":
			return StackPanel.fromXml(elem);
		case "Element":
			return getResource(elem.getAttributeValue("source"));
		default:
			return null;
		}
	}
	
	private String getPathForResource(String name) {
		return String.format("resources/ui/%s.xml", name);
	}
}
