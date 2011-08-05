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

public class SimpleMatrix implements java.io.Serializable
{
	protected int rows;
	protected int cols;
	protected double[] data;
public SimpleMatrix(double[][] rowColData){
	rows = rowColData.length;
	cols = rowColData[0].length;
	data = new double[rows*cols];

	for (int i = 0; i < rows; i ++){
		for (int j = 0; j< cols; j++){
			set_elem(i,j,rowColData[i][j]);
		}
	}	
}
public SimpleMatrix(int r, int c){
	data = new double[r * c];
	rows = r;
	cols = c;

	for (int i = 0; i < rows * cols; i ++){
		data[i] = 0;
	}	
}
/**
 * This method was created by a SmartGuide.
 * @param mat cbit.vcell.math.Matrix
 */
public SimpleMatrix (SimpleMatrix mat) {
	this.rows = mat.rows;
	this.cols = mat.cols;
	data = new double[rows * cols];
	try {
		for (int i = 0; i < rows; i ++){
			for (int j = 0; j < cols; j ++){
				set_elem(i, j, mat.get_elem(i, j));
			}
		}
	}catch (Exception e){
		e.printStackTrace(System.out);
	}				
}
public static SimpleMatrix findNullSpace(SimpleMatrix a) throws MatrixException {
	
	if (a.rows <= 1){
		throw new MatrixException("this must have more than one row");
	}
	int numVars = a.rows;
	
	SimpleMatrix b = new SimpleMatrix(a);
	SimpleMatrix K = new SimpleMatrix(numVars,numVars);
	K.identity();
	int rank = gaussianElimination(K,b);
	if (rank == numVars){
		return null;
	}
	int nullity = numVars - rank;
//	b.show();
//	K.show();

	//
	// the 'newK' matrix is the last N-rank rows of 'K' (the elimination operations of 'a')
	//
	SimpleMatrix newK = new SimpleMatrix(nullity,numVars);
	for (int i=0;i<(nullity);i++){
		for (int j=0;j<numVars;j++){
			newK.set_elem(i,j,K.get_elem(rank+i,j));
		}
	}
//	newK.show();
	SimpleMatrix tempMatrix = new SimpleMatrix(nullity,nullity);
	tempMatrix.identity();
	int numberOfConservations = gaussianElimination(tempMatrix,newK);
	
	if (numberOfConservations == 0){
		throw new MatrixException("system has "+rank+" of "+numVars+" independent vars, and no conserved groups identified");
	}

	//
	//?????? I'm not sure if this assumption is valid always
	//
	if (numberOfConservations!=nullity){
		System.out.println("SimpleMatrix.findNullSpace(), WARNING???: numberOfConservations<"+numberOfConservations+"> != nullity<"+nullity+">");
	}
	
	SimpleMatrix returnMatrix = new SimpleMatrix(numberOfConservations,numVars);	
	for (int i=0;i<numberOfConservations;i++){
		for (int j=0;j<numVars;j++){
			returnMatrix.set_elem(i,j,newK.get_elem(i,j));
		}
	}
	return returnMatrix;
}
public static int gaussianElimination(SimpleMatrix K, SimpleMatrix b) throws MatrixException {
	
	if (b.rows < 1){
		throw new MatrixException("this matrix must have at least one row");
	}
	if (K.rows != b.rows){
		throw new MatrixException("number of rows not same for matrices K and a");
	}	
	int rank = 0;
	//
	// traverse each column
	//
	int currentRow = 0;
	for (int currentCol = 0; (currentCol < b.cols) && (currentRow < b.rows); currentCol++){
//System.out.println("trying to eliminate column "+(currentCol+1)+" in row "+(currentRow+1));
		//
		// find pivot row
		//
		double mag = 0;
		int pivotRow = -1;
		for (int j = currentRow; j < b.rows; j ++){
			double mag2 = Math.abs(b.get_elem(j, currentCol));
			if (mag2 > mag){
				mag = mag2;
				pivotRow = j;
			}
		}
		if (pivotRow == -1 || mag == 0){
			//
			// no row pivot, rotate row to bottom and try next pivot
			//
//System.out.println("no row pivot found for row "+(currentRow+1)+", rotating row ");
			//
			// rotate b matrix
			//
			for (int k=currentCol;k<b.cols;k++){
				double temp = b.get_elem(currentRow,k);
				for (int j = currentRow;j<b.rows-1;j++){
					b.set_elem(j,k, b.get_elem(j+1,k));
				}
				b.set_elem(b.rows-1,k,temp);
			}
			//
			// rotate K matrix
			//		
			for (int k=0;k<K.cols;k++){
				double temp = K.get_elem(currentRow,k);
				for (int j = currentRow;j<K.rows-1;j++){
					K.set_elem(j,k, K.get_elem(j+1,k));
				}
				K.set_elem(K.rows-1,k,temp);
			}
			continue;
		}else if (pivotRow != currentRow){
			//
			// move pivot row into position
			//
//System.out.println("swapping row "+(pivotRow+1)+" for row "+(currentRow+1)+".....");			
			double temp;
			for (int j = currentCol; j < b.cols; j ++){
				temp = b.get_elem(currentRow, j);
				b.set_elem(currentRow, j, b.get_elem(pivotRow, j));
				b.set_elem(pivotRow, j, temp);
			}
			for (int j = 0; j < K.cols; j ++){
				temp = K.get_elem(currentRow, j);
				K.set_elem(currentRow, j, K.get_elem(pivotRow, j));
				K.set_elem(pivotRow, j, temp);
			}
		}
//System.out.println("normalizing pivot row "+(currentRow+1));			
		//
		// normalize pivot row
		//
		rank++;
		mag = b.get_elem(currentRow, currentCol);
		for (int j = currentCol; j < b.cols; j++){
			b.set_elem(currentRow, j, b.get_elem(currentRow, j) / mag);
		}	
		for (int j = 0; j < K.cols; j++){
			K.set_elem(currentRow, j, K.get_elem(currentRow, j) / mag);
		}	
		//
		// eliminate pivot row component from other rows
		//
//System.out.println("eliminating pivot row "+(currentRow+1)+" components");			
		for (int k = 0; k < K.rows; k ++){
			if (k == currentRow) continue;

			double mag2 = b.get_elem(k, currentCol);

			for (int j = currentCol; j < b.cols; j ++){
				b.set_elem(k, j, b.get_elem(k, j) - mag2 * b.get_elem(currentRow, j));
			}	
			for (int j = 0; j < K.cols; j ++){
				K.set_elem(k, j, K.get_elem(k, j) -	mag2 * K.get_elem(currentRow, j));
			}
		}
		currentRow++;
	}		
	return rank;
}
private static int gaussianElimination1(SimpleMatrix b) throws MatrixException {
	
	if (b.rows < 1){
		throw new MatrixException("this matrix must have at least one row");
	}
	int rank = 0;
	//
	// traverse each column
	//
	int currentRow = 0;
	for (int currentCol = 0; (currentCol < b.cols) && (currentRow < b.rows); currentCol++){
//System.out.println("trying to eliminate column "+(currentCol+1)+" in row "+(currentRow+1));
		//
		// find pivot row
		//
		double mag = 0;
		int pivotRow = -1;
		for (int j = currentRow; j < b.rows; j ++){
			double mag2 = Math.abs(b.get_elem(j, currentCol));
			if (mag2 > mag){
				mag = mag2;
				pivotRow = j;
			}
		}
		if (pivotRow == -1 || mag == 0){
			//
			// no row pivot, rotate row to bottom and try next pivot
			//
//System.out.println("no row pivot found for row "+(currentRow+1)+", rotating row ");
			//
			// rotate b matrix
			//
			for (int k=currentCol;k<b.cols;k++){
				double temp = b.get_elem(currentRow,k);
				for (int j = currentRow;j<b.rows-1;j++){
					b.set_elem(j,k, b.get_elem(j+1,k));
				}
				b.set_elem(b.rows-1,k,temp);
			}
			continue;
		}else if (pivotRow != currentRow){
			//
			// move pivot row into position
			//
//System.out.println("swapping row "+(pivotRow+1)+" for row "+(currentRow+1)+".....");			
			double temp;
			for (int j = currentCol; j < b.cols; j ++){
				temp = b.get_elem(currentRow, j);
				b.set_elem(currentRow, j, b.get_elem(pivotRow, j));
				b.set_elem(pivotRow, j, temp);
			}
		}
//System.out.println("normalizing pivot row "+(currentRow+1));			
		//
		// normalize pivot row
		//
		rank++;
		mag = b.get_elem(currentRow, currentCol);
		for (int j = currentCol; j < b.cols; j++){
			b.set_elem(currentRow, j, b.get_elem(currentRow, j) / mag);
		}	
		//
		// eliminate pivot row component from other rows
		//
//System.out.println("eliminating pivot row "+(currentRow+1)+" components");			
		for (int k = 0; k < b.rows; k ++){
			if (k == currentRow) continue;

			double mag2 = b.get_elem(k, currentCol);

			for (int j = currentCol; j < b.cols; j ++){
				b.set_elem(k, j, b.get_elem(k, j) - mag2 * b.get_elem(currentRow, j));
			}	
		}
		currentRow++;
	}		
	return rank;
}
public double get_elem(int r, int c) {
	if (r < 0 || r >= rows){
		throw new IllegalArgumentException("r out of range <"+r+">");
	}
	if (c < 0 || c >= cols){
		throw new IllegalArgumentException("c out of range <"+c+">");
	}
	return data[c + r * cols];
}
/**
 * Insert the method's description here.
 * Creation date: (5/5/00 12:56:34 AM)
 * @return double[]
 */
public double[][] getDataCopy() {
	double D[][] = new double[rows][cols];
	for (int i=0;i<rows;i++){
		for (int j=0;j<cols;j++){
			D[i][j] = get_elem(i,j);
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
				set_elem(i, j, 1);
			}else{
				set_elem(i, j, 0);
			}
		}
	}			
}
public void matinv(SimpleMatrix a) throws MatrixException {
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
		set_elem(0, 0, 1 / a.get_elem(0, 0));
		return;
	}

	SimpleMatrix b = new SimpleMatrix(a);

	int n = rows;
	for (int i = 0; i < n; i ++){
		for (int j = 0; j < n; j ++){
			if (i == j){
				set_elem(i, j, 1);
			}else{
				set_elem(i, j, 0);
			}
		}
	}			

	for (int i = 0; i < n; i ++){
		//
		// find pivot
		//
		double mag = 0;
		int pivot = -1;
		for (int j = i; j < n; j ++){
			double mag2 = Math.abs(b.get_elem(j, i));
			if (mag2 > mag){
				mag = mag2;
				pivot = j;
			}
		}
		//
		// no pivot (error)
		//
		if (pivot == -1 || mag == 0){
			return;
		}
		//
		// move pivot row into position
		//
		if (pivot != i){
			double temp;
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
			b.set_elem(i, j, b.get_elem(i, j) / mag);
		}	
		for (int j = 0; j < n; j ++){
			set_elem(i, j, get_elem(i, j) / mag);
		}	
		//
		// eliminate pivot row component from other rows
		//
		for (int k = 0; k < n; k ++){
			if (k == i) continue;

			double mag2 = b.get_elem(k, i);

			for (int j = i; j < n; j ++){
				b.set_elem(k, j, b.get_elem(k, j) - mag2 * b.get_elem(i, j));
			}	
			for (int j = 0; j < n; j ++){
				set_elem(k, j, get_elem(k, j) -	mag2 * get_elem(i, j));
			}
		}
	}
}	
public void matmul(SimpleMatrix a, SimpleMatrix b) throws MatrixException {
	if ((a.cols != b.rows) || (a.rows != rows) || (b.cols != cols)){
		return;
	}	

	for (int i = 0; i < rows; i ++){
		for (int j = 0; j < cols; j ++){
			double s = 0;
			for (int k = 0; k < a.cols; k ++){
				s += a.get_elem(i, k) * b.get_elem(k, j);
			}	
			set_elem(i, j, s);
		}
	}	
}
public void set_elem(int r, int c, double x) {
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
			set_elem(i, j, Math.random());
		}
	}		
}
public void set_rand_int() throws MatrixException {
	for (int i = 0; i < rows; i ++){
		for (int j = 0; j < cols; j ++){
			set_elem(i, j, (int)(4*(Math.random()-0.5)));
		}
	}		
}
public void show() throws MatrixException {
	System.out.println("Rows = " + rows + " Cols = " + cols);
	for (int i = 0; i < rows; i ++){
		StringBuffer s = new StringBuffer();
		for (int j = 0; j < cols; j ++){
			s.append(get_elem(i, j));
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
 * @exception java.lang.Exception The exception description.
 */
public static double[] solveLinear(SimpleMatrix A) throws MatrixException {
	if (A.rows<1 || A.cols!=A.rows+1){
		throw new MatrixException("bad argument, A is "+A.rows+" by "+A.cols);
	}
	int numVars = A.rows;
	
	int rank = SimpleMatrix.gaussianElimination1(A);
	if (rank != numVars){
		A.show();
		throw new MatrixException("singular matrix");
	}

	double x[] = new double[numVars];
	
	for (int i=0;i<numVars;i++){
		x[i] = A.get_elem(i,numVars);
	}

	return x;
}
/**
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
public void zero() throws MatrixException {
	for (int i = 0; i < rows; i ++){
		for (int j = 0; j < cols; j ++){
			set_elem(i, j, 0);
		}
	}			
}
}
