package org.vcell.sybil.models.bpimport.table.groups;

/*   DependentCellGroup  --- by Oliver Ruebenacker, UCHC --- July 2008 to March 2010
 *   Assigns cells into groups depending on another column.
 */

import java.util.Collection;
import java.util.Iterator;

import org.vcell.sybil.models.bpimport.table.Cell;
import org.vcell.sybil.models.bpimport.table.Column;
import org.vcell.sybil.models.bpimport.table.options.CellOption;
import org.vcell.sybil.util.iterators.IterOfOne;

public class DependentCellDefaultGroup extends DependentCellGroup {

	protected Cell cell;
	
	public DependentCellDefaultGroup(Cell cell, Column columnDependencyNew, 
			Collection<? extends CellOption> defaultOptions) {
		super(cell.table(), cell.column(), columnDependencyNew, null, defaultOptions);
	}
	
	public Iterator<Cell> cellIter() { return new IterOfOne<Cell>(cell); }
	
}