package com.documentService.document.messaging.events;

/**
 * This is a generalised super class for all service events. Using the class the event handling
 * can be made generic, the other classes in this package are extending from this super class
 * and are used to define various service events
 */
public abstract class AbstractDocumentServiceEvent {

    public abstract DocumentServiceEventType getEventType();

}
