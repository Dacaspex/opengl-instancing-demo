package engine;

import geom.Entity;
import geom.InstancedEntity;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;
import util.Transform;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class InstancedMesh {
    private final int vao;
    private final int positionsVbo;
    private final int indicesVbo;
    private final int vertexCount;
    private final int modelViewVbo;
    private final FloatBuffer modelViewBuffer;

    public InstancedMesh(
            int vao,
            int positionsVbo,
            int indicesVbo,
            int vertexCount,
            int modelViewVbo,
            FloatBuffer modelViewBuffer
    ) {
        this.vao = vao;
        this.positionsVbo = positionsVbo;
        this.indicesVbo = indicesVbo;
        this.vertexCount = vertexCount;
        this.modelViewVbo = modelViewVbo;
        this.modelViewBuffer = modelViewBuffer;
    }

    public static InstancedMesh create(float[] positions, int[] indices, int numInstances) {
        FloatBuffer positionsBuffer = null;
        IntBuffer indicesBuffer = null;
        FloatBuffer modelViewBuffer;
        int vao;
        int positionsVbo, indicesVbo, modelViewVbo;
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

            // Model view vbo (instanced)
            modelViewBuffer = MemoryUtil.memAllocFloat(numInstances * 4 * 4);

            modelViewVbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, modelViewVbo);
            int start = 1;
            for (int i = 0; i < 4; i++) {
                glVertexAttribPointer(start, 4, GL_FLOAT, false, 4 * 4 * 4, i * 4 * 4);
                glVertexAttribDivisor(start, 1);
                start++;
            }

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

        return new InstancedMesh(vao, positionsVbo, indicesVbo, vertexCount, modelViewVbo, modelViewBuffer);
    }

    public void beforeRender() {
        glBindVertexArray(vao);
        glEnableVertexAttribArray(0); // Positions
        glEnableVertexAttribArray(1); // Model view
        glEnableVertexAttribArray(2); // Model view
        glEnableVertexAttribArray(3); // Model view
        glEnableVertexAttribArray(4); // Model view
    }

    public void renderInstanced(List<InstancedEntity> entities, Transform transform, Matrix4f viewMatrix) {
        beforeRender();

        modelViewBuffer.clear();

        int i = 0;
        for (InstancedEntity entity : entities) {
            Matrix4f modelViewMatrix = transform.getModelViewMatrix(entity, viewMatrix);
            modelViewMatrix.get(i * 16, modelViewBuffer);
            i++;
        }

        glBindBuffer(GL_ARRAY_BUFFER, modelViewVbo);
        glBufferData(GL_ARRAY_BUFFER, modelViewBuffer, GL_DYNAMIC_DRAW);

        glDrawElementsInstanced(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0, entities.size());

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        afterRender();
    }

    public void afterRender() {
        glDisableVertexAttribArray(4);
        glDisableVertexAttribArray(3);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        if (modelViewBuffer != null) {
            MemoryUtil.memFree(modelViewBuffer);
        }
    }
}
