package cbit.vcell.matrix;

import org.vcell.expression.RationalNumber;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
			set_elem(i,j,r.getNum(),r.getDen());
		}
	}	
}
public RationalMatrixFast(int r, int c){
	numData = new long[r * c];
	denData = new long[r * c];
	rows = r;
	cols = c;

	for (int i = 0; i < rows * cols; i ++){
		numData[i] = 0;
		denData[i] = 1;
	}	
}
public RationalMatrixFast(int r, int c, long values[]){
	if (values.length != r*c){
		throw new IllegalArgumentException("value array not of proper size");
	}
	numData = new long[r * c];
	denData = new long[r * c];
	rows = r;
	cols = c;

	for (int i = 0; i < rows * cols; i ++){
		numData[i] = values[i];
		denData[i] = 1;
	}	
}
/**
 * This method was created by a SmartGuide.
 * @param mat cbit.vcell.math.Matrix
 */
public RationalMatrixFast (RationalMatrix mat) {
	this.rows = mat.getNumRows();
	this.cols = mat.getNumCols();
	this.numData = new long[rows * cols];
	this.denData = new long[rows * cols];
	for (int i = 0; i < rows; i ++){
		for (int j = 0; j < cols; j ++){
			RationalNumber r = mat.get_elem(i,j);
			set_elem(i, j, r.getNum(), r.getDen());
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
	if (r < 0 || r >= rows){
		throw new IllegalArgumentException("r out of range <"+r+">");
	}
	if (c < 0 || c >= cols){
		throw new IllegalArgumentException("c out of range <"+c+">");
	}
	int index = c + r * cols;
	
	long numerator = numData[index] * den;
	long denominator = denData[index] * num;

	long sign = (numerator*denominator < 0)?(-1):(1);
	long gcf = RationalNumber.getGreatestCommonFactor(numerator,denominator);
	numData[index] = Math.abs(numerator)*sign/gcf;
	denData[index] = Math.abs(denominator)/gcf;
}
public RationalMatrix findNullSpace() throws MatrixException {
	
	if (rows <= 1){
		throw new MatrixException("this must have more than one row");
	}
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
//	RationalMatrixFast tempMatrix = new RationalMatrixFast(nullity,nullity);
//	tempMatrix.identity();
	int numberOfConservations = newK.gaussianElimination(); // tempMatrix);
	
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
}
public int gaussianElimination() throws MatrixException {
	
	if (rows < 1){
		throw new MatrixException("this matrix must have at least one row");
	}
	int rank = 0;
	long workarray[] = new long[cols];
	//
	// traverse each column
	//
	int currentRow = 0;
	for (int currentCol = 0; (currentCol < cols) && (currentRow < rows); currentCol++){
//System.out.println("trying to eliminate column "+(currentCol+1)+" in row "+(currentRow+1));
		//
		// find pivot row
		//
		long numMag = 0;
		long denMag = 1;
		double absValueMag = Math.abs(((double)numMag)/denMag);
		
		int pivotRow = -1;
		for (int j = currentRow; j < rows; j ++){
			int j_current_index = j*cols + currentCol;
			double absValueCurr = Math.abs(((double)numData[j_current_index])/denData[j_current_index]);
			if (absValueCurr > absValueMag){
				numMag = numData[j_current_index];
				denMag = denData[j_current_index];
				absValueMag = absValueCurr;
				pivotRow = j;
			}
		}
		if (pivotRow == -1 || numMag == 0){
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
		numMag = numData[curr_curr_index];
		denMag = denData[curr_curr_index];
		
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
	
	if (getNumRows() < 1){
		throw new MatrixException("this matrix must have at least one row");
	}
	if (K.getNumRows() != getNumRows()){
		throw new MatrixException("number of rows not same for matrices K and a");
	}
	long workarray[] = new long[getNumCols()*getNumRows()];
	int rank = 0;
	//
	// traverse each column
	//
	int currentRow = 0;
	for (int currentCol = 0; (currentCol < getNumCols()) && (currentRow < getNumRows()); currentCol++){
//System.out.println("trying to eliminate column "+(currentCol+1)+" in row "+(currentRow+1));
		//
		// find pivot row
		//
		long numMag = 0;
		long denMag = 1;
		double absValueMag = 0.0;
		int pivotRow = -1;
		for (int j = currentRow; j < getNumRows(); j ++){
			int j_curr_index = j*cols + currentCol;
			double absValueCurr = Math.abs(((double)numData[j_curr_index])/((double)denData[j_curr_index]));
			if (absValueCurr > absValueMag){
				numMag = numData[j_curr_index];
				denMag = denData[j_curr_index];
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
		numMag = numData[curr_curr_index];
		denMag = denData[curr_curr_index];
		absValueMag = Math.abs(((double)numMag)/((double)denMag));

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
		RationalNumber r = a.get_elem(0, 0);
		set_elem(0, 0, r.getDen(), r.getNum());
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
		RationalNumber mag = new RationalNumber(0);
		int pivot = -1;
		for (int j = i; j < n; j ++){
			RationalNumber mag2 = b.get_elem(j, i);
			if (Math.abs(mag2.doubleValue()) > Math.abs(mag.doubleValue())){
				mag = mag2;
				pivot = j;
			}
		}
		//
		// no pivot (error)
		//
		if (pivot == -1 || mag.doubleValue() == 0){
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
		mag = b.get_elem(i, i);
		for (int j = i; j < n; j ++){
			b.div_elem(i, j, mag.getNum(), mag.getDen());
		}	
		for (int j = 0; j < n; j ++){
			div_elem(i, j, mag.getNum(), mag.getDen());
		}	
		//
		// eliminate pivot row component from other rows
		//
		for (int k = 0; k < n; k ++){
			if (k == i) continue;

			RationalNumber mag2 = b.get_elem(k, i);

			for (int j = i; j < n; j ++){
				RationalNumber r = b.get_elem(k, j).sub(mag2.mult(b.get_elem(i, j)));
				b.set_elem(k, j, r.getNum(), r.getDen());
			}	
			for (int j = 0; j < n; j ++){
				RationalNumber r = get_elem(k, j).sub(mag2.mult(get_elem(i, j)));
				set_elem(k, j, r.getNum(), r.getDen());
			}
		}
	}
}	
public void matmul(RationalMatrix a, RationalMatrix b) throws MatrixException {
	if ((a.getNumCols() != b.getNumRows()) || (a.getNumRows() != getNumRows()) || (b.getNumCols() != getNumCols())){
		return;
	}	

	for (int i = 0; i < rows; i ++){
		for (int j = 0; j < cols; j ++){
			RationalNumber s = new RationalNumber(0);
			for (int k = 0; k < a.getNumCols(); k ++){
				s = s.add(a.get_elem(i, k).mult(b.get_elem(k, j)));
			}	
			set_elem(i, j, s.getNum(), s.getDen());
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
			long denominator = scaleDen * denData[row_index];
			
			//
			// simplify and store in destRow 
			//
			long sign = (numerator*denominator < 0)?(-1):(1);
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

	long sign = (numerator*denominator < 0)?(-1):(1);
	long gcf = RationalNumber.getGreatestCommonFactor(numerator,denominator);
	numData[index] = Math.abs(numerator)*sign/gcf;
	denData[index] = Math.abs(denominator)/gcf;
}
public void show() {
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
void sub_elem(int r, int c, long num, long den) {
	if (r < 0 || r >= rows){
		throw new IllegalArgumentException("r out of range <"+r+">");
	}
	if (c < 0 || c >= cols){
		throw new IllegalArgumentException("c out of range <"+c+">");
	}
	int index = c + r * cols;
	
	long numerator = numData[index] * den - num * denData[index];
	long denominator = denData[index] * den;

	long sign = (numerator*denominator < 0)?(-1):(1);
	long gcf = RationalNumber.getGreatestCommonFactor(numerator,denominator);
	numData[index] = Math.abs(numerator)*sign/gcf;
	denData[index] = Math.abs(denominator)/gcf;
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
			long denominator = scaleDen * denData[pivotRow_index];
			
			//
			// simplify only once.and store in destRow 
			//
			long sign = (numerator*denominator < 0)?(-1):(1);
			long gcf = RationalNumber.getGreatestCommonFactor(numerator,denominator);
			numerator = Math.abs(numerator)*sign/gcf;
			denominator = Math.abs(denominator)/gcf;

			//
			// subtract from destRow entry 
			//
			numerator = numData[destRow_index] * denominator - numerator * denData[destRow_index];
			denominator = denData[destRow_index] * denominator;

			//
			// simplify only once.and store in destRow 
			//
			sign = (numerator*denominator < 0)?(-1):(1);
			gcf = RationalNumber.getGreatestCommonFactor(numerator,denominator);
			numData[destRow_index] = Math.abs(numerator)*sign/gcf;
			denData[destRow_index] = Math.abs(denominator)/gcf;
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
	for (int i = 0; i < numData.length; i ++){
		numData[i] = 0;
		denData[i] = 1;
	}			
}
}
