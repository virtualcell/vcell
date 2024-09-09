package org.vcell.model.ssld;

import java.io.*;
import java.util.Scanner;

import org.vcell.util.springsalad.IOHelp;

public class StateCounter {

    private final State state;

    public static final String FREE = "Free";
    public static final String BOUND = "Bound";
    public static final String TOTAL = "Total";
    public static final String NONE = "None";

    private boolean countFree = true;
    private boolean countBound = true;
    private boolean countTotal = true;

    public StateCounter(State state){
        this.state = state;
    }

    public void setMeasurement(String type, boolean bool) {
        switch(type) {
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

    public boolean countTotal(){
        return countTotal;
    }
    public boolean countFree(){
        return countFree;
    }
    public boolean countBound(){
        return countBound;
    }
    public String getStateName(){
        return state.getName();
    }

    public void loadCounter(Scanner sc) {
        sc.next();
        while(sc.hasNext()) {
            this.setMeasurement(sc.next(), true);
        }
    }

    public static void loadCounters(SsldModel model, Scanner dataScanner) {
        while(dataScanner.hasNextLine()){
            Scanner sc = new Scanner(dataScanner.nextLine());
            Molecule mol = model.getMolecule(IOHelp.getNameInQuotes(sc));
            sc.next();
            SiteType type = mol.getType(IOHelp.getNameInQuotes(sc));
            sc.next();
            State state = type.getState(IOHelp.getNameInQuotes(sc));
            sc.next();
            state.getStateCounter().loadCounter(sc);
            sc.close();
        }
        dataScanner.close();
    }

}
