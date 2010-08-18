package org.vcell.smoldyn.model;

import org.vcell.smoldyn.model.SpeciesState.StateType;
import org.vcell.smoldyn.model.util.Point;
import org.vcell.smoldyn.simulation.SimulationUtilities;



/**
 * A molecule on a {@link Surface}.  A surfacemolecule must be on a non-null Surface,
 * and be associated with a {@link SpeciesState} whose {@link StateType} is
 * NOT solution.
 * 
 * Invariants:  surface is non-null
 * 		speciesstate is non-null AND not StateType.solution
 * 		neither speciesstate, point nor surface may be changed
 * 
 * @author mfenwick
 *
 */
public class SurfaceMolecule implements SmoldynMolecule {

	private final SpeciesState speciesstate;
	private final Surface surface;
	private final Point point;
	private final int count;
	
	
	/**
	 * 
	 * @param speciesstate -- not null, and must not be equal to StateType.solution
	 * @param surface -- not null
	 * @param point -- not null
	 * @param count -- positive
	 * @throws RuntimeException if StateType of speciesstate is solution
	 */
	public SurfaceMolecule(SpeciesState speciesstate, Surface surface, Point point, int count) {
		SimulationUtilities.checkForNull("speciesstate or surface", speciesstate, surface);
		SimulationUtilities.assertIsTrue("surface molecules must not be in solution", speciesstate.getState() != StateType.solution);
		SimulationUtilities.checkForPositive("count", count);
		this.speciesstate = speciesstate;
		this.surface = surface;
		this.point = point;
		this.count = count;
	}
	
	
	public SpeciesState getSpeciesStateDiffusion() {
		return this.speciesstate;
	}
	
	public Surface getSurface() {
		return this.surface;
	}
	
	public Point getPoint() {
		return this.point;
	}
	
	public int getCount() {
		return this.count;
	}
}
