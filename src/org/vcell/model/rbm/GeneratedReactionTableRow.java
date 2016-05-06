package org.vcell.model.rbm;

import java.beans.PropertyVetoException;
import java.util.List;

import cbit.vcell.bionetgen.BNGReaction;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.Structure;

public class GeneratedReactionTableRow {

	private BNGReaction reactionObject;
	private String index;
	private String expression;
	
	private final NetworkConstraintsPanel owner;
	
	private ReactionRule reactionRule;
	
	public GeneratedReactionTableRow(BNGReaction reactionObject, NetworkConstraintsPanel owner) {
		this.reactionObject = reactionObject;
		this.owner = owner;
	}
	
	public BNGReaction getReactionObject() {
		return reactionObject;
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
		deriveRule(expression, model);
	}
	
	private void deriveRule(String inputString, Model tempModel) {
		
		if(owner != null && owner.getSimulationContext() != null) {
			List <MolecularType> mtList = owner.getSimulationContext().getModel().getRbmModelContainer().getMolecularTypeList();
			try {
				tempModel.getRbmModelContainer().setMolecularTypeList(mtList);
			} catch (PropertyVetoException e1) {
				e1.printStackTrace();
				throw new RuntimeException("Unexpected exception setting " + MolecularType.typeName + " list: "+e1.getMessage(),e1);
			}
		} else {
			return;		// something is wrong, we just show nothing rather than crash
		}
		
		int arrowIndex = inputString.indexOf("<->");
		boolean bReversible = true;
		if (arrowIndex < 0) {
			arrowIndex = inputString.indexOf("->");
			bReversible = false;
		}
		
		String left = inputString.substring(0, arrowIndex).trim();
		String right = inputString.substring(arrowIndex + (bReversible ? 3 : 2)).trim();
		if (left.length() == 0 && right.length() == 0) {
			return;
		}
		
		String name = reactionObject.getRuleName();
		if(name.contains(GeneratedReactionTableModel.reverse)) {
			name = name.substring(GeneratedReactionTableModel.reverse.length());
		}
		// try to get the name of the original structure from the original rule and make here another structure with the same name
		String strStructure = null;
		Structure structure;
		SimulationContext sc = owner.getSimulationContext();
		ReactionRule rr = sc.getModel().getRbmModelContainer().getReactionRule(name);
		if(rr != null && rr.getStructure() != null) {
			strStructure = rr.getStructure().getName();
		}
		if(strStructure != null) {
			if(tempModel.getStructure(strStructure) == null) {
				try {
					tempModel.addFeature(strStructure);
				} catch (ModelException | PropertyVetoException e) {
					e.printStackTrace();
				}
			}
			structure = tempModel.getStructure(strStructure);
		} else {
			structure = tempModel.getStructure(0);
		}
		// making the fake rules just for display purpose, actually they are the flattened reactions resulted from bngl
		// the name is probably not unique, it's likely that many flattened reactions are derived from the same rule
		reactionRule = tempModel.getRbmModelContainer().createReactionRule(name, structure, bReversible);
		
		String regex = "[^!]\\+";
		String[] patterns = left.split(regex);
		for (String spString : patterns) {
			try {
				spString = spString.trim();
				// if compartments are present, we're cheating big time making some fake compartments just for compartment name display purposes
				SpeciesPattern speciesPattern = (SpeciesPattern)RbmUtils.parseSpeciesPattern(spString, tempModel);
				strStructure = RbmUtils.parseCompartment(spString, tempModel);
				speciesPattern.resolveBonds();
				if(strStructure != null) {
					if(tempModel.getStructure(strStructure) == null) {
						tempModel.addFeature(strStructure);
					}
					structure = tempModel.getStructure(strStructure);
				} else {
					structure = tempModel.getStructure(0);
				}
				reactionRule.addReactant(new ReactantPattern(speciesPattern, structure));
			} catch(Throwable ex) {
				ex.printStackTrace();
				return;
			}
		}

		patterns = right.split(regex);
		for (String spString : patterns) {
			try {
				spString = spString.trim();						
				SpeciesPattern speciesPattern = (SpeciesPattern)RbmUtils.parseSpeciesPattern(spString, tempModel);
				strStructure = RbmUtils.parseCompartment(spString, tempModel);
				speciesPattern.resolveBonds();
				if(strStructure != null) {
					if(tempModel.getStructure(strStructure) == null) {
						tempModel.addFeature(strStructure);
					}
					structure = tempModel.getStructure(strStructure);
				} else {
					structure = tempModel.getStructure(0);
				}

				reactionRule.addProduct(new ProductPattern(speciesPattern, structure));
			} catch(Throwable ex) {
				ex.printStackTrace();
				return;
			}
		}			
		System.out.println(" --- done.");
		
	}
	public ReactionRule getReactionRule() {
		return reactionRule;
	}

}

