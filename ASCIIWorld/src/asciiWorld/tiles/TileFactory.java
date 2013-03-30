package asciiWorld.tiles;

import java.util.HashMap;
import java.util.Map;

public class TileFactory {
	
	private static TileFactory _instance;
	
	private Map<String, Tile> _resourceCache;
	
	private TileFactory() {
		_resourceCache = new HashMap<String, Tile>();
	}
	
	public static TileFactory get() {
		if (_instance == null) {
			_instance = new TileFactory();
		}
		return _instance;
	}
	
	/**
	 * 
	 * @param name
	 * @return A fresh copy of the requested tile.
	 * @throws Exception
	 */
	public Tile getResource(String name) throws Exception {
		try {
			if (!_resourceCache.containsKey(name)) {
				_resourceCache.put(name, Tile.load(getPathForResource(name)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(String.format("The tile '%s' does not exist.", name), e);
		}
		return _resourceCache.get(name).clone();
	}
	
	private String getPathForResource(String name) {
		return String.format("resources/tiles/%s.xml", name);
	}
}
