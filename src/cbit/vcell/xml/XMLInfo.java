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

import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionableType;

/**
 * Insert the type's description here.
 * Creation date: (6/13/2004 1:37:46 PM)
 * @author: Anuradha Lakshminarayana
 */
public class XMLInfo extends XMLSource implements VCDocumentInfo{
	private String defaultName;
/**
 * XMLInfo constructor comment.
 */
public XMLInfo(String newXMLString) {
	super(newXMLString);
}

public XMLInfo(File newXMLfile) {
	super(newXMLfile);
}

public XMLInfo(String newXMLString,String defaultName) {
	super(newXMLString);
	this.defaultName = defaultName;
}

public Version getVersion() {
	// throw new RuntimeException("Not yet implemented!!");
	return new Version("Unnamed Version", new org.vcell.util.document.User("vcell", new org.vcell.util.document.KeyValue("123")));
}

public VersionableType getVersionType() {	
	return null;
}
public String getDefaultName(){
	return defaultName;
}

public VCellSoftwareVersion getSoftwareVersion() {
	return null;
}

public int getVCDocumentType(){
	return VCDocument.XML_DOC;
}

}
