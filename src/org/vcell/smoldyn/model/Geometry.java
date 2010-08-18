package org.vcell.smoldyn.model;


import java.util.Hashtable;

import org.vcell.smoldyn.model.util.Point;
import org.vcell.smoldyn.model.util.Rectangle;
import org.vcell.smoldyn.simulation.SimulationUtilities;
import org.vcell.smoldyn.simulation.SmoldynException;



/**
 * The Geometry includes {@link Compartment}s and {@link Surface}s, both of which are stored in Hashtables and hashed by name,
 * thus ensuring that names are unique for each of them (although a Compartment may have the same name as a Surface).
 * Null compartments and surfaces can not be added (a property of a Hashtable).  The Geometry is also responsible for the number of
 * dimensions and for boundaries along those dimensions.
 * 
 * <p>
 * <strong class="warning">Important:  this is not currently a Smoldyn concept.  Smoldyn just sees boundaries, surfaces, 
 * and compartments.</strong>
 * </p>
 * 
 * Invariants:
 * 		the dimensionality must be 3 (at the moment)
 * 		the dimensionality must not be changed (at the moment -- listener events????)
 * 		the boundaries must not be changed (at the moment)
 * 		each compartment name must be unique with respect to the other compartments
 * 		each surface name must be unique with respect to the other surfaces
 * 		the string under which each compartment is hashed must be the same as that returned by the compartment's getName() method (and
 * 			the same must hold for surfaces)
 * 		the number of boundary objects must be equal to the dimensionality as an integer
 * 		each compartment and surface must not lie outside the volume defined by the boundaries
 * 
 * @author mfenwick
 *
 */
public class Geometry implements Geometryable {
	public static final String SIMULATIONBOUNDINGSURFACE = "boundingwalls";
	public static final String SIMULATIONENCLOSINGCOMPARTMENT = "enclosingcompartment";
	private final Hashtable<String, Compartment> compartments = new Hashtable<String, Compartment>();
	private final Hashtable<String, Surface> surfaces = new Hashtable<String, Surface>();
	private final Boundaries boundaries;
	
	
	/**
	 * Instantiates a new Geometry with the specified boundaries, also adding a Surface and a Compartment which exactly match the
	 * boundaries.
	 * 
	 * @param boundaries Boundaries
	 */
	public Geometry(Boundaries boundaries) {
		SimulationUtilities.checkForNull("Geometry constructor parameter", boundaries);
		this.boundaries = boundaries;
		Surface boundingsurface = new Surface(SIMULATIONBOUNDINGSURFACE);
		int i = 0;
		for(double [] wall : dims2Rects(boundaries.getXlow(), boundaries.getXhigh(), boundaries.getYlow(), boundaries.getYhigh(),
				boundaries.getZlow(), boundaries.getZhigh())) {
			boundingsurface.addPanel(new Rectangle("simulationboundingrectangle" + i, i % 2 == 0, (int) wall[0], 
					new Point(wall[1], wall[2], wall[3]), wall[4], wall[5]));
			i++;
		}
		this.surfaces.put(boundingsurface.getName(), boundingsurface);
		Compartment enclosingcompartment = new Compartment(SIMULATIONENCLOSINGCOMPARTMENT, new Surface [] {boundingsurface}, 
				new Point [] {getInteriorPoint()});
		this.compartments.put(enclosingcompartment.getName(), enclosingcompartment);
	}

	
	private Point getInteriorPoint() {
		return new Point((boundaries.getXhigh() - boundaries.getXlow()) / 2, (boundaries.getYhigh() - boundaries.getYlow()) / 2, 
				(boundaries.getZhigh() - boundaries.getZlow()) / 2);
	}
	
