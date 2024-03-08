package com.kingmarco.observers;

import com.kingmarco.forge.GameObject;
import com.kingmarco.observers.events.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * A class is responsible for managing observers and notifying them of events.
 *
 * This class maintains a list of observers and provides methods to add observers and notify them of events.
 */
public class EventSystem {
    private static List<Observer> observers = new ArrayList<>();

    /**
     * Adds an observer to the list of observers.
     *
     * @param observer The observer to be added.
     */
    public static void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Notifies all observers of an event.
     *
     * This method iterates over the list of observers and calls their onNotify method, passing the GameObject and Event as arguments.
     *
     * @param obj The GameObject that the event is related to.
     * @param event The event that the observers are to be notified of.
     */
    public static void notify(GameObject obj, Event event){
        for (Observer observer: observers){
            observer.onNotify(obj, event);
        }
    }
}
