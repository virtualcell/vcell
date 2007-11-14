package cbit.vcell.opt.solvers;
import cbit.vcell.solver.ode.FunctionColumnDescription;
import cbit.vcell.parser.*;
import cbit.vcell.opt.ReferenceData;
import cbit.sql.KeyValue;
import cbit.vcell.math.MathDescription;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.ode.IDASolverStandalone;
import cbit.vcell.solver.SolverStatus;
/**
 * Insert the type's description here.
 * Creation date: (9/5/2005 1:32:55 PM)
 * @author: Jim Schaff
 */
public class OdeLSFunction extends cbit.function.DefaultScalarFunction {
	private cbit.vcell.opt.OdeObjectiveFunction odeObjectiveFunction = null;
//	private cbit.vcell.opt.Parameter[] parameters = null;
	private String unscaledParameterNames[] = null;
	private double parameterScalings[] = null;
	private cbit.vcell.solver.ode.ODESolverResultSet odeSolverResultSet = null;
	private java.io.File directory = null;
	private OptSolverCallbacks optSolverCallbacks = null;

/**
 * OdeLSFunction constructor comment.
 */
public OdeLSFunction(cbit.vcell.opt.OdeObjectiveFunction argOdeObjectiveFunction, String[] argUnscaledParameterNames, double[] argParameterScalings, OptSolverCallbacks argOptSolverCallbacks) {
	super();
	this.odeObjectiveFunction = argOdeObjectiveFunction;

	if (argUnscaledParameterNames == null || argParameterScalings == null) {
		throw new RuntimeException("Parameters cannot be null");
	}
	this.unscaledParameterNames = argUnscaledParameterNames;
	this.parameterScalings = argParameterScalings;
	this.optSolverCallbacks = argOptSolverCallbacks;
	
	try {
		java.io.File newFile = java.io.File.createTempFile("vcell", ".dummy"); 
		directory = newFile.getParentFile();
		newFile.delete();
	} catch (java.io.IOException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Could not create temp directory : " + e.getMessage());
	}
}


/**
 * f method comment.
 */
private double calculateWeightedError(double[] x) {
	//
	// Clear previous results
	//
	odeSolverResultSet = null;
	String idaInputString = null;
	
	try {
		long t1 = System.currentTimeMillis();
		if (optSolverCallbacks.getStopRequested()){
			throw new RuntimeException("user aborted");
		}
		//
		// Create new simulation and apply current parameter values in x[] as math overrides
		//
		cbit.sql.SimulationVersion simVersion = new cbit.sql.SimulationVersion(
			new KeyValue("12345"),
			"name",
			new cbit.vcell.server.User("user",new KeyValue("123")),
			new cbit.vcell.server.GroupAccessNone(),
			null, // versionBranchPointRef
			new java.math.BigDecimal(1.0), // branchID
			new java.util.Date(),
			cbit.sql.VersionFlag.Archived,
			"",
			null);
		Simulation simulation = new Simulation(simVersion,odeObjectiveFunction.getMathDescription());
		cbit.vcell.solver.MathOverrides mathOverrides = simulation.getMathOverrides();
		for (int i = 0; i < unscaledParameterNames.length; i++){
			double unscaledParameterValue = x[i] * parameterScalings[i];
			mathOverrides.putConstant(new cbit.vcell.math.Constant(unscaledParameterNames[i],new cbit.vcell.parser.Expression(unscaledParameterValue)));
			System.out.print(unscaledParameterNames[i]+"="+x[i]+" ");
		}
		System.out.println();
		ReferenceData refData = odeObjectiveFunction.getReferenceData();
		double refDataEndTime = refData.getColumnData(0)[refData.getNumRows()-1];
		simulation.getSolverTaskDescription().setTimeBounds(new cbit.vcell.solver.TimeBounds(0.0, refDataEndTime));
		simulation.getSolverTaskDescription().setSolverDescription(SolverDescription.IDA);

		cbit.vcell.solver.ode.IDAFileWriter idaFileWriter = new cbit.vcell.solver.ode.IDAFileWriter(simulation);
		idaFileWriter.initialize();
		java.io.StringWriter stringWriter = new java.io.StringWriter();
		idaFileWriter.writeInputFile(new java.io.PrintWriter(stringWriter,true), unscaledParameterNames);
		stringWriter.close();
		StringBuffer buffer = stringWriter.getBuffer();
		idaInputString = buffer.toString();
		
			
		long t2 = System.currentTimeMillis();

		final cbit.vcell.solvers.NativeIDASolver nativeIDASolver = new cbit.vcell.solvers.NativeIDASolver();
		optSolverCallbacks.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
			public void propertyChange(java.beans.PropertyChangeEvent evt){
				if (((OptSolverCallbacks)evt.getSource()).getStopRequested()){
					nativeIDASolver.setStopRequested(true);
				}
			}
		});
		nativeIDASolver.setStopRequested(optSolverCallbacks.getStopRequested());
		cbit.vcell.util.RowColumnResultSet rcResultSet = nativeIDASolver.solve(idaInputString, x);

