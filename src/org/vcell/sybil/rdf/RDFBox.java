package org.vcell.sybil.rdf;

import org.vcell.sybil.util.keys.KeyOfOne;
import org.vcell.sybil.util.keys.KeyOfTwo;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * a container for RDF data for shared use.
 * @author ruebenacker
 *
 */

public interface RDFBox {

	public Model getRdf();
	
	public static interface RDFThing { 
		public Resource resource(); 
		public RDFBox box();
		// public B subBox();
	}
	
	public static interface RDFProperty { public Property property(); }
	
	public static class ResourceWrapper extends 
	KeyOfTwo<RDFBox, Resource> implements RDFThing {
		public ResourceWrapper(RDFBox box, Resource resource) { super(box, resource); }
		public RDFBox box() { return a(); };
		public Resource resource() { return b(); }
	}
	
	public static class PropertyWrapper extends KeyOfOne<Property> implements RDFProperty {
		public PropertyWrapper(Property property) { super(property); }
		public Property property() { return a(); }
	}
	
	public static class Default implements RDFBox {

		Model rdfModel = ModelFactory.createDefaultModel();

		public Default() { this(ModelFactory.createDefaultModel()); }
		public Default(Model rdfModel) { this.rdfModel = rdfModel; }
		
		public Model getRdf() { return rdfModel; }
		public void setRDF(Model rdf) { this.rdfModel = rdf; }

		public RDFThing createThing() { 
			return new ResourceWrapper(this, rdfModel.createResource()); 
		}
		
		public RDFThing createThing(String uri) { 
			return new ResourceWrapper(this, rdfModel.createResource(uri)); 
		}
		
	}
	
	
	
}
