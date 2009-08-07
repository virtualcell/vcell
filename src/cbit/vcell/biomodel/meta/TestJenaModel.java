/**
 * 
 */
package cbit.vcell.biomodel.meta;

import org.vcell.sybil.rdf.schemas.BioPAX2;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * some RDF test data
 * @author ruebenacker
 *
 */

public class TestJenaModel {
	
	public static Model getTestModel(String ns) {
		Model model = ModelFactory.createDefaultModel();
		Resource egfr = model.createResource(ns + "EGFR");
		model.add(egfr, RDF.type, BioPAX2.protein);
		Resource cytosol = model.createResource(ns + "Cytosol");
		model.add(cytosol, RDF.type, SBPAX.Location);
		Resource EGFRCytosol = model.createResource(ns + "EGFRCytosol");
		model.add(EGFRCytosol, RDF.type, SBPAX.Species);
		model.add(EGFRCytosol, SBPAX.consistsOf, egfr);
		model.add(EGFRCytosol, SBPAX.locatedAt, cytosol);		
		return model;
	}
	
}