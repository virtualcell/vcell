/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.Metadata;
import ncsa.hdf.object.h5.H5ScalarDS;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.vcell.util.CacheException;
import org.vcell.util.Coordinate;
import org.vcell.util.CoordinateIndex;
import org.vcell.util.DataAccessException;
import org.vcell.util.DataJobListenerHolder;
import org.vcell.util.Extent;
import org.vcell.util.FileUtils;
import org.vcell.util.ISize;
import org.vcell.util.NumberUtils;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.Origin;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SimResampleInfoProvider;
import org.vcell.util.document.TSJobResultsNoStats;
import org.vcell.util.document.TSJobResultsSpaceStats;
import org.vcell.util.document.TSJobResultsTimeStats;
import org.vcell.util.document.TimeSeriesJobResults;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VCDataJobID;
import org.vcell.vis.io.ChomboFiles;
import org.vcell.vis.io.MovingBoundarySimFiles;
import org.vcell.vis.io.VCellSimFiles;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;
import org.vcell.vis.mapping.chombo.ChomboVtkFileWriter;
import org.vcell.vis.mapping.movingboundary.MovingBoundaryVtkFileWriter;
import org.vcell.vis.mapping.vcell.CartesianMeshVtkFileWriter;

import cbit.image.VCImageUncompressed;
import cbit.plot.PlotData;
import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.MessageEvent;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldDataParameterVariable;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.field.db.FieldDataDBOperationDriver;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.math.GradientFunctionDefinition;
import cbit.vcell.math.InsideVariable;
import cbit.vcell.math.MathException;
import cbit.vcell.math.OutsideVariable;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VariableType.VariableDomain;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionInvocation;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.VariableSymbolTable;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.DataIndexHelper;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.TimePointHelper;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputInfoOP;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputTimeSeriesOP;
import cbit.vcell.simdata.DataOperationResults.DataProcessingOutputDataValues;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.AnnotatedFunction.FunctionCategory;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationDataIdentifierOldStyle;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.FVSolverStandalone;
import cbit.vcell.solvers.MembraneElement;
import cbit.vcell.solvers.mb.MovingBoundaryReader;
import cbit.vcell.util.AmplistorUtils;
import cbit.vcell.util.EventRateLimiter;
import cbit.vcell.xml.XmlParseException;
/**
 * This interface was generated by a SmartGuide.
 * 
 */

public class DataSetControllerImpl implements SimDataConstants,DataJobListenerHolder {
	
	private boolean bAllowOptimizedTimeDataRetrieval = true;
	
	private static final int TXYZ_OFFSET = 4;
	
	public interface ProgressListener{
		public void updateProgress(double progress);
		public void updateMessage(String message);
	};
	
	private static class TimeInfo {
		private boolean[] wantsTheseTimes;
		private double[] desiredTimeValues;
		public TimeInfo(VCDataIdentifier vcdID,double startTime,double step,double endTime,double allDataTimes[]) throws DataAccessException{
			if (allDataTimes.length<=0){
				throw new DataAccessException("No times found for "+vcdID.toString());
			}	

			wantsTheseTimes = new boolean[allDataTimes.length];
			desiredTimeValues = null;
			int desiredNumTimes = 0;
			
			Arrays.fill(wantsTheseTimes,false);
			double[] tempTimes = new double[allDataTimes.length];
			
			int stepCounter = 0;
			for(int i=0;i<allDataTimes.length;i+= 1){
				if(allDataTimes[i] > endTime){
					break;
				}
				if(allDataTimes[i] == startTime){
					tempTimes[desiredNumTimes] = allDataTimes[i];
					desiredNumTimes+= 1;
					stepCounter = 0;
					wantsTheseTimes[i] = true;
					if(step == 0){
						break;
					}
				}else if(desiredNumTimes > 0 && stepCounter%step == 0){
					tempTimes[desiredNumTimes] = allDataTimes[i];
					desiredNumTimes+= 1;
					wantsTheseTimes[i] = true;
				}
				stepCounter+= 1;
			}
			if(desiredNumTimes == 0){
				throw new IllegalArgumentException("Couldn't find startTime "+startTime);
			}
			desiredTimeValues = new double[desiredNumTimes];
			System.arraycopy(tempTimes,0,desiredTimeValues,0,desiredNumTimes);
			
		}
		public boolean[] getWantsTheseTimes() {
			return wantsTheseTimes;
		}
		public double[] getDesiredTimeValues() {
			return desiredTimeValues;
		}
		
	};
	
	private static class VolumeIndexNearFar {
		public int volIndexNear;
		public int volIndexFar;
		public VolumeIndexNearFar(int indexNear,int indexFar){
			this.volIndexNear = indexNear;
			this.volIndexFar = indexFar;
		}
		public static double interpolate(double valNear,double valFar){
			return 1.5 * valNear-0.5 * valFar;

		}
	};
	
	private boolean dataCachingEnabled = true;
	
	private HashMap<User, Vector<ExternalDataIdentifier>>
	userExtDataIDH =
		new HashMap<User, Vector<ExternalDataIdentifier>>();
	
	private static final int NUM_STATS = 6;//min,max,mean,wmean
	private static final int MIN_OFFSET = 0;
	private static final int MAX_OFFSET = 1;
	private static final int MEAN_OFFSET = 2;
	private static final int WMEAN_OFFSET = 3;
	private static final int SUM_OFFSET = 4;
	private static final int WSUM_OFFSET = 5;
	//
	private SessionLog log = null;
	private File primaryRootDirectory =  null;
	private File secondaryRootDirectory =  null;
	private Cachetable cacheTable0 = null;
	private Cachetable chomboExtrapolatedValuesCache = null;
	private Vector<DataJobListener> aDataJobListener = null;
	//
	public static class SpatialStatsInfo {
		public boolean bWeightsValid;
		public double[][] spaceWeight;
		public double[] totalSpace;
	};
	//
	private class MultiFunctionIndexes{
		private FunctionIndexes[] functionIndexesArr;
		private String[] allFuncVarNames = new String[0];
		private int[][] allFuncIndexes = new int[0][];
		private int blockSize;
		private double[][][] valuesOverTime = null;
//		private double[][] valuesOverLine = null;
		private VCData simData = null;
		private double[] dataSetTimes = null;
		private boolean[] wantsTheseTimes = null;
		private DataSetControllerImpl.ProgressListener progressListener;
		
		public MultiFunctionIndexes(
				final VCDataIdentifier argVcdID, 
				AnnotatedFunction argFunc,
				int[] argIndices,
				boolean[] argWantsTheseTimes,
				DataSetControllerImpl.ProgressListener argProgressListener,
				OutputContext outputContext)
									throws IOException,DataAccessException,ExpressionException,MathException{
										
			wantsTheseTimes = argWantsTheseTimes;
			dataSetTimes = DataSetControllerImpl.this.getDataSetTimes(argVcdID);
			simData = getVCData(argVcdID);
			functionIndexesArr = findFunctionIndexes(argVcdID,argFunc,argIndices,outputContext);
			if(!functionIndexesArr[0].hasNearFarInterpolation()){
				blockSize = functionIndexesArr[0].getIndexes().length;
				for(int i=0;i<functionIndexesArr.length;i+= 1){
					FunctionIndexes fi = functionIndexesArr[i];
					String[] tempVN = new String[allFuncVarNames.length + fi.getSimFileVarNames().length];
					System.arraycopy(allFuncVarNames,0,tempVN,0,allFuncVarNames.length);
					System.arraycopy(fi.getSimFileVarNames(),0,tempVN,allFuncVarNames.length,fi.getSimFileVarNames().length);
					int[][] tempInd = new int[allFuncIndexes.length + fi.getIndexes().length][];
					for(int j=0;j<tempInd.length;j+= 1){
						if(j < allFuncIndexes.length){
							tempInd[j] = allFuncIndexes[j];
						}else{
							tempInd[j] = new int[] {fi.getIndexes()[j-allFuncIndexes.length]};
						}
					}
					allFuncIndexes = tempInd;
					allFuncVarNames = tempVN;
				}
			}else{
				blockSize = functionIndexesArr[0].getIndexes().length*2;
				int varIndex = -1;
				for(int i=0;i<functionIndexesArr.length;i+= 1){
					FunctionIndexes fi = functionIndexesArr[i];
					String[] tempVN = new String[allFuncVarNames.length + fi.getSimFileVarNames().length*2];
					System.arraycopy(allFuncVarNames,0,tempVN,0,allFuncVarNames.length);
					System.arraycopy(fi.getSimFileVarNames(),0,tempVN,allFuncVarNames.length,fi.getSimFileVarNames().length);
					System.arraycopy(fi.getSimFileVarNames(),0,tempVN,allFuncVarNames.length+fi.getSimFileVarNames().length,fi.getSimFileVarNames().length);
					int[][] tempInd = new int[allFuncIndexes.length + fi.getIndexes().length*2][];
					for(int j=0;j<tempInd.length;j+= 1){
						if(j < allFuncIndexes.length){
							tempInd[j] = allFuncIndexes[j];
						}else if(j < allFuncIndexes.length + fi.getIndexes().length){
							tempInd[j] = new int[] {fi.getIndexes()[j-allFuncIndexes.length]};
						}else{
							varIndex = j-allFuncIndexes.length-fi.getIndexes().length;
							tempInd[j] = new int[] {fi.getIndexes()[varIndex]};
							tempInd[j][0] = fi.findFarIndex(varIndex, tempInd[j][0]);
						}
					}
					allFuncIndexes = tempInd;
					allFuncVarNames = tempVN;
				}				
			}
			
			progressListener = argProgressListener;
		}
		public double evaluateTimeFunction(OutputContext outputContext,int timeIndex,int dataIndex)
								throws ExpressionException,DataAccessException,IOException{
			if(valuesOverTime == null){
				valuesOverTime =
					simData.getSimDataTimeSeries(outputContext,this.geExpandedSimFileVarNames(),this.getExpandedFunctionIndexes(),wantsTheseTimes,progressListener);
			}
			double[] argsD = getArgBlock(valuesOverTime[timeIndex],dataIndex);
			return functionIndexesArr[dataIndex].evaluateFunction(dataSetTimes[timeIndex],argsD);
		}
//		public double evaluateLineFunction(double time,int dataIndex)
//								throws ExpressionException,DataAccessException,IOException{
//			if(valuesOverLine == null){
//				valuesOverLine =
//					simData.getSimDataLineScan(this.geExpandedSimFileVarNames(),this.getExpandedFunctionIndexes(),time);
//			}
//			double[] argsD = getArgBlock(valuesOverLine,dataIndex);
//			return functionIndexesArr[dataIndex].evaluateFunction(time,argsD);
//		}
		private String[] geExpandedSimFileVarNames(){
			return allFuncVarNames;
		}
		private int[][] getExpandedFunctionIndexes(){
			return allFuncIndexes;
		}
		private double[] getArgBlock(double[][] sourceValues,int index){
			double[] argsD = new double[blockSize];
			for(int k=0;k<blockSize;k+= 1){
				argsD[k] = sourceValues[(blockSize*index)+k][0];
			}
			return argsD;
		}
	};
	//
	private class FunctionIndexes{
		private AnnotatedFunction function;
		private Coordinate xyz;
		private String[] funcVarNames;
		private String[] simFileVarNames;
		private int[] funcIndexes;
		private double[] functionArgs;
		private VolumeIndexNearFar[] inside_near_far_indexes;
		private VolumeIndexNearFar[] outside_near_far_indexes;

		public int findFarIndex(int varIndex,int origVal){
			if(inside_near_far_indexes[varIndex] != null && inside_near_far_indexes[varIndex].volIndexFar != -1){
				return inside_near_far_indexes[varIndex].volIndexFar;
			}else if(outside_near_far_indexes[varIndex] != null && outside_near_far_indexes[varIndex].volIndexFar != -1){
				return outside_near_far_indexes[varIndex].volIndexFar;			
			}
			return origVal;
		}
		public boolean hasNearFarInterpolation(){
			if(inside_near_far_indexes != null){
				for (int i = 0; i < inside_near_far_indexes.length; i++) {
					if(inside_near_far_indexes[i] != null){
						return true;
					}
				}
			}
			if(outside_near_far_indexes != null){
				for (int i = 0; i < outside_near_far_indexes.length; i++) {
					if(outside_near_far_indexes[i] != null){
						return true;
					}
				}
			}
			return false;
		}
		public String[] getSimFileVarNames(){
			return simFileVarNames;
		}
		public int[] getIndexes(){
			return funcIndexes;
		}
		public double evaluateFunction(double time,double[] argValues) throws ExpressionException{
			functionArgs[0] = time; // time
			functionArgs[1] = xyz.getX(); // x
			functionArgs[2] = xyz.getY(); // y
			functionArgs[3] = xyz.getZ(); // z
			for(int i=0;i<funcVarNames.length;i+= 1){
				functionArgs[i+TXYZ_OFFSET] = argValues[i];
				if(inside_near_far_indexes != null && inside_near_far_indexes[i] != null && inside_near_far_indexes[i].volIndexFar != -1){
					functionArgs[i+TXYZ_OFFSET] = VolumeIndexNearFar.interpolate(argValues[i], argValues[funcVarNames.length+i]);
				}else if(outside_near_far_indexes != null && outside_near_far_indexes[i] != null && outside_near_far_indexes[i].volIndexFar != -1){
					functionArgs[i+TXYZ_OFFSET] = VolumeIndexNearFar.interpolate(argValues[i], argValues[funcVarNames.length+i]);			
				}
			}
//			if(time ==0){
//				System.out.print("multi-func evalFunction ");
//				for (int i = 0; i < functionArgs.length; i++) {
//					System.out.print(functionArgs[i]);
//				}
//				System.out.println(" "+(functionArgs[functionArgs.length-2]/functionArgs[functionArgs.length-1]));
//			}
			return function.getExpression().evaluateVector(functionArgs);
		}
		public FunctionIndexes(AnnotatedFunction argAF,Coordinate argXYZ,
				String[] argVarNames,String[] argSimFileVarNames,int[] argIndexes,
				VolumeIndexNearFar[] arg_inside_near_far_indexes,
				VolumeIndexNearFar[] arg_outside_near_far_indexes){
			for(int i=0;i<argVarNames.length;i+= 1){
				if(
					(arg_inside_near_far_indexes != null && arg_inside_near_far_indexes[i] != null && arg_inside_near_far_indexes[i].volIndexNear != argIndexes[i])
					||
					(arg_outside_near_far_indexes != null && arg_outside_near_far_indexes[i] != null && arg_outside_near_far_indexes[i].volIndexNear != argIndexes[i]))
				{
					throw new RuntimeException("FunctionIndexes: 'near' indexes should always match argIndexes when they exist");
				}
			}
			function = argAF;
			xyz = argXYZ;
			funcVarNames = argVarNames;
			funcIndexes = argIndexes;
			simFileVarNames = argSimFileVarNames;
			functionArgs = new double[argSimFileVarNames.length+TXYZ_OFFSET];
			inside_near_far_indexes = arg_inside_near_far_indexes;
			outside_near_far_indexes = arg_outside_near_far_indexes;
		}
	};

/**
 * This method was created by a SmartGuide.
 */
	public DataSetControllerImpl (SessionLog sessionLog, Cachetable aCacheTable, File primaryDir, File secondDir) throws FileNotFoundException {
		this.cacheTable0 = aCacheTable;
		this.primaryRootDirectory = primaryDir;
		this.secondaryRootDirectory = secondDir;
		this.log = sessionLog;
		

	}


/**
 * Add a cbit.vcell.desktop.controls.ExportListener.
 */
public void addDataJobListener(DataJobListener newListener) {
	if (aDataJobListener == null) {
		aDataJobListener = new java.util.Vector<DataJobListener>();
	};
	aDataJobListener.addElement(newListener);
}


/**
 * Insert the method's description here.
 * Creation date: (3/20/2006 3:37:39 PM)
 */
private SpatialStatsInfo calcSpatialStatsInfo(OutputContext outputContext,TimeSeriesJobSpec timeSeriesJobSpec,VCDataIdentifier vcdID) throws Exception{

	if(getVCData(vcdID) instanceof SimulationData && ((SimulationData)getVCData(vcdID)).isPostProcessing(outputContext, timeSeriesJobSpec.getVariableNames()[0])){
		return new SpatialStatsInfo();
	}
	SpatialStatsInfo ssi = new SpatialStatsInfo();
	//Determine weights for indices of each variable if we are going to be calculating spatial statistics
	ssi.bWeightsValid = true;
    //if(timeSeriesJobSpec.isCalcSpaceStats()){
	    CartesianMesh myMesh = getMesh(vcdID);
	    DataIdentifier[] dataIDs = getDataIdentifiers(outputContext,vcdID);
	    ssi.spaceWeight = new double[timeSeriesJobSpec.getVariableNames().length][];
	    ssi.totalSpace = new double[timeSeriesJobSpec.getVariableNames().length];
	    for(int i=0;i<timeSeriesJobSpec.getVariableNames().length;i+= 1){
		    ssi.spaceWeight[i] = new double[timeSeriesJobSpec.getIndices()[i].length];
		    Boolean isVolume = null;
		    for(int j=0;j<dataIDs.length;j+= 1){
			    if(dataIDs[j].getName().equals(timeSeriesJobSpec.getVariableNames()[i])){
				    isVolume = new Boolean(dataIDs[j].getVariableType().equals(VariableType.VOLUME) || dataIDs[j].getVariableType().equals(VariableType.VOLUME_REGION));
				    break;
			    }
		    }
		    if(isVolume == null){
			    throw new RuntimeException("Couldn't find variable type for varname="+timeSeriesJobSpec.getVariableNames()[i]+" during TimeSeries calc spatial stats");
		    }else{
			    for(int j=0;j<timeSeriesJobSpec.getIndices()[i].length;j+= 1){
				    if(isVolume.booleanValue()){
					    ssi.spaceWeight[i][j] = myMesh.calculateMeshElementVolumeFromVolumeIndex(timeSeriesJobSpec.getIndices()[i][j]);
				    }else{//assume membrane
					    double area = myMesh.getMembraneElements()[timeSeriesJobSpec.getIndices()[i][j]].getArea();
					    if(area == MembraneElement.AREA_UNDEFINED){
					    	ssi.bWeightsValid = false;
					    	break;
					    }
					    ssi.spaceWeight[i][j] = area;
				    }
				    ssi.totalSpace[i]+= ssi.spaceWeight[i][j];
			    }
		    }
		    if(!ssi.bWeightsValid){
			    break;
		    }
	    }
    //}

    //if(ssi.bWeightsValid){
	    return ssi;
    //}else{
	    //return null;
    //}
}


/**
 * Insert the method's description here.
 * Creation date: (2/16/2006 12:28:29 PM)
 */
private static TimeSeriesJobResults calculateStatisticsFromWhole(
	TimeSeriesJobSpec timeSeriesJobSpec,
    double[][][] timeSeriesFormatedValuesArr,
    double[] desiredTimeValues,
    SpatialStatsInfo spatialStatsInfo) throws Exception{

    if(timeSeriesJobSpec.isCalcTimeStats()){
	    double[][] timeMinArr = null;
	    double[][] timeMaxArr = null;
	    double[][] timeMeanArr = null;
	    timeMinArr = new double[timeSeriesJobSpec.getVariableNames().length][];
	    timeMaxArr = new double[timeSeriesJobSpec.getVariableNames().length][];
	    timeMeanArr = new double[timeSeriesJobSpec.getVariableNames().length][];
	    double val=0;
	    for(int i=0;i<timeSeriesFormatedValuesArr.length;i+= 1){
		    timeMinArr[i] = new double[timeSeriesJobSpec.getIndices()[i].length];
		    timeMaxArr[i] = new double[timeSeriesJobSpec.getIndices()[i].length];
		    timeMeanArr[i] = new double[timeSeriesJobSpec.getIndices()[i].length];
		    for(int j=1;j<timeSeriesFormatedValuesArr[i].length;j+= 1){//skip index 0 (has times)
			    double min = Double.POSITIVE_INFINITY;
			    double max = Double.NEGATIVE_INFINITY;
			    double mean = 0;
			    for(int k=0;k<desiredTimeValues.length;k+= 1){
				    val = timeSeriesFormatedValuesArr[i][j][k];
				    if(val < min){min=val;}
				    if(val > max){max=val;}
				    mean+= val;
			    }
			    mean/= desiredTimeValues.length;
			    timeMinArr[i][j-1] = min;
			    timeMaxArr[i][j-1] = max;
			    timeMeanArr[i][j-1] = mean;
		    }
	    }
	    if(!timeSeriesJobSpec.isCalcSpaceStats()){//No space stats
	        return new TSJobResultsTimeStats(
	            timeSeriesJobSpec.getVariableNames(),
	            timeSeriesJobSpec.getIndices(),
	            desiredTimeValues,
	            timeMinArr,timeMaxArr,timeMeanArr);
	    }else {
		    double[] timeSpaceStatsMin = new double[timeSeriesFormatedValuesArr.length];
		    double[] timeSpaceStatsMax = new double[timeSeriesFormatedValuesArr.length];
		    double[] timeSpaceStatsUnweightedMean = new double[timeSeriesFormatedValuesArr.length];
		    double[] timeSpaceStatsWeightedMean = new double[timeSeriesFormatedValuesArr.length];
		    for(int i=0;i<timeSeriesFormatedValuesArr.length;i+= 1){
			    double min = Double.POSITIVE_INFINITY;
			    double max = Double.NEGATIVE_INFINITY;
			    double mean = 0;
			    double wmean = 0;
			    for(int j=0;j<timeSeriesJobSpec.getIndices()[i].length;j+= 1){
				    if(timeMinArr[i][j] < min){min=timeMinArr[i][j];}
				    if(timeMaxArr[i][j] > max){max=timeMaxArr[i][j];}
				    mean+= timeMeanArr[i][j];
				    if(spatialStatsInfo.bWeightsValid){wmean+= timeMeanArr[i][j]*spatialStatsInfo.spaceWeight[i][j];}
			    }
			    mean/= timeSeriesFormatedValuesArr[i].length;
			    if(spatialStatsInfo.bWeightsValid){wmean/= spatialStatsInfo.totalSpace[i];}
			    
			    timeSpaceStatsMin[i] = min;
			    timeSpaceStatsMax[i] = max;
			    timeSpaceStatsUnweightedMean[i] = mean;
			    timeSpaceStatsWeightedMean[i] = wmean;
		    }
	        return new TSJobResultsTimeStats(
	            timeSeriesJobSpec.getVariableNames(),
	            timeSeriesJobSpec.getIndices(),
	            desiredTimeValues,
	            timeSpaceStatsMin,timeSpaceStatsMax,timeSpaceStatsUnweightedMean,(spatialStatsInfo.bWeightsValid?timeSpaceStatsWeightedMean:null));
	    }
    }else if(timeSeriesJobSpec.isCalcSpaceStats()){
	    
	    double val=0;
		
	    double[][] spaceStatsMin = new double[timeSeriesJobSpec.getVariableNames().length][desiredTimeValues.length];
	    double[][] spaceStatsMax = new double[timeSeriesJobSpec.getVariableNames().length][desiredTimeValues.length];
	    double[][] spaceStatsUnweightedMean = new double[timeSeriesJobSpec.getVariableNames().length][desiredTimeValues.length];
	    double[][] spaceStatsWeightedMean = new double[timeSeriesJobSpec.getVariableNames().length][desiredTimeValues.length];
	    double[][] spaceStatsUnweightedSum = new double[timeSeriesJobSpec.getVariableNames().length][desiredTimeValues.length];
	    double[][] spaceStatsWeightedSum = new double[timeSeriesJobSpec.getVariableNames().length][desiredTimeValues.length];
	    for(int k=0;k<desiredTimeValues.length;k+= 1){//times
		    for(int i=0;i<timeSeriesFormatedValuesArr.length;i+= 1){//Variable names
			    double min = Double.POSITIVE_INFINITY;
			    double max = Double.NEGATIVE_INFINITY;
			    double mean = 0;
			    double wmean = 0;
			    double sum = 0;
			    double wsum = 0;
			    for(int j=1;j<timeSeriesFormatedValuesArr[i].length;j+= 1){//index
				    val = timeSeriesFormatedValuesArr[i][j][k];
//			    	if(k == 0 ){
//System.out.println("varIndex="+i+" pixelIndex="+(j-1)+" pixelIndexVal="+timeSeriesJobSpec.getIndices()[i][j-1]+" timeIndex="+k+" val="+val);
//			    	}
				    if(val < min){min=val;}
				    if(val > max){max=val;}
				    sum+= val;
				    if(spatialStatsInfo.bWeightsValid){wsum+= val*spatialStatsInfo.spaceWeight[i][j-1];}
			    }
			    mean = sum/timeSeriesJobSpec.getIndices()[i].length;
			    if(spatialStatsInfo.bWeightsValid){wmean = wsum/spatialStatsInfo.totalSpace[i];}

			    spaceStatsMin[i][k] = min;
			    spaceStatsMax[i][k] = max;
			    spaceStatsUnweightedMean[i][k] = mean;
			    spaceStatsWeightedMean[i][k] = wmean;
			    spaceStatsUnweightedSum[i][k] = sum;
			    spaceStatsWeightedSum[i][k] = wsum;
		    }
	    }
        return new TSJobResultsSpaceStats(
            timeSeriesJobSpec.getVariableNames(),
            timeSeriesJobSpec.getIndices(),
            desiredTimeValues,
            spaceStatsMin,spaceStatsMax,
            spaceStatsUnweightedMean,
            (spatialStatsInfo.bWeightsValid?spaceStatsWeightedMean:null),
            spaceStatsUnweightedSum,
            (spatialStatsInfo.bWeightsValid?spaceStatsWeightedSum:null),
            (spatialStatsInfo.bWeightsValid?spatialStatsInfo.totalSpace:null));
    }

    throw new IllegalArgumentException("Couldn't determine format of data to return");
}

public DataOperationResults doDataOperation(DataOperation dataOperation) throws DataAccessException{
	VCDataJobID vcDataJobID = null;
	try{
		if(dataOperation instanceof DataOperation.DataProcessingOutputTimeSeriesOP){
			vcDataJobID = ((DataOperation.DataProcessingOutputTimeSeriesOP)dataOperation).getTimeSeriesJobSpec().getVcDataJobID();
		}
		if(!(getVCData(dataOperation.getVCDataIdentifier()) instanceof SimulationData)){
			return null;
		}
		File dataProcessingOutputFileHDF5 = ((SimulationData)getVCData(dataOperation.getVCDataIdentifier())).getDataProcessingOutputSourceFileHDF5();
		DataOperationResults dataOperationResults = getDataProcessingOutput(dataOperation,dataProcessingOutputFileHDF5);
		if(vcDataJobID != null){
			fireDataJobEventIfNecessary(vcDataJobID,MessageEvent.DATA_COMPLETE, dataOperation.getVCDataIdentifier(), new Double(0), ((DataOperationResults.DataProcessingOutputTimeSeriesValues)dataOperationResults).getTimeSeriesJobResults(),null);
		}
		return dataOperationResults;
	}catch(Exception e){
		if(vcDataJobID != null){
			fireDataJobEventIfNecessary(vcDataJobID,MessageEvent.DATA_FAILURE, dataOperation.getVCDataIdentifier(), new Double(0), null,e);	
		}
		if(e instanceof DataAccessException){
			throw (DataAccessException)e;
		}else{
			throw new DataAccessException("Datasetcontrollerimpl.doDataOperation error: "+e.getMessage(),e);
		}
	}
}
private static class DataProcessingHelper{
	public String[] specificVarNames;
	public TimePointHelper specificTimePointHelper;
	public DataIndexHelper specificDataIndexHelper;
	public double[][][] specificDataValues;//[var][time][data]
	
