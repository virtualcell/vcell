package org.vcell.optimization;

import java.io.IOException;

import org.jdom.Element;

import cbit.util.xml.XmlUtil;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.opt.OdeObjectiveFunction;
import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.ode.FunctionColumnDescription;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.solvers.NativeCVODESolver;
import cbit.vcell.solvers.NativeIDASolver;
import cbit.vcell.util.RowColumnResultSet;


public class CopasiOptimizationSolver {	
	public enum CopasiOptimizationParameterType {
		Number_of_Generations("Number of Generations"),
		Number_of_Iterations("Number of Iterations"),
		Population_Size("Population Size"),
		Random_Number_Generator("Random Number Generator"),
		Seed("Seed"),
		IterationLimit("Iteration Limit"),
		Tolerance("Tolerance"),
		Rho("Rho"),
		Scale("Scale"),
		Swarm_Size("Swarm Size"),
		Std_Deviation("Std. Deviation"),
		Start_Temperature("Start Temperature"),
		Cooling_Factor("Cooling Factor"),
		Pf("Pf");
		
		String displayName;
		CopasiOptimizationParameterType(String displayName) {
			this.displayName = displayName;
		}
		public final String getDisplayName() {
			return displayName;
		}
	}
	
	public static class CopasiOptimizationParameter {
		private CopasiOptimizationParameterType type;
		private double value;
		
		CopasiOptimizationParameter(CopasiOptimizationParameterType type, double dv) {
			this.type = type;
			value = dv;
		}

		CopasiOptimizationParameter(CopasiOptimizationParameter anotherParameter, double dv) {
			this.type = anotherParameter.type;
			value = dv;
		}
		
		public final double getValue() {
			return value;
		}

		public final void setValue(double value) {
			this.value = value;
		}

		public final CopasiOptimizationParameterType getType() {
			return type;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CopasiOptimizationParameter) {
				CopasiOptimizationParameter cop = (CopasiOptimizationParameter)obj;
				if (cop.type != type) {
					return false;
				}
				if (cop.value != value) {
					return false;
				}
				return true;
			}
			return false;
		}
	}
	
	public static class CopasiOptimizationMethod {
		private CopasiOptimizationMethodType type;
		private CopasiOptimizationParameter[] realParameters;
		
		public CopasiOptimizationMethod(CopasiOptimizationMethodType type) {
			this.type = type;
			CopasiOptimizationParameter[] defaultParameters = type.getDefaultParameters();
			if (defaultParameters != null) {
				this.realParameters = new CopasiOptimizationParameter[defaultParameters.length];
				System.arraycopy(type.defaultParameters, 0, realParameters, 0, defaultParameters.length);
			}
		}
		public final CopasiOptimizationMethodType getType() {
			return type;
		}
		public final CopasiOptimizationParameter[] getParameters() {
			return realParameters;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CopasiOptimizationMethod) {
				CopasiOptimizationMethod com = (CopasiOptimizationMethod)obj;
				if (com.type != type) {
					return false;
				}
				if (realParameters.length != com.realParameters.length) {
					return false;
				}
				for (int i = 0; i < realParameters.length; i ++) {
					if (!realParameters[i].equals(com.realParameters[i])) {
						return false;
					}
				}
				return true;
			}
			return false;
		}
	}
	
	public enum CopasiOptimizationMethodType {
		Statistics("Current Solution Statistics", null),
		EvolutionaryProgram("Evolutionary Programming", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Number_of_Generations, 200),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Population_Size, 20),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0),
		}),
		SRES("Evolution Strategy (SRES)", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Number_of_Generations, 200),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Population_Size, 20),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Pf, 0.475),
		}),
		GeneticAlgorithm("Genetic Algorithm", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Number_of_Generations, 200),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Population_Size, 20),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0),
		}),
		GeneticAlgorithmSR("Genetic Algorithm SR", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Number_of_Generations, 200),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Population_Size, 20),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Pf, 0.475),
		}),
		HookeJeeves("Hooke & Jeeves", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.IterationLimit, 50),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Tolerance, 1e-5),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Rho, 0.2),
		}),
		LevenbergMarquardt("Levenberg - Marquardt", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.IterationLimit, 200),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Tolerance, 1e-5),
		}),
		NelderMead("Nelder - Mead", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.IterationLimit, 200),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Tolerance, 1e-5),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Scale, 10),
		}),
		ParticleSwarm("Particle Swarm", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.IterationLimit, 2000),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Swarm_Size, 50),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Std_Deviation, 1e-6),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0),
		}),
	    RandomSearch("Random Search", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Number_of_Iterations, 100000),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0),
		}),
	    SimulatedAnnealing("Simulated Annealing", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Start_Temperature, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Cooling_Factor, 0.85),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Tolerance, 1e-6),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0),
		}),
	    SteepestDescent("Steepest Descent", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.IterationLimit, 100),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Tolerance, 1e-6),
		}),
	    Praxis("Praxis", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Tolerance, 1e-5),
		}),
	    TruncatedNewton("Truncated Newton", null);
				    
		private String name;
		private String displayName;
		private CopasiOptimizationParameter[] defaultParameters;
		CopasiOptimizationMethodType(String arg_name, CopasiOptimizationParameter[] parameters) {
			name = arg_name;
			displayName = name;
			this.defaultParameters = parameters;
		}
		public final String getName() {
			return name;
		}
		public final String getDisplayName() {
			return displayName;
		}
		public final CopasiOptimizationParameter[] getDefaultParameters() {
			return defaultParameters;
		}
		
	}
	
	private static native String solve(String modelSbml, String optProblemXml, OptSolverCallbacks optSolverCallbacks);
	
