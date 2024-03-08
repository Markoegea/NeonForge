package com.kingmarco.renderer;

import static org.lwjgl.opengl.GL30.*;

/**
 * The Framebuffer class handles the creation and management of a framebuffer.
 *
 * This class provides methods to bind and unbind the framebuffer, and get the framebuffer ID, texture ID, texture width, and texture height.
 */
public class Framebuffer {
    private int fboID;
    private Texture texture = null;

    /**
     * Constructs a Framebuffer with a specified width and height.
     *
     * This method generates a framebuffer, binds it, creates a texture and attaches it to the framebuffer,
     * creates a renderbuffer and attaches it to the framebuffer, checks if the framebuffer is complete, and unbinds the framebuffer.
     *
     * @param width The width of the framebuffer.
     * @param height The height of the framebuffer.
     */
    public Framebuffer(int width, int height) {
        // Generate framebuffer
        fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);

        //Create the texture to render the data to, and attach it to out framebuffer
        this.texture = new Texture(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D,
                this.texture.getTexID(), 0);

        // Create renderbuffer store the depth info
        int rboID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            assert false : "Error: Framebuffer is not complete";
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
     * Binds the framebuffer.
     *
     * This method binds the framebuffer for rendering.
     */
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
    }

    /**
     * Unbinds the framebuffer.
     *
     * This method unbinds the framebuffer after rendering.
     */
    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
     * Returns the ID of the framebuffer.
     *
     * @return The ID of the framebuffer.
     */
    public int getFboID() {
        return fboID;
    }

    /**
     * Returns the ID of the texture attached to the framebuffer.
     *
     * @return The ID of the texture.
     */
    public int getTextureId() {
        return texture.getTexID();
    }

    /**
     * Returns the width of the texture attached to the framebuffer.
     *
     * @return The width of the texture.
     */
    public int getTextureWidth() {
        return texture.getWidth();
    }

    /**
     * Returns the height of the texture attached to the framebuffer.
     *
     * @return The height of the texture.
     */
    public int getTextureHeight() {
        return texture.getHeight();
    }
}
