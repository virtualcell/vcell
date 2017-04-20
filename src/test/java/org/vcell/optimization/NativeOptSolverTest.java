/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import cbit.vcell.opt.OptSolverResultSet;
import cbit.vcell.resource.NativeLib;

public class NativeOptSolverTest {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			NativeLib.NATIVE_SOLVERS.load();
			String optProblemXML = getXMLString("..\\numerics\\OptStandalone2\\test9.xml");
			OptSolverCallbacks optSolverCallbacks = new DefaultOptSolverCallbacks();
			NativeOptSolver nativeOptSolver = new NativeOptSolver();
			
			String optSolverResultSetXML = nativeOptSolver.nativeSolve_CFSQP(optProblemXML, optSolverCallbacks);
			System.out.println(optSolverResultSetXML);

			OptXmlReader optXmlReader = new OptXmlReader();
			OptSolverResultSet optResultSet = optXmlReader.getOptimizationResultSet(optSolverResultSetXML);		
			
			optResultSet.show();
			
			System.out.println("done");
		}catch (Exception e){
			e.printStackTrace(System.out);
		}

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



}
