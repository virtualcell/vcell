package cbit.vcell.solver.stoch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayInt;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;

import cbit.vcell.math.Action;
import cbit.vcell.math.JumpProcess;
import cbit.vcell.math.MathException;
import cbit.vcell.math.StochVolVariable;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.VarIniCondition;
import cbit.vcell.math.Variable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.StochSimOptions;
import cbit.vcell.solver.UniformOutputTimeSpec;
import flanagan.math.Fmath;
/**
 * This class is used to write input file for stochastic hybrid solvers.
 * The input file will be in NetCDF format containing all the requred model information
 * and simulation control information.
 * simulation will be the only one input parameter in this class.
 * @author Tracy LI
 * @version 1.0 
 */

public class NetCDFWriter {

	private Simulation simulation = null;
	private String filename = null;
	// to store variables and their orders in the reactions. It is set to global in this
	// class, since it is useful in a few functions and we don't want to calculate it 
	// again and again. it is calculated in function getReactionRateLaws.
	private Hashtable<String,Integer>[]  varInProbOrderHash = null; 
	/**
	 * constructor 
	 */
	public NetCDFWriter()
	{}
	/**
	 * another constructor
	 * @param arg_simulation
	 */
	public NetCDFWriter(Simulation arg_simulation, String fn) 
	{
		simulation = arg_simulation;
		filename = fn;
	}

