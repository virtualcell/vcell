package org.vcell.sybil.rdf;

/*   RDFBag  --- by Oliver Ruebenacker, UCHC --- April 2010
 *   A wrapper for RDF bags
 */

import org.vcell.sybil.rdf.RDFBox.ResourceWrapper;

import com.hp.hpl.jena.rdf.model.Bag;

public class RDFBagWrapper extends ResourceWrapper implements RDFBag {

	public RDFBagWrapper(RDFBox box) { super(box, box.getRdf().createBag()); }
	public RDFBagWrapper(RDFBox box, Bag resource) { super(box, resource); }
	public Bag bag() { return (Bag) resource(); }
	
}