/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.sbo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.sbpax.impl.IndexedGraph;
import org.sbpax.schemas.util.DefaultNameSpaces;
import org.sbpax.util.SesameRioUtil;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionMathMLParserModifiedForSBOReader;

public class SBOReader {
	
	public static final String SBO_NS = SBOTerm.SBO_BASE_URI;
	public static final URI PART_OF = new URIImpl(SBO_NS + "part_of");
	
	public static final String DEFAULT_SBO_PATH = "/sbo/SBO_OWL.owl";
	
	protected static ExpressionMathMLParserModifiedForSBOReader mathMLParser = 
			new ExpressionMathMLParserModifiedForSBOReader(null);

	public static Map<String, SBOTerm> readSBOFromResources() 
			throws RDFParseException, RDFHandlerException, IOException { 
		return readSBOFromResources(DEFAULT_SBO_PATH); 
	}

	public static Map<String, SBOTerm> readSBOFromResources(String path) 
			throws RDFParseException, RDFHandlerException, IOException {
		InputStream is = SBOReader.class.getResourceAsStream(path);
		Graph graph = new IndexedGraph();
		SesameRioUtil.readRDFFromStream(is, graph, 
				DefaultNameSpaces.defaultMap.convertToMap(), RDFFormat.RDFXML, 
				SBO_NS);
		return extractSBO(graph);
	}

	public static void remove(Graph graph, Resource subject, URI predicate, 
			Value object) {
		Statement statement = 
				graph.getValueFactory().createStatement(subject, predicate, object);
		graph.remove(statement);
	}
	
	public static void removeStatements(Iterator<Statement> iterator) {
		while(iterator.hasNext()) {
			iterator.next();
			iterator.remove();
		}		
	}
	
	public static void cleanReadStatements(Graph graph) {
		remove(graph, PART_OF, RDF.TYPE, OWL.TRANSITIVEPROPERTY);
		removeStatements(graph.match(null, RDFS.LABEL, null));
		removeStatements(graph.match(null, RDFS.COMMENT, null));
		removeStatements(graph.match(null, RDFS.SUBCLASSOF, null));
	}
	
	public static void showAllSBOTerms(Collection<SBOTerm> sboTerms) {
		for(SBOTerm sboTerm : sboTerms) {
			System.out.println(sboTerm.getURI());
			for(String label : sboTerm.getLabels()) {
				System.out.println("Label : " + label);
			}
			for(String comment : sboTerm.getComments()) {
				System.out.println("Comment : " + comment);
			}
			for(SBOTerm subClass : sboTerm.getSubClasses()) {
				System.out.println("Sub-class : " + subClass.getURI());				
			}
			for(SBOTerm superClass : sboTerm.getSuperClasses()) {
				System.out.println("Super-class: " + superClass.getURI());				
			}
		}
	}
	
	public static void parseMathExpression(SBOTerm sboTerm) {
		int nMathMLLabels = 0;
		for(String label : sboTerm.getLabels()) {
			if(label.contains("mathml")) {
				try {
					Expression expression = mathMLParser.fromMathML(label);
					System.out.println(expression);
					sboTerm.setExpression(expression);
				} catch (ExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				++nMathMLLabels;
			}
		}
		System.out.println(sboTerm.getIndex() + " has " + nMathMLLabels 
				+ " labels containing MathML.");
	}
	
	public static Map<String, SBOTerm> extractSBO(Graph graph) {
		HashMap<String, SBOTerm> sboTerms = new HashMap<String, SBOTerm>();
		Iterator<Statement> stmtIter1 = graph.match(null, RDF.TYPE, OWL.CLASS);
		while(stmtIter1.hasNext()) {
			Statement statement = stmtIter1.next();
			Resource subject = statement.getSubject();
			if(subject instanceof URI) {
				SBOTerm sboTerm = 
						SBOUtil.createSBOTermFromURI(((URI) subject).toString());
				sboTerms.put(sboTerm.getIndex(), sboTerm);
			}
		}
		for(Statement statement : graph) {
			Resource subject = statement.getSubject();
			if(subject instanceof URI) {
				String uri = subject.toString();
				String index = SBOUtil.getIndexFromURI(uri);
				SBOTerm sboTerm = sboTerms.get(index);
				if(sboTerm != null) {
					URI predicate = statement.getPredicate();
					if(RDFS.LABEL.equals(predicate)) {
						Value object = statement.getObject();
						if(object instanceof Literal) {
							Literal literal = (Literal) object;
							sboTerm.getLabels().add(literal.stringValue());
						}
					} else if(RDFS.COMMENT.equals(predicate)) {
						Value object = statement.getObject();
						if(object instanceof Literal) {
							Literal literal = (Literal) object;
							sboTerm.getComments().add(literal.stringValue());
						}
					} else if(RDFS.SUBCLASSOF.equals(predicate)) {
						Value object = statement.getObject();
						if(object instanceof URI) {
							URI uriObject = (URI) object;
							String indexObject = 
									SBOUtil.getIndexFromURI(uriObject.stringValue());
							SBOTerm sboTermObject = sboTerms.get(indexObject);
							sboTerm.getSubClasses().add(sboTermObject);
							sboTermObject.getSuperClasses().add(sboTerm);
						}
					}
				}
			}
		}
		cleanReadStatements(graph);
		try {
			SesameRioUtil.writeRDFToStream(System.out, graph, 
					DefaultNameSpaces.defaultMap.convertToMap(), RDFFormat.N3);
		} catch (RDFHandlerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showAllSBOTerms(sboTerms.values());
		for(SBOTerm sboTerm : sboTerms.values()) {
			parseMathExpression(sboTerm);
		}
		return sboTerms;
	}
	
	public static void main(String[] args) 
			throws RDFParseException, RDFHandlerException, IOException {
		readSBOFromResources();
	}
	
}
