package com.kingmarco.GameFunctionality;

import com.kingmarco.components.*;
import com.kingmarco.forge.GameObject;

/**
 * This class provides utility methods for generating game objects and prefabs.
 * It delegates the generation to the MyPrefabs class.
 */
public class Prefabs {

    //TODO: Your Code here
    /**
     * Generates a game object with a sprite by delegating to the MyPrefabs class.
     *
     * @param sprite The sprite to be added to the game object.
     * @param sizeX The x-size of the game object.
     * @param sizeY The y-size of the game object.
     * @return The generated game object.
     */
    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY){
        return MyPrefabs.generateSpriteObject(sprite,sizeX,sizeY);
    }

    /**
     * Generates a prefab game object from a file by delegating to the MyPrefabs class.
     *
     * @param file The file from which to generate the prefab.
     * @return The generated prefab game object.
     */
    public static GameObject generatePrefab(String file){
        return MyPrefabs.generatePrefab(file);
    }
}
