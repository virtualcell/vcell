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

/*   OntSpecUtil  --- by Oliver Ruebenacker, UCHC --- May to September 2008
 *   Methods for supporting the creation of specifications for ontologies
 */

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.sbpax.schemas.util.DefaultNameSpaces;
import cbit.util.xml.XmlUtil;

public class OntSpecUtil {

	public static void writeToFile(Graph schema, String fileName, RDFFormat style) {
		System.out.println("Writing Ontology to " + fileName);
		try { 
			java.io.StringWriter strWriter = new StringWriter();
			SesameRioUtil.writeRDFToWriter(strWriter, schema, DefaultNameSpaces.defaultMap.convertToMap(), style);
			XmlUtil.writeXMLStringToFile(strWriter.getBuffer().toString(), fileName, true);
		} catch (IOException e) { 
			e.printStackTrace(System.out); 
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}
	
	public static void writeToFiles(Graph schema, String baseFileName) {
		writeToFile(schema, baseFileName + ".owl", RDFFormat.RDFXML);
		writeToFile(schema, baseFileName + ".n3", RDFFormat.N3);
	}

	public static boolean isMissingComment(Graph graph, Value node) {
		if(node instanceof URI) {
			Iterator<Statement> iter = graph.match((Resource) node, RDFS.COMMENT, null);
			return !iter.hasNext();			
		}
		return false;
	}
	
	public static Set<Resource> findMissingComment(Graph schema) {
		Set<Resource> resources = new HashSet<Resource>();
		for(Statement statement : schema) {
			Resource subject = statement.getSubject();
			if(isMissingComment(schema, subject)) { resources.add(subject); }
			URI predicate = statement.getPredicate();
			if(isMissingComment(schema, predicate)) { resources.add(predicate); };
			Value object = statement.getObject();
			if(isMissingComment(schema, object)) { resources.add((Resource) object); }
		}
		return resources;
	}
	
	public static void listMissingComments(Graph schema, Set<? extends Resource> excludedResources) {
		Set<Resource> resources = findMissingComment(schema);
		for(Resource resource : resources) {
			if(!excludedResources.contains(resource)) {
				System.out.println("Comment missing for: " + resource);			
			}
		}
	}
	
}
