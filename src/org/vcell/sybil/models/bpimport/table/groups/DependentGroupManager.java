package org.vcell.sybil.models.bpimport.table.groups;

/*   DependentGroupManager  --- by Oliver Ruebenacker, UCHC --- July 2008 to March 2010
 *   Assigns cells into groups depending on another column.
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.vcell.sybil.models.bpimport.table.Cell;
import org.vcell.sybil.models.bpimport.table.Column;
import org.vcell.sybil.models.bpimport.table.Row;
import org.vcell.sybil.models.bpimport.table.options.CellOption;

public abstract class DependentGroupManager extends GroupManager {
	
	protected Column columnDependency;
	protected Map<CellGroup, DependentCellGroup> groupMap = new HashMap<CellGroup, DependentCellGroup>();
	
	public DependentGroupManager(Column columnDepNew, Collection<? extends CellOption> defaultOptions) { 
		super(defaultOptions);
		columnDependency = columnDepNew; 
		columnDependency.groupManager().deps().add(this);
	}
	
	@Override
	public DependentCellGroup determineGroup(Cell cell) { return defaultGroup(cell); }

	@Override
	public DependentCellGroup defaultGroup(Cell cell) { 
		Row row = cell.row();
		Cell cellDep = row.cell(columnDependency);
		if(cellDep != null) {
			CellGroup groupDep = cellDep.group();
			if(groupDep != null) {
				DependentCellGroup group = groupMap.get(groupDep);
				if(!(group instanceof DependentCellGroup)) {
					group = 
						new DependentCellGroup(cell.table(), cell.column(), columnDependency, groupDep, 
								defaultOptions);
					groupMap.put(groupDep, group);
				}
				updateOptions(group);
				return group;
			}
		}
		return new DependentCellDefaultGroup(cell, columnDependency, defaultOptions);
	}

	public void dependencyGroup(Cell cellDep) { 
		if(column != null) {
			Cell cell = cellDep.table().cell(column, cellDep.row());
			assignGroup(cell);
			updateOptions((DependentCellGroup) cell.group());
		}
	}

	public abstract void updateOptions(DependentCellGroup group);
	
}