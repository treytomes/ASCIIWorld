package asciiWorld.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Border extends ContentControl {
	
	private static final float DEFAULT_LINE_WIDTH = 1.0f;

	private Rectangle _bounds; // can be either Rectangle or RoundedRectangle
	private Color _color;
	private Boolean _filled;
	private float _lineWidth;
	
	public Border(Rectangle bounds, Color color, Boolean filled, float lineWidth) throws Exception {
		super();
		
		setBounds(bounds);
		setColor(color);
		setFilled(filled);
		setLineWidth(lineWidth);
	}
	
	public Border(Rectangle bounds, Color color, Boolean filled) throws Exception {
		this(bounds, color, filled, DEFAULT_LINE_WIDTH);
	}
	
	public Border(Color color, Boolean filled) throws Exception {
		this(new Rectangle(0, 0, 0, 0), color, filled, DEFAULT_LINE_WIDTH);
	}
	
	public Rectangle getBounds() {
		return _bounds;
	}
	
	public void setBounds(Rectangle value) {
		_bounds = value;
		setContentBounds();
	}
	
	public Color getColor() {
		return _color;
	}
	
	public void setColor(Color value) {
		_color = value;
	}
	
	public Boolean getFilled() {
		return _filled;
	}
	
	public void setFilled(Boolean value) {
		_filled = value;
	}
	
	public float getLineWidth() {
		return _lineWidth;
	}
	
	public void setLineWidth(float value) {
		_lineWidth = value;
	}
	
	@Override
	public void render(Graphics g) {
		Rectangle previousWorldClip = setTransform(g);
		//g.setWorldClip(getBounds());
		
		float originalLineWidth = g.getLineWidth();
		g.setLineWidth(getLineWidth());
		
		g.setColor(getColor());
		if (getFilled()) {
			g.fill(getBounds());
		} else {
			g.draw(getBounds());
		}
		
		g.setLineWidth(originalLineWidth);
		
		//g.setWorldClip(null);
		clearTransform(g, previousWorldClip);

		super.render(g);
	}
	
	@Override
	public void moveTo(Vector2f position) {
		getBounds().setLocation(position);
	}
	
	protected Boolean contains(Vector2f point) {
		return getBounds().contains(point.x, point.y);
	}
}