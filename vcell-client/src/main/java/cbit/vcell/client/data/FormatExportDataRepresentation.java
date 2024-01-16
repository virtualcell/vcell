package cbit.vcell.client.data;

import java.util.ArrayList;
import java.util.HashMap;

public class FormatExportDataRepresentation {
    public HashMap<String, SimlationExportDataRepresentation> simulationDataMap;
    public ArrayList<String> formatJobIDs;

    public FormatExportDataRepresentation(HashMap<String, SimlationExportDataRepresentation> simulationDataMap, ArrayList<String> formatJobIDs){
        this.formatJobIDs = formatJobIDs;
        this.simulationDataMap = simulationDataMap;
    }
}
