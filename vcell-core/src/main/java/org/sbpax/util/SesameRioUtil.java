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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterRegistry;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.rio.rdfxml.RDFXMLWriterFactory;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriterFactory;

public class SesameRioUtil {

	public static RDFParser createRDFParser(Graph graph, Map<String, String> nsMap, RDFFormat format) {
		RDFParser parser = Rio.createParser(format);
		StatementCollector statementCollector = new StatementCollector(graph, nsMap);
		parser.setRDFHandler(statementCollector);
		return parser;
	}
	
	public static void readRDFFromStream(InputStream is, Graph graph, Map<String, String> nsMap, RDFFormat format, String baseURI) 
	throws IOException, RDFParseException, RDFHandlerException {
		RDFParser parser = createRDFParser(graph, nsMap, format);
		parser.parse(is, baseURI);
	}
	
	public static void readRDFFromReader(InputStream reader, Graph graph, Map<String, String> nsMap, RDFFormat format, String baseURI) 
	throws IOException, RDFParseException, RDFHandlerException {
		RDFParser parser = createRDFParser(graph, nsMap, format);
		parser.parse(reader, baseURI);
	}
	
	public static void readRDFFromString(String string, Graph graph, Map<String, String> nsMap, RDFFormat format, String baseURI) 
	throws IOException, RDFParseException, RDFHandlerException {
		Reader reader = new StringReader(string);
		RDFParser parser = createRDFParser(graph, nsMap, format);
		parser.parse(reader, baseURI);
	}
	
	public static void writeRDFToStream(OutputStream os, Graph graph, Map<String, String> nsMap, RDFFormat format) 
	throws RDFHandlerException {
		RDFWriter rdfWriter = Rio.createWriter(format, os);
		writeRDFToRDFWriter(rdfWriter, graph, nsMap);
	}

	public static void writeRDFToWriter(Writer writer, Graph graph, Map<String, String> nsMap, RDFFormat format) 
	throws RDFHandlerException {
		RDFWriter rdfWriter = Rio.createWriter(format, writer);
		writeRDFToRDFWriter(rdfWriter, graph, nsMap);
	}

	public static String writeRDFToString(Graph graph, Map<String, String> nsMap, RDFFormat format) 
	throws RDFHandlerException {
		StringWriter stringWriter = new StringWriter();
		RDFWriter rdfWriter = Rio.createWriter(format, stringWriter);
		writeRDFToRDFWriter(rdfWriter, graph, nsMap);
		return stringWriter.getBuffer().toString();
	}
		
	public static void setWriteRDFXMLPretty(boolean writeRDFXMLPretty) {
		if(writeRDFXMLPretty) {
			RDFWriterRegistry.getInstance().add(new RDFXMLPrettyWriterFactory());			
		} else {
			RDFWriterRegistry.getInstance().add(new RDFXMLWriterFactory());
		}
	}

	static { setWriteRDFXMLPretty(true); }
	
	private static void writeRDFToRDFWriter(RDFWriter rdfWriter, Graph graph,
			Map<String, String> nsMap) throws RDFHandlerException {
		rdfWriter.startRDF();
		for(Map.Entry<String, String> entry : nsMap.entrySet()) {
			rdfWriter.handleNamespace(entry.getKey(), entry.getValue());
		}
		for(Statement statement : graph) {
			rdfWriter.handleStatement(statement);			
		}
		rdfWriter.endRDF();
	}

}
