package org.vcell.model.ssld;

import java.io.*;
import java.util.Scanner;

import org.vcell.util.springsalad.IOHelp;

public class BondCounter {

    private final BindingReaction reaction;
    private boolean counted;

    public static String COUNTED = "Counted";
    public static String NOT_COUNTED = "Not_Counted";

    public BondCounter(BindingReaction reaction){
        this.reaction = reaction;
        counted = true;
    }

    public String getReactionName(){
        return reaction.getName();
    }

    public boolean isCounted(){
        return counted;
    }

    public void setCounted(boolean bool){
        counted = bool;
    }

     /* ***************  LOAD ALL COUNTERS *****************/
    // Since there is only a single data field to read in, it doesn't make
    // sense to define a method to load a single counter.
    public static void loadCounters(SsldModel model, Scanner dataScanner) {
        while(dataScanner.hasNextLine()){
            String [] next = dataScanner.nextLine().split(":");
            BindingReaction reaction = model.getBindingReaction(IOHelp.getNameInQuotes(new Scanner(next[0])));
            BondCounter counter = reaction.getBondCounter();
            counter.setCounted(next[1].trim().equals(COUNTED));
        }
        dataScanner.close();
    }

}
