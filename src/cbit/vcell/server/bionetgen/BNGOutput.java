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
import java.util.HashSet;

import org.vcell.util.BigString;
/**
 * Insert the type's description here.
 * Creation date: (6/27/2005 2:59:47 PM)
 * @author: Anuradha Lakshminarayana
 */
public class BNGOutput implements java.io.Serializable {
	public final static String BNG_NET_FILE_SUFFIX = ".net";
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
