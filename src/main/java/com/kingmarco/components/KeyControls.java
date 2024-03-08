package com.kingmarco.components;

import com.kingmarco.editor.PropertiesWindow;
import com.kingmarco.forge.GameObject;
import com.kingmarco.forge.KeyListener;
import com.kingmarco.forge.Window;
import com.kingmarco.util.Settings;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Class responsible for the keyboard shortcuts that control the selected game objects.
 * */
public class KeyControls extends Component{
    // TODO: Refactor Code

    /**
     * Get the selected game objects and change their setting based in the keyboard listener
     *
     * @param dt The time elapsed since the last update (in seconds).
     * */
    @Override
    public void editorUpdate(float dt) {
        PropertiesWindow propertiesWindow = Window.getImGuiLayer().getPropertiesWindow();
        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
        List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjects();
        float multiplier = KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT) ? 0.1f : 1.0f;

        if (activeGameObjects == null) return;
        if (activeGameObjects.isEmpty()) return;

        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyListener.keyBeginPress(GLFW_KEY_D) && activeGameObjects.size() > 1){
            List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
            propertiesWindow.clearSelected();
            for (GameObject go : gameObjects) {
                GameObject copy = go.copy();
                Window.getScene().addGameObjectToScene(copy);
                propertiesWindow.addActiveGameObject(copy);
                if (copy.getComponent(StateMachine.class) != null) {
                    copy.getComponent(StateMachine.class).refreshTextures();
                }
            }
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyListener.keyBeginPress(GLFW_KEY_C)){
            if (activeGameObject == null) return;
            GameObject newObj = activeGameObject.copy();
            Window.getScene().addGameObjectToScene(newObj);
            newObj.transform.position.add(Settings.GRID_WIDTH, 0.0f);
            propertiesWindow.setActiveGameObject(newObj);
            if (newObj.getComponent(StateMachine.class) != null) {
                newObj.getComponent(StateMachine.class).refreshTextures();
            }
        } else if (KeyListener.keyBeginPress(GLFW_KEY_DELETE)){
            for (GameObject go : activeGameObjects) {
                go.destroy();
            }
            propertiesWindow.clearSelected();
        } else if (KeyListener.isKeyPressed(GLFW_KEY_PAGE_DOWN)){
            for (GameObject go : activeGameObjects){
                go.transform.zIndex--;
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_PAGE_UP)){
            for (GameObject go : activeGameObjects){
                go.transform.zIndex++;
            }
        }  else if (KeyListener.isKeyPressed(GLFW_KEY_UP)){
            for (GameObject go : activeGameObjects){
                go.transform.position.y += Settings.GRID_HEIGHT * multiplier;
            }
        }  else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT)){
            for (GameObject go : activeGameObjects){
                go.transform.position.x -= Settings.GRID_WIDTH * multiplier;
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT)){
            for (GameObject go : activeGameObjects){
                go.transform.position.x += Settings.GRID_WIDTH * multiplier;
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_DOWN)){
            for (GameObject go : activeGameObjects){
                go.transform.position.y -= Settings.GRID_HEIGHT * multiplier;
            }
        }
    }
}
