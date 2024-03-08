package com.kingmarco.components;

import com.kingmarco.renderer.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for represents a sprite sheet containing multiple sprites.
 */
public class SpritesSheet {

    private Texture texture;
    private List<Sprite> sprites;

    /**
     * Constructs a sprite sheet from a texture with specified parameters.
     *
     * @param texture      The texture containing the sprite sheet.
     * @param spriteWidth  The width of each individual sprite.
     * @param spriteHeight The height of each individual sprite.
     * @param numSprites   The total number of sprites in the sheet.
     * @param spacing      The spacing between adjacent sprites.
     */
    public SpritesSheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing){
        this.sprites = new ArrayList<>();

        this.texture = texture;
        int currentX = 0;
        int currentY = texture.getHeight() - spriteHeight;
        for (int i=0; i < numSprites; i++){
            float topY = (currentY + spriteHeight) / (float) texture.getHeight();
            float rightX = (currentX + spriteWidth) / (float) texture.getWidth();
            float leftX = currentX / (float) texture.getWidth();
            float bottomY = currentY / (float) texture.getHeight();

            Vector2f[] texCoords = {
                    new Vector2f(rightX, topY),
                    new Vector2f(rightX, bottomY),
                    new Vector2f(leftX, bottomY),
                    new Vector2f(leftX, topY),
            };
            Sprite sprite = new Sprite();
            sprite.setTexture(this.texture);
            sprite.setTexCoords(texCoords);
            sprite.setWidth(spriteWidth);
            sprite.setHeight(spriteHeight);
            this.sprites.add(sprite);

            currentX += spriteWidth + spacing;
            if (currentX >= texture.getWidth()) {
                currentX = 0;
                currentY -= spriteHeight + spacing;
            }
        }
    }

    /**
     * Gets the sprite at the specified index.
     *
     * @param index The index of the desired sprite.
     * @return The sprite at the specified index.
     */
    public Sprite getSprite(int index) {
        return this.sprites.get(index);
    }

    /**
     * Gets the total number of sprites in the sheet.
     *
     * @return The number of sprites.
     */
    public int size() {
        return this.sprites.size();
    }
}
