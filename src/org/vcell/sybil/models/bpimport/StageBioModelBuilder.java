package org.vcell.sybil.models.bpimport;

/*   StageBioModelBuilder  --- by Oliver Ruebenacker, UCHC --- July 2008 to October 2009
 *   Population a BioModel from a model of SBPAX reactions
 */

import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBInferenceBox;
import org.vcell.sybil.models.sbbox.util.LocationUtil;
import org.vcell.sybil.models.views.SBWorkView;
import org.vcell.sybil.util.exception.CatchUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;

public class StageBioModelBuilder {

	@SuppressWarnings("serial")
	public static class UnsupportedTopologyException extends Exception {
		public UnsupportedTopologyException(String message) { super(message); }
	}
	
	@SuppressWarnings("serial")
	public static class UnsupportedReactionClassException extends Exception {
		public UnsupportedReactionClassException(String message) { super(message); }
	}
	
	protected static class ModifierSpeciesReference {
		protected SpeciesContext speciesContext;
		public ModifierSpeciesReference(SpeciesContext species) { this.speciesContext = species; }
		public SpeciesContext speciesContext() { return speciesContext; }
	}
	
	protected static class SpeciesReference extends ModifierSpeciesReference {
		protected int sc;
		public SpeciesReference(SpeciesContext species, int sc) { 
			super(species);
			this.sc = sc;
		}
		public int sc() { return sc; }
	}
	
	protected static class BuilderTray {
		protected SBBox box;
		protected BioModel bio;
	    
		public BuilderTray(SBBox box, BioModel bio) {
			this.box = box;
			this.bio = bio;
		}
		
		public SBBox box() { return box; }
		public BioModel bio() { return bio; }
		public Model model() { return bio.getModel(); }
	}

	public static void build(SBWorkView view) {

		SBBox.MutableSystemModel systemModel = view.systemModel();
		SBInferenceBox box = view.box();
		box.performSYBREAMReasoning();
		BuilderTray tray = new BuilderTray(box, view.bioModel());

		for(SBBox.Location location : view.locations()) {	    	
			try { addStructure(tray, location); } 
			catch (UnsupportedTopologyException e) { e.printStackTrace(); } 
			catch (InvocationTargetException e) { e.printStackTrace(); }
		}
		for(SBBox.Substance substance : systemModel.substances()) {
			try { addSpecies(tray, substance); } 
			catch (InvocationTargetException e) { e.printStackTrace(); }
		}
		for(SBBox.ProcessModel processModel : systemModel.processModels()) {
			SBBox.Process process = processModel.process();
			if(process != null) {
				SimpleReaction reaction;
				try {
					reaction = getReaction(tray, process);
					//StmtIterator partIterCat = process.listProperties(SBPAX.hasParticipantCatalyst);
					for(SBBox.ParticipantLeft participantLeft : process.participantsLeft()) {	
						SpeciesReference reactantRef = getSpeciesReference(tray, participantLeft);
						if(reactantRef != null) {
							try {
								reaction.addReactant(reactantRef.speciesContext(), reactantRef.sc());
							} 
							catch (Exception e) { e.printStackTrace(); }
						}
					}
					for(SBBox.ParticipantRight participantRight : process.participantsRight()) {
						SpeciesReference productRef = getSpeciesReference(tray, participantRight);
						if(productRef != null) {
							try {
								reaction.addProduct(productRef.speciesContext(), productRef.sc());
							} 
							catch (Exception e) { e.printStackTrace(); }
						}
					}
					for(SBBox.ParticipantCatalyst participantCat : process.participantsCat()) {	
						ModifierSpeciesReference modifierRef = 
							getModifierSpeciesReference(tray, participantCat);
						if(modifierRef != null) {
							if(modifierRef != null) {
								try {
									reaction.addCatalyst(modifierRef.speciesContext());
								} 
								catch (Exception e) { e.printStackTrace(); }
							}
						}
					}
					try {
						tray.model().addReactionStep(reaction);
					} catch (PropertyVetoException e) {
						throw new InvocationTargetException(e, "Could not add reaction " 
								+ process.label() + " to model");
					}
				} 
				catch (UnsupportedTopologyException e) { e.printStackTrace(); } 
				catch (InvocationTargetException e) { e.printStackTrace(); } 
			}
		}
	}

