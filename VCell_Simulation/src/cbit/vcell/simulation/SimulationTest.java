package cbit.vcell.simulation;

import org.vcell.expression.ExpressionFactory;

/**
 * Insert the type's description here.
 * Creation date: (12/3/2001 2:05:06 PM)
 * @author: Jim Schaff
 */
public class SimulationTest {
/**
 * Insert the method's description here.
 * Creation date: (12/3/2001 2:05:49 PM)
 * @return cbit.vcell.solver.Simulation
 */
public static Simulation getExample() {
	try {
		return new Simulation(cbit.vcell.math.MathDescriptionTest.getOdeExample());
	}catch (Exception e){
		throw new RuntimeException(e.getMessage());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2002 12:00:14 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		boolean bRoundCoefficients = false;
		// testSubstitution(bRoundCoefficients);
		testSimInfo();
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/2/2004 6:05:40 PM)
 */
public static void testEquivalenceCapability(){
	try {
		cbit.vcell.simulation.Simulation sim = cbit.vcell.simulation.SimulationTest.getExample();
		cbit.vcell.simulation.Simulation sim2 = null;
		long time_1 = System.currentTimeMillis();
		int numIterations = 500;
		// copy by cloneSerializable()
		for (int i = 0; i < numIterations; i++){
			sim2 = (cbit.vcell.simulation.Simulation)cbit.util.BeanUtils.cloneSerializable(sim);
		}
		long time_2 = System.currentTimeMillis();
		// copy by copy-constructor
		for (int i = 0; i < numIterations; i++){
			sim2 = new cbit.vcell.simulation.Simulation(sim,true);
		}
		cbit.vcell.math.SubDomain domain = (cbit.vcell.math.SubDomain)sim2.getMathDescription().getSubDomains().nextElement();
		System.out.println("subDomain = '"+domain.toString()+"'");
		cbit.vcell.math.Equation equation = (cbit.vcell.math.Equation)domain.getEquations().nextElement();
		System.out.println("orig equation = '"+equation.toString()+"'");
		sim2.getMathDescription().addVariable(new cbit.vcell.math.Constant("abc",ExpressionFactory.createExpression(0.0)));
		equation.setRateExpression(ExpressionFactory.add(equation.getRateExpression(), ExpressionFactory.createExpression("abc")));
		System.out.println("new equation = '"+equation.toString()+"'");
		long time_3 = System.currentTimeMillis();
		int compareEqualCount=0;
		for (int i = 0; i < numIterations; i++){
			if (sim2.compareEqual(sim)){
				compareEqualCount++;
			}
		}
		long time_4 = System.currentTimeMillis();
		int compareEquivCount=0;
		for (int i = 0; i < numIterations; i++){
			StringBuffer reasonBuffer = new StringBuffer();
			String mathEquivalency = cbit.vcell.math.MathDescription.testEquivalency(sim.getMathDescription(),sim2.getMathDescription(),reasonBuffer);
			if (Simulation.testEquivalency(sim,sim2,mathEquivalency)){
				compareEquivCount++;
			}
		}
		long time_5 = System.currentTimeMillis();

		System.out.println("average time for clone Simulation: "+((time_2-time_1)/1000.0)/numIterations);
		System.out.println("average time to copy-constructor copy Simulation: "+((time_3-time_2)/1000.0)/numIterations);
		System.out.println("average time to compareEqual() Simulations: "+((time_4-time_3)/1000.0)/numIterations+", %equal="+((double)compareEqualCount)/numIterations);
		System.out.println("average time to testEquivalency() Simulations: "+((time_5-time_4)/1000.0)/numIterations+", %equal="+((double)compareEquivCount)/numIterations);
		System.out.println("");
	}catch (Exception e){
		e.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2002 11:37:39 PM)
 * @param sim cbit.vcell.solver.Simulation
 * @param mathDesc1 cbit.vcell.math.MathDescription
 * @param mathDesc2 cbit.vcell.math.MathDescription
 */
public static boolean testIfEquilavent(Simulation sim, cbit.vcell.math.MathDescription mathDesc1, cbit.vcell.math.MathDescription mathDesc2) {
	try {
		if (mathDesc1 == null){
			System.out.println("-------MathDesc 1 was null, couldn't compare-----------------");
			return false;
		}
		if (mathDesc2 == null){
			System.out.println("-------MathDesc 2 was null, couldn't compare-----------------");
			return false;
		}
		if (mathDesc1 == mathDesc2){
			System.out.println("-------MathDesc 1 and MathDesc 2 are same object ------------");
			return false;
		}
		mathDesc1 = (cbit.vcell.math.MathDescription)cbit.util.BeanUtils.cloneSerializable(mathDesc1);
		mathDesc2 = (cbit.vcell.math.MathDescription)cbit.util.BeanUtils.cloneSerializable(mathDesc2);
		sim.applyOverrides(mathDesc1);
		sim.applyOverrides(mathDesc2);
		StringBuffer reasonForDecision = new StringBuffer();
		boolean bEquivalent = cbit.vcell.math.MathDescriptionTest.testIfSame(mathDesc1,mathDesc2,reasonForDecision);
//		System.out.println(reasonForDecision);
		return bEquivalent;
	}catch (Throwable e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}	
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2002 12:01:39 PM)
 */
public static void testSimInfo() throws Exception {
	Simulation sim = getExample();
	System.out.println("\n\n --------- SimInfo = "+sim.getSimulationInfo().toString());
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2002 12:01:39 PM)
 */
public static void testSubstitution() throws Exception {
	cbit.vcell.math.MathDescription math = cbit.vcell.math.MathDescriptionTest.getExample();
	System.out.println("---------------- Original MathDescription ------------------------");
	System.out.println(math.getVCML_database());
	cbit.vcell.math.MathDescription simpleMath = cbit.vcell.math.MathDescription.createCanonicalMathDescription(math);
	System.out.println("---------------- Original MathDescription ------------------------");
	System.out.println(simpleMath.getVCML_database());
}
}
