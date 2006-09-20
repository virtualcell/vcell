package cbit.vcell.matrix;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;
import java.util.*;

public class RationalMatrixFastTest
{
	private Random random = new Random();
	boolean bHeavy = true;
private RationalMatrixFastTest() {
}
/**
 * Insert the method's description here.
 * Creation date: (5/5/2003 11:30:50 AM)
 * @return cbit.vcell.matrix.RationalMatrix
 * @param rows int
 * @param cols int
 */
public RationalMatrix createMatrix(int rows, int cols) {
	return new RationalMatrixFast(rows,cols);
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/2003 7:35:25 AM)
 * @return cbit.vcell.matrixtest.RationalMatrix
 * @param numRows int
 * @param numCols int
 */
public RationalMatrix getRandomMatrix(int numRows, int numCols) {
	RationalMatrix a = new RationalMatrixHeavy(numRows, numCols);
	set_rand_int(a);
	return a;
}
public static void main(String argv[]) {
	try {
		if (argv.length != 1){
			System.out.println("usage:  cbit.vcell.math.MatrixTest  matrixRank");
			return;
		}
			
		RationalMatrixFastTest app = new RationalMatrixFastTest();
		int n = Integer.valueOf(argv[0]).intValue();


		app.test_SwapRows();
		app.test_RotateRows();
		app.test_GaussianElimination();
		app.test_SubScaledRow();
		app.test_ScaledRow();

		//for (int run = 0; run < 2; run++){
			//try {
				//if (run==0){
					//System.out.println("testing RationalMatrixHeavy");
					//app.bHeavy = true;
				//}else{
					//System.out.println("testing RationalMatrixFast");
					//app.bHeavy = false;
				//}
				////app.testLINEAR_SOLVER2();
				////app.testELIMINATION();
				//app.testGEPASI_Brusselator();
				//app.testGEPASI_hmm();
				//app.testGEPASI_SignalTransduction1();
				//app.testPAPER_example();
				//app.testSIMPLE_example1();
				//app.testUNKNOWN_example();
				//app.testBORIS_example();

				//app.random.setSeed(0);
				
				//for (int i=0;i<30;i++){
					//app.testRANDOM(20,15);  // # vars, # reaction
				//}
			//}catch (Exception e){
				//e.printStackTrace(System.out);
			//}
		//}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/2003 7:39:46 AM)
 * @param matrix cbit.vcell.matrixtest.RationalMatrix
 * @param vars java.lang.String[]
 */
private void printResults(RationalMatrix nullSpaceMatrix, String[] vars) throws Exception {
	if (nullSpaceMatrix == null){
		System.out.println("the matrix has full rank, there are no dependencies");
		return;
	}
		
	if (vars.length != nullSpaceMatrix.getNumCols()){
		throw new Exception("varName array not same dimension as b matrix");
	}
				
	System.out.println("there are "+nullSpaceMatrix.getNumRows()+" dependencies");
//	b.show();
	for (int i=0;i<nullSpaceMatrix.getNumRows();i++){
		//
		// find first variable
		//
		boolean bFirst = false;
		for (int j=0;j<nullSpaceMatrix.getNumCols();j++){
			RationalNumber coeff = nullSpaceMatrix.get_elem(i,j).minus();
			if (!bFirst && coeff.doubleValue() != 0.0){
				System.out.print(vars[j]+" = K_"+vars[j]+" ");
				bFirst = true;
			}else if (coeff.doubleValue() != 0.0){
				System.out.print("  +  "+coeff+" * "+vars[j]);
			}	
		}
		System.out.println("");
	}		
}
public void set_rand_int(RationalMatrix m) {
	for (int i = 0; i < m.getNumRows(); i ++){
		for (int j = 0; j < m.getNumCols(); j ++){
			m.set_elem(i, j, (int)(2.2*(random.nextDouble()-0.5)),1);
		}
	}		
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.Matrix
 */
public void test_GaussianElimination() throws Exception {
	long workarray[] = new long[10];
	long values[] = {
		 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
		10,11,12,13,14,15,16,17,18,19,
		20,21,22,23,24,25,26,27,28,29,
		30,31,32,33,34,35,36,37,38,39,
		40,41,42,43,44,45,46,47,48,49,
		50,51,52,53,54,55,56,57,58,59,
		60,61,62,63,64,65,66,67,68,69,
		70,71,72,73,74,75,76,77,78,79,
		80,81,82,83,84,85,86,87,88,89,
		90,91,92,93,94,95,96,97,98,99
	};

	System.out.println("============= ELIMINATION ==================================");
	RationalMatrix a = new RationalMatrixFast(10,10,values);
	a.show();
	System.out.println("eliminating using RationalMatrixFast");
	int rank = a.gaussianElimination();
	System.out.println("rank = "+rank);
	a.show();
	a = new RationalMatrixHeavy(new RationalMatrixFast(10,10,values));	
	System.out.println("eliminating using RationalMatrixHeavy");
	rank = a.gaussianElimination();
	System.out.println("rank = "+rank);
	a.show();	
	System.out.println("-------------                                 ------------------");
	System.out.println("\n");
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.Matrix
 */
public void test_RotateRows() throws Exception {
	long workarray[] = new long[10];
	long values[] = {
		 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
		10,11,12,13,14,15,16,17,18,19,
		20,21,22,23,24,25,26,27,28,29,
		30,31,32,33,34,35,36,37,38,39,
		40,41,42,43,44,45,46,47,48,49,
		50,51,52,53,54,55,56,57,58,59,
		60,61,62,63,64,65,66,67,68,69,
		70,71,72,73,74,75,76,77,78,79,
		80,81,82,83,84,85,86,87,88,89,
		90,91,92,93,94,95,96,97,98,99
	};

	RationalMatrixFast a = new RationalMatrixFast(10,10,values);
	System.out.println("============= ROW ROTATING TO BOTTOM ==================================");
	a.show();
	System.out.println("Rotating row 0 to bottom");
	a.rotate_row_to_bottom(0,workarray);
	a.show();
	
	a = new RationalMatrixFast(10,10,values);
	System.out.println("Rotating row 1 to bottom");
	a.rotate_row_to_bottom(1,workarray);
	a.show();
	
	a = new RationalMatrixFast(10,10,values);
	System.out.println("Rotating row 8 to bottom");
	a.rotate_row_to_bottom(8,workarray);
	a.show();
	
	a = new RationalMatrixFast(10,10,values);
	System.out.println("Rotating row 9 to bottom");
	a.rotate_row_to_bottom(9,workarray);
	a.show();
	
	System.out.println("-------------                                 ------------------");
	System.out.println("\n");
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.Matrix
 */
public void test_ScaledRow() throws Exception {
	long workarray[] = new long[10];
	long values[] = {
		 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
		10,11,12,13,14,15,16,17,18,19,
		20,21,22,23,24,25,26,27,28,29,
		30,31,32,33,34,35,36,37,38,39,
		40,41,42,43,44,45,46,47,48,49,
		50,51,52,53,54,55,56,57,58,59,
		60,61,62,63,64,65,66,67,68,69,
		70,71,72,73,74,75,76,77,78,79,
		80,81,82,83,84,85,86,87,88,89,
		90,91,92,93,94,95,96,97,98,99
	};

	RationalMatrixFast a = new RationalMatrixFast(10,10,values);
	System.out.println("============= SCALED ROW ==================================");
	a.show();
	System.out.println("scaling row 0 by 2/3 starting at column 0");
	a.scale_row(0,0,2,3);
	a.show();
	
	a = new RationalMatrixFast(10,10,values);
	System.out.println("scaling row 2 by 5 starting at column 5");
	a.scale_row(2,5,5,1);
	a.show();
	
	a = new RationalMatrixFast(10,10,values);
	System.out.println("scaling row 8 by 2 starting at column 7");
	a.scale_row(8,7,2,1);
	a.show();
	System.out.println("-------------                                 ------------------");
	System.out.println("\n");
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.Matrix
 */
public void test_SubScaledRow() throws Exception {
	long workarray[] = new long[10];
	long values[] = {
		 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
		10,11,12,13,14,15,16,17,18,19,
		20,21,22,23,24,25,26,27,28,29,
		30,31,32,33,34,35,36,37,38,39,
		40,41,42,43,44,45,46,47,48,49,
		50,51,52,53,54,55,56,57,58,59,
		60,61,62,63,64,65,66,67,68,69,
		70,71,72,73,74,75,76,77,78,79,
		80,81,82,83,84,85,86,87,88,89,
		90,91,92,93,94,95,96,97,98,99
	};

	RationalMatrixFast a = new RationalMatrixFast(10,10,values);
	System.out.println("============= SUBTRACTING SCALED ROW ==================================");
	a.show();
	System.out.println("subtracting 1 * row 0 from row 1 all columns");
	a.sub_scaled_row(0,0,1,1,1);
	a.show();
	
	a = new RationalMatrixFast(10,10,values);
	System.out.println("subtracting 1 * row 0 from row 1 starting at column 5");
	a.sub_scaled_row(0,5,1,1,1);
	a.show();
	
	a = new RationalMatrixFast(10,10,values);
	System.out.println("subtracting 2/3 * row 0 from row 9");
	a.sub_scaled_row(0,0,2,3,9);
	a.show();
	System.out.println("-------------                                 ------------------");
	System.out.println("\n");
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.Matrix
 */
public void test_SwapRows() throws Exception {
	long workarray[] = new long[10];
	long values[] = {
		 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
		10,11,12,13,14,15,16,17,18,19,
		20,21,22,23,24,25,26,27,28,29,
		30,31,32,33,34,35,36,37,38,39,
		40,41,42,43,44,45,46,47,48,49,
		50,51,52,53,54,55,56,57,58,59,
		60,61,62,63,64,65,66,67,68,69,
		70,71,72,73,74,75,76,77,78,79,
		80,81,82,83,84,85,86,87,88,89,
		90,91,92,93,94,95,96,97,98,99
	};

	RationalMatrixFast a = new RationalMatrixFast(10,10,values);
	System.out.println("============= ROW SWAPPING ==================================");
	a.show();
	System.out.println("swapping rows 3 and 7 with offset 0");
	a.swap_rows(3,7,0,workarray);
	a.show();
	
	a = new RationalMatrixFast(10,10,values);
	System.out.println("swapping rows 3 and 9 with offset 1");
	a.swap_rows(3,9,1,workarray);
	a.show();
	
	a = new RationalMatrixFast(10,10,values);
	System.out.println("swapping rows 0 and 9 with offset 5");
	a.swap_rows(0,9,5,workarray);
	a.show();
	
	a = new RationalMatrixFast(10,10,values);
	System.out.println("swapping rows 0 and 5 with offset 9");
	a.swap_rows(0,5,9,workarray);
	a.show();
	
	System.out.println("-------------                                 ------------------");
	System.out.println("\n");
}
}
