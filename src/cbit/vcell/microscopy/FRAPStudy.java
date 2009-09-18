package cbit.vcell.microscopy;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileFilter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.media.jai.BorderExtender;
import javax.media.jai.KernelJAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.AddConstDescriptor;
import javax.media.jai.operator.AndDescriptor;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.CropDescriptor;
import javax.media.jai.operator.DilateDescriptor;
import javax.media.jai.operator.ErodeDescriptor;
import javax.media.jai.operator.ExtremaDescriptor;
import javax.media.jai.operator.LookupDescriptor;

import loci.formats.AWTImageTools;
import loci.formats.ImageTools;
import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;

import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SimulationVersion;
import org.vcell.util.document.VersionFlag;
import org.vcell.util.Compare;
import org.vcell.util.Extent;
import org.vcell.util.FileUtils;
import org.vcell.util.ISize;
import org.vcell.util.Issue;
import org.vcell.util.Origin;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.VCDataIdentifier;

import org.vcell.util.Matchable;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.server.DataManager;
import cbit.vcell.client.server.PDEDataManager;
import cbit.vcell.field.FieldDataFileOperationSpec;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.ImageSubVolume;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.estparamwizard.FRAPReactionDiffusionParamPanel;
import cbit.vcell.model.Feature;
import cbit.vcell.model.MassActionKinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.parser.Expression;
import org.vcell.util.document.User;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.MergedDataInfo;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.simdata.DataSetControllerImpl.ProgressListener;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverStatus;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.FVSolverStandalone;

public class FRAPStudy implements Matchable{
	private transient String xmlFilename = null;
	private transient String directory = null;
	public static final String EXTRACELLULAR_NAME = "extracellular";
	public static final String CYTOSOL_NAME = "cytosol";
	public static final String SPECIES_NAME_PREFIX_MOBILE = "fluor_primary_mobile"; 
	public static final String SPECIES_NAME_PREFIX_SLOW_MOBILE = "fluor_secondary_mobile";
	public static final String SPECIES_NAME_PREFIX_BINDING_SITE = "binding_site";
	public static final String SPECIES_NAME_PREFIX_IMMOBILE = "fluor_immobile"; 
	public static final String SPECIES_NAME_PREFIX_COMBINED = "fluor_combined"; 
	private String name = null; 
	private String description = null;
	private String originalImageFilePath = null;
	private FRAPData frapData = null;
	private int startingIndexForRecovery = -1;

	private BioModel bioModel = null;
	private ExternalDataInfo frapDataExternalDataInfo = null;
	private ExternalDataInfo roiExternalDataInfo = null;
	//Added in Feb 2009, we want to store reference data together with the model in .vfrap file. 
	private SimpleReferenceData storedRefData = null;
	
	public static final String IMAGE_EXTDATA_NAME = "timeData";
	public static final String ROI_EXTDATA_NAME = "roiData";
	public static final String REF_EXTDATA_NAME = "refData";
	
	//models
	public static final String MODEL_ONE_DIFF_COMPONENT = "Diffusion with one diffusing component";
	public static final String MODEL_TWO_DIFF_COMPONENTS = "Diffusion with two diffusing components";
	public static final String MODEL_DIFF_BINDING = "Reaction plus binding";
	public ArrayList<String> selectedModels = new ArrayList<String>();
	public String bestModel = MODEL_ONE_DIFF_COMPONENT;
	
	private FRAPModelParameters frapModelParameters = null;
	PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private transient FRAPOptData frapOptData = null;//temporary data structure for reference data used in optimization
	
	public static class InitialModelParameters 
	{
		public final String diffusionRate;
		public final String mobileFraction;
		public final String monitorBleachRate;
		public final String startingIndexForRecovery;
		public InitialModelParameters(String diffRate, String mFrac, String bwmRate, String startingIdx)
		{
			diffusionRate = (diffRate != null && diffRate.length()>0)?diffRate:null;
			mobileFraction = (mFrac != null && mFrac.length() > 0)?mFrac:null;
			monitorBleachRate = (bwmRate != null && bwmRate.length() > 0)?bwmRate:null;
			startingIndexForRecovery = (startingIdx != null && startingIdx.length() > 0)?startingIdx:null;
		}
	}	
	
	public static class PureDiffusionModelParameters 
	{
		public final String primaryDiffusionRate;
		public final String primaryMobileFraction;
		public final String secondaryDiffusionRate;
		public final String secondaryMobileFraction;
		public final String monitorBleachRate;
		public final Boolean isSecondaryDiffusionApplied;
		public PureDiffusionModelParameters(String primaryDiffRate, String primaryMF, String secDiffRate, String secMF, String bwmRate, Boolean secDiffApplied)
		{
			primaryDiffusionRate = (primaryDiffRate !=null && primaryDiffRate.length() > 0)?primaryDiffRate:null;
			primaryMobileFraction = (primaryMF != null && primaryMF.length() > 0)?primaryMF:null;
			secondaryDiffusionRate = (secDiffRate != null && secDiffRate.length() > 0)?secDiffRate:null;
			secondaryMobileFraction = (secMF != null && secMF.length() > 0)?secMF:null;
			monitorBleachRate = (bwmRate != null && bwmRate.length() > 0)?bwmRate:null;
			isSecondaryDiffusionApplied = secDiffApplied;
		}
		public boolean arePureDiffParametesAllNotNull()
		{
			if(primaryDiffusionRate == null)
			{
				return false;
			}
			if(primaryMobileFraction == null)
			{
				return false;
			}
			if(secondaryDiffusionRate== null)
			{
				return false;
			}
			if(secondaryMobileFraction== null)
			{
				return false;
			}
			if(monitorBleachRate== null)
			{
				return false;
			}
			if(isSecondaryDiffusionApplied == null)
			{
				return false;
			}
			return true;
		}
	}	
	
	public static class ReactionDiffusionModelParameters 
	{
		public final String freeDiffusionRate;
		public final String freeMobileFraction;
		public final String complexDiffusionRate;
		public final String complexMobileFraction;
		public final String monitorBleachRate;
		public final String bsConcentration;
		public final String reacOnRate;
		public final String reacOffRate;
		public ReactionDiffusionModelParameters(String freeDiffRate, String freeMF, String complexDiffRate, String complexMF, String bwmRate, String bsConc, String onRate, String offRate)
		{
			freeDiffusionRate = (freeDiffRate != null && freeDiffRate.length()>0)?freeDiffRate:null;
			freeMobileFraction = (freeMF != null && freeMF.length() > 0)?freeMF:null;
			complexDiffusionRate = (complexDiffRate != null && complexDiffRate.length() > 0)?complexDiffRate:null;
			complexMobileFraction = (complexMF!=null && complexMF.length() > 0)?complexMF:null;
			monitorBleachRate = (bwmRate!=null && bwmRate.length()>0)?bwmRate:null;
			bsConcentration = (bsConc!=null && bsConc.length() > 0)?bsConc:null;
			reacOnRate = (onRate!=null && onRate.length()>0)?onRate:null;
			reacOffRate = (offRate!=null && offRate.length()>0)?offRate:null;
		}
	}
	
	public static class FRAPModelParameters {
		public InitialModelParameters iniModelParameters = null;
		public PureDiffusionModelParameters pureDiffModelParameters = null;
		public ReactionDiffusionModelParameters reacDiffModelParameters = null;
		public FRAPModelParameters(){};
		public FRAPModelParameters(InitialModelParameters iniParams, PureDiffusionModelParameters pureDiffParams, ReactionDiffusionModelParameters reacDiffParams)
		{
			iniModelParameters = iniParams;
			pureDiffModelParameters = pureDiffParams;
			reacDiffModelParameters = reacDiffParams;
		}
		public InitialModelParameters getIniModelParameters() {
			return iniModelParameters;
		}
		public void setIniModelParameters(InitialModelParameters iniModelParameters) {
			this.iniModelParameters = iniModelParameters;
		}
		public PureDiffusionModelParameters getPureDiffModelParameters() {
			return pureDiffModelParameters;
		}
		public void setPureDiffModelParameters(
				PureDiffusionModelParameters pureDiffModelParameters) {
			this.pureDiffModelParameters = pureDiffModelParameters;
		}
		public ReactionDiffusionModelParameters getReacDiffModelParameters() {
			return reacDiffModelParameters;
		}
		public void setReacDiffModelParameters(
				ReactionDiffusionModelParameters reacDiffModelParameters) {
			this.reacDiffModelParameters = reacDiffModelParameters;
		}
	}
	
	public static class AnalysisParameters{
		public final String freeDiffusionRate;
		public final String freeMobileFraction;
		public final String complexDiffusionRate;
		public final String complexMobileFraction;
		public final String bleachWhileMonitorRate;
		public final String bsConcentration;
		public final String onRate;
		public final String offRate;
		
		public AnalysisParameters(String freeDiffRate,String freeMF,String complexDiffRate, String complexMF, String bwmRate, String bsConc, String onRate, String offRate)
		{
//			if(diffusionRate == null || mobileFraction == null || bleachWhileMonitorRate == null){
//				throw new IllegalArgumentException("AnalysisParameters cannot be null");
//			}
			this.freeDiffusionRate = freeDiffRate;
			this.freeMobileFraction = freeMF;
			this.complexDiffusionRate = complexDiffRate;
			this.complexMobileFraction = complexMF;
			this.bleachWhileMonitorRate = bwmRate;
			this.bsConcentration = bsConc;
			this.onRate = onRate;
			this.offRate = offRate;
		}
		public boolean equals(Object obj){
			if(!(obj instanceof AnalysisParameters)){
				return false;
			}
			AnalysisParameters analysisParameters = (AnalysisParameters)obj;
			return
				Compare.isEqualOrNull(freeDiffusionRate,analysisParameters.freeDiffusionRate) &&
				Compare.isEqualOrNull(freeMobileFraction,analysisParameters.freeMobileFraction) &&
				Compare.isEqualOrNull(complexDiffusionRate,analysisParameters.complexDiffusionRate) &&
				Compare.isEqualOrNull(complexMobileFraction,analysisParameters.complexMobileFraction) &&
				Compare.isEqualOrNull(bleachWhileMonitorRate,analysisParameters.bleachWhileMonitorRate) &&
				Compare.isEqualOrNull(bsConcentration,analysisParameters.bsConcentration) &&
				Compare.isEqualOrNull(onRate,analysisParameters.onRate) &&
				Compare.isEqualOrNull(offRate,analysisParameters.offRate);
		}
		
		public int hashCode(){
			return 
			((freeDiffusionRate!=null)?(freeDiffusionRate.hashCode()):(0)) +
			((freeMobileFraction!=null)?(freeMobileFraction.hashCode()):(0)) +
			((complexDiffusionRate!=null)?(complexDiffusionRate.hashCode()):(0)) +
			((complexMobileFraction!=null)?(complexMobileFraction.hashCode()):(0)) +
			((bleachWhileMonitorRate!=null)?(bleachWhileMonitorRate.hashCode()):(0)) +
			((bsConcentration!=null)?(bsConcentration.hashCode()):(0)) +
			((onRate!=null)?(onRate.hashCode()):(0)) +
			((offRate!=null)?(offRate.hashCode()):(0));
		}
		
		public boolean isAllSimParamExist()
		{
			return  freeDiffusionRate != null &&
				    freeMobileFraction !=null &&
				    complexDiffusionRate != null &&
				    complexMobileFraction != null &&
				    bleachWhileMonitorRate != null &&
				    bsConcentration != null &&
				    onRate != null &&
				    offRate != null;
		}
		
		public String toString(){
			return "freeDiffRate="+freeDiffusionRate+", freeMobileFraction="+freeMobileFraction+",complexDiffRate="+complexDiffusionRate+",complexMobileFraction="+complexMobileFraction+", monitorBleachRate="+bleachWhileMonitorRate+",BSConcentration="+bsConcentration+",ReactionOnRate="+onRate+",ReactionOffRate="+offRate;
		}
	};

	public static class CurveInfo {
		AnalysisParameters analysisParameters;
		String roiName = null;

		public CurveInfo(AnalysisParameters analysisParameters,String roiName){
			this.analysisParameters = analysisParameters;
			this.roiName = roiName;
		}

		public boolean equals(Object obj){
			if (!(obj instanceof CurveInfo)){
				return false;
			}
			CurveInfo ci = (CurveInfo)obj;
			if(analysisParameters == null && ci.analysisParameters == null){
				return ci.roiName.equals(roiName);
			}
			if(ci.analysisParameters != null && analysisParameters != null){
				return
				ci.analysisParameters.equals(analysisParameters) &&
				ci.roiName.equals(roiName);		
			}
			return false;
		}
		
