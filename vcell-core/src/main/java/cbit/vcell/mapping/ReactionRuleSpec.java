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
	
	public static final String ANY_STATE = "Any_State";
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
	if (!(object instanceof ReactionRuleSpec)){
		return false;
	}
	reactionSpec = (ReactionRuleSpec)object;

	if (!reactionRule.compareEqual(reactionSpec.reactionRule)){
		return false;
	}
	
	if (fieldReactionRuleMapping != reactionSpec.fieldReactionRuleMapping){
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

// --------------------------------------------------------------------------------------------------------
public void analizeReaction(Map<String, Object> analysisResults) {
	List<ReactantPattern> rpList = reactionRule.getReactantPatterns();
	List<ProductPattern> ppList = reactionRule.getProductPatterns();
	// sanity check; get out of here fast if basic conditions are not satisfied
	// TODO: move some tests in issues code maybe
	if(rpList == null || rpList.isEmpty() | rpList.size() > 2) {
		return;					// no matter the reaction, we can't have have more than 2 reactants
	}
	if(ppList == null || ppList.size() != 1) {
		return;					// no matter the reaction, we have exactly 1 product
	}
	for(ReactantPattern rp : rpList) {
		SpeciesPattern sp = rp.getSpeciesPattern();
		if(sp == null) {
			return;
		}
		List<MolecularTypePattern> mtpList = sp.getMolecularTypePatterns();
		if(mtpList == null || mtpList.size() != 1) {	// each reactant can have exactly one molecule 
			return;
		}
	}
	ProductPattern pp = ppList.get(0);
	SpeciesPattern spProduct = pp.getSpeciesPattern();
	if(spProduct == null) {
		return;
	}
	List<MolecularTypePattern> mtpProductList = spProduct.getMolecularTypePatterns();
	if(mtpProductList == null || mtpProductList.size() > 2) {	// no more than 2 molecules in the product
		return;
	}
	// looks expensive but a springsalad binding reaction has exactly 2 reactants one molecule each
	// and exactly one product made of 2 molecules
	// since instances of mtp / mcp are different between reactants and products, we work with instances of mc / mp
	Map<MolecularComponentPattern, MolecularComponentPattern> reactantToProductSitesMap = new LinkedHashMap<> ();
	Map<MolecularComponentPattern, MolecularComponentPattern> productToReactantSitesMap = new LinkedHashMap<> ();
	Map<MolecularComponentPattern, MolecularTypePattern> siteToMoleculeMap = new LinkedHashMap<> ();
	
	// map each reactant site to corresponding product site and vice-versa, map each site to its molecule
	for(ReactantPattern rp : rpList) {
		SpeciesPattern spReactant = rp.getSpeciesPattern();
		List<MolecularTypePattern> mtpReactantList = spReactant.getMolecularTypePatterns();
		MolecularTypePattern mtpReactant = mtpReactantList.get(0);
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

	boolean bondAndStateChange = false;
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
				String mtpReactantKey = "mtpReactantState" + stateTransitionCounter;
				String mcpReactantKey = "mcpReactantState" + stateTransitionCounter;
				String mtpProductKey = "mtpProductState" + stateTransitionCounter;
				String mcpProductKey = "mcpProductState" + stateTransitionCounter;
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
			String mtpKey = "mtpReactantBond" + bondTransitionCounter;
			String mcpKey = "mcpReactantBond" + bondTransitionCounter;
			analysisResults.put(mtpKey, mtpReactant);
			analysisResults.put(mcpKey, mcpReactant);
			
			if(cspReactant != null) {
				if(!cspReactant.compareEqual(cspProduct)) {
					bondAndStateChange = true;		// state changed on a bonding site, not allowed
				}
			}

			String stateReactant;
			if(cspReactant == null) {
				stateReactant = "ERROR";
			} else if(cspReactant.isAny()) {
				stateReactant = ANY_STATE;
			} else {
				ComponentStateDefinition csdReactant = cspReactant.getComponentStateDefinition();
				stateReactant = csdReactant.getName();
			}
			String cspKey = "cspReactantBond" + bondTransitionCounter;
			analysisResults.put(cspKey, stateReactant);
		}
		mcpCount++;
	}
	analysisResults.put("bondAndStateChange", bondAndStateChange);
	analysisResults.put("stateTransitionCounter", stateTransitionCounter);
	analysisResults.put("bondTransitionCounter", bondTransitionCounter);
}
public Subtype getSubtype(Map<String, Object> analysisResults) {
	if(isCreationReaction() == true) {
		return Subtype.CREATION;
	}
	if(isDecayReaction() == true) {
		return Subtype.DECAY;
	}

	if(isBindingReaction(analysisResults) == true) {
		return Subtype.BINDING;
	}
	return Subtype.INCOMPATIBLE;
}
private boolean isCreationReaction() {
	List<ReactantPattern> rpList = reactionRule.getReactantPatterns();
	if(rpList.size() == 1) {
		SpeciesPattern sp = rpList.get(0).getSpeciesPattern();
		if(sp.getMolecularTypePatterns().size() == 1) {
			MolecularTypePattern mtp = sp.getMolecularTypePatterns().get(0);
			if(mtp.getComponentPatternList().size() == 0) {
				return true;
			}
		}
	}
	return false;
}
private boolean isDecayReaction() {
	List<ProductPattern> ppList = reactionRule.getProductPatterns();
	if(ppList.size() == 1) {
		SpeciesPattern sp = ppList.get(0).getSpeciesPattern();
		if(sp.getMolecularTypePatterns().size() == 1) {
			MolecularTypePattern mtp = sp.getMolecularTypePatterns().get(0);
			if(mtp.getComponentPatternList().size() == 0) {
				return true;
			}
		}
	}
	return false;
}
private boolean isBindingReaction(Map<String, Object> analysisResults) {
	Object ret = analysisResults.get("bondAndStateChange");
	if(ret == null) {
		return false;
	}
	boolean bondAndStateChange = (boolean)ret;
	if(bondAndStateChange == true) {
		return false;
	}
	ret = analysisResults.get("bondTransitionCounter");
	if(ret == null) {
		return false;
	}
	int transitions = (int)ret;
	if(transitions == 2) {
		return true;
	}
	return false;
}
public void writeData(StringBuilder sb, Subtype subtype) {			// SpringSaLaD exporting the binding rule information
	Map<String, Object> analysisResults = new LinkedHashMap<> ();
	analizeReaction(analysisResults);
	
	switch(getSubtype(analysisResults)) {
	case CREATION:
	case DECAY:
		// we invoke the static method below (writeDecayData()) on all species at once
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
	// one reactant and one product
	MolecularTypePattern mtpReactant = (MolecularTypePattern)analysisResults.get("mtpReactantState");
	MolecularTypePattern mtpProduct = (MolecularTypePattern)analysisResults.get("mtpProductState");
	MolecularComponentPattern mcpReactant = (MolecularComponentPattern)analysisResults.get("mcpReactantState");
	MolecularComponentPattern mcpProduct = (MolecularComponentPattern)analysisResults.get("mcpProductState");

}
private void writeAllostericData(StringBuilder sb, Subtype subtype, Map<String, Object> analysisResults) {
	if(subtype != ReactionRuleSpec.Subtype.ALLOSTERIC) {
		return;
	}
	// one reactant and one product
	MolecularTypePattern mtpReactant = (MolecularTypePattern)analysisResults.get("mtpReactantState");
	MolecularTypePattern mtpProduct = (MolecularTypePattern)analysisResults.get("mtpProductState");
	MolecularComponentPattern mcpReactant = (MolecularComponentPattern)analysisResults.get("mcpReactantState");
	MolecularComponentPattern mcpProduct = (MolecularComponentPattern)analysisResults.get("mcpProductState");

}
private void writeBindingData(StringBuilder sb, Subtype subtype, Map<String, Object> analysisResults) {
	if(subtype != ReactionRuleSpec.Subtype.BINDING) {
		return;
	}
	// here we only deal with the 2 reactants
	MolecularTypePattern mtpReactantOne = (MolecularTypePattern)analysisResults.get("mtpReactantBond1");		// '1' - reactant. left side of the bond
	MolecularTypePattern mtpReactantTwo = (MolecularTypePattern)analysisResults.get("mtpReactantBond2");		// '2' - reactant, right side of the bond
	MolecularComponentPattern mcpReactantOne = (MolecularComponentPattern)analysisResults.get("mcpReactantBond1");
	MolecularComponentPattern mcpReactantTwo = (MolecularComponentPattern)analysisResults.get("mcpReactantBond2");
	String stateReactantOne = (String)analysisResults.get("cspReactantBond1");
	String stateReactantTwo =  (String)analysisResults.get("cspReactantBond2");
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

public SpeciesContext getCreatedSpecies(SpeciesContextSpec[] speciesContextSpecs) {
	if(!isCreationReaction()) {
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
public SpeciesContext getDestroyedSpecies(SpeciesContextSpec[] speciesContextSpecs) {
	if(!isDecayReaction()) {
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
public void gatherIssues(IssueContext issueContext, List<Issue> issueList, ReactionContext rc) {
//	ReactionRuleCombo r = new ReactionRuleCombo(this, rc);
//	ReactionRule reactionRule = getReactionRule();
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
		if(isDecayReaction()) {
			String msg = "Decay Reaction.";
			String tip = msg;
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
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
