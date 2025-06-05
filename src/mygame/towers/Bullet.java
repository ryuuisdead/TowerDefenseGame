package mygame.towers;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

public class Bullet extends Node {

    private Vector3f direction;
    private float speed = 10f;
    private boolean active = true;

    public Bullet(AssetManager assetManager, Vector3f position, Vector3f target) {
        Sphere sphere = new Sphere(2, 2, 0.2f);
        Geometry geom = new Geometry("Bullet", sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);
        geom.setMaterial(mat);
        this.attachChild(geom);
        this.setLocalTranslation(position);
        this.direction = target.subtract(position).normalize();
    }

    public void update(float tpf) {
        if (!active) return;

        this.move(direction.mult(tpf * speed));
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }

    public Vector3f getPosition() {
        return this.getLocalTranslation();
    }
}
