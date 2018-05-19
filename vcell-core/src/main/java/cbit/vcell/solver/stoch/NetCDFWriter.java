/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.stoch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.vcell.util.Compare;

import cbit.vcell.math.Action;
import cbit.vcell.math.JumpProcess;
import cbit.vcell.math.MathException;
import cbit.vcell.math.StochVolVariable;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.VarIniCondition;
import cbit.vcell.math.VarIniCount;
import cbit.vcell.math.Variable;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.NonspatialStochSimOptions;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayLong;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;

/**
 * This class is used to write input file for stochastic hybrid solvers.
 * The input file will be in NetCDF format containing all the requred model information
 * and simulation control information.
 * simulation will be the only one input parameter in this class.
 * @author Tracy LI
 * @version 1.0 
 */

public class NetCDFWriter {

	private String filename = null;
	private SimulationTask simTask = null;
	// to store variables and their orders in the reactions. It is set to global in this
	// class, since it is useful in a few functions and we don't want to calculate it 
	// again and again. it is calculated in function getReactionRateLaws.
	private Hashtable<String,Integer>[]  varInProbOrderHash = null;
	private boolean bMessaging;

	/**
	 * another constructor
	 * @param arg_simulation
	 */
	public NetCDFWriter(SimulationTask simTask, String fn, boolean argMessaging) 
	{
		this.simTask = simTask;
		filename = fn;
		bMessaging = argMessaging;
	}

