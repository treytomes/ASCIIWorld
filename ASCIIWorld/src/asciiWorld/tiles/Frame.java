package asciiWorld.tiles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

public class Frame implements ITile, IRenderable {
	
	public static final int TILEINDEX_SOLID = 219;
	
	private static final int DEFAULT_TILEINDEX = TILEINDEX_SOLID;
	private static final Color DEFAULT_FOREGROUNDCOLOR = Color.white;
	private static final Color DEFAULT_BACKGROUNDCOLOR = Color.black;
	
	private Color _backgroundColor;
	private Color _foregroundColor;
	private int _tileIndex;
	
	public Frame(int tileIndex, Color foregroundColor, Color backgroundColor) {
		setBackgroundColor(backgroundColor);
		setForegroundColor(foregroundColor);
		setTileIndex(tileIndex);
	}
	
	public Frame(int tileIndex, Color foregroundColor) {
		this(tileIndex, foregroundColor, DEFAULT_BACKGROUNDCOLOR);
	}
	
	public Frame(int tileIndex) {
		this(tileIndex, DEFAULT_FOREGROUNDCOLOR, DEFAULT_BACKGROUNDCOLOR);
	}
	
	public Frame() {
		this(DEFAULT_TILEINDEX, DEFAULT_FOREGROUNDCOLOR, DEFAULT_BACKGROUNDCOLOR);
	}
	
	protected Frame(Frame clone) {
		this(clone.getTileIndex(), clone.getForegroundColor(), clone.getBackgroundColor());
	}
	
	@Override
	public Color getBackgroundColor() {
		return _backgroundColor;
	}
	
	public void setBackgroundColor(Color value) {
		_backgroundColor = value;
	}

	@Override
	public Color getForegroundColor() {
		return _foregroundColor;
	}
	
	public void setForegroundColor(Color value) {
		_foregroundColor = value;
	}

	@Override
	public int getTileIndex() {
		return _tileIndex;
	}
	
	public void setTileIndex(int value) {
		_tileIndex = value;
	}
	
	public void render(TileSet tiles, Vector2f position, float rotation, TransformEffect transform) {
		/*if (tile.getBackgroundColor().getAlpha() > 0) {
			Vector2f tileSize = getTileSize();
			Shape backFill = new Rectangle(0, 0, tileSize.x + 1, tileSize.y + 1)
				.transform(Transform.createRotateTransform((float)Math.toRadians(rotation), tileSize.x / 2.0f, tileSize.y / 2.0f))
				.transform(Transform.createTranslateTransform(position.x, position.y));
			g.setColor(tile.getBackgroundColor());
			g.fill(backFill);
		}*/
		tiles.draw(TILEINDEX_SOLID, position, getBackgroundColor(), rotation, transform);
		tiles.draw(getTileIndex(), position, getForegroundColor(), rotation, transform);
	}
	
	public void render(TileSet tiles, Vector2f position, float rotation) {
		render(tiles, position, rotation, TransformEffect.None);
	}
	
	public void render(TileSet tiles, Vector2f position) {
		render(tiles, position, 0);
	}
	
	public void render(TileSet tiles) {
		render(tiles, Vector2f.zero());
	}
	
	public Frame clone() {
		return new Frame(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Frame) {
			Frame otherFrame = Frame.class.cast(obj);
			return
					(otherFrame.getBackgroundColor() == getBackgroundColor()) &&
					(otherFrame.getForegroundColor() == getForegroundColor()) &&
					(otherFrame.getTileIndex() == getTileIndex());
		} else {
			return false;
		}
	}
	
	public static Frame load(String path) throws JDOMException, IOException {
		return fromXml((Element)new SAXBuilder().build(new File(path)).getRootElement());
	}
	
	public static Frame fromXml(Element elem) {
		Color backgroundColor = stringToColor(elem.getAttribute("backgroundColor").getValue());
		Color foregroundColor = stringToColor(elem.getAttribute("foregroundColor").getValue());
		
		String tileIndexText = elem.getAttribute("tileIndex").getValue();
		if ((tileIndexText.charAt(0) == '\'') && (tileIndexText.length() == 3) && (tileIndexText.charAt(2) == '\'')) {
			int tileIndex = (int)tileIndexText.charAt(1);
			return new Frame(tileIndex, foregroundColor, backgroundColor);
		} else {
			int tileIndex = Integer.parseInt(elem.getAttribute("tileIndex").getValue());
			return new Frame(tileIndex, foregroundColor, backgroundColor);
		}
	}
	
	public void save(String path) throws IOException {
		Element elem = toXml();
		Document doc = new Document(elem);
		doc.setRootElement(elem);
		
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		xmlOutput.output(doc, new FileWriter(path));
		
		System.out.println(String.format("Saved Frame to '%s.'", path));
	}
	
	public Element toXml() {
		Element elem = new Element("Frame");
		elem.setAttribute("backgroundColor", colorToString(getBackgroundColor()));
		elem.setAttribute("foregroundColor", colorToString(getForegroundColor()));
		elem.setAttribute("tileIndex", Integer.toString(getTileIndex()));
		return elem;
	}
	
	private static String colorToString(final Color color) {
		return String.format("%s%s%s%s",
				Integer.toHexString(color.getAlpha()),
				Integer.toHexString(color.getRed()),
				Integer.toHexString(color.getGreen()),
				Integer.toHexString(color.getBlue()));
	}
	
	private static Color stringToColor(final String text) {
		return new Color(
				Integer.parseInt(text.substring(2, 4), 16),
				Integer.parseInt(text.substring(4, 6), 16),
				Integer.parseInt(text.substring(6, 8), 16),
				Integer.parseInt(text.substring(0, 2), 16));
	}
}
