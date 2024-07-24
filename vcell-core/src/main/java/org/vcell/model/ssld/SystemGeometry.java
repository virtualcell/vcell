package org.vcell.model.ssld;

public class SystemGeometry {

    // Flags to indicate location relative to the membrane
    public static final String INSIDE = "Intracellular";
    public static final String OUTSIDE = "Extracellular";
    public static final String MEMBRANE = "Membrane";

    // Get all flags
    public static String [] getLocations(){
        return new String[]{INSIDE, MEMBRANE, OUTSIDE};
    }

}
