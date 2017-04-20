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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;


/** Contains static methods for DOM operations
 * @author mlevin
 *
 */
public class XmlTools {

	private XmlTools() {}
	
	

	/** schema location is wrong
	 *  not used currently
	 * @return
	 */
	public static Schema getSbmlSchema() {
		URL xsdUrl = ClassLoader.getSystemResource("cbit/vcell/xml/sbml-l2v3.xsd");
		SchemaFactory schF = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema sbmlXsd;
		try {
		sbmlXsd = schF.newSchema(xsdUrl);
		} catch( Exception e) {
			throw new RuntimeException("error loading schema");
		}
		return sbmlXsd;
	}
	
	/** not used
	 * @param sbml
	 * @return
	 */
	public static boolean validate(InputStream sbml) {
		try {
			Validator validator = getSbmlSchema().newValidator();
			InputSource is = new InputSource(sbml);
			SAXSource ss = new SAXSource(is);
			validator.validate( ss );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
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

	/** was used for troubleshooting; no longer
	 * @param fileName
	 * @return
	 */
	public static String readFileAsString(String fileName) {
		File fn = new File(fileName);
		return readFileAsString(fn);
	}
	
	public static String readFileAsString(File file) {
		final int BUFF = 1024;
		StringBuffer fileData = new StringBuffer(BUFF);
		
		try {
			FileReader fr = new FileReader(file);
			BufferedReader reader = new BufferedReader( fr );
			char[] buf = new char[BUFF];
			int numRead=0;
			while( (numRead=reader.read(buf)) != -1){
				fileData.append(buf, 0, numRead);
			}
			reader.close();
		} catch(IOException e) {
			e.printStackTrace();
			String msg = "couldn't read file '" + file + "'";
			throw new RuntimeException(msg, e);
		}
		
		return fileData.toString();
	}
	
}
