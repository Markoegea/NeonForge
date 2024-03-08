package com.kingmarco.components;

import com.kingmarco.editor.PropertiesWindow;
import com.kingmarco.forge.GameObject;
import com.kingmarco.forge.KeyListener;
import com.kingmarco.forge.MouseListener;
import com.kingmarco.forge.Window;
import com.kingmarco.renderer.DebugDraw;
import com.kingmarco.renderer.DrawLines;
import com.kingmarco.renderer.PickingTexture;
import com.kingmarco.scenes.Scene;
import com.kingmarco.util.Settings;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

/**
 * Class responsible for the mouse shortcuts that control the selected game objects.
 * */
public class MouseControls extends Component {
    GameObject holdingObject = null;
    private float debounceTime = 0.05f;
    private float debounce = debounceTime;
    private boolean boxSelectSet = false;
    private Vector2f boxSelectSart = new Vector2f();
    private Vector2f boxSelectEnd = new Vector2f();
    private DrawLines drawLines = new DrawLines(Integer.MAX_VALUE);
    protected PropertiesWindow propertiesWindow;

    public MouseControls(PropertiesWindow propertiesWindow) {
        this.propertiesWindow = propertiesWindow;
        DebugDraw.addDrawLines(drawLines);
    }

    /**
     * Pick up a game object to place it in the scene
     * @param go Game object that will be placed in the scene
     * */
    public void pickupObject(GameObject go) {
        if (this.holdingObject != null){
           this.holdingObject.destroy();
        }
        this.holdingObject = go;
        this.holdingObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.8f,0.8f,0.8f,0.5f));
        Window.getScene().addGameObjectToScene(go);
    }

    /**
     * Place the game object in the scene
     * */
    public void place() {
        GameObject newObj = this.holdingObject.copy();
        newObj.getComponent(SpriteRenderer.class).setColor(new Vector4f(1, 1, 1, 1));
        if (newObj.getComponent(StateMachine.class) != null) {
            newObj.getComponent(StateMachine.class).refreshTextures();
        }
        Window.getScene().addGameObjectToScene(newObj);
    }

    /**
     * Get the selected game object, change their setting and place in the scene based in the mouse listener
     *
     * @param dt The time elapsed since the last update (in seconds).
     * */
    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;
        PickingTexture pickingTexture = Window.getImGuiLayer().getPropertiesWindow().getPickingTexture();
        Scene currentScene = Window.getScene();
        if (holdingObject != null){
            float x = MouseListener.getWorldX();
            float y = MouseListener.getWorldY();
            holdingObject.transform.position.x = ((int)Math.floor(x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH) + Settings.GRID_WIDTH / 2.0f;
            holdingObject.transform.position.y = ((int)Math.floor(y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) + Settings.GRID_HEIGHT / 2.0f;
            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
                float halfWidth = Settings.GRID_WIDTH / 2.0f;
                float halfHeight = Settings.GRID_HEIGHT / 2.0f;
                if (MouseListener.isDragging() &&
                        !blockInSquare(holdingObject.transform.position.x - halfWidth,
                                holdingObject.transform.position.y - halfHeight)){
                    place();
                } else if (!MouseListener.isDragging() && debounce < 0) {
                    place();
                    debounce = debounceTime;
                }
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
                holdingObject.destroy();
                holdingObject = null;
            }
        } else if (MouseListener.mouseButtonDown((GLFW_MOUSE_BUTTON_LEFT)) && !MouseListener.isDragging()){
            int x = (int) MouseListener.getScreenX();
            int y = (int) MouseListener.getScreenY();
            int gameObjectId = pickingTexture.readPixel(x, y);
            GameObject selectedGameObj = currentScene.getGameObject(gameObjectId);
            if (selectedGameObj != null) {
                Window.getImGuiLayer().getPropertiesWindow().setActiveGameObject(selectedGameObj);
            } else if (!MouseListener.isDragging()){
                Window.getImGuiLayer().getPropertiesWindow().clearSelected();
            }
        } else if (MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)
                && holdingObject == null && propertiesWindow.getActiveGameObject() == null){
            if (!boxSelectSet){
                Window.getImGuiLayer().getPropertiesWindow().clearSelected();
                boxSelectSart = MouseListener.getScreen();
                boxSelectSet = true;
            }
            boxSelectEnd = MouseListener.getScreen();
            Vector2f boxSelectStartWorld = MouseListener.screenToWorld(boxSelectSart);
            Vector2f boxSelectEndWorld = MouseListener.screenToWorld(boxSelectEnd);
            Vector2f halfSize =
                    (new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f);
            drawLines.addBox2D(new Vector2f(boxSelectStartWorld).add(halfSize),
                    new Vector2f(halfSize).mul(2.0f), 0.0f);
        } else if (boxSelectSet) {
            boxSelectSet = false;
            int screenStartX = (int)boxSelectSart.x;
            int screenStartY = (int)boxSelectSart.y;
            int screenEndX = (int)boxSelectEnd.x;
            int screenEndY = (int)boxSelectEnd.y;
            boxSelectSart.zero();
            boxSelectEnd.zero();

            if (screenEndX < screenStartX) {
                int tmp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = tmp;
            }
            if (screenEndY < screenStartY) {
                int tmp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = tmp;
            }

            float[] gameObjectsIds = pickingTexture.readPixels(
                    new Vector2i(screenStartX, screenStartY),
                    new Vector2i(screenEndX, screenEndY)
            );
            Set<Integer> uniqueGameObjectsIds = new HashSet<>();

            for (float objId : gameObjectsIds){
                uniqueGameObjectsIds.add((int) objId);
            }

            for (Integer gameObjectId : uniqueGameObjectsIds) {
                GameObject pickedObj = Window.getScene().getGameObject(gameObjectId);
                if (pickedObj != null) {
                    Window.getImGuiLayer().getPropertiesWindow().addActiveGameObject(pickedObj);
                }
            }
        }
    }

    /**
     * Check if in the square grid is a game object with a sprite renderer
     *
     * @return true if there is a game object in a square grid or false if there is none
     * */
    private boolean blockInSquare(float x, float y) {
        PropertiesWindow propertiesWindow = Window.getImGuiLayer().getPropertiesWindow();
        Vector2f start = new Vector2f(x, y);
        Vector2f end = new Vector2f(start).add(new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT));
        Vector2f startScreenf = MouseListener.worldToScreen(start);
        Vector2f endScreenf = MouseListener.worldToScreen(end);
        Vector2i startScreen = new Vector2i((int)startScreenf.x + 2, (int)startScreenf.y + 2);
        Vector2i endScreen = new Vector2i((int)endScreenf.x - 2, (int)endScreenf.y - 2);
        float[] gameObjectIds = propertiesWindow.getPickingTexture().readPixels(startScreen, endScreen);

        for (int i = 0; i < gameObjectIds.length; i++){
            if (gameObjectIds[i] >= 0){
                GameObject pickedObj = Window.getScene().getGameObject((int)gameObjectIds[i]);
                if (pickedObj.getComponent(SpriteRenderer.class) != null) {
                    return true;
                }
            }
        }
        return false;
    }

}
