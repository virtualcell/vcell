package org.vcell.sybil.rdf;

/*   RDFBag  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   A wrapper for RDF bags
 */

import org.vcell.sybil.rdf.RDFBox.ResourceWrapper;

import com.hp.hpl.jena.rdf.model.Bag;

public class RDFBagWrapper<B extends RDFBox> extends ResourceWrapper<B> implements RDFBag<B> {

	public RDFBagWrapper(B box) { super(box, box.getRdf().createBag()); }
	public RDFBagWrapper(B box, Bag resource) { super(box, resource); }
	public Bag bag() { return (Bag) resource(); }
	
}