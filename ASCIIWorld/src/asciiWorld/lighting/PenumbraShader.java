package asciiWorld.lighting;

import org.newdawn.slick.geom.Vector2f;

public class PenumbraShader {

	private static final String PATH_VERTEXSHADER = "resources/shaders/penumbraShader.vert";
	private static final String PATH_FRAGMENTSHADER = "resources/shaders/penumbraShader.frag";
	
	private ShaderProgram _program;

	public PenumbraShader() {
		_program = new ShaderProgram(PATH_VERTEXSHADER, PATH_FRAGMENTSHADER);
	}
	
	public void enable() {
		_program.enable();
	}
	
	public void disable() {
		_program.disable();
	}
	
	public void setState(Vector2f origin, float angle, Vector2f innerVector, float innerIntensity, float outerIntensity) {
		_program.setUniform("origin", origin.x, origin.y);
        _program.setUniform("angle", angle);
        _program.setUniform("inner", innerVector.x, innerVector.y);
        _program.setUniform("inner_intensity", innerIntensity);
        _program.setUniform("outer_intensity", outerIntensity);
	}
}