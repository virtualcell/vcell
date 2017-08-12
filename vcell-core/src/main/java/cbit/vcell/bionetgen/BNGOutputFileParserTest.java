/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.bionetgen;
import java.io.File;
/**
 * Insert the type's description here.
 * Creation date: (1/19/2006 11:14:29 AM)
 * @author: Jim Schaff
 */
public class BNGOutputFileParserTest {
/**
 * Insert the method's description here.
 * Creation date: (1/19/2006 11:15:19 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	BNGOutputFileParser bngNetFileParser = new BNGOutputFileParser(); 
	File bngNetFile = new java.io.File("C:\\TEMP\\ddd\\BioNetGen.net");
	BNGOutputSpec outputSpec = null;
	try {
		outputSpec = bngNetFileParser.createBngOutputSpec(bngNetFile);
	} catch (java.io.FileNotFoundException e1) {
		throw new RuntimeException("could not read BNG .net file : "+e1.getMessage());
	} catch (java.io.IOException e2) {
		throw new RuntimeException("could not read BNG .net file : "+e2.getMessage());
	}
	bngNetFileParser.printBNGNetOutput(outputSpec);
	
	
}
}
