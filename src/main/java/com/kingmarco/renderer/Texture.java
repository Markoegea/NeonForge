package com.kingmarco.renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

/**
 * The Texture class handles the creation and management of a texture.
 *
 * This class provides methods to initialize the texture, bind and unbind the texture,
 * get the filepath, width, height, and texture ID of the texture, and check if another object is equal to this texture.
 */
public class Texture {
    private String filepath;
    private transient int texID;
    private int width, height;

    public Texture() {
        texID = -1;
        width = -1;
        height = -1;
    }

    public Texture(int width, int height){
        this.width = width;
        this.height = height;
        this.filepath = "Generated";

        // Generate texture on GPU
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height,
                        0, GL_RGB, GL_UNSIGNED_BYTE, 0);
    }

    /**
     * Initializes the texture with a specified filepath.
     *
     * This method sets the filepath of the texture, generates a texture on the GPU, binds the texture,
     * sets the texture parameters, loads the image from the filepath, checks if the image was loaded successfully,
     * creates the texture based on the image, and frees the image.
     *
     * @param filepath The filepath of the texture.
     */
    public void init(String filepath){
        this.filepath = filepath;

        // Generate texture on GPU
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        // Set texture parameters
        // Repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // When stretching the image, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        //When shrinking an image, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(this.filepath, width, height, channels, 0);

        if (image != null) {
            this.width = width.get(0);
            this.height = height.get(0);

            if (channels.get(0) == 3) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),
                        0, GL_RGB, GL_UNSIGNED_BYTE, image);
            } else if (channels.get(0) == 4){
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),
                        0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            } else {
                assert false : "Error: (Texture) Unknown number of channels'" + channels.get(0) + "'";
            }
        } else {
            assert false : "Error: (Texture) Could not load image '" + filepath + "'";
        }

        stbi_image_free(image);
    }

    /**
     * Binds the texture.
     *
     * This method binds the texture for rendering.
     */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    /**
     * Unbinds the texture.
     *
     * This method unbinds the texture after rendering.
     */
    public void unbind(){
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Returns the filepath of the texture.
     *
     * @return The filepath of the texture.
     */
    public String getFilepath() {
        return this.filepath;
    }

    /**
     * Returns the width of the texture.
     *
     * @return The width of the texture.
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Returns the height of the texture.
     *
     * @return The height of the texture.
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Returns the ID of the texture.
     *
     * @return The ID of the texture.
     */
    public int getTexID() {
        return texID;
    }

    /**
     * Checks if another object is equal to this texture.
     *
     * This method checks if another object is of the same class as this texture, and if so, checks if their filepath, texture ID, width, and height are equal.
     *
     * @param obj The object to be compared with this texture.
     * @return True if the other object is equal to this texture, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) return false;

        Texture texture = (Texture) obj;

        return this.filepath.equals(texture.filepath) && this.texID == texture.texID &&
                this.width == texture.width && this.height == texture.height;
    }
}
