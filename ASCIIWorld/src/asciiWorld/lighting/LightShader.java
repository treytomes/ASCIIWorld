package asciiWorld.lighting;

import org.newdawn.slick.Color;

public class LightShader {

	private static final String PATH_VERTEXSHADER = "resources/shaders/lightShader.vert";
	private static final String PATH_FRAGMENTSHADER = "resources/shaders/lightShader.frag";
	
	private ShaderProgram _program;
	
	public LightShader() {
		_program = new ShaderProgram(PATH_VERTEXSHADER, PATH_FRAGMENTSHADER);
	}
	
	public void enable() {
		_program.enable();
	}
	
	public void setState(Color color) {
		_program.setUniform("color", color.r, color.g, color.b, color.a);
	}
	
	public void disable() {
		_program.disable();
	}
}