	public String[] statVarNames;
	public String[] statVarUnits;
	public double[][] statValues;
	public double[] times;
	public ArrayList<String> imageNames;
	public ArrayList<Origin> imageOrigin;
	public ArrayList<Extent> imageExtent;
	public ArrayList<ISize>  imageISize;
	public Object tempData;
	public long[] tempDims;
	public DataProcessingHelper(){
	}
	public DataProcessingHelper(String[] specificVarNames,TimePointHelper specificTimePointHelper,DataIndexHelper specificDataIndexHelper){
		this.specificVarNames = specificVarNames;
		this.specificTimePointHelper = specificTimePointHelper;
		this.specificDataIndexHelper = specificDataIndexHelper;
		this.specificDataValues = new double[specificVarNames.length][][];
	}
	public boolean isInfoOnly(){
		return specificVarNames == null;
	}
	public int getNumImageVars(){
		return (imageNames==null?0:imageNames.size());
	}
	public String[] getVarNames(){
		String[] arr = new String[statVarNames.length+getNumImageVars()];
		for (int i = 0; i < arr.length; i++) {
			if(i < statVarNames.length){
				arr[i] = statVarNames[i];
			}else{
				arr[i] = imageNames.get(i-statVarNames.length);
			}
		}
		return arr;
	}
	public ISize[] getVarISizes(){
		if(getNumImageVars() == 0){
			return null;
		}
		ISize[] arr = new ISize[statVarNames.length+getNumImageVars()];
		for (int i = 0; i < arr.length; i++) {
			if(i < statVarNames.length){
				arr[i] = null;
			}else{
				arr[i] = imageISize.get(i-statVarNames.length);
			}
		}
		return arr;
	}
	public Origin[] getVarOrigins(){
		if(getNumImageVars() == 0){
			return null;
		}
		Origin[] arr = new Origin[statVarNames.length+getNumImageVars()];
		for (int i = 0; i < arr.length; i++) {
			if(i < statVarNames.length){
				arr[i] = null;
			}else{
				arr[i] = imageOrigin.get(i-statVarNames.length);
			}
		}
		return arr;
	}
	public Extent[] getVarExtents(){
		if(getNumImageVars() == 0){
			return null;
		}
		Extent[] arr = new Extent[statVarNames.length+getNumImageVars()];
		for (int i = 0; i < arr.length; i++) {
			if(i < statVarNames.length){
				arr[i] = null;
			}else{
				arr[i] = imageExtent.get(i-statVarNames.length);
			}
		}
		return arr;
	}
	public String[] getVarUnits(){
		String[] arr = new String[statVarNames.length+getNumImageVars()];
		for (int i = 0; i < arr.length; i++) {
			if(i < statVarNames.length){
				arr[i] = statVarUnits[i];
			}else{
				arr[i] = null;
			}
		}
		return arr;
	}
	public DataOperationResults.DataProcessingOutputInfo.PostProcessDataType[] getPostProcessDataTypes(){
		DataOperationResults.DataProcessingOutputInfo.PostProcessDataType[] arr = new DataOperationResults.DataProcessingOutputInfo.PostProcessDataType[statVarNames.length+getNumImageVars()];
		for (int i = 0; i < arr.length; i++) {
			if(i < statVarNames.length){
				arr[i] = DataOperationResults.DataProcessingOutputInfo.PostProcessDataType.statistic;
			}else{
				arr[i] = DataOperationResults.DataProcessingOutputInfo.PostProcessDataType.image;
			}
		}
		return arr;
	}
	public HashMap<String, double[]> getVarStatValues(){
		HashMap<String, double[]> varStatValues = new HashMap<String, double[]>();
		for (int i = 0; i < statVarNames.length; i++) {
			varStatValues.put(statVarNames[i],statValues[i]);
		}
		return varStatValues;
	}
}

public static DataOperationResults getDataProcessingOutput(DataOperation dataOperation,File dataProcessingOutputFileHDF5) throws Exception {

	DataOperationResults dataProcessingOutputResults = null;
	FileFormat hdf5FileFormat = null;
	try{
		if (dataProcessingOutputFileHDF5.exists()) {
			// retrieve an instance of H5File
			FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
			if (fileFormat == null){
				throw new Exception("Cannot find HDF5 FileFormat.");
			}
			// open the file with read-only access	
			hdf5FileFormat = fileFormat.open(dataProcessingOutputFileHDF5.getAbsolutePath(), FileFormat.READ);
			hdf5FileFormat.setMaxMembers(Simulation.MAX_LIMIT_SPATIAL_TIMEPOINTS);
			// open the file and retrieve the file structure
			hdf5FileFormat.open();
			Group root = (Group)((javax.swing.tree.DefaultMutableTreeNode)hdf5FileFormat.getRootNode()).getUserObject();
			if(dataOperation instanceof DataProcessingOutputInfoOP){
				DataProcessingHelper dataProcessingHelper = new DataProcessingHelper();
				iterateHDF5(root,"",dataProcessingHelper);
				dataProcessingOutputResults = new DataOperationResults.DataProcessingOutputInfo(dataOperation.getVCDataIdentifier(),
						dataProcessingHelper.getVarNames(),
						dataProcessingHelper.getVarISizes(),
						dataProcessingHelper.times,
						dataProcessingHelper.getVarUnits(),
						dataProcessingHelper.getPostProcessDataTypes(),
						dataProcessingHelper.getVarOrigins(),
						dataProcessingHelper.getVarExtents(),
						dataProcessingHelper.getVarStatValues());
				//map function names to PostProcess state variable name
				ArrayList<String> postProcessImageVarNames = new ArrayList<String>();
				for (int i = 0; i < ((DataOperationResults.DataProcessingOutputInfo)dataProcessingOutputResults).getVariableNames().length; i++) {
					String variableName = ((DataOperationResults.DataProcessingOutputInfo)dataProcessingOutputResults).getVariableNames()[i];
					if(((DataOperationResults.DataProcessingOutputInfo)dataProcessingOutputResults).getPostProcessDataType(variableName).equals(DataOperationResults.DataProcessingOutputInfo.PostProcessDataType.image)){
						postProcessImageVarNames.add(variableName);
					}
				}
				HashMap<String, String> mapFunctionNameToStateVarName = null;
				if(((DataProcessingOutputInfoOP)dataOperation).getOutputContext() != null){
					mapFunctionNameToStateVarName = new HashMap<String, String>();
					for (int i = 0; i < ((DataProcessingOutputInfoOP)dataOperation).getOutputContext().getOutputFunctions().length; i++) {
						AnnotatedFunction annotatedFunction = ((DataProcessingOutputInfoOP)dataOperation).getOutputContext().getOutputFunctions()[i];
						if(annotatedFunction.getFunctionType().equals(VariableType.POSTPROCESSING)){
							String[] symbols = annotatedFunction.getExpression().flatten().getSymbols();
							//Find any PostProcess state var that matches a symbol in the function
							for (int j = 0; j < symbols.length; j++) {
								if(postProcessImageVarNames.contains(symbols[j])){
									mapFunctionNameToStateVarName.put(annotatedFunction.getName(), symbols[j]);
									break;
								}
							}
						}
					}
				}
				if(mapFunctionNameToStateVarName != null && mapFunctionNameToStateVarName.size() > 0){
					dataProcessingOutputResults = new DataOperationResults.DataProcessingOutputInfo(((DataOperationResults.DataProcessingOutputInfo)dataProcessingOutputResults),mapFunctionNameToStateVarName);
				}
			}else{
				OutputContext outputContext = dataOperation.getOutputContext();
				String[] variableNames = null;
				DataIndexHelper dataIndexHelper = null;
				TimePointHelper timePointHelper = null;
				if(dataOperation instanceof DataOperation.DataProcessingOutputDataValuesOP){
					variableNames = new String[] {((DataOperation.DataProcessingOutputDataValuesOP)dataOperation).getVariableName()};
					dataIndexHelper = ((DataOperation.DataProcessingOutputDataValuesOP)dataOperation).getDataIndexHelper();
					timePointHelper = ((DataOperation.DataProcessingOutputDataValuesOP)dataOperation).getTimePointHelper();
				}else if(dataOperation instanceof DataOperation.DataProcessingOutputTimeSeriesOP){
					variableNames = ((DataOperation.DataProcessingOutputTimeSeriesOP)dataOperation).getTimeSeriesJobSpec().getVariableNames();
					TimeSeriesJobSpec timeSeriesJobSpec = ((DataOperation.DataProcessingOutputTimeSeriesOP)dataOperation).getTimeSeriesJobSpec();
					double[] specificTimepoints = extractTimeRange(((DataOperation.DataProcessingOutputTimeSeriesOP)dataOperation).getAllDatasetTimes(), timeSeriesJobSpec.getStartTime(), timeSeriesJobSpec.getEndTime());
					timePointHelper = TimePointHelper.createSpecificTimePointHelper(specificTimepoints);
					timeSeriesJobSpec.initIndices();
					dataIndexHelper = DataIndexHelper.createSpecificDataIndexHelper(timeSeriesJobSpec.getIndices()[0]);
				}else{
					throw new Exception("Unknown Dataoperation "+dataOperation.getClass().getName());
				}
				if(variableNames.length != 1){
					throw new Exception("Only 1 variable request at a time");
				}
				AnnotatedFunction[] annotatedFunctions = (outputContext==null?null:outputContext.getOutputFunctions());
				AnnotatedFunction foundFunction = null;
				if(annotatedFunctions != null){
					for (int i = 0; i < annotatedFunctions.length; i++) {
						if(annotatedFunctions[i].getName().equals(variableNames[0])){
							foundFunction = annotatedFunctions[i];
							break;
						}
					}
				}
				double[] alltimes = null;
				if(foundFunction != null){
					DataOperationResults.DataProcessingOutputInfo dataProcessingOutputInfo =
						(DataOperationResults.DataProcessingOutputInfo)getDataProcessingOutput(new DataOperation.DataProcessingOutputInfoOP(dataOperation.getVCDataIdentifier(),false,dataOperation.getOutputContext()), dataProcessingOutputFileHDF5);
					alltimes = dataProcessingOutputInfo.getVariableTimePoints();
					FunctionHelper functionHelper = getPostProcessStateVariables(foundFunction, dataProcessingOutputInfo);
					DataProcessingHelper dataProcessingHelper = new DataProcessingHelper(functionHelper.postProcessStateVars,timePointHelper,dataIndexHelper);
					iterateHDF5(root,"",dataProcessingHelper);
					dataProcessingOutputResults =
						evaluatePostProcessFunction(dataProcessingOutputInfo, functionHelper.postProcessStateVars, dataProcessingHelper.specificDataValues,
								dataIndexHelper, timePointHelper, functionHelper.flattenedBoundExpression,variableNames[0]);
				}else{
					DataProcessingHelper dataProcessingHelper =
						new DataProcessingHelper(new String[] {variableNames[0]},timePointHelper,dataIndexHelper);
					iterateHDF5(root,"",dataProcessingHelper);
					alltimes = dataProcessingHelper.times;
					if(dataProcessingHelper.specificDataValues == null){
						throw new Exception("Couldn't find postprocess data as specified for var="+variableNames[0]);
					}
					dataProcessingOutputResults = new DataOperationResults.DataProcessingOutputDataValues(dataOperation.getVCDataIdentifier(),
						variableNames[0],timePointHelper,dataIndexHelper, dataProcessingHelper.specificDataValues[0]);	
				}
				if(dataOperation instanceof DataOperation.DataProcessingOutputTimeSeriesOP){
					TimeSeriesJobResults timeSeriesJobResults = null;
					DataProcessingOutputTimeSeriesOP dataProcessingOutputTimeSeriesOP = (DataOperation.DataProcessingOutputTimeSeriesOP)dataOperation;
					double[][] dataValues = ((DataOperationResults.DataProcessingOutputDataValues)dataProcessingOutputResults).getDataValues();//[time][data]
					double[] desiredTimes = (timePointHelper.isAllTimePoints()?alltimes:timePointHelper.getTimePoints());
					double[][][] timeSeriesFormatedValuesArr = new double[variableNames.length][dataIndexHelper.getDataIndexes().length+1][desiredTimes.length];
					for (int i = 0; i < timeSeriesFormatedValuesArr.length; i++) {//var
						for (int j = 0; j < timeSeriesFormatedValuesArr[i].length; j++) {//index
							if(j==0){
								timeSeriesFormatedValuesArr[i][j] = desiredTimes;
								continue;
							}
							for (int k = 0; k < timeSeriesFormatedValuesArr[i][j].length; k++) {//time
								//assume 1 variable for now
								timeSeriesFormatedValuesArr[i][j][k] = dataValues[k][j-1];
							}
						}
					}

					if(dataProcessingOutputTimeSeriesOP.getTimeSeriesJobSpec().isCalcSpaceStats()){
						SpatialStatsInfo spatialStatsInfo = new SpatialStatsInfo();
						spatialStatsInfo.bWeightsValid = false;
						timeSeriesJobResults =
							calculateStatisticsFromWhole(dataProcessingOutputTimeSeriesOP.getTimeSeriesJobSpec(), timeSeriesFormatedValuesArr, timePointHelper.getTimePoints(), spatialStatsInfo);
					}else{
						timeSeriesJobResults =
							new TSJobResultsNoStats(
								variableNames,
								new int[][] {dataIndexHelper.getDataIndexes()},
								timePointHelper.getTimePoints(),
								timeSeriesFormatedValuesArr);
					}
					dataProcessingOutputResults = new DataOperationResults.DataProcessingOutputTimeSeriesValues(dataOperation.getVCDataIdentifier(), timeSeriesJobResults);
				}
			}
		}else{
			throw new FileNotFoundException("Data Processing Output file '"+dataProcessingOutputFileHDF5.getPath()+"' not found");
		}
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		if(hdf5FileFormat != null){try{hdf5FileFormat.close();}catch(Exception e){e.printStackTrace();}}
	}

	return dataProcessingOutputResults;
}

private static double[] extractTimeRange(double[] alltimes,double startTime,double stoptime){
	ArrayList<Double> selectedtimePointsList = new ArrayList<Double>();
	for (int i = 0; i < alltimes.length; i++) {
		if(alltimes[i] >= startTime && alltimes[i] <= stoptime){
			selectedtimePointsList.add(alltimes[i]);
		}
	}
	double[] selectedTimePoints = new double[selectedtimePointsList.size()];
	for (int j = 0; j < selectedtimePointsList.size(); j++) {
		selectedTimePoints[j] = selectedtimePointsList.get(j);
	}
	return selectedTimePoints;
}

private static class FunctionHelper{
	public String[] postProcessStateVars;
	public Expression flattenedBoundExpression;
	public FunctionHelper(String[] postProcessStateVars,Expression flattenedBoundExpression) {
		this.postProcessStateVars = postProcessStateVars;
		this.flattenedBoundExpression = flattenedBoundExpression;
	}
	
}
private static FunctionHelper getPostProcessStateVariables(AnnotatedFunction annotatedFunction,DataOperationResults.DataProcessingOutputInfo dataProcessingOutputInfo) throws Exception{
	Expression flattenedExpression = annotatedFunction.getExpression().flatten();
	String[] postProcessSymbols = flattenedExpression.getSymbols();
	ArrayList<String> alteredPostProcessSymbols = new ArrayList<String>();
	for (int i = 0; i < postProcessSymbols.length; i++) {
		if(postProcessSymbols[i].equals("t") || postProcessSymbols[i].equals("x") || postProcessSymbols[i].equals("y") || postProcessSymbols[i].equals("z")){
			//skip
		}else{
			alteredPostProcessSymbols.add(postProcessSymbols[i]);
		}
	}
	postProcessSymbols = alteredPostProcessSymbols.toArray(new String[0]);
	String[] bindSymbols = new String[postProcessSymbols.length+TXYZ_OFFSET];
	bindSymbols[0]="t";
	bindSymbols[1]="x";
	bindSymbols[2]="y";
	bindSymbols[3]="z";
	for (int i = 0; i < postProcessSymbols.length; i++) {
		bindSymbols[i+TXYZ_OFFSET] = postProcessSymbols[i];
	}
	SimpleSymbolTable simpleSymbolTable = new SimpleSymbolTable(bindSymbols);
	flattenedExpression.bindExpression(simpleSymbolTable);
	return new FunctionHelper(postProcessSymbols, flattenedExpression);
}

private static DataProcessingOutputDataValues evaluatePostProcessFunction(
	DataOperationResults.DataProcessingOutputInfo dataProcessingOutputInfo,
	String[] postProcessSymbols,
	double[][][] postProcessData,
	DataIndexHelper dataIndexHelper,
	TimePointHelper timePointHelper,
	Expression flattenedBoundExpression,String varName) throws Exception{
	
	ISize iSize = dataProcessingOutputInfo.getVariableISize(postProcessSymbols[0]);
	VCImageUncompressed vcImage = new VCImageUncompressed(null, new byte[iSize.getXYZ()], new Extent(1, 1, 1), iSize.getX(), iSize.getY(), iSize.getZ());
	int dimension = 1+(iSize.getY()> 1?1:0)+(iSize.getZ()>1?1:0);
	RegionImage regionImage = new RegionImage(vcImage, dimension, dataProcessingOutputInfo.getVariableExtent(postProcessSymbols[0]), dataProcessingOutputInfo.getVariableOrigin(postProcessSymbols[0]), RegionImage.NO_SMOOTHING);
	CartesianMesh cartesianMesh = CartesianMesh.createSimpleCartesianMesh(
		dataProcessingOutputInfo.getVariableOrigin(postProcessSymbols[0]), dataProcessingOutputInfo.getVariableExtent(postProcessSymbols[0]), dataProcessingOutputInfo.getVariableISize(postProcessSymbols[0]), regionImage);
	
	double[] timePoints = null;
	if(timePointHelper.isAllTimePoints()){
		timePoints = dataProcessingOutputInfo.getVariableTimePoints();
	}else{
		timePoints = timePointHelper.getTimePoints();
	}

	double[][] evaluatedValues = new double[timePoints.length][];
	int dataIndexCount = 0;
	ISize DATA_SIZE = dataProcessingOutputInfo.getVariableISize(postProcessSymbols[0]);
	int DATA_SIZE_XY = DATA_SIZE.getX()*DATA_SIZE.getY();
	if(dataIndexHelper.isAllDataIndexes()){
		dataIndexCount = DATA_SIZE.getXYZ();
	}else if(dataIndexHelper.isSingleSlice()){
		dataIndexCount = DATA_SIZE_XY;
	}else{
		dataIndexCount = dataIndexHelper.getDataIndexes().length;
	}
		
	
	double args[] = new double[TXYZ_OFFSET+postProcessSymbols.length];

	for (int t = 0; t < timePoints.length; t++) {
		evaluatedValues[t] = new double[dataIndexCount];
		args[0] = timePoints[t];
		for (int i = 0; i < dataIndexCount; i++) {
			Coordinate coord;
			if(dataIndexHelper.isAllDataIndexes()){
				coord = cartesianMesh.getCoordinateFromVolumeIndex(i);
			}else if(dataIndexHelper.isSingleSlice()){
				coord = cartesianMesh.getCoordinateFromVolumeIndex(dataIndexHelper.getSliceIndex()*DATA_SIZE_XY+i);
			}else{
				coord = cartesianMesh.getCoordinateFromVolumeIndex(dataIndexHelper.getDataIndexes()[i]);
			}
			args[1] = coord.getX();
			args[2] = coord.getY();
			args[3] = coord.getZ();
			for (int j = 0; j < postProcessSymbols.length; j++) {
				args[TXYZ_OFFSET+j] = postProcessData[j][t][i];
			}
			evaluatedValues[t][i] = flattenedBoundExpression.evaluateVector(args);
//			System.out.println("in="+args[4]+" out="+evaluatedValues[t][i]+" sin(in)="+Math.sin(args[4]));
		}		
	}
	return new DataOperationResults.DataProcessingOutputDataValues(dataProcessingOutputInfo.getVCDataIdentifier(),varName,timePointHelper,dataIndexHelper , evaluatedValues);
}

private static void iterateHDF5(HObject hObject,String indent,DataProcessingHelper dataProcessingHelper) throws Exception{
	if(hObject instanceof Group){
		Group group = ((Group)hObject);
		printInfo(group,indent);
		if(group.getName().equals("/") || group.getName().equals(SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_POSTPROCESSING)){
			List<HObject> postProcessMembers = ((Group)hObject).getMemberList();
			for(HObject nextHObject:postProcessMembers){
				iterateHDF5(nextHObject, indent+" ", dataProcessingHelper);
			}
		}else if(group.getName().equals(SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_VARIABLESTATISTICS) && dataProcessingHelper.isInfoOnly()){
			populateStatNamesAndUnits(hObject, dataProcessingHelper);
			List<HObject> statDataAtEachTime = group.getMemberList();
			dataProcessingHelper.statValues = new double[dataProcessingHelper.statVarNames.length][statDataAtEachTime.size()];
			for(HObject nextStatData:statDataAtEachTime){
				printInfo(nextStatData,indent+" ");
				processDims(nextStatData, dataProcessingHelper,false);//always get stats data when ask for info
	    		double[] stats = (double[])dataProcessingHelper.tempData;
	    		int timeIndex = Integer.parseInt(nextStatData.getName().substring("time".length()));
	    		for (int j = 0; j < stats.length; j++) {
	    			dataProcessingHelper.statValues[j][timeIndex] = stats[j];		
				}
			}
		}else{//must be image data
			if(dataProcessingHelper.isInfoOnly()){
				dataProcessingHelper.imageNames = new ArrayList<String>();
				dataProcessingHelper.imageISize = new ArrayList<ISize>();
				dataProcessingHelper.imageOrigin = new ArrayList<Origin>();
				dataProcessingHelper.imageExtent = new ArrayList<Extent>();
				Origin imgDataOrigin;
				Extent imgDataExtent;
				HashMap<String, String> attrHashMap = getHDF5Attributes(group);
	    		if(attrHashMap.size() == 2){
		    		imgDataOrigin = new Origin(Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_ORIGINX)), 0, 0);
		    		imgDataExtent = new Extent(Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_EXTENTX)), 1, 1);//this is 1D, however the extentY, Z cannot take 0
	    		}	
	    		else if(attrHashMap.size() == 4){
		    		imgDataOrigin = new Origin(Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_ORIGINX)), Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_ORIGINY)), 0);
		    		imgDataExtent = new Extent(Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_EXTENTX)), Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_EXTENTY)), 1);//this is 2D, however the extentZ cannot take 0
	    		}
	    		else if(attrHashMap.size() == 6){
		    		imgDataOrigin = new Origin(Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_ORIGINX)), Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_ORIGINY)), Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_ORIGINZ)));
		    		imgDataExtent = new Extent(Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_EXTENTX)), Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_EXTENTY)), Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_EXTENTZ)));
	    		}else{
	    			throw new Exception("Unexpected number of origin/extent values");
	    		}
				dataProcessingHelper.imageNames.add(hObject.getName());
				dataProcessingHelper.imageOrigin.add(imgDataOrigin);
				dataProcessingHelper.imageExtent.add(imgDataExtent);
				//get ISize
				processDims(((Group)hObject).getMemberList().get(0), dataProcessingHelper,true);
				long[] dims = dataProcessingHelper.tempDims;
				ISize isize = new ISize((int)dims[0], (int)(dims.length>1?dims[1]:1), (int)(dims.length>2?dims[2]:1));
				dataProcessingHelper.imageISize.add(isize);
			}else{
				int currentVarNameIndex = -1;
				for (int i = 0; i < dataProcessingHelper.specificVarNames.length; i++) {
					if(group.getName().equals(dataProcessingHelper.specificVarNames[i])){
						currentVarNameIndex = i;
						break;
					}
				}
				if(currentVarNameIndex == -1){
					return;//skip this group
				}
				dataProcessingHelper.specificDataValues[currentVarNameIndex] = new double[(dataProcessingHelper.specificTimePointHelper.isAllTimePoints()?dataProcessingHelper.times.length:dataProcessingHelper.specificTimePointHelper.getTimePoints().length)][];
	    		List<HObject> imageDataAtEachTime = ((Group)hObject).getMemberList();
	    		int foundTimePointIndex = 0;
				for(HObject nextImageData:imageDataAtEachTime){
//					if(dataProcessingHelper.isInfoOnly()){
//						printInfo(nextImageData,indent+" ");
//						processDims(nextImageData, dataProcessingHelper,true);
//						long[] dims = dataProcessingHelper.tempDims;
//						ISize isize = new ISize((int)dims[0], (int)(dims.length>1?dims[1]:1), (int)(dims.length>2?dims[2]:1));
//						dataProcessingHelper.imageISize.add(isize);
//						break;//only need 1st one for info
//					}else{
		        		int hdf5GroupTimeIndex = Integer.parseInt(nextImageData.getName().substring(SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_TIMEPREFIX.length()));
		        		if(dataProcessingHelper.specificTimePointHelper.isAllTimePoints() || dataProcessingHelper.specificTimePointHelper.getTimePoints()[foundTimePointIndex] == dataProcessingHelper.times[hdf5GroupTimeIndex]){
	
		        			int timeIndex = (dataProcessingHelper.specificTimePointHelper.isAllTimePoints()?hdf5GroupTimeIndex:foundTimePointIndex);
		        			processDims(nextImageData, dataProcessingHelper,false);
		        			long[] dims = dataProcessingHelper.tempDims;
		        			ISize isize = new ISize((int)dims[0], (int)(dims.length>1?dims[1]:1), (int)(dims.length>2?dims[2]:1));
		        			if(dataProcessingHelper.specificDataIndexHelper.isAllDataIndexes()){
		        				dataProcessingHelper.specificDataValues[currentVarNameIndex][timeIndex] = (double[])dataProcessingHelper.tempData;
		        			}else if(dataProcessingHelper.specificDataIndexHelper.isSingleSlice()){
		        				dataProcessingHelper.specificDataValues[currentVarNameIndex][timeIndex] = new double[isize.getX()*isize.getY()];
		        				System.arraycopy(
		        					(double[])dataProcessingHelper.tempData,dataProcessingHelper.specificDataIndexHelper.getSliceIndex()*(isize.getX()*isize.getY()),
		        					dataProcessingHelper.specificDataValues[currentVarNameIndex][timeIndex], 0, isize.getX()*isize.getY());
		        			}else{
		        				dataProcessingHelper.specificDataValues[currentVarNameIndex][timeIndex] = new double[dataProcessingHelper.specificDataIndexHelper.getDataIndexes().length];
		        				for (int i = 0; i < dataProcessingHelper.specificDataIndexHelper.getDataIndexes().length; i++) {
		        					dataProcessingHelper.specificDataValues[currentVarNameIndex][timeIndex][i] = ((double[])dataProcessingHelper.tempData)[dataProcessingHelper.specificDataIndexHelper.getDataIndexes()[i]];
								}
		        			}
		        			foundTimePointIndex++;
		        			if(!dataProcessingHelper.specificTimePointHelper.isAllTimePoints() && foundTimePointIndex == dataProcessingHelper.specificTimePointHelper.getTimePoints().length){
		        				//break out after we get our data
		        				break;
		        			}
		        		}
	
//					}
				}
			}
		}
	}else if(hObject instanceof Dataset){
		Dataset dataset = (Dataset)hObject;
		printInfo(dataset,indent);
		if(dataset.getName().equals(SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_TIMES)){
			processDims(hObject, dataProcessingHelper,false);
			dataProcessingHelper.times = (double[])dataProcessingHelper.tempData;
		}
	}else if(hObject instanceof Datatype){
		printInfo(hObject, indent);
	}else{
		printInfo(hObject, indent);
	}
}
private static void printInfo(HObject hObject,String indent) throws Exception{
	if(true){return;}
	System.out.println(indent+hObject.getName()+":"+hObject.getClass().getName());
	List metaDatas = hObject.getMetadata();
	for(Object metaData:metaDatas){
		if(metaData instanceof Attribute){
			Attribute attribute = (Attribute)metaData;
			System.out.println(indent+"metadata="+attribute.getName()+" "+attribute.getType().getDatatypeDescription());
		}else{
			System.out.println(indent+"metadata="+metaData.getClass().getName());
		}
	}	
}
private static void processDims(HObject hObject,DataProcessingHelper dataProcessingHelper,boolean bInfoOnly) throws Exception{
	H5ScalarDS h5ScalarDS = (H5ScalarDS)hObject;
	h5ScalarDS.init();
	dataProcessingHelper.tempDims = h5ScalarDS.getDims();
	
	//make sure all dimensions are selected for loading if 3D
	//note: for 3D, only 1st slice selected by default
	long[] selectedDims = h5ScalarDS.getSelectedDims();
	if(selectedDims != null && selectedDims.length > 2){
		//changes internal class variable used during read
		selectedDims[2] = dataProcessingHelper.tempDims[2];
	}
	if(!bInfoOnly){
		//load all data
		dataProcessingHelper.tempData = h5ScalarDS.read();
	}

	if(dataProcessingHelper.tempDims != null){
		if(dataProcessingHelper.tempDims.length > 1){
			//For HDF5View (x stored in index 1) and (y stored in index 0) so must switch back to normal assumption
			long dimsY = dataProcessingHelper.tempDims[0];
			dataProcessingHelper.tempDims[0] = dataProcessingHelper.tempDims[1];
			dataProcessingHelper.tempDims[1] = dimsY;
		}
//		//uncomment for Debug
//    	System.out.print(" dims=(");
//    	for (int j = 0; j < dataProcessingHelper.tempDims.length; j++) {
//			System.out.print((j>0?"x":"")+dataProcessingHelper.tempDims[j]);
//		}
//    	System.out.print(")");
	}
}
private static void populateStatNamesAndUnits(HObject hObject,DataProcessingHelper dataProcessingHelper) throws Exception{
	if(!hObject.getName().equals(SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_VARIABLESTATISTICS)){
		throw new Exception("expecting obejct name "+SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_VARIABLESTATISTICS);
	}
    final String NAME_ATTR = "_name";
    final String UNIT_ATTR = "_unit";
    final String STAT_PREFIX = "comp_";

	List<Metadata> metaDataL = hObject.getMetadata();
	if(metaDataL != null){
		HashMap<String, String> attrHashMap = getHDF5Attributes(hObject);//map contains the same number of names and attributes
		String[] variableStatNames = null;
		String[] variableUnits = null;
		Iterator<String> attrIterTemp = attrHashMap.keySet().iterator();
		boolean bHasUnit = false;
		for (int j = 0; j < attrHashMap.size(); j++) {
			String compVal = attrIterTemp.next();
			if(compVal.contains(NAME_ATTR) || compVal.contains(UNIT_ATTR)){
				bHasUnit = true;
				break;
			}
		}
		if(bHasUnit){
			variableStatNames = new String[attrHashMap.size()/2];
			variableUnits = new String[attrHashMap.size()/2]; 
		}else{
			variableStatNames = new String[attrHashMap.size()]; // old way
		}
		Iterator<String> attrIter = attrHashMap.keySet().iterator();
		for (int j = 0; j < attrHashMap.size(); j++) {
			String compVal = attrIter.next();
			if(compVal.contains(NAME_ATTR)){
				int compVarIdx = Integer.parseInt(compVal.substring(STAT_PREFIX.length(), compVal.indexOf('_', STAT_PREFIX.length())));
				variableStatNames[compVarIdx] = attrHashMap.get(compVal);
			}else if(compVal.contains(UNIT_ATTR)){
				int compVarIdx = Integer.parseInt(compVal.substring(STAT_PREFIX.length(), compVal.indexOf('_', STAT_PREFIX.length())));
				variableUnits[compVarIdx] = attrHashMap.get(compVal);
			}else{//old way for var names(e.g. comp_0 = abc) with no "_name" or "_unit"
				int compVarIdx = Integer.parseInt(compVal.substring(STAT_PREFIX.length()));
				variableStatNames[compVarIdx] = attrHashMap.get(compVal);
			}
		}
		dataProcessingHelper.statVarNames = variableStatNames;
		dataProcessingHelper.statVarUnits = variableUnits;
	}
}

