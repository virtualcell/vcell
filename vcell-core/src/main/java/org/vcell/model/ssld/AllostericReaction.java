package org.vcell.model.ssld;

import java.util.Scanner;
import java.util.ArrayList;

import cbit.vcell.Historical;
import org.vcell.util.springsalad.IOHelp;

public class AllostericReaction extends Reaction {

    private String name;

    private Molecule molecule;      // Molecule and site undergoing transition
    private Site site;
    private State initialState;
    private State finalState;

    private Site allostericSite;    // Reaction only proceed when allostericSite is in the right state
    private State allostericState;

    private double rate; // Reaction rate ( Units s-1 )

    public AllostericReaction() {
        this(null, null, null);
    }
    public AllostericReaction(String name) {
        this(name, null, null);
    }
    public AllostericReaction(String name, Molecule molecule, Site site) {
        this.name = name;
        this.molecule = molecule;
        this.site = site;
        this.initialState = null;
        this.finalState = null;
        this.allostericSite = null;
        this.allostericState = null;
        rate = 0;
    }

    @Override
    public String getName() {
        return name;
    }
    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }

    public void setMolecule(Molecule molecule) {
        this.molecule = molecule;
    }
    public Molecule getMolecule() {
        return molecule;
    }

    public void setSite(Site site) {
        this.site = site;
    }
    public Site getSite() {
        return site;
    }

    public void setInitialState(State state) {
        initialState = state;
    }
    public void setFinalState(State state) {
        finalState = state;
    }
    public State getInitialState() {
        return initialState;
    }
    public State getFinalState() {
        return finalState;
    }

    public void setAllostericSite(Site site) {
        this.allostericSite = site;
    }
    public void setAllostericState(State state) {
        this.allostericState = state;
    }
    public Site getAllostericSite() {
        return allostericSite;
    }
    public State getAllostericState() {
        return allostericState;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
    public double getRate() {
        return rate;
    }

    @Historical
    @Override
    public String writeReaction() {
        throw new UnsupportedOperationException("This operation is implemented elsewhere for the vcell version of springsalad");
    }

    @Override
    public void loadReaction(SsldModel model, Scanner sc) {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        name = IOHelp.getNameInQuotes(sc);
        sc.next();
        molecule = model.getMolecule(IOHelp.getNameInQuotes(sc));
        sc.next();
        sc.next();
        site = molecule.getSite(sc.nextInt());
        sc.next();
        SiteType type = site.getType();
        initialState = type.getState(IOHelp.getNameInQuotes(sc));
        sc.next();
        finalState = type.getState(IOHelp.getNameInQuotes(sc));
        sc.next();
        rate = sc.nextDouble();
        sc.next();
        allostericSite = molecule.getSite(sc.nextInt());
        sc.next();
        SiteType alloType = allostericSite.getType();
        allostericState = alloType.getState(IOHelp.getNameInQuotes(sc));
        // </editor-fold>
    }

    public static ArrayList<AllostericReaction> loadReactions(SsldModel model, Scanner sc) {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        ArrayList<AllostericReaction> allostericReactions = new ArrayList<>();
        AllostericReaction reaction;
        while(sc.hasNextLine()){
            reaction= new AllostericReaction();
            reaction.loadReaction(model, new Scanner(sc.nextLine()));
            allostericReactions.add(reaction);
        }
        sc.close();
        return allostericReactions;
        // </editor-fold>
    }

}
