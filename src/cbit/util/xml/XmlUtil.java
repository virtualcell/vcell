package cbit.util.xml;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionMathMLParser;
import cbit.vcell.parser.MathMLTags;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * General Xml utility methods.
 * Creation date: (5/8/2003 12:48:40 PM)
 * @author: Daniel Lucio
 */
public class XmlUtil {
		
	//some SAX parsers...
	public static final String PARSER_XERCES = "org.apache.xerces.parsers.SAXParser";
	public static final String PARSER_CRIMSON = "org.apache.crimson.parser.XMLReaderImpl";
	public static final String PARSER_AELFRED = "com.icl.saxon.aelfred.SAXDriver";        	//"net.sf.saxon.aelfred.SAXDriver"; //old EOFException problem...
	public static final String PARSER_PICCOLO = "com.bluecast.xml.Piccolo";
	//public static final String PARSER_XPP = "org.xmlpull.v1.sax2.Driver";
	
	public static final String SCHEMA_LOC_PROP_NAME = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
	public static final String NS_SCHEMA_LOC_PROP_NAME = "http://apache.org/xml/properties/schema/external-schemaLocation";

	private static String errorLog = "";

	public static String getErrorLog() {

		return errorLog;
	}

/**
 * no instances allowed
 */
private XmlUtil() {
	super();
}


	public static String getXMLString(String fileName) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String temp;
		StringBuffer buf = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			buf.append(temp);
			buf.append("\n");
		}
		
		return buf.toString();
	}
	
	public static void writeXMLString(String xmlString, String filename) throws IOException {
		File outputFile = new File(filename);
		java.io.FileWriter fileWriter = new java.io.FileWriter(outputFile);
		fileWriter.write(xmlString);
		fileWriter.flush();
		fileWriter.close();
	}


//useful for the translators.
	public static Element readXML(Reader reader, String schemaLocation, String parserClass, String schemaLocationPropName) throws RuntimeException {
		
		SAXBuilder builder = null; 
		Element root = null;
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
	  		Document sDoc = builder.build(reader);
	  		root = sDoc.getRootElement();
	  		// flush/replace previous error log with every read.
	  		String errorHandlerLog = errorHandler.getErrorLog();
	  		if (errorHandlerLog.length() > 0) {
				System.out.println(errorHandlerLog);
				XmlUtil.errorLog = errorHandlerLog;
	  		} else {
				XmlUtil.errorLog = "";                     
	  		}
		} catch (JDOMException e) { 
        	e.printStackTrace();
        	throw new RuntimeException("source document is not well-formed\n"+e.getMessage());
    	} catch (IOException e) { 
        	e.printStackTrace();
        	throw new RuntimeException("Unable to read source document\n"+e.getMessage());
    	}

    	return root;
	}


/**
 * This method is used to set the Default Namespace to the XML document represented by 'rootNode'.
 * Creation date: (5/8/2003 12:51:03 PM)
 * @return org.jdom.Element
 * @param rootNode org.jdom.Element
 * @param namespace org.jdom.Namespace
 * @exception cbit.vcell.xml.XmlParseException The exception description.
 */
public static org.jdom.Element setDefaultNamespace(org.jdom.Element rootNode, org.jdom.Namespace namespace) {
	//only if there is a node and it has no default namespace!
	if (rootNode!=null & rootNode.getNamespaceURI().length()==0) {
		//set namespace for this node
		rootNode.setNamespace(namespace);
		
    	java.util.Iterator childIterator = rootNode.getChildren().iterator();
    	
    	while (childIterator.hasNext()) {
    		org.jdom.Element child = (org.jdom.Element)childIterator.next();
    		//check children
    		setDefaultNamespace(child, namespace);
    	}
	}
	
	return rootNode;
}


	public static Element stringToXML(String str, String schemaLocation) throws RuntimeException { 

		return stringToXML(str, schemaLocation, null);
	}


	public static Element stringToXML(String str, String schemaLocation, String parserClass) throws RuntimeException { 

		return readXML(new StringReader(str), schemaLocation, parserClass, XmlUtil.SCHEMA_LOC_PROP_NAME);
	}


//utility method with default settings. 
	public static String xmlToString(Element root) {		
		return xmlToString(root,false);		        
	}
	
	public static String xmlToString(Element root,boolean bTrimAllWhiteSpace) {
		XMLOutputter xmlOut = new XMLOutputter("   ");
	    xmlOut.setNewlines(true);
		xmlOut.setTrimAllWhite(bTrimAllWhiteSpace);		
		return xmlOut.outputString(root);		        
	}

}