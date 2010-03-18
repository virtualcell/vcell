package org.vcell.sybil.models.bpimport;

/*   StageSBMLBuilder  --- by Oliver Ruebenacker, UCHC --- July 2008 to October 2009
 *   Building an SBML model from a model of SBPAX reactions
 */

import java.util.HashMap;
import java.util.Map;
import org.sbml.libsbml.Compartment;
import org.sbml.libsbml.Model;
import org.sbml.libsbml.ModifierSpeciesReference;
import org.sbml.libsbml.Reaction;
import org.sbml.libsbml.SBMLNamespaces;
import org.sbml.libsbml.Species;
import org.sbml.libsbml.SpeciesReference;
import org.sbml.libsbml.SpeciesType;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.views.SBWorkView;
import org.vcell.sybil.util.sbml.LibSBMLUtil;
import org.vcell.sybil.util.sbml.LibSBMLUtil.LibSBMLException;

public class StageSBMLBuilder {

	protected static class BuilderTray {
		protected SBWorkView view;
		protected SBMLNamespaces namespaces;
	    protected Map<SBBox.Substance, SpeciesType> speciesTypeMap = 
	    	new HashMap<SBBox.Substance, SpeciesType>();
	    protected Map<SBBox.Species, Species> speciesMap = 
	    	new HashMap<SBBox.Species, Species>();
	    protected Map<SBBox.Location, Compartment> compartmentMap = 
	    	new HashMap<SBBox.Location, Compartment>();	    
		public BuilderTray(SBWorkView view, SBMLNamespaces namespaces) {
			this.view = view;
			this.namespaces = namespaces;
		}
		public SBWorkView view() { return view; }
		public com.hp.hpl.jena.rdf.model.Model rdf() { return view.box().getRdf(); }
		public Model sbml() { return view.sbmlDoc().getModel(); }
		public SBMLNamespaces namespaces() { return namespaces; }
	    public Map<SBBox.Substance, SpeciesType> speciesTypeMap() { return speciesTypeMap; }
	    public Map<SBBox.Species, Species> speciesMap() { return speciesMap; }
	    public Map<SBBox.Location, Compartment> compartmentMap() { return compartmentMap; }
	}
	
	public static void build(SBWorkView view) throws LibSBMLException  {
		
	    view.setSBMLDoc(LibSBMLUtil.newDoc());
	    
	    SBBox.MutableSystemModel systemModel = view.systemModel();
	    SBBox box = view.box();
	    box.performOWLMicroReasoning();
	    BuilderTray tray = new BuilderTray(view, new SBMLNamespaces());
	    
	    for(SBBox.Location location : view.locations()) { addCompartment(tray, location); }
	    for(SBBox.Substance substance : systemModel.substances()) {	addSpeciesType(tray, substance); }
	    for(SBBox.ProcessModel processModel : systemModel.processModels()) {
	    	SBBox.Process process = processModel.process();
	    	if(process != null) {
	    		Reaction reaction = newReaction(tray, process); 
	    		for(SBBox.ParticipantCatalyst participantCat : process.participantsCat()) {
	    			ModifierSpeciesReference modifierRef = 
	    				getModifierSpeciesReference(tray, participantCat);
	    			if(modifierRef != null) {
	    				if(reaction.getModifier(modifierRef.getSpecies()) == null) {
	    					reaction.addModifier(modifierRef);
	    				}							
	    			}
	    		}
	    		for(SBBox.ParticipantLeft participantLeft : process.participantsLeft()) {						
	    			SpeciesReference reactantRef = 
	    				getSpeciesReference(tray, participantLeft);
	    			if(reactantRef != null) {
	    				if(reaction.getReactant(reactantRef.getSpecies()) == null) {
	    					reaction.addReactant(reactantRef);								
	    				}							
	    			}
	    		}
	    		for(SBBox.ParticipantRight participantRight : process.participantsRight()) {
	    			SpeciesReference productRef = 
	    				getSpeciesReference(tray, participantRight);
	    			if(productRef != null) {
	    				if(reaction.getProduct(productRef.getSpecies()) == null) {
	    					reaction.addProduct(productRef);								
	    				}							
	    			}
	    		}
	    		view.sbmlDoc().getModel().addReaction(reaction);
	    	}
	    }
	}

	public static SpeciesType addSpeciesType(BuilderTray tray, SBBox.Substance substance) {
		SpeciesType speciesType = tray.speciesTypeMap().get(substance);
		if(speciesType == null) {
			speciesType = new SpeciesType(tray.namespaces());
			speciesType.setId(substance.label());
			speciesType.setMetaId(substance.label());
			speciesType.setName(substance.name());
			tray.speciesTypeMap().put(substance, speciesType);
			//sbmlModel.addSpeciesType(speciesType);
		}
		return speciesType;
	}

	public static Reaction newReaction(BuilderTray tray, SBBox.Process process) {
		Reaction reaction = new Reaction(tray.namespaces());
		reaction.setId(process.label());
		reaction.setMetaId(process.label());
		reaction.setName(process.name());
		return reaction;
	}

	public static ModifierSpeciesReference 
	getModifierSpeciesReference(BuilderTray tray, SBBox.ParticipantCatalyst participantCat) {
		Species species = new Species(tray.namespaces());
		SBBox.Species speciesSB = participantCat.species();
		if(speciesSB != null) { species = getSpecies(tray, speciesSB); }
		ModifierSpeciesReference modifierSpeciesReference = 
			new ModifierSpeciesReference(tray.namespaces());
		modifierSpeciesReference.setSpecies(species.getId());
		return modifierSpeciesReference;
	}

	public static SpeciesReference 
	getSpeciesReference(BuilderTray tray, SBBox.Participant participant) {
		Species species = new Species(tray.namespaces());
		double sc = 1.0;
		SBBox.Species speciesSB = participant.species();
		if(speciesSB != null) { species = getSpecies(tray, speciesSB); }
		SBBox.Stoichiometry stoichiometry = participant.stoichiometry();
		if(stoichiometry != null) { sc = stoichiometry.sc(); }
		SpeciesReference speciesReference = new SpeciesReference(tray.namespaces());
		speciesReference.setSpecies(species.getId());
		speciesReference.setStoichiometry(sc);
		return speciesReference;
	}

	public static Species getSpecies(BuilderTray tray, SBBox.Species speciesSB) {
		Species species = tray.speciesMap().get(speciesSB);
		if(species == null) {
			try {
				species = new Species(tray.namespaces());
				species.setId(speciesSB.label());
				species.setMetaId(speciesSB.label());
				species.setName(speciesSB.name());
				addSpeciesType(tray, speciesSB.substance());
				//species.setSpeciesType(speciesType.getId());
				Compartment compartment = addCompartment(tray, speciesSB.location());
				species.setCompartment(compartment.getId());
				tray.speciesMap().put(speciesSB, species);
				tray.sbml().addSpecies(species);				
			} catch(Exception e) { e.printStackTrace(); }
		}
		return species;
	}

	public static Compartment addCompartment(BuilderTray tray, SBBox.Location loc) {
		Compartment compartment = tray.compartmentMap().get(loc);
		if(compartment == null) {
			compartment = new Compartment(tray.namespaces());
			compartment.setId(loc.label());
			compartment.setMetaId(loc.label());
			compartment.setName(loc.name());
			SBBox.Location outLoc = loc.locationSurrounding();
			if(outLoc != null) { compartment.setOutside(outLoc.label()); }
			compartment.setSpatialDimensions(loc.dims());
			tray.compartmentMap().put(loc, compartment);
			tray.sbml().addCompartment(compartment);
		}
		return compartment;
	}

}
