package asciiWorld;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class FontFactory {
	
	private static final String DEFAULT_FONT_NAME = "Courier New";
	private static final int DEFAULT_FONT_STYLE = Font.BOLD;
	private static final int DEFAULT_FONT_SIZE = 20;
	
	private static FontFactory _instance;
	
	private List<Font> _resourceCache;
	
	private FontFactory() {
		_resourceCache = new ArrayList<Font>();
	}
	
	public static FontFactory get() {
		if (_instance == null) {
			_instance = new FontFactory();
		}
		return _instance;
	}
	
	public UnicodeFont getDefaultFont() throws SlickException {
		return getResource(DEFAULT_FONT_NAME, DEFAULT_FONT_STYLE, DEFAULT_FONT_SIZE);
	}
	
	/**
	 * 
	 * @param name
	 * @return A fresh copy of the requested resource.
	 * @throws Exception
	 */
	public UnicodeFont getResource(String name, int style, int size) throws SlickException {
		// Try to find the font in the cache.
		for (Font ttf : _resourceCache) {
			if ((ttf.getName() == name) && (ttf.getStyle() == style) && (ttf.getSize() == size)) {
				return initializeFont(ttf);
			}
		}
		
		// No font was found, so create it.
		Font ttf = createFont(name, style, size);
		_resourceCache.add(ttf);
		return initializeFont(ttf);
	}
	
	public UnicodeFont getResource(int style, int size) throws SlickException {
		return getResource(DEFAULT_FONT_NAME, style, size);
	}
	
	public UnicodeFont getResource(int size) throws SlickException {
		return getResource(DEFAULT_FONT_NAME, DEFAULT_FONT_STYLE, size);
	}

	/*public String fromXml(Element elem) throws Exception {
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
		return String.format("resources/fonts/%s.xml", name);
	}*/
	
	private UnicodeFont initializeFont(Font ttf) throws SlickException {
		UnicodeFont font = new UnicodeFont(ttf);
		font.getEffects().add(new ColorEffect(java.awt.Color.white));
		font.addAsciiGlyphs();
		font.loadGlyphs();
		return font;
	}

	private Font createFont(String name, int style, int size) {
		return new Font(name, style, size);
	}
}
