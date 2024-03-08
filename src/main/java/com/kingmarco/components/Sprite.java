package com.kingmarco.components;

import com.kingmarco.renderer.Texture;
import org.joml.Vector2f;

/**
 * Class responsible to represents a sprite in a 2D game.
 */
public class Sprite {

    private float width, height;

    private Texture texture = null;
    private Vector2f[] texCoords = {
        new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1),
    };

    /**
     * Gets the texture associated with this sprite.
     *
     * @return The texture.
     */
    public Texture getTexture() {
        return this.texture;
    }

    /**
     * Sets the texture for this sprite.
     *
     * @param texture The texture to set.
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    /**
     * Gets the texture coordinates for this sprite.
     *
     * @return The texture coordinates.
     */
    public Vector2f[] getTexCoords() {
        return this.texCoords;
    }

    /**
     * Sets the texture coordinates for this sprite.
     *
     * @param texCoords The texture coordinates to set.
     */
    public void setTexCoords(Vector2f[] texCoords) {
        this.texCoords = texCoords;
    }

    /**
     * Gets the width of the sprite.
     *
     * @return The width.
     */
    public float getWidth() {
        return width;
    }

    /**
     * Sets the width of the sprite.
     *
     * @param width The width to set.
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Gets the height of the sprite.
     *
     * @return The height.
     */
    public float getHeight() {
        return height;
    }

    /**
     * Sets the height of the sprite.
     *
     * @param height The height to set.
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * Gets the texture ID associated with this sprite.
     *
     * @return The texture ID, or -1 if no texture is set.
     */
    public int getTexId() {
        return texture == null ? -1 : texture.getTexID();
    }
}
