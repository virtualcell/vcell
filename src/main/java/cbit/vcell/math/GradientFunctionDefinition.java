/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math;

import cbit.vcell.math.ReservedVariable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionDomainException;
import cbit.vcell.parser.SimpleSymbolTable.SimpleSymbolTableFunctionEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry.Differentiable;
import cbit.vcell.parser.SymbolTableFunctionEntry.Evaluable;

public class GradientFunctionDefinition extends SimpleSymbolTableFunctionEntry implements Differentiable, Evaluable {
	
	private static final FunctionArgType[] ARGUMENT_TYPES = new FunctionArgType[] { FunctionArgType.NUMERIC, FunctionArgType.LITERAL};
	private static final String[] ARGUMENT_NAMES = new String[]{"VariableName","VectorComponent"};
	public final static String FUNCTION_name = "vcGrad";
	
	private static final int BOTH_NEIGHBORS = 0;
	private static final int NO_NEIGHBORS = 3;
	private static final int USE_ADJACENT_PLUS = 1;
	private static final int USE_ADJACENT = 2;
	public static final int GRADIENT_NUM_SPATIAL_ELEMENTS = 13;
	public static final String GRAD_MAGNITUDE = "m";
	public static final String GRAD_X = "x";
	public static final String GRAD_Y = "y";
	public static final String GRAD_Z = "z";
	  
	public GradientFunctionDefinition() {
		super(FUNCTION_name, 
				ARGUMENT_NAMES, 
				ARGUMENT_TYPES, 
				null,  // expression 
				null,  // units
				null); // namescope
	}

	public Expression differentiate(Expression[] args, String variable) throws ExpressionException {
		if (variable==null){
			throw new RuntimeException("cannot differentiate with respect to a null variable");
		}
		if (args.length!=2){
			throw new IllegalArgumentException("expecting 2 arguments for vcGrad()");
		}
		if (variable.equals(ReservedVariable.X.getName()) || variable.equals(ReservedVariable.Y.getName()) || variable.equals(ReservedVariable.Z.getName())){
			throw new ExpressionException("differentiation with respect to x,y,z not supported for vcGrad() function");
		}
		//
		// take advantage of commutative property of differential operators: d/dvar(grad(u)) = grad(du/dvar)
		// so, if du/dvar=constant then grad(du/dvar) = 0, since grad(constant)=0
		//
		Expression variableArgument = args[0];
		Expression exp = variableArgument.differentiate(variable).flatten();
		if (exp.isNumeric()){
			return new Expression(0.0);
		}else{
			throw new ExpressionException("differentiation with respect to '"+variable+"' not supported for function vcField() with time argument '"+variableArgument.infix()+"'");
		}
	}

	public double evaluateConstant(Expression[] args) throws ExpressionException {
		throw new ExpressionException("cannot evaluate vcGrad() to a constant value");
	}

