package org.vcell.physics.math;


import jscl.plugin.Expression;
import jscl.plugin.ParseException;

import org.vcell.physics.component.IndependentVariable;
import org.vcell.physics.component.Parameter;
import org.vcell.physics.component.Variable;

import cbit.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (11/9/2005 5:30:21 PM)
 * @author: Jim Schaff
 */
public class MathSystemTest {
/**
 * Insert the method's description here.
 * Creation date: (11/9/2005 5:33:55 PM)
 * @return cbit.vcell.mapping.MathSystem2
 */
	public static MathSystem getExample() {
		MathSystem mathSystem = new MathSystem();
		try {

			//mathSystem.addEquation(new Expression("VS.V + capacitor.V + inductor.V + resistor.V"));
			//mathSystem.addEquation(new Expression("VS.i - capacitor.i"));
			//mathSystem.addEquation(new Expression("capacitor.i - inductor.i"));
			//mathSystem.addEquation(new Expression("inductor.i - resistor.i"));
			//mathSystem.addEquation(new Expression("resistor.i - VS.i"));
			//mathSystem.addEquation(new Expression("sin(t)-VS.V"));
			//mathSystem.addEquation(new Expression("inductor.L * inductor.i.prime - inductor.V"));
			//mathSystem.addEquation(new Expression("capacitor.C * capacitor.V.prime - capacitor.i"));
			//mathSystem.addEquation(new Expression("resistor.i*resistor.R - resistor.V"));
			//mathSystem.addEquation(new Expression("capacitor.C - 1.0"));
			//mathSystem.addEquation(new Expression("inductor.L - 1.0"));
			//mathSystem.addEquation(new Expression("resistor.R - 1.0"));

			mathSystem.addEquation(Expression.valueOf("VSdotV + capacitordotV + inductordotV + resistordotV"));
			mathSystem.addEquation(Expression.valueOf("VSdoti - capacitordoti"));
			mathSystem.addEquation(Expression.valueOf("capacitordoti - inductordoti"));
			mathSystem.addEquation(Expression.valueOf("inductordoti - resistordoti"));
			//mathSystem.addEquation(new Expression("resistor.i - VS.i"));
			mathSystem.addEquation(Expression.valueOf("sin(t)-VSdotV"));
			mathSystem.addEquation(Expression.valueOf("d(inductordoti,t) - inductordotV"));
			mathSystem.addEquation(Expression.valueOf("d(capacitordotV,t) - capacitordoti"));
			mathSystem.addEquation(Expression.valueOf("resistordoti - resistordotV"));
			//mathSystem.addEquation(new Expression("capacitor.C - 1.0"));
			//mathSystem.addEquation(new Expression("inductor.L - 1.0"));
			//mathSystem.addEquation(new Expression("resistor.R - 1.0"));

		}catch (ParseException e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}	

		
		return mathSystem;
	}
	
	public static MathSystem getPlanarPendulumExample() {
		MathSystem mathSystem = new MathSystem();
		try {
			mathSystem.addSymbol(new OOMathSymbol("m",new Parameter("m",VCUnitDefinition.getInstance("kg"))));
			mathSystem.addSymbol(new OOMathSymbol("g",new Parameter("g",VCUnitDefinition.getInstance("m.s-2"))));
			mathSystem.addSymbol(new OOMathSymbol("F",new Parameter("F",VCUnitDefinition.getInstance("N"))));
			mathSystem.addSymbol(new OOMathSymbol("L",new Parameter("L",VCUnitDefinition.getInstance("m"))));
			mathSystem.addSymbol(new OOMathSymbol("Px",new Variable("Px",VCUnitDefinition.getInstance("m"))));
			mathSystem.addSymbol(new OOMathSymbol("Vx",new Variable("Vx",VCUnitDefinition.getInstance("m.s-1"))));
			mathSystem.addSymbol(new OOMathSymbol("Py",new Variable("Py",VCUnitDefinition.getInstance("m"))));
			mathSystem.addSymbol(new OOMathSymbol("Vy",new Variable("Vy",VCUnitDefinition.getInstance("m.s-1"))));
			mathSystem.addSymbol(new OOMathSymbol("t",new IndependentVariable("t",VCUnitDefinition.UNIT_s)));
			mathSystem.addEquation(Expression.valueOf("m*d(Vx,t) + Px / L * F"));
			mathSystem.addEquation(Expression.valueOf("m*d(Vy,t) + Py / L * F + m * g"));
			mathSystem.addEquation(Expression.valueOf("Px^2 + Py^2 - L^2"));
			mathSystem.addEquation(Expression.valueOf("d(Px,t) - Vx"));
			mathSystem.addEquation(Expression.valueOf("d(Py,t) - Vy"));
		}catch (ParseException e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}	
		return mathSystem;
	}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		MathSystem mathSystem = getPlanarPendulumExample();
		mathSystem.show();
		IndexReduction.reduceDAEIndexPantelides(mathSystem);
		mathSystem.show();
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}
