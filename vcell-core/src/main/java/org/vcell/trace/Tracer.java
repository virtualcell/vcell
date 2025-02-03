package org.vcell.trace;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Tracer {
    // We want each thread to have its own "singleton"; we don't want to be able to add to another thread's tracer, but we want the ability to "observe it"
    private static final ConcurrentHashMap<Long, Tracer> masterTracerRecord = new ConcurrentHashMap<>();

    // single global instance of CliTracer, but each thread has its own stack of spans
//    private final ThreadLocal<Stack<Span>> spanStackVar = ThreadLocal.withInitial(Stack<Span>::new);
//    private final ThreadLocal<Span> rootSpanVar = ThreadLocal.withInitial(() -> new Span(null, Span.ContextType.Root, "root"));
//    private final ThreadLocal<List<TraceEvent>> traceEventsVar = ThreadLocal.withInitial(ArrayList::new);
    private final Stack<Span> spanStackVar;
    private final Span rootSpanVar;
    private final List<TraceEvent> traceEventsVar;

    public static Tracer getInstance(){
        return Tracer.getInstance(Thread.currentThread().getId());
    }

    public static Tracer getInstance(long threadId) {
        if (!Tracer.masterTracerRecord.containsKey(threadId)) Tracer.masterTracerRecord.put(threadId, new Tracer());
        return Tracer.masterTracerRecord.get(threadId);
    }

    private Tracer() {
        this.spanStackVar = new Stack<>();
        this.rootSpanVar = new Span(null, Span.ContextType.Root, "root");
        this.traceEventsVar = new ArrayList<>();
    }

    /**
     * This method is explicitly static because it should only ever do work on the current thread, and *never* modify work on another thread.
     * FAILING TO OBEY THIS MAY CAUSE RACE CONDITIONS, MESSY / HARD TO DEBUG CODE, ETC.
     * @param message message to record with the success
     */
    public static void success(String message) {
        Tracer.getInstance().traceEventsVar.add(new TraceEvent(TraceEvent.EventType.Success, currentSpan(), message));
    }

    /**
     * This method is explicitly static because it should only ever do work on the current thread, and *never* modify work on another thread.
     * FAILING TO OBEY THIS MAY CAUSE RACE CONDITIONS, MESSY / HARD TO DEBUG CODE, ETC.
     * @param message message to record with the failure
     */
    public static void failure(Exception exception, String message) {
        Tracer.getInstance().traceEventsVar.add(new TraceEvent(TraceEvent.EventType.Failure, currentSpan(), message, exception));
    }

    /**
     * This method is explicitly static because it should only ever do work on the current thread, and *never* modify work on another thread.
     * FAILING TO OBEY THIS MAY CAUSE RACE CONDITIONS, MESSY / HARD TO DEBUG CODE, ETC.
     * @param message message to log
     */
    public static void log(String message) {
        Tracer.getInstance().traceEventsVar.add(new TraceEvent(TraceEvent.EventType.Log, currentSpan(), message));
    }

    /**
     * This method is explicitly static because it should only ever do work on the current thread, and *never* modify work on another thread.
     * FAILING TO OBEY THIS MAY CAUSE RACE CONDITIONS, MESSY / HARD TO DEBUG CODE, ETC.
     * @param userTraceEvent user trace event to log
     */
    public void logEvent(TraceEvent.UserTraceEvent userTraceEvent) {
        Tracer.getInstance().traceEventsVar.add(new TraceEvent(TraceEvent.EventType.UserDefined, currentSpan(), userTraceEvent));
    }

    /**
     * Returned list is unmodifiable because we only want the owning thread to make edits.
     * @return an unmodified copy of the underlying trace events
     */
    public List<TraceEvent> getTraceEvents() {
        return List.copyOf(this.traceEventsVar);
    }

    /**
     * Method is static so that only the thread that "owns" the trace events may clear it.
     */
    public static void clearTraceEvents() {
        Tracer.getInstance().traceEventsVar.clear();
    }

    // write text report of error events to System.err
    public void reportErrors(boolean bPrintStacktrace) {
        for (TraceEvent event : this.getTraceEvents()) {
            if (event.eventType != TraceEvent.EventType.Failure) continue;
            System.err.println(event.span.getNestedContextName() + "**** Error: " + event.message);
            if (bPrintStacktrace && null != event.exception) event.exception.printStackTrace(System.err);
        }
    }

    public List<TraceEvent> getErrors() {
        return this.getTraceEvents().stream().filter(event -> event.eventType == TraceEvent.EventType.Failure).toList();
    }

    // has error event been recorded?
    public boolean hasErrors() {
        return !this.getErrors().isEmpty();
    }

    public static Span currentSpan() {
        Tracer currentInstance = Tracer.getInstance();
        return currentInstance.spanStackVar.empty() ? currentInstance.rootSpanVar : currentInstance.spanStackVar.peek();
    }

    public static void addTag(String key, String value) {
        Tracer.currentSpan().addTag(key, value);
    }

    // start span
    public static Span startSpan(Span.ContextType contextType, String spanName) {
        return Tracer.startSpan(contextType, spanName, null);
    }

    public static Span startSpan(Span.ContextType contextType, String spanName, Map<String,String> tags) {
        Span span = new Span(currentSpan(), contextType, spanName, tags);
        Tracer.getInstance().spanStackVar.push(span);
        return span;
    }

    static void endSpan(Span span) {
        Tracer currentInstance = Tracer.getInstance();
        if (span == currentInstance.rootSpanVar) {
            throw new IllegalStateException("Cannot end root span");
        }
        if (currentInstance.spanStackVar.empty()) {
            throw new IllegalStateException("No span to end");
        }
        if (currentInstance.spanStackVar.peek() != span){
            throw new IllegalStateException("Span to end is not the current span");
        }
        currentInstance.spanStackVar.pop();
    }
}
