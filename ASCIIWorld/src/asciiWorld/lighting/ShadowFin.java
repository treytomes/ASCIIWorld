package asciiWorld.lighting;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.geom.Vector2f;

public class ShadowFin {
	
	private static PenumbraShader _shader;
	
	private Vector2f _rootPosition;
	private Vector2f _outer;
	private float _penumbraIntensity;
	private Vector2f _inner;
	private float _umbraIntensity;
	private float _depth;
	private int _index;
	
	public ShadowFin(Vector2f rootPosition, int index) {
		_rootPosition = rootPosition;
		_outer = null;
		_penumbraIntensity = 1.0f;
		_inner = null;
		_umbraIntensity = 0.0f;
		_depth = 0.0f;
		setIndex(index);
	}
	
	public int getIndex() {
		return _index;
	}
	
	private void setIndex(int value) {
		_index = value;
	}
	
	public Vector2f getOuter() {
		return _outer;
	}
	
	public void setOuter(Vector2f value) {
		_outer = value;
	}
	
	public Vector2f getInner() {
		return _inner;
	}
	
	public void setInner(Vector2f value) {
		_inner = value;
	}
	
	public void setUmbraIntensity(float value) {
		_umbraIntensity = value;
	}
	
	public void setPenumbraIntensity(float value) {
		_penumbraIntensity = value;
	}
	
	public float getAngle() {
		Vector2f uv = _inner.copy().normalise();
		Vector2f pv = _outer.copy().normalise();
		return (float)Math.acos(uv.dot(pv));
	}
	
	public void render() {
		PenumbraShader shader = getShader();
		shader.enable();
		shader.setState(_rootPosition, getAngle(), _inner, _umbraIntensity, _penumbraIntensity);
		
		GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex3f(_rootPosition.x, _rootPosition.y, _depth);
        GL11.glVertex3f(_rootPosition.x + _outer.x, _rootPosition.y + _outer.y, _depth);
        GL11.glVertex3f(_rootPosition.x + _inner.x, _rootPosition.y + _inner.y, _depth);
        GL11.glEnd();
		
		shader.disable();
	}
	
	private static PenumbraShader getShader() {
		if (_shader == null) {
			_shader = new PenumbraShader();
		}
		return _shader;
	}
}
