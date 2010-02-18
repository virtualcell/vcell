package org.vcell.sybil.rdf;

import org.vcell.sybil.util.keys.KeyOfTwo;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * a container for RDF data for shared use.
 * @author ruebenacker
 *
 */

public interface RDFBox {

	public Model getRdf();
	
	public static interface RDFThing<B extends RDFBox> { 
		public Resource resource(); 
		public B box();
		// public B subBox();
	}
	
	public static class ResourceWrapper<B extends RDFBox> extends 
	KeyOfTwo<B, Resource> implements RDFThing<B> {
		public ResourceWrapper(B box, Resource resource) { super(box, resource); }
		public B box() { return a(); };
		public Resource resource() { return b(); }
	}
	
	public static class Default implements RDFBox {

		Model rdfModel = ModelFactory.createDefaultModel();

		public Default() { this(ModelFactory.createDefaultModel()); }
		public Default(Model rdfModel) { this.rdfModel = rdfModel; }
		
		public Model getRdf() { return rdfModel; }
		public void setRDF(Model rdf) { this.rdfModel = rdf; }
		
	}
	
	
	
}
