package asciiWorld.tiles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.IHasSize;

public class TileSet implements IHasSize {
	
	static final int SCALE_FILTER = Image.FILTER_NEAREST;
	
	String _sourceImagePath;
	Image _sourceImage;
	int _rows;
	int _columns;
	Vector2f _size;
	Boolean _isBatchDraw = false;
	
	public TileSet(String imagePath, int rows, int columns) throws SlickException {
		setSourceImagePath(imagePath);
		setRows(rows);
		setColumns(columns);
	}
	
	public String getSourceImagePath() {
		return _sourceImagePath;
	}
	
	public void setSourceImagePath(String imagePath) throws SlickException {
		_sourceImagePath = imagePath;
		if (_sourceImage != null) {
			_sourceImage.destroy();
		}
		_sourceImage = new Image(_sourceImagePath);
		_sourceImage.setFilter(SCALE_FILTER);
		reset();
	}
	
	public Image getSourceImage() {
		return _sourceImage;
	}
	
	public int getTileCount() {
		return getRows() * getColumns();
	}
	
	public int getRows() {
		return _rows;
	}
	
	public void setRows(int rows) {
		_rows = rows;
		reset();
	}
	
	public int getColumns() {
		return _columns;
	}
	
	public void setColumns(int columns) {
		_columns = columns;
		reset();
	}
	
	public Vector2f getSize() {
		return _size;
	}
	
	public Rectangle getDestinationRectangle(Vector2f position) {
		return new Rectangle(position.x, position.y, _size.x, _size.y);
	}
	
	public Rectangle getSourceRectangle(int tileIndex) {
		int sourceX = (tileIndex % _columns) * (int)_size.x;
		int sourceY = (int)(tileIndex / _columns) * (int)_size.y;
		return new Rectangle(sourceX, sourceY, _size.x, _size.y);
	}
	
	public void scaleImage(float scale) throws SlickException {
		_sourceImage = _sourceImage.getScaledCopy(scale);
		reset();
	}
	
	public void startBatchDraw() {
		if (!_isBatchDraw) {
			_isBatchDraw = true;
			_sourceImage.startUse();
		}
	}
	
	public void endBatchDraw() {
		if (_isBatchDraw) {
			_isBatchDraw = false;
			_sourceImage.endUse();
		}
	}
	
	public void draw(int tileIndex, Vector2f position, Color color, float rotation, TransformEffect transform) {
		if (!_isBatchDraw) {
			_sourceImage.setRotation(rotation);
		}

		Rectangle destinationRectangle = getDestinationRectangle(position);
		Rectangle sourceRectangle = getSourceRectangle(tileIndex);
		
		Boolean hFlip = (transform == TransformEffect.FlipHorizontally) || (transform == TransformEffect.FlipBoth);
		Boolean vFlip = (transform == TransformEffect.FlipVertically) || (transform == TransformEffect.FlipBoth);
		
		//float destX1 = hFlip ? (destinationRectangle.getMaxX() + 1) : destinationRectangle.getMinX();
		//float destX2 = hFlip ? destinationRectangle.getMinX() : (destinationRectangle.getMaxX() + 1);
		//float destY1 = vFlip ? (destinationRectangle.getMaxY() + 1) : destinationRectangle.getMinY();
		//float destY2 = vFlip ? destinationRectangle.getMinY() : (destinationRectangle.getMaxY() + 1);
		
		float destX1 = destinationRectangle.getMinX();
		float destX2 = destinationRectangle.getMaxX();
		float destY1 = destinationRectangle.getMinY();
		float destY2 = destinationRectangle.getMaxY();
		
		//float srcX1 = false ? (sourceRectangle.getMaxX() + 1) : sourceRectangle.getMinX();
		//float srcX2 = false ? sourceRectangle.getMinX() : (sourceRectangle.getMaxX() + 1);
		//float srcY1 = false ? (sourceRectangle.getMaxY() + 1) : sourceRectangle.getMinY();
		//float srcY2 = false ? sourceRectangle.getMinY() : (sourceRectangle.getMaxY() + 1);
		
		float srcX1 = hFlip ? sourceRectangle.getMaxX() : sourceRectangle.getMinX();
		float srcX2 = hFlip ? (sourceRectangle.getMinX()) : (sourceRectangle.getMaxX());
		float srcY1 = vFlip ? sourceRectangle.getMaxY() : sourceRectangle.getMinY();
		float srcY2 = vFlip ? (sourceRectangle.getMinY()) : (sourceRectangle.getMaxY());
		
		if (_isBatchDraw) {
			_sourceImage.drawEmbedded(destX1, destY1, destX2, destY2, srcX1, srcY1, srcX2, srcY2, color);
		} else {
			_sourceImage.draw(destX1, destY1, destX2, destY2, srcX1, srcY1, srcX2, srcY2, color);
		}

		if (!_isBatchDraw) {
			_sourceImage.setRotation(0);
		}
	}
	
	public void draw(int tileIndex, Vector2f position, Color color, float rotation) {
		draw(tileIndex, position, color, rotation, TransformEffect.None);
	}
	
	public void draw(int tileIndex, Vector2f position, Color color) {
		draw(tileIndex, position, color, 0.0f);
	}
	
	public void draw(int tileIndex, Vector2f position) {
		draw(tileIndex, position, Color.white);
	}
	
	public void drawString(String text, Vector2f position, Color color) {
		Vector2f copy = position.copy();
		for (int index = 0; index < text.length(); index++) {
			draw((int)text.charAt(index), copy, color);
			copy.x += _size.x;
		}
	}

	public void drawString(String text, Vector2f position) {
		drawString(text, position, Color.white);
	}
	
	public static TileSet load(String path) throws JDOMException, IOException, SlickException {
		return fromXml((Element)new SAXBuilder().build(new File(path)).getRootElement());
	}
	
	public static TileSet fromXml(Element elem) throws DataConversionException, SlickException {
		String sourceImagePath = elem.getAttribute("source").getValue();
		int rows = elem.getAttribute("rows").getIntValue();
		int columns = elem.getAttribute("columns").getIntValue();
		return new TileSet(sourceImagePath, rows, columns);
	}
	
	public void save(String path) throws IOException {
		Element elem = toXml();
		Document doc = new Document(elem);
		doc.setRootElement(elem);
		
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		xmlOutput.output(doc, new FileWriter(path));
		
		System.out.println(String.format("Saved TileSet to '%s.'", path));
	}
	
	public Element toXml() {
		Element elem = new Element("TileSet");
		elem.setAttribute("source", getSourceImagePath());
		elem.setAttribute("rows", Integer.toString(getRows()));
		elem.setAttribute("columns", Integer.toString(getColumns()));
		return elem;
	}
	
	private void reset() {
		if ((_rows != 0) && (_columns != 0)) {
			_size = new Vector2f(_sourceImage.getWidth() / _columns, _sourceImage.getHeight() / _rows);
			//getSourceImage().setCenterOfRotation(_tileSize.x / 2, _tileSize.y / 2);
		}
	}
}