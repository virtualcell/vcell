/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.rdf;

/*   StringUtil  --- by Oliver Ruebenacker, UCHC --- September 2008
 *   Utilities for Jena model input and output
 */

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class JenaIOUtil {
	
	public static Model modelFromText(String text, String uri) {
		Model model = ModelFactory.createDefaultModel();
		StringReader strReader = new StringReader(text);
		model.read(strReader, uri);
		return model;
	}
	
	public static Model modelFromText(Model model, String text, String uri) {
		StringReader strReader = new StringReader(text);
		model.read(strReader, uri);
		return model;
	}
	
	public static void writeModel(Model model, Writer writer, RDFFormat style) {
		model.write(writer, style.toString());
	}

	public static String textFromModel(Model model, RDFFormat style) {
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StringWriter strWriter = new StringWriter();
		model.write(strWriter, style.toString());
		return strWriter.getBuffer().toString();
	}

}
