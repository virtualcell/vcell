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

import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;

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
public GeneralPermeabilityKinetics(ReactionStep reactionStep) throws ExpressionException {
	super(KineticsDescription.GeneralPermeability.getName(),reactionStep);
	try {
		KineticsParameter currentParm = new KineticsParameter(getDefaultParameterName(ROLE_CurrentDensity),new Expression(0.0),ROLE_CurrentDensity,null);
		KineticsParameter rateParm = new KineticsParameter(getDefaultParameterName(ROLE_ReactionRate),new Expression(0.0),ROLE_ReactionRate,null);
		KineticsParameter permeabilityParam = new KineticsParameter(getDefaultParameterName(ROLE_Permeability),new Expression(0.0),ROLE_Permeability,null);
		KineticsParameter chargeValence = new KineticsParameter(getDefaultParameterName(ROLE_ChargeValence),new Expression(1.0),ROLE_ChargeValence,null);

		setKineticsParameters(new KineticsParameter[] { currentParm, rateParm, chargeValence, permeabilityParam });
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
public void gatherIssues(java.util.Vector issueList) {
	
	super.gatherIssues(issueList);
	
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
		issueList.add(new Issue(this,IssueCategory.KineticsApplicability,"GeneralPermeability Kinetics must have exactly one reactant",Issue.SEVERITY_ERROR));
	}
	if (productCount!=1){
		issueList.add(new Issue(this,IssueCategory.KineticsApplicability,"GeneralPermeability Kinetics must have exactly one product",Issue.SEVERITY_WARNING));
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
			KineticsParameter chargeValenceParm = getKineticsParameterFromRole(ROLE_ChargeValence);
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
		Expression z = getSymbolExpression(getKineticsParameterFromRole(ROLE_ChargeValence));
		Expression outside_exp = getSymbolExpression(R0.getSpeciesContext());
		Expression inside_exp = getSymbolExpression(P0.getSpeciesContext());
		Expression permeability_exp = getSymbolExpression(permeability);
		
		// P * (fOutside - fInside)
		Expression newRateExp = Expression.mult(permeability_exp, Expression.add(outside_exp, Expression.negate(inside_exp)));		
		rateParm.setExpression(newRateExp);

		if (getReactionStep().getPhysicsOptions() == ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL){
			Expression tempCurrentExpression = null;
			Expression rate = getSymbolExpression(rateParm);
			Expression F_nmol = getSymbolExpression(getReactionStep().getModel().getFARADAY_CONSTANT_NMOLE());
			tempCurrentExpression = Expression.mult(rate, z, F_nmol);
			if (currentParm == null){
				addKineticsParameter(new KineticsParameter(getDefaultParameterName(ROLE_CurrentDensity),tempCurrentExpression,ROLE_CurrentDensity,null));
			}else{
				currentParm.setExpression(tempCurrentExpression);
			}
		}else{
			if (currentParm != null && !currentParm.getExpression().isZero()){
				currentParm.setExpression(new Expression(0.0));
			}
		}
	}
}
}
