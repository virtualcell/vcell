package org.vcell.sybil.rdf.specgen;

/*   OntologyInfo  --- by Oliver Ruebenacker, UCHC --- September 2008
 *   Generator for ontology specification
 */

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.vcell.sybil.rdf.JenaUtil;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OntologyInfo {

	public static interface Filter { 
		public boolean accept(Resource resource); 
		public int compare(Resource r1, Resource r2);
	}

	public static class DefaultFilter implements Filter {

		public boolean accept(Resource resource) {
			return resource.isURIResource() && resource.getLocalName() != null 
			&& !resource.getLocalName().equals("");
		}

		public int compare(Resource resource1, Resource resource2) {
			return resource1.getLocalName().compareToIgnoreCase(resource2.getLocalName());
		}
		
	}
	
	protected Model schema;
	protected Vector<Resource> resources = new Vector<Resource>();
	protected Filter filter = new DefaultFilter();
	
	public OntologyInfo(Model schema) {
		this.schema = schema;
		extractResources(schema, resources, filter);
		sortResources(resources, filter);
	}

	public static void extractResources(Model schema, List<Resource> resources, Filter filter) {
		StmtIterator stateIter = schema.listStatements();
		while(stateIter.hasNext()) {
			Statement statement = stateIter.nextStatement();
			for(Resource resource : JenaUtil.resourcesFromStatement(statement)) {
				if(filter.accept(resource) && !resources.contains(resource)) {
					resources.add(resource);
				}
			}
		}
	}
	
	public static void sortResources(List<Resource> resources, Filter filter ) {
		for(int iLast = resources.size() - 2; iLast >= 0; --iLast) {
			for(int i = 0; i <= iLast; ++i) {
				Resource r1 = resources.get(i);
				Resource r2 = resources.get(i+1);
				if(filter.compare(r1, r2) > 0) {
					resources.set(i, r2);
					resources.set(i+1, r1);
				}
			}
		}
	}

	public void write() { write(System.out); }
	
	public void write(PrintStream out) {
		for(Resource resource : resources) {
			String localName = resource.getLocalName();
			if(localName != null && localName != "") {
				out.println();
				out.println("       ***   " + localName + "   ***");
				out.println();
				out.println("  URI: " + resource.getURI());
				writeSuperClasses(out, resource);
				writeSubClasses(out, resource);
				writeHasObjectProperty(out, resource, RDFS.domain, "Has domain");
				writeHasObjectProperty(out, resource, RDFS.range, "Has range");
				OntologyInfo.writeIsPropertyOf(out, resource, RDFS.domain, "Is domain of");
				OntologyInfo.writeIsPropertyOf(out, resource, RDFS.range, "Is range of");
				out.println();
				writeComments(out, resource);
				out.println();
				
			}
		}
	}

	public static String resourcesToString(Set<Resource> resources) {
		String string = "";
		boolean isFirst = true;
		for(Resource resource : resources) {
			String localName = resource.getLocalName();
			if(localName != null && localName != "") {
				if(isFirst) {
					string = localName;
					isFirst = false;
				} else {
					string = string + ", " + localName;
				}
			}
		}
		return string;
	}
	
	public static void writeSuperClasses(PrintStream out, Resource resource) {
		StmtIterator stateIter = resource.listProperties(RDFS.subClassOf);
		Set<Resource> resources = new HashSet<Resource>();
		while(stateIter.hasNext()) {
			RDFNode superClassNode = stateIter.nextStatement().getObject();
			if(superClassNode instanceof Resource) {
				resources.add((Resource) superClassNode);
			}
		}
		if(resources.size() > 0) { 
			out.println("  Is sub class of: " + resourcesToString(resources));
		}
	}
	
	public static void writeHasObjectProperty(PrintStream out, Resource resource, Property property,
			String intro) {
		StmtIterator stateIter = resource.listProperties(property);
		Set<Resource> resources = new HashSet<Resource>();
		while(stateIter.hasNext()) {
			RDFNode superClassNode = stateIter.nextStatement().getObject();
			if(superClassNode instanceof Resource) {
				resources.add((Resource) superClassNode);
			}
		}
		if(resources.size() > 0) { 
			out.println("  " + intro + ": " + resourcesToString(resources));
		}
	}
	
	public static void writeSubClasses(PrintStream out, Resource resource) {
		Model schema = resource.getModel();
		StmtIterator stateIter = schema.listStatements((Resource) null, RDFS.subClassOf, resource);
		Set<Resource> resources = new HashSet<Resource>();
		while(stateIter.hasNext()) {
			resources.add(stateIter.nextStatement().getSubject());
		}
		if(resources.size() > 0) { 
			out.println("  Is super class of: " + resourcesToString(resources));
		}
	}
	
	public static void writeIsPropertyOf(PrintStream out, Resource resource, Property property,
			String intro) {
		Model schema = resource.getModel();
		StmtIterator stateIter = schema.listStatements((Resource) null, property, resource);
		Set<Resource> resources = new HashSet<Resource>();
		while(stateIter.hasNext()) {
			resources.add(stateIter.nextStatement().getSubject());
		}
		if(resources.size() > 0) { 
			out.println("  " + intro + ": " + resourcesToString(resources));
		}
	}
	
	public static void writeComments(PrintStream out, Resource resource) {
		StmtIterator stateIter = resource.listProperties(RDFS.comment);
		while(stateIter.hasNext()) {
			RDFNode commentNode = stateIter.nextStatement().getObject();
			if(commentNode instanceof Literal) {
				Literal commentLiteral = (Literal) commentNode;
				String comment = commentLiteral.getString();
				if(comment != null && comment != "") {
					out.println("  Comment: " + comment);							
				}
			}
		}
	}
	
}
