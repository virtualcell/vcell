package cbit.vcell.export.server;

import cbit.vcell.mapping.SimulationContext;
import org.vcell.api.common.events.ExportHumanReadableDataSpec;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class HumanReadableExportData implements Serializable {
    public final String simulationName;
    public final String biomodelName;
    public final String applicationName;
    public ArrayList<String> differentParameterValues;
    public String applicationType;
    // File name that is saved by the user or server. In N5 case it'll be the dataset name. This way individual datasets can be automatically opened
    public String serverSavedFileName;
    public boolean nonSpatial;
    public HashMap<Integer, String> subVolume;
    public HumanReadableExportData(String simulationName, String applicationName, String biomodelName,
                                   ArrayList<String> differentParameterValues,
                                   String serverSavedFileName, String applicationType, boolean nonSpatial, HashMap<Integer, String> subVolume){
        this.simulationName = simulationName;
        this.applicationName = applicationName;
        this.biomodelName = biomodelName;
        this.differentParameterValues = differentParameterValues;
        this.serverSavedFileName = serverSavedFileName;
        this.applicationType = applicationType;
        this.nonSpatial = nonSpatial;
        this.subVolume = subVolume;
    }

    public ExportHumanReadableDataSpec toJsonRep() {
        return new ExportHumanReadableDataSpec(biomodelName, applicationName, simulationName, differentParameterValues, serverSavedFileName, applicationType, nonSpatial, subVolume);
    }
    public static HumanReadableExportData fromJsonRep(ExportHumanReadableDataSpec rep) {
        return new HumanReadableExportData(rep.simulationName, rep.applicationName, rep.bioModelName, rep.differentParameterValues,
                rep.serverSavedFileName, rep.applicationType, rep.nonSpatial, rep.subVolume);
    }
}
