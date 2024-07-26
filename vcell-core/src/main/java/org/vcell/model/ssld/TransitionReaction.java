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

    private String condition;   // The condition on this reaction
    private double rate;        // Each transition reaction has a single rate ( Units s-1 )


    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String writeReaction() {
        throw new UnsupportedOperationException("This operation is implemented elsewhere for the vcell version of springsalad");
    }
    @Override
    public void loadReaction(SsldModel model, Scanner dataScanner) {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        name = IOHelp.getNameInQuotes(dataScanner);
        dataScanner.next();
        molecule = model.getMolecule(IOHelp.getNameInQuotes(dataScanner));
        dataScanner.next();
        type = molecule.getType(IOHelp.getNameInQuotes(dataScanner));
        dataScanner.next();
        initialState = type.getState(IOHelp.getNameInQuotes(dataScanner));
        dataScanner.next();
        finalState = type.getState(IOHelp.getNameInQuotes(dataScanner));
        dataScanner.next();
        rate = dataScanner.nextDouble();
        dataScanner.next();
        condition = dataScanner.next();
        if(!condition.equals(BOUND_CONDITION)){
            conditionalMolecule = null;
            conditionalType = null;
            conditionalState = null;
        } else {
            conditionalMolecule = model.getMolecule(IOHelp.getNameInQuotes(dataScanner));
            dataScanner.next();
            conditionalType = conditionalMolecule.getType(IOHelp.getNameInQuotes(dataScanner));
            dataScanner.next();
            String condState = IOHelp.getNameInQuotes(dataScanner);
            if(condState.equals(TransitionReaction.ANY_STATE_STRING)){
                conditionalState = TransitionReaction.ANY_STATE;
            } else {
                conditionalState = conditionalType.getState(condState);
            }
        }
        // </editor-fold>
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public String toString() {
        return name;
    }

    public static ArrayList<TransitionReaction> loadReactions(SsldModel model, Scanner sc){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        ArrayList<TransitionReaction> transitionReactions = new ArrayList<>();
        TransitionReaction reaction;
        while(sc.hasNextLine()){
            reaction = new TransitionReaction();
            reaction.loadReaction(model, new Scanner(sc.nextLine()));
            transitionReactions.add(reaction);
        }
        sc.close();
        return transitionReactions;
        // </editor-fold>
    }

}
