package cbit.vcell.client.data;

import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.model.ModelProcess;
import org.vcell.util.gui.ScrollTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExportedDataTableModel extends VCellSortTableModel<ExportedDataTableModel.TableData>{

    public final static String prefix = "dataSetName=";

    public static final String bioModelCol = "BM Name";
    public static final String appNameCol = "App Name";
    public static final String simNameCol = "Sim Name";
    public static final String timeSliceCol = "Time Slice";
    public static final String variablesCol = "Variables";
    public static final String formatCol = "Format";
    public static final String defaultParametersCol = "Default Parameters";
    public static final String setParametersCol = "Set Parameters";
    public static final String dateExportedCol = "Date Exported";

    public static final ArrayList<String> header = new ArrayList<>(){{
        add(bioModelCol);
        add(appNameCol);
        add(simNameCol);
        add(timeSliceCol);
        add(formatCol);
        add(dateExportedCol);
    }};

    private List<TableData> tableData = new ArrayList<>();
    protected String searchText = null;

    public ExportedDataTableModel(ScrollTable scrollTable){
        super(scrollTable, header.toArray(new String[0]));
    }

    @Override
    protected Comparator<TableData> getComparator(int col, boolean ascending) {
        int ascendingMask = ascending ? 1: -1;
        return new Comparator<TableData>() {
            @Override
            public int compare(TableData o1, TableData o2) {
                if (col == header.indexOf(bioModelCol)){
                    return ascendingMask * o1.bioModelName.compareTo(o2.bioModelName);
                } else if (col == header.indexOf(appNameCol)){
                    return ascendingMask * o1.appName.compareTo(o2.appName);
                } else if (col == header.indexOf(formatCol)) {
                    return ascendingMask * o1.format.compareTo(o2.format);
                } else if (col == header.indexOf(simNameCol)) {
                    return ascendingMask * o1.simName.compareTo(o2.simName);
                } else if (col == header.indexOf(timeSliceCol)) {
                    String[] tokens1 = o1.timeSlice.split("/");
                    String[] tokens2 = o2.timeSlice.split("/");
                    double timeRange1 = Double.parseDouble(tokens1[1]) - Double.parseDouble(tokens1[0]);
                    double timeRange2 = Double.parseDouble(tokens2[1]) - Double.parseDouble(tokens2[0]);
                    return (int) (ascendingMask *  Math.round(timeRange1 - timeRange2));
                } else{
                    return ascendingMask * o1.dateExported.compareTo(o2.dateExported);
                }
            }
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TableData data = getValueAt(rowIndex);
        if (columnIndex == header.indexOf(appNameCol)){
            return data.appName;
        } else if (columnIndex == header.indexOf(bioModelCol)) {
            return data.bioModelName;
        } else if (columnIndex == header.indexOf(simNameCol)) {
            return data.simName;
        } else if (columnIndex == header.indexOf(timeSliceCol)) {
            return  data.timeSlice;
        } else if (columnIndex == header.indexOf(dateExportedCol)) {
            return data.dateExported;
        } else if (columnIndex == header.indexOf(formatCol)) {
            return data.format;
        }
        return null;
    }

    public void addRow(TableData row){
        tableData.add(row);
    }
    public void refreshData(){
        List<TableData> newData = computeData();
        setData(newData);
    }

    protected ArrayList<TableData> computeData() {
        ArrayList<TableData> newData = new ArrayList<>();
        if (searchText == null || searchText.length() == 0) {
            newData.addAll(tableData);
        } else {
            String lowerCaseSearchText = searchText.toLowerCase();
            for(TableData tableDataRow : tableData) {
                if(tableDataRow.variables.toLowerCase().contains(lowerCaseSearchText)) {
                    if(!newData.contains(tableDataRow)) {
                        newData.add(tableDataRow);
                        continue;
                    }
                }
                if(tableDataRow.bioModelName.toLowerCase().contains(lowerCaseSearchText)) {
                    if(!newData.contains(tableDataRow)) {
                        newData.add(tableDataRow);
                        continue;
                    }
                }
                if(tableDataRow.appName.toLowerCase().contains(lowerCaseSearchText)) {
                    if(!newData.contains(tableDataRow)) {
                        newData.add(tableDataRow);
                        continue;
                    }
                }
                if(tableDataRow.simName.toLowerCase().contains(lowerCaseSearchText)) {
                    if(!newData.contains(tableDataRow)) {
                        newData.add(tableDataRow);
                        continue;
                    }
                }
                if(tableDataRow.simID.toLowerCase().contains(lowerCaseSearchText)) {
                    if(!newData.contains(tableDataRow)) {
                        newData.add(tableDataRow);
                        continue;
                    }
                }

                if(tableDataRow.link != null && tableDataRow.link.toLowerCase().contains(lowerCaseSearchText)) {
                    if (!newData.contains(tableDataRow)) {
                        newData.add(tableDataRow);
//                      continue;
                    }
                }
                if(tableDataRow.name != null && tableDataRow.name.toLowerCase().contains(lowerCaseSearchText)) {
                    if(!newData.contains(tableDataRow)) {
                        newData.add(tableDataRow);
                        continue;
                    }
                }
                // TODO: we could also iterate through the list of parameters
            }
        }
        return newData;
    }
    public void setSearchText(String newValue) {
        if (newValue == searchText) { // takes care of both null
            return;
        }
        if (newValue == null && searchText.length() == 0
                || searchText == null && newValue.length() == 0) {
            return;
        }
        String oldValue = searchText;
        searchText = newValue;
    }


    public void resetData(){tableData = new ArrayList<>();}

    @Override
    public Class<?> getColumnClass(int iCol) {
        switch (iCol) {
            default:
                return String.class;
        }
    }

    /* An array would make more sense to me for the current use of this class, but it'll follow what was done previously for now. Dan stated a class is used
    * to glue different Object types together so it'll just do a class for now. */
    public static class TableData {
        public String jobID = null;
        public String simID = null;
        public String dateExported = null;
        public String format = null;
        public String link = null;
        public String name = null;      // user given name for the saved dataset
        public String bioModelName = null;
        public String timeSlice = null;
        public String appName = null;
        public String simName = null;
        public String variables = null;
        public ArrayList<String> differentParameterValues;
        public boolean nonSpatial;
        public String applicationType = null;

        public TableData(String jobID, String simID, String dateExported, String format, String link, String name,
                         String bioModelName, String timeSlice, String appName, String simName, String variables,
                         ArrayList<String> differentParameterValues, boolean nonSpatial, String applicationType){
            this.jobID = jobID;
            this.simID = simID;
            this.dateExported = dateExported;
            this.format = format;
            this.link = link;
            this.name = name;
            this.bioModelName = bioModelName;
            this.timeSlice = timeSlice;
            this.appName = appName;
            this.simName = simName;
            this.variables = variables;
            this.differentParameterValues = differentParameterValues;
            this.nonSpatial = nonSpatial;
            this.applicationType = applicationType;
        }
    }

}
