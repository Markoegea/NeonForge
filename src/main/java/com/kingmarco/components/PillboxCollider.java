package com.kingmarco.components;

import com.kingmarco.forge.Window;
import com.kingmarco.physics2d.components.Box2DCollider;
import com.kingmarco.physics2d.components.CircleCollider;
import com.kingmarco.physics2d.components.RigidBody2D;
import org.joml.Vector2f;

/**
 * Class responsible for the collider with the shape of a pill, that control the collisions
 * */
public class PillboxCollider extends Component{
    private transient CircleCollider topCircle = new CircleCollider();
    private transient CircleCollider bottomCircle = new CircleCollider();
    private transient Box2DCollider box = new Box2DCollider();
    private transient  boolean resetFixtureNextFrame = false;

    public float width = 0.1f;
    public float height = 0.2f;
    public Vector2f offset = new Vector2f();


    /**
     * This method is called when the scene starts.
     */
    @Override
    public void start() {
        this.topCircle.gameObject = this.gameObject;
        this.bottomCircle.gameObject = this.gameObject;
        this.box.gameObject = this.gameObject;
        reCalculateColliders();
    }

    /**
     * Update the colliders that compose the pillbox colliders in editor mode
     *
     * @param dt The time elapsed since the last update (in seconds).
     * */
    @Override
    public void editorUpdate(float dt) {
        topCircle.editorUpdate(dt);
        bottomCircle.editorUpdate(dt);
        box.editorUpdate(dt);

        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }

    /**
     * Update the physics when necessary.
     *
     * @param dt The time elapsed since the last update (in seconds).
     * */
    @Override
    public void update(float dt) {

        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }

    /**
     * Change the width of the pillbox and update the physics.
     *
     * @param newVal Float value of the new width
     * */
    public void setWidth(float newVal) {
        this.width = newVal;
        reCalculateColliders();
        resetFixture();
    }

    /**
     * Change the height of the pillbox and update the physics.
     *
     * @param newVal Float value of the new height
     * */
    public void setHeight(float newVal) {
        this.height = newVal;
        reCalculateColliders();
        resetFixture();
    }

    /**
     * Reset the physics of the collider.
     * */
    public void resetFixture() {
        if (Window.getPhysics().isLocked()) {
            resetFixtureNextFrame = true;
            return;
        }

        resetFixtureNextFrame = false;

        if (gameObject != null) {
            RigidBody2D rb = gameObject.getComponent(RigidBody2D.class);
            if (rb != null) {
                Window.getPhysics().resetPillboxCollider(rb, this);
            }
        }
    }

    /**
     * Change the size of the inner colliders.
     * */
    public void reCalculateColliders() {
        float circleRadius = width / 4.0f;
        float boxHeight = height - 2 * circleRadius;
        topCircle.setRadius(circleRadius);
        bottomCircle.setRadius(circleRadius);
        topCircle.setOffset(new Vector2f(offset).add(0, boxHeight / 4.0f));
        bottomCircle.setOffset(new Vector2f(offset).sub(0, boxHeight / 4.0f));
        box.setHalfSize(new Vector2f(width / 2.0f, boxHeight / 2.0f));
        box.setOffset(offset);
    }

    /**
     * Get the top circle collider object.
     *
     * @return CircleCollider object
     * */
    public CircleCollider getTopCircle() {
        return topCircle;
    }

    /**
     * Get the bottom circle collider object.
     *
     * @return CircleCollider object
     * */
    public CircleCollider getBottomCircle() {
        return bottomCircle;
    }

    /**
     * Get the box collider object.
     *
     * @return Box2DCollider object
     * */
    public Box2DCollider getBox() {
        return box;
    }
}
