/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.xml;

import java.io.File;
import java.io.StringReader;
import org.jdom.Document;
import cbit.util.xml.XmlUtil;

/**
 * This class is a light-weight XMLInfo for containing the XMLDoc's string or file; without version-related info.
 */

/**
 * Insert the type's description here.
 * Creation date: (6/13/2004 1:37:46 PM)
 * @author: Anuradha Lakshminarayana
 */
public final class XMLSource {
	private String xmlString = null;
	private File xmlFile = null;
	private transient Document xmlDoc = null;
/**
 * XMLInfo constructor comment.
 */
public XMLSource(String newXMLString) {
	super();
	xmlString = newXMLString;
}

public XMLSource(File newXMLfile) {
	super();
	xmlFile = newXMLfile;
}

/**
 * Insert the method's description here.
 * Creation date: (6/13/2004 1:50:07 PM)
 * @return java.lang.String
 */
public String getXmlString() {
	return xmlString;
}

public File getXmlFile() {
	return xmlFile;
}

public Document getXmlDoc() {
	if (xmlDoc == null) {
		generateXMLDocument();
	}
	return xmlDoc;
}

private void generateXMLDocument() {
	if (xmlFile != null) {
		xmlDoc = XmlUtil.readXML(getXmlFile());
	} else if (xmlString != null){
		xmlDoc = XmlUtil.readXML(new StringReader(xmlString), null, null, XmlUtil.SCHEMA_LOC_PROP_NAME);
	}
}

}
