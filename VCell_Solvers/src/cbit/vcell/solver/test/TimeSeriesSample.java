package cbit.vcell.solver.test;

/**
 * Insert the type's description here.
 * Creation date: (1/16/2003 2:24:27 PM)
 * @author: Jim Schaff
 */
public class TimeSeriesSample {
	private double fieldTime[] = null;
	private double fieldData[] = null;
/**
 * TimeSeriesSample constructor comment.
 */
public TimeSeriesSample(double time[], double data[]) {
	super();
	this.fieldTime = time;
	this.fieldData = data;
}
/**
 * Insert the method's description here.
 * Creation date: (1/16/2003 2:25:51 PM)
 * @return double[]
 */
public double[] getData() {
	return fieldData;
}
/**
 * Insert the method's description here.
 * Creation date: (1/16/2003 2:25:41 PM)
 * @return double[]
 */
public double[] getTimes() {
	return fieldTime;
}
}
