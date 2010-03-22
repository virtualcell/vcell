package org.vcell.sybil.gui.port;

/*   CellComboBox  --- by Oliver Ruebenacker, UCHC --- July 2008
 *   A combo box build on a bounded list
 */

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;

import org.vcell.sybil.models.bpimport.table.Cell;
import org.vcell.sybil.models.bpimport.table.options.CellOption;

public class CellComboBox extends JComboBox implements ItemListener, CellOption.Selector {

	private static final long serialVersionUID = -1195701244211670910L;

	protected Object lastSelected;
	
		protected Cell cell;
		protected TableCellEditor editor;
		
		public CellComboBox(Cell cellNew, TableCellEditor editorNew) { 
			super(cellNew.boxModel()); 
			cell = cellNew; 
			editor = editorNew;
			addItemListener(this);			
		}
		
		public void itemStateChanged(ItemEvent event) {
			if(event.getStateChange() == ItemEvent.SELECTED) {
				Object item = event.getItem();
				if(item instanceof CellOption) {
					CellOption option = (CellOption) item;
					option.eventSelect(cell, this);
				}
			} else if(event.getStateChange() == ItemEvent.DESELECTED) {
				lastSelected = event.getItem();
			}

		}
		
	public void returnToLastSelection() { setSelectedItem(lastSelected); }
	public void select(CellOption optionNew) { setSelectedItem(optionNew); }
	public CellOption selected() { return (CellOption) getSelectedItem(); }
	public void cancel() { editor.cancelCellEditing(); }
	public void stop() { editor.stopCellEditing(); }
	
}