//uncomment it for Debug
//private static String DATASETNAME = "/";
//enum H5O_type {
//    H5O_TYPE_UNKNOWN(-1), // Unknown object type
//    H5O_TYPE_GROUP(0), // Object is a group
//    H5O_TYPE_DATASET(1), // Object is a dataset
//    H5O_TYPE_NAMED_DATATYPE(2), // Object is a named data type
//    H5O_TYPE_NTYPES(3); // Number of different object types
//	private static final Map<Integer, H5O_type> lookup = new HashMap<Integer, H5O_type>();
//
//	static {
//		for (H5O_type s : EnumSet.allOf(H5O_type.class))
//			lookup.put(s.getCode(), s);
//	}
//
//	private int code;
//
//	H5O_type(int layout_type) {
//		this.code = layout_type;
//	}
//
//	public int getCode() {
//		return this.code;
//	}
//
//	public static H5O_type get(int code) {
//		return lookup.get(code);
//	}
//}
//
//public static void do_iterate(File hdfFile) {
//	int file_id = -1;
//
//	// Open a file using default properties.
//	try {
//		file_id = H5.H5Fopen(hdfFile.getAbsolutePath(), HDF5Constants.H5F_ACC_RDONLY, HDF5Constants.H5P_DEFAULT);
//	}
//	catch (Exception e) {
//		e.printStackTrace();
//	}
//
//	// Begin iteration.
//	System.out.println("Objects in root group:");
//	try {
//		if (file_id >= 0) {
//			int count = (int)H5.H5Gn_members(file_id, DATASETNAME);
//			String[] oname = new String[count];
//            int[] otype = new int[count];
//            int[] ltype = new int[count];
//			long[] orefs = new long[count];
//			H5.H5Gget_obj_info_all(file_id, DATASETNAME, oname, otype, ltype, orefs, HDF5Constants.H5_INDEX_NAME);
//
//			// Get type of the object and display its name and type.
//			for (int indx = 0; indx < otype.length; indx++) {
//				switch (H5O_type.get(otype[indx])) {
//				case H5O_TYPE_GROUP:
//					System.out.println("  Group: " + oname[indx]);
//					break;
//				case H5O_TYPE_DATASET:
//					System.out.println("  Dataset: " + oname[indx]);
//					break;
//				case H5O_TYPE_NAMED_DATATYPE:
//					System.out.println("  Datatype: " + oname[indx]);
//					break;
//				default:
//					System.out.println("  Unknown: " + oname[indx]);
//				}
//			}
//		}
//	}
//	catch (Exception e) {
//		e.printStackTrace();
//	}
//
//	// Close the file.
//	try {
//		if (file_id >= 0)
//			H5.H5Fclose(file_id);
//	}
//	catch (Exception e) {
//		e.printStackTrace();
//	}
//}

//public static void populateHDF5(Group g, String indent,DataProcessingOutput0 dataProcessingOutput,boolean bVarStatistics,String imgDataName,Origin imgDataOrigin,Extent imgDataExtent) throws Exception
//{
//    if (g == null)
//        return;
//      
//    List members = g.getMemberList();
//
//    int n = members.size();
//    indent += "    ";
//    HObject obj = null;
//
//    String nameAtt = "_name";
//    String unitAtt = "_unit";
//    for (int i=0; i<n; i++){
//    	
//        obj = (HObject)members.get(i);
//        //uncomment for Debug
//        /*System.out.print(indent+obj+" ("+obj.getClass().getName()+") isGroup="+(obj instanceof Group));*/
//        if(obj.getName().equals(SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_VARIABLESTATISTICS)){
//	    	List<Metadata> metaDataL = obj.getMetadata();
//	    	if(metaDataL != null){
//	    		HashMap<String, String> attrHashMap = getHDF5Attributes(obj);//map contains the same number of names and attributes
//	    		String[] variableStatNames = null;
//	    		String[] variableUnits = null;
//	    		Iterator<String> attrIterTemp = attrHashMap.keySet().iterator();
//	    		boolean bHasUnit = false;
//	    		for (int j = 0; j < attrHashMap.size(); j++) {
//	    			String compVal = attrIterTemp.next();
//	    			if(compVal.contains(nameAtt) || compVal.contains(unitAtt)){
//	    				bHasUnit = true;
//	    				break;
//	    			}
//	    		}
//	    		if(bHasUnit){
//	    			variableStatNames = new String[attrHashMap.size()/2];
//	    			variableUnits = new String[attrHashMap.size()/2]; 
//	    		}else{
//	    			variableStatNames = new String[attrHashMap.size()]; // old way
//	    		}
//	    		Iterator<String> attrIter = attrHashMap.keySet().iterator();
//	    		for (int j = 0; j < attrHashMap.size(); j++) {
//	    			String compVal = attrIter.next();
//	    			int compVarIdx = Integer.parseInt(compVal.substring(5, 6));
//	    			if(compVal.contains(nameAtt)){
//	    				variableStatNames[compVarIdx] = attrHashMap.get(compVal);
//	    			}else if(compVal.contains(unitAtt)){
//	    				variableUnits[compVarIdx] = attrHashMap.get(compVal);
//	    			}else{//old way for var names(e.g. comp_0 = abc) with no "_name" or "_unit"
//	    				variableStatNames[compVarIdx] = attrHashMap.get(compVal);
//	    			}
//				}
//	        	dataProcessingOutput.setVariableStatNames(variableStatNames);
//	        	dataProcessingOutput.setVariableUnits(variableUnits);
//	        	dataProcessingOutput.setVariableStatValues(new double[variableStatNames.length][dataProcessingOutput.getTimes().length]);
//	        	bVarStatistics = true;
//	    	}
//        }else if(obj instanceof H5ScalarDS){
//        	H5ScalarDS h5ScalarDS = (H5ScalarDS)obj;
//        	h5ScalarDS.init();
//        	long[] dims = h5ScalarDS.getDims();
//        	
//        	//make sure all dimensions are selected for loading if 3D
//        	//note: for 3D, only 1st slice selected by default
//        	long[] selectedDims = h5ScalarDS.getSelectedDims();
//        	if(selectedDims != null && selectedDims.length > 2){
//        		//changes internal class variable used during read
//        		selectedDims[2] = dims[2];
//        	}
//        	
//        	//load all data
//        	Object data = h5ScalarDS.read();
//
//        	if(dims != null){
//        		if(dims.length > 1){
//        			//For HDF5View (x stored in index 1) and (y stored in index 0) so must switch back to normal assumption
//        			long dimsY = dims[0];
//        			dims[0] = dims[1];
//        			dims[1] = dimsY;
//        		}
//        		//uncomment for Debug
//	        	/*System.out.print(" dims=(");
//	        	for (int j = 0; j < dims.length; j++) {
//					System.out.print((j>0?"x":"")+dims[j]);
//				}
//	        	System.out.print(")");*/
//        	}
//        	
////        	System.out.print(" len="+times.length);
//        	if(obj.getName().equals(SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_TIMES)){
//            	double[] times = (double[])data;
//            	dataProcessingOutput.setTimes(times);        		
//        	}else if(bVarStatistics){
//        		double[] stats = (double[])data;
//        		int timeIndex = Integer.parseInt(obj.getName().substring("time".length()));
//        		for (int j = 0; j < stats.length; j++) {
//            		dataProcessingOutput.getVariableStatValues()[j][timeIndex] = stats[j];		
//				}
//        	}else{
//        		double min = ((double[])data)[0];
//        		double max = min;
//        		for (int j = 0; j < ((double[])data).length; j++) {
//					min = Math.min(min, ((double[])data)[j]);
//					max = Math.max(max, ((double[])data)[j]);
//				}
//        		int xSize = (int)dims[0];
//        		int ySize = (int)(dims.length>1?dims[1]:1);
//        		int zSize = (int)(dims.length>2?dims[2]:1);
//        		SourceDataInfo sourceDataInfo = 
//        			new SourceDataInfo(SourceDataInfo.RAW_VALUE_TYPE, (double[])data, (imgDataExtent==null?new Extent(1,1,1):imgDataExtent), (imgDataOrigin==null?null:imgDataOrigin), new Range(min, max), 0, xSize, 1, ySize, xSize, zSize, xSize*ySize);
//        		Vector<SourceDataInfo> otherData = dataProcessingOutput.getDataGenerators().get(imgDataName);
//        		int timeIndex = Integer.parseInt(obj.getName().substring(SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_TIMEPREFIX.length()));
//        		otherData.add(sourceDataInfo);
//        		if(otherData.size()-1 != timeIndex){
//        			throw new Exception("Error HDF5 parse: added data index does not match timeIndex");
//        		}
//        	}
//        }else if (obj instanceof H5Group && !obj.getName().equals(SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_POSTPROCESSING)){
//        	bVarStatistics = false;
//        	imgDataName = obj.getName();
//        	dataProcessingOutput.getDataGenerators().put(imgDataName, new Vector<SourceDataInfo>());
//
//	    	List<Metadata> metaDataL = obj.getMetadata();
//	    	if(metaDataL != null){//assume 6 attributes defining origin and extent
//	    		HashMap<String, String> attrHashMap = getHDF5Attributes(obj);
//	    		if(attrHashMap.size() == 2){
//		    		imgDataOrigin = new Origin(Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_ORIGINX)), 0, 0);
//		    		imgDataExtent = new Extent(Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_EXTENTX)), 1, 1);//this is 1D, however the extentY, Z cannot take 0
//	    		}	
//	    		else if(attrHashMap.size() == 4){
//		    		imgDataOrigin = new Origin(Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_ORIGINX)), Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_ORIGINY)), 0);
//		    		imgDataExtent = new Extent(Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_EXTENTX)), Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_EXTENTY)), 1);//this is 2D, however the extentZ cannot take 0
//	    		}
//	    		else if(attrHashMap.size() == 6){
//		    		imgDataOrigin = new Origin(Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_ORIGINX)), Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_ORIGINY)), Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_ORIGINZ)));
//		    		imgDataExtent = new Extent(Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_EXTENTX)), Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_EXTENTY)), Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_EXTENTZ)));
//	    		}
//	    	}
//
//        }
//        System.out.println();
//        
//        if (obj instanceof Group)
//        {
//        	populateHDF5((Group)obj, indent,dataProcessingOutput,bVarStatistics,imgDataName,imgDataOrigin,imgDataExtent);
//        }
//    }
//}
private static HashMap<String, String> getHDF5Attributes(HObject hObject) throws Exception{
	HashMap<String, String> attrHashMap = new HashMap<String, String>();
	List<Metadata> metaDataL = hObject.getMetadata();
	if(metaDataL != null){
		for (int j = 0; j < metaDataL.size(); j++) {
    		Attribute attr = (Attribute)metaDataL.get(j);
    		String attrValue = attr.toString(",");
    		//System.out.print(" "+attr.getName()+"='"+attrValue+"'");
    		attrHashMap.put(attr.getName(),attr.toString(","));
		}
	}
	return attrHashMap;
}
private boolean isDomainInside(CartesianMesh mesh, Domain domain, int membraneIndex) {
	boolean bInside = true;
	int volIndexNear = mesh.getMembraneElements()[membraneIndex].getInsideVolumeIndex();
	if (!mesh.getCompartmentSubdomainNamefromVolIndex(volIndexNear).equals(domain.getName())) {
		bInside = false;
	}
	return bInside;
}

private double interpolateVolDataValToMemb(CartesianMesh mesh, Domain domain, int membraneIndex, SimDataHolder simDataHolder, boolean IsRegion){
	return interpolateVolDataValToMemb(mesh, membraneIndex, simDataHolder, isDomainInside(mesh, domain, membraneIndex), IsRegion);
}

private double interpolateVolDataValToMemb(CartesianMesh mesh,int membraneIndex,SimDataHolder simDataHolder,boolean isInside,boolean IsRegion){
	
	VolumeIndexNearFar volIndexNearFar = interpolateFindNearFarIndex(mesh, membraneIndex, isInside,IsRegion);
	if(volIndexNearFar.volIndexFar == -1){
		return simDataHolder.getData()[volIndexNearFar.volIndexNear];
	}
	return VolumeIndexNearFar.interpolate(simDataHolder.getData()[volIndexNearFar.volIndexNear], simDataHolder.getData()[volIndexNearFar.volIndexFar]);	
}

private VolumeIndexNearFar interpolateFindNearFarIndex(CartesianMesh mesh, Domain domain, int membraneIndex, boolean IsRegion){
	return interpolateFindNearFarIndex(mesh, membraneIndex, isDomainInside(mesh, domain, membraneIndex), IsRegion);
}

private VolumeIndexNearFar interpolateFindNearFarIndex(CartesianMesh mesh,int membraneIndex,boolean isInside,boolean isRegion){
	int volIndexNear = -1;
	int volIndexFar = -1;
	if(isInside){
		volIndexNear = mesh.getMembraneElements()[membraneIndex].getInsideVolumeIndex();
		volIndexFar = volIndexNear+(volIndexNear-mesh.getMembraneElements()[membraneIndex].getOutsideVolumeIndex());
	}else{
		volIndexNear = mesh.getMembraneElements()[membraneIndex].getOutsideVolumeIndex();
		volIndexFar = volIndexNear+(volIndexNear-mesh.getMembraneElements()[membraneIndex].getInsideVolumeIndex());
	}
	//Check if totally out of bounds
	if(volIndexFar < 0 || volIndexFar > mesh.getNumVolumeElements()){
		volIndexFar = -1;
	}else{
		//Check if index wrapped
		CoordinateIndex coordFar = mesh.getCoordinateIndexFromVolumeIndex(volIndexFar);
		CoordinateIndex coordNear = mesh.getCoordinateIndexFromVolumeIndex(volIndexNear);
		if( Math.abs(coordFar.x-coordNear.x) > 1 ||
			Math.abs(coordFar.y-coordNear.y) > 1 ||
			Math.abs(coordFar.z-coordNear.z) > 1
		){
			volIndexFar = -1;
		}else{
			//Check if in same region
			if(mesh.getVolumeRegionIndex(volIndexNear) != mesh.getVolumeRegionIndex(volIndexFar)){
				volIndexFar = -1;
			}			
		}
	}
	volIndexNear = (isRegion?mesh.getVolumeRegionIndex(volIndexNear):volIndexNear);
	volIndexFar = (volIndexFar == -1?-1:(isRegion?mesh.getVolumeRegionIndex(volIndexFar):volIndexFar));
	return new VolumeIndexNearFar(volIndexNear,volIndexFar);
}

/**
 * Insert the method's description here.
 * Creation date: (10/13/00 9:13:52 AM)
 * @return cbit.vcell.simdata.SimDataBlock
 * @param user cbit.vcell.server.User
 * @param simResults cbit.vcell.simdata.SimResults
 * @param function cbit.vcell.math.Function
 * @param time double
 */
private SimDataBlock evaluateFunction(
		OutputContext outputContext,
	final VCDataIdentifier vcdID, 
	VCData simData, 
	AnnotatedFunction function, 
	double time)
	throws ExpressionException, DataAccessException, IOException, MathException {

	Expression exp = new Expression(function.getExpression());
	exp = SolverUtilities.substituteSizeAndNormalFunctions(exp, function.getFunctionType().getVariableDomain());
	exp.bindExpression(simData);
	exp = fieldFunctionSubstitution(outputContext, vcdID, exp);
	
	//
	// get Dependent datasets
	//
	// variables are indexed by a number, t=0, x=1, y=2, z=3, a(i) = 4+i where a's are other variables
	// these variables
	//
	CartesianMesh mesh = null;
	if(function.getFunctionType().equals(VariableType.POSTPROCESSING)){
		mesh = ((SimulationData)simData).getPostProcessingMesh(function.getName(),outputContext);
	}
	if(mesh == null){
		mesh = getMesh(vcdID);
	}
	
	
	String[] dependentIDs = exp.getSymbols();
	Vector<SimDataHolder> dataSetList = new Vector<SimDataHolder>();
	Vector<DataSetIdentifier> dependencyList = new Vector<DataSetIdentifier>();
	int varIndex = TXYZ_OFFSET;
	int dataLength = 0;
	long lastModified = 0;
	VariableType variableType = function.getFunctionType();
	if (variableType.equals(VariableType.VOLUME) || variableType.equals(VariableType.POSTPROCESSING)) {
		dataLength = mesh.getNumVolumeElements();
	} else if (variableType.equals(VariableType.MEMBRANE)) {
		dataLength = mesh.getNumMembraneElements();
	} else if (variableType.equals(VariableType.VOLUME_REGION)) {
		dataLength = mesh.getNumVolumeRegions();
	} else if (variableType.equals(VariableType.MEMBRANE_REGION)) {
		dataLength = mesh.getNumMembraneRegions();
	}
	VariableType computedVariableType = null;
	int computedDataLength = 0;
	for (int i = 0; dependentIDs!=null && i < dependentIDs.length; i++) {
		SymbolTableEntry ste = exp.getSymbolBinding(dependentIDs[i]);
		if (ste instanceof DataSetIdentifier) {
			DataSetIdentifier dsi = (DataSetIdentifier) ste;
			dependencyList.addElement(dsi);
			dsi.setIndex(varIndex++);
			if (dsi.getName().endsWith(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX) || dsi.getName().endsWith(InsideVariable.INSIDE_VARIABLE_SUFFIX)){
				String volVarName = dsi.getName().substring(0,dsi.getName().lastIndexOf("_"));
				SimDataBlock simDataBlock = getSimDataBlock(outputContext,vcdID, volVarName, time);
				lastModified = simDataBlock.getPDEDataInfo().getTimeStamp();
				//
				// if inside/outside volume element dependent, then can only be a membrane type 
				//
				if (simDataBlock.getVariableType().equals(VariableType.VOLUME)){
					computedVariableType = VariableType.MEMBRANE;
					computedDataLength = mesh.getMembraneElements().length;
				//
				// if inside/outside volume element dependent, then can only be a membrane type 
				//
				}else if (simDataBlock.getVariableType().equals(VariableType.VOLUME_REGION) && variableType==null){
					computedVariableType = VariableType.MEMBRANE_REGION;
					computedDataLength = mesh.getNumMembraneRegions();
				}
				dataSetList.addElement(simDataBlock);
			}else{	
				SimDataBlock simDataBlock = getSimDataBlock(outputContext,vcdID, dsi.getName(), time);
				if (variableType==null || simDataBlock.getVariableType().isExpansionOf(variableType)) {
					lastModified = simDataBlock.getPDEDataInfo().getTimeStamp();
					computedDataLength = simDataBlock.getData().length;
					computedVariableType = simDataBlock.getVariableType();
				}
				dataSetList.addElement(simDataBlock);
			}
		} else if (ste instanceof ReservedVariable) {
			ReservedVariable rv = (ReservedVariable) ste;
			if (rv.isTIME()) {
				rv.setIndex(0);
			} else if (rv.isX()) {
				rv.setIndex(1);
			} else if (rv.isY()) {
				rv.setIndex(2);
			} else if (rv.isZ()) {
				rv.setIndex(3);
			}
		}else if (ste instanceof FieldDataParameterVariable){
			//Field Data
			((FieldDataParameterVariable)ste).setIndex(varIndex++);
			final double[] steResampledFieldData =
				((FieldDataParameterVariable)ste).getResampledFieldData();
			final VariableType newVariableType =
				(steResampledFieldData.length == mesh.getNumVolumeElements()?
							VariableType.VOLUME:
								(steResampledFieldData.length == mesh.getNumMembraneElements()?
										VariableType.MEMBRANE:null)
				);
			if(newVariableType == null){
				throw new DataAccessException("Couldn't determine VariableType for FieldData");
			}
			if(variableType != null && !variableType.equals(newVariableType)){
				throw new DataAccessException("Incompatible VariableType for FieldData");
			}
			SimDataHolder newSimDataHolder =
				new SimDataHolder(){
					public double[] getData() {
						return steResampledFieldData;
					}
					public VariableType getVariableType() {
						return newVariableType;
					}
			};
			dataSetList.addElement(newSimDataHolder);
			dependencyList.add(new DataSetIdentifier(ste.getName(), newVariableType,((FieldDataParameterVariable) ste).getDomain()));
			if(variableType == null){
				computedVariableType = newVariableType;
				computedDataLength = newSimDataHolder.getData().length;
			}
		}
	}	       
	       
	if (computedDataLength <= 0) {
		log.alert("dependencies for function '"+function+"' not found, assuming datalength of volume");
		computedDataLength = mesh.getDataLength(VariableType.VOLUME);
		computedVariableType = VariableType.VOLUME;
//		try {
//			computedDataLength = mesh.getDataLength(VariableType.VOLUME);
//			computedVariableType = VariableType.VOLUME;
//		}catch (MathException e){
//			log.exception(e);
//			throw new RuntimeException("MathException, cannot determine domain for function '"+function+"'");
//		}catch (FileNotFoundException e){
//			log.exception(e);
//			throw new RuntimeException("Mesh not found, cannot determine domain for function '"+function+"'");
//		}
	}

	if (!variableType.equals(computedVariableType)) {
		System.err.println("function [" + function.getName() + "] variable type [" + variableType.getTypeName() + "] is not equal to computed variable type [" + computedVariableType.getTypeName() + "].");
	}
	if (dataLength == 0)
	{
		dataLength = computedDataLength;
		variableType = computedVariableType;
	}
	//
	//Gradient Info for special processing
	//
	boolean isGrad = hasGradient(exp);
	
	if(isGrad && !variableType.equals(VariableType.VOLUME)){
		throw new DataAccessException("Gradient function is not implemented for datatype "+variableType.getTypeName());
	}
	double args[] = new double[varIndex+(isGrad?12*varIndex:0)];
	double data[] = new double[dataLength];
	args[0] = time; // time
	args[1] = 0.0; // x
	args[2] = 0.0; // y
	args[3] = 0.0; // z	
	String dividedByZeroMsg = "";
	for (int i = 0; i < dataLength; i++) {
		//
		// initialize argments to expression
		//
		if (variableType.equals(VariableType.VOLUME) || variableType.equals(VariableType.POSTPROCESSING)){
			Coordinate coord = mesh.getCoordinateFromVolumeIndex(i);
			args[1] = coord.getX();
			args[2] = coord.getY();
			args[3] = coord.getZ();
			for (int j = 0; j < varIndex - TXYZ_OFFSET; j++) {
				SimDataHolder simDataHolder = dataSetList.elementAt(j);
				if (simDataHolder.getVariableType().equals(VariableType.VOLUME) || simDataHolder.getVariableType().equals(VariableType.POSTPROCESSING)){
					args[TXYZ_OFFSET + j] = simDataHolder.getData()[i];
				}else if (simDataHolder.getVariableType().equals(VariableType.VOLUME_REGION)){
					int volumeIndex = mesh.getVolumeRegionIndex(i);
					args[TXYZ_OFFSET + j] = simDataHolder.getData()[volumeIndex];
				}else if (simDataHolder.getVariableType().equals(VariableType.POINT_VARIABLE)){
					args[TXYZ_OFFSET + j] = simDataHolder.getData()[0];
				}
			}
			if(isGrad){
				getSpatialNeighborData(mesh,i,varIndex,time,dataSetList,args);
			}
		}else if (variableType.equals(VariableType.VOLUME_REGION)){
			for (int j = 0; j < varIndex - TXYZ_OFFSET; j++) {
				SimDataHolder simDataHolder = dataSetList.elementAt(j);
				if (simDataHolder.getVariableType().equals(VariableType.VOLUME_REGION)){
					args[TXYZ_OFFSET + j] = simDataHolder.getData()[i];
				}else if (simDataHolder.getVariableType().equals(VariableType.POINT_VARIABLE)){
					args[TXYZ_OFFSET + j] = simDataHolder.getData()[0];
				}
			}
		}else if (variableType.equals(VariableType.MEMBRANE)){
			Coordinate coord = mesh.getCoordinateFromMembraneIndex(i);
			args[1] = coord.getX();
			args[2] = coord.getY();
			args[3] = coord.getZ();
			for (int j = 0; j < varIndex - TXYZ_OFFSET; j++) {
				DataSetIdentifier dsi = (DataSetIdentifier)dependencyList.elementAt(j);
				SimDataHolder simDataHolder = dataSetList.elementAt(j);
				if (simDataHolder.getVariableType().equals(VariableType.VOLUME)) {
					if (mesh.isChomboMesh())
					{
						String varName = dsi.getName();
						if (dsi.getName().endsWith(InsideVariable.INSIDE_VARIABLE_SUFFIX))
						{
							varName = varName.substring(0, varName.lastIndexOf(InsideVariable.INSIDE_VARIABLE_SUFFIX));
						}
						else if (dsi.getName().endsWith(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX))
						{
							varName = varName.substring(0, varName.lastIndexOf(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX));
						}
						args[TXYZ_OFFSET + j] = getChomboExtrapolatedValues(vcdID, varName, time).getData()[i];
					}
					else
					{
						if (dsi.getName().endsWith(InsideVariable.INSIDE_VARIABLE_SUFFIX)){
							args[TXYZ_OFFSET + j] = interpolateVolDataValToMemb(mesh,i,simDataHolder,true,false);
						} else if (dsi.getName().endsWith(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX)){
							args[TXYZ_OFFSET + j] = interpolateVolDataValToMemb(mesh,i,simDataHolder,false,false);
						} else {
							args[TXYZ_OFFSET + j] = interpolateVolDataValToMemb(mesh,dsi.getDomain(), i,simDataHolder,false);
						}
					}
				}else if (simDataHolder.getVariableType().equals(VariableType.VOLUME_REGION)) {
					if (dsi.getName().endsWith(InsideVariable.INSIDE_VARIABLE_SUFFIX)){
						args[TXYZ_OFFSET + j] = interpolateVolDataValToMemb(mesh,i,simDataHolder,true,true);
					} else if (dsi.getName().endsWith(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX)){
						args[TXYZ_OFFSET + j] = interpolateVolDataValToMemb(mesh,i,simDataHolder,false,true);
					} else {
						args[TXYZ_OFFSET + j] = interpolateVolDataValToMemb(mesh,dsi.getDomain(),i,simDataHolder,true);
					}
				}else if (simDataHolder.getVariableType().equals(VariableType.MEMBRANE)){
					args[TXYZ_OFFSET + j] = simDataHolder.getData()[i];
				}else if (simDataHolder.getVariableType().equals(VariableType.MEMBRANE_REGION)){
					int memRegionIndex = mesh.getMembraneRegionIndex(i);
					args[TXYZ_OFFSET + j] = simDataHolder.getData()[memRegionIndex];
				}else if (simDataHolder.getVariableType().equals(VariableType.POINT_VARIABLE)){
					args[TXYZ_OFFSET + j] = simDataHolder.getData()[0];
				}

			}
		}else if (variableType.equals(VariableType.MEMBRANE_REGION)){
			for (int j = 0; j < varIndex - TXYZ_OFFSET; j++) {
				DataSetIdentifier dsi = (DataSetIdentifier)dependencyList.elementAt(j);
				SimDataHolder simDataHolder = dataSetList.elementAt(j);
				if (simDataHolder.getVariableType().equals(VariableType.VOLUME_REGION) && dsi.getName().endsWith(InsideVariable.INSIDE_VARIABLE_SUFFIX)){
					//
					// find "inside" volume element index for first membrane element in MembraneRegion 'i'.
					//
					for (int k = 0; k < mesh.getMembraneElements().length; k++){
						if (mesh.getMembraneRegionIndex(k)==i){
							args[TXYZ_OFFSET + j] = interpolateVolDataValToMemb(mesh,i,simDataHolder,true,true);
							break;
						}
					}
				}else if (simDataHolder.getVariableType().equals(VariableType.VOLUME_REGION) && dsi.getName().endsWith(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX)){
					//
					// find "outside" volume element index for first membrane element in MembraneRegion 'i'.
					//
					for (int k = 0; k < mesh.getMembraneElements().length; k++){
						if (mesh.getMembraneRegionIndex(k)==i){
							args[TXYZ_OFFSET + j] = interpolateVolDataValToMemb(mesh,i,simDataHolder,false,true);
							break;
						}
					}
				}else if (simDataHolder.getVariableType().equals(VariableType.MEMBRANE)){
					args[TXYZ_OFFSET + j] = simDataHolder.getData()[i];
				}else if (simDataHolder.getVariableType().equals(VariableType.MEMBRANE_REGION)){
					int memRegionIndex = i;
					args[TXYZ_OFFSET + j] = simDataHolder.getData()[memRegionIndex];
				}else if (simDataHolder.getVariableType().equals(VariableType.POINT_VARIABLE)){
					args[TXYZ_OFFSET + j] = simDataHolder.getData()[0];
				}

			}
		}
		try {
			data[i] = exp.evaluateVector(args);
//			if(time ==0){
//				System.out.print("non-multi evalFunction ");
//				for (int m = 0; m < args.length; m++) {
//					System.out.print(args[m]);
//				}
//				System.out.println(" "+(args[args.length-2]/args[args.length-1]));
//			}
		}catch (DivideByZeroException e){
			dividedByZeroMsg = e.getMessage();
			data[i] = Double.POSITIVE_INFINITY;
		}
	}
	if (dividedByZeroMsg.length() != 0) {
		System.out.println("DataSetControllerImpl.evaluateFunction(): DivideByZero " + dividedByZeroMsg);
	}

	PDEDataInfo pdeDataInfo = new PDEDataInfo(vcdID.getOwner(), vcdID.getID(), function.getName(), time, lastModified);
	return new SimDataBlock(pdeDataInfo, data, variableType);
}



