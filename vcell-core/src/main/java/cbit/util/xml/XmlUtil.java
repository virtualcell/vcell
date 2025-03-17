/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.util.xml;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.xml.XmlReader;
import cbit.vcell.xml.Xmlproducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * General Xml utility methods.
 * Creation date: (5/8/2003 12:48:40 PM)
 * @author: Daniel Lucio
 */
public class XmlUtil {
	private final static Logger lg = LogManager.getLogger(XmlUtil.class);
		
	//some SAX parsers...
	public static final String PARSER_XERCES = "org.apache.xerces.parsers.SAXParser";
	public static final String PARSER_CRIMSON = "org.apache.crimson.parser.XMLReaderImpl";
	public static final String PARSER_AELFRED = "com.icl.saxon.aelfred.SAXDriver";        	//"net.sf.saxon.aelfred.SAXDriver"; //old EOFException problem...
	public static final String PARSER_PICCOLO = "com.bluecast.xml.Piccolo";
	//public static final String PARSER_XPP = "org.xmlpull.v1.sax2.Driver";
	
	public static final String SCHEMA_LOC_PROP_NAME = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
	public static final String NS_SCHEMA_LOC_PROP_NAME = "http://apache.org/xml/properties/schema/external-schemaLocation";

	private static String errorLog = "";
	
	private static Transformer transformer = null;
	
	private static Transformer getTransformer( ) throws TransformerConfigurationException, TransformerFactoryConfigurationError {
		if (transformer != null) {
			return transformer;
		}
		transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		return transformer;
	}

	public static String getErrorLog() {

		return errorLog;
	}

/**
 * no instances allowed
 */
private XmlUtil() {
	super();
}


/**
 * read stuff from file ; not necessarily XML
 * @param fileName
 * @return
 * @throws IOException
 */
	public static String getXMLString(String fileName) throws IOException {

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try{
			File inFile = new File(fileName);
			byte[] stringBytes = new byte[(int)inFile.length()];
			fis = new FileInputStream(inFile);
			bis = new BufferedInputStream(fis);
			int readCount = 0;
			while((readCount+= bis.read(stringBytes, readCount, stringBytes.length-readCount)) != stringBytes.length){}
			return new String(stringBytes);
		}finally{
			try{
				if(bis != null){bis.close();}
			}catch(Exception e){
				lg.error(e.getMessage(), e);
				//ignore so any original Exception is passed
			}
		}
	}

	/**
	 * write any string to file
	 * @param xmlString
	 * @param filename
	 * @param bUseUTF8
	 * @throws IOException
	 */
	public static void writeXMLStringToFile(String xmlString, String filename, boolean bUseUTF8) throws IOException {
		File outputFile = new File(filename);
		OutputStreamWriter fileOSWriter = null;
		if (bUseUTF8){
			fileOSWriter = new OutputStreamWriter(new FileOutputStream(outputFile),"UTF-8");
		}else{
			fileOSWriter = new OutputStreamWriter(new FileOutputStream(outputFile));
		}
		fileOSWriter.write(xmlString);
		fileOSWriter.flush();
		fileOSWriter.close();
	}


//useful for the translators.
	public static Document readXML(Reader reader, String schemaLocation, String parserClass, String schemaLocationPropName) throws RuntimeException {
		
		SAXBuilder builder = null; 
		Document sDoc = null;
		GenericXMLErrorHandler errorHandler = new GenericXMLErrorHandler();
		try {
			if (schemaLocation != null && schemaLocation.length() > 0) {           //ignores the parserClass, since xerces is the only validating parser we have
		  		builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser", true);
		  		builder.setFeature("http://xml.org/sax/features/validation", true); 
	      		builder.setFeature("http://apache.org/xml/features/validation/schema", true);
	      		builder.setErrorHandler(errorHandler);
				builder.setProperty(schemaLocationPropName, schemaLocation);
			} else {                                                              //ignore schemaLocationPropName
				if (parserClass == null) {
					builder = new SAXBuilder(false);                            //not necessarily 'xerces'
				} else {
					builder = new SAXBuilder(parserClass, false);
				}
				builder.setErrorHandler(errorHandler);
			}
	  		sDoc = builder.build(reader);
			// ----- Element root = null;
	  		// ----- root = sDoc.getRootElement();
	  		// flush/replace previous error log with every read.
	  		String errorHandlerLog = errorHandler.getErrorLog();
	  		if (errorHandlerLog.length() > 0) {
				System.out.println(errorHandlerLog);
				XmlUtil.errorLog = errorHandlerLog;
	  		} else {
				XmlUtil.errorLog = "";                     
	  		}
		} catch (JDOMException e) {
        	throw new RuntimeException("source document is not well-formed\n"+e.getMessage(), e);
    	} catch (IOException e) {
        	throw new RuntimeException("Unable to read source document\n"+e.getMessage(), e);
    	}

    	return sDoc;
	}
	
