package org.vcell.sybil.rdf;

/*   RDFBag  --- by Oliver Ruebenacker, UCHC --- April 2010
 *   A wrapper for RDF bags
 */

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.vcell.sybil.rdf.RDFBox.ResourceWrapper;

public class RDFBagWrapper extends ResourceWrapper {

	public RDFBagWrapper(RDFBox box) { 
		super(box, box.getRdf().getValueFactory().createBNode()); 
		box.getRdf().add(resource(), RDF.TYPE, RDF.BAG);
	}
	
	public RDFBagWrapper(RDFBox box, Resource resource) { super(box, resource); }
	
	public void delete() {
		Statement statement = box().getRdf().getValueFactory().createStatement(resource(), RDF.TYPE, RDF.BAG);
		box().getRdf().remove(statement);
	}
	
	public static boolean isRDFContainerMembershipProperty(URI uri) {
		if(RDF.LI.equals(uri)) { return true; }
		if(uri.stringValue().contains(RDF.NAMESPACE + "_")) { return true; }
		return false;
	}
	
}