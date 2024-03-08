package com.kingmarco.components;

import com.kingmarco.util.AssetPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to storage frame sprites, that are used to animate a game object.
 * @author marko
 */
public class AnimationState {
    public String title;
    public List<Frame> animationFrames = new ArrayList<>();
    public boolean doesLoop = false;

    private static Sprite defaultSprite = new Sprite();
    private transient float timeTracker = 0.0f;
    private transient int currentSprite = 0;

    /**
     * Loop through the frame list to
     * refresh the frames using their file path
     */
    public void refreshTextures() {
        for (Frame frame : animationFrames){
            frame.sprite.setTexture(AssetPool.getTexture(frame.sprite.getTexture().getFilepath()));
        }
    }

    /**
     * Add a frame to the frame list, using:
     *
     * @param sprite The sprite object that contains the texture that is used to animate
     * @param frameTime Amount of time in seconds that the frame last
     */
    public void addFrame(Sprite sprite, float frameTime){
        animationFrames.add(new Frame(sprite, frameTime));
    }

    /**
     * Updates the animation state based on the elapsed time.
     *
     * @param dt The time elapsed since the last update (in seconds).
     */
    public void update(float dt) {
        if (currentSprite < animationFrames.size()){
            timeTracker -= dt;
            if (timeTracker <= 0) {
                if (currentSprite != animationFrames.size() - 1 || doesLoop) {
                    currentSprite = (currentSprite + 1) % animationFrames.size();
                }
                timeTracker = animationFrames.get(currentSprite).frameTime;
            }
        }
    }

    /**
     * Retrieves the current sprite from the animation.
     *
     * @return The current sprite, or the default sprite if the animation is out of bounds.
     */
    public Sprite getCurrentSprite() {
        if (currentSprite < animationFrames.size()) {
            return animationFrames.get(currentSprite).sprite;
        }

        return defaultSprite;
    }

    /**
     * Set if the animation should stop or loop
     *
     * @param doesLoop Decide if the animations can loop (true) or stop at the last frame (false)
     */
    public void setLoop(boolean doesLoop) {
        this.doesLoop = doesLoop;
    }
}
