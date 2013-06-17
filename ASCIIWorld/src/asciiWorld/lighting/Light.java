package asciiWorld.lighting;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.math.RandomFactory;

public class Light {

	private static final boolean RENDER_SOURCE = false;
	private static final float DEFAULT_SOURCE_RADIUS = 5.0f;
	private static final float DEFAULT_RADIUS = 600.0f;
	private static final float DEFAULT_DEPTH = 0.0f;
	private static final float TWO_PI = (float)Math.PI * 2.0f;

	private static LightShader _shader;

	private Vector2f _position;
	private float _radius;
	//private float _depth; // TODO: Is depth always 0?  Will it ever need to be otherwise?
	private Color _color;
	private float _sourceRadius;
	
	public Light(Vector2f position, float radius, float depth, Color color) {
		setPosition(position);
		_radius = radius;
		//_depth = depth;
		_color = color;

		_sourceRadius = DEFAULT_SOURCE_RADIUS;
	}
	
	public Light(Vector2f position, float radius, float depth) {
		this(position, radius, depth, createRandomColor());
	}
	
	public Light(Vector2f position, float radius) {
		this(position, radius, DEFAULT_DEPTH);
	}
	
	public Light(Vector2f position) {
		this(position, DEFAULT_RADIUS);
	}
	
	public Vector2f getPosition() {
		return _position;
	}
	
	public void setPosition(Vector2f value) {
		_position = value;
	}
	
	public Color getColor() {
		return _color;
	}
	
	public void setColor(Color value) {
		_color = value;
	}
	
	public float getIntensity() {
		return _color.a;
	}

	public void setIntensity(float value) {
		_color.a = value;
	}
	
	public float getRadius() {
		return _radius;
	}
	
	public void setRadius(float value) {
		_radius = value;
	}
	
	public Vector2f outerVector(Vector2f edge, int step) {
		boolean useNegative = (getPosition().x < edge.x);
		Vector2f perpVec = new Vector2f(getPosition().x - edge.x, getPosition().y - edge.y).normalise();
		
		if (step == 1) {
			if (useNegative) {
				perpVec = rotate(perpVec.scale(-_sourceRadius), TWO_PI / 4.0f);
			} else {
				perpVec = rotate(perpVec.scale(_sourceRadius), -TWO_PI / 4.0f);
			}
		} else {
			if (useNegative) {
				perpVec = rotate(perpVec.scale(-_sourceRadius), -TWO_PI / 4.0f);
			} else {
				perpVec = rotate(perpVec, TWO_PI / 4.0f).scale(_sourceRadius);
			}
		}
		
		return new Vector2f(edge.x - (getPosition().x + perpVec.x), edge.y - (getPosition().y + perpVec.y)).normalise().scale(_radius * 10.0f);
	}
	
	public Vector2f innerVector(Vector2f edge, int step) {
		boolean useNegative = (getPosition().x < edge.x);
		Vector2f perpVec = new Vector2f(getPosition().x - edge.x, getPosition().y - edge.y).normalise();
		
		if (step == 1) {
			if (useNegative) {
				perpVec = rotate(perpVec.scale(-_sourceRadius), -TWO_PI / 4.0f);
			} else {
				perpVec = rotate(perpVec, TWO_PI / 4.0f).scale(_sourceRadius);
			}
		} else {
			if (useNegative) {
				perpVec = rotate(perpVec.scale(-_sourceRadius), TWO_PI / 4.0f);
			} else {
				perpVec = rotate(perpVec.scale(_sourceRadius), -TWO_PI / 4.0f);
			}
		}
		
		return new Vector2f(edge.x - (getPosition().x + perpVec.x), edge.y - (getPosition().y + perpVec.y)).normalise().scale(_radius * 10.0f);
	}
	
	/**
	 * Draw a circle representing the light's position.
	 * @param g
	 */
	private void renderSource(Graphics g) {
		g.setColor(_color);
		g.fillOval(getPosition().x - _sourceRadius, getPosition().y - _sourceRadius, _sourceRadius * 2, _sourceRadius * 2);
	}
	
	public void render(Graphics g) {
		LightShader shader = getShader();
		shader.enable();
		shader.setState(_color);

		g.pushTransform();
		g.translate(getPosition().x, getPosition().y);
		g.scale(_radius, _radius);
		g.fillRect(-1, -1, 2, 2);
		g.popTransform();
        
		shader.disable();

		if (RENDER_SOURCE) {
			renderSource(g);
		}
	}
	
	private static Vector2f rotate(Vector2f vector, float angle) {
		float ca = (float)Math.cos(angle);
		float sa = (float)Math.sin(angle);
		return new Vector2f(vector.x * ca - vector.y * sa, vector.x * sa + vector.y * ca);
	}
	
	private static Color createRandomColor() {
		return new Color(
				(float)RandomFactory.get().nextDouble(),
				(float)RandomFactory.get().nextDouble(),
				(float)RandomFactory.get().nextDouble(),
				(float)RandomFactory.get().nextDouble());
	}
	
	private static LightShader getShader() {
		if (_shader == null) {
			_shader = new LightShader();
		}
		return _shader;
	}
}