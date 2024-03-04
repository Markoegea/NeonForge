package com.kingmarco.forge;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

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

    public static void endFrame() {
        get().scrollY = 0.0f;
        get().scrollX = 0.0f;
        get().lastPosX = get().xPos;
        get().lastPosY = get().yPos;
    }

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

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static float getX() {
        return (float)get().xPos;
    }

    public static float getY() {
        return (float)get().yPos;
    }

    public static float getScrollX() {
        return (float)get().scrollX;
    }

    public static float getScrollY() {
        return (float)get().scrollY;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button){
        if (button < get().mouseButtonPressed.length){
            return get().mouseButtonPressed[button];
        } else {
            return false;
        }
    }

    public static float getScreenX() {
        return getScreen().x;
    }

    public static float getScreenY(){
        return getScreen().y;
    }

    /**I need to use the finalWidth and finalHeight, because I use the coordinates
     * in the glviewport, and this is very sensitive with the coordinates
     * */
    public static Vector2f getScreen() {
        float mousePosX = (get().gameViewportDistance.x + (get().gameViewportPos.x / 2f)) - Window.getPosX();
        float currentX = (getX() - mousePosX);
        currentX = (currentX / get().gameViewportSize.x) * Window.getFinalWidth();

        float mousePosY = (get().gameViewportDistance.y + (get().gameViewportPos.y / 2f)) - Window.getPosY();
        float currentY = (getY() - mousePosY);
        currentY = Window.getFinalHeight() - ((currentY / get().gameViewportSize.y) * Window.getFinalHeight());

        return new Vector2f(currentX, currentY);
    }

    public static float getScreenDx() {
        return (float)(get().lastPosX - get().xPos);
    }

    public static float getScreenDy() {
        return (float)(get().lastPosY - get().yPos);
    }

    public static Vector2f getScreenD(){
        return new Vector2f(getScreenDx(), getScreenDy());
    }

    public static float getWorldX() {
        return getWorld().x;
    }

    public static float getWorldY() {
        return getWorld().y;
    }

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

    public static Vector2f screenToWorld(Vector2f screenCoords){
        Vector2f normalizedScreenCords = new Vector2f(
                screenCoords.x / Window.getFinalWidth(),
                screenCoords.y / Window.getFinalHeight()
        );
        normalizedScreenCords.mul(2.0f).sub(new Vector2f(1.0f, 1.0f));
        Camera camera = Window.getScene().camera();
        Vector4f tmp = new Vector4f(normalizedScreenCords.x, normalizedScreenCords.y, 0, 1);
        Matrix4f inverseView = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        tmp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(tmp.x, tmp.y);
    }

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

    public static void setGameViewportPos(Vector2f gameViewportPos) {
        get().gameViewportPos.set(gameViewportPos);
    }

    public static void setGameViewportSize(Vector2f gameViewportSize) {
        get().gameViewportSize.set(gameViewportSize);
    }

    public static void setGameViewportDistance(Vector2f gameViewportDistance) {
        get().gameViewportDistance.set(gameViewportDistance);
        System.out.println(gameViewportDistance.x +" "+ gameViewportDistance.y);
    }
}
