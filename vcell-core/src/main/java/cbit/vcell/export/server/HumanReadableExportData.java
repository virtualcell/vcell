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
    public HumanReadableExportData(String simulationName, String applicationName, String biomodelName,
                                   ArrayList<String> defaultParameterValues, ArrayList<String> setParameterValues){
        this.simulationName = simulationName;
        this.applicationName = applicationName;
        this.biomodelName = biomodelName;
        this.defaultParameterValues = defaultParameterValues;
        this.setParameterValues = setParameterValues;
    }

    public ExportHumanReadableDataSpec toJsonRep() {
        return new ExportHumanReadableDataSpec(biomodelName, applicationName, simulationName, defaultParameterValues, setParameterValues);
    }
    public static HumanReadableExportData fromJsonRep(ExportHumanReadableDataSpec rep) {
        return new HumanReadableExportData(rep.simulationName, rep.applicationName, rep.bioModelName, rep.defaultParameterValues, rep.setParameterValues);
    }
}
