package com.kingmarco.components;

import com.kingmarco.editor.PropertiesWindow;
import com.kingmarco.forge.MouseListener;
import org.joml.Vector2f;

/**
 * Class responsible for create the gizmo that scale the game object
 * */
public class ScaleGizmo extends Gizmo {
    public ScaleGizmo(Sprite scalesSprite, PropertiesWindow propertiesWindow){
        super(scalesSprite, propertiesWindow);
    }

    /**
     * Update the game object scale depends on the mouse position
     *
     * @param dt The time elapsed since the last update (in seconds).
     * */
    @Override
    public void editorUpdate(float dt) {
        if (deactivate) return;
        if (activeGameObject != null) {
            Vector2f delta = MouseListener.screenToWorld(MouseListener.getScreenD());
            if (xAxisActive && !yAxisActive) {
                activeGameObject.transform.scale.x -= delta.x;
            } else if (yAxisActive) {
                activeGameObject.transform.scale.y -= -delta.y;
            }
        }
        super.editorUpdate(dt);
    }
}
