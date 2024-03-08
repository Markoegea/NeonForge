package com.kingmarco.renderer;

import org.joml.Vector2i;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.*;

/**
 * The PickingTexture class handles the creation and management of a picking texture.
 *
 * This class provides methods to initialize the picking texture, enable and disable writing to the framebuffer,
 * read a pixel from the picking texture, and read a range of pixels from the picking texture.
 */
public class PickingTexture {
    private int pickingTextureId;
    private int fbo;
    private int depthTexture;

    public PickingTexture(int width, int height) {
        if (!init(width, height)) {
            assert false : "Error initializing picking texture";
        }
    }

    /**
     * Initializes the picking texture with a specified width and height.
     *
     * This method generates a framebuffer, binds it, creates a texture and attaches it to the framebuffer,
     * creates a renderbuffer and attaches it to the framebuffer, checks if the framebuffer is complete,
     * and unbinds the texture and framebuffer.
     *
     * @param width The width of the picking texture.
     * @param height The height of the picking texture.
     * @return True if the initialization was successful, false otherwise.
     */
    public boolean init(int width, int height){
        // Generate framebuffer
        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        //Create the texture to render the data to, and attach it to out framebuffer
        pickingTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, pickingTextureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0,
                GL_RGB, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D,
                this.pickingTextureId, 0);

        // Create the texture object for the depth buffer
        glEnable(GL_TEXTURE_2D);
        depthTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0,
                GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
                GL_TEXTURE_2D, depthTexture, 0);

        // Disable the reading
        glReadBuffer(GL_NONE);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            assert false : "Error: Framebuffer is not complete";
        }

        // Unbind the texture and framebuffer
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        return true;
    }

    /**
     * Enables writing to the framebuffer.
     *
     * This method binds the framebuffer for drawing.
     */
    public void enableWriting() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo);
    }

    /**
     * Disables writing to the framebuffer.
     *
     * This method unbinds the framebuffer after drawing.
     */
    public void disableWriting() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    /**
     * Reads a pixel from the picking texture at a specified position.
     *
     * This method binds the framebuffer for reading, sets the read buffer to the color attachment, reads the pixel at the specified position, and returns the value of the pixel.
     *
     * @param x The x-coordinate of the pixel.
     * @param y The y-coordinate of the pixel.
     * @return The value of the pixel.
     */
    public int readPixel(int x, int y) {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        float[] pixels = new float[3];
        glReadPixels(x, y, 1, 1, GL_RGB, GL_FLOAT, pixels);

        return (int) (pixels[0]) - 1;
    }

    /**
     * Reads a range of pixels from the picking texture between two specified points.
     *
     * This method binds the framebuffer for reading, sets the read buffer to the color attachment, reads the pixels in the specified range, and returns the values of the pixels.
     *
     * @param start The starting point of the range.
     * @param end The ending point of the range.
     * @return The values of the pixels in the specified range.
     */
    public float[] readPixels(Vector2i start, Vector2i end) {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        Vector2i size = new Vector2i(end).sub(start).absolute();
        int numPixels = size.x * size.y;
        float[] pixels = new float[3 * numPixels];
        glReadPixels(start.x, start.y, size.x, size.y, GL_RGB, GL_FLOAT, pixels);

        for (int i = 0; i < pixels.length; i++){
            pixels[i] -= 1;
        }

        return pixels;
    }
}
