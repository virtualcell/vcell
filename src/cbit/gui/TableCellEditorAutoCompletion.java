package cbit.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.vcell.util.gui.DialogUtils;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;

@SuppressWarnings("serial")
public class TableCellEditorAutoCompletion extends DefaultCellEditor {
	private TextFieldAutoCompletion textFieldAutoCompletion = null;
	private JTable thisTable = null;
	private boolean bValidateBinding = false;
	
	public TableCellEditorAutoCompletion(JTable table, boolean arg_bValidateBinding) {		
		super(new TextFieldAutoCompletion());
		textFieldAutoCompletion = (TextFieldAutoCompletion)getComponent();
		thisTable = table;	
		bValidateBinding = arg_bValidateBinding;
	}
	
	@Override
	public boolean stopCellEditing() {
		if (thisTable.getCellEditor() == null) {
			return true;
		}
		if (textFieldAutoCompletion.getSelectedIndex() >= 0) {
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
					if (bValidateBinding) {
						exp.bindExpression(textFieldAutoCompletion.getSymbolTable());
					}
				} catch (ExpressionBindingException ex) {
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(thisTable.getParent(), ex.getMessage() + "\n\nUse 'Ctrl-Space' to see a list of available names in your model or 'Esc' to revert to the original expression.");
					bOK = false;
				} catch (ExpressionException ex) {
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(thisTable.getParent(), ex.getMessage() + "\n\nUse 'Esc' to revert to the original expression.");
					bOK = false;
				}
			}
		}
		if (!bOK) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					thisTable.requestFocus();
					thisTable.setRowSelectionInterval(row, row);
					((JComponent)getComponent()).setBorder(new LineBorder(Color.red));
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
			if (scopedExpression.getNameScope() != null) {
				textFieldAutoCompletion.setSymbolTable(scopedExpression.getNameScope().getScopedSymbolTable());
				textFieldAutoCompletion.setAutoCompleteSymbolFilter(scopedExpression.getAutoCompleteSymbolFilter());
			}
		}
		((JComponent)getComponent()).setBorder(null);
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

	@Override
	public void cancelCellEditing() {
		if (textFieldAutoCompletion.getSelectedIndex() >= 0) {
			return;
		} 
		super.cancelCellEditing();
	 }	 
}
