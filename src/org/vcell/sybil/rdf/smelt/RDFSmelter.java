package org.vcell.sybil.rdf.smelt;

/*   RDFSmelter  --- by Oliver Ruebenacker, UCHC --- June 2009
 *   An object for systematic RDF modification
 */

import org.openrdf.model.Graph;

public interface RDFSmelter {
	
	public Graph smelt(Graph rdf);

}
