package org.vcell.cli.run.hdf5;

import cbit.vcell.resource.NativeLib;
import cbit.vcell.simdata.Hdf5Utils;
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import org.jlibsedml.Variable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Hdf5Writer {

    public static void writeHdf5(Hdf5FileWrapper hdf5FileWrapper, File outDirForCurrentSedml) throws HDF5Exception {
        NativeLib.HDF5.load();
        File hdf5TempFile = new File(outDirForCurrentSedml, "report.h5");
        System.out.println("writing to file "+hdf5TempFile.getAbsolutePath());
        int hdf5FileID = H5.H5Fcreate(hdf5TempFile.getAbsolutePath(), HDF5Constants.H5F_ACC_TRUNC,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        int jobGroupID = Hdf5Utils.createGroup(hdf5FileID, hdf5FileWrapper.combineArchiveLocation);
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
                    int numJobs = dataSourceNonspatial.jobData.size();
                    Map<Variable, double[]> varDataMap = dataSourceNonspatial.jobData.get(0).varData;
                    List<Variable> vars = new ArrayList(varDataMap.keySet());
                    int numVariablesPerJob = varDataMap.keySet().size();
                    int numTimePoints = varDataMap.get(vars.get(0)).length;

                    final long[] dataDimensions;
                    final int totalDataSize;
                    if (numJobs == 1) {
                        dataDimensions = new long[]{numVariablesPerJob, numTimePoints};
                        totalDataSize = numTimePoints * numVariablesPerJob;
                    } else {
                        dataDimensions = new long[]{numVariablesPerJob, numTimePoints, numJobs};
                        totalDataSize = numTimePoints * numVariablesPerJob * numJobs;
                    }

                    double[] bigDataBuffer = new double[totalDataSize];
                    int jobIndex = 0;
                    for (Hdf5DataSourceNonspatial.Hdf5JobData jobData : dataSourceNonspatial.jobData) {
                        for (int varIndex = 0; varIndex < vars.size(); varIndex++) {
                            Variable var = vars.get(varIndex);
                            double[] dataArray = jobData.varData.get(var);
                            for (int dataIndex = 0; dataIndex < dataArray.length; dataIndex++) {
                                double value = dataArray[dataIndex];
                                bigDataBuffer[varIndex * numTimePoints * numJobs + dataIndex * numJobs + jobIndex] = value;
                            }
                        }
                        jobIndex++;
                    }
                    String datasetPath = "/"+hdf5FileWrapper.combineArchiveLocation+"/"+datasetWrapper.datasetMetadata.sedmlId;
                    hdf5DataspaceID = H5.H5Screate_simple(dataDimensions.length, dataDimensions, null);
                    hdf5DatasetID = H5.H5Dcreate(jobGroupID, datasetPath, HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceID, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
                    H5.H5Dwrite_double(hdf5DatasetID, HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, (double[])bigDataBuffer);
                }else if (datasetWrapper.dataSource instanceof Hdf5DataSourceSpatial) {
                    Hdf5DataSourceSpatial dataSourceSpatial = (Hdf5DataSourceSpatial)datasetWrapper.dataSource;
                    System.out.println(dataSourceSpatial);
                    Hdf5DataSourceSpatialVarDataItem firstVarDataItem = dataSourceSpatial.varDataItems.get(0);
                    int[] spaceTimeDimensions = firstVarDataItem.spaceTimeDimensions;
                    int[] spatialDimensions = Arrays.copyOf(spaceTimeDimensions, spaceTimeDimensions.length-1);
                    long numJobs = dataSourceSpatial.varDataItems.stream().map(varDataItem -> varDataItem.jobIndex).collect(Collectors.toSet()).size();
                    long numVars = dataSourceSpatial.varDataItems.stream().map(varDataItem -> varDataItem.sedmlVariable.getName()).collect(Collectors.toSet()).size();
                    long numTimes = firstVarDataItem.times.length;
                    if (numTimes != spaceTimeDimensions[spaceTimeDimensions.length-1]){
                        throw new RuntimeException("unexpected dimension "+spaceTimeDimensions+" for data, expected last dimension to be that of time: "+numTimes);
                    }
                    List<Long> dataDimensionList = new ArrayList<>();
                    dataDimensionList.add(numVars);
                    for (long dim : spatialDimensions){
                        dataDimensionList.add(dim);
                    }
                    dataDimensionList.add(numTimes);
                    if (numJobs>1){
                        dataDimensionList.add(numJobs);
                    }
                    long[] dataDimensions = dataDimensionList.stream().mapToLong(l -> l).toArray();
                    int totalDataSize = 1;
                    for (long dim : dataDimensions){
                        totalDataSize *= dim;
                    }
                    double[] bigDataBuffer = new double[totalDataSize];
                    for (int i=0;i<totalDataSize;i++){
                        bigDataBuffer[i] = i;
                    }
                    // need to populate the bigDataBuffer ....

                    String datasetPath = "/"+hdf5FileWrapper.combineArchiveLocation+"/"+datasetWrapper.datasetMetadata.sedmlId;
                    hdf5DataspaceID = H5.H5Screate_simple(dataDimensions.length, dataDimensions, null);
                    hdf5DatasetID = H5.H5Dcreate(jobGroupID, datasetPath, HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceID, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
                    H5.H5Dwrite_double(hdf5DatasetID, HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, (double[])bigDataBuffer);
                }else{
                    continue;
                }

                Hdf5Utils.insertAttribute(hdf5DatasetID, "_type", datasetWrapper.datasetMetadata._type);
                Hdf5Utils.insertAttributes(hdf5DatasetID, "sedmlDataSetDataTypes", datasetWrapper.datasetMetadata.sedmlDataSetDataTypes);
                Hdf5Utils.insertAttributes(hdf5DatasetID, "sedmlDataSetIds", datasetWrapper.datasetMetadata.sedmlDataSetIds);
                if (datasetWrapper.datasetMetadata.sedmlDataSetNames.get(0) != null &&
                        datasetWrapper.datasetMetadata.sedmlDataSetNames.get(0).length()>0) {
                    Hdf5Utils.insertAttributes(hdf5DatasetID, "sedmlDataSetNames", datasetWrapper.datasetMetadata.sedmlDataSetNames);
                }
                Hdf5Utils.insertAttributes(hdf5DatasetID, "sedmlDataSetLabels", datasetWrapper.datasetMetadata.sedmlDataSetLabels);
//                Hdf5Utils.insertAttributes(hdf5DatasetID, "sedmlDataSetShapes", datasetWrapper.datasetMetadata.sedmlDataSetShapes);
                Hdf5Utils.insertAttribute(hdf5DatasetID, "sedmlId", datasetWrapper.datasetMetadata.sedmlId);
                Hdf5Utils.insertAttribute(hdf5DatasetID, "sedmlName", datasetWrapper.datasetMetadata.sedmlName);
                Hdf5Utils.insertAttribute(hdf5DatasetID, "uri", datasetWrapper.datasetMetadata.uri);
//                if (paramNames.size() != 0) {
//                    // for scans????
//                    Hdf5Utils.insertAttributes(help0.hdf5DatasetValuesID, "paramNames", paramNames);
//                    Hdf5Utils.insertAttributes(help0.hdf5DatasetValuesID, "paramValues", paramValues);
//                }
                H5.H5Dclose(hdf5DatasetID);
                H5.H5Sclose(hdf5DataspaceID);
            }
            H5.H5Gclose(jobGroupID);
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
