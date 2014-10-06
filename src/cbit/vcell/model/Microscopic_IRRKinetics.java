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

import cbit.vcell.model.Model.ReservedSymbol;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

public class Microscopic_IRRKinetics extends DistributedKinetics {

	public Microscopic_IRRKinetics(SimpleReaction simpleReaction) throws ExpressionException {
		super(KineticsDescription.Microscopic_irreversible.getName(),simpleReaction);
		try {
			//calculated parameters
			KineticsParameter rateParm = new KineticsParameter(getDefaultParameterName(ROLE_ReactionRate),new Expression(0.0),ROLE_ReactionRate,null);
			KineticsParameter currentParm = new KineticsParameter(getDefaultParameterName(ROLE_CurrentDensity),new Expression(0.0),ROLE_CurrentDensity,null);
			KineticsParameter kOn = new KineticsParameter(getDefaultParameterName(ROLE_KOn),new Expression(0.0),ROLE_KOn,null);
			KineticsParameter netChargeValence = new KineticsParameter(getDefaultParameterName(ROLE_NetChargeValence),new Expression(1.0),ROLE_NetChargeValence,null);

			//user input parameters
			KineticsParameter bindingRadius = new KineticsParameter(getDefaultParameterName(ROLE_Binding_Radius),new Expression(0.0),ROLE_Binding_Radius,null);
			KineticsParameter diff_reactant1 = new KineticsParameter(getDefaultParameterName(ROLE_Diffusion_Reactant1),new Expression(0.0),ROLE_Diffusion_Reactant1,null);
			KineticsParameter diff_reactant2 = new KineticsParameter(getDefaultParameterName(ROLE_Diffusion_Reactant2),new Expression(0.0),ROLE_Diffusion_Reactant2,null);
			KineticsParameter conc_reactant1 = new KineticsParameter(getDefaultParameterName(ROLE_Concentration_Reactant1),new Expression(0.0),ROLE_Concentration_Reactant1,null);
			KineticsParameter conc_reactant2 = new KineticsParameter(getDefaultParameterName(ROLE_Concentration_Reactant2),new Expression(0.0),ROLE_Concentration_Reactant2,null);
			
			if (simpleReaction.getStructure() instanceof Membrane){
				setKineticsParameters(new KineticsParameter[] { rateParm, currentParm, netChargeValence, bindingRadius, kOn, diff_reactant1, diff_reactant2, conc_reactant1, conc_reactant2 });
			}else{
				throw new RuntimeException("Macroscopic_Irreversible kinetics not supported in a volumetric compartment.");
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
		if (!(obj instanceof Microscopic_IRRKinetics)){
			return false;
		}
		
		Microscopic_IRRKinetics mac_irr = (Microscopic_IRRKinetics)obj;

		if (!compareEqual0(mac_irr)){
			return false;
		}
		
		return true;
	}

	public KineticsDescription getKineticsDescription() {
		return KineticsDescription.Microscopic_irreversible;
	}
	
	public KineticsParameter getBindingRadiusParameter() {
		return getKineticsParameterFromRole(ROLE_Binding_Radius);
	}
 
	public KineticsParameter getKOnParameter() {
		return getKineticsParameterFromRole(ROLE_KOn);
	}

	public KineticsParameter getDiffReactant1Parameter() {
		return getKineticsParameterFromRole(ROLE_Diffusion_Reactant1);
	}

	public KineticsParameter getDiffReactant2Parameter() {
		return getKineticsParameterFromRole(ROLE_Diffusion_Reactant2);
	}

	public KineticsParameter getConcReactant1Parameter() {
		return getKineticsParameterFromRole(ROLE_Concentration_Reactant1);
	}

	public KineticsParameter getConcReactant2Parameter() {
		return getKineticsParameterFromRole(ROLE_Concentration_Reactant2);
	}
	
	protected void refreshUnits() {
		if (bRefreshingUnits){
			return;
		}
		try {
			bRefreshingUnits=true;
			
			Kinetics.KineticsParameter rateParam = getReactionRateParameter();
			Kinetics.KineticsParameter currentDensityParam = getCurrentDensityParameter();
			Kinetics.KineticsParameter kOnParam = getKOnParameter();
			Kinetics.KineticsParameter bindingRadiusParam = getBindingRadiusParameter();
			Kinetics.KineticsParameter diff_react_1Param = getDiffReactant1Parameter();
			Kinetics.KineticsParameter diff_react_2Param = getDiffReactant2Parameter();
			Kinetics.KineticsParameter conc_react_1Param = getConcReactant1Parameter();
			Kinetics.KineticsParameter conc_react_2Param = getConcReactant2Parameter();
			Kinetics.KineticsParameter chargeValence = getChargeValenceParameter();
			
			Model model = getReactionStep().getModel();
			if (model != null) {
				ModelUnitSystem modelUnitSystem = model.getUnitSystem();
				if (getReactionStep().getStructure() instanceof Membrane){
					rateParam.setUnitDefinition(modelUnitSystem.getMembraneReactionRateUnit());
					if (currentDensityParam!=null){
						currentDensityParam.setUnitDefinition(modelUnitSystem.getCurrentDensityUnit());
					}
				}else if (getReactionStep().getStructure() instanceof Feature){
					throw new RuntimeException("Macroscopic_IRR kinetics not supported in a volumetric compartment.");
				}else{
					throw new RuntimeException("unexpected structure type "+getReactionStep().getStructure()+" in Microscopic_IRRKinetics.refreshUnits()");
				}
				
				if (bindingRadiusParam != null) {
					bindingRadiusParam.setUnitDefinition(modelUnitSystem.getBindingRadiusUnit());
				}
				
				if (kOnParam != null) {
					kOnParam.setUnitDefinition(modelUnitSystem.getTimeUnit().getInverse().divideBy(modelUnitSystem.getMembraneConcentrationUnit()));
				}
				
				if (diff_react_1Param != null) {
					diff_react_1Param.setUnitDefinition(modelUnitSystem.getDiffusionRateUnit());
				}
	
				if (diff_react_2Param != null) {
					diff_react_2Param.setUnitDefinition(modelUnitSystem.getDiffusionRateUnit());
				}
	
				if (conc_react_1Param != null) {
					conc_react_1Param.setUnitDefinition(modelUnitSystem.getMembraneConcentrationUnit());
				}
	
				if (conc_react_2Param != null) {
					conc_react_2Param.setUnitDefinition(modelUnitSystem.getMembraneConcentrationUnit());
				}
				
				if (chargeValence != null) {
					chargeValence.setUnitDefinition(modelUnitSystem.getInstance_DIMENSIONLESS());
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
	protected void updateGeneratedExpressions() throws cbit.vcell.parser.ExpressionException, PropertyVetoException {
		KineticsParameter rateParm = getKineticsParameterFromRole(ROLE_ReactionRate);
		KineticsParameter currentParm = getKineticsParameterFromRole(ROLE_CurrentDensity);
		KineticsParameter kOnParam = getKineticsParameterFromRole(ROLE_KOn);
		KineticsParameter bindingRadiusParam = getKineticsParameterFromRole(ROLE_Binding_Radius);
		KineticsParameter diff_react1Param = getKineticsParameterFromRole(ROLE_Diffusion_Reactant1);
		KineticsParameter diff_react2Param = getKineticsParameterFromRole(ROLE_Diffusion_Reactant2);
		KineticsParameter conc_react1Param = getKineticsParameterFromRole(ROLE_Concentration_Reactant1);
		KineticsParameter conc_react2Param = getKineticsParameterFromRole(ROLE_Concentration_Reactant2);
		
		if (currentParm==null && rateParm==null){
			return;
		}
		
		// rate prameter expr.
		ReactionParticipant rp_Array[] = getReactionStep().getReactionParticipants();
		Expression kOn_exp = getSymbolExpression(kOnParam);
		Expression newRateExp = null;
		int reactantCount = 0;
		for (int i = 0; i < rp_Array.length; i++) {
			Expression term = null;
			Expression speciesContext = getSymbolExpression(rp_Array[i].getSpeciesContext());
			int stoichiometry = rp_Array[i].getStoichiometry();
			if (rp_Array[i] instanceof Reactant){
				reactantCount++;
				if (stoichiometry < 1){
					throw new ExpressionException("reactant must have stoichiometry of at least 1");
				}else if (stoichiometry == 1){
					term = speciesContext;
				}else{
					term = Expression.power(speciesContext,new Expression(stoichiometry));
				}	
				kOn_exp = Expression.mult(kOn_exp,term);	
			}
		}

		if (reactantCount > 0){
			newRateExp = kOn_exp;
		}else{
			newRateExp = new Expression(0.0);
		}
		rateParm.setExpression(newRateExp);
		
		// current Parameter. set to 0??
		currentParm.setExpression(new Expression(0.0));
		
		// Kon parameter
		ReservedSymbol pi_ReservedSymbol = getReactionStep().getModel().getPI_CONSTANT();
		Expression Pa = Expression.max(getSymbolExpression(conc_react1Param), getSymbolExpression(conc_react2Param));
		Expression sqrt_Pa_PI = Expression.sqrt(Expression.mult(Pa, getSymbolExpression(pi_ReservedSymbol))); //sqrt(Pa*PI)
		Expression b = Expression.div(new Expression(1), sqrt_Pa_PI); //  1/sqrt(Pa*PI)
		Expression ln_b = Expression.log(b);
		Expression ln_Radius = Expression.log(getSymbolExpression(bindingRadiusParam));
		Expression sumD = Expression.add(getSymbolExpression(diff_react1Param), getSymbolExpression(diff_react2Param));
		Expression exp2_PI_D = Expression.mult(new Expression(2.0), getSymbolExpression(pi_ReservedSymbol), sumD); //2*PI*D
		Expression expLnb_LnR = Expression.add(ln_b, Expression.negate(ln_Radius)); //Lnb-LnR
		Expression kOnExp = Expression.div(exp2_PI_D, expLnb_LnR); // Kon = 2*PI*D/Ln(b/R)
		
		if (kOnParam != null && kOn_exp != null) {
			kOnParam.setExpression(kOnExp);
		} 
		
		// SECONDARY CURRENT DENSITY
		// update from reaction rate
		updateInwardCurrentDensityFromReactionRate();
		
	}	

}
