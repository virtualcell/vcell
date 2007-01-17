package cbit.vcell.xml;
/**
 * This class encapsulates the transformation process.

   Update: The methods that retrieve the xslt files have been set to fail. This class is not in use. 
 * Creation date: (2/5/2002 2:41:44 PM)
 * @author: Daniel Lucio
 */
public class XmlTransformer {
    //private XML variables
    private javax.xml.transform.TransformerFactory tFactory = null;

/**
 * XmlTransformer constructor comment.
 */
public XmlTransformer() {
    super();
    //***** prepare XML support ******
    //get a transformer factory (TRAX)
    tFactory = javax.xml.transform.TransformerFactory.newInstance();
}


/**
 * This method is just a convenient wrapper for the XSLT processor.
 * Creation date: (1/30/2002 4:44:25 PM)
 * @return java.lang.String
 * @param source java.lang.String
 */
public String transform(cbit.vcell.xml.XmlDialect xmldialectArg, String xmlArg)
    throws XmlParseException {
    String resultXml = null;

    if (xmldialectArg == null) {
	    throw new XmlParseException("Invalid Null dialect!");
    }

    if (xmldialectArg.equals(XmlDialect.VCML)) {
	    //just return it
        return xmlArg;
    } else {
	    //call the XSLT processor
	    resultXml= this.transform("Not used for now", xmlArg);
    }

    return resultXml;
}


/**
 * This method actually performs the XSLT transformation.
 * Creation date: (1/30/2002 4:44:25 PM)
 * @return java.lang.String
 * @param source java.lang.String
 */
private String transform(String xsltName, String xmlArg) throws XmlParseException {
	//get transformer that will work with the given styleshet
    javax.xml.transform.Transformer transformer = null;

    try {
    	transformer = tFactory.newTransformer(new javax.xml.transform.stream.StreamSource( this.getClass().getResourceAsStream(xsltName) ) );
    } catch (javax.xml.transform.TransformerConfigurationException e) {
	    System.out.println("A problem occurred when trying to create the tranformer!");
	    e.printStackTrace();
	    throw new XmlParseException(e.getMessage());
    }
    
	//** apply the transformation **
	//prepare the streams
	java.io.StringReader stringReader = new java.io.StringReader(xmlArg);
	java.io.StringWriter stringWriter = new java.io.StringWriter();
	//
	try {
	    transformer.transform(new javax.xml.transform.stream.StreamSource(stringReader),new javax.xml.transform.stream.StreamResult(stringWriter));		
	} catch (javax.xml.transform.TransformerException e) {
		System.out.println("A problem occurred when applying the transformation.");
		e.printStackTrace();
		throw new XmlParseException(e.getMessage());
	}

	//return the result
	return stringWriter.toString();
}


/**
 * This a convenient JDOM wrapper for SAXON.
 * Creation date: (5/8/2003 3:12:35 PM)
 * @return org.jdom.Document
 * @param sourceDocument org.jdom.Document
 * @param dialect cbit.vcell.xml.XmlDialect
 */
public org.jdom.Document transform(org.jdom.Document sourceDocument, XmlDialect dialect) throws XmlParseException{
	//create a wrapper
	com.icl.saxon.jdom.DocumentWrapper docw = new com.icl.saxon.jdom.DocumentWrapper(sourceDocument, sourceDocument.getRootElement().getNamespaceURI());
	com.icl.saxon.om.NamePool pool = com.icl.saxon.om.NamePool.getDefaultNamePool();
	docw.setNamePool(pool);

	//Create a transformer
	System.setProperty("javax.xml.transform.TransformerFactory", "com.icl.saxon.TransformerFactoryImpl");
	javax.xml.transform.TransformerFactory tfactory = javax.xml.transform.TransformerFactory.newInstance();
	javax.xml.transform.Transformer transformer = null;
	
	try {
		javax.xml.transform.Templates templates = tfactory.newTemplates(new javax.xml.transform.stream.StreamSource(this.getClass().getResourceAsStream("Not used for now")));
		transformer = templates.newTransformer();
	} catch (javax.xml.transform.TransformerConfigurationException e) {
		e.printStackTrace();
		throw new XmlParseException("Error creating a XSLT transformer!"+" : "+e.getMessage());
	}
	//Apply the transformation
	java.io.StringWriter writer = new java.io.StringWriter();
	
	try {
		transformer.transform(docw, new javax.xml.transform.stream.StreamResult(writer));
	} catch (javax.xml.transform.TransformerException e) {
		e.printStackTrace();
		throw new XmlParseException("Error applying the transformation!\n"+e.getMessage()+"\n"+e.getLocationAsString());
	}

	//return a JDOM document
	org.jdom.Document resultDocument = null;
	
	try {
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder("com.icl.saxon.aelfred.SAXDriver");
		resultDocument = builder.build(new java.io.StringReader(writer.toString()));
	} catch (java.io.IOException e) {
		e.printStackTrace();
		throw new XmlParseException("IO error dumping a document in a string into a JDom document!"+" : "+e.getMessage());
	} catch (org.jdom.JDOMException e) {
		e.printStackTrace();
		throw new XmlParseException("Error dumping a document in a string into a JDom document!"+" : "+e.getMessage());
	}
 
	return resultDocument;
}
}