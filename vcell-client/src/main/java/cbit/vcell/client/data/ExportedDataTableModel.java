package cbit.vcell.client.data;

import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import org.vcell.util.gui.ScrollTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExportedDataTableModel extends VCellSortTableModel<ExportedDataTableModel.ExportMetaData>{

    public static final int iColJobID = 1;
    public static final int iColSimID = 0;
    public static final int iColDateExported = 2;
    public static final int iColFormat = 3;
    public static final int iColLink = 4;

    public static final String[] header = new String[] {"Sim ID", "Job ID", "Date Exported", "Format", "Link"};

    public List<ExportMetaData> exportMetaData = new ArrayList<>();

    public ExportedDataTableModel(ScrollTable scrollTable){
        super(scrollTable, header);
    }

    @Override
    protected Comparator<ExportMetaData> getComparator(int col, boolean ascending) {
        int ascendingMask = ascending ? 1: -1;
        return new Comparator<ExportMetaData>() {
            @Override
            public int compare(ExportMetaData o1, ExportMetaData o2) {
                switch (col){
                    default:
                        return ascendingMask * o1.dateExported.compareTo(o2.dateExported);

                }
            }
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ExportMetaData data = getValueAt(rowIndex);
        switch (columnIndex){
            case iColJobID:
                return data.jobID;
            case iColSimID:
                return data.simID;
            case iColDateExported:
                return data.dateExported;
            case iColFormat:
                return data.format;
            case iColLink:
                return data.link;
            default:
                return null;
        }
    }


    public void addRow(ExportMetaData row){
        exportMetaData.add(row);
    }
    public void refreshData(){
        setData(exportMetaData);
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
    public static class ExportMetaData {
        public String jobID = null;
        public String simID = null;
        public String dateExported = null;
        public String format = null;
        public String link = null;

        public ExportMetaData(String jobID, String simID, String dateExported, String format, String link){
            this.jobID = jobID;
            this.simID = simID;
            this.dateExported = dateExported;
            this.format = format;
            this.link = link;
        }
    }

}