	public static Species addSpecies(BuilderTray tray, SBBox.Substance substance) 
	throws InvocationTargetException {
		String label = substance.label();
		Species species = tray.model().getSpecies(label);
		if(species == null) {
			species = new Species(label, "");
			try { tray.model().addSpecies(species); } 
			catch (PropertyVetoException e) {
				String message = "Exception while trying to add species " + label + " ";
				throw new InvocationTargetException(e, message);
			}
		}
		return species;
	}
	
	public static SimpleReaction getReaction(BuilderTray tray, SBBox.Process process) 
	throws UnsupportedTopologyException, InvocationTargetException {
		SimpleReaction reaction = null;
		SBBox.Location location = LocationUtil.bestLocationForParticipants(process.participants());
		if(location != null) {
			Structure structure = addStructure(tray, location);
			if(structure != null) {
				String label = process.label();
				try { 
					ReactionStep reactionStep = tray.model().getReactionStep(label);
					if(reactionStep instanceof SimpleReaction) {
						reaction = (SimpleReaction) reactionStep;
					} else if (reactionStep == null) {
						reaction = new SimpleReaction(structure, label); 						
					} else {
						throw new UnsupportedReactionClassException("Name " + label +
								" already given to a reaction which is not a simple reaction.");
					}
				} 
				catch (PropertyVetoException e) {
					throw new InvocationTargetException(e, 
							"Could not create reaction " + label);
				} catch (UnsupportedReactionClassException e) {
					e.printStackTrace();
				}				
			}
		}
		return reaction;
	}
	
	public static ModifierSpeciesReference 
	getModifierSpeciesReference(BuilderTray tray, SBBox.ParticipantCatalyst participantCat) {
		SpeciesContext speciesContext = null;		
		SBBox.Species speciesSB = participantCat.species();
		if(speciesSB != null) { speciesContext = getSpeciesContext(tray, speciesSB); }
		ModifierSpeciesReference modifierSpeciesReference = null;
		if(speciesContext != null) {
			modifierSpeciesReference = new ModifierSpeciesReference(speciesContext);
		}
		return modifierSpeciesReference;
	}

	public static SpeciesReference 
	getSpeciesReference(BuilderTray tray, SBBox.Participant participant) {
		SpeciesContext speciesContext = null;
		double sc = 1.0;
		SBBox.Species speciesSB = participant.species();
		if(speciesSB != null) { speciesContext = getSpeciesContext(tray, speciesSB); }
		SBBox.Stoichiometry stoichiometry = participant.stoichiometry();
		if(stoichiometry != null) { sc = stoichiometry.sc(); }
		SpeciesReference speciesReference = null;
		if(speciesContext != null) { speciesReference = new SpeciesReference(speciesContext, (int) sc); }
		return speciesReference;
	}

	public static SpeciesContext getSpeciesContext(BuilderTray tray, SBBox.Species speciesSB) {
		SBBox.Substance substance = speciesSB.substance();
		SBBox.Location location = speciesSB.location();
		SpeciesContext speciesContext = null;
		try {
			Species	species = addSpecies(tray, substance);
			Structure structure = addStructure(tray, location);
			speciesContext = tray.model().getSpeciesContext(species, structure);
			if(speciesContext == null) {
				speciesContext = new SpeciesContext(species, structure);
				tray.model().addSpeciesContext(speciesContext);
			}
		} catch(Exception e) { CatchUtil.handle(e); }
		return speciesContext;
	}
	
	public static Structure addStructure(BuilderTray box, SBBox.Location loc) 
	throws UnsupportedTopologyException, InvocationTargetException {
		Structure structure;
		int dims = loc.dims();
		if(dims == 3) { structure = addFeature(box, loc); }
		else if(dims == 2) { structure = addMembrane(box, loc); }
		else { throw newWrongDimsException(loc, dims); }
		return structure;
	}