		public int hashCode(){
			return (analysisParameters != null?analysisParameters.hashCode():0)+roiName.hashCode();
		}
		public boolean isExperimentInfo(){
			return analysisParameters == null;
		}
		public AnalysisParameters getAnalysisParameters(){
			return analysisParameters;
		}
		public String getROIName(){
			return roiName;
		}
	}

	
	public static class SpatialAnalysisResults{
		public final AnalysisParameters[] analysisParameters;
		public final double[] shiftedSimTimes;
		public final Hashtable<CurveInfo, double[]> curveHash;
		public static String[] ORDERED_ROINAMES =
			new String[] {
				FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name(),	
				FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name(),	
				FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2.name(),	
				FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3.name(),	
				FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4.name(),	
				FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5.name(),
				FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6.name(),
				FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7.name(),
				FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8.name()
			};
		
		public static final int ANALYSISPARAMETERS_COLUMNS_COUNT = 3;
		public static final int COLUMN_INDEX_DIFFUSION_RATE = 0;
		public static final int COLUMN_INDEX_MOBILE_FRACTION = 1;
		public static final int COLUMN_INDEX_MONITOR_BLEACH_RATE = 2;
		
		public static final int ARRAY_INDEX_EXPDATASOURCE = 0;
		public static final int ARRAY_INDEX_SIMDATASOURCE = 1;
		
		private static final String ENDOFLINE = "\r\n";
		private static final String SEPCHAR = "\t";


		public SpatialAnalysisResults(AnalysisParameters[] analysisParameters,double[] shiftedSimTimes,	Hashtable<CurveInfo, double[]> curveHash)
		{
			this.analysisParameters = analysisParameters;
			this.shiftedSimTimes = shiftedSimTimes;
			this.curveHash = curveHash;
		}
		public AnalysisParameters[] getAnalysisParameters()
		{
			return analysisParameters;
		}
		//This function put exp data and sim data in a hashtable, the key is analysisParameters(right now only 1)
		public Hashtable<AnalysisParameters, DataSource[]> createSummaryReportSourceData(
				final double[] frapDataTimeStamps,int startIndexForRecovery, boolean hasSimData) throws Exception{
			
			Hashtable<AnalysisParameters, DataSource[]> allDataHash = new Hashtable<AnalysisParameters, DataSource[]>();
			
			Hashtable<CurveInfo, double[]> ROIInfoHash = curveHash;
			Set<CurveInfo> roiInfoSet = ROIInfoHash.keySet();
			Iterator<CurveInfo> roiInfoIter = roiInfoSet.iterator();
			//exp data are stored by ROI type.
			Hashtable<String, double[]> expROIData = new Hashtable<String, double[]>();
			//sim data are stored by AnalysisParameters and then by ROI type.
			Hashtable<AnalysisParameters, Hashtable<String, double[]>> simROIData = new Hashtable<AnalysisParameters, Hashtable<String,double[]>>();
			int roiCount = 0;
			int analysisParametersCount = 0;// how many set of parameters are used for sim. 1 for now. 
			while(roiInfoIter.hasNext()){
				CurveInfo roiCurveInfo = roiInfoIter.next();
				if(roiCurveInfo.isExperimentInfo()){
					expROIData.put(roiCurveInfo.getROIName(), ROIInfoHash.get(roiCurveInfo));
					roiCount++;
				}else{
					Hashtable<String,double[]> simROIDataHash = simROIData.get(roiCurveInfo.getAnalysisParameters());
					if(simROIDataHash == null){
						simROIDataHash  = new Hashtable<String, double[]>();
						simROIData.put(roiCurveInfo.getAnalysisParameters(), simROIDataHash);
						analysisParametersCount++;
					}
					simROIDataHash.put(roiCurveInfo.getROIName(), ROIInfoHash.get(roiCurveInfo));
				}
			}
			//this is for exp data. each row of reference data contains time + intensities under 9 ROIs(bleached + ring1..8). totally 10 cols for each row.		
			ReferenceData referenceData = 
				createReferenceData(frapDataTimeStamps,null,startIndexForRecovery,"");
			//loop only 1 time, right now the analysisParameters[]'s length is 1.
			for (int analysisParametersRow = 0; analysisParametersRow < analysisParametersCount; analysisParametersRow++) 
			{
				AnalysisParameters currentAnalysisParameters = analysisParameters[analysisParametersRow];
				
				DataSource[] newDataSourceArr = new DataSource[2];
				final DataSource expDataSource = new DataSource(referenceData,"exp"); // rows for time points and cols for t + roibleached + ring1--8 (10 cols)
				newDataSourceArr[ARRAY_INDEX_EXPDATASOURCE] = expDataSource;
				DataSource simDataSource = null;
				if(hasSimData)
				{
					ODESolverResultSet odeSolverResultSet =
						createODESolverResultSet(currentAnalysisParameters,null,"");
					simDataSource = new DataSource(odeSolverResultSet, "sim"); //rows for time points and cols for t + roibleached + ring1--8 (10 cols)
				}
				newDataSourceArr[ARRAY_INDEX_SIMDATASOURCE] = simDataSource;
				allDataHash.put(currentAnalysisParameters,newDataSourceArr);
			}
			return allDataHash;
		}


		public Object[][] createSummaryReportTableData(double[] frapDataTimeStamps,int startTimeIndex){
			String[] summaryReportColumnNames = SpatialAnalysisResults.getSummaryReportColumnNames();
			final Object[][] tableData = new Object[analysisParameters.length][summaryReportColumnNames.length];
			for (int analysisParametersRow = 0; analysisParametersRow < analysisParameters.length; analysisParametersRow++) {
				AnalysisParameters currentAnalysisParameters = analysisParameters[analysisParametersRow];
				tableData[analysisParametersRow][COLUMN_INDEX_DIFFUSION_RATE] = Double.parseDouble(currentAnalysisParameters.freeDiffusionRate);
				tableData[analysisParametersRow][COLUMN_INDEX_MOBILE_FRACTION] = Double.parseDouble(currentAnalysisParameters.freeMobileFraction);
				tableData[analysisParametersRow][COLUMN_INDEX_MONITOR_BLEACH_RATE] = currentAnalysisParameters.bleachWhileMonitorRate;
				
				for (int roiColumn = 0; roiColumn < SpatialAnalysisResults.ORDERED_ROINAMES.length; roiColumn++) {
					ODESolverResultSet simDataSource =
						createODESolverResultSet(currentAnalysisParameters,SpatialAnalysisResults.ORDERED_ROINAMES[roiColumn],"");
					ReferenceData expDataSource =
						createReferenceData(frapDataTimeStamps,SpatialAnalysisResults.ORDERED_ROINAMES[roiColumn],startTimeIndex,"");

					int numSamples = expDataSource.getNumRows();
					double sumSquaredError = MathTestingUtilities.calcWeightedSquaredError(simDataSource, expDataSource);
					tableData[analysisParametersRow][roiColumn+ANALYSISPARAMETERS_COLUMNS_COUNT] =
						Math.sqrt(sumSquaredError)/(numSamples-1);//unbiased estimator is numsamples-1
				}
			}
			return tableData;
		}
		
		public static String createCSVSummaryReport(String[] summaryReportColumnNames,Object[][] summaryData){
			StringBuffer reportSB = new StringBuffer();
			for (int i = 0; i < summaryReportColumnNames.length; i++) {
				reportSB.append((i != 0?SEPCHAR:"")+summaryReportColumnNames[i]);
			}
			reportSB.append(ENDOFLINE);
			for (int j = 0; j < summaryData.length; j++) {
				for (int k = 0; k < summaryReportColumnNames.length; k++) {
					reportSB.append((k != 0?SEPCHAR:"")+summaryData[j][k].toString());
				}
				reportSB.append(ENDOFLINE);
			}
			return reportSB.toString();
		}

		public static String createCSVTimeData(
				String[] summaryReportColumnNames,int selectedColumn,
				double[] expTimes,double[] expColumnData,
				double[] simTimes,double[] simColumnData){
			StringBuffer dataSB = new StringBuffer();
			dataSB.append("Exp Time"+SEPCHAR+" Exp Data (norm):"+summaryReportColumnNames[selectedColumn]+SEPCHAR+
					"Sim Time"+SEPCHAR+" Sim Data (norm):"+summaryReportColumnNames[selectedColumn]);
			dataSB.append(ENDOFLINE);
			for (int j = 0; j < Math.max(expTimes.length, simTimes.length); j++) {
				if(j <expTimes.length){
					dataSB.append(expTimes[j]+SEPCHAR+expColumnData[j]);
				}else{
					dataSB.append("NULL"+SEPCHAR+"NULL");
				}
				dataSB.append(SEPCHAR);
				if(j <simTimes.length){
					dataSB.append(simTimes[j]+SEPCHAR+simColumnData[j]);
				}else{
					dataSB.append("NULL"+SEPCHAR+"NULL");
				}
				dataSB.append(ENDOFLINE);
			}
			return dataSB.toString();
		}
		public static String[] getSummaryReportColumnNames(){
			String[] columnNames = new String[SpatialAnalysisResults.ORDERED_ROINAMES.length+ANALYSISPARAMETERS_COLUMNS_COUNT];
			for (int i = 0; i < columnNames.length; i++) {
				if(i==COLUMN_INDEX_DIFFUSION_RATE){
					columnNames[i] = "Diffusion Rate";
				}else if(i==COLUMN_INDEX_MOBILE_FRACTION){
					columnNames[i] = "Mobile Fraction";
				}else if(i==COLUMN_INDEX_MONITOR_BLEACH_RATE){
					columnNames[i] = "Monitor Bleach Rate";
				}else{
					final String ROI_PREFIX = "ROI_";
					final String ROI_TYPENAME_PREFIX = ROI_PREFIX+"BLEACHED_";
					String roiTypeName = SpatialAnalysisResults.ORDERED_ROINAMES[i-ANALYSISPARAMETERS_COLUMNS_COUNT];
					if(roiTypeName.startsWith(ROI_TYPENAME_PREFIX)){
						columnNames[i] = roiTypeName.substring(ROI_TYPENAME_PREFIX.length());
					}else{
						columnNames[i] = roiTypeName.substring(ROI_PREFIX.length());
					}
					columnNames[i]+= " (se)";
				}
			}
			return columnNames;
		}


