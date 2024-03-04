package com.kingmarco.GameFunctionality;

import com.kingmarco.components.Component;
import com.kingmarco.components.PillboxCollider;
import com.kingmarco.components.StateMachine;
import com.kingmarco.forge.GameObject;
import com.kingmarco.forge.KeyListener;
import com.kingmarco.forge.Window;
import com.kingmarco.physics2d.Physics2D;
import com.kingmarco.physics2d.RaycastInfo;
import com.kingmarco.physics2d.components.RigidBody2D;
import com.kingmarco.renderer.DebugDraw;
import com.kingmarco.renderer.DrawLines;
import com.kingmarco.util.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {
    public enum PlayerState {
        Small,
        Big,
        Fire,
        Invincible
    }

    public float walkSpeed = 1.9f;
    public float jumpBoost = 1.0f;
    public float jumpImpulse = 3.0f;
    public float slowDownForce = 0.05f;
    public Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);


    private PlayerState playerState = PlayerState.Small;
    public transient boolean onGround = false;
    private transient float groundDebounce = 0.0f;
    private transient float groundDebounceTime = 0.1f;
    private transient RigidBody2D rb;
    private transient StateMachine stateMachine;
    private transient float bigJumpBoostFactor = 1.05f;
    private transient float playerWidth = 0.25f;
    private transient int jumpTime = 0;
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f velocity = new Vector2f();
    private transient boolean isDead = false;
    private transient int enemyBounce = 0;
    private final transient DrawLines drawLines = new DrawLines(2);

    @Override
    public void start() {
        this.rb = gameObject.getComponent(RigidBody2D.class);
        this.stateMachine = gameObject.getComponent(StateMachine.class);
        this.rb.setGravityScale(0.0f);

        drawLines.start();
        DebugDraw.addDrawLines(drawLines);
    }

    @Override
    public void update(float dt) {
        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)){
            this.gameObject.transform.scale.x = playerWidth;
            this.acceleration.x = walkSpeed;

            if (this.velocity.x < 0) {
                //this.stateMachine.trigger("switchDirection");
                this.velocity.x += slowDownForce;
            } else {
                //this.stateMachine.trigger("startRunning");
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)){
            this.gameObject.transform.scale.x = playerWidth;
            this.acceleration.x = -walkSpeed;

            if (this.velocity.x > 0) {
                //this.stateMachine.trigger("switchDirection");
                this.velocity.x -= slowDownForce;
            } else {
                //this.stateMachine.trigger("startRunning");
            }
        } else {
            this.acceleration.x = 0;
            if (this.velocity.x > 0){
                this.velocity.x = Math.max(0, this.velocity.x - slowDownForce);
            } else if (this.velocity.x < 0) {
                this.velocity.x = Math.min(0, this.velocity.x + slowDownForce);
            }
        }

        checkOnGround();
        System.out.println(onGround);
        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE) && (jumpTime > 0 || onGround || groundDebounce > 0)) {
            if ((onGround || groundDebounce > 0) && jumpTime == 0) {
                AssetPool.getSound("assets/texture/NinjaAdventure/Sounds/Game/ogg/Jump.ogg").play();
                jumpTime = 28;
                this.velocity.y = jumpImpulse;
            } else if (jumpTime > 0) {
                jumpTime--;
                this.velocity.y = ((jumpTime / 2.2f) * jumpBoost);
            } else {
                this.velocity.y = 0;
            }
            groundDebounce = 0;
        } else if (!onGround) {
            if (this.jumpTime > 0) {
                this.velocity.y *= 0.35f;
                this.jumpTime = 0;
            }
            groundDebounce -= dt;
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        } else {
            this.velocity.y = 0;
            this.acceleration.y = 0;
            groundDebounce = groundDebounceTime;
        }

        this.velocity.x += this.acceleration.x * dt;
        this.velocity.x = Math.max(Math.min(this.velocity.x, this.terminalVelocity.x), -this.terminalVelocity.x);
        this.rb.setVelocity(this.velocity);
        this.rb.setAngularVelocity(0);

        if (!onGround) {
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
            this.velocity.y += this.acceleration.y * dt;
            this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -this.terminalVelocity.y);
            //stateMachine.trigger("jump");
        } else {
            //stateMachine.trigger("stopJumping");
        }
    }

    public void checkOnGround() {
        float innerPlayerWidth = this.playerWidth * 0.6f;
        float yVal = playerState == PlayerState.Small ? -0.14f : -0.24f;

        onGround = Physics2D.checkOnGround(gameObject, innerPlayerWidth, yVal);

        //drawLines.addLine2D(raycastBegin, raycastEnd, new Vector3f(1f, 0f, 0f));
        //drawLines.addLine2D(raycast2Begin, raycast2End, new Vector3f(1f, 0f, 0f));
    }

    public void powerup() {
        switch (playerState) {
            case Small:
                playerState = PlayerState.Big;
                //AssetPool.getSound().play();
                gameObject.transform.scale.y = 0.42f;
                PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
                if (pb != null) {
                    jumpBoost *= bigJumpBoostFactor;
                    walkSpeed *= bigJumpBoostFactor;
                    pb.setHeight(0.63f);
                }
                break;
            case Big:
                playerState = PlayerState.Fire;
                //AssetPool.getSound().play();
                break;
        }
        //stateMachine.trigger("powerup");
    }

    @Override
    public void preSolve(GameObject collingObject, Contact contact, Vector2f hitNormal) {
        contact.setEnabled(false);
    }

    @Override
    public void beginCollision(GameObject collingObject, Contact contact, Vector2f hitNormal) {
        if (isDead) return;
        if (Math.abs(hitNormal.x) > 0.8f){
            this.velocity.x = 0;
        } else if (hitNormal.y > 0.8f) {
            this.velocity.y = 0;
            this.acceleration.y = 0;
            this.jumpTime = 0;
        }
    }

    public boolean hasWon() {
        return false;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }
}
