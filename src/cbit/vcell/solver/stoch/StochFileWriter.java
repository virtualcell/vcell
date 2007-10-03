package cbit.vcell.solver.stoch;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.math.*;
import cbit.vcell.solver.*;
import cbit.vcell.math.JumpProcess;
import cbit.vcell.math.Action;
import cbit.vcell.math.MathException;
import cbit.vcell.math.SubDomain;
import java.io.*;
import java.util.Date;
import java.util.Vector;

/**
 * The function reads model information from simulation and
 * generates the stochastic input file for simulation engine.
 * Creation date: (6/22/2006 4:22:59 PM)
 * @author: Tracy LI
 */
public class StochFileWriter 
{
	private Simulation simulation = null;

/**
 * StochFileWriter constructor comment.
 */
public StochFileWriter(Simulation arg_simulation) 
{
	simulation = arg_simulation;
}


/**
 * Find all the dependent processes for a given process.
 * Creation date: (6/28/2006 4:16:55 PM)
 */
private Vector<String> getDependencies(JumpProcess process, Vector processList) throws Exception
{
	Vector result = new Vector();
	//get affected vars in the given process.
	String[] affectedVars = new String[process.getActions().size()];
	for(int i=0; i<process.getActions().size(); i++)
	{
		affectedVars[i] = ((Action)process.getActions().elementAt(i)).getVar().getName();
	}
	if(affectedVars.length > 0)
	{
		//go through all the processes to find dependants
		for(int i=0; i<processList.size(); i++)
		{
			if(!process.compareEqual(((JumpProcess)processList.elementAt(i))))//to avoid comparing with it's own
			{
				Expression probExp = ((JumpProcess)processList.elementAt(i)).getProbabilityRate();
				probExp = getSimulation().substituteFunctions(probExp).flatten();
				String[] vars = probExp.getSymbols();
				if((vars != null)&&(vars.length>0))
				{				
					if(hasCommonElement(affectedVars,vars))
						result.addElement(((JumpProcess)processList.elementAt(i)).getName());
				}
			}
		}
	}
	return result;
}


/**
 * Insert the method's description here.
 * Creation date: (6/22/2006 5:38:54 PM)
 */
public Simulation getSimulation()
{
	return simulation;
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
	for(int i=0; i<oriArray.length; i++)
		for(int j=0; j<comArray.length; j++)
			if(oriArray[i].equals(comArray[j]))
				return true;
	return false;
}


/**
 * Get infomation for stochasic input file.
 * Validate the information. If it is valid, a true value will be return.
 * The function will be called by writeStochInputFile().
 * Creation date: (6/23/2006 3:59:34 PM)
 * @return boolean
 */
public boolean initialize() throws Exception
{
	if (!getSimulation().getIsValid()) 
	{
		throw new MathException("Invalid simulation : "+getSimulation().getWarning());
	}
	if (getSimulation().getIsSpatial()) 
	{
		throw new MathException("Stochastic simulation for spacial problems is under development.");		
	}
	if (getSimulation().getMathDescription().hasFastSystems()) 
	{
		
		throw new MathException("Math description contains algebraic constraints, cannot create file.");
	}
	//check variables
	if(!getSimulation().getMathDescription().getVariables().hasMoreElements())
	{
		throw new MathException("Stochastic model has no variable.");
	}
	//check subDomain
	SubDomain subDomain = null;
	java.util.Enumeration e=getSimulation().getMathDescription().getSubDomains();
	if(e.hasMoreElements())
	{
		subDomain = (SubDomain)e.nextElement();
	}
	else throw new MathException("There is no sub domain.");
	//check jump processes
	if(subDomain.getJumpProcesses().size()<1)
	{
		throw new MathException("Stochastic model has no jump process.");
	}
	//TODO
	return true;
}

/**
 * check if the expression in probability rate contains illegal symbols
 * which should only includes variable names .
 * Creation date: (11/14/2006 6:28:35 PM)
 */
public boolean isValidProbabilityExpression(Expression probExp)
{
	String[] symbols = probExp.getSymbols();
	for(int i=0; symbols != null && i<symbols.length; i++)
	{
		if(getSimulation().getMathDescription().getVariable(symbols[i]) != null)
			continue;
		else
			return false;
	}
	return true;
}

/**
 * Insert the method's description here.
 * Creation date: (6/22/2006 5:36:03 PM)
 */
public void writeStochInputFile(PrintWriter pw) throws Exception 
{
	writeStochInputFile(pw, null);
}


/**
 * Write the model to a text file which serves as an input for Stochastic simulation engine.
 * Creation date: (6/22/2006 5:37:26 PM)
 */
public void writeStochInputFile(PrintWriter pw, String[] parameterNames) throws Exception,cbit.vcell.parser.ExpressionException
{
	if(initialize())
	{
		// Write control information
		pw.println("<control>");
		cbit.vcell.solver.SolverTaskDescription solverTaskDescription = getSimulation().getSolverTaskDescription();
		cbit.vcell.solver.TimeBounds timeBounds = solverTaskDescription.getTimeBounds();
		cbit.vcell.solver.OutputTimeSpec outputTimeSpec = solverTaskDescription.getOutputTimeSpec();
		ErrorTolerance errorTolerance = solverTaskDescription.getErrorTolerance();
		StochSimOptions stochOpt = solverTaskDescription.getStochOpt();
		pw.println("STARTING_TIME"+"\t"+ timeBounds.getStartingTime());
		pw.println("ENDING_TIME "+"\t"+ timeBounds.getEndingTime());
//		pw.println("MAX_ITERATION"+"\t"+outputTimeSpec.getKeepAtMost());
		pw.println("TOLERANCE "+"\t"+errorTolerance.getAbsoluteErrorTolerance());
		if(outputTimeSpec.isDefault())
			pw.println("SAMPLE_INTERVAL"+"\t"+((DefaultOutputTimeSpec)outputTimeSpec).getKeepEvery());
		else if (outputTimeSpec.isUniform())
			pw.println("SAVE_PERIOD"+"\t"+((UniformOutputTimeSpec)outputTimeSpec).getOutputTimeStep());
		pw.println("NUM_TRIAL"+"\t"+solverTaskDescription.getStochOpt().getNumOfTrials());

		if(stochOpt.isUseCustomSeed())
	  		pw.println("SEED"+"\t"+stochOpt.getCustomSeed());
	  	pw.println("</control>");
	  	pw.println();

	  	//write model information
	  	java.util.Enumeration e = getSimulation().getMathDescription().getSubDomains();//Model info. will be extracted from subDomain of mathDescription
	  	SubDomain subDomain = null;
	  	if(e.hasMoreElements())
			subDomain = (SubDomain)e.nextElement();
	  	if(subDomain != null)
	  	{
		  	pw.println("<model>");
			//  variables
			pw.println("<discreteVariables>");
			Vector varInis = subDomain.getVarIniConditions(); //There is only one subDomain for compartmental model
			if((varInis != null) && (varInis.size()>0))
		    {
			    pw.println("TotalVars"+"\t"+varInis.size());
			  	for(int i=0; i<varInis.size(); i++)
				{
			  		try{
			  			Expression iniExp = ((VarIniCondition)varInis.elementAt(i)).getIniVal();
			  			iniExp.bindExpression(getSimulation());
						iniExp = getSimulation().substituteFunctions(iniExp).flatten();
			  			double iniValue = iniExp.evaluateConstant();
			  			pw.println(((VarIniCondition)varInis.elementAt(i)).getVar().getName()+"\t"+Math.round(iniValue));
			  		}catch(cbit.vcell.parser.ExpressionException ex)
			  		{
			  			ex.printStackTrace();
			  			throw new MathFormatException("variable "+((VarIniCondition)varInis.elementAt(i)).getVar().getName()+"'s initial condition is required to be a constant.");
			  		}
				}
			}
			else pw.println("TotalVars"+"\t"+"0");
			pw.println("</discreteVariables>");
			pw.println();
			
			// jump processes	
			pw.println("<jumpProcesses>");
			Vector jumpProcesses = subDomain.getJumpProcesses();
			if((jumpProcesses != null) && (jumpProcesses.size()>0))
		    {
			    pw.println("TotalProcesses"+"\t"+jumpProcesses.size());
			    for(int i=0; i<jumpProcesses.size(); i++)
			    {
				    pw.println(((JumpProcess)jumpProcesses.elementAt(i)).getName());
				}
		    }
		    else pw.println("TotalProcesses"+"\t"+"0");
			pw.println("</jumpProcesses>");
			pw.println();

			// process description
			pw.println("<processDesc>");
			if((jumpProcesses != null) && (jumpProcesses.size()>0))
		    {
				pw.println("TotalDescriptions"+"\t"+jumpProcesses.size());
				for (int i=0; i<jumpProcesses.size(); i++)
				{
					JumpProcess temProc = (JumpProcess)jumpProcesses.elementAt(i);
					pw.println("JumpProcess"+"\t"+temProc.getName()); //jump process name
					Expression probExp = temProc.getProbabilityRate();
					try {
						probExp.bindExpression(getSimulation());
						probExp = getSimulation().substituteFunctions(probExp).flatten();
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
					pw.println("\t"+"Propensity"+"\t"+ probExp.infix()); //Propensity
					//effects					
					pw.println("\t"+"Effect"+"\t"+temProc.getActions().size());
					for(int j=0; j<temProc.getActions().size(); j++)
					{
						pw.print("\t\t"+((Action)temProc.getActions().elementAt(j)).getVar().getName()+"\t"+((Action)temProc.getActions().elementAt(j)).getOperation());
						pw.println("\t"+((Action)temProc.getActions().elementAt(j)).evaluateOperand());
						pw.println();
					}
					//dependencies
					Vector dependencies = getDependencies(temProc, jumpProcesses);
					if((dependencies != null) && (dependencies.size() > 0))
					{
						pw.println("\t"+"DependentProcesses"+"\t"+dependencies.size());
						for(int j=0; j<dependencies.size(); j++)
							pw.println("\t\t"+(dependencies.elementAt(j)));
					}
					else pw.println("\t"+"DependentProcesses"+"\t"+"0");
					pw.println();
				}
			}
		    else pw.println("TotalDescriptions"+"\t"+"0");
		    pw.println("</processDesc>");
			pw.println("</model>");	
	  	} //if (subDomain != null)
    } //if (initialize())
}

}