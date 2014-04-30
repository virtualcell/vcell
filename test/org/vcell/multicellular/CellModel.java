package org.vcell.multicellular;

import java.util.ArrayList;
import java.util.List;

import org.sbml.libsbml.ASTNode;
import org.sbml.libsbml.Compartment;
import org.sbml.libsbml.CompartmentMapping;
import org.sbml.libsbml.DomainType;
import org.sbml.libsbml.KineticLaw;
import org.sbml.libsbml.Model;
import org.sbml.libsbml.Reaction;
import org.sbml.libsbml.SpatialCompartmentPlugin;
import org.sbml.libsbml.SpatialParameterPlugin;
import org.sbml.libsbml.SpatialSymbolReference;
import org.sbml.libsbml.Species;
import org.sbml.libsbml.SpeciesReference;
import org.sbml.libsbml.libsbml;
import org.vcell.sbml.SBMLUtils;
import org.vcell.util.TokenMangler;

public class CellModel {
	public final Model model;
	public final DomainType domainType;
	public final int ordinal;
	private ArrayList<Compartment> compartments = new ArrayList<Compartment>();
	
	public CellModel(Model model, DomainType domainType, int ordinal){
		this.model = model;
		this.domainType = domainType;
		this.ordinal = ordinal;
	}
	public List<Compartment> getCompartments(){
		return compartments;
	}
	public Compartment createCompartment(String name){
		if (model.getCompartment(name) != null){
			throw new RuntimeException("compartment "+name+" already exists in this model");
		}
		Compartment compartment = model.createCompartment();
		compartment.setId(name);
		compartment.setSpatialDimensions(3);
		compartments.add(compartment);
		SpatialCompartmentPlugin cplugin = (SpatialCompartmentPlugin)compartment.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
		CompartmentMapping compMapping = cplugin.getCompartmentMapping();
		compMapping.setSpatialId("mapping_"+domainType.getSpatialId()+"_"+compartment.getId());
		compMapping.setCompartment(compartment.getId());
		compMapping.setDomainType(domainType.getSpatialId());
		return compartment;
	}
	
	public Compartment getCompartment(String compartmentId){
		for (Compartment c : compartments){
			if (c.getId().equals(compartmentId)){
				return c;
			}
		}
		return null;
	}
	
	public List<Species> getSpeciesList(){
		ArrayList<Species> speciesList = new ArrayList<Species>();
		for (int i=0; i<model.getNumSpecies(); i++){
			Species species = model.getSpecies(i);
			if (getCompartment(species.getCompartment())!=null){
				speciesList.add(species);
			}
		}
		return speciesList;
	}
	
	public Species createSpecies(String compartmentId, String speciesId) {
		if (getCompartment(compartmentId) == null){
			throw new RuntimeException("compartment "+compartmentId+" not found for this cellType");
		}
		if (model.getSpecies(speciesId) != null){
			throw new RuntimeException("species "+speciesId+" already found in model");
		}
		Species s1 = model.createSpecies();
		s1.setCompartment(compartmentId);
		s1.setId(speciesId);
		return s1;
	}
	
	public Reaction createReaction(Compartment compartment, String reactionId, Species[] reactants, Species[] products, String rateLawInfix) {
		Reaction reaction = model.createReaction();
		reaction.setId(reactionId);
		for (Species reactant : reactants){
			SpeciesReference reactantRef = reaction.createReactant();
			reactantRef.setStoichiometry(1);
			reactantRef.setSpecies(reactant.getId());
		}
		for (Species product : products){
			SpeciesReference productRef = reaction.createProduct();
			productRef.setStoichiometry(1);
			productRef.setSpecies(product.getId());
		}
		reaction.setCompartment(compartment.getId());
		KineticLaw kineticLaw = model.createKineticLaw();
		ASTNode mathAST = libsbml.parseFormula(rateLawInfix);
		
		kineticLaw.setMath(mathAST);
		reaction.setKineticLaw(kineticLaw);
		return reaction;
	}
}