public static OptimizationResultSet solve(OptimizationSpec os,	OptimizationSolverSpec optSolverSpec, final OptSolverCallbacks optSolverCallbacks) 
						throws IOException, ExpressionException, OptimizationException {
	Element optProblemXML = OptXmlWriter.getOptProblemDescriptionXML(os);
	String modelSbml = null;
	try {		
		String inputXML = XmlUtil.xmlToString(optProblemXML);
//		PrintWriter pw = new PrintWriter("c:\\test10.xml");
//		pw.println(inputXML);
//		pw.close();
		String optResultsXML = solve(modelSbml, inputXML, optSolverCallbacks);
		OptSolverResultSet newOptResultSet = OptXmlReader.getOptimizationResultSet(optResultsXML);
		ODESolverResultSet odeSolverResultSet = null;
		if (os.getObjectiveFunction() instanceof OdeObjectiveFunction){
			RowColumnResultSet rcResultSet = null;
			OdeObjectiveFunction odeObjFunc = (OdeObjectiveFunction)os.getObjectiveFunction();
			Element objFuncElement = optProblemXML.getChild(OptXmlTags.ObjectiveFunction_Tag);
			Element modelElement = objFuncElement.getChild(OptXmlTags.Model_Tag);
			String modelType = modelElement.getAttributeValue(OptXmlTags.ModelType_Attr);
			String modelInput = modelElement.getText();
			if (modelType.equals(OptXmlTags.ModelType_Attr_IDA)){
				NativeIDASolver nativeIDASolver = new NativeIDASolver();
				rcResultSet = nativeIDASolver.solve(modelInput,newOptResultSet.getBestEstimates());
			}else if (modelType.equals(OptXmlTags.ModelType_Attr_CVODE)){
				NativeCVODESolver nativeCVODESolver = new NativeCVODESolver();
				rcResultSet = nativeCVODESolver.solve(modelInput,newOptResultSet.getBestEstimates());
			}
			MathDescription mathDesc = odeObjFunc.getMathDescription();
			Simulation sim = new Simulation(mathDesc);
			SimulationSymbolTable simSymbolTable = new SimulationSymbolTable(sim, 0);
			MathOverrides mathOverrides = sim.getMathOverrides();
			String[] parameterNames = newOptResultSet.getParameterNames();
			double[] parameterValues = newOptResultSet.getBestEstimates();
			for (int i = 0; i < parameterValues.length; i++) {
				mathOverrides.putConstant(new Constant(parameterNames[i],new Expression(parameterValues[i])));
			}
			odeSolverResultSet = getOdeSolverResultSet(rcResultSet, simSymbolTable, parameterNames, parameterValues);
		}	
		OptimizationResultSet optResultSet = new OptimizationResultSet(newOptResultSet, odeSolverResultSet);
		return optResultSet;
		
	} catch (Throwable e){
		e.printStackTrace(System.out);
		throw new OptimizationException(e.getMessage());
	}
}
	
private static ODESolverResultSet getOdeSolverResultSet(RowColumnResultSet rcResultSet, SimulationSymbolTable simSymbolTable, String[] parameterNames, double[] parameterValues){
	//
	// get simulation results - copy from RowColumnResultSet into OdeSolverResultSet
	//
	
	ODESolverResultSet odeSolverResultSet = new ODESolverResultSet();
	for (int i = 0; i < rcResultSet.getDataColumnCount(); i++){
		odeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(rcResultSet.getColumnDescriptions(i).getName()));
	}
	for (int i = 0; i < rcResultSet.getRowCount(); i++){
		odeSolverResultSet.addRow(rcResultSet.getRow(i));
	}
	//
	// add appropriate Function columns to result set
	//
	Function functions[] = simSymbolTable.getFunctions();
	for (int i = 0; i < functions.length; i++){
		if (cbit.vcell.solver.SimulationSymbolTable.isFunctionSaved(functions[i])){
			Expression exp1 = new Expression(functions[i].getExpression());
			try {
				exp1 = simSymbolTable.substituteFunctions(exp1).flatten();
				//
				// substitute in place all "optimization parameter" values.
				//
				for (int j = 0; parameterNames!=null && j < parameterNames.length; j++) {
					exp1.substituteInPlace(new Expression(parameterNames[j]), new Expression(parameterValues[j]));
				}
			} catch (MathException e) {
				e.printStackTrace(System.out);
				throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
			} catch (ExpressionException e) {
				e.printStackTrace(System.out);
				throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
			}
			
			try {
				FunctionColumnDescription cd = new FunctionColumnDescription(exp1.flatten(),functions[i].getName(), null, functions[i].getName(), false);
				odeSolverResultSet.addFunctionColumn(cd);
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
			}
		}
	}
	return odeSolverResultSet;
}

}