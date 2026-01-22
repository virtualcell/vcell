package org.jlibsedml;

import java.io.File;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.ParsingException;

import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.xom.XOMXPath;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jlibsedml.modelsupport.ToolSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Non-API utility class.
 * @author radams
 */
public class SEDMLUtils {
    static Logger   log = LoggerFactory.getLogger(SEDMLUtils.class);
	

	/**
	 * Parses xPath string specified as arg1 in XML file specified as arg2 (using XOM and Jaxen)
	 * @param xPathExpr
	 * @param xmlFile
	 * @return
	 */
	private static String parseXPath(String xPathExpr, File xmlFile) {
		try {
			  Builder builder = new Builder();
			  nu.xom.Document document = builder.build(xmlFile);
			  
			  nu.xom.Element rootElement = document.getRootElement();
			  String defaultNamespaceURI = rootElement.getNamespaceURI();
			  String defaultNamespacePrefix = rootElement.getNamespacePrefix();
			  if ("".equals(defaultNamespacePrefix)) {
				  ToolSupport.nameSpaces_PrefixesHashMap.put("sbml", defaultNamespaceURI);
			  } else {
				  ToolSupport.nameSpaces_PrefixesHashMap.put(defaultNamespacePrefix, defaultNamespaceURI);
			  }
	
			  org.jaxen.XPath xpath = new XOMXPath(xPathExpr);
			  xpath.setNamespaceContext( new SimpleNamespaceContext(ToolSupport.nameSpaces_PrefixesHashMap));
			  Object node = xpath.selectSingleNode(document);
			  String elementId = ((nu.xom.Element)node).getAttributeValue("id");
			  log.debug("Returned node id : " + elementId);
			  
			  return elementId;
			  
			} catch ( JaxenException e) {
				// An error occurred parsing or executing the XPath
				throw new RuntimeException(e.getMessage(), e);
			} catch ( IOException e) {
			  // An error occurred opening the document
				throw new RuntimeException(e.getMessage(), e);
			} catch ( ParsingException e) {
			  // An error occurred parsing the document
				throw new RuntimeException(e.getMessage(), e);
			}
		
			
	
			
	}

	

	

	/**
	 * Reads in a SEDML (XML) file into a SedDocument
	 * @param fileName
	 * @return
	 */
	 static SedMLDataContainer readSedDocument(String fileName) {
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new File(fileName));
			Element sedRoot = doc.getRootElement();
	
			SedMLReader reader = new SedMLReader();
			
			SedMLDataContainer sedDoc = reader.getSedDocument(sedRoot);
			return sedDoc;
		} catch (Exception e) {
			throw new RuntimeException("Could not create SedMLDocument from file '" + fileName + "'", e);
		}
	}

	
	

	/** Convert entire JDOM document to prettily formatted string.
	 * @param xmlDoc
	 * @param bTrimAllWhiteSpace
	 * @return
	 */
	 static String xmlToString(Document xmlDoc, boolean bTrimAllWhiteSpace) {
		XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
		
		return xmlOut.outputString(xmlDoc);		        
	}


	

	/**
	 * Retrieve the variable specified by 'xPathString' from the XML model specified in 'modelXmlStr'.
	 * @param xPathString
	 * @param modelXmlStr
	 * @return the variable name.
	 */
	public static String getVarFromXPathStr(String xPathString, String modelXmlStr) {
	   if (xPathString != null) {
		   try {
			   // Convert the xml string for model associated with the task (referenced in the variable) to ByteArrayInputStream
			   File xmlFile = File.createTempFile("tempxml", ".xml");
			   File modelXmlFile = Libsedml.writeXMLStringToFile(modelXmlStr, xmlFile.getAbsolutePath(), true);
			   
			   // invoke parser for Xpath to get the required element from the xml document
			   String varName = parseXPath(xPathString, modelXmlFile);
			   return varName;
			   // If using JDOM
			   // Element varElement = SEDMLUtils.parseXPath(newXPathStr, modelXmlFile);
			   // return varElement.getAttributeValue("id");
			} catch (Exception e) {
				throw new RuntimeException("Error parsing Xpath string : " + e.getMessage(), e);
			}
	   }
	   return null;
	}

}
