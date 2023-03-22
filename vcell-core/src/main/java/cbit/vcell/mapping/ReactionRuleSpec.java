/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.model.rbm.MolecularType;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.document.BioModelChildSummary.MathType;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;
import org.vcell.util.Pair;

import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.RbmKineticLaw;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;

public class ReactionRuleSpec implements ModelProcessSpec, IssueSource {
	private ReactionRule reactionRule = null;

	public enum ReactionRuleMappingType {
		INCLUDED("included","included"),
		EXCLUDED("excluded","excluded");
		
		private final String displayName;
		private final String databaseName;
		
		ReactionRuleMappingType(String displayName, String databaseName){
			this.displayName = displayName;
			this.databaseName = databaseName;
		}
		public String getDisplayName(){
			return this.displayName;
		}
		public String getDatabaseName() {
			return this.databaseName;
		}
		public static ReactionRuleMappingType fromDatabaseName(String databaseName){
			for (ReactionRuleMappingType t : values()){
				if (t.getDatabaseName().equals(databaseName)){
					return t;
				}
			}
			return null;
		}
	}
	
	public static final String ANY_STATE = "Any_State";		// SpringSaLaD stuff
	private double fieldBondLength = 1;
	public enum Subtype {
		INCOMPATIBLE("Not Compatible"),
		CREATION("Creation"),
		DECAY("Decay"),
		TRANSITION("Transition"),
		ALLOSTERIC("Allosteric"),
		BINDING("Binding");
		
		final public String columnName;		// column name in ModelProcessSpecsTableModel
		private Subtype(String columnName) {
			this.columnName = columnName;
		}
	}
	public enum TransitionCondition {
		NONE("None"),
		FREE("Free"),
		BOUND("Bound");
		
		final public String columnName;
		private TransitionCondition(String columnName) {
			this.columnName = columnName;
		}
	}

	
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private ReactionRuleMappingType fieldReactionRuleMapping = ReactionRuleMappingType.INCLUDED;
	
	public class ReactionRuleCombo implements IssueSource {	// used only for Issue reporting stuff
		final ReactionRuleSpec rs;
		final ReactionContext rc;
		
		public ReactionRuleCombo(ReactionRuleSpec rs, ReactionContext rc) {
			this.rs = rs;
			this.rc = rc;
		}
		public ReactionRuleSpec getReactionSpec() {
			return rs;
		}
		public ReactionContext getReactionContext() {
			return rc;
		}
	}

	public ReactionRuleSpec(ReactionRuleSpec argReactionRuleSpec) {
		setReactionRule(argReactionRuleSpec.reactionRule);
		this.fieldReactionRuleMapping = argReactionRuleSpec.fieldReactionRuleMapping;
		refreshDependencies();
	}


	public ReactionRuleSpec(ReactionRule argReactionRule) {
		setReactionRule(argReactionRule);
		refreshDependencies();
	}  


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param object java.lang.Object
 */
public boolean compareEqual(Matchable object) {
	ReactionRuleSpec reactionSpec = null;
	if (!(object instanceof ReactionRuleSpec)) {
		return false;
	}
	reactionSpec = (ReactionRuleSpec)object;
	if (!reactionRule.compareEqual(reactionSpec.reactionRule)) {
		return false;
	}
	if (fieldReactionRuleMapping != reactionSpec.fieldReactionRuleMapping) {
		return false;
	}
	return true;
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName,oldValue,newValue);
}

public static final String WrongNumberOfMolecules = "wrongNumberOfMolecules";
public static final String NumReactants = "numReactants";
public static final String NumProducts = "numProducts";
public static final String MtpReactantState = "mtpReactantState";		// the mtp with a state change
public static final String McpReactantState = "mcpReactantState";		// the site where the state changes
public static final String MtpProductState = "mtpProductState";
public static final String McpProductState = "mcpProductState";
public static final String MtpReactantBond = "mtpReactantBond";			// the mtp with a binding transition
public static final String McpReactantBond = "mcpReactantBond";			// the site that is bonding
public static final String StateTransitionCounter = "stateTransitionCounter";
public static final String BondTransitionCounter = "bondTransitionCounter";
public static final String CspReactantBond = "cspReactantBond";