	public static Document readXML(File file) throws RuntimeException {
		
		SAXBuilder builder = new SAXBuilder(false);                           
		Document sDoc = null;
		GenericXMLErrorHandler errorHandler = new GenericXMLErrorHandler();
  		builder.setErrorHandler(errorHandler);
		try {
	  		sDoc = builder.build(file);
			// Element root = null;
	  		// root = sDoc.getRootElement();
	  		// flush/replace previous error log with every read.
	  		String errorHandlerLog = errorHandler.getErrorLog();
	  		if (errorHandlerLog.length() > 0) {
				System.out.println(errorHandlerLog);
				XmlUtil.errorLog = errorHandlerLog;
	  		} else {
				XmlUtil.errorLog = "";                     
	  		}
		} catch (JDOMException e) {
        	throw new RuntimeException("source document is not well-formed\n"+e.getMessage(), e);
    	} catch (IOException e) {
        	throw new RuntimeException("Unable to read source document\n"+e.getMessage(), e);
    	}

    	return sDoc;
	}
	
	public static Document readXML(InputStream inputStream) throws RuntimeException {
		
		SAXBuilder builder = new SAXBuilder(false);                           
		Document sDoc = null;
		GenericXMLErrorHandler errorHandler = new GenericXMLErrorHandler();
  		builder.setErrorHandler(errorHandler);
		try {
	  		sDoc = builder.build(inputStream);
			// Element root = null;
	  		// root = sDoc.getRootElement();
	  		// flush/replace previous error log with every read.
	  		String errorHandlerLog = errorHandler.getErrorLog();
	  		if (errorHandlerLog.length() > 0) {
				System.out.println(errorHandlerLog);
				XmlUtil.errorLog = errorHandlerLog;
	  		} else {
				XmlUtil.errorLog = "";                     
	  		}
		} catch (JDOMException e) {
        	throw new RuntimeException("source document is not well-formed\n"+e.getMessage(), e);
    	} catch (IOException e) {
        	throw new RuntimeException("Unable to read source document\n"+e.getMessage(), e);
    	}

    	return sDoc;
	}
	

/**
 * This method is used to set the Default Namespace to the XML document represented by 'rootNode'.
 * Creation date: (5/8/2003 12:51:03 PM)
 * @return Element
 * @param rootNode Element
 * @param namespace Namespace
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public static Element setDefaultNamespace(Element rootNode, Namespace namespace) {
	//only if there is a node and it has no default namespace!
	if (rootNode!=null && rootNode.getNamespaceURI().length()==0) {
		//set namespace for this node
		rootNode.setNamespace(namespace);
		
    	java.util.Iterator<?> childIterator = rootNode.getChildren().iterator();
    	
    	while (childIterator.hasNext()) {
    		Element child = (Element)childIterator.next();
    		//check children
    		setDefaultNamespace(child, namespace);
    	}
	}
	
	return rootNode;
}


	public static Document stringToXML(String str, String schemaLocation) throws RuntimeException { 

		return stringToXML(str, schemaLocation, null);
	}


	public static Document stringToXML(String str, String schemaLocation, String parserClass) throws RuntimeException { 

		return readXML(new StringReader(str), schemaLocation, parserClass, XmlUtil.SCHEMA_LOC_PROP_NAME);
	}


//utility method with default settings. 
	public static String xmlToString(Element root) {		
		return xmlToString(root,false);		        
	}
	
	/**
	 * @param doc non-null
	 * @param writer non-null
	 * @param bTrimAllWhiteSpace remove whitespace from output
	 * @throws IOException
	 */
	public static void writeXml(Document doc, Writer writer, boolean bTrimAllWhiteSpace) throws IOException 
	{
		Objects.requireNonNull(doc);
		Objects.requireNonNull(writer);
		
		XMLOutputter xmlOut = new XMLOutputter();
		Format f = Format.getPrettyFormat();
	    if (bTrimAllWhiteSpace) {
	    	f.setTextMode(Format.TextMode.TRIM_FULL_WHITE);
	    } 
		xmlOut.setFormat(f);
		xmlOut.output(doc, writer);
	}
	
