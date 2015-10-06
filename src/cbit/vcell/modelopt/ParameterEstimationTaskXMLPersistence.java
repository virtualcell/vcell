/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modelopt;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;
import org.jdom.Namespace;
import org.vcell.util.CommentStringTokenizer;

import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathException;
import cbit.vcell.model.Parameter;
import cbit.vcell.opt.CopasiOptimizationMethod;
import cbit.vcell.opt.CopasiOptimizationMethod.CopasiOptimizationMethodType;
import cbit.vcell.opt.CopasiOptimizationParameter;
import cbit.vcell.opt.CopasiOptimizationParameter.CopasiOptimizationParameterType;
import cbit.vcell.opt.OptSolverResultSet;
import cbit.vcell.opt.OptSolverResultSet.OptRunResultSet;
import cbit.vcell.opt.OptXmlTags;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.xml.XMLTags;
/**
 * Insert the type's description here.
 * Creation date: (5/5/2006 9:00:56 AM)
 * @author: Jim Schaff
 */
public class ParameterEstimationTaskXMLPersistence {
	
	public final static String NameAttribute = XMLTags.NameAttrTag;
	public final static String AnnotationTag = XMLTags.AnnotationTag;
	public final static String ParameterMappingSpecListTag = "parameterMappingSpecList";
	public final static String ParameterMappingSpecTag = "parameterMappingSpec";
	public final static String ParameterReferenceAttribute = "parameterReferenceAttribute";
	public final static String LowLimitAttribute = "lowLimit";
	public final static String HighLimitAttribute = "highLimit";
	public final static String CurrentValueAttribute = "currentValue";
	public final static String ScaleAttribute = "scale";
	public final static String SelectedAttribute = "selected";
	public final static String ReferenceDataTag = "referenceData";
	public final static String NumRowsAttribute = "numRows";
	public final static String NumColumnsAttribute = "numColumns";
	public final static String DataColumnListTag = "dataColumnList";
	public final static String DataColumnTag = "dataColumn";
	public final static String WeightAttribute = "weight";
	public final static String DataRowListTag = "dataRowList";
	public final static String DataRowTag = "dataRow";
	public final static String ReferenceDataMappingSpecListTag = "referenceDataMappingSpecList";
	public final static String ReferenceDataMappingSpecTag = "referenceDataMappingSpec";
	public final static String ReferenceDataColumnNameAttribute = "referenceDataColumnName";
	public final static String ReferenceDataModelSymbolAttribute = "referenceDataModelSymbol";
	public final static String OptimizationSolverSpecTag = "optimizationSolverSpec";
	public final static String OptimizationSolverTypeAttribute = "optimizationSolverType";
	public final static String OptimizationListOfParametersTag = "ListOfParameters";
	public final static String OptimizationParameterTag = "Parameter";
	public final static String OptimizationParameterNameAttribute = "Name";
	public final static String OptimizationParameterValueAttribute = "Value";
	public final static String OptimizationSolverNumOfRunsAttribute = "NumOfRuns";

/**
 * Insert the method's description here.
 * Creation date: (5/5/2006 4:50:36 PM)
 * @return cbit.vcell.modelopt.ParameterEstimationTask
 * @param element org.jdom.Element
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
public static ParameterEstimationTask getParameterEstimationTask(Element parameterEstimationTaskElement, SimulationContext simContext) 
throws ExpressionException, MappingException, MathException, java.beans.PropertyVetoException {
		
	Namespace ns = parameterEstimationTaskElement.getNamespace();
	ParameterEstimationTask parameterEstimationTask = new ParameterEstimationTask(simContext);
	String name = parameterEstimationTaskElement.getAttributeValue(NameAttribute);
	parameterEstimationTask.setName(name);
	Element annotationElement = parameterEstimationTaskElement.getChild(AnnotationTag, ns);
	if (annotationElement!=null){
		String annotationText = annotationElement.getText();
		parameterEstimationTask.setAnnotation(annotationText);
	}

	//
	// read ParameterMappingSpecs
	//
	Element parameterMappingSpecListElement = parameterEstimationTaskElement.getChild(ParameterMappingSpecListTag, ns);
	if (parameterMappingSpecListElement!=null){
		List<Element> parameterMappingSpecElementList = parameterMappingSpecListElement.getChildren(ParameterMappingSpecTag, ns);
		for (Element parameterMappingSpecElement : parameterMappingSpecElementList ){
			String parameterName = parameterMappingSpecElement.getAttributeValue(ParameterReferenceAttribute);
			SymbolTableEntry ste = getSymbolTableEntry(simContext,parameterName);
		
			if (ste instanceof Parameter){
				Parameter parameter = (Parameter)ste;
				ParameterMappingSpec parameterMappingSpec = parameterEstimationTask.getModelOptimizationSpec().getParameterMappingSpec(parameter);
				
				if  (parameterMappingSpec != null) {
					String lowLimitString = parameterMappingSpecElement.getAttributeValue(LowLimitAttribute);
					if (lowLimitString!=null){
						parameterMappingSpec.setLow(parseDouble(lowLimitString));
					}
					String highLimitString = parameterMappingSpecElement.getAttributeValue(HighLimitAttribute);
					if (highLimitString!=null){
						parameterMappingSpec.setHigh(parseDouble(highLimitString));
					}
					String currentValueString = parameterMappingSpecElement.getAttributeValue(CurrentValueAttribute);
					if (currentValueString!=null){
						parameterMappingSpec.setCurrent(Double.parseDouble(currentValueString));
					}
					String selectedString = parameterMappingSpecElement.getAttributeValue(SelectedAttribute);
					if (selectedString!=null){
						parameterMappingSpec.setSelected(Boolean.valueOf(selectedString).booleanValue());
					}
				} 
			}else{
				System.out.println("couldn't read parameterMappingSpec '"+parameterName+"', ste="+ste);
			}	
		}
	}

	//
	// read ReferenceData
	//
	Element referenceDataElement = parameterEstimationTaskElement.getChild(ReferenceDataTag, ns);
	if (referenceDataElement!=null){
		String numRowsText = referenceDataElement.getAttributeValue(NumRowsAttribute);
		String numColsText = referenceDataElement.getAttributeValue(NumColumnsAttribute);
		//int numRows = Integer.parseInt(numRowsText);
		int numCols = Integer.parseInt(numColsText);

		//
		// read columns
		//	
		String[] columnNames = new String[numCols];
		double[] columnWeights = new double[numCols];
		int columnCounter = 0;
		Element dataColumnListElement = referenceDataElement.getChild(DataColumnListTag, ns);
		List<Element> dataColumnList = dataColumnListElement.getChildren(DataColumnTag, ns);
		for (Element dataColumnElement : dataColumnList){
			columnNames[columnCounter] = dataColumnElement.getAttributeValue(NameAttribute);
			columnWeights[columnCounter] = Double.parseDouble(dataColumnElement.getAttributeValue(WeightAttribute));
			columnCounter ++;
		}

		//
		// read rows
		//
		Vector<double[]> rowDataVector = new Vector<double[]>();
		Element dataRowListElement = referenceDataElement.getChild(DataRowListTag, ns);
		List<Element> dataRowList = dataRowListElement.getChildren(DataRowTag, ns);
		for (Element dataRowElement : dataRowList){
			String rowText = dataRowElement.getText();
			CommentStringTokenizer tokens = new CommentStringTokenizer(rowText);
			double[] rowData = new double[numCols];
			for (int j = 0; j < numCols; j++){
				if (tokens.hasMoreTokens()){
					String token = tokens.nextToken();
					rowData[j] = Double.parseDouble(token);
				}else{
					throw new RuntimeException("failed to read row data for ReferenceData");
				}
			}
			rowDataVector.add(rowData);
		}
		
		ReferenceData referenceData = new SimpleReferenceData(columnNames, columnWeights, rowDataVector);
		
		parameterEstimationTask.getModelOptimizationSpec().setReferenceData(referenceData);
	}

	
	//
	// read ReferenceDataMappingSpecs
	//
	Element referenceDataMappingSpecListElement = parameterEstimationTaskElement.getChild(ReferenceDataMappingSpecListTag, ns);
	if (referenceDataMappingSpecListElement!=null){
		List<Element> referenceDataMappingSpecList = referenceDataMappingSpecListElement.getChildren(ReferenceDataMappingSpecTag, ns);
		for (Element referenceDataMappingSpecElement : referenceDataMappingSpecList){
			String referenceDataColumnName = referenceDataMappingSpecElement.getAttributeValue(ReferenceDataColumnNameAttribute);
			String referenceDataModelSymbolName = referenceDataMappingSpecElement.getAttributeValue(ReferenceDataModelSymbolAttribute);

			ReferenceDataMappingSpec referenceDataMappingSpec = parameterEstimationTask.getModelOptimizationSpec().getReferenceDataMappingSpec(referenceDataColumnName);
			SymbolTableEntry modelSymbolTableEntry = null;
			if (referenceDataModelSymbolName!=null){
				modelSymbolTableEntry = getSymbolTableEntry(simContext,referenceDataModelSymbolName);
				if (referenceDataMappingSpec!=null && modelSymbolTableEntry!=null){
					referenceDataMappingSpec.setModelObject(modelSymbolTableEntry);
				}
			}
		}
	}

	//
	// read OptimizationSolverSpec
	//
	Element optimizationSolverSpecElement = parameterEstimationTaskElement.getChild(OptimizationSolverSpecTag, ns);
	if (optimizationSolverSpecElement!=null){
		OptimizationSolverSpec optSolverSpec = null;
		String optimizationSolverTypeName = optimizationSolverSpecElement.getAttributeValue(OptimizationSolverTypeAttribute);
		
		//getting parameters
		Element optimizationSolverParameterList = optimizationSolverSpecElement.getChild(OptimizationListOfParametersTag, ns);
		if(optimizationSolverParameterList != null)
		{
			List<Element> listOfSolverParams = optimizationSolverParameterList.getChildren(OptimizationParameterTag, ns);
			CopasiOptimizationMethod copasiOptMethod = null;
			if(listOfSolverParams != null && listOfSolverParams.size() > 0)
			{
				List<CopasiOptimizationParameter> copasiSolverParams = new ArrayList<CopasiOptimizationParameter>();
				for(Element solverParam : listOfSolverParams)
				{
					String paramName = solverParam.getAttributeValue(OptimizationParameterNameAttribute);
					double paramValue = Double.parseDouble(solverParam.getAttributeValue(OptimizationParameterValueAttribute));
					CopasiOptimizationParameter copasiParam = new CopasiOptimizationParameter(getCopasiOptimizationParameterTypeByName(paramName), paramValue);
					copasiSolverParams.add(copasiParam);
				}
				copasiOptMethod = new CopasiOptimizationMethod(getCopasiOptimizationMethodTypeByName(optimizationSolverTypeName), copasiSolverParams.toArray(new CopasiOptimizationParameter[copasiSolverParams.size()])); 
			}
			else //no parameters
			{
				copasiOptMethod = new CopasiOptimizationMethod(getCopasiOptimizationMethodTypeByName(optimizationSolverTypeName), new CopasiOptimizationParameter[0]);
			}
			optSolverSpec = new OptimizationSolverSpec(copasiOptMethod);
			//add number of runs attribute
			String numOfRunsStr = optimizationSolverSpecElement.getAttributeValue(OptimizationSolverNumOfRunsAttribute);
			if( numOfRunsStr!= null)
			{
				int numOfRuns = Integer.parseInt(numOfRunsStr);
				optSolverSpec.setNumOfRuns(numOfRuns);
			}
		}
		
		parameterEstimationTask.setOptimizationSolverSpec(optSolverSpec);
	}
	if(optimizationSolverSpecElement == null || parameterEstimationTask.getOptimizationSolverSpec() == null) //optimization solver spec is null create a default copasi evolutionary programming
	{
		OptimizationSolverSpec optSolverSpec = new OptimizationSolverSpec(new CopasiOptimizationMethod(CopasiOptimizationMethodType.EvolutionaryProgram));
		parameterEstimationTask.setOptimizationSolverSpec(optSolverSpec);
	}

	//read optimization solver result set
	Element optimizationResultSetElement = parameterEstimationTaskElement.getChild(OptXmlTags.OptimizationResultSet_Tag, ns);
	if(optimizationResultSetElement != null)
	{
		OptimizationResultSet optResultSet = null;
		//read optsolverResultSet
		if(optimizationResultSetElement.getChild(OptXmlTags.bestOptRunResultSet_Tag, ns) != null)
		{
			Element optSolverResultSetElement = optimizationResultSetElement.getChild(OptXmlTags.bestOptRunResultSet_Tag, ns);
			OptSolverResultSet optSolverResultSet = null;
			//get best parameters, best func value, number of evaluations and construct an optRunResultSet
			Element paramListElement = optSolverResultSetElement.getChild(OptimizationListOfParametersTag, ns);
			OptRunResultSet optRunResultSet = null;
			List<String> paramNames = new ArrayList<String>();
			List<Double> paramValues = new ArrayList<Double>();
			if(paramListElement != null && !paramListElement.getChildren().isEmpty())
			{
				List<Element> paramElements = paramListElement.getChildren(OptimizationParameterTag, ns);
				if(paramElements != null)
				{
					for(Element paramElement : paramElements)
					{
						String paramName = paramElement.getAttributeValue(OptimizationParameterNameAttribute);
						double paramValue = Double.parseDouble(paramElement.getAttributeValue(OptimizationParameterValueAttribute));
						paramNames.add(paramName);
						paramValues.add(paramValue);
					}
				}
			}
			Element bestFuncValueElement = optSolverResultSetElement.getChild(OptXmlTags.ObjectiveFunction_Tag, ns);
			double bestFuncValue = Double.parseDouble(bestFuncValueElement.getAttributeValue(OptimizationParameterValueAttribute));
			Element numEvaluationsElement = optSolverResultSetElement.getChild(OptXmlTags.OptSolverResultSetFunctionEvaluations_Tag, ns);
			long numEvaluations = Long.parseLong(numEvaluationsElement.getAttributeValue(OptimizationParameterValueAttribute));
			//change List<Double> to double[]
			double[] values = new double[paramValues.size()];
			int index = 0;
			for(Double value : paramValues)
			{
				values[index++] = value;
			}
			optRunResultSet = new OptRunResultSet(values, bestFuncValue, numEvaluations, null);
			//create optSolverResultSet
			optSolverResultSet = new OptSolverResultSet(paramNames.toArray(new String[paramNames.size()]), optRunResultSet);
			//create optimization result set
			optResultSet = new OptimizationResultSet(optSolverResultSet, null);
		}
		parameterEstimationTask.setOptimizationResultSet(optResultSet);
	}
	return parameterEstimationTask;
}


private static CopasiOptimizationParameterType getCopasiOptimizationParameterTypeByName(String parameterName)
{
	for(CopasiOptimizationParameterType paramType : CopasiOptimizationParameterType.values())
	{
		if(paramType.getDisplayName().equals(parameterName))
		{
			return paramType;
		}
	}
	return null;
}

private static CopasiOptimizationMethodType getCopasiOptimizationMethodTypeByName(String methodName)
{
	for(CopasiOptimizationMethodType methodType : CopasiOptimizationMethodType.values())
	{
		if(methodType.getName().equals(methodName))
		{
			return methodType;
		}
	}
	return null;
}

/**
 * Insert the method's description here.
 * Creation date: (5/6/2006 12:31:48 AM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param simContext cbit.vcell.mapping.SimulationContext
 * @param symbol java.lang.String
 */
private static SymbolTableEntry getSymbolTableEntry(SimulationContext simContext, String parameterName) {
	SymbolTableEntry ste = simContext.getModel().getEntry(parameterName);
	
	if (ste==null){
		ste = simContext.getEntry(parameterName);
	}
	return ste;
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2006 9:02:39 AM)
 * @return java.lang.String
 * @param parameterEstimationTask cbit.vcell.modelopt.ParameterEstimationTask
 */
public static Element getXML(ParameterEstimationTask parameterEstimationTask) {

	
	Element parameterEstimationTaskElement = new Element(XMLTags.ParameterEstimationTaskTag);
	// name attribute
	parameterEstimationTaskElement.setAttribute(NameAttribute,mangle(parameterEstimationTask.getName()));
	// annotation content (optional)
	String annotation = parameterEstimationTask.getAnnotation();
	if (annotation!=null && annotation.length()>0) {
		org.jdom.Element annotationElement = new org.jdom.Element(AnnotationTag);
		annotationElement.setText(mangle(annotation));
		parameterEstimationTaskElement.addContent(annotationElement);
	}

	//
	// add ParameterMappingSpecs
	//
	ParameterMappingSpec[] parameterMappingSpecs = parameterEstimationTask.getModelOptimizationSpec().getParameterMappingSpecs();
	if (parameterMappingSpecs!=null && parameterMappingSpecs.length>0){
		Element parameterMappingSpecListElement = new Element(ParameterMappingSpecListTag);
		for (int i = 0; i < parameterMappingSpecs.length; i++){
			Element parameterMappingSpecElement = new Element(ParameterMappingSpecTag);
			
			Parameter parameter = parameterMappingSpecs[i].getModelParameter();
			parameterMappingSpecElement.setAttribute(ParameterReferenceAttribute, parameter.getNameScope().getAbsoluteScopePrefix()+parameter.getName());
			parameterMappingSpecElement.setAttribute(LowLimitAttribute, Double.toString(parameterMappingSpecs[i].getLow()));
			parameterMappingSpecElement.setAttribute(HighLimitAttribute, Double.toString(parameterMappingSpecs[i].getHigh()));
			parameterMappingSpecElement.setAttribute(CurrentValueAttribute, Double.toString(parameterMappingSpecs[i].getCurrent()));
			parameterMappingSpecElement.setAttribute(SelectedAttribute, String.valueOf(parameterMappingSpecs[i].isSelected()));
			
			parameterMappingSpecListElement.addContent(parameterMappingSpecElement);
		}
		parameterEstimationTaskElement.addContent(parameterMappingSpecListElement);
	}


	//
	// add ReferenceData
	//
	ReferenceData referenceData = parameterEstimationTask.getModelOptimizationSpec().getReferenceData();
	if (referenceData!=null && referenceData.getNumDataColumns()>0){
		Element referenceDataElement = new Element(ReferenceDataTag);
		referenceDataElement.setAttribute(NumRowsAttribute,Integer.toString(referenceData.getNumDataRows()));
		referenceDataElement.setAttribute(NumColumnsAttribute,Integer.toString(referenceData.getNumDataColumns()));

		Element dataColumnListElement = new Element(DataColumnListTag);
		for (int i = 0; i < referenceData.getColumnNames().length; i++){
			Element dataColumnElement = new Element(DataColumnTag);
			dataColumnElement.setAttribute(NameAttribute,referenceData.getColumnNames()[i]);
			dataColumnElement.setAttribute(WeightAttribute,Double.toString(referenceData.getColumnWeights()[i]));
			dataColumnListElement.addContent(dataColumnElement);
		}
		referenceDataElement.addContent(dataColumnListElement);

		Element dataRowListElement = new Element(DataRowListTag);
		for (int i = 0; i < referenceData.getNumDataRows(); i++){
			Element dataRowElement = new Element(DataRowTag);
			String rowText = "";
			for (int j = 0; j < referenceData.getNumDataColumns(); j++){
				if (j>0){
					rowText += " ";
				}
				rowText += referenceData.getDataByRow(i)[j];
			}
			dataRowElement.addContent(rowText);
			dataRowListElement.addContent(dataRowElement);
		}
		referenceDataElement.addContent(dataRowListElement);

		
		parameterEstimationTaskElement.addContent(referenceDataElement);
	}

	//
	// add ReferenceDataMappingSpecs
	//
	ReferenceDataMappingSpec[] referenceDataMappingSpecs = parameterEstimationTask.getModelOptimizationSpec().getReferenceDataMappingSpecs();
	if (referenceDataMappingSpecs!=null && referenceDataMappingSpecs.length>0){
		Element referenceDataMappingSpecListElement = new Element(ReferenceDataMappingSpecListTag);
		for (int i = 0; i < referenceDataMappingSpecs.length; i++){
			SymbolTableEntry modelSymbol = referenceDataMappingSpecs[i].getModelObject();
			Element referenceDataMappingSpecElement = new Element(ReferenceDataMappingSpecTag);
			referenceDataMappingSpecElement.setAttribute(ReferenceDataColumnNameAttribute,referenceDataMappingSpecs[i].getReferenceDataColumnName());
			if (modelSymbol!=null){
				referenceDataMappingSpecElement.setAttribute(ReferenceDataModelSymbolAttribute, modelSymbol.getName());
			}
			referenceDataMappingSpecListElement.addContent(referenceDataMappingSpecElement);
		}
		parameterEstimationTaskElement.addContent(referenceDataMappingSpecListElement);
	}

	//
	// add OptimizationSolverSpec
	//
	if (parameterEstimationTask.getOptimizationSolverSpec()!=null){
		OptimizationSolverSpec solverSpec = parameterEstimationTask.getOptimizationSolverSpec();
		if(solverSpec.getCopasiOptimizationMethod() != null)
		{
			CopasiOptimizationMethod copasiOptMethod = solverSpec.getCopasiOptimizationMethod();
			Element optimizationSolverSpecElement = new Element(OptimizationSolverSpecTag);
			optimizationSolverSpecElement.setAttribute(OptimizationSolverTypeAttribute, copasiOptMethod.getType().getName());
			optimizationSolverSpecElement.setAttribute(OptimizationSolverNumOfRunsAttribute, solverSpec.getNumOfRuns()+"");
			//adding solve parameter list to optimization solver spec
			CopasiOptimizationParameter[] solverParams = copasiOptMethod.getParameters();
			if(solverParams != null && solverParams.length > 0)
			{
				Element listOfSolverParams = new Element(OptimizationListOfParametersTag);
				for(CopasiOptimizationParameter solverParam : solverParams)
				{
					Element optSolverParam = new Element(OptimizationParameterTag);
					optSolverParam.setAttribute(OptimizationParameterNameAttribute, solverParam.getType().getDisplayName());
					optSolverParam.setAttribute(OptimizationParameterValueAttribute, solverParam.getValue()+"");
					listOfSolverParams.addContent(optSolverParam);
				}
				optimizationSolverSpecElement.addContent(listOfSolverParams);
			}
					
			parameterEstimationTaskElement.addContent(optimizationSolverSpecElement);
		}
	}	
	
	//add optimization solver result set
	if(parameterEstimationTask.getOptimizationResultSet() != null)
	{
		OptimizationResultSet optResultSet = parameterEstimationTask.getOptimizationResultSet();
		Element optimizationResultSetElement = new Element(OptXmlTags.OptimizationResultSet_Tag);
		if(optResultSet.getOptSolverResultSet() != null)
		{
			OptSolverResultSet optSolverResultSet = optResultSet.getOptSolverResultSet();
			Element optSolverResultSetElement = new Element(OptXmlTags.bestOptRunResultSet_Tag);
			//write best parameters
			String[] paramNames = optSolverResultSet.getParameterNames();
			double[] bestValues = optSolverResultSet.getBestEstimates();
			if(paramNames != null && paramNames.length > 0 && bestValues != null && bestValues.length > 0 && paramNames.length == bestValues.length)
			{
				Element listOfBestParams = new Element(OptimizationListOfParametersTag);
				for(int i=0; i<paramNames.length; i++)
				{
					Element resultParam = new Element(OptimizationParameterTag);
					resultParam.setAttribute(OptimizationParameterNameAttribute, paramNames[i]);
					resultParam.setAttribute(OptimizationParameterValueAttribute, bestValues[i]+"");
					listOfBestParams.addContent(resultParam);
				}
				optSolverResultSetElement.addContent(listOfBestParams);
			}
			//write objective function value
			double objectiveFuncValue = optSolverResultSet.getLeastObjectiveFunctionValue();
			Element objFuncElement = new Element(OptXmlTags.ObjectiveFunction_Tag);
			objFuncElement.setAttribute(OptimizationParameterValueAttribute, objectiveFuncValue+"");
			optSolverResultSetElement.addContent(objFuncElement);
			//write num function evaluations
			long numFuncEvaluations = optSolverResultSet.getObjFunctionEvaluations();
			Element numFuncEvaluationsElement = new Element(OptXmlTags.OptSolverResultSetFunctionEvaluations_Tag);
			numFuncEvaluationsElement.setAttribute(OptimizationParameterValueAttribute, numFuncEvaluations+"");
			optSolverResultSetElement.addContent(numFuncEvaluationsElement);
			
			optimizationResultSetElement.addContent(optSolverResultSetElement);
		}
		parameterEstimationTaskElement.addContent(optimizationResultSetElement);
	}
	return parameterEstimationTaskElement;
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2006 9:39:57 AM)
 * @return java.lang.String
 * @param input java.lang.String
 */
private static String mangle(String input) {
	return input;
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2006 11:25:52 PM)
 * @return double
 * @param doubleString java.lang.String
 */
private static double parseDouble(String doubleString) {
	try {
		return Double.parseDouble(doubleString);
	}catch (NumberFormatException e){
		if (doubleString.equalsIgnoreCase("-Infinity")){
			return Double.NEGATIVE_INFINITY;
		}else if (doubleString.equalsIgnoreCase("Infinity")){
			return Double.POSITIVE_INFINITY;
		}else{
			throw e;
		}
	}
}
}
