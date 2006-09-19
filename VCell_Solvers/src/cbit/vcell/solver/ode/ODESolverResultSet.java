package cbit.vcell.solver.ode;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.math.*;
/**
 *  This will have a list of Variables (NB: ReservedVariable.TIME is a ReservedVariable,
 *  and a ReservedVariable is a Variable...also, StateVariables are NOT Variables, but
 *  they are equivalent to them, and indeed are constructed from them.  So, the cols of
 *  this class will be represented by a vector of Variables, and the rows will be a
 *  vector of double[]...
 *  This guy probably has some synchronization problems...
 */
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:59:02 PM)
 * @author: John Wagner
 */
public class ODESolverResultSet extends RowColumnResultSet implements java.io.Serializable {
	public static final String TIME_COLUMN = ReservedVariable.TIME.getName();
/**
 * SimpleODEData constructor comment.
 *  JMW : THIS NEEDS TO BE FIXED...THIS CONSTRUCTOR SHOULD NOT
 *  BE DOING ANY COLUMN CONSTRUCTION AT ALL!
 */
public ODESolverResultSet() {
	super();
}
}