	/**
	 * get simulation para.
	 */
	public Simulation getSimulation()
	{
		return simulation;
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
	public void writeHybridInputFile(String[] parameterNames) throws Exception,cbit.vcell.parser.ExpressionException,IOException, InvalidRangeException
	{
		if(initialize())
		{
			//we need to get model and control information first
			NetcdfFileWriteable ncfile = NetcdfFileWriteable.createNew(filename, false);
			java.util.Enumeration e = getSimulation().getMathDescription().getSubDomains();//Model info. will be extracted from subDomain of mathDescription
		  	SubDomain subDomain = (SubDomain)e.nextElement();//remember we are dealing with compartmental model here. only 1 subdomain.
		  	JumpProcess reactions[] = (JumpProcess[])subDomain.getJumpProcesses().toArray(new JumpProcess[subDomain.getJumpProcesses().size()]);
		  	Expression probs[] = new Expression[reactions.length]; // the probabilities for reactions
		  	for(int i=0; i< reactions.length; i++) 
			{
				probs[i] = getSimulation().substituteFunctions(reactions[i].getProbabilityRate());
				probs[i]=probs[i].flatten();
			}
			VarIniCondition varInis[] = (VarIniCondition[])subDomain.getVarIniConditions().toArray(new VarIniCondition[subDomain.getVarIniConditions().size()]);
			Vector vars = new Vector(); // the non-constant stoch variables
			for(int i=0; i< varInis.length; i++)
			{
				if(varInis[i].getVar() instanceof StochVolVariable)
				{
					vars.addElement(varInis[i].getVar());
				}
			}
			ReactionRateLaw[] reactionRateLaws = getReactionRateLaws(probs);
			
		  	cbit.vcell.solver.SolverTaskDescription solverTaskDescription = getSimulation().getSolverTaskDescription();
			cbit.vcell.solver.TimeBounds timeBounds = solverTaskDescription.getTimeBounds();
			cbit.vcell.solver.UniformOutputTimeSpec timeSpec = (UniformOutputTimeSpec)solverTaskDescription.getOutputTimeSpec();
			cbit.vcell.solver.UniformOutputTimeSpec outputTimeSpec = ((UniformOutputTimeSpec)solverTaskDescription.getOutputTimeSpec());
			StochSimOptions stochOpt = solverTaskDescription.getStochOpt();
					
			//create an empty NetCDF-3 file
			//define dimensions
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
			//scalars
			ncfile.addVariable("TStart", DataType.DOUBLE, new ArrayList());
			ncfile.addVariable("TEnd", DataType.DOUBLE, new ArrayList());
			ncfile.addVariable("SaveTime", DataType.DOUBLE, new ArrayList());
			ncfile.addVariable("Volume", DataType.DOUBLE, new ArrayList());
			ncfile.addVariable("CellGrowthTime", DataType.DOUBLE, new ArrayList());
			ncfile.addVariable("CellGrowthTimeSD", DataType.DOUBLE, new ArrayList());
			ncfile.addVariable("ExpType", DataType.INT, new ArrayList());
			ncfile.addVariable("LastTrial", DataType.INT, new ArrayList());
			ncfile.addVariable("LastModel", DataType.INT, new ArrayList());
			ncfile.addVariable("MaxNumModels", DataType.INT, new ArrayList());
			ncfile.addVariable("NumModels", DataType.INT, new ArrayList());
			//variables with at least 1 dimension
			ArrayList dimspecies = new ArrayList();
			dimspecies.add(numSpecies);
			ArrayList dimreactions = new ArrayList();
			dimreactions.add(numReactions);
			ncfile.addVariable("SpeciesSplitOnDivision", DataType.INT, dimspecies);
			ncfile.addVariable("SaveSpeciesData", DataType.INT, dimspecies);
			ncfile.addVariable("Reaction_Rate_Laws", DataType.INT, dimreactions);
			ncfile.addVariable("Reaction_DListLen", DataType.INT, dimreactions);
			ncfile.addVariable("Reaction_StoichListLen", DataType.INT, dimreactions);
			ncfile.addVariable("Reaction_OptionalData", DataType.INT, dimreactions);
			ArrayList dims = new ArrayList();
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
				throw new IOException(ioe.getMessage());
			}
			
			//write data to the NetCDF file
			try{
				ArrayDouble.D0 scalarDouble = new ArrayDouble.D0();
				//TStart, TEnd, SaveTime
				if(((timeBounds.getEndingTime()-timeBounds.getEndingTime())/outputTimeSpec.getOutputTimeStep())== ((timeBounds.getEndingTime()-timeBounds.getEndingTime())%outputTimeSpec.getOutputTimeStep()))
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
					System.err.println("(TEnd-TStart/SaveTime) should be an integer.");
					throw new RuntimeException("(TEnd-TStart/SaveTime) should be an integer.");
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
					A1.setInt(idx.set(i), 0);
				try{
					ncfile.write("SpeciesSplitOnDivision", new int[1], A1);
			    } catch (IOException ioe) {
			    	System.err.println("ERROR writing file");
			    } catch (InvalidRangeException ire) {
			    	ire.printStackTrace();
			    }
				
			    //SaveSpeciesData
			    ArrayInt A2 = new ArrayInt.D1(numSpecies.getLength());
			    idx = A2.getIndex();
			    for(int i=0; i<numSpecies.getLength(); i++)
			    	A2.setInt(idx.set(i), 1);
			    try{
					ncfile.write("SaveSpeciesData", new int[1], A2);
			    } catch (IOException ioe) {
			    	System.err.println("ERROR writing file");
			    } catch (InvalidRangeException ire) {
			    	ire.printStackTrace();
			    }
			    
			    //Reaction_Rate_Laws
			    
			    ArrayInt A3 = new ArrayInt.D1(numReactions.getLength());
			    idx = A3.getIndex();
			    for(int i=0; i<numReactions.getLength(); i++)
			    {
			    	A3.setInt(idx.set(i),reactionRateLaws[i].getLawType());
			    }
			    try{
					ncfile.write("Reaction_Rate_Laws", new int[1], A3);
			    } catch (IOException ioe) {
			    	System.err.println("ERROR writing file");
			    } catch (InvalidRangeException ire) {
			    	ire.printStackTrace();
			    }
			    
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
			    try{
					ncfile.write("Reaction_DListLen", new int[1], A4);
			    } catch (IOException ioe) {
			    	System.err.println("ERROR writing file");
			    } catch (InvalidRangeException ire) {
			    	ire.printStackTrace();
			    }
			    
			    //Reaction_StoichListLen
			    ArrayInt A5 = new ArrayInt.D1(numReactions.getLength());
			    idx = A5.getIndex();
			    for(int i=0; i<numReactions.getLength(); i++)
			    {
			    	A5.setInt(idx.set(i),reactions[i].getActions().size());
			    }
			    try{
					ncfile.write("Reaction_StoichListLen", new int[1], A5);
			    } catch (IOException ioe) {
			    	System.err.println("ERROR writing file");
			    } catch (InvalidRangeException ire) {
			    	ire.printStackTrace();
			    }
			    
			    //Reaction_OptionalData
			    ArrayInt A6 = new ArrayInt.D1(numReactions.getLength());
			    idx = A6.getIndex();
			    for(int i=0; i<numReactions.getLength(); i++)
			    {
			    	A6.setInt(idx.set(i), 0);
			    }
			    try{
					ncfile.write("Reaction_OptionalData", new int[1], A6);
			    } catch (IOException ioe) {
			    	System.err.println("ERROR writing file");
			    } catch (InvalidRangeException ire) {
			    	ire.printStackTrace();
			    }
			    
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
			    try{
					ncfile.write("Reaction_StoichCoeff", new int[2], A7);
			    } catch (IOException ioe) {
			    	System.err.println("ERROR writing file when processing reaction stoich coeff.");
			    } catch (InvalidRangeException ire) {
			    	ire.printStackTrace();
			    }
			    
			    //Reaction_StoichSpecies
			    ArrayInt  A8 = new ArrayInt.D2(numReactions.getLength(), numMaxStoichList.getLength());
			    idx = A8.getIndex();
			    for(int i=0; i<numReactions.getLength(); i++)
				{
			    	Vector actions = reactions[i].getActions();
			    	for(int j=0; j<actions.size(); j++)
			    	{
			    		A8.setInt(idx.set(i, j), getVariableIndex(((Action)actions.elementAt(j)).getVar().getName(), vars));
			    	}
				}
			    try{
					ncfile.write("Reaction_StoichSpecies", new int[2], A8);
			    } catch (IOException ioe) {
			    	System.err.println("ERROR writing file when processing reaction stoich species");
			    } catch (InvalidRangeException ire) {
			    	ire.printStackTrace();
			    }
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
							String name = (String)varnames.nextElement();
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
							lowOrderName = (String)varnames.nextElement();
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
				try {
				     ncfile.write("Reaction_DepList",new int[2],A9);
				} catch (IOException ioe) {
				     System.err.println("ERROR writing file when processing reaction Dep List.");
				} catch (InvalidRangeException ire) {
				     ire.printStackTrace();
				}
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
				try {
				     ncfile.write("Reaction_names", A10);
				} catch (IOException ioe) {
				     System.err.println("ERROR writing chars when processing reaction names.");
				} catch (InvalidRangeException ire) {
				     ire.printStackTrace();
				}
								
			    //Species_names
				try {
				     ArrayChar A11 = new ArrayChar.D2(numSpecies.getLength(), stringLen.getLength());
				     for(int i=0; i<numSpecies.getLength(); i++)
					 {
				    	 String name = ((Variable)vars.elementAt(i)).getName();
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
				} catch (IOException ioe) {
				     System.err.println("ERROR writing chars when processing species names.");
				} catch (InvalidRangeException ire) {
				     ire.printStackTrace();
				}
				//SpeciesIC(NumSpecies) ;
				ArrayInt A12 = new ArrayInt.D1(numSpecies.getLength());
			    idx = A12.getIndex();
			    for(int i=0; i<numSpecies.getLength(); i++)
			    {
			    	try{
			    		Expression varIni = subDomain.getVarIniCondition(((Variable)vars.elementAt(i)).getName()).getIniVal();
			    		varIni.bindExpression(simulation);
			    		varIni = simulation.substituteFunctions(varIni).flatten();
			    		int val = (int)Math.round(varIni.evaluateConstant());
			    		A12.setInt(idx.set(i),val);
			    	}catch(ExpressionException ex)
			    	{
			    		ex.printStackTrace(System.err);
			    		throw new ExpressionException(ex.getMessage());
			    	}
			    }
			    try{
					ncfile.write("SpeciesIC", new int[1], A12);
			    } catch (IOException ioe) {
			    	System.err.println("ERROR writing file when processing species initial conditions.");
			    } catch (InvalidRangeException ire) {
			    	ire.printStackTrace();
			    }
				//Reaction_Rate_Constants(NumReactions, NumMaxDepList) ;
			    ArrayDouble A13 = new ArrayDouble.D2(numReactions.getLength(), numMaxDepList.getLength());
				idx = A13.getIndex();
				for(int i=0; i<numReactions.getLength(); i++)
				{
					ReactionRateLaw rl = reactionRateLaws[i];
					A13.setDouble(idx.set(i,0), rl.getRateConstant());
				}
				try{
					ncfile.write("Reaction_Rate_Constants", A13);
			    } catch (IOException ioe) {
			    	System.err.println("ERROR writing file when processing reaction rate constants.");
			    } catch (InvalidRangeException ire) {
			    	ire.printStackTrace();
			    }
			    
			}catch(InvalidRangeException ire)
			{
				ire.printStackTrace(System.err);
				throw new InvalidRangeException(ire.getMessage());
			}
			
			try {
			   ncfile.close();
			} catch (IOException ioe) {
			   ioe.printStackTrace();
			}
		}		
	}		
			
	/**
	 * 
	 * @param reacs
	 * @return ReactionRateLaw[]
	 */		
	private  ReactionRateLaw[] getReactionRateLaws(Expression[] probs) throws ExpressionException
	{
		ReactionRateLaw[] results = new ReactionRateLaw[probs.length];
		varInProbOrderHash = new Hashtable[probs.length];
		for(int i=0; i<probs.length; i++)
		{
			varInProbOrderHash[i] = new Hashtable<String, Integer>();
			results[i] = new ReactionRateLaw();
			Expression prob = probs[i];
			String[] symbols = prob.getSymbols();
			String[] varSymbols = getVariableSymbols(symbols);//get variables involved in the reaction
			
			//get orders of variables in the reaction. useful when reactions are mass actions
			//Meanwhile, also get coefficient for the reaction to infer the rate constant.
			//the differentiate will remove sth. like X*(X-1) or X*2-X or X^2...etc from the prob expression.
			//useful when reacions are mass actions
			Expression coefExp = null; //coefficient
			Expression diffExp = new Expression(prob); //probability expression
			for (int j = 0; j < varSymbols.length; j++) {
			      String var = varSymbols[j];
			      try{  
				      diffExp = diffExp.differentiate(var).flatten();
				      varInProbOrderHash[i].put(var,1);
				      while (diffExp.hasSymbol(var) && varInProbOrderHash[i].get(var)<5){
				          varInProbOrderHash[i].put(var,varInProbOrderHash[i].get(var)+1);
				          diffExp = diffExp.differentiate(var).flatten();
				      }
			      }catch(ExpressionException e)
			      {
			    	  e.printStackTrace(System.err);
			    	  throw new ExpressionException(e.getMessage());
			      }
				      
			      System.out.println("var "+varSymbols[j]+" has order "+varInProbOrderHash[i].get(var));     
			}
			coefExp = new Expression(diffExp);
			//remove the factors from differentiation in coefficient. e.g. when differentiate a X^3, we will get a 3! in coefficient
			double factor=1;
			for (int k = 0; k < varSymbols.length; k++) {
			      int order = varInProbOrderHash[i].get(varSymbols[k]);
			      factor = factor * new Fmath().factorial(order);
			}
			coefExp = new Expression(coefExp.infix()+"/"+factor);
			System.out.println("coefficient = "+coefExp.flatten().infix());
			//save info. into rate law array
			int totalOrder = 0;
			for(int j=0; j<varSymbols.length; j++)
			{
				totalOrder = totalOrder + varInProbOrderHash[i].get(varSymbols[j]);
			}
			if(totalOrder == 0)
			{
				results[i].setLawType(ReactionRateLaw.order_0);
				coefExp = new Expression(coefExp.flatten().infix()+"/6.02e23");
				try{
					coefExp.bindExpression(simulation);
					coefExp = simulation.substituteFunctions(coefExp).flatten();
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
					coefExp.bindExpression(simulation);
					coefExp = simulation.substituteFunctions(coefExp).flatten();
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
					//k= c*6.02e23/2, since in VCell, "/2" is already incorporated into c, so we remove this item from the conversion.
					coefExp = new Expression(coefExp.flatten().infix()+"*6.02e23");
					results[i].setLawType(ReactionRateLaw.order_2_1substrate);
					try{
						coefExp.bindExpression(simulation);
						coefExp = simulation.substituteFunctions(coefExp).flatten();
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
						coefExp.bindExpression(simulation);
						coefExp = simulation.substituteFunctions(coefExp).flatten();
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
					coefExp.bindExpression(simulation);
					coefExp = simulation.substituteFunctions(coefExp).flatten();
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
		Vector vars = new Vector();
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
			return (String[])vars.toArray(new String[vars.size()]);
		}
		else return (String[])vars.toArray(new String[0]);
	}
	
	//variable index based on VarIniCondition List
	private int getVariableIndex(String varname, Vector vars)
	{
		for(int i = 0; i<vars.size(); i++)
		{
			if(varname.equals(((Variable)vars.elementAt(i)).getName()))
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
