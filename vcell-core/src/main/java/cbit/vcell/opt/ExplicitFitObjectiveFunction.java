/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.opt;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;

import cbit.function.DefaultScalarFunction;
import cbit.function.ScalarFunction;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import scala.collection.mutable.HashSet;
/**
 * Insert the type's description here.
 * Creation date: (8/3/2005 12:09:38 PM)
 * @author: Jim Schaff
 * Modified the data structure on March 1st, to take multiple explicitFunctions 
 * to fit referenceData. the function and the data to be fit are stored in a data 
 * structure ExpressionDataPair.
 */
public class ExplicitFitObjectiveFunction extends ObjectiveFunction {
	private ExpressionDataPair[] funcDataPairs = null;
	private SimpleReferenceData simpleReferenceData = null;

public class ExplicitFitScalarFunction extends DefaultScalarFunction {

	@Override
	public double f(double[] x) {
		int timeIndex = simpleReferenceData.findColumn("t");
		if (timeIndex < 0){
			throw new RuntimeException("did not find independent variable named 't' in ExplicitFitScalarFunction");
		}
		double[] timeData = simpleReferenceData.getDataByColumn(timeIndex);
		double[] symbolValues = new double[1 + x.length];
		for (int i=0;i<x.length;i++){
			symbolValues[i+1] = x[i];
		}
		
		double weightedSquaredErrors = 0;
		for (ExpressionDataPair pair : funcDataPairs){
			double[] refData = simpleReferenceData.getDataByColumn(pair.referenceDataIndex);
			Weights weights = simpleReferenceData.getWeights();
			if (weights instanceof ElementWeights){
				ElementWeights elementWeights = (ElementWeights)weights;
				for (int i=0;i<timeData.length;i++){
					symbolValues[0] = timeData[i];
					try {
						double functionVal = pair.fitFunction.evaluateVector(symbolValues);
						double dataVal = refData[i];
						double weightedError = (dataVal - functionVal)*elementWeights.getWeight(i, pair.referenceDataIndex-1);
						weightedSquaredErrors += (weightedError*weightedError);
					} catch (ExpressionException e) {
						e.printStackTrace();
					}
				}
			}else if (weights instanceof VariableWeights){
				VariableWeights variableWeights = (VariableWeights)weights;
				double weight = variableWeights.getWeightByVarIdx(pair.referenceDataIndex-1);
				for (int i=0;i<timeData.length;i++){
					symbolValues[0] = timeData[i];
					try {
						double functionVal = pair.fitFunction.evaluateVector(symbolValues);
						double dataVal = refData[i];
						double weightedError = (dataVal - functionVal)*weight;
						weightedSquaredErrors += (weightedError*weightedError);
					} catch (ExpressionException e) {
						e.printStackTrace();
					}
				}
			}else if (weights instanceof TimeWeights){
				TimeWeights timeWeights = (TimeWeights)weights;
				for (int i=0;i<timeData.length;i++){
					symbolValues[0] = timeData[i];
					try {
						double functionVal = pair.fitFunction.evaluateVector(symbolValues);
						double dataVal = refData[i];
						double weightedError = (dataVal - functionVal)*timeWeights.getWeightByTimeIdx(i);
						weightedSquaredErrors += (weightedError*weightedError);
					} catch (ExpressionException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return Math.sqrt(weightedSquaredErrors);
	}

	@Override
	public int getNumArgs() {
		HashSet<String> symbolHash = new HashSet<String>();
		for (ExpressionDataPair pair : funcDataPairs){
			String[] symbols = pair.fitFunction.getSymbols();
			for (String symbol : symbols){
				symbolHash.add(symbol);
			}
		}
		return symbolHash.size();
	}
	
}

public static class ExpressionDataPair 
{
	private Expression fitFunction = null;
	private int referenceDataIndex = -1; 
	public ExpressionDataPair(Expression fnExp, int refDataIndex) {
		this.fitFunction = fnExp;
		this.referenceDataIndex = refDataIndex;
	}
	
	public Expression getFitFunction()
	{
		return fitFunction;
	}
	public int getReferenceDataIndex()
	{
		return referenceDataIndex;
	}
}


public ExplicitFitObjectiveFunction(ExpressionDataPair[] fnDataPair, ReferenceData argReferenceData) {
	super();
	this.funcDataPairs = fnDataPair;
	this.simpleReferenceData = new SimpleReferenceData(argReferenceData);
}


/**
 * Insert the method's description here.
 * Creation date: (8/3/2005 1:17:52 PM)
 * @param tokens cbit.vcell.math.CommentStringTokenizer
 */
public static ExplicitFitObjectiveFunction fromVCML(CommentStringTokenizer tokens) throws DataAccessException {
//	try {
//		String token = tokens.nextToken();
//		if (!token.equalsIgnoreCase("{")){
//			throw new DataAccessException("unexpected token "+token+" expecting "+"{");
//		}
//		MathDescription mathDescription = new MathDescription("odeSystem");
//		mathDescription.setGeometry(new cbit.vcell.geometry.Geometry("geometry",0));
//		mathDescription.read_database(tokens);
//
//		SimpleReferenceData simpleReferenceData = SimpleReferenceData.fromVCML(tokens);
//
//		// read "}" for end of file
//		token = tokens.nextToken();
//		if (!token.equals("}")){
//			throw new RuntimeException("unexpected symbol '"+token+"', expecting '}'");
//		}
//		
//		ExplicitFitObjectiveFunction odeObjectiveFunction = new ExplicitFitObjectiveFunction();
//		odeObjectiveFunction.mathDescription = mathDescription;
//		odeObjectiveFunction.simpleReferenceData = simpleReferenceData;
//		return odeObjectiveFunction;
//
//	}catch (DataAccessException e){
//		throw e;
//	}catch (Throwable e){
//		e.printStackTrace(System.out);
//		throw new cbit.vcell.server.DataAccessException("line #"+(tokens.lineIndex()+1)+" Exception: "+e.getMessage());
//	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 2:30:24 PM)
 * @param issueList java.util.Vector
 */
public void gatherIssues(IssueContext issueContext, java.util.List<Issue> issueList) {
//	//
//	// check for a data column named "t"
//	//
//	if (simpleReferenceData.findColumn("t")<0){
//		issueList.add(new cbit.util.Issue(this,"objectiveFunction","missing time data column with name 't'",cbit.util.Issue.SEVERITY_ERROR));
//	}
//	//
//	// for those columns that are not "t", check for a corresponding math description Function or VolumeVariable
//	//
//	String[] columnNames = simpleReferenceData.getColumnNames();
//	for (int i = 0; i < columnNames.length; i++){
//		if (columnNames[i].equals("t")){
//			continue;
//		}
//		cbit.vcell.math.Variable mathVar = mathDescription.getVariable(columnNames[i]);
//		if (mathVar==null){
//			issueList.add(new cbit.util.Issue(this,"objectiveFunction","data column '"+columnNames[i]+"' not found in math model",cbit.util.Issue.SEVERITY_ERROR));
//		}else if (!(mathVar instanceof cbit.vcell.math.VolVariable) && !(mathVar instanceof cbit.vcell.math.Function)){
//			issueList.add(new cbit.util.Issue(this,"objectiveFunction","data column '"+columnNames[i]+"' not a variable or function in math model",cbit.util.Issue.SEVERITY_ERROR));
//		}
//	}
//	if (simpleReferenceData.findColumn("t")<0){
//		issueList.add(new cbit.util.Issue(this,"objectiveFunction","missing time data column with name 't'",cbit.util.Issue.SEVERITY_ERROR));
//	}
	
}

/*
 * returns the SimpleReferenceData.
 */
public ReferenceData getReferenceData() {
	return simpleReferenceData;
}

/*
 * 
 */
public ExpressionDataPair[] getExpressionDataPairs()
{
	return funcDataPairs;
}

/*
 * get VCML of the ExplicitFitObjectiveFunction.
 */
public String getVCML() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("ExplicitFitObjectiveFunction {\n");
	for(int i=0; i<funcDataPairs.length; i++)
	{
		buffer.append(funcDataPairs[i].getFitFunction().infix() + "\t" + "DataIndex" + "\t" + funcDataPairs[i].getReferenceDataIndex() + "\n");
	}
	
	buffer.append(simpleReferenceData.getVCML()+"\n");
	buffer.append("}\n");
	return buffer.toString();
}

/*
 * 
 */
public Expression getFunctionExpression(int indexInArray) {
	return funcDataPairs[indexInArray].getFitFunction();
}

public int getReferenceDataColumnIndex(int indexInArray){
	return funcDataPairs[indexInArray].getReferenceDataIndex();
}


public ScalarFunction getScalarFunction() {
	return new ExplicitFitScalarFunction();
}
}
