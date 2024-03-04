package com.kingmarco.forge;

import com.kingmarco.observers.EventSystem;
import com.kingmarco.observers.Observer;
import com.kingmarco.observers.events.Event;
import com.kingmarco.observers.events.EventType;
import com.kingmarco.physics2d.Physics2D;
import com.kingmarco.renderer.*;
import com.kingmarco.scenes.LevelEditorSceneInitializer;
import com.kingmarco.scenes.LevelGameSceneInitializer;
import com.kingmarco.scenes.Scene;
import com.kingmarco.scenes.SceneInitializer;
import com.kingmarco.util.AssetPool;
import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.openal.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.windows.WinBase;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {
    private static Scene currentScene;
    private static Window window = null;
    private final String title;
    private int[] width, height;
    private final int finalWidth, finalHeight;
    private float posX, posY;
    private long glfwWindow;
    private ImGuiLayer imGuiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;
    public float r, g, b, a;
    private long audioContext;
    private long audioDevice;
    private boolean runtimePlaying = false;
    private String nameAudioDevice = "";

    private Window(){
        this.width = new int[]{1920};
        this.height = new int[]{1080};
        this.finalWidth = 1920;
        this.finalHeight = 1080;
        this.posX = 0f;
        this.posY = 0f;
        this.title = "NeonForge";
        EventSystem.addObserver(this);
        r = 0.36f;
        g = 0.24f;
        b = 0.26f;
        a = 1;
    }

    public static void changeScene(SceneInitializer sceneInitializer){
        if (currentScene != null){
            currentScene.destroy();
        }
        getImGuiLayer().getPropertiesWindow().setActiveGameObject(null);
        currentScene = new Scene(sceneInitializer);
        currentScene.load();
        currentScene.init();
        currentScene.awake();
    }

    public static Window get() {
       if (Window.window == null){
           Window.window = new Window();
       }
       return Window.window;
    }

    public static Physics2D getPhysics() { return currentScene.getPhysics();}

    public static Scene getScene() {
        return currentScene;
    }

    public void run() {
        System.out.println("Hello LWJGL "+ Version.getVersion()+ "!");

        init();
        loop();

        //Destroy the audio context
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);

        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if(!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);


        // Create the window
        glfwWindow = glfwCreateWindow(this.width[0], this.height[0], this.title, NULL, NULL);
        glfwGetWindowSize(glfwWindow, this.width, this.height);
        //glfwSetWindowMonitor(glfwWindow, NULL,0,0, this.width[0], this.height[0], GLFW_DONT_CARE);

        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window");
        }

        // Event Listeners Callback
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });
        glfwSetWindowPosCallback(glfwWindow, (p, newPosX, newPosY) -> {
            Window.setPosX(newPosX);
            Window.setPosY(newPosY);
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // Initialize the audio device
        changeAudioDevice();

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Blend the images
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        this.framebuffer = new Framebuffer(finalWidth, finalHeight);
        this.pickingTexture = new PickingTexture(finalWidth, finalHeight);
        glViewport(0, 0, finalWidth, finalHeight);

        this.imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        this.imGuiLayer.initImGui();

        Window.changeScene(new LevelEditorSceneInitializer());
    }

    private void changeAudioDevice(){
        // Initialize the audio device
        String defaultDeviceName = alcGetString(0, ALC11.ALC_ALL_DEVICES_SPECIFIER);
        if (nameAudioDevice.equals(defaultDeviceName)) return;
        nameAudioDevice = defaultDeviceName;

        audioDevice = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        assert alCapabilities.OpenAL10 : "Audio library not supported.";

        EventSystem.notify(null, new Event(EventType.AudioDeviceChanged));
    }

    public void loop() {
        float beginTime = (float) glfwGetTime();
        float endTime;
        float dt = -1.0f;
        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

        currentScene.start();
        while (!glfwWindowShouldClose(glfwWindow)){
            //change Audio Device when change
            changeAudioDevice();

            // Poll events
            glfwPollEvents();

            // Render pass 1. Render to picking texture
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

            glViewport(0, 0, finalWidth, finalHeight);
            glClearColor(0,0,0,0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting();
            glEnable(GL_BLEND);

            // Render pass 2. Render actual game
            DebugDraw.beginFrame();

            this.framebuffer.bind();
            Vector4f clearColor = currentScene.camera().getClearColor();
            glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                Renderer.bindShader(defaultShader);
                if (runtimePlaying) {
                    currentScene.update(dt);
                } else {
                    currentScene.editorUpdate(dt);
                }
                currentScene.render();
                DebugDraw.draw();
            }

            this.framebuffer.unbind();

            this.imGuiLayer.update(dt, currentScene);
            KeyListener.endFrame();
            MouseListener.endFrame();
            glfwSwapBuffers(glfwWindow);


            endTime = (float) glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    public static int getWidth() {
        return get().width[0];
    }

    public static int getHeight() {
        return get().height[0];
    }

    public static void setWidth(int newWidth) {
        get().width[0] = newWidth;
    }

    public static void setHeight(int newHeight) {
        get().height[0] = newHeight;
    }

    public static int getFinalWidth() {
        return get().finalWidth;
    }

    public static int getFinalHeight() {
        return get().finalHeight;
    }

    public static float getPosX(){
        return get().posX;
    }

    public static float getPosY(){
        return get().posY;
    }

    public static void setPosX(float newPosX){
        get().posX = newPosX;
    }

    public static void setPosY(float newPosY){
        get().posY = newPosY;
    }

    public static Framebuffer getFramebuffer() {
        return get().framebuffer;
    }

    public static float getTargetAspectRatio() {
        return  16.0f / 9.0f;
    }

    public static ImGuiLayer getImGuiLayer() {
        return get().imGuiLayer;
    }

    @Override
    public void onNotify(GameObject object, Event event) {
        switch (event.type){
            case GameEngineStartPlay:
                this.runtimePlaying = true;
                currentScene.save();
                Window.changeScene(new LevelGameSceneInitializer());
                break;
            case GameEngineStopPlay:
                this.runtimePlaying = false;
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case LoadLevel:
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case SaveLevel:
                currentScene.save();
                break;
        }
    }
}
