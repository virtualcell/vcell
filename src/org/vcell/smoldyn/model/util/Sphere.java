package org.vcell.smoldyn.model.util;

import java.util.ArrayList;

import org.vcell.smoldyn.model.Surface;



/**
 * @author mfenwick
 *
 */
public class Sphere implements Panel {

	private Float radius;
	private Point center;
	private String name;
	private Surface surface;
	private ArrayList<Panel> neighbors = new ArrayList<Panel>();
	
	
	/**
	 * @param name
	 * @param radius
	 * @param center
	 */
	public Sphere(String name, Float radius, Point center) {
		this.name = name;
		this.radius = radius;//if radius is less than 0, switches inside with outside
		this.center = center;
	}

	
	public Float getRadius() {
		return radius;
	}

	public Point getCenter() {
		return center;
	}

	/**@Override*/
	public ShapeType getShapeType() {
		return ShapeType.sph;
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
