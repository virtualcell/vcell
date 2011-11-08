/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.util;

/*   OntologyInfo  --- by Oliver Ruebenacker, UCHC --- September 2008
 *   Generator for ontology specification
 */

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDFS;
import org.sbpax.util.sets.SetOfThree;
import org.sbpax.util.sets.SetOfTwo;

public class OntologyInfo {

	public static interface Filter { 
		public boolean accept(Resource resource); 
		public int compare(Resource r1, Resource r2);
	}

	public static class DefaultFilter implements Filter {

		public boolean accept(Resource resource) {
			return (resource instanceof URI)  && ((URI) (resource)).getLocalName() != null 
			&& !((URI) (resource)).getLocalName().equals("");
		}

		public int compare(Resource resource1, Resource resource2) {
			return ((URI) (resource1)).getLocalName().compareToIgnoreCase(((URI) (resource2)).getLocalName());
		}
		
	}
	
	protected Graph schema;
	protected List<Resource> resources = new ArrayList<Resource>();
	protected Filter filter = new DefaultFilter();
	
	public OntologyInfo(Graph schema) {
		this.schema = schema;
		extractResources(schema, resources, filter);
		sortResources(resources, filter);
	}

	public static Set<Resource> resourcesFromStatement(Statement statement) {
		Resource subject = statement.getSubject();
		URI predicate = statement.getPredicate();
		Value object = statement.getObject();
		if(object instanceof Resource) { return new SetOfThree<Resource>(subject, predicate, (Resource) object); }
		else { return new SetOfTwo<Resource>(subject, predicate); }
	}
	
	public static void extractResources(Graph schema, List<Resource> resources, Filter filter) {
		for(Statement statement : schema) {
			for(Resource resource : resourcesFromStatement(statement)) {
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
			writeResource(schema, out, resource);
		}
		out.println(StringUtil.multiply("=", 77));
	}

	public void write(PrintStream out, Set<? extends Resource> excludedResources) {
		for(Resource resource : resources) {
			if(!excludedResources.contains(resource)) {
				writeResource(schema, out, resource);				
			}
		}
		out.println(StringUtil.multiply("=", 77));
	}

	public void writeResource(Graph graph, PrintStream out, Resource resource) {
		String localName = resource instanceof URI ? ((URI) (resource)).getLocalName() : resource.stringValue();
		if(localName != null && localName != "") {
			out.println(StringUtil.multiply("=", 77));
			out.println("       ***   " + localName + "   ***");
			out.println(StringUtil.multiply("-", 77));
			if(resource instanceof URI) {
				out.println("  URI: " + ((URI) (resource)).stringValue());				
			}
			writeSuperClasses(graph, out, resource);
			writeSubClasses(graph, out, resource);
			writeHasObjectProperty(graph, out, resource, RDFS.DOMAIN, "Has domain");
			writeHasObjectProperty(graph, out, resource, RDFS.RANGE, "Has range");
			OntologyInfo.writeIsPropertyOf(graph, out, resource, RDFS.DOMAIN, "Is domain of");
			OntologyInfo.writeIsPropertyOf(graph, out, resource, RDFS.RANGE, "Is range of");
			out.println();
			writeComments(graph, out, resource);
			out.println();
			
		}
	}

	public static String resourcesToString(Set<Resource> resources) {
		String string = "";
		boolean isFirst = true;
		for(Resource resource : resources) {
			if(resource instanceof URI) {
				URI uri = (URI) resource;
				String localName = uri.getLocalName();
				if(localName != null && localName != "") {
					if(isFirst) {
						string = localName;
						isFirst = false;
					} else {
						string = string + ", " + localName;
					}
				}
			}
		}
		return string;
	}

	public static void writeSuperClasses(Graph graph, PrintStream out, Resource resource) {
		Iterator<Statement> iter = graph.match(resource, RDFS.SUBCLASSOF, null);
		Set<Resource> resources = new HashSet<Resource>();
		while(iter.hasNext()) {
			Value superClassNode = iter.next().getObject();
			if(superClassNode instanceof Resource) {
				resources.add((Resource) superClassNode);
			}
		}
		if(resources.size() > 0) { 
			out.println("  Is sub class of: " + resourcesToString(resources));
		}
	}
	
	public static void writeHasObjectProperty(Graph graph, PrintStream out, Resource resource, URI property,
			String intro) {
		Iterator<Statement> iter = graph.match(resource, property, null);
		Set<Resource> resources = new HashSet<Resource>();
		while(iter.hasNext()) {
			Value superClassNode = iter.next().getObject();
			if(superClassNode instanceof Resource) {
				resources.add((Resource) superClassNode);
			}
		}
		if(resources.size() > 0) { 
			out.println("  " + intro + ": " + resourcesToString(resources));
		}
	}
	
	public static void writeSubClasses(Graph graph, PrintStream out, Resource resource) {
		Iterator<Statement> iter = graph.match(null, RDFS.SUBCLASSOF, resource);
		Set<Resource> resources = new HashSet<Resource>();
		while(iter.hasNext()) {
			resources.add(iter.next().getSubject());
		}
		if(resources.size() > 0) { 
			out.println("  Is super class of: " + resourcesToString(resources));
		}
	}
	
	public static void writeIsPropertyOf(Graph graph, PrintStream out, Resource resource, URI property,
			String intro) {
		Iterator<Statement> iter = graph.match(null, property, resource);
		Set<Resource> resources = new HashSet<Resource>();
		while(iter.hasNext()) {
			resources.add(iter.next().getSubject());
		}
		if(resources.size() > 0) { 
			out.println("  " + intro + ": " + resourcesToString(resources));
		}
	}
	
	public static void writeComments(Graph graph, PrintStream out, Resource resource) {
		Iterator<Statement> iter = graph.match(resource, RDFS.COMMENT, null);
		while(iter.hasNext()) {
			Value commentNode = iter.next().getObject();
			if(commentNode instanceof Literal) {
				Literal commentLiteral = (Literal) commentNode;
				String comment = commentLiteral.stringValue();
				if(comment != null && comment != "") {
					out.println("  Comment: " + comment);							
				}
			}
		}
	}
	
}
