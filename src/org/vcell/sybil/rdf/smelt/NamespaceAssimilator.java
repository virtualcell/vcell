package org.vcell.sybil.rdf.smelt;

/*   NamespaceAssimilator  --- by Oliver Ruebenacker, UCHC --- June to July 2009
 *   Changes a set of resources into the same namespace
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.vcell.sybil.rdf.baptizer.RDFLocalNamer;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;

public class NamespaceAssimilator implements RDFSmelter {
	
	protected Set<Resource> resources;
	protected String namespace;
	protected RDFLocalNamer localNamer;
	protected Map<Resource, Resource> resourceMap = new HashMap<Resource, Resource>();
	
	public NamespaceAssimilator(Set<Resource> resources, String namespace, RDFLocalNamer localNamer) {
		this.resources = resources;
		this.namespace = namespace;
		this.localNamer = localNamer;
	}
	
	public Model smelt(Model rdf) {
		Model rdfNew = ModelFactory.createDefaultModel();
		rdfNew.setNsPrefixes(rdf);
		StmtIterator stmtIter = rdf.listStatements();
		while(stmtIter.hasNext()) {
			Statement statement = stmtIter.nextStatement();
			Resource subjectNew = smelt(rdf, statement.getSubject());
			Property predicateNew = smelt(rdf, statement.getPredicate());
			RDFNode objectNew = statement.getObject() instanceof Resource ? 
					smelt(rdf, (Resource) statement.getObject()) : statement.getObject();
			rdfNew.add(subjectNew, predicateNew, objectNew);
		}
		for(Map.Entry<Resource, Resource> entry : resourceMap.entrySet()) {
			rdfNew.add(entry.getKey(), OWL.sameAs, entry.getValue());
		}
		rdfNew.setNsPrefixes(rdf);
		return rdfNew;
	}

	public Resource smelt(Model rdf, Resource resource) {
		Resource resourceMapped = resource;
		if(!resources.contains(resource)) {
			if(resource.isURIResource()) {
				if(!namespace.equals(resource.getNameSpace())) {
					resourceMapped = resourceMap.get(resource);
					if(resourceMapped == null) {
						String localName = resource.getLocalName();
						String localNameNew = localName;
						int i = 0;
						resourceMapped = rdf.createResource(namespace + localNameNew);
						while(rdf.containsResource(resourceMapped)) {
							++i;
							localNameNew = localName + i;
							resourceMapped = rdf.createResource(namespace + localNameNew);
						}
						resourceMap.put(resource, resourceMapped);
					}
				}
			} else {
				resourceMapped = resourceMap.get(resource);
				if(resourceMapped == null) {
					String localNameBase = localNamer.newLocalName(resource);
					String localNameNew;
					int i = 0;
					do {
						++i;
						localNameNew = localNameBase + i;
						resourceMapped = rdf.createResource(namespace + localNameNew);
					} while(rdf.containsResource(resourceMapped));
					resourceMap.put(resource, resourceMapped);
				}
				
			}
		}
		return resourceMapped;
	}
	
	public Property smelt(Model rdf, Property property) {
		Property propertyMapped = property;
		if(!resources.contains(property)) {
			if(!namespace.equals(property.getNameSpace())) {
				Resource resourceMapped = resourceMap.get(property);
				if(resourceMapped instanceof Property) {
					propertyMapped = (Property) resourceMapped;
				} else if (resourceMapped instanceof Resource) {
					propertyMapped = rdf.createProperty(resourceMapped.getURI());
					resourceMap.put(property, propertyMapped);
				} else {
					String localName = property.getLocalName();
					String localNameNew = localName;
					int i = 0;
					propertyMapped = rdf.createProperty(namespace + localNameNew);
					while(rdf.containsResource(propertyMapped)) {
						++i;
						localNameNew = localName + i;
						propertyMapped = rdf.createProperty(namespace + localNameNew);
					}
					resourceMap.put(property, propertyMapped);
				} 
			}
		}
		return propertyMapped;
	}
	
	public Resource map(Resource resource) {
		Resource resourceMapped = resourceMap.get(resource);
		if(resourceMapped == null) { resourceMapped = resource; }
		return resourceMapped;
	}
	public Map<Resource, Resource> resourceMap() { return resourceMap; }

}
