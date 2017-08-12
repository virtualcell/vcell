/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.vcell.optimization.DefaultOptSolverCallbacks;
import org.vcell.optimization.OptSolverCallbacks;

import cbit.vcell.opt.ExplicitFitObjectiveFunction;
import cbit.vcell.opt.OptSolverResultSet.OptRunResultSet;
import cbit.vcell.opt.OptSolverResultSet.ProfileDistribution;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.opt.solvers.PowellOptimizationSolver;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

public class ReactionDominantTest 
{
	public static final int DATA_LENGTH = 300;
	public static final String Fbleached_bleachFast = "(Omega + alpha*W)/Omega_c - Omega/Omega_c*(1-alpha)*(1/(1+Koff/Kon_star))*exp(-Koff*t)";
	public static final String Funbleached_bleachFast = "(Omega + alpha*W)/Omega_c + Omega/Omega_c*(1-alpha)*(1/(1+Koff/Kon_star))*exp(-Koff*t)";
	public static final String p = "1/(1+(Kon_star/Koff))";
	public static final String epsilon = "W/Omega_c";
	public static final String Fbleached_diffFast = "p*alpha^epsilon+(1-p)*(1-epsilon+alpha*epsilon)-(1-p)*(1-alpha)*(1-epsilon)*exp(-Koff*t)";
	public static final String Funbleached_diffFast = " p*alpha^epsilon+(1-p)*(1-epsilon+alpha*epsilon)+(1-p)*epsilon*(1-alpha)*exp(-Koff*t)";
	
	public static final Parameter para_alpha = new Parameter("alpha", 1e-6, 1, 1.0, 0.367879);
	public static final Parameter para_Kon_star = new Parameter("Kon_star", 1e-6, 100, 1.0, 0.1);
	public static final Parameter para_Koff = new Parameter("Koff", 1e-6, 100, 1.0, 0.1);
	public static final int para_idx_alpha = 0;
	public static final int para_idx_Kon_Star = 1;
	public static final int para_idx_Koff = 2;
	
	public static final String strOmega = "Omega";
	public static final String strW = "W";
	public static final String strOmega_c = "Omega_c";
	public static final int Omega = 6280;
	public static final int W = 81;
	public static final int Omega_c = 6361;
	
	private double[] t = null;
	private double[] data_bleached = null;
	private double[] data_unbleached = null;
	private String fileDir = "C:\\VirtualMicroscopy\\testReactionDominant\\";
	
	public final String inFilename = fileDir + "\\test1_diffusionfast.txt";
	
	
	private void readData() throws IOException
	{
		t = new double[DATA_LENGTH];
		data_bleached = new double[DATA_LENGTH];
		data_unbleached = new double[DATA_LENGTH];
		
		File inputFile = new File(inFilename);
		Scanner input = new Scanner(inputFile);
		int counter = 0;
		while(input.hasNext())
		{
			 t[counter] = input.nextDouble();
			 data_bleached[counter] = input.nextDouble();
			 data_unbleached[counter] = input.nextDouble();
			 counter ++;
		}
		input.close();
	}
	
	private OptimizationResultSet solve() throws ExpressionException, IOException
	{
		Expression Fbleached_bleachFastExp = new Expression(Fbleached_bleachFast);
		Expression OmegaExp = new Expression(strOmega);
		Expression Omega_cExp = new Expression(strOmega_c);
		Expression WExp = new Expression(strW);
		Fbleached_bleachFastExp.substituteInPlace(OmegaExp, new Expression(Omega));
		Fbleached_bleachFastExp.substituteInPlace(Omega_cExp, new Expression(Omega_c));
		Fbleached_bleachFastExp.substituteInPlace(WExp, new Expression(W));
		
		Parameter parameters[] = new Parameter[] {para_alpha, para_Kon_star, para_Koff};
//		Expression Fbleached_bleachFast = Fbleached_bleachFastExp.flatten();
		
		//choose optimization solver, currently we have Powell and CFSQP 
		PowellOptimizationSolver optService = new PowellOptimizationSolver();
		OptimizationSpec optSpec = new OptimizationSpec();
		//create simple reference data
		double[][] realData = new double[2][t.length];
		for(int i=0; i<t.length; i++)
		{
			realData[0][i] = t[i];
			realData[1][i]= data_bleached[i];
		}
		String[] colNames = new String[]{"t", "intensity"};
		double[] weights = new double[]{1.0,1.0};
		SimpleReferenceData refData = new SimpleReferenceData(colNames, weights, realData);
		//send to optimization service	
		ExplicitFitObjectiveFunction.ExpressionDataPair oneExpDataPair= new ExplicitFitObjectiveFunction.ExpressionDataPair(Fbleached_bleachFastExp.flatten(),1);
		ExplicitFitObjectiveFunction.ExpressionDataPair[] expDataPairs = new ExplicitFitObjectiveFunction.ExpressionDataPair[]{oneExpDataPair};
		optSpec.setObjectiveFunction(new ExplicitFitObjectiveFunction(expDataPairs, refData));
		optSpec.setComputeProfileDistributions(true);
		// Add parameters to the optimizationSpec
		// get the initial guess to send it to the f() function. ....
		for (int i = 0; i < parameters.length; i++) {
			optSpec.addParameter(parameters[i]);
		}
		//Parameters in OptimizationSolverSpec are solver type and objective function change tolerance. 
		OptimizationSolverSpec optSolverSpec = new OptimizationSolverSpec(OptimizationSolverSpec.SOLVERTYPE_POWELL,0.000001);
		OptSolverCallbacks optSolverCallbacks = new DefaultOptSolverCallbacks();
		OptimizationResultSet optResultSet = null;
		optResultSet = optService.solve(optSpec, optSolverSpec, optSolverCallbacks);
		
		String[] paramNames = optResultSet.getOptSolverResultSet().getParameterNames();
		ArrayList<ProfileDistribution> profileDistributionList = optResultSet.getOptSolverResultSet().getProfileDistributionList();
		for(int i=0; i<profileDistributionList.size(); i++)
		{
			outputProfileLikelihood(profileDistributionList.get(i), i, paramNames[i], new File(fileDir));
		}
		
		return optResultSet;
	}
	

	private void outputProfileLikelihood(ProfileDistribution pd, int paramIdx, String fixedParamName, File outputDir) 
	{
		try{
			System.out.println("Writing profile likelihood...");
			//output results
			String outFileName = outputDir.getAbsolutePath() + "\\" +fixedParamName +"_profileLikelihood" +".txt"; 
			File outFile = new File(outFileName);
			FileWriter fstream = new FileWriter(outFile);
	        BufferedWriter out = new BufferedWriter(fstream);
	        //output profile
	        out.write("Log base 10 param value" + "\t" + "error");
	        ArrayList<OptRunResultSet> orrs = pd.getOptRunResultSetList();
	        for(int i=0; i < orrs.size(); i++)
	        {
	        	out.newLine();
	        	String rowStr = Math.log10(orrs.get(i).getParameterValues()[paramIdx]) + "\t" + orrs.get(i).getObjectiveFunctionValue();
	        	out.write(rowStr);
	        }
		    out.close();
		    System.out.println("Output is done. Restults saved to " + outFileName);
		}catch(IOException e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	public static void main(String[] args)
	{
		try
		{
			ReactionDominantTest test = new ReactionDominantTest();
			test.readData();
			test.solve();
			
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
}
