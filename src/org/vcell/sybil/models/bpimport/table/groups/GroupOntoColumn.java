package org.vcell.sybil.models.bpimport.table.groups;

/*   OntoColumn  --- by Oliver Ruebenacker, UCHC --- July 2008 to July 2009
 *   Assigns cells into depending and including cells from another column
 */

import java.util.Collection;
import java.util.Iterator;

import org.vcell.sybil.models.bpimport.table.Cell;
import org.vcell.sybil.models.bpimport.table.Column;
import org.vcell.sybil.models.bpimport.table.options.CellOption;

public class GroupOntoColumn extends DependentGroupManager {
	
	public GroupOntoColumn(Column columnNew, Collection<? extends CellOption> defaultOptions) { 
		super(columnNew, defaultOptions); 
	}
	
	@Override
	public void updateOptions(DependentCellGroup group) {
		try {
			Iterator<Cell> cellDepIter = group.groupDependency().cellIter();
			while(cellDepIter.hasNext()) {
				Cell cell = cellDepIter.next();
				CellOption.Selection selected = cell.selected();
				if(!group.options().contains(selected)) { group.options().add(selected); }
			}			
		} catch(NullPointerException e) {}
		Iterator<Cell> cellIter = group.cellIter();
		while(cellIter.hasNext()) {
			Cell cell = cellIter.next();
			CellOption.Selection selected = cell.selected();
			if(!group.options().contains(selected)) { group.options().add(selected); }
		}
	}

	
}