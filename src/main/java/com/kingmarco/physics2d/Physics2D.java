package com.kingmarco.physics2d;

import com.kingmarco.components.PillboxCollider;
import com.kingmarco.components.Transform;
import com.kingmarco.forge.GameObject;
import com.kingmarco.forge.Window;
import com.kingmarco.physics2d.components.Box2DCollider;
import com.kingmarco.physics2d.components.CircleCollider;
import com.kingmarco.physics2d.components.RigidBody2D;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.joml.Vector2f;

/**
 * The Physics2D class handles the physics of the game.
 *
 * This class provides methods to add and destroy game objects, update the physics system, set sensors, reset colliders, and add colliders.
 */
public class Physics2D {
    private Vec2 gravity = new Vec2(0, -10.0f);
    private World world = new World(gravity);

    private float physicsTime = 0.0f;
    private float physicsTimeStep = 1.0f / 60.0f;
    private int velocityIterations = 8;
    private int positionIterations = 3;

    public Physics2D() {
        world.setContactListener(new GameContactListener());
    }

    /**
     * Adds a game object to the physics system.
     *
     * This method retrieves the RigidBody2D component of the game object, creates a BodyDef based on the properties of the RigidBody2D,
     * creates a Body in the world using the BodyDef, and adds colliders to the Body based on the components of the game object.
     *
     * @param go The game object to be added.
     */
    public void add(GameObject go){
        RigidBody2D rb = go.getComponent(RigidBody2D.class);
        if (rb != null && rb.getRawBody() == null) {
            Transform transform = go.transform;

            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float)Math.toRadians(transform.rotation);
            bodyDef.position.set(transform.position.x, transform.position.y);
            bodyDef.angularDamping = rb.getAngularDamping();
            bodyDef.linearDamping = rb.getLinearDamping();
            bodyDef.fixedRotation = rb.isFixedRotation();
            bodyDef.bullet = rb.isContinuousCollision();
            bodyDef.gravityScale = rb.gravityScale;
            bodyDef.angularVelocity = rb.angularVelocity;
            bodyDef.userData = rb.gameObject;

            switch (rb.getBodyType()){
                case Kinematic: bodyDef.type = BodyType.KINEMATIC; break;
                case Static: bodyDef.type = BodyType.STATIC; break;
                case Dynamic: bodyDef.type = BodyType.DYNAMIC; break;
            }

            Body body = this.world.createBody(bodyDef);
            body.m_mass = rb.getMass();
            rb.setRawBody(body);

            CircleCollider circleCollider;
            Box2DCollider boxCollider;
            PillboxCollider pillboxCollider;

            if ((circleCollider = go.getComponent(CircleCollider.class)) != null){
                addCircleCollider(rb, circleCollider);
            }
            if ((boxCollider = go.getComponent(Box2DCollider.class)) != null) {
                addBox2DCollider(rb, boxCollider);
            }
            if ((pillboxCollider = go.getComponent(PillboxCollider.class)) != null) {
                addPillboxCollider(rb, pillboxCollider);
            }
        }
    }

    /**
     * Destroys a game object in the physics system.
     *
     * This method retrieves the RigidBody2D component of the game object, destroys the raw body in the world, and sets the raw body of the RigidBody2D to null.
     *
     * @param go The game object to be destroyed.
     */
    public void destroyGameObject(GameObject go){
        RigidBody2D rb = go.getComponent(RigidBody2D.class);
        if (rb == null) return;
        if (rb.getRawBody() == null) return;
        world.destroyBody(rb.getRawBody());
        rb.setRawBody(null);
    }

    /**
     * Updates the physics system.
     *
     * This method increments the physics time by the time passed since the last frame, and if the physics time is greater than or equal to 0,
     * it decrements the physics time by the physics time step and steps the world.
     *
     * @param dt The time passed since the last frame.
     */
    public void update(float dt){
        physicsTime += dt;
        if (physicsTime >= 0.0f){
            physicsTime -= physicsTimeStep;
            world.step(physicsTimeStep, velocityIterations, positionIterations);
        }
    }

    /**
     * Sets a rigid body as a sensor.
     *
     * This method retrieves the raw body of the rigid body, and if it exists, it sets all fixtures of the raw body as sensors.
     *
     * @param rb The rigid body to be set as a sensor.
     */
    public void setIsSensor(RigidBody2D rb) {
        Body body = rb.getRawBody();
        if (body == null) return;

        Fixture fixture = body.getFixtureList();
        while (fixture != null){
            fixture.m_isSensor = true;
            fixture = fixture.m_next;
        }
    }

    /**
     * Sets a rigid body as not a sensor.
     *
     * This method retrieves the raw body of the rigid body, and if it exists, it sets all fixtures of the raw body as not sensors.
     *
     * @param rb The rigid body to be set as not a sensor.
     */
    public void setNotSensor(RigidBody2D rb) {
        Body body = rb.getRawBody();
        if (body == null) return;

        Fixture fixture = body.getFixtureList();
        while (fixture != null){
            fixture.m_isSensor = false;
            fixture = fixture.m_next;
        }
    }

    /**
     * Resets a circle collider of a rigid body.
     *
     * This method retrieves the raw body of the rigid body, destroys all fixtures of the raw body, adds a new circle collider to the rigid body, and resets the mass data of the raw body.
     *
     * @param rb The rigid body whose circle collider is to be reset.
     * @param circleCollider The new circle collider to be added to the rigid body.
     */
    public void resetCircleCollider(RigidBody2D rb, CircleCollider circleCollider) {
        Body body = rb.getRawBody();
        if (body == null) return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addCircleCollider(rb, circleCollider);
        body.resetMassData();
    }

    /**
     * Adds a circle collider to a rigid body.
     *
     * This method retrieves the raw body of the rigid body, creates a CircleShape based on the properties of the circle collider,
     * creates a FixtureDef based on the properties of the CircleShape and the rigid body, and creates a fixture in the raw body using the FixtureDef.
     *
     * @param rb The rigid body to which the circle collider is to be added.
     * @param circleCollider The circle collider to be added to the rigid body.
     */
    public void addCircleCollider(RigidBody2D rb, CircleCollider circleCollider) {
        Body body = rb.getRawBody();
        assert body != null : "Raw body must not be null";

        CircleShape shape = new CircleShape();
        shape.setRadius(circleCollider.getRadius());
        shape.m_p.set(circleCollider.getOffset().x, circleCollider.getOffset().y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = circleCollider.gameObject;
        fixtureDef.isSensor = rb.isSensor();
        body.createFixture(fixtureDef);
    }

    /**
     * Resets a Box2DCollider of a rigid body.
     *
     * This method retrieves the raw body of the rigid body, destroys all fixtures of the raw body, adds a new Box2DCollider to the rigid body, and resets the mass data of the raw body.
     *
     * @param rb The rigid body whose Box2DCollider is to be reset.
     * @param boxCollider The new Box2DCollider to be added to the rigid body.
     */
    public void resetBox2DCollider(RigidBody2D rb, Box2DCollider boxCollider) {
        Body body = rb.getRawBody();
        if (body == null) return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addBox2DCollider(rb, boxCollider);
        body.resetMassData();
    }

    /**
     * Adds a Box2DCollider to a rigid body.
     *
     * This method retrieves the raw body of the rigid body, creates a PolygonShape based on the properties of the Box2DCollider, creates a FixtureDef based on the properties of the PolygonShape and the rigid body, and creates a fixture in the raw body using the FixtureDef.
     *
     * @param rb The rigid body to which the Box2DCollider is to be added.
     * @param boxCollider The Box2DCollider to be added to the rigid body.
     */
    public void addBox2DCollider(RigidBody2D rb, Box2DCollider boxCollider){
        Body body = rb.getRawBody();
        assert body != null : "Raw body must not be null";

        PolygonShape shape = new PolygonShape();
        Vector2f halfSize = new Vector2f(boxCollider.getHalfSize()).mul(0.5f);
        Vector2f offset = boxCollider.getOffset();
        Vector2f origin = new Vector2f(boxCollider.getOrigin());
        shape.setAsBox(halfSize.x, halfSize.y, new Vec2(offset.x, offset.y), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = boxCollider.gameObject;
        fixtureDef.isSensor = rb.isSensor();
        body.createFixture(fixtureDef);
    }

    /**
     * Resets a PillboxCollider of a rigid body.
     *
     * This method retrieves the raw body of the rigid body, destroys all fixtures of the raw body, adds a new PillboxCollider to the rigid body, and resets the mass data of the raw body.
     *
     * @param rb The rigid body whose PillboxCollider is to be reset.
     * @param pillboxCollider The new PillboxCollider to be added to the rigid body.
     */
    public void resetPillboxCollider(RigidBody2D rb, PillboxCollider pillboxCollider) {
        Body body = rb.getRawBody();
        if (body == null) return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addPillboxCollider(rb, pillboxCollider);
        body.resetMassData();
    }

    /**
     * Adds a PillboxCollider to a rigid body.
     *
     * This method retrieves the raw body of the rigid body, and adds a Box2DCollider and two CircleColliders to the rigid body based on the properties of the PillboxCollider.
     *
     * @param rb The rigid body to which the PillboxCollider is to be added.
     * @param pillboxCollider The PillboxCollider to be added to the rigid body.
     */
    public void addPillboxCollider(RigidBody2D rb, PillboxCollider pillboxCollider){
        Body body = rb.getRawBody();
        assert body != null : "Raw body must not be null";

        addBox2DCollider(rb, pillboxCollider.getBox());
        addCircleCollider(rb, pillboxCollider.getTopCircle());
        addCircleCollider(rb, pillboxCollider.getBottomCircle());
    }

    /**
     * Returns the size of the fixture list of a body.
     *
     * This method iterates over the fixtures of the body and counts them.
     *
     * @param body The body whose fixture list size is to be returned.
     * @return The size of the fixture list of the body.
     */
    private int fixtureListSize(Body body){
        int size = 0;
        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            size++;
            fixture = fixture.m_next;
        }
        return size;
    }

    /**
     * Returns whether the world is locked.
     *
     * @return True if the world is locked, false otherwise.
     */
    public boolean isLocked() {
        return world.isLocked();
    }

    /**
     * Checks whether a game object is on the ground.
     *
     * This method performs two raycasts from the game object towards the ground, and returns true if either raycast hits an object.
     *
     * @param gameObject The game object to check.
     * @param innerPlayerWidth The inner width of the player.
     * @param height The height to raycast.
     * @return True if the game object is on the ground, false otherwise.
     */
    public static boolean checkOnGround(GameObject gameObject, float innerPlayerWidth, float height) {
        Vector2f raycastBegin = new Vector2f(gameObject.transform.position);
        raycastBegin.sub(innerPlayerWidth / 2.0f, 0.0f);
        Vector2f raycastEnd = new Vector2f(raycastBegin).add(0.0f, height);

        RaycastInfo info = Window.getPhysics().raycast(gameObject, raycastBegin, raycastEnd);

        Vector2f raycast2Begin = new Vector2f(raycastBegin).add(innerPlayerWidth, 0.0f);
        Vector2f raycast2End = new Vector2f(raycastEnd).add(innerPlayerWidth, 0.0f);
        RaycastInfo info2 = Window.getPhysics().raycast(gameObject, raycast2Begin, raycast2End);

        return ((info.hit && info.hitObject != null) || (info2.hit && info2.hitObject != null));
    }

    /**
     * Performs a raycast from one point to another.
     *
     * This method creates a RaycastInfo callback, performs a raycast in the world using the callback and the two points, and returns the callback.
     *
     * @param requestingObject The game object requesting the raycast.
     * @param point1 The starting point of the raycast.
     * @param point2 The ending point of the raycast.
     * @return The RaycastInfo callback used in the raycast.
     */
    public RaycastInfo raycast(GameObject requestingObject, Vector2f point1, Vector2f point2) {
        RaycastInfo callBack = new RaycastInfo(requestingObject);
        world.raycast(callBack, new Vec2(point1.x, point1.y), new Vec2(point2.x, point2.y));
        return callBack;
    }

    /**
     * Returns the gravity of the world.
     *
     * @return The gravity of the world.
     */
    public Vector2f getGravity() {
        return new Vector2f(this.world.getGravity().x, world.getGravity().y);
    }
}
