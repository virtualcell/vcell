package cbit.vcell.matrix;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;
import java.util.*;

public class RationalMatrixTest
{
//	private Random random = new Random();
//	private int matrixType = MATRIX_TYPE_HEAVY;
	private static String matrixTypeNames[] = { "RationalMatrixHeavy", "RationalNumberMatrix", "RationalMatrixFast", "RationalExpMatrix", "RationalMatrixFast_old", "RationalExpMatrix_old" };
	
	private static final int MATRIX_TYPE_HEAVY = 0;
	private static final int MATRIX_TYPE_SAFE = 1;
	private static final int MATRIX_TYPE_FAST = 2;
	private static final int MATRIX_TYPE_EXP = 3;
	private static final int MATRIX_TYPE_FAST_OLD = 4;
	private static final int MATRIX_TYPE_EXP_OLD = 5;

	private static final int NUM_MATRIX_TYPES = 3;
private RationalMatrixTest() {
}

public static void main(String argv[]) {
	if (argv.length != 1){
		System.out.println("usage:  cbit.vcell.math.MatrixTest  matrixRank");
		return;
	}
		
	try {
		RationalMatrixTest app = new RationalMatrixTest();
		int n = Integer.valueOf(argv[0]).intValue();
		//app.testLINEAR_SOLVER2();
		//app.testELIMINATION();
		Random rand = new Random(0);
		boolean includeExpMatrix_small = false;
		boolean includeExpMatrix_large = false;
		boolean bVerbose = true;
		app.testNullSpaceProblem(app.nsp_BORIS_example(),includeExpMatrix_small, bVerbose);
		app.testNullSpaceProblem(app.nsp_ELIMINATION(),includeExpMatrix_small, bVerbose);
		app.testNullSpaceProblem(app.nsp_GEPASI_Brusselator(),includeExpMatrix_small, bVerbose);
		app.testNullSpaceProblem(app.nsp_GEPASI_hmm(),includeExpMatrix_small, bVerbose);
		app.testNullSpaceProblem(app.nsp_GEPASI_SignalTransduction1(),includeExpMatrix_small, bVerbose);
		app.testNullSpaceProblem(app.nsp_PAPER_example(),includeExpMatrix_small, bVerbose);
		app.testNullSpaceProblem(app.nsp_SIMPLE_example1(),includeExpMatrix_small, bVerbose);
		app.testNullSpaceProblem(app.nsp_UNKNOWN_example(),includeExpMatrix_small, bVerbose);
		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 25, 14),includeExpMatrix_large, bVerbose);
		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 50, 25),includeExpMatrix_large, bVerbose);
		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 100, 54),includeExpMatrix_large, bVerbose);
		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 200, 154),includeExpMatrix_large, bVerbose);
		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 100,100),includeExpMatrix_large, bVerbose);
