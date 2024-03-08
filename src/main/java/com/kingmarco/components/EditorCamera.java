package com.kingmarco.components;

import com.kingmarco.forge.Camera;
import com.kingmarco.forge.KeyListener;
import com.kingmarco.forge.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Class used to control the editor camera
 * */
public class EditorCamera extends Component{

    private float dragDebounce = 0.032f;
    private Camera levelEditorCamera;
    private Vector2f clickOrigin;
    private boolean reset = false;
    private float lerpTime = 0.0f;
    private float dragSensitivity = 9.5f;
    private float scrollSensitivity = 0.1f;

    /**
     * Constructor for Editor with specified camera object.
     *
     * @param levelEditorCamera The camera object to use.
     */
    public EditorCamera(Camera levelEditorCamera){
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    /**
     * Updates the editor camera based on the mouse and keyboard events.
     *
     * @param dt The time elapsed since the last update (in seconds).
     */
    @Override
    public void editorUpdate(float dt) {
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0){
            this.clickOrigin = new Vector2f(MouseListener.getWorldX(), MouseListener.getWorldY());
            dragDebounce -= dt;
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            Vector2f mousePos = new Vector2f(MouseListener.getWorldX(), MouseListener.getWorldY());
            Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
            Vector2f vector2f = delta.mul(dt).mul(dragSensitivity);
            levelEditorCamera.position.sub(vector2f);
            this.clickOrigin.lerp(mousePos, dt);
        }

        if (dragDebounce <= 0.0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)){
            dragDebounce = 0.1f;
        }

        if (MouseListener.getScrollY() != 0.0f){
            float addValue = (float)Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity),
                    1 / levelEditorCamera.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            levelEditorCamera.addZoom(addValue);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_ALT)) {
            reset = true;
        }

        if (reset) {
            levelEditorCamera.position.lerp(new Vector2f(), lerpTime);
            levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() +
                    ((1.0f - levelEditorCamera.getZoom()) * lerpTime));
            this.lerpTime += 0.1f * dt;
            if (Math.abs(levelEditorCamera.position.x) <= 5.0f &&
                    Math.abs(levelEditorCamera.position.y) <= 5.0f) {
                this.lerpTime = 0.0f;
                levelEditorCamera.position.set(0f, 0f);
                this.levelEditorCamera.setZoom(1.0f);
                reset = false;
            }
        }
    }
}
