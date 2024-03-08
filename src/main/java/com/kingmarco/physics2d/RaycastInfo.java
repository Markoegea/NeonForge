package com.kingmarco.physics2d;

import com.kingmarco.forge.GameObject;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;

/**
 * The RaycastInfo class implements the RayCastCallback interface and stores information about a raycast.
 *
 * This class contains properties for the fixture, point, normal, fraction, hit status, and hit object of the raycast,
 * as well as the game object requesting the raycast. It also provides a method to report a fixture hit by the raycast.
 */
public class RaycastInfo implements RayCastCallback {

    public Fixture fixture;
    public Vector2f point;
    public Vector2f normal;
    public float fraction;
    public boolean hit;
    public GameObject hitObject;

    private GameObject requestingObject;

    public RaycastInfo(GameObject obj) {
        fixture = null;
        point = new Vector2f();
        normal = new Vector2f();
        fraction = 0.0f;
        hit = false;
        hitObject = null;
        this.requestingObject = obj;
    }

    /**
     * Reports a fixture hit by the raycast.
     *
     * This method is called when a fixture is hit by the raycast. It checks whether the fixture's game object is the same as the requesting game object,
     * and if not, it sets the properties of the RaycastInfo based on the fixture and returns the fraction at which the raycast hit the fixture.
     *
     * @param fixture The fixture hit by the raycast.
     * @param point The point at which the raycast hit the fixture.
     * @param normal The normal at the point where the raycast hit the fixture.
     * @param fraction The fraction of the raycast length at which the raycast hit the fixture.
     * @return The fraction at which the raycast hit the fixture, or 1 if the fixture's game object is the same as the requesting game object.
     */
    @Override
    public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
        if (fixture.m_userData == requestingObject) {
            return 1;
        }
        this.fixture = fixture;
        this.point = new Vector2f(point.x, point.y);
        this.normal = new Vector2f(normal.x, normal.y);
        this.fraction = fraction;
        this.hit = fraction != 0;
        this.hitObject = (GameObject) fixture.m_userData;

        return fraction;
    }
}
