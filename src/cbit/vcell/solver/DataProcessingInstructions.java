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
import java.util.StringTokenizer;

import org.jdom.Element;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.util.xml.XmlUtil;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.math.FieldFunctionDefinition;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.xml.XMLTags;

@SuppressWarnings("serial")
public final class DataProcessingInstructions implements Matchable, Serializable {
	public static final String VFRAP = "VFRAP";
	public static final String ROI_TIME_SERIES = "RoiTimeSeries";
	private String scriptName;
	private String scriptInput;

	public DataProcessingInstructions(String scriptName, String scriptInput){
		if (!scriptName.equals(VFRAP) && !scriptName.equals(ROI_TIME_SERIES)) {
			throw new RuntimeException("DataProcessingInstruction '" + scriptName + "' not supported.");
		}
		this.scriptName = scriptName;
		this.scriptInput = scriptInput;
	}

	public String getScriptName() {
		return scriptName;
	}
	
	public static DataProcessingInstructions getRoiTimeSeriesInstructions(int numRegions, KeyValue fieldDataKey, FieldFunctionArguments fd, boolean bStoreEnabled) {
		StringBuffer sb = new StringBuffer();
		sb.append("StoreEnabled " + bStoreEnabled + "\n");
		sb.append("SampleImage " + numRegions + " " + fieldDataKey + " " + fd.infix()+"\n");
		return new DataProcessingInstructions("RoiTimeSeries", sb.toString());
	}
	
	public static DataProcessingInstructions getVFrapInstructions(int[] volumePoints, int[] membranePoints, int numRegions, int zSlice, KeyValue fieldDataKey, FieldFunctionArguments fd, boolean bStoreEnabled) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("VolumePoints " + volumePoints.length + "\n");
		for (int i = 0; i < volumePoints.length; i++) {
			sb.append(volumePoints[i] + " ");
			if ((i+1) % 20 == 0) {
				sb.append("\n");
			}
		}
		sb.append("\n");
		if (membranePoints != null && membranePoints.length > 0) {	
			sb.append("MembranePoints " + membranePoints.length + "\n");
			for (int i = 0; i < membranePoints.length; i++) {
				sb.append(membranePoints[i] + " ");
				if ((i+1) % 20 == 0) {
					sb.append("\n");
				}
			}
			sb.append("\n");
		}
		sb.append("SampleImage " + numRegions + " " + zSlice + " " + fieldDataKey + " " + fd.infix()+"\n");
		sb.append("StoreEnabled " + bStoreEnabled + "\n");

		return new DataProcessingInstructions(VFRAP, sb.toString());
	}
	
	public String getScriptInput() {
		return scriptInput;
	}

	public static DataProcessingInstructions fromDbXml(String xmlString){
		Element root = XmlUtil.stringToXML(xmlString, null).getRootElement();
		if (!root.getName().equals(XMLTags.DataProcessingInstructionsTag)){
			throw new RuntimeException("expected root element to be named "+XMLTags.DataProcessingInstructionsTag);
		}
		String scriptName = root.getAttributeValue(XMLTags.DataProcessingScriptNameAttrTag);
		String scriptInput = root.getText();
		return new DataProcessingInstructions(scriptName,scriptInput); 
	}
	
	public String getDbXml(){
		Element root = new Element(XMLTags.DataProcessingInstructionsTag);
		root.setAttribute(XMLTags.DataProcessingScriptNameAttrTag, scriptName);
		root.setText(scriptInput);
		return XmlUtil.xmlToString(root);
	}
	
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof DataProcessingInstructions){
			DataProcessingInstructions dpi = (DataProcessingInstructions)obj;
			if (!Compare.isEqual(dpi.scriptName,scriptName)){
				return false;
			}
			if (!Compare.isEqual(dpi.scriptInput,scriptInput)){
				return false;
			}
			return true;
		}
		return false;
	}
	
	public FieldDataIdentifierSpec getSampleImageFieldData(User user) {
		if (scriptInput != null) {
			int index = scriptInput.indexOf("SampleImage");
			StringTokenizer st = new StringTokenizer(scriptInput.substring(index));
			st.nextToken(); // SampleImage
			st.nextToken(); // numRegions
			if (getScriptName().equals(VFRAP)) {
				st.nextToken(); // zSlice
			}
			String key = st.nextToken(); // key
			
			index = scriptInput.indexOf(FieldFunctionDefinition.FUNCTION_name);
			if (index >= 0) {
				st = new StringTokenizer(scriptInput.substring(index), "\n");
				if (st.hasMoreTokens()) {
					String fieldFunction = st.nextToken();
					try {
						Expression exp = new Expression(fieldFunction);					
						FieldFunctionArguments[] ffa = FieldUtilities.getFieldFunctionArguments(exp);
						return new FieldDataIdentifierSpec(ffa[0], new ExternalDataIdentifier(KeyValue.fromString(key), user, ffa[0].getFieldName()));
					} catch (ExpressionException e) {
						e.printStackTrace();
						throw new RuntimeException(e.getMessage());
					} catch (Exception e){
						e.printStackTrace();
						throw new RuntimeException("Failed to load data processing script.");
					}
				}
			}
		}
		return null;
	}

}
