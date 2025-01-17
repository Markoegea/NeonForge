package com.kingmarco.forge;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * A class responsible for the mouse position in the screen and the world scene.
 * */
public class MouseListener {
    private static MouseListener instance = null;
    private double scrollX, scrollY;
    private double xPos, yPos, lastPosX, lastPosY;
    private final boolean[] mouseButtonPressed = new boolean[9];
    private boolean isDragging;
    private int mouseButtonDown = 0;
    private Vector2f gameViewportPos = new Vector2f();
    private Vector2f gameViewportSize = new Vector2f();
    private Vector2f gameViewportDistance = new Vector2f();

    private MouseListener() {
        this.scrollX = 0.0f;
        this.scrollY = 0.0f;
        this.xPos = 0.0f;
        this.yPos = 0.0f;
        this.lastPosX = 0.0f;
        this.lastPosY = 0.0f;
    }

    public static MouseListener get() {
        if (MouseListener.instance == null){
            MouseListener.instance = new MouseListener();
        }
        return MouseListener.instance;
    }

    /**
     * Reset the mouse values
     * */
    public static void clear() {
        get().scrollX = 0.0f;
        get().scrollY = 0.0f;
        get().xPos = 0.0f;
        get().yPos = 0.0f;
        get().lastPosX = 0.0f;
        get().lastPosY = 0.0f;
        get().mouseButtonDown = 0;
        get().isDragging = false;
        Arrays.fill(get().mouseButtonPressed, false);
    }

    /**
     * Reset values at the end of each frame
     * */
    public static void endFrame() {
        get().scrollY = 0.0f;
        get().scrollX = 0.0f;
        get().lastPosX = get().xPos;
        get().lastPosY = get().yPos;
    }

    /**
     * Callback that register the mouse position
     *
     * @param window  The window associated with the key event.
     * @param xpos  Mouse position in x.
     * @param ypos  Mouse position in y.
     * */
    public static void mousePosCallback(long window, double xpos, double ypos){
        if (!Window.getImGuiLayer().getGameViewWindow().getWantCaptureMouse()){
            clear();
        }
        if (get().mouseButtonDown > 0) {
            get().isDragging = true;
        }
        get().lastPosX = get().xPos;
        get().lastPosY = get().yPos;
        get().xPos = xpos;
        get().yPos = ypos;
    }

