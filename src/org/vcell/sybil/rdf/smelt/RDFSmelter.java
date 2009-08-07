package org.vcell.sybil.rdf.smelt;

/*   RDFSmelter  --- by Oliver Ruebenacker, UCHC --- June 2009
 *   An object for systematic RDF modification
 */

import com.hp.hpl.jena.rdf.model.Model;

public interface RDFSmelter {
	
	public Model smelt(Model rdf);

}
