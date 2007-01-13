package cbit.vcell.solver.stoch;
import cbit.vcell.math.JumpProcess;
import cbit.vcell.math.Action;
import cbit.vcell.math.MathException;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.VarIniCondition;
import cbit.vcell.simulation.DefaultOutputTimeSpec;
import cbit.vcell.simulation.ErrorTolerance;
import cbit.vcell.simulation.Simulation;
import cbit.vcell.simulation.SolverTaskDescription;
import cbit.vcell.simulation.TimeBounds;

import java.io.*;
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
 * Find all the dependent processes for a give process.
 * Creation date: (6/28/2006 4:16:55 PM)
 */
private Vector getDependencies(JumpProcess process, Vector processList) 
{
	Vector result = new Vector();
	//get affected vars in the given process.
	String[] affectedVars = new String[process.getActions().size()];
	for(int i=0; i<process.getActions().size(); i++)
	{
		affectedVars[i] = ((Action)process.getActions().elementAt(i)).getVar().getName();
	}
	//go through all the processes to find dependants
	for(int i=0; i<processList.size(); i++)
	{
		if(process.getName().compareTo(((JumpProcess)processList.elementAt(i)).getName())!=0)//to avoid comparing with it's own
		{
			String[] vars = ((JumpProcess)processList.elementAt(i)).getProbabilityRate().getSymbols();
			if((vars != null)&&(vars.length>0))
			{				
				if(hasCommonElement(affectedVars,vars))
					result.addElement(((JumpProcess)processList.elementAt(i)).getName());
			}
			else	result.addElement(((JumpProcess)processList.elementAt(i)).getName());
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
			if(oriArray[i].compareTo(comArray[j])==0)
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
public void writeStochInputFile(PrintWriter pw, String[] parameterNames) throws Exception
{
	if(initialize())
	{
		// Write control information
		pw.println("<control>");
		SolverTaskDescription solverTaskDescription = getSimulation().getSolverTaskDescription();
		TimeBounds timeBounds = solverTaskDescription.getTimeBounds();
		DefaultOutputTimeSpec outputTimeSpec = ((DefaultOutputTimeSpec)solverTaskDescription.getOutputTimeSpec());
		ErrorTolerance errorTolerance = solverTaskDescription.getErrorTolerance();
		pw.println("STARTING_TIME"+"\t"+ timeBounds.getStartingTime());
		//pw.println("ENDING_TIME " + timeBounds.getEndingTime());
		//pw.println("ENDING_TIME "+"\t"+"75"); //for time =75 k134=1 k2=10 trial=1000
		pw.println("ENDING_TIME "+"\t"+ timeBounds.getEndingTime());
		pw.println("MAX_ITERATION"+"\t"+outputTimeSpec.getKeepAtMost());
		pw.println("TOLERANCE "+"\t"+errorTolerance.getAbsoluteErrorTolerance());
		pw.println("SAMPLE_INTERVAL"+"\t"+outputTimeSpec.getKeepEvery());
		pw.println("NUM_TRIAL"+"\t"+"1");//TODO: should get from user
	  	pw.println("MAX_NUM_MOLECUES "+"\t"+"30");//TODO: should get from user
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
			double[] vals;//need to be assigned values
			if((varInis != null) && (varInis.size()>0))
		    {
			    pw.println("TotalVars"+"\t"+varInis.size());
			  	for(int i=0; i<varInis.size(); i++)
				{
					pw.println(((VarIniCondition)varInis.elementAt(i)).getVar().getName()+"\t"+((VarIniCondition)varInis.elementAt(i)).evaluateIniVal());//TODO:to get the variable's contant values
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
					pw.println("\t"+"Propensity"+"\t"+temProc.getProbabilityRate().infix()); //Propensity
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
							pw.println("\t\t"+((String)dependencies.elementAt(j)));
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