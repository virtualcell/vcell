/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server.bionetgen;
import java.io.StringBufferInputStream;
import java.util.HashSet;

import org.jdom.Document;
import org.vcell.util.BigString;

import cbit.util.xml.XmlUtil;
import cbit.vcell.bionetgen.BNGOutputFileParser;
/**
 * Insert the type's description here.
 * Creation date: (6/27/2005 2:59:47 PM)
 * @author: Anuradha Lakshminarayana
 */
public class BNGOutput implements java.io.Serializable {
	public final static String BNG_NET_FILE_SUFFIX = ".net";
	public final static String BNG_NFSIMXML_FILE_SUFFIX = ".xml";
	private org.vcell.util.BigString consoleOutput;
	private org.vcell.util.BigString[] bng_fileContents;
	private String[] bng_filenames;

/**
 * BNGOutput constructor comment.
 */
public BNGOutput(String argConsoleOutput, String[] filenames, String[] filecontents) {
	super();
	consoleOutput = new BigString(argConsoleOutput);
	if (filenames.length != filecontents.length) {
		throw new RuntimeException("The lengths of filenames and filecontents don't match");
	}
	bng_filenames = filenames;
	bng_fileContents = new BigString[filecontents.length];
	for (int i = 0; i < filecontents.length; i ++) {
		bng_fileContents[i] = new BigString(filecontents[i]);
	}		
}


public String getNetFileContent() {
	HashSet<Integer> netFileIndices = new HashSet<Integer>();
	for (int i=0;i<bng_filenames.length;i++){
		if (bng_filenames[i].toLowerCase().endsWith(BNG_NET_FILE_SUFFIX)){
			netFileIndices.add(i);
		}
	}
	if (netFileIndices.size()==1){
		return bng_fileContents[netFileIndices.iterator().next()].toString();
	}
	throw new RuntimeException("BioNetGen generated an empty reaction network.");
}
public void extractCompartmentsFromNetFile() {
	HashSet<Integer> netFileIndices = new HashSet<Integer>();
	for (int i=0;i<bng_filenames.length;i++){
		if (bng_filenames[i].toLowerCase().endsWith(BNG_NET_FILE_SUFFIX)){
			netFileIndices.add(i);
		}
	}
	if (netFileIndices.size()!=1){
		throw new RuntimeException("BioNetGen was unable to generate reaction network.");
	}
	int location = netFileIndices.iterator().next();
	String s = bng_fileContents[location].toString();
	
	String p = BNGOutputFileParser.extractCompartments(s);
	
	bng_fileContents[location] = new BigString(p);
}
public void insertEntitiesInNetFile(String entities, String kind) {
	if(entities == null) {
		return;
	}
	HashSet<Integer> netFileIndices = new HashSet<Integer>();
	for (int i=0;i<bng_filenames.length;i++){
		if (bng_filenames[i].toLowerCase().endsWith(BNG_NET_FILE_SUFFIX)){
			netFileIndices.add(i);
		}
	}
	if (netFileIndices.size()!=1){
		throw new RuntimeException("BioNetGen was unable to generate reaction network.");
	}
	int location = netFileIndices.iterator().next();
	String s = bng_fileContents[location].toString();
	
	String prologue = s.substring(0, s.indexOf("begin " + kind));
	String epilogue = s.substring(s.indexOf("end " + kind));
	
	String d = prologue;
	d += "begin " + kind + "\n";
	d += entities;
	d += epilogue;
	
	bng_fileContents[location] = new BigString(d);
}

public Document getNFSimXMLDocument() {
	HashSet<Integer> netFileIndices = new HashSet<Integer>();
	for (int i=0;i<bng_filenames.length;i++){
		if (bng_filenames[i].toLowerCase().endsWith(BNG_NFSIMXML_FILE_SUFFIX)){
			netFileIndices.add(i);
		}
	}
	if (netFileIndices.size()==1){
		String nfsimXMLString = bng_fileContents[netFileIndices.iterator().next()].toString();
System.out.println("BNGOutput.getNFSimXMLDocument(): +++++++++++ \n"+nfsimXMLString);
		Document nfsimXMLDocument = XmlUtil.stringToXML(nfsimXMLString,null);
		return nfsimXMLDocument;
	}
	throw new RuntimeException("BioNetGen was unable to generate reaction network.");
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2006 12:03:34 PM)
 * @return cbit.util.BigString
 */
public String getBNGFileContent(int index) {
	return bng_fileContents[index].toString();
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2006 3:34:34 PM)
 * @return java.lang.String[]
 */
public java.lang.String[] getBNGFilenames() {
	return bng_filenames;
}


/**
 * Insert the method's description here.
 * Creation date: (6/27/2005 3:57:21 PM)
 * @return cbit.util.BigString
 */
public String getConsoleOutput() {
	return consoleOutput.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2006 12:03:34 PM)
 * @return cbit.util.BigString
 */
public int getNumBNGFiles() {
	return bng_filenames.length;
}
}
