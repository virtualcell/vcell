package cbit.rmi.event.client.events;

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
    public int zSlices;
    public int tSlices;
    public int numChannels;

    public ExportHumanReadableDataSpec(String bioModelName, String applicationName, String simulationName,
                                       ArrayList<String> differentParameterValues,
                                       String serverSavedFileName, String applicationType, boolean nonSpatial, HashMap<Integer, String> subVolume,
                                       int zSlices, int tSlices, int numChannels){
        this.bioModelName = bioModelName;
        this.applicationName = applicationName;
        this.simulationName = simulationName;
        this.differentParameterValues = differentParameterValues;
        this.serverSavedFileName = serverSavedFileName;
        this.applicationType = applicationType;
        this.nonSpatial = nonSpatial;
        this.subVolume = subVolume;
        this.zSlices = zSlices;
        this.tSlices = tSlices;
        this.numChannels = numChannels;
    }


}
