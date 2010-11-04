package cbit.vcell.opt.solvers;

import java.io.IOException;

import org.jdom.Element;
import org.vcell.optimization.NativeOptSolver;
import org.vcell.optimization.OptSolverResultSet;
import org.vcell.optimization.OptSolverResultSet.OptRunResultSet;
import org.vcell.optimization.OptXmlReader;
import org.vcell.optimization.OptXmlTags;
import org.vcell.optimization.OptXmlWriter;

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
import cbit.vcell.opt.OptimizationStatus;
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


public class NewOptimizationSolver implements OptimizationSolver {

public OptimizationResultSet solve(OptimizationSpec os,	OptimizationSolverSpec optSolverSpec, final OptSolverCallbacks optSolverCallbacks) 
						throws IOException, ExpressionException, OptimizationException {
	Element optProblemXML = OptXmlWriter.getOptProblemDescriptionXML(os);
	org.vcell.optimization.OptSolverCallbacks callbacks = new org.vcell.optimization.OptSolverCallbacks(){
		
		public void addEvaluation(double[] paramValues,	double objectiveFuncValue) {
			optSolverCallbacks.addEvaluation(paramValues, objectiveFuncValue);
		}

		public Evaluation getBestEvaluation() {
			return null;
		}

		public long getEvaluationCount() {
			return optSolverCallbacks.getEvaluationCount();
		}

		public boolean getStopRequested() {
			return optSolverCallbacks.getStopRequested();
		}

		public void setStopRequested(boolean stopRequested) {
			optSolverCallbacks.setStopRequested(stopRequested);
		}
		
	};
	NativeOptSolver newNativeOptSolver = new NativeOptSolver();
	try {		
		String inputXML = XmlUtil.xmlToString(optProblemXML);
//		PrintWriter pw = new PrintWriter("c:\\test10.xml");
//		pw.println(inputXML);
//		pw.close();
		String optResultsXML = newNativeOptSolver.nativeSolve_CFSQP(inputXML, callbacks);		
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
				rcResultSet = nativeIDASolver.solve(modelInput,newOptResultSet.getParameterValues());
			}else if (modelType.equals(OptXmlTags.ModelType_Attr_CVODE)){
				NativeCVODESolver nativeCVODESolver = new NativeCVODESolver();
				rcResultSet = nativeCVODESolver.solve(modelInput,newOptResultSet.getParameterValues());
			}
			MathDescription mathDesc = odeObjFunc.getMathDescription();
			Simulation sim = new Simulation(mathDesc);
			SimulationSymbolTable simSymbolTable = new SimulationSymbolTable(sim, 0);
			MathOverrides mathOverrides = sim.getMathOverrides();
			String[] parameterNames = newOptResultSet.getParameterNames();
			double[] parameterValues = newOptResultSet.getParameterValues();
			for (int i = 0; i < parameterValues.length; i++) {
				mathOverrides.putConstant(new Constant(parameterNames[i],new Expression(parameterValues[i])));
			}
			odeSolverResultSet = getOdeSolverResultSet(rcResultSet, simSymbolTable, parameterNames, parameterValues);
		}	
		OptimizationResultSet optResultSet = new OptimizationResultSet(newOptResultSet, odeSolverResultSet);
		return optResultSet;
		
	}catch (Exception e){
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