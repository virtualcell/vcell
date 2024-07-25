package org.vcell.model.ssld;

import java.util.Scanner;
import java.util.ArrayList;

import cbit.vcell.Historical;
import org.vcell.util.springsalad.IOHelp;

public class BindingReaction extends Reaction {


    // Might want to name the reactions
    private String name;

    private final Molecule [] molecule = new Molecule[2];
    private final SiteType [] type = new SiteType[2];
    private final State [] state = new State[2];

    private double kon = 0;
    private double koff = 0;

    // Let the user specify the bond length, in nm
    private double bondLength = 1.0;

    public final static String ANY_STATE_STRING = "Any_State";
    public final static State ANY_STATE = new State(null, ANY_STATE_STRING);

    // It's easiest to include a bondData class with each BindingReaction.
    // This isn't the cleanest way to handle this, but putting BondCounter classes
    // somewhere else would be much more work at this point.
    private final BondCounter bondData;

    public BindingReaction() {
        this(null);
    }

    public BindingReaction(String name) {
        this.name = name;
        bondData = new BondCounter(this);
        for(int i=0; i<2; i++) {
            molecule[i] = null;
            type[i] = null;
            state[i] = null;
        }
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String getName() {
        return name;
    }
    public void setkon(double kon ) {
        this.kon = kon;
    }
    public double getkon() {
        return kon;
    }
    public void setkoff(double koff) {
        this.koff = koff;
    }
    public double getkoff() {
        return koff;
    }
    public void setBondLength(double length) {
        bondLength = length;
    }
    public double getBondLength() {
        return bondLength;
    }

    public Molecule [] getMolecules() {
        return molecule;
    }
    public Molecule getMolecule(int i) {
        return molecule[i];
    }
    public void setMolecules(Molecule mol1, Molecule mol2) {
        molecule[0] = mol1;
        molecule[1] = mol2;
    }
    public void setMolecule(int i, Molecule mol) {
        molecule[i] = mol;
    }

    public SiteType [] getTypes() {
        return type;
    }
    public SiteType getType(int i) {
        return type[i];
    }
    public void setTypes(SiteType type1, SiteType type2) {
        type[0] = type1;
        type[1] = type2;
    }
    public void setType(int i, SiteType type) {
        this.type[i] = type;
    }

    public State [] getStates() {
        return state;
    }
    public State getState(int i) {
        return state[i];
    }
    public void setStates(State state1, State state2) {
        state[0] = state1;
        state[1] = state2;
    }
    public void setState(int i, State state) {
        this.state[i] = state;
    }
    public BondCounter getBondCounter() {
        return bondData;
    }

    @Override
    public String toString() {
        return name;
    }

    @Historical
    @Override
    public String writeReaction() {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        StringBuilder sb = new StringBuilder();
//        sb.append("'").append(name).append("'       ");
//        if(molecule[0] != null && molecule[1] != null){
//            sb.append("'").append(molecule[0].getName()).append("' : '")
//                    .append(type[0].getName()).append("' : '")
//                    .append(state[0].toString());
//            sb.append("'  +  '");
//            sb.append(molecule[1].getName()).append("' : '")
//                    .append(type[1].getName()).append("' : '")
//                    .append(state[1].toString());
//            sb.append("'  kon ").append(Double.toString(kon));
//            sb.append("  koff ").append(Double.toString(koff));
//            sb.append("  Bond_Length ").append(Double.toString(bondLength));
//        }
        return sb.toString();
        // </editor-fold>
    }

    /*
     * The maximum possible on-rate is given by kon_max = 4*pi*R*D, so we'd
     * better have kon < 4*pi*R*D . If that isn't satisfied then nothing else will work.
     */
    public boolean checkOnRate() {

        if(type[0] == null || type[1] == null) {
            return true;
        }
        double R = type[0].getReactionRadius() + type[1].getReactionRadius();	// nm
        double D = type[0].getD() + type[1].getD();

        double kon_scale = 1660000.0 * kon;
        double D_scale = D * 1000000.0;				// nm^2/s
        double rhs1 = 4.0*Math.PI*R*D_scale;

        boolean check = (kon_scale < rhs1);
        return check;
    }

    @Override
    public void loadReaction(SsldModel model, Scanner dataScanner) {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        name = IOHelp.getNameInQuotes(dataScanner);
        molecule[0] = model.getMolecule(IOHelp.getNameInQuotes(dataScanner));
        dataScanner.next();
        type[0] = molecule[0].getType(IOHelp.getNameInQuotes(dataScanner));
        dataScanner.next();
        String state0 = IOHelp.getNameInQuotes(dataScanner);
        if(state0.equals(BindingReaction.ANY_STATE_STRING)){
            state[0] = BindingReaction.ANY_STATE;
        } else {
            state[0] = type[0].getState(state0);
        }
        // Skip the plus sign
        dataScanner.next();
        molecule[1] = model.getMolecule(IOHelp.getNameInQuotes(dataScanner));
        dataScanner.next();
        type[1] = molecule[1].getType(IOHelp.getNameInQuotes(dataScanner));
        dataScanner.next();
        String state1 = IOHelp.getNameInQuotes(dataScanner);
        if(state1.equals(BindingReaction.ANY_STATE_STRING)){
            state[1] = BindingReaction.ANY_STATE;
        } else {
            state[1] = type[1].getState(state1);
        }
        dataScanner.next();     // Skip 'kon'
        kon = dataScanner.nextDouble();
        dataScanner.next();     // Skip 'koff'
        koff = dataScanner.nextDouble();
        dataScanner.next();     // Skip bond_length
        bondLength = dataScanner.nextDouble();
        // </editor-fold>
    }

    public static ArrayList<BindingReaction> loadReactions(SsldModel model, Scanner sc) {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        ArrayList<BindingReaction> reactions = new ArrayList<>();
        BindingReaction reaction;
        while(sc.hasNextLine()){
            reaction = new BindingReaction();
            reaction.loadReaction(model, new Scanner(sc.nextLine()));
            reactions.add(reaction);
        }
        sc.close();
        return reactions;
        // </editor-fold>
    }

}