		public ReferenceData[] createReferenceDataForAllDiffusionRates(double[] frapDataTimeStamps){
			ReferenceData[] referenceDataArr = new ReferenceData[analysisParameters.length];
			for (int i = 0; i < analysisParameters.length; i++) {
				referenceDataArr[i] = createReferenceData(frapDataTimeStamps,null,0,"");//new SimpleReferenceData(expRefDataLabels, expRefDataWeights, expRefDataColumns);
			}
			return referenceDataArr;
		}
		private boolean isROITypeOK(String argROIName){
			boolean bFound = false;
			for (int i = 0; i < ORDERED_ROINAMES.length; i++) {
				if(ORDERED_ROINAMES[i].equals(argROIName)){
					bFound = true;
					break;
				}
			}
			return bFound;
		}
		public ReferenceData createReferenceData(double[] frapDataTimeStamps,String argROIName,int startTimeIndex,String description){
			if(argROIName != null){
				if(!isROITypeOK(argROIName)){
					throw new IllegalArgumentException("couldn't find ROIType "+argROIName);
				}
			}
			final int numROITypes = (argROIName == null?FRAPStudy.SpatialAnalysisResults.ORDERED_ROINAMES.length:1);
			String[] expRefDataLabels = new String[numROITypes+1];
			double[] expRefDataWeights = new double[numROITypes+1];
			double[][] expRefDataColumns = new double[numROITypes+1][];//store exp data, first column t, then columns for data according to ROItype
			expRefDataLabels[0] = "t";
			expRefDataWeights[0] = 1.0;
			double[] truncatedTimes = new double[frapDataTimeStamps.length-startTimeIndex];
			System.arraycopy(frapDataTimeStamps, startTimeIndex, truncatedTimes, 0, /*shiftedSimTimes.length*/truncatedTimes.length);
			expRefDataColumns[0] = truncatedTimes;
			for (int j2 = 0; j2 < numROITypes; j2++) {
				String currentROIName = (argROIName == null?FRAPStudy.SpatialAnalysisResults.ORDERED_ROINAMES[j2]:argROIName);
				expRefDataLabels[j2+1] = (description == null?"":description)+currentROIName;
				expRefDataWeights[j2+1] = 1.0;
				double[] allTimesData = curveHash.get(new FRAPStudy.CurveInfo(null,currentROIName));//get exp data in curveHash for current ROItype
				double[] truncatedTimesData = new double[allTimesData.length-startTimeIndex];
				System.arraycopy(allTimesData, startTimeIndex, truncatedTimesData, 0, truncatedTimesData.length);
				expRefDataColumns[j2+1] = truncatedTimesData;
			}
			return new SimpleReferenceData(expRefDataLabels, expRefDataWeights, expRefDataColumns);
		}
		public ODESolverResultSet[] createODESolverResultSetForAllDiffusionRates(){
			ODESolverResultSet[] odeSolverResultSetArr = new ODESolverResultSet[analysisParameters.length];
			for (int i = 0; i < analysisParameters.length; i++) {
				odeSolverResultSetArr[i] = createODESolverResultSet(analysisParameters[i],null,"");//fitOdeSolverResultSet;
			}
			return odeSolverResultSetArr;
		}
		public ODESolverResultSet createODESolverResultSet(AnalysisParameters argAnalysisParameters,String argROIName,String description){
			if(argROIName != null){
				if(!isROITypeOK(argROIName)){
					throw new IllegalArgumentException("couldn't find ROIType "+argROIName);
				}
			}
			int analysisParametersIndex = -1;
			for (int i = 0; i < analysisParameters.length; i++) {
				if(analysisParameters[i].equals(argAnalysisParameters)){
					analysisParametersIndex = i;
					break;
				}
			}
			if(analysisParametersIndex == -1){
				throw new IllegalArgumentException("couldn't find AnalysisParameteers "+analysisParametersIndex);
			}
			int numROITypes = (argROIName == null?FRAPStudy.SpatialAnalysisResults.ORDERED_ROINAMES.length:1);
			ODESolverResultSet fitOdeSolverResultSet = new ODESolverResultSet();
			fitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription("t"));
			for (int j = 0; j < numROITypes; j++) {
				String currentROIName = (argROIName == null?FRAPStudy.SpatialAnalysisResults.ORDERED_ROINAMES[j]:argROIName);
				String name = (description == null?/*"sim D="+diffusionRates[diffusionRateIndex]+"::"*/"":description)+currentROIName;
				fitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(name));
			}
			//
			// populate time
			//
			for (int j = 0; j < shiftedSimTimes.length; j++) {
				double[] row = new double[numROITypes+1];
				row[0] = shiftedSimTimes[j];
				fitOdeSolverResultSet.addRow(row);
			}
			//
			// populate values
			//
			for (int j = 0; j < numROITypes; j++) {
				String currentROIName = (argROIName == null?FRAPStudy.SpatialAnalysisResults.ORDERED_ROINAMES[j]:argROIName);
				double[] values = curveHash.get(new FRAPStudy.CurveInfo(analysisParameters[analysisParametersIndex],currentROIName)); // get simulated data for this ROI
				for (int k = 0; k < values.length; k++) {
					fitOdeSolverResultSet.setValue(k, j+1, values[k]);
				}
			}
			return fitOdeSolverResultSet;
		}
	}//end of class SpatialAnalysisResults
	
	//
	// store the external data identifiers as annotation within a MathModel.
	//
	
	public ExternalDataInfo getFrapDataExternalDataInfo() {
		return frapDataExternalDataInfo;
	}
	
	
	public static FRAPStudy.SpatialAnalysisResults spatialAnalysis(
			PDEDataManager simulationDataManager,
			int startingIndexForRecovery,
			double startingIndexForRecoveryExperimentalTimePoint,
			String freeDiffusionRate,
			String freeMobileFraction,
			String complexDiffusionRate,
			String complexMobileFraction,
			String monitorBleachRate,
			String bsConcentration,
			String reacOnRate,
			String reacOffRate,
			FRAPData frapData,
			double[] preBleachAverageXYZ,
			DataSetControllerImpl.ProgressListener progressListener
		) throws Exception{
		

		String[] varNames = new String[] {SPECIES_NAME_PREFIX_COMBINED};
		AnalysisParameters analysisParameters =
			new AnalysisParameters(
					freeDiffusionRate,
					freeMobileFraction,
					complexDiffusionRate,
					complexMobileFraction,
					monitorBleachRate,
					bsConcentration,
					reacOnRate,
					reacOffRate);
		
		
		//
		// for each simulation time step, get the data under each ROI <indexed by ROI, then diffusion
		//
		// preallocate arrays for all data first
		Hashtable<CurveInfo, double[]> curveHash = new Hashtable<CurveInfo, double[]>();
		ArrayList<int[]> nonZeroIndicesForROI = new ArrayList<int[]>();
		for (int i = 0; i < SpatialAnalysisResults.ORDERED_ROINAMES.length; i++) {
//			for (int j = 0; j < diffusionRates.length; j++) {
				curveHash.put(new CurveInfo(analysisParameters,SpatialAnalysisResults.ORDERED_ROINAMES[i]), new double[frapData.getImageDataset().getImageTimeStamps().length-startingIndexForRecovery]);
//			}
			ROI roi_2D = frapData.getRoi(SpatialAnalysisResults.ORDERED_ROINAMES[i]);
			nonZeroIndicesForROI.add(roi_2D.getRoiImages()[0].getNonzeroIndices());
		}
		
		//
		// collect data for experiment (over all ROIs), normalized with pre-bleach average
		//
		if(progressListener != null){progressListener.updateMessage("Spatial Analysis - normalizing data...");}
		double[] avgBkIntensity = frapData.getAvgBackGroundIntensity();
		for (int i = 0; i < SpatialAnalysisResults.ORDERED_ROINAMES.length; i++) {
			double[] normalizedAverageFluorAtEachTimeUnderROI = new double[frapData.getImageDataset().getImageTimeStamps().length];
			for (int j = 0; j < normalizedAverageFluorAtEachTimeUnderROI.length; j++) {
				normalizedAverageFluorAtEachTimeUnderROI[j] = frapData.getAverageUnderROI(j,frapData.getRoi(SpatialAnalysisResults.ORDERED_ROINAMES[i]), preBleachAverageXYZ, avgBkIntensity[j]);				
			}
			curveHash.put(new CurveInfo(null,SpatialAnalysisResults.ORDERED_ROINAMES[i]), normalizedAverageFluorAtEachTimeUnderROI);
		}
		
		//
		// get times to compare experimental data with simulated results.
		//
		double[] shiftedSimTimes = null;
		if(simulationDataManager != null)
		{
			double[] simTimes = simulationDataManager.getDataSetTimes();
			shiftedSimTimes = simTimes.clone();
			for (int j = 0; j < simTimes.length; j++) {
				shiftedSimTimes[j] = simTimes[j] + startingIndexForRecoveryExperimentalTimePoint;//timeStamps[startingIndexOfRecovery];
			}
			//
			// get Simulation Data
			//
			int totalLen = simTimes.length*varNames.length*SpatialAnalysisResults.ORDERED_ROINAMES.length;
			if(progressListener != null){progressListener.updateMessage("Spatial Analysis - normalization and data reduction");}
			if(progressListener != null){progressListener.updateProgress(0);}
			
			//simulation may have different number of time points(1 more or 1 less) compare with experimental data.
			//curveHash was intialized with exp time data length. so, we have to replace it with the real sim data length.
			if(simTimes.length != (frapData.getImageDataset().getImageTimeStamps().length-startingIndexForRecovery))
			{
				for (int k = 0; k < SpatialAnalysisResults.ORDERED_ROINAMES.length; k++) {
					CurveInfo ci = new CurveInfo(analysisParameters,SpatialAnalysisResults.ORDERED_ROINAMES[k]);
					curveHash.remove(ci);
					curveHash.put(ci, new double[simTimes.length]);
				}
			}
			for (int i = 0; i < simTimes.length; i++) {
				for (int j = 0; j < varNames.length; j++) {
					SimDataBlock simDataBlock = simulationDataManager.getSimDataBlock(varNames[j], simTimes[i]);
					double[] data = simDataBlock.getData();
					for (int k = 0; k < SpatialAnalysisResults.ORDERED_ROINAMES.length; k++) {
						CurveInfo ci = new CurveInfo(analysisParameters,SpatialAnalysisResults.ORDERED_ROINAMES[k]);
						int[] roiIndices = nonZeroIndicesForROI.get(k);
						if(roiIndices != null && roiIndices.length > 0){
							double accum = 0.0;
							for (int index = 0; index < roiIndices.length; index++) {
								accum += data[roiIndices[index]];
							}
							double[] values = curveHash.get(ci);
							values[i] = accum/roiIndices.length;
						}
						double currentLen = i*varNames.length*SpatialAnalysisResults.ORDERED_ROINAMES.length + j*SpatialAnalysisResults.ORDERED_ROINAMES.length + k;
						if(progressListener != null){progressListener.updateProgress(currentLen/totalLen);}
					}
				}
			}
		}
		SpatialAnalysisResults spatialAnalysisResults = 
			new SpatialAnalysisResults(new AnalysisParameters[] {analysisParameters},/*varNames,*/shiftedSimTimes,curveHash);
		return spatialAnalysisResults;
	}
	
	public static void runFVSolverStandalone(
			File simulationDataDir,
			SessionLog sessionLog,
			Simulation sim,
			ExternalDataIdentifier imageDataExtDataID,
			ExternalDataIdentifier roiExtDataID,
			ProgressListener progressListener) throws Exception{
		runFVSolverStandalone(simulationDataDir, sessionLog, sim, imageDataExtDataID, roiExtDataID, progressListener, false);
	}
	
	public static void runFVSolverStandalone(
		File simulationDataDir,
		SessionLog sessionLog,
		Simulation sim,
		ExternalDataIdentifier imageDataExtDataID,
		ExternalDataIdentifier roiExtDataID,
		ProgressListener progressListener,
		boolean bCheckSteadyState) throws Exception{

		FieldFunctionArguments[] fieldFunctionArgs = sim.getMathDescription().getFieldFunctionArguments();
		FieldDataIdentifierSpec[] fieldDataIdentifierSpecs = new FieldDataIdentifierSpec[fieldFunctionArgs.length];
		for (int i = 0; i < fieldDataIdentifierSpecs.length; i++) {
			if (fieldFunctionArgs[i].getFieldName().equals(imageDataExtDataID.getName())){
				fieldDataIdentifierSpecs[i] = new FieldDataIdentifierSpec(fieldFunctionArgs[i],imageDataExtDataID);
			}else if (fieldFunctionArgs[i].getFieldName().equals(roiExtDataID.getName())){
				fieldDataIdentifierSpecs[i] = new FieldDataIdentifierSpec(fieldFunctionArgs[i],roiExtDataID);
			}else{
				throw new RuntimeException("failed to resolve field named "+fieldFunctionArgs[i].getFieldName());
			}
		}
		
		int jobIndex = 0;
		SimulationJob simJob = new SimulationJob(sim,fieldDataIdentifierSpecs,jobIndex);
		
		//FVSolverStandalone class expects the PropertyLoader.finiteVolumeExecutableProperty to exist
		System.setProperty(PropertyLoader.finiteVolumeExecutableProperty, LocalWorkspace.getFinitVolumeExecutableFullPathname());
		//if we need to check steady state, do the following two lines
		if(bCheckSteadyState)
		{
			simJob.getWorkingSim().getSolverTaskDescription().setStopAtSpatiallyUniform(true);
			simJob.getWorkingSim().getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-6, 1e-2));
		}
		
		FVSolverStandalone fvSolver = new FVSolverStandalone(simJob,simulationDataDir,sessionLog,false);
		fvSolver.startSolver();
		
		SolverStatus status = fvSolver.getSolverStatus();
		while (status.getStatus() != SolverStatus.SOLVER_FINISHED && status.getStatus() != SolverStatus.SOLVER_ABORTED )
		{
			if(progressListener != null){
				progressListener.updateProgress(fvSolver.getProgress());
			}
			Thread.sleep(100);
			status = fvSolver.getSolverStatus();
		}

		if(status.getStatus() == SolverStatus.SOLVER_FINISHED){
			String roiMeshFileName =
				SimulationData.createCanonicalMeshFileName(
					roiExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT, false);
			String imageDataMeshFileName =
				SimulationData.createCanonicalMeshFileName(
					imageDataExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT, false);
			String simulationMeshFileName =
				SimulationData.createCanonicalMeshFileName(
					sim.getVersion().getVersionKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT, false);
			// delete old external data mesh files and copy simulation mesh file to them
			File roiMeshFile = new File(simulationDataDir,roiMeshFileName);
			File imgMeshFile = new File(simulationDataDir,imageDataMeshFileName);
			File simMeshFile = new File(simulationDataDir,simulationMeshFileName);
			if(!roiMeshFile.delete()){throw new Exception("Couldn't delete ROI Mesh file "+roiMeshFile.getAbsolutePath());}
			if(!imgMeshFile.delete()){throw new Exception("Couldn't delete ImageData Mesh file "+imgMeshFile.getAbsolutePath());}
			FileUtils.copyFile(simMeshFile, roiMeshFile);
			FileUtils.copyFile(simMeshFile, imgMeshFile);
		}
		else{
			throw new Exception("Sover did not finish normally." + status.toString());
		}
	}

	public static ExternalDataInfo createNewExternalDataInfo(LocalWorkspace localWorkspace,String extDataIDName){
		File targetDir = new File(localWorkspace.getDefaultSimDataDirectory());
		ExternalDataIdentifier newImageDataExtDataID =
			new ExternalDataIdentifier(LocalWorkspace.createNewKeyValue(),
					LocalWorkspace.getDefaultOwner(),extDataIDName);
		ExternalDataInfo newImageDataExtDataInfo =
			new ExternalDataInfo(newImageDataExtDataID,
				new File(targetDir,newImageDataExtDataID.getID()+SimDataConstants.LOGFILE_EXTENSION).getAbsolutePath());
		return newImageDataExtDataInfo;
	}
	public void setFrapDataExternalDataInfo(ExternalDataInfo imageDatasetExternalDataInfo) {
		ExternalDataInfo oldValue = this.frapDataExternalDataInfo;
		this.frapDataExternalDataInfo = imageDatasetExternalDataInfo;
		propertyChangeSupport.firePropertyChange("imageDatasetExternalDataInfo", oldValue, imageDatasetExternalDataInfo);
	}

	public ExternalDataInfo getRoiExternalDataInfo() {
		return roiExternalDataInfo;
	}

	public void setRoiExternalDataInfo(ExternalDataInfo roiExternalDataInfo) {
		ExternalDataInfo oldValue = this.roiExternalDataInfo;
		this.roiExternalDataInfo = roiExternalDataInfo;
		propertyChangeSupport.firePropertyChange("roiExternalDataInfo", oldValue, roiExternalDataInfo);
	}
		
	public BioModel getBioModel() {
		return bioModel;
	}
	/*
	 * for selected models
	 */
	public ArrayList<String> getSelectedModels()
	{
		return selectedModels;
	}
	
	public void addAllSelectedModel(String[] modelTypes)
	{
		selectedModels.clear();
		for(int i=0; i<modelTypes.length; i++)
		{
			selectedModels.add(modelTypes[i]);
		}
	}
	
	public boolean hasSelectedModel(String aModelType)
	{
		for(int i=0; i < selectedModels.size(); i++)
		{
			if(selectedModels.get(i).equals(aModelType))
			{
				return true;
			}
		}
		return false;
	}
	
	public void clearSelectedModel()
	{
		selectedModels.clear();
	}
	
	public static void removeExternalDataAndSimulationFiles(
			KeyValue simulationKeyValue,
			ExternalDataIdentifier frapDataExtDataId,ExternalDataIdentifier roiExtDataId,
			LocalWorkspace localWorkspace) throws Exception{
		
		if(frapDataExtDataId != null){
			FRAPStudy.deleteCanonicalExternalData(localWorkspace,frapDataExtDataId);
		}
		if(roiExtDataId != null){
			FRAPStudy.deleteCanonicalExternalData(localWorkspace,roiExtDataId);
		}
		if(frapDataExtDataId != null && roiExtDataId != null){
			File mergedFunctionFile = 
				FRAPStudy.getMergedFunctionFile(frapDataExtDataId,roiExtDataId,
						new File(localWorkspace.getDefaultSimDataDirectory()));
			if(mergedFunctionFile != null){
				mergedFunctionFile.delete();
			}
		}
		if(simulationKeyValue != null){
			File userDir =
				new File(localWorkspace.getDefaultSimDataDirectory());
			FRAPStudy.deleteSimulationFiles(userDir, simulationKeyValue);
		}
	}

	private static File getMergedFunctionFile(ExternalDataIdentifier frapDataExtDataId,ExternalDataIdentifier roiExtDataId,
			File simDataDirectory)
	{
			VCDataIdentifier[] vcIdentifierArray = new VCDataIdentifier[]{frapDataExtDataId,roiExtDataId};
			MergedDataInfo mergedDataInfo =
				new MergedDataInfo(LocalWorkspace.getDefaultOwner(),
					new VCDataIdentifier[]{frapDataExtDataId,roiExtDataId}, FRAPStudyPanel.VFRAP_DS_PREFIX);
			return
				new File(simDataDirectory,
					mergedDataInfo.getID()+SimDataConstants.FUNCTIONFILE_EXTENSION);
	}

	private static void deleteSimulationFiles(File rootDir,KeyValue simKey){
		File[] oldSimFilesToDelete = FRAPStudy.getSimulationFileNames(rootDir,simKey);
		for (int i = 0; oldSimFilesToDelete != null && i < oldSimFilesToDelete.length; i++) {
			System.out.println("delete "+oldSimFilesToDelete[i].delete()+"  "+oldSimFilesToDelete[i].getAbsolutePath());
		}
	}

	public static File[] getSimulationFileNames(File rootDir,KeyValue simKey){
		final String deleteTheseSimID = Simulation.createSimulationID(simKey);
		return
			rootDir.listFiles(
				new FileFilter(){
					public boolean accept(File pathname) {
						if (pathname.getName().startsWith(deleteTheseSimID)){
							return true;
						}
						return false;
					}
				}
			);
	}
	
	public static BioModel createNewRefBioModel(
			FRAPStudy sourceFrapStudy,
			String baseDiffusionRate,
			TimeStep tStep,
			KeyValue simKey,
			User owner,
			int startingIndexForRecovery) throws Exception {

		if (sourceFrapStudy==null){
			throw new Exception("No FrapStudy is defined");
		}
		if (sourceFrapStudy.getFrapData()==null){
			throw new Exception("No FrapData is defined");
		}
		if (sourceFrapStudy.getFrapData().getImageDataset()==null){
			throw new Exception("No ImageDataSet is defined");
		}
		ROI cellROI_2D = sourceFrapStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
		if (cellROI_2D==null){
			throw new Exception("No Cell ROI is defined");
		}
		if(owner == null){
			throw new Exception("Owner is not defined");
		}

		Extent extent = sourceFrapStudy.getFrapData().getImageDataset().getExtent();

		double[] timeStamps = sourceFrapStudy.getFrapData().getImageDataset().getImageTimeStamps();
		TimeBounds timeBounds = new TimeBounds(0.0,timeStamps[timeStamps.length-1]-timeStamps[startingIndexForRecovery]);
		double timeStepVal = timeStamps[startingIndexForRecovery+1] - timeStamps[startingIndexForRecovery];
		TimeStep timeStep = null;
		if(tStep != null)
		{
			timeStep = tStep;
		}
		else
		{
			timeStep = new TimeStep(timeStepVal, timeStepVal, timeStepVal);
		}
		
		int numX = cellROI_2D.getRoiImages()[0].getNumX();
		int numY = cellROI_2D.getRoiImages()[0].getNumY();
		int numZ = cellROI_2D.getRoiImages().length;
		short[] shortPixels = cellROI_2D.getRoiImages()[0].getPixels();
		byte[] bytePixels = new byte[numX*numY*numZ];
		final byte EXTRACELLULAR_PIXVAL = 0;
		final byte CYTOSOL_PIXVAL = 1;
		for (int i = 0; i < bytePixels.length; i++) {
			if (shortPixels[i]!=0){
				bytePixels[i] = CYTOSOL_PIXVAL;
			}
		}
		VCImage maskImage;
		try {
			maskImage = new VCImageUncompressed(null,bytePixels,extent,numX,numY,numZ);
		} catch (ImageException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create mask image for geometry");
		}
		Geometry geometry = new Geometry("geometry",maskImage);
		if(geometry.getGeometrySpec().getNumSubVolumes() != 2){
			throw new Exception("Cell ROI has no ExtraCellular.");
		}
		int subVolume0PixVal = ((ImageSubVolume)geometry.getGeometrySpec().getSubVolume(0)).getPixelValue();
		geometry.getGeometrySpec().getSubVolume(0).setName((subVolume0PixVal == EXTRACELLULAR_PIXVAL?EXTRACELLULAR_NAME:CYTOSOL_NAME));
		int subVolume1PixVal = ((ImageSubVolume)geometry.getGeometrySpec().getSubVolume(1)).getPixelValue();
		geometry.getGeometrySpec().getSubVolume(1).setName((subVolume1PixVal == CYTOSOL_PIXVAL?CYTOSOL_NAME:EXTRACELLULAR_NAME));
		geometry.getGeometrySurfaceDescription().updateAll();

		BioModel bioModel = new BioModel(null);
		bioModel.setName("unnamed");
		Model model = new Model("model");
		bioModel.setModel(model);
		model.addFeature(EXTRACELLULAR_NAME, null, null);
		Feature extracellular = (Feature)model.getStructure(EXTRACELLULAR_NAME);
		model.addFeature(CYTOSOL_NAME, extracellular, "plasmaMembrane");
		Feature cytosol = (Feature)model.getStructure(CYTOSOL_NAME);

		String roiDataName = "roiData";
		
		final int ONE_DIFFUSION_SPECIES_COUNT = 1;
		final int MOBILE_SPECIES_INDEX = 0;
				
		Expression[] diffusionConstants = new Expression[ONE_DIFFUSION_SPECIES_COUNT];
		Species[] species = new Species[ONE_DIFFUSION_SPECIES_COUNT]; 
		SpeciesContext[] speciesContexts = new SpeciesContext[ONE_DIFFUSION_SPECIES_COUNT];
		Expression[] initialConditions = new Expression[ONE_DIFFUSION_SPECIES_COUNT];
		
		//Mobile Species
		diffusionConstants[MOBILE_SPECIES_INDEX] = new Expression(baseDiffusionRate);
		species[MOBILE_SPECIES_INDEX] =
				new Species(SPECIES_NAME_PREFIX_MOBILE, "Mobile bleachable species");
		speciesContexts[MOBILE_SPECIES_INDEX] = 
				new SpeciesContext(null,species[MOBILE_SPECIES_INDEX].getCommonName(),species[MOBILE_SPECIES_INDEX],cytosol,true);
		initialConditions[MOBILE_SPECIES_INDEX] =
				new Expression("(field("+roiDataName+",postbleach_first,0) / field("+roiDataName+",prebleach_avg,0))");
		
		SimulationContext simContext = new SimulationContext(bioModel.getModel(),geometry);
		bioModel.addSimulationContext(simContext);
		FeatureMapping cytosolFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(cytosol);
		cytosolFeatureMapping.setSubVolume(geometry.getGeometrySpec().getSubVolume(CYTOSOL_NAME));
		cytosolFeatureMapping.setResolved(true);
		FeatureMapping extracellularFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(extracellular);
		extracellularFeatureMapping.setSubVolume(geometry.getGeometrySpec().getSubVolume(EXTRACELLULAR_NAME));
		extracellularFeatureMapping.setResolved(true);
		
		for (int i = 0; i < initialConditions.length; i++) {
			model.addSpecies(species[i]);
			model.addSpeciesContext(speciesContexts[i]);
		}
		
		for (int i = 0; i < speciesContexts.length; i++) {
			SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(speciesContexts[i]);
			scs.getInitialConditionParameter().setExpression(initialConditions[i]);
			scs.getDiffusionParameter().setExpression(diffusionConstants[i]);
		}

		MathMapping mathMapping = new MathMapping(simContext);
		MathDescription mathDesc = mathMapping.getMathDescription();
		simContext.setMathDescription(mathDesc);
				
		SimulationVersion simVersion = new SimulationVersion(simKey,"sim1",owner,new GroupAccessNone(),new KeyValue("0"),new BigDecimal(0),new Date(),VersionFlag.Current,"",null);
		Simulation newSimulation = new Simulation(simVersion,simContext.getMathDescription());
//		newSimulation.getSolverTaskDescription().setSolverDescription(SolverDescription.SundialsPDE);
//		newSimulation.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec(timeStepVal));
		simContext.addSimulation(newSimulation);
		newSimulation.getSolverTaskDescription().setTimeBounds(timeBounds);
		newSimulation.getMeshSpecification().setSamplingSize(cellROI_2D.getISize());
		newSimulation.getSolverTaskDescription().setTimeStep(timeStep);
		
		return bioModel;
	}
	
	public static BioModel createNewSimBioModel(
			FRAPStudy sourceFrapStudy,
			Parameter[] params,
			TimeStep tStep,
			KeyValue simKey,
			User owner,
			int startingIndexForRecovery) throws Exception {

		if (sourceFrapStudy==null){
			throw new Exception("No FrapStudy is defined");
		}
		if (sourceFrapStudy.getFrapData()==null){
			throw new Exception("No FrapData is defined");
		}
		if (sourceFrapStudy.getFrapData().getImageDataset()==null){
			throw new Exception("No ImageDataSet is defined");
		}
		ROI cellROI_2D = sourceFrapStudy.getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
		if (cellROI_2D==null){
			throw new Exception("No Cell ROI is defined");
		}
		if(owner == null){
			throw new Exception("Owner is not defined");
		}

		double df = params[FRAPReactionDiffusionParamPanel.INDEX_FREE_DIFF_RATE].getInitialGuess();
		double ff = params[FRAPReactionDiffusionParamPanel.INDEX_FREE_FRACTION].getInitialGuess();
		double bwmRate = params[FRAPReactionDiffusionParamPanel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess();
		double dc = params[FRAPReactionDiffusionParamPanel.INDEX_COMPLEX_DIFF_RATE].getInitialGuess();
		double fc =params[FRAPReactionDiffusionParamPanel.INDEX_COMPLEX_FRACTION].getInitialGuess();
		double fimm = params[FRAPReactionDiffusionParamPanel.INDEX_IMMOBILE_FRACTION].getInitialGuess();
		double bs = params[FRAPReactionDiffusionParamPanel.INDEX_BINDING_SITE_CONCENTRATION].getInitialGuess();
		double onRate = params[FRAPReactionDiffusionParamPanel.INDEX_ON_RATE].getInitialGuess();
		double offRate = params[FRAPReactionDiffusionParamPanel.INDEX_OFF_RATE].getInitialGuess();
		
		Extent extent = sourceFrapStudy.getFrapData().getImageDataset().getExtent();

		double[] timeStamps = sourceFrapStudy.getFrapData().getImageDataset().getImageTimeStamps();
		TimeBounds timeBounds = new TimeBounds(0.0,timeStamps[timeStamps.length-1]-timeStamps[startingIndexForRecovery]);
		double timeStepVal = timeStamps[startingIndexForRecovery+1] - timeStamps[startingIndexForRecovery];
//		double defaultReacDiffTimeStep = 0.001;
//		TimeStep timeStep = new TimeStep(defaultReacDiffTimeStep, defaultReacDiffTimeStep, defaultReacDiffTimeStep);//not used, too small that run very slowly
		int keepEvery =1;
		DefaultOutputTimeSpec timeSpec = new DefaultOutputTimeSpec(keepEvery, 1000);//not used until we use smaller time step

		TimeStep timeStep = null;
		if(tStep != null)
		{
			timeStep = tStep;
		}
		else
		{
			timeStep = new TimeStep(timeStepVal, timeStepVal, timeStepVal);
		}
		
		int numX = cellROI_2D.getRoiImages()[0].getNumX();
		int numY = cellROI_2D.getRoiImages()[0].getNumY();
		int numZ = cellROI_2D.getRoiImages().length;
		short[] shortPixels = cellROI_2D.getRoiImages()[0].getPixels();
		byte[] bytePixels = new byte[numX*numY*numZ];
		final byte EXTRACELLULAR_PIXVAL = 0;
		final byte CYTOSOL_PIXVAL = 1;
		for (int i = 0; i < bytePixels.length; i++) {
			if (shortPixels[i]!=0){
				bytePixels[i] = CYTOSOL_PIXVAL;
			}
		}
		VCImage maskImage;
		try {
			maskImage = new VCImageUncompressed(null,bytePixels,extent,numX,numY,numZ);
		} catch (ImageException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create mask image for geometry");
		}
		Geometry geometry = new Geometry("geometry",maskImage);
		if(geometry.getGeometrySpec().getNumSubVolumes() != 2){
			throw new Exception("Cell ROI has no ExtraCellular.");
		}
		int subVolume0PixVal = ((ImageSubVolume)geometry.getGeometrySpec().getSubVolume(0)).getPixelValue();
		geometry.getGeometrySpec().getSubVolume(0).setName((subVolume0PixVal == EXTRACELLULAR_PIXVAL?EXTRACELLULAR_NAME:CYTOSOL_NAME));
		int subVolume1PixVal = ((ImageSubVolume)geometry.getGeometrySpec().getSubVolume(1)).getPixelValue();
		geometry.getGeometrySpec().getSubVolume(1).setName((subVolume1PixVal == CYTOSOL_PIXVAL?CYTOSOL_NAME:EXTRACELLULAR_NAME));
		geometry.getGeometrySurfaceDescription().updateAll();

		BioModel bioModel = new BioModel(null);
		bioModel.setName("unnamed");
		Model model = new Model("model");
		bioModel.setModel(model);
		model.addFeature(EXTRACELLULAR_NAME, null, null);
		Feature extracellular = (Feature)model.getStructure(EXTRACELLULAR_NAME);
		model.addFeature(CYTOSOL_NAME, extracellular, "plasmaMembrane");
		Feature cytosol = (Feature)model.getStructure(CYTOSOL_NAME);

		String roiDataName = "roiData";
		
		final int SPECIES_COUNT = 4;
		final int FREE_SPECIES_INDEX = 0;
		final int BS_SPECIES_INDEX = 1;
		final int COMPLEX_SPECIES_INDEX = 2;
		final int IMMOBILE_SPECIES_INDEX =3;
		
		Expression[] diffusionConstants = null;
		Species[] species = null;
		SpeciesContext[] speciesContexts = null;
		Expression[] initialConditions = null;
		
		
		diffusionConstants = new Expression[SPECIES_COUNT];
		species = new Species[SPECIES_COUNT];
		speciesContexts = new SpeciesContext[SPECIES_COUNT];
		initialConditions = new Expression[SPECIES_COUNT];
				
		//Free Species
		diffusionConstants[FREE_SPECIES_INDEX] = new Expression(df);
		species[FREE_SPECIES_INDEX] =
				new Species(FRAPStudy.SPECIES_NAME_PREFIX_MOBILE,	"Mobile bleachable species");
		speciesContexts[FREE_SPECIES_INDEX] = 
				new SpeciesContext(null,species[FREE_SPECIES_INDEX].getCommonName(),species[FREE_SPECIES_INDEX],cytosol,true);
		initialConditions[FREE_SPECIES_INDEX] =
				new Expression(ff+"*(field("+roiDataName+",postbleach_first,0) / field("+roiDataName+",prebleach_avg,0))");
		
		//Immobile Species (No diffusion)
		//Set very small diffusion rate on immobile to force evaluation as state variable (instead of FieldData function)
		//If left as a function errors occur because functions involving FieldData require a database connection
		final String IMMOBILE_DIFFUSION_KLUDGE = "1e-14";
		diffusionConstants[IMMOBILE_SPECIES_INDEX] = new Expression(IMMOBILE_DIFFUSION_KLUDGE);
		species[IMMOBILE_SPECIES_INDEX] =
				new Species(FRAPStudy.SPECIES_NAME_PREFIX_IMMOBILE,"Immobile bleachable species");
		speciesContexts[IMMOBILE_SPECIES_INDEX] = 
				new SpeciesContext(null,species[IMMOBILE_SPECIES_INDEX].getCommonName(),species[IMMOBILE_SPECIES_INDEX],cytosol,true);
		initialConditions[IMMOBILE_SPECIES_INDEX] =
				new Expression(fimm+"*(field("+roiDataName+",postbleach_first,0) / field("+roiDataName+",prebleach_avg,0))");

		//BS Species
		diffusionConstants[BS_SPECIES_INDEX] = new Expression(IMMOBILE_DIFFUSION_KLUDGE);
		species[BS_SPECIES_INDEX] =
				new Species(FRAPStudy.SPECIES_NAME_PREFIX_BINDING_SITE,"Binding Site species");
		speciesContexts[BS_SPECIES_INDEX] = 
				new SpeciesContext(null,species[BS_SPECIES_INDEX].getCommonName(),species[BS_SPECIES_INDEX],cytosol,true);
		initialConditions[BS_SPECIES_INDEX] =
				new Expression(bs+"* field("+roiDataName+",prebleach_avg,0)");
	
		//Complex species
		diffusionConstants[COMPLEX_SPECIES_INDEX] = new Expression(dc);
		species[COMPLEX_SPECIES_INDEX] =
				new Species(FRAPStudy.SPECIES_NAME_PREFIX_SLOW_MOBILE, "Slower mobile bleachable species");
		speciesContexts[COMPLEX_SPECIES_INDEX] = 
				new SpeciesContext(null,species[COMPLEX_SPECIES_INDEX].getCommonName(),species[COMPLEX_SPECIES_INDEX],cytosol,true);
		initialConditions[COMPLEX_SPECIES_INDEX] =
				new Expression(fc+"*(field("+roiDataName+",postbleach_first,0) / field("+roiDataName+",prebleach_avg,0))");
				
	
		// add reactions to species if there is bleachWhileMonitoring rate.
		for (int i = 0; i < initialConditions.length; i++) {
			model.addSpecies(species[i]);
			model.addSpeciesContext(speciesContexts[i]);
			//reaction with BMW rate, which should not be applied to binding site
			if(!(species[i].getCommonName().equals(FRAPStudy.SPECIES_NAME_PREFIX_BINDING_SITE)))
			{
				SimpleReaction simpleReaction = new SimpleReaction(cytosol,speciesContexts[i].getName()+"_bleach");
				model.addReactionStep(simpleReaction);
				simpleReaction.addReactant(speciesContexts[i], 1);
				MassActionKinetics massActionKinetics = new MassActionKinetics(simpleReaction);
				simpleReaction.setKinetics(massActionKinetics);
				KineticsParameter kforward = massActionKinetics.getForwardRateParameter();
				simpleReaction.getKinetics().setParameterValue(kforward, new Expression(new Double(bwmRate)));
			}
		}

		// add the binding reaction: F + BS <-> C
		SimpleReaction simpleReaction2 = new SimpleReaction(cytosol,"reac_binding");
		model.addReactionStep(simpleReaction2);
		simpleReaction2.addReactant(speciesContexts[FREE_SPECIES_INDEX], 1);
		simpleReaction2.addReactant(speciesContexts[BS_SPECIES_INDEX], 1);
		simpleReaction2.addProduct(speciesContexts[COMPLEX_SPECIES_INDEX], 1);
		MassActionKinetics massActionKinetics = new MassActionKinetics(simpleReaction2);
		simpleReaction2.setKinetics(massActionKinetics);
		KineticsParameter kforward = massActionKinetics.getForwardRateParameter();
		KineticsParameter kreverse = massActionKinetics.getReverseRateParameter();
		simpleReaction2.getKinetics().setParameterValue(kforward, new Expression(new Double(onRate)));
		simpleReaction2.getKinetics().setParameterValue(kreverse, new Expression(new Double(offRate)));
		
		//create simulation context		
		SimulationContext simContext = new SimulationContext(bioModel.getModel(),geometry);
		bioModel.addSimulationContext(simContext);
		FeatureMapping cytosolFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(cytosol);
		cytosolFeatureMapping.setSubVolume(geometry.getGeometrySpec().getSubVolume(FRAPStudy.CYTOSOL_NAME));
		cytosolFeatureMapping.setResolved(true);
		FeatureMapping extracellularFeatureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(extracellular);
		extracellularFeatureMapping.setSubVolume(geometry.getGeometrySpec().getSubVolume(FRAPStudy.EXTRACELLULAR_NAME));
		extracellularFeatureMapping.setResolved(true);
		
		for (int i = 0; i < speciesContexts.length; i++) {
			SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(speciesContexts[i]);
			scs.getInitialConditionParameter().setExpression(initialConditions[i]);
			scs.getDiffusionParameter().setExpression(diffusionConstants[i]);
		}

		MathMapping mathMapping = new MathMapping(simContext);
		MathDescription mathDesc = mathMapping.getMathDescription();
		//Add total fluorescence as function of mobile(optional: and slower mobile) and immobile fractions
		mathDesc.addVariable(
				new Function(FRAPStudy.SPECIES_NAME_PREFIX_COMBINED,
					new Expression(species[FREE_SPECIES_INDEX].getCommonName()+"+"+species[COMPLEX_SPECIES_INDEX].getCommonName()+"+"+species[IMMOBILE_SPECIES_INDEX].getCommonName())));
		simContext.setMathDescription(mathDesc);

		SimulationVersion simVersion = new SimulationVersion(simKey,"sim1",owner,new GroupAccessNone(),new KeyValue("0"),new BigDecimal(0),new Date(),VersionFlag.Current,"",null);
		Simulation newSimulation = new Simulation(simVersion,mathDesc);
		simContext.addSimulation(newSimulation);
		newSimulation.getSolverTaskDescription().setTimeBounds(timeBounds);
		newSimulation.getMeshSpecification().setSamplingSize(cellROI_2D.getISize());
		newSimulation.getSolverTaskDescription().setTimeStep(timeStep);
		newSimulation.getSolverTaskDescription().setOutputTimeSpec(timeSpec);//not used util we apply smaller time step
		
		return bioModel;
	}
	
	private KernelJAI createCircularBinaryKernel(int radius){
		int enclosingBoxSideLength = radius*2+1;
		float[] kernalData = new float[enclosingBoxSideLength*enclosingBoxSideLength];
		Point2D kernalPoint = new Point2D.Float(0f,0f);
		int index = 0;
		for (int y = -radius; y <= radius; y++) {
			for (int x = -radius; x <= radius; x++) {
				if(kernalPoint.distance(x, y) <= radius){
					kernalData[index] = 1.0f;
				}
				index++;
			}
		}
		return new KernelJAI(enclosingBoxSideLength,enclosingBoxSideLength,radius,radius,kernalData);
	}
	private PlanarImage binarize(UShortImage source){
		return binarize(AWTImageTools.makeImage(source.getPixels(), source.getNumX(), source.getNumY(),false));
	}
	private PlanarImage binarize(BufferedImage source){
		PlanarImage planarSource = PlanarImage.wrapRenderedImage(source);
		double[][] minmaxArr = (double[][])ExtremaDescriptor.create(planarSource, null, 1, 1, false, 1,null).getProperty("extrema");
		short[] lookupData = new short[(int)0x010000];
		lookupData[(int)minmaxArr[1][0]] = 1;
		LookupTableJAI lookupTable = new LookupTableJAI(lookupData,true);
		planarSource = LookupDescriptor.create(planarSource, lookupTable, null).createInstance();
		return planarSource;		
	}
	private UShortImage convertToUShortImage(PlanarImage source,Origin origin,Extent extent) throws ImageException{
    	short[] shortData = new short[source.getWidth() * source.getHeight()];
    	source.getData().getDataElements(0, 0, source.getWidth(),source.getHeight(), shortData);
    	return new UShortImage(shortData,origin,extent,source.getWidth(),source.getHeight(),1);	
	}
	private UShortImage erodeDilate(UShortImage source,KernelJAI dilateErodeKernel,UShortImage mask,boolean bErode) throws ImageException{
		PlanarImage completedImage = null;
		PlanarImage operatedImage = null;
		PlanarImage planarSource = binarize(source);
		Integer borderPad = dilateErodeKernel.getWidth()/2;
		planarSource = 
			BorderDescriptor.create(planarSource,
				borderPad, borderPad, borderPad, borderPad,
				BorderExtender.createInstance(BorderExtender.BORDER_ZERO), null).createInstance();
		if(bErode){
			planarSource = AddConstDescriptor.create(planarSource, new double[] {1.0}, null).createInstance();
	    	RenderedOp erodeOP = ErodeDescriptor.create(planarSource, dilateErodeKernel, null);
	    	operatedImage = erodeOP.createInstance();
			
		}else{
	    	RenderedOp dilationOP = DilateDescriptor.create(planarSource, dilateErodeKernel, null);
	    	operatedImage = dilationOP.createInstance();
		}
		operatedImage =
    		CropDescriptor.create(operatedImage,
    			new Float(0), new Float(0),
    			new Float(source.getNumX()), new Float(source.getNumY()), null);
    	operatedImage = binarize(operatedImage.getAsBufferedImage());
		if (mask != null) {
			RenderedOp andDescriptor = AndDescriptor.create(operatedImage,binarize(mask), null);
			completedImage = andDescriptor.createInstance();
		}else{
			completedImage = operatedImage;
		}
		return convertToUShortImage(completedImage, source.getOrigin(),source.getExtent());
    	
}
//	private void writeUShortFile(UShortImage uShortImage,File outFile){
//		writeBufferedImageFile(
//			ImageTools.makeImage(uShortImage.getPixels(),uShortImage.getNumX(), uShortImage.getNumY()),outFile);
//
//	}
//	private void writeBufferedImageFile(BufferedImage bufferedImage,File outFile){
//		try{
//		ImageIO.write(
//			FormatDescriptor.create(bufferedImage, DataBuffer.TYPE_BYTE,null).createInstance(),
//			"bmp", outFile);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//	}
	private UShortImage fastDilate(UShortImage dilateSource,int radius,UShortImage mask) throws ImageException{
		short[] sourcePixels = dilateSource.getPixels();
		short[] targetPixels = dilateSource.getPixels().clone();
		KernelJAI dilateKernel = createCircularBinaryKernel(radius);
		float[] kernelData = dilateKernel.getKernelData();
		BitSet kernelBitSet = new BitSet(kernelData.length);
		for (int i = 0; i < kernelData.length; i++) {
			if(kernelData[i] == 1.0f){
				kernelBitSet.set(i);
			}
		}
		boolean bNeedsFill = false;
		for (int y = 0; y < dilateSource.getNumY(); y++) {
			int yOffset = y*dilateSource.getNumX();
			int yMinus = yOffset-dilateSource.getNumX();
			int yPlus = yOffset+dilateSource.getNumX();
			for (int x = 0; x < dilateSource.getNumX(); x++) {
				bNeedsFill = false;
				if(sourcePixels[x+yOffset] != 0){
					if(x-1 >= 0 && sourcePixels[(x-1)+yOffset] == 0){
						bNeedsFill = true;
					}else
					if(y-1 >= 0 && sourcePixels[x+yMinus] == 0){
						bNeedsFill = true;
					}else
					if(x+1 < dilateSource.getNumX() && sourcePixels[(x+1)+yOffset] == 0){
						bNeedsFill = true;
					}else
					if(y+1 < dilateSource.getNumY() && sourcePixels[x+yPlus] == 0){
						bNeedsFill = true;
					}
					if(bNeedsFill){
						int masterKernelIndex = 0;
						for (int y2 = y-radius; y2 <= y+radius; y2++) {
							if(y2>= 0 && y2 <dilateSource.getNumY()){
								int kernelIndex = masterKernelIndex;
								int targetYIndex = y2*dilateSource.getNumX();
								for (int x2 = x-radius; x2 <= x+radius; x2++) {
									if(kernelBitSet.get(kernelIndex) &&
										x2>= 0 && x2 <dilateSource.getNumX()){
										targetPixels[targetYIndex+x2] = 1;
									}
									kernelIndex++;
								}
							}
							masterKernelIndex+= dilateKernel.getWidth();
						}
					}
				}
			}
		}
		UShortImage resultImage =
			new UShortImage(targetPixels,
					dilateSource.getOrigin(),
					dilateSource.getExtent(),
					dilateSource.getNumX(),
					dilateSource.getNumY(),
					dilateSource.getNumZ());
		resultImage.and(mask);
		return resultImage;
	}
	
	public void refreshDependentROIs(){
		UShortImage cellROI_2D = null;
		UShortImage bleachedROI_2D = null;
		UShortImage dilatedROI_2D_1 = null;
    	UShortImage dilatedROI_2D_2 = null;
    	UShortImage dilatedROI_2D_3 = null;
    	UShortImage dilatedROI_2D_4 = null;
    	UShortImage dilatedROI_2D_5 = null;
    	UShortImage erodedROI_2D_0 = null;
    	UShortImage erodedROI_2D_1 = null;
    	UShortImage erodedROI_2D_2 = null;

    	try {
    		cellROI_2D =
    			convertToUShortImage(binarize(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getRoiImages()[0]),
    				getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getRoiImages()[0].getOrigin(),
    				getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getRoiImages()[0].getExtent());
    		bleachedROI_2D =
    			convertToUShortImage(
    					AndDescriptor.create(binarize(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()).getRoiImages()[0]),
    						binarize(cellROI_2D), null).createInstance(),
    					getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()).getRoiImages()[0].getOrigin(),
    					getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()).getRoiImages()[0].getExtent());
