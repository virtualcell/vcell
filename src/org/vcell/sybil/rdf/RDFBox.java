package org.vcell.sybil.rdf;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.vcell.sybil.rdf.impl.HashGraph;
import org.vcell.sybil.util.keys.KeyOfOne;
import org.vcell.sybil.util.keys.KeyOfTwo;


/**
 * a container for RDF data for shared use.
 * @author ruebenacker
 *
 */

public interface RDFBox {

	public Graph getRdf();
	
	public static interface RDFThing { 
		public Resource resource(); 
		public RDFBox box();
		// public B subBox();
	}
	
	public static interface RDFProperty { public Resource property(); }
	
	public static class ResourceWrapper extends 
	KeyOfTwo<RDFBox, Resource> implements RDFThing {
		public ResourceWrapper(RDFBox box, Resource resource) { super(box, resource); }
		public RDFBox box() { return a(); };
		public Resource resource() { return b(); }
	}
	
	public static class PropertyWrapper extends KeyOfOne<URI> implements RDFProperty {
		public PropertyWrapper(URI property) { super(property); }
		public URI property() { return a(); }
	}
	
	public static class Default implements RDFBox {

		Graph rdfModel;

		public Default() { this(new HashGraph()); }
		public Default(Graph rdfModel) { this.rdfModel = rdfModel; }
		
		public Graph getRdf() { return rdfModel; }
		public void setRDF(Graph rdf) { this.rdfModel = rdf; }

		public RDFThing createThing() { 
			return new ResourceWrapper(this, rdfModel.getValueFactory().createBNode()); 
		}
		
		public RDFThing createThing(String uri) { 
			return new ResourceWrapper(this, rdfModel.getValueFactory().createURI(uri)); 
		}
		
	}
	
	
	
}
