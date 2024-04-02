package org.vcell.api.common.events;

import java.util.ArrayList;
import java.util.HashMap;

public class ExportHumanReadableDataSpec {
    public final String bioModelName;
    public String applicationName;
    public String simulationName;
    public ArrayList<String> differentParameterValues;
    public String serverSavedFileName;
    public String applicationType;
    public boolean nonSpatial;
    public HashMap<Integer, String> subVolume;

    public ExportHumanReadableDataSpec(String bioModelName, String applicationName, String simulationName,
                                       ArrayList<String> differentParameterValues,
                                       String serverSavedFileName, String applicationType, boolean nonSpatial, HashMap<Integer, String> subVolume){
        this.bioModelName = bioModelName;
        this.applicationName = applicationName;
        this.simulationName = simulationName;
        this.differentParameterValues = differentParameterValues;
        this.serverSavedFileName = serverSavedFileName;
        this.applicationType = applicationType;
        this.nonSpatial = nonSpatial;
        this.subVolume = subVolume;
    }


}
