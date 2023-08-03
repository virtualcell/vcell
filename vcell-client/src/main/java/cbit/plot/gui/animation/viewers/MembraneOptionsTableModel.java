
package cbit.plot.gui.animation.viewers;

import javax.swing.table.AbstractTableModel;

public class MembraneOptionsTableModel extends AbstractTableModel {
    
    private final String [] columnNames = {"Surface", "Color", "Shininess (1 - 128)"};
    private final String [] rowNames = {"Intracellular", "Extracellular"};
    
    private final Membrane membrane;
    
    public MembraneOptionsTableModel(Membrane membrane){
        this.membrane = membrane;
    }
    
    /* *****************  TABLE METHODS **********************************/
    
    @Override
    public int getRowCount() {
        return 2;
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
        switch(col){
            case 0:
                return rowNames[row];
            case 1:
                if(row == 0){
                    return membrane.getInnerColorName();
                } else {
                    return membrane.getOuterColorName();
                }
            case 2:
                if(row == 0){
                    return membrane.getInnerShininess();
                } else {
                    return membrane.getOuterShininess();
                }
            default:
                return "Index out of bounds";
        }
    }
    
    @Override
    public Class getColumnClass(int c){
        if(c<2){
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
        if(col == 1){
            if(row == 0){
                membrane.setInnerColor((String)value);
            } else {
                membrane.setOuterColor((String)value);
            }
        } else if(col == 2){
            if(row == 0){
                membrane.setInnerShininess((Float)value);
            } else {
                membrane.setOuterShininess((Float)value);
            }
        }
    }
    
    
}
