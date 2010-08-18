package org.vcell.smoldyn.model;


import java.util.Hashtable;
import java.util.HashSet;
import org.vcell.smoldyn.model.SpeciesState.StateType;
import org.vcell.smoldyn.model.util.Point;
import org.vcell.smoldyn.model.util.SurfaceActions;
import org.vcell.smoldyn.model.util.SurfaceReactionParticipants;
import org.vcell.smoldyn.model.util.VolumeReactionParticipants;
import org.vcell.smoldyn.model.util.Point.PointFactory;
import org.vcell.smoldyn.simulation.Simulation;
import org.vcell.smoldyn.simulation.SimulationUtilities;
import org.vcell.smoldyn.simulation.SmoldynException;


/**
 * A model contains the data that corresponds to the physical representation of a {@link Simulation}.
 * This includes information such as the number of dimensions in the system, the {@link Geometry}
 * of the system, {@link VolumeMolecule}s, reactions (both {@link SurfaceReaction}s and {@link VolumeReaction}s)
 * , and {@link Boundaries}s.
 * 
 * General contract for getter methods of the data members of a Model:  if the data member is a complex
 * data type, such as an array, arraylist, or hashtable, the getter method will return a new array filled
 * with copies of the contents.  Otherwise, for a simple type the getter method will just return the data
 * member.
 * 
 * General contract for elements with names:  names will be enforced to be unique, within the Model, for that 
 * type of element.  For example, Model will enforce that each Species has a name different from all the other
 * Species in the Model.  Attempting to do otherwise will cause an exception to be thrown.
 * 
 * Invariants:
 * 		dimensionality is 3 (at the moment) and must match dimensionality of the geometry
 * 		each species name is unique with respect to the other species (same for speciesstates, volume and surface reactions, and
 * 			surface actions)
 * 		the string under which a species is hashed must exactly match its getName() method (same for speciesstates, volume and
 * 			surface reactions, and surface actions)
 * 		each speciesstate value refers to a species contained within the Model instance 
 * 		every VolumeReaction and VolumeMolecule must refer either to a Compartment contained by the Model instance, or null
 * 		all participants of VolumeReactions and SurfaceReactions must refer to species/speciesstates contained within the Model instance
 * 		all VolumeMolecules and SurfaceMolecules must refer to species/speciesstate contained by the Model instance, and also must refer
 * 			to Points contained within the geometry's boundaries (or refer to null)
 * 
 * 
 * @author mfenwick
 *
 */
public class Model implements Modelable {
	private final Hashtable<String, Species> species = new Hashtable<String, Species>();
	private final Hashtable<String, SpeciesState> speciesstates = new Hashtable<String, SpeciesState>();
	private final HashSet<VolumeMolecule> volumemolecules = new HashSet<VolumeMolecule>();
	private final HashSet<SurfaceMolecule> surfacemolecules = new HashSet<SurfaceMolecule>();
	private final Hashtable<String, VolumeReaction> volumereactions = new Hashtable<String, VolumeReaction>();
	private final Hashtable<String, SurfaceReaction> surfacereactions = new Hashtable<String, SurfaceReaction>();
	private final HashSet<ManipulationEvent> manipulationevents = new HashSet<ManipulationEvent>();
	private final Hashtable<Surface, Hashtable<Species, SurfaceActions>> surfaceactions = 
		new Hashtable<Surface, Hashtable<Species,SurfaceActions>>();
	private final Dimensionality dimensionality;
	private final Geometryable geometry;
	private final PointFactory pointfactory;
	
	/**
	 * Initializes a model with the specified number of dimensions.  The dimensionality must be specified
	 * in the constructor because properties of other classes depend on it.  The 
	 * dimensionality is set at initialization only.  Other instance data may be modified
	 * using set and add methods.  Geometry may be null, in which case a new default Geometry is initialized
	 * and added to the Model.  However, if a Geometry is specified, its dimensionality must match that
	 * of the Model. 		
	 * 
	 * @param dimensionality Number of dimensions to be used in the model/simulation run
	 * @param geometry Contains the {@link Surface}s and {@link Compartment}s
	 */
	public Model(Dimensionality dimensionality, Geometry geometry) {
		SimulationUtilities.checkForNull("geometry and dimensions", geometry, dimensionality);
		this.geometry = geometry;
		this.pointfactory = new PointFactory(geometry);
		this.dimensionality = dimensionality;
	}
	
	
	/**
	 * @return the number of dimensions of the model (between one and three)
	 */
	public int getDimensionality() {
		return this.dimensionality.getDimensionality();
	}

