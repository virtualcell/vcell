/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.matrix.test;

import cbit.vcell.matrix.RationalMatrix;
import cbit.vcell.matrix.RationalMatrixFast;
import cbit.vcell.matrix.RationalNumber;
import cbit.vcell.matrix.SimpleMatrix;

/**
 * Insert the type's description here.
 * Creation date: (5/13/2003 10:11:04 AM)
 * @author: Jim Schaff
 */
public class SVDTest {
/**
 * Insert the method's description here.
 * Creation date: (5/13/2003 4:37:34 PM)
 * @param matrix Jama.Matrix
 */
public static RationalMatrix findNullSpaceSVD(RationalMatrix rA) {
	try {
		Jama.Matrix A = getJamaMatrix(rA);
	    Jama.Matrix At = A.transpose();
		Jama.SingularValueDecomposition svdMatrix = new Jama.SingularValueDecomposition(At);
	    Jama.Matrix U = svdMatrix.getU();
	    Jama.Matrix V = svdMatrix.getV();
	    double S[] = svdMatrix.getSingularValues();

	    //System.out.println("A' = U*S*V'");

	    //System.out.println("A' = ");
	    //At.print(8,5);

	    //System.out.println("U = ");
	    //U.print(8,5);

	    //System.out.println("V = ");
	    //V.print(8,5);

	    //System.out.print("S = ");
	    //for (int i = 0; i < S.length; i++){
	    //	System.out.print(S[i]+" ");
	    //}
	    //System.out.println();

	    int rank = svdMatrix.rank();
	    //System.out.println("rank = "+rank);

	    Jama.Matrix Vt = V.transpose();

	    Jama.Matrix nsMatrix = Vt.getMatrix(rank,Vt.getRowDimension()-1,0,Vt.getColumnDimension()-1);
	    
	    //System.out.println("NS = ");
	    //nsMatrix.print(8,5);
	    SimpleMatrix matrix = new SimpleMatrix(nsMatrix.getArrayCopy());
	    //matrix.show();
	    SimpleMatrix.gaussianElimination(new SimpleMatrix(matrix.getNumRows(),matrix.getNumRows()),matrix);
	    //matrix.show();
		RationalMatrixFast nsRationalMatrix = getRationalMatrixFast(matrix);
		//nsRationalMatrix.show();
		return nsRationalMatrix;
	}catch (Throwable e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/13/2003 4:37:34 PM)
 * @param matrix Jama.Matrix
 */
public static RationalMatrix findNullSpaceVCell(RationalMatrix rA) {
	try {
		SimpleMatrix A = new SimpleMatrix(getJamaMatrix(rA).getArrayCopy());
		SimpleMatrix ns = SimpleMatrix.findNullSpace(A);
	    //ns.show();
		RationalMatrixFast nsRationalMatrix = getRationalMatrixFast(ns);
		//nsRationalMatrix.show();
		return nsRationalMatrix;
	}catch (Throwable e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.Matrix
 */
public static RationalMatrixFast getGEPASI_Brusselator() throws Exception {
	int numVars = 7;
	int numReactions = 8;
	RationalMatrixFast a = new RationalMatrixFast(numVars, numReactions);
	a.zero();
	String vars[] = new String[numVars];
	int A = 0; vars[A] = "A";
	int B = 1; vars[B] = "B";
	int D = 2; vars[D] = "D";
	int E = 3; vars[E] = "E";
	int F = 4; vars[F] = "F";
	int X = 5; vars[X] = "X";
	int Y = 6; vars[Y] = "Y";
	//
	// A -> X
	// 2X + Y -> 3X
	// X + B -> Y + D
	// X -> E
	// A = E
	// A = F
	//
	int r=0;
	// A -> X
	a.set_elem(A,r,-1);		a.set_elem(X,r,1);  r++;
	// 2X + Y -> 3X
	a.set_elem(Y,r,-1);		a.set_elem(X,r,1); r++;
	// X + B -> Y + D
	a.set_elem(X,r,-1);		a.set_elem(B,r,-1);		a.set_elem(Y,r,1);		a.set_elem(D,r,1); r++;
	// X -> E
	a.set_elem(X,r,-1);		a.set_elem(E,r,1); r++;
	// A = E
	a.set_elem(A,r,-1);		a.set_elem(E,r,1); r++;
	a.set_elem(E,r,-1);		a.set_elem(A,r,1); r++;
	// A = F
	a.set_elem(A,r,-1);		a.set_elem(F,r,1); r++;
	a.set_elem(F,r,-1);		a.set_elem(A,r,1); r++;

	return a;
}
/**
 * Insert the method's description here.
 * Creation date: (5/13/2003 11:06:29 AM)
 * @return Jama.Matrix
 */
public static Jama.Matrix getJamaMatrix(RationalMatrix rMatrix) {
	Jama.Matrix jamaMatrix = new Jama.Matrix(rMatrix.getNumRows(),rMatrix.getNumCols());
	for (int i = 0; i < rMatrix.getNumRows(); i++){
		for (int j = 0; j < rMatrix.getNumCols(); j++){
			jamaMatrix.set(i,j,rMatrix.get_elem(i,j).doubleValue());
		}
		
	}
	return jamaMatrix;
}
/**
 * Insert the method's description here.
 * Creation date: (5/13/2003 11:06:29 AM)
 * @return Jama.Matrix
 */
public static RationalMatrixFast getRationalMatrixFast(SimpleMatrix matrix) {
	RationalMatrixFast rMatrix = new RationalMatrixFast(matrix.getNumRows(),matrix.getNumCols());
	for (int i = 0; i < rMatrix.getNumRows(); i++){
		for (int j = 0; j < rMatrix.getNumCols(); j++){
			RationalNumber r = RationalNumber.getApproximateFraction(matrix.get_elem(i,j));
			rMatrix.set_elem(i,j,r.getNumBigInteger().longValue(),r.getDenBigInteger().longValue());
		}
	}
	return rMatrix;
}
/**
 * Insert the method's description here.
 * Creation date: (5/13/2003 11:06:29 AM)
 * @return Jama.Matrix
 */
public static RationalMatrixFast getRationalMatrixFast(Jama.Matrix matrix) {
	RationalMatrixFast rMatrix = new RationalMatrixFast(matrix.getRowDimension(),matrix.getColumnDimension());
	for (int i = 0; i < rMatrix.getNumRows(); i++){
		for (int j = 0; j < rMatrix.getNumCols(); j++){
			RationalNumber r = RationalNumber.getApproximateFraction(matrix.get(i,j));
			rMatrix.set_elem(i,j,r.getNumBigInteger().longValue(),r.getDenBigInteger().longValue());
		}
	}
	return rMatrix;
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
    try {
	    Jama.Matrix A = getJamaMatrix(getGEPASI_Brusselator());

	    Jama.Matrix At = A.transpose();
		Jama.SingularValueDecomposition svdMatrix = new Jama.SingularValueDecomposition(At);
	    Jama.Matrix U = svdMatrix.getU();
	    Jama.Matrix V = svdMatrix.getV();
	    double S[] = svdMatrix.getSingularValues();

	    System.out.println("A' = U*S*V'");

	    System.out.println("A' = ");
	    At.print(8,5);

	    System.out.println("U = ");
	    U.print(8,5);

	    System.out.println("V = ");
	    V.print(8,5);

	    System.out.print("S = ");
	    for (int i = 0; i < S.length; i++){
	    	System.out.print(S[i]+" ");
	    }
	    System.out.println();

	    int rank = svdMatrix.rank();
	    System.out.println("rank = "+rank);

	    Jama.Matrix Vt = V.transpose();

	    Jama.Matrix nsMatrix = Vt.getMatrix(rank,Vt.getRowDimension()-1,0,Vt.getColumnDimension()-1);
	    
	    System.out.println("NS = ");
	    nsMatrix.print(8,5);
	    SimpleMatrix matrix = new SimpleMatrix(nsMatrix.getArrayCopy());
	    matrix.show();
	    SimpleMatrix.gaussianElimination(new SimpleMatrix(matrix.getNumRows(),matrix.getNumRows()),matrix);
	    matrix.show();
		RationalMatrixFast nsRationalMatrix = getRationalMatrixFast(matrix);
		nsRationalMatrix.show();
    } catch (Throwable e) {
        e.printStackTrace(System.out);
    }
}
}
