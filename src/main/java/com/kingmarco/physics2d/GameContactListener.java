package com.kingmarco.physics2d;

import com.kingmarco.components.Component;
import com.kingmarco.forge.GameObject;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

/**
 * The GameContactListener class implements the ContactListener interface and handles contact between game objects.
 *
 * This class provides methods to handle the beginning and end of contact, as well as pre-solve and post-solve operations.
 */
public class GameContactListener implements ContactListener {

    /**
     * Called when contact begins between two game objects.
     *
     * This method retrieves the game objects involved in the contact, calculates the normal of the contact,
     * and calls the beginCollision method on all components of the game objects.
     *
     * @param contact The contact between the game objects.
     */
    @Override
    public void beginContact(Contact contact) {
        GameObject objA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objB = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c : objA.getAllComponents()) {
            c.beginCollision(objB, contact, aNormal);
        }

        for (Component c : objB.getAllComponents()) {
            c.beginCollision(objA, contact, bNormal);
        }
    }

    /**
     * Called when contact ends between two game objects.
     *
     * This method retrieves the game objects involved in the contact, calculates the normal of the contact,
     * and calls the endCollision method on all components of the game objects.
     *
     * @param contact The contact between the game objects.
     */
    @Override
    public void endContact(Contact contact) {
        GameObject objA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objB = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c : objA.getAllComponents()) {
            c.endCollision(objB, contact, aNormal);
        }

        for (Component c : objB.getAllComponents()) {
            c.endCollision(objA, contact, bNormal);
        }
    }

    /**
     * Called before the solver calculates the contact.
     *
     * This method retrieves the game objects involved in the contact, calculates the normal of the contact,
     * and calls the preSolve method on all components of the game objects.
     *
     * @param contact The contact between the game objects.
     * @param manifold The contact manifold.
     */
    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        GameObject objA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objB = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c : objA.getAllComponents()) {
            c.preSolve(objB, contact, aNormal);
        }

        for (Component c : objB.getAllComponents()) {
            c.preSolve(objA, contact, bNormal);
        }
    }

    /**
     * Called after the solver calculates the contact.
     *
     * This method retrieves the game objects involved in the contact, calculates the normal of the contact,
     * and calls the postSolve method on all components of the game objects.
     *
     * @param contact The contact between the game objects.
     * @param contactImpulse The impulse applied during the contact.
     */
    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        GameObject objA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objB = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c : objA.getAllComponents()) {
            c.postSolve(objB, contact, aNormal);
        }

        for (Component c : objB.getAllComponents()) {
            c.postSolve(objA, contact, bNormal);
        }
    }
}
