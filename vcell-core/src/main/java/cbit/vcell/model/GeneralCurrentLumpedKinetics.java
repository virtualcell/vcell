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

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (8/9/2006 5:45:48 PM)
 * @author: Anuradha Lakshminarayana
 */
public class GeneralCurrentLumpedKinetics extends LumpedKinetics {
/**
 * GeneralTotalKinetics constructor comment.
 * @param name java.lang.String
 * @param reactionStep cbit.vcell.model.ReactionStep
 */
public GeneralCurrentLumpedKinetics(ReactionStep reactionStep) throws ExpressionException {
	super(KineticsDescription.GeneralCurrentLumped.getName(), reactionStep);
	try {
		KineticsParameter lumpedCurrentParm = new KineticsParameter(getDefaultParameterName(ROLE_LumpedCurrent),new Expression(0.0),ROLE_LumpedCurrent,null);
		KineticsParameter lumpedReactionRateParm = new KineticsParameter(getDefaultParameterName(ROLE_LumpedReactionRate),new Expression(0.0),ROLE_LumpedReactionRate,null);
		KineticsParameter carrierChargeValence = new KineticsParameter(getDefaultParameterName(ROLE_CarrierChargeValence),new Expression(1.0),ROLE_CarrierChargeValence,null);
		KineticsParameter netChargeValence = new KineticsParameter(getDefaultParameterName(ROLE_NetChargeValence),new Expression(1.0),ROLE_NetChargeValence,null);

		if (reactionStep instanceof FluxReaction){
			setKineticsParameters(new KineticsParameter[] { lumpedCurrentParm, lumpedReactionRateParm, carrierChargeValence });
		}else{
			setKineticsParameters(new KineticsParameter[] { lumpedCurrentParm, lumpedReactionRateParm, netChargeValence });
		}
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
	if (!(obj instanceof GeneralCurrentLumpedKinetics)){
		return false;
	}
	
	GeneralCurrentLumpedKinetics gck = (GeneralCurrentLumpedKinetics)obj;

	if (!compareEqual0(gck)){
		return false;
	}
	
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 3:08:25 PM)
 * @return cbit.util.Issue[]
 */
@Override
public void gatherIssues(IssueContext issueContext, List<Issue> issueList){

	super.gatherIssues(issueContext,issueList);
	
	if (getReactionStep() instanceof SimpleReaction){
		issueList.add(new Issue(getReactionStep(),issueContext,IssueCategory.KineticsApplicability,"General Current Kinetics expected within a flux reaction only",Issue.SEVERITY_ERROR));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 5:45:48 PM)
 * @return cbit.vcell.model.KineticsDescription
 */
public KineticsDescription getKineticsDescription() {
	return KineticsDescription.GeneralCurrentLumped;
}



/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 5:45:48 PM)
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
			Kinetics.KineticsParameter rateParm = getLumpedReactionRateParameter();
			if (getReactionStep() instanceof FluxReaction){
				if (rateParm != null){
					rateParm.setUnitDefinition(modelUnitSystem.getLumpedReactionRateUnit());
				}
			}else if (getReactionStep() instanceof SimpleReaction){
				throw new RuntimeException("General Current Lumped Kinetics not expected within a flux reaction only");
			}
			Kinetics.KineticsParameter currentParm = getLumpedCurrentParameter();
			if (currentParm != null){
				currentParm.setUnitDefinition(modelUnitSystem.getCurrentUnit());
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
 * Creation date: (8/9/2006 5:45:48 PM)
 * @exception cbit.vcell.parser.ExpressionException The exception description.
 */
protected void updateGeneratedExpressions() throws ExpressionException, java.beans.PropertyVetoException {
	KineticsParameter lumpedCurrentParm = getLumpedCurrentParameter();
	KineticsParameter lumpedReactionRate = getLumpedReactionRateParameter();
	
	if (lumpedCurrentParm==null && lumpedReactionRate==null){
		return;
	}
	
	if (getReactionStep().getPhysicsOptions() == ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL){
		Expression tempRateExpression = getLumpedReactionRate();
		if (lumpedReactionRate == null){
			addKineticsParameter(new KineticsParameter(getDefaultParameterName(ROLE_LumpedReactionRate),tempRateExpression,ROLE_LumpedReactionRate, getReactionStep().getModel().getUnitSystem().getLumpedReactionRateUnit()));
		}else{
			lumpedReactionRate.setExpression(tempRateExpression);
		}
	}else{
		if (lumpedReactionRate != null && !lumpedReactionRate.getExpression().isZero()){
			//removeKineticsParameter(rateParm);
			lumpedReactionRate.setExpression(new Expression(0.0));
		}
		KineticsParameter unitParam = getKineticsParameterFromRole(ROLE_ElectricalUnitFactor);
		if (unitParam!=null){
			removeKineticsParameter(unitParam);
		}
	}
}
}