	public PointFactory getPointFactory() {
		return this.pointfactory;
	}
	
	/**
	 * Requests addition of a {@link Species} with the given name to the model.
	 * The name must be unique and non-null.
	 * 
	 * @param name String
	 * @throws SmoldynException 
	 * @throws NullPointerException if name is null (thrown by Hashtable)
	 * @throws RuntimeException if name is not unique
	 * 
	 */
	public void addSpecies(String name) throws SmoldynException {
		if (this.hasSpecies(name)) {
			SimulationUtilities.throwAlreadyHasKeyException("species name (name was: <" + name + ">)");
		}
		Species species = new Species(name);
		this.species.put(species.getName(), species);
	}
	
	/**
	 * Gets the Species of the Model.
	 * 
	 * @return {@link Species} [] an array containing all the Species of the Model
	 */
	public Species [] getSpecies() {
		return species.values().toArray(new Species [species.size()]);
	}
	
	private boolean hasSpecies(String speciesname) {
		if(this.species.get(speciesname) == null) {
			return false;
		}
		return true;
	}
	
	public Species getSpecies(String name) throws SmoldynException {
		if (this.species.get(name) == null) {
			SimulationUtilities.throwNoAssociatedValueException("species of name <" + name + ">");
		}
		return this.species.get(name);
	}
	
	
	
	/**
	 * Adds a {@link SpeciesState} to the Model with the specified name and {@link StateType}.
	 * First, it checks whether the search name and statetype are null.  Then, it checks
	 * whether the Model has a Species with the specified name.  If it does, then it
	 * instantiates a new SpeciesState with the given name and StateType, and puts it
	 * into the Model's {@link Hashtable}.  A {@link RuntimeException} is thrown if there is already
	 * an entry for the key (which is calculated from the name and statetype).
	 * 
	 * @param speciesname String
	 * @param statetype StateType
	 * @throws SmoldynException 
	 * @throws RuntimeException if Model has no Species with the specified name
	 * @throws RuntimeException if Model already has a SpeciesState with the specified Species and StateType
	 */
	public void addSpeciesState(String speciesname, StateType statetype, Double isotropicdiffusionconstant) throws SmoldynException {
		this.validateSpeciesStateArguments(speciesname, statetype);
		String key = getSpeciesStateHashString(speciesname, statetype);
		if(this.speciesstates.get(key) != null) {
			SimulationUtilities.throwAlreadyHasKeyException("species state");
		}
		SpeciesState speciesstate = new SpeciesState(this.getSpecies(speciesname), statetype, isotropicdiffusionconstant);
		this.speciesstates.put(key, speciesstate);
	}
	
	private static String getSpeciesStateHashString(String speciesname, StateType statetype) {
		return speciesname + statetype.toString();
	}
	
	private void validateSpeciesStateArguments(String speciesname, StateType statetype) throws SmoldynException {
		SimulationUtilities.checkForNull("statetype", statetype);
		if(!this.hasSpecies(speciesname)) {
			SimulationUtilities.throwNoAssociatedValueException("species");
		}
	}
	
	public SpeciesState [] getSpeciesStates() {
		return speciesstates.values().toArray(new SpeciesState [speciesstates.size()]);
	}
	
	public SpeciesState getSpeciesState(String speciesname, StateType statetype) throws SmoldynException {
		validateSpeciesStateArguments(speciesname, statetype);
		String key = getSpeciesStateHashString(speciesname, statetype);
		SpeciesState ssd = this.speciesstates.get(key);
		if(ssd == null) {
			SimulationUtilities.throwNoAssociatedValueException("species state (looking for <" + speciesname + "(" + statetype + ")>");
		}
		return ssd;
	}

	
	
