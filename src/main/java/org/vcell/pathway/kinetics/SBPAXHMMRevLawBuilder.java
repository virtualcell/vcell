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

import java.beans.PropertyVetoException;

import org.vcell.pathway.sbo.SBOList;

import cbit.vcell.model.Catalyst;
import cbit.vcell.model.HMM_REVKinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;


public class SBPAXHMMRevLawBuilder implements KineticLawBuilder {

	public Match getMatch(KineticContext context) {
		if(context.getReactants().size() != 1 || context.getProducts().size() != 1) {
			return Match.NONE;
		}
		int nParams = 0;
		if(context.hasParamForSBO(SBOList.MICHAELIS_CONST_FORW)) { ++nParams; }
		if(context.hasParamForSBO(SBOList.MICHAELIS_CONST_REV)) { ++nParams; }
		boolean hasOneCatalyst = context.getCatalysts().size() == 1;
		if(context.hasParamForSBO(SBOList.MAXIMAL_VELOCITY_FORW) || 
				(hasOneCatalyst && context.hasParamForSBO(SBOList.CATALYTIC_RATE_CONST_FORW))) { 
			++nParams; 
		}
		if(context.hasParamForSBO(SBOList.MAXIMAL_VELOCITY_REV) ||
				(hasOneCatalyst && context.hasParamForSBO(SBOList.CATALYTIC_RATE_CONST_REV))) { 
			++nParams; 
		}
		if(nParams == 4) { return Match.PERFECT; }
		else if(nParams == 3) { return Match.MOSTLY; }
		else if(nParams > 0) { return Match.SOME; }
		return Match.NONE;
	}

	public void addKinetics(KineticContext context) {
		try {
			ReactionStep reaction = context.getReaction();
			HMM_REVKinetics kinetics = new HMM_REVKinetics((SimpleReaction)reaction);
			NameScope modelScope = reaction.getModel().getNameScope();
			ModelParameter kMichaelisFwd = context.getParameter(SBOList.MICHAELIS_CONST_FORW);
			if(kMichaelisFwd != null) {
				KineticsParameter kmfParameter = kinetics.getKmFwdParameter();
				kmfParameter.setExpression(new Expression(kMichaelisFwd, modelScope)); 
				kmfParameter.setUnitDefinition(kMichaelisFwd.getUnitDefinition());
			}
			ModelParameter kcatf = context.getParameter(SBOList.CATALYTIC_RATE_CONST_FORW);
			if(kcatf != null && context.getCatalysts().size() == 1) {
				KineticsParameter vmaxfParameter = kinetics.getVmaxFwdParameter();
				Catalyst catalyst = context.getCatalysts().iterator().next();
				vmaxfParameter.setExpression(Expression.mult(new Expression(kcatf, modelScope),
						new Expression(catalyst.getSpeciesContext(), modelScope))); 
				//					vmaxParameter.setUnitDefinition(vMax.getUnitDefinition());
			} else {
				ModelParameter vMaxf = context.getParameter(SBOList.MAXIMAL_VELOCITY_FORW);
				if(vMaxf != null) {
					KineticsParameter vmaxfParameter = kinetics.getVmaxFwdParameter();
					vmaxfParameter.setExpression(new Expression(vMaxf, modelScope)); 
					vmaxfParameter.setUnitDefinition(vMaxf.getUnitDefinition());
				}
			}

			ModelParameter kMichaelisRev = context.getParameter(SBOList.MICHAELIS_CONST_REV);
			if(kMichaelisRev != null) {
				KineticsParameter kmrParameter = kinetics.getKmRevParameter();
				kmrParameter.setExpression(new Expression(kMichaelisRev, modelScope)); 
				kmrParameter.setUnitDefinition(kMichaelisRev.getUnitDefinition());
			}
			ModelParameter kcatr = context.getParameter(SBOList.CATALYTIC_RATE_CONST_FORW);
			if(kcatr != null && context.getCatalysts().size() == 1) {
				KineticsParameter vmaxrParameter = kinetics.getVmaxRevParameter();
				Catalyst catalyst = context.getCatalysts().iterator().next();
				vmaxrParameter.setExpression(Expression.mult(new Expression(kcatr, modelScope),
						new Expression(catalyst.getSpeciesContext(), modelScope))); 
				//					vmaxParameter.setUnitDefinition(vMax.getUnitDefinition());
			} else {
				ModelParameter vMaxr = context.getParameter(SBOList.MAXIMAL_VELOCITY_REV);
				if(vMaxr != null) {
					KineticsParameter vmaxrParameter = kinetics.getVmaxRevParameter();
					vmaxrParameter.setExpression(new Expression(vMaxr, modelScope)); 
					vmaxrParameter.setUnitDefinition(vMaxr.getUnitDefinition());
				}
			}
		} catch (ExpressionException e) {
			e.printStackTrace();
		}
	}

	
	
}
