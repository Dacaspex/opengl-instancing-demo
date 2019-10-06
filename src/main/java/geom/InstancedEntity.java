package geom;

import org.joml.Vector3f;

public class InstancedEntity {
    private final Vector3f position;
    private final Vector3f rotation;
    private float scale;

    public InstancedEntity(Vector3f position, Vector3f rotation, float scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public InstancedEntity(Vector3f position) {
        this(position, new Vector3f(0), 1);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }
}
