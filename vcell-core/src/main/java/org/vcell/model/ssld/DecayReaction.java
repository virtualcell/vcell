package org.vcell.model.ssld;

import java.util.Scanner;
import java.util.ArrayList;

import cbit.vcell.Historical;
import org.vcell.util.springsalad.IOHelp;

public class DecayReaction extends Reaction {

    private final Molecule molecule;
    private double kcreate; // Units uM/s
    private double kdecay;  // Units 1/s

    public DecayReaction(Molecule molecule) {
        this.molecule = molecule;
        kcreate = 0;
        kdecay = 0;
    }

    public void setDecayRate(double rate) {
        kdecay = rate;
    }
    public void setCreationRate(double rate) {
        kcreate = rate;
    }
    public double getDecayRate() {
        return kdecay;
    }
    public double getCreationRate() {
        return kcreate;
    }

    @Override
    public String getName() {
        return molecule.getName();
    }
    @Override
    public String toString() {
        return molecule.getName();
    }
    // Only need this because we extend Reaction class.  Does nothing here.
    @Override
    public void setName(String name) { }

    @Historical
    @Override
    public String writeReaction() {
        throw new UnsupportedOperationException("This operation is implemented elsewhere for the vcell version of springsalad");
    }
    @Override
    public void loadReaction(SsldModel model, Scanner sc) {
        sc.next();      // Skip kcreate
        kcreate = sc.nextDouble();
        sc.next();
        kdecay = sc.nextDouble();
        sc.close();
    }

    public static void loadReactions(SsldModel model, Scanner dataScanner) {
        while(dataScanner.hasNextLine()) {
            String []  nextLine = dataScanner.nextLine().split(":");
            Molecule molecule = model.getMolecule(IOHelp.getNameInQuotes(new Scanner(nextLine[0])));
            DecayReaction reaction = molecule.getDecayReaction();
            reaction.loadReaction(model, new Scanner(nextLine[1].trim()));
        }
        dataScanner.close();
    }}
