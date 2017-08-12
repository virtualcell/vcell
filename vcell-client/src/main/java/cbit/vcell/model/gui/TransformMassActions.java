/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model.gui;

import java.beans.PropertyVetoException;
import java.io.IOException;

import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.MassActionSolver;
import cbit.vcell.model.MassActionSolver.MassActionFunction;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.parser.Expression;

public class TransformMassActions {
	private TransformedReaction[] transReactionSteps = null;
	private boolean[] isTransformable = null;

	public static class TransformedReaction {
		public final static int TRANSFORMABLE_WITH_NOCHANGE = 0;
		public final static int TRANSFORMABLE = 1;
		public final static int NOTRANSFORMABLE = 2;
		public final static int NOTRANSFORMABLE_STOCHCAPABLE = 3;
		public final static String Label_Ok = "Ok";
		public final static String Label_Transformable = "Can be transformed to mass action.";
		public final static String Label_StochForm_NotTransformable = "Stochastic capable, but can NOT be transformed to mass action.";
		public final static String Label_Failed = "Failed.";
		public final static String Label_expectedReacForm = "Looking for the reaction rate according to the form of Kf*(reactant1^n1)*(reactant2^n2)*...-Kr*(product1^m1)*(product2^m2)*...";
		public final static String Label_expectedFluxForm = "Looking for the flux density function according to the form of p1*SpeciesOutside-p2*SpeciesInside.";
		public final static String Label_FailedOtherReacLaw = "Only reactions with general kinetic laws may be transformed.";
		public final static String Label_FailedOtherFluxLaw = "Only fluxes with general flux density/permeability may be transformed.";

		private String transformRemark = "";
		private int transformType = 0;
		private MassActionFunction massActionFunc = null;

		public TransformedReaction() {
		}

		public void setTransformType(int type) {
			transformType = type;
		}

		public int getTransformType() {
			return transformType;
		}

		public String getTransformRemark() {
			return transformRemark;
		}

		public MassActionFunction getMassActionFunction() {
			return massActionFunc;
		}

		public void setMassActionFunction(MassActionFunction maFunc) {
			massActionFunc = maFunc;
		}

		public void setTransformRemark(String transformRemark) {
			this.transformRemark = transformRemark;
		}
	}// end of static class TransformedReaction

	public TransformMassActions() {
	}

	public void transformReactions(ReactionStep[] origReactions)
			throws Exception {
		transReactionSteps = new TransformedReaction[origReactions.length];
		isTransformable = new boolean[origReactions.length];

		for (int i = 0; i < origReactions.length; i++) {
			transReactionSteps[i] = transformOne(origReactions[i]);
			if (transReactionSteps[i].getTransformType() == TransformedReaction.TRANSFORMABLE) {
				isTransformable[i] = true;
			} else {
				isTransformable[i] = false;
			}
		}
	}

