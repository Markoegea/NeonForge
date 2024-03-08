package com.kingmarco.components;

import com.kingmarco.editor.JImGui;
import org.joml.Vector2f;

/**
 * Class responsible for the position, scale,
 * rotation and zIndex of the {@link com.kingmarco.forge.GameObject}.
 * */
public class Transform extends Component {

    public Vector2f position;
    public Vector2f scale;
    public float rotation = 0.0f;
    public int zIndex;

    public Transform() {
        init(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position){
        init(position, new Vector2f());
    }

    public Transform (Vector2f position, Vector2f scale) {
        init(position, scale);
    }

    /**
     * Initialize the position, scale and zIndex
     *
     * @param position The {@link Vector2f} position in x, y value.
     * @param scale The {@link Vector2f} scale in x, y value
     * */
    public void init(Vector2f position, Vector2f scale){
        this.position = position;
        this.scale = scale;
        this.zIndex = 0;
    }


    /**
     * Creates a copy of this transform.
     *
     * @return A new Transform with the same position and scale.
     */
    public Transform copy() {
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
    }

    /**
     * Displays the transform properties in the ImGui UI.
     */
    @Override
    public void imgui() {
        gameObject.setName(JImGui.inputText("Name: ", gameObject.getName()));
        JImGui.drawVec2Control("Position", this.position);
        JImGui.drawVec2Control("Scale", this.scale, 32.0f);
        this.rotation = JImGui.dragFloat("Rotation", this.rotation);
        this.zIndex = JImGui.dragInt("Z-Index", this.zIndex);
    }

    /**
     * Copies the values of this transform to another transform.
     *
     * @param to The target transform to copy to.
     */
    public void copy(Transform to) {
        to.position.set(this.position);
        to.scale.set(this.scale);
    }

    /**
     * Checks if this transform is equal to another object.
     *
     * @param o The object to compare with.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o){
        if (o == null) return false;
        if (!(o instanceof Transform)) return false;

        Transform t = (Transform) o;
        return t.position.equals(this.position) && t.scale.equals(this.scale) &&
                t.rotation == this.rotation && t.zIndex == this.zIndex;
    }
}