//		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 200,200),includeExpMatrix_large, bVerbose);
//		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 500,500),includeExpMatrix_large, bVerbose);
//		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 1000,1000),includeExpMatrix_large, bVerbose);
//		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 2000,2000),includeExpMatrix_large, bVerbose);
//		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 5000,5000),includeExpMatrix_large, bVerbose);
//		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 360, 240),includeExpMatrix_large, bVerbose);
//		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 360, 240),includeExpMatrix_large, bVerbose);
//		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 360, 240),includeExpMatrix_large, bVerbose);
//		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 360, 270),includeExpMatrix_large, bVerbose);
//		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 360, 270),includeExpMatrix_large, bVerbose);
//		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 360, 270),includeExpMatrix_large, bVerbose);
//		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 36, 27),includeExpMatrix_large, bVerbose);
//		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 36, 27),includeExpMatrix_large, bVerbose);
//		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 36, 27),includeExpMatrix_large, bVerbose);
//		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 36, 27),includeExpMatrix_large, bVerbose);
//		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 36, 27),includeExpMatrix_large, bVerbose);
//		app.testNullSpaceProblem(app.nsp_RANDOM(rand, 36, 27),includeExpMatrix_large, bVerbose);

		
//
//		if (run != MATRIX_TYPE_EXP){
//				for (int i=0;i<1;i++){
//					app.testRANDOM(n,n*3/4);  // # vars, # reaction
//				}
//		}
//			}catch (Exception e){
//				e.printStackTrace(System.out);
//			}
//		}
//		//app.random.setSeed(0);
//		//for (int i=0;i<1;i++){
//			//app.testRANDOM_SVD(n,n*3/4);  // # vars, # reaction
//		//}
//		for (int i=0;i<1;i++){
//			app.testRANDOM_VCell(n,n*3/4);  // # vars, # reaction
//		}
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
private static void printReactions(RationalMatrix stoichMatrix, String[] vars, boolean bVerbose) throws Exception {
	if (vars.length != stoichMatrix.getNumRows()){
		throw new Exception("varName array not same dimension as A matrix");
	}
	System.out.println(stoichMatrix.getNumRows()+" species, "+stoichMatrix.getNumCols()+" reactions (some may be null)");
	if (bVerbose){
		for (int j=0;j<stoichMatrix.getNumCols();j++){
			StringBuffer reactants = new StringBuffer();
			StringBuffer products = new StringBuffer();
			boolean bFirstReactant = true;
			boolean bFirstProduct = true;
			for (int i=0;i<stoichMatrix.getNumRows();i++){
				String varName = vars[i];
				RationalNumber coeff = stoichMatrix.get_elem(i,j);
				if (coeff.doubleValue() < 0.0){
					// reactant
					if (bFirstReactant){
						bFirstReactant=false;
					}else{
						reactants.append(" + ");
					}
					if (coeff.doubleValue() == -1.0){
						reactants.append(varName);
					}else{
						reactants.append(coeff.minus()+" "+varName);
					}
				}else if (coeff.doubleValue() > 0.0){
					// product
					if (bFirstProduct){
						bFirstProduct=false;
					}else{
						products.append(" + ");
					}
					if (coeff.doubleValue() == 1.0){
						products.append(varName);
					}else{
						products.append(coeff+" "+varName);
					}
				} // else do nothing (stoich coeff == 0)
			}
			System.out.println(reactants.toString()+" ==> "+products.toString());
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/2003 7:39:46 AM)
 * @param matrix cbit.vcell.matrixtest.RationalMatrix
 * @param vars java.lang.String[]
 */
private static void printResults(RationalMatrix nullSpaceMatrix, String[] vars) throws Exception {
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
		for (int j=0;j<nullSpaceMatrix.getNumCols();j++){
			RationalNumber coeff = nullSpaceMatrix.get_elem(i,j);
			if (coeff.doubleValue() > 0){
				if (coeff.doubleValue() == 1.0){
					System.out.print(" + "+vars[j]);
				}else{
					System.out.print(" + "+coeff+" * "+vars[j]);
				}
			}else if (coeff.doubleValue() < 0){
				if (coeff.doubleValue()==-1){
					System.out.print(" - "+vars[j]);
				}else{
					System.out.print(" - "+coeff.minus()+" * "+vars[j]);
				}
			}
		}
		System.out.println("");
	}		
}
public void set_rand_int(RationalMatrix m, Random random) {
	//
	// scale by the number of species so that a reaction has on average three participants
	//
	float threshold = 2.1f/(2.0f+m.getNumRows());
	for (int i = 0; i < m.getNumRows(); i ++){
		for (int j = 0; j < m.getNumCols(); j ++){
			if (random.nextFloat()<threshold){
				int sign = (random.nextBoolean())?(-1):(1);
				int magnitude = (int)Math.round(random.nextDouble()+0.7);
				//int magnitude = 1random.nextInt(Integer.MAX_VALUE/10000000); //1; // (int)Math.round(random.nextDouble()+0.7);
				m.set_elem(i, j, sign*magnitude);
			}
		}
	}		
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.Matrix
 */
public NullSpaceProblem nsp_BORIS_example() throws Exception {
	int numVars = 3;
	int numReactions = 5;
	RationalMatrixFast a = new RationalMatrixFast(numVars, numReactions);
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

	NullSpaceProblem nsp = new NullSpaceProblem();
	nsp.title = "Test Boris Example";
	nsp.relations = new String[] { "no dependencies" };
	nsp.stoichMatrix = a;
	nsp.vars = vars;
	return nsp;
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.Matrix
 */
public NullSpaceProblem nsp_ELIMINATION() throws Exception {
	int numVars = 3;
	int numReactions = 4;
	RationalMatrixFast a = new RationalMatrixFast(numVars, numReactions);
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

	NullSpaceProblem nsp = new NullSpaceProblem();
	nsp.title = "ELIMINATION";
	nsp.relations = new String[] { "no dependencies" };
	nsp.stoichMatrix = a;
	nsp.vars = vars;
	return nsp;
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.Matrix
 */
public void testNullSpaceProblem(NullSpaceProblem nsp, boolean bIncludeExpMatrix, boolean bVerbose) throws Exception {
	System.out.println("============= "+nsp.title+" =====================");
	
//	printReactions(nsp.stoichMatrix, nsp.vars, bVerbose);
	printReactions(nsp.stoichMatrix, nsp.vars, false);
	
	for (int i = 0; i < nsp.relations.length; i++) {
		System.out.println("------------- "+nsp.relations[i]+" ------------------");
	}
//	System.out.println("stoich matrix:");
//	nsp.stoichMatrix.show();

	Vector stoichMatVector = new Vector();
	stoichMatVector.add(new RationalMatrixHeavy(nsp.stoichMatrix));
	stoichMatVector.add(new RationalNumberMatrix(nsp.stoichMatrix));
	stoichMatVector.add(new RationalMatrixFast(nsp.stoichMatrix));
	stoichMatVector.add(new RationalExpMatrix(nsp.stoichMatrix));
	// get stoichMatrix in old form RationalMatrixFast
	stoichMatVector.add(new cbit.vcell.matrix.RationalMatrixFast(nsp.stoichMatrix));
	stoichMatVector.add(new cbit.vcell.matrix.RationalExpMatrix(nsp.stoichMatrix));
	if (bIncludeExpMatrix){
		stoichMatVector.add(new RationalExpMatrix(nsp.stoichMatrix));
	}
//	RationalMatrix[] stoichMats = stoichMatVector.toArray(new RationalMatrix[stoichMatVector.size()]);
//	RationalMatrix[] nsMats = new RationalMatrix[stoichMats.length];
	
	Vector nsMats = new Vector();
	//compareAndPrint(stoichMatVector, nsp.vars, bVerbose);

	for (int i = 0; i < stoichMatVector.size(); i++) {
		System.out.print("finding null space for "+matrixTypeNames[i]+"  ");
		long t1 = System.currentTimeMillis();
		try {
			
			if (stoichMatVector.elementAt(i) instanceof RationalMatrix) {
				RationalMatrix rm = (RationalMatrix)stoichMatVector.elementAt(i);
				nsMats.addElement(rm.findNullSpace());
			} else if (stoichMatVector.elementAt(i) instanceof cbit.vcell.matrix.RationalMatrix) {
				cbit.vcell.matrix.RationalMatrix rm_old = (cbit.vcell.matrix.RationalMatrix)stoichMatVector.elementAt(i);
				try {
					nsMats.addElement(rm_old.findNullSpace());
				}catch (Exception e){
					System.out.println("failed in 'old matrix'");
					e.printStackTrace(System.out);
				}
			}
			
			long t2 = System.currentTimeMillis();
			System.out.println("time="+(t2-t1)+" ms");
		}catch (ArithmeticException e){
			e.printStackTrace(System.out);
			nsMats.set(i, null);
		}
	}
	compareAndPrint(nsMats, nsp.vars, bVerbose);
	System.out.println("\n\n\n");
}

public static class NullSpaceProblem {
	public RationalMatrixFast stoichMatrix;
	public String[] vars;
	public String title;
	public String[] relations;
}

public NullSpaceProblem nsp_GEPASI_Brusselator(){
	NullSpaceProblem nsp = new NullSpaceProblem();

	int numVars = 7;
	int numReactions = 8;

	nsp.title = "GEPASI Brusselator";
	nsp.relations = new String[] {
			"A + X + Y + E + F = C",
			"B + D = C"};
	nsp.vars = new String[numVars];
	int A = 0; nsp.vars[A] = "A";
	int B = 1; nsp.vars[B] = "B";
	int D = 2; nsp.vars[D] = "D";
	int E = 3; nsp.vars[E] = "E";
	int F = 4; nsp.vars[F] = "F";
	int X = 5; nsp.vars[X] = "X";
	int Y = 6; nsp.vars[Y] = "Y";
	nsp.stoichMatrix = new RationalMatrixFast(numVars,numReactions);
	//nsp.stoichMatrix.zero();
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
	nsp.stoichMatrix.set_elem(A,r,-1);		nsp.stoichMatrix.set_elem(X,r,1);  r++;
	// 2X + Y -> 3X
	nsp.stoichMatrix.set_elem(Y,r,-1);		nsp.stoichMatrix.set_elem(X,r,1); r++;
	// X + B -> Y + D
	nsp.stoichMatrix.set_elem(X,r,-1);		nsp.stoichMatrix.set_elem(B,r,-1);		nsp.stoichMatrix.set_elem(Y,r,1);		nsp.stoichMatrix.set_elem(D,r,1); r++;
	// X -> E
	nsp.stoichMatrix.set_elem(X,r,-1);		nsp.stoichMatrix.set_elem(E,r,1); r++;
	// A = E
	nsp.stoichMatrix.set_elem(A,r,-1);		nsp.stoichMatrix.set_elem(E,r,1); r++;
	nsp.stoichMatrix.set_elem(E,r,-1);		nsp.stoichMatrix.set_elem(A,r,1); r++;
	// A = F
	nsp.stoichMatrix.set_elem(A,r,-1);		nsp.stoichMatrix.set_elem(F,r,1); r++;
	nsp.stoichMatrix.set_elem(F,r,-1);		nsp.stoichMatrix.set_elem(A,r,1); r++;
	
	return nsp;
}

private static void compareAndPrint(Vector b, String[] vars, boolean bVerbose) throws Exception{
	//
	// compare all solutions to "FAST" matrix
	//
	for (int i = 0; i < b.size(); i++) {
		if (b.elementAt(i)==null){
			System.out.println("emptry null space matrix for "+matrixTypeNames[i]+" (full rank)");
		}
	}
	RationalMatrix b0 = (RationalMatrix)b.elementAt(0);
	for (int i = 1; i < b.size(); i++) {
		if (b.elementAt(i) instanceof RationalMatrix) {
			RationalMatrix bi = (RationalMatrix)b.elementAt(i);
			if (b0 != null && bi!=null){
				for (int j = 0; j < bi.getNumRows(); j++) {
					for (int k = 0; k < bi.getNumCols(); k++) {
						if (!b0.get_elem(j, k).equals(bi.get_elem(j,k))){
							for (int index = 0; index < b.size(); index++) {
								System.out.println("matrix "+matrixTypeNames[index]);
								if (b.get(index)!=null){
									if (b.get(index) instanceof cbit.vcell.matrix.RationalMatrix) {
										((cbit.vcell.matrix.RationalMatrix)b.get(index)).show();
									} else {
										((RationalMatrix)b.get(index)).show();
									}
									
								}else{
									System.out.println("no results or failure");
								}
							}
							throw new RuntimeException("null space matrix different, "+matrixTypeNames[0]+"("+j+","+k+")="+b0.get_elem(j,k)+"="+b0.get_elem(j,k).floatValue()+" and "+matrixTypeNames[i]+"("+j+","+k+")="+bi.get_elem(j,k)+"="+bi.get_elem(j,k).floatValue());
						}
					}
				}
			}
		} else if (b.elementAt(i) instanceof cbit.vcell.matrix.RationalMatrix) {
			cbit.vcell.matrix.RationalMatrix bi = (cbit.vcell.matrix.RationalMatrix)b.elementAt(i);
			if (b0 != null && bi!=null){
				for (int j = 0; j < bi.getNumRows(); j++) {
					for (int k = 0; k < bi.getNumCols(); k++) {
						RationalNumber b0_elem = b0.get_elem(j,k);
						cbit.vcell.matrix.RationalNumber bi_elem_cbit = bi.get_elem(j,k);
						RationalNumber bi_elem = new RationalNumber(bi_elem_cbit.getNumBigInteger(),bi_elem_cbit.getDenBigInteger());
						if (!b0_elem.equals(bi_elem)){
							for (int index = 0; index < b.size(); index++) {
								System.out.println("matrix "+matrixTypeNames[index]);
								if (b.get(index)!=null){
									if (b.get(index) instanceof cbit.vcell.matrix.RationalMatrix) {
										((cbit.vcell.matrix.RationalMatrix)b.get(index)).show();
									} else {
										((RationalMatrix)b.get(index)).show();
									}
									
								}else{
									System.out.println("no results or failure");
								}
							}
							throw new RuntimeException("null space matrix different, "+matrixTypeNames[0]+"("+j+","+k+")="+b0.get_elem(j,k)+"="+b0.get_elem(j,k).floatValue()+" and "+matrixTypeNames[i]+"("+j+","+k+")="+bi.get_elem(j,k)+"="+bi.get_elem(j,k).floatValue());
						}
					}
				}
			}

		}
		//if (b[i]==null || b[i].get)
	}
	if (b0!=null && bVerbose){
//		b[0].show();
		printResults(b0,vars);
	}
	
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.RationalMatrix
 */
public NullSpaceProblem nsp_GEPASI_hmm() throws Exception {
	int numVars = 4;
	int numReactions = 3;
	RationalMatrixFast a = new RationalMatrixFast(numVars, numReactions);
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
	
	
	NullSpaceProblem nsp = new NullSpaceProblem();
	nsp.title = "GEPASI  hmm";
	nsp.relations = new String[] { "E + ES = C",  "S - E + P = C" };
	nsp.stoichMatrix = a;
	nsp.vars = vars;
	return nsp;
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.RationalMatrix
 */
public NullSpaceProblem nsp_GEPASI_SignalTransduction1() throws Exception {
	int numVars = 10;
	int numReactions = 12;
	RationalMatrixFast a = new RationalMatrixFast(numVars, numReactions);
	a.zero();
	String vars[] = new String[numVars];
	int K1 = 0;		vars[K1] = "Kinase1";
	int K1P = 1;	vars[K1P] = "Kinase1-P";
	int K2 = 2;		vars[K2] = "Kinase2";
	int K2P = 3;	vars[K2P] = "Kinase2-P";
	int EI = 4;		vars[EI] = "Enzyme-i";
	int EA = 5;		vars[EA] = "Enzyme-a";
	int S = 6;		vars[S] = "S";
	int M = 7;		vars[M] = "M";
	int P1 = 8;		vars[P1] = "P1";
	int P2 = 9;		vars[P2] = "P2";
	
	int r = 0;
	// K1 = K1P
	a.set_elem(K1, r, -1);	a.set_elem(K1P, r, 1);	r++;
	a.set_elem(K1, r, 1);	a.set_elem(K1P, r, -1);	r++;
	// K2 = K2P
	a.set_elem(K2, r, -1);	a.set_elem(K2P, r, 1);	r++;
	a.set_elem(K2, r, 1);	a.set_elem(K2P, r, -1);	r++;
	// EI = EA
	a.set_elem(EI, r, -1);	a.set_elem(EA, r, 1);	r++;
	a.set_elem(EI, r, 1);	a.set_elem(EA, r, -1);	r++;
	// S = M
	a.set_elem(S, r, -1);	a.set_elem(M, r, 1);	r++;
	a.set_elem(S, r, 1);	a.set_elem(M, r, -1);	r++;
	// M = P1
	a.set_elem(M, r, -1);	a.set_elem(P1, r, 1);	r++;
	a.set_elem(M, r, 1);	a.set_elem(P1, r, -1);	r++;
	// M = P2
	a.set_elem(M, r, -1);	a.set_elem(P2, r, 1);	r++;
	a.set_elem(M, r, 1);	a.set_elem(P2, r, -1);	r++;
	
	NullSpaceProblem nsp = new NullSpaceProblem();
	nsp.title = "GEPASI - Signal Transduction 1";
	nsp.relations = new String[] { 
			"Enzyme-i + Enzyme-a = C",  
			"Kinase1 + Kinase1-P = C",
			"Kinase2 + Kinase2-P = C",
			"S + M + P1 + P2 = C"};
	nsp.stoichMatrix = a;
	nsp.vars = vars;
	return nsp;
}

private RationalMatrix createMatrix(int matrixType, int rows, int columns){
	switch (matrixType){
	case MATRIX_TYPE_EXP:{
		return new RationalExpMatrix(rows,columns);
		// break;
	}
	case MATRIX_TYPE_FAST:{
		return new RationalMatrixFast(rows,columns);
		// break;
	}
	case MATRIX_TYPE_HEAVY:{
		return new RationalMatrixHeavy(rows,columns);
		// break;
	}
	case MATRIX_TYPE_SAFE:{
		return new RationalNumberMatrix(rows,columns);
		// break;
	}
	default:{
		throw new RuntimeException("matrixType "+matrixType+" unknown");
	}
	}
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.RationalMatrix
 */
public void testLINEAR_SOLVER2(int matrixType, Random random) throws Exception {

	int numVars = 5;
	RationalMatrix A = createMatrix(matrixType, numVars, numVars);
	set_rand_int(A, random);
A.show();
	RationalMatrix b = createMatrix(matrixType,numVars,1);
	set_rand_int(b, random);
b.show();
	RationalMatrix M = createMatrix(matrixType, numVars, numVars+1);
	for (int i=0;i<numVars;i++){
		for (int j=0;j<numVars;j++){
			M.set_elem(i,j,A.get_elem(i,j).getNumBigInteger().longValue(),A.get_elem(i,j).getDenBigInteger().longValue());
		}
		M.set_elem(i,numVars,b.get_elem(i,0).getNumBigInteger().longValue(),b.get_elem(i,0).getDenBigInteger().longValue());
	}
M.show();

	RationalNumber x[] = M.solveLinear();
		
	for (int i=0;i<numVars;i++){
		System.out.println("X["+i+"] = "+x[i]);
	}

	RationalMatrix X = createMatrix(matrixType,numVars,1);
	for (int i=0;i<numVars;i++){
		X.set_elem(i,0,x[i].getNumBigInteger().longValue(),x[i].getDenBigInteger().longValue());
	}

	RationalMatrix newB = createMatrix(matrixType,numVars,1);
	if (newB instanceof RationalExpMatrix){
		((RationalExpMatrix)newB).matmul((RationalExpMatrix)A,(RationalExpMatrix)X);
	}else if (newB instanceof RationalMatrixFast){
		((RationalMatrixFast)newB).matmul((RationalMatrixFast)A,(RationalMatrixFast)X);
	}else if (newB instanceof RationalMatrixHeavy){
		((RationalMatrixHeavy)newB).matmul((RationalMatrixHeavy)A,(RationalMatrixHeavy)X);
	}else if (newB instanceof RationalNumberMatrix){
		((RationalNumberMatrix)newB).matmul((RationalNumberMatrix)A,(RationalNumberMatrix)X);
	}else{
		throw new RuntimeException("unsupported matrix type : "+newB.getClass().toString());
	}
newB.show();
b.show();
	
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.RationalMatrix
 */
public NullSpaceProblem nsp_PAPER_example() throws Exception {
	int numVars = 6;
	int numReactions = 5;
	RationalMatrixFast a = new RationalMatrixFast(numVars, numReactions);
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

	NullSpaceProblem nsp = new NullSpaceProblem();
	nsp.title = "Paper Example";
	nsp.relations = new String[] { 
			"X2 = C - 0.5 X4 + 0.5 X5 - X6",  
			"X3 = C - 0.5 X4 - 0.5 X5"};
	nsp.stoichMatrix = a;
	nsp.vars = vars;
	return nsp;
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.Matrix
 */
public NullSpaceProblem nsp_RANDOM(Random random, int numVars, int numReactions) throws Exception {
	RationalMatrixFast a = new RationalMatrixFast(numVars, numReactions);
	set_rand_int(a, random);
	String vars[] = new String[numVars];
	for (int i=0;i<numVars;i++){
		vars[i] = "X"+(i+1);
	}	

	NullSpaceProblem nsp = new NullSpaceProblem();
	nsp.title = "Random Example";
	nsp.relations = new String[] { "unknown results" };
	nsp.stoichMatrix = a;
	nsp.vars = vars;
	return nsp;
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.Matrix
 */
public void testRANDOM_SVD(Random random, int matrixType, int numVars, int numReactions) throws Exception {
	RationalMatrix a = createMatrix(matrixType, numVars, numReactions);
	set_rand_int(a, random);
	String vars[] = new String[numVars];
	for (int i=0;i<numVars;i++){
		vars[i] = "X"+(i+1);
	}	
	System.out.println("============= Random <<<SVD>>> Example   ============================");
//	a.show();
	long startTime = System.currentTimeMillis();
	RationalMatrix b = SVDTest.findNullSpaceSVD(a);
	long endTime = System.currentTimeMillis();
	printResults(b,vars);
	System.out.println("------------- Unknown <<<SVD>>> Results::: ("+(endTime-startTime)+" ms) ------------------");
	System.out.println("\n");
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.Matrix
 */
public void testRANDOM_VCell(Random random, int matrixType, int numVars, int numReactions) throws Exception {
	RationalMatrix a = createMatrix(matrixType, numVars, numReactions);
	set_rand_int(a, random);
	String vars[] = new String[numVars];
	for (int i=0;i<numVars;i++){
		vars[i] = "X"+(i+1);
	}	
	System.out.println("============= Random <<<vcell>>> Example   ============================");
//	a.show();
	long startTime = System.currentTimeMillis();
	RationalMatrix b = SVDTest.findNullSpaceVCell(a);
	long endTime = System.currentTimeMillis();
	printResults(b,vars);
	System.out.println("------------- Unknown <<<vcell>>> Results::: ("+(endTime-startTime)+" ms) ------------------");
	System.out.println("\n");
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.RationalMatrix
 */
public NullSpaceProblem nsp_SIMPLE_example1() throws Exception {
	int numVars = 4;
	int numReactions = 2;
	RationalMatrixFast a = new RationalMatrixFast(numVars, numReactions);
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

	NullSpaceProblem nsp = new NullSpaceProblem();
	nsp.title = "SIMPLE Example 1";
	nsp.relations = new String[] { 
			"X1 + X3 = C",  
			"-X1 + X2 + X4 = C"};
	nsp.stoichMatrix = a;
	nsp.vars = vars;
	return nsp;
}
/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.RationalMatrix
 */
public NullSpaceProblem nsp_UNKNOWN_example() throws Exception {
	int numVars = 6;
	int numReactions = 5;
	RationalMatrixFast a = new RationalMatrixFast(numVars, numReactions);
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

	NullSpaceProblem nsp = new NullSpaceProblem();
	nsp.title = "Unknown Example";
	nsp.relations = new String[] { 
			"X1 + X3 = C",  
			"-X1 + 2 X3 + 2 X4 + X5 = C"};
	nsp.stoichMatrix = a;
	nsp.vars = vars;
	return nsp;
}
}