// --------------------------------------------------------------------------------------------------------
public void analizeReaction(Map<String, Object> analysisResults) {
	List<ReactantPattern> rpList = reactionRule.getReactantPatterns();
	List<ProductPattern> ppList = reactionRule.getProductPatterns();
	// sanity check; get out of here fast if basic conditions are not satisfied
	// TODO: move some tests in issues code maybe
	analysisResults.put(WrongNumberOfMolecules, true);
	boolean wrongNumberOfReactantsProducts = false;
	if(rpList == null || rpList.isEmpty()) {
		analysisResults.put(NumReactants, 0);
		wrongNumberOfReactantsProducts = true;
	}
	if(ppList == null || ppList.isEmpty()) {
		analysisResults.put(NumProducts, 0);
		wrongNumberOfReactantsProducts = true;
	}
	if(rpList.size() > 2) {
		analysisResults.put(NumReactants, rpList.size());
		wrongNumberOfReactantsProducts = true;
	}
	if(ppList.size() > 2) {
		analysisResults.put(NumProducts, ppList.size());
		wrongNumberOfReactantsProducts = true;
	}
	if(wrongNumberOfReactantsProducts) {
		return;
	}
	analysisResults.put(NumReactants, rpList.size());
	analysisResults.put(NumProducts, ppList.size());

	for(ReactantPattern rp : rpList) {
		SpeciesPattern sp = rp.getSpeciesPattern();
		if(sp == null) {
			return;
		}
		List<MolecularTypePattern> mtpList = sp.getMolecularTypePatterns();
		if(mtpList == null || mtpList.isEmpty() || mtpList.size() > 2) {		// each reactant may have 1 or 2 molecule 
			return;
		}
	}
	for(ProductPattern pp : ppList) {
		SpeciesPattern sp = pp.getSpeciesPattern();
		if(sp == null) {
			return;
		}
		List<MolecularTypePattern> mtpList = sp.getMolecularTypePatterns();
		if(mtpList == null || mtpList.isEmpty() || mtpList.size() > 2) {		// each product may have 1 or 2 molecule 
			return;
		}
	}
	
	analysisResults.put(WrongNumberOfMolecules, false);
	// looks expensive but a springsalad binding reaction has exactly 2 reactants one molecule each
	// and exactly one product made of 2 molecules. Most other springsalad reactions are even simpler
	// since instances of mtp / mcp are different between reactants and products, we work with instances of mc / mp
	Map<MolecularComponentPattern, MolecularComponentPattern> reactantToProductSitesMap = new LinkedHashMap<> ();
	Map<MolecularComponentPattern, MolecularComponentPattern> productToReactantSitesMap = new LinkedHashMap<> ();
	Map<MolecularComponentPattern, MolecularTypePattern> siteToMoleculeMap = new LinkedHashMap<> ();
	
	// map each reactant site to corresponding product site and vice-versa, map each site to its molecule
	for(ReactantPattern rp : rpList) {
		for(ProductPattern pp : ppList) {
			SpeciesPattern spReactant = rp.getSpeciesPattern();
			SpeciesPattern spProduct = pp.getSpeciesPattern();
			List<MolecularTypePattern> mtpReactantList = spReactant.getMolecularTypePatterns();
			List<MolecularTypePattern> mtpProductList = spProduct.getMolecularTypePatterns();
			for(MolecularTypePattern mtpReactant : mtpReactantList) {
				for(MolecularTypePattern mtpProduct : mtpProductList) {
					MolecularType mtProduct = mtpProduct.getMolecularType();
					MolecularType mtReactant = mtpReactant.getMolecularType();
					String pmlProduct = mtpProduct.getParticipantMatchLabel();
					String pmlReactant = mtpReactant.getParticipantMatchLabel();
					if(mtProduct == mtReactant && pmlProduct.equals(pmlReactant)) {
						// bingo, matched a reactant with a product
						List<MolecularComponentPattern> mcpReactantList = mtpReactant.getComponentPatternList();
						List<MolecularComponentPattern> mcpProductList = mtpProduct.getComponentPatternList();
						if(mcpReactantList == null || mcpProductList == null || mcpReactantList.isEmpty() || mcpProductList.isEmpty()) {
							return;		// no components in some molecule, need at least one
						}
						for(MolecularComponentPattern mcpReactant : mcpReactantList) {
							MolecularComponentPattern mcpProduct = mtpProduct.getMolecularComponentPattern(mcpReactant.getMolecularComponent());
							reactantToProductSitesMap.put(mcpReactant, mcpProduct);
							productToReactantSitesMap.put(mcpProduct, mcpReactant);
							siteToMoleculeMap.put(mcpReactant, mtpReactant);
							siteToMoleculeMap.put(mcpProduct, mtpProduct);
						}
					}
				}
			}
		}
	}

	int mcpCount = 0;					// index in the for loop
	int stateTransitionCounter = 0;		// number of state changes between reactant site anc corresponding product site
	int bondTransitionCounter = 0;		// number of times when an unbound site becomes bound
	for(Map.Entry<MolecularComponentPattern, MolecularComponentPattern> entry : productToReactantSitesMap.entrySet()) {
		MolecularComponentPattern mcpProduct = entry.getKey();
		MolecularComponentPattern mcpReactant = entry.getValue();
		
		ComponentStatePattern cspReactant = mcpReactant.getComponentStatePattern();
		ComponentStatePattern cspProduct = mcpProduct.getComponentStatePattern();
		if(cspReactant != null) {
			if(!cspReactant.compareEqual(cspProduct)) {
				stateTransitionCounter++;		// state changed between corresponding reactant and product sites
				String mtpReactantKey = MtpReactantState + stateTransitionCounter;
				String mcpReactantKey = McpReactantState + stateTransitionCounter;
				String mtpProductKey = MtpProductState + stateTransitionCounter;
				String mcpProductKey = McpProductState + stateTransitionCounter;
				MolecularTypePattern mtpReactant = siteToMoleculeMap.get(mcpReactant);
				MolecularTypePattern mtpProduct = siteToMoleculeMap.get(mcpProduct);
				analysisResults.put(mtpReactantKey, mtpReactant);
				analysisResults.put(mcpReactantKey, mcpReactant);
				analysisResults.put(mtpProductKey, mtpProduct);
				analysisResults.put(mcpProductKey, mcpProduct);
			}
		}
		
		if(mcpReactant.getBondType() == BondType.None && mcpProduct.getBondType() == BondType.Specified) {
			MolecularTypePattern mtpReactant = siteToMoleculeMap.get(mcpProduct);
			bondTransitionCounter++;
			String mtpKey = MtpReactantBond + bondTransitionCounter;
			String mcpKey = McpReactantBond + bondTransitionCounter;
			analysisResults.put(mtpKey, mtpReactant);
			analysisResults.put(mcpKey, mcpReactant);
			
			String stateReactant;
			if(cspReactant == null) {
				stateReactant = "ERROR";
			} else if(cspReactant.isAny()) {
				stateReactant = ANY_STATE;
			} else {
				ComponentStateDefinition csdReactant = cspReactant.getComponentStateDefinition();
				stateReactant = csdReactant.getName();
			}
			String cspKey = CspReactantBond + bondTransitionCounter;
			analysisResults.put(cspKey, stateReactant);
		}
		mcpCount++;
	}
	analysisResults.put(StateTransitionCounter, stateTransitionCounter);
	analysisResults.put(BondTransitionCounter, bondTransitionCounter);
}
public Subtype getSubtype(Map<String, Object> analysisResults) {
	if(isCreationReaction(analysisResults) == true) {
		return Subtype.CREATION;
	}
	if(isDecayReaction(analysisResults) == true) {
		return Subtype.DECAY;
	}
	if(isTransitionReaction(analysisResults) == true) {
		return Subtype.TRANSITION;
	}
	if(isAllostericReaction(analysisResults) == true) {
		return Subtype.ALLOSTERIC;
	}
	if(isBindingReaction(analysisResults) == true) {
		return Subtype.BINDING;
	}
	return Subtype.INCOMPATIBLE;
}
public TransitionCondition getTransitionCondition(Map<String, Object> analysisResults) {
	Subtype st = getSubtype(analysisResults);
	if(Subtype.TRANSITION == st) {
		List<ReactantPattern> rpList = reactionRule.getReactantPatterns();		// we already know that this list is not empty
		List<ProductPattern> ppList = reactionRule.getProductPatterns();
		List<MolecularTypePattern> mtpReactantList = rpList.get(0).getSpeciesPattern().getMolecularTypePatterns();
		List<MolecularTypePattern> mtpProductList = ppList.get(0).getSpeciesPattern().getMolecularTypePatterns();
		if(mtpReactantList.size() == 1 && mtpProductList.size() == 1) {
			int numAnyStates = 0;
			int numBondsPossible = 0;
			int numBondsUnbound = 0;
			MolecularTypePattern mtpReactant = mtpReactantList.get(0);
			for(MolecularComponentPattern mcpReactant : mtpReactant.getComponentPatternList()) {
				ComponentStatePattern cspReactant = mcpReactant.getComponentStatePattern();
				BondType bondTypeReactant = mcpReactant.getBondType();
				if(cspReactant == null) {
					return null;	// all sites must have at least one state
				}
				if(!cspReactant.isAny() && BondType.Possible != bondTypeReactant) {
					// this is the transition site, bond must be "Possible"
					return null;
				}
				if(!cspReactant.isAny()) {
					;
				} else {
					numAnyStates++;
				}
				if(BondType.Possible == bondTypeReactant) {
					numBondsPossible++;
				} else if(BondType.None == bondTypeReactant) {
					numBondsUnbound++;
				} else {
					return null;	// all bonds must be either none or possible
				}
			}
			if(numBondsPossible-1 == numAnyStates && numBondsPossible == mtpReactant.getComponentPatternList().size()) {
				return TransitionCondition.NONE;	// transition reaction condition is "None" (all sites except the transition site are possibly bound AND in "Any" state)
			} else if(numBondsUnbound == numAnyStates && numBondsUnbound == mtpReactant.getComponentPatternList().size()-1) {
				return TransitionCondition.FREE;	// transition reaction condition is "Free" (all sites except the transition site are unbound AND in "Any" state)
			}
			return null;
		} else if(mtpReactantList.size() == 2 && mtpProductList.size() == 2) {
			MolecularTypePattern mtpTransitionReactant = (MolecularTypePattern)analysisResults.get(MtpReactantState + "1");		// one mtp, one mcp
			int totalExistsBonds = 0;
			int totalExplicitStates = 0;
			int numExplicitStates[] = { 0, 0};
			int numExistsBonds[] = { 0, 0 };
			for(int i=0; i < mtpReactantList.size(); i++) {
				MolecularTypePattern mtpReactant = mtpReactantList.get(i);
				List<MolecularComponentPattern> mcpList = mtpReactant.getComponentPatternList();
				if(mcpList == null || mcpList.isEmpty()) {
					return null;
				}
				for(MolecularComponentPattern mcpReactant : mtpReactant.getComponentPatternList()) {
					ComponentStatePattern cspReactant = mcpReactant.getComponentStatePattern();
					if(cspReactant == null) {
						return null;	// all sites must have at least one state
					}
					BondType bondTypeReactant = mcpReactant.getBondType();
					if(BondType.Exists == bondTypeReactant) {
						if(mtpTransitionReactant == mtpReactant) {
							// the transition site must be in one molecule, the condition bound site must be in the other
							return null;
						}
						numExistsBonds[i]++;	// the condition binding site has bond type "Exists"
						totalExistsBonds++;
					} else if(BondType.Possible != bondTypeReactant) {
						return null;		// all other bonds in the reactant must be of type "Possible"
						// we know that the 2 molecules are bound somehow, because they are in the same species
						// but we don't care between which sites the bond is
					}
					if(cspReactant.isAny()) {
						continue;
					} else {
						// the site with explicit state must be the same that has bond type exists
						if(BondType.Exists != bondTypeReactant && mtpTransitionReactant != mtpReactant) {
							return null;
						}
					}
					numExplicitStates[i]++;
					totalExplicitStates++;
				}
			}
			
			if(totalExistsBonds != 1) {
				return null;		// the condition bound site is the only one with BondType "exists"
			}
			if(totalExplicitStates < 1 || totalExplicitStates > 2) {
				// transitioning site must be in explicit state
				// the condition binding site may be explicit or any
				// all the other sites must be in "Any" state
				return null;
			}
			if(numExplicitStates[0] == 1 && numExplicitStates[1] == 1) {
				// above we made sure that the transition molecule and the condition molecule are not the same
				return TransitionCondition.BOUND;	// must have exactly one explicit state in each molecule 
			} else if(numExplicitStates[0] == 0 && numExplicitStates[1] == 1 && numExistsBonds[0] == 1) {
				// this else-if and the next: the only explicit site is the one transitioning
				// the conditional site type may also be in the "any" state but must have bond type "Exists"
				return TransitionCondition.BOUND;
			} else if(numExplicitStates[0] == 1 && numExplicitStates[1] == 0 && numExistsBonds[1] == 1) {
				return TransitionCondition.BOUND;
			}
		}
		return null;
	}
	return null;
}
private boolean isCreationReaction(Map<String, Object> analysisResults) {
	List<ReactantPattern> rpList = reactionRule.getReactantPatterns();
	if(rpList.size() == 1) {
		SpeciesPattern sp = rpList.get(0).getSpeciesPattern();
		if(sp.getMolecularTypePatterns().size() == 1) {
			MolecularTypePattern mtp = sp.getMolecularTypePatterns().get(0);
			if(mtp.getComponentPatternList().size() == 0 && SpeciesContextSpec.SourceMoleculeString.equals(mtp.getMolecularType().getName())) {
				return true;
			}
		}
	}
	return false;
}
private boolean isDecayReaction(Map<String, Object> analysisResults) {
	List<ProductPattern> ppList = reactionRule.getProductPatterns();
	if(ppList.size() == 1) {
		SpeciesPattern sp = ppList.get(0).getSpeciesPattern();
		if(sp.getMolecularTypePatterns().size() == 1) {
			MolecularTypePattern mtp = sp.getMolecularTypePatterns().get(0);
			if(mtp.getComponentPatternList().size() == 0 && SpeciesContextSpec.SinkMoleculeString.equals(mtp.getMolecularType().getName())) {
				return true;
			}
		}
	}
	return false;
}
private boolean isTransitionReaction(Map<String, Object> analysisResults) {
	Object ret = analysisResults.get(WrongNumberOfMolecules);
	if(ret == null || (boolean)ret == true) {
		return false;
	}
	ret = analysisResults.get(StateTransitionCounter);
	if(ret == null || (int)ret != 1) {
		return false;		// we need exactly 1 state transition
	}
	ret = analysisResults.get(BondTransitionCounter);
	if(ret == null || (int)ret != 0) {
		return false;		// no binding allowed
	}
	if(analysisResults.get(NumReactants) == null || analysisResults.get(NumProducts) == null)  {
		return false;
	}
	int numReactants = (int)analysisResults.get(NumReactants);
	int numProducts = (int)analysisResults.get(NumProducts);
	if(numReactants != 1 || numProducts != 1) {
		return false;		// we may only have 1 reactant and 1 product
	}
	if((numReactants == 1 && numProducts != 1) || (numReactants == 2 && numProducts != 2)) {
		return false;		// equal number of reactant and products, either 1 of each or 2 of each
	}
	List<ReactantPattern> rpList = reactionRule.getReactantPatterns();		// we already know that this list is not empty
	List<ProductPattern> ppList = reactionRule.getProductPatterns();
	List<MolecularTypePattern> mtpReactantList = rpList.get(0).getSpeciesPattern().getMolecularTypePatterns();
	List<MolecularTypePattern> mtpProductList = ppList.get(0).getSpeciesPattern().getMolecularTypePatterns();
	if((mtpReactantList.size() == 1 && mtpProductList.size() != 1) || (mtpReactantList.size() == 2 && mtpProductList.size() != 2)) {
		return false;		// equal number of molecules in the reactant and product, either 1 of each or 2 of each
	}

	if(mtpReactantList.size() == 1 && mtpProductList.size() == 1) {		// check if it's a simple transition
		MolecularTypePattern mtpReactant = mtpReactantList.get(0);
		List<MolecularComponentPattern> mcpList = mtpReactant.getComponentPatternList();
		if(mcpList == null || mcpList.isEmpty()) {
			return false;
		}
		int numAnyStates = 0;
		int numBondsPossible = 0;
		int numBondsUnbound = 0;
		for(MolecularComponentPattern mcpReactant : mtpReactant.getComponentPatternList()) {
			ComponentStatePattern cspReactant = mcpReactant.getComponentStatePattern();
			BondType bondTypeReactant = mcpReactant.getBondType();
			if(cspReactant == null) {
				return false;	// all sites must have at least one state
			}
			if(!cspReactant.isAny() && BondType.Possible != bondTypeReactant) {
				// this is the transition site, bond must be "Possible"
				return false;
			}
			if(!cspReactant.isAny()) {
				;
			} else {
				numAnyStates++;
			}
			if(BondType.Possible == bondTypeReactant) {
				numBondsPossible++;
			} else if(BondType.None == bondTypeReactant) {
				numBondsUnbound++;
			} else {
				return false;	// all bonds must be either none or possible
			}
		}
		if(numBondsPossible-1 == numAnyStates && numBondsPossible == mtpReactant.getComponentPatternList().size()) {
			return true;	// transition reaction condition is "None" (all sites except the transition site are possibly bound AND in "Any" state)
		} else if(numBondsUnbound == numAnyStates && numBondsUnbound == mtpReactant.getComponentPatternList().size()-1) {
			return true;	// transition reaction condition is "Free" (all sites except the transition site are unbound AND in "Any" state)
		}
	} else if(mtpReactantList.size() == 2 && mtpProductList.size() == 2) {		// check if it's a 2 molecules bond-conditioned transition
		// the transition reactant is the one containing the Site which is transitioning (changing state)
		// the other molecule is the bond condition
		MolecularTypePattern mtpTransitionReactant = (MolecularTypePattern)analysisResults.get(MtpReactantState + "1");		// one mtp, one mcp
		int totalExistsBonds = 0;
		int totalExplicitStates = 0;
		int numExplicitStates[] = { 0, 0};
		int numExistsBonds[] = { 0, 0 };
		for(int i=0; i < mtpReactantList.size(); i++) {
			MolecularTypePattern mtpReactant = mtpReactantList.get(i);
			List<MolecularComponentPattern> mcpList = mtpReactant.getComponentPatternList();
			if(mcpList == null || mcpList.isEmpty()) {
				return false;
			}
			for(MolecularComponentPattern mcpReactant : mtpReactant.getComponentPatternList()) {
				ComponentStatePattern cspReactant = mcpReactant.getComponentStatePattern();
				if(cspReactant == null) {
					return false;	// all sites must have at least one state
				}
				BondType bondTypeReactant = mcpReactant.getBondType();
				if(BondType.Exists == bondTypeReactant) {
					if(mtpTransitionReactant == mtpReactant) {
						// the transition site must be in one molecule, the condition bound site must be in the other
						return false;
					}
					numExistsBonds[i]++;	// the condition binding site has bond type "Exists"
					totalExistsBonds++;
				} else if(BondType.Possible != bondTypeReactant) {
					return false;		// all other bonds in the reactant must be of type "Possible"
					// we know that the 2 molecules are bound somehow, because they are in the same species
					// but we don't care between which sites the bond is
				}
				if(cspReactant.isAny()) {
					continue;
				} else {
					// the site with explicit state must be the same that has bond type exists
					if(BondType.Exists != bondTypeReactant && mtpTransitionReactant != mtpReactant) {
						return false;
					}
				}
				numExplicitStates[i]++;
				totalExplicitStates++;
			}
		}
		
		if(totalExistsBonds != 1) {
			return false;		// the condition bound site is the only one with BondType "exists"
		}
		if(totalExplicitStates < 1 || totalExplicitStates > 2) {
			// transitioning site must be in explicit state
			// the condition binding site may be explicit or any
			// all the other sites must be in "Any" state
			return false;
		}
		if(numExplicitStates[0] == 1 && numExplicitStates[1] == 1) {
			// above we made sure that the transition molecule and the condition molecule are not the same
			return true;	// must have exactly one explicit state in each molecule 
		} else if(numExplicitStates[0] == 0 && numExplicitStates[1] == 1 && numExistsBonds[0] == 1) {
			// this else-if and the next: the only explicit site is the one transitioning
			// the conditional site type may also be in the "any" state but must have bond type "Exists"
			return true;
		} else if(numExplicitStates[0] == 1 && numExplicitStates[1] == 0 && numExistsBonds[1] == 1) {
			return true;
		}
	}
	return false;
}
private boolean isAllostericReaction(Map<String, Object> analysisResults) {
	Object ret = analysisResults.get(WrongNumberOfMolecules);
	if(ret == null || (boolean)ret == true) {
		return false;
	}
	ret = analysisResults.get(StateTransitionCounter);
	if(ret == null || (int)ret != 1) {
		return false;		// we need exactly 1 state transition
	}
	ret = analysisResults.get(BondTransitionCounter);
	if(ret == null || (int)ret != 0) {
		return false;		// no binding allowed
	}
	ret = analysisResults.get(NumReactants);
	if(ret == null || (int)ret != 1) {
		return false;		// exactly 1 reactant
	}
	ret = analysisResults.get(NumProducts);
	if(ret == null || (int)ret != 1) {
		return false;		// exactly 1 product
	}
	List<ReactantPattern> rpList = reactionRule.getReactantPatterns();		// we already know that this list is not empty
	List<ProductPattern> ppList = reactionRule.getProductPatterns();
	List<MolecularTypePattern> mtpReactantList = rpList.get(0).getSpeciesPattern().getMolecularTypePatterns();
	List<MolecularTypePattern> mtpProductList = ppList.get(0).getSpeciesPattern().getMolecularTypePatterns();
	if(mtpReactantList.size() != 1 || mtpProductList.size() != 1) {
		return false;
	}
	MolecularTypePattern mtpReactant = mtpReactantList.get(0);
	MolecularTypePattern mtpProduct = mtpProductList.get(0);
	int reactantExplicitStates = 0;
	int productExplicitStates = 0;
	List<MolecularComponentPattern> mcpReactantList = mtpReactant.getComponentPatternList();
	List<MolecularComponentPattern> mcpProductList = mtpProduct.getComponentPatternList();
	for(MolecularComponentPattern mcp : mcpReactantList) {
		if(mcp.getComponentStatePattern() == null) {
			return false;		// all sites must have at least one state
		}
		if(!mcp.getComponentStatePattern().isAny()) {
			reactantExplicitStates++;
		}
	}
	for(MolecularComponentPattern mcp : mcpProductList) {
		if(!mcp.getComponentStatePattern().isAny()) {
			productExplicitStates++;
		}
	}
	if(reactantExplicitStates == 2 && productExplicitStates == 2) {
		return true;	// we need 2 explicit states: one that is transitioning and one for the allosteric condition
	}
	return false;
}
private boolean isBindingReaction(Map<String, Object> analysisResults) {
	Object ret = analysisResults.get(WrongNumberOfMolecules);
	if(ret == null || (boolean)ret == true) {
		return false;
	}
	ret = analysisResults.get(StateTransitionCounter);
	if(ret == null || (int)ret != 0) {
		return false;		// no state transitions allowed in a binding reaction
	}
	ret = analysisResults.get(NumReactants);
	if(ret == null || (int)ret != 2) {
		return false;		// exactly 2 reactants
	}
	ret = analysisResults.get(NumProducts);
	if(ret == null || (int)ret != 1) {
		return false;		// exactly 1 product
	}
	List<ReactantPattern> rpList = reactionRule.getReactantPatterns();		// we already know that these 2 lists are not empty
	List<ProductPattern> ppList = reactionRule.getProductPatterns();
	for(ReactantPattern p : rpList) {
		if(p.getSpeciesPattern().getMolecularTypePatterns().size() != 1) {			// each reactant must have 1 molecule
			return false;
		}
	}
	if(ppList.get(0).getSpeciesPattern().getMolecularTypePatterns().size() != 2) {	// the product must have 2 molecules
		return false;
	}
	ret = analysisResults.get(BondTransitionCounter);
	if(ret == null) {
		return false;
	}
	int bindingTransitions = (int)ret;
	if(bindingTransitions == 2) {
		return true;	// one binding reaction produces 2 binding transitions
	}
	return false;
}

