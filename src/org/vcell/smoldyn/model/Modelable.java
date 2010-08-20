package org.vcell.smoldyn.model;


import java.util.Hashtable;

import org.vcell.smoldyn.model.Species.StateType;
import org.vcell.smoldyn.model.util.Point;
import org.vcell.smoldyn.model.util.ReactionParticipants;
import org.vcell.smoldyn.model.util.SurfaceActions;
import org.vcell.smoldyn.model.util.Point.PointFactory;
import org.vcell.smoldyn.simulation.SmoldynException;



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
	
	public void addSpecies(String name) throws SmoldynException;
	public Species [] getSpecies();
	public Species getSpecies(String name) throws SmoldynException;
	
	public void addVolumeMolecule(String compartmentname, String speciesname, Point point, int count) throws SmoldynException;
	public VolumeMolecule [] getVolumeMolecules();
	public void addSurfaceMolecule(String surfacename, String speciesname, StateType statetype, Point point, int count) throws SmoldynException;
	public SurfaceMolecule [] getSurfaceMolecules();
	
//	public void addVolumeReaction(String compartmentname, VolumeReaction volumereaction);
	public VolumeReaction [] getVolumeReactions();
	public void addVolumeReaction(String name, String compartmentname, ReactionParticipants participants, double rate) throws SmoldynException;
	public VolumeReaction getVolumeReaction(String reactionname) throws SmoldynException;
	
	public SurfaceReaction [] getSurfaceReactions();
	public void addSurfaceReaction(String reactionname, String surfacename, ReactionParticipants participants, double rate) throws SmoldynException;
	public SurfaceReaction getSurfaceReaction(String reactionname) throws SmoldynException;
	
	public Geometryable getGeometry();
	
	public void addManipulationEvent();
	public ManipulationEvent [] getManipulationEvents();
		
	public Hashtable<Species, SurfaceActions> getSurfaceActions(String surfacename) throws SmoldynException;
	public void addSurfaceActions(String surfacename, String speciesname, SurfaceActions surfaceactions) throws SmoldynException;
}
