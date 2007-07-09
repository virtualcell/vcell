package cbit.vcell.geometry;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.util.Coordinate;

import edu.uchc.vcell.expression.internal.*;
/**
 * This type was created in VisualAge.
 */
public class PolyLine extends SampledCurve{
	
/**
 * Insert the method's description here.
 * Creation date: (7/31/00 4:50:55 PM)
 */
public PolyLine() {
    super();
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/00 4:50:55 PM)
 */
public PolyLine(Coordinate[] controlPoints) {
    super(controlPoints);
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
