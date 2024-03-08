package com.kingmarco.forge;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * A class responsible to create and manage the camera matrix.
 * */
public class Camera {
    private Matrix4f projectionMatrix, viewMatrix, inverseProjection, inverseView;
    private float projectionWidth = 6f;
    private float projectionHeight = 3f;
    private Vector4f clearColor = new Vector4f(1, 1, 1, 1);
    private Vector2f projectionSize = new Vector2f(projectionWidth, projectionHeight);
    public Vector2f position;
    private float zoom = 1.0f;

    public Camera(Vector2f position){
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjection = new Matrix4f();
        this.inverseView = new Matrix4f();
        adjustProjection();
    }

    /**
     * Adjusts the projection matrix based on the current zoom level and projection size.
     */
    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f,projectionSize.x * this.zoom,
                0.0f, projectionSize.y * this.zoom, 0.0f, 100.0f);
        inverseProjection = new Matrix4f(this.projectionMatrix).invert();
    }

    /**
     * Computes the view matrix based on the camera position.
     *
     * @return The view matrix.
     */
    public Matrix4f getViewMatrix(){
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();
        viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f),
                cameraFront.add(position.x, position.y, 0.0f),
                cameraUp);
        inverseView = new Matrix4f(this.viewMatrix).invert();
        return this.viewMatrix;
    }

    /**
     * Retrieves the projection matrix.
     *
     * @return The projection matrix.
     */
    public Matrix4f getProjectionMatrix(){
        return this.projectionMatrix;
    }

    /**
     * Retrieves the inverse projection matrix.
     *
     * @return The inverse projection matrix.
     */
    public Matrix4f getInverseProjection() {
        return this.inverseProjection;
    }

    /**
     * Retrieves the inverse view matrix.
     *
     * @return The inverse view matrix.
     */
    public Matrix4f getInverseView() {
        return this.inverseView;
    }

    /**
     * Retrieves the projection size.
     *
     * @return The projection size.
     */
    public Vector2f getProjectionSize() {
        return this.projectionSize;
    }

    /**
     * Retrieves the current zoom level.
     *
     * @return The zoom level.
     */
    public float getZoom() {
        return zoom;
    }

    /**
     * Sets the zoom level.
     *
     * @param zoom The new zoom level.
     */
    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    /**
     * Adjusts the zoom level by adding a specified value.
     *
     * @param value The value to add to the zoom level.
     */
    public void addZoom(float value) {
        this.zoom += value;
    }

    /**
     * Retrieves the clear color for rendering.
     *
     * @return The clear color.
     */
    public Vector4f getClearColor() {
        return clearColor;
    }
}
