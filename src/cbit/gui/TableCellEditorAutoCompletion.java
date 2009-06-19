package cbit.gui;

import java.awt.Component;
import java.util.Set;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import org.vcell.util.gui.DialogUtils;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ScopedExpression;

public class TableCellEditorAutoCompletion extends DefaultCellEditor {
	private TextFieldAutoCompletion textFieldAutoCompletion = null;
	private JTable thisTable = null;
	
	public TableCellEditorAutoCompletion(JTable table) {		
		super(new TextFieldAutoCompletion());
		textFieldAutoCompletion = (TextFieldAutoCompletion)getComponent();
		thisTable = table;	
		thisTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	}
	
	public boolean stopCellEditing() {
		if (thisTable.getCellEditor() == null) {
			return true;
		}
		if (textFieldAutoCompletion.isPopupVisible()) {
			return false;
		}
		final int row = thisTable.getSelectedRow();
		textFieldAutoCompletion.stopEditing();
		boolean bOK = true;
		if (textFieldAutoCompletion.getSymbolTable() != null) {				
			String text = textFieldAutoCompletion.getText();
			if (text.trim().length() > 0) {
				try {
					Expression exp = new Expression(text);
					exp.bindExpression(textFieldAutoCompletion.getSymbolTable());
				} catch (ExpressionBindingException ex) {
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(thisTable.getParent(), ex.getMessage() + "\n\nUse Ctrl-Space to see a list of available names in your model.");
					bOK = false;		
				} catch (ExpressionException ex) {
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(thisTable.getParent(), ex.getMessage());
					bOK = false;
				}
			}
		}
		if (!bOK) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					thisTable.requestFocus();
					thisTable.setRowSelectionInterval(row, row);					
					textFieldAutoCompletion.requestFocus();										
				}				
			});
			return false;
		}
		return super.stopCellEditing();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (value instanceof ScopedExpression) {
			ScopedExpression scopedExpression = (ScopedExpression)value;
			textFieldAutoCompletion.setSymbolTable(scopedExpression.getNameScope().getScopedSymbolTable());
			textFieldAutoCompletion.setSymbolTableEntryFilter(scopedExpression.getSymbolTableEntryFilter());
		}
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

	 public void cancelCellEditing() {
		if (textFieldAutoCompletion.isPopupVisible()) {			
			return;
		} 
		super.cancelCellEditing();
	 }	 
}
