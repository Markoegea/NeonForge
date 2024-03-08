package com.kingmarco.observers;

import com.kingmarco.forge.GameObject;
import com.kingmarco.observers.events.Event;

/**
 * The Observer interface is used for classes that need to be notified of events.
 *
 * Classes that implement this interface must provide an implementation for the onNotify method.
 */
public interface Observer {
    /**
     * Method called when an event occurs.
     *
     * @param object The GameObject that the event is related to.
     * @param event The event that occurred.
     */
    void onNotify(GameObject object, Event event);
}
