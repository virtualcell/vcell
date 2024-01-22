package org.vcell.api.common.events;

import java.util.ArrayList;

public class ExportHumanReadableDataSpec {
    public final String bioModelName;
    public String applicationName;
    public String simulationName;
    public ArrayList<String> defaultParameterValues;
    public ArrayList<String> setParameterValues;
    public String serverSavedFileName;

    public ExportHumanReadableDataSpec(String bioModelName, String applicationName, String simulationName,
                                       ArrayList<String> defaultParameterValues, ArrayList<String> setParameterValues,
                                       String serverSavedFileName){
        this.bioModelName = bioModelName;
        this.applicationName = applicationName;
        this.simulationName = simulationName;
        this.defaultParameterValues = defaultParameterValues;
        this.setParameterValues = setParameterValues;
        this.serverSavedFileName = serverSavedFileName;
    }


}
