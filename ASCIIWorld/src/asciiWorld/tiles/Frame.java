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

import asciiWorld.Convert;

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
	
	public void render(TileSet tiles) { 
		if (_backgroundColor.a > 0) {
			tiles.draw(TILEINDEX_SOLID, _backgroundColor);
		}
		
		if (_foregroundColor.a > 0) {
			tiles.draw(_tileIndex, _foregroundColor);
		}
	}

	@Override
	public void renderBatched(TileSet tiles, SpriteBatch spriteBatch, float x, float y, Vector2f scale, float rotation) {
		if (_backgroundColor.a > 0) {
			tiles.drawBatched(spriteBatch, x, y, scale, rotation, TILEINDEX_SOLID, _backgroundColor);
		}
		
		if (_foregroundColor.a > 0) {
			tiles.drawBatched(spriteBatch, x, y, scale, rotation, _tileIndex, _foregroundColor);
		}
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
		Color backgroundColor = Convert.stringToColor(elem.getAttribute("backgroundColor").getValue());
		Color foregroundColor = Convert.stringToColor(elem.getAttribute("foregroundColor").getValue());
		
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
		elem.setAttribute("backgroundColor", Convert.colorToString(getBackgroundColor()));
		elem.setAttribute("foregroundColor", Convert.colorToString(getForegroundColor()));
		elem.setAttribute("tileIndex", Integer.toString(getTileIndex()));
		return elem;
	}
}
