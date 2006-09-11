package cbit.vcell.opt.solvers;
import cbit.util.GroupAccessNone;
import cbit.util.KeyValue;
import cbit.util.User;
import cbit.util.VersionFlag;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.Simulation;
import cbit.vcell.opt.*;

/**
 * Insert the type's description here.
 * Creation date: (4/10/2006 10:25:33 AM)
 * @author: Jim Schaff
 */
public class NativeOptSolver implements OptimizationSolver {
	static {
		System.loadLibrary("NativeSolvers");
	}

	
/**
 * NativeIDAOptSolver constructor comment.
 */
public NativeOptSolver() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2006 10:27:22 AM)
 * @return double[]
 * @param optSpec cbit.vcell.opt.OptimizationSpec
 * @exception java.io.IOException The exception description.
 * @exception cbit.vcell.parser.ExpressionException The exception description.
 * @exception cbit.vcell.opt.OptimizationException The exception description.
 */
public native OptimizationResultSet nativeSolve_CFSQP(String[] paramNames, double[] LB, double[] UB, double[] initialGuess, double[] scaleFactors,
	int numNonLinearInequality, int numLinearInequality, int numNonLinearEquality, int numLinearEquality,  
	String[] constraints, String[] refColumnNames, String[] refColumnMappingExpressions, double[] refColumnWeights, double[][] refRowData, String idaInputString, OptSolverCallbacks optSolverCallbacks) throws Exception;


/**
 * Insert the method's description here.
 * Creation date: (4/10/2006 10:27:22 AM)
 * @return double[]
 * @param optSpec cbit.vcell.opt.OptimizationSpec
 * @exception java.io.IOException The exception description.
 * @exception cbit.vcell.parser.ExpressionException The exception description.
 * @exception cbit.vcell.opt.OptimizationException The exception description.
 */
public native OptimizationResultSet nativeSolve_MultiShooting(String[] paramNames, double[] LB, double[] UB, double[] initialGuess, double[] scaleFactors,
	int numNonLinearInequality, int numLinearInequality, int numNonLinearEquality, int numLinearEquality,  
	String[] constraints, String[] refColumnNames, String[] refColumnMappingExpressions, double[] refColumnWeights, double[][] refRowData, String idaInputString, double maxTimeStep, OptSolverCallbacks optSolverCallbacks) throws Exception;


/**
 * Insert the method's description here.
 * Creation date: (4/10/2006 10:27:22 AM)
 * @return double[]
 * @param optSpec cbit.vcell.opt.OptimizationSpec
 * @exception java.io.IOException The exception description.
 * @exception cbit.vcell.parser.ExpressionException The exception description.
 * @exception cbit.vcell.opt.OptimizationException The exception description.
 */
