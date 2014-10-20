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

import cbit.vcell.model.Model.ElectricalTopology;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (2/18/2002 5:07:08 PM)
 * @author: Anuradha Lakshminarayana
 */
public class NernstKinetics extends DistributedKinetics {
/**
 * NernstKinetics constructor comment.
 * @param reactionStep cbit.vcell.model.ReactionStep
 * @exception java.lang.Exception The exception description.
 */
public NernstKinetics(FluxReaction fluxReaction) throws ExpressionException {
	super(KineticsDescription.Nernst.getName(),fluxReaction);
	try {
		KineticsParameter currentParm = new KineticsParameter(getDefaultParameterName(ROLE_CurrentDensity),new Expression(0.0),ROLE_CurrentDensity,null);
		KineticsParameter rateParm = new KineticsParameter(getDefaultParameterName(ROLE_ReactionRate),new Expression(0.0),ROLE_ReactionRate,null);
		KineticsParameter conductivityParm = new KineticsParameter(getDefaultParameterName(ROLE_Conductivity),new Expression(0.0),ROLE_Conductivity,null);
		KineticsParameter carrierChargeValence = new KineticsParameter(getDefaultParameterName(ROLE_CarrierChargeValence),new Expression(1.0),ROLE_CarrierChargeValence,null);

		setKineticsParameters(new KineticsParameter[] { currentParm, rateParm, carrierChargeValence, conductivityParm });
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
	if (!(obj instanceof NernstKinetics)){
		return false;
	}
	
	NernstKinetics nk = (NernstKinetics)obj;

	if (!compareEqual0(nk)){
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
		if (reactionParticipants[i] instanceof Reactant){
			reactantCount++;
		}
		if (reactionParticipants[i] instanceof Product){
			productCount++;
		}
	}
	if (reactantCount!=1){
		issueList.add(new Issue(getReactionStep(),issueContext,IssueCategory.KineticsApplicability,"Nernst Kinetics must have exactly one reactant",Issue.SEVERITY_ERROR));
	}
	if (productCount!=1){
		issueList.add(new Issue(getReactionStep(),issueContext,IssueCategory.KineticsApplicability,"Nernst Kinetics must have exactly one product",Issue.SEVERITY_WARNING));
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 3:44:55 PM)
 * @return cbit.vcell.model.KineticsParameter
 */
public KineticsParameter getConductivityParameter() {
	return getKineticsParameterFromRole(ROLE_Conductivity);
}

/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 9:52:55 AM)
 * @return cbit.vcell.model.KineticsDescription
 */
public KineticsDescription getKineticsDescription() {
	return KineticsDescription.Nernst;
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
			Kinetics.KineticsParameter conductivityParm = getConductivityParameter();
			if (conductivityParm != null){
				conductivityParm.setUnitDefinition(modelUnitSystem.getConductanceUnit().divideBy(modelUnitSystem.getAreaUnit()));
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
	KineticsParameter rateParm = getKineticsParameterFromRole(ROLE_ReactionRate);
	KineticsParameter currentParm = getKineticsParameterFromRole(ROLE_CurrentDensity);
	KineticsParameter conductivity = getKineticsParameterFromRole(ROLE_Conductivity);
	if (currentParm==null && rateParm==null){
		return;
	}
	
	Membrane membrane = (Membrane)getReactionStep().getStructure();
	if (!(getReactionStep().getStructure() instanceof Membrane)){
		return;
	}
	ElectricalTopology electricalTopology = getReactionStep().getModel().getElectricalTopology();
	Membrane.MembraneVoltage V = membrane.getMembraneVoltage();
	Feature negativeFeature = electricalTopology.getNegativeFeature(membrane);
	Feature positiveFeature = electricalTopology.getPositiveFeature(membrane);

	if (negativeFeature == null || positiveFeature == null){
		return;
	}
	
	ReactionParticipant reactionParticipants[] = getReactionStep().getReactionParticipants();
	ReactionParticipant Neg0 = null;
	ReactionParticipant Pos0 = null;
	for (int i = 0; i < reactionParticipants.length; i++){
		ReactionParticipant rp = reactionParticipants[i];
		if ((rp instanceof Reactant || rp instanceof Product)){
			if (rp.getStructure() == negativeFeature){
				Neg0 = rp;
			}else if (rp.getStructure() == positiveFeature){
				Pos0 = rp;
			}
		}
	}
	
	if (Neg0!=null && Pos0!=null){
		Model model = getReactionStep().getModel();
		Expression carrier_z = getSymbolExpression(getKineticsParameterFromRole(ROLE_CarrierChargeValence));
		Expression net_z = carrier_z;
		Expression F = getSymbolExpression(model.getFARADAY_CONSTANT());
		Expression R = getSymbolExpression(model.getGAS_CONSTANT());
		Expression T = getSymbolExpression(model.getTEMPERATURE());
		Expression Pos0_exp = getSymbolExpression(Pos0.getSpeciesContext());
		Expression Neg0_exp = getSymbolExpression(Neg0.getSpeciesContext());
		Expression V_exp = getSymbolExpression(V);
		Expression conductivity_exp = getSymbolExpression(conductivity);
		
		// 	new Expression("A0*(("+R+"*"+T+"/("+VALENCE_SYMBOL+"*"+F+"))*log(P0/R0)-"+VOLTAGE_SYMBOL+")"),
//		Expression newCurrExp = new Expression(conductivity.getName()+"*(("+R.getName()+"*"+T.getName()+"/("+z+"*"+F.getName()+"))*log("+P0.getName()+"/"+R0.getName()+") - "+V.getName()+")");
		Expression logterm = Expression.log(Expression.div(Neg0_exp, Pos0_exp));      // log(P/R)
		Expression term1 = Expression.div(Expression.mult(R, T), Expression.mult(carrier_z, F));  // (R * T / (z * F))
		// C * (term1 * logterm - V)
		Expression newCurrExp = Expression.mult(conductivity_exp, Expression.add(Expression.mult(term1, logterm), Expression.negate(V_exp)));		
		currentParm.setExpression(newCurrExp);
		
		// SECONDARY REACTION RATE
		// update from current density
		updateReactionRatesFromInwardCurrentDensity();
	}
}
}
