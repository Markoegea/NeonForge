package com.kingmarco.scenes;

/**
 * This abstract class provides a structure for initializing scenes.
 * It contains abstract methods for initializing the scene, loading resources, and handling ImGui rendering.
 */
public abstract class SceneInitializer {

    /**
     * Initializes the given scene.
     * This method should be overridden by subclasses to provide specific initialization logic.
     *
     * @param scene The scene to initialize.
     */
    public abstract void init(Scene scene);

    /**
     * Loads the resources needed for the scene.
     * This method should be overridden by subclasses to provide specific resource loading logic.
     *
     * @param scene The scene for which to load resources.
     */
    public abstract void loadResources(Scene scene);

    /**
     * Handles ImGui rendering.
     * This method should be overridden by subclasses to provide specific ImGui rendering logic.
     */
    public abstract void imgui();
}
