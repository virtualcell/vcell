package cbit.vcell.solvers;

import cbit.vcell.resource.ResourceUtil;

/**
 * Insert the type's description here.
 * Creation date: (12/7/2005 2:11:23 PM)
 * @author: Jim Schaff
 */
public class NativeCVODESolver {
	static {
		ResourceUtil.loadNativeSolverLibrary();
    }

	private boolean bStopRequested = false;

/**
 * NativeIDASolver constructor comment.
 */
public NativeCVODESolver() {
	super();
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
 * Creation date: (12/7/2005 3:52:46 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		final NativeCVODESolver nativesolver = new NativeCVODESolver();
						
		String input = 	"STARTING_TIME 0.0\n"+
			"ENDING_TIME 100000.0\n"+
			"RELATIVE_TOLERANCE 1.0E-9\n"+
			"ABSOLUTE_TOLERANCE 1.0E-9\n"+
			"MAX_TIME_STEP 1.0\n"+
			"KEEP_EVERY 1\n"+
			"NUM_EQUATIONS 2\n"+
			"ODE species1_cyt INIT 2.0 RATE  - (species1_cyt - species2_cyt);\n"+
			"ODE species2_cyt INIT 3.0 RATE ( - ((0.01 * species2_cyt) - (6.0 - species2_cyt - species1_cyt)) + species1_cyt - species2_cyt);";	

		/*
		Thread t = new Thread() {
			public void run() {
				try {
				Thread.sleep(200);
				nativesolver.setStopRequested(true);
				} catch (Exception ex) {
				}
			}
		};

		t.start();
		
		*/
		
		System.out.println("**************Solve 2************************");
		cbit.vcell.util.RowColumnResultSet rcrs = nativesolver.solve(input);
		for (int i = 0; i < rcrs.getRowCount(); i ++) {
			double[] row = rcrs.getRow(i);
			for (int j = 0; j < row.length; j ++) {
				System.out.print(row[j] + " ");
			}
			System.out.println();
		}
	} catch (Exception ex) {
		System.out.println("\nJava caught exception: " + ex.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/7/2005 2:13:54 PM)
 * @return double[][]
 * @param idaInput java.lang.String
 */
private native cbit.vcell.util.RowColumnResultSet nativeSolve(String cvodeInput, double[] paramValues) throws Exception;


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
public cbit.vcell.util.RowColumnResultSet solve(String idaInput) throws Exception {
	return nativeSolve(idaInput, null);
}


/**
 * Insert the method's description here.
 * Creation date: (12/7/2005 2:13:54 PM)
 * @return double[][]
 * @param idaInput java.lang.String
 */
public cbit.vcell.util.RowColumnResultSet solve(String idaInput, double[] paramValues) throws Exception {
	return nativeSolve(idaInput, paramValues);
}
}