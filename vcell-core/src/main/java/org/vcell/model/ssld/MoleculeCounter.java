package org.vcell.model.ssld;

import java.io.*;
import java.util.Scanner;

import org.vcell.util.springsalad.IOHelp;

public class MoleculeCounter {

    Molecule molecule;

    public static final String FREE = "Free";
    public static final String BOUND = "Bound";
    public static final String TOTAL = "Total";
    public static final String NONE = "None";

    private boolean countFree = true;
    private boolean countBound = true;
    private boolean countTotal = true;

    public MoleculeCounter(Molecule molecule) {
        this.molecule = molecule;
    }

    public void setMeasurement(String type, boolean bool){
        switch(type){
            case FREE:
                countFree = bool;
                break;
            case BOUND:
                countBound = bool;
                break;
            case TOTAL:
                countTotal = bool;
                break;
            case NONE:
                countFree = false;
                countBound = false;
                countTotal = false;
                break;
            default:
                System.out.println("CountData setMeasurement() received the following unexpected input: " + type);
        }
    }

    public boolean countTotal() {
        return countTotal;
    }
    public boolean countFree() {
        return countFree;
    }
    public boolean countBound() {
        return countBound;
    }

    public String getMoleculeName() {
        return molecule.getName();
    }

    public void loadCounter(Scanner dataScanner){
        // Skip the word "measure"
        dataScanner.next();
        while(dataScanner.hasNext()){
            this.setMeasurement(dataScanner.next(), true);
        }
        dataScanner.close();
    }

    /* *********** LOAD ALL MOLECULE COUNTERS ********************/

    public static void loadCounters(SsldModel model, Scanner dataScanner){
        while(dataScanner.hasNextLine()){
            String [] next = dataScanner.nextLine().split(":");
            Molecule mol = model.getMolecule(IOHelp.getNameInQuotes(new Scanner(next[0])));
            MoleculeCounter counter = mol.getMoleculeCounter();
            counter.loadCounter(new Scanner(next[1].trim()));
        }
        dataScanner.close();
    }


}
