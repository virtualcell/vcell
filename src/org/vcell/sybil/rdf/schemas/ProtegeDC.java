/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.rdf.schemas;

/*   ProtegeDC  --- by Oliver Ruebenacker, UCHC --- May 2008
 *   Protege's version of the Dublin Core schema
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.vcell.sybil.rdf.NameSpace;
import org.vcell.sybil.rdf.OntUtil;
import org.vcell.sybil.rdf.SesameRioUtil;
import org.vcell.sybil.rdf.impl.HashGraph;
import org.vcell.sybil.util.sets.SetUtil;

public class ProtegeDC {

	public static final String URI = "http://protege.stanford.edu/plugins/owl/dc/protege-dc.owl";
	
	public static final NameSpace ns = new NameSpace("", "http://purl.org/dc/elements/1.1/");
	public static final NameSpace nsXSD = new NameSpace("xsd", "http://www.w3.org/2001/XMLSchema#");	
	public static final NameSpace nsDC = new NameSpace("dc", "http://purl.org/dc/elements/1.1/");	
	public static final NameSpace nsDCTerms = new NameSpace("dcterms", "http://purl.org/dc/terms/");	
	
	public static final Graph schema = new HashGraph();

	public static final Set<NameSpace> NAMESPACES = SetUtil.newSet(ns, nsDC, 
			new NameSpace("rdfs", RDFS.NAMESPACE), nsXSD, nsDCTerms, new NameSpace("owl", OWL.NAMESPACE),
			new NameSpace("rdf", RDF.NAMESPACE));
 	
	public static final Resource ontology = OntUtil.createOntologyNode(schema, URI);
	
	public static final URI relation = OntUtil.createAnnotationProperty(schema, ns + "relation");
	public static final URI description = 
		OntUtil.createAnnotationProperty(schema, ns + "description");
	public static final URI language = OntUtil.createAnnotationProperty(schema, ns + "language");
	public static final URI identifier = OntUtil.createAnnotationProperty(schema, ns + "identifier");
	public static final URI subject = OntUtil.createAnnotationProperty(schema, ns + "subject");
	public static final URI rights = OntUtil.createAnnotationProperty(schema, ns + "rights");
	public static final URI coverage = OntUtil.createAnnotationProperty(schema, ns + "coverage");
	public static final URI contributor = 
		OntUtil.createAnnotationProperty(schema, ns + "contributor");
	public static final URI format = OntUtil.createAnnotationProperty(schema, ns + "format");
	public static final URI type = OntUtil.createAnnotationProperty(schema, ns + "type");
	public static final URI creator = OntUtil.createAnnotationProperty(schema, ns + "creator");
	public static final URI date = OntUtil.createAnnotationProperty(schema, ns + "date");
	public static final URI created = OntUtil.createAnnotationProperty(schema, ns + "created");
	public static final URI publisher = OntUtil.createAnnotationProperty(schema, ns + "publisher");
	public static final URI source = OntUtil.createAnnotationProperty(schema, ns + "source");
	public static final URI title = OntUtil.createAnnotationProperty(schema, ns + "title");
	
	public static void main(String[] args) throws RDFHandlerException {
		Map<String, String> nsMap = new HashMap<String, String>();
		SesameRioUtil.writeRDFToStream(System.out, schema, nsMap, RDFFormat.N3);
	}
	
}
