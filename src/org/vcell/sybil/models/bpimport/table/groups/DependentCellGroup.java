package org.vcell.sybil.models.bpimport.table.groups;

/*   DependentCellGroup  --- by Oliver Ruebenacker, UCHC --- July 2008 to March 2010
 *   Assigns cells into groups depending on another column.
 */

import java.util.Collection;
import java.util.Iterator;

import org.vcell.sybil.models.bpimport.table.Cell;
import org.vcell.sybil.models.bpimport.table.Column;
import org.vcell.sybil.models.bpimport.table.ColumnCellIter;
import org.vcell.sybil.models.bpimport.table.ProcessTableModel;
import org.vcell.sybil.models.bpimport.table.options.CellOption;
import org.vcell.sybil.util.iterators.FilterIter;

public class DependentCellGroup extends CellGroup implements FilterIter.Tester<Cell> {

	protected Column column;
	protected Column columnDependency;
	protected CellGroup groupDependency;
	
	public DependentCellGroup(ProcessTableModel tableNew, Column columnNew, Column columnDepNew, 
			CellGroup groupDepNew, Collection<? extends CellOption> defaultOptions) {
		super(tableNew, defaultOptions);
		column = columnNew;
		columnDependency = columnDepNew;
		groupDependency = groupDepNew;
	}

	public Column columnDependency() { return columnDependency; }
	public CellGroup groupDependency() { return groupDependency; }
	
	public boolean accepts(Cell cell) {
		try { return cell.row().cell(columnDependency).group().equals(groupDependency); }
		catch (NullPointerException e) { return false; }
	}

	public Iterator<Cell> cellIter() {
		return new FilterIter<Cell>(new ColumnCellIter(table, column), this);
	}
	
}