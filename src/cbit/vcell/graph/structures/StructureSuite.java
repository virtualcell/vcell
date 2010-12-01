package cbit.vcell.graph.structures;

/*  The structures displayed by a reaction cartoon
 *   November 2010
 */

import java.util.Collection;
import cbit.vcell.model.Structure;

public abstract class StructureSuite {

	protected final String title;
	
	public StructureSuite(String title) {
		this.title = title;
	}
	
	public String getTitle() { return title; }
	public abstract Collection<Structure> getStructures();
	public abstract boolean areReactionsShownFor(Structure structure);

	public boolean equals(Object object) {
		if(object instanceof StructureSuite) {
			StructureSuite suite = (StructureSuite) object;
			return title.equals(suite.getTitle());
		}
		return false;
	}
	
	public int hashCode() {
		return title.hashCode();
	}
	
}