public FieldDataFileOperationResults fieldDataFileOperation(FieldDataFileOperationSpec fieldDataFileOperationSpec)
	throws ObjectNotFoundException{

	if(fieldDataFileOperationSpec.opType == FieldDataFileOperationSpec.FDOS_COPYSIM){
		Vector<File> removeFilesIfErrorV = new Vector<File>();
		try{
			int simJobIndex = fieldDataFileOperationSpec.sourceSimParamScanJobIndex;
			//Determine style so file names can be constructed properly
			VCSimulationDataIdentifier sourceSimDataID =
				new VCSimulationDataIdentifier(
						new VCSimulationIdentifier(
								fieldDataFileOperationSpec.sourceSimDataKey,
								fieldDataFileOperationSpec.sourceOwner),
						simJobIndex);
			
			SimulationData simulationData = (SimulationData)getVCData(sourceSimDataID);
			boolean isOldStyle = (simulationData.getResultsInfoObject() instanceof VCSimulationDataIdentifierOldStyle);
			//
			//log,mesh,zip,func
			//
			KeyValue origSimKey = fieldDataFileOperationSpec.sourceSimDataKey;
			File meshFile_orig = simulationData.getMeshFile(false);
			File funcFile_orig = simulationData.getFunctionsFile(false);
			File subdomainFile_orig = simulationData.getSubdomainFile();
			File fdLogFile_orig = simulationData.getLogFile();
			File zipFile_orig = simulationData.getZipFile(false, 0);
			boolean bCopySubdomainFile = subdomainFile_orig.exists();
			//Dont' check subdomainFile_orig
			if(!(meshFile_orig.exists() && funcFile_orig.exists() && fdLogFile_orig.exists() && zipFile_orig.exists())){
				throw new RuntimeException("Couldn't find all of the files required to copy sim");
			}
	
			File userDir = getPrimaryUserDir(fieldDataFileOperationSpec.owner, true);
			File meshFile_new = new File(userDir,SimulationData.createCanonicalMeshFileName(fieldDataFileOperationSpec.specEDI.getKey(),0,false));
			File funcFile_new = new File(userDir,SimulationData.createCanonicalFunctionsFileName(fieldDataFileOperationSpec.specEDI.getKey(),0,false));
			File subdomainFile_new = new File(userDir,SimulationData.createCanonicalSubdomainFileName(fieldDataFileOperationSpec.specEDI.getKey(),0,false));
			File fdLogFile_new = new File(userDir,SimulationData.createCanonicalSimLogFileName(fieldDataFileOperationSpec.specEDI.getKey(),0,false));
			File zipFile_new = new File(userDir,SimulationData.createCanonicalSimZipFileName(fieldDataFileOperationSpec.specEDI.getKey(),0,0,false,false));
			if(meshFile_new.exists() || funcFile_new.exists() || fdLogFile_new.exists() || zipFile_new.exists() || (bCopySubdomainFile && subdomainFile_new.exists())){
				throw new RuntimeException("File names required for new Field Data already exist on server");
			}

			removeFilesIfErrorV.add(funcFile_new);
			removeFilesIfErrorV.add(meshFile_new);
			removeFilesIfErrorV.add(fdLogFile_new);
			//Simple copy of mesh and funcfile because they do not have to be changed
			FileUtils.copyFile(meshFile_orig, meshFile_new, false, false, 8*1024);
			FileUtils.copyFile(funcFile_orig, funcFile_new, false, false, 8*1024);
			if(bCopySubdomainFile){
				FileUtils.copyFile(subdomainFile_orig, subdomainFile_new, false, false, 8*1024);
			}
			
			//Copy Log file and replace original simID with ExternalDataIdentifier id
	        BufferedWriter writer = null;
	        try{
		        String origLog = FileUtils.readFileToString(fdLogFile_orig);
		        String newLogStr;
	        	String replace_new =
	        		SimulationData.createSimIDWithJobIndex(
	        				fieldDataFileOperationSpec.specEDI.getKey(),
	        				FieldDataFileOperationSpec.JOBINDEX_DEFAULT, false);
		        if(isOldStyle){
		        	String replace_orig =
		        		SimulationData.createSimIDWithJobIndex(origSimKey, 0, true);
			        newLogStr = origLog.replaceAll(replace_orig, replace_new);	        	
		        }else{
		        	String replace_orig =
		        		SimulationData.createSimIDWithJobIndex(
		        				origSimKey,
		        				fieldDataFileOperationSpec.sourceSimParamScanJobIndex, false);
			        newLogStr= origLog.replaceAll(replace_orig, replace_new);	        	
		        }
		        writer = new BufferedWriter(new FileWriter(fdLogFile_new));
		        writer.write(newLogStr);
		        writer.close();
	        }finally{
		        try{if(writer != null){writer.close();}}catch(Exception e){/*ignore*/};
	        }
	        
	        //
	        //Copy zip file and rename entries
	        //
	        int zipIndex = 0;
	        while(true){//Loop because there might be more than 1 zip file for large datasets
				zipFile_orig = simulationData.getZipFile(false, zipIndex);
				if(!zipFile_orig.exists()){
					//done
					break;
				}
				zipFile_new = new File(userDir,SimulationData.createCanonicalSimZipFileName(fieldDataFileOperationSpec.specEDI.getKey(),zipIndex,0,false,false));
				if(zipFile_new.exists()){
					throw new DataAccessException("new zipfile name "+zipFile_new.getAbsolutePath()+" already exists");
				}
				removeFilesIfErrorV.add(zipFile_new);
				
				ZipFile inZipFile = null;
		        InputStream zis = null;
		        ZipOutputStream zos = null;
		        try{
		        	inZipFile = new ZipFile(zipFile_orig);;
			        zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile_new)));
			        Enumeration<? extends ZipEntry> zipEntryEnum = inZipFile.getEntries();
			        while(zipEntryEnum.hasMoreElements()){
			        	ZipEntry zeIN = zipEntryEnum.nextElement();
			        	byte[] zdataIN = new byte[(int)zeIN.getSize()];
			        	int num = 0;
			        	int numTotal = 0;
			        	zis = new BufferedInputStream(inZipFile.getInputStream((ZipArchiveEntry) zeIN));
			    //long startTime = System.currentTimeMillis();
			        	while((num = zis.read(zdataIN, numTotal, zdataIN.length-numTotal)) != -1 && numTotal != zdataIN.length){
			        		numTotal+= num;
			        	}
			    //System.out.println("zipread time="+((System.currentTimeMillis()-startTime)/1000.0));
			        	zis.close();
			        	String newName;
			        	String replace_new =
			        		SimulationData.createSimIDWithJobIndex(
			        				fieldDataFileOperationSpec.specEDI.getKey(),
			        				FieldDataFileOperationSpec.JOBINDEX_DEFAULT, false);
				        if(isOldStyle){
				        	String replace_orig =
				        		SimulationData.createSimIDWithJobIndex(origSimKey, 0, true);
				        	newName = zeIN.getName().replaceAll(replace_orig, replace_new);	        	
				        }else{
				        	String replace_orig =
				        		SimulationData.createSimIDWithJobIndex(
				        				origSimKey,
				        				fieldDataFileOperationSpec.sourceSimParamScanJobIndex, false);
				        	newName = zeIN.getName().replaceAll(replace_orig, replace_new);	        	
				        }
			        	ZipEntry zeOUT = new ZipEntry(newName);
			        	zeOUT.setComment(zeIN.getComment());
			        	zeOUT.setCompressedSize(zeIN.getCompressedSize());
			        	zeOUT.setCrc(zeIN.getCrc());
			        	zeOUT.setExtra(zeIN.getExtra());
			        	zeOUT.setMethod(zeIN.getMethod());
			        	zeOUT.setSize(zeIN.getSize());
			        	zeOUT.setTime(zeIN.getTime());
//			    startTime = System.currentTimeMillis();
			        	zos.putNextEntry(zeOUT);
			        	zos.write(zdataIN, 0, zdataIN.length);
//				System.out.println("zipwrite time="+((System.currentTimeMillis()-startTime)/1000.0)+"\n");
			        }
		        }finally{
		        	try{if(zis != null){zis.close();}}catch(Exception e){/*ignore*/};
			        try{if(zos != null){zos.close();}}catch(Exception e){/*ignore*/};
		        }
		        zipIndex+= 1;
	        }
	        //Now see if we can read what we just wrote
			return fieldDataFileOperation(
					FieldDataFileOperationSpec.
						createInfoFieldDataFileOperationSpec(
							fieldDataFileOperationSpec.specEDI.getKey(),
							fieldDataFileOperationSpec.owner,
							FieldDataFileOperationSpec.JOBINDEX_DEFAULT));
		}catch(Exception e){
			e.printStackTrace();
			try{
				for(int i=0;i<removeFilesIfErrorV.size();i+= 1){
					removeFilesIfErrorV.elementAt(i).delete();
				}
			}catch(Throwable e2){
				//ignore, we tried to cleanup
			}
			throw new RuntimeException("Error copying sim data to new Field Data\n"+e.getMessage());
		}

	}else if(fieldDataFileOperationSpec.opType == FieldDataFileOperationSpec.FDOS_ADD){
		if(fieldDataFileOperationSpec.cartesianMesh == null){
			throw new RuntimeException("Field Data Operation 'ADD' cartesianMesh cannot be null");
		}
		if(fieldDataFileOperationSpec.times == null || fieldDataFileOperationSpec.times.length == 0){
			throw new RuntimeException("Field Data Operation 'ADD' times cannot be null");
		}
		if(fieldDataFileOperationSpec.times[0] != 0){
			throw new RuntimeException("Field Data Operation 'ADD' first time must be 0.0");
		}
		if(fieldDataFileOperationSpec.varNames == null || fieldDataFileOperationSpec.varNames.length == 0){
			throw new RuntimeException("Field Data Operation 'ADD' variable names cannot be null");
		}
		if((fieldDataFileOperationSpec.shortSpecData != null && fieldDataFileOperationSpec.doubleSpecData != null) ||
			(fieldDataFileOperationSpec.shortSpecData == null && fieldDataFileOperationSpec.doubleSpecData == null)){
			throw new RuntimeException("Field Data Operation 'ADD' must have ONLY 1 data specifier, short or double");			
		}
		if(fieldDataFileOperationSpec.shortSpecData != null && (
				fieldDataFileOperationSpec.shortSpecData.length != fieldDataFileOperationSpec.times.length ||
				fieldDataFileOperationSpec.shortSpecData[0].length != fieldDataFileOperationSpec.varNames.length
				)){
			throw new RuntimeException(
					"Field Data Operation 'ADD' 'short' data dimension does not match\n"+
					"times and variable names array lengths");
		}
		if(fieldDataFileOperationSpec.doubleSpecData != null && (
				fieldDataFileOperationSpec.doubleSpecData.length != fieldDataFileOperationSpec.times.length ||
				fieldDataFileOperationSpec.doubleSpecData[0].length != fieldDataFileOperationSpec.varNames.length
				)){
			throw new RuntimeException(
					"Field Data Operation 'ADD' 'double' data dimension does not match\n"+
					"times and variable names array lengths");
		}
		if(fieldDataFileOperationSpec.variableTypes == null || fieldDataFileOperationSpec.variableTypes.length == 0){
			throw new RuntimeException("Field Data Operation 'ADD' variable types cannot be null");
		}
		if(fieldDataFileOperationSpec.variableTypes.length != fieldDataFileOperationSpec.varNames.length){
			throw new RuntimeException("Field Data Operation 'ADD' variable types count does not match variable names count");
		}

		//byte[][][] allData = fieldDataFileOperationSpec.specData;
		double[] times = fieldDataFileOperationSpec.times;
		ExternalDataIdentifier dataset = fieldDataFileOperationSpec.specEDI;
		String[] vars = fieldDataFileOperationSpec.varNames;
		VariableType[] varTypes = fieldDataFileOperationSpec.variableTypes;
		File userDir = null;
		try{
			userDir = getPrimaryUserDir(fieldDataFileOperationSpec.owner, true);
		}catch(FileNotFoundException e){
			throw new RuntimeException("Couldn't create new user directory on server");
		}
		
		double[][][] convertedData = null;
		if(fieldDataFileOperationSpec.doubleSpecData != null){
			convertedData = fieldDataFileOperationSpec.doubleSpecData;
		}else{
			//convert short to double
			convertedData = new double[times.length][vars.length][];
			for(int i=0;i<times.length;i+= 1){
				for(int j=0;j<vars.length;j+= 1){
					if (fieldDataFileOperationSpec.shortSpecData!=null){
						convertedData[i][j] = new double[fieldDataFileOperationSpec.shortSpecData[i][j].length];
						for (int k = 0; k < fieldDataFileOperationSpec.shortSpecData[i][j].length; k += 1) {
							convertedData[i][j][k] = (double) (((int)fieldDataFileOperationSpec.shortSpecData[i][j][k]) & 0x0000FFFF);
						}
					}else{
						throw new RuntimeException("no pixel data found");
					}
				}
			}
		}

		//Write Log file
		File fdLogFile =
			new File(userDir,
					SimulationData.createCanonicalSimLogFileName(
							dataset.getKey(),0,false));
		PrintStream ps = null;
		File zipFile =
			new File(userDir,
					SimulationData.createCanonicalSimZipFileName(
							dataset.getKey(),0,0,false,false));
		Vector<String> simFileNamesV = new Vector<String>();
		try{
			if(!fdLogFile.createNewFile()){
				throw new Exception("File.createNewFile() returned null");
			}
			ps = new PrintStream(fdLogFile);
			for(int i=0;i<times.length;i+= 1){
				String simFilename =
					SimulationData.createCanonicalSimFilePathName(
							dataset.getKey(), i,0,false);
				simFileNamesV.add(simFilename);
				ps.println(i+"\t"+simFilename+"\t"+zipFile.getName()+"\t"+times[i]+"");
			}
			ps.flush();
		}catch(Exception e){
			throw new RuntimeException("Couldn't create log file "+fdLogFile.getAbsolutePath()+"\n"+e.getMessage());
		}finally{
			if(ps != null){ps.close();}
		}

		//Write zipFile
		java.util.zip.ZipOutputStream zipOut = null;
		try{
			java.io.BufferedOutputStream bout = new java.io.BufferedOutputStream(new java.io.FileOutputStream(zipFile));
			zipOut = new java.util.zip.ZipOutputStream(bout);
		    for(int t=0;t<times.length;t+= 1){
				java.io.File temp = java.io.File.createTempFile("temp",null);
				DataSet.writeNew(temp,vars,varTypes,fieldDataFileOperationSpec.isize,convertedData[t]);
				java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(simFileNamesV.get(t));
				zipOut.putNextEntry(zipEntry);
				//----------------------------
				java.io.BufferedInputStream in = new java.io.BufferedInputStream(new java.io.FileInputStream(temp));
				byte[] bytes = new byte[65536];
				try {
					int b = in.read(bytes);
					while (b != -1) {
						zipOut.write(bytes, 0, b);
						b = in.read(bytes);
					}
				}catch(IOException e){
					throw new Exception("Error writing zip file bytes");
				}finally {
					//cleanup
					in.close();
					temp.delete();
				}
				//----------------------------
		    }
		}catch(Exception e){
			throw new RuntimeException("Couldn't create zip file "+zipFile.getAbsolutePath()+"\n"+e.getMessage());
		}finally{
			try{
				zipOut.close();
			}catch(IOException e){
				e.printStackTrace();
				//ignore
			}
		}
		//Write Mesh file
		FileOutputStream fos = null;
		File meshFile = null;
		try {
			CartesianMesh mesh = fieldDataFileOperationSpec.cartesianMesh;
			 meshFile = new File(userDir,SimulationData.createCanonicalMeshFileName(fieldDataFileOperationSpec.specEDI.getKey(),0,false));
			fos = new FileOutputStream(meshFile);
			mesh.write(new PrintStream(fos));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error writing mesh file "+meshFile.getAbsolutePath()+"\n"+e.getMessage());
		}finally{
			try {
				if(fos != null){
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				//ignore
			}
		}
		
		//Write Functionfile file
		PrintStream ps2 = null;
		File funcFile = null;
		try {
			funcFile = new File(userDir,SimulationData.createCanonicalFunctionsFileName(fieldDataFileOperationSpec.specEDI.getKey(),0,false));
			FileOutputStream fos2 = new FileOutputStream(funcFile);
			ps2 =new PrintStream(fos2);
			ps2.println(
					"##---------------------------------------------"+"\n"+
					"##  "+funcFile.getAbsolutePath()+"\n"+
					"##---------------------------------------------"+"\n"
					);
			ps2.flush();
			ps2.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error writing function file "+funcFile.getAbsolutePath()+"\n"+e.getMessage());
		}finally{
			if(ps2 != null){
				ps2.close();
			}
		}
		
		try{
			//return Info
			return fieldDataFileOperation(
					FieldDataFileOperationSpec.
						createInfoFieldDataFileOperationSpec(
							fieldDataFileOperationSpec.specEDI.getKey(),
							fieldDataFileOperationSpec.owner,
							FieldDataFileOperationSpec.JOBINDEX_DEFAULT));
		}catch(Exception e){
			e.printStackTrace();
			//ignore
		}
		
	}else if(fieldDataFileOperationSpec.opType == FieldDataFileOperationSpec.FDOS_DEPENDANTFUNCS){
		throw new RuntimeException("This function is not currently used");
//		//
//		//Check for references to FieldData from users User Defined Functions
//		//
//		HashMap<String, KeyValue> dbFuncFileNamesAndSimKeys = null;
//		try{
//			dbFuncFileNamesAndSimKeys =
//				FieldDataDBOperationDriver.getFunctionFileNamesAndSimKeys(
//					fieldDataFileOperationSpec.specEDI.getOwner());
//		}catch(Exception e){
//			e.printStackTrace();
//			throw new RuntimeException("couldn't get Function File names from Database\n"+e.getMessage());
//		}
//		//String regex = "^.*"+MathMLTags.FIELD+"\\s*\\(\\s*"+fieldDataFileOperationSpec.specEDI.getName()+"\\s*,.*$";
//		String regex = "^.*?field\\s*\\(\\s*"+fieldDataFileOperationSpec.specEDI.getName()+"\\s*,.*?$";
//		java.util.regex.Pattern pattern =
//			java.util.regex.Pattern.compile(regex);//,java.util.regex.Pattern.MULTILINE|java.util.regex.Pattern.DOTALL);
//		Matcher matcher = pattern.matcher("");
//		Set<Map.Entry<String,KeyValue>> funcAndSimsES = dbFuncFileNamesAndSimKeys.entrySet();
//		Vector<FieldDataFileOperationResults.FieldDataReferenceInfo> referencingFuncFileDescription = 
//			new Vector<FieldDataFileOperationResults.FieldDataReferenceInfo>();
//		boolean bSearchSecondary =
//			secondaryRootDirectory != null &&
//			!primaryRootDirectory.equals(secondaryRootDirectory);
//		TreeSet<String> searchedFuncFilesTS = new TreeSet<String>();
//		Iterator<Map.Entry<String,KeyValue>> iter = funcAndSimsES.iterator();
//		FunctionFileGenerator.FuncFileLineInfo funcfileInfo = null;
//		while(iter.hasNext()){
//			Map.Entry<String,KeyValue> currentEntry = iter.next();
//			File currentFile = null;
//			for (int i = 0; i < (bSearchSecondary?2:1); i++) {
//				if(searchedFuncFilesTS.contains(currentEntry.getKey())){
//					continue;
//				}
//				currentFile = new File(
//					getUserDirectoryName(
//						(i==0?primaryRootDirectory:secondaryRootDirectory),
//						fieldDataFileOperationSpec.specEDI.getOwner()),currentEntry.getKey());
//				if(!currentFile.exists()){
//					continue;
//				}
//				searchedFuncFilesTS.add(currentEntry.getKey());
//				LineNumberReader lineNumberReader = null;
//				Vector<String> referringFieldfunctionNamesV = new Vector<String>();
//				try{
//					lineNumberReader = new LineNumberReader(new FileReader(currentFile));
//					String funcFileLine = null;
//					while((funcFileLine = lineNumberReader.readLine()) != null){
//						funcfileInfo = FunctionFileGenerator.readFunctionLine(funcFileLine);
//						if(funcfileInfo != null && funcfileInfo.functionExpr != null){
//							matcher.reset(funcfileInfo.functionExpr);
//							if(matcher.matches()){
//								referringFieldfunctionNamesV.add(funcfileInfo.functionName);
//							}
//						}
//					}
//					lineNumberReader.close();
//					if(referringFieldfunctionNamesV.size() > 0){
//						FieldDataFileOperationResults.FieldDataReferenceInfo fieldDataReferenceInfo =
//							FieldDataDBOperationDriver.getModelDescriptionForSimulation(
//								fieldDataFileOperationSpec.specEDI.getOwner(), currentEntry.getValue());
//						fieldDataReferenceInfo.funcNames = referringFieldfunctionNamesV.toArray(new String[0]);
//						referencingFuncFileDescription.add(fieldDataReferenceInfo);
////						for (int j = 0; j < referringFieldfunctionNamesV.size(); j++) {
////							referencingFuncFileDescription.add(new String[][] {
////								referringFieldfunctionNamesV.elementAt(j),modelDescription});							
////						}
//					}
//				}catch(Exception e){
//					e.printStackTrace();
//					throw new RuntimeException(e.getMessage(),e);
//				}finally{
//					if(lineNumberReader != null){try{lineNumberReader.close();}catch(Exception e){e.printStackTrace();}}
//				}
//			}
//		}
//		if(referencingFuncFileDescription.size() > 0){
//			FieldDataFileOperationResults fdfor = new FieldDataFileOperationResults();
//			fdfor.dependantFunctionInfo =
//				referencingFuncFileDescription.toArray(new FieldDataFileOperationResults.FieldDataReferenceInfo[0]);
//			return fdfor;
//		}
//		return null;
	}else if(fieldDataFileOperationSpec.opType == FieldDataFileOperationSpec.FDOS_DELETE){
//		if(fieldDataFileOperation(
//			FieldDataFileOperationSpec.
//				createDependantFuncsFieldDataFileOperationSpec(
//					fieldDataFileOperationSpec.specEDI)) != null){
//			throw new RuntimeException("Error: Delete failed, reference to FieldData '"+fieldDataFileOperationSpec.specEDI.getName()+"' found in Simulation Data function");
//		}
		//
		//Remove FieldData from caches
		//
		if(cacheTable0 != null){
			VCSimulationIdentifier vcSimID =
				new VCSimulationIdentifier(
					fieldDataFileOperationSpec.specEDI.getKey(),
					fieldDataFileOperationSpec.specEDI.getOwner());
			VCSimulationDataIdentifier simDataID =
				new VCSimulationDataIdentifier(
						vcSimID,
						FieldDataFileOperationSpec.JOBINDEX_DEFAULT);
			cacheTable0.removeAll(simDataID);
			cacheTable0.removeAll(fieldDataFileOperationSpec.specEDI);
		}
		if(userExtDataIDH != null){
			userExtDataIDH.remove(fieldDataFileOperationSpec.specEDI.getOwner());
		}
		
		SimulationData simulationData = null;
		try{
			simulationData = (SimulationData)getVCData(fieldDataFileOperationSpec.specEDI);
		}catch(Exception e){
			throw new ObjectNotFoundException(e.getMessage(),e);
		}
		File fdLogFile = simulationData.getLogFile();
		File fdMeshFile = simulationData.getMeshFile(false);
		File fdFunctionFile = simulationData.getFunctionsFile(true);
		File fdSubdomainFile = simulationData.getSubdomainFile();
		if(!fdLogFile.delete()){
			System.out.println("Couldn't delete log file "+fdLogFile.getAbsolutePath());
		}
		if(!fdMeshFile.delete()){
			System.out.println("Couldn't delete Mesh file "+fdMeshFile.getAbsolutePath());
		}
		if(!fdFunctionFile.delete()){
			System.out.println("Couldn't delete Functions file "+fdFunctionFile.getAbsolutePath());
		}
		if(fdSubdomainFile.exists() && fdSubdomainFile.delete()){
			System.out.println("Couldn't delete Subdomains file "+fdSubdomainFile.getAbsolutePath());
		}

		int index = 0;
		while(true){
			File fdZipFile = simulationData.getZipFile(false, index);
			if(index != 0 && !fdZipFile.exists()){
				break;
			}
			if(!fdZipFile.delete()){
				System.out.println("Couldn't delete zip file "+fdZipFile.getAbsolutePath());
			}
			index+= 1;
		}
		return null;
	}else if(fieldDataFileOperationSpec.opType == FieldDataFileOperationSpec.FDOS_INFO){
		try{
			FieldDataFileOperationResults fdor = new FieldDataFileOperationResults();
			VCDataIdentifier sourceSimDataID =
				new VCSimulationDataIdentifier(
					new VCSimulationIdentifier(
							fieldDataFileOperationSpec.sourceSimDataKey,
							fieldDataFileOperationSpec.sourceOwner),
				fieldDataFileOperationSpec.sourceSimParamScanJobIndex);
			fdor.dataIdentifierArr =
				getDataIdentifiers(null,sourceSimDataID);
			CartesianMesh mesh = getMesh(sourceSimDataID);
			fdor.extent = mesh.getExtent();
			fdor.origin = mesh.getOrigin();
			fdor.iSize = new ISize(mesh.getSizeX(),mesh.getSizeY(),mesh.getSizeZ());
			fdor.times = getDataSetTimes(sourceSimDataID);
			return fdor;
		}catch(FileNotFoundException e){
			throw new ObjectNotFoundException("Error FieldDataOp get INFO",e);
		}catch(Exception e){
			throw new RuntimeException("Error FieldDataFileOperationSpec INFO Operation\n"+e.getMessage());
		}
	}
	
	throw new RuntimeException("Field data operation "+fieldDataFileOperationSpec.opType+" unknown.");
}