	public double evaluateVector(Expression[] arguments, double[] values) throws ExpressionException {
		//
		//Assumes 13 blocks of double values arranged in order given below
		//p,xm,xp,ym,yp,zm,zp,xmm,xpp,ymm,ypp,zmm,zpp
		//All 13 blocks composed of [time,x,y,z] followed by data values
		//First grad argument is a code indicating the gradient function
		//"m" = magnitude(xyz), "x" = x gradient, "y" = y gradient, "z" = z gradient,
		//Second grad argument is variable to be evaluated
		//
		if(arguments.length != 2){
			throw new ExpressionException("vcGrad() function expetcs 2 arguments");
		}
		Expression argNode = arguments[0];
		String opName = arguments[1].infix().replace("'","");
		if(((values.length%GRADIENT_NUM_SPATIAL_ELEMENTS) != 0)){
			throw new ExpressionException("number of grad values is not an even multiple of "+GRADIENT_NUM_SPATIAL_ELEMENTS+"\n"+
											"current point plus 12 spatial neighbors)");
		}
		int numInternalArgs = values.length/GRADIENT_NUM_SPATIAL_ELEMENTS;
		double[] tempArgs = new double[numInternalArgs];
		double result = 0;
		boolean isMagnitude = opName.equalsIgnoreCase(GRAD_MAGNITUDE);
		double[] magnitudeComponents = (isMagnitude?new double[3]:null);
		for(int i=0;i<3;i+= 1){
			int axisCode =
				(opName.equalsIgnoreCase(GRAD_X) || (isMagnitude && i==0)?org.vcell.util.Coordinate.X_AXIS:0) +
				(opName.equalsIgnoreCase(GRAD_Y) || (isMagnitude && i==1)?org.vcell.util.Coordinate.Y_AXIS:0) +
				(opName.equalsIgnoreCase(GRAD_Z) || (isMagnitude && i==2)?org.vcell.util.Coordinate.Z_AXIS:0);

			int[] gradCase = getGradCase(values,axisCode,numInternalArgs);
			//
			if(gradCase == null){
				throw new FunctionDomainException("grad(A,B) unknown case for axis code "+axisCode);
			}else if(gradCase[0] == NO_NEIGHBORS){
				// =[p]= (bounded on both sides)
				//g(x) = 0;
				result = 0;
			}else if(gradCase[0] == USE_ADJACENT){
				// =[p0][p]= or =[p][p0]= (bound on 1 side with 1 adjacent available)
				//g(x) = (u(p0) - u(p))/(p0-p)
				System.arraycopy(values,gradCase[2]*numInternalArgs,tempArgs,0,tempArgs.length);
				double eV = argNode.evaluateVector(tempArgs);
				double eP = tempArgs[1+axisCode];
				System.arraycopy(values,gradCase[1]*numInternalArgs,tempArgs,0,tempArgs.length);
				double pV = argNode.evaluateVector(tempArgs);
				double pP = tempArgs[1+axisCode];
				result = (eV-pV)/(eP-pP);
			}else if(gradCase[0] == USE_ADJACENT_PLUS){
				// ...[p1][p0][p]= or =[p][p0][p1]... (bound on 1 side only)
				//g(x) = ((-3*u(p)) + (4*u(p0)) + (-u(p1))) / (p1-p)
				System.arraycopy(values,gradCase[3]*numInternalArgs,tempArgs,0,tempArgs.length);
				double eeV = argNode.evaluateVector(tempArgs);
				double eeP = tempArgs[1+axisCode];
				System.arraycopy(values,gradCase[2]*numInternalArgs,tempArgs,0,tempArgs.length);
				double eV = argNode.evaluateVector(tempArgs);
				//double eP = tempArgs[1+axisCode];
				System.arraycopy(values,gradCase[1]*numInternalArgs,tempArgs,0,tempArgs.length);
				double pV = argNode.evaluateVector(tempArgs);
				double pP = tempArgs[1+axisCode];
				result = ((-3*pV)+(4*eV)+(-eeV))/(eeP-pP);
			}else if(gradCase[0] == BOTH_NEIGHBORS){
				// [pm][p][pp] (not bound on either side)
				//g(x) = (u(pp) - u(pm))/(pp-pm)
				System.arraycopy(values,gradCase[3]*numInternalArgs,tempArgs,0,tempArgs.length);
				double eV = argNode.evaluateVector(tempArgs);
				double eP = tempArgs[1+axisCode];
				System.arraycopy(values,gradCase[1]*numInternalArgs,tempArgs,0,tempArgs.length);
				double wV = argNode.evaluateVector(tempArgs);
				double wP = tempArgs[1+axisCode];
				result = (eV-wV)/(eP-wP);			
			}

			if(!isMagnitude){
				break;
			}else{
				magnitudeComponents[i] = result;
			}
		}
		if(isMagnitude){
			result = Math.sqrt(
						(magnitudeComponents[0] *magnitudeComponents[0]) +
						(magnitudeComponents[1] *magnitudeComponents[1]) +
						(magnitudeComponents[2] *magnitudeComponents[2])
					);
		}
		return result;
	}

/**
 * Insert the method's description here.
 * Creation date: (2/10/2007 2:47:07 PM)
 */
private static int[] getGradCase(double[] args,int axisCode,int numInternalArgs) {


	final int COORD_OFFSET = 1;
	
	int axisOffset = axisCode*2;
	int wIndex = (1+0+axisOffset);
	boolean w = Double.isNaN(args[COORD_OFFSET+numInternalArgs*wIndex]);
	int eIndex = (1+1+axisOffset);
	boolean e = Double.isNaN(args[COORD_OFFSET+numInternalArgs*eIndex]);
	//
	//Both adjacent neighbors available [ok][p][ok]
	//
	if(!w && !e){
		return new int[] {BOTH_NEIGHBORS,wIndex,0,eIndex};
	}
	//
	//Neither adjacent neighbor available [block][p][block]
	//
	if(w && e){
		return new int[] {NO_NEIGHBORS,-1,-1,-1};
	}

	int wwIndex = (1+6+axisOffset);
	boolean ww = Double.isNaN(args[COORD_OFFSET+numInternalArgs*wwIndex]);
	int eeIndex = (1+7+axisOffset);
	boolean ee = Double.isNaN(args[COORD_OFFSET+numInternalArgs*eeIndex]);
	//
	//One adjacent neighbor and One adjacent-adjacent neighbor available [block][p][ok][ok] -or- [ok][ok][p][block]
	//
	if((!e && !ee)){
		return new int[] {USE_ADJACENT_PLUS,0,eIndex,eeIndex};
	}
	if((!w && !ww)){
		return new int[] {USE_ADJACENT_PLUS,0,wIndex,wwIndex};
	}
	//
	//One adjacent neighbor is available only [block][p][ok][block] -or- [block][ok][p][block]
	//
	if(!w){
		return new int[] {USE_ADJACENT,0,wIndex,-1};
	}
	if(!e){
		return new int[] {USE_ADJACENT,0,eIndex,-1};
	}		

	return null;
	
}
}
