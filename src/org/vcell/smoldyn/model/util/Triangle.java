package org.vcell.smoldyn.model.util;

import java.util.ArrayList;

import org.vcell.smoldyn.model.Surface;



/**
 * @author mfenwick
 *
 */
public class Triangle implements Panel {

	private Point point1;
	private Point point2;
	private Point point3;
	private String name;
	private Surface surface;
	private ArrayList<Panel> neighbors;

	/**
	 * A triangle has points at its three corners.
	 * 
	 * @param name String
	 * @param point1 Point
	 * @param point2 Point
	 * @param point3 Point
	 */
	public Triangle(String name, Point point1, Point point2, Point point3) {
		this.name = name;
		this.point1 = point1;
		this.point2 = point2;
		this.point3 = point3;
	}
	
	public Point getPoint1() {
		return point1;
	}
	
	public Point getPoint2() {
		return point2;
	}
	
	public Point getPoint3() {
		return point3;
	}
	
	/**
	 * WARNING:  this function may actually return the real points, and not copies of them, making it DANGEROUS to change their values!
	 * Figure out this Java issue!
	 * 
	 * @return {@link Point} [] -- the three points at the vertices of the triangle
	 */
	public Point [] getPoints() {
		return new Point [] {point1, point2, point3};
	}

	/**@Override*/
	public ShapeType getShapeType() {
		return ShapeType.tri;
	}

	/**@Override*/
	public String getName() {
		return this.name;
	}

	/**@Override*/
	public Surface getSurface() {
		return this.surface;
	}

	/**@Override*/
	public Panel[] getNeighbors() {
		return neighbors.toArray(new Panel [neighbors.size()]);
	}

	/**@Override*/
	public void setSurface(Surface surface) {
		this.surface = surface;
	}
}