private Vector<DataSetIdentifier> identifyDataDependencies(AnnotatedFunction function){
	//
	// identify data dependencies
	//
	
	Expression exp = function.getExpression();
	String dependentIDs[] = exp.getSymbols();
	Vector<DataSetIdentifier> dependencyList = new Vector<DataSetIdentifier>();
	//
	// get Dependents
	// variables are indexed by a number, t=0, x=1, y=2, z=3, a(i) = 4+i where a's are other variables
	//
	int varIndex = TXYZ_OFFSET;
	
	for (int i = 0; dependentIDs!=null && i < dependentIDs.length; i++) {
		SymbolTableEntry ste = exp.getSymbolBinding(dependentIDs[i]);
		if (ste instanceof DataSetIdentifier) {
			DataSetIdentifier dsi = (DataSetIdentifier) ste;
			dependencyList.addElement(dsi);
			dsi.setIndex(varIndex++);
		} else if (ste instanceof ReservedVariable) {
			ReservedVariable rv = (ReservedVariable) ste;
			if (rv.isTIME()) {
				rv.setIndex(0);
			} else if (rv.isX()) {
				rv.setIndex(1);
			} else if (rv.isY()) {
				rv.setIndex(2);
			} else if (rv.isZ()) {
				rv.setIndex(3);
			}
		}
	}
	
	return dependencyList;
}
/**
 * Insert the method's description here.
 * Creation date: (10/13/00 9:13:52 AM)
 * @return cbit.vcell.simdata.SimDataBlock
 * @param user cbit.vcell.server.User
 * @param simResults cbit.vcell.simdata.SimResults
 * @param function cbit.vcell.math.Function
 * @param time double
 */
private FunctionIndexes[] findFunctionIndexes(VCDataIdentifier vcdID,AnnotatedFunction function,int[] dataIndexes,OutputContext outputContext)
	throws ExpressionException, DataAccessException, IOException, MathException {

	if(function.getFunctionType().equals(VariableType.POSTPROCESSING)){
		FunctionIndexes[] fiArr = new FunctionIndexes[dataIndexes.length];
		Vector<DataSetIdentifier> dependencyList = identifyDataDependencies(function);
		SimulationData simData = (SimulationData)getVCData(vcdID);
		String[] varNames = new String[dependencyList.size()];
		String[] simFileVarNames = new String[dependencyList.size()];
		for (int i = 0; i < varNames.length; i++) {
			varNames[i] = dependencyList.get(i).getName();
			simFileVarNames[i] = dependencyList.get(i).getName();
		}
		CartesianMesh mesh = simData.getPostProcessingMesh(function.getName(), outputContext);
		int[][] varIndexes = new int[dataIndexes.length][varNames.length];
		for(int i=0;i<dataIndexes.length;i+= 1){
			for (int j = 0; j < varNames.length; j++) {
				varIndexes[i][j] = dataIndexes[i];
			}
		}
//		VolumeIndexNearFar[][] inside_near_far_indexes = null;//new VolumeIndexNearFar[dataIndexes.length][/*varNames.length*/];
//		VolumeIndexNearFar[][] outside_near_far_indexes = null;//new VolumeIndexNearFar[dataIndexes.length][/*varNames.length*/];
		for(int i=0;i<dataIndexes.length;i+= 1){
			fiArr[i] = new FunctionIndexes(function,mesh.getCoordinateFromVolumeIndex(dataIndexes[i]),varNames,simFileVarNames,varIndexes[i],null,null/*inside_near_far_indexes[i],outside_near_far_indexes[i]*/);
		}
		return fiArr;

	}
	
	VariableType variableType = function.getFunctionType();
	Vector<DataSetIdentifier> dependencyList = identifyDataDependencies(function);
	int varIndex = TXYZ_OFFSET+dependencyList.size();
	//
	// get Indexes and simFileNames
	//
	Coordinate initCoord = new Coordinate(0,0,0);
	Coordinate[] coords = new Coordinate[dataIndexes.length];
	for(int i=0;i<coords.length;i+= 1){
		coords[i] = initCoord;
	}
	String[] varNames = new String[varIndex-TXYZ_OFFSET];
	String[] simFileVarNames = new String[varNames.length];
	int[][] varIndexes = new int[dataIndexes.length][varNames.length];
	//New data needed for INSIDE-OUTSIDE interpolation
	VolumeIndexNearFar[][] inside_near_far_indexes = new VolumeIndexNearFar[dataIndexes.length][varNames.length];
	VolumeIndexNearFar[][] outside_near_far_indexes = new VolumeIndexNearFar[dataIndexes.length][varNames.length];
	
	CartesianMesh mesh = getMesh(vcdID);
	//
	// initialize argments to expression
	//
	for(int i=0;i<dataIndexes.length;i+= 1){
		coords[i] = mesh.getCoordinateFromVolumeIndex(dataIndexes[i]);

		if (variableType.equals(VariableType.VOLUME)){
			//coord = mesh.getCoordinateFromVolumeIndex(dataIndex);
			coords[i] = mesh.getCoordinateFromVolumeIndex(dataIndexes[i]);
			for (int j = 0; j < varIndex - TXYZ_OFFSET; j++) {
				DataSetIdentifier dsi = (DataSetIdentifier)dependencyList.elementAt(j);
				if (dsi.getVariableType().equals(VariableType.VOLUME)){
					varNames[j]=dsi.getName();
					simFileVarNames[j] = dsi.getName();
					varIndexes[i][j] = dataIndexes[i];
				}else if (dsi.getVariableType().equals(VariableType.VOLUME_REGION)){
					int volumeIndex = mesh.getVolumeRegionIndex(dataIndexes[i]);
					varNames[j]=dsi.getName();
					simFileVarNames[j] = dsi.getName();
					varIndexes[i][j] = volumeIndex;
				}
			}
		}else if (variableType.equals(VariableType.VOLUME_REGION)){
			for (int j = 0; j < varIndex - TXYZ_OFFSET; j++) {
				DataSetIdentifier dsi = (DataSetIdentifier)dependencyList.elementAt(j);
				if (dsi.getVariableType().equals(VariableType.VOLUME_REGION)){
					varNames[j]=dsi.getName();
					simFileVarNames[j] = dsi.getName();
					varIndexes[i][j] = dataIndexes[i];
				}
			}
		}else if (variableType.equals(VariableType.MEMBRANE)){
			//coord = mesh.getCoordinateFromMembraneIndex(dataIndex);
			coords[i] = mesh.getCoordinateFromMembraneIndex(dataIndexes[i]);
			for (int j = 0; j < varIndex - TXYZ_OFFSET; j++) {
				DataSetIdentifier dsi = (DataSetIdentifier)dependencyList.elementAt(j);
				if (dsi.getVariableType().equals(VariableType.VOLUME)){
					if (mesh.isChomboMesh())
					{
						// don't do any varname modifications here,
						// because chombo needs this info to decide
						// which data set to read, extrapolate values or solution.
						// if varname doesn't have _INSIDE or _OUTSIDE
						// add _INSIDE to varname to indicate it needs to read extrapolated values
						String varName = dsi.getName();
						if (!varName.endsWith(InsideVariable.INSIDE_VARIABLE_SUFFIX) && !varName.endsWith(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX))
						{
							varName += InsideVariable.INSIDE_VARIABLE_SUFFIX;
							// add this new varname to the list if it's not already there
							getVCData(vcdID).getEntry(varName); 
						}
						simFileVarNames[j] = varName;
						varIndexes[i][j] = dataIndexes[i];
					}
					else
					{
						if (dsi.getName().endsWith(InsideVariable.INSIDE_VARIABLE_SUFFIX)){		
							int volInsideIndex = mesh.getMembraneElements()[dataIndexes[i]].getInsideVolumeIndex();
							varNames[j]=dsi.getName();
							simFileVarNames[j] = dsi.getName().substring(0,dsi.getName().lastIndexOf("_"));
							varIndexes[i][j] = volInsideIndex;
							inside_near_far_indexes[i][j] = interpolateFindNearFarIndex(mesh, dataIndexes[i], true, false);
						}else if (dsi.getName().endsWith(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX)){					
							int volOutsideIndex = mesh.getMembraneElements()[dataIndexes[i]].getOutsideVolumeIndex();
							varNames[j]=dsi.getName();
							simFileVarNames[j] = dsi.getName().substring(0,dsi.getName().lastIndexOf("_"));
							varIndexes[i][j] = volOutsideIndex;
							outside_near_far_indexes[i][j] = interpolateFindNearFarIndex(mesh, dataIndexes[i], false, false);
						} else {	
							varNames[j]=dsi.getName();
							simFileVarNames[j] = dsi.getName();
							if(isDomainInside(mesh, dsi.getDomain(), dataIndexes[i])){
								inside_near_far_indexes[i][j] = interpolateFindNearFarIndex(mesh, dsi.getDomain(), dataIndexes[i], false);
								varIndexes[i][j] = inside_near_far_indexes[i][j].volIndexNear;
							}else{
								outside_near_far_indexes[i][j] = interpolateFindNearFarIndex(mesh, dsi.getDomain(), dataIndexes[i], false);
								varIndexes[i][j] = outside_near_far_indexes[i][j].volIndexNear;
							}
						}
					}
				}else if (dsi.getVariableType().equals(VariableType.VOLUME_REGION) && dsi.getName().endsWith(InsideVariable.INSIDE_VARIABLE_SUFFIX)){					
					int insideVolumeIndex = mesh.getMembraneElements()[dataIndexes[i]].getInsideVolumeIndex();
					int volRegionIndex = mesh.getVolumeRegionIndex(insideVolumeIndex);
					varNames[j]=dsi.getName();
					simFileVarNames[j] = dsi.getName().substring(0,dsi.getName().lastIndexOf("_"));
					varIndexes[i][j] = volRegionIndex;
					inside_near_far_indexes[i][j] = interpolateFindNearFarIndex(mesh, dataIndexes[i], true, true);
				}else if (dsi.getVariableType().equals(VariableType.VOLUME_REGION) && dsi.getName().endsWith(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX)){					
					int outsideVolumeIndex = mesh.getMembraneElements()[dataIndexes[i]].getOutsideVolumeIndex();
					int volRegionIndex = mesh.getVolumeRegionIndex(outsideVolumeIndex);
					varNames[j]=dsi.getName();
					simFileVarNames[j] = dsi.getName().substring(0,dsi.getName().lastIndexOf("_"));
					varIndexes[i][j] = volRegionIndex;
					outside_near_far_indexes[i][j] = interpolateFindNearFarIndex(mesh, dataIndexes[i], false, true);
				}else if (dsi.getVariableType().equals(VariableType.MEMBRANE)){
					varNames[j]=dsi.getName();
					simFileVarNames[j] = dsi.getName();
					varIndexes[i][j] = dataIndexes[i];
				}else if (dsi.getVariableType().equals(VariableType.MEMBRANE_REGION)){
					int memRegionIndex = mesh.getMembraneRegionIndex(dataIndexes[i]);
					varNames[j]=dsi.getName();
					simFileVarNames[j] = dsi.getName();
					varIndexes[i][j] = memRegionIndex;
				}
			}
		}else if (variableType.equals(VariableType.MEMBRANE_REGION)){
			for (int j = 0; j < varIndex - TXYZ_OFFSET; j++) {
				DataSetIdentifier dsi = (DataSetIdentifier)dependencyList.elementAt(j);
				if (dsi.getVariableType().equals(VariableType.VOLUME_REGION) && dsi.getName().endsWith(InsideVariable.INSIDE_VARIABLE_SUFFIX)){					
					//
					// find "inside" volume element index for first membrane element in MembraneRegion 'i'.
					//
					int insideVolumeIndex = -1;
					for (int k = 0; k < mesh.getMembraneElements().length; k++){
						if (mesh.getMembraneRegionIndex(k)==dataIndexes[i]){
							insideVolumeIndex = mesh.getMembraneElements()[k].getInsideVolumeIndex();
							break;
						}
					}
					int volRegionIndex = mesh.getVolumeRegionIndex(insideVolumeIndex);
					varNames[j]=dsi.getName();
					simFileVarNames[j] = dsi.getName().substring(0,dsi.getName().lastIndexOf("_"));
					varIndexes[i][j] = volRegionIndex;
					inside_near_far_indexes[i][j] = interpolateFindNearFarIndex(mesh, dataIndexes[i], true, true);;
				}else if (dsi.getVariableType().equals(VariableType.VOLUME_REGION) && dsi.getName().endsWith(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX)){					
					//
					// find "outside" volume element index for first membrane element in MembraneRegion 'i'.
					//
					int outsideVolumeIndex = -1;
					for (int k = 0; k < mesh.getMembraneElements().length; k++){
						if (mesh.getMembraneRegionIndex(k)==dataIndexes[i]){
							outsideVolumeIndex = mesh.getMembraneElements()[k].getOutsideVolumeIndex();
							break;
						}
					}
					int volRegionIndex = mesh.getVolumeRegionIndex(outsideVolumeIndex);
					varNames[j]=dsi.getName();
					simFileVarNames[j] = dsi.getName().substring(0,dsi.getName().lastIndexOf("_"));
					varIndexes[i][j] = volRegionIndex;
					outside_near_far_indexes[i][j] = interpolateFindNearFarIndex(mesh, dataIndexes[i], false, true);
				}else if (dsi.getVariableType().equals(VariableType.MEMBRANE)){
					varNames[j]=dsi.getName();
					simFileVarNames[j] = dsi.getName();
					varIndexes[i][j] = dataIndexes[i];
				}else if (dsi.getVariableType().equals(VariableType.MEMBRANE_REGION)){
					int memRegionIndex = mesh.getMembraneRegionIndex(dataIndexes[i]);
					varNames[j]=dsi.getName();
					simFileVarNames[j] = dsi.getName();
					varIndexes[i][j] = memRegionIndex;
				}
			}
		}
	}

	FunctionIndexes[] fiArr = new FunctionIndexes[dataIndexes.length];
	for(int i=0;i<dataIndexes.length;i+= 1){
		fiArr[i] = new FunctionIndexes(function,coords[i],varNames,simFileVarNames,varIndexes[i],inside_near_far_indexes[i],outside_near_far_indexes[i]/*,insideArgIndex,outsideArgIndex*/);
	}
	return fiArr;
	//
}


private Expression fieldFunctionSubstitution(OutputContext outputContext,final VCDataIdentifier vcdID, Expression functionExpression)
	throws ExpressionException, DataAccessException, IOException, MathException{
	
	SimResampleInfoProvider simResampleInfoProvider = null;
	Expression origExpression = new Expression(functionExpression);
	
	if(vcdID instanceof VCSimulationDataIdentifier){
		simResampleInfoProvider = ((VCSimulationDataIdentifier)vcdID);
	}else if(vcdID instanceof VCSimulationDataIdentifierOldStyle){
		simResampleInfoProvider = ((VCSimulationDataIdentifierOldStyle)vcdID);
	}else if(vcdID instanceof ExternalDataIdentifier){
		simResampleInfoProvider = ((ExternalDataIdentifier)vcdID);
	}else{
		return origExpression;
	}	
	
	FieldFunctionArguments[] fieldfuncArgumentsArr = FieldUtilities.getFieldFunctionArguments(origExpression);
	if(fieldfuncArgumentsArr == null || fieldfuncArgumentsArr.length == 0){
		return origExpression;
	}
	
	String[] origSymbols = origExpression.getSymbols();
	Vector<SymbolTableEntry> originalSymbolTablEntrryV = new Vector<SymbolTableEntry>();
	for (int i = 0; origSymbols!=null && i < origSymbols.length; i++) {
		if(!originalSymbolTablEntrryV.contains(origExpression.getSymbolBinding(origSymbols[i]))){
			originalSymbolTablEntrryV.add(origExpression.getSymbolBinding(origSymbols[i]));
		}
	}
	
	Expression exp = new Expression(origExpression);
	
	//
	//Handle Field Data Function field(...)
	//
	double[][] resampledFieldDatas = null;
	HashMap<String, Integer> substSymbolIndexH = new HashMap<String, Integer>();
//	if(fieldfuncArgumentsArr != null && fieldfuncArgumentsArr.length > 0){
		FieldDataIdentifierSpec[] fieldDataIdentifierSpecArr = getFieldDataIdentifierSpecs(fieldfuncArgumentsArr,simResampleInfoProvider.getOwner());
		//Substitute Field Data Functions for simple symbols for lookup-------
		for(int i=0;i<fieldfuncArgumentsArr.length;i+= 1){
			for (int j = 0; j < fieldDataIdentifierSpecArr.length; j++) {
				if(fieldfuncArgumentsArr[i].equals(fieldDataIdentifierSpecArr[j].getFieldFuncArgs())){
					String substFieldName =
						fieldfuncArgumentsArr[i].getFieldName()+"_"+
						fieldfuncArgumentsArr[i].getVariableName()+"_"+
						fieldfuncArgumentsArr[i].getTime().evaluateConstant();
					substFieldName = TokenMangler.fixTokenStrict(substFieldName);
					if(exp.hasSymbol(substFieldName)){
						throw new DataAccessException("Substitute Field data name is not unique");
					}
					String fieldFuncString =
						SimulationData.createCanonicalFieldFunctionSyntax(
							fieldDataIdentifierSpecArr[j].getExternalDataIdentifier().getName(),
							fieldfuncArgumentsArr[i].getVariableName(),
							fieldfuncArgumentsArr[i].getTime().evaluateConstant(),
							fieldfuncArgumentsArr[i].getVariableType().getTypeName());
					exp.substituteInPlace(new Expression(fieldFuncString), new Expression(substFieldName));
					substSymbolIndexH.put(substFieldName,i);
					break;
				}
			}
		}
		//----------------------------------------------------------------------

		boolean[] bResample = new boolean[fieldDataIdentifierSpecArr.length];
		Arrays.fill(bResample, true);
		writeFieldFunctionData(
				outputContext,
				fieldDataIdentifierSpecArr,
				bResample,
				getMesh(simResampleInfoProvider),
				simResampleInfoProvider,
				getMesh(simResampleInfoProvider).getNumMembraneElements(),
				FVSolverStandalone.HESM_KEEP_AND_CONTINUE);
		
		resampledFieldDatas = new double[fieldfuncArgumentsArr.length][];
		for(int i=0;i<fieldfuncArgumentsArr.length;i+= 1){
//			File resampledFile =
//				new File(getUserDir(vcsdID.getOwner()),
//					vcsdID.getID()+
//					FieldDataIdentifierSpec.getDefaultFieldDataFileNameForSimulation(fieldfuncArgumentsArr[i])
//				);
//			File resampledFile = new File(getPrimaryUserDir(simResampleInfoProvider.getOwner(), true),
//				SimulationData.createCanonicalResampleFileName(
//					simResampleInfoProvider, fieldfuncArgumentsArr[i]));
			File resampledFile = ((SimulationData)getVCData(simResampleInfoProvider)).getFieldDataFile(simResampleInfoProvider, fieldfuncArgumentsArr[i]);
			resampledFieldDatas[i] = DataSet.fetchSimData(fieldfuncArgumentsArr[i].getVariableName(),resampledFile);
		}
//	}
	
	//Rebind all the symbols
	String[] dependentIDs = exp.getSymbols();
	VariableSymbolTable varSymbolTable = new VariableSymbolTable();
	for (int i = 0; dependentIDs!=null && i < dependentIDs.length; i++) {
		SymbolTableEntry newSymbolTableEntry = null;
		for (int j = 0; j < originalSymbolTablEntrryV.size(); j++) {
			if(originalSymbolTablEntrryV.elementAt(j).getName().equals(dependentIDs[i])){
				newSymbolTableEntry = originalSymbolTablEntrryV.elementAt(j);
				break;
			}
		}
		if(newSymbolTableEntry == null ){
			if(substSymbolIndexH.containsKey(dependentIDs[i])){
				int resampledDataIndex = substSymbolIndexH.get(dependentIDs[i]).intValue();
				FieldDataParameterVariable fieldDataParameterVariable =
					new FieldDataParameterVariable(
							dependentIDs[i],
							resampledFieldDatas[resampledDataIndex]);
				newSymbolTableEntry = fieldDataParameterVariable;
			}
		}
		if(newSymbolTableEntry == null){
			throw new DataAccessException("Field Data Couldn't find substituted expression while evaluating function");
		}
		varSymbolTable.addVar(newSymbolTableEntry);
	}
	exp.bindExpression(varSymbolTable);
	
	return exp;
}

private void fireDataJobEventIfNecessary(
		VCDataJobID vcDataJobID,
		int argEventType,
		VCDataIdentifier argVCDataID,
		Double argProgress,
		TimeSeriesJobResults argTSJR,
		Exception argFailedJobExc){
	
	if(vcDataJobID == null || !vcDataJobID.isBackgroundTask()){
		return;
	}
	fireDataJobMessage_private(
		new DataJobEvent(
			vcDataJobID,
			argEventType,
			argVCDataID,
			argProgress,
			argTSJR,argFailedJobExc
		));
			
}
/**
 * Method to support listener events.
 */
