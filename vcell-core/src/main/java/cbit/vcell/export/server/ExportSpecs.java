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
import java.math.BigDecimal;
import java.sql.*;
import java.util.Arrays;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.util.Range;
import org.vcell.util.document.VCDataIdentifier;

import cbit.image.DisplayAdapterService;
import cbit.image.DisplayPreferences;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
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

	public static ExportParamScanInfo getParamScanInfo(Simulation simulation, int selectedParamScanJobIndex){
		int scanCount = simulation.getScanCount();
		if(scanCount == 1){//no parameter scan
			return null;
		}
		String[] scanConstantNames = simulation.getMathOverrides().getScannedConstantNames();
		Arrays.sort(scanConstantNames);
		int[] paramScanJobIndexes = new int[scanCount];
		String[][] scanConstValues = new String[scanCount][scanConstantNames.length];
		for (int i = 0; i < scanCount; i++) {
			paramScanJobIndexes[i] = i;
			for (int j = 0; j < scanConstantNames.length; j++) {
				String paramScanValue = simulation.getMathOverrides().getActualExpression(scanConstantNames[j], new MathOverrides.ScanIndex(i)).infix();
	//			System.out.println("ScanIndex="+i+" ScanConstName='"+scanConstantNames[j]+"' paramScanValue="+paramScanValue);
				scanConstValues[i][j] = paramScanValue;
			}
		}
		return new ExportParamScanInfo(paramScanJobIndexes, selectedParamScanJobIndex, scanConstantNames, scanConstValues);
	}

	public interface SimulationSelector{
		public void selectSimulations();
		public void selectParamScanInfo();
		public ExportSpecs.SimNameSimDataID[] getSelectedSimDataInfo();
		public int[] getselectedParamScanIndexes();
		public int getNumAvailableSimulations();
		public int getNumAvailableParamScans();
	}

	public static class ExportParamScanInfo implements Matchable,Serializable{
		private int[] paramScanJobIndexes;//these are the param scan job indexes we are possibly interested in
		private int defaultParamScanJobIndex;//this is the "selected" param scan simdata job index at the time this object was created, 0 if no param scan
		private String[] paramScanConstantNames;
		private String[][] paramScanConstantValues;
		
		
		public ExportParamScanInfo(int[] paramScanJobIndexes,int defaultParamScanJobIndex, String[] paramScanConstantNames,String[][] paramScanConstantValues) {
			this.paramScanJobIndexes = paramScanJobIndexes;
			this.defaultParamScanJobIndex = defaultParamScanJobIndex;
			this.paramScanConstantNames = paramScanConstantNames;
			this.paramScanConstantValues = paramScanConstantValues;
		}
		public boolean compareEqual(Matchable obj) {
			if (obj instanceof ExportParamScanInfo) {
				ExportParamScanInfo exportParamScanInfo = (ExportParamScanInfo)obj;
				if (defaultParamScanJobIndex == exportParamScanInfo.defaultParamScanJobIndex &&
					Compare.isEqualOrNull(paramScanJobIndexes, exportParamScanInfo.paramScanJobIndexes)){
					
					for (int i = 0;paramScanConstantNames!=null &&  i<paramScanConstantNames.length; i++) {
						if(!paramScanConstantNames[i].equals(exportParamScanInfo.paramScanConstantNames[i])){
							return false;
						}
					}
					for (int i = 0;paramScanConstantValues!=null &&  i<paramScanConstantValues.length; i++) {
						if(!paramScanConstantValues[i].equals(exportParamScanInfo.paramScanConstantValues[i])){
							return false;
						}
					}
					
					return true;
				}
			}
			return false;
			
		}
		public int[] getParamScanJobIndexes() {
			return paramScanJobIndexes;
		}
		public int getDefaultParamScanJobIndex() {
			return defaultParamScanJobIndex;
		}
		public String[] getParamScanConstantNames() {
			return paramScanConstantNames;
		}
		public String[][] getParamScanConstantValues() {
			return paramScanConstantValues;
		}
	}

	public static class SimNameSimDataID implements Matchable,Serializable{
		private String simulationName;
		private VCSimulationIdentifier vcSimulationIdentifier;
		private ExportParamScanInfo exportParamScanInfo;
		public SimNameSimDataID(String simulationName,VCSimulationIdentifier vcSimulationIdentifier,ExportParamScanInfo exportParamScanInfo) {
			this.simulationName = simulationName;
			this.vcSimulationIdentifier = vcSimulationIdentifier;
			this.exportParamScanInfo = exportParamScanInfo;
		}
		public VCDataIdentifier getVCDataIdentifier(int paramScanJobIndex){
			if(exportParamScanInfo == null && paramScanJobIndex > 0){
				throw new IllegalArgumentException("Error SimNameSimDataID.getVCDataIdentifier: jobIndex > 0 unexpected with no parameter scan");
			}else if(exportParamScanInfo != null && paramScanJobIndex >= exportParamScanInfo.getParamScanJobIndexes().length){
				throw new IllegalArgumentException("Error SimNameSimDataID.getVCDataIdentifier: jobIndex > parameter scan count");
			}
			return new VCSimulationDataIdentifier(vcSimulationIdentifier, paramScanJobIndex);
		}
		public int getDefaultJobIndex(){
			return (exportParamScanInfo==null?0:exportParamScanInfo.defaultParamScanJobIndex);
		}
		public ExportParamScanInfo getExportParamScanInfo(){
			return exportParamScanInfo;
		}
		public String getSimulationName(){
			return simulationName;
		}
		public boolean compareEqual(Matchable obj) {
			if (obj instanceof SimNameSimDataID) {
				SimNameSimDataID simNameSimDataID = (SimNameSimDataID)obj;
				if (
					simulationName.equals(simNameSimDataID.getSimulationName()) &&
					vcSimulationIdentifier.equals(simNameSimDataID.vcSimulationIdentifier) &&
					Compare.isEqualOrNull(exportParamScanInfo, simNameSimDataID.getExportParamScanInfo())){
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * This method was created in VisualAge.
	 */
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


private void saveExportToDatabase(
		ExportEvent exportEvent,
		ExportSpecs exportSpecs,
		long userRef,
		long modelRef
) throws SQLException {

	final String INSERT_HISTORY =
			"INSERT INTO vc_model_export_history (" +
					"  id, job_id, user_ref, model_ref, export_format, export_date, uri," +
					"  data_id, simulation_name, application_name, biomodel_name, variables," +
					"  start_time, end_time, saved_file_name, application_type, non_spatial," +
					"  z_slices, t_slices, num_variables" +
					") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	final String INSERT_PARAM =
			"INSERT INTO vc_model_parameter_values (" +
					"  export_id, user_ref, model_ref, parameter_name," +
					"  parameter_original, parameter_changed" +
					") VALUES (?,?,?,?,?,?)";

	try (Connection conn = DriverManager.getConnection("DB_URL", "quarkus", "quarkus");
		 PreparedStatement psHist = conn.prepareStatement(INSERT_HISTORY);
		 PreparedStatement psParam = conn.prepareStatement(INSERT_PARAM)) {


		HumanReadableExportData meta = exportSpecs.getHumanReadableExportData();
		TimeSpecs              tm   = exportSpecs.getTimeSpecs();

		// 1) vc_model_export_history
		//psHist.setLong       (1, iD);
		psHist.setLong       (2, exportEvent.getJobID());
		psHist.setLong       (3, userRef);
		psHist.setLong       (4, modelRef);
		psHist.setString     (5, exportSpecs.getFormat().name());
		psHist.setTimestamp  (6, new Timestamp(System.currentTimeMillis()));
		psHist.setString     (7, exportEvent.getLocation());
		psHist.setString     (8, exportSpecs.getVCDataIdentifier().toString());
		psHist.setString     (9, exportSpecs.getSimulationName());
		psHist.setString     (10, meta.applicationName);
		psHist.setString     (11, meta.biomodelName);

		// variables → SQL text[]
		String[] vars    = exportSpecs.getVariableSpecs().getVariableNames();
		Array    sqlVars = conn.createArrayOf("text", vars);
		psHist.setArray  (12, sqlVars);

		// start/end time
		String timeRange = tm.toString();
		String[] time_parts   = timeRange.split("/");
		BigDecimal start = new BigDecimal(time_parts[0]);
		BigDecimal end   = new BigDecimal(time_parts[1]);


		psHist.setBigDecimal (13, start);
		psHist.setBigDecimal (14, end);

		psHist.setString     (15, meta.serverSavedFileName);

		psHist.setString     (15, meta.serverSavedFileName);
		psHist.setString     (16, meta.applicationType);
		psHist.setBoolean    (17, meta.nonSpatial);
		psHist.setInt        (18, meta.zSlices);
		psHist.setInt        (19, meta.tSlices);
		psHist.setInt        (20, meta.numChannels);

		psHist.executeUpdate();


		for (String entry : meta.differentParameterValues) {

			String[] param_parts = entry.split(",");
			if (param_parts.length == 3) {
				//psParam.setLong       (1, iD);
				psParam.setLong       (2, userRef);
				psParam.setLong       (3, modelRef);
				psParam.setString     (4, (param_parts[0]));
				psParam.setBigDecimal (5, new BigDecimal(param_parts[1]));
				psParam.setBigDecimal (6, new BigDecimal(param_parts[2]));
				psParam.addBatch();
			}
		}
		psParam.executeBatch();
	}
}




}
