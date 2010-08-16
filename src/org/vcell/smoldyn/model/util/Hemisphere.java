package org.vcell.smoldyn.model.util;

import java.util.ArrayList;

import org.vcell.smoldyn.model.Surface;



/**
 * @author mfenwick
 *
 */
public class Hemisphere implements Panel {

	private Point center;
	private Float radius;
	private Vector normal;
	private String name;
	private ArrayList<Panel> neighbors = new ArrayList<Panel>();
	private Surface surface;
	
	
	/**
	 * @param center
	 * @param radius
	 * @param normal
	 */
	public Hemisphere(String name, Point center, Float radius, Vector normal) {
		this.name = name;
		this.center = center;
		this.radius = radius;
		this.normal = normal;
	}
	
	
	public Point getCenter() {
		return center;
	}
	
	public Float getRadius() {
		return radius;
	}
	
	public Vector getNormal() {
		return normal;
	}
	
	/**@Override*/
	public ShapeType getShapeType() {
		return ShapeType.hemi;
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
	public void setSurface(Surface surface) {
		this.surface = surface;
	}

	/**@Override*/
	public Panel[] getNeighbors() {
		return neighbors.toArray(new Panel [neighbors.size()]);
	}
}
