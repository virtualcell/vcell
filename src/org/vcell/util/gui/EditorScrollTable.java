package org.vcell.util.gui;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;

import cbit.gui.ScopedExpression;
import cbit.gui.TableCellEditorAutoCompletion;
import cbit.gui.TextFieldAutoCompletion;
import cbit.vcell.model.gui.ScopedExpressionTableCellRenderer;

/**
 * ScrollTable extends JTable and has a JScrollPane which encloses the table itself. The default cell renderer
 * is {@link DefaultScrollTableCellRenderer} which has special background for noneditable table cells. The cell render
 * for {@link ScopedExpression} is {@link ScopedExpressionTableCellRenderer} and the cell editor for {@link ScopedExpression}
 * is {@link TableCellEditorAutoCompletion}.
 * 
 * The default auto resize mode is AUTO_RESIZE_OFF because most of tables have expression columns. The expression 
 * columns will be formatted nicely automatically. You need to set auto resize mode if you don't want AUTO_RESIZE_OFF.
 * 
 * By default, when editing an expression, it will automatically try to bind expression to find errors. However, 
 * there are exceptions like in Kinetics Editor, new parameter will be added it is not already defined. Here you 
 * don't want to validate expression bindings. So you need to call ScrollTable.setValidateExpressionBinding(false).
 * 
 * To add ScrollTable to a JPanel, call ScrollTable.getEnclosingScrollPane() and add that to JPanel.
 * @author fgao
 *
 */
@SuppressWarnings("serial")
public class EditorScrollTable extends ScrollTable {
	
	protected TextFieldAutoCompletion textFieldCellEditor = null;
	protected class DefaultScrollTableCellEditor extends DefaultCellEditor {		public DefaultScrollTableCellEditor() {
			super(new TextFieldAutoCompletion());
			textFieldCellEditor = (TextFieldAutoCompletion)editorComponent;
			textFieldCellEditor.setBorder(DefaultScrollTableCellRenderer.DEFAULT_GAP);
			textFieldCellEditor.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					bEditingStoppedFromKey = true;
				}
			});
		}
	}
	
	protected class DefaultScrollTableAutoCompleteCellEditor extends TableCellEditorAutoCompletion {		public DefaultScrollTableAutoCompleteCellEditor(JTable table, boolean arg_bValidateBinding) {
			super(table, arg_bValidateBinding);
			textFieldAutoCompletion.addActionListener(new ActionListener() {				
				public void actionPerformed(ActionEvent e) {
					if (textFieldAutoCompletion.getSelectedIndex() < 0) {
						bEditingStoppedFromKey = true;
					}					
				}
			});
		}
	}
	
	private DefaultScrollTableCellEditor defaultCellEditor = new DefaultScrollTableCellEditor();
	private boolean bEditingStoppedFromKey = false;
	
	public EditorScrollTable() {
		super();		
		initialize();
	}
	
	private void initialize() {		
		setDefaultEditor(Object.class, defaultCellEditor);
	}

	@Override
	public void editingStopped(ChangeEvent e) {
		int editRow = editingRow;
		int editColumn = editingColumn;
		super.editingStopped(e);
		if (!bEditingStoppedFromKey) {
			return;
		}
		bEditingStoppedFromKey = false;
		for (int c = editColumn + 1; c < getColumnCount(); c ++) {
			if (dataModel.isCellEditable(editRow, c)) {
				editCellAt(editRow, c);
				return;
			}
		}
		for (int r = editRow + 1; r < getRowCount(); r ++) {
			for (int c = 0; c < getColumnCount(); c ++) {
				if (dataModel.isCellEditable(r, c)) {
					editCellAt(r, c);
					return;
				}
			}
		}
	}

	@Override
	public boolean editCellAt(final int row, final int column, EventObject e) {
		hoverColumn = -1;
		hoverRow = -1;
		boolean r = super.editCellAt(row, column, e);
		if (r) {
			clearSelection();
			SwingUtilities.invokeLater(new Runnable() {
				
				public void run() {
					if (getEditorComponent() != null) {
						if (getEditorComponent() instanceof JTextField) {
							((JTextField)getEditorComponent()).selectAll();
						}
						getEditorComponent().requestFocusInWindow();
						Rectangle rect =  getCellRect(row, column, true);
						scrollRectToVisible(rect);
					}
				}
			});
		}
		return r;
	}	
	
	public void setEditingStoppedFromKey() {
		bEditingStoppedFromKey = true;
	}
}
