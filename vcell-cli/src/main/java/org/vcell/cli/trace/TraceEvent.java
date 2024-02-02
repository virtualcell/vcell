package org.vcell.cli.trace;

public class TraceEvent {
    public enum EventType {
        Failure, Log
    }
    public final Span span;
    public final EventType eventType;
    public final String message;
    public final Exception exception;

    public TraceEvent(EventType eventType, Span span, String message) {
        this(eventType, span, message, null);
    }

    public TraceEvent(EventType eventType, Span span, String message, Exception exception) {
        this.eventType = eventType;
        this.span = span;
        this.message = message;
        this.exception = exception;
    }
}

