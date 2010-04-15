package org.vcell.sybil.models.sbbox;

/*   DataTray  --- by Oliver Ruebenacker, UCHC --- June 2008 to December 2009
 *   Organizes the RDF data and structures to edit it
 */

import org.vcell.sybil.rdf.RDFBox;

import com.hp.hpl.jena.rdf.model.Model;

public interface InferenceBox extends RDFBox {
	
	public void performSYBREAMReasoning();
	
	public Model getSbpax();
	public Model getSchema();
	public Model getData();
	public Model getRdf();
}