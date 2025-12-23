package org.jlibsedml.components.task;

import org.jlibsedml.components.AbstractIdentifiableElement;

public abstract class AbstractTask extends AbstractIdentifiableElement {

    public AbstractTask(String id, String name) {
        super(id, name);
    }
    public abstract String getModelReference() ;
    public abstract String getSimulationReference() ;
}
