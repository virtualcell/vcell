package org.vcell.sybil.models.bpimport.table;

/*   TableUI  --- by Oliver Ruebenacker, UCHC --- July 2008 to July 2009
 *   User interface support for a process table
 */

import org.vcell.sybil.models.bpimport.table.options.CellLiteralOption;
import org.vcell.sybil.models.bpimport.table.options.CellThingOption;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.factories.ThingFactory;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;

public interface TableUI {

	public static class NoProperInputException extends Exception {
		private static final long serialVersionUID = 4785905183683914310L;
		public NoProperInputException() { super("Can not create node from input"); }
	}
	
	public <V extends SBBox.NamedThing>
	CellThingOption<V> askForCellResourceOption(ThingFactory<? extends V> factory, 
			SBBox box, Resource sampleNode) 
	throws NoProperInputException;
	public CellLiteralOption askForCellLiteralOption(Literal sampleNode) throws NoProperInputException;
	
	public static class Empty implements TableUI {
		public <V extends SBBox.NamedThing>
		CellThingOption<V> askForCellResourceOption(ThingFactory<? extends V> factory, 
				SBBox box, Resource sampleNode) { 
			return new CellThingOption<V>(factory.create(sampleNode)); 
		}
		public CellLiteralOption askForCellLiteralOption(Literal sampleNode) { 
			return new CellLiteralOption(sampleNode); 
		}
	}
	
}
