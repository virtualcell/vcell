package org.vcell.trace;

import java.util.HashMap;
import java.util.Map;

public class Span implements AutoCloseable {

    public enum ContextType {
        Root,
        Other,
        OMEX_EXPORT,
        OMEX_EXECUTE,
        SIMULATION_RUN,
        PROCESSING_SIMULATION_OUTPUTS,
        PROCESSING_SEDML,
        SIMULATIONS_RUN,
        BioModel;
    }

    public String getNestedContextName() {
        // compose a string of context types and names from the root to this span
        StringBuilder sb = new StringBuilder();
        Span currentSpan = this;
        while (currentSpan != null) {
            sb.insert(0, currentSpan.contextType + "(" + currentSpan.contextName + ") | ");
            currentSpan = currentSpan.parentSpan;
        }
        sb.insert(0, " | ");
        return sb.toString();
    }

    public final Span parentSpan;
    private final ContextType contextType;
    private final String contextName;
    private final HashMap<String, String> tags = new HashMap<>();

    Span(Span parentSpan, ContextType contextType, String contextName, Map<String,String> tags) {
        this.parentSpan = parentSpan;
        this.contextType = contextType;
        this.contextName = contextName;
        if (tags != null) this.tags.putAll(tags);
    }
    Span(Span parentSpan, ContextType contextType, String contextName) {
        this(parentSpan, contextType, contextName, null);
    }

    public void addTag(String key, String value) {
        this.tags.put(key, value);
    }

    public Map<String,String> getTags() {
        return tags;
    }

    public ContextType getContextType() {
        return contextType;
    }
    public String getContextName() {
        return contextName;
    }

    public void close() {
        Tracer.endSpan(this);
    }
}
