package geom;

import engine.Mesh;
import org.joml.Vector3f;

public class Entity {
    private final Mesh mesh;
    private final Vector3f position;
    private final Vector3f rotation;
    private float scale;

    public Entity(Mesh mesh) {
        this.mesh = mesh;
        this.position = new Vector3f(0, -1f, -5f);
        this.rotation = new Vector3f(0, 0f, 0f);
        this.scale = 1;
    }

    public Entity(Mesh mesh, Vector3f position) {
        this.mesh = mesh;
        this.position = position;
        this.rotation = new Vector3f(0, 0f, 0f);
        this.scale = 1;
    }

    public Mesh getMesh() {
        return mesh;
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