	public void addVolumeMolecule(String compartmentname, String speciesname, Point point, int count) throws SmoldynException {
		Compartment compartment = this.geometry.getCompartment(compartmentname);//compartmentname = null -> implicit comparment of entire sim volume ---> MAKE EXPLICIT
		Species species = this.getSpecies(speciesname);
		this.volumemolecules.add(new VolumeMolecule(compartment, species, point, count));
	}
	
	public VolumeMolecule [] getVolumeMolecules() {
		return volumemolecules.toArray(new VolumeMolecule [volumemolecules.size()]);
	}

	public void addSurfaceMolecule(String surfacename, String speciesname, StateType statetype, Point point, int count) throws SmoldynException {
		validateSpeciesStateArguments(speciesname, statetype);
		Surface surface = this.geometry.getSurface(surfacename);
		SpeciesState speciesstate = getSpeciesState(speciesname, statetype);
		this.surfacemolecules.add(new SurfaceMolecule(speciesstate, surface, null, count));
	}
	
	public SurfaceMolecule [] getSurfaceMolecules() {
		return surfacemolecules.toArray(new SurfaceMolecule [surfacemolecules.size()]);
	}

	
	
	public VolumeReaction [] getVolumeReactions() {
		return volumereactions.values().toArray(new VolumeReaction [volumereactions.size()]);
	}
	
	public void addVolumeReaction(String volumereactionname, String compartmentname, String reactant1, String reactant2, 
			String product1, String product2, double rate) throws SmoldynException {
		if(hasVolumeReaction(volumereactionname)) {
			SimulationUtilities.throwAlreadyHasKeyException("volume reaction name");
		}
		Compartment compartment = this.geometry.getCompartment(compartmentname);
		VolumeReactionParticipants vrpreact, vrpproduct;
		vrpreact = getVolumeReactionParticipants(reactant1, reactant2);
		vrpproduct = getVolumeReactionParticipants(product1, product2);
		this.volumereactions.put(volumereactionname, new VolumeReaction(volumereactionname, compartment, vrpreact, 
				vrpproduct, rate));
	}
	
	private boolean hasVolumeReaction(String volumereactionname) {
		if(this.volumereactions.get(volumereactionname) == null) {
			return false;
		}
		return true;
	}
	
	public VolumeReaction getVolumeReaction(String reactionname) throws SmoldynException {
		if(!hasVolumeReaction(reactionname)) {
			SimulationUtilities.throwNoAssociatedValueException("volume reaction");
		}
		return this.volumereactions.get(reactionname);
	}
	
	
	public SurfaceReaction [] getSurfaceReactions() {
		return surfacereactions.values().toArray(new SurfaceReaction [surfacereactions.size()]);
	}
	
	public void addSurfaceReaction(String reactionname, String surfacename, String reactant1, StateType reactstate1, 
			String reactant2, StateType reactstate2, String product1, StateType productstate1,
			String product2, StateType productstate2, double rate) throws SmoldynException {
		if(hasSurfaceReaction(reactionname)) {
			SimulationUtilities.throwAlreadyHasKeyException("surface reaction name");
		}
		Surface surface = this.geometry.getSurface(surfacename);
		SurfaceReactionParticipants srpreact, srpproduct;
		srpreact = getSurfaceReactionParticipants(reactant1, reactstate1, reactant2, reactstate2);
		srpproduct = getSurfaceReactionParticipants(product1, productstate1, product2, productstate2);
		SurfaceReaction surfreaction = new SurfaceReaction(reactionname, surface, srpreact, srpproduct, rate);
		this.surfacereactions.put(reactionname, surfreaction);
	}
	
	private boolean hasSurfaceReaction(String reactionname) {
		if(this.surfacereactions.get(reactionname) == null) {
			return false;
		}
		return true;
	}
	
	public SurfaceReaction getSurfaceReaction(String reactionname) throws SmoldynException {
		if(!hasSurfaceReaction(reactionname)) {
			SimulationUtilities.throwNoAssociatedValueException("surface reaction");
		}
		return this.surfacereactions.get(reactionname);
	}
	

	public Geometryable getGeometry() {
		return this.geometry;
	}	
	
	
	public void addManipulationEvent() {
		//TODO
	}
	
