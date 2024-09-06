package org.vcell.model.ssld;

public class State {

    private String name;

    private final SiteType type;                // reference to the site type
    private final StateCounter stateCounter;    // each state has a state counter

    public State(SiteType type, String name) {
        this.name = name;
        this.type = type;
        stateCounter = new StateCounter(this);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }

    public SiteType getType() {
        return type;
    }
    public String getTypeName() {
        return type.getName();
    }

    public Molecule getMolecule() {
        return type.getMolecule();
    }
    public String getMoleculeName() {
        return type.getMoleculeName();
    }
    public StateCounter getStateCounter() {
        return stateCounter;
    }

}
