package org.vcell.model.ssld;

import org.vcell.util.springsalad.IOHelp;

import java.io.*;
import java.util.Scanner;

public class SitePropertyCounter {

    /* ********  The site whose properties we're measuring  *********/
    private final Site site;

    private boolean trackData;

    public SitePropertyCounter(Site site){
        this.site = site;
        trackData = true;
    }

    /* ********  Set and get trackData *************/

    public boolean isTracked(){
        return trackData;
    }

    public void setTracked(boolean bool){
        trackData = bool;
    }

    // Since there is only a single data field to read in, it doesn't make
    // sense to define a method to load a single counter.
    public static void loadCounters(SsldModel model, Scanner dataScanner){
        while(dataScanner.hasNextLine()){
            Scanner sc = new Scanner(dataScanner.nextLine());
            Molecule molecule = model.getMolecule(IOHelp.getNameInQuotes(sc));
            // Skip "Site"
            sc.next();
            int index = sc.nextInt();
            Site mSite = molecule.getSite(index);
            SitePropertyCounter propertyCounter = mSite.getPropertyCounter();
            // Skip ":"
            sc.next();
            // Skip "Track"
            sc.next();
            // Skip "Properties"
            sc.next();
            // Read in the boolean
            propertyCounter.setTracked(sc.nextBoolean());
            sc.close();
        }
        dataScanner.close();
    }

}
