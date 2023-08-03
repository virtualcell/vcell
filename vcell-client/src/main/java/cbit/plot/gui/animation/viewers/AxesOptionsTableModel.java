
package cbit.plot.gui.animation.viewers;

import javax.swing.table.AbstractTableModel;

public class AxesOptionsTableModel extends AbstractTableModel {
    
    private final String [] columnNames = {"Axis", "Length (nm)", "Tick Spacing (nm)",
        "Tick Size (nm)", "Axis Thickness (nm)", "Color"};
    
    private final String [] axisNames = {"x-axis", "y-axis", "z-axis"};
    
    private final Axes axes;
    
    public AxesOptionsTableModel(Axes axes){
        this.axes = axes;
    }
    
    /* *****************  TABLE METHODS **********************************/
    
    @Override
    public int getRowCount() {
        return 3;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int col){
        return columnNames[col];
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        Axis axis = axes.getAxis(row);
        switch(col){
            case 0:
                return axisNames[row];
            case 1:
                return axis.getLength();
            case 2:
                return axis.getTickSpacing();
            case 3:
                return axis.getTickMarkSize();
            case 4:
                return axis.getThickness();
            case 5:
                return axis.getColorName();
            default:
                return "Index out of bounds.";
        }
    }
    
    @Override
    public Class getColumnClass(int c){
        if(c == 0 || c== 5){
            return String.class;
        } else {
            return Float.class;
        }
    }
    
    // Nothing is editable
    @Override
    public boolean isCellEditable(int row, int col){
        return col != 0;
    }
    
    @Override
    public void setValueAt(Object value, int row, int col){
        Axis axis = axes.getAxis(row);
        switch(col){
            case 1:
                axis.setLength((Float)value);
                break;
            case 2:
                axis.setTickSpacing((Float) value);
                break;
            case 3:
                axis.setTickMarkSize((Float) value);
                break;
            case 4:
                axis.setThickness((Float)value);
                break;
            case 5:
                axis.setColorByName((String)value);
                break;
            default:
                System.out.println("Unexpected column index in "
                        + "AxesOptionsTableModel.setValueAt() . ");
        }
    }
    
}
