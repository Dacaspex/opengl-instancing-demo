package main;

import engine.*;
import geom.Entity;
import geom.InstancedEntity;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.Version;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Launcher {
    private Window window;
    private Renderer renderer;
    private MouseInput mouseInput;
    private Camera camera;

    public Launcher() {
        this.window = new Window();
        this.renderer = new Renderer();
        this.mouseInput = new MouseInput();
        this.camera = new Camera();
    }

    public static void main(String[] args) {
        (new Launcher()).run();
    }

    void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        window.init();
        renderer.init();
        mouseInput.init(window);

        loop();

        window.terminate();
    }

    private void loop() {
        float[] positions = new float[]{
                -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,
                // back
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f
        };
        int[] indices = new int[]{
                // front
                0, 1, 2,
                2, 3, 0,
                // right
                1, 5, 6,
                6, 2, 1,
                // back
                7, 6, 5,
                5, 4, 7,
                // left
                4, 0, 3,
                3, 7, 4,
                // bottom
                4, 5, 1,
                1, 0, 4,
                // top
                3, 2, 6,
                6, 7, 3
        };

        Mesh mesh = Mesh.create(positions, indices);
        InstancedMesh instancedMesh = InstancedMesh.create(positions, indices, 512000);

        // Normal entities
        List<Entity> entities = new ArrayList<>();
        int start = -100;
        int end = 100;
        int step = 5;
        int steps = (end - start) / step;
        for (int i = 0; i < steps; i++) {
            for (int j = 0; j < steps; j++) {
                for (int k = 0; k < steps; k++) {
                    Vector3f position = new Vector3f(start + i * step, start + j * step, start + k * step);
                    entities.add(new Entity(mesh, position));
                }
            }
        }

        System.out.println(entities.size());

        // Instanced entities
        List<InstancedEntity> instancedEntities = new ArrayList<>();
        int iStart = -200;
        int iEnd = 200;
        int iStep = 5;
        int iSteps = (iEnd - iStart) / iStep;
        for (int i = 0; i < iSteps; i++) {
            for (int j = 0; j < iSteps; j++) {
                for (int k = 0; k < iSteps; k++) {
                    Vector3f position = new Vector3f(iStart + i * iStep, iStart + j * iStep, iStart + k * iStep);
                    instancedEntities.add(new InstancedEntity(position));
                }
            }
        }

        System.out.println(instancedEntities.size());

        ShaderProgram shader = ShaderProgram.create("/shaders/vertex.vs", "/shaders/fragment.fs");

        long lastUpdate = System.currentTimeMillis();
        long numFrames = 0;
        while (!window.shouldClose()) {
//            renderer.render(window, camera, entities);
            renderer.renderInstanced(window, camera, instancedEntities, instancedMesh);

            handleInput();

            if (lastUpdate + 1000 < System.currentTimeMillis()) {
                long diff = System.currentTimeMillis() - lastUpdate;

                float fps = (float) numFrames / ((float) diff * 0.001f);

                window.setTitle(Float.toString(fps));
                lastUpdate = System.currentTimeMillis();
                numFrames = 0;
            }

            window.render();
            numFrames++;
        }

        shader.cleanup();
        mesh.cleanup();
    }

    void handleInput() {
        mouseInput.input(window);

        Vector3f cameraInc = new Vector3f(0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Q)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_E)) {
            cameraInc.y = 1;
        }

        float cameraPosStep = 0.1f;
        float mouseSensitivity = 0.2f;

        // Update camera position
        camera.movePosition(
                cameraInc.x * cameraPosStep,
                cameraInc.y * cameraPosStep,
                cameraInc.z * cameraPosStep
        );

        // Update camera based on mouse
        if (mouseInput.isLeftButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * mouseSensitivity, rotVec.y * mouseSensitivity, 0);
        }
    }
}
