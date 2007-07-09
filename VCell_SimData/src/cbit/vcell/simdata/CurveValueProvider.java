package cbit.vcell.simdata;

import org.vcell.util.Coordinate;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (1/12/2001 2:26:46 PM)
 * @author: Frank Morgan
 */
public interface CurveValueProvider {
/**
 * Insert the method's description here.
 * Creation date: (7/6/2003 8:39:58 PM)
 * @param curve cbit.vcell.geometry.Curve
 */
void curveAdded(cbit.vcell.geometry.Curve curve);
/**
 * Insert the method's description here.
 * Creation date: (7/6/2003 7:42:49 PM)
 * @param curve cbit.vcell.geometry.Curve
 */
void curveRemoved(cbit.vcell.geometry.Curve curve);
/**
 * Insert the method's description here.
 * Creation date: (1/12/2001 2:27:54 PM)
 * @return java.lang.String
 * @param csi cbit.vcell.geometry.CurveSelectionInfo
 */
String getCurveValue(cbit.vcell.geometry.CurveSelectionInfo csi);
/**
 * Insert the method's description here.
 * Creation date: (7/4/2003 6:04:41 PM)
 */
cbit.vcell.geometry.CurveSelectionInfo getInitalCurveSelection(int tool,Coordinate wc);
/**
 * Insert the method's description here.
 * Creation date: (7/4/2003 6:08:33 PM)
 */
boolean isAddControlPointOK(int tool,Coordinate wc,cbit.vcell.geometry.Curve addedToThisCurve);
/**
 * Insert the method's description here.
 * Creation date: (7/4/2003 6:09:38 PM)
 */
boolean providesInitalCurve(int tool,Coordinate wc);
}
