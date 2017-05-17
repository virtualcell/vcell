/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solvers;

import java.io.PrintWriter;

import org.apache.log4j.Logger;

import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.util.JNISupport;

/**
 * Insert the type's description here.
 * Creation date: (12/7/2005 2:11:23 PM)
 * @author: Jim Schaff
 */
public class NativeIDASolver {
	static {
		NativeLib.NATIVE_SOLVERS.load();
    }

	private boolean bStopRequested = false;
	private static final Logger lg = Logger.getLogger(NativeIDASolver.class);

/**
 * NativeIDASolver constructor comment.
 */
public NativeIDASolver() {
	super();
	JNISupport.verifyPackage(RowColumnResultSet.class, "cbit.vcell.math");
	if (lg.isInfoEnabled()) {
		lg.info(jniLibraryVersion());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/16/2005 10:36:54 AM)
 * @return boolean
 */
public boolean isStopRequested() {
	return bStopRequested;
}


/**
 * Insert the method's description here.
 * Creation date: (12/7/2005 3:52:46 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		final NativeIDASolver nativesolver = new NativeIDASolver();
		String input = testInput( );

		/*
		Thread t = new Thread() {
			public void run() {
				try {
				Thread.sleep(200);
				nativesolver.setStopRequested(true);
				} catch (Exception ex) {
				}
			}
		};

		t.start();
		
		*/
		double x[] = {1.0, 1.0, 1000.0};
		System.out.println("**************Solve 2************************");
		RowColumnResultSet rcrs = nativesolver.solve(input, x);
		for (int i = 0; i < rcrs.getRowCount(); i ++) {
			double[] row = rcrs.getRow(i);
			for (int j = 0; j < row.length; j ++) {
				System.out.print(row[j] + " ");
			}
			System.out.println();
		}
	} catch (Exception ex) {
		System.out.println("\nJava caught exception: " + ex.getMessage());
	}
}

/**
 * @return a test input string
 */
public static String testInput( ) {
		return 	"SOLVER IDA\n" +
		"STARTING_TIME 0.0\n" +
		"ENDING_TIME 1.0\n" +
		"RELATIVE_TOLERANCE 1.0E-9\n" +
		"ABSOLUTE_TOLERANCE 1.0E-9\n" +
		"MAX_TIME_STEP 1.0\n" +
		"KEEP_EVERY 1\n" +
		"NUM_PARAMETERS 3\n" +
		"Kf_reaction0\n" +
		"Kr_reaction0\n" +
		"P\n" +
		"NUM_EQUATIONS 4\n" +
		"VAR species0_cyt INIT 200.0;\n" +
		"VAR species1_cyt INIT 2.0;\n" +
		"VAR species3_cyt INIT 0.0;\n" +
		"VAR Voltage_mitoMem INIT 0.1;\n" +
		"TRANSFORM\n" +
		"1 0 0 0 \n" +
		"0 1 0 0 \n" +
		"0 0 1 0 \n" +
		"0 0 0 1 \n" +
		"INVERSETRANSFORM\n" +
		"1 0 0 0 \n" +
		"0 1 0 0 \n" +
		"0 0 1 0 \n" +
		"0 0 0 1 \n" +
		"RHS DIFFERENTIAL 4 ALGEBRAIC 0\n" +
		" - ((Kf_reaction0 * species0_cyt) - (Kr_reaction0 * species1_cyt));\n" +
		"((Kf_reaction0 * species0_cyt) - (Kr_reaction0 * species1_cyt));\n" +
		"((25.0 * (8.0E-5 - (0.8 * species3_cyt))) - species3_cyt);\n" +
		"(1000.0 * ( - (0.0010 * sin(t)) - (1.4928057733942749E-5 * Voltage_mitoMem * ((5.0 * (162.2 - (0.8 * species1_cyt) - (0.8 * species0_cyt))) - (species0_cyt * exp( - (0.07736348328121241 * Voltage_mitoMem)))) * P / (1.0 - exp( - (0.07736348328121241 * Voltage_mitoMem))))));\n";
}


/**
 * Insert the method's description here.
 * Creation date: (12/7/2005 2:13:54 PM)
 * @return double[][]
 * @param idaInput java.lang.String
 */
private native cbit.vcell.math.RowColumnResultSet nativeSolve(String idaInput, double[] paramValues) throws Exception;

/**
 * @return shared library version information
 */
private native String jniLibraryVersion( );

private cbit.vcell.math.RowColumnResultSet callNative(String idaInput, double[] paramValues) throws Exception {
	if (lg.isDebugEnabled()) {
		String name = "idaInput.txt";
		try (PrintWriter pw = new PrintWriter(name)) {
			pw.println(idaInput);
		}
		lg.debug("IDA input: " + idaInput);
		lg.debug("IDA input to file " + name);
	}
	return nativeSolve(idaInput,paramValues);
}


/**
 * Insert the method's description here.
 * Creation date: (12/16/2005 10:37:26 AM)
 * @param newStopRequested boolean
 */
public void setStopRequested(boolean newStopRequested) {
	bStopRequested = newStopRequested;
}


/**
 * Insert the method's description here.
 * Creation date: (12/7/2005 2:13:54 PM)
 * @return double[][]
 * @param idaInput java.lang.String
 */
public cbit.vcell.math.RowColumnResultSet solve(String idaInput) throws Exception {
	return callNative(idaInput, null);
}


/**
 * Insert the method's description here.
 * Creation date: (12/7/2005 2:13:54 PM)
 * @return double[][]
 * @param idaInput java.lang.String
 */
public cbit.vcell.math.RowColumnResultSet solve(String idaInput, double[] paramValues) throws Exception {
	return callNative(idaInput, paramValues);
}
}
