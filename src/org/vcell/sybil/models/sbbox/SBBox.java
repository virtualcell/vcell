package org.vcell.sybil.models.sbbox;

/*   SBBox  --- by Oliver Ruebenacker, UCHC --- June 2008 to April 2010
 *   Organizes the RDF data and structures to edit it
 */

import org.vcell.sybil.models.sbbox.factories.Factories;
import org.vcell.sybil.rdf.RDFBox;

public interface SBBox extends RDFBox {
	
	public static interface NamedThing extends RDFThing {
		public SBBox box();
	}
	
	public Factories factories();
		
}
