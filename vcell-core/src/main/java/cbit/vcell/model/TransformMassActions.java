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

import java.awt.Component;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Vector;

import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.MassActionSolver.MassActionFunction;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;

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

	public static void applyTransformAll(Model model) throws Exception
	{
		TransformMassActions instance = new TransformMassActions();
		ReactionStep[] origReactions = model.getReactionSteps();
		instance.transformReactions(origReactions);
		//isTransformable and TransformedREactions are stored according to the indexes in model reaction steps.
		boolean[] isTransformable = instance.getIsTransformable();
		TransformMassActions.TransformedReaction[] trs = instance.getTransformedReactionSteps();
		//set transformed Mass Action kinetics to model reactions
		for(int i=0; i< origReactions.length; i++)
		{
			if(isTransformable[i])
			{
				transformKinetics(origReactions, trs, i);
			}
		}
	}

	public static void transformKinetics(ReactionStep[] origReactions, TransformMassActions.TransformedReaction[] trs,
			int i) throws ExpressionException, PropertyVetoException, ExpressionBindingException {
		// for simple reaction, we replace the original kinetics with MassActionKinetics if it wasn't MassActionKinetics
		if(origReactions[i] instanceof SimpleReaction) 
		{
			if(!(origReactions[i].getKinetics() instanceof MassActionKinetics))
			{
				//***Below we will physically change the simple reaction*** 
				
				//put all kinetic parameters together into array newKps
				Vector<Kinetics.KineticsParameter> newKps = new Vector<Kinetics.KineticsParameter>();
				
				//get original kinetic parameters which are not current density and reaction rate.
				//those parameters are basically the symbols in rate expression.
				Vector<Kinetics.KineticsParameter> origKps = new Vector<Kinetics.KineticsParameter>();
				Kinetics.KineticsParameter[] Kps = origReactions[i].getKinetics().getKineticsParameters();
				for (int j = 0; j < Kps.length; j++) {
					if (!(Kps[j].getRole() == Kinetics.ROLE_CurrentDensity || Kps[j].getRole() == Kinetics.ROLE_ReactionRate))
					{
						origKps.add(Kps[j]);
					}
				}
				
				// create mass action kinetics for the original reaction step
				MassActionKinetics maKinetics = new MassActionKinetics(origReactions[i]);
				maKinetics.getKineticsParameterFromRole(Kinetics.ROLE_KForward).setExpression(trs[i].getMassActionFunction().getForwardRate());
				maKinetics.getKineticsParameterFromRole(Kinetics.ROLE_KReverse).setExpression(trs[i].getMassActionFunction().getReverseRate());
				
				// copy rate, currentdensity, Kf and Kr to the new Kinetic
				// Parameter list first(to make sure that paramters are in
				// the correct order in the newly created Mass Action
				// Kinetics)
				for (int j = 0; j < maKinetics.getKineticsParameters().length; j++) {
					newKps.add(maKinetics.getKineticsParameters(j));
				}
				// copy other kinetic parameters from original kinetics
				for (int j = 0; j < origKps.size(); j++) {
					newKps.add(origKps.elementAt(j));
				}
				// add parameters to mass action kinetics
				KineticsParameter[] newParameters = new KineticsParameter[newKps.size()];
				newParameters = (KineticsParameter[]) newKps.toArray(newParameters);
				maKinetics.addKineticsParameters(newParameters);
										
				//after adding all the parameters, we bind the forward/reverse rate expression with symbol table (the reaction step itself)
				origReactions[i].getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_KForward).getExpression().bindExpression(origReactions[i]);
				origReactions[i].getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_KReverse).getExpression().bindExpression(origReactions[i]);
			}
		}
	}

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
	
	public void initializeTransformation(ReactionStep[] origReactions) {
		transReactionSteps = new TransformedReaction[origReactions.length];
		isTransformable = new boolean[origReactions.length];
	}

	public TransformedReaction transformOne(ReactionStep origRS)throws PropertyVetoException, IOException {
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
