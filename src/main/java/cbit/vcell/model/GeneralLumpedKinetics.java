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

import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (8/9/2006 5:45:48 PM)
 * @author: Anuradha Lakshminarayana
 */
public class GeneralLumpedKinetics extends LumpedKinetics {
/**
 * GeneralTotalKinetics constructor comment.
 * @param name java.lang.String
 * @param reactionStep cbit.vcell.model.ReactionStep
 */
public GeneralLumpedKinetics(ReactionStep reactionStep) throws ExpressionException {
	super(KineticsDescription.GeneralLumped.getName(), reactionStep);
	try {
		KineticsParameter lumpedCurrentParm = new KineticsParameter(getDefaultParameterName(ROLE_LumpedCurrent),new Expression(0.0),ROLE_LumpedCurrent,null);
		KineticsParameter lumpedReactionRateParm = new KineticsParameter(getDefaultParameterName(ROLE_LumpedReactionRate),new Expression(0.0),ROLE_LumpedReactionRate,null);
		KineticsParameter netChargeValence = new KineticsParameter(getDefaultParameterName(ROLE_NetChargeValence),new Expression(1.0),ROLE_NetChargeValence,null);
		KineticsParameter carrierChargeValence = new KineticsParameter(getDefaultParameterName(ROLE_CarrierChargeValence),new Expression(1.0),ROLE_CarrierChargeValence,null);

		if (reactionStep.getStructure() instanceof Membrane){
			if (reactionStep instanceof FluxReaction){
				setKineticsParameters(new KineticsParameter[] { lumpedCurrentParm, lumpedReactionRateParm, carrierChargeValence });
			}else{
				setKineticsParameters(new KineticsParameter[] { lumpedCurrentParm, lumpedReactionRateParm, netChargeValence });
			}
		}else{
			setKineticsParameters(new KineticsParameter[] { lumpedReactionRateParm });
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
	if (!(obj instanceof GeneralLumpedKinetics)){
		return false;
	}
	
	GeneralLumpedKinetics gck = (GeneralLumpedKinetics)obj;

	if (!compareEqual0(gck)){
		return false;
	}
	
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 5:45:48 PM)
 * @return cbit.vcell.model.KineticsDescription
 */
public KineticsDescription getKineticsDescription() {
	return KineticsDescription.GeneralLumped;
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
			Kinetics.KineticsParameter lumpedReactionRateParm = getLumpedReactionRateParameter();
			Kinetics.KineticsParameter lumpedCurrentParm = getLumpedCurrentParameter();
			if (getReactionStep().getStructure() instanceof Membrane){
				if (lumpedCurrentParm!=null){
					lumpedCurrentParm.setUnitDefinition(modelUnitSystem.getCurrentUnit());
				}
			}
			if (lumpedReactionRateParm!=null){
				lumpedReactionRateParm.setUnitDefinition(modelUnitSystem.getLumpedReactionRateUnit());
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
		Expression tempCurrentExpression = getLumpedInwardCurrent();
		if (lumpedCurrentParm == null){
			addKineticsParameter(new KineticsParameter(getDefaultParameterName(ROLE_LumpedCurrent),tempCurrentExpression,ROLE_LumpedCurrent, getReactionStep().getModel().getUnitSystem().getCurrentUnit()));
		}else{
			lumpedCurrentParm.setExpression(tempCurrentExpression);
		}
	}else{
		if (lumpedCurrentParm != null && !lumpedCurrentParm.getExpression().isZero()){
			//removeKineticsParameter(currentParm);
			lumpedCurrentParm.setExpression(new Expression(0.0));
		}
		KineticsParameter unitParam = getKineticsParameterFromRole(ROLE_ElectricalUnitFactor);
		if (unitParam!=null){
			removeKineticsParameter(unitParam);
		}
	}
}
}
