package cbit.vcell.client.data;

import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import org.vcell.util.gui.ScrollTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExportedDataTableModel extends VCellSortTableModel<ExportedDataTableModel.TableData>{

    public static final ArrayList<String> header = new ArrayList<>(){{
        add("BM Name");
        add("App Name");
        add("Sim Name");
        add("Time Slice");
        add("Variables");
        add("Date Exported");
        add("Format");
    }};

    public List<TableData> tableData = new ArrayList<>();

    public ExportedDataTableModel(ScrollTable scrollTable){
        super(scrollTable, header.toArray(new String[0]));
    }

    @Override
    protected Comparator<TableData> getComparator(int col, boolean ascending) {
        int ascendingMask = ascending ? 1: -1;
        return new Comparator<TableData>() {
            @Override
            public int compare(TableData o1, TableData o2) {
                switch (col){
                    default:
                        return ascendingMask * o1.dateExported.compareTo(o2.dateExported);

                }
            }
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TableData data = getValueAt(rowIndex);
        if (columnIndex == header.indexOf("App Name")){
            return data.appName;
        } else if (columnIndex == header.indexOf("BM Name")) {
            return data.bioModelName;
        } else if (columnIndex == header.indexOf("Sim Name")) {
            return data.simName;
        } else if (columnIndex == header.indexOf("Time Slice")) {
            return  data.timeSlice;
        } else if (columnIndex == header.indexOf("Variables")) {
            return data.variables;
        } else if (columnIndex == header.indexOf("Date Exported")) {
            return data.dateExported;
        } else if (columnIndex == header.indexOf("Format")) {
            return data.format;
        }
        return null;
    }

    public TableData getRowData(int rowIndex){
        return tableData.get(rowIndex);
    }


    public void addRow(TableData row){
        tableData.add(row);
    }
    public void refreshData(){
        setData(tableData);
    }

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
        public String bioModelName = null;
        public String timeSlice = null;
        public String appName = null;
        public String simName = null;
        public String variables = null;

        public TableData(String jobID, String simID, String dateExported, String format, String link,
                         String bioModelName, String timeSlice, String appName, String simName, String variables){
            this.jobID = jobID;
            this.simID = simID;
            this.dateExported = dateExported;
            this.format = format;
            this.link = link;
            this.bioModelName = bioModelName;
            this.timeSlice = timeSlice;
            this.appName = appName;
            this.simName = simName;
            this.variables = variables;
        }
    }

}
