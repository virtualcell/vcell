package cbit.vcell.simdata.gui;

import org.vcell.util.Coordinate;

import cbit.vcell.geometry.Curve;
import cbit.vcell.geometry.CurveSelectionInfo;

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
	public static String DESCRIPTION_VOLUME = "v";
	public  static String DESCRIPTION_MEMBRANE = "m";

/**
 * Insert the method's description here.
 * Creation date: (7/6/2003 8:39:58 PM)
 * @param curve cbit.vcell.geometry.Curve
 */
void curveAdded(Curve curve);
/**
 * Insert the method's description here.
 * Creation date: (7/6/2003 7:42:49 PM)
 * @param curve cbit.vcell.geometry.Curve
 */
void curveRemoved(Curve curve);
/**
 * Insert the method's description here.
 * Creation date: (1/12/2001 2:27:54 PM)
 * @return java.lang.String
 * @param csi cbit.vcell.geometry.CurveSelectionInfo
 */
String getCurveValue(CurveSelectionInfo csi);
/**
 * Insert the method's description here.
 * Creation date: (7/4/2003 6:04:41 PM)
 */
CurveSelectionInfo getInitalCurveSelection(int tool,Coordinate wc);
/**
 * Insert the method's description here.
 * Creation date: (7/4/2003 6:08:33 PM)
 */
boolean isAddControlPointOK(int tool,Coordinate wc,Curve addedToThisCurve);
/**
 * Insert the method's description here.
 * Creation date: (7/4/2003 6:09:38 PM)
 */
boolean providesInitalCurve(int tool,Coordinate wc);

void setDescription(Curve curve);
}
