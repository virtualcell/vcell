/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.annotate;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.openrdf.model.Graph;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;

/*   JDOM2Model  --- May 2009
 *   Add RDF from JDOM elements representing RDF/XML
 *   Last change: Oliver Ruebenacker
 */

public class JDOM2Model {

	protected Graph model;
	
	public JDOM2Model(Graph model) { this.model = model; }
	
	public Graph model() { return model; }
	
	public void addJDOM(Element element, String baseURI) throws IOException, RDFParseException, RDFHandlerException {
		Document document = new Document((Element) element.clone());
		StringWriter stringWriter = new StringWriter();
		new XMLOutputter().output(document, stringWriter);
		StringReader stringReader = new StringReader(stringWriter.getBuffer().toString());
		RDFParser rdfParser = Rio.createParser(RDFFormat.RDFXML);
		rdfParser.setRDFHandler(new StatementCollector(model));
		rdfParser.parse(stringReader, baseURI);
	}
	
}
