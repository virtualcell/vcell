package org.vcell.sybil.rdf.schemas;

import java.util.List;

import org.vcell.sybil.rdf.NameSpace;
import org.vcell.sybil.rdf.RDFBox;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * a container for an ontology
 * @author ruebenacker
 *
 */

public interface OntologyBox extends RDFBox {

	public Model getRdf();
	public String uri();	
	public NameSpace ns();
	public String label();
	public List<DatatypeProperty> labelProperties();

}
