package cbit.vcell.geometry;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class CompositeLine extends CompositeCurve {
/**
 * CompositeLine constructor comment.
 */
public CompositeLine() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void addCurve(Curve curve) {
	// cbit.util.Assertion.assert(curve instanceof Line);
	super.addCurve(curve);
}
/**
 * This method was created in VisualAge.
 */
public void addLine(Line line) {
	addCurve(line);
}
/**
 * This method was created in VisualAge.
 */
public Line getLine(int i) {
	return ((Line) getCurve(i));
}
/**
 * This method was created in VisualAge.
 */
public int getLineCount() {
	return (getCurveCount());
}
/**
 * This method was created in VisualAge.
 */
public void insertCurve(Curve curve, int i) {
	// cbit.util.Assertion.assert(curve instanceof Line);
	super.insertCurve(curve, i);
}
/**
 * This method was created in VisualAge.
 */
public void insertLine(Line line, int i) {
	insertCurve(line, i);
}
/**
 * This method was created in VisualAge.
 */
public void removeLine (int i) {
	removeCurve (i);
}
/**
 * This method was created in VisualAge.
 */
public void removeLine (Line line) {
	removeCurve (line);
}
}
