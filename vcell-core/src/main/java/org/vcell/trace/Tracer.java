package org.vcell.trace;

import java.util.*;


public class Tracer {

    // single global instance of CliTracer, but each thread has its own stack of spans
    private final static Tracer instance = new Tracer();
    private final static ThreadLocal<Stack<Span>> spanStackVar = ThreadLocal.withInitial(Stack<Span>::new);
    private final static ThreadLocal<Span> rootSpanVar = ThreadLocal.withInitial(() -> new Span(null, Span.ContextType.Root, "root"));
    private final static ThreadLocal<List<TraceEvent>> traceEventsVar = ThreadLocal.withInitial(ArrayList::new);

    public static void failure(Exception exception, String message) {
        traceEventsVar.get().add(new TraceEvent(TraceEvent.EventType.Failure, currentSpan(), message, exception));
    }

    public static void log(String message) {
        traceEventsVar.get().add(new TraceEvent(TraceEvent.EventType.Log, currentSpan(), message));
    }

    public static List<TraceEvent> getTraceEvents() {
        return traceEventsVar.get();
    }

    public static void clearTraceEvents() {
        traceEventsVar.remove();
    }

    // write text report of error events to System.err
    public static void reportErrors(boolean bPrintStacktrace) {
        //List<TraceEvent> events = getTraceEvents();
        List<TraceEvent> events = traceEventsVar.get();
        for (TraceEvent event : events) {
            if (event.eventType == TraceEvent.EventType.Failure) {
                System.err.println(event.span.getNestedContextName()+"**** Error: " + event.message);
                if (bPrintStacktrace)
                    event.exception.printStackTrace(System.err);
            }
        }
    }

    // has error event been recorded?
    public static boolean hasErrors() {
        return traceEventsVar.get().stream().anyMatch(event -> event.eventType == TraceEvent.EventType.Failure);
    }

    public static Span currentSpan() {
        if (spanStackVar.get().empty()) {
            return rootSpanVar.get();
        }else{
            return spanStackVar.get().peek();
        }
    }

    public static void addTag(String key, String value) {
        currentSpan().addTag(key, value);
    }

    // start span
    public static Span startSpan(Span.ContextType contextType, String spanName) {
        return startSpan(contextType, spanName, null);
    }

    public static Span startSpan(Span.ContextType contextType, String spanName, Map<String,String> tags) {
        Span span = new Span(currentSpan(), contextType, spanName, tags);
        spanStackVar.get().push(span);
        return span;
    }

    static void endSpan(Span span) {
        if (span == rootSpanVar.get()) {
            throw new IllegalStateException("Cannot end root span");
        }
        if (spanStackVar.get().empty()) {
            throw new IllegalStateException("No span to end");
        }
        if (spanStackVar.get().peek() != span){
            throw new IllegalStateException("Span to end is not the current span");
        }
        spanStackVar.get().pop();
    }
}
