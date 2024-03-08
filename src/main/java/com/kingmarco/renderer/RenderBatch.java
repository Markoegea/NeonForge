package com.kingmarco.renderer;

import com.kingmarco.components.SpriteRenderer;
import com.kingmarco.forge.GameObject;
import com.kingmarco.forge.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * A class responsible to create a batch of sprites to be rendered at the specific zIndex.
 * */
public class RenderBatch implements Comparable<RenderBatch>{
    // Vertex
    // =======
    // Pos               Color                          tex coords    tex id
    //float, float,      float, float, float, float     float, float  float

    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;
    private final int ENTITY_ID_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    private final int ENTITY_ID_OFFSET = TEX_ID_OFFSET + TEX_ID_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 10;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private List<Texture> textures;
    private int vaoID, vboID;
    private int maxBatchSize;
    private int zIndex;

    private Renderer renderer;

    public RenderBatch(int maxBatchSize, int zIndex, Renderer renderer) {
        this.zIndex = zIndex;
        this.renderer = renderer;

        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        // 4 vertices quads
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
    }

    /**
     * Initializes the rendering process.
     *
     * This method generates and binds a Vertex Array Object (VAO), allocates space for vertices,
     * creates and uploads an indices buffer, and enables the buffer attribute pointers.
     */
    public void start() {
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable the buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_ID_OFFSET);
        glEnableVertexAttribArray(4);
    }

    /**
     * Adds a sprite to the render batch.
     *
     * This method sets the sprite as dirty, gets the index and adds the sprite to the render batch,
     * checks if the sprite has a texture and if it's not already in the textures list, adds it,
     * loads the sprite properties to the local vertices array, and checks if the number of sprites has reached the maximum batch size.
     *
     * @param spr The sprite to be added.
     */
    public void addSprite(SpriteRenderer spr) {
        spr.setDirty(true);

        // Get Index and add renderObject
        int index = this.numSprites;
        this.sprites[index] = spr;
        this.numSprites++;

        if (spr.getTexture() != null){
            if (!textures.contains(spr.getTexture())){
                textures.add(spr.getTexture());
            }
        }

        //Add properties to local vertices array
        loadVertexProperties(index);

        if (numSprites >= this.maxBatchSize) {
            this.hasRoom = false;
        }
        //reBufferData(true);
    }

    /**
     * Renders the batches.
     *
     * This method iterates over the sprites, checks if each sprite is dirty and if so,
     * loads its properties to the local vertices array and sets it as not dirty,
     * checks if the sprite's zIndex is different from the batch's zIndex and if so,
     * destroys the sprite and adds it to the renderer, re-buffers the data if needed,
     * uses the shader and uploads the projection and view matrices, binds the textures,
     * uploads the texture slots, binds the VAO and enables the attribute pointers,
     * draws the elements, disables the attribute pointers and unbinds the VAO,
     * unbinds the textures, and detaches the shader.
     *
     * @param shader The shader to be used for rendering.
     */
    public void render(Shader shader) {
        boolean reBufferData = false;
        for (int i=0; i < sprites.length; i++){
            SpriteRenderer spr = sprites[i];
            if (spr == null){
                continue;
            }
            if (spr.isDirty()) {
                loadVertexProperties(i);
                spr.setDirty(false);
                reBufferData = true;
            }

            // TODO: Get better solution for this
            if (spr.gameObject.transform.zIndex != this.zIndex){
                destroyIfExists(spr.gameObject);
                renderer.add(spr.gameObject);
                i--;
            }
        }

        reBufferData(reBufferData);

        //Use shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());
        for (int i=0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", texSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (int i=0; i < textures.size(); i++) {
            textures.get(i).unbind();
        }
        shader.detach();
    }


    /**
     * Re-buffers the data if the reBufferData parameter is true.
     * It binds the buffer to the vboID and then updates the buffer data with the vertices.
     *
     * @param reBufferData A boolean indicating whether to re-buffer the data.
     */
    private void reBufferData(boolean reBufferData) {
        if (reBufferData){
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }
    }

    /**
     * Loads the properties of the vertex at the given index.
     * It retrieves the sprite at the index, calculates the offset, and loads various properties such as color,
     * texture coordinates, texture id, and entity id.
     *
     * @param index The index of the vertex whose properties are to be loaded.
     */
    private void loadVertexProperties(int index) {
        SpriteRenderer sprite = this.sprites[index];

        // Find offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        Vector2f[] texCoords = sprite.getTexCoords();

        int texId = 0;
        if (sprite.getTexture() != null){
            for (int i=0; i< textures.size(); i++){
                if (textures.get(i).equals(sprite.getTexture())){
                    texId = i + 1;
                    break;
                }
            }
        }

        boolean isRotated = sprite.gameObject.transform.rotation != 0.0f;
        Matrix4f transformMatrix = new Matrix4f().identity();
        if (isRotated) {
            transformMatrix.translate(sprite.gameObject.transform.position.x,
                                        sprite.gameObject.transform.position.y, 0f);
            transformMatrix.rotate((float)Math.toRadians(sprite.gameObject.transform.rotation),
                    0, 0, 1);
            transformMatrix.scale(sprite.gameObject.transform.scale.x,
                    sprite.gameObject.transform.scale.y, 1);
        }

        //Add vertice with the appropriate properties
        float xAdd = 0.5f;
        float yAdd = 0.5f;
        for (int i=0; i < 4; i++) {
            if (i == 1){
                yAdd = -0.5f;
            } else if (i == 2) {
                xAdd = -0.5f;
            } else if (i == 3) {
                yAdd = 0.5f;
            }

            Vector4f currentPos = new Vector4f(sprite.gameObject.transform.position.x +
                                                (xAdd * sprite.gameObject.transform.scale.x),
                                            sprite.gameObject.transform.position.y +
                                                (yAdd * sprite.gameObject.transform.scale.y),
                                            0, 1);
            if (isRotated) {
                currentPos = new Vector4f(xAdd, yAdd, 0, 1).mul(transformMatrix);
            }
            // Load position
            vertices[offset] = currentPos.x;
            vertices[offset + 1] = currentPos.y;

            //Load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            //Load texture coordinates
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;

            // Load texture id
            vertices[offset + 8] = texId;

            //Load entity id
            vertices[offset + 9] = sprite.gameObject.getUid() + 1;

            offset += VERTEX_SIZE;
        }
    }

    /**
     * Generates the indices for the sprites.
     * It creates an array of elements and loads the element indices for each sprite.
     *
     * @return An array of integers representing the indices of the sprites.
     */
    private int[] generateIndices() {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * maxBatchSize];
        for (int i=0; i < maxBatchSize; i++){
            loadElementIndices(elements, i);
        }

        return elements;
    }

    /**
     * Loads the element indices for the given index into the elements array.
     *
     * @param elements The array of elements.
     * @param index The index for which to load the element indices.
     */
    private void loadElementIndices(int[] elements, int index){
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // 3, 2, 0, 0, 2, 1           7, 6, 4, 4, 6, 5
        // Triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset;

        // Triangle 2
        elements[offsetArrayIndex + 3] = offset;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    /**
     * Destroys the GameObject if it exists.
     *
     * @param go The GameObject to destroy.
     * @return A boolean indicating whether the GameObject was destroyed.
     */
    public boolean destroyIfExists(GameObject go) {
        SpriteRenderer sprite = go.getComponent(SpriteRenderer.class);
        int spriteIndex = findSpriteIndex(sprite);

        if (spriteIndex != -1) {
            shiftSpritesLeft(spriteIndex);
            numSprites--;
            if (numSprites <= 0){
                renderer.removeRenderBatch(this);
            }
            return true;
        }

        return false;
    }

    /**
     * Finds the index of the given SpriteRenderer in the sprites array.
     *
     * @param sprite The SpriteRenderer to find.
     * @return The index of the SpriteRenderer, or -1 if it is not found.
     */
    private int findSpriteIndex(SpriteRenderer sprite) {
        for (int i = 0; i < numSprites; i++) {
            if (sprites[i] == sprite) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Shifts the sprites in the sprites array to the left starting from the given index.
     *
     * @param startIndex The index from which to start shifting.
     */
    private void shiftSpritesLeft(int startIndex) {
        for (int i = startIndex; i < sprites.length; i++) {
            sprites[i] = sprites[i + 1];
            if (sprites[i+1] == null) break;
            sprites[i].setDirty(true);
        }
    }

    /**
     * Checks if the RenderBatch has room for more sprites.
     *
     * @return A boolean indicating whether the RenderBatch has room.
     */
    public boolean hasRoom() {
        return this.hasRoom;
    }

    /**
     * Checks if the RenderBatch has room for more textures.
     *
     * @return A boolean indicating whether the RenderBatch has texture room.
     */
    public boolean hasTextureRoom() {
        return this.textures.size() < 8;
    }

    /**
     * Checks if the RenderBatch contains the given texture.
     *
     * @param tex The texture to check.
     * @return A boolean indicating whether the RenderBatch contains the texture.
     */
    public boolean hasTexture(Texture tex) {
        return this.textures.contains(tex);
    }

    /**
     * Gets the z-index of the RenderBatch.
     *
     * @return The z-index of the RenderBatch.
     */
    public int getzIndex() {
        return this.zIndex;
    }

    /**
     * Compares this RenderBatch with another based on their z-indices.
     *
     * @param o The other RenderBatch to compare with.
     * @return A negative integer, zero, or a positive integer as this RenderBatch's z-index is less than,
     * equal to, or greater than the specified RenderBatch's z-index.
     */
    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.getzIndex());
    }
}
