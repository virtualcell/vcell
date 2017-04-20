/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model;

import java.util.List;

import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.IssueContext;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (2/18/2002 5:07:08 PM)
 * @author: Anuradha Lakshminarayana
 */
public class GeneralPermeabilityKinetics extends DistributedKinetics {
/**
 * GeneralPermeabilityKinetics constructor comment.
 * @param reactionStep cbit.vcell.model.ReactionStep
 * @exception java.lang.Exception The exception description.
 */
public GeneralPermeabilityKinetics(FluxReaction fluxReaction) throws ExpressionException {
	super(KineticsDescription.GeneralPermeability.getName(),fluxReaction);
	try {
		KineticsParameter currentParm = new KineticsParameter(getDefaultParameterName(ROLE_CurrentDensity),new Expression(0.0),ROLE_CurrentDensity,null);
		KineticsParameter rateParm = new KineticsParameter(getDefaultParameterName(ROLE_ReactionRate),new Expression(0.0),ROLE_ReactionRate,null);
		KineticsParameter permeabilityParam = new KineticsParameter(getDefaultParameterName(ROLE_Permeability),new Expression(0.0),ROLE_Permeability,null);
		KineticsParameter carrierChargeValence = new KineticsParameter(getDefaultParameterName(ROLE_CarrierChargeValence),new Expression(1.0),ROLE_CarrierChargeValence,null);

		setKineticsParameters(new KineticsParameter[] { currentParm, rateParm, carrierChargeValence, permeabilityParam });
		updateGeneratedExpressions();
		refreshUnits();
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException("unexpected exception: "+e.getMessage());
	}
}
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj == this){
		return true;
	}
	if (!(obj instanceof GeneralPermeabilityKinetics)){
		return false;
	}
	
	GeneralPermeabilityKinetics gpk = (GeneralPermeabilityKinetics)obj;

	if (!compareEqual0(gpk)){
		return false;
	}
	
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 3:26:54 PM)
 * @return cbit.util.Issue[]
 */
@Override
public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
	
	super.gatherIssues(issueContext,issueList);
	
	//
	// check for correct number of reactants and products
	//
	int reactantCount=0;
	int productCount=0;
	ReactionParticipant reactionParticipants[] = getReactionStep().getReactionParticipants();
	for (int i = 0; i < reactionParticipants.length; i++){
		if (reactionParticipants[i] instanceof Product){
			reactantCount++;
		}
		if (reactionParticipants[i] instanceof Reactant){
			productCount++;
		}
	}
	if (reactantCount!=1){
		issueList.add(new Issue(getReactionStep(),issueContext,IssueCategory.KineticsApplicability,"GeneralPermeability Kinetics must have exactly one reactant",Issue.SEVERITY_ERROR));
	}
	if (productCount!=1){
		issueList.add(new Issue(getReactionStep(),issueContext,IssueCategory.KineticsApplicability,"GeneralPermeability Kinetics must have exactly one product",Issue.SEVERITY_WARNING));
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 3:44:55 PM)
 * @return cbit.vcell.model.KineticsParameter
 */
public KineticsParameter getPermeabilityParameter() {
	return getKineticsParameterFromRole(ROLE_Permeability);
}

/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 9:52:55 AM)
 * @return cbit.vcell.model.KineticsDescription
 */
public KineticsDescription getKineticsDescription() {
	return KineticsDescription.GeneralPermeability;
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/2004 3:56:05 PM)
 */
protected void refreshUnits() {
	if (bRefreshingUnits){
		return;
	}
	try {
		bRefreshingUnits=true;
		Model model = getReactionStep().getModel();
		if (model != null) {
			ModelUnitSystem modelUnitSystem = model.getUnitSystem();
			Kinetics.KineticsParameter rateParm = getReactionRateParameter();
			if (rateParm != null){
				rateParm.setUnitDefinition(modelUnitSystem.getFluxReactionUnit());
			}
			Kinetics.KineticsParameter currentDensityParm = getCurrentDensityParameter();
			if (currentDensityParm != null){
				currentDensityParm.setUnitDefinition(modelUnitSystem.getCurrentDensityUnit());
			}
			Kinetics.KineticsParameter permeabilityParm = getPermeabilityParameter();
			if (permeabilityParm != null){
				permeabilityParm.setUnitDefinition(modelUnitSystem.getPermeabilityUnit());
			}
			KineticsParameter chargeValenceParm = getChargeValenceParameter();
			if (chargeValenceParm!=null){
				chargeValenceParm.setUnitDefinition(modelUnitSystem.getInstance_DIMENSIONLESS());
			}
		}
	}finally{
		bRefreshingUnits=false;
	}	
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2003 12:05:14 AM)
 * @exception cbit.vcell.parser.ExpressionException The exception description.
 */
protected void updateGeneratedExpressions() throws cbit.vcell.parser.ExpressionException, java.beans.PropertyVetoException {
	// PRIMARY REACTION RATE
	// 
	KineticsParameter rateParm = getKineticsParameterFromRole(ROLE_ReactionRate);
	KineticsParameter currentParm = getKineticsParameterFromRole(ROLE_CurrentDensity);
	KineticsParameter permeability = getKineticsParameterFromRole(ROLE_Permeability);
	if (currentParm==null && rateParm==null){
		return;
	}
	
	ReactionParticipant reactionParticipants[] = getReactionStep().getReactionParticipants();
	Reactant R0 = null;
	Product P0 = null;
	for (int i = 0; i < reactionParticipants.length; i++){
		if (reactionParticipants[i] instanceof Reactant){
			R0 = (Reactant)reactionParticipants[i];
		}
		if (reactionParticipants[i] instanceof Product){
			P0 = (Product)reactionParticipants[i];
		}
	}
	
	if (R0!=null && P0!=null){
		Expression reactant_exp = getSymbolExpression(R0.getSpeciesContext());
		Expression product_exp = getSymbolExpression(P0.getSpeciesContext());
		Expression permeability_exp = getSymbolExpression(permeability);
		
		// P * (Product - Reactant)
		Expression newRateExp = Expression.mult(permeability_exp, Expression.add(reactant_exp, Expression.negate(product_exp)));		
		rateParm.setExpression(newRateExp);
	}
	
	
	// SECONDARY CURRENT DENSITY
	// update from reaction rate
	updateInwardCurrentDensityFromReactionRate();
}
}
