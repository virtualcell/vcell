package org.vcell.sybil.gui.port;

/*   ProcessTableCellEditor  --- by Oliver Ruebenacker, UCHC --- July 2008
 *   An editor for process table cells, primarily producing a combo box
 */

import java.awt.Component;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

import org.vcell.sybil.models.bpimport.table.Cell;

public class ProcessTableCellEditor implements TableCellEditor {

	protected Cell cell;
	protected Set<CellEditorListener> listeners = new HashSet<CellEditorListener>();
	
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		if(value instanceof Cell) { 
			cell = (Cell) value;
			return new CellComboBox(cell, this); 
		}
		return null;
	}

	public void addCellEditorListener(CellEditorListener l) { listeners.add(l); }
	public void removeCellEditorListener(CellEditorListener l) { listeners.remove(l); }

	public void cancelCellEditing() {
		for(CellEditorListener listener : listeners) { listener.editingCanceled(new ChangeEvent(this)); }
	}

	public boolean stopCellEditing() {
		for(CellEditorListener listener : listeners) { listener.editingStopped(new ChangeEvent(this)); }
		return true;
	}

	public Cell getCellEditorValue() { return cell; }
	public boolean isCellEditable(EventObject anEvent) { return true; }
	public boolean shouldSelectCell(EventObject anEvent) { return false;}

}
