package engine;

import org.joml.Vector3f;

public class Camera {
    private final Vector3f position;
    private final Vector3f rotation;

    public Camera() {
        this.position = new Vector3f(0);
        this.rotation = new Vector3f(0);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if (offsetZ != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float) Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if (offsetX != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }
}
