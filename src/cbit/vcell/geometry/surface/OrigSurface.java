package cbit.vcell.geometry.surface;

/**
 * Insert the type's description here.
 * Creation date: (6/28/2003 12:10:01 AM)
 * @author: John Wagner
 */
public class OrigSurface implements Surface, java.io.Serializable {
	private int fieldInteriorRegionIndex = 0;
	private int fieldExteriorRegionIndex = 0;
	private java.util.Vector fieldPolygons = new java.util.Vector();
/**
 * Boundary constructor comment.
 */
public OrigSurface(int interiorRegionIndex, int exteriorRegionIndex) {
	super();
	fieldInteriorRegionIndex = interiorRegionIndex;
	fieldExteriorRegionIndex = exteriorRegionIndex;
}
/**
 * Boundary constructor comment.
 */
public OrigSurface(int interiorRegionIndex, int exteriorRegionIndex, Polygon polygon) {
	super();
	fieldInteriorRegionIndex = interiorRegionIndex;
	fieldExteriorRegionIndex = exteriorRegionIndex;
	addPolygon(polygon);
}
/**
 * Boundary constructor comment.
 */
public void addPolygon(Polygon quadrilateral) {
	fieldPolygons.addElement(quadrilateral);
}
/**
 * Boundary constructor comment.
 */
public void addSurface(Surface surface) {
	boolean bReverse = !(surface.getExteriorRegionIndex() == fieldExteriorRegionIndex && surface.getInteriorRegionIndex() == fieldInteriorRegionIndex);

	if (bReverse) {
		surface.reverseDirection();
	}
	for (int i = 0; i < surface.getPolygonCount(); i++) {			
		addPolygon(surface.getPolygons(i));
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/6/2004 2:41:40 PM)
 * @return double
 */
public double getArea() {
	double area = 0.0;
	for (int i = 0; i < fieldPolygons.size(); i++){
		area += ((Polygon)fieldPolygons.elementAt(i)).getArea();
	}
	return area;
}
/**
 * Boundary constructor comment.
 */
public int getExteriorRegionIndex() {
	return(fieldExteriorRegionIndex);
}
/**
 * Boundary constructor comment.
 */
public int getInteriorRegionIndex() {
	return(fieldInteriorRegionIndex);
}
/**
 * Boundary constructor comment.
 */
public int getPolygonCount() {
	return(fieldPolygons.size());
}
/**
 * Boundary constructor comment.
 */
public Polygon getPolygons(int i) {
	return((Polygon) fieldPolygons.elementAt(i));
}
/**
 * Quadrilateral constructor comment.
 */
public void reverseDirection() {
	int regionIndex = fieldInteriorRegionIndex;
	fieldInteriorRegionIndex = fieldExteriorRegionIndex;
	fieldExteriorRegionIndex = regionIndex;
	for (int i = 0; i < getPolygonCount(); i++) {
		getPolygons(i).reverseDirection();
	}
}
}
