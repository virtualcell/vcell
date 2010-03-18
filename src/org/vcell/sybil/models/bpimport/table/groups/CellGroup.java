package org.vcell.sybil.models.bpimport.table.groups;

/*   Cell  --- by Oliver Ruebenacker, UCHC --- June 2008 to July 2009
 *   A model for a cell of a process table
 */

import java.util.Collection;
import java.util.Iterator;
import org.vcell.sybil.models.bpimport.table.Cell;
import org.vcell.sybil.models.bpimport.table.ProcessTableModel;
import org.vcell.sybil.models.bpimport.table.options.CellNoneOption;
import org.vcell.sybil.models.bpimport.table.options.CellOption;
import org.vcell.sybil.util.lists.BoundedVector;

public abstract class CellGroup {

	protected BoundedVector<CellOption> options = new BoundedVector<CellOption>();	
	protected ProcessTableModel table;
	
	public CellGroup(ProcessTableModel tableNew, Collection<? extends CellOption> defaultOptions) {
		table = tableNew;
		options.addAll(defaultOptions);
	}
	
	public BoundedVector<CellOption> options() { return options; }
	public CellOption.Selection defaultSelectedOption() { return new CellNoneOption(); }
	public abstract Iterator<Cell> cellIter();
}
