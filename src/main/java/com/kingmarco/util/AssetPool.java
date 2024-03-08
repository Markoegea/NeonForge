package com.kingmarco.util;

import com.kingmarco.components.SpritesSheet;
import com.kingmarco.forge.Sound;
import com.kingmarco.renderer.Shader;
import com.kingmarco.renderer.Texture;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The AssetPool class manages the assets used in the game, including shaders, textures, sprite sheets, and sounds.
 *
 * This class provides static methods to get shaders, textures, sprite sheets, and sounds by their resource names,
 * add sprite sheets and sounds to the asset pool, and get all sounds in the asset pool.
 */
public class AssetPool {
    private static final Map<String, Shader> shaders = new HashMap<>();
    private static final Map<String, Texture> textures = new HashMap<>();
    private static final Map<String, SpritesSheet> spritesheets = new HashMap<>();
    private static final Map<String, Sound> sounds = new HashMap<>();

    /**
     * Returns the shader with the specified resource name.
     *
     * This method checks if the shader is already in the asset pool, and if not, it creates a new shader, compiles it, adds it to the asset pool, and returns it.
     *
     * @param resourceName The resource name of the shader.
     * @return The shader with the specified resource name.
     */
    public static Shader getShader(String resourceName){
        File file = new File(resourceName);
        if (AssetPool.shaders.containsKey(file.getAbsolutePath())){
            return AssetPool.shaders.get(file.getAbsolutePath());
        } else {
            Shader shader = new Shader(resourceName);
            shader.compile();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    /**
     * Returns the texture with the specified resource name.
     *
     * This method checks if the texture is already in the asset pool, and if not, it creates a new texture, initializes it, adds it to the asset pool, and returns it.
     *
     * @param resourceName The resource name of the texture.
     * @return The texture with the specified resource name.
     */
    public static Texture getTexture(String resourceName){
        File file = new File(resourceName);
        if (AssetPool.textures.containsKey(file.getAbsolutePath())){
            return AssetPool.textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture();
            texture.init(resourceName);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    /**
     * Adds a sprite sheet to the asset pool.
     *
     * This method checks if the sprite sheet is already in the asset pool, and if not, it adds it to the asset pool.
     *
     * @param resourceName The resource name of the sprite sheet.
     * @param spritesSheet The sprite sheet to be added.
     */
    public static void addSpriteSheet(String resourceName, SpritesSheet spritesSheet){
        File file = new File(resourceName);
        if (!AssetPool.spritesheets.containsKey(file.getAbsolutePath())) {
            AssetPool.spritesheets.put(file.getAbsolutePath(), spritesSheet);
        }
    }

    /**
     * Returns the sprite sheet with the specified resource name.
     *
     * This method checks if the sprite sheet is in the asset pool, and if so, it returns it. If not, it asserts an error.
     *
     * @param resourceName The resource name of the sprite sheet.
     * @return The sprite sheet with the specified resource name.
     */
    public static SpritesSheet getSpriteSheet(String resourceName) {
        File file = new File(resourceName);
        if (!AssetPool.spritesheets.containsKey(file.getAbsolutePath())){
            assert false : "Error: Tried to access spriteSheet '" + resourceName + "' and it has not been added to asset pool";
        }
        return AssetPool.spritesheets.getOrDefault(file.getAbsolutePath(), null);
    }

    /**
     * Returns all sounds in the asset pool.
     *
     * @return A collection of all sounds in the asset pool.
     */
    public static Collection<Sound> getAllSounds() {
        return sounds.values();
    }

    /**
     * Returns the sound with the specified resource name.
     *
     * This method checks if the sound is in the asset pool, and if so, it returns it. If not, it asserts an error.
     *
     * @param soundFile The resource name of the sound.
     * @return The sound with the specified resource name.
     */
    public static Sound getSound(String soundFile) {
        File file = new File(soundFile);
        if (sounds.containsKey(file.getAbsolutePath())){
            return sounds.get(file.getAbsolutePath());
        } else {
            assert false : "Sound file not added '" + soundFile + "'";
        }
        return null;
    }

    /**
     * Adds a sound to the asset pool and returns it.
     *
     * This method checks if the sound is already in the asset pool, and if not, it creates a new sound, adds it to the asset pool, and returns it.
     *
     * @param soundFile The resource name of the sound.
     * @param loops Whether the sound should loop.
     * @return The sound that was added.
     */
    public static Sound addSound(String soundFile, boolean loops) {
        File file = new File(soundFile);
        if (sounds.containsKey(file.getAbsolutePath())){
            return sounds.get(file.getAbsolutePath());
        } else {
           Sound sound = new Sound(file.getAbsolutePath(), loops);
           AssetPool.sounds.put(file.getAbsolutePath(), sound);
           return sound;
        }
    }
}
