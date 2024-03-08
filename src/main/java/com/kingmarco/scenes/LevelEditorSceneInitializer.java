package com.kingmarco.scenes;

import com.kingmarco.components.*;
import com.kingmarco.forge.GameObject;
import com.kingmarco.forge.Sound;
import com.kingmarco.forge.Window;
import com.kingmarco.physics2d.components.Box2DCollider;
import com.kingmarco.physics2d.components.RigidBody2D;
import com.kingmarco.physics2d.enums.BodyType;
import com.kingmarco.util.AssetPool;
import com.kingmarco.util.Settings;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;

import java.io.File;
import java.util.Collection;

/**
 * This class initializes the level editor scene, load the resources and UI elements.
 */
public class LevelEditorSceneInitializer extends SceneInitializer {

    private SpritesSheet tileMap;

    private GameObject levelEditorStuff;

    public LevelEditorSceneInitializer(){

    }

    /**
     * Initializes the given scene with the necessary components for the level editor.
     * It loads the sprite sheet, creates the level editor game object,
     * adds the necessary components to it, and adds it to the scene.
     *
     * @param scene The scene to initialize.
     */
    @Override
    public void init(Scene scene) {
        tileMap = AssetPool.getSpriteSheet("assets/texture/NinjaAdventure/Actor/Characters/BlackNinjaMage/SpriteSheet.png");
        SpritesSheet gizmos = AssetPool.getSpriteSheet("assets/texture/gizmos.png");
        levelEditorStuff = scene.createGameObject("LevelEditor");
        levelEditorStuff.setNoSerialize();
        levelEditorStuff.addComponent(new MouseControls(Window.getImGuiLayer().getPropertiesWindow()));
        levelEditorStuff.addComponent(new KeyControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(scene.camera()));
        levelEditorStuff.addComponent(new GizmoSystem(gizmos));
        scene.addGameObjectToScene(levelEditorStuff);
    }

    /**
     * This method is called when the scene starts.
     * To get the important files, resources, sprites, and sounds.
     */
    @Override
    public void loadResources(Scene scene) {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.getTexture("assets/texture/NinjaAdventure/Items/Food/Beaf.png");
        AssetPool.addSpriteSheet("assets/texture/NinjaAdventure/Actor/Characters/BlackNinjaMage/SpriteSheet.png",
                new SpritesSheet(
                        AssetPool.getTexture("assets/texture/NinjaAdventure/Actor/Characters/BlackNinjaMage/SpriteSheet.png"),
                        16, 16, 28, 0
                ));
        AssetPool.addSpriteSheet("assets/texture/NinjaAdventure/Backgrounds/Tilesets/Pipes.png",
                new SpritesSheet(
                        AssetPool.getTexture("assets/texture/NinjaAdventure/Backgrounds/Tilesets/Pipes.png"),
                        16, 16, 60, 0
                ));
        AssetPool.addSpriteSheet("assets/texture/NinjaAdventure/Actor/Boss/GiantRacoon/Idle.png",
                new SpritesSheet(
                        AssetPool.getTexture("assets/texture/NinjaAdventure/Actor/Boss/GiantRacoon/Idle.png"),
                        60, 60, 6, 0));
        AssetPool.addSpriteSheet("assets/texture/gizmos.png",
                new SpritesSheet(
                        AssetPool.getTexture("assets/texture/gizmos.png"),
                        24, 48, 3, 0
                ));
        AssetPool.addSpriteSheet("assets/texture/NinjaAdventure/Actor/Characters/BlackNinjaMage/SeparateAnim/Walk.png",
                new SpritesSheet(
                        AssetPool.getTexture("assets/texture/NinjaAdventure/Actor/Characters/BlackNinjaMage/SeparateAnim/Walk.png"),
                                16, 16, 16,0
                ));

        AssetPool.addSound("assets/texture/NinjaAdventure/Musics/5 - Peaceful.ogg",true);
        AssetPool.addSound("assets/texture/NinjaAdventure/Musics/7 - Sad Theme.ogg",false);
        AssetPool.addSound("assets/texture/NinjaAdventure/Musics/8 - End Theme.ogg",false);
        AssetPool.addSound("assets/texture/NinjaAdventure/Musics/12 - Temple.ogg",false);
        AssetPool.addSound("assets/texture/NinjaAdventure/Musics/16 - Melancholia.ogg",false);
        AssetPool.addSound("assets/texture/NinjaAdventure/Musics/20 - Good Time.ogg",false);
        AssetPool.addSound("assets/texture/NinjaAdventure/Musics/17 - Fight.ogg",false);
        AssetPool.addSound("assets/texture/NinjaAdventure/Musics/22 - Dream.ogg",false);
        AssetPool.addSound("assets/texture/NinjaAdventure/Musics/24 - Final Area.ogg",false);
        AssetPool.addSound("assets/texture/NinjaAdventure/Musics/26 - Lost Village.ogg",false);
        AssetPool.addSound("assets/texture/NinjaAdventure/Musics/30 - Ruins.ogg",false);
        AssetPool.addSound("assets/texture/NinjaAdventure/Sounds/Game/ogg/Jump.ogg",false);
        AssetPool.addSound("assets/texture/NinjaAdventure/Sounds/Game/ogg/MiniImpact.ogg",false);

        for (GameObject g: scene.getGameObjects()){
            if(g.getComponent(SpriteRenderer.class) == null) return;
            SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
            if (spr.getTexture() == null) return;
            spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));

