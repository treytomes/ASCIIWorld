package asciiWorld.tiles;

import java.util.HashMap;
import java.util.Map;

import org.jdom2.Element;

public class TileSetFactory {
	
	private static final String DEFAULT_TILESET = "OEM437";
	
	private static TileSetFactory _instance;
	
	private Map<String, TileSet> _resourceCache;
	
	private TileSetFactory() {
		_resourceCache = new HashMap<String, TileSet>();
	}
	
	public static TileSetFactory get() {
		if (_instance == null) {
			_instance = new TileSetFactory();
		}
		return _instance;
	}
	
	public TileSet getDefaultTileSet() throws Exception {
		return getResource(DEFAULT_TILESET);
	}
	
	/**
	 * 
	 * @param name
	 * @return The requested resource.
	 * @throws Exception
	 */
	public TileSet getResource(String name) throws Exception {
		if (!_resourceCache.containsKey(name)) {
			_resourceCache.put(name, TileSet.load(getPathForResource(name)));
		}
		return _resourceCache.get(name);
	}

	public TileSet fromXml(Element elem) throws Exception {
		return TileSet.fromXml(elem);
	}
	
	private String getPathForResource(String name) {
		return String.format("resources/tileSets/%s.xml", name);
	}
}
