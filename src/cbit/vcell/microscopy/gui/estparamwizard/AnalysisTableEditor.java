package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

import cbit.vcell.microscopy.FRAPSingleWorkspace;


@SuppressWarnings("serial")
public class AnalysisTableEditor extends AbstractCellEditor implements TableCellEditor, ActionListener
{
	private JButton button = null;
	private JTable table;
	private PropertyChangeSupport propertyChangeSupport;
	
	public AnalysisTableEditor(JTable table) {
		super();
		this.table = table;
		button = new JButton("Plot...");
		button.setVerticalTextPosition(SwingConstants.CENTER); 
		button.setHorizontalTextPosition(SwingConstants.LEFT);
	    button.addActionListener(this);
	    propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener p) {
    	propertyChangeSupport.addPropertyChangeListener(p);
    }
  
    public void removePropertyChangeListener(PropertyChangeListener p) {
    	propertyChangeSupport.removePropertyChangeListener(p);
    }
    
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    	propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
	
	public Component getTableCellEditorComponent(JTable table, Object value,
	                 boolean isSelected, int row, int column) 
	{
		if(column == AnalysisTableModel.COLUMN_DIFF_ONE_CI_PLOT || column == AnalysisTableModel.COLUMN_DIFF_TWO_CI_PLOT)
		{
			return button;
		}
	    return null;
	}

	public Object getCellEditorValue() 
	{
	    return button.toString();
	}

	public void actionPerformed(ActionEvent e) 
	{
		fireEditingStopped();
		int oldSelectedCol = -1;
		firePropertyChange(FRAPSingleWorkspace.PROPERTY_CHANGE_CONFIDENCEINTERVAL_DETAILS, oldSelectedCol, table.getSelectedColumn());
//		System.out.println("table col selected:" + table.getSelectedColumn() + "    table row selected:" + table.getSelectedRow());
	}
	public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    public boolean stopCellEditing() {
        return super.stopCellEditing();
    }

    public void cancelCellEditing() {
        super.cancelCellEditing();
    }
}
