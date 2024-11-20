package org.vcell.trace;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TracerTest {

    @BeforeEach
    public void setUp() {
        Tracer.clearTraceEvents();
    }

    @Test
    public void testSpan() {
        Span span = null;
        try {
            span = Tracer.startSpan(Span.ContextType.Other, "Application 1");
            Tracer.addTag("key", "value");
            span.addTag("key", "value");
            throw new Exception("error message");
        } catch (Exception e) {
            Tracer.failure(e, "error message");
        } finally {
            if (span != null) {
                span.close();
            }
        }
        TraceEvent errorEvent = Tracer.getTraceEvents().stream()
                .filter(event -> event.eventType == TraceEvent.EventType.Failure).findFirst().get();
        Assertions.assertEquals("error message", errorEvent.message);
    }

    @Test
    public void testNestedSpan() {
        Span outer_span = null;
        try {
            outer_span = Tracer.startSpan(Span.ContextType.Other, "outer");
            Span inner_span = null;
            try {
                inner_span = Tracer.startSpan(Span.ContextType.Other, "inner");
                Tracer.addTag("location", "inside");
                throw new Exception("inner error message");
            } catch (Exception e) {
                Tracer.failure(e, "exception in inner span");
            } finally {
                if (inner_span != null) {
                    inner_span.close();
                }
            }
        } finally {
            if (outer_span != null) {
                outer_span.close();
            }
        }
        TraceEvent errorEvent = Tracer.getTraceEvents().stream()
                .filter(event -> event.eventType == TraceEvent.EventType.Failure).findFirst().get();
        Assertions.assertEquals("exception in inner span", errorEvent.message);
        Assertions.assertEquals(errorEvent.span.getNestedContextName(), " | Root(root) | Other(outer) | Other(inner) | ");
    }

}
