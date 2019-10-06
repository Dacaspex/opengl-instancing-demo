package engine;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private final int vao;
    private final int positionsVbo;
    private final int indicesVbo;
    private final int vertexCount;

    public Mesh(int vao, int positionsVbo, int indicesVbo, int vertexCount) {
        this.vao = vao;
        this.positionsVbo = positionsVbo;
        this.indicesVbo = indicesVbo;
        this.vertexCount = vertexCount;
    }

    public static Mesh create(float[] positions, int[] indices) {
        FloatBuffer positionsBuffer = null;
        IntBuffer indicesBuffer = null;
        int vao;
        int positionsVbo, indicesVbo;
        int vertexCount;
        try {
            vao = glGenVertexArrays();
            glBindVertexArray(vao);

            // Positions vbo
            positionsBuffer = MemoryUtil.memAllocFloat(positions.length);
            vertexCount = indices.length;
            positionsBuffer.put(positions).flip();

            positionsVbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, positionsVbo);
            glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Indices vbo
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();

            indicesVbo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVbo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            // Unbind
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } catch (Exception e) {
            throw new RuntimeException("Could not create mesh", e);
        } finally {
            if (positionsBuffer != null) {
                MemoryUtil.memFree(positionsBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }

        return new Mesh(vao, positionsVbo, indicesVbo, vertexCount);
    }

    public void render() {
        glBindVertexArray(vao);
        glEnableVertexAttribArray(0); // Positions

        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDisableVertexAttribArray(0);

        // Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(positionsVbo);
        glDeleteBuffers(indicesVbo);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vao);
    }
}
