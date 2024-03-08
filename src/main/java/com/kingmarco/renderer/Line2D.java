package com.kingmarco.renderer;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * The Line2D class represents a line in 2D space.
 *
 * This class provides methods to get the start and end points of the line, the color of the line, and to decrement the lifetime of the line.
 */
public class Line2D{
    private Vector2f from;
    private Vector2f to;
    private Vector3f color;
    private int lifeTime;

    public Line2D(Vector2f from, Vector2f to, Vector3f color, int lifeTime) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.lifeTime = lifeTime;
    }

    /**
     * Decrements the lifetime of the line and returns the new lifetime.
     *
     * @return The new lifetime of the line.
     */
    public int beginFrame() {
        this.lifeTime--;
        return this.lifeTime;
    }

    /**
     * Returns the starting point of the line.
     *
     * @return The starting point of the line.
     */
    public Vector2f getFrom() {
        return from;
    }

    /**
     * Returns the ending point of the line.
     *
     * @return The ending point of the line.
     */
    public Vector2f getTo() {
        return to;
    }

    /**
     * Returns the color of the line.
     *
     * @return The color of the line.
     */
    public Vector3f getColor() {
        return color;
    }
}