	public ManipulationEvent [] getManipulationEvents() {
		return this.manipulationevents.toArray(new ManipulationEvent [manipulationevents.size()]);
	}
	
	
	/**
	 * Gets the actions for each species that are associated with the specified {@link Surface}.
	 * 
	 * @param surfacename the name of one of the surfaces in the model's {@link Geometry}.
	 *  
	 * @return a Hashtable, with key = Species and value = the actions that occur for that species at the surface.
	 * 		The return value MAY be null, if no actions have been specified, which implies that the only action is
	 * 		reflection.
	 * @throws SmoldynException 
	 * 
	 */
	public Hashtable<Species, SurfaceActions> getSurfaceActions(String surfacename) throws SmoldynException {
		Surface surface = this.geometry.getSurface(surfacename);
		Hashtable<Species, SurfaceActions> surfaceactions = this.surfaceactions.get(surface);
		return surfaceactions;
	}
	
	public void addSurfaceActions(String surfacename, String speciesname) throws SmoldynException {
		this.addSurfaceActions(surfacename, speciesname, 0, 0, 0, 0, 0, 0);
	}
	
	public void addSurfaceActions(String surfacename, String speciesname, double reflect, double absorb, double transmit) throws SmoldynException {
		this.addSurfaceActions(surfacename, speciesname, reflect, transmit, absorb, reflect, transmit, absorb);
	}
	
	/**
	 * Add actions for a species at a surface.  If actions have already been specified for this Surface and Species, it will simply be
	 * overwritten without warning.  This is due to the decision to store actions in nested hashtables, which was done so that all actions
	 * at a specific surface may be easily grabbed.  If actions have not already been specified, then a new entry is added to the inner hash
	 * table.  If NO actions AT ALL have been specified for the surface, then a new entry (which is itself a hashtable) is added to the 
	 * outer hashtable, and one entry is added to the (new) inner hashtable.
	 * @throws SmoldynException 
	 */
	public void addSurfaceActions(String surfacename, String speciesname, double frontreflect, double frontabsorb, double fronttransmit,
			double backreflect, double backabsorb, double backtransmit) throws SmoldynException {
		Surface surface = this.geometry.getSurface(surfacename);
		Species species = this.getSpecies(speciesname);
		SurfaceActions surfaceactions = new SurfaceActions(frontreflect, frontabsorb, fronttransmit, backreflect,
				backabsorb, backtransmit);
		if(this.surfaceactions.get(surface) == null) {
			this.surfaceactions.put(surface, new Hashtable<Species, SurfaceActions>());
		}
		this.surfaceactions.get(surface).put(species, surfaceactions);
	}
	
	
	private VolumeReactionParticipants getVolumeReactionParticipants(String speciesname1, String speciesname2) throws SmoldynException {
//		ModelUtilities.printDebuggingStatement("volume reaction participants: " + speciesname1 + "    " + speciesname2);
		Species species1 = null;
		Species species2 = null;
		if(speciesname1 != null) {
			species1 = this.getSpecies(speciesname1);
		}
		if(speciesname2 != null) {
			species2 = this.getSpecies(speciesname2);
		}
		VolumeReactionParticipants volumereactionparticipants = new VolumeReactionParticipants(species1, species2);
		return volumereactionparticipants;
	}
	
	private SurfaceReactionParticipants getSurfaceReactionParticipants(String species1, StateType statetype1,
			String species2, StateType statetype2) throws SmoldynException {
		SpeciesState participant1 = null;
		SpeciesState participant2 = null;
		if(species1 != null) {
			participant1 = this.getSpeciesState(species1, statetype1);
		}
		if(species2 != null) {
			participant2 = this.getSpeciesState(species2, statetype2);
		}
		SurfaceReactionParticipants surfacereactionparticipants = new SurfaceReactionParticipants(participant1, participant2);
		return surfacereactionparticipants;
	}
	
	
	/**
	 * 
	 * Smoldyn supports 1-d, 2-d, and 3-d models.  This enumeration restricts its value to 
	 * what Smoldyn supports.  The method getDimensionality() may then be used to get the value
	 * (as an int) of the dimensionality.
	 * @author mfenwick
	 */
	public enum Dimensionality {

		one (1),
		two (2),
		three (3);
		
		private final int dim;
		
		private Dimensionality(int dim) {
			this.dim = dim;
		}
		
		public int getDimensionality() {
			return this.dim;
		}
	}

}
