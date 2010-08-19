package org.vcell.smoldyn.model;


import java.util.HashSet;
import java.util.Hashtable;

import org.vcell.smoldyn.model.util.Point;
import org.vcell.smoldyn.simulation.SimulationUtilities;



/**
 * A Smoldyn compartment is a volume region where reactions can occur and particles are located.
 * 
 * <p>
 * <h2>Smoldyn Compartments</h2>
 * <ul>
 * <li>A compartment is a region of volume bounded by a {@link Surface}(s). It does not include its bounding surfaces. Compartments 
 * are useful for input or output and zeroth and first order reactions can be made to be only active within specified compartments. Also, 
 * they are used for communication with the MOOSE simulator</li>
 * <li>The inside of a compartment is defined to be all points from which one can draw a straight line to one of the “inside-defining 
 * points” without crossing any bounding surface. For example, to create a spherical compartment, one would define a spherical surface as 
 * the boundary and some point inside the sphere (the center, or any other internal point) to be the inside-defining point. This 
 * definition allows a wide variety of options. For example, it allows disjoint compartments and compartments that are not inside closed
 * surfaces. To set a sharp edge to a compartment, but one which does not affect molecule diffusion, just add a surface that is transparent 
 * to all molecules but which serves as one of the compartment’s bounding surfaces</li>
 * <li>compartments can be composed from previously defined compartments using logic arguments. This way, for example, a cell 
 * cytoplasm compartment can be defined as the region that is within a cell compartment but that is not also within a nucleus compartment. 
 * Or, the region that is outside of a cell can be simply defined as the region that is not inside the cell (note: this has NOT been
 * modeled yet  // TODO )</li>
 * </ul>
 * 
 * A compartment is a subunit of volume within a model.  A compartment is 
 * defined by its bounding surfaces (see {@link Surface}).  A compartment 
 * also has interior points, which are used to define the inside and outside
 * of the compartment.  TODO figure out how Smoldyn determines which is the 
 * front, and which is the back....
 * 
 * Invariants:  name is not null
 * 		all interior points and bounding surfaces are not null
 * 		the name of each bounding surface is unique with respect to the other bounding surfaces
 * 		the String under which each bounding surface is hashed is identical to the name of the bounding surface
 * 		the name may not be changed
 * 
 * @author mfenwick
 *
 */
public class Compartment{
	private final String name;
	private final Hashtable<String, Surface> boundingsurfaces = new Hashtable<String, Surface>();
	private final HashSet<Point> interiorpoints = new HashSet<Point>();

	
	/**
	 * Instantiates a Smoldyn Compartment with the given name.
	 * 
	 * @param name -- not null
	 * @param boundingsurfaces -- not null
	 * @param interiorpoints -- not null
	 */
	public Compartment(String name, Surface [] boundingsurfaces, Point [] interiorpoints){
		SimulationUtilities.checkForNull("argument to Compartment constructor", name, boundingsurfaces, interiorpoints);
		this.name = name;
		for(Surface surface : boundingsurfaces) {
			SimulationUtilities.checkForNull("surface", surface);
			this.boundingsurfaces.put(surface.getName(), surface);
		}
		for(Point point : interiorpoints) {
			SimulationUtilities.checkForNull("point", point);
			this.interiorpoints.add(point);
		}
	}
	
	public Point[] getPoints(){
		return interiorpoints.toArray(new Point[interiorpoints.size()]);
	}
	
	public Surface [] getSurfaces(){
		return boundingsurfaces.values().toArray(new Surface [this.boundingsurfaces.size()]);
	}

	public String getName() {
		return name;
	}
}


