package cbit.vcell.solver;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * The Abstract definition for the solver factory that creates
 * solver-related objects.
 * Creation date: (8/24/2000 11:23:26 PM)
 * @author: John Wagner
 */
public class SolverTypes {
	private static String[] fieldODESolverDescriptions = new String[] {
		SolverDescription.ForwardEuler.getName(),
		SolverDescription.RungeKutta2.getName(),
		SolverDescription.RungeKutta4.getName(),
		SolverDescription.AdamsMoulton.getName(),
		SolverDescription.RungeKuttaFehlberg.getName(),
		SolverDescription.LSODA.getName()
	};
	private static String[] fieldPDESolverDescriptions = new String[] {
		SolverDescription.FiniteVolume.getName()
	};
	private static String[] fieldStochSolverDescriptions = new String[] {
		SolverDescription.StochGibson.getName()
	};

/**
 * getODESolverDescriptions method comment.
 */
public static java.lang.String[] getODESolverDescriptions() {
	return (fieldODESolverDescriptions);
}


/**
 * getPDESolverDescriptions method comment.
 */
public static java.lang.String[] getPDESolverDescriptions() {
	return (fieldPDESolverDescriptions);
}


/**
 * Get stochasic solver(s)' description(s)
 * Creation date: (9/27/2006 9:37:49 AM)
 * @return java.lang.String[]
 */
public static String[] getStochSolverDescriptions() {
	return fieldStochSolverDescriptions;
}
}