private void fireDataJobMessage_private(DataJobEvent event) {
	if (aDataJobListener == null) {
		return;
	};
	int currentSize = aDataJobListener.size();
	DataJobListener tempListener = null;
	for (int index = 0; index < currentSize; index++){
		tempListener = (DataJobListener)aDataJobListener.elementAt(index);
		if (tempListener != null) {
			tempListener.dataJobMessage(event);
		};
	};
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String[]
 */
public DataIdentifier[] getDataIdentifiers(OutputContext outputContext, VCDataIdentifier vcdID) throws DataAccessException, IOException, FileNotFoundException {
	log.print("DataSetControllerImpl.getDataIdentifiers("+vcdID.getID()+")");

	VCData simData = getVCData(vcdID);
	//filter names with _INSIDE and _OUTSIDE
	DataIdentifier[] dataIdentifiersIncludingOutsideAndInside = simData.getVarAndFunctionDataIdentifiers(outputContext);
	Vector<DataIdentifier> v = new Vector<DataIdentifier>();
	for (int i = 0; i < dataIdentifiersIncludingOutsideAndInside.length; i++){
		DataIdentifier di = dataIdentifiersIncludingOutsideAndInside[i];
		if (!di.getName().endsWith(InsideVariable.INSIDE_VARIABLE_SUFFIX) && !di.getName().endsWith(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX)) {		
			if (di.getVariableType() == null || di.getVariableType().equals(VariableType.UNKNOWN)) {
				if (di.isFunction()) {
					AnnotatedFunction f = getFunction(outputContext,vcdID,di.getName());
					VariableType varType = getVariableTypeForFieldFunction(outputContext,vcdID, f);
					di = new DataIdentifier(di.getName(), varType, di.getDomain(), di.isFunction(), f.getDisplayName());
				}
			}		
			v.addElement(di);
		}
	}
	DataIdentifier[] ids = new DataIdentifier[v.size()];
	v.copyInto(ids);
	return ids;
}


/**
 * This method was created by a SmartGuide.
 * @return double[]
 */
public double[] getDataSetTimes(VCDataIdentifier vcdID) throws DataAccessException {
	try {
		VCData simData = getVCData(vcdID);
		double times[] = simData.getDataTimes();
		String timeString = "null";
		if (times!=null){
			timeString = String.valueOf(times[0]);
			if (times.length>1){
				timeString += "..."+times[times.length-1];
			}
		}
		log.print("DataSetControllerImpl.getDataSetTimes() returning ["+timeString+"]");
		return times;
	}catch (IOException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}

private FieldDataIdentifierSpec[] getFieldDataIdentifierSpecs(
		FieldFunctionArguments[] fieldFuncArgumentsArr,User user) throws DataAccessException{

	FieldDataIdentifierSpec[] fieldDataIdentifierSpecs = null;
	try{//Try without refreshing from the database
		fieldDataIdentifierSpecs =
			getFieldDataIdentifierSpecs_private(fieldFuncArgumentsArr,user,false);
	}catch(ObjectNotFoundException onfe){
		//Refresh from database and try once more
		fieldDataIdentifierSpecs =
			getFieldDataIdentifierSpecs_private(fieldFuncArgumentsArr,user,true);
	}
	return fieldDataIdentifierSpecs;
}

private FieldDataIdentifierSpec[] getFieldDataIdentifierSpecs_private(
FieldFunctionArguments[] fieldFuncArgumentsArr,User user,boolean bForceUpdate) throws DataAccessException{

	Vector<ExternalDataIdentifier> userExtDataIdentifiersV = userExtDataIDH.get(user);
	if(	userExtDataIdentifiersV == null  ||
		userExtDataIdentifiersV.size() < fieldFuncArgumentsArr.length ||
		bForceUpdate){
		//must refresh
		userExtDataIDH = FieldDataDBOperationDriver.getAllExternalDataIdentifiers();
		userExtDataIdentifiersV = userExtDataIDH.get(user);
	}
	FieldDataIdentifierSpec[] fieldDataIdentifierSpecs =
	new FieldDataIdentifierSpec[fieldFuncArgumentsArr.length];
	Arrays.fill(fieldDataIdentifierSpecs, null);
	for (int i = 0; i < fieldFuncArgumentsArr.length; i++) {
		for (int j = 0; userExtDataIdentifiersV != null && j < userExtDataIdentifiersV.size(); j++) {
			if(fieldFuncArgumentsArr[i].getFieldName().equals(userExtDataIdentifiersV.elementAt(j).getName())){
				fieldDataIdentifierSpecs[i] =
					new FieldDataIdentifierSpec(fieldFuncArgumentsArr[i],userExtDataIdentifiersV.elementAt(j));
				break;
			}
		}
		if(fieldDataIdentifierSpecs[i] == null){
			throw new ObjectNotFoundException(
				"The data locator for FieldData Function '"+fieldFuncArgumentsArr[i].getFieldName()+"' could not be found.");
		}
	}
	return fieldDataIdentifierSpecs;
}

public AnnotatedFunction getFunction(OutputContext outputContext,VCDataIdentifier vcdID,String variableName) throws DataAccessException{
	try {
		VCData vcData = getVCData(vcdID);
		AnnotatedFunction annotatedFunction = vcData.getFunction(outputContext,variableName);
		checkFieldDataExists(annotatedFunction,vcdID.getOwner());
		return annotatedFunction;
	}catch (IOException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}
/**
 * This method was created by a SmartGuide.
 * @return double[]
 */
public AnnotatedFunction[] getFunctions(OutputContext outputContext,VCDataIdentifier vcdID) throws DataAccessException,ExpressionBindingException {
	try {
		VCData simData = getVCData(vcdID);
		return simData.getFunctions(outputContext);
	}catch (IOException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}

private void checkFieldDataExists(AnnotatedFunction annotatedFunction,User user) throws DataAccessException{
	if(annotatedFunction == null){
		return;
	}
	FieldFunctionArguments[] fieldfuncArgs = FieldUtilities.getFieldFunctionArguments(annotatedFunction.getExpression());
	if(fieldfuncArgs != null && fieldfuncArgs.length > 0){
		FieldDataIdentifierSpec[] fieldDataIdentifierSpecs = getFieldDataIdentifierSpecs(fieldfuncArgs, user);
	}
}

private VariableType getVariableTypeForFieldFunction(OutputContext outputContext,VCDataIdentifier vcdID, AnnotatedFunction function) throws DataAccessException {
	VariableType funcType = function.getFunctionType(); 
	if (funcType == null || funcType.equals(VariableType.UNKNOWN)) {
		FieldFunctionArguments[] ffas  = FieldUtilities.getFieldFunctionArguments(function.getExpression());
		if (ffas == null || ffas.length == 0) {
			throw new DataAccessException("Unknown function type for function " + function.getName());
		}
		// use the type from the first field function
		FieldDataIdentifierSpec[] fdiss = getFieldDataIdentifierSpecs(ffas, vcdID.getOwner());
		SimDataBlock dataBlock = getSimDataBlock(outputContext,fdiss[0].getExternalDataIdentifier(), ffas[0].getVariableName(), 0.0);
		funcType = dataBlock.getVariableType();		
	}
	return funcType;
}

/**
 * This method was created by a SmartGuide.
 * @return cbit.plot.PlotData
 * @param varName java.lang.String
 * @param begin cbit.vcell.math.CoordinateIndex
 * @param end cbit.vcell.math.CoordinateIndex
 */
public PlotData getLineScan(OutputContext outputContext, VCDataIdentifier vcdID, String varName, double time, SpatialSelection spatialSelection) throws DataAccessException, MathException {
	try {
		if (spatialSelection == null){
			throw new IllegalArgumentException("null spatialSelection");
		}
		if (spatialSelection.isPoint()){
			throw new RuntimeException("'Point' spatialSelection not expected");
		}
		double dataTimes[] = getDataSetTimes(vcdID);
		if (dataTimes==null || dataTimes.length <= 0) {
			return null;
		}
		CartesianMesh mesh = getMesh(vcdID);
		//mesh is transient and is null if we got here by a serialized path (e.g. rmi)
		spatialSelection.setMesh(mesh);
		
		SimDataBlock simDataBlock = getSimDataBlock(outputContext,vcdID,varName,time);
		if (simDataBlock == null){
			return null;
		}
		DataIdentifier dataIdentifier = null;
		try {
			DataIdentifier dataIdentifiers[] = getDataIdentifiers(outputContext,vcdID);
			for (int i = 0; i < dataIdentifiers.length; i++){
				if (dataIdentifiers[i].getName().equals(varName)){
					dataIdentifier = dataIdentifiers[i];
				}
			}
		}catch (IOException e){
			throw new DataAccessException(e.getMessage());
		}

		double data[] = simDataBlock.getData();
		if (data == null) {
			return null;
		}
		
		if (spatialSelection instanceof SpatialSelectionVolume){

			SpatialSelectionVolume ssVolume = (SpatialSelectionVolume)spatialSelection;
			SpatialSelection.SSHelper ssvHelper = ssVolume.getIndexSamples(0.0,1.0);
			if (dataIdentifier.getVariableType().equals(VariableType.VOLUME)){
				ssvHelper.initializeValues_VOLUME(data);
			}else if(dataIdentifier.getVariableType().equals(VariableType.VOLUME_REGION)){
				ssvHelper.initializeValues_VOLUMEREGION(data);
			}else{
				throw new RuntimeException(SpatialSelectionVolume.class.getName()+" does not support variableType="+dataIdentifier.getVariableType());
			}
			try {
				if(ssvHelper.getMembraneIndexesInOut() != null && ssvHelper.getMembraneIndexesInOut().length > 0){
					adjustMembraneAdjacentVolumeValues(
							outputContext,
						new double[][] {ssvHelper.getSampledValues()},false,simDataBlock,
						ssvHelper.getSampledIndexes(),
						ssvHelper.getMembraneIndexesInOut(),
						vcdID,
						varName,
						mesh,
						new TimeInfo(vcdID,time,1,time,getDataSetTimes(vcdID)));//PostProcess never calls LineScan so send in VCell times
				}
			} catch (Exception e) {
				throw new DataAccessException("Error getLineScan adjustingMembraneValues\n"+e.getMessage(),e);
			}

			double[] values = ssvHelper.getSampledValues();
			if (mesh.isChomboMesh())
			{
				// convert NaN to 0
				for (int i = 0; i < values.length; i ++)
				{
					if (Double.isNaN(values[i]))
					{
						values[i] = 0;
					}
				}
			}
			return new PlotData(ssvHelper.getWorldCoordinateLengths(), values);
			
		}else if (spatialSelection instanceof SpatialSelectionContour){
			//
			// get length of span (in microns)
			//
			double lengthMicrons = spatialSelection.getLengthInMicrons();
			if (lengthMicrons <= 0) {
				return null;
			}
			int sizeScan;

			int[] sampleIndexes = null;
			double[] lineScan = null;
			double[] distance = null;
			
			SpatialSelectionContour ssContour = (SpatialSelectionContour)spatialSelection;
			sampleIndexes = ssContour.getIndexSamples();
			sizeScan = sampleIndexes.length;
			//
			// if contour region, must translate from Contour indexes to ContourRegion indexes
			//
			if (dataIdentifier.getVariableType().equals(VariableType.CONTOUR_REGION)){
				for (int i = 0; i < sampleIndexes.length; i++){
					sampleIndexes[i] = mesh.getContourRegionIndex(sampleIndexes[i]);
				}
			}
			lineScan = new double[sizeScan];
			distance = new double[sizeScan];
			for (int i=0;i<sizeScan;i++){
				lineScan[i] = data[sampleIndexes[i]];
				distance[i] = (((double) i) / (sizeScan - 1)) * lengthMicrons;
			}
			
			return new PlotData(distance, lineScan);

			
		}else if (spatialSelection instanceof SpatialSelectionMembrane){
			SpatialSelectionMembrane ssMembrane = (SpatialSelectionMembrane)spatialSelection;

			SpatialSelection.SSHelper ssmHelper = ssMembrane.getIndexSamples();
			if (dataIdentifier.getVariableType().equals(VariableType.MEMBRANE)){
				ssmHelper.initializeValues_MEMBRANE(data);
			}else if(dataIdentifier.getVariableType().equals(VariableType.MEMBRANE_REGION)){
				ssmHelper.initializeValues_MEMBRANEREGION(data);
			}else{
				throw new RuntimeException(SpatialSelectionMembrane.class.getName()+" does not support variableType="+dataIdentifier.getVariableType());
			}

			return new PlotData(ssmHelper.getWorldCoordinateLengths(),ssmHelper.getSampledValues());
			
		}else{
			throw new RuntimeException("unexpected SpatialSelection type "+spatialSelection.getClass().toString());
		}

	} catch (DataAccessException e) {
		log.exception(e);
		throw e;
	} catch (IOException e) {
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}

/**
 * This method was created in VisualAge.
 * @return CartesianMesh
 */
public CartesianMesh getMesh(VCDataIdentifier vcdID) throws DataAccessException, IOException, MathException {

	VCMongoMessage.sendTrace("DataSetControllerImpl.getMesh("+vcdID.getID()+")  <<EXIT>>");
	log.print("DataSetControllerImpl.getMesh("+vcdID.getOwner().getName()+","+vcdID.getID()+")");
	
	VCData simData = null;
	try {
		simData = getVCData(vcdID);
	}catch (DataAccessException e){
	}
	
	if (simData==null){
		throw new DataAccessException("no simResults for user "+vcdID.getOwner().getName()+" with simID="+vcdID.getID());
	}

	
	CartesianMesh mesh = simData.getMesh();
	
	if (mesh==null){
		log.alert("DataSetControllerImpl.getMesh(): creating dummy CartesianMesh");
		try {
			int size[] = simData.getVolumeSize();
			if (size==null){
				VCMongoMessage.sendTrace("DataSetControllerImpl.getMesh("+vcdID.getID()+")  <<EXIT size==null>>");
				return null;
			}
			VCMongoMessage.sendTrace("DataSetControllerImpl.getMesh("+vcdID.getID()+")  <<EXIT size not null but can't read>>");
			throw new RuntimeException("DataSetControllerImpl.getMesh(): size not null but couldn't read Mesh");
		}catch (Throwable e2){
			log.exception(e2);
			log.alert("DataSetControllerImpl.getMesh(): error creating dummy mesh: "+e2.getMessage());
			VCMongoMessage.sendTrace("DataSetControllerImpl.getMesh("+vcdID.getID()+")  <<EXIT null>>");
			return null;
		}
	}else{
		VCMongoMessage.sendTrace("DataSetControllerImpl.getMesh("+vcdID.getID()+")  <<EXIT non-null>>");
		return mesh;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/14/00 1:43:47 PM)
 * @return cbit.vcell.simdata.ODEDataBlock
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
public ODEDataBlock getODEDataBlock(VCDataIdentifier vcdID) throws DataAccessException {
	
	log.print("DataSetControllerImpl.getODEDataBlock()");

	try {
		//
		// check if already cached
		//
		VCData simData = getVCData(vcdID);
		ODEDataInfo odeDataInfo = new ODEDataInfo(vcdID.getOwner(), vcdID.getID(), simData.getDataBlockTimeStamp(ODE_DATA, 0));
		ODEDataBlock odeDataBlock = (cacheTable0 != null?cacheTable0.get(odeDataInfo):null);
		if (odeDataBlock != null){
			return odeDataBlock;
		}else{
			odeDataBlock = simData.getODEDataBlock();
			if (odeDataBlock != null){
//					cacheTable.put(odeDataInfo, odeDataBlock);
				if(cacheTable0 != null){
					try {
						cacheTable0.put(odeDataInfo, odeDataBlock);
					} catch (CacheException e) {
						// if can't cache the results, it is ok
						e.printStackTrace();
					}
				}
				return odeDataBlock;
			}else{
				String msg = "failure reading ODE data for " + vcdID.getOwner().getName() + "'s " + vcdID.getID();
				log.alert("DataSetControllerImpl.getODEDataBlock(): "+msg);
				throw new DataAccessException(msg);
			}
		}
	}catch (IOException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 * @return double[]
 * @param varName java.lang.String
 * @param time double
 */
public ParticleDataBlock getParticleDataBlock(VCDataIdentifier vcdID, double time) throws DataAccessException {
	log.print("DataSetControllerImpl.getParticleDataBlock(" + time + ")");

	try {
		//
		// check if already cached
		//
		VCData simData = getVCData(vcdID);
		ParticleDataInfo particleDataInfo = new ParticleDataInfo(vcdID.getOwner(), vcdID.getID(), time, simData.getDataBlockTimeStamp(PARTICLE_DATA, time));
		ParticleDataBlock particleDataBlock = (cacheTable0 != null?cacheTable0.get(particleDataInfo):null);
		if (particleDataBlock != null){
			return particleDataBlock;
		}else{
			particleDataBlock = simData.getParticleDataBlock(time);
			if (particleDataBlock != null){
//				cacheTable.put(particleDataInfo, particleDataBlock);
				if(cacheTable0 != null){
					try {
						cacheTable0.put(particleDataInfo, particleDataBlock);
					} catch (CacheException e) {
						// if can't cache the data, it is ok
						e.printStackTrace();
					}
				}
				return particleDataBlock;
			}else{
				String msg = "failure reading at t = " + time + " for " + vcdID.getOwner().getName() + "'s " + vcdID.getID();
				log.alert("DataSetControllerImpl.getParticleDataBlock(): "+msg);
				throw new DataAccessException(msg);
			}
		}
	}catch (IOException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean getParticleDataExists(VCDataIdentifier vcdID) throws DataAccessException, IOException, FileNotFoundException {
	VCMongoMessage.sendTrace("DataSetControllerImpl.getParticleDataExists("+vcdID.getID()+") ... <<ENTER>>");
	VCData simData = getVCData(vcdID);
	VCMongoMessage.sendTrace("DataSetControllerImpl.getParticleDataExists("+vcdID.getID()+") got VCData");
	boolean bParticleDataExists = simData.getParticleDataExists();
	VCMongoMessage.sendTrace("DataSetControllerImpl.getParticleDataExists("+vcdID.getID()+") ... <<EXIT>>");
	return bParticleDataExists;
}


/**
 * This method was created by a SmartGuide.
 * @return double[]
 * @param varName java.lang.String
 * @param time double
 */
public SimDataBlock getSimDataBlock(OutputContext outputContext, VCDataIdentifier vcdID, String varName, double time) throws DataAccessException {
	VCMongoMessage.sendTrace("DataSetControllerImpl.getSimDataBlock(" + varName + ", " + time + ")  <<ENTER>>");

	try {
		//
		// check if already cached for non-function variables
		//
		VCData simData = getVCData(vcdID);
		VCMongoMessage.sendTrace("DataSetControllerImpl.getSimDataBlock(" + varName + ", " + time + ") got VCData");
		long dataBlockTimeStamp = simData.getDataBlockTimeStamp(PDE_DATA, time);
		VCMongoMessage.sendTrace("DataSetControllerImpl.getSimDataBlock(" + varName + ", " + time + ") got dataBlockTimeStamp");
		PDEDataInfo pdeDataInfo = new PDEDataInfo(vcdID.getOwner(),vcdID.getID(),varName,time,dataBlockTimeStamp);
		SimDataBlock simDataBlock = null;
		AnnotatedFunction function = getFunction(outputContext,vcdID,varName);
		VCMongoMessage.sendTrace("DataSetControllerImpl.getSimDataBlock(" + varName + ", " + time + ") got function");
		if (function == null){
			simDataBlock = (cacheTable0 != null?cacheTable0.get(pdeDataInfo):null);
			if (simDataBlock == null) {
				simDataBlock = simData.getSimDataBlock(outputContext,varName,time);
				if (simDataBlock != null && dataCachingEnabled) {
//					cacheTable.put(pdeDataInfo,simDataBlock);
					if(cacheTable0 != null){
						try {
							cacheTable0.put(pdeDataInfo,simDataBlock);
						} catch (CacheException e) {
							// if can't cache the data, it is ok
							e.printStackTrace();
						}
					}
				}
			}				
		}else{
			if (simData instanceof SimulationData) {
				function = ((SimulationData)simData).simplifyFunction(function);
			}
			VCMongoMessage.sendTrace("DataSetControllerImpl.getSimDataBlock(" + varName + ", " + time + ") evaluating function");
			simDataBlock = evaluateFunction(outputContext,vcdID,simData,function,time);
		}
		CartesianMesh mesh = getMesh(vcdID);
		if (mesh != null && mesh.isChomboMesh()){
			for (int i = 0; i < simDataBlock.getData().length; i++) {
				if(simDataBlock.getData()[i] == 1.23456789e300){
					simDataBlock.getData()[i] = Double.NaN;
				}
			}
		}
		if (simDataBlock != null) {
			VCMongoMessage.sendTrace("DataSetControllerImpl.getSimDataBlock(" + varName + ", " + time + ")  <<EXIT-simDataBlock not null>>");
			return simDataBlock;
		} else {
			String msg = "failure reading "+varName+" at t="+time+" for "+vcdID.getOwner().getName()+"'s "+vcdID.getID();
			log.alert("DataSetControllerImpl.getDataBlockValues(): "+msg);
			VCMongoMessage.sendTrace("DataSetControllerImpl.getSimDataBlock(" + varName + ", " + time + ")  <<EXIT-Exception>>");
			throw new DataAccessException(msg);			
		}
	}catch (MathException e){
		log.exception(e);
		VCMongoMessage.sendTrace("DataSetControllerImpl.getSimDataBlock(" + varName + ", " + time + ")  <<EXIT-Exception>>");
		throw new DataAccessException(e.getMessage());
	}catch (IOException e){
		log.exception(e);
		VCMongoMessage.sendTrace("DataSetControllerImpl.getSimDataBlock(" + varName + ", " + time + ")  <<EXIT-Exception>>");
		throw new DataAccessException(e.getMessage());
	}catch (ExpressionException e){
		log.exception(e);
		VCMongoMessage.sendTrace("DataSetControllerImpl.getSimDataBlock(" + varName + ", " + time + ")  <<EXIT-Exception>>");
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2007 1:37:32 PM)
 * @return double[]
 */
private double[] getSpatialNeighborData(CartesianMesh mesh,int volumeIndex,int numArgs,double time,Vector<SimDataHolder> dataSetList,double[] args) {
	
	int regionIndex = mesh.getVolumeRegionIndex(volumeIndex);
	double[] spatialNeighborData = args;
	int argCount = numArgs;
	for(int i=0;i<12;i+= 1){
		int x = (i==0?-1:0)+(i==1?1:0)+(i==6?-2:0)+(i==7?2:0);
		int y = (i==2?-1:0)+(i==3?1:0)+(i==8?-2:0)+(i==9?2:0);
		int z = (i==4?-1:0)+(i==5?1:0)+(i==10?-2:0)+(i==11?2:0);
	//for(int z=-1;z<=1;z+=2){
		//for(int y=-1;y<=1;y+=2){
			//for(int x=-1;x<=1;x+=2){
				spatialNeighborData[argCount] = time;
				argCount+= 1;
				//
				CoordinateIndex ci = mesh.getCoordinateIndexFromVolumeIndex(volumeIndex);
				ci.x+= x;
				ci.y+= y;
				ci.z+= z;
				if( ci.x >= 0 && ci.x < mesh.getSizeX() &&
					ci.y >= 0 && ci.y < mesh.getSizeY() &&
					ci.z >= 0 && ci.z < mesh.getSizeZ()){//Inside boundary of data
					Coordinate coord = mesh.getCoordinate(ci);
					int neighborVolumeIndex = mesh.getVolumeIndex(ci);
					int neighborRegionIndex = mesh.getVolumeRegionIndex(neighborVolumeIndex);
					if(neighborRegionIndex == regionIndex){
						spatialNeighborData[argCount] = coord.getX();
						argCount+= 1;
						spatialNeighborData[argCount] = coord.getY();
						argCount+= 1;
						spatialNeighborData[argCount] = coord.getZ();
						argCount+= 1;
						for (int j = 0; j < numArgs - TXYZ_OFFSET; j++) {
							SimDataHolder simDataHolder = dataSetList.elementAt(j);
							if (simDataHolder.getVariableType().equals(VariableType.VOLUME)){
								spatialNeighborData[argCount] = simDataHolder.getData()[neighborVolumeIndex];
								argCount+= 1;
							}else{
								throw new RuntimeException("only VOLUME variables allowed in grad functions");
							}
						}
						continue;
					}
					//else{//Outside the current region, mark neighbor as undefined
						//for(int j=0;j<(numArgs-4);j+= 1){//four less because time and coordinate are inserted already
							//spatialNeighborData[argCount] = Double.NaN;
							//argCount+= 1;
						//}
					//}
				}
				//else{
				//Outside boundary of data, mark neighbor as undefined
				for(int j=0;j<(numArgs-1);j+= 1){//one less because time is inserted already
					spatialNeighborData[argCount] = Double.NaN;
					argCount+= 1;
				}
				//}
			//}
		//}
		//}
	}
	return spatialNeighborData;
}

private boolean hasGradient(Expression exp){
	boolean hasGradient = false;
	FunctionInvocation[] functionInvocations = exp.getFunctionInvocations(null);
	for (FunctionInvocation fi : functionInvocations){
		if (fi.getSymbolTableFunctionEntry() instanceof GradientFunctionDefinition){
			hasGradient = true;
		}
	}
	return hasGradient;
}

private TimeSeriesJobResults getSpecialTimeSeriesValues(OutputContext outputContext,VCDataIdentifier vcdID,
		TimeSeriesJobSpec timeSeriesJobSpec,TimeInfo timeInfo) throws Exception{
	
	String[] variableNames = timeSeriesJobSpec.getVariableNames();
	CartesianMesh mesh = getMesh(vcdID);
	//Automatically 'special' (non-optimized timedata retrieval) if isAllowOptimizedTimeDataRetrieval() == false
	boolean bIsSpecial = !isAllowOptimizedTimeDataRetrieval();
	if(!bIsSpecial){
		VCData simData = getVCData(vcdID);		

		//
		//Gradient and FieldData functions are special.
		//They have to be evaluated using the 'full data' method using evaluateFunction(...).
		//They cannot be evaluated using the fast findFunctionIndexes(...) method.
		//Also if the 'roi' is a significant fraction of the whole dataset then
		//the 'full data' method of evaluation is more efficient
		final double SIGNIFICANT_ROI_FRACTION = .2;//a guess for best efficiency
		final int ABSOLUTE_SIZE_LIMIT = 10000;
		for(int i=0;i<variableNames.length;i+= 1){
			DataSetIdentifier dsi = (DataSetIdentifier)simData.getEntry(variableNames[i]);
			if(dsi.getVariableType().equals(VariableType.VOLUME)){
				if(timeSeriesJobSpec.getIndices()[i].length >
				mesh.getNumVolumeElements()*SIGNIFICANT_ROI_FRACTION){
					bIsSpecial = true;
					break;
				}
			}else if(dsi.getVariableType().equals(VariableType.MEMBRANE)){
				if(timeSeriesJobSpec.getIndices()[i].length >
				mesh.getNumMembraneElements()*SIGNIFICANT_ROI_FRACTION){
					bIsSpecial = true;
					break;
				}
			}
			AnnotatedFunction functionFromVarName = getFunction(outputContext,vcdID,variableNames[i]);
			if(functionFromVarName != null){
				FieldFunctionArguments[] fieldFunctionArgumentsArr = FieldUtilities.getFieldFunctionArguments(functionFromVarName.getExpression());
				if(hasGradient(functionFromVarName.getExpression()) ||
					(fieldFunctionArgumentsArr != null && fieldFunctionArgumentsArr.length > 0)){
					bIsSpecial = true;
					break;
				}
				//check function absolute size limit
				Expression exp = functionFromVarName.getExpression();
				String[] funcSymbols = exp.getSymbols();
				int varCount = 0;
				if(funcSymbols != null){
					for (int j = 0; j < funcSymbols.length; j++) {
						SymbolTableEntry ste = exp.getSymbolBinding(funcSymbols[j]);
						if (ste instanceof DataSetIdentifier) {
							varCount+= 1;
						}
					}
				}
				varCount = Math.max(varCount, 1);
				if(varCount*timeSeriesJobSpec.getIndices()[i].length > ABSOLUTE_SIZE_LIMIT){
					bIsSpecial = true;
					break;
				}
			}else if(timeSeriesJobSpec.getIndices()[i].length > ABSOLUTE_SIZE_LIMIT){
				bIsSpecial = true;
				break;
			}
		}
	}
	if(!bIsSpecial){
		return null;
	}
	
	TimeSeriesJobResults tsjr = null;
	if(timeSeriesJobSpec.isCalcTimeStats()){
		throw new RuntimeException("Time Stats Not yet implemented for 'special' data");		
	}else if(timeSeriesJobSpec.isCalcSpaceStats()){//Get spatial statistics at each time point
		SpatialStatsInfo ssi = calcSpatialStatsInfo(outputContext,timeSeriesJobSpec, vcdID);
	    double[/*varName*/][/*time*/] argMin = new double[variableNames.length][timeInfo.desiredTimeValues.length];
	    double[][] argMax = new double[variableNames.length][timeInfo.desiredTimeValues.length];
	    double[][] argUnweightedMean = new double[variableNames.length][timeInfo.desiredTimeValues.length];
	    double[][] argWeightedMean = new double[variableNames.length][timeInfo.desiredTimeValues.length];
	    double[][] argUnweightedSum = new double[variableNames.length][timeInfo.desiredTimeValues.length];
	    double[][] argWeightedSum = new double[variableNames.length][timeInfo.desiredTimeValues.length];
	    double[/*varName*/] argTotalSpace = new double[variableNames.length];
	    
		double[][][] indicesForVarForOneTimepoint = new double[1][][];
		for(int varNameIndex =0;varNameIndex<variableNames.length;varNameIndex+= 1){
			int[] dataIndices = timeSeriesJobSpec.getIndices()[varNameIndex];
			indicesForVarForOneTimepoint[0] = new double[dataIndices.length + 1][1];
			for(int timeIndex=0;timeIndex<timeInfo.desiredTimeValues.length;timeIndex+= 1){
				indicesForVarForOneTimepoint[0][0] = new double[] {timeInfo.desiredTimeValues[timeIndex]};
				int num = varNameIndex*timeInfo.desiredTimeValues.length+timeIndex;
				int denom = variableNames.length*timeInfo.desiredTimeValues.length;
				double progressTime = 100.0 * (double)num / (double)denom;
				fireDataJobEventIfNecessary(
								timeSeriesJobSpec.getVcDataJobID(),
								MessageEvent.DATA_PROGRESS,
								vcdID,
								new Double(progressTime),
								null,null
							);
				SimDataBlock simDatablock = getSimDataBlock(outputContext,vcdID, variableNames[varNameIndex], timeInfo.desiredTimeValues[timeIndex]);
				double[] data = simDatablock.getData();
				//Put indices in format expected by calcStats (SHOULD BE CHANGED)
				for(int dataIndex=0;dataIndex<dataIndices.length;dataIndex+= 1){
					indicesForVarForOneTimepoint[0][dataIndex+1][0] = data[dataIndices[dataIndex]];
				}
				if(timeSeriesJobSpec.getCrossingMembraneIndices() != null && timeSeriesJobSpec.getCrossingMembraneIndices().length > 0){
					adjustMembraneAdjacentVolumeValues(
							outputContext,
						indicesForVarForOneTimepoint[0],true,simDatablock,
						timeSeriesJobSpec.getIndices()[varNameIndex],
						timeSeriesJobSpec.getCrossingMembraneIndices()[varNameIndex],
						vcdID,
						variableNames[varNameIndex],
						mesh,
						timeInfo);
				}
				tsjr = calculateStatisticsFromWhole(timeSeriesJobSpec, indicesForVarForOneTimepoint, indicesForVarForOneTimepoint[0][0], ssi);
				
				argMin[varNameIndex][timeIndex] = ((TSJobResultsSpaceStats)tsjr).getMinimums()[0][0];
				argMax[varNameIndex][timeIndex] = ((TSJobResultsSpaceStats)tsjr).getMaximums()[0][0];
				argUnweightedMean[varNameIndex][timeIndex] = ((TSJobResultsSpaceStats)tsjr).getUnweightedMean()[0][0];
				if(((TSJobResultsSpaceStats)tsjr).getWeightedMean()==null){
					argWeightedMean = null;
				}else{
					argWeightedMean[varNameIndex][timeIndex] = ((TSJobResultsSpaceStats)tsjr).getWeightedMean()[0][0];					
				}
				argUnweightedSum[varNameIndex][timeIndex] = ((TSJobResultsSpaceStats)tsjr).getUnweightedSum()[0][0];
				if(((TSJobResultsSpaceStats)tsjr).getWeightedSum() == null){
					argWeightedSum = null;
				}else{
					argWeightedSum[varNameIndex][timeIndex] = ((TSJobResultsSpaceStats)tsjr).getWeightedSum()[0][0];					
				}
				if(((TSJobResultsSpaceStats)tsjr).getTotalSpace() == null){
					argTotalSpace = null;
				}else{
					argTotalSpace[varNameIndex] = ((TSJobResultsSpaceStats)tsjr).getTotalSpace()[0];			
				}
			}
		}
		tsjr = new TSJobResultsSpaceStats(
	            timeSeriesJobSpec.getVariableNames(),
	            timeSeriesJobSpec.getIndices(),
	            timeInfo.desiredTimeValues,
	            argMin,argMax,
	            argUnweightedMean,
	            argWeightedMean,
	            argUnweightedSum,
	            argWeightedSum,
	            argTotalSpace);
	}else{//Get the values for for all the variables and indices
		double[][][] varIndicesTimesArr = new double[variableNames.length][][];
		for(int varNameIndex =0;varNameIndex<variableNames.length;varNameIndex+= 1){
			int[] dataIndices = timeSeriesJobSpec.getIndices()[varNameIndex];
			varIndicesTimesArr[varNameIndex] = new double[dataIndices.length + 1][timeInfo.desiredTimeValues.length];
			varIndicesTimesArr[varNameIndex][0] = timeInfo.desiredTimeValues;
			for(int timeIndex=0;timeIndex<timeInfo.desiredTimeValues.length;timeIndex+= 1){
				int num = varNameIndex*timeInfo.desiredTimeValues.length+timeIndex;
				int denom = variableNames.length*timeInfo.desiredTimeValues.length;
				double progressTime = 100.0 * (double)num / (double)denom;
				fireDataJobEventIfNecessary(
								timeSeriesJobSpec.getVcDataJobID(),
								MessageEvent.DATA_PROGRESS,
								vcdID,
								new Double(progressTime),
								null,null
							);
				SimDataBlock simDatablock = getSimDataBlock(outputContext,vcdID, variableNames[varNameIndex], timeInfo.desiredTimeValues[timeIndex]);
				double[] data = simDatablock.getData();
				for(int dataIndex=0;dataIndex<dataIndices.length;dataIndex+= 1){
					varIndicesTimesArr[varNameIndex][dataIndex+1][timeIndex] = data[dataIndices[dataIndex]];
				}
				if(timeSeriesJobSpec.getCrossingMembraneIndices() != null && timeSeriesJobSpec.getCrossingMembraneIndices().length > 0){
					adjustMembraneAdjacentVolumeValues(
							outputContext,
						varIndicesTimesArr[varNameIndex],true,simDatablock,
						timeSeriesJobSpec.getIndices()[varNameIndex],
						timeSeriesJobSpec.getCrossingMembraneIndices()[varNameIndex],
						vcdID,
						variableNames[varNameIndex],
						mesh,
						timeInfo);
				}
			}
		}
		tsjr = new TSJobResultsNoStats(
				timeSeriesJobSpec.getVariableNames(),
				timeSeriesJobSpec.getIndices(),
				timeInfo.desiredTimeValues,
				varIndicesTimesArr
				);
	}
	
	return tsjr;
}




private TimeSeriesJobResults getTimeSeriesValues_private(OutputContext outputContext, final VCDataIdentifier vcdID,final TimeSeriesJobSpec timeSeriesJobSpec) throws DataAccessException {

	double dataTimes[] = null;
	boolean isPostProcessing = false;
	try{
		if(getVCData(vcdID) instanceof SimulationData && ((SimulationData)getVCData(vcdID)).isPostProcessing(outputContext, timeSeriesJobSpec.getVariableNames()[0])){
			isPostProcessing = true;
			dataTimes = ((SimulationData)getVCData(vcdID)).getDataTimesPostProcess(outputContext);
		}
	}catch(Exception e){
		//ignore
		e.printStackTrace();
	}
	if(dataTimes == null){
		dataTimes =  getDataSetTimes(vcdID);
	}
	TimeInfo timeInfo = new TimeInfo(vcdID,timeSeriesJobSpec.getStartTime(),timeSeriesJobSpec.getStep(),timeSeriesJobSpec.getEndTime(),dataTimes);

//	boolean[] wantsTheseTimes = timeInfo.getWantsTheseTimes();
//	double[] desiredTimeValues = timeInfo.getDesiredTimeValues();
//	int desiredNumTimes = desiredTimeValues.length;
	
	if (dataTimes.length<=0){
		return null;
	}	

	boolean[] wantsTheseTimes = new boolean[dataTimes.length];
	double[] desiredTimeValues = null;
	int desiredNumTimes = 0;
	
	Arrays.fill(wantsTheseTimes,false);
	double[] tempTimes = new double[dataTimes.length];
	
	int stepCounter = 0;
	for(int i=0;i<dataTimes.length;i+= 1){
		if(dataTimes[i] > timeSeriesJobSpec.getEndTime()){
			break;
		}
		if(dataTimes[i] == timeSeriesJobSpec.getStartTime()){
			tempTimes[desiredNumTimes] = dataTimes[i];
			desiredNumTimes+= 1;
			stepCounter = 0;
			wantsTheseTimes[i] = true;
			if(timeSeriesJobSpec.getStep() == 0){
				break;
			}
		}else if(desiredNumTimes > 0 && stepCounter%timeSeriesJobSpec.getStep() == 0){
			tempTimes[desiredNumTimes] = dataTimes[i];
			desiredNumTimes+= 1;
			wantsTheseTimes[i] = true;
		}
		stepCounter+= 1;
	}
	if(desiredNumTimes == 0){
		throw new IllegalArgumentException("Couldn't find startTime "+timeSeriesJobSpec.getStartTime());
	}
	desiredTimeValues = new double[desiredNumTimes];
	System.arraycopy(tempTimes,0,desiredTimeValues,0,desiredNumTimes);
	
	//Check timeInfo
	if(desiredTimeValues.length != timeInfo.getDesiredTimeValues().length){
		throw new DataAccessException("timeInfo check failed");
	}
	for (int i = 0; i < desiredTimeValues.length; i++) {
		if(desiredTimeValues[i] != timeInfo.getDesiredTimeValues()[i]){
				throw new DataAccessException("timeInfo check failed");
		}
	}
	for (int i = 0; i < wantsTheseTimes.length; i++) {
		if(wantsTheseTimes[i] != timeInfo.getWantsTheseTimes()[i]){
				throw new DataAccessException("timeInfo check failed");
		}
	}
	
	try{
		timeSeriesJobSpec.initIndices(/*getMesh(vcdID).getNumVolumeElements()*/);

		//See if we need special processing
		TimeSeriesJobResults specialTSJR = getSpecialTimeSeriesValues(outputContext,vcdID,timeSeriesJobSpec,timeInfo);
		if(specialTSJR != null){
			return specialTSJR;
		}
		//
		VCData vcData = getVCData(vcdID);
		
		//
		//Determine Memory Usage for this job to protect server
		//
		final long MAX_MEM_USAGE = 20000000;//No TimeSeries jobs larger than this
		long memUsage = 0;
		boolean bHasFunctionVars = false;//efficient function stats are not yet implemented so check to adjust calculation
		for(int i=0;i<timeSeriesJobSpec.getVariableNames().length;i+= 1){
			bHasFunctionVars = bHasFunctionVars || (getFunction(outputContext,vcdID,timeSeriesJobSpec.getVariableNames()[i]) != null);
		}
		for(int i=0;i<timeSeriesJobSpec.getIndices().length;i+= 1){
			memUsage+= (timeSeriesJobSpec.isCalcSpaceStats() && !bHasFunctionVars ? NUM_STATS : timeSeriesJobSpec.getIndices()[i].length);
		}
		memUsage*= desiredNumTimes*8*2;
		System.out.println("DataSetControllerImpl.getTimeSeriesValues: job memory="+memUsage);
		if(memUsage > MAX_MEM_USAGE){
			throw new DataAccessException(
				"DataSetControllerImpl.getTimeSeriesValues: Job too large"+(bHasFunctionVars?"(has function vars)":"")+", requires approx. "+memUsage+
				" bytes of memory (only "+MAX_MEM_USAGE+" bytes allowed).  Choose fewer datapoints or times.");
		}
		//
		Vector<double[][]> valuesV = new Vector<double[][]>();
		SpatialStatsInfo spatialStatsInfo = null;
		if(timeSeriesJobSpec.isCalcSpaceStats()){
			spatialStatsInfo = calcSpatialStatsInfo(outputContext,timeSeriesJobSpec,vcdID);
		}
		final EventRateLimiter eventRateLimiter = new EventRateLimiter();
		for(int k=0;k<timeSeriesJobSpec.getVariableNames().length;k+= 1){
			double[][] timeSeries = null;
			String varName = timeSeriesJobSpec.getVariableNames()[k];
			int[] indices = timeSeriesJobSpec.getIndices()[k];
			if(timeSeriesJobSpec.isCalcSpaceStats() && !bHasFunctionVars){
				timeSeries = new double[NUM_STATS + 1][desiredNumTimes];
			}else{
				timeSeries = new double[indices.length + 1][desiredNumTimes];
			}
			timeSeries[0] = desiredTimeValues;
			ProgressListener progressListener = new ProgressListener(){
				public void updateProgress(double progress) {
					//System.out.println("Considering firing progress event at "+new Date());
					if (eventRateLimiter.isOkayToFireEventNow()){
						//System.out.println("ACTUALLY firing Progress event at "+new Date());
						fireDataJobEventIfNecessary(
								timeSeriesJobSpec.getVcDataJobID(),
								MessageEvent.DATA_PROGRESS,
								vcdID,
								new Double(progress),
								null,null
							);
					}
				}
				public void updateMessage(String message){
					//ignore
				}
			};
			AnnotatedFunction function = getFunction(outputContext,vcdID,varName);
			if(function != null){
				if (vcData instanceof SimulationData) {
					function = ((SimulationData)vcData).simplifyFunction(function);
				} else {
					throw new Exception("DataSetControllerImpl::getTimeSeriesValues_private(): has to be SimulationData to get time plot.");
				}
				MultiFunctionIndexes mfi = new MultiFunctionIndexes(vcdID,function,indices,wantsTheseTimes, progressListener,outputContext);
				for (int i=0;i<desiredTimeValues.length;i++){
					fireDataJobEventIfNecessary(
							timeSeriesJobSpec.getVcDataJobID(),
							MessageEvent.DATA_PROGRESS,
							vcdID,
							new Double(NumberUtils.formatNumber(
								100.0*(double)(k*desiredTimeValues.length+i)/
								(double)(timeSeriesJobSpec.getVariableNames().length*desiredTimeValues.length),3)),
							null,null
						);
					for (int j = 0; j < indices.length; j++){
						timeSeries[j + 1][i] = mfi.evaluateTimeFunction(outputContext,i,j);
					}
				}
			}else{
				double[][][] valuesOverTime = null;
				if(timeSeriesJobSpec.isCalcSpaceStats() && !bHasFunctionVars){
					valuesOverTime = vcData.getSimDataTimeSeries(outputContext,new String[] {varName},new int[][]{indices},wantsTheseTimes,spatialStatsInfo,progressListener);
				}else{
					valuesOverTime = vcData.getSimDataTimeSeries(outputContext,new String[] {varName},new int[][]{indices},wantsTheseTimes,progressListener);
				}
				for (int i=0;i<desiredTimeValues.length;i++){
					fireDataJobEventIfNecessary(
							timeSeriesJobSpec.getVcDataJobID(),
							MessageEvent.DATA_PROGRESS,
							vcdID,
							new Double(NumberUtils.formatNumber(
								100.0*(double)(k*desiredTimeValues.length+i)/
								(double)(timeSeriesJobSpec.getVariableNames().length*desiredTimeValues.length),3)),
							null,null
						);
					if(timeSeriesJobSpec.isCalcSpaceStats() && !bHasFunctionVars){
						timeSeries[MIN_OFFSET + 1][i] = valuesOverTime[i][0][MIN_OFFSET];// min
						timeSeries[MAX_OFFSET + 1][i] = valuesOverTime[i][0][MAX_OFFSET];// max
						timeSeries[MEAN_OFFSET + 1][i] = valuesOverTime[i][0][MEAN_OFFSET];// mean
						timeSeries[WMEAN_OFFSET + 1][i] = valuesOverTime[i][0][WMEAN_OFFSET];// wmean
						timeSeries[SUM_OFFSET + 1][i] = valuesOverTime[i][0][SUM_OFFSET];// sum
						timeSeries[WSUM_OFFSET + 1][i] = valuesOverTime[i][0][WSUM_OFFSET];// wsum
					}else{
						for (int j = 0; j < indices.length; j++){
							timeSeries[j + 1][i] = valuesOverTime[i][0][j];
						}
					}
				}
			}
			
			valuesV.add(timeSeries);
		}

		if(timeSeriesJobSpec.isCalcSpaceStats() && !bHasFunctionVars){
			double[][] min = new double[timeSeriesJobSpec.getVariableNames().length][desiredTimeValues.length];
			double[][] max = new double[timeSeriesJobSpec.getVariableNames().length][desiredTimeValues.length];
			double[][] mean = new double[timeSeriesJobSpec.getVariableNames().length][desiredTimeValues.length];
			double[][] wmean = new double[timeSeriesJobSpec.getVariableNames().length][desiredTimeValues.length];
			double[][] sum = new double[timeSeriesJobSpec.getVariableNames().length][desiredTimeValues.length];
			double[][] wsum = new double[timeSeriesJobSpec.getVariableNames().length][desiredTimeValues.length];
			for(int i=0;i<valuesV.size();i+= 1){
				double[][] timeStat = (double[][])valuesV.elementAt(i);
				for(int j=0;j<desiredTimeValues.length;j+= 1){
					min[i][j] = timeStat[MIN_OFFSET+1][j];
					max[i][j] = timeStat[MAX_OFFSET+1][j];
					mean[i][j] = timeStat[MEAN_OFFSET+1][j];
					wmean[i][j] = timeStat[WMEAN_OFFSET+1][j];
					sum[i][j] = timeStat[SUM_OFFSET+1][j];
					wsum[i][j] = timeStat[WSUM_OFFSET+1][j];
				}
			}
			return new TSJobResultsSpaceStats(
					timeSeriesJobSpec.getVariableNames(),
					timeSeriesJobSpec.getIndices(),
					desiredTimeValues,
					min,max,
					mean,
					(spatialStatsInfo.bWeightsValid?wmean:null),
					sum,
					(spatialStatsInfo.bWeightsValid?wsum:null),
					(spatialStatsInfo.bWeightsValid?spatialStatsInfo.totalSpace:null)
				);
		}else if(timeSeriesJobSpec.isCalcSpaceStats() && bHasFunctionVars){
			double[][][] timeSeriesFormatedValuesArr = new double[valuesV.size()][][];
			valuesV.copyInto(timeSeriesFormatedValuesArr);
			return calculateStatisticsFromWhole(timeSeriesJobSpec,timeSeriesFormatedValuesArr,desiredTimeValues,spatialStatsInfo);
		}else{
			double[][][] timeSeriesFormatedValuesArr = new double[valuesV.size()][][];
			valuesV.copyInto(timeSeriesFormatedValuesArr);
			TSJobResultsNoStats tsJobResultsNoStats =  new TSJobResultsNoStats(
	            timeSeriesJobSpec.getVariableNames(),
	            timeSeriesJobSpec.getIndices(),
	            desiredTimeValues,
	            timeSeriesFormatedValuesArr);
			if(!isPostProcessing && timeSeriesJobSpec.getCrossingMembraneIndices() != null && timeSeriesJobSpec.getCrossingMembraneIndices().length > 0){
				adjustMembraneAdjacentVolumeValues(
						outputContext,
						tsJobResultsNoStats.getTimesAndValuesForVariable(timeSeriesJobSpec.getVariableNames()[0]),
						true,null,
						timeSeriesJobSpec.getIndices()[0],
						timeSeriesJobSpec.getCrossingMembraneIndices()[0],
						vcdID,
						timeSeriesJobSpec.getVariableNames()[0],
						getMesh(vcdID),
						timeInfo
					);
			}
			return tsJobResultsNoStats;
		}
		
	}catch (DataAccessException e){
		log.exception(e);
		throw e;
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("DataSetControllerImpl.getTimeSeriesValues: "+(e.getMessage() == null?e.getClass().getName():e.getMessage()));
	}
}


public TimeSeriesJobResults getTimeSeriesValues(OutputContext outputContext,final VCDataIdentifier vcdID,final TimeSeriesJobSpec timeSeriesJobSpec) throws DataAccessException {

	fireDataJobEventIfNecessary(timeSeriesJobSpec.getVcDataJobID(), 
		MessageEvent.DATA_START, vcdID,	new Double(0), null, null);

	dataCachingEnabled = false;
	Exception failException = null;
	try{
		TimeSeriesJobResults timeSeriesJobResults = getTimeSeriesValues_private(outputContext,vcdID,timeSeriesJobSpec);
		fireDataJobEventIfNecessary(timeSeriesJobSpec.getVcDataJobID(),
			MessageEvent.DATA_COMPLETE, vcdID, new Double(100), timeSeriesJobResults, null);
		
		return timeSeriesJobResults;		
	}catch (Exception e) {
		failException = e;
		e.printStackTrace();
		if(e instanceof DataAccessException){
			throw (DataAccessException)e;
		}else{
			throw new DataAccessException(e.getClass().getName()+"\n"+e.getMessage());
		}
	}finally{
		dataCachingEnabled = true;
		if(failException != null){
			fireDataJobEventIfNecessary(timeSeriesJobSpec.getVcDataJobID(),
				MessageEvent.DATA_FAILURE, vcdID, new Double(0), null,failException);			
		}
	}
}

private static File getUserDirectoryName(File userParentDirectory,User user){
	return new File(userParentDirectory, user.getName());
}
/**
 * This method was created in VisualAge.
 * @return java.io.File
 * @param user cbit.vcell.server.User
 */
private File getPrimaryUserDir(User user, boolean bVerify) throws FileNotFoundException {
	File userDir = getUserDirectoryName(primaryRootDirectory, user);
	if (userDir.exists()){
		if (userDir.isDirectory()){
			return userDir;
		} else {
			throw new FileNotFoundException("file " + userDir.getPath() + " is not a directory");
		}
	}else{
		if (userDir.mkdir()){
			return userDir;
		} else if (bVerify) {
			throw new FileNotFoundException("cannot create directory "+userDir.getPath());
		}
	}
	return null;
}

private File getSecondaryUserDir(User user) throws FileNotFoundException {
	File userDir = getUserDirectoryName(secondaryRootDirectory, user);
	if (userDir.exists()){
		if (userDir.isDirectory()){
			return userDir;
		}else{
			throw new FileNotFoundException("file " + userDir.getPath() + " is not a directory");
		}
	}else{
		return null;
	}
}

/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.SimResults
 * @param user User
 * @param simID java.lang.String
 */
public VCData getVCData(VCDataIdentifier vcdID) throws DataAccessException, IOException {
	VCMongoMessage.sendTrace("DataSetControllerImpl.getVCData("+vcdID.getID()+") ... <<ENTER>>");

	VCData vcData = (cacheTable0 != null?cacheTable0.get(vcdID):null);
	//
	// check to see if cached version is compatible with current data
	//
	if (vcData==null){
		System.out.println("getVCData " + vcdID);
		if (vcdID instanceof MergedDataInfo) {
			try {
				User user = vcdID.getOwner();
				VCDataIdentifier[] vcdIdentifiers = ((MergedDataInfo)vcdID).getDataIDs();
				VCMongoMessage.sendTrace("DataSetControllerImpl.getVCData("+vcdID.getID()+") : creating new MergedData : <<BEGIN>>");
				vcData = new MergedData(user, getPrimaryUserDir(vcdID.getOwner(), false), getSecondaryUserDir(vcdID.getOwner()), this, vcdIdentifiers, ((MergedDataInfo)vcdID).getDataSetPrefix());
				VCMongoMessage.sendTrace("DataSetControllerImpl.getVCData("+vcdID.getID()+") : creating new MergedData : <<END>>");
			} catch (IOException e) {
				e.printStackTrace(System.out);
				throw new RuntimeException(e.getMessage());
			}
		} else {  // assume vcdID instanceof cbit.vcell.solver.SimulationInfo or a test adapter
			VCMongoMessage.sendTrace("DataSetControllerImpl.getVCData("+vcdID.getID()+") : creating new SimulationData : <<BEGIN>>");
			SimulationData.SimDataAmplistorInfo simDataAmplistorInfo = AmplistorUtils.getSimDataAmplistorInfoFromPropertyLoader();
			vcData = new SimulationData(vcdID, getPrimaryUserDir(vcdID.getOwner(), false), getSecondaryUserDir(vcdID.getOwner()),simDataAmplistorInfo);
			VCMongoMessage.sendTrace("DataSetControllerImpl.getVCData("+vcdID.getID()+") : creating new SimulationData : <<END>>");
		}
		if(cacheTable0 != null){
			try {
				VCMongoMessage.sendTrace("DataSetControllerImpl.getVCData("+vcdID.getID()+") : caching vcData : <<BEGIN>>");
				cacheTable0.put(vcdID,vcData);
				VCMongoMessage.sendTrace("DataSetControllerImpl.getVCData("+vcdID.getID()+") : caching vcData : <<END>>");
			} catch (CacheException e) {
				// if  can't cache the data, it is ok
				e.printStackTrace();
			}
		}
	}

	VCMongoMessage.sendTrace("DataSetControllerImpl.getVCData("+vcdID.getID()+") ... <<EXIT>>");
	return vcData;
}

private void adjustMembraneAdjacentVolumeValues(
		OutputContext outputContext,
		double[][] dataToAdjust,boolean bTimeFormat,SimDataBlock fullDataValueSource,
		int[] volumeDataIndexes,
		int[] membraneIndexesInOut,
		VCDataIdentifier vcdID,
		String varName,
		CartesianMesh mesh,
		TimeInfo timeInfo) throws Exception{
	
	if(membraneIndexesInOut == null || membraneIndexesInOut.length == 0){
		return;
	}
	if(bTimeFormat){
		if(dataToAdjust.length != volumeDataIndexes.length+1 || dataToAdjust[0].length != timeInfo.desiredTimeValues.length){
			throw new IllegalArgumentException(this.getClass().getName()+".adjustMembraneAdjacentVolumeValues array format wrong for time flag="+bTimeFormat);
		}
	}else{
		if(dataToAdjust.length != timeInfo.desiredTimeValues.length || dataToAdjust[0].length != volumeDataIndexes.length){
			throw new IllegalArgumentException(this.getClass().getName()+".adjustMembraneAdjacentVolumeValues array format wrong for time flag="+bTimeFormat);
		}
	}
	boolean bIsSpecial = false;
	AnnotatedFunction insideFunction = null;
	AnnotatedFunction outsideFunction = null;
	VCData vcData = getVCData(vcdID);
	DataIdentifier[] myDataIdentifers = getDataIdentifiers(outputContext,vcdID);
	for (int i = 0; i < myDataIdentifers.length; i++) {
		if(myDataIdentifers[i].getName().equals(varName)){
			if(myDataIdentifers[i].getVariableType().equals(VariableType.MEMBRANE)){
				throw new IllegalArgumentException(this.getClass().getName()+".adjustMembraneAdjacentVolumeValues Not for Membrane Variables");
			}
			Expression insideExp = null;
			Expression outsideExp = null;
			if(myDataIdentifers[i].isFunction()){
				AnnotatedFunction sourceFunction = null;
				AnnotatedFunction[] functionsArr = getFunctions(outputContext,vcdID);
				for (int j = 0; j < functionsArr.length; j++) {
					if(functionsArr[j].getName().equals(varName)){
						// need to subsitute and flatten the expression since we no longer store simplified expression.
						if (vcData instanceof SimulationData) {
							sourceFunction = ((SimulationData)vcData).simplifyFunction(functionsArr[j]);
						} else {
							throw new Exception("DataSetControllerImpl::getTimeSeriesValues_private(): has to be SimulationData to get time plot.");
						}						
						break;
					}
				}
				
				Vector<DataSetIdentifier> dependencyList = identifyDataDependencies(sourceFunction);
				insideExp = new Expression(sourceFunction.getExpression());
				outsideExp = new Expression(sourceFunction.getExpression());
				for (int j = 0; j < dependencyList.size(); j++) {
					insideExp.substituteInPlace(
							new Expression(dependencyList.elementAt(j).getName()),
							new Expression(dependencyList.elementAt(j).getName()+InsideVariable.INSIDE_VARIABLE_SUFFIX));
					outsideExp.substituteInPlace(
							new Expression(dependencyList.elementAt(j).getName()),
							new Expression(dependencyList.elementAt(j).getName()+OutsideVariable.OUTSIDE_VARIABLE_SUFFIX));
				}

			}else{
				insideExp = new Expression(varName+InsideVariable.INSIDE_VARIABLE_SUFFIX);
				outsideExp = new Expression(varName+OutsideVariable.OUTSIDE_VARIABLE_SUFFIX);
			}
			if(insideExp != null && outsideExp != null){
				insideExp.bindExpression(vcData);
				outsideExp.bindExpression(vcData);
				Domain domain = null; //TODO domain
				insideFunction = new AnnotatedFunction("",insideExp,domain,"",VariableType.MEMBRANE, FunctionCategory.PREDEFINED);
				outsideFunction = new AnnotatedFunction("",outsideExp,domain,"",VariableType.MEMBRANE,FunctionCategory.PREDEFINED);
				insideFunction.setExpression(insideExp.flatten());
				outsideFunction.setExpression(outsideExp.flatten());
				FieldFunctionArguments[] insideExpFieldFunctionArgs = FieldUtilities.getFieldFunctionArguments(insideExp);
				FieldFunctionArguments[] outsideExpFieldFunctionArgs = FieldUtilities.getFieldFunctionArguments(outsideExp);
				bIsSpecial =
					!isAllowOptimizedTimeDataRetrieval() || 
					hasGradient(insideExp) || hasGradient(outsideExp) ||
					(insideExpFieldFunctionArgs != null && insideExpFieldFunctionArgs.length > 0) ||
					(outsideExpFieldFunctionArgs != null && outsideExpFieldFunctionArgs.length > 0);
				if(bIsSpecial && fullDataValueSource == null){
					throw new IllegalArgumentException(this.getClass().getName()+".adjustMembraneAdjacentVolumeValues: special values need SimDataBlock");
				}
			}
			break;
		}
	}
	int crossingCount = 0;
	for (int j = 0; j < membraneIndexesInOut.length; j++) {
		if(membraneIndexesInOut[j] != -1){
			crossingCount+= 1;
		}
	}
	if(crossingCount > 0){
		int[] crossingCondensedIndexes = new int[crossingCount];
		int[] crossingCondensedOrigLocation = new int[crossingCount];
		crossingCount = 0;
		for (int j = 0; j < membraneIndexesInOut.length; j++) {
			if(membraneIndexesInOut[j] != -1){
				crossingCondensedOrigLocation[crossingCount] = j;
				crossingCondensedIndexes[crossingCount] = membraneIndexesInOut[j];
				crossingCount+= 1;
			}
		}
		MultiFunctionIndexes mfi_inside = null;
		MultiFunctionIndexes mfi_outside = null;
		for (int j = 0; j < crossingCount; j++) {
			double specialInsideVal = (bIsSpecial?interpolateVolDataValToMemb(mesh, crossingCondensedIndexes[j], fullDataValueSource, true, false):0);
			double specialOutsideVal = (bIsSpecial?interpolateVolDataValToMemb(mesh, crossingCondensedIndexes[j], fullDataValueSource, false, false):0);
			VolumeIndexNearFar vinf_inside =
				interpolateFindNearFarIndex(mesh, crossingCondensedIndexes[j], true, false);
			VolumeIndexNearFar vinf_outside =
				interpolateFindNearFarIndex(mesh, crossingCondensedIndexes[j], false, false);
			if(vinf_inside.volIndexNear == volumeDataIndexes[crossingCondensedOrigLocation[j]]){
				if(!bIsSpecial && mfi_inside == null){
					mfi_inside =
						new MultiFunctionIndexes(vcdID,insideFunction,crossingCondensedIndexes,timeInfo.wantsTheseTimes,null,outputContext);
				}
				for (int k = 0; k < timeInfo.desiredTimeValues.length; k++) {
					if(bTimeFormat){
						dataToAdjust[crossingCondensedOrigLocation[j]+1][k] = (bIsSpecial?specialInsideVal:mfi_inside.evaluateTimeFunction(outputContext,k,j));
					}else{
						dataToAdjust[k][crossingCondensedOrigLocation[j]] = (bIsSpecial?specialInsideVal:mfi_inside.evaluateTimeFunction(outputContext,k,j));
					}
				}
			}else if(vinf_outside.volIndexNear == volumeDataIndexes[crossingCondensedOrigLocation[j]]){
				if(!bIsSpecial && mfi_outside == null){
					mfi_outside =
						new MultiFunctionIndexes(vcdID,outsideFunction,crossingCondensedIndexes,timeInfo.wantsTheseTimes,null,outputContext);
				}
				for (int k = 0; k < timeInfo.desiredTimeValues.length; k++) {
					if(bTimeFormat){
						dataToAdjust[crossingCondensedOrigLocation[j]+1][k] = (bIsSpecial?specialOutsideVal:mfi_outside.evaluateTimeFunction(outputContext,k,j));
					}else{
						dataToAdjust[k][crossingCondensedOrigLocation[j]] = (bIsSpecial?specialOutsideVal:mfi_outside.evaluateTimeFunction(outputContext,k,j));
					}
				}
			}else{
				throw new Exception("couldn't match 'near' indexes");
			}
		}
	}else{
		throw new IllegalArgumentException("No non-null membrane crossing indexes found");
	}

}

/**
 * Add a cbit.vcell.desktop.controls.ExportListener.
 */
public void removeDataJobListener(DataJobListener djListener) {
	if (aDataJobListener != null) {
		aDataJobListener.remove(djListener);
	};
}


public void setAllowOptimizedTimeDataRetrieval(boolean bAllowOptimizedTimeDataRetrieval){
	this.bAllowOptimizedTimeDataRetrieval = bAllowOptimizedTimeDataRetrieval;
}
public boolean isAllowOptimizedTimeDataRetrieval(){
	return bAllowOptimizedTimeDataRetrieval;
}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 1:28:12 PM)
 * @throws FileNotFoundException 
 * @throws DataAccessException 
 */
public void writeFieldFunctionData(
		OutputContext outputContext,
		FieldDataIdentifierSpec[] argFieldDataIDSpecs,
		boolean[] bResampleFlags,
		CartesianMesh newMesh,
		SimResampleInfoProvider simResampleInfoProvider,
		int simResampleMembraneDataLength,
		int handleExistingResampleMode) throws FileNotFoundException, DataAccessException, IOException {
	
	if(	handleExistingResampleMode != FVSolverStandalone.HESM_KEEP_AND_CONTINUE &&
		handleExistingResampleMode != FVSolverStandalone.HESM_OVERWRITE_AND_CONTINUE &&
		handleExistingResampleMode != FVSolverStandalone.HESM_THROW_EXCEPTION
	){
		throw new IllegalArgumentException("Unknown mode "+handleExistingResampleMode);
	}
	
	if(argFieldDataIDSpecs == null || argFieldDataIDSpecs.length == 0){
		return;
	}
	
	HashMap<FieldDataIdentifierSpec, File> uniqueFieldDataIDSpecAndFileH = new HashMap<FieldDataIdentifierSpec, File>();
	HashMap<FieldDataIdentifierSpec, Boolean> bFieldDataResample = new HashMap<FieldDataIdentifierSpec, Boolean>();
	for (int i = 0; i < argFieldDataIDSpecs.length; i ++) {
		if (!uniqueFieldDataIDSpecAndFileH.containsKey(argFieldDataIDSpecs[i])){
			File newResampledFieldDataFile = null;
			try{
				newResampledFieldDataFile = ((SimulationData)getVCData(simResampleInfoProvider)).getFieldDataFile(simResampleInfoProvider, argFieldDataIDSpecs[i].getFieldFuncArgs());
			}catch(FileNotFoundException e){
				e.printStackTrace();
				//use the original way
				newResampledFieldDataFile = new File(getPrimaryUserDir(simResampleInfoProvider.getOwner(), true),
				SimulationData.createCanonicalResampleFileName(simResampleInfoProvider,argFieldDataIDSpecs[i].getFieldFuncArgs()));
			}
			if (handleExistingResampleMode == FVSolverStandalone.HESM_THROW_EXCEPTION && newResampledFieldDataFile.exists()){
				throw new RuntimeException("Resample Error: mode not allow overwrite or ignore of " +
						"existing file\n" + newResampledFieldDataFile.getAbsolutePath());
			}
			uniqueFieldDataIDSpecAndFileH.put(argFieldDataIDSpecs[i],newResampledFieldDataFile);
			bFieldDataResample.put(argFieldDataIDSpecs[i], bResampleFlags[i]);
		}
	}
	try {
		Set<Entry<FieldDataIdentifierSpec, File>> resampleSet = uniqueFieldDataIDSpecAndFileH.entrySet();
		Iterator<Entry<FieldDataIdentifierSpec, File>> resampleSetIter = resampleSet.iterator();
		while(resampleSetIter.hasNext()) {
			Entry<FieldDataIdentifierSpec, File> resampleEntry = resampleSetIter.next();
			if (handleExistingResampleMode == FVSolverStandalone.HESM_KEEP_AND_CONTINUE && resampleEntry.getValue().exists()){
				continue;
			}			
			FieldDataIdentifierSpec fieldDataIdSpec = resampleEntry.getKey();
			boolean bResample = bFieldDataResample.get(fieldDataIdSpec);
			CartesianMesh origMesh = getMesh(fieldDataIdSpec.getExternalDataIdentifier());
			SimDataBlock simDataBlock = getSimDataBlock(outputContext,fieldDataIdSpec.getExternalDataIdentifier(),fieldDataIdSpec.getFieldFuncArgs().getVariableName(), 
					fieldDataIdSpec.getFieldFuncArgs().getTime().evaluateConstant());
			VariableType varType = fieldDataIdSpec.getFieldFuncArgs().getVariableType();
			VariableType dataVarType = simDataBlock.getVariableType();
			if (!varType.equals(VariableType.UNKNOWN) && !varType.equals(dataVarType)) {
				throw new IllegalArgumentException("field function variable type (" + varType.getTypeName() + ") doesn't match real variable type (" + dataVarType.getTypeName() + ")");
			}
			double[] origData = simDataBlock.getData();
			double[] newData = null;
			CartesianMesh resampleMesh = newMesh;
			if (!bResample) {				
				if (resampleMesh.getGeometryDimension() != origMesh.getGeometryDimension()) {
					throw new DataAccessException("Field data " + fieldDataIdSpec.getFieldFuncArgs().getFieldName() + " (" + origMesh.getGeometryDimension() 
							+ "D) should have same dimension as simulation mesh (" + resampleMesh.getGeometryDimension() + "D) because it is not resampled to simulation mesh (e.g. Point Spread Function)");
				}
				newData = origData;
				resampleMesh = origMesh;
			} else {
				if (CartesianMesh.isSpatialDomainSame(origMesh, resampleMesh)){
					newData = origData;
					if (simDataBlock.getVariableType().equals(VariableType.MEMBRANE)){
						if (origData.length != simResampleMembraneDataLength){
							throw new Exception("FieldData variable \""+fieldDataIdSpec.getFieldFuncArgs().getVariableName()+
								"\" ("+simDataBlock.getVariableType().getTypeName()+") "+
								"resampling failed: Membrane Data lengths must be equal"
							);
						}
					} else if(!simDataBlock.getVariableType().equals(VariableType.VOLUME)){
						throw new Exception("FieldData variable \""+fieldDataIdSpec.getFieldFuncArgs().getVariableName()+
								"\" ("+simDataBlock.getVariableType().getTypeName()+") "+
								"resampling failed: Only Volume and Membrane variable types are supported"
						);
					}
				} else {
					if(!simDataBlock.getVariableType().compareEqual(VariableType.VOLUME)){
						throw new Exception("FieldData variable \""+fieldDataIdSpec.getFieldFuncArgs().getVariableName()+
								"\" ("+simDataBlock.getVariableType().getTypeName()+") "+
								"resampling failed: Only VOLUME FieldData variable type allowed when\n"+
								"FieldData spatial domain does not match Simulation spatial domain.\n"+
								"Check dimension, xsize, ysize, zsize, origin and extent are equal."
						);
					}
					if(origMesh.getSizeY() == 1 && origMesh.getSizeZ() == 1){
						newData = MathTestingUtilities.resample1DSpatialSimple(origData, origMesh, resampleMesh);						
					}else if(origMesh.getSizeZ() == 1){
						newData = MathTestingUtilities.resample2DSpatialSimple(origData, origMesh, resampleMesh);						
					}else{
						newData = MathTestingUtilities.resample3DSpatialSimple(origData, origMesh, resampleMesh);						
					}
				}
			}
			DataSet.writeNew(resampleEntry.getValue(),
					new String[] {fieldDataIdSpec.getFieldFuncArgs().getVariableName()},
					new VariableType[]{simDataBlock.getVariableType()},
					new ISize(resampleMesh.getSizeX(),resampleMesh.getSizeY(),resampleMesh.getSizeZ()),
					new double[][]{newData});
		}
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
		throw new DataAccessException(ex.getMessage());
	}
}


public DataSetMetadata getDataSetMetadata(VCDataIdentifier vcdataID) throws FileNotFoundException, DataAccessException, IOException {
	DataSetTimeSeries dataSetTimeSeries = getDataSetTimeSeries(vcdataID, null);
	DataSetMetadata dataSetMetadata = new DataSetMetadata(vcdataID,dataSetTimeSeries);
	return dataSetMetadata;
}



public DataSetTimeSeries getDataSetTimeSeries(VCDataIdentifier vcdataID, String[] variableNames) throws FileNotFoundException, DataAccessException, IOException {
	try {
		ODEDataBlock odeDatablock = getODEDataBlock(vcdataID);
		return new DataSetTimeSeries(vcdataID, odeDatablock);
	}catch (Exception e){
		System.err.println(e.getMessage());
		final DataOperationResults.DataProcessingOutputInfo dataProcessingOutputInfo = (DataOperationResults.DataProcessingOutputInfo)doDataOperation(new DataOperation.DataProcessingOutputInfoOP(vcdataID,true,null));
		DataSetTimeSeries.DataSetPostProcessData dataSetPostProcessData = new DataSetTimeSeries.DataSetPostProcessData(){
			@Override
			public double[] getTimes() {
				return dataProcessingOutputInfo.getVariableTimePoints();
			}
			@Override
			public String[] getVariableStatNames() {
				ArrayList<String> statNames = new ArrayList<String>();
				for (int i = 0; i < dataProcessingOutputInfo.getVariableNames().length; i++) {
					if(!dataProcessingOutputInfo.getPostProcessDataType(dataProcessingOutputInfo.getVariableNames()[i]).equals(DataOperationResults.DataProcessingOutputInfo.PostProcessDataType.statistic)){
						statNames.add(dataProcessingOutputInfo.getVariableNames()[i]);
					}
				}
				return statNames.toArray(new String[0]);
			}
			@Override
			public double[] getVariableStatValues(String varName) {
				return dataProcessingOutputInfo.getVariableStatValues().get(varName);
			}
		};
		return new DataSetTimeSeries(vcdataID, dataSetPostProcessData);
	}
}


public boolean getIsChombo(VCDataIdentifier vcdataID) throws IOException, DataAccessException {
	VCMongoMessage.sendTrace("DataSetControllerImpl.getIsChombo("+vcdataID.getID()+")  <<EXIT>>");
	log.print("DataSetControllerImpl.getMesh("+vcdataID.getOwner().getName()+","+vcdataID.getID()+")");
	
	VCData simData = null;
	try {
		simData = getVCData(vcdataID);
	}catch (DataAccessException e){
		e.printStackTrace(System.err);
	}
	
	if (simData==null){
		throw new DataAccessException("no simResults for user "+vcdataID.getOwner().getName()+" with simID="+vcdataID.getID());
	}

	return simData.isChombo();
}


public boolean getIsMovingBoundary(VCDataIdentifier vcdataID) throws IOException, DataAccessException {
	VCMongoMessage.sendTrace("DataSetControllerImpl.getIsMovingBoundary("+vcdataID.getID()+")  <<EXIT>>");
	log.print("DataSetControllerImpl.getIsMovingBoundary("+vcdataID.getOwner().getName()+","+vcdataID.getID()+")");
	
	VCData simData = null;
	try {
		simData = getVCData(vcdataID);
	}catch (DataAccessException e){
	}
	
	if (simData==null){
		throw new DataAccessException("no simResults for user "+vcdataID.getOwner().getName()+" with simID="+vcdataID.getID());
	}

	return simData.isMovingBoundary();
}


public ChomboFiles getChomboFiles(VCDataIdentifier vcdataID) throws DataAccessException, IOException {
	VCMongoMessage.sendTrace("DataSetControllerImpl.getChomboFiles("+vcdataID.getID()+")  <<EXIT>>");
	log.print("DataSetControllerImpl.getChomboFiles("+vcdataID.getOwner().getName()+","+vcdataID.getID()+")");
	
	VCData simData = getVCData(vcdataID);

	try {
		return simData.getChomboFiles();
	} catch (XmlParseException e) {
		e.printStackTrace();
		throw new DataAccessException("failed to getChomboFiles(): "+e.getMessage(),e);
	} catch (ExpressionException e) {
		e.printStackTrace();
		throw new DataAccessException("failed to getChomboFiles(): "+e.getMessage(),e);
	}
}


public VCellSimFiles getVCellSimFiles(VCDataIdentifier vcdataID) throws DataAccessException, IOException {
	VCMongoMessage.sendTrace("DataSetControllerImpl.getVCellSimFiles("+vcdataID.getID()+")  <<EXIT>>");
	log.print("DataSetControllerImpl.getVCellSimFiles("+vcdataID.getOwner().getName()+","+vcdataID.getID()+")");
	
	VCData simData = getVCData(vcdataID);

	return simData.getVCellSimFiles();
}

public MovingBoundarySimFiles getMovingBoundarySimFiles(VCDataIdentifier vcdataID) throws DataAccessException, IOException {
	VCMongoMessage.sendTrace("DataSetControllerImpl.getMovingBoundarySimFiles("+vcdataID.getID()+")  <<EXIT>>");
	log.print("DataSetControllerImpl.getMovingBoundarySimFiles("+vcdataID.getOwner().getName()+","+vcdataID.getID()+")");
	
	VCData simData = getVCData(vcdataID);

	return simData.getMovingBoundarySimFiles();
}



	private SimDataBlock getChomboExtrapolatedValues(VCDataIdentifier vcdID, String varName, double time) throws DataAccessException
	{
		final String methodName = "DataSetControllerImpl.getSimDataBlock(" + varName + ", " + time + ")";
		VCMongoMessage.sendTrace(methodName + " <<ENTER>>");

		try {
			//
			// check if already cached for non-function variables
			//
			VCData vcData = getVCData(vcdID);
			if (vcData == null || !(vcData instanceof SimulationData))
			{
				return null;
			}
			SimulationData simData = (SimulationData)vcData;
			VCMongoMessage.sendTrace(methodName + " got VCData");
			long dataBlockTimeStamp = simData.getDataBlockTimeStamp(PDE_DATA, time);
			VCMongoMessage.sendTrace(methodName + " got dataBlockTimeStamp");
			PDEDataInfo pdeDataInfo = new PDEDataInfo(vcdID.getOwner(), vcdID.getID(),varName,time,dataBlockTimeStamp);
			SimDataBlock simDataBlock = null;
			if (dataCachingEnabled && chomboExtrapolatedValuesCache == null)
			{
				chomboExtrapolatedValuesCache = new Cachetable(MessageConstants.MINUTE_IN_MS * 10);
			}
			simDataBlock = chomboExtrapolatedValuesCache.get(pdeDataInfo);
			if (simDataBlock == null) {
				VCMongoMessage.sendTrace(methodName + " read chombo extrapolated values");
				simDataBlock = simData.getChomboExtrapolatedValues(varName, time);
				if (simDataBlock != null && dataCachingEnabled) {
					if(chomboExtrapolatedValuesCache != null){
						try {
							chomboExtrapolatedValuesCache.put(pdeDataInfo,simDataBlock);
						} catch (CacheException e) {
							// if can't cache the data, it is ok
							e.printStackTrace();
						}
					}
				}
			}				
			
			if (simDataBlock != null) {
				VCMongoMessage.sendTrace(methodName + "  <<EXIT-Success>>");
				return simDataBlock;
			}
			
			String msg = "failure reading "+varName+" at t="+time+" for "+vcdID.getOwner().getName()+"'s "+vcdID.getID();
			log.alert(methodName +msg);
			throw new DataAccessException(msg);			
		} catch (Exception e){
			log.exception(e);
			VCMongoMessage.sendTrace(methodName + "  <<EXIT-Exception>>");
			throw new DataAccessException(e.getMessage());
		}
	}


	public VtuFileContainer getEmptyVtuMeshFiles(ChomboFiles chomboFiles, VCDataIdentifier vcdataID, int timeIndex) throws DataAccessException {
		try {
			if (timeIndex>0){
				throw new RuntimeException("only time index 0 supported for chombo vtk mesh files");
			}
			ChomboVtkFileWriter chomboVTKFileWriter = new ChomboVtkFileWriter();
			File primaryDirectory = getPrimaryUserDir(vcdataID.getOwner(), false);
			VtuFileContainer vtuFiles = chomboVTKFileWriter.getEmptyVtuMeshFiles(chomboFiles, primaryDirectory);
			return vtuFiles;
		}catch (Exception e){
			log.exception(e);
			throw new DataAccessException("failed to retrieve VTK files: "+e.getMessage(),e);
		}
	}
	
	public VtuFileContainer getEmptyVtuMeshFiles(VCellSimFiles vcellSimFiles, VCDataIdentifier vcdataID, int timeIndex) throws DataAccessException {
		if (timeIndex>0){
			throw new RuntimeException("only time index 0 supported for finitevolume vtk mesh files");
		}
		try {
			CartesianMeshVtkFileWriter cartesianMeshVTKFileWriter = new CartesianMeshVtkFileWriter();
			File primaryDirectory = PropertyLoader.getRequiredDirectory(PropertyLoader.primarySimDataDirProperty);
			VtuFileContainer vtuFiles = cartesianMeshVTKFileWriter.getEmptyVtuMeshFiles(vcellSimFiles, primaryDirectory);
			return vtuFiles;
		}catch (Exception e){
			log.exception(e);
			throw new DataAccessException("failed to retrieve VTK files: "+e.getMessage(),e);
		}
	}

	public VtuFileContainer getEmptyVtuMeshFiles(MovingBoundarySimFiles movingBoundarySimFiles, VCDataIdentifier vcdataID, int timeIndex) throws DataAccessException {
		try {
			MovingBoundaryVtkFileWriter movingBoundaryVtkFileWriter = new MovingBoundaryVtkFileWriter();
			File primaryDirectory = PropertyLoader.getRequiredDirectory(PropertyLoader.primarySimDataDirProperty);
			VtuFileContainer vtuFiles = movingBoundaryVtkFileWriter.getEmptyVtuMeshFiles(movingBoundarySimFiles, timeIndex, primaryDirectory);
			return vtuFiles;
		}catch (Exception e){
			log.exception(e);
			throw new DataAccessException("failed to retrieve VTK files: "+e.getMessage(),e);
		}
	}

	public double[] getVtuMeshData(ChomboFiles chomboFiles, OutputContext outputContext, VCDataIdentifier vcdataID, VtuVarInfo var, double time) throws DataAccessException {
		try {
			double[] times = getDataSetTimes(vcdataID);
			int timeIndex = -1;
			for (int i=0;i<times.length;i++){
				if (times[i] == time){
					timeIndex = i;
					break;
				}
			}
			if (timeIndex<0){
				throw new DataAccessException("data for dataset "+vcdataID+" not found at time "+time);
			}
			ChomboVtkFileWriter chomboVTKFileWriter = new ChomboVtkFileWriter();
			double[] vtuData = chomboVTKFileWriter.getVtuMeshData(chomboFiles, outputContext, getUserDataDirectory(vcdataID), time, var, timeIndex);
			return vtuData;
		}catch (Exception e){
			log.exception(e);
			throw new DataAccessException("failed to retrieve VTK files: "+e.getMessage(),e);
		}
	}
	
	public double[] getVtuMeshData(MovingBoundarySimFiles movingBoundarySimFiles, OutputContext outputContext, VCDataIdentifier vcdataID, VtuVarInfo var, double time) throws DataAccessException {
		try {
			double[] times = getDataSetTimes(vcdataID);
			int timeIndex = -1;
			for (int i=0;i<times.length;i++){
				if (times[i] == time){
					timeIndex = i;
					break;
				}
			}
			if (timeIndex<0){
				throw new DataAccessException("data for dataset "+vcdataID+" not found at time "+time);
			}
			//VCData simulationData = getVCData(vcdataID);
			SimDataBlock simDataBlock = getSimDataBlock(outputContext, vcdataID, var.name, time);
			double[] volumeVarData = simDataBlock.getData();
			MovingBoundaryVtkFileWriter movingBoundaryVtkFileWriter = new MovingBoundaryVtkFileWriter();
			double[] vtuData = movingBoundaryVtkFileWriter.getVtuMeshData(movingBoundarySimFiles, volumeVarData, getUserDataDirectory(vcdataID), var, timeIndex);
			return vtuData;
		}catch (Exception e){
			log.exception(e);
			throw new DataAccessException("failed to retrieve VTK files: "+e.getMessage(),e);
		}
	}
	
	
	private File getUserDataDirectory(VCDataIdentifier vcdataID){
		File primaryDirectory = PropertyLoader.getRequiredDirectory(PropertyLoader.primarySimDataDirProperty);
		return new File(primaryDirectory,vcdataID.getOwner().getName());
	}


	public double[] getVtuMeshData(VCellSimFiles vcellFiles, OutputContext outputContext, VCDataIdentifier vcdataID, VtuVarInfo var, double time) throws DataAccessException {
		try {
			CartesianMeshVtkFileWriter cartesianMeshVTKFileWriter = new CartesianMeshVtkFileWriter();
			SimDataBlock simDataBlock = getSimDataBlock(outputContext, vcdataID, var.name, time);
			double[] vtuData = cartesianMeshVTKFileWriter.getVtuMeshData(vcellFiles, outputContext, simDataBlock, getUserDataDirectory(vcdataID), var, time);
			return vtuData;
		}catch (Exception e){
			log.exception(e);
			throw new DataAccessException("failed to retrieve VTK files: "+e.getMessage(),e);
		}
	}


	public VtuVarInfo[] getVtuVarInfos(VCellSimFiles vcellFiles, OutputContext outputContext, VCDataIdentifier vcdataID) throws DataAccessException {
		try {
			CartesianMeshVtkFileWriter cartesianMeshVTKFileWriter = new CartesianMeshVtkFileWriter();
			VtuVarInfo[] vtuVarInfos = cartesianMeshVTKFileWriter.getVtuVarInfos(vcellFiles, outputContext, getVCData(vcdataID));
			return vtuVarInfos;
		}catch (Exception e){
			log.exception(e);
			throw new DataAccessException("failed to retrieve VTK variable list: "+e.getMessage(),e);
		}
	}


	public VtuVarInfo[] getVtuVarInfos(ChomboFiles chomboFiles, OutputContext outputContext, VCDataIdentifier vcdataID) throws DataAccessException {
		try {
			ChomboVtkFileWriter chomboVTKFileWriter = new ChomboVtkFileWriter();
			VtuVarInfo[] vtuVarInfos = chomboVTKFileWriter.getVtuVarInfos(chomboFiles, outputContext, getVCData(vcdataID));
			return vtuVarInfos;
		}catch (Exception e){
			log.exception(e);
			throw new DataAccessException("failed to retrieve VTK variable list: "+e.getMessage(),e);
		}
	}

	public VtuVarInfo[] getVtuVarInfos(MovingBoundarySimFiles movingBoundaryFiles, OutputContext outputContext, VCDataIdentifier vcdataID) throws DataAccessException {
		try {
			DataIdentifier[] dataIdentifiers = getDataIdentifiers(outputContext, vcdataID);
			if (dataIdentifiers==null){
				return null;
			}
			ArrayList<VtuVarInfo> vtuVarInfos = new ArrayList<VtuVarInfo>();
			for (DataIdentifier di : dataIdentifiers){
				String name = di.getName();
				String displayName = di.getDisplayName();
				if (di.getDomain() != null){
					System.err.println("DataSetControllerImpl.getVtuVarInfos(movingboundary): need to support proper domain names now");
				}
				String domainName = MovingBoundaryReader.getFakeInsideDomainName();
				VariableDomain variableDomain = null;
				VariableType variableType = di.getVariableType();
				if (variableType.equals(VariableType.VOLUME) || variableType.equals(VariableType.VOLUME_REGION)){
					variableDomain = VariableDomain.VARIABLEDOMAIN_VOLUME;
				}else if (variableType.equals(VariableType.MEMBRANE) || variableType.equals(VariableType.MEMBRANE_REGION)){
					variableDomain = VariableDomain.VARIABLEDOMAIN_MEMBRANE;
				}else if (variableType.equals(VariableType.POINT_VARIABLE)){
					variableDomain = VariableDomain.VARIABLEDOMAIN_POINT;
				}else if (variableType.equals(VariableType.CONTOUR) || variableType.equals(VariableType.CONTOUR_REGION)){
					variableDomain = VariableDomain.VARIABLEDOMAIN_CONTOUR;
				}else if (variableType.equals(VariableType.NONSPATIAL)){
					variableDomain = VariableDomain.VARIABLEDOMAIN_UNKNOWN;
				}else if (variableType.equals(VariableType.POSTPROCESSING)){
					variableDomain = VariableDomain.VARIABLEDOMAIN_POSTPROCESSING;
				}else{
					System.err.print("skipping var "+di+", unsupported data type");
				}
				String functionExpression = null;
				boolean bMeshVariable = false;
if (name.toUpperCase().contains("SIZE")){
	System.err.println("Skipping Moving Boundary variable '"+name+"' because it is a size ... change later");
	continue;
}
				vtuVarInfos.add(new VtuVarInfo(name, displayName, domainName, variableDomain, functionExpression, bMeshVariable));
			}
			return vtuVarInfos.toArray(new VtuVarInfo[0]);
		}catch (Exception e){
			log.exception(e);
			throw new DataAccessException("failed to retrieve VTK variable list: "+e.getMessage(),e);
		}
	}


}
