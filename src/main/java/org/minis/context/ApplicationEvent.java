package org.minis.context;

import java.util.EventObject;

/**
 * ApplicationEvent extends EventObject from the Java Utils Package, which is a simple wrapper around Java's event
 * listener in Java. This provides an entry point to decouple our code using the observer pattern
 * to decouple our code.
 */
public class ApplicationEvent extends EventObject {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public ApplicationEvent(Object source) {
        super(source);
    }
}