//			writeUShortFile(cellROI_2D, new File("D:\\developer\\eclipse\\workspace\\cellROI_2D.bmp"));
//			writeUShortFile(bleachedROI_2D, new File("D:\\developer\\eclipse\\workspace\\bleachedROI_2D.bmp"));

    		dilatedROI_2D_1 =
    			fastDilate(bleachedROI_2D, 4, cellROI_2D);
//    			erodeDilate(bleachedROI_2D, createCircularBinaryKernel(8), cellROI_2D,false);

			dilatedROI_2D_2 = 
				fastDilate(bleachedROI_2D, 10, cellROI_2D);
//    			erodeDilate(bleachedROI_2D, createCircularBinaryKernel(16), cellROI_2D,false);

	    	dilatedROI_2D_3 = 
	    		fastDilate(bleachedROI_2D, 18, cellROI_2D);
//    			erodeDilate(bleachedROI_2D, createCircularBinaryKernel(24), cellROI_2D,false);

	    	dilatedROI_2D_4 = 
	    		fastDilate(bleachedROI_2D, 28, cellROI_2D);
//    			erodeDilate(bleachedROI_2D, createCircularBinaryKernel(32), cellROI_2D,false);

	    	dilatedROI_2D_5 = 
	    		fastDilate(bleachedROI_2D, 40, cellROI_2D);
