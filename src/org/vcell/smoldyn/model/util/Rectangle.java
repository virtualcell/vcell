package org.vcell.smoldyn.model.util;

import org.vcell.smoldyn.model.Surface;
import org.vcell.smoldyn.simulation.SimulationUtilities;



/**
 * @author mfenwick
 *
 */
public class Rectangle implements Panel {

	private final boolean sign;
	private final int perpendicular_axis;
	private final Point corner;
	private final double dim1;
	private final double dim2;
	private final String name;
	private Surface surface;
	
	
	/**
	 * Constructs a new Smoldyn rectangle.
	 * 
	 * @param name
	 * @param perpendicularAxis
	 * @param corner
	 * @param dim1
	 * @param dim2
	 */
	public Rectangle(String name, boolean sign, int perpendicularAxis, Point corner, double dim1, double dim2) {
		SimulationUtilities.checkForNull("name or point", name, corner);
		SimulationUtilities.checkForPositive("rectangle dimensions", dim1, dim2);
		SimulationUtilities.assertIsTrue("perpendicular axis must be 0, 1, or 2", perpendicularAxis >= 0 && perpendicularAxis <= 2);
		this.name = name;
		this.sign = sign;
		this.perpendicular_axis = perpendicularAxis;
		this.corner = corner;
		this.dim1 = dim1;
		this.dim2 = dim2;
	}
	
	public boolean isSign() {
		return this.sign;
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

	public int getPerpendicular_axis() {
		return perpendicular_axis;
	}

	public Point getCorner() {
		return corner;
	}

	public double getDim1() {
		return dim1;
	}

	public double getDim2() {
		return dim2;
	}
	
}
