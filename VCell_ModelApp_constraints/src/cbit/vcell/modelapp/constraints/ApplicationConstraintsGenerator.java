package cbit.vcell.modelapp.constraints;

import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;

import net.sourceforge.interval.ia_math.RealInterval;
import cbit.vcell.constraints.AbstractConstraint;
import cbit.vcell.constraints.ConstraintContainerImpl;
import cbit.vcell.constraints.GeneralConstraint;
import cbit.vcell.constraints.SimpleBounds;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Equation;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.Variable;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.modelapp.SimulationContext;
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
			double initialValue = (simContext.getReactionContext().getSpeciesContextSpec(speciesContexts[i])).getInitialConditionParameter().getExpression().evaluateConstant();
			ccImpl.addSimpleBound(new SimpleBounds(speciesContexts[i].getName(),new RealInterval(initialValue),AbstractConstraint.MODELING_ASSUMPTION,"specified \"initialCondition\""));
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
				
				IExpression forwardRateConstraintExp = ExpressionFactory.createExpression(((MassActionKinetics)kinetics).getForwardRateParameter().getExpression().infix()+">=0");
				forwardRateConstraintExp = getSteadyStateExpression(forwardRateConstraintExp);
				if (!forwardRateConstraintExp.compareEqual(ExpressionFactory.createExpression(1.0))){
					ccImpl.addGeneralConstraint(new GeneralConstraint(
									forwardRateConstraintExp,
									AbstractConstraint.MODELING_ASSUMPTION,
									"non-negative forward rate"));
				}
				
				IExpression reverseRateConstraintExp = ExpressionFactory.createExpression(((MassActionKinetics)kinetics).getReverseRateParameter().getExpression().infix()+">=0");
				reverseRateConstraintExp = getSteadyStateExpression(reverseRateConstraintExp);
				if (!reverseRateConstraintExp.compareEqual(ExpressionFactory.createExpression(1.0))){
					ccImpl.addGeneralConstraint(new GeneralConstraint(
									reverseRateConstraintExp,
									AbstractConstraint.MODELING_ASSUMPTION,
									"non-negative reverse rate"));
				}
			}
			IExpression kineticRateConstraintExp = ExpressionFactory.createExpression(kinetics.getRateParameter().getName()+"=="+kinetics.getRateParameter().getExpression().infix());
			kineticRateConstraintExp = getSteadyStateExpression(kineticRateConstraintExp);
			if (!kineticRateConstraintExp.compareEqual(ExpressionFactory.createExpression(1.0))){
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
				IExpression exp = parameters[j].getExpression();
				if (exp.getSymbols()==null || exp.getSymbols().length==0){
					//
					// apply parameter as simple bounds
					//
					try {
						double constantValue = exp.evaluateConstant();
						RealInterval interval = new RealInterval(constantValue);
						ccImpl.addSimpleBound(new SimpleBounds(parameters[j].getName(),interval,AbstractConstraint.MODELING_ASSUMPTION,"model value"));
					}catch (org.vcell.expression.ExpressionException e){
						System.out.println("error evaluating parameter "+parameters[j].getName()+" in reaction step "+reactionSteps[i].getName());
					}
				}else{
					IExpression parameterDefinitionExp = ExpressionFactory.createExpression(parameters[j].getName()+"=="+parameters[j].getExpression().infix());
					parameterDefinitionExp = getSteadyStateExpression(parameterDefinitionExp);
					if (!parameterDefinitionExp.compareEqual(ExpressionFactory.createExpression(1.0))){
						ccImpl.addGeneralConstraint(new GeneralConstraint(
											parameterDefinitionExp,
											AbstractConstraint.MODELING_ASSUMPTION,
											"parameter definition"));
					}
				}
			}
		}

		ccImpl.addSimpleBound(new SimpleBounds(
									cbit.vcell.model.ReservedSymbol.FARADAY_CONSTANT.getName(),
									new RealInterval(cbit.vcell.model.ReservedSymbol.FARADAY_CONSTANT.getExpression().evaluateConstant()),
									AbstractConstraint.PHYSICAL_LIMIT,
									"Faraday's constant"));
		ccImpl.addSimpleBound(new SimpleBounds(
									cbit.vcell.model.ReservedSymbol.TEMPERATURE.getName(),
									new RealInterval(300),
									AbstractConstraint.PHYSICAL_LIMIT,
									"Absolute Temperature Kelvin"));
		ccImpl.addSimpleBound(new SimpleBounds(
									cbit.vcell.model.ReservedSymbol.GAS_CONSTANT.getName(),
									new RealInterval(cbit.vcell.model.ReservedSymbol.GAS_CONSTANT.getExpression().evaluateConstant()),
									AbstractConstraint.PHYSICAL_LIMIT,
									"ideal gas constant"));
		ccImpl.addSimpleBound(new SimpleBounds(
									cbit.vcell.model.ReservedSymbol.KMILLIVOLTS.getName(),
									new RealInterval(cbit.vcell.model.ReservedSymbol.KMILLIVOLTS.getExpression().evaluateConstant()),
									AbstractConstraint.PHYSICAL_LIMIT,
									"ideal gas constant"));
		ccImpl.addSimpleBound(new SimpleBounds(
									cbit.vcell.model.ReservedSymbol.KMOLE.getName(),
									new RealInterval(cbit.vcell.model.ReservedSymbol.KMOLE.getExpression().evaluateConstant()),
									AbstractConstraint.PHYSICAL_LIMIT,
									"conversion factor"));
		//
		// add K_fluxs
		//
		try {
			simContext.setMathDescription((new cbit.vcell.mapping.MathMapping(simContext)).getMathDescription());
		}catch (Throwable e){
			e.printStackTrace(System.out);
			throw new RuntimeException("cannot create mathDescription");
		}
		MathDescription mathDesc = simContext.getMathDescription();

		java.util.Enumeration enumVars = mathDesc.getVariables();
		while (enumVars.hasMoreElements()){
			Variable var = (Variable)enumVars.nextElement();
			if (var.getName().startsWith("Kflux_") && var instanceof Function){
				IExpression kfluxExp = ExpressionFactory.createExpression(((Function)var).getExpression());
				kfluxExp.bindExpression(mathDesc);
				kfluxExp = MathUtilities.substituteFunctions(kfluxExp,mathDesc);
				kfluxExp = kfluxExp.flatten();
				ccImpl.addSimpleBound(new SimpleBounds(var.getName(),new RealInterval(kfluxExp.evaluateConstant()),AbstractConstraint.MODELING_ASSUMPTION,"flux conversion factor"));
			}
		}
		
		return ccImpl;
	}catch (org.vcell.expression.ExpressionException e){
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
private static IExpression getSteadyStateExpression(IExpression argExp) throws org.vcell.expression.ExpressionException {
	IExpression exp = ExpressionFactory.createExpression(argExp);
	exp.substituteInPlace(ExpressionFactory.createExpression("t"),ExpressionFactory.createExpression(0.0));
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
			double initialValue = (simContext.getReactionContext().getSpeciesContextSpec(speciesContexts[i])).getInitialConditionParameter().getExpression().evaluateConstant();
			double lowInitialValue = Math.min(initialValue/tolerance,initialValue*tolerance);
			double highInitialValue = Math.max(initialValue/tolerance,initialValue*tolerance);
			ccImpl.addSimpleBound(new SimpleBounds(speciesContexts[i].getName(),new RealInterval(lowInitialValue,highInitialValue),AbstractConstraint.MODELING_ASSUMPTION,"close to specified \"initialCondition\""));
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
				
				IExpression forwardRateConstraintExp = ExpressionFactory.createExpression(((MassActionKinetics)kinetics).getForwardRateParameter().getExpression().infix()+">=0");
				forwardRateConstraintExp = getSteadyStateExpression(forwardRateConstraintExp);
				if (!forwardRateConstraintExp.compareEqual(ExpressionFactory.createExpression(1.0))){
					ccImpl.addGeneralConstraint(new GeneralConstraint(
									forwardRateConstraintExp,
									AbstractConstraint.MODELING_ASSUMPTION,
									"non-negative forward rate"));
				}
				
				IExpression reverseRateConstraintExp = ExpressionFactory.createExpression(((MassActionKinetics)kinetics).getReverseRateParameter().getExpression().infix()+">=0");
				reverseRateConstraintExp = getSteadyStateExpression(reverseRateConstraintExp);
				if (!reverseRateConstraintExp.compareEqual(ExpressionFactory.createExpression(1.0))){
					ccImpl.addGeneralConstraint(new GeneralConstraint(
									reverseRateConstraintExp,
									AbstractConstraint.MODELING_ASSUMPTION,
									"non-negative reverse rate"));
				}
			}
			IExpression kineticRateConstraintExp = ExpressionFactory.createExpression(kinetics.getRateParameter().getName()+"=="+kinetics.getRateParameter().getExpression().infix());
			kineticRateConstraintExp = getSteadyStateExpression(kineticRateConstraintExp);
			if (!kineticRateConstraintExp.compareEqual(ExpressionFactory.createExpression(1.0))){
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
			simContext.setMathDescription((new cbit.vcell.mapping.MathMapping(simContext)).getMathDescription());
		}catch (Throwable e){
			e.printStackTrace(System.out);
			throw new RuntimeException("cannot create mathDescription");
		}
		MathDescription mathDesc = simContext.getMathDescription();
		if (mathDesc.getGeometry().getDimension()>0){
			throw new RuntimeException("spatial simulations not yet supported");
		}
		CompartmentSubDomain subDomain = (CompartmentSubDomain)mathDesc.getSubDomains().nextElement();
		java.util.Enumeration enumEquations = subDomain.getEquations();
		while (enumEquations.hasMoreElements()){
			Equation equation = (Equation)enumEquations.nextElement();
			IExpression rateConstraintExp = ExpressionFactory.createExpression(equation.getRateExpression().infix()+"==0");
			rateConstraintExp = getSteadyStateExpression(rateConstraintExp);
			if (!rateConstraintExp.compareEqual(ExpressionFactory.createExpression(1.0))){
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
				IExpression exp = parameters[j].getExpression();
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
					}catch (org.vcell.expression.ExpressionException e){
						System.out.println("error evaluating parameter "+parameters[j].getName()+" in reaction step "+reactionSteps[i].getName());
					}
				}else{
					IExpression parameterDefinitionExp = ExpressionFactory.createExpression(parameters[j].getName()+"=="+parameters[j].getExpression().infix());
					ccImpl.addGeneralConstraint(new GeneralConstraint(
										getSteadyStateExpression(parameterDefinitionExp),
										AbstractConstraint.MODELING_ASSUMPTION,
										"parameter definition"));
				}
			}
		}

		ccImpl.addSimpleBound(new SimpleBounds(
									cbit.vcell.model.ReservedSymbol.FARADAY_CONSTANT.getName(),
									new RealInterval(cbit.vcell.model.ReservedSymbol.FARADAY_CONSTANT.getExpression().evaluateConstant()),
									AbstractConstraint.PHYSICAL_LIMIT,
									"Faraday's constant"));
		ccImpl.addSimpleBound(new SimpleBounds(
									cbit.vcell.model.ReservedSymbol.TEMPERATURE.getName(),
									new RealInterval(300),
									AbstractConstraint.PHYSICAL_LIMIT,
									"Absolute Temperature Kelvin"));
		ccImpl.addSimpleBound(new SimpleBounds(
									cbit.vcell.model.ReservedSymbol.GAS_CONSTANT.getName(),
									new RealInterval(cbit.vcell.model.ReservedSymbol.GAS_CONSTANT.getExpression().evaluateConstant()),
									AbstractConstraint.PHYSICAL_LIMIT,
									"ideal gas constant"));
		ccImpl.addSimpleBound(new SimpleBounds(
									cbit.vcell.model.ReservedSymbol.KMILLIVOLTS.getName(),
									new RealInterval(cbit.vcell.model.ReservedSymbol.KMILLIVOLTS.getExpression().evaluateConstant()),
									AbstractConstraint.PHYSICAL_LIMIT,
									"ideal gas constant"));
		ccImpl.addSimpleBound(new SimpleBounds(
									cbit.vcell.model.ReservedSymbol.KMOLE.getName(),
									new RealInterval(cbit.vcell.model.ReservedSymbol.KMOLE.getExpression().evaluateConstant()),
									AbstractConstraint.PHYSICAL_LIMIT,
									"conversion factor"));
		//
		// add K_fluxs
		//
		java.util.Enumeration enumVars = mathDesc.getVariables();
		while (enumVars.hasMoreElements()){
			Variable var = (Variable)enumVars.nextElement();
			if (var.getName().startsWith("Kflux_") && var instanceof Function){
				IExpression kfluxExp = ExpressionFactory.createExpression(((Function)var).getExpression());
				kfluxExp.bindExpression(mathDesc);
				kfluxExp = MathUtilities.substituteFunctions(kfluxExp,mathDesc);
				kfluxExp = kfluxExp.flatten();
				ccImpl.addSimpleBound(new SimpleBounds(var.getName(),new RealInterval(kfluxExp.evaluateConstant()),AbstractConstraint.MODELING_ASSUMPTION,"flux conversion factor"));
			}
		}
		
		return ccImpl;
	}catch (org.vcell.expression.ExpressionException e){
		e.printStackTrace(System.out);
		return null;
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		return null;
	}
}
}