package org.vcell.smoldyn.model;


import java.util.Hashtable;

import org.vcell.smoldyn.model.Model.Dimensionality;
import org.vcell.smoldyn.model.util.Point;
import org.vcell.smoldyn.simulation.SimulationUtilities;



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
	private final Hashtable<String, Compartment> compartments = new Hashtable<String, Compartment>();
	private final Hashtable<String, Surface> surfaces = new Hashtable<String, Surface>();
	private final Boundaries boundaries;
	
	
	/**
	 * Instantiates a new Geometry with the specified number of dimensions.
	 * 
	 * @param dimensions {@link Dimensionality} the number of spatial dimensions
	 * @throws NullPointerException if {@link Dimensionality} is null
	 * @throws IllegalArgumentException if dimensions not equal to 3
	 */
	public Geometry(Boundaries boundaries) {
		SimulationUtilities.checkForNull("Geometry constructor parameter", boundaries);
		this.boundaries = boundaries;
		
	}

	
	public Boundaries getBoundaries() {
		return this.boundaries;
	}

	/**
	 * Adds a {@link Compartment} to the Model.  Name must be unique--an exception
	 * will be thrown if there is already a compartment with the given name.
	 * 
	 * @param compartmentname String
	 * @throws NullPointerException if compartment is null
	 * @throws RuntimeException if compartment with the specified name already exists
	 */
	public void addCompartment(String compartmentname, String [] boundingsurfacenames, Point [] interiorpoints) {
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
	 * @throws RuntimeException if no compartment with the specified name
	 * 
	 */
	public Compartment getCompartment(String compartmentname) {
		if(compartmentname == null) {//implies that the Compartment is the entire simulation volume
			return null;
		}
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
	 * @throws NullPointerException if surface is null
	 * @throws RuntimeException if surface name is already present
	 * @throws RuntimeException if surface is already present
	 */
	public void addSurface(String surfacename) {
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
	 * @throws RuntimeException if no surface with the specified name, AND an exception
	 * is requested
	 * 
	 */
	public Surface getSurface(String surfacename) {
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
