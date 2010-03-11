package org.vcell.sybil.rdf;

/*   RDFBag  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   A wrapper for RDF bags
 */

import org.vcell.sybil.rdf.RDFBox.RDFThing;
import com.hp.hpl.jena.rdf.model.Bag;

public interface RDFBag<B extends RDFBox> extends RDFThing<B> {
	public Bag bag();
	
}