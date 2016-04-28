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

import java.beans.PropertyVetoException;
import java.util.List;

import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;

import cbit.vcell.model.Model.ElectricalTopology;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (2/18/2002 5:07:08 PM)
 * @author: Anuradha Lakshminarayana
 */
public class GHKKinetics extends DistributedKinetics {
/**
 * NernstKinetics constructor comment.
 * @param fluxReaction cbit.vcell.model.FluxReaction
 * @exception java.lang.Exception The exception description.
 */
public GHKKinetics(FluxReaction fluxReaction) throws ExpressionException {
	super(KineticsDescription.GHK.getName(),fluxReaction);
	try {
		KineticsParameter currentParm = new KineticsParameter(getDefaultParameterName(ROLE_CurrentDensity),new Expression(0.0),ROLE_CurrentDensity,null);
		KineticsParameter rateParm = new KineticsParameter(getDefaultParameterName(ROLE_ReactionRate),new Expression(0.0),ROLE_ReactionRate,null);
		KineticsParameter permeabilityParm = new KineticsParameter(getDefaultParameterName(ROLE_Permeability),new Expression(0.0),ROLE_Permeability,null);
		KineticsParameter carrierChargeValence = new KineticsParameter(getDefaultParameterName(ROLE_CarrierChargeValence),new Expression(1.0),ROLE_CarrierChargeValence,null);

		setKineticsParameters(new KineticsParameter[] { currentParm, rateParm, carrierChargeValence, permeabilityParm });
		updateGeneratedExpressions();
		refreshUnits();
	}catch (PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException("unexpected exception: "+e.getMessage());
	}
}
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (obj == this){
		return true;
	}
	if (!(obj instanceof GHKKinetics)){
		return false;
	}
	
	GHKKinetics ghk = (GHKKinetics)obj;

	if (!compareEqual0(ghk)){
		return false;
	}
	
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 3:11:16 PM)
 * @return cbit.util.Issue[]
 */
@Override
public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
	
	super.gatherIssues(issueContext, issueList);

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
		issueList.add(new Issue(getReactionStep(), issueContext, IssueCategory.KineticsApplicability,"GHK Kinetics must have exactly one reactant",Issue.SEVERITY_ERROR));
	}
	if (productCount!=1){
		issueList.add(new Issue(getReactionStep(), issueContext, IssueCategory.KineticsApplicability,"GHK Kinetics must have exactly one product",Issue.SEVERITY_WARNING));
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 9:52:55 AM)
 * @return cbit.vcell.model.KineticsDescription
 */
public KineticsDescription getKineticsDescription() {
	return KineticsDescription.GHK;
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
protected void updateGeneratedExpressions() throws ExpressionException, PropertyVetoException {
	KineticsParameter currentParm = getKineticsParameterFromRole(ROLE_CurrentDensity);
	KineticsParameter rateParm = getKineticsParameterFromRole(ROLE_ReactionRate);
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

	KineticsParameter P = getKineticsParameterFromRole(ROLE_Permeability);
	Model model = getReactionStep().getModel();
	Expression F = getSymbolExpression(model.getFARADAY_CONSTANT());
	Expression K_GHK = getSymbolExpression(model.getK_GHK());
	Expression R = getSymbolExpression(model.getGAS_CONSTANT());
	Expression T = getSymbolExpression(model.getTEMPERATURE());
	
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
			
	//"-A0*pow("+VALENCE_SYMBOL+",2)*"+VOLTAGE_SYMBOL+"*pow("+F+",2)/("+R+"*"+T+")*(R0-(P0*exp(-"+VALENCE_SYMBOL+"*"+F+"*"+VOLTAGE_SYMBOL+"/("+R+"*"+T+"))))/(1 - exp(-"+VALENCE_SYMBOL+"*"+F+"*"+VOLTAGE_SYMBOL+"/("+R+"*"+T+")))"
	if (Neg0!=null && Pos0!=null && P!=null){
		// PRIMARY CURRENT DENSITY
		//
		Expression V_exp = getSymbolExpression(V);
		Expression carrier_z = getSymbolExpression(getKineticsParameterFromRole(Kinetics.ROLE_CarrierChargeValence));
		Expression net_z = carrier_z;
		Expression Neg0_exp = getSymbolExpression(Neg0.getSpeciesContext());
		Expression Pos0_exp = getSymbolExpression(Pos0.getSpeciesContext());
		Expression P_exp = getSymbolExpression(P);
		Expression exponentTerm = Expression.div(Expression.mult(Expression.negate(carrier_z), F, V_exp), Expression.mult(R, T));
		Expression expTerm = Expression.exp(exponentTerm);
		Expression term1 = Expression.add(Pos0_exp, Expression.negate(Expression.mult(Neg0_exp, expTerm)));
		Expression numerator = Expression.negate(Expression.mult(P_exp, K_GHK, Expression.power(carrier_z, 2.0), V_exp, Expression.power(F, 2.0), term1));
		Expression denominator = Expression.mult(R, T, Expression.add(new Expression(1.0), Expression.negate(expTerm)));
		Expression newCurrExp = Expression.div(numerator, denominator);
		
//		Expression newCurrExp = new Expression("-"+P.getName()+"*"+K_GHK.getName()+"*pow("+z+",2)*"+V.getName()+"*pow("+F.getName()+",2)/("+R.getName()+"*"+T.getName()+")*("+R0.getName()+"-("+P0.getName()+"*exp(-"+z+"*"+F.getName()+"*"+V.getName()+"/("+R.getName()+"*"+T.getName()+"))))/(1 - exp(-"+z+"*"+F.getName()+"*"+V.getName()+"/("+R.getName()+"*"+T.getName()+")))");
		currentParm.setExpression(newCurrExp);
		
		// SECONDARY REACTION RATE
		// update from current density
		updateReactionRatesFromInwardCurrentDensity();
	}
}
}
