package org.vcell.smoldyn.model.util;

import org.vcell.smoldyn.model.Surface;



/**
 * @author mfenwick
 *
 */
public class Rectangle implements Panel {

	private Integer perpendicular_axis;
	private Point corner;
	private Float dim1;
	private Float dim2;
	private String name;
	private Surface surface;
	
	
	/**
	 * @param name
	 * @param perpendicularAxis
	 * @param corner
	 * @param dim1
	 * @param dim2
	 */
	public Rectangle(String name, int perpendicularAxis, Point corner, float dim1, float dim2) {
		this.name = name;
		this.perpendicular_axis = perpendicularAxis;
		this.corner = corner;
		this.dim1 = dim1;
		this.dim2 = dim2;
	}
	
	
	public ShapeType getShapeType() {
		return ShapeType.rect;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Panel [] getNeighbors() {
		return null;
	}
	
	public Surface getSurface() {
		return this.surface;
	}
	
	public void setSurface(Surface surface) {
		this.surface = surface;
	}

	public Integer getPerpendicular_axis() {
		return perpendicular_axis;
	}

	public Point getCorner() {
		return corner;
	}

	public Float getDim1() {
		return dim1;
	}

	public Float getDim2() {
		return dim2;
	}
	
}
