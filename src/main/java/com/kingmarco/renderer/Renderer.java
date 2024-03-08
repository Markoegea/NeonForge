package com.kingmarco.renderer;

import com.kingmarco.components.SpriteRenderer;
import com.kingmarco.forge.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Renderer class handles the rendering of game objects.
 *
 * This class provides methods to add game objects and their sprites to render batches,
 * destroy game objects, bind and get the bound shader, render the batches, and remove render batches.
 */
public class Renderer {
    private static Shader currentShader;
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    /**
     * Adds a game object to a render batch.
     *
     * This method gets the SpriteRenderer component of the game object and adds it to a render batch.
     *
     * @param go The game object to be added.
     */
    public void add(GameObject go){
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null){
            add(spr);
        }
    }

    /**
     * Adds a sprite to a render batch.
     *
     * This method checks if there is a render batch that has room for the sprite and
     * either the same zIndex as the sprite's game object or the same texture as the sprite.
     * If such a render batch exists, the sprite is added to it. If not, a new render batch is created, started,
     * and added to the list of render batches, and the sprite is added to the new render batch.
     *
     * @param sprite The sprite to be added.
     */
    private void add(SpriteRenderer sprite){
        boolean added = false;
        for (RenderBatch batch : batches){
            if (batch.hasRoom() && batch.getzIndex() == sprite.gameObject.transform.zIndex){
                Texture tex = sprite.getTexture();
                if (tex == null || (batch.hasTexture(tex) || batch.hasTextureRoom())) {
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE,
                    sprite.gameObject.transform.zIndex, this);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }

    /**
     * Destroys a game object.
     *
     * This method checks if the game object has a SpriteRenderer component, and if so, removes the game object from the render batch that contains it.
     *
     * @param go The game object to be destroyed.
     */
    public void destroyGameObject(GameObject go) {
        if (go.getComponent(SpriteRenderer.class) == null) return;
        for (RenderBatch batch : batches){
            if (batch.destroyIfExists(go)){
                return;
            }
        }
    }

    /**
     * Binds a shader.
     *
     * This method sets the currently bound shader to the specified shader.
     *
     * @param shader The shader to be bound.
     */
    public static void bindShader(Shader shader){
        currentShader = shader;
    }

    /**
     * Returns the currently bound shader.
     *
     * @return The currently bound shader.
     */
    public static Shader getBoundShader() {
        return currentShader;
    }

    /**
     * Renders the batches.
     *
     * This method iterates over the render batches and renders each one using the currently bound shader.
     */
    public void render() {
        for (int i = 0; i < batches.size(); i++){
            RenderBatch batch = batches.get(i);
            batch.render(currentShader);
        }
    }

    /**
     * Removes a render batch.
     *
     * This method removes a specified render batch from the list of render batches.
     *
     * @param renderBatch The render batch to be removed.
     */
    public void removeRenderBatch(RenderBatch renderBatch){
        batches.remove(renderBatch);
    }
}