	public TransformedReaction transformOne(ReactionStep origRS)throws PropertyVetoException, IOException, ClassNotFoundException {
		TransformedReaction transformedRS = new TransformedReaction();
		
		if (origRS instanceof SimpleReaction) {
			//we separate mass action and general law, because if it passes, mass action uses label 'ok' and general uses label 'stochastic capable'
			if (origRS.getKinetics().getKineticsDescription().equals(KineticsDescription.MassAction)) 
			{
				Expression rateExp = origRS.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
				try {
					Parameter forwardRateParameter = origRS.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_KForward);
					Parameter reverseRateParameter = origRS.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_KReverse);
					MassActionSolver.MassActionFunction maFunc = MassActionSolver.solveMassAction(forwardRateParameter, reverseRateParameter, rateExp, origRS);
					// set transformed reaction step
					transformedRS.setTransformType(TransformedReaction.TRANSFORMABLE_WITH_NOCHANGE);
					transformedRS.setTransformRemark(TransformedReaction.Label_Ok);
					transformedRS.setMassActionFunction(maFunc);
				} catch (Exception e) {
					// Mass Action Solver failed to parse the rate expression
					transformedRS.setMassActionFunction(new MassActionSolver.MassActionFunction());
					transformedRS.setTransformType(TransformedReaction.NOTRANSFORMABLE);
					transformedRS.setTransformRemark(TransformedReaction.Label_Failed + " " + e.getMessage());
				}
			}
			else if (origRS.getKinetics().getKineticsDescription().equals(KineticsDescription.General))
			{
				Expression rateExp = origRS.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
				try {
					Parameter forwardRateParameter = null;
					Parameter reverseRateParameter = null;
					MassActionSolver.MassActionFunction maFunc = MassActionSolver.solveMassAction(forwardRateParameter, reverseRateParameter, rateExp, origRS);
				
					// set transformed reaction step
					transformedRS.setMassActionFunction(maFunc);
					// if number of reactant is 0, or number of product is 0, we can not transform it to mass action law
					if(((SimpleReaction)origRS).getNumReactants() == 0 || ((SimpleReaction)origRS).getNumProducts() == 0)
					{
						transformedRS.setTransformType(TransformedReaction.NOTRANSFORMABLE_STOCHCAPABLE);
						transformedRS.setTransformRemark(TransformedReaction.Label_StochForm_NotTransformable);
					}
					else
					{
						transformedRS.setTransformType(TransformedReaction.TRANSFORMABLE);
						transformedRS.setTransformRemark(TransformedReaction.Label_Transformable);
					}
				} catch (Exception e) {
					// Mass Action Solver failed to parse the rate expression
					transformedRS.setMassActionFunction(new MassActionSolver.MassActionFunction());
					transformedRS.setTransformType(TransformedReaction.NOTRANSFORMABLE);
					transformedRS.setTransformRemark(TransformedReaction.Label_Failed + " " + e.getMessage());
				}
			}
			else // other kinetic rate laws other than MassAction and General
			{
				transformedRS.setMassActionFunction(new MassActionSolver.MassActionFunction());
				transformedRS.setTransformType(TransformedReaction.NOTRANSFORMABLE);
				transformedRS.setTransformRemark(TransformedReaction.Label_Failed+TransformedReaction.Label_FailedOtherReacLaw);
			}
		} else // flux
		{
			if (origRS instanceof FluxReaction) {
				//fluxes which are described by general density function/permeability
				if (origRS.getKinetics().getKineticsDescription().equals(KineticsDescription.General) || 
						 origRS.getKinetics().getKineticsDescription().equals(KineticsDescription.GeneralPermeability))
				{
					Expression rateExp = origRS.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
					try {
						// forward and reverse rate parameters may be null
						Parameter forwardRateParameter = null;
						Parameter reverseRateParameter = null;
						if (origRS.getKinetics().getKineticsDescription().equals(KineticsDescription.GeneralPermeability)){
							forwardRateParameter = origRS.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_Permeability);
							reverseRateParameter = origRS.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_Permeability);
						}
						MassActionSolver.MassActionFunction maFunc = MassActionSolver.solveMassAction(forwardRateParameter, reverseRateParameter, rateExp, origRS);
					
						// set transformed reaction step
						transformedRS.setMassActionFunction(maFunc);
						transformedRS.setTransformType(TransformedReaction.NOTRANSFORMABLE_STOCHCAPABLE);
						transformedRS.setTransformRemark(TransformedReaction.Label_StochForm_NotTransformable);
					} catch (Exception e) {
						// Mass Action Solver failed to parse the rate expression
						transformedRS.setMassActionFunction(new MassActionSolver.MassActionFunction());
						transformedRS.setTransformType(TransformedReaction.NOTRANSFORMABLE);
						transformedRS.setTransformRemark(TransformedReaction.Label_Failed + " " + e.getMessage());
					}
				} else // other fluxes which are not described by general density function or general permeability
				{
					transformedRS.setMassActionFunction(new MassActionSolver.MassActionFunction());
					transformedRS.setTransformType(TransformedReaction.NOTRANSFORMABLE);
					transformedRS.setTransformRemark(TransformedReaction.Label_Failed+TransformedReaction.Label_FailedOtherFluxLaw);
				}
			}
		}
		return transformedRS;
	}

	public TransformedReaction[] getTransformedReactionSteps() {
		return transReactionSteps;
	}

	public boolean[] getIsTransformable() {
		return isTransformable;
	}

}
