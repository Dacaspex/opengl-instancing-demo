package engine;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import util.Utilities;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    private final int id;
    private final Map<String, Integer> uniforms;

    public ShaderProgram(int id) {
        this.id = id;
        this.uniforms = new HashMap<>();
    }

    public static ShaderProgram create(String vertexShaderPath, String fragmentShaderPath) {
        String vertexShaderSource;
        String fragmentShaderSource;
        try {
            vertexShaderSource = Utilities.loadResource(vertexShaderPath);
            fragmentShaderSource = Utilities.loadResource(fragmentShaderPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        int programId = glCreateProgram();
        int vertexShaderId = attachShader(programId, vertexShaderSource, GL_VERTEX_SHADER);
        int fragmentShaderId = attachShader(programId, fragmentShaderSource, GL_FRAGMENT_SHADER);

        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new RuntimeException(String.format("Could not link shader: %s", glGetProgramInfoLog(programId, 1024)));
        }

        glDetachShader(programId, vertexShaderId);
        glDetachShader(programId, fragmentShaderId);

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println(String.format(
                    "Warning validating shader %s %s: %s",
                    vertexShaderPath,
                    fragmentShaderPath,
                    glGetProgramInfoLog(programId, 1024)
            ));
        }

        return new ShaderProgram(programId);
    }

    private static int attachShader(int programId, String source, int type) {
        int shaderId = glCreateShader(type);
        if (shaderId == 0) {
            throw new RuntimeException(String.format("Could not create shader: %s", type));
        }

        glShaderSource(shaderId, source);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException(String.format("Error compiling shader: %s", glGetShaderInfoLog(shaderId, 1024)));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void bind() {
        glUseProgram(id);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void createUniform(String name) {
        int location = glGetUniformLocation(id, name);
        if (location < 0) {
            throw new RuntimeException(String.format("Could not find uniform location: %s", name));
        }
        uniforms.put(name, location);
    }

    public void setUniform(String name, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            value.get(buffer);
            glUniformMatrix4fv(uniforms.get(name), false, buffer);
        }
    }

    public void cleanup() {
        unbind();
        if (id != 0) {
            glDeleteProgram(id);
        }
    }
}
