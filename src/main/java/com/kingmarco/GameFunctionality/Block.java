package com.kingmarco.GameFunctionality;

import com.kingmarco.components.Component;
import com.kingmarco.forge.GameObject;
import com.kingmarco.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public abstract class Block extends Component {
    private transient boolean bopGoingUp = true;
    private transient boolean doBopAnimation = false;
    private transient Vector2f bopStart;
    private transient Vector2f topBopLocation;
    private transient boolean active = true;

    public float bopSpeed = 0.4f;

    @Override
    public void start() {
        this.bopStart = new Vector2f(this.gameObject.transform.position);
        this.topBopLocation = new Vector2f(bopStart).add(0.0f, 0.02f);
    }

    @Override
    public void update(float dt) {
        if (doBopAnimation) {
            if (bopGoingUp) {
                if (this.gameObject.transform.position.y < topBopLocation.y) {
                    this.gameObject.transform.position.y += bopSpeed * dt;
                } else {
                    bopGoingUp = false;
                }
            } else {
                if (this.gameObject.transform.position.y > bopStart.y) {
                    this.gameObject.transform.position.y -= bopSpeed * dt;
                } else {
                    this.gameObject.transform.position.y = this.bopStart.y;
                    bopGoingUp = true;
                    doBopAnimation = false;
                }
            }
        }
    }

    @Override
    public void beginCollision(GameObject collingObject, Contact contact, Vector2f hitNormal) {
        PlayerController playerController = collingObject.getComponent(PlayerController.class);
        if (active && playerController != null) {
            doBopAnimation = true;
            AssetPool.getSound("assets/texture/NinjaAdventure/Sounds/Game/ogg/MiniImpact.ogg").play();
            playerHit(playerController);
        }
    }

    public void setActive(boolean status) {
        this.active = status;
    }

    abstract void playerHit(PlayerController playerController);
}
