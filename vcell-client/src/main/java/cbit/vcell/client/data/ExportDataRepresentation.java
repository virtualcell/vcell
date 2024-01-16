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

}
