package com.kingmarco.editor;

import com.kingmarco.components.SpriteRenderer;
import com.kingmarco.forge.GameObject;
import com.kingmarco.physics2d.components.Box2DCollider;
import com.kingmarco.physics2d.components.CircleCollider;
import com.kingmarco.physics2d.components.RigidBody2D;
import com.kingmarco.renderer.PickingTexture;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

/**
 * A class responsible to create the scene properties and manage them.
 * */
public class PropertiesWindow {
    private List<GameObject> activeGameObjects = null;
    private List<Vector4f> activeGameObjectColor;
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture){
        this.activeGameObjects = new ArrayList<>();
        this.pickingTexture = pickingTexture;
        this.activeGameObjectColor = new ArrayList<>();
    }

    /**
     * Open the ImGui properties when use right-click to add components to the {@link GameObject}.
     * */
    public void imgui() {
        if (activeGameObjects.size() == 1 && activeGameObjects.get(0) != null){
            activeGameObject = activeGameObjects.get(0);
            ImGui.begin("Properties");

            if (ImGui.beginPopupContextWindow("ComponentAdder")){
                if (ImGui.menuItem("Add Rigidbody")){
                    if (activeGameObject.getComponent(RigidBody2D.class) == null){
                        activeGameObject.addComponent(new RigidBody2D());
                    }
                }

                if (ImGui.menuItem("Add Box Collider")) {
                    if (activeGameObject.getComponent(Box2DCollider.class) == null &&
                            activeGameObject.getComponent(CircleCollider.class) == null) {
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }

                if (ImGui.menuItem("Add Circle Collider")) {
                    if (activeGameObject.getComponent(CircleCollider.class) == null &&
                            activeGameObject.getComponent(Box2DCollider.class) == null){
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }

                ImGui.endPopup();
            }

            activeGameObject.imgui();
            ImGui.end();
        }
    }

    /**
     * Get the active {@link GameObject} or return null otherwise.
     *
     * @return the active {@link GameObject} or null.
     * */
    public GameObject getActiveGameObject() {
        return activeGameObjects.size() == 1 ? this.activeGameObjects.get(0) : null;
    }

    /**
     * Get the list of {@link GameObject}.
     *
     * @return the list of the active {@link GameObject}.
     * */
    public List<GameObject> getActiveGameObjects() {
        return this.activeGameObjects;
    }

    /**
     * Clear the active {@link GameObject}'s color and clear the active {@link GameObject} list.
     * */
    public void clearSelected() {
        if (!activeGameObjectColor.isEmpty()) {
            int i = 0;
            for (GameObject go : activeGameObjects) {
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if (spr != null) {
                    spr.setColor(activeGameObjectColor.get(i));
                }
                i++;
            }
        }
        this.activeGameObjects.clear();
    }

    /**
     * Clear the selected {@link GameObject}'s list and add another {@link GameObject} to the list.
     * */
    public void setActiveGameObject(GameObject go) {
        if (go != null) {
            clearSelected();
            this.activeGameObjects.add(go);
        }
    }

    /**
     * Add a {@link GameObject} to the color list and an active game object list.
     *
     * @param go An {@link GameObject} to be added to the active list and to the current color list.
     * */
    public void addActiveGameObject(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null) {
            this.activeGameObjectColor.add(new Vector4f(spr.getColor()));
            spr.setColor(new Vector4f(0.8f, 0.8f, 0.0f, 0.8f));
        } else {
            this.activeGameObjectColor.add(new Vector4f());
        }
        this.activeGameObjects.add(go);
    }

    /**
     * Get the current {@link PickingTexture} object.
     *
     * @return A {@link PickingTexture} object.
     * */
    public PickingTexture getPickingTexture() {
        return pickingTexture;
    }
}