public cbit.vcell.opt.OptimizationResultSet solve(OptimizationSpec os, OptimizationSolverSpec optSolverSpec, OptSolverCallbacks optSolverCallbacks) 
	throws java.io.IOException, cbit.vcell.parser.ExpressionException, cbit.vcell.opt.OptimizationException {
	try {		
		Constraint[] nonlinearInequality = os.getConstraints(ConstraintType.NonlinearInequality);
		Constraint[] linearInequality = os.getConstraints(ConstraintType.LinearInequality);
		Constraint[] nonlinearEquality = os.getConstraints(ConstraintType.NonlinearEquality);
		Constraint[] linearEquality = os.getConstraints(ConstraintType.LinearEquality);
		
		Parameter[] parameters = os.getParameters();
		
		String[] parameterNames = new String[parameters.length];
		double[] LB = new double[parameters.length];
		double[] UB = new double[parameters.length];
		double[] initGuess = new double[parameters.length];
		double[] scaleFactors = new double[parameters.length];
		
		for (int i = 0; i < parameters.length; i++){
			parameterNames[i] = parameters[i].getName();
			LB[i] = parameters[i].getLowerBound();
			UB[i] = parameters[i].getUpperBound();
			initGuess[i] = parameters[i].getInitialGuess();
			scaleFactors[i] = parameters[i].getScale();
		}	

		String[] constraintExpressions = new String[os.getNumConstraints()];
		int count = 0;
		for (int i = 0; i < nonlinearInequality.length; i ++) {
			constraintExpressions[count ++] = nonlinearInequality[i].getExpression().infix();
		}
		
		for (int i = 0; i < linearInequality.length; i ++) {
			constraintExpressions[count ++] = linearInequality[i].getExpression().infix();
		}
		for (int i = 0; i < nonlinearEquality.length; i ++) {
			constraintExpressions[count ++] = nonlinearEquality[i].getExpression().infix();
		}
		
		for (int i = 0; i < linearEquality.length; i ++) {
			constraintExpressions[count ++] = linearEquality[i].getExpression().infix();
		}

		OdeObjectiveFunction odeObjectiveFunction = null;	
		try {
			odeObjectiveFunction = (OdeObjectiveFunction)os.getObjectiveFunction();
		} catch (ClassCastException ex) {
			throw new RuntimeException("NativeIdaOptSolver can only optimize ODE objective function!");
		}
		
		SimulationVersion simVersion = new SimulationVersion(
			new KeyValue("12345"),
			"name",
			new User("user",new KeyValue("123")),
			new GroupAccessNone(),
			null, // versionBranchPointRef
			new java.math.BigDecimal(1.0), // branchID
			new java.util.Date(),
			VersionFlag.Archived,
			"",
			null);
	
		
		Simulation simulation = new Simulation(simVersion,odeObjectiveFunction.getMathDescription());

		ReferenceData refData = odeObjectiveFunction.getReferenceData();

		String[] refColumnMappingExpressions = new String[refData.getNumColumns()-1];
		for (int i = 1; i < refData.getNumColumns(); i ++) {
			cbit.vcell.math.Variable var = odeObjectiveFunction.getMathDescription().getVariable(refData.getColumnNames()[i]);
			if (var instanceof cbit.vcell.math.Function) {
				Expression exp = new Expression(var.getExpression());
				exp.bindExpression(simulation);
				exp = cbit.vcell.math.MathUtilities.substituteFunctions(exp, simulation);
				refColumnMappingExpressions[i-1] = exp.flatten().infix();
			} else {
				refColumnMappingExpressions[i-1] = var.getName();
			}
		}

		double[][] data = new double[refData.getNumRows()][];
		
		for (int i = 0; i < refData.getNumRows(); i ++) {
			data[i] = refData.getRowData(i);
		}

		double refDataEndTime = refData.getColumnData(0)[refData.getNumRows()-1];
		simulation.getSolverTaskDescription().setTimeBounds(new cbit.vcell.solver.TimeBounds(0.0, refDataEndTime));
		//int timeIndex = refData.findColumn("t");		
		//simulation.getSolverTaskDescription().setOutputTimeSpec(new cbit.vcell.solver.ExplicitOutputTimeSpec(refData.getColumnData(timeIndex)));

		cbit.vcell.solver.ode.IDAFileWriter idaFileWriter = new cbit.vcell.solver.ode.IDAFileWriter(simulation);
		idaFileWriter.initialize();
		java.io.StringWriter stringWriter = new java.io.StringWriter();
		idaFileWriter.writeIDAFile(new java.io.PrintWriter(stringWriter,true), parameterNames);
		stringWriter.close();
		StringBuffer buffer = stringWriter.getBuffer();
		String idaInputString = buffer.toString();

		
		OptimizationResultSet optimizationResultSet = null;
		
		if (optSolverSpec.getSolverType().equals(OptimizationSolverSpec.SOLVERTYPE_CFSQP)) {
			optimizationResultSet = nativeSolve_CFSQP(parameterNames, LB, UB, initGuess, scaleFactors,
					nonlinearInequality.length, linearInequality.length, nonlinearEquality.length, linearEquality.length, constraintExpressions, 
					refData.getColumnNames(), refColumnMappingExpressions, refData.getColumnWeights(), data, idaInputString, optSolverCallbacks);
		} else {
			double maxTimeStep = refDataEndTime / 50;
			optimizationResultSet = nativeSolve_MultiShooting(parameterNames, LB, UB, initGuess, scaleFactors,
					nonlinearInequality.length, linearInequality.length, nonlinearEquality.length, linearEquality.length, constraintExpressions, 
					refData.getColumnNames(), refColumnMappingExpressions, refData.getColumnWeights(), data, idaInputString, maxTimeStep, optSolverCallbacks);
		}

		return optimizationResultSet;
		
	} catch (java.beans.PropertyVetoException ex) {
		ex.printStackTrace();
		throw new OptimizationException(ex.getMessage());
	} catch (Exception ex) {
		ex.printStackTrace();
		throw new OptimizationException(ex.getMessage());
	}
}
}