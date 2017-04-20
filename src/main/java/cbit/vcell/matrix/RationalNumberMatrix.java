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
public class RationalNumberMatrix implements RationalMatrix, java.io.Serializable
{
	protected int rows;
	protected int cols;
	protected RationalNumber[] data;
	
	public RationalNumberMatrix(RationalNumber[][] rowColData){
		rows = rowColData.length;
		cols = rowColData[0].length;
		data = new RationalNumber[rows*cols];

		for (int i = 0; i < rows; i ++){
			for (int j = 0; j< cols; j++){
				set_elem(i,j,rowColData[i][j]);
			}
		}	
	}
	RationalNumberMatrix(RationalMatrixFast rationalMatrixFast){
		rows = rationalMatrixFast.getNumRows();
		cols = rationalMatrixFast.getNumCols();
		data = new RationalNumber[rows*cols];

		for (int i = 0; i < rows; i ++){
			for (int j = 0; j< cols; j++){
				RationalNumber rationalNumber = rationalMatrixFast.get_elem(i, j);
				set_elem(i,j,rationalNumber);
			}
		}	
	}
public RationalNumberMatrix(int r, int c){
	data = new RationalNumber[r * c];
	rows = r;
	cols = c;

	for (int i = 0; i < rows * cols; i ++){
		data[i] = RationalNumber.ZERO;
	}	
}
/**
 * This method was created by a SmartGuide.
 * @param mat cbit.vcell.math.Matrix
 */
public RationalNumberMatrix (RationalNumberMatrix mat) {
	this.rows = mat.rows;
	this.cols = mat.cols;
	data = new RationalNumber[rows * cols];
	System.arraycopy(mat.data, 0, data, 0, mat.data.length);
}

public RationalMatrix findNullSpace() throws MatrixException {
	
//	if (rows <= 1){
//		throw new MatrixException("this must have more than one row");
//	}
	int numVars = rows;
	
	RationalNumberMatrix b = new RationalNumberMatrix(this);
	RationalNumberMatrix K = new RationalNumberMatrix(numVars,numVars);
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
	RationalNumberMatrix newK = new RationalNumberMatrix(nullity,numVars);
	for (int i=0;i<(nullity);i++){
		for (int j=0;j<numVars;j++){
			newK.set_elem(i,j,K.get_elem(rank+i,j));
		}
	}
//	newK.show();
	RationalNumberMatrix tempMatrix = new RationalNumberMatrix(nullity,nullity);
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
	
	RationalNumberMatrix returnMatrix = new RationalNumberMatrix(numberOfConservations,numVars);	
	for (int i=0;i<numberOfConservations;i++){
		for (int j=0;j<numVars;j++){
			returnMatrix.set_elem(i,j,newK.get_elem(i,j));
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
		RationalNumber mag = RationalNumber.ZERO;
		int pivotRow = -1;
		for (int j = currentRow; j < rows; j ++){
			RationalNumber mag2 = get_elem(j, currentCol);
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
				RationalNumber temp = get_elem(currentRow,k);
				for (int j = currentRow;j<rows-1;j++){
					set_elem(j,k, get_elem(j+1,k));
				}
				set_elem(rows-1,k,temp);
			}
			continue;
		}else if (pivotRow != currentRow){
			//
			// move pivot row into position
			//
//System.out.println("swapping row "+(pivotRow+1)+" for row "+(currentRow+1)+".....");			
			RationalNumber temp;
			for (int j = currentCol; j < cols; j ++){
				temp = get_elem(currentRow, j);
				set_elem(currentRow, j, get_elem(pivotRow, j));
				set_elem(pivotRow, j, temp);
			}
		}
//System.out.println("normalizing pivot row "+(currentRow+1));			
		//
		// normalize pivot row
		//
		rank++;
		mag = get_elem(currentRow, currentCol);
		for (int j = currentCol; j < cols; j++){
			set_elem(currentRow, j, get_elem(currentRow, j).div(mag));
		}	
		//
		// eliminate pivot row component from other rows
		//
//System.out.println("eliminating pivot row "+(currentRow+1)+" components");			
		for (int k = 0; k < rows; k ++){
			if (k == currentRow) continue;

			RationalNumber mag2 = get_elem(k, currentCol);

			for (int j = currentCol; j < cols; j ++){
				RationalNumber r = get_elem(currentRow, j);
				if (!r.isZero()){
					set_elem(k, j, get_elem(k, j).sub(mag2.mult(r)));
				}
			}	
		}
		currentRow++;
	}		
	return rank;
}
public int gaussianElimination(RationalNumberMatrix K) throws MatrixException {
	
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
		int this_currentRow_offset = currentRow * cols;
		int K_currentRow_offset = currentRow * K.cols;
		
		RationalNumber mag = RationalNumber.ZERO;
		int pivotRow = -1;
		for (int j = currentRow; j < rows; j ++){
			RationalNumber mag2 = data[j * cols + currentCol];//get(j, currentCol);
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
				RationalNumber temp = data[this_currentRow_offset + k]; //get(currentRow,k);
				for (int j = currentRow;j<rows-1;j++){
					//set_elem(j,k, get(j+1,k));
					data[j * cols + k] = data[(j+1) * cols + k];
				}
				//set_elem(rows-1,k,temp);
				data[(rows - 1) * cols + k] = temp;
			}
			//
			// rotate K matrix
			//		
			for (int k=0;k<K.cols;k++){
				RationalNumber temp = K.data[K_currentRow_offset + k];//K.get(currentRow,k);
				for (int j = currentRow;j<K.rows-1;j++){
					//K.set_elem(j,k, K.get(j+1,k));
					K.data[j * K.cols + k] = K.data[(j+1) * K.cols + k];
				}
				//K.set_elem(K.rows-1,k,temp);
				K.data[(K.rows - 1) * K.cols + k] = temp;
			}
			continue;
		}else if (pivotRow != currentRow){
			int this_pivotRow_offset = pivotRow * cols;
			int K_pivotRow_offset = pivotRow * K.cols;
			//
			// move pivot row into position
			//
//System.out.println("swapping row "+(pivotRow+1)+" for row "+(currentRow+1)+".....");			
			RationalNumber temp;
			for (int j = currentCol; j < cols; j ++){
				temp = data[this_currentRow_offset + j];//get(currentRow, j);
				//set_elem(currentRow, j, get(pivotRow, j));
				//set_elem(pivotRow, j, temp);				
				data[this_currentRow_offset + j] = data[this_pivotRow_offset + j];
				data[this_pivotRow_offset + j] = temp;
			}
			for (int j = 0; j < K.cols; j ++){
				temp = K.data[K_currentRow_offset + j];//K.get(currentRow, j);
				//K.set_elem(currentRow, j, K.get(pivotRow, j));
				//K.set_elem(pivotRow, j, temp);				
				K.data[K_currentRow_offset + j] = K.data[K_pivotRow_offset + j];
				K.data[K_pivotRow_offset + j] = temp;
			}
		}
//System.out.println("normalizing pivot row "+(currentRow+1));			
		//
		// normalize pivot row
		//
		rank++;
		mag = data[this_currentRow_offset + currentCol];//get(currentRow, currentCol);
		for (int j = currentCol; j < cols; j++){
			//set_elem(currentRow, j, get(currentRow, j).div(mag));
			data[this_currentRow_offset + j] = data[this_currentRow_offset + j].div(mag);
		}	
		for (int j = 0; j < K.cols; j++){
			//K.set_elem(currentRow, j, K.get(currentRow, j).div(mag));
			K.data[K_currentRow_offset + j] =  K.data[K_currentRow_offset + j].div(mag);
		}	
		//
		// eliminate pivot row component from other rows
		//
//System.out.println("eliminating pivot row "+(currentRow+1)+" components");			
		for (int k = 0; k < K.rows; k ++){
			if (k == currentRow) continue;

			RationalNumber mag2 = data[k * cols + currentCol];//get(k, currentCol);

			for (int j = currentCol; j < cols; j ++){
				RationalNumber r = data[this_currentRow_offset + j];//get(currentRow, j);
				if (!r.isZero()){
					//set_elem(k, j, get(k, j).sub(mag2.mult(r)));
					data[k * cols + j] = data[k * cols + j].sub(mag2.mult(r));
				}
			}	
			for (int j = 0; j < K.cols; j ++){
				RationalNumber r = K.data[K_currentRow_offset + j];//K.get(currentRow, j);
				if (!r.isZero()){
					//K.set_elem(k, j, K.get(k, j).sub(mag2.mult(r)));
					K.data[k * K.cols + j] = K.data[k * K.cols + j].sub(mag2.mult(r));
				}
			}
		}
		currentRow++;
	}		
	return rank;
}