//    			erodeDilate(bleachedROI_2D, createCircularBinaryKernel(40), cellROI_2D,false);
			
			erodedROI_2D_0 = new UShortImage(bleachedROI_2D);
			
			// The erode always causes problems if eroding without checking the bleached length and hight.
			// we have to check the min length of the bleahed area to make sure erode within the length.
			Rectangle bleachRect = bleachedROI_2D.getNonzeroBoundingBox();
			int minLen = Math.min(bleachRect.height, bleachRect.width);
			if((minLen/2.0) < 5)
			{
				erodedROI_2D_1 = erodeDilate(bleachedROI_2D, createCircularBinaryKernel(1), bleachedROI_2D,true);
				erodedROI_2D_2 = erodeDilate(bleachedROI_2D, createCircularBinaryKernel(2), bleachedROI_2D,true);
			}
			else
			{
				erodedROI_2D_1 = erodeDilate(bleachedROI_2D, createCircularBinaryKernel(2), bleachedROI_2D,true);
				erodedROI_2D_2 = erodeDilate(bleachedROI_2D, createCircularBinaryKernel(5), bleachedROI_2D,true);
			}			
			
			UShortImage reverseErodeROI_2D_1 = new UShortImage(erodedROI_2D_1);
			reverseErodeROI_2D_1.reverse();
			erodedROI_2D_0.and(reverseErodeROI_2D_1);
			
			UShortImage reverseErodeROI_2D_2 = new UShortImage(erodedROI_2D_2);
			reverseErodeROI_2D_2.reverse();
			erodedROI_2D_1.and(reverseErodeROI_2D_2);
			
			UShortImage reverseDilateROI_2D_4 = new UShortImage(dilatedROI_2D_4);
			reverseDilateROI_2D_4.reverse();
			dilatedROI_2D_5.and(reverseDilateROI_2D_4);

			UShortImage reverseDilateROI_2D_3 = new UShortImage(dilatedROI_2D_3);
