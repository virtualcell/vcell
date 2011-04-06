package org.vcell.sybil.gui.port;

/*   ProcessTable  --- by Oliver Ruebenacker, UCHC --- July 2008
 *   The table to edit translation of entities
 */

import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.vcell.sybil.models.bpimport.table.Cell;
import org.vcell.sybil.models.bpimport.table.ProcessTableModel;

public class ProcessTable extends JTable {

	private static final long serialVersionUID = -2520577139535635777L;

	protected TableGUI tableGUI = new TableGUI(this);
	
	public ProcessTable() {
		setDefaultEditor(Cell.class, new ProcessTableCellEditor());
	}
	
	public ProcessTable(ProcessTableModel model) {
		super(model);
		model.setUI(tableGUI);
		setDefaultEditor(Cell.class, new ProcessTableCellEditor());
	}
	
	protected boolean columnLayoutManual = false;
	
	@Override
	public void doLayout() { 
		if(tableHeader.getResizingColumn() != null) { columnLayoutManual = true; }
		super.doLayout(); 
	}
	
	@Override
	public void setModel(TableModel model) {
		super.setModel(model);
		if(model instanceof ProcessTableModel) { ((ProcessTableModel) model).setUI(tableGUI); }
	}
	
	public ProcessTableModel model() { return (ProcessTableModel) getModel(); }
	
}
