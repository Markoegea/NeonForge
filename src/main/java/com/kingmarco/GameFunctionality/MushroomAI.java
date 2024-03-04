package com.kingmarco.GameFunctionality;

import com.kingmarco.components.Component;
import com.kingmarco.forge.GameObject;
import com.kingmarco.physics2d.components.RigidBody2D;
import com.kingmarco.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class MushroomAI extends Component {
    private transient boolean goingRight = true;
    private transient RigidBody2D rb;
    private transient Vector2f speed = new Vector2f(1.0f, 0.0f);
    private transient float maxSpeed = 0.8f;
    private transient boolean hitPlayer = false;

    @Override
    public void start() {
        this.rb = gameObject.getComponent(RigidBody2D.class);
        //AssetPool.getSound("").play();
    }

    @Override
    public void update(float dt) {
        if (goingRight && Math.abs(rb.getVelocity().x) < maxSpeed) {
            rb.addVelocity(speed);
        } else if (!goingRight && Math.abs(rb.getVelocity().x) < maxSpeed) {
            rb.addVelocity(new Vector2f(-speed.x, speed.y));
        }
    }

    @Override
    public void preSolve(GameObject collingObject, Contact contact, Vector2f hitNormal) {
        PlayerController playerController = collingObject.getComponent(PlayerController.class);
        if (playerController != null) {
            contact.setEnabled(false);
            if (!hitPlayer) {
                playerController.powerup();
                this.gameObject.destroy();
                hitPlayer = true;
            }
        }
        if (Math.abs(hitNormal.y) < 0.1f) {
            goingRight = hitNormal.x < 0;
        }
    }
}
