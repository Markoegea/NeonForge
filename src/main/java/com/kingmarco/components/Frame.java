package com.kingmarco.components;

/**
 * Class that represents a single frame in an animation.
 */
public class Frame {
    public Sprite sprite;
    public float frameTime;

    public Frame() {
    }

    /**
     * Constructor for Frame with specified sprite and frame time.
     *
     * @param sprite The sprite for this frame.
     * @param time The duration (in seconds) this frame should be displayed.
     */
    public Frame(Sprite sprite, float time){
        this.sprite = sprite;
        this.frameTime = time;
    }
}
