package org.vcell.sybil.rdf.baptizer;

/*   RDFLocalNameGenerator  --- by Oliver Ruebenacker, UCHC --- June 2009
 *   Creates a new local name for (e.g. anonymous) resources
 */

import com.hp.hpl.jena.rdf.model.Resource;

public interface RDFLocalNamer {
	
	public String newLocalName(Resource resource);

}
