package org.vcell.sybil.models.bpimport.table;

/*   ProcessTableModel  --- by Oliver Ruebenacker, UCHC --- June 2008 to November 2009
 *   A model for a process table
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.vcell.sybil.models.bpimport.edges.EdgeSBTray;
import org.vcell.sybil.models.bpimport.edges.MutableEdge;
import org.vcell.sybil.models.bpimport.table.groups.CellGroup;
import org.vcell.sybil.models.bpimport.table.options.CellThingOption;
import org.vcell.sybil.models.sbbox.SBBox;

public class ProcessTableModel implements TableModel {

	protected SBBox box;
	protected ColumnManager colManager;
	protected Vector<Row> rows = new Vector<Row>();
	protected Set<TableModelListener> listeners = new HashSet<TableModelListener>();
	protected Map<SBBox.Participant, Row> rowMap = new HashMap<SBBox.Participant, Row>();
	protected TableUI ui = new TableUI.Empty();
	
	public ProcessTableModel(EdgeSBTray edgeBox) {
		box = edgeBox.box();
		colManager = new ColumnManager(box);
		Set<SBBox.MutableLocation> locations = box.factories().locationFactory().openAll();
		Set<CellThingOption<SBBox.MutableLocation>> locOptions = 
			new HashSet<CellThingOption<SBBox.MutableLocation>>();
		for(SBBox.MutableLocation locationImport : locations) {
			SBBox.MutableLocation location = box().factories().locationFactory().insert(locationImport);
			locOptions.add(new CellThingOption<SBBox.MutableLocation>(location));
		}
		for(MutableEdge edge : edgeBox.edges()) { addEdgeToRow(edge, locOptions); }
	}
	
	public SBBox box() { return box; }
	public Class<Cell> getColumnClass(int columnIndex) { return Cell.class; }
	public int getColumnCount() { return colManager.numCols(); }
	public String getColumnName(int colInd) { return colManager.cols().get(colInd).name(); }
	public int getRowCount() { return rows.size(); }
	public ColumnManager colManager() { return colManager; }
	public List<Column> cols() { return colManager.cols(); }
	public Vector<Row> rows() { return rows; }

	public void setValueAt(Object value, int rowInd, int colInd) {
		if(value instanceof Cell) { rows.elementAt(rowInd).setCell((Cell) value, colInd); }
	}

	public Cell getValueAt(int rowInd, int colInd) { return rows.elementAt(rowInd).cell(colInd); }

	public boolean isCellEditable(int iRow, int iCol) { return colManager.cols().get(iCol).editable(); }

	public void addTableModelListener(TableModelListener l) { listeners.add(l); }
	public void removeTableModelListener(TableModelListener l) { listeners.remove(l); }
	
	public void addEdgeToRow(MutableEdge edge, 
			Set<CellThingOption<SBBox.MutableLocation>> locOptions) {
		SBBox.Participant participant = edge.participant();
		Row row = rowMap.get(participant);
		if(row == null) {
			row = new Row(this);
			rows.add(row);
			rowMap.put(participant, row);
		}
		row.addEdge(edge);
		Cell locCell = row.cell(colManager.colLocation());
		for(CellThingOption<SBBox.MutableLocation> locOption : locOptions) { 
			locCell.add(locOption, false, false); 
		}
	}
	
	public void setUI(TableUI uiNew) { ui = uiNew; }
	public TableUI ui() { return ui; }

	public void assignGroup(Cell cell) { cell.column().groupManager().assignGroup(cell); }
	
	public CellGroup defaultGroup(Cell cell) { return cell.column().groupManager().defaultGroup(cell); }
	
	public Cell cell(Column column, Row row) { return row.cell(column); }
	
}
