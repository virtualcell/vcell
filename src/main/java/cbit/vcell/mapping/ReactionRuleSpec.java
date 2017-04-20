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
import java.io.Serializable;
import java.util.List;

import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;

import cbit.vcell.model.ModelProcess;
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

}
