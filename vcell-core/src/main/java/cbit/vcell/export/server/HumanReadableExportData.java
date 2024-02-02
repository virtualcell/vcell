package cbit.vcell.export.server;

import org.vcell.api.common.events.ExportHumanReadableDataSpec;

import java.io.Serializable;
import java.util.ArrayList;

public class HumanReadableExportData implements Serializable {
    public final String simulationName;
    public final String biomodelName;
    public final String applicationName;
    public ArrayList<String> defaultParameterValues;
    public ArrayList<String> setParameterValues;
    // File name that is saved by the user or server. In N5 case it'll be the dataset name. This way individual datasets can be automatically opened
    public String serverSavedFileName;
    public HumanReadableExportData(String simulationName, String applicationName, String biomodelName,
                                   ArrayList<String> defaultParameterValues, ArrayList<String> setParameterValues,
                                   String serverSavedFileName){
        this.simulationName = simulationName;
        this.applicationName = applicationName;
        this.biomodelName = biomodelName;
        this.defaultParameterValues = defaultParameterValues;
        this.setParameterValues = setParameterValues;
        this.serverSavedFileName = serverSavedFileName;
    }

    public ExportHumanReadableDataSpec toJsonRep() {
        return new ExportHumanReadableDataSpec(biomodelName, applicationName, simulationName, defaultParameterValues, setParameterValues, serverSavedFileName);
    }
    public static HumanReadableExportData fromJsonRep(ExportHumanReadableDataSpec rep) {
        return new HumanReadableExportData(rep.simulationName, rep.applicationName, rep.bioModelName, rep.defaultParameterValues, rep.setParameterValues, rep.serverSavedFileName);
    }
}
