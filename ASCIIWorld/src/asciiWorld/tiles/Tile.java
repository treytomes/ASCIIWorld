package asciiWorld.tiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public class Tile {
	
	private static final int MS_PER_SEC = 1000;
	
	private int _framesPerSecond;
	private int _msecPerFrame;
	private IRenderable[] _frames = null;
	private int _animationIndex;
	private float _friction;
	private List<TileTransform> _transformations = null;
	private TileSet _tileSet;
	private Vector2f _tileCenter;
	
	private int _currentTime;
	private int _lastUpdateTime;
	
	private String _tileSetName;
	
	public Tile() {
		_animationIndex = 0;
		_lastUpdateTime = 0;
		_tileSet = null;
		_tileSetName = null;
		_tileCenter = null;
		_transformations = new ArrayList<TileTransform>();
	}
	
	public Tile(Tile clone) {
		this();
		
		//setTileSet(clone.getTileSet());
		setFramesPerSecond(clone.getFramesPerSecond());
		setFrames(clone.copyFrames());
		setFriction(clone.getFriction());
		setTransformations(clone.copyTransformations());

		_tileSetName = clone._tileSetName;
	}
	
	public Vector2f getCenter() {
		if (_tileCenter == null) {
			getTileSet();
		}
		return _tileCenter;
	}
	
	public TileSet getTileSet() {
		if (_tileSet == null) {
			if (_tileSetName != null) {
				try {
					setTileSet(TileSetFactory.get().getResource(_tileSetName));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return _tileSet;
	}
	
	public void setTileSet(TileSet value) {
		_tileSet = value;

		Vector2f tileSize = getTileSet().getSize();
		_tileCenter = new Vector2f(tileSize.x / 2.0f, tileSize.x / 2.0f);

		if (_tileSet != null) {
			_tileSetName = _tileSet.getName();
		} else {
			_tileSetName = null;
		}
	}
	
	public int getFramesPerSecond() {
		return _framesPerSecond;
	}
	
	public void setFramesPerSecond(int value) {
		_framesPerSecond = value;
		_msecPerFrame = MS_PER_SEC / _framesPerSecond;
	}
	
	public IRenderable[] getFrames() {
		return _frames;
	}
	
	public void setFrames(IRenderable[] value) {
		_frames = value;
	}
	
	public List<TileTransform> getTransformations() {
		return _transformations;
	}
	
	public void setTransformations(List<TileTransform> value) {
		_transformations = value;
	}
	
	public float getFriction() {
		return _friction;
	}
	
	public void setFriction(float value) {
		_friction = value;
	}
	
	public float getRotation() {
		float rotation = 0.0f;
		if (getTransformations() != null) {
			for (TileTransform transform : getTransformations()) {
				rotation += transform.getRotation();
			}
		}
		return rotation;
	}
	
	public TransformEffect getEffect() {
		TransformEffect effect = TransformEffect.None;
		if (getTransformations() != null) {
			for (TileTransform transform : getTransformations()) {
				switch (transform.getEffect()) {
				case FlipHorizontally:
					if (effect == TransformEffect.None) {
						effect = TransformEffect.FlipHorizontally;
					} else if (effect == TransformEffect.FlipVertically) {
						effect = TransformEffect.FlipBoth;
					}
					break;
				case FlipVertically:
					if (effect == TransformEffect.None) {
						effect = TransformEffect.FlipVertically;
					} else if (effect == TransformEffect.FlipHorizontally) {
						effect = TransformEffect.FlipBoth;
					}
					break;
				case FlipBoth:
					effect = TransformEffect.FlipBoth;
					break;
				default:
					break;
				}
			}
		}
		return effect;
	}
	
	public Vector2f getScale() {
		switch (getEffect()) {
		case FlipHorizontally:
			return new Vector2f(-1, 1);
		case FlipVertically:
			return new Vector2f(1, -1);
		case FlipBoth:
			return Vector2f.one().scale(-1.0f);
		case None:
			return Vector2f.one();
		}
		return null;
	}
	
	public IRenderable getCurrentFrame() {
		return _frames[_animationIndex];
	}
	
	public void update(int deltaTime) {
		_currentTime += deltaTime;
		if (_currentTime >= (_lastUpdateTime + _msecPerFrame)) {
			_animationIndex = (_animationIndex + 1) % _frames.length;
			_lastUpdateTime = _currentTime;
		}
	}

	public void render(Graphics g, Vector2f position) {
		render(g, position.x, position.y);
	}

	public void render(Graphics g, float x, float y) {
		if (_tileCenter == null) {
			getTileSet();
		}
		
		g.pushTransform();
		
		g.translate(_tileCenter.x, _tileCenter.y);
		if (getEffect() != TransformEffect.None) {
			Vector2f scale = getScale();
			g.scale(scale.x, scale.y);
		}
		
		g.translate(x - _tileCenter.x, y - _tileCenter.y);
		
		g.rotate(_tileCenter.x, _tileCenter.y, getRotation());

		getCurrentFrame().render(getTileSet());
		g.popTransform();
	}
	
	public void render(Graphics g) {
		render(g, 0, 0);
	}
	
	public Tile clone() {
		return new Tile(this);
	}
	
	public static Tile load(String path) throws Exception {
		return fromXml((Element)new SAXBuilder().build(new File(path)).getRootElement());
	}
	
	public static Tile fromXml(Element elem) throws Exception {
		Tile tile = new Tile();
		
		tile._tileSetName = elem.getAttributeValue("tileSet");
		//tile.setTileSet(TileSetFactory.get().getResource(tileSetName));
		
		Element framesElement = elem.getChild("Frames");
		
		tile.setFramesPerSecond(framesElement.getAttribute("framesPerSecond").getIntValue());
		
		tile.setFrames(parseFrames(framesElement.getChildren()));
		
		Element propertiesElement = elem.getChild("Properties");
		if (propertiesElement != null) {
			tile.parseProperties(propertiesElement.getChildren());
		}
		
		Element transformationsElement = elem.getChild("Transformations");
		if (transformationsElement != null) {
			tile.setTransformations(parseTransformations(transformationsElement.getChildren()));
		}
		
		return tile;
	}
	
	private static IRenderable[] parseFrames(final List<Element> frameElements) {
		IRenderable[] frames = new IRenderable[frameElements.size()];
		for (int index = 0; index < frameElements.size(); index++) {
			if (frameElements.get(index).getName() == "Frame") {
				frames[index] = Frame.fromXml(frameElements.get(index));
			} else if (frameElements.get(index).getName() == "FrameStack") {
				frames[index] = FrameStack.fromXml(frameElements.get(index));
			}
		}
		return frames;
	}
	
	private void parseProperties(final List<Element> propertyElements) throws Exception {
		for (Element propertyElement : propertyElements) {
			parseProperty(propertyElement);
		}
	}
	
	private void parseProperty(final Element propertyElement) throws Exception {
		switch (propertyElement.getAttribute("name").getValue()) {
		case "Friction":
			setFriction(propertyElement.getAttribute("value").getFloatValue());
			break;
		default:
			throw new Exception(String.format("The property '%s' does not exist.", propertyElement.getAttribute("name").getValue()));
		}
	}
	
	private static List<TileTransform> parseTransformations(final List<Element> transformationElements) throws DataConversionException {
		List<TileTransform> transformations = new ArrayList<TileTransform>(); // TileTransform[transformationElements.size()];
		for (int index = 0; index < transformationElements.size(); index++) {
			transformations.add(TileTransform.fromXml(transformationElements.get(index)));
		}
		return transformations;
	}
	
	/*public void save(String path) throws Exception {
		Element elem = toXml();
		Document doc = new Document(elem);
		doc.setRootElement(elem);
		
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		xmlOutput.output(doc, new FileWriter(path));
		
		System.out.println(String.format("Saved Tile to '%s.'", path));
	}*/
	
	/*public Element toXml() throws Exception {
		Element tileElement = new Element("Tile");
		tileElement.setAttribute("tileSet", getTileSet().getName());
		
		Element framesElement = new Element("Frames");
		framesElement.setAttribute(new Attribute("framesPerSecond", Integer.toString(getFramesPerSecond())));
		for (IRenderable frame : getFrames()) {
			framesElement.addContent(frame.toXml());
		}
		tileElement.addContent(framesElement);
		
		Element propertiesElement = new Element("Properties");
		propertiesElement.addContent(propertyToXml("Friction"));

		return tileElement;
	}*/
	
	/*private Element propertyToXml(String propertyName) throws Exception {
		Element elem = new Element("Property");
		switch (propertyName) {
		case "Friction":
			elem.setAttribute("name", "Friction");
			elem.setAttribute("value", Float.toString(getFriction()));
			break;
		default:
			throw new Exception(String.format("The property '%s' does not exist.", propertyName));
		}
		return elem;
	}*/

	/**
	 * 
	 * @return A deep copy of the frames array.
	 */
	protected IRenderable[] copyFrames() {
		IRenderable[] myFrames = getFrames();
		IRenderable[] newFrames = new IRenderable[myFrames.length];
		for (int index = 0; index < myFrames.length; index++) {
			newFrames[index] = myFrames[index].clone();
		}
		return newFrames;
	}

	/**
	 * 
	 * @return A deep copy of the transformations array.
	 */
	protected List<TileTransform> copyTransformations() {
		List<TileTransform> myTransformations = getTransformations();
		if (myTransformations == null) {
			return null;
		} else {
			List<TileTransform> newTransformations = new ArrayList<TileTransform>(); // TileTransform[myTransformations.length];
			for (int index = 0; index < myTransformations.size(); index++) {
				newTransformations.add(myTransformations.get(index).clone());
			}
			return newTransformations;
		}
	}
}