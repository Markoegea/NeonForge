package com.kingmarco.observers.events;

/**
 * A class responsible to storage the event type in an object.
 * */
public class Event {
    public EventType type;

    public Event(EventType type) {
        this.type = type;
    }

    public Event() {
        this.type = EventType.UserEvent;
    }
}