	public static Feature addFeature(BuilderTray tray, SBBox.Location locFeature) 
	throws UnsupportedTopologyException, InvocationTargetException {
		String featureName = locFeature.label();
		Feature feature = null;
		Structure structure = tray.model().getStructure(featureName);
		if(structure instanceof Feature) { 
			feature = (Feature) structure; 
		} else if(structure != null) { 
			throw new UnsupportedTopologyException(featureName + " has three dimensions, " 
					+ "but is not listed as a feature."); 
		}
		if(feature == null) {
			SBBox.Location outLoc = locFeature.locationSurrounding();
			if(outLoc != null) {
				int outLocDims = outLoc.dims();
				if(outLocDims == 3) {
					Feature parent = addFeature(tray, outLoc);
					String membraneName = featureName + "_Membrane";
					addFeatureAndMembrane(tray, featureName, parent, membraneName);
				} else if(outLocDims == 2) {
					SBBox.Location parentLoc = outLoc.locationSurrounding();
					Feature parent = null;
					if(parentLoc != null) { parent = addFeature(tray, parentLoc); }
					else { parent = tray.bio().getModel().getTopFeature(); }
					String membraneName = outLoc.label();
					addFeatureAndMembrane(tray, featureName, parent, membraneName);
				} else { throw newWrongDimsException(outLoc, outLocDims); }
			} else {
				addFeatureAndMembrane(tray, featureName, null, featureName + "_Membrane");
			}
			feature = (Feature) tray.model().getStructure(featureName);
		}
		return feature;
	}
	
	public static Membrane addMembrane(BuilderTray tray, SBBox.Location locMembrane) 
	throws UnsupportedTopologyException, InvocationTargetException {
		Membrane membrane = null;
		String membraneName = locMembrane.label();
		Structure structure = tray.model().getStructure(membraneName);
		if(structure instanceof Membrane) { membrane = (Membrane) structure; } 
		else if(structure != null) { 
			throw new UnsupportedTopologyException(membraneName + " has two dimensions, " 
					+ "but is not listed as a membrane."); 
		}
		if(membrane == null) {
			Set<SBBox.Location> inLocs = locMembrane.locationsSurrounded();
			if(inLocs.size() != 1) {
				throw new UnsupportedTopologyException("Need exactly one location neighbouring " +
						"membrane " + membraneName + " from the inside, but got " + inLocs.size());
			}
			SBBox.Location inLoc = inLocs.iterator().next();
			int inLocDims = inLoc.dims();
			if(inLocDims != 3) {
				throw new UnsupportedTopologyException("Inside neighbour of membrane " +
						membraneName + " has to have three dimensions, but has " + inLocDims);
			}
			String featureName = inLoc.label();
			Feature parent = null;
			SBBox.Location outLoc = locMembrane.locationSurrounding();
			if(outLoc != null) {
				int outLocDims = outLoc.dims();
				if(outLocDims != 3) {
					throw new UnsupportedTopologyException("Outside neighbour of membrane " +
							membraneName + " has to have three dimensions, but has " + outLocDims);
				}
				parent = addFeature(tray, outLoc);
			} else {
				parent = tray.model().getTopFeature();
			}
			addFeatureAndMembrane(tray, featureName, parent, membraneName);
			membrane = (Membrane) tray.model().getStructure(membraneName);
		}
		return membrane;
	}
	
	public static void addFeatureAndMembrane(BuilderTray box, 
			String feature, Feature parent, String membrane) 
	throws InvocationTargetException {
		try { box.model().addFeature(feature, parent, membrane); } 
		catch (Exception e) {
			throw newTryingToAddException(e, feature, parent, membrane);
		}
	}
	
	public static UnsupportedTopologyException newWrongDimsException(SBBox.Location loc, int dims) {
		return new UnsupportedTopologyException("Only supporting 2 and 3 dimensions, " +
				"but " + loc.label() + " has " + dims);			
	}
	
	public static InvocationTargetException newTryingToAddException(Throwable e, String feature, 
			Feature parent, String membrane) {
		String parentString = parent != null ? parent.toString() : "[null]";
		String message = "Exception while trying to add feature " + feature +
		" and membrane " + membrane + " to parent " + parentString;
		return new InvocationTargetException(e, message);
	}
	
}
