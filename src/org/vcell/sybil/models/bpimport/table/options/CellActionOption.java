package org.vcell.sybil.models.bpimport.table.options;

/*   CellActionOption  --- by Oliver Ruebenacker, UCHC --- July 2008
 *   An option to choose from in a cell, representing an action to be taken
 */

import org.vcell.sybil.models.bpimport.table.Cell;

public abstract class CellActionOption implements CellOption {

	public static class ID {};
	
	protected ID id;
	protected String name;
	
	public CellActionOption(ID idNew, String nameNew) { id = idNew; name = nameNew; }
	
	public abstract CellOption eventSelect(Cell cell, CellOption.Selector selector);
	
	public ID id() { return id; }
	public String name() { return name; }
	public String toString() { return "[" + name + "]"; }

	public boolean equals(Object o) { 
		return o instanceof CellActionOption ? id.equals(((CellActionOption) o).id()) : false; 
	}
	
	public int hashCode() { return id.hashCode(); }

}
