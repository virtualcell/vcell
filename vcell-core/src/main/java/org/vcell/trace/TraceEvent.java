package org.vcell.trace;

import org.vcell.sbml.vcell.SBMLImportException;

public class TraceEvent {
    public enum EventType {
        Failure, Log, Success, UserDefined
    }
    public interface UserTraceEvent {
    }

    public final Span span;
    public final EventType eventType;
    public final String message;
    public final Exception exception;
    public final UserTraceEvent userTraceEvent;

    public TraceEvent(EventType eventType, Span span, String message) {
        this.eventType = eventType;
        this.span = span;
        this.message = message;
        this.exception = null;
        this.userTraceEvent = null;
    }

    public TraceEvent(EventType eventType, Span span, String message, Exception exception) {
        this.eventType = eventType;
        this.span = span;
        this.message = message;
        this.exception = exception;
        this.userTraceEvent = null;
    }

    public TraceEvent(EventType eventType, Span span, UserTraceEvent userTraceEvent) {
        this.eventType = eventType;
        this.span = span;
        this.message = null;
        this.exception = null;
        this.userTraceEvent = userTraceEvent;
    }

    // method to test if  class should be a subclass of Exception

    public boolean hasException(Class<? extends Exception> exceptionClass) {
        Exception e = exception;
        while (e != null) {
            if (exceptionClass.isInstance(e)) {
                return true;
            }
            if (e.getCause() instanceof Exception ee) {
                e = ee;
            } else {
                break;
            }
        }
        return false;
    }


}

