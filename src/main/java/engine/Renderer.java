package engine;

import geom.Entity;
import geom.InstancedEntity;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;
import util.Transform;

import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;

    private ShaderProgram shader;
    private Transform transform;

    public void init() {
        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 0.1f, 0.0f);
        glEnable(GL_DEPTH_TEST);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        shader = ShaderProgram.create("/shaders/vertex.vs", "/shaders/fragment.fs");
        shader.createUniform("projectionMatrix");
//        shader.createUniform("modelViewMatrix");

        transform = new Transform();
    }

    public void render(Window window, Camera camera, List<Entity> entities) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        shader.bind();

        Matrix4f projectionMatrix = transform.getProjectionMatrix(
                FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR
        );
        shader.setUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = transform.getViewMatrix(camera);

        for (Entity e : entities) {
            Matrix4f modelViewMatrix = transform.getModelViewMatrix(e, viewMatrix);
            shader.setUniform("modelViewMatrix", modelViewMatrix);
            e.getMesh().render();
        }

        shader.unbind();
    }

    public void renderInstanced(Window window, Camera camera, List<InstancedEntity> entities, InstancedMesh mesh) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        shader.bind();

        Matrix4f projectionMatrix = transform.getProjectionMatrix(
                FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR
        );
        shader.setUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = transform.getViewMatrix(camera);

        mesh.renderInstanced(entities, transform, viewMatrix);

        shader.unbind();
    }
}
