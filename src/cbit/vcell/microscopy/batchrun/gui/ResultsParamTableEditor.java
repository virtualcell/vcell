package cbit.vcell.microscopy.batchrun.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import cbit.vcell.microscopy.gui.estparamwizard.HyperLinkLabel;

public class ResultsParamTableEditor extends AbstractCellEditor implements TableCellEditor, ActionListener

{
	private JButton button = new JButton("Details...");
	private JTable table;
	
	public ResultsParamTableEditor(JTable table) {
		super();
		this.table = table;
	    button.addActionListener(this);
	}
	 
	public Component getTableCellEditorComponent(JTable table, Object value,
	                 boolean isSelected, int row, int column) 
	{
		if (isSelected) {
	      button.setForeground(table.getSelectionForeground());
	      button.setBackground(table.getSelectionBackground());
	    } else {
	      button.setForeground(table.getForeground());
	      button.setBackground(table.getBackground());
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
		
		System.out.println("Action preformed in RestultsParamTableEditor");
		System.out.println("row:" + table.getSelectedRow() + "     col:" +table.getSelectedColumn());
		fireEditingStopped();
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
