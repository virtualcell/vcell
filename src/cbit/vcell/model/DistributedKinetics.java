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

import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Structure.StructureSize;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionUtils;
import cbit.vcell.units.VCUnitDefinition;


/**
 * DistributedKinetics is the abstract superclass of all reaction kinetics that operate locally (can be defined at a point)
 * and form the basis for distributed parameter, spatial modeling.  This is the "text-book" description of chemical 
 * kinetics (in terms of time rate of change of local concentration).
 * 
 * For electrical transport, current density describes the local charge transport across a unit area of membrane rather than
 * the "lumped" description (total current crossing the entire membrane).
 * 
 * For nonspatial descriptions, this is a less convenient form for some users because it requires separately defining
 * compartment size and time rate of change of concentrations, where only their product matters.  For spatial applications,
 * this is the only form that can give rise to spatially inhomogeneous behavior.
 * 
 * A DistributedKinetics may be formed from a corresponding LumpedKinetics by assuming that the LumpedKinetics can be
 * uniformly distributed within a compartment of known size. 
 *
 * @see LumpedKinetics
 *
 */
public abstract class DistributedKinetics extends Kinetics {

	public DistributedKinetics(String name, ReactionStep reactionStep) {
		super(name, reactionStep);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/14/2003 8:53:00 AM)
	 * @return cbit.vcell.model.Parameter
	 */
	public final KineticsParameter getCurrentDensityParameter() {
		return getKineticsParameterFromRole(ROLE_CurrentDensity);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/14/2003 8:53:00 AM)
	 * @return cbit.vcell.model.Parameter
	 */
	public final KineticsParameter getReactionRateParameter() {
		return getKineticsParameterFromRole(ROLE_ReactionRate);
	}
	
	public final KineticsParameter getAuthoritativeParameter(){
		if (getKineticsDescription().isElectrical()){
			return getCurrentDensityParameter();
		}else{
			return getReactionRateParameter();
		}
	}

	public static DistributedKinetics toDistributedKinetics(LumpedKinetics origLumpedKinetics){
		KineticsParameter[] origLumpedKineticsParms = origLumpedKinetics.getKineticsParameters();
		ReactionStep reactionStep = origLumpedKinetics.getReactionStep();
		try {
			Vector<KineticsParameter> parmsToAdd = new Vector<KineticsParameter>();
			
			
			DistributedKinetics distributedKinetics = null;
			StructureSize structureSize = origLumpedKinetics.getReactionStep().getStructure().getStructureSize();
			Expression sizeExp = new Expression(structureSize.getName());
			VCUnitDefinition sizeUnit = structureSize.getUnitDefinition();
			if (origLumpedKinetics.getKineticsDescription().isElectrical()){
				distributedKinetics = new GeneralCurrentKinetics(reactionStep);
			}else{
				distributedKinetics = new GeneralKinetics(reactionStep);
			}
			Expression unitFactor = new Expression(distributedKinetics.getAuthoritativeParameter().getUnitDefinition().multiplyBy(sizeUnit).divideBy(origLumpedKinetics.getAuthoritativeParameter().getUnitDefinition()).getDimensionlessScale());
			Expression distributionFactor = Expression.div(unitFactor, sizeExp);
			KineticsParameter lumpedAuthoritativeParm = origLumpedKinetics.getAuthoritativeParameter();
			KineticsParameter distAuthoritativeParam = distributedKinetics.getAuthoritativeParameter();
			Expression newDistributedAuthoritativeExp = Expression.mult(distributionFactor,lumpedAuthoritativeParm.getExpression()).flatten();
			Expression substitutedExp = newDistributedAuthoritativeExp.getSubstitutedExpression(sizeExp, new Expression(1.0));
			if (ExpressionUtils.functionallyEquivalent(newDistributedAuthoritativeExp,substitutedExp,false)){
				newDistributedAuthoritativeExp = substitutedExp.flatten();
			}
			parmsToAdd.add(distributedKinetics.new KineticsParameter(distAuthoritativeParam.getName(),newDistributedAuthoritativeExp,distAuthoritativeParam.getRole(),distAuthoritativeParam.getUnitDefinition()));

			for (int i = 0; i < origLumpedKineticsParms.length; i++) {
				if (origLumpedKineticsParms[i].getRole()!=Kinetics.ROLE_LumpedReactionRate &&
						origLumpedKineticsParms[i].getRole()!=Kinetics.ROLE_LumpedCurrent){
					parmsToAdd.add(distributedKinetics.new KineticsParameter(origLumpedKineticsParms[i].getName(),new Expression(origLumpedKineticsParms[i].getExpression()),Kinetics.ROLE_UserDefined,origLumpedKineticsParms[i].getUnitDefinition()));
				}
			}
			distributedKinetics.addKineticsParameters(parmsToAdd.toArray(new KineticsParameter[parmsToAdd.size()]));
			return distributedKinetics;
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create distributed Kinetics for reaction: \""+origLumpedKinetics.getReactionStep().getName()+"\": "+e.getMessage());
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create distributed Kinetics for reaction: \""+origLumpedKinetics.getReactionStep().getName()+"\": "+e.getMessage());
		}
	}

}
