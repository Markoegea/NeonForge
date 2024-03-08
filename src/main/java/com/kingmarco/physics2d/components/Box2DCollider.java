package com.kingmarco.physics2d.components;

import com.kingmarco.components.Component;
import com.kingmarco.renderer.DebugDraw;
import com.kingmarco.renderer.DrawLines;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * The Box2DCollider class extends Component and represents a box-shaped 2D collider.
 *
 * This class contains properties for the half size, origin, and offset of the collider,
 * as well as a DrawLines instance for visualizing the collider.
 */
public class Box2DCollider extends Component {
    private Vector2f halfSize = new Vector2f(1);
    private Vector2f origin = new Vector2f();
    private Vector2f offset = new Vector2f();
    private transient DrawLines drawLines = new DrawLines(2);

    public Box2DCollider() {
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
     * Returns the half size of the collider.
     *
     * @return The half size of the collider.
     */
    public Vector2f getHalfSize() {
        return halfSize;
    }

    /**
     * Sets the half size of the collider.
     *
     * @param halfSize The new half size of the collider.
     */
    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
    }

    /**
     * Returns the origin of the collider.
     *
     * @return The origin of the collider.
     */
    public Vector2f getOrigin() {
        return this.origin;
    }

    /**
     * Updates the collider in the editor.
     *
     * This method calculates the center of the collider, adds a box to the DrawLines instance,
     * and updates the DrawLines instance.
     *
     * @param dt The time passed since the last frame.
     */
    @Override
    public void editorUpdate(float dt) {
        Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
        drawLines.addBox2D(center, this.halfSize, this.gameObject.transform.rotation, new Vector3f(0f, 0f, 1f));
    }
}
