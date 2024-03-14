package com.kingmarco.GameFunctionality;

import com.kingmarco.components.*;
import com.kingmarco.forge.GameObject;
import com.kingmarco.scenes.Scene;
import com.kingmarco.scenes.SceneInitializer;
import com.kingmarco.util.AssetPool;

/**
 * This class initializes the level game scene.
 * It extends the SceneInitializer class.
 */
public class LevelGameSceneInitializer extends SceneInitializer {
    private GameObject levelGameStuff;

    public LevelGameSceneInitializer(){

    }

    /**
     * Initializes the given scene with the necessary components for the level game.
     * It creates the level game object, adds the necessary components to it, starts it, and adds it to the scene.
     *
     * @param scene The scene to initialize.
     */
    @Override
    public void init(Scene scene) {
        levelGameStuff = scene.createGameObject("LevelGame");
        levelGameStuff.addComponent(new GameCamera(scene.camera()));
        levelGameStuff.start();
        scene.addGameObjectToScene(levelGameStuff);
    }

    /**
     * Loads the resources needed for the scene.
     * It loads shaders, textures, sprite sheets, and sounds from the asset pool, and refreshes the textures of the game objects in the scene.
     *
     * @param scene The scene for which to load resources.
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
     * Currently, it does not perform any actions.
     */
    public void start() {

    }

    /**
     * This method is used for ImGui rendering.
     * Currently, it does not perform any actions.
     */
    @Override
    public void imgui() {

    }
}