public RationalNumber get_elem(int r, int c) {
	if (r < 0 || r >= rows){
		throw new IllegalArgumentException("r out of range <"+r+">");
	}
	if (c < 0 || c >= cols){
		throw new IllegalArgumentException("c out of range <"+c+">");
	}
	return data[c + r * cols];
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
				set_elem(i, j, RationalNumber.ONE);
			}else{
				set_elem(i, j, RationalNumber.ZERO);
			}
		}
	}			
}
public void matinv(RationalNumberMatrix a) throws MatrixException {
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
		set_elem(0, 0, a.get_elem(0, 0).inverse());
		return;
	}

	RationalNumberMatrix b = new RationalNumberMatrix(a);

	int n = rows;
	for (int i = 0; i < n; i ++){
		for (int j = 0; j < n; j ++){
			if (i == j){
				set_elem(i, j, RationalNumber.ONE);
			}else{
				set_elem(i, j, RationalNumber.ZERO);
			}
		}
	}			

	for (int i = 0; i < n; i ++){
		//
		// find pivot (any non-zero element is fine, with symbols, can't really tell magnitude anyway)
		//
		RationalNumber mag = RationalNumber.ZERO;
		int pivot = -1;
		for (int j = i; j < n; j ++){
			RationalNumber mag2 = b.get_elem(j, i);
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
			RationalNumber temp;
			for (int j = i; j < n; j ++){
				temp = b.get_elem(i, j);
				b.set_elem(i, j, b.get_elem(pivot, j));
				b.set_elem(pivot, j, temp);
			}

			for (int j = 0; j < n; j ++){
				temp = get_elem(i, j);
				set_elem(i, j, get_elem(pivot, j));
				set_elem(pivot, j, temp);
			}
		}
		//
		// normalize pivot row
		//
		mag = b.get_elem(i, i);
		for (int j = i; j < n; j ++){
			b.set_elem(i, j, b.get_elem(i, j).div(mag));
		}	
		for (int j = 0; j < n; j ++){
			set_elem(i, j, get_elem(i, j).div(mag));
		}	
		//
		// eliminate pivot row component from other rows
		//
		for (int k = 0; k < n; k ++){
			if (k == i) continue;

			RationalNumber mag2 = b.get_elem(k, i);

			for (int j = i; j < n; j ++){
				b.set_elem(k, j, b.get_elem(k, j).sub(mag2.mult(b.get_elem(i, j))));
			}	
			for (int j = 0; j < n; j ++){
				set_elem(k, j, get_elem(k, j).sub(mag2.mult(get_elem(i, j))));
			}
		}
	}
}	
public void matmul(RationalNumberMatrix a, RationalNumberMatrix b) throws MatrixException {
	if ((a.getNumCols() != b.getNumRows()) || (a.getNumRows() != getNumRows()) || (b.getNumCols() != getNumCols())){
		return;
	}	

	for (int i = 0; i < rows; i ++){
		for (int j = 0; j < cols; j ++){
			RationalNumber s = RationalNumber.ZERO;
			for (int k = 0; k < a.cols; k ++){
				s = s.add(a.get_elem(i, k).mult(b.get_elem(k, j)));
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
	data[c + r * cols] = new RationalNumber(x);
}
public void set_elem(int r, int c, long num, long den) {
	if (r < 0 || r >= rows){
		throw new IllegalArgumentException("r out of range <"+r+">");
	}
	if (c < 0 || c >= cols){
		throw new IllegalArgumentException("c out of range <"+c+">");
	}
	data[c + r * cols] = new RationalNumber(BigInteger.valueOf(num),BigInteger.valueOf(den));
}
public void set_elem(int r, int c, RationalNumber x) {
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
			set_elem(i, j, new RationalNumber(BigInteger.valueOf((long)(1000*Math.random())),BigInteger.valueOf((long)(1000*Math.random()))));
		}
	}		
}
public void set_rand_int() throws MatrixException {
	for (int i = 0; i < rows; i ++){
		for (int j = 0; j < cols; j ++){
			set_elem(i, j, new RationalNumber((long)(4*(Math.random()-0.5))));
		}
	}		
}
public void show() {
	System.out.println("Rows = " + rows + " Cols = " + cols);
	for (int i = 0; i < rows; i ++){
		StringBuffer s = new StringBuffer();
		for (int j = 0; j < cols; j ++){
			s.append(get_elem(i, j).infix());
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
public RationalNumber[] solveLinearExpressions() throws MatrixException {
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
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
public void zero() {
	for (int i = 0; i < rows; i ++){
		for (int j = 0; j < cols; j ++){
			set_elem(i, j, RationalNumber.ZERO);
		}
	}			
}
}
