package com.kingmarco.forge;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * A class responsible to provides functionality for handling keyboard input.
 * It tracks whether keys are pressed or released and maintains a state for each key.
 */
public class KeyListener {
    private static KeyListener instance = null;
    private boolean keyPressed[] = new boolean[350];
    private boolean keyBeginPress[] = new boolean[350];

    private KeyListener() {

    }

    /**
     * Clears the state of keys at the end of each frame.
     */
    public static void endFrame() {
        Arrays.fill(get().keyBeginPress, false);
    }

    /**
     * Retrieves the singleton instance of the `KeyListener`.
     *
     * @return The `KeyListener` instance.
     */
    public static KeyListener get() {
        if (KeyListener.instance == null){
            KeyListener.instance = new KeyListener();
        }
        return KeyListener.instance;
    }

    /**
     * Callback method invoked when a key event occurs.
     *
     * @param window   The window associated with the key event.
     * @param key      The key code.
     * @param scancode The platform-specific scancode.
     * @param action   The action (GLFW_PRESS or GLFW_RELEASE).
     * @param mods     Modifier flags.
     */
    public static void keyCallback(long window, int key, int scancode, int action, int mods){
        if (action == GLFW_PRESS){
            get().keyPressed[key] = true;
            get().keyBeginPress[key] = true;
        } else if (action == GLFW_RELEASE) {
            get().keyPressed[key] = false;
            get().keyBeginPress[key] = false;
        }
    }

    /**
     * Checks if a specific key is currently pressed.
     *
     * @param keyCode The key code of the desired key.
     * @return `true` if the key is pressed, otherwise `false`.
     */
    public static boolean isKeyPressed(int keyCode) {
        return get().keyPressed[keyCode];
    }

    /**
     * Checks if a specific key has just been pressed (beginning of the press).
     *
     * @param keyCode The key code of the desired key.
     * @return `true` if the key has just been pressed, otherwise `false`.
     */
    public static boolean keyBeginPress(int keyCode){
        return get().keyBeginPress[keyCode];
    }
}