    /**
     * Callback that register the mouse button and state
     *
     * @param window   The window associated with the key event.
     * @param button   The selected mouse button.
     * @param action   The action (GLFW_PRESS or GLFW_RELEASE).
     * @param mods     Modifier flags.
     * */
    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS){
            get().mouseButtonDown++;
            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE){
            get().mouseButtonDown--;
            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = false;
                get().isDragging = false;
            }
        }
    }

    /**
     * Callback that register the scroll value in x and y.
     *
     * @param window   The window associated with the key event.
     * @param xOffset  The scroll value in x.
     * @param yOffset  The scroll value in y.
     * */
    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    /**
     * Return mouse position in x.
     *
     * @return float value of the x position.
     * */
    public static float getX() {
        return (float)get().xPos;
    }

    /**
     * Return mouse position in y.
     *
     * @return float value of the y position.
     * */
    public static float getY() {
        return (float)get().yPos;
    }

    /**
     * Return mouse position in y.
     *
     * @return float value of the y position.
     * */
    public static float getScrollX() {
        return (float)get().scrollX;
    }

    /**
     * Return mouse position in y.
     *
     * @return float value of the y position.
     * */
    public static float getScrollY() {
        return (float)get().scrollY;
    }

    /**
     * Return of the mouse is being dragging.
     *
     * @return True if the mouse is being dragging or False otherwise.
     * */
    public static boolean isDragging() {
        return get().isDragging;
    }

    /**
     * Return of the mouse specified mouse button is being pressed.
     *
     * @param button index of the mouse button.
     * @return True if the specified mouse button is being press or False otherwise.
     * */
    public static boolean mouseButtonDown(int button){
        if (button < get().mouseButtonPressed.length){
            return get().mouseButtonPressed[button];
        } else {
            return false;
        }
    }

    /**
     * Retrieves a float value representing the screen x value.
     * @return A float containing the screen value in x.
     */
    public static float getScreenX() {
        return getScreen().x;
    }

    /**
     * Retrieves a float value representing the screen y value.
     * @return A float containing the screen value in y.
     */
    public static float getScreenY(){
        return getScreen().y;
    }


    /**
     * Calculates the screen position of the mouse.
     *
     * Use the finalWidth and finalHeight, because the coordinates
     * in the glviewport, is very sensitive with the coordinates
     *
     * @return A {@link Vector2f} containing the screen coordinates (x, y)
     * */
    public static Vector2f getScreen() {
        float mousePosX = (get().gameViewportDistance.x + (get().gameViewportPos.x / 2f));
        float currentX = (getX() - mousePosX);
        currentX = (currentX / get().gameViewportSize.x) * Window.getFinalWidth();

        float mousePosY = (get().gameViewportDistance.y + (get().gameViewportPos.y / 2f));
        float currentY =  (getY() - mousePosY);
        currentY = Window.getFinalHeight() - ((currentY / get().gameViewportSize.y) * Window.getFinalHeight());

        return new Vector2f(currentX, currentY);
    }

    /**
     * Calculates the difference in the x-coordinate of the screen position.
     * @return The difference in x-coordinate (dx) between the last position and the current position.
     */
    public static float getScreenDx() {
        return (float)(get().lastPosX - get().xPos);
    }

    /**
     * Calculates the difference in the y-coordinate of the screen position.
     * @return The difference in y-coordinate (dy) between the last position and the current position.
     */
    public static float getScreenDy() {
        return (float)(get().lastPosY - get().yPos);
    }

    /**
     * Retrieves a 2D vector representing the screen displacement.
     * @return A {@link Vector2f} containing the screen displacement (dx, dy).
     */
    public static Vector2f getScreenD(){
        return new Vector2f(getScreenDx(), getScreenDy());
    }

    /**
     * Retrieves the x-coordinate of the world position.
     * @return The x-coordinate of the world position.
     */
    public static float getWorldX() {
        return getWorld().x;
    }

    /**
     * Retrieves the y-coordinate of the world position.
     * @return The y-coordinate of the world position.
     */
    public static float getWorldY() {
        return getWorld().y;
    }

    /**
     * Retrieves a 2D vector representing the world position.
     * @return A {@link Vector2f} containing the world position (x, y).
     */
    public static Vector2f getWorld() {

        float mousePosX = (get().gameViewportDistance.x + (get().gameViewportPos.x / 2f)) - Window.getPosX();
        float currentX = (getX() - mousePosX);
        currentX = (2.0f * (currentX / get().gameViewportSize.x)) - 1.0f;

        float mousePosY = (get().gameViewportDistance.y + (get().gameViewportPos.y / 2f)) - Window.getPosY();
        float currentY = (getY() - mousePosY);
        currentY = (2.0f * (1.0f - (currentY / get().gameViewportSize.y))) - 1.0f;

        Vector4f tmp = new Vector4f(currentX, currentY, 0, 1);

        Camera camera = Window.getScene().camera();
        Matrix4f inverseView = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        tmp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(tmp.x, tmp.y);
    }

    /**
     * Converts screen coordinates to world coordinates.
     *
     * @param screenCoords The screen coordinates to be converted.
     * @return The world coordinates corresponding to the given screen coordinates.
     */
    public static Vector2f screenToWorld(Vector2f screenCoords){
        Vector2f normalizedScreenCords = new Vector2f(
                screenCoords.x / Window.getWidth(),
                screenCoords.y / Window.getHeight()
        );
        normalizedScreenCords.mul(2.0f).sub(new Vector2f(1.0f, 1.0f));
        Camera camera = Window.getScene().camera();
        Vector4f tmp = new Vector4f(normalizedScreenCords.x, normalizedScreenCords.y, 0, 1);
        Matrix4f inverseView = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        tmp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(tmp.x, tmp.y);
    }

    /**
     * Converts world coordinates to screen coordinates.
     *
     * @param worldCoords The world coordinates to be converted.
     * @return The screen coordinates corresponding to the given world coordinates.
     */
    public static Vector2f worldToScreen(Vector2f worldCoords){
        Camera camera = Window.getScene().camera();
        Vector4f ndcSpacePos = new Vector4f(worldCoords.x, worldCoords.y, 0, 1);
        Matrix4f view = new Matrix4f(camera.getViewMatrix());
        Matrix4f projection = new Matrix4f(camera.getProjectionMatrix());
        ndcSpacePos.mul(projection.mul(view));
        Vector2f windowSpace = new Vector2f(ndcSpacePos.x, ndcSpacePos.y).mul(1.0f / ndcSpacePos.w);
        windowSpace.add(new Vector2f(1.0f, 1.0f)).mul(0.5f);
        windowSpace.mul(new Vector2f(Window.getWidth(), Window.getHeight()));

        return windowSpace;
    }

    /**
     * Sets the game viewport position.
     *
     * @param gameViewportPos The new game viewport position.
     */
    public static void setGameViewportPos(Vector2f gameViewportPos) {
        get().gameViewportPos.set(gameViewportPos);
    }

    /**
     * Sets the game viewport size.
     *
     * @param gameViewportSize The new game viewport size.
     */
    public static void setGameViewportSize(Vector2f gameViewportSize) {
        get().gameViewportSize.set(gameViewportSize);
    }

    /**
     * Sets the game viewport distance.
     *
     * @param gameViewportDistance The new game viewport distance.
     */
    public static void setGameViewportDistance(Vector2f gameViewportDistance) {
        get().gameViewportDistance.set(gameViewportDistance);
    }
}
