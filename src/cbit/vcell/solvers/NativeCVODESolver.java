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

import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.util.JNISupport;

/**
 * Insert the type's description here.
 * Creation date: (12/7/2005 2:11:23 PM)
 * @author: Jim Schaff
 */
public class NativeCVODESolver {
	static {
		NativeLib.NATIVE_SOLVERS.load( );
    }

	private boolean bStopRequested = false;

/**
 * NativeIDASolver constructor comment.
 */
public NativeCVODESolver() {
	super();
	JNISupport.verifyPackage(RowColumnResultSet.class, "cbit.vcell.math");
	System.out.println(jniLibraryVersion());
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
 * Creation date: (12/7/2005 2:13:54 PM)
 * @return double[][]
 * @param idaInput java.lang.String
 */
private native cbit.vcell.math.RowColumnResultSet nativeSolve(String cvodeInput, double[] paramValues) throws Exception;

/**
 * @return shared library version information
 */
private native String jniLibraryVersion( );


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
	return nativeSolve(idaInput, null);
}


/**
 * Insert the method's description here.
 * Creation date: (12/7/2005 2:13:54 PM)
 * @return double[][]
 * @param idaInput java.lang.String
 */
public cbit.vcell.math.RowColumnResultSet solve(String idaInput, double[] paramValues) throws Exception {
	return nativeSolve(idaInput, paramValues);
}
}
