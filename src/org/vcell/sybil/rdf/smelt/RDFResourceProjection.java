package org.vcell.sybil.rdf.smelt;

/*   RDFResourceProjection  --- by Oliver Ruebenacker, UCHC --- August 2010
 *   Applies a map to project RDF resources, statements and models. 
 *   Assumes identity where the map is empty
 */

import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class RDFResourceProjection implements RDFSmelter {

	// TODO testing
	
	protected final Map<Resource, Resource> map;
	
	public RDFResourceProjection(Map<Resource, Resource> map) {
		this.map = map;
	}
	
	public RDFNode project(RDFNode node) {
		RDFNode nodeMapped = map.get(node);
		if(nodeMapped == null) { nodeMapped = node; }
		return nodeMapped;
	}
	
	public Resource project(Resource resource) {
		Resource resourceMapped = map.get(resource);
		if(resourceMapped == null) { resourceMapped = resource; }
		return resourceMapped;
	}
	
	public Property project(Property property) {
		Resource resourceMapped = map.get(property);
		Property propertyMapped = property;
		if(resourceMapped != null && resourceMapped.isURIResource()) {
			String uri = resourceMapped.getURI();
			if(uri != null && uri.length() > 0) {
				propertyMapped = ResourceFactory.createProperty(uri);
			}
		}
		return propertyMapped;
	}
	
	public Statement project(Statement statement) {
		Resource subjectMapped = project(statement.getSubject());
		Property predicateMapped = project(statement.getPredicate());
		RDFNode objectMapped = project(statement.getObject());
		return ResourceFactory.createStatement(subjectMapped, predicateMapped, objectMapped);
	}

	public Model project(Model model) {
		Model modelMapped = ModelFactory.createDefaultModel();
		StmtIterator stmtIter = model.listStatements();
		while(stmtIter.hasNext()) {
			modelMapped.add(project(stmtIter.nextStatement()));
		}
		modelMapped.setNsPrefixes(model);
		return modelMapped;
	}
	
	public Model smelt(Model rdf) { return project(rdf); }

}
