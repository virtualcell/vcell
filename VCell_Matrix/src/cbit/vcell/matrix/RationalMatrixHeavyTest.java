package cbit.vcell.matrix;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;

import org.vcell.expression.RationalExpression;
import org.vcell.expression.RationalNumber;

public class RationalMatrixHeavyTest
{
	private Random random = new Random();
private RationalMatrixHeavyTest() {
}
/**
 * Insert the method's description here.
 * Creation date: (5/5/2003 11:30:50 AM)
 * @return cbit.vcell.matrix.RationalMatrix
 * @param rows int
 * @param cols int
 */
public RationalMatrixHeavy createMatrix(int rows, int cols) {
	return new RationalMatrixHeavy(rows,cols);
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/2003 7:35:25 AM)
 * @return cbit.vcell.matrixtest.RationalMatrix
 * @param numRows int
 * @param numCols int
 */
public RationalMatrixHeavy getRandomMatrix(int numRows, int numCols) {
	RationalMatrixHeavy a = new RationalMatrixHeavy(numRows, numCols);
	set_rand_int(a);
	return a;
}
public static void main(String argv[]) {
	if (argv.length != 1){
		System.out.println("usage:  cbit.vcell.math.MatrixTest  matrixRank");
		return;
	}
		
	RationalMatrixHeavyTest app = new RationalMatrixHeavyTest();
	int n = Integer.valueOf(argv[0]).intValue();


	try {
//		app.testLINEAR_SOLVER2();
///*
		//app.testELIMINATION();
		app.testGEPASI_Brusselator();
		app.testGEPASI_hmm();
		app.testGEPASI_SignalTransduction1();
		app.testPAPER_example();
		app.testSIMPLE_example1();
		app.testUNKNOWN_example();
		app.testBORIS_example();

		app.random.setSeed(0);
		
		for (int i=0;i<30;i++){
			app.testRANDOM(20,15);  // # vars, # reaction
		}	
//*/
	}catch (Exception e){
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
public void testBORIS_example() throws Exception {
	int numVars = 3;
	int numReactions = 5;
	RationalMatrix a = createMatrix(numVars, numReactions);
	String vars[] = new String[numVars];
	int A = 0; vars[A] = "A";
	int B = 1; vars[B] = "B";
	int C = 2; vars[C] = "C";
	int r=0;
	// A = B
	a.set_elem(A,r,-1);	a.set_elem(B,r,1);  r++;
	a.set_elem(A,r,1);	a.set_elem(B,r,-1);  r++;
	// B -> C
	a.set_elem(B,r,-1);	a.set_elem(C,r,1);  r++;
	// A -> C
	a.set_elem(A,r,-1);	a.set_elem(C,r,1);  r++;
	// C -> *
	a.set_elem(C,r,-1);	  r++;

	System.out.println("============= Test Boris Example ==================================");
//	a.show();
	RationalMatrix b = a.findNullSpace();
	printResults(b,vars);
	System.out.println("------------- no dependencies ------------------");
	System.out.println("\n");
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.Matrix
 */
public void testELIMINATION() throws Exception {
	int numVars = 3;
	int numReactions = 4;
	RationalMatrixHeavy a = createMatrix(numVars, numReactions);
	String vars[] = new String[numVars];
	int A = 0; vars[A] = "A";
	int B = 1; vars[B] = "B";
	int C = 2; vars[C] = "C";
	int r=0;
	// A = B
	a.set_elem(A,r,-1);	a.set_elem(B,r,1);  r++;
	a.set_elem(A,r,1);	a.set_elem(B,r,-1);  r++;
	// B -> C
	a.set_elem(B,r,1);	a.set_elem(C,r,1);  r++;
	// A -> C
	a.set_elem(A,r,-1);	a.set_elem(C,r,1);  r++;
	// C -> *
//	a.set_elem(C,r,-1);	  r++;

	System.out.println("============= ELIMINATION ==================================");
	RationalMatrixHeavy K = createMatrix(a.getNumRows(),a.getNumRows());
	K.identity();
	int rank = a.gaussianElimination(K);
	System.out.println("RANK is "+rank);
//	Matrix b = Matrix.findDependencies(a,vars);
//	printResults(b,vars);
	System.out.println("-------------                                 ------------------");
	System.out.println("\n");
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.Matrix
 */
public void testELIMINATIONExp() throws Exception {
	int numVars = 3;
	int numReactions = 4;
	RationalExpMatrix a = new RationalExpMatrix(numVars, numReactions);
	a.zero();
	String vars[] = new String[numVars];
	int A = 0; vars[A] = "A";
	int B = 1; vars[B] = "B";
	int C = 2; vars[C] = "C";
	int r=0;
	// A = B
	a.set_elem(A,r,new RationalExpression(-1));	a.set_elem(B,r,new RationalExpression(1));  r++;
	a.set_elem(A,r,new RationalExpression(1));	a.set_elem(B,r,new RationalExpression(-1));  r++;
	// B -> C
	a.set_elem(B,r,new RationalExpression(1));	a.set_elem(C,r,new RationalExpression(1));  r++;
	// A -> C
	a.set_elem(A,r,new RationalExpression(-1));	a.set_elem(C,r,new RationalExpression(1));  r++;
	// C -> *
//	a.set_elem(C,r,-1);	  r++;

	System.out.println("============= ELIMINATION ==================================");
	RationalExpMatrix K = new RationalExpMatrix(a.rows,a.rows);
	K.identity();
	int rank = a.gaussianElimination(K);
	System.out.println("RANK is "+rank);
//	Matrix b = Matrix.findDependencies(a,vars);
//	printResults(b,vars);
	System.out.println("-------------                                 ------------------");
	System.out.println("\n");
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.Matrix
 */
public void testGEPASI_Brusselator() throws Exception {
	int numVars = 7;
	int numReactions = 8;
	RationalMatrix a = createMatrix(numVars, numReactions);
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

	System.out.println("============= GEPASI Brusselator =====================");
//	a.show();
	RationalMatrix b = a.findNullSpace();
	printResults(b,vars);
	System.out.println("------------- A + X + Y + E + F = C ------------------");
	System.out.println("------------- B + D = C             ------------------");
	System.out.println("\n");
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.RationalMatrix
 */
public void testGEPASI_hmm() throws Exception {
	int numVars = 4;
	int numReactions = 3;
	RationalMatrix a = createMatrix(numVars, numReactions);
	a.zero();
	String vars[] = new String[numVars];
	int S  = 0; vars[S]  = "S";
	int E  = 1; vars[E]  = "E";
	int ES = 2; vars[ES] = "ES";
	int P  = 3; vars[P]  = "P";
	int r=0;
	// S + E = ES
	a.set_elem(S,r,-1);		a.set_elem(E,r,-1);		a.set_elem(ES,r,1);  r++;
	a.set_elem(S,r,1);		a.set_elem(E,r,1);		a.set_elem(ES,r,-1);  r++;
	// ES -> E + P
	a.set_elem(ES,r,-1);	a.set_elem(E,r,1);		a.set_elem(P,r,1); r++;

	System.out.println("============= GEPASI  hmm ====================");
//	a.show();
	RationalMatrix b = a.findNullSpace();
	printResults(b,vars);
	System.out.println("------------- E + ES = C    ------------------");
	System.out.println("------------- S - E + P = C ------------------");
	System.out.println("\n");
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.RationalMatrix
 */
public void testGEPASI_SignalTransduction1() throws Exception {
	int numVars = 10;
	int numReactions = 12;
	RationalMatrix a = createMatrix(numVars, numReactions);
	a.zero();
	String vars[] = new String[numVars];
	int K1 = 0;
	vars[K1] = "Kinase1";
	int K1P = 1;
	vars[K1P] = "Kinase1-P";
	int K2 = 2;
	vars[K2] = "Kinase2";
	int K2P = 3;
	vars[K2P] = "Kinase2-P";
	int EI = 4;
	vars[EI] = "Enzyme-i";
	int EA = 5;
	vars[EA] = "Enzyme-a";
	int S = 6;
	vars[S] = "S";
	int M = 7;
	vars[M] = "M";
	int P1 = 8;
	vars[P1] = "P1";
	int P2 = 9;
	vars[P2] = "P2";
	int r = 0;
	// K1 = K1P
	a.set_elem(K1, r, -1);
	a.set_elem(K1P, r, 1);
	r++;
	a.set_elem(K1, r, 1);
	a.set_elem(K1P, r, -1);
	r++;
	// K2 = K2P
	a.set_elem(K2, r, -1);
	a.set_elem(K2P, r, 1);
	r++;
	a.set_elem(K2, r, 1);
	a.set_elem(K2P, r, -1);
	r++;
	// EI = EA
	a.set_elem(EI, r, -1);
	a.set_elem(EA, r, 1);
	r++;
	a.set_elem(EI, r, 1);
	a.set_elem(EA, r, -1);
	r++;
	// S = M
	a.set_elem(S, r, -1);
	a.set_elem(M, r, 1);
	r++;
	a.set_elem(S, r, 1);
	a.set_elem(M, r, -1);
	r++;
	// M = P1
	a.set_elem(M, r, -1);
	a.set_elem(P1, r, 1);
	r++;
	a.set_elem(M, r, 1);
	a.set_elem(P1, r, -1);
	r++;
	// M = P2
	a.set_elem(M, r, -1);
	a.set_elem(P2, r, 1);
	r++;
	a.set_elem(M, r, 1);
	a.set_elem(P2, r, -1);
	r++;
	System.out.println("============= GEPASI - Signal Transduction 1 ===========");
	//	a.show();
	RationalMatrix b = a.findNullSpace();
	printResults(b, vars);
	System.out.println("------------- Enzyme-i + Enzyme-a = C ------------------");
	System.out.println("------------- Kinase1 + Kinase1-P = C ------------------");
	System.out.println("------------- Kinase2 + Kinase2-P = C ------------------");
	System.out.println("------------- S + M + P1 + P2 = C     ------------------");
	System.out.println("\n");
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.RationalMatrix
 */
public void testLINEAR_SOLVER2() throws Exception {

	int numVars = 5;
	RationalMatrixHeavy A = getRandomMatrix(numVars,numVars);
A.show();
	RationalMatrixHeavy b = getRandomMatrix(numVars,1);
	b.set_rand();
b.show();
	RationalMatrixHeavy M = createMatrix(numVars, numVars+1);
	for (int i=0;i<numVars;i++){
		for (int j=0;j<numVars;j++){
			M.set_elem(i,j,A.get_elem(i,j));
		}
		M.set_elem(i,numVars,b.get_elem(i,0));
	}
M.show();

	RationalNumber x[] = M.solveLinear();
		
	for (int i=0;i<numVars;i++){
		System.out.println("X["+i+"] = "+x[i]);
	}

	RationalMatrixHeavy X = createMatrix(numVars,1);
	for (int i=0;i<numVars;i++){
		X.set_elem(i,0,x[i]);
	}

	RationalMatrixHeavy newB = createMatrix(numVars,1);
	newB.matmul(A,X);
newB.show();
b.show();
	
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.RationalMatrix
 */
public void testPAPER_example() throws Exception {
	int numVars = 6;
	int numReactions = 5;
	RationalMatrix a = createMatrix(numVars, numReactions);
	a.zero();
	String vars[] = new String[numVars];
	int X1 = 0; vars[X1] = "X1";
	int X2 = 1; vars[X2] = "X2";
	int X3 = 2; vars[X3] = "X3";
	int X4 = 3; vars[X4] = "X4";
	int X5 = 4; vars[X5] = "X5";
	int X6 = 5; vars[X6] = "X6";
	int r=0;
	// * -> X1
	a.set_elem(X1,r,1);		r++;
	// X1 + X6 -> X2
	a.set_elem(X1,r,-1);	a.set_elem(X6,r,-1);	a.set_elem(X2,r,1);		r++;
	// X2 + 2 X5 -> X3
	a.set_elem(X2,r,-1);	a.set_elem(X5,r,-2);	a.set_elem(X3,r,1);		r++;
	// X3 -> X4 + X5
	a.set_elem(X3,r,-1);	a.set_elem(X4,r,1);		a.set_elem(X5,r,1);		r++;
	// X4 -> X5 + X6 + *
	a.set_elem(X4,r,-1);	a.set_elem(X5,r,1);		a.set_elem(X6,r,1);		r++;

	System.out.println("============= Paper Example ==================================");
//	a.show();
	RationalMatrix b = a.findNullSpace();
	printResults(b,vars);
	System.out.println("------------- X2 = C - 0.5 X4 + 0.5 X5 - X6 ------------------");
	System.out.println("------------- X3 = C - 0.5 X4 - 0.5 X5      ------------------");
	System.out.println("\n");
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.Matrix
 */
public void testRANDOM(int numVars, int numReactions) throws Exception {
	RationalMatrix a = getRandomMatrix(numVars, numReactions);
	String vars[] = new String[numVars];
	for (int i=0;i<numVars;i++){
		vars[i] = "X"+(i+1);
	}	
	System.out.println("============= Random Example   ============================");
//	a.show();
	RationalMatrix b = a.findNullSpace();
	printResults(b,vars);
	System.out.println("------------- Unknown Results::: ------------------");
	System.out.println("\n");
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.RationalMatrix
 */
public void testSIMPLE_example1() throws Exception {
	int numVars = 4;
	int numReactions = 2;
	RationalMatrix a = createMatrix(numVars, numReactions);
	a.zero();
	String vars[] = new String[numVars];
	int X1 = 0; vars[X1] = "X1";
	int X2 = 1; vars[X2] = "X2";
	int X3 = 2; vars[X3] = "X3";
	int X4 = 3; vars[X4] = "X4";
	int r=0;
	// X1 + X2 -> X3
	a.set_elem(X1,r,-1);	a.set_elem(X2,r,-1);	a.set_elem(X3,r,1);  r++;
	// X2 = X4
	a.set_elem(X2,r,-1);	a.set_elem(X4,r,1);		r++;
//	a.set_elem(X2,r,1);		a.set_elem(X4,r,-1);	r++;

	System.out.println("============= SIMPLE Example 1 ==================================");
//	a.show();
	RationalMatrix b = a.findNullSpace();
	printResults(b,vars);
	System.out.println("-------------  X1 + X3 = C         -----------------");
	System.out.println("------------- -X1 + X2 + X4 = C    -----------------");
	System.out.println("\n");
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.RationalMatrix
 */
public void testUNKNOWN_example() throws Exception {
	int numVars = 6;
	int numReactions = 5;
	RationalMatrix a = createMatrix(numVars, numReactions);
	a.zero();
	String vars[] = new String[numVars];
	int X1 = 0; vars[X1] = "X1";
	int X2 = 1; vars[X2] = "X2";
	int X3 = 2; vars[X3] = "X3";
	int X4 = 3; vars[X4] = "X4";
	int X5 = 4; vars[X5] = "X5";
	int X6 = 5; vars[X6] = "X6";
	int r=0;
	// X3 -> X4
	a.set_elem(X3,r,-1);	a.set_elem(X4,r,1);  r++;
	// X1 + X4 + X6 -> X5
	a.set_elem(X1,r,-1);	a.set_elem(X4,r,-1);	a.set_elem(X6,r,-1);	a.set_elem(X5,r,1); r++;
	// X4 -> X1 + X3 + X5 + X6
	a.set_elem(X4,r,-1);	a.set_elem(X1,r,1);		a.set_elem(X3,r,1); 	a.set_elem(X5,r,1); 	a.set_elem(X6,r,1); r++;
	// * -> X2
	a.set_elem(X2,r,1); r++;
	// X1 + X5 -> X2 + X6
	a.set_elem(X1,r,-1);	a.set_elem(X5,r,-1);	a.set_elem(X2,r,1);	a.set_elem(X6,r,1); r++;

	System.out.println("============= Unknown Example ==================================");
//	a.show();
	RationalMatrix b = a.findNullSpace();
	b.show();
	printResults(b,vars);
	System.out.println("------------- -X1 + 2 X3 + 2 X4 + X5 = C ------------------");
	System.out.println("\n");
}
}
