/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.server;
import java.io.Serializable;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import cbit.rmi.event.ExportEvent;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Range;
import org.vcell.util.document.VCDataIdentifier;

import cbit.image.DisplayAdapterService;
import cbit.image.DisplayPreferences;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class ExportSpecs implements Serializable {
	private org.vcell.util.document.VCDataIdentifier vcDataIdentifier = null;
	private ExportFormat format;
	private TimeSpecs timeSpecs;
	private VariableSpecs variableSpecs;
	private GeometrySpecs geometrySpecs;
	private FormatSpecificSpecs formatSpecificSpecs;
	private String simulatioName;
	private String contextName;

	private HumanReadableExportData humanReadableExportData;

	public interface SimulationSelector{
		public void selectSimulations();
		public void selectParamScanInfo();
		public SimNameSimDataID[] getSelectedSimDataInfo();
		public int[] getselectedParamScanIndexes();
		public int getNumAvailableSimulations();
		public int getNumAvailableParamScans();
	}

	public ExportSpecs(org.vcell.util.document.VCDataIdentifier vcdID, ExportFormat format,
			VariableSpecs variableSpecs, TimeSpecs timeSpecs, 
			GeometrySpecs geometrySpecs, FormatSpecificSpecs formatSpecificSpecs,
			String simulationName,String contextName) {
		this.vcDataIdentifier = vcdID;
		this.format = format;
		this.variableSpecs = variableSpecs;
		this.timeSpecs = timeSpecs;
		this.geometrySpecs = geometrySpecs;
		this.formatSpecificSpecs = formatSpecificSpecs;
		this.simulatioName = simulationName;
		this.contextName = contextName;
	}

	@JsonCreator
	public ExportSpecs(@JsonProperty("vcdataIdentifier") VCDataIdentifier vcdataIdentifier, @JsonProperty("format") ExportFormat format,
					   @JsonProperty("variableSpecs") VariableSpecs variableSpecs, @JsonProperty("timeSpecs") TimeSpecs timeSpecs,
					   @JsonProperty("geometrySpecs") GeometrySpecs geometrySpecs, @JsonProperty("formatSpecificSpecs") FormatSpecificSpecs formatSpecificSpecs,
					   @JsonProperty("simulationName") String simulationName, @JsonProperty("contextName") String contextName,
					   @JsonProperty("humanReadableExportData") HumanReadableExportData humanReadableExportData) {
		this.vcDataIdentifier = vcdataIdentifier;
		this.format = format;
		this.variableSpecs = variableSpecs;
		this.timeSpecs = timeSpecs;
		this.geometrySpecs = geometrySpecs;
		this.formatSpecificSpecs = formatSpecificSpecs;
		this.simulatioName = simulationName;
		this.contextName = contextName;
		this.humanReadableExportData = humanReadableExportData;
	}

	public String getContextName(){
		return contextName;
	}

	public String getSimulationName(){
		return simulatioName;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (4/2/2001 12:04:55 AM)
	 * @return boolean
	 * @param object java.lang.Object
	 */
	public boolean equals(Object object) {
		if (object instanceof ExportSpecs) {
			ExportSpecs exportSpecs = (ExportSpecs)object;
			if (
				(vcDataIdentifier == null && exportSpecs.getVCDataIdentifier() == null) || vcDataIdentifier.equals(exportSpecs.getVCDataIdentifier()) &&
				format == exportSpecs.getFormat() &&
				(variableSpecs == null && exportSpecs.getVariableSpecs() == null) || variableSpecs.equals(exportSpecs.getVariableSpecs()) &&
				(timeSpecs == null && exportSpecs.getTimeSpecs() == null) || timeSpecs.equals(exportSpecs.getTimeSpecs()) &&
				(geometrySpecs == null && exportSpecs.getGeometrySpecs() == null) || geometrySpecs.equals(exportSpecs.getGeometrySpecs()) &&
				((formatSpecificSpecs == null && exportSpecs.getFormatSpecificSpecs() == null) || ((formatSpecificSpecs != null && exportSpecs.getFormatSpecificSpecs() != null) && formatSpecificSpecs.equals(exportSpecs.getFormatSpecificSpecs()))) &&
				Compare.isEqualOrNull(simulatioName , exportSpecs.simulatioName) &&
				Compare.isEqualOrNull(contextName , exportSpecs.contextName)){
				return true;
			}
		}
		return false;
	}


	/**
	 * This method was created in VisualAge.
	 * @return cbit.vcell.export.server.VariableSpecs
	 */
	public ExportFormat getFormat() {
		return format;
	}


	/**
	 * This method was created in VisualAge.
	 * @return cbit.vcell.export.server.VariableSpecs
	 */
	public FormatSpecificSpecs getFormatSpecificSpecs() {
		return formatSpecificSpecs;
	}


	/**
	 * This method was created in VisualAge.
	 * @return cbit.vcell.export.server.VariableSpecs
	 */
	public GeometrySpecs getGeometrySpecs() {
		return geometrySpecs;
	}


	/**
	 * This method was created in VisualAge.
	 * @return cbit.vcell.export.server.VariableSpecs
	 */
	public TimeSpecs getTimeSpecs() {
		return timeSpecs;
	}


	/**
	 * This method was created in VisualAge.
	 * @return cbit.vcell.export.server.VariableSpecs
	 */
	public VariableSpecs getVariableSpecs() {
		return variableSpecs;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (4/1/2001 7:20:40 PM)
	 * @return cbit.vcell.solver.SimulationInfo
	 */
	public org.vcell.util.document.VCDataIdentifier getVCDataIdentifier() {
		return vcDataIdentifier;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (4/2/2001 4:33:23 PM)
	 * @return int
	 */
	public int hashCode() {
		return toString().hashCode();
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (4/2/2001 4:23:04 PM)
	 * @return java.lang.String
	 */
	public String toString() {
		if(getTimeSpecs()!= null && getTimeSpecs().getAllTimes().length > 0)
			return "ExportSpecs [" + getVCDataIdentifier() + ", format: " + getFormat() + ", " + getVariableSpecs() + ", " + getTimeSpecs() + ", " + getGeometrySpecs() + ", " + getFormatSpecificSpecs() + "]";
		else
			return "ExportSpecs [" + getVCDataIdentifier() + ", format: " + getFormat() + ", " + getVariableSpecs() + ", " + getGeometrySpecs() + ", " + getFormatSpecificSpecs() + "]";
	}

	public static void setupDisplayAdapterService(DisplayPreferences displayPreferences,DisplayAdapterService displayAdapterService,Range currentVarAndTimeValRange) {
		displayAdapterService.setValueDomain(currentVarAndTimeValRange);
		
		displayAdapterService.setActiveScaleRange(currentVarAndTimeValRange);
		if(displayPreferences != null) {
			displayAdapterService.setActiveScaleRange(BeanUtils.selectRange(displayPreferences.isAuto(), displayPreferences.isAlltimes(), displayPreferences.getScaleSettings(), currentVarAndTimeValRange));
		}
		String colorMode = (displayPreferences==null?DisplayAdapterService.BLUERED:(displayPreferences.getColorMode()==null?DisplayAdapterService.BLUERED:displayPreferences.getColorMode()));
		displayAdapterService.setActiveColorModelID(colorMode);
		
		int[] specialColors = (displayPreferences==null?displayAdapterService.getSpecialColors():(displayPreferences.getSpecialColors()==null?displayAdapterService.getSpecialColors():displayPreferences.getSpecialColors()));
		System.arraycopy(specialColors, 0, displayAdapterService.getSpecialColors(), 0,specialColors.length);
	}

	public void setExportMetaData(HumanReadableExportData humanReadableExportData){
		this.humanReadableExportData = humanReadableExportData;
	}

	public HumanReadableExportData getHumanReadableExportData(){
		return humanReadableExportData;
	}
}
