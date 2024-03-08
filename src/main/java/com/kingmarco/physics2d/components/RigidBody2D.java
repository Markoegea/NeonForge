package com.kingmarco.physics2d.components;

import com.kingmarco.components.Component;
import com.kingmarco.forge.Window;
import com.kingmarco.physics2d.enums.BodyType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.joml.Vector2f;

/**
 * The RigidBody2D class extends Component and represents a 2D rigid body.
 *
 * This class contains properties for the velocity, angular velocity, and gravity scale of the rigid body,
 * as well as methods to update the rigid body, add velocity, add impulse, and set various properties.
 */
public class RigidBody2D extends Component {
    private Vector2f velocity = new Vector2f();
    private float angularDamping = 0.8f;
    private float linearDamping = 0.9f;
    private float mass = 0;
    private BodyType bodyType = BodyType.Dynamic;
    private float friction = 0.1f;
    public float angularVelocity = 0.0f;
    public float gravityScale = 1.0f;
    private boolean isSensor = false;

    private boolean fixedRotation = false;
    private boolean continuousCollision = true;

    private transient Body rawBody = null;

    /**
     * Updates the rigid body.
     *
     * This method updates the position, rotation, and velocity of the game object based on the state of the raw body.
     * If the {@link Body} type is Dynamic or Kinematic, the game object's properties are set based on the raw body's properties.
     * If the {@link Body} type is Static, the raw body's properties are set based on the game object's properties.
     *
     * @param dt The time passed since the last frame.
     */
    @Override
    public void update(float dt) {
        if (rawBody != null){
            if (this.bodyType == BodyType.Dynamic || this.bodyType == BodyType.Kinematic){
                this.gameObject.transform.position.set(
                        rawBody.getPosition().x, rawBody.getPosition().y
                );
                this.gameObject.transform.rotation = (float)Math.toDegrees(rawBody.getAngle());
                Vec2 vel = rawBody.getLinearVelocity();
                this.velocity.set(vel.x, vel.y);
            } else if (this.bodyType == BodyType.Static) {
                this.rawBody.setTransform(
                        new Vec2(this.gameObject.transform.position.x, this.gameObject.transform.position.y),
                        this.gameObject.transform.rotation
                );
            }
        }
    }

    /**
     * Adds velocity to the rigid body.
     *
     * This method applies a force to the center of the raw body, which adds velocity to it.
     *
     * @param forceToAdd The force to add to the rigid body.
     */
    public void addVelocity(Vector2f forceToAdd){
        if (rawBody != null) {
            rawBody.applyForceToCenter(new Vec2(forceToAdd.x, forceToAdd.y));
        }
    }

    /**
     * Adds an impulse to the rigid body.
     *
     * This method applies a linear impulse to the world center of the raw body.
     *
     * @param impulse The impulse to add to the rigid body.
     */
    public void addImpulse(Vector2f impulse) {
        if (rawBody != null) {
            rawBody.applyLinearImpulse(new Vec2(impulse.x, impulse.y), rawBody.getWorldCenter());
        }
    }

    /**
     * Returns the velocity of the rigid body.
     *
     * @return The velocity of the rigid body.
     */
    public Vector2f getVelocity() {
        return velocity;
    }

