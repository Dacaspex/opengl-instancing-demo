package util;

import engine.Camera;
import geom.Entity;
import geom.InstancedEntity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {
    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;
    private final Matrix4f modelViewMatrix;

    public Transform() {
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.modelViewMatrix = new Matrix4f();
    }

    public Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);

        return projectionMatrix;
    }

    public Matrix4f getModelViewMatrix(Entity entity, Matrix4f viewMatrix) {
        Vector3f rotation = entity.getRotation();

        modelViewMatrix
                .identity()
                .translate(entity.getPosition())
                .rotateX((float) Math.toRadians(-rotation.x))
                .rotateY((float) Math.toRadians(-rotation.y))
                .rotateZ((float) Math.toRadians(-rotation.z))
                .scale(entity.getScale());
        Matrix4f viewCurr = new Matrix4f(viewMatrix);

        return viewCurr.mul(modelViewMatrix);
    }

    public Matrix4f getModelViewMatrix(InstancedEntity entity, Matrix4f viewMatrix) {
        Vector3f rotation = entity.getRotation();

        modelViewMatrix
                .identity()
                .translate(entity.getPosition())
                .rotateX((float) Math.toRadians(-rotation.x))
                .rotateY((float) Math.toRadians(-rotation.y))
                .rotateZ((float) Math.toRadians(-rotation.z))
                .scale(entity.getScale());
        Matrix4f viewCurr = new Matrix4f(viewMatrix);

        return viewCurr.mul(modelViewMatrix);
    }

    public Matrix4f getViewMatrix(Camera camera) {
        Vector3f position = camera.getPosition();
        Vector3f rotation = camera.getRotation();

        viewMatrix.identity();
        // First do the rotation so camera rotates over its position
        viewMatrix
                .rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(-position.x, -position.y, -position.z);

        return viewMatrix;
    }
}
