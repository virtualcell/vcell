package org.vcell.sybil.models.bpimport.table.options;

/*   CellResourceOption  --- by Oliver Ruebenacker, UCHC --- July 2009 to November 2009
 *   An option to choose from in a cell, representing a Resource
 */

import org.vcell.sybil.models.sbbox.SBBox;

public class CellThingOption<T extends SBBox.NamedThing> extends CellSelectableOption implements CellOption {

	protected T thing;

	public CellThingOption(T thing) { this.thing = thing; }
	
	public T thing() { return thing; }
	public String label() { return thing.label(); }
	public String toString() { return thing.label(); }
	public boolean equals(Object o) {
		if(o instanceof CellThingOption<?>) {
			return thing.equals(((CellThingOption<?>) o).thing());
		}
		return false;
	}
	
	public int hashCode() { return thing.hashCode(); }

}
