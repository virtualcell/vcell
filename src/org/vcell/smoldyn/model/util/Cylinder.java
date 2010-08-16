package org.vcell.smoldyn.model.util;

import java.util.ArrayList;

import org.vcell.smoldyn.model.Surface;



/**
 * @author mfenwick
 *
 */
public class Cylinder implements Panel {

	private Point axisstart;
	private Point axisend;
	private Float radius;
	private Surface surface;
	private String name;
	private ArrayList<Panel> neighbors;
	
	
	/**
	 * @param name
	 * @param axisstart
	 * @param axisend
	 * @param radius
	 */
	public Cylinder(String name, Point axisstart, Point axisend, Float radius) {
		this.name = name;
		this.axisstart = axisstart;
		this.axisend = axisend;
		this.radius = radius;
	}
	
	
	public Point getAxisstart() {
		return axisstart;
	}
	
	public Point getAxisend() {
		return axisend;
	}
	
	public Float getRadius() {
		return radius;
	}


	/**
	 * @Override
	 */
	public ShapeType getShapeType() {
		return ShapeType.cyl;
	}


	/**
	 * @Override*/
	public String getName() {
		return this.name;
	}


	/**
	 * @Override*/
	public Surface getSurface() {
		return this.surface;
	}


	/**
	 * @Override*/
	public Panel[] getNeighbors() {
		return neighbors.toArray(new Panel [neighbors.size()]);
	}


	/**
	 * @Override*/
	public void setSurface(Surface surface) {
		this.surface = surface;
	}
	
}
