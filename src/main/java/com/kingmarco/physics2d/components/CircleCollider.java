package com.kingmarco.physics2d.components;


import com.kingmarco.components.Component;
import com.kingmarco.renderer.DebugDraw;
import com.kingmarco.renderer.DrawLines;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * The CircleCollider class extends Component and represents a circle-shaped 2D collider.
 *
 * This class contains properties for the radius and offset of the collider,
 * as well as a DrawLines instance for visualizing the collider.
 */
public class CircleCollider extends Component {
    private float radius = 1f;
    private Vector2f offset = new Vector2f();
    private transient DrawLines drawLines = new DrawLines(2);

    public CircleCollider() {
        drawLines.start();
        DebugDraw.addDrawLines(drawLines);
    }

    /**
     * Returns the offset of the collider.
     *
     * @return The offset of the collider.
     */
    public Vector2f getOffset() {
        return this.offset;
    }

    /**
     * Sets the offset of the collider.
     *
     * @param newOffset The new offset of the collider.
     */
    public void setOffset(Vector2f newOffset) {
        this.offset.set(newOffset);
    }

    /**
     * Returns the radius of the collider.
     *
     * @return The radius of the collider.
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Sets the radius of the collider.
     *
     * @param radius The new radius of the collider.
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * Updates the collider in the editor.
     *
     * This method calculates the center of the collider and adds a circle to the DrawLines instance.
     *
     * @param dt The time passed since the last frame.
     */
    @Override
    public void editorUpdate(float dt) {
        Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
        drawLines.addCircle(center, radius);
    }
}
