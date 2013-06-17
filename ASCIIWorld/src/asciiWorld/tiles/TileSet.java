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
import org.lwjgl.opengl.GL11;
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
	
	int[] _displayLists;
	
	public TileSet(String imagePath, int rows, int columns) throws SlickException {
		setSourceImagePath(imagePath);
		setRows(rows);
		setColumns(columns);
		
		_displayLists = new int[getTileCount()];
		for (int index = 0; index < getTileCount(); index++) {
		    float x = (index % _columns) * _size.x;
		    float y = (index / _columns) * _size.y;

			float texLeft = x / _sourceImage.getWidth();
			float texTop = y / _sourceImage.getHeight();
			float texRight = (x + _size.x) / _sourceImage.getWidth();
			float texBottom = (y + _size.y) / _sourceImage.getHeight();

			_displayLists[index] = GL11.glGenLists(1);
			GL11.glNewList(_displayLists[index], GL11.GL_COMPILE);
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, _sourceImage.getTexture().getTextureID());
			GL11.glBegin(GL11.GL_QUADS);
			
			GL11.glTexCoord2d(texLeft, texTop);
			GL11.glVertex2d(0, 0);

			GL11.glTexCoord2d(texRight, texTop);
		    GL11.glVertex2d(_size.x, 0);

			GL11.glTexCoord2d(texRight, texBottom);
		    GL11.glVertex2d(_size.x, _size.y);

			GL11.glTexCoord2d(texLeft, texBottom);
		    GL11.glVertex2d(0, _size.y);
			
			GL11.glEnd();
			GL11.glEndList();
		}
	}

	public String getName() {
		String path = getSourceImagePath();
		path = path.substring(path.lastIndexOf("/") + 1);
		path = path.substring(0, path.lastIndexOf("."));
		return path;
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
	
	public void draw(int tileIndex, Color color) { //, Vector2f position, Color color, float rotation, TransformEffect transform) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		color.bind();
		GL11.glCallList(_displayLists[tileIndex]);
		
		/*_sourceImage.setRotation(rotation);

		Rectangle destinationRectangle = getDestinationRectangle(position);
		Rectangle sourceRectangle = getSourceRectangle(tileIndex);
		
		Boolean hFlip = (transform == TransformEffect.FlipHorizontally) || (transform == TransformEffect.FlipBoth);
		Boolean vFlip = (transform == TransformEffect.FlipVertically) || (transform == TransformEffect.FlipBoth);
		
		float destX1 = destinationRectangle.getMinX();
		float destX2 = destinationRectangle.getMaxX();
		float destY1 = destinationRectangle.getMinY();
		float destY2 = destinationRectangle.getMaxY();
		
		float srcX1 = hFlip ? sourceRectangle.getMaxX() : sourceRectangle.getMinX();
		float srcX2 = hFlip ? (sourceRectangle.getMinX()) : (sourceRectangle.getMaxX());
		float srcY1 = vFlip ? sourceRectangle.getMaxY() : sourceRectangle.getMinY();
		float srcY2 = vFlip ? (sourceRectangle.getMinY()) : (sourceRectangle.getMaxY());
		
		_sourceImage.draw(destX1, destY1, destX2, destY2, srcX1, srcY1, srcX2, srcY2, color);

		_sourceImage.setRotation(0);*/
	}
	
	/*public void draw(int tileIndex, Vector2f position, Color color, float rotation) {
		draw(tileIndex, position, color, rotation, TransformEffect.None);
	}
	
	/*public void draw(int tileIndex, Vector2f position, Color color) {
		draw(tileIndex, position, color, 0.0f);
	}
	
	public void draw(int tileIndex, Vector2f position) {
		draw(tileIndex, position, Color.white);
	}*/
	
	public void drawString(String text, Vector2f position, Color color) {
		GL11.glPushMatrix();
		//Vector2f copy = position.copy();
		for (int index = 0; index < text.length(); index++) {
			draw((int)text.charAt(index), color);
			GL11.glTranslated(_size.x, 0, 0);
			//copy.x += _size.x;
		}
		GL11.glPopMatrix();
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