package cbit.vcell.client.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class ExportDataRepresentation {
    public Stack<String> globalJobIDs;
    public HashMap<String, FormatExportDataRepresentation> formatData;

    public ExportDataRepresentation(Stack<String> globalJobIDs, HashMap<String, FormatExportDataRepresentation> formatData){
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

        public ArrayList<String> differentParameterValues;
        public String savedFileName;
        public String applicationType;
        public boolean nonSpatial;
        public int zSlices;
        public int tSlices;
        public int numVariables;

        public SimulationExportDataRepresentation(String exportDate, String uri, String jobID, String dataID, String simulationName,
                                                  String applicationName, String biomodelName, String variables, String startAndEndTime,
                                                  ArrayList<String> differentParameterValues,
                                                  String savedFileName, String applicationType, boolean nonSpatial, int zSlices, int tSlices,
                                                  int numVariables){
            this.exportDate = exportDate;
            this.uri = uri;
            this.jobID = jobID;
            this.dataID = dataID;
            this.simulationName = simulationName;
            this.applicationName = applicationName;
            this.biomodelName = biomodelName;
            this.variables = variables;
            this.startAndEndTime = startAndEndTime;
            this.differentParameterValues = differentParameterValues;
            this.savedFileName = savedFileName;
            this.applicationType = applicationType;
            this.nonSpatial = nonSpatial;
            this.zSlices = zSlices;
            this.tSlices = tSlices;
            this.numVariables = numVariables;
        }
    }

}
