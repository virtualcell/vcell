/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.matrix;

import java.math.BigInteger;

@SuppressWarnings("serial")
public class RationalExpMatrix implements RationalMatrix, java.io.Serializable
{
	protected int rows;
	protected int cols;
	protected RationalExp[] data;
	
	public RationalExpMatrix(RationalExp[][] rowColData){
		rows = rowColData.length;
		cols = rowColData[0].length;
		data = new RationalExp[rows*cols];

		for (int i = 0; i < rows; i ++){
			for (int j = 0; j< cols; j++){
				set_elem(i,j,rowColData[i][j]);
			}
		}	
	}
	RationalExpMatrix(RationalMatrixFast rationalMatrixFast){
		rows = rationalMatrixFast.getNumRows();
		cols = rationalMatrixFast.getNumCols();
		data = new RationalExp[rows*cols];

		for (int i = 0; i < rows; i ++){
			for (int j = 0; j< cols; j++){
				RationalNumber rationalNumber = rationalMatrixFast.get_elem(i, j);
				RationalExp rationalExp = new RationalExp(rationalNumber);
				set_elem(i,j,rationalExp);
			}
		}	
	}
public RationalExpMatrix(int r, int c){
	data = new RationalExp[r * c];
	rows = r;
	cols = c;

	for (int i = 0; i < rows * cols; i ++){
		data[i] = RationalExp.ZERO;
	}	
}
/**
 * This method was created by a SmartGuide.
 * @param mat cbit.vcell.math.Matrix
 */
public RationalExpMatrix (RationalExpMatrix mat) {
	this.rows = mat.rows;
	this.cols = mat.cols;
	data = new RationalExp[rows * cols];
	try {
		for (int i = 0; i < rows; i ++){
			for (int j = 0; j < cols; j ++){
				set_elem(i, j, mat.get(i, j));
			}
		}
	}catch (Exception e){
		e.printStackTrace(System.out);
	}				
}

public RationalExpMatrix transpose(){
	RationalExpMatrix transposedMatrix = new RationalExpMatrix(cols,rows);
	for (int i = 0; i < rows; i++) {
		for (int j = 0; j < cols; j++) {
			transposedMatrix.set_elem(j, i, new RationalExp(get(i, j)));
		}
	}
	return transposedMatrix;
}


public RationalMatrix findNullSpace() throws MatrixException {
	
//	if (rows <= 1){
//		throw new MatrixException("this must have more than one row");
//	}
	int numVars = rows;
	
	RationalExpMatrix b = new RationalExpMatrix(this);
	RationalExpMatrix K = new RationalExpMatrix(numVars,numVars);
	K.identity();
	int rank = b.gaussianElimination(K);
	if (rank == numVars){
		return null;
	}
	int nullity = numVars - rank;
//	b.show();
//	K.show();

	//
	// the 'newK' matrix is the last N-rank rows of 'K' (the elimination operations of 'a')
	//
	RationalExpMatrix newK = new RationalExpMatrix(nullity,numVars);
	for (int i=0;i<(nullity);i++){
		for (int j=0;j<numVars;j++){
			newK.set_elem(i,j,K.get(rank+i,j));
		}
	}
//	newK.show();
	RationalExpMatrix tempMatrix = new RationalExpMatrix(nullity,nullity);
	tempMatrix.identity();
	int numberOfConservations = newK.gaussianElimination(tempMatrix);
	
	if (numberOfConservations == 0){
		throw new MatrixException("system has "+rank+" of "+numVars+" independent vars, and no conserved groups identified");
	}

	//
	//?????? I'm not sure if this assumption is valid always
	//
	if (numberOfConservations!=nullity){
		System.out.println("Matrix.findNullSpace(), WARNING???: numberOfConservations<"+numberOfConservations+"> != nullity<"+nullity+">");
	}
	
	RationalExpMatrix returnMatrix = new RationalExpMatrix(numberOfConservations,numVars);	
	for (int i=0;i<numberOfConservations;i++){
		for (int j=0;j<numVars;j++){
			returnMatrix.set_elem(i,j,newK.get(i,j));
		}
	}
	return returnMatrix;
}
public int gaussianElimination() throws MatrixException {
	
	if (rows < 1){
		throw new MatrixException("this matrix must have at least one row");
	}
	int rank = 0;
	//
	// traverse each column
	//
	int currentRow = 0;
	for (int currentCol = 0; (currentCol < cols) && (currentRow < rows); currentCol++){
//System.out.println("trying to eliminate column "+(currentCol+1)+" in row "+(currentRow+1));
		//
		// find pivot row
		//
		RationalExp mag = RationalExp.ZERO;
		int pivotRow = -1;
		for (int j = currentRow; j < rows; j ++){
			RationalExp mag2 = get(j, currentCol);
			if (!mag2.isZero()){
				mag = mag2;
				pivotRow = j;
			}
		}
		if (pivotRow == -1 || mag.isZero()){
			//
			// no row pivot, rotate row to bottom and try next pivot
			//
//System.out.println("no row pivot found for row "+(currentRow+1)+", rotating row ");
			//
			// rotate b matrix
			//
			for (int k=currentCol;k<cols;k++){
				RationalExp temp = get(currentRow,k);
				for (int j = currentRow;j<rows-1;j++){
					set_elem(j,k, get(j+1,k));
				}
				set_elem(rows-1,k,temp);
			}
			continue;
		}else if (pivotRow != currentRow){
			//
			// move pivot row into position
			//
//System.out.println("swapping row "+(pivotRow+1)+" for row "+(currentRow+1)+".....");			
			RationalExp temp;
			for (int j = currentCol; j < cols; j ++){
				temp = get(currentRow, j);
				set_elem(currentRow, j, get(pivotRow, j));
				set_elem(pivotRow, j, temp);
			}
		}
//System.out.println("normalizing pivot row "+(currentRow+1));			
		//
		// normalize pivot row
		//
		rank++;
		mag = get(currentRow, currentCol);
		for (int j = currentCol; j < cols; j++){
			set_elem(currentRow, j, get(currentRow, j).div(mag));
		}	
		//
		// eliminate pivot row component from other rows
		//
//System.out.println("eliminating pivot row "+(currentRow+1)+" components");			
		for (int k = 0; k < rows; k ++){
			if (k == currentRow) continue;

			RationalExp mag2 = get(k, currentCol);

			for (int j = currentCol; j < cols; j ++){
				RationalExp r = get(currentRow, j);
				if (!r.isZero()){
					set_elem(k, j, get(k, j).sub(mag2.mult(r)));
				}
			}	
		}
		currentRow++;
	}		
	return rank;
}
public int gaussianElimination(RationalExpMatrix K) throws MatrixException {
	
	if (rows < 1){
		throw new MatrixException("this matrix must have at least one row");
	}
	if (K.rows != rows){
		throw new MatrixException("number of rows not same for matrices K and a");
	}	
	int rank = 0;
	//
	// traverse each column
	//
	int currentRow = 0;
	for (int currentCol = 0; (currentCol < cols) && (currentRow < rows); currentCol++){
//System.out.println("trying to eliminate column "+(currentCol+1)+" in row "+(currentRow+1));
		//
		// find pivot row
		//
		RationalExp mag = RationalExp.ZERO;
		int pivotRow = -1;
		for (int j = currentRow; j < rows; j ++){
			RationalExp mag2 = get(j, currentCol);
			if (!mag2.isZero()){
				mag = mag2;
				pivotRow = j;
				break;
			}
		}
		if (pivotRow == -1 || mag.isZero()){
			//
			// no row pivot, rotate row to bottom and try next pivot
			//
//System.out.println("no row pivot found for row "+(currentRow+1)+", rotating row ");
			//
			// rotate b matrix
			//
			for (int k=currentCol;k<cols;k++){
				RationalExp temp = get(currentRow,k);
				for (int j = currentRow;j<rows-1;j++){
					set_elem(j,k, get(j+1,k));
				}
				set_elem(rows-1,k,temp);
			}
			//
			// rotate K matrix
			//		
			for (int k=0;k<K.cols;k++){
				RationalExp temp = K.get(currentRow,k);
				for (int j = currentRow;j<K.rows-1;j++){
					K.set_elem(j,k, K.get(j+1,k));
				}
				K.set_elem(K.rows-1,k,temp);
			}
			continue;
		}else if (pivotRow != currentRow){
			//
			// move pivot row into position
			//
//System.out.println("swapping row "+(pivotRow+1)+" for row "+(currentRow+1)+".....");			
			RationalExp temp;
			for (int j = currentCol; j < cols; j ++){
				temp = get(currentRow, j);
				set_elem(currentRow, j, get(pivotRow, j));
				set_elem(pivotRow, j, temp);
			}
			for (int j = 0; j < K.cols; j ++){
				temp = K.get(currentRow, j);
				K.set_elem(currentRow, j, K.get(pivotRow, j));
				K.set_elem(pivotRow, j, temp);
			}
		}
//System.out.println("normalizing pivot row "+(currentRow+1));			
		//
		// normalize pivot row
		//
		rank++;
		mag = get(currentRow, currentCol);
		for (int j = currentCol; j < cols; j++){
			set_elem(currentRow, j, get(currentRow, j).div(mag).simplify());
		}	
		for (int j = 0; j < K.cols; j++){
			K.set_elem(currentRow, j, K.get(currentRow, j).div(mag).simplify());
		}	
		//
		// eliminate pivot row component from other rows
		//
//System.out.println("eliminating pivot row "+(currentRow+1)+" components");			
		for (int k = 0; k < K.rows; k ++){
			if (k == currentRow) continue;

			RationalExp mag2 = get(k, currentCol);

			for (int j = currentCol; j < cols; j ++){
				RationalExp r = get(currentRow, j);
				if (!r.isZero()){
					set_elem(k, j, get(k, j).sub(mag2.mult(r).simplify()).simplify());
				}
			}	
			for (int j = 0; j < K.cols; j ++){
				RationalExp r = K.get(currentRow, j);
				if (!r.isZero()){
					K.set_elem(k, j, K.get(k, j).sub(mag2.mult(r).simplify()).simplify());
				}
			}
		}
		currentRow++;
	}		
	return rank;
}
public RationalExp get(int r, int c) {
	if (r < 0 || r >= rows){
		throw new IllegalArgumentException("r out of range <"+r+">");
	}
	if (c < 0 || c >= cols){
		throw new IllegalArgumentException("c out of range <"+c+">");
	}
	return data[c + r * cols];
}
public RationalNumber get_elem(int r, int c) {
	if (r < 0 || r >= rows){
		throw new IllegalArgumentException("r out of range <"+r+">");
	}
	if (c < 0 || c >= cols){
		throw new IllegalArgumentException("c out of range <"+c+">");
	}
	return data[c + r * cols].getConstant();
}
/**
 * Insert the method's description here.
 * Creation date: (5/5/00 12:56:34 AM)
 * @return double[]
 */
public RationalExp[][] getDataCopy() {
	RationalExp D[][] = new RationalExp[rows][cols];
	for (int i=0;i<rows;i++){
		for (int j=0;j<cols;j++){
			D[i][j] = get(i,j);
		}
	}
	return D;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumCols() {
	return cols;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumRows() {
	return rows;
}
/**
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
public void identity() throws MatrixException {
	if (rows != cols){
		throw new MatrixException("num rows must equal num columns");
	}	
	for (int i = 0; i < rows; i ++){
		for (int j = 0; j < cols; j ++){
			if (i == j){
				set_elem(i, j, RationalExp.ONE);
			}else{
				set_elem(i, j, RationalExp.ZERO);
			}
		}
	}			
}
public void matinv(RationalExpMatrix a) throws MatrixException {
	if (a.rows < 1){
		throw new MatrixException("must have at least one row");
	}
	
	if (a.rows != a.cols){
		throw new MatrixException("must be a square matrix");
	}
	
	if (a.rows != rows || a.cols != cols){
		throw new MatrixException("matrices must be same size");
	}	

	if (a.rows == 1)	{
		set_elem(0, 0, a.get(0, 0).inverse());
		return;
	}

	RationalExpMatrix b = new RationalExpMatrix(a);

	int n = rows;
	for (int i = 0; i < n; i ++){
		for (int j = 0; j < n; j ++){
			if (i == j){
				set_elem(i, j, RationalExp.ONE);
			}else{
				set_elem(i, j, RationalExp.ZERO);
			}
		}
	}			

	for (int i = 0; i < n; i ++){
		//
		// find pivot (any non-zero element is fine, with symbols, can't really tell magnitude anyway)
		//
		RationalExp mag = RationalExp.ZERO;
		int pivot = -1;
		for (int j = i; j < n; j ++){
			RationalExp mag2 = b.get(j, i);
			if (!mag2.isZero()){
				mag = mag2;
				pivot = j;
			}
		}
		//
		// no pivot (error)
		//
		if (pivot == -1 || mag.isZero()){
			return;
		}
		//
		// move pivot row into position
		//
		if (pivot != i){
			RationalExp temp;
			for (int j = i; j < n; j ++){
				temp = b.get(i, j);
				b.set_elem(i, j, b.get(pivot, j));
				b.set_elem(pivot, j, temp);
			}

			for (int j = 0; j < n; j ++){
				temp = get(i, j);
				set_elem(i, j, get(pivot, j));
				set_elem(pivot, j, temp);
			}
		}
		//
		// normalize pivot row
		//
		mag = b.get(i, i);
		for (int j = i; j < n; j ++){
			b.set_elem(i, j, b.get(i, j).div(mag));
		}	
		for (int j = 0; j < n; j ++){
			set_elem(i, j, get(i, j).div(mag));
		}	
		//
		// eliminate pivot row component from other rows
		//
		for (int k = 0; k < n; k ++){
			if (k == i) continue;

			RationalExp mag2 = b.get(k, i);

			for (int j = i; j < n; j ++){
				b.set_elem(k, j, b.get(k, j).sub(mag2.mult(b.get(i, j))));
			}	
			for (int j = 0; j < n; j ++){
				set_elem(k, j, get(k, j).sub(mag2.mult(get(i, j))));
			}
		}
	}
}	
public void matmul(RationalExpMatrix a, RationalExpMatrix b) throws MatrixException {
	if ((a.getNumCols() != b.getNumRows()) || (a.getNumRows() != getNumRows()) || (b.getNumCols() != getNumCols())){
		return;
	}	

	for (int i = 0; i < rows; i ++){
		for (int j = 0; j < cols; j ++){
			RationalExp s = RationalExp.ZERO;
			for (int k = 0; k < a.cols; k ++){
				s = s.add(a.get(i, k).mult(b.get(k, j)));
			}	
			set_elem(i, j, s);
		}
	}	
}
public void set_elem(int r, int c, long x) {
	if (r < 0 || r >= rows){
		throw new IllegalArgumentException("r out of range <"+r+">");
	}
	if (c < 0 || c >= cols){
		throw new IllegalArgumentException("c out of range <"+c+">");
	}
	data[c + r * cols] = new RationalExp(BigInteger.valueOf(x));
}
public void set_elem(int r, int c, long num, long den) {
	if (r < 0 || r >= rows){
		throw new IllegalArgumentException("r out of range <"+r+">");
	}
	if (c < 0 || c >= cols){
		throw new IllegalArgumentException("c out of range <"+c+">");
	}
	data[c + r * cols] = new RationalExp(BigInteger.valueOf(num),BigInteger.valueOf(den));
}
public void set_elem(int r, int c, RationalExp x) {
	if (r < 0 || r >= rows){
		throw new IllegalArgumentException("r out of range <"+r+">");
	}
	if (c < 0 || c >= cols){
		throw new IllegalArgumentException("c out of range <"+c+">");
	}
	data[c + r * cols] = x;
}
public void set_rand() throws MatrixException {
	for (int i = 0; i < rows; i ++){
		for (int j = 0; j < cols; j ++){
			set_elem(i, j, new RationalExp(BigInteger.valueOf((long)(1000*Math.random())),BigInteger.valueOf((long)(1000*Math.random()))));
		}
	}		
}
public void set_rand_int() throws MatrixException {
	for (int i = 0; i < rows; i ++){
		for (int j = 0; j < cols; j ++){
			set_elem(i, j, new RationalExp(BigInteger.valueOf((long)(4*(Math.random()-0.5)))));
		}
	}		
}
public void show() {
	System.out.println("Rows = " + rows + " Cols = " + cols);
	for (int i = 0; i < rows; i ++){
		StringBuffer s = new StringBuffer();
		for (int j = 0; j < cols; j ++){
			s.append(get(i, j).infixString());
			if (j < cols - 1){
				s.append(",\t");
			}	
		}
		System.out.println(s.toString());
	}
}
/**
 * solves the system M x = b, where argument A = [M;b], returns x
 * @return double[]
 * @param A cbit.vcell.mapping.Matrix
 * @exception javlang.Exception The exception description.
 */
public RationalNumber[] solveLinear() throws MatrixException {
	if (rows<1 || cols!=rows+1){
		throw new MatrixException("bad argument, A is "+rows+" by "+cols);
	}
	int numVars = rows;
	
	int rank = gaussianElimination();
	if (rank != numVars){
		show();
		throw new MatrixException("singular matrix");
	}

	RationalNumber x[] = new RationalNumber[numVars];
	
	for (int i=0;i<numVars;i++){
		x[i] = get_elem(i,numVars);
	}

	return x;
}
/**
 * solves the system M x = b, where argument A = [M;b], returns x
 * @return double[]
 * @param A cbit.vcell.mapping.Matrix
 * @exception javlang.Exception The exception description.
 */
public RationalExp[] solveLinearExpressions() throws MatrixException {
	if (rows<1 || cols!=rows+1){
		throw new MatrixException("bad argument, A is "+rows+" by "+cols);
	}
	int numVars = rows;
	
	int rank = gaussianElimination();
	if (rank != numVars){
		show();
		throw new MatrixException("singular matrix");
	}

	RationalExp x[] = new RationalExp[numVars];
	
	for (int i=0;i<numVars;i++){
		x[i] = get(i,numVars);
	}

	return x;
}
/**
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
public void zero() {
	for (int i = 0; i < rows; i ++){
		for (int j = 0; j < cols; j ++){
			set_elem(i, j, RationalExp.ZERO);
		}
	}			
}
}