            if(g.getComponent(StateMachine.class) == null) continue;
            StateMachine stateMachine = g.getComponent(StateMachine.class);
            stateMachine.refreshTextures();
        }
    }

    /**
     * This method is called when the scene starts.
     * */
    public void start() {
        /*if (gameObjects.isEmpty()){
            SpritesSheet spritesSheet = AssetPool.getSpriteSheet("assets/texture/NinjaAdventure/Actor/Boss/GiantRacoon/Idle.png");

            GameObject obj2 = new GameObject("Object 2",
                new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), 1);
            SpriteRenderer obj2Sprite = new SpriteRenderer();
            //obj2Sprite.setSprite(spritesSheet.getSprite(0));
            Sprite sprite = new Sprite();
            sprite.setTexture(AssetPool.getTexture("assets/texture/NinjaAdventure/Items/Food/Beaf.png"));
            obj2Sprite.setSprite(sprite);
            obj2.addComponent(obj2Sprite);
            obj2.addComponent(new Rigidbody());
            this.addGameObjectToScene(obj2);

            GameObject obj1 = new GameObject("Object 1",
                    new Transform(new Vector2f(500, 100), new Vector2f(256, 256)), 1);
            SpriteRenderer obj1Sprite = new SpriteRenderer();
            obj1Sprite.setSprite(spritesSheet.getSprite(0));
            obj1.addComponent(obj1Sprite);
            this.addGameObjectToScene(obj1);
        }*/
        //this.activeGameObject = gameObjects.get(0);
        //gameObjects.get(0).addComponent(new Rigidbody());
    }

    /**
     * Create and show ImGui elements and tabs.
     * */
    @Override
    public void imgui() {
        //System.out.println("X: " + MouseListener.getScreenX() + " Y: "+MouseListener.getScreenY());

        ImGui.begin("Level Editor Stuff");
        levelEditorStuff.imgui();
        ImGui.end();

        ImGui.begin("Objects");

        if (ImGui.beginTabBar("WindowTabBar")) {
            if (ImGui.beginTabItem("Blocks")) {
                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);

                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);

                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < tileMap.size(); i++) {
                    Sprite sprite = tileMap.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 3;
                    float spriteHeight = sprite.getHeight() * 3;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject object = Prefabs.generateSpriteObject(sprite, Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
                        /* TODO Have a ground floor*/
                        RigidBody2D rb = new RigidBody2D();
                        rb.setBodyType(BodyType.Static);
                        object.addComponent(rb);
                        Box2DCollider b2b = new Box2DCollider();
                        b2b.setHalfSize(new Vector2f(0.25f, 0.25f));
                        object.addComponent(b2b);
                        //object.addComponent(new BreakableBrick());
                        levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < tileMap.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Prefabs")){
                SpritesSheet playerSprites = AssetPool.getSpriteSheet("assets/texture/NinjaAdventure/Actor/Characters/BlackNinjaMage/SeparateAnim/Walk.png");
                Sprite sprite = playerSprites.getSprite(0);
                float spriteWidth = sprite.getWidth() * 3;
                float spriteHeight = sprite.getHeight() * 3;
                int id = sprite.getTexId();
                Vector2f[] texCoords = sprite.getTexCoords();

                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generatePrefab("assets/texture/NinjaAdventure/Actor/Characters/BlackNinjaMage/SeparateAnim/Walk.png");
                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.sameLine();

                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Sounds")) {
                Collection<Sound> sounds = AssetPool.getAllSounds();
                for (Sound sound : sounds) {
                    File tmp = new File(sound.getFilepath());
                    if (ImGui.button(tmp.getName())) {
                        if (!sound.isPlaying()){
                            sound.play();
                        } else {
                            sound.stop();
                        }
                    }

                    if (ImGui.getContentRegionAvailX() > 100) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
        ImGui.end();
    }
}
