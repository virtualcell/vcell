package cbit.vcell.model.gui;

import java.beans.PropertyVetoException;
import java.io.IOException;

import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.parser.Expression;
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
				Expression rateExp = origRS.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
				try {
					MassActionSolver.MassActionFunction maFunc = MassActionSolver.solveMassAction(rateExp, origRS);
					// set transformed reaction step
					transformedRS.setTransformType(TransformedReaction.TRANSFORMED_WITH_NOCHANGE);
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
				// fluxes which are described by GeneralPermeability
				if (origRS.getKinetics().getKineticsDescription().equals(KineticsDescription.GeneralPermeability)) 
				{
					try {
						Expression permeabilityExpr = origRS.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_Permeability).getExpression();
						permeabilityExpr = MassActionSolver.substituteParameters(permeabilityExpr, false);
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
				//fluxes which are described by general density function
				else if (origRS.getKinetics().getKineticsDescription().equals(KineticsDescription.General))
				{
					Expression rateExp = origRS.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
					try {
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

}
