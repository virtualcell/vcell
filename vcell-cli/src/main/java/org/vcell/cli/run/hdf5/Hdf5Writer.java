package org.vcell.cli.run.hdf5;

import cbit.vcell.resource.NativeLib;
import cbit.vcell.simdata.Hdf5Utils;
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import org.jlibsedml.Variable;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Hdf5Writer {

    private final static Logger logger = LogManager.getLogger(Hdf5Writer.class);

    public static void writeHdf5(Hdf5FileWrapper hdf5FileWrapper, File outDirForCurrentSedml) throws HDF5Exception {
        NativeLib.HDF5.load();
        File hdf5TempFile = new File(outDirForCurrentSedml, "reports.h5");
        logger.info("writing to file " + hdf5TempFile.getAbsolutePath());

        // Generate File ID
        int hdf5FileID = H5.H5Fcreate(hdf5TempFile.getAbsolutePath(), HDF5Constants.H5F_ACC_TRUNC,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        

        // Generate group IDs
        for (String uri : hdf5FileWrapper.pathToGroupIDTranslator.keySet()){
            int groupID = Hdf5Utils.createGroup(hdf5FileID, uri);
            hdf5FileWrapper.pathToGroupIDTranslator.put(uri, groupID);
            Hdf5Utils.insertAttribute(groupID, "combineArchiveLocation", uri);
            Hdf5Utils.insertAttribute(groupID, "uri", uri);
        }

        int jobGroupID = hdf5FileWrapper.pathToGroupIDTranslator.get(hdf5FileWrapper.uri);
        
        try {
            for (Hdf5DatasetWrapper datasetWrapper : hdf5FileWrapper.datasetWrappers) {
                // here this is either a plot or a report

//                ArrayList<String> paramNames = new ArrayList<String>();
//                ArrayList<String> paramValues = new ArrayList<String>();
//                for (int z = 0; z < paramScanParamNames.length; z++) {
//                    paramNames.add(paramScanParamNames[z]);
//                    paramValues.add(paramScanParamNames[z]);
//                }
                int hdf5DataspaceID = -1;
                int hdf5DatasetID = -1;
                if (datasetWrapper.dataSource instanceof Hdf5DataSourceNonspatial) {
                    Hdf5DataSourceNonspatial dataSourceNonspatial = (Hdf5DataSourceNonspatial) datasetWrapper.dataSource;
                    Map<Variable, double[]> varDataMap = dataSourceNonspatial.jobData.get(0).varData;
                    List<Variable> vars = new ArrayList<>(varDataMap.keySet());
                    long numVariablesPerJob = varDataMap.keySet().size();
                    long numTimePoints = varDataMap.get(vars.get(0)).length;

                    List<Long> dataDimensionList = new ArrayList<>();
                    int numJobs = 1;
                    for (int scanBound : dataSourceNonspatial.scanBounds){
                        dataDimensionList.add((long)scanBound+1);
                        numJobs *= (scanBound+1);
                    }
                    dataDimensionList.add(numVariablesPerJob);
                    dataDimensionList.add(numTimePoints);
                    long[] dataDimensions = dataDimensionList.stream().mapToLong(l -> l).toArray();
                    int totalDataSize = 1;
                    for (long dim : dataDimensions){
                        totalDataSize *= (int)dim;
                    }

                    double[] bigDataBuffer = new double[totalDataSize];
                    int bufferOffset = 0;
                    for (Hdf5DataSourceNonspatial.Hdf5JobData jobData : dataSourceNonspatial.jobData) {
                        for (int varIndex = 0; varIndex < vars.size(); varIndex++) {
                            Variable var = vars.get(varIndex);
                            double[] dataArray = jobData.varData.get(var);
                            for (int dataIndex = 0; dataIndex < dataArray.length; dataIndex++) {
                                System.arraycopy(dataArray,0, bigDataBuffer, bufferOffset, dataArray.length);
                            }
                            bufferOffset += (int)numTimePoints;
                        }
                    }
                    String datasetPath = Paths.get(hdf5FileWrapper.uri, datasetWrapper.datasetMetadata.sedmlId).toString();
                    hdf5DataspaceID = H5.H5Screate_simple(dataDimensions.length, dataDimensions, null);
                    hdf5DatasetID = H5.H5Dcreate(jobGroupID, File.separator + datasetPath, HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceID, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
                    H5.H5Dwrite_double(hdf5DatasetID, HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, (double[])bigDataBuffer);
                }else if (datasetWrapper.dataSource instanceof Hdf5DataSourceSpatial) {
                    Hdf5DataSourceSpatial dataSourceSpatial = (Hdf5DataSourceSpatial)datasetWrapper.dataSource;
                    System.out.println(dataSourceSpatial);
                    Hdf5DataSourceSpatialVarDataItem firstVarDataItem = dataSourceSpatial.varDataItems.get(0);
                    int[] spaceTimeDimensions = firstVarDataItem.spaceTimeDimensions;
                    int[] spatialDimensions = Arrays.copyOf(spaceTimeDimensions, spaceTimeDimensions.length-1);
                    int numSpatialPoints = Arrays.stream(spatialDimensions).sum();
                    //long numJobs = dataSourceSpatial.varDataItems.stream().map(varDataItem -> varDataItem.jobIndex).collect(Collectors.toSet()).size();
                    List<Variable> sedmlVars = new ArrayList<>(dataSourceSpatial.varDataItems.stream().map(varDataItem -> varDataItem.sedmlVariable).collect(Collectors.toSet()));
                    long numVars = sedmlVars.size();
                    long numTimes = firstVarDataItem.times.length;
                    if (numTimes != spaceTimeDimensions[spaceTimeDimensions.length-1]){
                        throw new RuntimeException("unexpected dimension "+spaceTimeDimensions+" for data, expected last dimension to be that of time: "+numTimes);
                    }
                    List<Long> dataDimensionList = new ArrayList<>();
                    int numJobs = 1;
                    for (int scanBound : dataSourceSpatial.scanBounds){
                        dataDimensionList.add((long)scanBound+1);
                        numJobs *= (scanBound+1);
                    }
                    dataDimensionList.add(numVars);
                    for (long dim : spatialDimensions){
                        dataDimensionList.add(dim);
                    }
                    dataDimensionList.add(numTimes);
                    long[] dataDimensions = dataDimensionList.stream().mapToLong(l -> l).toArray();
                    int totalDataSize = 1;
                    for (long dim : dataDimensions){
                        totalDataSize *= (int)dim;
                    }
                    double[] bigDataBuffer = new double[totalDataSize];
//                    for (int i=0;i<totalDataSize;i++){
//                        bigDataBuffer[i] = i;
//                    }
                    int bufferOffset = 0;
                    for (int jobIndex=0; jobIndex<numJobs; jobIndex++){
                        for (int varIndex = 0; varIndex < numVars; varIndex++) {
                            Variable var = sedmlVars.get(varIndex);
                            // find data for var and jobIndex
                            for (Hdf5DataSourceSpatialVarDataItem varDataItem : dataSourceSpatial.varDataItems) {
                                if (varDataItem.sedmlVariable.equals(var) && varDataItem.jobIndex == jobIndex){
                                    double[] dataArray = varDataItem.getSpatialData();
                                    System.arraycopy(dataArray,0,bigDataBuffer,bufferOffset,dataArray.length);
                                    bufferOffset += dataArray.length;
                                    break;
                                }
                            }
                        }
                    }

                    String datasetPath = Paths.get(hdf5FileWrapper.uri, datasetWrapper.datasetMetadata.sedmlId).toString();
                    hdf5DataspaceID = H5.H5Screate_simple(dataDimensions.length, dataDimensions, null);
                    hdf5DatasetID = H5.H5Dcreate(jobGroupID, File.separator + datasetPath, HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceID, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
                    H5.H5Dwrite_double(hdf5DatasetID, HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, (double[])bigDataBuffer);

                    Hdf5Utils.insertAttributes(hdf5DatasetID, "times", firstVarDataItem.times);
                }else{
                    continue;
                }

                Hdf5Utils.insertAttribute(hdf5DatasetID, "_type", datasetWrapper.datasetMetadata._type);
                Hdf5Utils.insertAttributes(hdf5DatasetID, "scanParameterNames", Arrays.asList(datasetWrapper.dataSource.scanParameterNames));
                Hdf5Utils.insertAttributes(hdf5DatasetID, "sedmlDataSetDataTypes", datasetWrapper.datasetMetadata.sedmlDataSetDataTypes);
                Hdf5Utils.insertAttributes(hdf5DatasetID, "sedmlDataSetIds", datasetWrapper.datasetMetadata.sedmlDataSetIds);
                Hdf5Utils.insertAttributes(hdf5DatasetID, "sedmlDataSetNames", datasetWrapper.datasetMetadata.sedmlDataSetNames);
                if (datasetWrapper.datasetMetadata.sedmlDataSetNames.get(0) != null &&
                        datasetWrapper.datasetMetadata.sedmlDataSetNames.get(0).length()>0) {
                    Hdf5Utils.insertAttributes(hdf5DatasetID, "sedmlDataSetNames", datasetWrapper.datasetMetadata.sedmlDataSetNames);
                }
                Hdf5Utils.insertAttributes(hdf5DatasetID, "sedmlDataSetLabels", datasetWrapper.datasetMetadata.sedmlDataSetLabels);
                Hdf5Utils.insertAttributes(hdf5DatasetID, "sedmlDataSetShapes", datasetWrapper.datasetMetadata.sedmlDataSetShapes);
                Hdf5Utils.insertAttribute(hdf5DatasetID, "sedmlId", datasetWrapper.datasetMetadata.sedmlId);
                Hdf5Utils.insertAttribute(hdf5DatasetID, "sedmlName", datasetWrapper.datasetMetadata.sedmlName);
                Hdf5Utils.insertAttribute(hdf5DatasetID, "uri", hdf5FileWrapper.uri + "/" + datasetWrapper.datasetMetadata.sedmlId);
//                if (paramNames.size() != 0) {
//                    // for scans????
//                    Hdf5Utils.insertAttributes(help0.hdf5DatasetValuesID, "paramNames", paramNames);
//                    Hdf5Utils.insertAttributes(help0.hdf5DatasetValuesID, "paramValues", paramValues);
//                }
                H5.H5Dclose(hdf5DatasetID);
                H5.H5Sclose(hdf5DataspaceID);
            }

            // Close all groups
            for (Integer groupID : hdf5FileWrapper.pathToGroupIDTranslator.values()){
                H5.H5Gclose(groupID);
            }
        } finally {
            if (hdf5FileID != -1) {
                try {
                    H5.H5Fclose(hdf5FileID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
