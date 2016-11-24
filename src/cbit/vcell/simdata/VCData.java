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
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.vis.io.ChomboFiles;
import org.vcell.vis.io.ComsolSimFiles;
import org.vcell.vis.io.MovingBoundarySimFiles;
import org.vcell.vis.io.VCellSimFiles;

import cbit.vcell.math.MathException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.DataSetControllerImpl.ProgressListener;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.xml.XmlParseException;
/**
 * This type was created in VisualAge.
 */
public abstract class VCData implements SimDataConstants,SymbolTable {
	protected static Logger LG = Logger.getLogger(VCData.class);

/**
 * SimResults constructor comment.
 */
protected VCData() {
}

public abstract SymbolTableEntry getEntry(String identifier);

/**
 * Insert the method's description here.
 * Creation date: (1/19/00 11:52:22 AM)
 * @return long
 * @param dataType int
 * @param timepoint double
 * @exception org.vcell.util.DataAccessException The exception description.
 */
public abstract long getDataBlockTimeStamp(int dataType, double timepoint) throws DataAccessException;


/**
 * This method was created in VisualAge.
 * @return double[]
 */
public abstract double[] getDataTimes() throws DataAccessException;

public abstract boolean isChombo() throws DataAccessException, IOException;
/**
 * Insert the method's description here.
 * Creation date: (10/11/00 5:16:06 PM)
 * @return cbit.vcell.math.Function
 * @param name java.lang.String
 */
public abstract AnnotatedFunction getFunction(OutputContext outputContext,String identifier);

/**
 * Insert the method's description here.
 * Creation date: (10/11/00 5:16:06 PM)
 * @return cbit.vcell.math.Function
 * @param name java.lang.String
 */
public abstract AnnotatedFunction[] getFunctions(OutputContext outputContext);


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.solvers.CartesianMesh
 */
public abstract CartesianMesh getMesh() throws DataAccessException, MathException;


/**
 * Insert the method's description here.
 * Creation date: (1/14/00 2:28:47 PM)
 * @return cbit.vcell.simdata.ODEDataBlock
 * @throws IOException 
 */
public abstract ODEDataBlock getODEDataBlock() throws DataAccessException, IOException;


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.ParticleDataBlock
 * @param double time
 */
public abstract ParticleDataBlock getParticleDataBlock(double time) throws DataAccessException, IOException;


/**
 * This method was created in VisualAge.
 * @return boolean
 */
public abstract boolean getParticleDataExists() throws DataAccessException;


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.SimResultsInfo
 */
public abstract VCDataIdentifier getResultsInfoObject();


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.DataBlock
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
public abstract SimDataBlock getSimDataBlock(OutputContext outputContext, String varName, double time) throws DataAccessException, IOException;

abstract double[][][] getSimDataTimeSeries0(
		OutputContext outputContext, 
		String varNames[],
		int[][] indexes,
		boolean[] wantsThisTime,
		DataSetControllerImpl.SpatialStatsInfo spatialStatsInfo,
		ProgressListener progressListener) 
throws DataAccessException,IOException;

/**
 * This method was created in VisualAge.
 * @return long
 */
public abstract long getSizeInBytes();


/**
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
public abstract DataIdentifier[] getVarAndFunctionDataIdentifiers(OutputContext outputContext) throws IOException, DataAccessException;


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.solvers.CartesianMesh
 */
abstract int[] getVolumeSize() throws IOException, DataAccessException;


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.DataBlock
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
public final double[][][] getSimDataTimeSeries(OutputContext outputContext, String varNames[],int[][] indexes,boolean[] wantsThisTime, ProgressListener progressListener) throws DataAccessException,IOException{

	return getSimDataTimeSeries0(outputContext, varNames,indexes,wantsThisTime,null,progressListener);
}

/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.DataBlock
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
public final double[][][] getSimDataTimeSeries(
		OutputContext outputContext, 
		String varNames[],
		int[][] indexes,
		boolean[] wantsThisTime,
		DataSetControllerImpl.SpatialStatsInfo spatialStatsInfo,
		ProgressListener progressListener) throws DataAccessException,IOException{

	return getSimDataTimeSeries0(outputContext, varNames,indexes,wantsThisTime,spatialStatsInfo, progressListener);

}

/**
 * Insert the method's description here.
 * Creation date: (3/20/2006 11:37:48 PM)
 * @return double[]
 * @param rawVals double[]
 */
double[] calcSpaceStats(double[] rawVals,int varIndex,DataSetControllerImpl.SpatialStatsInfo spatialStatsInfo) {
	
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    double mean = 0;
    double wmean = 0;
    double sum = 0;
    double wsum = 0;
    double val;
    for(int j=0;j<rawVals.length;j+= 1){
	    val = rawVals[j];
	    if(val < min){min=val;}
	    if(val > max){max=val;}
	    sum+= val;
	    if(spatialStatsInfo.bWeightsValid){wsum+= val*spatialStatsInfo.spaceWeight[varIndex][j];}
    }
    mean = sum/rawVals.length;
    if(spatialStatsInfo.bWeightsValid){wmean = wsum/spatialStatsInfo.totalSpace[varIndex];}

    return new double[] {min,max,mean,wmean,sum,wsum};
}

public abstract ChomboFiles getChomboFiles() throws IOException, XmlParseException, ExpressionException;

public abstract VCellSimFiles getVCellSimFiles() throws FileNotFoundException, DataAccessException;

public abstract MovingBoundarySimFiles getMovingBoundarySimFiles() throws FileNotFoundException, DataAccessException;

public abstract ComsolSimFiles getComsolSimFiles() throws FileNotFoundException, DataAccessException;

public abstract boolean isMovingBoundary() throws DataAccessException, IOException;

public abstract boolean isComsol() throws DataAccessException, IOException;

}
