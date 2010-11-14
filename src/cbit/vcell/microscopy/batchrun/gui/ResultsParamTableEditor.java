package cbit.vcell.microscopy.batchrun.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import org.vcell.util.DescriptiveStatistics;
import org.vcell.util.gui.HyperLinkLabel;

import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;

public class ResultsParamTableEditor extends AbstractCellEditor implements TableCellEditor, ActionListener

{
	private JButton button = new JButton("Details...");
	private JTable table;
	private PropertyChangeSupport propertyChangeSupport;
	
	public ResultsParamTableEditor(JTable table) {
		super();
		this.table = table;
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
		if(column == BatchRunResultsParamTableModel.COLUMN_DETAILS)
		{
			Object firstColStr = table.getValueAt(row, BatchRunResultsParamTableModel.COLUMN_FILE_NAME); 
			if((firstColStr instanceof String) && (firstColStr.equals(DescriptiveStatistics.MEAN_NAME)||
			   firstColStr.equals(DescriptiveStatistics.MEDIAN_NAME)||
			   firstColStr.equals(DescriptiveStatistics.MODE_NAME)||
			   firstColStr.equals(DescriptiveStatistics.MIN_NAME)||
			   firstColStr.equals(DescriptiveStatistics.MAX_NAME)||
			   firstColStr.equals(DescriptiveStatistics.STANDARD_DEVIATION_NAME)))
			{
				return null;
			}
			else
			{
				button.setBorderPainted(false);
				if (isSelected) {
			      button.setForeground(table.getSelectionForeground());
			      button.setBackground(table.getSelectionBackground());
			    } else {
			      button.setForeground(table.getForeground());
			      button.setBackground(table.getBackground());
			    }
				return button;
			}
			
		}
		
			
//	    label = (value == null) ? "" : value.toString();
//	    button.setText(label);
//	    isPushed = true;
	    return button;
	}

	public Object getCellEditorValue() 
	{
//		if (isPushed) 
//		{
//			System.out.println("In getCellEditorValue");
//	    }
//	    isPushed = false;
	    return "Details";
	}

	public void actionPerformed(ActionEvent e) 
	{
		fireEditingStopped();
		int oldSelectedRow = -1;
		firePropertyChange(FRAPBatchRunWorkspace.PROPERTY_CHANGE_BATCHRUN_DETAILS, oldSelectedRow, table.getSelectedRow());
	}
	public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    public boolean stopCellEditing() {
//    	isPushed = false;
        return super.stopCellEditing();
    }

    public void cancelCellEditing() {
        super.cancelCellEditing();
    }


}