	/**
	 * Extract infomation from simulation to write hybrid input file.
	 * Validate the information. If it is valid, a true value will be return.
	 * The function will be called by writeHybridInputFile().
	 * Creation date: (5/22/2007 3:59:34 PM)
	 * @return boolean
	 */
	public boolean initialize() throws Exception
	{
		Simulation simulation = simTask.getSimulation();	
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
	
		return true;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (5/22/2007 5:36:03 PM)
	 */
	public void writeHybridInputFile() throws Exception 
	{
		writeHybridInputFile(null);
	}


	/**
	 * Write the model to a NetCDF file which serves as an input for stoch hybrid simulator.
	 * To write to a NetCDF file is a bit complicated. First, we have to create a NetCDF-3
	 * file. And then feed in the data.
	 * Creation date: (5/22/2007 5:36:03 PM)
	 */
	public void writeHybridInputFile(String[] parameterNames) throws Exception,cbit.vcell.parser.ExpressionException,IOException, MathException, InvalidRangeException
	{
		Simulation simulation = simTask.getSimulation();
		SimulationSymbolTable simSymbolTable = simTask.getSimulationJob().getSimulationSymbolTable();
		if(initialize())
		{
			//we need to get model and control information first
			NetcdfFileWriteable ncfile = NetcdfFileWriteable.createNew(filename, false);
			java.util.Enumeration<SubDomain> e = simulation.getMathDescription().getSubDomains();//Model info. will be extracted from subDomain of mathDescription
		  	SubDomain subDomain = e.nextElement();//remember we are dealing with compartmental model here. only 1 subdomain.
		  	JumpProcess reactions[] = (JumpProcess[])subDomain.getJumpProcesses().toArray(new JumpProcess[subDomain.getJumpProcesses().size()]);
		  	//get species variable names
		  	Variable[] variables = simSymbolTable.getVariables();
			String[] speciesNames = new String[variables.length];
		  	for(int i=0; i< variables.length; i++)
		  		speciesNames[i]=variables[i].getName();
		  	Expression probs[] = new Expression[reactions.length]; // the probabilities for reactions
		  	for(int i=0; i< reactions.length; i++) 
			{
				probs[i] = simSymbolTable.substituteFunctions(reactions[i].getProbabilityRate());
				probs[i] = probs[i].flatten();
			}
			VarIniCondition varInis[] = (VarIniCondition[])subDomain.getVarIniConditions().toArray(new VarIniCondition[subDomain.getVarIniConditions().size()]);
			Vector<Variable> vars = new Vector<Variable>(); // the non-constant stoch variables
			for(int i=0; i< varInis.length; i++)
			{
				if(varInis[i].getVar() instanceof StochVolVariable)
				{
					vars.addElement(varInis[i].getVar());
				}
			}
					
			//get reaction rate law types and rate constants
			ReactionRateLaw[] reactionRateLaws = getReactionRateLaws(probs);
			
		  	SolverTaskDescription solverTaskDescription = simulation.getSolverTaskDescription();
			TimeBounds timeBounds = solverTaskDescription.getTimeBounds();
			UniformOutputTimeSpec timeSpec = (UniformOutputTimeSpec)solverTaskDescription.getOutputTimeSpec();
			UniformOutputTimeSpec outputTimeSpec = ((UniformOutputTimeSpec)solverTaskDescription.getOutputTimeSpec());
			NonspatialStochSimOptions stochOpt = solverTaskDescription.getStochOpt();
					
			//create an empty NetCDF-3 file
			//define dimensions
			
			/* these sizes must match the buffers allocated in corresponding Fortran code -- see globalvariables.f90
			in numerics Hy3S/src directory */
			Dimension numTrial = ncfile.addDimension("NumTrials", (int)stochOpt.getNumOfTrials());
			Dimension numSpecies = ncfile.addDimension("NumSpecies", vars.size());
			Dimension numReactions = ncfile.addDimension("NumReactions", subDomain.getJumpProcesses().size());
			int outPoints = ((int)((timeBounds.getEndingTime()-timeBounds.getStartingTime())/outputTimeSpec.getOutputTimeStep()))+1;
			Dimension numTimePoints = ncfile.addDimension("NumTimePoints", outPoints );
			Dimension numModels = ncfile.addDimension("NumModels", 1); 
			Dimension numMaxDepList = ncfile.addDimension("NumMaxDepList", 6);
			Dimension numMaxStoichList = ncfile.addDimension("NumMaxStoichList", 25);
			Dimension stringLen = ncfile.addDimension("StringLen", 72);
			//define variables
			//jms info
			ArrayList<Dimension> dims = new ArrayList<Dimension>();
			dims.add(stringLen);
			
			if (bMessaging) {
				ncfile.addVariable("JMS_BROKER", DataType.CHAR, dims);
				ncfile.addVariable("JMS_USER", DataType.CHAR, dims);
				ncfile.addVariable("JMS_PASSWORD", DataType.CHAR, dims);
				ncfile.addVariable("JMS_QUEUE", DataType.CHAR, dims);  
				ncfile.addVariable("JMS_TOPIC", DataType.CHAR, dims);
				ncfile.addVariable("VCELL_USER", DataType.CHAR, dims);
				ncfile.addVariable("SIMULATION_KEY", DataType.INT, new ArrayList<Dimension>());
				ncfile.addVariable("JOB_INDEX", DataType.INT, new ArrayList<Dimension>());
			}

			//scalars
			ncfile.addVariable("TStart", DataType.DOUBLE, new ArrayList<Dimension>());
			ncfile.addVariable("TEnd", DataType.DOUBLE, new ArrayList<Dimension>());
			ncfile.addVariable("SaveTime", DataType.DOUBLE, new ArrayList<Dimension>());
			ncfile.addVariable("Volume", DataType.DOUBLE, new ArrayList<Dimension>());
			ncfile.addVariable("CellGrowthTime", DataType.DOUBLE, new ArrayList<Dimension>());
			ncfile.addVariable("CellGrowthTimeSD", DataType.DOUBLE, new ArrayList<Dimension>());
			ncfile.addVariable("ExpType", DataType.INT, new ArrayList<Dimension>());
			ncfile.addVariable("LastTrial", DataType.INT, new ArrayList<Dimension>());
			ncfile.addVariable("LastModel", DataType.INT, new ArrayList<Dimension>());
			ncfile.addVariable("MaxNumModels", DataType.INT, new ArrayList<Dimension>());
			ncfile.addVariable("NumModels", DataType.INT, new ArrayList<Dimension>());
			//variables with at least 1 dimension
			ArrayList<Dimension> dimspecies = new ArrayList<Dimension>();
			dimspecies.add(numSpecies);
			ArrayList<Dimension> dimreactions = new ArrayList<Dimension>();
			dimreactions.add(numReactions);
			ncfile.addVariable("SpeciesSplitOnDivision", DataType.INT, dimspecies);
			ncfile.addVariable("SaveSpeciesData", DataType.INT, dimspecies);
			ncfile.addVariable("Reaction_Rate_Laws", DataType.INT, dimreactions);
			ncfile.addVariable("Reaction_DListLen", DataType.INT, dimreactions);
			ncfile.addVariable("Reaction_StoichListLen", DataType.INT, dimreactions);
			ncfile.addVariable("Reaction_OptionalData", DataType.INT, dimreactions);
			dims.clear();
			dims.add(numReactions);
			dims.add(numMaxStoichList);
			ncfile.addVariable("Reaction_StoichCoeff", DataType.INT, dims);
			ncfile.addVariable("Reaction_StoichSpecies", DataType.INT, dims);
			dims.clear();
			dims.add(numReactions);
			dims.add(numMaxDepList);
			ncfile.addVariable("Reaction_DepList", DataType.INT, dims);
			dims.clear();
			dims.add(numReactions);
			dims.add(stringLen);
			ncfile.addVariable("Reaction_names", DataType.CHAR, dims);
			dims.clear();
			dims.add(numSpecies);
			dims.add(stringLen);
			ncfile.addVariable("Species_names", DataType.CHAR, dims);
			ncfile.addVariable("SpeciesIC", DataType.INT, dimspecies);
			dims.clear();
			dims.add(numReactions);
			dims.add(numMaxDepList);
			ncfile.addVariable("Reaction_Rate_Constants", DataType.DOUBLE, dims);
			
			//create the file
			try{
				ncfile.create();
			}catch(IOException ioe){
				ioe.printStackTrace(System.err);
				throw new IOException("Error creating hybrid file "+filename+": "+ioe.getMessage());
			}
			
			
			//write data to the NetCDF file
			try{
				// write jms info
				if (bMessaging) {
					ArrayChar.D1 jmsString = new ArrayChar.D1(stringLen.getLength());
					String jmshost = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimHostExternal);
					
					//
					// Used for new REST HTTP messaging api (USE THIS WHEN Hyrbid Solvers are compiled).
					//
					//String jmsrestport = PropertyLoader.getRequiredProperty(PropertyLoader.jmsRestPortExternal);
					//String jmsurl = jmshost+":"+jmsrestport;
					
					//
					// connect to messaging using legacy AMQP protocol instead of new REST api.  Needed for legacy pre-compiled solvers.
					//
					String jmsport = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimPortExternal);
					String jmsurl = "failover:(tcp://"+jmshost+":"+jmsport+")";
					
					
					jmsString.setString(jmsurl);
					ncfile.write("JMS_BROKER", jmsString);
					jmsString.setString(PropertyLoader.getRequiredProperty(PropertyLoader.jmsUser));
					ncfile.write("JMS_USER", jmsString);
				    String jmsPassword = PropertyLoader.getSecretValue(PropertyLoader.jmsPasswordValue, PropertyLoader.jmsPasswordFile);
					jmsString.setString(jmsPassword);
					ncfile.write("JMS_PASSWORD", jmsString);
					jmsString.setString(VCellQueue.WorkerEventQueue.getName());
					ncfile.write("JMS_QUEUE", jmsString);
					jmsString.setString(VCellTopic.ServiceControlTopic.getName());
					ncfile.write("JMS_TOPIC", jmsString);
					jmsString.setString(simulation.getVersion().getOwner().getName());
					ncfile.write("VCELL_USER", jmsString);
					
					ArrayInt.D0 scalarJMS = new ArrayInt.D0();
					scalarJMS.set(Integer.parseInt(simulation.getVersion().getVersionKey()+""));
					ncfile.write("SIMULATION_KEY", scalarJMS);
					scalarJMS.set(simTask.getSimulationJob().getJobIndex());
					ncfile.write("JOB_INDEX", scalarJMS);
				}

				ArrayDouble.D0 scalarDouble = new ArrayDouble.D0();
				//TStart, TEnd, SaveTime
				if((timeBounds.getEndingTime()>timeBounds.getStartingTime()) && (outputTimeSpec.getOutputTimeStep()>0))
				{
					scalarDouble.set(timeBounds.getStartingTime());
					ncfile.write("TStart", scalarDouble);
					scalarDouble.set(timeBounds.getEndingTime());
					ncfile.write("TEnd", scalarDouble);
					scalarDouble.set(outputTimeSpec.getOutputTimeStep());
					ncfile.write("SaveTime", scalarDouble);
				}
				else
				{
					System.err.println("Time setting error. Ending time smaller than starting time or save interval is not a positive value.");
					throw new RuntimeException("Time setting error. Ending time smaller than starting time or save interval is not a positive value.");
				}
				
				//Volume
				//we set volume to 1. This model file cannot support multi-compartmental sizes.
				//When writting the rate constants, we must take the volume into account according to the reaction type.
				scalarDouble.set(1);
				ncfile.write("Volume", scalarDouble);
				
				//CellGrowthTime, CellGrowthTimeSD, 
				scalarDouble.set(0);
				ncfile.write("CellGrowthTime", scalarDouble);
				ncfile.write("CellGrowthTimeSD", scalarDouble);
				
				//ExpType, Last Trial, Last Model, MaxNumModels, NumModels
				ArrayInt.D0 scalarInt = new ArrayInt.D0();
				scalarInt.set(0);
				ncfile.write("LastTrial", scalarInt);
				ncfile.write("LastModel", scalarInt);
				scalarInt.set(1);
				ncfile.write("ExpType", scalarInt);
				ncfile.write("MaxNumModels", scalarInt);
				ncfile.write("NumModels", scalarInt);
					
				//SpeciesSplitOnDivision
				ArrayInt A1 = new ArrayInt.D1(numSpecies.getLength());
				Index idx = A1.getIndex();
				for(int i=0; i<numSpecies.getLength(); i++)
				{
					A1.setInt(idx.set(i), 0);
				}
				ncfile.write("SpeciesSplitOnDivision", new int[1], A1);
			    
				
			    //SaveSpeciesData
			    ArrayInt A2 = new ArrayInt.D1(numSpecies.getLength());
			    idx = A2.getIndex();
			    for(int i=0; i<numSpecies.getLength(); i++)
			    {
			    	A2.setInt(idx.set(i), 1);
			    }
			    ncfile.write("SaveSpeciesData", new int[1], A2);
			    
			    
			    //Reaction_Rate_Laws
			    ArrayInt A3 = new ArrayInt.D1(numReactions.getLength());
			    idx = A3.getIndex();
			    for(int i=0; i<numReactions.getLength(); i++)
			    {
			    	A3.setInt(idx.set(i),reactionRateLaws[i].getLawType());
			    }
			    ncfile.write("Reaction_Rate_Laws", new int[1], A3);
			    
			    
			    //Reaction_DListLen
			    ArrayInt A4 = new ArrayInt.D1(numReactions.getLength());
			    idx = A4.getIndex();
			    for(int i=0; i<numReactions.getLength(); i++)
			    {
			    	if(reactionRateLaws[i].getLawType() == ReactionRateLaw.order_0)
			    		A4.setInt(idx.set(i),0);
			    	else if((reactionRateLaws[i].getLawType() == ReactionRateLaw.order_1)||(reactionRateLaws[i].getLawType() == ReactionRateLaw.order_2_1substrate)||(reactionRateLaws[i].getLawType() == ReactionRateLaw.order_3_1substrate))
			    		A4.setInt(idx.set(i),1);
			    	else if((reactionRateLaws[i].getLawType() == ReactionRateLaw.order_2_2substrate)||(reactionRateLaws[i].getLawType() == ReactionRateLaw.order_3_2substrate))
			    		A4.setInt(idx.set(i),2);
			    	else if(reactionRateLaws[i].getLawType() == ReactionRateLaw.order_3_3substrate)
			    		A4.setInt(idx.set(i),3);
			    }
			    ncfile.write("Reaction_DListLen", new int[1], A4);
			    
			    //Reaction_StoichListLen
			    ArrayInt A5 = new ArrayInt.D1(numReactions.getLength());
			    idx = A5.getIndex();
			    for(int i=0; i<numReactions.getLength(); i++)
			    {
			    	A5.setInt(idx.set(i),reactions[i].getActions().size());
			    }
			    ncfile.write("Reaction_StoichListLen", new int[1], A5);
			    
			    
			    //Reaction_OptionalData
			    ArrayInt A6 = new ArrayInt.D1(numReactions.getLength());
			    idx = A6.getIndex();
			    for(int i=0; i<numReactions.getLength(); i++)
			    {
			    	A6.setInt(idx.set(i), 0);
			    }
			    ncfile.write("Reaction_OptionalData", new int[1], A6);
			    			    
			    //Reaction_StoichCoeff
			    ArrayInt  A7 = new ArrayInt.D2(numReactions.getLength(), numMaxStoichList.getLength());
			    idx = A7.getIndex();
			    for(int i=0; i<numReactions.getLength(); i++)
				{
			    	Action [] actions = (Action[])reactions[i].getActions().toArray(new Action[reactions[i].getActions().size()]);
			    	for(int j=0; j<actions.length; j++)
			    	{
			    		try{
			    			actions[j].getOperand().evaluateConstant();
			    			int coeff = (int)Math.round(actions[j].getOperand().evaluateConstant());
			    			A7.setInt(idx.set(i,j), coeff);
			    		}catch(ExpressionException ex)
			    		{
			    			ex.printStackTrace(System.err);
			    			throw new ExpressionException(ex.getMessage());
			    		}
			    	}
				}
			    ncfile.write("Reaction_StoichCoeff", new int[2], A7);
			    			    
			    //Reaction_StoichSpecies
			    ArrayInt  A8 = new ArrayInt.D2(numReactions.getLength(), numMaxStoichList.getLength());
			    idx = A8.getIndex();
			    for(int i=0; i<numReactions.getLength(); i++)
				{
			    	ArrayList<Action> actions = reactions[i].getActions();
			    	for(int j=0; j<actions.size(); j++)
			    	{
			    		A8.setInt(idx.set(i, j), getVariableIndex(((Action)actions.get(j)).getVar().getName(), vars));
			    	}
				}
			    ncfile.write("Reaction_StoichSpecies", new int[2], A8);
			    
			    //Reaction_DepList
			    ArrayInt A9 = new ArrayInt.D2(numReactions.getLength(), numMaxDepList.getLength());
				idx = A9.getIndex();
				for(int i=0; i<numReactions.getLength(); i++)
				{
					ReactionRateLaw rl = reactionRateLaws[i];
					Hashtable<String, Integer> tem = varInProbOrderHash[i];
					Enumeration<String> varnames = tem.keys();
					if(rl.getLawType() == ReactionRateLaw.order_0)
					{
						//don't do anything here. 
					}
					else if((rl.getLawType() == ReactionRateLaw.order_1)||(rl.getLawType() == ReactionRateLaw.order_2_1substrate)||(rl.getLawType() == ReactionRateLaw.order_3_1substrate)||(rl.getLawType() == ReactionRateLaw.order_2_2substrate)||(rl.getLawType() == ReactionRateLaw.order_3_3substrate))
					{
						int j=0;
						while(varnames.hasMoreElements())
						{
							String name = varnames.nextElement();
							A9.setInt(idx.set(i,j),getVariableIndex(name, vars));
							j++;
						}
					}
					else if(rl.getLawType() == ReactionRateLaw.order_3_2substrate)
					{
						int order = 0;
						String highOrderName = "";
						String lowOrderName = "";
						//we must make sure to put the higher order species first.
						while(varnames.hasMoreElements())
						{
							lowOrderName = varnames.nextElement();
							if(tem.get(lowOrderName) > order)
							{
								String s = highOrderName;
								highOrderName = lowOrderName;
								lowOrderName = s;
								order = tem.get(highOrderName);
							}
						}
						A9.setInt(idx.set(i,0),getVariableIndex(highOrderName, vars));
						A9.setInt(idx.set(i,1),getVariableIndex(lowOrderName, vars));
					}
				}
				ncfile.write("Reaction_DepList",new int[2],A9);
				
			    //Reaction_names
			    ArrayChar A10 = new ArrayChar.D2(numReactions.getLength(), stringLen.getLength());
			    for(int i=0; i<numReactions.getLength(); i++)
				{
			    	String name = reactions[i].getName();
			    	int diff = stringLen.getLength()-name.length();
			    	if(diff >= 0)
			    	{
				    	for(int j=0; j<diff; j++)
				    	{
				    	    name = name + " ";
				    	}
				    	A10.setString( i, name);
			    	}
			    	else throw new RuntimeException("Name of Reaction:"+name+ " is too long. Please shorten to "+ stringLen.getLength()+" chars." );
				}
				ncfile.write("Reaction_names", A10);
												
			    //Species_names
			     ArrayChar A11 = new ArrayChar.D2(numSpecies.getLength(), stringLen.getLength());
			     for(int i=0; i<numSpecies.getLength(); i++)
				 {
			    	 String name = vars.elementAt(i).getName();
			    	 int diff = stringLen.getLength()-name.length();
			    	 if(diff >= 0)
			    	 {
				    	 for(int j=0; j<diff; j++)
				    	 {
				    		 name = name + " ";
				    	 }
				    	 A11.setString( i, name);
			    	 }
			    	 else throw new RuntimeException("Name of Species:"+name+ " is too long. Please shorten to "+ stringLen.getLength()+" chars." );
				 }
			     ncfile.write("Species_names", A11);
				
				//Species Initial Condition (in number of molecules).
			    //Species iniCondition are sampled from a poisson distribution(which has a mean of the current iniExp value)
			     RandomDataGenerator dist = new RandomDataGenerator();
			    if(stochOpt.isUseCustomSeed())
				{
					Integer randomSeed = stochOpt.getCustomSeed();
					if (randomSeed != null) {
						dist.reSeed(randomSeed);
					}
				}
				ArrayLong A12 = new ArrayLong.D1(numSpecies.getLength());
			    idx = A12.getIndex();
			    for(int i=0; i<numSpecies.getLength(); i++)
			    {
			    	try{
			    		VarIniCondition varIniCondition = subDomain.getVarIniCondition(vars.elementAt(i));
			  			Expression varIniExp = varIniCondition.getIniVal();
			  			varIniExp.bindExpression(simSymbolTable);
						varIniExp = simSymbolTable.substituteFunctions(varIniExp).flatten();
						double expectedCount = varIniExp.evaluateConstant();
				  		long varCount = 0;
				  		if(varIniCondition instanceof VarIniCount)
				  		{
				  			varCount = (long)expectedCount;
				  		}
				  		else
				  		{
				  			if(expectedCount > 0)
				  			{
				  				varCount = dist.nextPoisson(expectedCount);
				  			}
				  		}
				  		A12.setLong(idx.set(i),varCount);
			  		}catch(ExpressionException ex)
			  		{
			  			ex.printStackTrace(System.err);
			  			throw new ExpressionException(ex.getMessage());
			  		}
			    }
			    ncfile.write("SpeciesIC", new int[1], A12);
			    
				//Reaction_Rate_Constants(NumReactions, NumMaxDepList) ;
			    ArrayDouble A13 = new ArrayDouble.D2(numReactions.getLength(), numMaxDepList.getLength());
				idx = A13.getIndex();
				for(int i=0; i<numReactions.getLength(); i++)
				{
					ReactionRateLaw rl = reactionRateLaws[i];
					A13.setDouble(idx.set(i,0), rl.getRateConstant());
				}
				ncfile.write("Reaction_Rate_Constants", A13);
			    
			} catch (IOException ioe)
			{
				ioe.printStackTrace(System.err);
				throw new IOException("Error writing hybrid input file "+filename+": "+ioe.getMessage());
			}catch(InvalidRangeException ire)
			{
				ire.printStackTrace(System.err);
				throw new InvalidRangeException("Error writing hybrid input file "+filename+": "+ire.getMessage());
			}
			
			try {
			   ncfile.close();
			} catch (IOException ioe) {
			   throw new IOException("Error closing file "+filename+". "+ioe.getMessage());
			}
			
		}		
	}		
			
	/**
	 * 
	 * @param reacs
	 * @return ReactionRateLaw[]
	 */		
	private  ReactionRateLaw[] getReactionRateLaws(Expression[] probs) throws ExpressionException, MathException
	{
		SimulationSymbolTable simSymbolTable = simTask.getSimulationJob().getSimulationSymbolTable();
		
		ReactionRateLaw[] results = new ReactionRateLaw[probs.length];
		varInProbOrderHash = new Hashtable[probs.length];
		for(int i=0; i<probs.length; i++)
		{
			varInProbOrderHash[i] = new Hashtable<String, Integer>();
			results[i] = new ReactionRateLaw();
			Expression prob = probs[i];
			String[] symbols = prob.getSymbols();
			String[] varSymbols = getVariableSymbols(symbols);//get stoch variables involved in the reaction
			int maxOrder = 5;// max allowed order for each variable(species)
			int totalOrder = 0;// order of the reaction
			Expression coefExp = null;// the expression of the rate constant
			if(symbols != null)
			{
				if((varSymbols != null && !Compare.isEqual(symbols, varSymbols)) || varSymbols == null)
				{
					throw new MathException("Unrecognized symbols in propensity function "+ prob + ". Propensity function should contain stochastic variable symbols only.");
				}
				
				if(symbols != null && varSymbols != null)
				{
					PropensitySolver.PropensityFunction pf = PropensitySolver.solvePropensity(prob, varSymbols, maxOrder);
					//save info. into rate law array
					//get total order and save each var's order according to each reaction.
					for(int j=0; j<pf.getSpeciesOrders().length; j++)
					{
						varInProbOrderHash[i].put(pf.getSpeceisNames()[j], pf.getSpeciesOrders()[j]);
						totalOrder = totalOrder + pf.getSpeciesOrders()[j];
					}
					//get rate constant
					coefExp = new Expression(pf.getRateExpression());
				}
			}
			else
			{
				coefExp = new Expression(prob);
			}
			if(totalOrder == 0)
			{
				results[i].setLawType(ReactionRateLaw.order_0);
				coefExp = new Expression(coefExp.flatten().infix()+"/6.02e23");
				try{
					coefExp.bindExpression(simSymbolTable);
					coefExp = simSymbolTable.substituteFunctions(coefExp).flatten();
					double val = coefExp.evaluateConstant();
					results[i].setRateConstant(val);
				}catch(Exception e)
				{
					e.printStackTrace(System.err);
					throw new ExpressionException(e.getMessage());
				}
			}
			else if(totalOrder == 1)
			{
				results[i].setLawType(ReactionRateLaw.order_1);
				coefExp = coefExp.flatten();
				try{
					coefExp.bindExpression(simSymbolTable);
					coefExp = simSymbolTable.substituteFunctions(coefExp).flatten();
					double val = coefExp.evaluateConstant();
					results[i].setRateConstant(val);
				}catch(Exception e)
				{
					e.printStackTrace(System.err);
					throw new ExpressionException(e.getMessage());
				}
			}
			else if(totalOrder == 2)
			{
				if(varSymbols.length == 1) // second order, two same molecules 
				{
					//k= c*6.02e23/2, since in VCell, "/2" is already incorporated into c, so we don'y need to take care of this item from the conversion.
					coefExp = new Expression(coefExp.flatten().infix()+"*6.02e23");
					results[i].setLawType(ReactionRateLaw.order_2_1substrate);
					try{
						coefExp.bindExpression(simSymbolTable);
						coefExp = simSymbolTable.substituteFunctions(coefExp).flatten();
						double val = coefExp.evaluateConstant();
						results[i].setRateConstant(val);
					}catch(Exception e)
					{
						e.printStackTrace(System.err);
						throw new ExpressionException(e.getMessage());
					}
				}
				else if(varSymbols.length == 2) //second order, two different molecules
				{
					coefExp = new Expression(coefExp.flatten().infix()+"*6.02e23");
					results[i].setLawType(ReactionRateLaw.order_2_2substrate);
					try{
						coefExp.bindExpression(simSymbolTable);
						coefExp = simSymbolTable.substituteFunctions(coefExp).flatten();
						double val = coefExp.evaluateConstant();
						results[i].setRateConstant(val);
					}catch(Exception e)
					{
						e.printStackTrace(System.err);
						throw new ExpressionException(e.getMessage());
					}
				}
			}
			else if(totalOrder == 3)
			{
				if(varSymbols.length == 1) // third order, three same molecules
				{
					results[i].setLawType(ReactionRateLaw.order_3_1substrate);
					//use c directly.  "/3!" is already incorporated into c, so we compensate this item from the conversion.
					coefExp = new Expression(coefExp.flatten().infix()+"*6");
				}
				else if(varSymbols.length == 3) // third order, three different molecules
				{
					results[i].setLawType(ReactionRateLaw.order_3_3substrate);
				}
				else if(varSymbols.length == 2) // third order, two different molecules
				{
					results[i].setLawType(ReactionRateLaw.order_3_2substrate);
					//use c directly.  "/(2!*1!)" is already incorporated into c, so we compensate this item from the conversion.
					coefExp = new Expression(coefExp.flatten().infix()+"*2");
				}
				coefExp = coefExp.flatten();
				try{
					coefExp.bindExpression(simSymbolTable);
					coefExp = simSymbolTable.substituteFunctions(coefExp).flatten();
					double val = coefExp.evaluateConstant();
					results[i].setRateConstant(val);
				}catch(Exception e)
				{
					e.printStackTrace(System.err);
					throw new ExpressionException(e.getMessage());
				}
			}
			else //order more than 3, throw exception
			{
				throw new RuntimeException("Reaction order is more than 3, we couldn't solve it by Hybrid simulation.");
			}
		}
		return results;
	}
		
	private String[] getVariableSymbols(String[] symbols)
	{
		Simulation simulation = simTask.getSimulation();
		
		Vector<String> vars = new Vector<String>();
		if(symbols != null)
		{
			for(int i=0; i< symbols.length; i++)
			{
				Variable v =simulation.getMathDescription().getVariable(symbols[i]);
				if( (v != null) && (v instanceof StochVolVariable))
				{
					vars.add(symbols[i]);
				}
			}
			return vars.toArray(new String[vars.size()]);
		}
		else return vars.toArray(new String[0]);
	}
	
	//variable index based on VarIniCondition List
	private int getVariableIndex(String varname, Vector<Variable> vars)
	{
		for(int i = 0; i<vars.size(); i++)
		{
			if(varname.equals(vars.elementAt(i).getName()))
				return (i+1);
		}
		return -1;
	}
	
	class ReactionRateLaw
	{
		public static final int  order_0 = 1;
		public static final int  order_1 = 2;
		public static final int  order_2_2substrate = 3 ;
		public static final int  order_2_1substrate = 4;
		public static final int  order_3_1substrate = 14;
		public static final int  order_3_2substrate = 15;
		public static final int  order_3_3substrate = 16;
		
		private int lawType = order_2_2substrate; //set default rate law type to 2nd order reaction with two species involved.
		private double c = 0;
		
		//constructor
		public ReactionRateLaw()
		{}
		//getters and setters
		public int getLawType()
		{
			return lawType;
		}
		public double getRateConstant()
		{
			return c;
		}
		public void setLawType(int newType)
		{
			lawType = newType;
		}
		public void setRateConstant(double newConstant)
		{
			c = newConstant; 
		}
	}// end of inner class ReactionRateLaw
	
}//end of class NetCDFWriter
