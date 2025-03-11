/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import cbit.vcell.opt.OptSolverResultSet;
import cbit.vcell.opt.OptSolverResultSet.OptRunResultSet;
import cbit.vcell.opt.OptXmlTags;
import cbit.vcell.opt.OptimizationStatus;

public class OptXmlReader {	
	public static Element stringToXML(String xmlString){
		try {
			StringReader reader = new StringReader(xmlString);
			SAXBuilder builder = new SAXBuilder();
			Document sDoc = builder.build(reader);
			Element root = sDoc.getRootElement();
			return root;
		} catch (JDOMException e) {
	    	throw new RuntimeException("source document is not well-formed\n"+e.getMessage(), e);
    	} catch (IOException e) {
        	throw new RuntimeException("Unable to read source document\n"+e.getMessage(), e);
		}		
	}
	
	public static OptSolverResultSet getOptimizationResultSet(String xmlString){
		Element optResultSetElement = stringToXML(xmlString);
		if (!optResultSetElement.getName().equals(OptXmlTags.OptSolverResultSet_Tag)){
			throw new RuntimeException("unexpected element '"+optResultSetElement.getName()+"', expecting "+OptXmlTags.OptSolverResultSet_Tag);
		}
		List<Element> paramElementList = optResultSetElement.getChildren(OptXmlTags.Parameter_Tag); 
		String[] parameterNames = new String[paramElementList.size()];
		int count = 0;
		for (Element parameterElement : paramElementList) {
			parameterNames[count] = parameterElement.getAttributeValue(OptXmlTags.ParameterName_Attr); 
			count++;
		}
		
		Element bestRunElement = optResultSetElement.getChild(OptXmlTags.bestOptRunResultSet_Tag);		
		try {			
			OptRunResultSet bestResultSet = getOptRunResultSet(bestRunElement);
			OptSolverResultSet optResultSet = new OptSolverResultSet(parameterNames, bestResultSet);
			return optResultSet;
		} catch (DataConversionException e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	private static OptRunResultSet getOptRunResultSet(Element optRunElement) throws DataConversionException {
		double bestObjectFuncValue = optRunElement.getAttribute(OptXmlTags.OptSolverResultSetBestObjectiveFunction_Attr).getDoubleValue();
		long numObjectFuncEvals = optRunElement.getAttribute(OptXmlTags.OptSolverResultSetNumObjectiveFunctionEvaluations_Attr).getLongValue();
		String status = optRunElement.getAttributeValue(OptXmlTags.OptSolverResultSetStatus_Attr);
		Attribute statusMessageAttr = optRunElement.getAttribute(OptXmlTags.OptSolverResultSetStatusMessage_Attr);
		List<Element> paramElementList = optRunElement.getChildren(OptXmlTags.Parameter_Tag); 
		String[] parameterNames = new String[paramElementList.size()];
		double[] parameterValues = new double[paramElementList.size()];
		int count = 0;
		for (Element parameterElement : paramElementList) {
			parameterNames[count] = parameterElement.getAttributeValue(OptXmlTags.ParameterName_Attr); 
			parameterValues[count] = Double.parseDouble(parameterElement.getAttributeValue(OptXmlTags.ParameterBestValue_Attr));
			count++;
		}
		int statusID = OptimizationStatus.statusFromXMLName(status);
		String statusMessage = null;
		if (statusMessageAttr!=null){
			statusMessage = statusMessageAttr.getValue();
		}
		OptimizationStatus optimizationStatus = new OptimizationStatus(statusID,statusMessage);		
		OptRunResultSet optRun = new OptRunResultSet(parameterValues, 
				new Double(bestObjectFuncValue), numObjectFuncEvals,optimizationStatus);
		return optRun;
	}
}
