package cbit.vcell.model.gui;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Vector;

import cbit.util.BeanUtils;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.stoch.FluxSolver;
import cbit.vcell.solver.stoch.MassActionSolver;

public class TransformMassActions 
{
	private TransformedReaction[] transReactionSteps = null;
	private boolean[] isTransformable = null;
		
	public static class TransformedReaction
	{
		public final static int TRANSFORMED_WITH_NOCHANGE = 0;
		public final static int TRANSFORMED = 1;
		public final static int NOTRANSFORMABLEREAC = 2;
		public final static int NOTRANSFORMABLEFLUX = 3;
		public final static String Label_Ok = "Ok";
		public final static String Label_Transformed = "Able to transform";
		public final static String Label_FailedReac = "Failed. Looking for the reactin rate according to the form of Kf*(reactant1^n1)*(reactant2^n2)*...-Kr*(product1^m1)*(product2^m2)*...";
		public final static String Label_FailedFlux = "Failed. Looking for the flux density function according to the form of p1*SpeciesOutside-p2*SpeciesInside";
		
		private String[] transformRemark = new String[]{Label_Ok, Label_Transformed, Label_FailedReac, Label_FailedFlux};
		private int transformType = 0;
		private ReactionStep transformedRS = null;
		public TransformedReaction()
		{
		}
		public void setTransformType(int type)
		{
			transformType = type;
		}
		public int getTransformType()
		{
			return transformType;
		}
		public String getTransformRemark()
		{
			return transformRemark[getTransformType()];
		}
		public ReactionStep getTransformedReaction()
		{
			return transformedRS;
		}
		public void setTransformedReaction(ReactionStep rs)
		{
			transformedRS = rs;
		}
	}// end of static class TransformedReaction
	
	
	public TransformMassActions()
	{
	}
	public void transformReactions(ReactionStep[] origReactions) throws Exception
	{
		transReactionSteps = new TransformedReaction[origReactions.length];
		isTransformable = new boolean[origReactions.length];
		
		for(int i=0; i< origReactions.length; i++)
		{
			transReactionSteps[i] = transformOne(origReactions[i]);
			if(transReactionSteps[i].getTransformType() == TransformedReaction.NOTRANSFORMABLEREAC || transReactionSteps[i].getTransformType() == TransformedReaction.NOTRANSFORMABLEFLUX)
			{
				isTransformable[i] = false;
			}
			else
			{
				isTransformable[i] = true;
			}
		}
	}
	
