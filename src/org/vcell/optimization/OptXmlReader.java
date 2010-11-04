package org.vcell.optimization;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.vcell.optimization.OptSolverResultSet.OptRunResultSet;
import org.vcell.optimization.OptSolverResultSet.ProfileDistribution;

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
	    	e.printStackTrace();
	    	throw new RuntimeException("source document is not well-formed\n"+e.getMessage());
    	} catch (IOException e) { 
        	e.printStackTrace();
        	throw new RuntimeException("Unable to read source document\n"+e.getMessage());
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
			ArrayList<ProfileDistribution> pdList = null;
			Element pdListElement = optResultSetElement.getChild(OptXmlTags.ProfileDistributionList_Tag);
			if (pdListElement != null) {
				List<Element> pdElementList = pdListElement.getChildren(OptXmlTags.ProfileDistribution_Tag);
				pdList = new ArrayList<ProfileDistribution>();
				for (Element pdElement : pdElementList) {
					String fixedParamName = pdElement.getAttributeValue(OptXmlTags.ProfileDistribution_FixedParameter_Attr);
					ArrayList<OptRunResultSet> optRunResultSetList = new ArrayList<OptRunResultSet>();
					
					List<Element> optRunElementList = pdElement.getChildren(OptXmlTags.OptRunResultSet_Tag);
					for (Element optRunElement : optRunElementList) {
						OptRunResultSet optRun = getOptRunResultSet(optRunElement);
						optRunResultSetList.add(optRun);
					}
					ProfileDistribution pd = new ProfileDistribution(fixedParamName, optRunResultSetList);
					pdList.add(pd);
				}
			}			
			OptSolverResultSet optResultSet = new OptSolverResultSet(parameterNames, bestResultSet, pdList);
			return optResultSet;
		} catch (DataConversionException e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
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
		int statusID = OptSolverStatus.statusFromXMLName(status);
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
