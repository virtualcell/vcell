/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.kinetics;

import org.vcell.pathway.sbo.SBOList;

import cbit.vcell.model.Catalyst;
import cbit.vcell.model.HMM_IRRKinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;


public class SBPAXHMMIrrevLawBuilder implements KineticLawBuilder {

	public Match getMatch(KineticContext context) {
		if(context.getReactants().size() != 1) {
			return Match.NONE;
		}
		if(context.hasParamForSBO(SBOList.REVERSE_PARAM)) {
			return Match.NONE;
		} else {
			boolean hasMichaelisConstant = context.hasParamForSBO(SBOList.MICHAELIS_CONST_FORW);
			boolean hasCatalyticRateConst = context.hasParamForSBO(SBOList.CATALYTIC_RATE_CONST_FORW);
			boolean hasMaximalVelocity = context.hasParamForSBO(SBOList.MAXIMAL_VELOCITY_FORW);
			boolean hasUsableRateConstant = 
				hasMaximalVelocity || (hasCatalyticRateConst && context.getCatalysts().size() == 1);
			if(hasMichaelisConstant && hasUsableRateConstant) {
				return Match.PERFECT;
			} else if(hasMichaelisConstant || hasUsableRateConstant) {
				return Match.SOME;
			}			
		}
		return Match.NONE;
	}

	public void addKinetics(KineticContext context) {
		try {
			ReactionStep reaction = context.getReaction();
			HMM_IRRKinetics kinetics = new HMM_IRRKinetics((SimpleReaction)reaction);
			NameScope modelScope = reaction.getModel().getNameScope();
			ModelParameter kMichaelis = context.getParameter(SBOList.MICHAELIS_CONST_FORW);
			if(kMichaelis != null) {
				KineticsParameter kmParameter = kinetics.getKmParameter();
				kmParameter.setExpression(new Expression(kMichaelis, modelScope)); 
				kmParameter.setUnitDefinition(kMichaelis.getUnitDefinition());
			}
			ModelParameter kcat = context.getParameter(SBOList.CATALYTIC_RATE_CONST_FORW);
			if(kcat != null && context.getCatalysts().size() == 1) {
				KineticsParameter vmaxParameter = kinetics.getVmaxParameter();
				Catalyst catalyst = context.getCatalysts().iterator().next();
				vmaxParameter.setExpression(Expression.mult(new Expression(kcat, modelScope),
						new Expression(catalyst.getSpeciesContext(), modelScope))); 
//				vmaxParameter.setUnitDefinition(vMax.getUnitDefinition());
			} else {
				ModelParameter vMax = context.getParameter(SBOList.MAXIMAL_VELOCITY_FORW);
				if(vMax != null) {
					KineticsParameter vmaxParameter = kinetics.getVmaxParameter();
					vmaxParameter.setExpression(new Expression(vMax, modelScope)); 
					vmaxParameter.setUnitDefinition(vMax.getUnitDefinition());
				}
			}
		} catch (ExpressionException e) {
			e.printStackTrace();
		}
	}

	
	
}