	public TransformedReaction transformOne(ReactionStep origRS) throws PropertyVetoException, IOException, ClassNotFoundException
	{
		TransformedReaction transformedRS = new TransformedReaction();
		ReactionStep clonedRS = (ReactionStep)BeanUtils.cloneSerializable(origRS);
					
		if(origRS instanceof SimpleReaction)
		{
			if(origRS.getKinetics().getKineticsDescription().equals(KineticsDescription.MassAction))
			{
				transformedRS.setTransformedReaction(origRS);
				transformedRS.setTransformType(TransformedReaction.TRANSFORMED_WITH_NOCHANGE);
			}
			else if(origRS.getKinetics().getKineticsDescription().equals(KineticsDescription.General))
			{
				Expression rateExp = origRS.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
				try{
					rateExp = substitueKineticPara(rateExp, origRS, false);
					MassActionSolver.MassActionFunction maFunc = MassActionSolver.solveMassAction(rateExp, origRS);
					//put all kinetic parameters together into array newKps
					Vector<Kinetics.KineticsParameter> newKps = new Vector<Kinetics.KineticsParameter>();
					//get original kinetic parameters which are not current density and reaction rate.
					Vector<Kinetics.KineticsParameter> origKps = new Vector<Kinetics.KineticsParameter>();
					Kinetics.KineticsParameter[] Kps = origRS.getKinetics().getKineticsParameters();
					for(int i=0; i<Kps.length; i++)
					{
						if(!(Kps[i].getRole() == Kinetics.ROLE_CurrentDensity || Kps[i].getRole() == Kinetics.ROLE_ReactionRate))
						{
							origKps.add(Kps[i]);
						}
					}
					//create mass action kinetics for the cloned reaction				
					MassActionKinetics maKinetics= new MassActionKinetics(clonedRS);
					maKinetics.getKineticsParameterFromRole(Kinetics.ROLE_KForward).setExpression(maFunc.getForwardRate());
					maKinetics.getKineticsParameterFromRole(Kinetics.ROLE_KReverse).setExpression(maFunc.getReverseRate());
					//copy rate, currentdensity, Kf and Kr to the new Kinetic Parameter list first(to make sure that paramters are in the correct order in the newly created Mass Action Kinetics)
					for(int i=0; i<maKinetics.getKineticsParameters().length; i++)
					{
						newKps.add(maKinetics.getKineticsParameters(i));
					}
					//copy other kinetic parameters from previos kinetics
					for(int i=0; i<origKps.size(); i++)
					{
						newKps.add(origKps.elementAt(i));
					}
					//add parameters to mass action kinetics
					KineticsParameter[] newParameters = new KineticsParameter[newKps.size()];
					newParameters = (KineticsParameter[])newKps.toArray(newParameters);	
					maKinetics.addKineticsParameters(newParameters);
					//set transformed reactin step					
					transformedRS.setTransformedReaction(clonedRS);
					transformedRS.setTransformType(TransformedReaction.TRANSFORMED);
				}catch(Exception e)
				{
					//Mass Action Solver failed to parse the rate expression
					transformedRS.setTransformedReaction(origRS);
					transformedRS.setTransformType(TransformedReaction.NOTRANSFORMABLEREAC);
				}
			}
			else // other kinetic rate laws other than MassAction and General
			{
				transformedRS.setTransformedReaction(origRS);
				transformedRS.setTransformType(TransformedReaction.NOTRANSFORMABLEREAC);
			}
		}
		else //flux
		{
			if(origRS instanceof FluxReaction)
			{
				if(origRS.getKinetics().getKineticsDescription().equals(KineticsDescription.General))
				{
					Expression rateExp = origRS.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
					try{
						rateExp = substitueKineticPara(rateExp, origRS, false);
						FluxSolver.FluxFunction fluxFunc = FluxSolver.solveFlux(rateExp, origRS);
						//change flux to simple Mass Action reaction and save it as transformed mass action, use this simple reaction to store forward
						//and reverse rate so that these can be displayed in the table. Flux itself won't be physically changed.
						SimpleReaction simpleRxn = new SimpleReaction(origRS.getStructure(), origRS.getName());
						// Set the kinetics
						MassActionKinetics maKinetics = new MassActionKinetics(simpleRxn);
						maKinetics.getKineticsParameterFromRole(Kinetics.ROLE_KForward).setExpression(fluxFunc.getRateToInside());
						maKinetics.getKineticsParameterFromRole(Kinetics.ROLE_KReverse).setExpression(fluxFunc.getRateToOutside());
						simpleRxn.setKinetics(maKinetics);
						transformedRS.setTransformedReaction(simpleRxn);
						//we won't phsically change it now, it will be properly parsed in stoch math mapping.
						transformedRS.setTransformType(TransformedReaction.TRANSFORMED_WITH_NOCHANGE);
					}catch(Exception e)
					{
//						Flux Solver failed to parse the rate expression
						transformedRS.setTransformedReaction(origRS);
						transformedRS.setTransformType(TransformedReaction.NOTRANSFORMABLEFLUX);
//						throw new MathException("Failed to transform flux " + origRS.getName() + ". Looking for the flux density function according to the form of p1*SpeciesOutside-p2*SpeciesInside.");
					}
				}
				else //other fluxex which are not described by general density function
				{
					transformedRS.setTransformedReaction(origRS);
					transformedRS.setTransformType(TransformedReaction.NOTRANSFORMABLEFLUX);
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
		while(bSubstituted)
		{
			bSubstituted = false;
			String symbols[] = result.getSymbols();
			for (int k = 0;symbols!=null && k < symbols.length; k++){
				Kinetics.KineticsParameter kp = rs.getKinetics().getKineticsParameter(symbols[k]);
				if (kp != null)
				{
					try{
						Expression expKP = substitueKineticPara(kp.getExpression(), rs, true);
						if(!expKP.flatten().isNumeric()||substituteConst)
						{
							result.substituteInPlace(new Expression(symbols[k]), new Expression(kp.getExpression()));
							bSubstituted = true;
						}
					}catch(ExpressionException e1){
						e1.printStackTrace();
						throw new ExpressionException(e1.getMessage());
					}
				}
			}
			
		}
		return result;
	}
}
