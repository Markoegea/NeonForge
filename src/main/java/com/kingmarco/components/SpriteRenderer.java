package com.kingmarco.components;

import com.kingmarco.editor.JImGui;
import com.kingmarco.renderer.Texture;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * Class responsible for the storage and management of the sprite of the game object
 * */
public class SpriteRenderer extends Component {

    private Vector4f color = new Vector4f(1, 1, 1, 1);
    private Sprite sprite = new Sprite();
    private transient Transform lastTransform;
    private transient boolean isDirty = true;

    /**
     * This method is called when the scene starts.
     * */
    @Override
    public void start() {
        this.lastTransform = gameObject.transform.copy();
    }

    /**
     * Updates the position of the sprite, based on the game object position
     *
     * @param dt The time elapsed since the last update (in seconds).
     */
    @Override
    public void update(float dt) {
        if (!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    /**
     * Updates the position of the sprite, based on the game object position in editor mode
     *
     * @param dt The time elapsed since the last update (in seconds).
     */
    @Override
    public void editorUpdate(float dt) {
        if (!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    /**
     * Displays an ImGui color picker for adjusting the sprite color.
     */
    @Override
    public void imgui() {
        if(JImGui.colorPicker4("Color Picker", this.color)){
            this.isDirty = true;
        }
    }

    /**
     * Gets the color of the sprite.
     *
     * @return The sprite color.
     */
    public Vector4f getColor() {
        return this.color;
    }

    /**
     * Gets the texture associated with the sprite.
     *
     * @return The sprite texture.
     */
    public Texture getTexture() {
        return sprite.getTexture();
    }

    /**
     * Gets the texture coordinates of the sprite.
     *
     * @return The sprite texture coordinates.
     */
    public Vector2f[] getTexCoords() {
        return sprite.getTexCoords();
    }

    /**
     * Sets the sprite for this component.
     *
     * @param sprite The sprite to set.
     */
    public void setSprite(Sprite sprite){
        this.sprite = sprite;
        this.isDirty = true;
    }

    /**
     * Sets the color of the sprite.
     *
     * @param color The color to set.
     */
    public void setColor(Vector4f color){
        if (!this.color.equals(color)) {
            this.isDirty = true;
            this.color.set(color);
        }
    }

    /**
     * Checks if the sprite component is dirty.
     *
     * @return True if the component is dirty, false otherwise.
     */
    public boolean isDirty() {
        return this.isDirty;
    }

    /**
     * Sets the dirty flag for the sprite component.
     *
     * @param flag The flag value to set.
     */
    public void setDirty(boolean flag){this.isDirty = flag;}

    /**
     * Sets the texture for the sprite.
     *
     * @param texture The texture to set.
     */
    public void setTexture(Texture texture){
        this.sprite.setTexture(texture);
    }
}
