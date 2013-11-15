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
import java.util.Arrays;

@SuppressWarnings("serial")
public class RationalMatrixFast implements RationalMatrix, java.io.Serializable
{
	private int rows;
	private int cols;
	private long numData[];
	private long denData[];
	
	
public RationalMatrixFast(RationalNumber[][] rowColData){
	rows = rowColData.length;
	cols = rowColData[0].length;
	numData = new long[rows*cols];
	denData = new long[rows*cols];

	for (int i = 0; i < rows; i ++){
		for (int j = 0; j< cols; j++){
			RationalNumber r = rowColData[i][j];
			BigInteger num = r.getNumBigInteger();
			long numLong = num.longValue();
			BigInteger den = r.getDenBigInteger();
			long denLong = den.longValue();
			if (num.equals(BigInteger.valueOf(numLong)) && den.equals(BigInteger.valueOf(denLong))){
				set_elem(i,j,numLong,denLong);
			}else{
				throw new IllegalArgumentException("RationalMatrixFast cannot represent element("+i+","+j+")="+r+" as ratio of longs");
			}
		}
	}	
}
public RationalMatrixFast(int r, int c){
	rows = r;
	cols = c;
	numData = new long[r * c];
	denData = new long[r * c];
	Arrays.fill(numData, 0L);
	Arrays.fill(denData, 1L);
}

public RationalMatrixFast(int r, int c, long values[]){
	if (values.length != r*c){
		throw new IllegalArgumentException("value array not of proper size");
	}
	rows = r;
	cols = c;
	numData = values.clone();
	denData = new long[r * c];
	Arrays.fill(denData, 1L);
}

public RationalMatrixFast transpose(){
	RationalMatrixFast transposedMatrix = new RationalMatrixFast(cols, rows);
	for (int i = 0; i < rows; i++) {
		for (int j = 0; j < cols; j++) {
			int transposedIndex = transposedMatrix.get_index(j, i);
			int thisIndex = this.get_index(i, j);
			transposedMatrix.numData[transposedIndex] = this.numData[thisIndex];
			transposedMatrix.denData[transposedIndex] = this.denData[thisIndex];
		}
	}
	return transposedMatrix;
}
/**
 * This method was created by a SmartGuide.
 * @param mat cbit.vcell.math.Matrix
 */
public RationalMatrixFast (RationalMatrix mat) {
	this.rows = mat.getNumRows();
	this.cols = mat.getNumCols();
	this.numData = new long[rows*cols];
	this.denData = new long[rows*cols];

	if (mat instanceof RationalMatrixFast){
		RationalMatrixFast otherMat = (RationalMatrixFast)mat;
		System.arraycopy(otherMat.numData, 0, numData, 0, numData.length);
		System.arraycopy(otherMat.denData, 0, denData, 0, denData.length);
	}else{
		for (int i = 0; i < rows; i ++){
			for (int j = 0; j < cols; j ++){
				RationalNumber r = mat.get_elem(i,j);
				BigInteger num = r.getNumBigInteger();
				long numLong = num.longValue();
				BigInteger den = r.getDenBigInteger();
				long denLong = den.longValue();
				if (num.equals(BigInteger.valueOf(numLong)) && den.equals(BigInteger.valueOf(denLong))){
					set_elem(i,j,numLong,denLong);
				}else{
					throw new IllegalArgumentException("RationalMatrixFast cannot represent element("+i+","+j+")="+r.infix()+" as ratio of longs");
				}
			}
		}
	}
}
/**
 * This method was created by a SmartGuide.
 * @param mat cbit.vcell.math.Matrix
 */
public RationalMatrixFast (RationalMatrixFast mat) {
	this.rows = mat.getNumRows();
	this.cols = mat.getNumCols();
	this.numData = new long[rows*cols];
	this.denData = new long[rows*cols];
	System.arraycopy(mat.numData,0,this.numData,0,rows*cols);
	System.arraycopy(mat.denData,0,this.denData,0,rows*cols);
}

void div_elem(int r, int c, long num, long den) {
	if (den==0L){
		throw new IllegalArgumentException("RationalMatrixFast.div_elem(): argument has zero denominator");
	}
	if (num==0L){
		throw new ArithmeticException("RationalMatrixFast.div_elem(): divide by zero");
	}
	int index_r_c = get_index(r, c);
	long numerator = numData[index_r_c] * den;
	if (numerator/den != numData[index_r_c]){
		throw new ArithmeticException("RationalMatrixFast.div_elem(): overflow");
	}
	long denominator = denData[index_r_c] * num;
	if (denominator/num != denData[index_r_c]){
		throw new ArithmeticException("RationalMatrixFast.div_elem(): overflow");
	}

	long sign = (numerator<0 != denominator<0)?(-1):(1);
	long gcf = RationalNumber.getGreatestCommonFactor(numerator,denominator);
	numData[index_r_c] = Math.abs(numerator)*sign/gcf;
	denData[index_r_c] = Math.abs(denominator)/gcf;
}

public RationalMatrix findNullSpace() throws MatrixException {
	
	try {
//		if (rows <= 1){
//			throw new MatrixException("this must have more than one row");
//		}
		int numVars = rows;
		RationalMatrixFast b = new RationalMatrixFast(this);
		RationalMatrixFast K = new RationalMatrixFast(numVars,numVars);
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
		RationalMatrixFast newK = new RationalMatrixFast(nullity,numVars);
		System.arraycopy(K.numData,	rank*K.cols,	newK.numData,	0,	nullity*K.cols);
		System.arraycopy(K.denData,	rank*K.cols,	newK.denData,	0,	nullity*K.cols);
	
		//	newK.show();
		int numberOfConservations = newK.gaussianElimination();
		
		if (numberOfConservations == 0){
			throw new MatrixException("system has "+rank+" of "+numVars+" independent vars, and no conserved groups identified");
		}
	
		//
		//?????? I'm not sure if this assumption is valid always
		//
		if (numberOfConservations!=nullity){
			System.out.println("Matrix.findNullSpace(), WARNING???: numberOfConservations<"+numberOfConservations+"> != nullity<"+nullity+">");
			RationalMatrixFast returnMatrix = new RationalMatrixFast(numberOfConservations,numVars);
			// copy first 'numberOfConservations' rows of newK into returnMatrix
			System.arraycopy(newK.numData,0,returnMatrix.numData,0,numberOfConservations*returnMatrix.cols);
			System.arraycopy(newK.denData,0,returnMatrix.denData,0,numberOfConservations*returnMatrix.cols);
			return returnMatrix;
		}else{
			return newK;
		}
	} catch (ArithmeticException e){
		//e.printStackTrace(System.out);
		System.out.println("arithmetic exception, trying RationalNumberMatrix.findNullSpace()");
		RationalNumberMatrix heavy = new RationalNumberMatrix(this);
		return heavy.findNullSpace();
	}
}
public int gaussianElimination() throws MatrixException {
	
	if (rows < 1){
		throw new MatrixException("this matrix must have at least one row");
	}
	int rank = 0;
	long workarray[] = new long[rows*cols];
	//
	// traverse each column
	//
	int currentRow = 0;
	for (int currentCol = 0; (currentCol < cols) && (currentRow < rows); currentCol++){
//System.out.println("trying to eliminate column "+(currentCol+1)+" in row "+(currentRow+1));
		//
		// find pivot row
		//
		double absValueMag = 0.0;
		int pivotRow = -1;
		for (int j = currentRow; j < rows; j ++){
			int j_current_index = j*cols + currentCol;
			double absValueCurr = Math.abs(((double)numData[j_current_index])/(double)denData[j_current_index]);
			if (absValueCurr > absValueMag){
				absValueMag = absValueCurr;
				pivotRow = j;
			}
		}
		if (pivotRow == -1 || absValueMag == 0){
			//
			// no row pivot, rotate row to bottom and try next pivot
			//
//System.out.println("no row pivot found for row "+(currentRow+1)+", rotating row ");
			//
			// rotate b matrix
			//
			rotate_row_to_bottom(currentRow,workarray);
			continue;
		}else if (pivotRow != currentRow){
			//
			// move pivot row into position
			//
//System.out.println("swapping row "+(pivotRow+1)+" for row "+(currentRow+1)+".....");			
			swap_rows(currentRow,pivotRow,currentCol,workarray);
		}
//System.out.println("normalizing pivot row "+(currentRow+1));			
		//
		// normalize pivot row
		//
		rank++;
		int curr_curr_index = currentRow*cols + currentCol;
		long numMag = numData[curr_curr_index];
		long denMag = denData[curr_curr_index];
		
		scale_row(currentRow,currentCol,denMag,numMag);
		//
		// eliminate pivot row component from other rows
		//
//System.out.println("eliminating pivot row "+(currentRow+1)+" components");			
		for (int k = 0; k < rows; k ++){
			if (k == currentRow) continue;
			
			int k_current_index = k*cols + currentCol;
			long numPivot = numData[k_current_index];
			long denPivot = denData[k_current_index];
			sub_scaled_row(currentRow,currentCol,numPivot,denPivot,k);
		}
		currentRow++;
	}		
	return rank;
}
public int gaussianElimination(RationalMatrixFast K) throws MatrixException {
	
	if (rows < 1){
		throw new MatrixException("this matrix must have at least one row");
	}
	if (K.rows != rows){
		throw new MatrixException("number of rows not same for matrices K and a");
	}
	long workarray[] = new long[cols*rows];
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
		double absValueMag = 0.0;
		int pivotRow = -1;
		for (int j = currentRow; j < rows; j ++){
			int j_current_index = j*cols + currentCol;
			double absValueCurr = Math.abs(((double)numData[j_current_index])/((double)denData[j_current_index]));
			if (absValueCurr > absValueMag){
				absValueMag = absValueCurr;
				pivotRow = j;
			}
		}
		if (pivotRow == -1 || absValueMag == 0){
			//
			// no row pivot, rotate row to bottom and try next pivot
			//
//System.out.println("no row pivot found for row "+(currentRow+1)+", rotating row ");
			rotate_row_to_bottom(currentRow,workarray);
			K.rotate_row_to_bottom(currentRow,workarray);
			continue;
		}else if (pivotRow != currentRow){
			//
			// move pivot row into position
			//
			swap_rows(currentRow,pivotRow,currentCol,workarray);
			K.swap_rows(currentRow,pivotRow,0,workarray);
		}
//System.out.println("normalizing pivot row "+(currentRow+1));			
		//
		// normalize pivot row
		//
		rank++;

		int curr_curr_index = currentRow*cols + currentCol;
		long numMag = numData[curr_curr_index];
		long denMag = denData[curr_curr_index];

		scale_row(currentRow,currentCol,denMag,numMag);
		K.scale_row(currentRow,0,denMag,numMag);

		//
		// eliminate pivot row component from other rows
		//
//System.out.println("eliminating pivot row "+(currentRow+1)+" components");			
		for (int k = 0; k < K.getNumRows(); k ++){
			if (k == currentRow) continue;

			int k_curr_index = k*cols + currentCol;
			long numPivot = numData[k_curr_index];
			long denPivot = denData[k_curr_index];

			sub_scaled_row(currentRow,currentCol,numPivot,denPivot,k);
			K.sub_scaled_row(currentRow,0,numPivot,denPivot,k);

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
	int index = c + r * cols;
	return new RationalNumber(numData[index], denData[index]);
}

private int get_index(int r, int c) {
	if (r < 0 || r >= rows){
		throw new IllegalArgumentException("r out of range <"+r+">");
	}
	if (c < 0 || c >= cols){
		throw new IllegalArgumentException("c out of range <"+c+">");
	}
	int index = c + r * cols;
	return index;
}
public long get_num(int r, int c) {
	if (r < 0 || r >= rows){
		throw new IllegalArgumentException("r out of range <"+r+">");
	}
	if (c < 0 || c >= cols){
		throw new IllegalArgumentException("c out of range <"+c+">");
	}
	int index = c + r * cols;
	return numData[index];
}
/**
 * Insert the method's description here.
 * Creation date: (5/5/00 12:56:34 AM)
 * @return double[]
 */
public RationalNumber[][] getDataCopy() {
	RationalNumber D[][] = new RationalNumber[rows][cols];
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
	Arrays.fill(numData, 0L);
	Arrays.fill(denData, 1L);
	for (int i = 0; i < rows; i ++){
		set_elem(i, i, 1);
	}			
}

public void matinv(RationalMatrixFast a) throws MatrixException {
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
		int index_0_0 = a.get_index(0,0);
		set_elem(0, 0, a.denData[index_0_0], a.numData[index_0_0]);
		return;
	}

	RationalMatrixFast b = new RationalMatrixFast(a);

	int n = rows;
	long workarray[] = new long[n];
	identity();

	for (int i = 0; i < n; i ++){
		//
		// find pivot
		//
		double magValue = 0.0;
		int pivot = -1;
		for (int j = i; j < n; j ++){
			int index_j_i = b.get_index(j, i);
			long mag2Num = b.numData[index_j_i];
			long mag2Den = b.denData[index_j_i];
			double mag2Value = ((double)mag2Num)/((double)mag2Den);
			if (Math.abs(mag2Value) > Math.abs(magValue)){
				magValue = mag2Value;
				pivot = j;
			}
		}
		//
		// no pivot (error)
		//
		if (pivot == -1 || magValue == 0){
			return;
		}
		//
		// move pivot row into position
		//
		if (pivot != i){
			// pivot b
			b.swap_rows(i,pivot,i,workarray);
			swap_rows(i,pivot,0,workarray);
		}
		//
		// normalize pivot row
		//
		int index_i_i = b.get_index(i, i);
		long magNum = b.numData[index_i_i];
		long magDen = b.denData[index_i_i];
		for (int j = i; j < n; j ++){
			b.div_elem(i, j, magNum, magDen);
		}	
		for (int j = 0; j < n; j ++){
			div_elem(i, j, magNum, magDen);
		}	
		//
		// eliminate pivot row component from other rows
		//
		for (int k = 0; k < n; k ++){
			if (k == i) continue;
			int index_k_i = b.get_index(k, i);
			long mag2Num = b.numData[index_k_i];
			long mag2Den = b.denData[index_k_i];

			b.sub_scaled_row(i, i, mag2Num, mag2Den, k);
			sub_scaled_row(i, 0, mag2Num, mag2Den, k);
		}
	}
}	

public void matmul(RationalMatrixFast a, RationalMatrixFast b) throws MatrixException {
	if ((a.getNumCols() != b.getNumRows()) || (a.getNumRows() != getNumRows()) || (b.getNumCols() != getNumCols())){
		return;
	}	

	for (int i = 0; i < rows; i ++){
		for (int j = 0; j < cols; j ++){
			RationalNumber s = new RationalNumber(0);
			for (int k = 0; k < a.getNumCols(); k ++){
				s = s.add(a.get_elem(i, k).mult(b.get_elem(k, j)));
			}
			if (!s.getNumBigInteger().equals(BigInteger.valueOf(s.getNumBigInteger().longValue()))){
				throw new ArithmeticException("RationalMatrixFast.matmul(): overflow");
			}
			if (!s.getDenBigInteger().equals(BigInteger.valueOf(s.getDenBigInteger().longValue()))){
				throw new ArithmeticException("RationalMatrixFast.matmul(): overflow");
			}
			set_elem(i, j, s.getNumBigInteger().longValue(), s.getDenBigInteger().longValue());
		}
	}	
}

/**
 * Insert the method's description here.
 * Creation date: (5/5/2003 3:46:05 PM)
 * @param row1 int
 * @param row2 int
 */
void rotate_row_to_bottom(int row1, long workarea[]) {
	int row1Offset = row1*cols;
	int row2Offset = (row1+1)*cols;
	int lastRowOffset = (rows-1)*cols;
	int length = lastRowOffset-row1Offset;
	//
	// swap numerator data
	//
	System.arraycopy(numData,	row1Offset,	workarea,	0,				cols);
	System.arraycopy(numData,	row2Offset,	numData,	row1Offset,		length);
	System.arraycopy(workarea,	0,			numData,	lastRowOffset,	cols);
	//
	// swap denominator data
	//
	System.arraycopy(denData,	row1Offset,	workarea,	0,				cols);
	System.arraycopy(denData,	row2Offset,	denData,	row1Offset,		length);
	System.arraycopy(workarea,	0,			denData,	lastRowOffset,	cols);
}
/**
 * Insert the method's description here.
 * Creation date: (5/6/2003 3:31:15 PM)
 * @param pivotRow int
 * @param columnOffset int
 * @param scaleNum long
 * @param scaleDen long
 * @param destRow int
 */
void scale_row(int row, int columnOffset, long scaleNum, long scaleDen) {
	//
	// row[j] = scale * row[j]
	//
	int row_index = row*cols + columnOffset;
	for (int j = columnOffset; j < cols; j ++){
		if (numData[row_index] != 0){
			//
			// multiply
			//
			long numerator   = scaleNum * numData[row_index];
			if (numerator/scaleNum!=numData[row_index]){
				throw new ArithmeticException("RationalMatrixFast.scale_row(): overflow");
			}
			long denominator = scaleDen * denData[row_index];
			if (denominator/scaleDen!=denData[row_index]){
				throw new ArithmeticException("RationalMatrixFast.scale_row(): overflow");
			}
			
			//
			// simplify and store in destRow 
			//
			long sign = (numerator<0 != denominator<0)?(-1):(1);
			long gcf = RationalNumber.getGreatestCommonFactor(numerator,denominator);
			numData[row_index] = Math.abs(numerator)*sign/gcf;
			denData[row_index] = Math.abs(denominator)/gcf;
		}
		row_index++;
	}
}

public void set_elem(int r, int c, long x) {
	if (r < 0 || r >= rows){
		throw new IllegalArgumentException("r out of range <"+r+">");
	}
	if (c < 0 || c >= cols){
		throw new IllegalArgumentException("c out of range <"+c+">");
	}
	int index = c + r * cols;
	numData[index] = x;
	denData[index] = 1;
}

public void set_elem(int r, int c, long numerator, long denominator) {
	if (r < 0 || r >= rows){
		throw new IllegalArgumentException("r out of range <"+r+">");
	}
	if (c < 0 || c >= cols){
		throw new IllegalArgumentException("c out of range <"+c+">");
	}
	int index = c + r * cols;

	long sign = (numerator<0 != denominator<0)?(-1):(1);
	long gcf = RationalNumber.getGreatestCommonFactor(numerator,denominator);
	numData[index] = Math.abs(numerator)*sign/gcf;
	denData[index] = Math.abs(denominator)/gcf;
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
    if (getNumRows() < 1 || getNumCols() != getNumRows() + 1) {
        throw new MatrixException(
            "bad argument, A is " + getNumRows() + " by " + getNumCols());
    }
    int numVars = getNumRows();

    int rank = gaussianElimination();
    if (rank != numVars) {
        show();
        throw new MatrixException("singular matrix");
    }

    RationalNumber x[] = new RationalNumber[numVars];

    for (int i = 0; i < numVars; i++) {
        x[i] = get_elem(i, numVars);
    }

    return x;
}

void sub_elem(int index, long num, long den) {
	long thisDen = denData[index];
	long thisNum = numData[index];
	
	long numerator;
	long denominator;
	if (den == thisDen){
		numerator = thisNum - num;
		if (numerator+num!=thisNum){
			throw new ArithmeticException("RationalMatrixFast.sub_elem(): overflow");
		}
		denominator = thisDen;
	}else{
		long numeratorA = thisNum * den;
		if (numeratorA/den!=thisNum){
			throw new ArithmeticException("RationalMatrixFast.sub_elem(): overflow");
		}
		long numeratorB = num * thisDen;
		if (numeratorB/num!=thisDen){
			throw new ArithmeticException("RationalMatrixFast.sub_elem(): overflow");
		}
		numerator = numeratorA - numeratorB;
		if (numerator+numeratorA!=numeratorB){
			throw new ArithmeticException("RationalMatrixFast.sub_elem(): overflow");
		}
		denominator = thisDen * den;
		if (denominator/den!=thisDen){
			throw new ArithmeticException("RationalMatrixFast.sub_elem(): overflow");
		}
	}
	if (numerator==0){
		numData[index] = 0L;
		denData[index] = 1L;
	}else{
		long sign = (numerator<0 != denominator<0)?(-1):(1);
		long gcf = RationalNumber.getGreatestCommonFactor(numerator,denominator);
		numData[index] = Math.abs(numerator)*sign/gcf;
		denData[index] = Math.abs(denominator)/gcf;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/6/2003 3:31:15 PM)
 * @param pivotRow int
 * @param columnOffset int
 * @param scaleNum long
 * @param scaleDen long
 * @param destRow int
 */
void sub_scaled_row(int pivotRow, int columnOffset, long scaleNum, long scaleDen, int destRow) {
	//
	// destRow[j] = destRow[j] - scale * pivotRow[j]
	//
	int pivotRow_index = pivotRow*cols + columnOffset;
	int destRow_index = destRow*cols + columnOffset;
	for (int j = columnOffset; j < cols; j ++){
		if (numData[pivotRow_index] != 0){
			//
			// get scaled pivotRow entry
			//
			long numerator   = scaleNum * numData[pivotRow_index];
			if (scaleNum!=0 && numerator/scaleNum!=numData[pivotRow_index]){
				throw new ArithmeticException("overflow error in RationalMatrixFast.sub_scaled_row");
			}
			long denominator = scaleDen * denData[pivotRow_index];
			if (scaleDen!=0 && denominator/scaleDen!=denData[pivotRow_index]){
				throw new ArithmeticException("overflow error in RationalMatrixFast.sub_scaled_row");
			}
			//
			// subtract from destRow entry 
			//
			sub_elem(destRow_index, numerator, denominator);
		}
		pivotRow_index++;
		destRow_index++;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/5/2003 3:46:05 PM)
 * @param row1 int
 * @param row2 int
 */
void swap_rows(int row1, int row2, int columnOffset, long workarea[]) {
	int row1Offset = row1*cols + columnOffset;
	int row2Offset = row2*cols + columnOffset;
	int length = cols - columnOffset;
	//
	// swap numerator data
	//
	System.arraycopy(numData,	row1Offset,	workarea,	0,			length);
	System.arraycopy(numData,	row2Offset,	numData,	row1Offset,	length);
	System.arraycopy(workarea,	0,			numData,	row2Offset,	length);
	//
	// swap denominator data
	//
	System.arraycopy(denData,	row1Offset,	workarea,	0,			length);
	System.arraycopy(denData,	row2Offset,	denData,	row1Offset,	length);
	System.arraycopy(workarea,	0,			denData,	row2Offset,	length);
}
/**
 * This method was created by a SmartGuide.
 * @exception java.lang.Exception The exception description.
 */
public void zero() {
	Arrays.fill(numData,0L);
	Arrays.fill(denData,1L);
}
}
