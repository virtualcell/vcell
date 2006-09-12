package cbit.vcell.solver.test;

/**
 * Insert the type's description here.
 * Creation date: (1/16/2003 2:17:14 PM)
 * @author: Jim Schaff
 */
public class SolverODESolution extends Object implements ODESolution {
	private cbit.vcell.util.RowColumnResultSet fieldRowColumnResultSet = null;
/**
 * SolverODESolution constructor comment.
 */
public SolverODESolution(cbit.vcell.util.RowColumnResultSet rowColumnResultSet) {
	super();
	this.fieldRowColumnResultSet = rowColumnResultSet;
}
/**
 * Insert the method's description here.
 * Creation date: (1/16/2003 2:27:03 PM)
 * @return double
 * @param name java.lang.String
 * @param x double
 * @param y double
 * @param z double
 * @param t double
 */
public TimeSeriesSample getSamples(java.lang.String name) throws cbit.vcell.parser.ExpressionException {
	int dataIndex = fieldRowColumnResultSet.findColumn(name);
	int timeIndex = fieldRowColumnResultSet.findColumn("t");
	double data[] = fieldRowColumnResultSet.extractColumn(dataIndex);
	double time[] = fieldRowColumnResultSet.extractColumn(timeIndex);
	return new TimeSeriesSample(time,data);
}
}
