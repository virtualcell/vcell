/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping;
import net.sourceforge.interval.ia_math.RealInterval;
import cbit.vcell.constraints.*;
import cbit.vcell.model.*;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.math.*;
/**
 * Insert the type's description here.
 * Creation date: (12/29/2004 1:34:26 PM)
 * @author: Jim Schaff
 */
public class ApplicationConstraintsGenerator {
/**
 * Insert the method's description here.
 * Creation date: (6/26/01 8:25:55 AM)
 * @return cbit.vcell.constraints.ConstraintContainerImpl
 */
public static ConstraintContainerImpl fromApplication(SimulationContext simContext) {
	try {
		ConstraintContainerImpl ccImpl = new ConstraintContainerImpl();
		//====================
		// add physical limits
		//====================

		//
		// no negative concentrations
		//
		cbit.vcell.model.Model model = simContext.getModel();
		cbit.vcell.model.SpeciesContext speciesContexts[] = model.getSpeciesContexts();
		for (int i = 0; i < speciesContexts.length; i++){
			ccImpl.addSimpleBound(new SimpleBounds(speciesContexts[i].getName(),new RealInterval(0,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"non-negative concentration"));
		}
		for (int i = 0; i < speciesContexts.length; i++){
			SpeciesContextSpecParameter initParam = (simContext.getReactionContext().getSpeciesContextSpec(speciesContexts[i])).getInitialConditionParameter();
			if(initParam != null)
			{
				double initialValue = initParam.getExpression().evaluateConstant();
				ccImpl.addSimpleBound(new SimpleBounds(speciesContexts[i].getName(),new RealInterval(initialValue),AbstractConstraint.MODELING_ASSUMPTION,"specified \"initialCondition\""));
			}
		}
		//=========================
		// add modeling assumptions
		//=========================

		//
		// mass action forward and reverse rates should be non-negative
		//
		cbit.vcell.model.ReactionStep reactionSteps[] = model.getReactionSteps();
		for (int i = 0; i < reactionSteps.length; i++){
			Kinetics kinetics = reactionSteps[i].getKinetics();
			if (kinetics instanceof MassActionKinetics){
				
				Expression forwardRateConstraintExp = new Expression(((MassActionKinetics)kinetics).getForwardRateParameter().getExpression().infix()+">=0");
				forwardRateConstraintExp = getSteadyStateExpression(forwardRateConstraintExp);
				if (!forwardRateConstraintExp.compareEqual(new Expression(1.0))){
					ccImpl.addGeneralConstraint(new GeneralConstraint(
									forwardRateConstraintExp,
									AbstractConstraint.MODELING_ASSUMPTION,
									"non-negative forward rate"));
				}
				
				Expression reverseRateConstraintExp = new Expression(((MassActionKinetics)kinetics).getReverseRateParameter().getExpression().infix()+">=0");
				reverseRateConstraintExp = getSteadyStateExpression(reverseRateConstraintExp);
				if (!reverseRateConstraintExp.compareEqual(new Expression(1.0))){
					ccImpl.addGeneralConstraint(new GeneralConstraint(
									reverseRateConstraintExp,
									AbstractConstraint.MODELING_ASSUMPTION,
									"non-negative reverse rate"));
				}
			}
			KineticsParameter authoritativeParameter = kinetics.getAuthoritativeParameter();
			Expression kineticRateConstraintExp = new Expression(authoritativeParameter.getName()+"=="+authoritativeParameter.getExpression().infix());
			kineticRateConstraintExp = getSteadyStateExpression(kineticRateConstraintExp);
			if (!kineticRateConstraintExp.compareEqual(new Expression(1.0))){
				ccImpl.addGeneralConstraint(new GeneralConstraint(
									kineticRateConstraintExp,
									AbstractConstraint.MODELING_ASSUMPTION,
									"definition"));
			}
		}
		//
		// kineticParameters defined as a number should be included as additional "fuzzy" constraints
		// parameter values (should be substituted as +/- one order of magnitude).
		//
		// kineticParameters that are functions of other parameters are treated as constraint expressions
		//
		for (int i = 0; i < reactionSteps.length; i++){
			Kinetics kinetics = reactionSteps[i].getKinetics();
			Kinetics.KineticsParameter parameters[] = kinetics.getKineticsParameters();
			for (int j = 0; j < parameters.length; j++){
				Expression exp = parameters[j].getExpression();
				if (exp.getSymbols()==null || exp.getSymbols().length==0){
					//
					// apply parameter as simple bounds
					//
					try {
						double constantValue = exp.evaluateConstant();
						RealInterval interval = new RealInterval(constantValue);
						ccImpl.addSimpleBound(new SimpleBounds(parameters[j].getName(),interval,AbstractConstraint.MODELING_ASSUMPTION,"model value"));
					}catch (cbit.vcell.parser.ExpressionException e){
						System.out.println("error evaluating parameter "+parameters[j].getName()+" in reaction step "+reactionSteps[i].getName());
					}
				}else{
					Expression parameterDefinitionExp = new Expression(parameters[j].getName()+"=="+parameters[j].getExpression().infix());
					parameterDefinitionExp = getSteadyStateExpression(parameterDefinitionExp);
					if (!parameterDefinitionExp.compareEqual(new Expression(1.0))){
						ccImpl.addGeneralConstraint(new GeneralConstraint(
											parameterDefinitionExp,
											AbstractConstraint.MODELING_ASSUMPTION,
											"parameter definition"));
					}
				}
			}
		}

		ccImpl.addSimpleBound(new SimpleBounds(
									model.getFARADAY_CONSTANT().getName(),
									new RealInterval(model.getFARADAY_CONSTANT().getExpression().evaluateConstant()),
									AbstractConstraint.PHYSICAL_LIMIT,
									"Faraday's constant"));
		ccImpl.addSimpleBound(new SimpleBounds(
									model.getTEMPERATURE().getName(),
									new RealInterval(300),
									AbstractConstraint.PHYSICAL_LIMIT,
									"Absolute Temperature Kelvin"));
		ccImpl.addSimpleBound(new SimpleBounds(
									model.getGAS_CONSTANT().getName(),
									new RealInterval(model.getGAS_CONSTANT().getExpression().evaluateConstant()),
									AbstractConstraint.PHYSICAL_LIMIT,
									"ideal gas constant"));
		ccImpl.addSimpleBound(new SimpleBounds(
									model.getKMILLIVOLTS().getName(),
									new RealInterval(model.getKMILLIVOLTS().getExpression().evaluateConstant()),
									AbstractConstraint.PHYSICAL_LIMIT,
									"ideal gas constant"));
		//
		// add K_fluxs
		//
//		try {
//			simContext.setMathDescription(simContext.createNewMathMapping().getMathDescription());
//		}catch (Throwable e){
//			e.printStackTrace(System.out);
//			throw new RuntimeException("cannot create mathDescription");
//		}
//		MathDescription mathDesc = simContext.getMathDescription();
//
//		Enumeration<Variable> enumVars = mathDesc.getVariables();
//		while (enumVars.hasMoreElements()){
//			Variable var = enumVars.nextElement();
//			if (var.getName().startsWith(MathMapping.PARAMETER_K_FLUX_PREFIX) && var instanceof Function){
//				Expression kfluxExp = new Expression(((Function)var).getExpression());
//				kfluxExp.bindExpression(mathDesc);
//				kfluxExp = MathUtilities.substituteFunctions(kfluxExp,mathDesc);
//				kfluxExp = kfluxExp.flatten();
//				ccImpl.addSimpleBound(new SimpleBounds(var.getName(),new RealInterval(kfluxExp.evaluateConstant()),AbstractConstraint.MODELING_ASSUMPTION,"flux conversion factor"));
//			}
//		}

		return ccImpl;
	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
		return null;
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/19/2003 4:25:50 PM)
 * @return cbit.vcell.parser.Expression
 * @param exp cbit.vcell.parser.Expression
 */
private static Expression getSteadyStateExpression(Expression argExp) throws cbit.vcell.parser.ExpressionException {
	Expression exp = new Expression(argExp);
	exp.substituteInPlace(new Expression("t"),new Expression(0.0));
	exp.bindExpression(null);
	exp = exp.flatten();
	return exp;
}


/**
 * Insert the method's description here.
 * Creation date: (6/26/01 8:25:55 AM)
 * @return cbit.vcell.constraints.ConstraintContainerImpl
 */
public static ConstraintContainerImpl steadyStateFromApplication(SimulationContext simContext, double tolerance) {
	try {
		ConstraintContainerImpl ccImpl = new ConstraintContainerImpl();
		//====================
		// add physical limits
		//====================

		//
		// no negative concentrations
		//
		cbit.vcell.model.Model model = simContext.getModel();
		cbit.vcell.model.SpeciesContext speciesContexts[] = model.getSpeciesContexts();
		for (int i = 0; i < speciesContexts.length; i++){
			ccImpl.addSimpleBound(new SimpleBounds(speciesContexts[i].getName(),new RealInterval(0,Double.POSITIVE_INFINITY),AbstractConstraint.PHYSICAL_LIMIT,"non-negative concentration"));
		}
		for (int i = 0; i < speciesContexts.length; i++){
			SpeciesContextSpecParameter initParam = (simContext.getReactionContext().getSpeciesContextSpec(speciesContexts[i])).getInitialConditionParameter();
			if(initParam != null)
			{
				double initialValue = initParam.getExpression().evaluateConstant();
				double lowInitialValue = Math.min(initialValue/tolerance,initialValue*tolerance);
				double highInitialValue = Math.max(initialValue/tolerance,initialValue*tolerance);
				ccImpl.addSimpleBound(new SimpleBounds(speciesContexts[i].getName(),new RealInterval(lowInitialValue,highInitialValue),AbstractConstraint.MODELING_ASSUMPTION,"close to specified \"initialCondition\""));
			}
		}
		//=========================
		// add modeling assumptions
		//=========================

		//
		// mass action forward and reverse rates should be non-negative
		//
		cbit.vcell.model.ReactionStep reactionSteps[] = model.getReactionSteps();
		for (int i = 0; i < reactionSteps.length; i++){
			Kinetics kinetics = reactionSteps[i].getKinetics();
			if (kinetics instanceof MassActionKinetics){
				
				Expression forwardRateConstraintExp = new Expression(((MassActionKinetics)kinetics).getForwardRateParameter().getExpression().infix()+">=0");
				forwardRateConstraintExp = getSteadyStateExpression(forwardRateConstraintExp);
				if (!forwardRateConstraintExp.compareEqual(new Expression(1.0))){
					ccImpl.addGeneralConstraint(new GeneralConstraint(
									forwardRateConstraintExp,
									AbstractConstraint.MODELING_ASSUMPTION,
									"non-negative forward rate"));
				}
				
				Expression reverseRateConstraintExp = new Expression(((MassActionKinetics)kinetics).getReverseRateParameter().getExpression().infix()+">=0");
				reverseRateConstraintExp = getSteadyStateExpression(reverseRateConstraintExp);
				if (!reverseRateConstraintExp.compareEqual(new Expression(1.0))){
					ccImpl.addGeneralConstraint(new GeneralConstraint(
									reverseRateConstraintExp,
									AbstractConstraint.MODELING_ASSUMPTION,
									"non-negative reverse rate"));
				}
			}
			KineticsParameter authoritativeParameter = kinetics.getAuthoritativeParameter();
			Expression kineticRateConstraintExp = new Expression(authoritativeParameter.getName()+"=="+authoritativeParameter.getExpression().infix());
			kineticRateConstraintExp = getSteadyStateExpression(kineticRateConstraintExp);
			if (!kineticRateConstraintExp.compareEqual(new Expression(1.0))){
				ccImpl.addGeneralConstraint(new GeneralConstraint(
									kineticRateConstraintExp,
									AbstractConstraint.MODELING_ASSUMPTION,
									"definition"));
			}
		}
		//
		// rates should be zero
		//
		try {
			simContext.setMathDescription(simContext.createNewMathMapping().getMathDescription());
		}catch (Throwable e){
			e.printStackTrace(System.out);
			throw new RuntimeException("cannot create mathDescription");
		}
		MathDescription mathDesc = simContext.getMathDescription();
		if (mathDesc.getGeometry().getDimension()>0){
			throw new RuntimeException("spatial simulations not yet supported");
		}
		CompartmentSubDomain subDomain = (CompartmentSubDomain)mathDesc.getSubDomains().nextElement();
		java.util.Enumeration<Equation> enumEquations = subDomain.getEquations();
		while (enumEquations.hasMoreElements()){
			Equation equation = (Equation)enumEquations.nextElement();
			Expression rateConstraintExp = new Expression(equation.getRateExpression().infix()+"==0");
			rateConstraintExp = getSteadyStateExpression(rateConstraintExp);
			if (!rateConstraintExp.compareEqual(new Expression(1.0))){
				// not a trivial constraint (always true)
				ccImpl.addGeneralConstraint(new GeneralConstraint(
									rateConstraintExp,
									AbstractConstraint.PHYSICAL_LIMIT,
									"definition of steady state"));
			}
		}

		//
		// kineticParameters defined as a number should be included as additional "fuzzy" constraints
		// parameter values (should be substituted as +/- one order of two).
		//
		// kineticParameters that are functions of other parameters are treated as constraint expressions
		//
		for (int i = 0; i < reactionSteps.length; i++){
			Kinetics kinetics = reactionSteps[i].getKinetics();
			Kinetics.KineticsParameter parameters[] = kinetics.getKineticsParameters();
			for (int j = 0; j < parameters.length; j++){
				Expression exp = parameters[j].getExpression();
				if (exp.getSymbols()==null || exp.getSymbols().length==0){
					//
					// apply parameter as simple bounds
					//
					try {
						double constantValue = exp.evaluateConstant();
						double lowValue = Math.min(constantValue/tolerance, constantValue*tolerance);
						double highValue = Math.max(constantValue/tolerance, constantValue*tolerance);
						RealInterval interval = new RealInterval(lowValue,highValue);
						ccImpl.addSimpleBound(new SimpleBounds(parameters[j].getName(),interval,AbstractConstraint.MODELING_ASSUMPTION,"parameter close to model default"));
					}catch (cbit.vcell.parser.ExpressionException e){
						System.out.println("error evaluating parameter "+parameters[j].getName()+" in reaction step "+reactionSteps[i].getName());
					}
				}else{
					Expression parameterDefinitionExp = new Expression(parameters[j].getName()+"=="+parameters[j].getExpression().infix());
					ccImpl.addGeneralConstraint(new GeneralConstraint(
										getSteadyStateExpression(parameterDefinitionExp),
										AbstractConstraint.MODELING_ASSUMPTION,
										"parameter definition"));
				}
			}
		}

		ccImpl.addSimpleBound(new SimpleBounds(
									model.getFARADAY_CONSTANT().getName(),
									new RealInterval(model.getFARADAY_CONSTANT().getExpression().evaluateConstant()),
									AbstractConstraint.PHYSICAL_LIMIT,
									"Faraday's constant"));
		ccImpl.addSimpleBound(new SimpleBounds(
									model.getTEMPERATURE().getName(),
									new RealInterval(300),
									AbstractConstraint.PHYSICAL_LIMIT,
									"Absolute Temperature Kelvin"));
		ccImpl.addSimpleBound(new SimpleBounds(
									model.getGAS_CONSTANT().getName(),
									new RealInterval(model.getGAS_CONSTANT().getExpression().evaluateConstant()),
									AbstractConstraint.PHYSICAL_LIMIT,
									"ideal gas constant"));
		ccImpl.addSimpleBound(new SimpleBounds(
									model.getKMILLIVOLTS().getName(),
									new RealInterval(model.getKMILLIVOLTS().getExpression().evaluateConstant()),
									AbstractConstraint.PHYSICAL_LIMIT,
									"ideal gas constant"));
		//
		// add K_fluxs
		//
		java.util.Enumeration<Variable> enumVars = mathDesc.getVariables();
		while (enumVars.hasMoreElements()){
			Variable var = (Variable)enumVars.nextElement();
			if (var.getName().startsWith("Kflux_") && var instanceof Function){
				Expression kfluxExp = new Expression(((Function)var).getExpression());
				kfluxExp.bindExpression(mathDesc);
				kfluxExp = MathUtilities.substituteFunctions(kfluxExp,mathDesc);
				kfluxExp = kfluxExp.flatten();
				ccImpl.addSimpleBound(new SimpleBounds(var.getName(),new RealInterval(kfluxExp.evaluateConstant()),AbstractConstraint.MODELING_ASSUMPTION,"flux conversion factor"));
			}
		}
		
		return ccImpl;
	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
		return null;
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		return null;
	}
}
}
