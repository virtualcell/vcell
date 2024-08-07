package cbit.vcell.client.data;

import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import org.vcell.util.gui.ScrollTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ParameterTableModelExport extends VCellSortTableModel<ParameterTableModelExport.ParameterTableData> {
    public static final String parameterName = "Parameter";
    public static final String defaultValue = "Default Value";
    public static final String setValue = "New Value";

    public static final ArrayList<String> header = new ArrayList<>(){{
        add(parameterName);
        add(defaultValue);
        add(setValue);
    }};

    private List<ParameterTableData> tableData = new ArrayList<>();
    public ParameterTableModelExport(ScrollTable scrollTable){super(scrollTable, header.toArray(new String[0]));}

    @Override
    protected Comparator getComparator(int col, boolean ascending) {
        int ascendingMask = ascending ? 1 : -1;
        return (o1, o2) -> {
            if (o1 instanceof ParameterTableData && o2 instanceof ParameterTableData){
                if (col == header.indexOf(parameterName)){
                    return ascendingMask * ((ParameterTableData) o1).parameterName.compareTo(((ParameterTableData) o2).parameterName);
                } else if (col == header.indexOf(defaultValue)) {
                    return ascendingMask * ((ParameterTableData) o1).defaultValue.compareTo(((ParameterTableData) o2).defaultValue);
                } else if (col == header.indexOf(setValue)) {
                    return ascendingMask * ((ParameterTableData) o1).setValue.compareTo(((ParameterTableData) o2).setValue);
                }
            }
            return 0;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ParameterTableData data = tableData.get(rowIndex);
        if (columnIndex == header.indexOf(parameterName)){
            return data.parameterName;
        } else if (columnIndex == header.indexOf(defaultValue)) {
            return data.defaultValue;
        } else if (columnIndex == header.indexOf(setValue)) {
            return data.setValue;
        }
        return null;
    }

    public void addRow(ParameterTableData row){tableData.add(row);}
    public void refreshData(){setData(tableData);}
    public void resetData(){tableData = new ArrayList<>();}
    public record ParameterTableData(
            String parameterName,
            String defaultValue,
            String setValue
    ){}
}
