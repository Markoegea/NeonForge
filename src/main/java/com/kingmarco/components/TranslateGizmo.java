package com.kingmarco.components;

import com.kingmarco.editor.PropertiesWindow;
import com.kingmarco.forge.MouseListener;
import com.kingmarco.renderer.DebugDraw;
import com.kingmarco.renderer.DrawLines;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

/**
 * Class responsible for create the gizmo tha translate the game object
 * */
public class TranslateGizmo extends Gizmo{

    private final DrawLines drawLines = new DrawLines(2);
    private boolean hover = false;
    private float constantScale = 1.1f;

    public TranslateGizmo(PropertiesWindow propertiesWindow){
        super(propertiesWindow);
    }

    /**
     * Initialize the gizmo lines.
     * */
    @Override
    public void start() {
        drawLines.start();
        DebugDraw.addDrawLines(drawLines);
    }

    /**
     * Update the game object position based in the mouse listener.
     *
     * @param dt The time elapsed since the last update (in seconds).
     * */
    @Override
    public void editorUpdate(float dt) {
        if (deactivate) return;

        if (activeGameObject != null) {
            if (hover && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                Vector2f delta = MouseListener.screenToWorld(MouseListener.getScreenD());
                activeGameObject.transform.position.x -= delta.x;
                activeGameObject.transform.position.y -= -delta.y;
            }
            drawLine(activeGameObject.transform.position, activeGameObject.transform.scale);
        }

        hover = checkHoverState();

        if (!hover){
            this.activeGameObject = this.propertiesWindow.getActiveGameObject();
        }
    }

    /**
     * Draw the 2D box around the game object
     *
     * @param gameObjectPosition The {@link Vector2f} position of the {@link com.kingmarco.forge.GameObject}.
     * @param gameObjectScale The {@link Vector2f} scale of the {@link com.kingmarco.forge.GameObject}.
     * */
    private void drawLine(Vector2f gameObjectPosition, Vector2f gameObjectScale){
        drawLines.addBox2D(new Vector2f(gameObjectPosition.x, gameObjectPosition.y),
                new Vector2f(gameObjectScale.x * constantScale,
                        gameObjectScale.y * constantScale), 0);
    }

    /**
     * Check if the mouse pointer is hover the selected {@link com.kingmarco.forge.GameObject}.
     *
     * @return True if the mouse is hover the {@link com.kingmarco.forge.GameObject}, False otherwise.
     * */
    private boolean checkHoverState() {
        if (this.activeGameObject == null) return false;
        Vector2f mousePos = MouseListener.getWorld();
        Vector2f gameObjectPosition = this.activeGameObject.transform.position;
        Vector2f gameObjectScale = this.activeGameObject.transform.scale;
        return mousePos.x >= (gameObjectPosition.x - gameObjectScale.x) &&
                mousePos.x <= (gameObjectPosition.x + gameObjectScale.x) &&
                mousePos.y >= (gameObjectPosition.y - gameObjectScale.y) &&
                mousePos.y <= (gameObjectPosition.y + gameObjectScale.y);
    }

    /**
     * Override the active method
     * */
    @Override
    protected void setActive() {

    }

    /**
     * Override the inactive method
     * */
    @Override
    protected void setInactive() {

    }
}
