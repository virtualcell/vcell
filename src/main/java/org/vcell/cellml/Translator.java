/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.cellml;
import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlUtil;

import org.jdom.Document;
import org.jdom.Element; 
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter; 
import org.vcell.util.document.VCDocument;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public abstract class Translator {

	//supported translations
//	public static final String VC_QUAL_CELL = "VCQualCell";
//	public static final String VC_QUAN_CELL = "VCQuanCell";
//	public static final String CELL_QUAL_VC = "CellQualVC";
//	public static final String CELL_QUAN_VC = "CellQuanVC";
	
	public static final String MATHML_NS = "http://www.w3.org/1998/Math/MathML";
	public static final String XHTML_NS = "http://www.w3.org/1999/xhtml";
	//public static final String CELLML_NS = "http://www.cellml.org/cellml/1.0#";
	public static final String CELLML_NS_PREFIX = "cellml";
	//schema instance
	protected static final String XML_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";

	//default schema Locations
	protected static final String DEF_VCML_SL = "http://www.nrcam.uchc.edu/xml/biomodel.xsd";
	protected static final String DEF_CELLML_SL = "http://www.nrcam.uchc.edu/xml/cellml.xsd";   		//for the future.
	
	protected String schemaLocation = null;
	protected String schemaLocationPropName = null;
	protected VCLogger vcLogger = null;
	protected Element sRoot = null;
	protected Element tRoot = null;
	
  	//needed for the test suite
  	protected Document getSource() throws IllegalStateException {

		return new Document((Element)sRoot.clone()); //return a copy of it  	
  	}


	public VCLogger getVCLogger() {

		return vcLogger;
	}


	private void print(Writer outStream, String printedDoc) throws IllegalStateException {
		
		if ( (printedDoc.equals("source") && sRoot == null) ||
			 (printedDoc.equals("target") && tRoot == null) )
		    throw new IllegalStateException ("Nothing to print.");
	    XMLOutputter xmlOut = new XMLOutputter();
	    // xmlOut.setNewlines(true);
	  	try {
		  	if (printedDoc.equals("source")) {
		    	xmlOut.getFormat().setTextMode(Format.TextMode.TRIM_FULL_WHITE);
		        xmlOut.output(sRoot, outStream);		        
		  	} else { 
		        xmlOut.output(tRoot, outStream);
		  	}
	  	} catch (IOException e) {
			System.err.println("Unable to write out XML file.");
			e.printStackTrace();
	  	}
	}


	public void printSource(Writer outStream) throws IllegalStateException {

		print(outStream, "source");
	}


	public void printTarget(Writer outStream) throws IllegalStateException {

		print(outStream, "target");
	}


	public void setVCLogger (VCLogger newVCLogger) {

		if (newVCLogger == null) {
			throw new IllegalArgumentException("Invalid TranslationMessager: " + newVCLogger);
		}
		vcLogger = newVCLogger;
	}


	protected abstract VCDocument translate() throws Exception;


  	public VCDocument translate (Reader reader, boolean validationOn) throws Exception {

		if (validationOn) {
      		this.sRoot = (XmlUtil.readXML(reader, schemaLocation, null, schemaLocationPropName)).getRootElement();
      		String errorLog = XmlUtil.getErrorLog();
      		if (errorLog.length() > 0) {
				System.err.println(errorLog);
				if (vcLogger != null) {
					vcLogger.sendMessage(VCLogger.Priority.LowPriority, VCLogger.ErrorType.SchemaValidation);
					//vcLogger.sendMessage(VCLogger.LOW_PRIORITY, TranslationMessage.SCHEMA_VALIDATION_ERROR, 
					//                          "The source model has invalid elements/attributes:\n" + errorLog)
				}
      		}
		} else {
			this.sRoot = (XmlUtil.readXML(reader, null, null, null)).getRootElement();
		} 
  		VCDocument vcDoc = translate();
		if (vcLogger != null && vcLogger.hasMessages()) {
			vcLogger.sendAllMessages();
		}
  		return vcDoc;
    }
}