	public static void writeXmlToStream(Element root, boolean bTrimAllWhiteSpace, OutputStream outStream) throws IOException
	{
		XMLOutputter xmlOut = new XMLOutputter();
	    // default newline in jdom 1.1.3 is '\r\n' : xmlOut.setNewlines(true);
	    if (bTrimAllWhiteSpace) {
	    	xmlOut.getFormat().setTextMode(Format.TextMode.TRIM_FULL_WHITE);
	    } 
		xmlOut.output(root, outStream);
	}
	
	public static String xmlToString(Element root,boolean bTrimAllWhiteSpace) {
		XMLOutputter xmlOut = new XMLOutputter();
	    // default newline in jdom 1.1.3 is '\r\n' :  xmlOut.setNewlines(true);
		xmlOut.setFormat(Format.getPrettyFormat());
		if (bTrimAllWhiteSpace) {
	    	xmlOut.getFormat().setTextMode(Format.TextMode.TRIM_FULL_WHITE);
	    } 
		return xmlOut.outputString(root);		        
	}

	public static String xmlToString(Document xmlDoc, boolean bTrimAllWhiteSpace) {
		XMLOutputter xmlOut = new XMLOutputter();
		// default newline in jdom 1.1.3 is '\r\n' : xmlOut.setNewlines(true);
		xmlOut.setFormat(Format.getPrettyFormat());
	    if (bTrimAllWhiteSpace) {
	    	xmlOut.getFormat().setTextMode(Format.TextMode.TRIM_FULL_WHITE);
	    } 
		return xmlOut.outputString(xmlDoc);		        
	}
	/**
	 * attempt pretty print XML String into nicely formatted XML
	 * @param xmlInput input string
	 * @return nicely formatted String or original string if there is transformation exception
	 */
	public static String beautify(String xmlInput) {
		try {
			Transformer tfr;
			tfr = getTransformer();
			StringWriter sw  = new StringWriter();
			StreamResult sr = new StreamResult(sw);
			StreamSource src = new StreamSource(new StringReader(xmlInput));
			tfr.transform(src, sr);
			return sw.toString();
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			lg.error(e);
		}
		return xmlInput;
	}
	/**
	 * get elements of specified type from collection
	 * @param lst List to process (may not be null)
	 * @param clzz desired type (may not be null)
	 * @return lst properly cast or new List of filtered types
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> filterList(List<?> lst, Class<T> clzz) {
		assert(lst != null);
		assert(clzz != null);
		boolean allGood = true;
		for  (Object o : lst) {
			if (!clzz.isAssignableFrom(o.getClass())) {
				allGood = false;
				break;
			}
		}
		if (allGood) {
			return (List<T>) lst;
		}
		List<T> rval = new ArrayList<T>(lst.size( ) );
		for  (Object o : lst) {
			if (clzz.isAssignableFrom(o.getClass())) {
				rval.add((T) o);
			}
		}
		return rval;
	}
	
	/**
	 * get children  of specified type from Element 
	 * @param e parent (may not be null)
	 * @param name of children (may not be null)
	 * @param clzz desired type (may not be null)
	 */
	public static <T> List<T> getChildren(Element e, String name, Class<T> clzz) {
		assert(e != null);
		assert(name != null);
		assert(clzz != null);
		return filterList(e.getChildren(name),clzz);
	}

	public static Geometry cloneGeometry(Geometry geometry) throws XmlParseException {
		Xmlproducer xmlproducer = new Xmlproducer(true);
		XmlReader xmlReader = new XmlReader(true);
		return xmlReader.getGeometry(xmlproducer.getXML(geometry));
	}
}
