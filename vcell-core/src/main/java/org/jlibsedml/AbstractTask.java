package org.jlibsedml;

public abstract class AbstractTask extends AbstractIdentifiableElement {

    public AbstractTask(String id, String name) {
        super(id, name);
    }
    public abstract String getModelReference() ;
    public abstract String getSimulationReference() ;
}