public void writeData(StringBuilder sb, Subtype subtype) {			// SpringSaLaD exporting the binding rule information
	Map<String, Object> analysisResults = new LinkedHashMap<> ();
	analizeReaction(analysisResults);
	
	switch(getSubtype(analysisResults)) {
	case CREATION:
	case DECAY:
		// we invoke the static method below (writeDecayData()) on all species at once from SpringSaLaDExporter.getDocumentAsString()
		// because we must collect first all the species that are being created / destroyed and their rates
		break;
	case TRANSITION:
		writeTransitionData(sb, subtype, analysisResults);
		break;
	case ALLOSTERIC:
		writeAllostericData(sb, subtype, analysisResults);
		break;
	case BINDING:
		writeBindingData(sb, subtype, analysisResults);
		break;
	default:
		break;
	}
}

public static void writeDecayData(StringBuilder sb, Map<SpeciesContext, Pair<String, String>> moleculeCreationDecayRates) {
	for (Map.Entry<SpeciesContext, Pair<String, String>> entry : moleculeCreationDecayRates.entrySet()) {
		SpeciesContext sc = entry.getKey();
		Pair<String, String> pair = entry.getValue();
		sb.append("'").append(sc.getName()).append("' : ")
			.append("kcreate  ").append(pair.one).append("  ")
			.append("kdecay  ").append(pair.two);
		sb.append("\n");
	}
}
private void writeTransitionData(StringBuilder sb, Subtype subtype, Map<String, Object> analysisResults) {
	if(subtype != ReactionRuleSpec.Subtype.TRANSITION) {
		return;
	}
	// one reactant and one product, these are the mtp and mcp of the site that is transitioning its state
	MolecularTypePattern mtpTransitionReactant = (MolecularTypePattern)analysisResults.get(MtpReactantState + "1");		// one mtp, one mcp
	MolecularComponentPattern mcpTransitionReactant = (MolecularComponentPattern)analysisResults.get(McpReactantState + "1");
	MolecularComponentPattern mcpTransitionProduct = (MolecularComponentPattern)analysisResults.get(McpProductState + "1");
	ComponentStatePattern cspTransitionReactant = mcpTransitionReactant.getComponentStatePattern();
	ComponentStatePattern cspTransitionProduct = mcpTransitionProduct.getComponentStatePattern();
	
	MolecularTypePattern mtpConditionReactant = null;			// the bound condition molecule
	MolecularComponentPattern mcpConditionReactant = null;		// the bound condition site (must be bond type "Exists")
	String stateConditionReactant = ANY_STATE;
	
	// we may have 2 mtp if the reaction condition is "Bound"!
	List<ReactantPattern> rpList = reactionRule.getReactantPatterns();		// we already know that this list is not empty
	List<MolecularTypePattern> mtpReactantList = rpList.get(0).getSpeciesPattern().getMolecularTypePatterns();

	TransitionCondition transitionCondition = TransitionCondition.FREE;		// molecules with just one site belong to free
	if(mtpReactantList.size() == 1) {	// transition reaction condition "None" or "Free"
		for(MolecularComponentPattern mcpOtherReactant : mtpTransitionReactant.getComponentPatternList()) {
			if(mcpOtherReactant != mcpTransitionReactant) {
				if(BondType.None == mcpOtherReactant.getBondType()) {
					// transition "None" has the bond types of all sites set to "None" (except the transitioning site which is "Possible")
					transitionCondition = TransitionCondition.NONE;
					break;
				}
			}
		}
	} else if(mtpReactantList.size() == 2) {	// transition reaction condition "Bound"
		transitionCondition = TransitionCondition.BOUND;
		if(mtpTransitionReactant == mtpReactantList.get(0)) {
			mtpConditionReactant = mtpReactantList.get(1);
		} else {
			mtpConditionReactant = mtpReactantList.get(0);
		}
		// now let's find the Site and the State of the binding condition molecule
		for(MolecularComponentPattern mcpCandidate : mtpConditionReactant.getComponentPatternList()) {
			if(BondType.Exists == mcpCandidate.getBondType()) {
				mcpConditionReactant = mcpCandidate;	// found the bond condition site, it's the one with bond type "Exists"
				if(!mcpConditionReactant.getComponentStatePattern().isAny()) {
					stateConditionReactant = mcpConditionReactant.getComponentStatePattern().getComponentStateDefinition().getName();
				}
				break;
			}
		}
	} else {
		throw new RuntimeException("writeTransitionData() error: something is wrong");
	}
	
	RbmKineticLaw kineticLaw = reactionRule.getKineticLaw();
	Expression kon = kineticLaw.getLocalParameterValue(RbmKineticLaw.RbmKineticLawParameterType.MassActionForwardRate);
	
	sb.append("'").append(reactionRule.getName()).append("' ::     ");
	sb.append("'").append(mtpTransitionReactant.getMolecularType().getName()).append("' : '")
		.append(mcpTransitionReactant.getMolecularComponent().getName()).append("' : '")
		.append(cspTransitionReactant.getComponentStateDefinition().getName());
	sb.append("' --> '");
	sb.append(cspTransitionProduct.getComponentStateDefinition().getName());
	sb.append("'  Rate ").append(kon.infix());
	sb.append("  Condition ").append(transitionCondition.columnName);
	if(TransitionCondition.BOUND == transitionCondition) {
		sb.append(" '").append(mtpConditionReactant.getMolecularType().getName()).append("' : '")
		.append(mcpConditionReactant.getMolecularComponent().getName()).append("' : '")
		.append(stateConditionReactant);
	}
	sb.append("\n");
}
private void writeAllostericData(StringBuilder sb, Subtype subtype, Map<String, Object> analysisResults) {
	if(subtype != ReactionRuleSpec.Subtype.ALLOSTERIC) {
		return;
	}
	// one reactant and one product, one mtp, the mcp that is changing state
	MolecularTypePattern mtpTransitionReactant = (MolecularTypePattern)analysisResults.get(MtpReactantState + "1");
	MolecularComponentPattern mcpTransitionReactant = (MolecularComponentPattern)analysisResults.get(McpReactantState + "1");
	MolecularComponentPattern mcpTransitionProduct = (MolecularComponentPattern)analysisResults.get(McpProductState + "1");
	ComponentStatePattern cspTransitionReactant = mcpTransitionReactant.getComponentStatePattern();
	ComponentStatePattern cspTransitionProduct = mcpTransitionProduct.getComponentStatePattern();
	int mcpAllostericReactantIndex = -1;
	MolecularComponentPattern mcpAllostericReactant = null;
	for(MolecularComponentPattern mcp : mtpTransitionReactant.getComponentPatternList()) {
		mcpAllostericReactantIndex++;
		if(mcpTransitionReactant == mcp) {
			continue;		// found the allosteric site index
		}
		if(mcp.getComponentStatePattern().isAny()) {
			continue;	// the allosteric state must be explicit
		}
		mcpAllostericReactant = mcp;
		break;		// we found the one and only site with an explicit state (other than the state transitioning site
	}
	if(mcpAllostericReactant == null) {
		throw new RuntimeException("writeAllostericData() error: something is wrong");
	}
	ComponentStatePattern cspAllostericReactant = mcpAllostericReactant.getComponentStatePattern();
	
	RbmKineticLaw kineticLaw = reactionRule.getKineticLaw();
	Expression kon = kineticLaw.getLocalParameterValue(RbmKineticLaw.RbmKineticLawParameterType.MassActionForwardRate);

	sb.append("'").append(reactionRule.getName()).append("' ::     ");
	sb.append("'").append(mtpTransitionReactant.getMolecularType().getName()).append("' : '")
		.append(mcpTransitionReactant.getMolecularComponent().getName()).append("' : '")
		.append(cspTransitionReactant.getComponentStateDefinition().getName());
	sb.append("' --> '");
	sb.append(cspTransitionProduct.getComponentStateDefinition().getName());
	sb.append("'  Rate ").append(kon.infix());
	sb.append(" Allosteric_Site ").append(mcpAllostericReactantIndex);
	sb.append(" State '").append(cspAllostericReactant.getComponentStateDefinition().getName()).append("'");
	sb.append("\n");
}
private void writeBindingData(StringBuilder sb, Subtype subtype, Map<String, Object> analysisResults) {
	if(subtype != ReactionRuleSpec.Subtype.BINDING) {
		return;
	}
	// here we only deal with the 2 reactants
	MolecularTypePattern mtpReactantOne = (MolecularTypePattern)analysisResults.get(MtpReactantBond + "1");		// '1' - reactant. left side of the bond
	MolecularTypePattern mtpReactantTwo = (MolecularTypePattern)analysisResults.get(MtpReactantBond + "2");		// '2' - reactant, right side of the bond
	MolecularComponentPattern mcpReactantOne = (MolecularComponentPattern)analysisResults.get(McpReactantBond + "1");
	MolecularComponentPattern mcpReactantTwo = (MolecularComponentPattern)analysisResults.get(McpReactantBond + "2");
	String stateReactantOne = (String)analysisResults.get(CspReactantBond + "1");
	String stateReactantTwo =  (String)analysisResults.get(CspReactantBond + "2");
	if(mtpReactantOne == null || mtpReactantTwo == null || mcpReactantOne == null || mcpReactantTwo == null) {
		throw new RuntimeException("writeBindingData() error: something is wrong");
	}
	
	sb.append("'").append(reactionRule.getName()).append("'       ");
	sb.append("'").append(mtpReactantOne.getMolecularType().getName()).append("' : '")
		.append(mcpReactantOne.getMolecularComponent().getName()).append("' : '")
		.append(stateReactantOne);
		sb.append("'  +  '");
	sb.append(mtpReactantTwo.getMolecularType().getName()).append("' : '")
		.append(mcpReactantTwo.getMolecularComponent().getName()).append("' : '")
		.append(stateReactantTwo);
	
	RbmKineticLaw kineticLaw = reactionRule.getKineticLaw();
	Expression kon = kineticLaw.getLocalParameterValue(RbmKineticLaw.RbmKineticLawParameterType.MassActionForwardRate);
	Expression koff = kineticLaw.getLocalParameterValue(RbmKineticLaw.RbmKineticLawParameterType.MassActionReverseRate);
	sb.append("'  kon  ").append(kon.infix());
	sb.append("  koff ").append(koff.infix());
	sb.append("  Bond_Length ").append(Double.toString(getFieldBondLength()));
	sb.append("\n");
}

