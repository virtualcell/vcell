package cbit.vcell.model.gui;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Vector;

import org.vcell.util.BeanUtils;

import cbit.vcell.mapping.MappingException;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.stoch.FluxSolver;
import cbit.vcell.solver.stoch.MassActionSolver;
import cbit.vcell.solver.stoch.MassActionSolver.MassActionFunction;

public class TransformMassActions {
	private TransformedReaction[] transReactionSteps = null;
	private boolean[] isTransformable = null;

	public static class TransformedReaction {
		public final static int TRANSFORMED_WITH_NOCHANGE = 0;
		public final static int TRANSFORMED = 1;
		public final static int NOTRANSFORMABLE = 2;
		// public final static int NOTRANSFORMABLEFLUX = 3;
		public final static String Label_Ok = "Ok";
		public final static String Label_Transformed = "Able to transform.";
		public final static String Label_Failed = "Failed.";
		public final static String Label_expectedReacForm = "Looking for the reactin rate according to the form of Kf*(reactant1^n1)*(reactant2^n2)*...-Kr*(product1^m1)*(product2^m2)*...";
		public final static String Label_expectedFluxForm = "Looking for the flux density function according to the form of p1*SpeciesOutside-p2*SpeciesInside.";
		public final static String Label_FailedOtherReacLaw = "Automatic transformation can apply to reactions with general[uM/s] kinetic law only.";
		public final static String Label_FailedOtherFluxLaw = "Automatic transformation can apply to fluxes with general flux density only.";

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
			if (transReactionSteps[i].getTransformType() == TransformedReaction.NOTRANSFORMABLE) {
				isTransformable[i] = false;
			} else {
				isTransformable[i] = true;
			}
		}
	}

	public TransformedReaction transformOne(ReactionStep origRS)throws PropertyVetoException, IOException, ClassNotFoundException {
		TransformedReaction transformedRS = new TransformedReaction();
		
		if (origRS instanceof SimpleReaction) {
			if (origRS.getKinetics().getKineticsDescription().equals(KineticsDescription.MassAction)) 
			{
				Expression forwardRate = origRS.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_KForward).getExpression();
				Expression reverseRate = origRS.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_KReverse).getExpression();
				if ((forwardRate != null && forwardRate.hasSymbol(ReservedSymbol.TIME.getName()))
					 || (reverseRate != null && reverseRate.hasSymbol(ReservedSymbol.TIME.getName())))
				{
					transformedRS.setTransformType(TransformedReaction.NOTRANSFORMABLE);
					transformedRS.setTransformRemark(TransformedReaction.Label_Failed
									+ " Reaction: "
									+ origRS.getName()
									+ " has symbol \'t\' in rate constant. Propensity of a stochastic jump process should not be a functon of time.");
				} else {
					transformedRS.setTransformType(TransformedReaction.TRANSFORMED_WITH_NOCHANGE);
					transformedRS.setTransformRemark(TransformedReaction.Label_Ok);
					MassActionSolver.MassActionFunction maFunc = new MassActionSolver.MassActionFunction(forwardRate, reverseRate); 
					transformedRS.setMassActionFunction(maFunc);
				}
			}
			else if (origRS.getKinetics().getKineticsDescription().equals(KineticsDescription.General))
			{
				Expression rateExp = origRS.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
				try {
					rateExp = substitueKineticPara(rateExp, origRS, false);
					MassActionSolver.MassActionFunction maFunc = MassActionSolver.solveMassAction(rateExp, origRS);
				
					// set transformed reaction step
					transformedRS.setMassActionFunction(maFunc);
					transformedRS.setTransformType(TransformedReaction.TRANSFORMED);
					transformedRS.setTransformRemark(TransformedReaction.Label_Transformed);
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
//				if (origRS.getKinetics().getKineticsDescription().equals(KineticsDescription.General))
//				{
//					Expression rateExp = origRS.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
//					try {
//						rateExp = substitueKineticPara(rateExp, origRS, false);
//						FluxSolver.FluxFunction fluxFunc = FluxSolver.solveFlux(rateExp, (FluxReaction) origRS);
////						// change flux to simple Mass Action reaction and save
////						// it as transformed mass action, use this simple
////						// reaction to store forward
////						// and reverse rate so that these can be displayed in
////						// the table. Flux itself won't be physically changed.
////						SimpleReaction simpleRxn = new SimpleReaction(origRS.getStructure(), origRS.getName());
////						// Set the kinetics
////						MassActionKinetics maKinetics = new MassActionKinetics(simpleRxn);
////						maKinetics.getKineticsParameterFromRole(Kinetics.ROLE_KForward).setExpression(fluxFunc.getRateToInside());
////						maKinetics.getKineticsParameterFromRole(Kinetics.ROLE_KReverse).setExpression(fluxFunc.getRateToOutside());
////						simpleRxn.setKinetics(maKinetics);
////						transformedRS.setTransformedReaction(simpleRxn);
//						
//						// Flux won't be phsically changed here, it will be properly
//						// parsed only in stochastic math mapping.
//						transformedRS.setMassActionFunction(new MassActionSolver.MassActionFunction(fluxFunc.getRateToInside(), fluxFunc.getRateToOutside()));
//						transformedRS.setTransformType(TransformedReaction.TRANSFORMED_WITH_NOCHANGE);
//						transformedRS.setTransformRemark(TransformedReaction.Label_Ok);
//					} catch (Exception e) {
//						// Flux Solver failed to parse the rate expression
//						transformedRS.setMassActionFunction(new MassActionSolver.MassActionFunction());
//						transformedRS.setTransformType(TransformedReaction.NOTRANSFORMABLE);
//						transformedRS.setTransformRemark(TransformedReaction.Label_Failed + " " + e.getMessage());
//					}
//				} else // other fluxex which are not described by general density function
				if (origRS.getKinetics().getKineticsDescription().equals(KineticsDescription.GeneralPermeability)) 
				{
					try {
						Expression permeabilityExpr = origRS.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_Permeability).getExpression();
						permeabilityExpr = substitueKineticPara(permeabilityExpr, origRS, false);
						if (permeabilityExpr != null && permeabilityExpr.hasSymbol(ReservedSymbol.TIME.getName()))
						{
							transformedRS.setTransformType(TransformedReaction.NOTRANSFORMABLE);
							transformedRS.setTransformRemark(TransformedReaction.Label_Failed
											+ " Flux: "
											+ origRS.getName()
											+ " has symbol \'t\' in rate constant. Propensity of a stochastic jump process should not be a functon of time.");
						} else {
							transformedRS.setTransformType(TransformedReaction.TRANSFORMED_WITH_NOCHANGE);
							transformedRS.setTransformRemark(TransformedReaction.Label_Ok);
							MassActionSolver.MassActionFunction maFunc = new MassActionSolver.MassActionFunction(permeabilityExpr, permeabilityExpr); 
							transformedRS.setMassActionFunction(maFunc);
						}
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
						rateExp = substitueKineticPara(rateExp, origRS, false);
						MassActionSolver.MassActionFunction maFunc = MassActionSolver.solveMassAction(rateExp, origRS);
					
						// set transformed reaction step
						transformedRS.setMassActionFunction(maFunc);
						transformedRS.setTransformType(TransformedReaction.TRANSFORMED);
						transformedRS.setTransformRemark(TransformedReaction.Label_Transformed);
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

	private Expression substitueKineticPara(Expression exp, ReactionStep rs, boolean substituteConst) throws MappingException, ExpressionException 
	{
		Expression result = new Expression(exp);
		boolean bSubstituted = true;
		while (bSubstituted) {
			bSubstituted = false;
			String symbols[] = result.getSymbols();
			for (int k = 0; symbols != null && k < symbols.length; k++) {
				Kinetics.KineticsParameter kp = rs.getKinetics().getKineticsParameter(symbols[k]);
				if (kp != null) {
					try {
						Expression expKP = substitueKineticPara(kp.getExpression(), rs, true);
						if (!expKP.flatten().isNumeric() || substituteConst) {
							result.substituteInPlace(new Expression(symbols[k]), new Expression(kp.getExpression()));
							bSubstituted = true;
						}
					} catch (ExpressionException e1) {
						e1.printStackTrace();
						throw new ExpressionException(e1.getMessage());
					}
				}
			}

		}
		return result;
	}
}
