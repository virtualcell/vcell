package cbit.vcell.geometry;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.Coordinate;
import edu.uchc.vcell.expression.internal.*;
/**
 * This type was created in VisualAge.
 */
public class Line extends SampledCurve{
	private static final int LINE_DEFAULT_NUM_SAMPLES = 2;
/**
 * Insert the method's description here.
 * Creation date: (7/31/00 4:50:55 PM)
 */
public Line() {
    super();
}
/**
 * SampledCurve constructor comment.
 */
public Line(Coordinate beginningCoordinate, Coordinate endingCoordinate) {
	super();
	appendControlPoint(beginningCoordinate);
	appendControlPoint(endingCoordinate);
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/00 6:49:32 PM)
 */
protected int getDefaultNumSamples() {
	return LINE_DEFAULT_NUM_SAMPLES;
}
/**
 * getMaxControlPoints method comment.
 */
public int getMaxControlPoints() {
	return 2;
}
/**
 * getMinControlPoints method comment.
 */
public int getMinControlPoints() {
	return 2;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 5:39:32 PM)
 */
public boolean isClosed() {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 5:36:14 PM)
 */
public void setClosed(boolean arg_bClosed) {
	//Do nothing, we should be false by default
}
}