public SpeciesContext getCreatedSpecies(SpeciesContextSpec[] speciesContextSpecs, Map<String, Object> analysisResults) {
	if(!isCreationReaction(analysisResults)) {
		return null;	// also verify we use the reserved name SpeciesContextSpec.SourceMoleculeString
	}
	List<ProductPattern> ppList = reactionRule.getProductPatterns();
	if(ppList.size() == 1) {
		SpeciesPattern spOurs = ppList.get(0).getSpeciesPattern();
		for(SpeciesContextSpec scs : speciesContextSpecs) {
			SpeciesContext scCandidate = scs.getSpeciesContext();
			SpeciesPattern spCandidate = scCandidate.getSpeciesPattern();
			if(spOurs.compareEqual(spCandidate)) {
				return scCandidate;
			}
		}
	}
	return null;
}
public SpeciesContext getDestroyedSpecies(SpeciesContextSpec[] speciesContextSpecs, Map<String, Object> analysisResults) {
	if(!isDecayReaction(analysisResults)) {
		return null;
	}
	List<ReactantPattern> rpList = reactionRule.getReactantPatterns();
	if(rpList.size() == 1) {
		SpeciesPattern spOurs = rpList.get(0).getSpeciesPattern();
		MolecularType mtOurs = spOurs.getMolecularTypePatterns().get(0).getMolecularType();
		for(SpeciesContextSpec scs : speciesContextSpecs) {
			SpeciesContext scCandidate = scs.getSpeciesContext();
			SpeciesPattern spCandidate = scCandidate.getSpeciesPattern();
			MolecularType mtCandidate = spCandidate.getMolecularTypePatterns().get(0).getMolecularType();
			if(mtOurs == mtCandidate) {
				return scCandidate;
			}
		}
	}
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (11/1/2005 10:06:04 AM)
 * @param issueList java.util.Vector
 */
private final static String GenericTip = "Please edit the reaction so that it matches a SpringSaLaD subtype or disable it in this table";
public void gatherIssues(IssueContext issueContext, List<Issue> issueList, ReactionContext rc) {
	ReactionRuleCombo r = new ReactionRuleCombo(this, rc);
	ReactionRule reactionRule = getReactionRule();
//	if(!isExcluded() && rc.getSimulationContext().isStoch() && (rc.getSimulationContext().getGeometry().getDimension()>0)) {
//		boolean haveParticle = false;
//		boolean haveContinuous = false;
//		for(ReactionParticipant p : reactionRule.getReactionParticipants()) {
//			if(p instanceof Product || p instanceof Reactant) {
//				SpeciesContextSpec candidate = rc.getSpeciesContextSpec(p.getSpeciesContext());
//				if(candidate.isForceContinuous() && !candidate.isConstant()) {
//					haveParticle = true;
//				} 
//				else if(!candidate.isForceContinuous() && !candidate.isConstant()) {
//					haveContinuous = true;
//				} 
//			}
//		}
//		if(haveParticle && haveContinuous) {
//			String msg = "Reaction " + step.getName() + " has both continuous and particle Participants.";
//			String tip = "Mass conservation for reactions of binding between discrete and continuous species is handled approximately. <br>" +
//					"To avoid any algorithmic approximation, which may produce undesired results, the user is advised to indicate <br>" +
//					"the continuous species in those reactions as modifiers (i.e. 'catalysts') in the physiology.";
//			issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.SEVERITY_WARNING));
//		}
//	}
	if(rc.getSimulationContext().getApplicationType() == SimulationContext.Application.SPRINGSALAD) {
		Map<String, Object> analysisResults = new LinkedHashMap<> ();
		analizeReaction(analysisResults);
		if(Subtype.INCOMPATIBLE == getSubtype(analysisResults)) {
			List<ReactantPattern> rpList = reactionRule.getReactantPatterns();
			List<ProductPattern> ppList = reactionRule.getProductPatterns();
			if(rpList == null || ppList == null) {
				String msg = "At least one reactant and one product are required.";
				String tip = GenericTip;
				issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
				return;
			}
			int numReactants = rpList.size();
			int numProducts = ppList.size();
			if(numReactants == 0 || numProducts == 0) {
				String msg = "At least one reactant and one product are required.";
				String tip = GenericTip;
				issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
				return;
			}
			if(numProducts > 1) {
				String msg = "SpringSaLaD only accepts one Product.";
				String tip = GenericTip;
				issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
				return;
			}
			if(numReactants > 2) {
				String msg = "SpringSaLaD only accepts 2 Reactants for the binding reactions and 1 Reactant for the other subtypes.";
				String tip = GenericTip;
				issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
				return;
			}
		}
		if(Subtype.INCOMPATIBLE == getSubtype(analysisResults)) {
			Object bondObject = analysisResults.get(BondTransitionCounter);
			Object stateObject = analysisResults.get(StateTransitionCounter);
			if(bondObject != null && stateObject != null) {
				int bondTransitionCounter = (int)bondObject;
				int stateTransitionCounter = (int)stateObject;
				if(bondTransitionCounter > 3) {		// a binding reaction produces 2 transitions
					String msg = "SpringSaLaD only accepts one binding transition in any reaction.";
					String tip = GenericTip;
					issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
					return;
				}
				if(stateTransitionCounter > 1) {
					String msg = "SpringSaLaD only accepts one state transition in any reaction.";
					String tip = GenericTip;
					issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
					return;
				}
				if(stateTransitionCounter > 0 && bondTransitionCounter > 0) {
					String msg = "SpringSaLaD does not accept a combination of state and binding transitions in any reaction.";
					String tip = GenericTip;
					issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
					return;
				}
			}
		}
		if(Subtype.INCOMPATIBLE == getSubtype(analysisResults)) {
			String msg = "Reaction incompatible with any SpringSaLaD reaction subtype.";
			String tip = GenericTip;
			issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
		}
		
	} else {		// not SpringSaLaD
		if(reactionRule.getReactantPatterns() == null || reactionRule.getProductPatterns() == null) {
			return;		// issue reported in physiology
		}
		// check if there are disjoint patterns (molecules not explicitely connected through components)
		for(ReactantPattern rp : reactionRule.getReactantPatterns()) {
			SpeciesPattern sp = rp.getSpeciesPattern();
			if(sp.getMolecularTypePatterns().size() < 2) {
				continue;	// no point in looking since the pattern has a single molecule
			}
			boolean isDisjoint = false;
			for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
				if(mtp.isDisjoint()) {
					isDisjoint = true;
					break;		// found an unconnected molecule in the species pattern
				}
			}
			if(isDisjoint) {	// tell the world
				String message = "Found disjoint sets in a reactant pattern, as in A().B() with no explicit bond through components.";
				String toolTip = "These type of patterns may make NFSim run slower or even fail. Is it highly advised to express this pattern with a different syntax.";
				issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, message, toolTip, Issue.Severity.WARNING));
				return;
			}
		}
		for(ProductPattern pp : reactionRule.getProductPatterns()) {
			SpeciesPattern sp = pp.getSpeciesPattern();
			if(sp.getMolecularTypePatterns().size() < 2) {
				continue;
			}
			boolean isDisjoint = false;
			for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
				if(mtp.isDisjoint()) {
					isDisjoint = true;
					break;
				}
			}
			if(isDisjoint) {
				String message = "Found disjoint sets in a product pattern, as in A().B() with no explicit bond through components.";
				String toolTip = "These type of patterns may make NFSim run slower or even fail. Is it highly advised to express this pattern with a different syntax.";
				issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, message, toolTip, Issue.Severity.WARNING));
				return;
			}
		}
	}
}

