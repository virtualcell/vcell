package org.vcell.sybil.models.bpimport.table.options;

/*   CellNodeOption  --- by Oliver Ruebenacker, UCHC --- July 2008 to March 2009
 *   An option to choose from in a cell, representing a node
 */

import org.vcell.sybil.models.bpimport.table.Cell;

public class CellSelectableOption implements CellOption.Selection {

	public CellOption eventSelect(Cell cell, CellOption.Selector selector) {
		selector.select(this);
		selector.stop();
		return this;
	}
	
}
