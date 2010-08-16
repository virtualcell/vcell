package cbit.vcell.solver.stoch;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import cbit.vcell.math.Action;
import cbit.vcell.math.JumpProcess;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathFormatException;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.VarIniCondition;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverFileWriter;
import cbit.vcell.solver.UniformOutputTimeSpec;

/**
 * The function reads model information from simulation and
 * generates the stochastic input file for simulation engine.
 * Creation date: (6/22/2006 4:22:59 PM)
 * @author: Tracy LI
 */
public class StochFileWriter extends SolverFileWriter 
{

/**
 * StochFileWriter constructor comment.
 */
public StochFileWriter(PrintWriter pw, SimulationJob arg_simulationJob, boolean bMessaging) 
{
	super(pw, arg_simulationJob, bMessaging);
}


/**
 * Find all the dependent processes for a given process.
 * Creation date: (6/28/2006 4:16:55 PM)
 */
private Vector<String> getDependencies(JumpProcess process, List<JumpProcess> processList) throws Exception
{
	Vector<String> result = new Vector<String>();
	//get affected vars in the given process.
	String[] affectedVars = new String[process.getActions().size()];
	for(int i=0; i<process.getActions().size(); i++)
	{
		affectedVars[i] = ((Action)process.getActions().get(i)).getVar().getName();
	}
	if(affectedVars.length > 0)
	{
		//go through all the processes to find dependants
		for(int i=0; i<processList.size(); i++)
		{
			if(!process.compareEqual(processList.get(i)))//to avoid comparing with it's own
			{
				Expression probExp = processList.get(i).getProbabilityRate();
				probExp = simulationJob.getSimulationSymbolTable().substituteFunctions(probExp).flatten();
				String[] vars = probExp.getSymbols();
				if((vars != null)&&(vars.length>0))
				{				
					if(hasCommonElement(affectedVars,vars)) {
						result.addElement(processList.get(i).getName());
					}
				}
			}
		}
	}
	return result;
}


/**
 * Check whether the two String array has at least a common element.
 * Creation date: (6/29/2006 8:55:01 AM)
 * @return boolean
 * @param oriArray java.lang.String[]
 * @param comArray java.lang.String[]
 */
private boolean hasCommonElement(String[] oriArray, String[] comArray) 
{
	for(int i=0; i<oriArray.length; i++) {
		for(int j=0; j<comArray.length; j++) {
			if(oriArray[i].equals(comArray[j])) {
				return true;
			}
		}
	}
	return false;
}


/**
 * Get infomation for stochasic input file.
 * Validate the information. If it is valid, a true value will be return.
 * The function will be called by writeStochInputFile().
 * Creation date: (6/23/2006 3:59:34 PM)
 * @return boolean
 */
public void initialize() throws Exception
{
	Simulation simulation = simulationJob.getSimulation();
	//check variables
	if(!simulation.getMathDescription().getVariables().hasMoreElements())
	{
		throw new MathException("Stochastic model has no variable.");
	}
	//check subDomain
	SubDomain subDomain = null;
	Enumeration<SubDomain> e=simulation.getMathDescription().getSubDomains();
	if(e.hasMoreElements())
	{
		subDomain = e.nextElement();
	}
	else throw new MathException("There is no sub domain.");
	//check jump processes
	if(subDomain.getJumpProcesses().size()<1)
	{
		throw new MathException("Stochastic model has no jump process.");
	}
}

/**
 * check if the expression in probability rate contains illegal symbols
 * which should only includes variable names .
 * Creation date: (11/14/2006 6:28:35 PM)
 */
public boolean isValidProbabilityExpression(Expression probExp)
{
	Simulation simulation = simulationJob.getSimulation();
	
	String[] symbols = probExp.getSymbols();
	for(int i=0; symbols != null && i<symbols.length; i++)
	{
		if(simulation.getMathDescription().getVariable(symbols[i]) != null)
			continue;
		else
			return false;
	}
	return true;
}

/**
 * Write the model to a text file which serves as an input for Stochastic simulation engine.
 * Creation date: (6/22/2006 5:37:26 PM)
 */
public void write(String[] parameterNames) throws Exception,ExpressionException
{
	Simulation simulation = simulationJob.getSimulation();
	SimulationSymbolTable simSymbolTable = simulationJob.getSimulationSymbolTable(); 
	
	initialize();
	writeJMSParamters();

	// Write control information
	printWriter.println("<control>");
	cbit.vcell.solver.SolverTaskDescription solverTaskDescription = simulation.getSolverTaskDescription();
	cbit.vcell.solver.TimeBounds timeBounds = solverTaskDescription.getTimeBounds();
	cbit.vcell.solver.OutputTimeSpec outputTimeSpec = solverTaskDescription.getOutputTimeSpec();
	ErrorTolerance errorTolerance = solverTaskDescription.getErrorTolerance();
	StochSimOptions stochOpt = solverTaskDescription.getStochOpt();
	printWriter.println("STARTING_TIME"+"\t"+ timeBounds.getStartingTime());
	printWriter.println("ENDING_TIME "+"\t"+ timeBounds.getEndingTime());
//		pw.println("MAX_ITERATION"+"\t"+outputTimeSpec.getKeepAtMost());
	printWriter.println("TOLERANCE "+"\t"+errorTolerance.getAbsoluteErrorTolerance());
	if(outputTimeSpec.isDefault())
		printWriter.println("SAMPLE_INTERVAL"+"\t"+((DefaultOutputTimeSpec)outputTimeSpec).getKeepEvery());
	else if (outputTimeSpec.isUniform())
		printWriter.println("SAVE_PERIOD"+"\t"+((UniformOutputTimeSpec)outputTimeSpec).getOutputTimeStep());
	printWriter.println("NUM_TRIAL"+"\t"+solverTaskDescription.getStochOpt().getNumOfTrials());

	if(stochOpt.isUseCustomSeed())
  		printWriter.println("SEED"+"\t"+stochOpt.getCustomSeed());
  	printWriter.println("</control>");
  	printWriter.println();

  	//write model information
  	Enumeration<SubDomain> e = simulation.getMathDescription().getSubDomains();//Model info. will be extracted from subDomain of mathDescription
  	SubDomain subDomain = null;
  	if(e.hasMoreElements()) {
		subDomain = e.nextElement();
  	}
  	if(subDomain != null)
  	{
	  	printWriter.println("<model>");
		//  variables
		printWriter.println("<discreteVariables>");
		List<VarIniCondition> varInis = subDomain.getVarIniConditions(); //There is only one subDomain for compartmental model
		if((varInis != null) && (varInis.size()>0))
	    {
		    printWriter.println("TotalVars"+"\t"+varInis.size());
		  	for(int i=0; i<varInis.size(); i++)
			{
		  		try{
		  			Expression iniExp = varInis.get(i).getIniVal();
		  			iniExp.bindExpression(simSymbolTable);
					iniExp = simSymbolTable.substituteFunctions(iniExp).flatten();
		  			double iniValue = iniExp.evaluateConstant();
		  			printWriter.println(((VarIniCondition)varInis.get(i)).getVar().getName()+"\t"+Math.round(iniValue));
		  		}catch(cbit.vcell.parser.ExpressionException ex)
		  		{
		  			ex.printStackTrace();
		  			throw new MathFormatException("variable "+((VarIniCondition)varInis.get(i)).getVar().getName()+"'s initial condition is required to be a constant.");
		  		}
			}
		}
		else printWriter.println("TotalVars"+"\t"+"0");
		printWriter.println("</discreteVariables>");
		printWriter.println();
		
		// jump processes	
		printWriter.println("<jumpProcesses>");
		List<JumpProcess> jumpProcesses = subDomain.getJumpProcesses();
		if((jumpProcesses != null) && (jumpProcesses.size()>0))
	    {
		    printWriter.println("TotalProcesses"+"\t"+jumpProcesses.size());
		    for(int i=0; i<jumpProcesses.size(); i++)
		    {
			    printWriter.println(jumpProcesses.get(i).getName());
			}
	    }
	    else printWriter.println("TotalProcesses"+"\t"+"0");
		printWriter.println("</jumpProcesses>");
		printWriter.println();

		// process description
		printWriter.println("<processDesc>");
		if((jumpProcesses != null) && (jumpProcesses.size()>0))
	    {
			printWriter.println("TotalDescriptions"+"\t"+jumpProcesses.size());
			for (int i=0; i<jumpProcesses.size(); i++)
			{
				JumpProcess temProc = (JumpProcess)jumpProcesses.get(i);
				printWriter.println("JumpProcess"+"\t"+temProc.getName()); //jump process name
				Expression probExp = temProc.getProbabilityRate();
				try {
					probExp.bindExpression(simSymbolTable);
					probExp = simSymbolTable.substituteFunctions(probExp).flatten();
					if(!isValidProbabilityExpression(probExp))
					{
						throw new MathFormatException("probability rate in jump process "+temProc.getName()+" has illegal symbols(should only contain variable names).");
					}
				}catch(cbit.vcell.parser.ExpressionException ex)
				{
					ex.printStackTrace();
					throw new cbit.vcell.parser.ExpressionException("Binding math description error in probability rate in jump process "+temProc.getName()+". Some symbols can not be resolved.");	
				}
				//Expression temp = replaceVarIniInProbability(probExp);
				printWriter.println("\t"+"Propensity"+"\t"+ probExp.infix()); //Propensity
				//effects					
				printWriter.println("\t"+"Effect"+"\t"+temProc.getActions().size());
				for(int j=0; j<temProc.getActions().size(); j++)
				{
					printWriter.print("\t\t"+((Action)temProc.getActions().get(j)).getVar().getName()+"\t"+((Action)temProc.getActions().get(j)).getOperation());
					printWriter.println("\t"+((Action)temProc.getActions().get(j)).evaluateOperand());
					printWriter.println();
				}
				//dependencies
				Vector<String> dependencies = getDependencies(temProc, jumpProcesses);
				if((dependencies != null) && (dependencies.size() > 0))
				{
					printWriter.println("\t"+"DependentProcesses"+"\t"+dependencies.size());
					for(int j=0; j<dependencies.size(); j++)
						printWriter.println("\t\t"+dependencies.elementAt(j));
				}
				else printWriter.println("\t"+"DependentProcesses"+"\t"+"0");
				printWriter.println();
			}
		}
	    else printWriter.println("TotalDescriptions"+"\t"+"0");
	    printWriter.println("</processDesc>");
		printWriter.println("</model>");	
  	} //if (subDomain != null)
}

}