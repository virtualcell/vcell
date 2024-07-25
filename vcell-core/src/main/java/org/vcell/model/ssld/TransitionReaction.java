package org.vcell.model.ssld;

import java.util.Scanner;
import java.util.ArrayList;

import org.vcell.util.springsalad.IOHelp;

public class TransitionReaction extends Reaction {

    // The name of the reaction
    private String name;

    // The molecule, site type, intial state, and final state
    private Molecule molecule;
    private SiteType type;
    private State initialState;
    private State finalState;

    // The reaction might depend on binding to another site.  I call this the
    // "conditional" site (and molecule, etc.).
    private Molecule conditionalMolecule;
    private SiteType conditionalType;
    private State conditionalState;

    // The conditional state could be any state of the conditional type
    public final static String ANY_STATE_STRING = "Any_State";
    public final static State ANY_STATE = new State(null, ANY_STATE_STRING);

    // There might be no conditions on the reaction, or maybe it must be free, etc.
    public final static String NO_CONDITION = "None";
    public final static String FREE_CONDITION = "Free";
    public final static String BOUND_CONDITION = "Bound";

    // The condition on this reaction
    private String condition;

    // Each transition reaction has a single rate
    private double rate;  // Units s-1


    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String writeReaction() {
        throw new UnsupportedOperationException("This operation is implemented elsewhere for the vcell version of springsalad");
    }
    @Override
    public void loadReaction(SsldModel model, Scanner sc) {
        // TODO: implement this
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public String toString() {
        return name;
    }

}
