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
import java.util.Vector;

import cbit.vcell.model.Structure.StructureSize;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionUtils;
import cbit.vcell.units.VCUnitDefinition;


/**
 * LumpedKinetics is the abstract superclass of all reaction kinetics that operate on pools of molecules 
 * and describe the rate of change of the total number of molecules or total current across a membrane.
 * 
 * For electrical transport, total current (rather than current density) is the "lumped" description of
 * charge transport.
 * 
 * For nonspatial descriptions, this can be a convenient form.  However, for spatial models either the
 * LumpedKinetics has to be translated to a corresponding DistributedKinetics (describing a distributed 
 * parameter system) or these will map to Region variables and Region equations that described lumped quantities.
 * 
 * A LumpedKinetics may be formed from a corresponding DistributedKinetics by integrating the local behavior 
 * over a given compartment of known size.  For nonspatial models, no assumptions are necessary, for spatial models
 * an assumption of uniform behavior over the compartment is required (e.g. no gradients or inhomogenieties).
 *
 * @see DistributedKinetics
 *
 */
public abstract class LumpedKinetics extends Kinetics {
	
	// store SBML unit for rate expression from SBML kinetic law.
//	private transient VCUnitDefinition sbmlRateUnit = null;

	public LumpedKinetics(String name, ReactionStep reactionStep) {
		super(name, reactionStep);
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (8/6/2002 3:37:07 PM)
	 * @return cbit.vcell.model.KineticsParameter
	 */
	public KineticsParameter getLumpedReactionRateParameter() {
		return getKineticsParameterFromRole(ROLE_LumpedReactionRate);
	}

	public final KineticsParameter getAuthoritativeParameter(){
		if (getKineticsDescription().isElectrical()){
			return getLumpedCurrentParameter();
		}else{
			return getLumpedReactionRateParameter();
		}
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (8/6/2002 3:37:07 PM)
	 * @return cbit.vcell.model.KineticsParameter
	 */
	public KineticsParameter getLumpedCurrentParameter() {
		return getKineticsParameterFromRole(ROLE_LumpedCurrent);
	}

	public static LumpedKinetics toLumpedKinetics(DistributedKinetics distributedKinetics){
		KineticsParameter[] distKineticsParms = distributedKinetics.getKineticsParameters();
		ReactionStep reactionStep = distributedKinetics.getReactionStep();
		try {
			Vector<KineticsParameter> parmsToAdd = new Vector<KineticsParameter>();
			
			LumpedKinetics lumpedKinetics = null;
			StructureSize structureSize = distributedKinetics.getReactionStep().getStructure().getStructureSize();
			Expression sizeExp = new Expression(structureSize.getName());
			VCUnitDefinition sizeUnit = structureSize.getUnitDefinition();
			if (distributedKinetics.getKineticsDescription().isElectrical()){
				lumpedKinetics = new GeneralCurrentLumpedKinetics(reactionStep);
			}else{
				lumpedKinetics = new GeneralLumpedKinetics(reactionStep);
			}
			Expression unitFactor = new Expression(lumpedKinetics.getAuthoritativeParameter().getUnitDefinition().divideBy(sizeUnit).divideBy(distributedKinetics.getAuthoritativeParameter().getUnitDefinition()).getDimensionlessScale());
			Expression lumpingFactor = Expression.mult(sizeExp, unitFactor);
			KineticsParameter distAuthoritativeParam = distributedKinetics.getAuthoritativeParameter();
			KineticsParameter lumpedAuthoritativeParam = lumpedKinetics.getAuthoritativeParameter();
			Expression newLumpedAuthoritativeExp = Expression.mult(lumpingFactor,distAuthoritativeParam.getExpression()).flatten();
			Expression substitutedExp = newLumpedAuthoritativeExp.getSubstitutedExpression(sizeExp, new Expression(1.0));
			if (ExpressionUtils.functionallyEquivalent(newLumpedAuthoritativeExp,substitutedExp,false)){
				newLumpedAuthoritativeExp = substitutedExp.flatten();
			}
			parmsToAdd.add(lumpedKinetics.new KineticsParameter(lumpedAuthoritativeParam.getName(),newLumpedAuthoritativeExp,lumpedAuthoritativeParam.getRole(),lumpedAuthoritativeParam.getUnitDefinition()));

			for (int i = 0; i < distKineticsParms.length; i++) {
				if (distKineticsParms[i].getRole()!=Kinetics.ROLE_ReactionRate &&
					distKineticsParms[i].getRole()!=Kinetics.ROLE_CurrentDensity){
					parmsToAdd.add(lumpedKinetics.new KineticsParameter(distKineticsParms[i].getName(),new Expression(distKineticsParms[i].getExpression()),Kinetics.ROLE_UserDefined,distKineticsParms[i].getUnitDefinition()));
				}
			}
			lumpedKinetics.addKineticsParameters(parmsToAdd.toArray(new KineticsParameter[parmsToAdd.size()]));
			return lumpedKinetics;
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create lumped Kinetics for reaction: \""+reactionStep.getName()+"\": "+e.getMessage());
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create lumped Kinetics for reaction: \""+reactionStep.getName()+"\": "+e.getMessage());
		}
	}

/*	public VCUnitDefinition getSbmlRateUnit() {
		return sbmlRateUnit;
	}
	
	public void setSbmlRateUnit(VCUnitDefinition sbmlRateUnit) {
		this.sbmlRateUnit = sbmlRateUnit;
	}
*/
}

