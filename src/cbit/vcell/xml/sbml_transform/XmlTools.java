/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

/**
 * 
 */
package cbit.vcell.xml.sbml_transform;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;


/** Contains static methods for DOM operations
 * @author mlevin
 *
 */
public class XmlTools {

	private XmlTools() {}
	
	public static void serialize(Document doc, OutputStream os) {
		DOMSource domSrc = new DOMSource(doc);
		StreamResult sr = new StreamResult(os);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer ser = null;
		try {
			ser = tf.newTransformer();
			ser.transform(domSrc, sr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static Document parseDom(CharSequence str) {
		InputStream is = new ByteArrayInputStream(str.toString().getBytes());
		Document doc = parseDom(is);
		return doc;
	}

	/** Warning: not validating
	 * @param sbml
	 * @return
	 */
	public static Document parseDom(InputStream sbml) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
//			factory.setValidating(true);
//			factory.setNamespaceAware(true);
//			Schema schema = getSbmlSchema();
//			factory.setSchema(schema);
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(sbml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}
}
