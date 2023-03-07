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

import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.document.BioModelChildSummary.MathType;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;

import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;

public class ReactionRuleSpec implements ModelProcessSpec {
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
	private double fieldBondLength = 1;

	
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
	int transitions = (int)analysisResults.get("transitions");
	if(transitions == 2) {
		return true;
	}
	return false;
}

public void analizeReaction(Map<String, Object> analysisResults) {
	
	// looks expensive but a springsalad binding reaction has exactly 2 reactants one molecule each
	// and exactly one product made of 2 molecules
	Map<MolecularComponentPattern, MolecularTypePattern> productSpecifiedBondsMap = new LinkedHashMap<> ();
	List<ReactantPattern> rpList = reactionRule.getReactantPatterns();
	List<ProductPattern> ppList = reactionRule.getProductPatterns();
	int transitions = 0;
	for(ProductPattern pp : ppList) {
		SpeciesPattern sp = pp.getSpeciesPattern();
		List<MolecularTypePattern> mtpList = sp.getMolecularTypePatterns();
		for(MolecularTypePattern mtp : mtpList) {
			List<MolecularComponentPattern> mcpList = mtp.getComponentPatternList();
			for(MolecularComponentPattern mcp : mcpList) {
				if(mcp.getBondType() == BondType.Specified) {
					productSpecifiedBondsMap.put(mcp, mtp);	// all the components in the product with specified bonds
				}
			}
		}
	}
	for(ReactantPattern rp : rpList) {		// we look to find exactly 2 components that change BondType from None to Specified
		SpeciesPattern sp = rp.getSpeciesPattern();
		List<MolecularTypePattern> mtpList = sp.getMolecularTypePatterns();
		for(MolecularTypePattern mtp : mtpList) {
			List<MolecularComponentPattern> mcpList = mtp.getComponentPatternList();
			for(MolecularComponentPattern mcp : mcpList) {
				if(mcp.getBondType() == BondType.None) {
					MolecularTypePattern candidate = productSpecifiedBondsMap.get(mcp);
					if(candidate != null && mtp == candidate) {		// bingo, found a transition
						// TODO: all the instances are different, not working w. patterns. Will try with mt, mc instances, or even names
						String mtpKey = "mtp" + (transitions+1);
						String mcpKey = "mcp" + (transitions+1);
						analysisResults.put(mtpKey, mtp);
						analysisResults.put(mcpKey, mcp);
						
						transitions++;
					}
				}
			}
		}
	}
	analysisResults.put("transitions", transitions);
	// TODO: should also check that all states stay unchanged
	return;
}

public void writeData(StringBuilder sb) {			// SpringSaLaD exporting the binding rule information
	Map<String, Object> analysisResults = new LinkedHashMap<> ();
	analizeReaction(analysisResults);
	
	switch(getSubtype(analysisResults)) {
	case CREATION:
	case DECAY:
		writeDecayData(sb);
		break;
	case TRANSITION:
		writeTransitionData(sb);
		break;
	case ALLOSTERIC:
		writeAllostericData(sb);
		break;
	case BINDING:
		writeBindingData(sb, analysisResults);
		break;
	default:
		break;
	}
}

private void writeDecayData(StringBuilder sb) {

}
private void writeTransitionData(StringBuilder sb) {

}
private void writeAllostericData(StringBuilder sb) {

}
private void writeBindingData(StringBuilder sb, Map<String, Object> analysisResults) {
	MolecularTypePattern mtpOne = (MolecularTypePattern)analysisResults.get("mtp1");
	MolecularTypePattern mtpTwo = (MolecularTypePattern)analysisResults.get("mtp2");
	MolecularComponentPattern mcpOne = (MolecularComponentPattern)analysisResults.get("mcp1");
	MolecularComponentPattern mcpTwo = (MolecularComponentPattern)analysisResults.get("mcp2");
	String stateOne = "any";
	String stateTwo = "any";
	if(mtpOne == null || mtpTwo == null || mcpOne == null || mcpTwo == null) {
		throw new RuntimeException("writeBindingData() error: something is wrong");
	}
	
	sb.append("'").append(reactionRule.getName()).append("'       ");

	
		sb.append("'").append(mtpOne.getMolecularType().getName()).append("' : '")
		.append(mcpOne.getMolecularComponent().getName()).append("' : '")
		.append(stateOne);
		sb.append("'  -->  '");
		sb.append(mtpTwo.getMolecularType().getName()).append("' : '")
		.append(mcpTwo.getMolecularComponent().getName()).append("' : '")
		.append(stateTwo);
		// TODO: get the real values from kinetic law
		sb.append("'  kon  ").append(Double.toString(0.9));
		sb.append("  koff ").append(Double.toString(0.2));
		sb.append("  Bond_Length ").append(Double.toString(getFieldBondLength()));
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
