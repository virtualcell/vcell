/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver;

import java.io.Serializable;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.Matchable;

import cbit.vcell.math.VCML;

public class NonspatialStochHybridOptions implements Serializable, Matchable {

	private double epsilon = 100;
	private double lambda = 10;
	private double MSRTolerance = 1/epsilon;
	private double SDETolerance = 1e-4;
	
	public NonspatialStochHybridOptions() {
	}

	public NonspatialStochHybridOptions(double arg_epsilon, double arg_lambda, double arg_MSRTolerance, double arg_SDETolerance)
	{
		epsilon = arg_epsilon;
		lambda = arg_lambda;
		MSRTolerance = arg_MSRTolerance;
		SDETolerance = arg_SDETolerance;
	}

	public NonspatialStochHybridOptions(NonspatialStochHybridOptions sho)
	{
		epsilon = sho.epsilon;
		lambda = sho.lambda;
		MSRTolerance = sho.MSRTolerance;
		SDETolerance = sho.SDETolerance;
	}	
	
	public double getEpsilon() {
		return epsilon;
	}

	public double getLambda() {
		return lambda;
	}

	public double getMSRTolerance() {
		return MSRTolerance;
	}

	public double getSDETolerance() {
		return SDETolerance;
	}
	
	/**
	 * get VCML string for stochHybridOption
	 */
	public String getVCML() 
	{
		//
		// write format as follows:
		//
		//   StochSimOptions {
		//		Epsilon 100
		//		Lambda 10
		//		MSRTolerance 0.01
		//		SDETolerance 1e-4
		//   }
		// if useCustomSeed == false, customSeed shouldn't be compared
		// ODE/PDE applications don't need to take care of this
		//
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(VCML.StochHybridOptions+" "+VCML.BeginBlock+"\n");
		
		buffer.append("   " + VCML.Epsilon + " " + getEpsilon() + "\n");
		buffer.append("   " + VCML.Lambda + " " + getLambda() + "\n");
		buffer.append("   " + VCML.MSRTolerance + " " + getMSRTolerance() + "\n");
		buffer.append("   " + VCML.SDETolerance + " " + getSDETolerance() + "\n");
		
		buffer.append(VCML.EndBlock+"\n");

		return buffer.toString();
	}
	
	/**
	 * Read VCML to feed data into the class
	 */
	public void readVCML(CommentStringTokenizer tokens) throws DataAccessException
	{
		//
		// read format as follows:
		//
		//   StochSimOptions {
		//		UseCustomSeed false
		//		CustomSeed 0
		//		NumOfTrials 1
		//		Epsilon 100
		//		Lambda 10
		//		MSRTolerance 0.01
		//		SDETolerance 1e-4
		//   }
		//
		try {
			String token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.StochSimOptions)) {
				token = tokens.nextToken();
				if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
					throw new DataAccessException(
						"unexpected token " + token + " expecting " + VCML.BeginBlock); 
				}
			}
			while (tokens.hasMoreTokens()) {
				token = tokens.nextToken();
				if (token.equalsIgnoreCase(VCML.EndBlock)) {
					break;
				}
				if (token.equalsIgnoreCase(VCML.Epsilon)) {
					token = tokens.nextToken();
					double val3 = Double.parseDouble(token);
					if(val3 < 1 )
						throw new DataAccessException("unexpected token " + token + ", Minimum number of molecue is requied to be greater than or equal to 1. ");
					else
						epsilon = val3;
					continue;
				}
				if (token.equalsIgnoreCase(VCML.Lambda)) {
					token = tokens.nextToken();
					double val4 = Double.parseDouble(token);
					if(val4 <= 0 )
						throw new DataAccessException("unexpected token " + token + ", num of trials is requied to be greater than 0. ");
					else
						lambda = val4;
					continue;
				}
				if (token.equalsIgnoreCase(VCML.MSRTolerance)) {
					token = tokens.nextToken();
					double val5 = Double.parseDouble(token);
					if(val5 <= 0 )
						throw new DataAccessException("unexpected token " + token + ", Maximum allowed effect of slow reactions is requied to be greater than 0. ");
					else
						MSRTolerance = val5;
					continue;
				}
				if (token.equalsIgnoreCase(VCML.SDETolerance)) {
					token = tokens.nextToken();
					double val6 = Double.parseDouble(token);
					if(val6 <= 0 )
						throw new DataAccessException("unexpected token " + token + ", SDE allowed value of drift and diffusion errors is requied to be greater than 0. ");
					else
						SDETolerance = val6;
					continue;
				}
				throw new DataAccessException("unexpected identifier " + token);
			}
		} catch (Throwable e) {
			throw new DataAccessException(
				"line #" + (tokens.lineIndex()+1) + " Exception: " + e.getMessage()); 
		}
	}
	
	/**
	 * Checks for internal representation of objects
	 * @return boolean
	 * @param obj java.lang.Object
	 */
	public boolean compareEqual(Matchable obj) 
	{
		if (this == obj) {
			return true;
		}
		if (obj != null && obj instanceof NonspatialStochHybridOptions) {
			NonspatialStochHybridOptions hybridOpt = (NonspatialStochHybridOptions) obj;
			if (getEpsilon() != hybridOpt.getEpsilon()) return false;
			if (getLambda() != hybridOpt.getLambda()) return false;
			if (getMSRTolerance() != hybridOpt.getMSRTolerance()) return false;
			if (getSDETolerance() != hybridOpt.getSDETolerance()) return false;
			return true;
		}
		return false;
	}
	
}
