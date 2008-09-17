package cbit.vcell.modelopt;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;
import org.jdom.Namespace;
/**
 * Insert the type's description here.
 * Creation date: (5/5/2006 9:00:56 AM)
 * @author: Jim Schaff
 */
public class ParameterEstimationTaskXMLPersistence {
	
	public final static String NameAttribute = cbit.vcell.xml.XMLTags.NameAttrTag;
	public final static String AnnotationTag = cbit.vcell.xml.XMLTags.AnnotationTag;
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

/**
 * Insert the method's description here.
 * Creation date: (5/5/2006 4:50:36 PM)
 * @return cbit.vcell.modelopt.ParameterEstimationTask
 * @param element org.jdom.Element
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
public static ParameterEstimationTask getParameterEstimationTask(Element parameterEstimationTaskElement, cbit.vcell.mapping.SimulationContext simContext) 
throws cbit.vcell.parser.ExpressionException, cbit.vcell.mapping.MappingException, cbit.vcell.math.MathException, java.beans.PropertyVetoException {
		
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
	List<Element> parameterMappingSpecElementList = parameterMappingSpecListElement.getChildren(ParameterMappingSpecTag, ns);
	for (int i = 0; i < parameterMappingSpecElementList.size(); i++){
		Element parameterMappingSpecElement = parameterMappingSpecElementList.get(i);
		String parameterName = parameterMappingSpecElement.getAttributeValue(ParameterReferenceAttribute);
		cbit.vcell.parser.SymbolTableEntry ste = getSymbolTableEntry(simContext,parameterName);
	
		if (ste instanceof cbit.vcell.model.Parameter){
			cbit.vcell.model.Parameter parameter = (cbit.vcell.model.Parameter)ste;
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

		Element dataColumnListElement = referenceDataElement.getChild(DataColumnListTag, ns);
		List<Element> dataColumnList = dataColumnListElement.getChildren(DataColumnTag, ns);
		for (int i = 0; i < dataColumnList.size(); i++){
			Element dataColumnElement = dataColumnList.get(i);
			columnNames[i] = dataColumnElement.getAttributeValue(NameAttribute);
			columnWeights[i] = Double.parseDouble(dataColumnElement.getAttributeValue(WeightAttribute));
		}

		//
		// read rows
		//
		Vector<double[]> rowDataVector = new Vector<double[]>();
		Element dataRowListElement = referenceDataElement.getChild(DataRowListTag, ns);
		List<Element> dataRowList = dataRowListElement.getChildren(DataRowTag, ns);
		for (int i = 0; i < dataRowList.size(); i++){
			Element dataRowElement = dataRowList.get(i);
			String rowText = dataRowElement.getText();
			cbit.vcell.math.CommentStringTokenizer tokens = new cbit.vcell.math.CommentStringTokenizer(rowText);
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
		
		cbit.vcell.opt.ReferenceData referenceData = new cbit.vcell.opt.SimpleReferenceData(columnNames, columnWeights, rowDataVector);
		
		parameterEstimationTask.getModelOptimizationSpec().setReferenceData(referenceData);
	}

	
	//
	// read ReferenceDataMappingSpecs
	//
	Element referenceDataMappingSpecListElement = parameterEstimationTaskElement.getChild(ReferenceDataMappingSpecListTag, ns);
	if (referenceDataMappingSpecListElement!=null){
		List<Element> referenceDataMappingSpecList = referenceDataMappingSpecListElement.getChildren(ReferenceDataMappingSpecTag, ns);
		for (int i = 0; i < referenceDataMappingSpecList.size(); i++){
			Element referenceDataMappingSpecElement = referenceDataMappingSpecList.get(i);
			String referenceDataColumnName = referenceDataMappingSpecElement.getAttributeValue(ReferenceDataColumnNameAttribute);
			String referenceDataModelSymbolName = referenceDataMappingSpecElement.getAttributeValue(ReferenceDataModelSymbolAttribute);

			ReferenceDataMappingSpec referenceDataMappingSpec = parameterEstimationTask.getModelOptimizationSpec().getReferenceDataMappingSpec(referenceDataColumnName);
			cbit.vcell.parser.SymbolTableEntry modelSymbolTableEntry = null;
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
		String optimizationSolverType = optimizationSolverSpecElement.getAttributeValue(OptimizationSolverTypeAttribute);
		cbit.vcell.opt.OptimizationSolverSpec optSolverSpec = new cbit.vcell.opt.OptimizationSolverSpec(optimizationSolverType);
		parameterEstimationTask.setOptimizationSolverSpec(optSolverSpec);
	}

	
	return parameterEstimationTask;
}


/**
 * Insert the method's description here.
 * Creation date: (5/6/2006 12:31:48 AM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param simContext cbit.vcell.mapping.SimulationContext
 * @param symbol java.lang.String
 */
private static cbit.vcell.parser.SymbolTableEntry getSymbolTableEntry(cbit.vcell.mapping.SimulationContext simContext, String parameterName) {
	cbit.vcell.parser.SymbolTableEntry ste = null;

	try {
		if (parameterName.startsWith("ReservedSymbols.")){
			String symbol = parameterName.substring(parameterName.indexOf("."));
			ste = cbit.vcell.model.ReservedSymbol.fromString(symbol);
		}
	}catch (Exception e){
	}
	if (ste==null){
		try {
			ste = simContext.getModel().getEntry(parameterName);
		}catch(cbit.vcell.parser.ExpressionBindingException e){
		}
	}
	if (ste==null){
		try {
			ste = simContext.getEntry(parameterName);
		}catch (cbit.vcell.parser.ExpressionBindingException e){
		}
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

	
	Element parameterEstimationTaskElement = new Element(cbit.vcell.xml.XMLTags.ParameterEstimationTaskTag);
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
			
			cbit.vcell.model.Parameter parameter = parameterMappingSpecs[i].getModelParameter();
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
	cbit.vcell.opt.ReferenceData referenceData = parameterEstimationTask.getModelOptimizationSpec().getReferenceData();
	if (referenceData!=null && referenceData.getNumColumns()>0){
		Element referenceDataElement = new Element(ReferenceDataTag);
		referenceDataElement.setAttribute(NumRowsAttribute,Integer.toString(referenceData.getNumRows()));
		referenceDataElement.setAttribute(NumColumnsAttribute,Integer.toString(referenceData.getNumColumns()));

		Element dataColumnListElement = new Element(DataColumnListTag);
		for (int i = 0; i < referenceData.getColumnNames().length; i++){
			Element dataColumnElement = new Element(DataColumnTag);
			dataColumnElement.setAttribute(NameAttribute,referenceData.getColumnNames()[i]);
			dataColumnElement.setAttribute(WeightAttribute,Double.toString(referenceData.getColumnWeights()[i]));
			dataColumnListElement.addContent(dataColumnElement);
		}
		referenceDataElement.addContent(dataColumnListElement);

		Element dataRowListElement = new Element(DataRowListTag);
		for (int i = 0; i < referenceData.getNumRows(); i++){
			Element dataRowElement = new Element(DataRowTag);
			String rowText = "";
			for (int j = 0; j < referenceData.getNumColumns(); j++){
				if (j>0){
					rowText += " ";
				}
				rowText += referenceData.getRowData(i)[j];
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
			cbit.vcell.parser.SymbolTableEntry modelSymbol = referenceDataMappingSpecs[i].getModelObject();
			Element referenceDataMappingSpecElement = new Element(ReferenceDataMappingSpecTag);
			referenceDataMappingSpecElement.setAttribute(ReferenceDataColumnNameAttribute,referenceDataMappingSpecs[i].getReferenceDataColumnName());
			if (modelSymbol!=null){
				referenceDataMappingSpecElement.setAttribute(ReferenceDataModelSymbolAttribute, modelSymbol.getNameScope().getAbsoluteScopePrefix()+modelSymbol.getName());
			}
			referenceDataMappingSpecListElement.addContent(referenceDataMappingSpecElement);
		}
		parameterEstimationTaskElement.addContent(referenceDataMappingSpecListElement);
	}

	//
	// add OptimizationSolverSpec
	//
	if (parameterEstimationTask.getOptimizationSolverSpec()!=null){
		Element optimizationSolverSpecElement = new Element(OptimizationSolverSpecTag);
		optimizationSolverSpecElement.setAttribute(OptimizationSolverTypeAttribute,parameterEstimationTask.getOptimizationSolverSpec().getSolverType());
		parameterEstimationTaskElement.addContent(optimizationSolverSpecElement);
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