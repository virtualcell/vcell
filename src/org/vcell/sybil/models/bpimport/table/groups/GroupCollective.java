package org.vcell.sybil.models.bpimport.table.groups;

/*   Collective  --- by Oliver Ruebenacker, UCHC --- July 2008 to July 2009
 *   Assigns all cells of a column into one group
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.vcell.sybil.models.bpimport.table.Cell;
import org.vcell.sybil.models.bpimport.table.Column;
import org.vcell.sybil.models.bpimport.table.ColumnCellIter;
import org.vcell.sybil.models.bpimport.table.ProcessTableModel;
import org.vcell.sybil.models.bpimport.table.options.CellOption;

public class GroupCollective extends GroupManager {

	public static class ColumnCellGroup extends CellGroup {

		protected Column column;
		
		public ColumnCellGroup(ProcessTableModel tableNew, Column columnNew,
				Collection<? extends CellOption> defaultOptions) {
			super(tableNew, defaultOptions);
			column = columnNew;
		}

		@Override
		public Iterator<Cell> cellIter() { return new ColumnCellIter(table, column); }
		
	}
		
	protected Map<Column, CellGroup> colToGroup = new HashMap<Column, CellGroup>();
	
	public GroupCollective(Collection<? extends CellOption> defaultOptions) { super(defaultOptions); }

	@Override
	public CellGroup determineGroup(Cell cell) { return defaultGroup(cell); }

	@Override
	public CellGroup defaultGroup(Cell cell) { 
		Column col = cell.column();
		CellGroup group = colToGroup.get(col);
		if(group == null) {
			group = new ColumnCellGroup(cell.table(), cell.column(), defaultOptions);
			colToGroup.put(col, group);
		}
		return group; 
	}

}