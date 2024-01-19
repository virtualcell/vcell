package cbit.vcell.client.data;

import java.util.ArrayList;
import java.util.HashMap;

public class ExportDataRepresentation {
    public ArrayList<String> globalJobIDs;
    public HashMap<String, FormatExportDataRepresentation> formatData;

    public ExportDataRepresentation(ArrayList<String> globalJobIDs, HashMap<String, FormatExportDataRepresentation> formatData){
        this.globalJobIDs = globalJobIDs;
        this.formatData = formatData;
    }

    public static class FormatExportDataRepresentation {
        public HashMap<String, SimulationExportDataRepresentation> simulationDataMap;
        public ArrayList<String> formatJobIDs;

        public FormatExportDataRepresentation(HashMap<String, SimulationExportDataRepresentation> simulationDataMap, ArrayList<String> formatJobIDs){
            this.formatJobIDs = formatJobIDs;
            this.simulationDataMap = simulationDataMap;
        }
    }

    public static class SimulationExportDataRepresentation {
        public String exportDate;
        public String uri;
        public String jobID;
        public String dataID;
        public String simulationName;
        public String applicationName;
        public String biomodelName;
        public String variables;
        public String startAndEndTime;

        public ArrayList<String> defaultParameterValues;
        public ArrayList<String> setParameterValues;

        public SimulationExportDataRepresentation(String exportDate, String uri, String jobID, String dataID, String simulationName,
                                                  String applicationName, String biomodelName, String variables, String startAndEndTime,
                                                  ArrayList<String> defaultParameterValues, ArrayList<String> setParameterValues){
            this.exportDate = exportDate;
            this.uri = uri;
            this.jobID = jobID;
            this.dataID = dataID;
            this.simulationName = simulationName;
            this.applicationName = applicationName;
            this.biomodelName = biomodelName;
            this.variables = variables;
            this.startAndEndTime = startAndEndTime;
            this.defaultParameterValues = defaultParameterValues;
            this.setParameterValues = setParameterValues;
        }
    }

}
