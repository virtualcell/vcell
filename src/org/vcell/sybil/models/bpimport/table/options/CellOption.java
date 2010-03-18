package org.vcell.sybil.models.bpimport.table.options;

/*   CellOption  --- by Oliver Ruebenacker, UCHC --- July 2008
 *   An option to choose from in a cell
 */

import org.vcell.sybil.models.bpimport.table.Cell;

public interface CellOption {
	
	public static interface Selection extends CellOption {}
	
	public static interface Selector {
		public void select(CellOption optionNew);
		public CellOption selected();
		public void returnToLastSelection();
		public void cancel();
		public void stop();
	}

	public CellOption eventSelect(Cell cell, Selector selector);
	
}