/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Gets the reactionMapping property (int) value.
 * @return The reactionMapping property value.
 * @see #setReactionMapping
 */
public ReactionRuleMappingType getReactionRuleMapping() {
	return fieldReactionRuleMapping;
}

/**
 * Insert the method's description here.
 * Creation date: (1/24/01 1:50:51 PM)
 * @return cbit.vcell.model.ReactionStep
 */
public ReactionRule getReactionRule() {
	return reactionRule;
}

/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isExcluded() {
	return fieldReactionRuleMapping == ReactionRuleMappingType.EXCLUDED;
}


/**
 * This method was created in VisualAge.
 */
public void refreshDependencies() {
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}

/**
 * Sets the reactionMapping property (int) value.
 * @param reactionMapping The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getReactionMapping
 */
public void setReactionRuleMapping(ReactionRuleMappingType reactionRuleMapping) {
	ReactionRuleMappingType oldValue = fieldReactionRuleMapping;
	fieldReactionRuleMapping = reactionRuleMapping;
	firePropertyChange("reactionRuleMapping", oldValue, reactionRuleMapping);
}

/**
 * Insert the method's description here.
 * Creation date: (2/14/2002 5:07:05 PM)
 * @param rs cbit.vcell.model.ReactionStep
 */
void setReactionRule(ReactionRule rr) {
	this.reactionRule = rr;	
}

/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String toString() {
	StringBuffer sb = new StringBuffer();
	
	sb.append(getClass().getName()+"\n");
   if (reactionRule != null) { sb.append(":'"+reactionRule.getName()+"' ("+fieldReactionRuleMapping.getDisplayName()+")\n"); }
 	
	return sb.toString();
}


@Override
public ReactionRule getModelProcess() {
	// TODO Auto-generated method stub
	return reactionRule;
}

@Override
public boolean isFast() {
	return false;
}

public double getFieldBondLength() {
	return fieldBondLength;
}

public void setFieldBondLength(double fieldBondLength) {
	this.fieldBondLength = fieldBondLength;
}


}
