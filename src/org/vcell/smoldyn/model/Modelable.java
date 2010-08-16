package org.vcell.smoldyn.model;


import java.util.Hashtable;

import org.vcell.smoldyn.model.SpeciesState.StateType;
import org.vcell.smoldyn.model.util.Point;
import org.vcell.smoldyn.model.util.SurfaceActions;
import org.vcell.smoldyn.model.util.Point.PointFactory;



/**
 * Specification of the Model contract.
 * A Model is responsible for the number of spatial dimensions, {@link Species}, {@link SpeciesState} properties,
 * a {@link Geometryable}, {@link VolumeMolecule}s and {@link SurfaceMolecule}s, {@link SurfaceReaction}s and {@link VolumeReaction}s,
 * {@link ManipulationEvent}s, and {@link SurfaceActions}.  Every element contained within the Modelable's data structures 
 * that has a name (practically, everything other than Geometryable, number of spatial dimensions, ManipulationEvents, and 
 * SurfaceActions has a client-defined name) should be accessable by name.  To access all of the elements of a data structure (to get all
 * Species, for instance), the Species data structure is converted to an array containing deep copies and returned.  Thus, changes to the
 * returned array should not affect the Modelable's member data.
 * 
 * @author mfenwick
 *
 */
public interface Modelable {
	
	public int getDimensionality();
	
	public PointFactory getPointFactory();
	
	public void addSpecies(String name);
	public Species [] getSpecies();
	public Species getSpecies(String name);
	
	public void addSpeciesState(String speciesname, StateType statetype, Double isotropicdiffusionconstant);
	public SpeciesState [] getSpeciesStates();	
	public SpeciesState getSpeciesState(String speciesname, StateType statetype);
	
	public void addVolumeMolecule(String compartmentname, String speciesname, Point point, int count);
	public VolumeMolecule [] getVolumeMolecules();
	public void addSurfaceMolecule(String surfacename, String speciesname, StateType statetype, Point point, int count);
	public SurfaceMolecule [] getSurfaceMolecules();
	
//	public void addVolumeReaction(String compartmentname, VolumeReaction volumereaction);
	public VolumeReaction [] getVolumeReactions();
	public void addVolumeReaction(String name, String compartmentname, String reactant1, String reactant2, String product1,
			String product2, double rate);
	public VolumeReaction getVolumeReaction(String reactionname);
	
	public SurfaceReaction [] getSurfaceReactions();
	public void addSurfaceReaction(String reactionname, String surfacename, String reactant1, StateType reactstate1, 
			String reactant2, StateType reactstate2, String product1, StateType productstate1,
			String product2, StateType productstate2, double rate);
	public SurfaceReaction getSurfaceReaction(String reactionname);
	
	public Geometryable getGeometry();
	
	public void addManipulationEvent();
	public ManipulationEvent [] getManipulationEvents();
		
	public Hashtable<Species, SurfaceActions> getSurfaceActions(String surfacename);
	public void addSurfaceActions(String surfacename, String speciesname);
	public void addSurfaceActions(String surfacename, String speciesname, double reflect, double absorb, double transmit);	
	public void addSurfaceActions(String surfacename, String speciesname, double frontreflect, double frontabsorb, double fronttransmit,
			double backreflect, double backabsorb, double backtransmit);
}