	private static double [] [] dims2Rects(double xlow, double xhigh, double ylow, double yhigh, double zlow, double zhigh) {
		final double xwidth = xhigh - xlow;
		final double ywidth = yhigh - ylow;
		final double zwidth = zhigh - zlow;
		//value interpretation: perpendicular axis, xcoordinate of corner point, ycoordinate, zcoordinate, 
			//extent in first dimension not equal to perpendicular axis, extent in second non-perp dimension
		final double [] x1 = {0, xlow, ylow, zlow, ywidth, zwidth};
		final double [] x2 = {0, xhigh, ylow, zlow, ywidth, zwidth};//should be a negative in front of the first 0
		final double [] y1 = {1, xlow, ylow, zlow, xwidth, zwidth};
		final double [] y2 = {1, xlow, yhigh, zlow, xwidth, zwidth};//should be a negative in front of the first 1
		final double [] z1 = {2, xlow, ylow, zlow, xwidth, ywidth};
		final double [] z2 = {2, xlow, ylow, zhigh, xwidth, ywidth};//should be a negative in front of the first 2
		final double [][] walls = {x1, x2, y1, y2, z1, z2};
		return walls;
	}
	
	public Boundaries getBoundaries() {
		return this.boundaries;
	}

	/**
	 * Adds a {@link Compartment} to the Model.  Name must be unique--an exception
	 * will be thrown if there is already a compartment with the given name.
	 * 
	 * @param compartmentname String
	 * @throws SmoldynException if compartment with the specified name already exists
	 */
	public void addCompartment(String compartmentname, String [] boundingsurfacenames, Point [] interiorpoints) throws SmoldynException {
		if (hasCompartment(compartmentname)) {
			SimulationUtilities.throwAlreadyHasKeyException("compartment name (name was: <" + compartmentname + ">)");
		}
		Surface [] boundingsurfaces = new Surface [boundingsurfacenames.length];
		for(int i = 0; i < boundingsurfacenames.length; i++) {
			boundingsurfaces[i] = this.getSurface(boundingsurfacenames[i]);
		}
		Compartment compartment = new Compartment(compartmentname, boundingsurfaces, interiorpoints);
		this.compartments.put(compartment.getName(), compartment);
	}
	
	public Compartment[] getCompartments() {
		return compartments.values().toArray(new Compartment[compartments.size()]);
	}
	
	/**
	 * Gets the {@link Compartment} with the specified name.  Due to the internal use of
	 * a Hashtable, a null name will cause an exception.  
	 * 
	 * @param compartmentname String
	 * @return compartment Compartment
	 * @throws SmoldynException if no compartment with the specified name
	 * 
	 */
	public Compartment getCompartment(String compartmentname) throws SmoldynException {
		Compartment compartment = this.compartments.get(compartmentname);
		if (compartment == null) {
			SimulationUtilities.throwNoAssociatedValueException("compartment (name was: <" + compartmentname + ">)");
		}
		return compartment;
	}
	
	public boolean hasCompartment(String compartmentname) {
		if(this.compartments.get(compartmentname) == null) {
			return false;
		}
		return true;
	}
	
	
	
	/**
	 * Requests the addition of a {@link Surface} to the Model.  Name must be unique--an exception
	 * will be thrown if there is already a surface with the given name.
	 * 
	 * @param surfacename String
	 * @throws SmoldynException if surface name is already present
	 */
	public void addSurface(String surfacename) throws SmoldynException {
		if (this.hasSurface(surfacename)) {
			SimulationUtilities.throwAlreadyHasKeyException("surface name (name was: <" + surfacename + ">)");
		}
		Surface surface = new Surface(surfacename);
		this.surfaces.put(surface.getName(), surface);
	}
	
	/**
	 * Gets the {@link Surface} with the specified name.  Due to the internal use of
	 * a Hashtable, a null name will cause an exception.  If no exception is requested,
	 * a null value may be returned.  If an exception is requested, an Exception will
	 * be thrown only if there is no surface with the specified name.
	 * 
	 * @param surfacename String
	 * @return surface Surface
	 * @throws SmoldynException if no surface with the specified name
	 * 
	 */
	public Surface getSurface(String surfacename) throws SmoldynException {
		Surface surface = this.surfaces.get(surfacename);
		if (surface == null) {
			SimulationUtilities.throwNoAssociatedValueException("surface (name was: <" + surfacename + ">)");
		}
		return surface;
	}
	
	public boolean hasSurface(String surfacename) {
		if(this.surfaces.get(surfacename) == null) {
			return false;
		}
		return true;
	}
	
	public Surface[] getSurfaces() {
		return surfaces.values().toArray(new Surface[surfaces.size()]);
	}
}
