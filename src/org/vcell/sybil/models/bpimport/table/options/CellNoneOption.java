package org.vcell.sybil.models.bpimport.table.options;

/*   CellNoneOption  --- by Oliver Ruebenacker, UCHC --- July 2008 to March 2009
 *   An option to choose from in a cell to indicate no node is selected
 */

public class CellNoneOption extends CellSelectableOption {

	public static final String LABEL = "[none]";
	
	public String label() { return LABEL; }
	@Override
	public String toString() { return LABEL; }
	@Override
	public boolean equals(Object o) { return o instanceof CellNoneOption; }
	@Override
	public int hashCode() { return LABEL.hashCode(); }

}