//			writeUShortFile(dilatedROI_2D_3, new File("D:\\developer\\eclipse\\workspace\\dilatedROI_2D_3.bmp"));
			reverseDilateROI_2D_3.reverse();
//			writeUShortFile(reverseDilateROI_2D_3, new File("D:\\developer\\eclipse\\workspace\\dilatedROI_2D_3_reverse.bmp"));
//			writeUShortFile(dilatedROI_2D_4, new File("D:\\developer\\eclipse\\workspace\\dilatedROI_2D_4.bmp"));
			dilatedROI_2D_4.and(reverseDilateROI_2D_3);
//			writeUShortFile(dilatedROI_2D_4, new File("D:\\developer\\eclipse\\workspace\\dilatedROI_2D_4_anded.bmp"));

			UShortImage reverseDilateROI_2D_2 = new UShortImage(dilatedROI_2D_2);
			reverseDilateROI_2D_2.reverse();
			dilatedROI_2D_3.and(reverseDilateROI_2D_2);

			UShortImage reverseDilateROI_2D_1 = new UShortImage(dilatedROI_2D_1);
			reverseDilateROI_2D_1.reverse();
			dilatedROI_2D_2.and(reverseDilateROI_2D_1);

			UShortImage reverseBleach_2D = new UShortImage(bleachedROI_2D);
			reverseBleach_2D.reverse();
			dilatedROI_2D_1.and(reverseBleach_2D);

    	}catch (ImageException e){
    		e.printStackTrace(System.out);
    	}
		ROI roiBleachedRing1_2D = getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name());
		if (roiBleachedRing1_2D==null){
			roiBleachedRing1_2D = new ROI(erodedROI_2D_2,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name());
			getFrapData().addReplaceRoi(roiBleachedRing1_2D);
		}else{
			System.arraycopy(erodedROI_2D_2.getPixels(), 0, roiBleachedRing1_2D.getRoiImages()[0].getPixels(), 0, erodedROI_2D_2.getPixels().length);
		}
		ROI roiBleachedRing2_2D = getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2.name());
		if (roiBleachedRing2_2D==null){
			roiBleachedRing2_2D = new ROI(erodedROI_2D_1,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2.name());
			getFrapData().addReplaceRoi(roiBleachedRing2_2D);
		}else{
			System.arraycopy(erodedROI_2D_1.getPixels(), 0, roiBleachedRing2_2D.getRoiImages()[0].getPixels(), 0, erodedROI_2D_1.getPixels().length);
		}
		ROI roiBleachedRing3_2D = getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3.name());
		if (roiBleachedRing3_2D==null){
			roiBleachedRing3_2D = new ROI(erodedROI_2D_0,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3.name());
			getFrapData().addReplaceRoi(roiBleachedRing3_2D);
		}else{
			System.arraycopy(erodedROI_2D_0.getPixels(), 0, roiBleachedRing3_2D.getRoiImages()[0].getPixels(), 0, erodedROI_2D_0.getPixels().length);
		}
		ROI roiBleachedRing4_2D = getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4.name());
		if (roiBleachedRing4_2D==null){
			roiBleachedRing4_2D = new ROI(dilatedROI_2D_1,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4.name());
			getFrapData().addReplaceRoi(roiBleachedRing4_2D);
		}else{
			System.arraycopy(dilatedROI_2D_1.getPixels(), 0, roiBleachedRing4_2D.getRoiImages()[0].getPixels(), 0, dilatedROI_2D_1.getPixels().length);

		}
		ROI roiBleachedRing5_2D = getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5.name());
		if (roiBleachedRing5_2D==null){
			roiBleachedRing5_2D = new ROI(dilatedROI_2D_2,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5.name());
			getFrapData().addReplaceRoi(roiBleachedRing5_2D);
		}else{
			System.arraycopy(dilatedROI_2D_2.getPixels(), 0, roiBleachedRing5_2D.getRoiImages()[0].getPixels(), 0, dilatedROI_2D_2.getPixels().length);
		}
		ROI roiBleachedRing6_2D = getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6.name());
		if (roiBleachedRing6_2D==null){
			roiBleachedRing6_2D = new ROI(dilatedROI_2D_3,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6.name());
			getFrapData().addReplaceRoi(roiBleachedRing6_2D);
		}else{
			System.arraycopy(dilatedROI_2D_3.getPixels(), 0, roiBleachedRing6_2D.getRoiImages()[0].getPixels(), 0, dilatedROI_2D_3.getPixels().length);
		}
		ROI roiBleachedRing7_2D = getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7.name());
		if (roiBleachedRing7_2D==null){
			roiBleachedRing7_2D = new ROI(dilatedROI_2D_4,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7.name());
			getFrapData().addReplaceRoi(roiBleachedRing7_2D);
		}else{
			System.arraycopy(dilatedROI_2D_4.getPixels(), 0, roiBleachedRing7_2D.getRoiImages()[0].getPixels(), 0, dilatedROI_2D_4.getPixels().length);
//			writeUShortFile(roiBleachedRing7_2D.getRoiImages()[0], new File("D:\\developer\\eclipse\\workspace\\ROI_BLEACHED_RING7.bmp"));
		}
		ROI roiBleachedRing8_2D = getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8.name());
		if (roiBleachedRing8_2D==null){
			roiBleachedRing8_2D = new ROI(dilatedROI_2D_5,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8.name());
			getFrapData().addReplaceRoi(roiBleachedRing8_2D);
		}else{
			System.arraycopy(dilatedROI_2D_5.getPixels(), 0, roiBleachedRing8_2D.getRoiImages()[0].getPixels(), 0, dilatedROI_2D_5.getPixels().length);
		}
	}
	
	public void  saveImageDatasetAsExternalData(LocalWorkspace localWorkspace,ExternalDataIdentifier newImageExtDataID,int startingIndexForRecovery) throws Exception{
			ImageDataset imageDataset = getFrapData().getImageDataset();
			if (imageDataset.getSizeC()>1){
				throw new RuntimeException("FRAPStudy.saveImageDatasetAsExternalData(): multiple channels not yet supported");
			}
			Extent extent = imageDataset.getExtent();
			ISize isize = imageDataset.getISize();
			int numImageToStore = imageDataset.getSizeT()-startingIndexForRecovery; //not include the prebleach 
	    	double[][][] pixData = new double[numImageToStore][2][]; //original fluor data and back ground average
	    	double[] timesArray = new double[numImageToStore];
	    	double[] bgAvg = getFrapData().getAvgBackGroundIntensity();
	    	
	    	for (int tIndex = startingIndexForRecovery; tIndex < imageDataset.getSizeT(); tIndex++) {
	    		short[] originalData = imageDataset.getPixelsZ(0, tIndex);// images according to zIndex at specific time points(tIndex)
	    		double[] doubleData = new double[originalData.length];
	    		double[] expandBgAvg = new double[originalData.length];
	    		for(int i = 0; i < originalData.length; i++)
	    		{
	    			doubleData[i] = 0x0000ffff & originalData[i];
	    			expandBgAvg[i] = bgAvg[tIndex];
	    		}
	    		pixData[tIndex-startingIndexForRecovery][0] = doubleData;
	    		pixData[tIndex-startingIndexForRecovery][1] = expandBgAvg;
	    		timesArray[tIndex-startingIndexForRecovery] = imageDataset.getImageTimeStamps()[tIndex]-imageDataset.getImageTimeStamps()[startingIndexForRecovery];
	    	}
	    	//changed in March 2008. Though biomodel is not created, we still let user save to xml file.
	    	Origin origin = new Origin(0,0,0);
	    	CartesianMesh cartesianMesh  = getCartesianMesh();
	    	FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
	    	fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
	    	fdos.cartesianMesh = cartesianMesh;
	    	fdos.doubleSpecData =  pixData;
	    	fdos.specEDI = newImageExtDataID;
	    	fdos.varNames = new String[] {"fluor","bg_average"};
	    	fdos.owner = LocalWorkspace.getDefaultOwner();
	    	fdos.times = timesArray;
	    	fdos.variableTypes = new VariableType[] {VariableType.VOLUME,VariableType.VOLUME};
	    	fdos.origin = origin;
	    	fdos.extent = extent;
	    	fdos.isize = isize;
			localWorkspace.getDataSetControllerImpl().fieldDataFileOperation(fdos);
	}
	
	public static File[] getCanonicalExternalDataFiles(LocalWorkspace localWorkspace,ExternalDataIdentifier originalExtDataID){
		if(originalExtDataID != null){
			File userDir = new File(localWorkspace.getDefaultSimDataDirectory());
			File fdLogFile =
				new File(userDir,
						SimulationData.createCanonicalSimLogFileName(originalExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT,false));
			File fdMeshFile =
				new File(userDir,
						SimulationData.createCanonicalMeshFileName(originalExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT,false));
			File fdFunctionFile =
				new File(userDir,
						SimulationData.createCanonicalFunctionsFileName(originalExtDataID.getKey(),FieldDataFileOperationSpec.JOBINDEX_DEFAULT,false));
			File fdZipFile =
				new File(userDir,
						SimulationData.createCanonicalSimZipFileName(originalExtDataID.getKey(), 0,FieldDataFileOperationSpec.JOBINDEX_DEFAULT,false));
			return new File[] {fdLogFile,fdMeshFile,fdFunctionFile,fdZipFile};
		}
		return null;
	}
	public static void deleteCanonicalExternalData(LocalWorkspace localWorkspace,ExternalDataIdentifier originalExtDataID) throws Exception{
		File[] externalDataFiles = getCanonicalExternalDataFiles(localWorkspace, originalExtDataID);
		for (int i = 0;externalDataFiles != null && i < externalDataFiles.length; i++) {
			externalDataFiles[i].delete();
		}
	}
	private CartesianMesh getCartesianMesh() throws Exception{
		CartesianMesh cartesianMesh = null;
		ImageDataset imgDataSet = getFrapData().getImageDataset();
		Extent extent = imgDataSet.getExtent();
		ISize isize = imgDataSet.getISize();
		Origin origin = new Origin(0,0,0);
    	if (getBioModel()==null){
    		cartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, extent,isize, 
    						new RegionImage( new VCImageUncompressed(null, new byte[isize.getXYZ()], extent, isize.getX(),isize.getY(),isize.getZ()),
    						0,null,null,RegionImage.NO_SMOOTHING));
    	}
    	else
    	{
	    	RegionImage regionImage = getBioModel().getSimulationContexts()[0].getGeometry().getGeometrySurfaceDescription().getRegionImage();
	    	if(regionImage == null){
	    		getBioModel().getSimulationContexts()[0].getGeometry().getGeometrySurfaceDescription().updateAll();
	    		regionImage = getBioModel().getSimulationContexts()[0].getGeometry().getGeometrySurfaceDescription().getRegionImage();
	    	}
	    	cartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, extent, isize, regionImage);
    	}
    	return cartesianMesh;
	}
	//this method calculates prebleach average for each pixel at different time points. back ground has been subtracted.
	//should not subtract background from it when using it as a normalized factor.
	public static double[] calculatePreBleachAverageXYZ(FRAPData frapData,int startingIndexForRecovery){
    	long[] accumPrebleachImage = new long[frapData.getImageDataset().getISize().getXYZ()];//ISize: Image size including x, y, z. getXYZ()=x*y*z
    	double[] avgPrebleachDouble = new double[accumPrebleachImage.length];
    	double[] backGround = frapData.getAvgBackGroundIntensity();
    	double accumAvgBkGround = 0; //accumulate background before starting index for recovery. used to subtract back ground.
    	// changed in June, 2008 average prebleach depends on if there is prebleach images. 
    	// Since the initial condition is normalized by prebleach avg, we have to take care the divided by zero error.
		if(startingIndexForRecovery > 0){
			if(backGround != null)//subtract background
			{
				for (int timeIndex = 0; timeIndex < startingIndexForRecovery; timeIndex++) {
					short[] pixels = frapData.getImageDataset().getPixelsZ(0, timeIndex);//channel index is 0. it is not supported yet. get image size X*Y*Z stored by time index. image store by UShortImage[Z*T]
					for (int i = 0; i < accumPrebleachImage.length; i++) {
						accumPrebleachImage[i] += 0x0000ffff&pixels[i];
					}
					accumAvgBkGround += backGround[timeIndex];
				}
				for (int i = 0; i < avgPrebleachDouble.length; i++) {
					avgPrebleachDouble[i] = ((double)accumPrebleachImage[i] - accumAvgBkGround)/(double)startingIndexForRecovery;
				}
			}
			else //don't subtract background
			{
				for (int timeIndex = 0; timeIndex < startingIndexForRecovery; timeIndex++) {
					short[] pixels = frapData.getImageDataset().getPixelsZ(0, timeIndex);//channel index is 0. it is not supported yet. get image size X*Y*Z stored by time index. image store by UShortImage[Z*T]
					for (int i = 0; i < accumPrebleachImage.length; i++) {
						accumPrebleachImage[i] += 0x0000ffff&pixels[i];
					}
				}
				for (int i = 0; i < avgPrebleachDouble.length; i++) {
					avgPrebleachDouble[i] = (double)accumPrebleachImage[i]/(double)startingIndexForRecovery;
				}
			}
		}
		else{
			//if no prebleach image, use the last recovery image intensity as prebleach average.
			System.err.println("need to determine factor for prebleach average if no pre bleach images.");
			short[] pixels = frapData.getImageDataset().getPixelsZ(0, (frapData.getImageDataset().getSizeT() - 1));
			for (int i = 0; i < pixels.length; i++) {
				avgPrebleachDouble[i] = ((double)(0x0000ffff&pixels[i]) - backGround[frapData.getImageDataset().getSizeT() - 1]);
			}
		}
		//for each pixel if it's grater than 0, we add 1 offset to it. 
		//if it is smaller or equal to 0 , we set it to 1.
		for (int i = 0; i < avgPrebleachDouble.length; i++) {
			if(avgPrebleachDouble[i] <= FRAPOptimization.epsilon){
				avgPrebleachDouble[i] = 1;
			}
			else
			{
				avgPrebleachDouble[i]=avgPrebleachDouble[i] - FRAPOptimization.epsilon +1;
			}
		}
		return avgPrebleachDouble;
	}
	
	public static double calculatePrebleachAvg_oneValue(FRAPData arg_frapData,int arg_startingIndexForRecovery)
	{
		double accum = 0;
		int counter = 0;
		double[] prebleachAvgXYZ = calculatePreBleachAverageXYZ(arg_frapData, arg_startingIndexForRecovery);
		ROI cellROI = arg_frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
		for(int i=0; i<prebleachAvgXYZ.length; i++)
		{
			if(cellROI.getPixelsXYZ()[i] != 0)
			{
				accum = accum + prebleachAvgXYZ[i];
				counter ++;
			}
		}
		return (accum/counter);
	}
	
	public void saveROIsAsExternalData(LocalWorkspace localWorkspace,ExternalDataIdentifier newROIExtDataID,int startingIndexForRecovery) throws Exception{
			ImageDataset imageDataset = getFrapData().getImageDataset();
			Extent extent = imageDataset.getExtent();
			ISize isize = imageDataset.getISize();
			int NumTimePoints = 1; 
			int NumChannels = 13;//actually it is total number of ROIs(cell,bleached + 8 rings)+prebleach+firstPostBleach+lastPostBleach
	    	double[][][] pixData = new double[NumTimePoints][NumChannels][]; // dimensions: time points, channels, whole image ordered by z slices. 
	    	

	    	double[] temp_background = getFrapData().getAvgBackGroundIntensity();
	    	double[] avgPrebleachDouble = calculatePreBleachAverageXYZ(getFrapData(),startingIndexForRecovery);
	    	
//	    	long[] accumPrebleachImage = new long[imageDataset.getISize().getXYZ()];//ISize: Image size including x, y, z. getXYZ()=x*y*z
//	    	double[] avgPrebleachDouble = new double[accumPrebleachImage.length];
//	    	// changed in June, 2008 average prebleach depends on if there is prebleach images. 
//	    	// Since the initial condition is normalized by prebleach avg, we have to take care the divided by zero error.
//			if(startingIndexForRecovery > 0){
//				for (int timeIndex = 0; timeIndex < startingIndexForRecovery; timeIndex++) {
//					short[] pixels = getFrapData().getImageDataset().getPixelsZ(0, timeIndex);//channel index is 0. it is not supported yet. get image size X*Y*Z stored by time index. image store by UShortImage[Z*T]
//					for (int i = 0; i < accumPrebleachImage.length; i++) {
//						accumPrebleachImage[i] += 0x0000ffff&pixels[i];
//					}
//				}
//				for (int i = 0; i < avgPrebleachDouble.length; i++) {
//					avgPrebleachDouble[i] = (double)accumPrebleachImage[i]/(double)startingIndexForRecovery;
//				}
//			}
//			else{
//				//if no prebleach image, use the last recovery image intensity as prebleach average.
//				System.err.println("need to determine factor for prebleach average if no pre bleach images.");
//				short[] pixels = getFrapData().getImageDataset().getPixelsZ(0, (imageDataset.getSizeT() - 1));
//				for (int i = 0; i < pixels.length; i++) {
//					avgPrebleachDouble[i] = 0x0000ffff&pixels[i];
//				}
//			}
//			
//			// since prebleach will be used to normalize image data, we have to eliminate the 0?
//			//or maybe we should leave as 0 and check later for zero when its used and deal with it.
//			for (int i = 0; i < avgPrebleachDouble.length; i++) {
//				if(avgPrebleachDouble[i] == 0){
//					avgPrebleachDouble[i] = FRAPStudy.Epsilon;
//				}
//
//			}
	    	pixData[0][0] = avgPrebleachDouble; // average of prebleach with background subtracted
	    	// first post-bleach with background subtracted
    		pixData[0][1] = createDoubleArray(getFrapData().getImageDataset().getPixelsZ(0, startingIndexForRecovery), temp_background[startingIndexForRecovery], true);
//    		adjustPrebleachAndPostbleachData(avgPrebleachDouble, pixData[0][1]);
    		// last post-bleach image (at last time point) with background subtracted
    		pixData[0][2] = createDoubleArray(getFrapData().getImageDataset().getPixelsZ(0, imageDataset.getSizeT()-1), temp_background[imageDataset.getSizeT()-1], true);
    		//below are ROIs, we don't need to subtract background for them.
    		pixData[0][3] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()).getBinaryPixelsXYZ(1), 0, false);
    		pixData[0][4] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getBinaryPixelsXYZ(1), 0, false);
    		if (getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name()) == null){
    			//throw new RuntimeException("must first generate \"derived masks\"");
    			pixData[0][5] = new double[imageDataset.getISize().getXYZ()];
	    		pixData[0][6] = new double[imageDataset.getISize().getXYZ()];
	    		pixData[0][7] = new double[imageDataset.getISize().getXYZ()];
	    		pixData[0][8] = new double[imageDataset.getISize().getXYZ()];
	    		pixData[0][9] = new double[imageDataset.getISize().getXYZ()];
	    		pixData[0][10] = new double[imageDataset.getISize().getXYZ()];
	    		pixData[0][11] = new double[imageDataset.getISize().getXYZ()];
	    		pixData[0][12] = new double[imageDataset.getISize().getXYZ()];
    		}
    		else{
    			pixData[0][5] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name()).getBinaryPixelsXYZ(1), 0, false);
	    		pixData[0][6] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2.name()).getBinaryPixelsXYZ(1), 0, false);
	    		pixData[0][7] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3.name()).getBinaryPixelsXYZ(1), 0, false);
	    		pixData[0][8] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4.name()).getBinaryPixelsXYZ(1), 0, false);
	    		pixData[0][9] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5.name()).getBinaryPixelsXYZ(1), 0, false);
	    		pixData[0][10] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6.name()).getBinaryPixelsXYZ(1), 0, false);
	    		pixData[0][11] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7.name()).getBinaryPixelsXYZ(1), 0, false);
	    		pixData[0][12] = createDoubleArray(getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8.name()).getBinaryPixelsXYZ(1), 0, false);
    		}
    		CartesianMesh cartesianMesh = getCartesianMesh();
    		Origin origin = new Origin(0,0,0);
	    		    	
	    	FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
	    	fdos.opType = FieldDataFileOperationSpec.FDOS_ADD;
	    	fdos.cartesianMesh = cartesianMesh;
	    	fdos.doubleSpecData =  pixData;
	    	fdos.specEDI = newROIExtDataID;
	    	fdos.varNames = new String[] {
	    			"prebleach_avg",
	    			"postbleach_first",
	    			"postbleach_last",
	    			"bleached_mask", 
	    			"cell_mask", 
	    			"ring1_mask",
	    			"ring2_mask",
	    			"ring3_mask",
	    			"ring4_mask",
	    			"ring5_mask",
	    			"ring6_mask",
	    			"ring7_mask",
	    			"ring8_mask"};
	    	fdos.owner = LocalWorkspace.getDefaultOwner();
	    	fdos.times = new double[] { 0.0 };
	    	fdos.variableTypes = new VariableType[] {
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME,
	    			VariableType.VOLUME};
	    	fdos.origin = origin;
	    	fdos.extent = extent;
	    	fdos.isize = isize;
	    	localWorkspace.getDataSetControllerImpl().fieldDataFileOperation(fdos);
	}
	//when creating double array for firstPostBleach and last PostBleach, etc images
	//We'll clamp all pixel value <= 0 to 0 and add offset 1 to the whole image.
	//For ROI images, we don't have to do so.
	private double[] createDoubleArray(short[] shortData, double bkGround, boolean isOffset1ProcessNeeded){
		double[] doubleData = new double[shortData.length];
		for (int i = 0; i < doubleData.length; i++) {
			doubleData[i] = ((0x0000FFFF&shortData[i]) - bkGround);
			if(isOffset1ProcessNeeded)
			{
				if(doubleData[i] <= FRAPOptimization.epsilon)
				{
					doubleData[i] = 1;
				}
				else
				{
					doubleData[i] = doubleData[i] - FRAPOptimization.epsilon + 1;
				}
			}
		}
		return doubleData;
	}
	//get external image dataset file name or ROI file name
	public File getExternalDataFile(ExternalDataInfo arg_extDataInfo)
	{
		final ExternalDataInfo extDataInfo = arg_extDataInfo;
		File extFile = new File(extDataInfo.getFilename());
		File f = new File(extFile.getParent(), extDataInfo.getExternalDataIdentifier().getOwner().getName());
		f= new  File(f,extFile.getName());
		return f;
	}
	
	public void clearBioModel(){
		setBioModel(null);
	}
	public void setBioModel(BioModel argBioModel) {
		BioModel oldValue = this.bioModel;
		this.bioModel = argBioModel;
		propertyChangeSupport.firePropertyChange("bioModel", oldValue, argBioModel);
	}
	public FRAPData getFrapData() {
		return frapData;
	}
	
	public void setFrapData(FRAPData frapData) {
		this.frapData = frapData;
	}
	
	public void gatherIssues(Vector<Issue> issueList){
		if (frapData!=null){
			frapData.gatherIssues(issueList);
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		String oldValue = this.description;
		this.description = description;
		propertyChangeSupport.firePropertyChange("description", oldValue, description);
	}

	public String getOriginalImageFilePath() {
		return originalImageFilePath;
	}

	public void setOriginalImageFilePath(String originalImageFilePath) {
		String oldValue = this.originalImageFilePath;
		this.originalImageFilePath = originalImageFilePath;
		propertyChangeSupport.firePropertyChange("originalImageFilePath", oldValue, originalImageFilePath);
	}
	public String getXmlFilename() {
		return xmlFilename;
	}

	public void setXmlFilename(String xmlFilename) {
		String oldValue = this.xmlFilename;
		this.xmlFilename = xmlFilename;
		propertyChangeSupport.firePropertyChange("xmlFilename", oldValue, xmlFilename);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldValue = this.name;
		this.name = name;
		propertyChangeSupport.firePropertyChange("name", oldValue, name);
	}

	public String getDirectory() {
		return directory;
	}

	void setDirectory(String directory) {
		String oldValue = this.directory;
		this.directory = directory;
		propertyChangeSupport.firePropertyChange("directory", oldValue, directory);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public boolean compareEqual(Matchable obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj instanceof FRAPStudy) 
		{
			FRAPStudy fStudy = (FRAPStudy)obj;
			if(!Compare.isEqualOrNull(name, fStudy.name))
			{
				return false;
			}
			if(!Compare.isEqualOrNull(description, fStudy.description))
			{
				return false;
			}
			if(!Compare.isEqualOrNull(originalImageFilePath, fStudy.originalImageFilePath))
			{
				return false;
			}
			if(!this.getFrapData().compareEqual(fStudy.getFrapData()))
			{
				return false;
			}
			if(!Compare.isEqualOrNull(getBioModel(), fStudy.getBioModel()))
			{
				return false;
			}
			if(!Compare.isEqualOrNull(getFrapDataExternalDataInfo(),fStudy.getFrapDataExternalDataInfo()))
			{
				return false;
			}
			if (!Compare.isEqualOrNull(getRoiExternalDataInfo(),fStudy.getRoiExternalDataInfo()))
			{
				return false;
			}
			if (!Compare.isEqualOrNull(getStoredRefData(),fStudy.getStoredRefData()))
			{
				return false;
			}
			return true;
		}

		return false;
	}

//	public ExternalDataInfo getRefExternalDataInfo() {
//		return refExternalDataInfo;
//	}
//
//
//	public void setRefExternalDataInfo(ExternalDataInfo refExternalDataInfo) {
//		this.refExternalDataInfo = refExternalDataInfo;
//	}
	
	public FRAPModelParameters getFrapModelParameters() {
		return frapModelParameters;
	}


	public void setFrapModelParameters(FRAPModelParameters frapModelParameters) {
		this.frapModelParameters = frapModelParameters;
	}
	
	public SimpleReferenceData getStoredRefData() {
		return storedRefData;
	}

	public void setStoredRefData(SimpleReferenceData storedRefData) {
		this.storedRefData = storedRefData;
	}
	
	public FRAPOptData getFrapOptData() {
		return frapOptData;
	}


	public void setFrapOptData(FRAPOptData frapOptData) {
		this.frapOptData = frapOptData;
	}

	public int getStartingIndexForRecovery() {
		return startingIndexForRecovery;
	}


	public void setStartingIndexForRecovery(int startingIndexForRecovery) {
		this.startingIndexForRecovery = startingIndexForRecovery;
	}
	
}
