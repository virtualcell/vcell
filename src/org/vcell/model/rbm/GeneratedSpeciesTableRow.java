package org.vcell.model.rbm;

import java.beans.PropertyVetoException;
import java.util.List;

import org.vcell.model.bngl.ParseException;
import org.vcell.util.Displayable;
import org.vcell.util.Pair;

import cbit.vcell.bionetgen.BNGSpecies;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;

public class GeneratedSpeciesTableRow {

	private String originalName;
	private BNGSpecies speciesObject;
	private String index;
	private String expression;

	private final NetworkConstraintsPanel owner;
	private SpeciesContext species;

	public GeneratedSpeciesTableRow(String originalName, BNGSpecies speciesObject, NetworkConstraintsPanel owner) {
		this.setOriginalName(originalName);
		this.speciesObject = speciesObject;
		this.owner = owner;
	}
	
	public String getOriginalName() {
		return originalName;
	}
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	public BNGSpecies getSpeciesObject() {
		return speciesObject;
	}

	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression, Model model) {
		this.expression = expression;
		deriveSpecies(expression, model);
	}
	
	private void deriveSpecies(String inputString, Model tempModel) {

		if(owner != null && owner.getSimulationContext() != null) {
			List <MolecularType> mtList = owner.getSimulationContext().getModel().getRbmModelContainer().getMolecularTypeList();
			try {
				tempModel.getRbmModelContainer().setMolecularTypeList(mtList);
			} catch (PropertyVetoException e1) {
				e1.printStackTrace();
				throw new RuntimeException("Unexpected exception setting " + MolecularType.typeName + " list: "+e1.getMessage(),e1);
			}
		} else {
			System.out.println("something is wrong, we just do nothing rather than crash");
			return;
		}
		try {
			
		String strStructure = null;
		if(inputString.contains(RbmUtils.SiteStruct)) {
			// we are in the mode where we emulate compartments by adding the compartment name as a fake site
			Pair<String, String> p = RbmUtils.extractCompartment(inputString);
			strStructure = p.one;
			inputString = p.two;
		} else {
			// should be the normal @comp::expression format - if it's not it will return null
			strStructure = RbmUtils.parseCompartment(inputString, tempModel);
		}
		Structure structure;
		if(strStructure != null) {
			if(tempModel.getStructure(strStructure) == null) {
				tempModel.addFeature(strStructure);
			}
			structure = tempModel.getStructure(strStructure);
		} else {
			structure = tempModel.getStructure(0);
		}
		SpeciesPattern sp = (SpeciesPattern)RbmUtils.parseSpeciesPattern(inputString, tempModel);
		sp.resolveBonds();
//		System.out.println(sp.toString());
		species = new SpeciesContext(new Species("a",""), structure, sp);
		} catch (ParseException | PropertyVetoException | ModelException e1) {
			e1.printStackTrace();
		}
	}
	public SpeciesContext getSpecies() {
		return species;
	}
}

