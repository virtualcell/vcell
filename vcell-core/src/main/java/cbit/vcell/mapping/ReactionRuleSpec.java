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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import cbit.vcell.mapping.SimulationContext.SimulationContextParameter;
import cbit.vcell.model.Feature;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.RbmKineticLaw;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionRuleParticipant;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Structure.SpringStructureEnum;
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
	
	public static final String ANY_STATE_STRING = "Any_State";		// SpringSaLaD stuff
	private double fieldBondLength = 1;		// only used for Subtype.BINDING reactions
	
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
		public static Subtype fromName(String nameCandidate) {
			for(Subtype st : Subtype.values()) {
				if(st.columnName.equals(nameCandidate)) {
					return st;
				}
			}
			return null;
		}
	}
	public enum TransitionCondition {	// everywhere internally in vcell we use RBM bond type naming conventions
		/*
		public enum MolecularComponentPattern.BondType {
			Specified(""), // numbers	// explicit (in springsalad this is TransitionCondition.BOUND)
			Exists("+"),    // "+"		// bound (this one doesn't exist in springsalad)
			Possible("?"),  // "?"		// any (in springsalad this is TransitionCondition.NONE)
			None("-");  	   			// unbound (in springsalad this is TransitionCondition.FREE)
			As you see, terminology is very confusing:
			BondType.None means TransitionCondition.Free (no bond, in other words condition must be no bond),  while
			BondType.Possible means TransitionCondition.None (bond can be anything, in other words no condition)
		 */
		NONE("Any", "None"),		// BondType.Possible	(?)	transitioning site bond can be anything (springsalad condition is NONE (no condition))
		FREE("Unbound", "Free"),	// BondType.None		(-) transitioning site must be unbound, no bond (springsalad condition is FREE(condition is that there is no bond)
		BOUND("Bound", "Bound");	// BondType.Exists		(+) transitioning site has explicit bond to site of another molecule
		
		final public String vcellName;
		final public String lngvName;
		private TransitionCondition(String vcellName, String lngvName) {
			this.vcellName = vcellName;
			this.lngvName = lngvName;
		}
		public static TransitionCondition fromVcellName(String nameCandidate) {
			for(TransitionCondition tc : TransitionCondition.values()) {
				if(tc.vcellName.equals(nameCandidate)) {
					return tc;
				}
			}
			return null;
		}
		public static TransitionCondition fromLngvName(String nameCandidate) {
			for(TransitionCondition tc : TransitionCondition.values()) {
				if(tc.lngvName.equals(nameCandidate)) {
					return tc;
				}
			}
			return null;
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
				stateReactant = ANY_STATE_STRING;
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
	Object ret = analysisResults.get(WrongNumberOfMolecules);
	if(ret == null || (boolean)ret == true) {
		return null;
	}
	ret = analysisResults.get(StateTransitionCounter);
	if(ret == null || (int)ret != 1) {
		return null;		// we need exactly 1 state transition
	}
	ret = analysisResults.get(BondTransitionCounter);
	if(ret == null || (int)ret != 0) {
		return null;		// no binding allowed
	}
	if(analysisResults.get(NumReactants) == null || analysisResults.get(NumProducts) == null)  {
		return null;
	}
	int numReactants = (int)analysisResults.get(NumReactants);
	int numProducts = (int)analysisResults.get(NumProducts);
	if(numReactants != 1 || numProducts != 1) {
		return null;		// we may only have 1 reactant and 1 product
	}
	if((numReactants == 1 && numProducts != 1) || (numReactants == 2 && numProducts != 2)) {
		return null;		// equal number of reactant and products, either 1 of each or 2 of each
	}
	List<ReactantPattern> rpList = reactionRule.getReactantPatterns();		// we already know that this list is not empty
	List<ProductPattern> ppList = reactionRule.getProductPatterns();
	List<MolecularTypePattern> mtpReactantList = rpList.get(0).getSpeciesPattern().getMolecularTypePatterns();
	List<MolecularTypePattern> mtpProductList = ppList.get(0).getSpeciesPattern().getMolecularTypePatterns();
	if((mtpReactantList.size() == 1 && mtpProductList.size() != 1) || (mtpReactantList.size() == 2 && mtpProductList.size() != 2)) {
		return null;		// equal number of molecules in the reactant and product, either 1 of each or 2 of each
	}

	if(mtpReactantList.size() == 1 && mtpProductList.size() == 1) {		// check if it's a simple transition (Condition "None" or "Free")
		int numAnyStates = 0;
		int numBondsPossible = 0;
		int numBondsUnbound = 0;
		MolecularTypePattern mtpReactant = mtpReactantList.get(0);
		MolecularComponentPattern mcpTransitionReactant = (MolecularComponentPattern)analysisResults.get(McpReactantState + "1");
		if(mcpTransitionReactant == null) {
			return null;	// we must have a reactant that's transitioning
		}
		for(MolecularComponentPattern mcpReactant : mtpReactant.getComponentPatternList()) {
			ComponentStatePattern cspReactant = mcpReactant.getComponentStatePattern();
			BondType bondTypeReactant = mcpReactant.getBondType();
			if(cspReactant == null) {
				return null;	// all sites must have at least one state
			}
//			if(!cspReactant.isAny() && BondType.Possible != bondTypeReactant) {
//				// this is the transition site, bond must be "Possible"
//				return null;
//			}
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
		/*
		public enum MolecularComponentPattern.BondType {
		Specified(""),	// numbers	// Explicit (in springsalad this is TransitionCondition.BOUND)
		Exists("+"),	// "+"		// Bound to some other (unspecified) site (this one doesn't exist in springsalad)
		Possible("?"),	// "?"		// Any bonding situation (in springsalad this is TransitionCondition.NONE)
		None("-");		// "-"		// Unbound (in springsalad this is TransitionCondition.FREE)

			As you see, terminology is very confusing:
			BondType.None means TransitionCondition.FREE (no bond, in other words condition must be no bond),  while
			BondType.Possible means TransitionCondition.NONE (bond can be anything, in other words no condition)

		public enum TransitionCondition {	// everywhere internally in vcell we use RBM bond type naming conventions
			NONE("Any", "None"),		// BondType.Possible	(?)	transitioning site bond can be anything (springsalad condition is NONE (no condition))
			FREE("Unbound", "Free"),	// BondType.None		(-) transitioning site must be unbound, no bond (springsalad condition is FREE(condition is that there is no bond)
			BOUND("Bound", "Bound");	// BondType.Exists		(+) transitioning site has explicit bond to site of another molecule
		 */
		int numSites = mtpReactant.getComponentPatternList().size();
		if(numBondsUnbound == 1 && numBondsPossible == numSites-1 ) {
			// transition site is bond Possible and in explicit state
			// all the other sites are unbound AND in "Any" state
			if(mcpTransitionReactant.getBondType() == BondType.None) {
				return TransitionCondition.FREE;    // condition is "Free" (Unbound)
			}
		} else if(numBondsUnbound == 0 && numBondsPossible == numSites && numAnyStates == numSites-1) {
			// transition site is bond Possible and in explicit state
			// all the other sites are bond Possible AND in "Any" state
			return TransitionCondition.NONE;	// transition reaction condition is "None" (bond can be anything, BondType is possible)
		}
		return null;
	} else if(mtpReactantList.size() == 2 && mtpProductList.size() == 2) {		// we look for transition with "Bound" condition, means 2 molecules
		MolecularTypePattern mtpTransitionReactant = (MolecularTypePattern)analysisResults.get(MtpReactantState + "1");	// the transitioning molecule
		int totalExplicitStates = 0;
		int numExplicitStates[] = { 0, 0};
		int numSpecifiedBonds[] = { 0, 0 };
		MolecularComponentPattern[] mcpExplicitStates = new MolecularComponentPattern[2];	// the site with the explicit state
		MolecularComponentPattern[] mcpSpecifiedBonds = new MolecularComponentPattern[2];	// the site with the specified bond

		for(int i=0; i < mtpReactantList.size(); i++) {
			// the transitioning site must have explicit bond in both reactant and product (which obviously must point to the condition molecule)
			// there must be only one transitioning state, the one in the transitioning molecule
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
				if(BondType.Specified == bondTypeReactant) {	// we must have exactly one Specified bond, all the others must be Possible
					numSpecifiedBonds[i]++;	// the condition binding site has bond type "Exists"
					mcpSpecifiedBonds[i] = mcpReactant;
				} else if(BondType.Possible != bondTypeReactant) {
					return null;		// all other bonds in the reactant must be of type "Possible"
				}

				if(!cspReactant.isAny()) {	// we also need to reack explicit states
					numExplicitStates[i]++;
					totalExplicitStates++;
					mcpExplicitStates[i] = mcpReactant;
				}
			}
		}

		// the explicit state if exists, must only be on the bound site
		if(mcpExplicitStates[0] != null && mcpExplicitStates[0] != mcpSpecifiedBonds[0]) {
			return null;
		}
		if(mcpExplicitStates[1] != null && mcpExplicitStates[1] != mcpSpecifiedBonds[1]) {
			return null;
		}

		// the only situations that correctly describe condition bound transition reaction
		if(numSpecifiedBonds[0] == 1 && numSpecifiedBonds[1] == 1 && numExplicitStates[0] == 1 && numExplicitStates[1] == 1) {
			return TransitionCondition.BOUND;
		} else if(numSpecifiedBonds[0] == 1 && numSpecifiedBonds[1] == 1 && totalExplicitStates == 1) {
			return TransitionCondition.BOUND;
		}
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
	TransitionCondition tc = getTransitionCondition(analysisResults);
	if(tc instanceof TransitionCondition) {
		return true;
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
	
	Set<ComponentStateDefinition> explicitReactantStatesSet = new LinkedHashSet<> ();
	List<MolecularComponentPattern> mcpReactantList = mtpReactant.getComponentPatternList();
	List<MolecularComponentPattern> mcpProductList = mtpProduct.getComponentPatternList();
	for(MolecularComponentPattern mcp : mcpReactantList) {
		if(mcp.getBondType() != BondType.Possible) {
			return false;	// by convention, all bonds must be set to "Possible"
		}
	}
	for(MolecularComponentPattern mcp : mcpReactantList) {
		if(mcp.getComponentStatePattern() == null) {
			return false;		// all sites must have at least one state
		}
		if(!mcp.getComponentStatePattern().isAny()) {
			explicitReactantStatesSet.add(mcp.getComponentStatePattern().getComponentStateDefinition());
			reactantExplicitStates++;
		}
	}
	int matchedStates = 0;
	int unmatchedStates = 0;
	for(MolecularComponentPattern mcp : mcpProductList) {
		if(!mcp.getComponentStatePattern().isAny()) {
			ComponentStateDefinition csdProductExplicit = mcp.getComponentStatePattern().getComponentStateDefinition();
			if(explicitReactantStatesSet.contains(csdProductExplicit)) {
				matchedStates++;
			} else {
				unmatchedStates++;
			}
			productExplicitStates++;
		}
	}
	if(matchedStates != 1 || unmatchedStates != 1) {
		// the reactant condition state must match the product, the transition state must not match, so one of each
		return false;
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
	for(ReactionRuleParticipant rrp : reactionRule.getReactionRuleParticipants()) {
		SpeciesPattern sp = rrp.getSpeciesPattern();
		for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
			for(MolecularComponentPattern mcp : mtp.getComponentPatternList()) {
				BondType bt = mcp.getBondType();
				ComponentStatePattern csp = mcp.getComponentStatePattern();
				if(csp == null || (!csp.isAny() && BondType.Possible == bt)) {
					// all the sites not binding must be in Any state
					return false;
				}
			}
		}
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
	String stateConditionReactant = ANY_STATE_STRING;
	
	// we may have 2 mtp if the reaction condition is "Bound"!
	List<ReactantPattern> rpList = reactionRule.getReactantPatterns();		// we already know that this list is not empty
	List<MolecularTypePattern> mtpReactantList = rpList.get(0).getSpeciesPattern().getMolecularTypePatterns();

	TransitionCondition transitionCondition = TransitionCondition.NONE;		// no restriction
	if(mtpReactantList.size() == 1) {	// transition reaction condition "None" or "Free"
		if(BondType.None == mcpTransitionReactant.getBondType()) {
			// transition "FREE" has the bond types of all sites set to "Possible" (except the transitioning site which is Unbound)
			transitionCondition = TransitionCondition.FREE;
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
			if(BondType.Specified == mcpCandidate.getBondType()) {
				mcpConditionReactant = mcpCandidate;	// found the bond condition site, it's the one with bond type "Specified"
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
	sb.append("  Condition ").append(transitionCondition.lngvName);
	if(TransitionCondition.BOUND == transitionCondition) {
		sb.append(" '").append(mtpConditionReactant.getMolecularType().getName()).append("' : '")
		.append(mcpConditionReactant.getMolecularComponent().getName()).append("' : '")
		.append(stateConditionReactant).append("'");
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
	int conditionIndex = -1;		// allosteric site index
	MolecularComponentPattern mcpAllostericReactant = null;
	for(MolecularComponentPattern mcp : mtpTransitionReactant.getComponentPatternList()) {
		conditionIndex++;
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
	int transitionIndex = mtpTransitionReactant.getMolecularType().getComponentList().indexOf(mcpTransitionReactant.getMolecularComponent());

	sb.append("'").append(reactionRule.getName()).append("' ::     ");
	sb.append("'").append(mtpTransitionReactant.getMolecularType().getName()).append("' : ")
		.append("Site ").append(transitionIndex).append(" : '")
		.append(cspTransitionReactant.getComponentStateDefinition().getName());
	sb.append("' --> '");
	sb.append(cspTransitionProduct.getComponentStateDefinition().getName());
	sb.append("'  Rate ").append(kon.infix());
	sb.append(" Allosteric_Site ").append(conditionIndex);
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
private final static String SpringSaLaDMsgAtLeastOne = "At least one reactant and one product are required.";
private final static String SpringSaLaDMsgAnchorReactionMustMembrane = "SpringSaLaD reactions must be located on the Membrane if one reactant is on Membrane.";
private final static String SpringSaLaDMsgEachReactionMust = "SpringSaLaD requires that each compartmental reaction and all its participants must be in the same compartment";
private final static String SpringSaLaDMsgAnchorCannotBePart = "The reserved site 'Anchor' can only be part of a membrane reactant pattern.";
private final static String SpringSaLaDMsgAnchorCannotBeTarget = "The reserved site 'Anchor' cannot be the target of any SpringSaLaD reaction.";
private final static String SpringSaLaDMsgOnlyAcceptsOneProduct = "SpringSaLaD only accepts one Product.";
private final static String SpringSaLaDMsgOnlyAcceptsTwo = "SpringSaLaD only accepts 2 Reactants for the binding reactions and 1 Reactant for the other subtypes.";
private final static String SpringSaLaDMsgOnlyAcceptsOneBinding = "SpringSaLaD only accepts one binding transition in any reaction.";
private final static String SpringSaLaDMsgOnlyAcceptsOneTransition = "SpringSaLaD only accepts one state transition in any reaction.";
private final static String SpringSaLaDMsgDoesNotAcceptCombination = "SpringSaLaD does not accept a combination of state and binding transitions in any reaction.";
private final static String SpringSaLaDMsgIncompatibleWithAny = "Reaction incompatible with any SpringSaLaD reaction subtype.";
private final static String SpringSaLaDMsgSubtypeMustBeIrreversible = "This reaction subtype must be irreversible. Make the reaction ireversible.";
private final static String SpringSaLaDMsgTransmembraneBinding = "Transmembrane binding not supported";
private final static String SpringSaLaDMsgSubtypeMustBeReversible = "This reaction subtype must be reversible. Make the reaction reversible and keep Kr at 0.";
private final static String SpringSaLaDMsgBoundTransitionNeedsBinding = "No Binding reaction found that would make the binding condition of this Transition reaction possible";
private static final String SpringSaLaDMsgNoComplexes = "SpringSaLaD does not accept explicit complexes in the list of reactants";

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

		if(isExcluded()) {
			return;		// we don't care as long as it's excluded
		}
		List<ReactantPattern> rpList = reactionRule.getReactantPatterns();
		List<ProductPattern> ppList = reactionRule.getProductPatterns();
		if(rpList == null || ppList == null) {
			String msg = SpringSaLaDMsgAtLeastOne;
			String tip = GenericTip;
			issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.ERROR));
			return;
		}
		// dan 07/31/24 correction: transition bound reactions accept complexes in the reactant pattern
		// since one molecule may represent the transition while another has the condition
		// interestingly, for allosteric reactions the conditional state must be in the same molecule
		// which undergoes transition
//		for(ReactantPattern rp : rpList) {
//			List<MolecularTypePattern> mtpList = rp.getSpeciesPattern().getMolecularTypePatterns();
//			if(mtpList.size() > 1) {
//				String msg = SpringSaLaDMsgNoComplexes;
//				String tip = GenericTip;
//				issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
//				return;
//			}
//		}
		int numReactants = rpList.size();
		int numProducts = ppList.size();
		if(numReactants == 0 || numProducts == 0) {
			String msg = SpringSaLaDMsgAtLeastOne;
			String tip = GenericTip;
			issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
			return;
		}

		// if at least one reactant is on a membrane, the reaction must be on a membrane
		for(ReactantPattern rp : rpList) {
			if(SpringStructureEnum.Membrane.columnName.equals(rp.getStructure().getName())) {
				if(!(SpringStructureEnum.Membrane.columnName.equals(reactionRule.getStructure().getName()))) {
					String msg = SpringSaLaDMsgAnchorReactionMustMembrane;
					String tip = msg;
					issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.ERROR));
					return;
				}
			}
		}

		// all compartmental reactions must have all reactants, all products and the reaction located in the same compartment
		Set<Structure> participantStructureSet = new LinkedHashSet<>();
		for(ReactionRuleParticipant rrp : reactionRule.getReactionRuleParticipants()) {
			if(SpringStructureEnum.Membrane.columnName.equals(rrp.getStructure().getName())) {
				break;	// we deal with membrane participants reactions separately
			}
			participantStructureSet.add(rrp.getStructure());
		}
		if(participantStructureSet.size() > 1) {
			String msg = SpringSaLaDMsgEachReactionMust;
			String tip = msg;
			issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.ERROR));
			return;
		} else if(participantStructureSet.size() == 1) {	// reaction must also be here
			if(!participantStructureSet.contains(reactionRule.getStructure())) {
				String msg = SpringSaLaDMsgEachReactionMust;
				String tip = msg;
				issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.ERROR));
				return;
			}
		}

		// the reserved site 'Anchor' must be not present in any reactant pattern that is not situated on a membrane
		// note that we do a similar check for seed species, but this is also needed 
		// (we can have a bad reactant pattern even though the seed species may be missing)
		for(ReactantPattern rp : rpList) {
			if(!SpringStructureEnum.Membrane.columnName.equals(rp.getStructure().getName())) {
				SpeciesPattern spReactant = rp.getSpeciesPattern();
				List<MolecularTypePattern> mtpReactantList = spReactant.getMolecularTypePatterns();
				for(MolecularTypePattern mtpReactant : mtpReactantList) {
					List<MolecularComponentPattern> mcpReactantList = mtpReactant.getComponentPatternList();
					for(MolecularComponentPattern mcpReactant : mcpReactantList) {
						MolecularComponent mcReactant = mcpReactant.getMolecularComponent();
						if(SpeciesContextSpec.AnchorSiteString.equals(mcReactant.getName())) {
							String msg = SpringSaLaDMsgAnchorCannotBePart;
							String tip = msg;
							issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.ERROR));
							return;
						}
					}
				}
			}
		}

		// the reserved site 'Anchor' must not be part of any reaction
		for(ReactantPattern rp : rpList) {
			if(!SpringStructureEnum.Membrane.columnName.equals(rp.getStructure().getName())) {
				continue;	// we don't have anchors if it's not a membrane molecule
			}
			SpeciesPattern spReactant = rp.getSpeciesPattern();
			List<MolecularTypePattern> mtpReactantList = spReactant.getMolecularTypePatterns();
			for(MolecularTypePattern mtpReactant : mtpReactantList) {
				List<MolecularComponentPattern> mcpReactantList = mtpReactant.getComponentPatternList();
				for(MolecularComponentPattern mcpReactant : mcpReactantList) {
					MolecularComponent mcReactant = mcpReactant.getMolecularComponent();
					if(!SpeciesContextSpec.AnchorSiteString.equals(mcReactant.getName())) {
						continue;
					}
					if(BondType.Possible != mcpReactant.getBondType() || !mcpReactant.getComponentStatePattern().isAny()) {
						String msg = SpringSaLaDMsgAnchorCannotBeTarget;
						String tip = msg;
						issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.ERROR));
						return;
					}
				}
			}
		}


		// ------------------------------------------------------------------------------------------------------------------------
		Map<String, Object> analysisResults = new LinkedHashMap<> ();
		analizeReaction(analysisResults);
		Subtype subtype = getSubtype(analysisResults);
		if(Subtype.INCOMPATIBLE == subtype) {
			if(numProducts > 1) {
				String msg = SpringSaLaDMsgOnlyAcceptsOneProduct;
				String tip = GenericTip;
				issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
				return;
			}
			if(numReactants > 2) {
				String msg = SpringSaLaDMsgOnlyAcceptsTwo;
				String tip = GenericTip;
				issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
				return;
			}
		}
		if(Subtype.INCOMPATIBLE == subtype) {
			Object bondObject = analysisResults.get(BondTransitionCounter);
			Object stateObject = analysisResults.get(StateTransitionCounter);
			if(bondObject != null && stateObject != null) {
				int bondTransitionCounter = (int)bondObject;
				int stateTransitionCounter = (int)stateObject;
				if(bondTransitionCounter > 3) {		// a binding reaction produces 2 transitions
					String msg = SpringSaLaDMsgOnlyAcceptsOneBinding;
					String tip = GenericTip;
					issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
					return;
				}
				if(stateTransitionCounter > 1) {
					String msg = SpringSaLaDMsgOnlyAcceptsOneTransition;
					String tip = GenericTip;
					issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
					return;
				}
				if(stateTransitionCounter > 0 && bondTransitionCounter > 0) {
					String msg = SpringSaLaDMsgDoesNotAcceptCombination;
					String tip = GenericTip;
					issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
					return;
				}
			}
		}
		if(Subtype.INCOMPATIBLE == subtype) {
			String msg = SpringSaLaDMsgIncompatibleWithAny;
			String tip = GenericTip;
			issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
		}

		// only for conditional bound transition reactions: we need a binding reaction to create the conditional bond
		switch(subtype) {
		case TRANSITION:
			TransitionCondition tc = getTransitionCondition(analysisResults);
			if(tc != TransitionCondition.BOUND) {
				break;	// we do this only for conditional bound transition reactions
			}
			// we need to find the molecule / site combination that needs to be bound
			MolecularType mtTransition = ((MolecularTypePattern)analysisResults.get(MtpReactantState + "1")).getMolecularType();	// the transitioning molecule
			MolecularComponent mcTransition = ((MolecularComponentPattern)analysisResults.get(McpReactantState + "1")).getMolecularComponent();
			Bond bond = ((MolecularComponentPattern)analysisResults.get(McpReactantState + "1")).getBond();	// we know it's a bound transition, so it must have a bond for sure
			MolecularType mtCondition = bond.molecularTypePattern.getMolecularType();
			MolecularComponent mcCondition = bond.molecularComponentPattern.getMolecularComponent();

			List <ReactionRule> rrList = rc.getModel().getRbmModelContainer().getReactionRuleList();
			reactionRule.getReactantPattern(0).getSpeciesPattern();
			// we go through all reaction rules, trying to find a binding reaction that would make this
			// conditional bound transition reaction possible
			boolean found = false;
			for(ReactionRule rr : rrList) {
				ReactionRuleSpec rrs = rc.getReactionRuleSpec(rr);
				if(rrs.isExcluded()) {
					continue;	// even if it's the right reaction, if it's excluded it won't do the job
				}
				Map<String, Object> ar = new LinkedHashMap<>();
				rrs.analizeReaction(ar);
				Subtype st = rrs.getSubtype(ar);
				if (st != Subtype.BINDING) {
					continue;
				}
				// the 2 reactants should match the pairs mtTransition / mcTransition and respectively mtCondition / mcCondition
				MolecularType mt1 = ((MolecularTypePattern) ar.get(MtpReactantBond + "1")).getMolecularType();    // '1' - reactant. left side of the bond
				MolecularType mt2 = ((MolecularTypePattern) ar.get(MtpReactantBond + "2")).getMolecularType();    // '2' - reactant, right side of the bond
				MolecularComponent mc1 = ((MolecularComponentPattern) ar.get(McpReactantBond + "1")).getMolecularComponent();
				MolecularComponent mc2 = ((MolecularComponentPattern) ar.get(McpReactantBond + "2")).getMolecularComponent();
				if (mtTransition == mt1 && mcTransition == mc1 && mtCondition == mt2 && mcCondition == mc2) {
					found = true;    // found the binding reaction we needed
					break;            // exit the for loop
				} else if (mtTransition == mt2 && mcTransition == mc2 && mtCondition == mt1 && mcCondition == mc1) {
					found = true;
					break;
				}
			}
			if (found == false) {
				// didn't find any binding reaction that would allow our conditional bound transition reaction to happen
				String msg = SpringSaLaDMsgBoundTransitionNeedsBinding;
				String tip = "Create a Binding reaction to make the binding condition of this Transition reaction possible.";
				issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
				return;
			}
			// TODO: to be very strict, we should also check if we have a seed species with the right site / state combination
			//  needed for the transition reactant, or another transition (or creation?) reaction that would produce it
			break;
		case ALLOSTERIC:
			// We check elsewhere that Allosteric cannot have the Anchor as transitioning or allosteric site
			// Nothing to do here
			break;
		default:
			break;
		}

		switch(subtype) {	// these reactions cannot be reversible
		case CREATION:
		case DECAY:
		case TRANSITION:
		case ALLOSTERIC:
			if(reactionRule.isReversible()) {
				String msg = SpringSaLaDMsgSubtypeMustBeIrreversible;
				String tip = "Make this reaction irreversible or disable it in this table.";
				issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
				return;
			}
			break;
		case BINDING:
			// we cannot have trans-membrane bonds - and there is no cross-membrane transport support in springsalad
			// we make sure the 2 sites bonding are in the same structure, we raise issue if not
			MolecularTypePattern mtpOursOne = (MolecularTypePattern)analysisResults.get(MtpReactantBond + "1");
			MolecularComponentPattern mcpOursOne = (MolecularComponentPattern)analysisResults.get(McpReactantBond + "1");
			MolecularTypePattern mtpOursTwo = (MolecularTypePattern)analysisResults.get(MtpReactantBond + "2");
			MolecularComponentPattern mcpOursTwo = (MolecularComponentPattern)analysisResults.get(McpReactantBond + "2");
			MolecularType mtOursOne = mtpOursOne.getMolecularType();
			MolecularType mtOursTwo = mtpOursTwo.getMolecularType();
			MolecularComponent mcOursOne = mcpOursOne.getMolecularComponent();
			MolecularComponent mcOursTwo = mcpOursTwo.getMolecularComponent();
			
			Map<MolecularComponentPattern, SiteAttributesSpec> siteAttributesMapOne = null;
			Map<MolecularComponentPattern, SiteAttributesSpec> siteAttributesMapTwo = null;
			SpeciesContextSpec[] speciesContextSpecs = rc.getSpeciesContextSpecs();
			for(SpeciesContextSpec scs : speciesContextSpecs) {
				SpeciesContext scCandidate = scs.getSpeciesContext();
				if(scCandidate == null || scCandidate.getSpeciesPattern() == null) {
					continue;
				}
				// we may have just one spCandidate wif the reaction is A + A -> A.A
				SpeciesPattern spCandidate = scCandidate.getSpeciesPattern();
				MolecularTypePattern mtpCandidate = spCandidate.getMolecularTypePatterns().get(0);
				MolecularType mtCandidate = mtpCandidate.getMolecularType();
				if(mtOursOne == mtCandidate) {
					siteAttributesMapOne = scs.getSiteAttributesMap();
				}
				if(mtOursTwo == mtCandidate) {
					siteAttributesMapTwo = scs.getSiteAttributesMap();
				}
			}
			SiteAttributesSpec sasOne = null;
			SiteAttributesSpec sasTwo = null;
			for(Map.Entry<MolecularComponentPattern, SiteAttributesSpec> entry : siteAttributesMapOne.entrySet()) {
				MolecularComponentPattern mcpCandidate = entry.getKey();
				if(MolecularComponentPattern.BondType.None != mcpCandidate.getBondType()) {
					continue;
				}
				MolecularComponent mcCandidate = mcpCandidate.getMolecularComponent();
				if(mcOursOne == mcCandidate) {
					sasOne = entry.getValue();
				} else if(mcOursTwo == mcCandidate) {
					sasTwo = entry.getValue();
				}
			}
			//
			// with previous logic, siteAttributesMapTwo could have been null (issue #977)
			// happened when the reaction was between 2 molecules of the same type  A + A -> A.A
			// the temp fixwas:  we check for non-null siteAttributesMapTwo
			// as from feb 24, 2025 this was fixed and siteAttributesMapTwo should never be null
			if(siteAttributesMapTwo == null) {
				throw new RuntimeException("Unexpected null value for siteAttributesMapTwo");
			}
			if(sasOne != null && sasTwo != null) {
				for(Map.Entry<MolecularComponentPattern, SiteAttributesSpec> entry : siteAttributesMapTwo.entrySet()) {
					MolecularComponentPattern mcpCandidate = entry.getKey();
					if(MolecularComponentPattern.BondType.None != mcpCandidate.getBondType()) {
						continue;
					}
					MolecularComponent mcCandidate = mcpCandidate.getMolecularComponent();
					if(mcOursOne == mcCandidate) {
						sasOne = entry.getValue();
					} else if(mcOursTwo == mcCandidate) {
						sasTwo = entry.getValue();
					}
				}
				if(sasOne.getLocation() != sasTwo.getLocation()) {
					String msg = SpringSaLaDMsgTransmembraneBinding;
					String tip = "Both binding reactant Sites need to be in the same compartment.";
					issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.ERROR));
					return;
				}
				if(checkOnRate(sasOne, sasTwo) == false) {	// rate doesn't check as acceptable
					String msg = "The forward rate Kf is too large (i.e. exceeds the diffusion limited rate) for this reaction rule.";
					String tip = "Please consider reducing Kon or increasing the Radius or D of the participating Site Types.";
					issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
					return;
				}
			}
			// binding reactions must be reversible
			if(!reactionRule.isReversible()) {
				String msg = SpringSaLaDMsgSubtypeMustBeReversible;
				String tip = "Make this reaction reversible or disable it in this table.";
				issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
				return;
			}
			break;
		case INCOMPATIBLE:
		default:
			break;
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
	/*
	 * The maximum possible on-rate is given by kon_max = 4*pi*R*D, so we'd
	 * better have kon < 4*pi*R*D . If that isn't satisfied then nothing else will work.
	 */
	public boolean checkOnRate(SiteAttributesSpec sasOne, SiteAttributesSpec sasTwo) {

		// set of acceptable numbers (marginally) for A + A -> A.A are:
		// Kon = 40 s-1uM-1
		// site radius 1nm
		// site diffusion rate 1 um2s-1

		double R = sasOne.computeReactionRadius() + sasTwo.computeReactionRadius();	// nm
		double D = sasOne.getDiffusionRate() + sasTwo.getDiffusionRate();

		LocalParameter lp = getModelProcess().getKineticLaw().getLocalParameter(RbmKineticLaw.RbmKineticLawParameterType.MassActionForwardRate);
		String doubleRegex = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
		if(!lp.getExpression().infix().matches(doubleRegex)) {
			return false;
		}
		double kon = Double.parseDouble(lp.getExpression().infix());
// TODO: check all scaling
		double kon_scale = 1660000.0 * kon;			// Kon also needs some conversion (TODO: why?)
		double D_scale = D * 1000000.0;				// D is in um^2/s, convert to nm^2/s

		double rhs1 = 4.0*Math.PI*R*D_scale;

		boolean check = (kon_scale < rhs1);
		return check;
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
 * @exception java.beans.PropertyVetoException The exception description.
 */
public void setReactionRuleMapping(ReactionRuleMappingType reactionRuleMapping) {
	ReactionRuleMappingType oldValue = fieldReactionRuleMapping;
	fieldReactionRuleMapping = reactionRuleMapping;
	firePropertyChange("reactionRuleMapping", oldValue, reactionRuleMapping);
}

/**
 * Insert the method's description here.
 * Creation date: (2/14/2002 5:07:05 PM)
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