		long t3 = System.currentTimeMillis();

		//
		// copy into OdeSolverResultSet
		//
		cbit.vcell.solver.ode.ODESolverResultSet tempOdeSolverResultSet = new cbit.vcell.solver.ode.ODESolverResultSet();
		for (int i = 0; i < rcResultSet.getDataColumnCount(); i++){
			tempOdeSolverResultSet.addDataColumn(new cbit.vcell.solver.ode.ODESolverResultSetColumnDescription(rcResultSet.getColumnDescriptions(i).getName()));
		}
		for (int i = 0; i < rcResultSet.getRowCount(); i++){
			tempOdeSolverResultSet.addRow(rcResultSet.getRow(i));
		}
		//
		// add appropriate Function columns to result set
		//
		cbit.vcell.math.Function functions[] = simulation.getFunctions();
		for (int i = 0; i < functions.length; i++){
			if (cbit.vcell.solvers.AbstractSolver.isFunctionSaved(functions[i])){
				Expression exp1 = new Expression(functions[i].getExpression());
				try {
					exp1 = simulation.substituteFunctions(exp1);
				} catch (cbit.vcell.math.MathException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
				} catch (cbit.vcell.parser.ExpressionException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
				}
				
				try {
					for (int j = 0; j < unscaledParameterNames.length; j ++) {
						exp1.substituteInPlace(new cbit.vcell.parser.Expression(unscaledParameterNames[j]), new cbit.vcell.parser.Expression(x[j]));
					}
					
					FunctionColumnDescription cd = new FunctionColumnDescription(exp1.flatten(),functions[i].getName(), null, functions[i].getName(), false);
					tempOdeSolverResultSet.addFunctionColumn(cd);
				}catch (cbit.vcell.parser.ExpressionException e){
					e.printStackTrace(System.out);
				}
			}
		}

		odeSolverResultSet = tempOdeSolverResultSet;
		
		long t4 = System.currentTimeMillis();
		System.out.println("init="+(t2-t1)+"ms, solve="+(t3-t2)+"ms, post-process="+(t4-t3)+"ms");
		return cbit.vcell.solver.test.MathTestingUtilities.calcWeightedSquaredError(odeSolverResultSet,refData);
	} catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	} catch (cbit.vcell.solver.SolverException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	} catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	} catch (Exception e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * f method comment.
 */
public double f(double[] x) {
	return calculateWeightedError(x);
}


/**
 * Insert the method's description here.
 * Creation date: (9/5/2005 1:32:55 PM)
 * @return int
 */
public int getNumArgs() {
	return unscaledParameterNames.length;
}


/**
 * Insert the method's description here.
 * Creation date: (9/6/2005 9:18:19 AM)
 * @return cbit.vcell.solver.ode.ODESolverResultSet
 */
public cbit.vcell.solver.ode.ODESolverResultSet getOdeSolverResultSet() {
	return odeSolverResultSet;
}
}