    /**
     * Sets the velocity of the rigid body.
     *
     * This method sets the velocity of the rigid body and updates the linear velocity of the raw body.
     *
     * @param velocity The new velocity of the rigid body.
     */
    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);

        if (rawBody != null){
            this.rawBody.setLinearVelocity(new Vec2(velocity.x, velocity.y));
        }
    }

    /**
     * Sets the angular velocity of the rigid body.
     *
     * This method sets the angular velocity of the rigid body and updates the angular velocity of the raw body.
     *
     * @param angularVelocity The new angular velocity of the rigid body.
     */
    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
        if (rawBody != null) {
            this.rawBody.setAngularVelocity(angularVelocity);
        }
    }

    /**
     * Sets the gravity scale of the rigid body.
     *
     * This method sets the gravity scale of the rigid body and updates the gravity scale of the raw body.
     *
     * @param gravityScale The new gravity scale of the rigid body.
     */
    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
        if (rawBody != null) {
            this.rawBody.setGravityScale(gravityScale);
        }
    }

    /**
     * Returns whether the rigid body is a sensor.
     *
     * @return True if the rigid body is a sensor, false otherwise.
     */
    public boolean isSensor() {
        return isSensor;
    }

    /**
     * Sets the rigid body as a sensor.
     *
     * If the raw body of the rigid body exists, this method also updates the physics system to set the rigid body as a sensor.
     */
    public void setIsSensor() {
        isSensor = true;
        if (rawBody != null) {
            Window.getPhysics().setIsSensor(this);
        }
    }

    /**
     * Sets the rigid body as not a sensor.
     *
     * If the raw body of the rigid body exists, this method also updates the physics system to set the rigid body as not a sensor.
     */
    public void setNoSensor() {
        isSensor = false;
        if (rawBody != null) {
            Window.getPhysics().setNotSensor(this);
        }
    }

    /**
     * Returns the friction of the rigid body.
     *
     * @return The friction of the rigid body.
     */
    public float getFriction(){
        return this.friction;
    }

    /**
     * Returns the angular damping of the rigid body.
     *
     * @return The angular damping of the rigid body.
     */
    public float getAngularDamping() {
        return angularDamping;
    }

    /**
     * Sets the angular damping of the rigid body.
     *
     * @param angularDamping The new angular damping of the rigid body.
     */
    public void setAngularDamping(float angularDamping) {
        this.angularDamping = angularDamping;
    }

    /**
     * Returns the linear damping of the rigid body.
     *
     * @return The linear damping of the rigid body.
     */
    public float getLinearDamping() {
        return linearDamping;
    }

    /**
     * Sets the linear damping of the rigid body.
     *
     * @param linearDamping The new linear damping of the rigid body.
     */
    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }

    /**
     * Returns the mass of the rigid body.
     *
     * @return The mass of the rigid body.
     */
    public float getMass() {
        return mass;
    }

    /**
     * Sets the mass of the rigid body.
     *
     * @param mass The new mass of the rigid body.
     */
    public void setMass(float mass) {
        this.mass = mass;
    }

    /**
     * Returns the body type of the rigid body.
     *
     * @return The body type of the rigid body.
     */
    public BodyType getBodyType() {
        return bodyType;
    }

    /**
     * Sets the body type of the rigid body.
     *
     * @param bodyType The new body type of the rigid body.
     */
    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    /**
     * Returns whether the rigid body has fixed rotation.
     *
     * @return True if the rigid body has fixed rotation, false otherwise.
     */
    public boolean isFixedRotation() {
        return fixedRotation;
    }

    /**
     * Sets whether the rigid body should have fixed rotation.
     *
     * @param fixedRotation True if the rigid body should have fixed rotation, false otherwise.
     */
    public void setFixedRotation(boolean fixedRotation) {
        this.fixedRotation = fixedRotation;
    }

    /**
     * Returns whether the rigid body has continuous collision.
     *
     * @return True if the rigid body has continuous collision, false otherwise.
     */
    public boolean isContinuousCollision() {
        return continuousCollision;
    }

    /**
     * Sets whether the rigid body should have continuous collision.
     *
     * @param continuousCollision True if the rigid body should have continuous collision, false otherwise.
     */
    public void setContinuousCollision(boolean continuousCollision) {
        this.continuousCollision = continuousCollision;
    }

    /**
     * Returns the raw body of the rigid body.
     *
     * @return The raw body of the rigid body.
     */
    public Body getRawBody() {
        return rawBody;
    }

    /**
     * Sets the raw body of the rigid body.
     *
     * @param rawBody The new raw body of the rigid body.
     */
    public void setRawBody(Body rawBody) {
        this.rawBody = rawBody;
    }
}
