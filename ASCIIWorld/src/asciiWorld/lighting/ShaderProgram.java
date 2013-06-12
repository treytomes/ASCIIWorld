package asciiWorld.lighting;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import asciiWorld.io.FileHelper;

public class ShaderProgram {

	private String _vertexShaderPath;
	private String _fragmentShaderPath;
	
	private int _vertexShader;
	private int _fragmentShader;
	private int _program;
	private boolean _isEnabled;
	
	public ShaderProgram(String vertexShaderPath, String fragmentShaderPath) {
		_vertexShaderPath = vertexShaderPath;
		_fragmentShaderPath = fragmentShaderPath;
		
		try {
			_vertexShader = createVertexShader(_vertexShaderPath);
			_fragmentShader = createFragmentShader(_fragmentShaderPath);
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		
		_program = createProgram(_vertexShader, _fragmentShader);
		if (_program == 0) {
			return;
		}
		
		_isEnabled = false;
	}
	
	public void enable() {
		if (!_isEnabled) {
			ARBShaderObjects.glUseProgramObjectARB(_program);
			_isEnabled = true;
		}
	}
	
	public void disable() {
		if (_isEnabled) {
			ARBShaderObjects.glUseProgramObjectARB(0);
			_isEnabled = false;
		}
	}

    /**
	 * Set the integer value associated with the uniform variable name.
	 *
	 * @param name of the uniform variable.
	 * @param The value to set the uniform variable.
	 */
    public void setUniform(String name, int i) {
        GL20.glUniform1i(getUniformLocation(name), i);
    }

    /**
	 * Set the float value associated with the uniform variable name.
	 *
	 * @param name of the uniform variable.
	 * @param The value to set the uniform variable.
	 */
    public void setUniform(String name, float i) {
        GL20.glUniform1f(getUniformLocation(name), i);
    }

    /**
	 * Set the int values associated with the uniform variable name.
	 * This is used to set a shader variable of type vec2.
	 *
	 * @param name of the uniform variable.
	 * @param The value to set the uniform variable.
	 */
	public void setUniform(String name, int a, int b) {
        GL20.glUniform2i(getUniformLocation(name), a, b);
    }

    /**
	 * Set the float values associated with the uniform variable name.
	 * This is used to set a shader variable of type vec2.
	 *
	 * @param name of the uniform variable.
	 * @param The value to set the uniform variable.
	 */
    public void setUniform(String name, float a, float b) {
        GL20.glUniform2f(getUniformLocation(name), a, b);
    }

    /**
	 * Set the int values associated with the uniform variable name.
	 * This is used to set a shader variable of type vec3.
	 *
	 * @param name of the uniform variable.
	 * @param The value to set the uniform variable.
	 */
    public void setUniform(String name, int a, int b, int c) {
        GL20.glUniform3i(getUniformLocation(name), a, b, c);
    }

    /**
	 * Set the float values associated with the uniform variable name.
	 * This is used to set a shader variable of type vec3.
	 *
	 * @param name of the uniform variable.
	 * @param The value to set the uniform variable.
	 */
    public void setUniform(String name, float a, float b, float c) {
        GL20.glUniform3f(getUniformLocation(name), a, b, c);
    }

    /**
	 * Set the int values associated with the uniform variable name.
	 * This is used to set a shader variable of type vec4.
	 *
	 * @param name of the uniform variable.
	 * @param The value to set the uniform variable.
	 */
    public void setUniform(String name, int a, int b, int c, int d) {
        GL20.glUniform4i(getUniformLocation(name), a, b, c, d);
    }

    /**
	 * Set the float values associated with the uniform variable name.
	 * This is used to set a shader variable of type vec4.
	 *
	 * @param name of the uniform variable.
	 * @param The value to set the uniform variable.
	 */
    public void setUniform(String name, float a, float b, float c, float d) {
        GL20.glUniform4f(getUniformLocation(name), a, b, c, d);
    }

    // Reference: http://relativity.net.au/gaming/glsl/WrapperClass.html
        /**
	 * Set the float values associated with the uniform variable name.
	 * This is used to set a shader variable of type mat4.
	 *
	 * @param name of the uniform variable.
	 * @param The value to set the uniform variable.
	 */
    /*public void setUniform(String name, boolean transpose, Matrix4f matrix) {

        buffer.position(0);
        buffer.limit(16);
        matrix.store(buffer);
        buffer.position(0);

       GL20.glUniformMatrix4(uniforms.get(name).intValue(),transpose,buffer);

    }

    public void setUniform(String name, boolean transpose, Matrix3f matrix) {

        buffer.position(0);
        buffer.limit(9);
        matrix.store(buffer);
        buffer.position(0);

       GL20.glUniformMatrix3(uniforms.get(name).intValue(),transpose,buffer);

    }

    public void setUniform(String name, boolean transpose, Matrix2f matrix) {

        buffer.position(0);
        buffer.limit(4);
        matrix.store(buffer);
        buffer.position(0);

       GL20.glUniformMatrix4(uniforms.get(name).intValue(),transpose,buffer);

    }

    public void setUniform(String name, boolean transpose, FloatBuffer buffer) {

        buffer.position(0);

    switch(buffer.capacity())
    {
        case(4):
       GL20.glUniformMatrix2(uniforms.get(name).intValue(),transpose,buffer);
       break;
       case(9):
       GL20.glUniformMatrix3(uniforms.get(name).intValue(),transpose,buffer);
       break;
        case(16):
       GL20.glUniformMatrix4(uniforms.get(name).intValue(),transpose,buffer);
       break;
    }
    }*/
	
	
	private int getUniformLocation(String name) {
		return GL20.glGetUniformLocation(_program, name);
	}
	
	private static int createProgram(int vertexShader, int fragmentShader) {
		int program = ARBShaderObjects.glCreateProgramObjectARB();
		if (program != 0) {
			// Attach the shaders to the program.
			ARBShaderObjects.glAttachObjectARB(program, vertexShader);
			ARBShaderObjects.glAttachObjectARB(program, fragmentShader);
			
			// Link the program.
			ARBShaderObjects.glLinkProgramARB(program);
			if (!getObjectStatus(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB)) {
				throw new RuntimeException(getLogInfo(program));
			}
			
			// Validate the program.
			ARBShaderObjects.glValidateProgramARB(program);
			if (!getObjectStatus(program, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB)) {
				throw new RuntimeException(getLogInfo(program));
			}
		}
		
		return program;
	}
	
	private static int createVertexShader(String path)
			throws Exception {
		return createShader(path, ARBVertexShader.GL_VERTEX_SHADER_ARB);
	}
	
	private static int createFragmentShader(String path)
			throws Exception {
		return createShader(path, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
	}
	
	private static int createShader(String path, int shaderType)
			throws Exception {
		int shader = 0;
		try {
			shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
			
			if (shader == 0) {
				return 0;
			}
			
			ARBShaderObjects.glShaderSourceARB(shader, FileHelper.readToEnd(path));
			ARBShaderObjects.glCompileShaderARB(shader);
			
			if (!getObjectStatus(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB)) {
				throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
			}
			
			return shader;
		} catch (Exception ex) {
			ARBShaderObjects.glDeleteObjectARB(shader);
			throw ex;
		}
	}
	
	private static boolean getObjectStatus(int obj, int statusType) {
		return ARBShaderObjects.glGetObjectParameteriARB(obj, statusType) == GL11.GL_TRUE;
	}
	
	private static String getLogInfo(int obj) {
		return ARBShaderObjects.glGetInfoLogARB(
				obj,
				ARBShaderObjects.glGetObjectParameteriARB(
						obj,
						ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}
}
