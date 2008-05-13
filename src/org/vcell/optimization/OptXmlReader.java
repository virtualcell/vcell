package org.vcell.optimization;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class OptXmlReader {
	
	public OptXmlReader() {
		
	}
	
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
	
	public OptimizationResultSet getOptimizationResultSet(String xmlString){
		Element optResultSetElement = stringToXML(xmlString);
		if (!optResultSetElement.getName().equals(OptXmlTags.OptSolverResultSet_Tag)){
			throw new RuntimeException("unexpected element '"+optResultSetElement.getName()+"', expecting "+OptXmlTags.OptSolverResultSet_Tag);
		}
		Attribute bestObjectFuncValueAttr = optResultSetElement.getAttribute(OptXmlTags.OptSolverResultSetBestObjectiveFunction_Attr);
		Attribute numObjectFuncEvalsAttr = optResultSetElement.getAttribute(OptXmlTags.OptSolverResultSetNumObjectiveFunctionEvaluations_Attr);
		Attribute statusAttr = optResultSetElement.getAttribute(OptXmlTags.OptSolverResultSetStatus_Attr);
		Attribute statusMessageAttr = optResultSetElement.getAttribute(OptXmlTags.OptSolverResultSetStatusMessage_Attr);
		List paramElementList = optResultSetElement.getChildren(OptXmlTags.Parameter_Tag); 
		String[] parameterNames = new String[paramElementList.size()];
		double[] parameterValues = new double[paramElementList.size()];
		Iterator parameterElementIter = paramElementList.iterator();
		int count = 0;
		while (parameterElementIter.hasNext()){
			Element parameterElement = (Element)parameterElementIter.next();
			parameterNames[count] = parameterElement.getAttributeValue(OptXmlTags.ParameterName_Attr); 
			parameterValues[count] = Double.parseDouble(parameterElement.getAttributeValue(OptXmlTags.ParameterBestValue_Attr));
			count++;
		}
		int statusID = OptimizationStatus.statusFromXMLName(statusAttr.getValue());
		String statusMessage = null;
		if (statusMessageAttr!=null){
			statusMessage = statusMessageAttr.getValue();
		}
		OptimizationStatus optStatus = new OptimizationStatus(statusID,statusMessage);
		try {
			OptimizationResultSet optResultSet = new OptimizationResultSet(
					parameterNames,parameterValues,
					new Double(bestObjectFuncValueAttr.getDoubleValue()),
					numObjectFuncEvalsAttr.getIntValue(),optStatus);
			return optResultSet;
		}catch (DataConversionException e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}

}
