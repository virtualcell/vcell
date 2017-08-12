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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;

import org.jdom.Document;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionableType;

/**
 * Insert the type's description here.
 * Creation date: (6/13/2004 1:37:46 PM)
 * @author: Anuradha Lakshminarayana
 */
public class ExternalDocInfo implements org.vcell.util.document.VCDocumentInfo{
	private String textContents = null;
	private File file = null;
	private transient Boolean bXML = null;
	private boolean isBioModelsNet = false;
	private String defaultName;
/**
 * XMLInfo constructor comment.
 */
public ExternalDocInfo(String textContents) {
	this.textContents = textContents;
	this.file = null;
	this.defaultName = null;
}

public ExternalDocInfo(File file) {
	this.textContents = null;
	this.file = file;
	this.defaultName = null;
}

public ExternalDocInfo(String textContents,String defaultName) {
	this.textContents = textContents;
	this.file = null;
	this.defaultName = defaultName;
}

private ExternalDocInfo(String textContents,String defaultName,boolean isBioModelsNet){
	this.textContents = textContents;
	this.file = null;
	this.defaultName = defaultName;
	this.isBioModelsNet = isBioModelsNet;
}
public static ExternalDocInfo createBioModelsNetExternalDocInfo(String textContents,String defaultName){
	return new ExternalDocInfo(textContents, defaultName,true);
}
public boolean isBioModelsNet(){
	return isBioModelsNet;
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.Version
 */
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
public final File getFile() {
	return file;
}
public VCellSoftwareVersion getSoftwareVersion() {
	return null;
}

public boolean isXML() {
	if (bXML != null){
		return bXML;
	}
	try {
		createXMLSource();
	}catch (Exception e){
		System.out.println(e.getMessage());
	}
	return bXML;
}

public XMLSource createXMLSource() throws Exception {
	XMLSource xmlSource = null;
	if (textContents!=null){
		xmlSource = new XMLSource(textContents);
	}else if (file!=null){
		xmlSource = new XMLSource(file);
	}
	if (bXML != null && bXML){ // if previously successful, no need to parse again.
		return xmlSource;
	}
	try {
		Document document = xmlSource.getXmlDoc(); // throws an exception if not a well formed XML document
		bXML = true;
		return xmlSource;
	}catch (Exception e){
		bXML = false;
		System.out.println(e.getMessage());
		throw e;
	}
}

public Reader getReader() {
	if (file!=null){
		try {
			return new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(),e);
		}
	}else if (textContents!=null){
		return new StringReader(textContents);
	}else{
		throw new RuntimeException("neither file not textContents set for this ExternalDocInfo");
	}
}

@Override
public VCDocumentType getVCDocumentType() {
	return VCDocument.VCDocumentType.EXTERNALFILE_DOC;
}
}
