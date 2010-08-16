package org.vcell.smoldyn.model.util;

import java.util.ArrayList;

import org.vcell.smoldyn.model.Surface;



/**
 * @author mfenwick
 *
 */
public class Disk implements Panel {

	private Point center;
	private Float radius;
	private Vector normal;
	private String name;
	private ArrayList<Panel> neighbors = new ArrayList<Panel>();
	private Surface surface;
	
	
	/**
	 * @param name
	 * @param center
	 * @param radius
	 * @param normal
	 */
	public Disk(String name, Point center, float radius, Vector normal) {
		this.name = name;
		this.radius = radius;
		this.center = center;
		this.normal = normal;
	}
	
	
	public Point getCenter() {
		return center;
	}
	
	public Float getRadius() {
		return this.radius;
	}
	
	public Vector getNormal() {
		return normal;
	}
	
	/**@Override*/
	public ShapeType getShapeType() {
		return ShapeType.disk;
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
