package org.vcell.smoldyn.model;

import org.vcell.smoldyn.model.util.Point;
import org.vcell.smoldyn.simulation.SmoldynException;


/**
 * Specification of the Geometry contract.  An object fulfilling this contract
 *  is responsible for Dimensionality, Boundaries, Surfaces, and Compartments.  
 *  This object allows the client to set boundaries, add and remove {@link Surface}s and {@link Compartment}s, 
 *  and presumably enforces invariant conditions (for example, that Surface names
 *  must be unique), and get the Boundaries, dimensions, Surfaces, and Compartments.  
 * 
 * @author mfenwick
 *
 */
public interface Geometryable {

	public Boundaries getBoundaries();
	
	public void addSurface(String surfacename) throws SmoldynException;
	public Surface [] getSurfaces();
	public Surface getSurface(String surfacename) throws SmoldynException;
	public boolean hasSurface(String surfacename);
	
	public void addCompartment(String compartmentname, String [] boundingsurfacenames, Point [] interiorpoints) throws SmoldynException;
	public Compartment[] getCompartments();
	public Compartment getCompartment(String compartmentname) throws SmoldynException;
	public boolean hasCompartment(String comparmentname);
}
