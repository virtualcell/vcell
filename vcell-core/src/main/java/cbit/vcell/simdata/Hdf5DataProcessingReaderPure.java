package cbit.vcell.simdata;

import cbit.vcell.math.VariableType;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import ncsa.hdf.object.*;
import ncsa.hdf.object.h5.H5ScalarDS;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.document.TSJobResultsNoStats;
import org.vcell.util.document.TimeSeriesJobResults;
import org.vcell.util.document.TimeSeriesJobSpec;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cbit.vcell.simdata.DataOperationResults.DataProcessingOutputInfo.PostProcessDataType;

import static cbit.vcell.simdata.SimDataConstants.*;


public class Hdf5DataProcessingReaderPure {


    public DataOperationResults.DataProcessingOutputInfo getDataProcessingOutput(DataOperation.DataProcessingOutputInfoOP infoOP, File dataProcessingOutputFileHDF5) throws Exception {
        Hdf5PostProcessor.PostProcessing postProcessing = new Hdf5PostProcessor.PostProcessing(dataProcessingOutputFileHDF5.toPath());
        postProcessing.read();
        //DataSetControllerImpl.DataProcessingHelper dataProcessingHelper = new DataSetControllerImpl.DataProcessingHelper();
        // TODO: populate this data from postprocessing

        // status_by_channel is a list of double arrays of shape (times), where each item of the list corresponds to a statistic
        List<double[]> timeseries_per_statistic = postProcessing.statistics_by_channel;
        HashMap<String,double[]> varStatValues = new HashMap<>();
        for (int i = 0; i < postProcessing.getVariables().size(); i++) {
            varStatValues.put(postProcessing.getVariables().get(i).stat_var_name(), timeseries_per_statistic.get(i));
        }
        // collect data for the statistics variables
        List<String> varNames = new ArrayList<>(postProcessing.getVariables().stream().map(vi -> vi.stat_var_name()).toList());
        List<String> units = new ArrayList<>(postProcessing.getVariables().stream().map(vi -> vi.unit()).toList());
        List<PostProcessDataType> vtypes = new ArrayList<>(postProcessing.getVariables().stream().map(vi -> PostProcessDataType.statistic).toList());
        List<ISize> isizes = new ArrayList<>(postProcessing.getVariables().stream().map(im -> (ISize) null).toList());
        List<Origin> origins = new ArrayList<>(postProcessing.getVariables().stream().map(im -> (Origin) null).toList());
        List<Extent> extents = new ArrayList<>(postProcessing.getVariables().stream().map(im -> (Extent) null).toList());
        // add data for the image variables
        for (Hdf5PostProcessor.ImageMetadata imageMetadata : postProcessing.imageMetadata){
            varNames.add(imageMetadata.name);
            units.add(null);
            vtypes.add(PostProcessDataType.image);
            isizes.add(new ISize(imageMetadata.shape[2],imageMetadata.shape[1],imageMetadata.shape[0]));  // shape is (z, y, x)
            origins.add(new Origin(imageMetadata.origin[0],imageMetadata.origin[1],imageMetadata.origin[2]));
            extents.add(new Extent(imageMetadata.extent[0],imageMetadata.extent[1],imageMetadata.extent[2]));
        }
        DataOperationResults.DataProcessingOutputInfo info = new DataOperationResults.DataProcessingOutputInfo(
                infoOP.getVCDataIdentifier(),
                varNames.toArray(new String[0]),
                isizes.toArray(new ISize[0]),
                postProcessing.times,
                units.toArray(new String[0]),
                vtypes.toArray(new PostProcessDataType[0]),
                origins.toArray(new Origin[0]),
                extents.toArray(new Extent[0]),
                varStatValues
        );


        ArrayList<String> postProcessImageVarNames = new ArrayList<>();
        for (int i = 0; i < info.getVariableNames().length; i++) {
            String variableName = info.getVariableNames()[i];
            if(info.getPostProcessDataType(variableName).equals(DataOperationResults.DataProcessingOutputInfo.PostProcessDataType.image)){
                postProcessImageVarNames.add(variableName);
            }
        }
        HashMap<String, String> mapFunctionNameToStateVarName = null;
        if(infoOP.getOutputContext() != null){
            mapFunctionNameToStateVarName = new HashMap<>();
            for (int i = 0; i < infoOP.getOutputContext().getOutputFunctions().length; i++) {
                AnnotatedFunction annotatedFunction = infoOP.getOutputContext().getOutputFunctions()[i];
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
        if(mapFunctionNameToStateVarName != null && !mapFunctionNameToStateVarName.isEmpty()){
            info = new DataOperationResults.DataProcessingOutputInfo(info,mapFunctionNameToStateVarName);
        }
        return info;
    }


    public DataOperationResults.DataProcessingOutputDataValues getDataProcessingOutput(DataOperation.DataProcessingOutputDataValuesOP dataValuesOp, File dataProcessingOutputFileHDF5) throws Exception {
        Hdf5PostProcessor.PostProcessing postProcessing = new Hdf5PostProcessor.PostProcessing(dataProcessingOutputFileHDF5.toPath());
        postProcessing.read();
        // find indices of time dataValuesOp.getTimePointHelper().getTimePoints() in postProcessing.times
        int[] timeIndices = new int[dataValuesOp.getTimePointHelper().getTimePoints().length];
        for (int i = 0; i < timeIndices.length; i++) {
            timeIndices[i] = -1;
            for (int j = 0; j < postProcessing.times.length; j++) {
                if (postProcessing.times[j] == dataValuesOp.getTimePointHelper().getTimePoints()[i]){
                    timeIndices[i] = j;
                    break;
                }
            }
            if (timeIndices[i] == -1){
                throw new Exception("Time point not found in postprocessing data: " + dataValuesOp.getTimePointHelper().getTimePoints()[i]);
            }
        }
        double[][] timeSpecificDataValues = new double[timeIndices.length][];
        Hdf5PostProcessor.ImageMetadata imageMetadata = postProcessing.imageMetadata.stream().filter(im -> im.name.equals(dataValuesOp.getVariableName())).findFirst().orElse(null);
        int count = 0;
        for (int timeIndex : timeIndices) {
            double[][][] values3D = postProcessing.readImageData(imageMetadata, timeIndex);
            // flatten 3D array into 1D array
            double[] values1D = new double[values3D.length * values3D[0].length * values3D[0][0].length];
            int k = 0;
            for (int i = 0; i < values3D.length; i++) {
                for (int j = 0; j < values3D[0].length; j++) {
                    for (int l = 0; l < values3D[0][0].length; l++) {
                        values1D[k++] = values3D[i][j][l];
                    }
                }
            }
            timeSpecificDataValues[count++] = values1D;
        }

        return new DataOperationResults.DataProcessingOutputDataValues(
                dataValuesOp.getVCDataIdentifier(),
                dataValuesOp.getVariableName(),
                dataValuesOp.getTimePointHelper(),
                dataValuesOp.getDataIndexHelper(),
                timeSpecificDataValues);
    }


    public DataOperationResults.DataProcessingOutputTimeSeriesValues getDataProcessingOutput(DataOperation.DataProcessingOutputTimeSeriesOP timeSeriesOp, File dataProcessingOutputFileHDF5) throws Exception {
        Hdf5PostProcessor.PostProcessing postProcessing = new Hdf5PostProcessor.PostProcessing(dataProcessingOutputFileHDF5.toPath());
        postProcessing.read();
        return new DataOperationResults.DataProcessingOutputTimeSeriesValues(timeSeriesOp.getVCDataIdentifier(), null);
//        DataOperation.DataProcessingOutputDataValuesOP.DataIndexHelper dataIndexHelper = null;
//        DataOperation.DataProcessingOutputDataValuesOP.TimePointHelper timePointHelper = null;
//        String[] variableNames = timeSeriesOp.getTimeSeriesJobSpec().getVariableNames();
//        if(variableNames.length != 1){
//            throw new Exception("Only 1 variable request at a time");
//        }
//        OutputContext outputContext = timeSeriesOp.getOutputContext();
//        AnnotatedFunction[] annotatedFunctions = (outputContext==null?null:outputContext.getOutputFunctions());
//        AnnotatedFunction foundFunction = null;
//        if(annotatedFunctions != null){
//            for (int i = 0; i < annotatedFunctions.length; i++) {
//                if(annotatedFunctions[i].getName().equals(variableNames[0])){
//                    foundFunction = annotatedFunctions[i];
//                    break;
//                }
//            }
//        }
//        DataOperationResults.DataProcessingOutputDataValues dataProcessingOutputResults = null;
//        double[] alltimes = null;
//        if(foundFunction != null){
//            DataOperationResults.DataProcessingOutputInfo dataProcessingOutputInfo =
//                    getDataProcessingOutput(new DataOperation.DataProcessingOutputInfoOP(timeSeriesOp.getVCDataIdentifier(),false,timeSeriesOp.getOutputContext()), dataProcessingOutputFileHDF5);
//            alltimes = dataProcessingOutputInfo.getVariableTimePoints();
//            DataSetControllerImpl.FunctionHelper functionHelper = DataSetControllerImpl.getPostProcessStateVariables(foundFunction, dataProcessingOutputInfo);
//            DataSetControllerImpl.DataProcessingHelper dataProcessingHelper = new DataSetControllerImpl.DataProcessingHelper(functionHelper.postProcessStateVars,timePointHelper,dataIndexHelper);
//            iterateHDF5(postProcessing,"",dataProcessingHelper);
//            dataProcessingOutputResults =
//                    DataSetControllerImpl.evaluatePostProcessFunction(dataProcessingOutputInfo, functionHelper.postProcessStateVars, dataProcessingHelper.specificDataValues,
//                            dataIndexHelper, timePointHelper, functionHelper.flattenedBoundExpression,variableNames[0]);
//        }else{
//            DataSetControllerImpl.DataProcessingHelper dataProcessingHelper =
//                    new DataSetControllerImpl.DataProcessingHelper(new String[] {variableNames[0]},timePointHelper,dataIndexHelper);
//            iterateHDF5(postProcessing,"",dataProcessingHelper);
//            alltimes = dataProcessingHelper.times;
//            if(dataProcessingHelper.specificDataValues == null){
//                throw new Exception("Couldn't find postprocess data as specified for var="+variableNames[0]);
//            }
//            dataProcessingOutputResults = new DataOperationResults.DataProcessingOutputDataValues(timeSeriesOp.getVCDataIdentifier(),
//                    variableNames[0],timePointHelper,dataIndexHelper, dataProcessingHelper.specificDataValues[0]);
//        }
//        TimeSeriesJobResults timeSeriesJobResults = null;
//        double[][] dataValues = dataProcessingOutputResults.getDataValues();//[time][data]
//        double[] desiredTimes = (timePointHelper.isAllTimePoints()?alltimes:timePointHelper.getTimePoints());
//        double[][][] timeSeriesFormatedValuesArr = new double[variableNames.length][dataIndexHelper.getDataIndexes().length+1][desiredTimes.length];
//        for (int i = 0; i < timeSeriesFormatedValuesArr.length; i++) {//var
//            for (int j = 0; j < timeSeriesFormatedValuesArr[i].length; j++) {//index
//                if(j==0){
//                    timeSeriesFormatedValuesArr[i][j] = desiredTimes;
//                    continue;
//                }
//                for (int k = 0; k < timeSeriesFormatedValuesArr[i][j].length; k++) {//time
//                    //assume 1 variable for now
//                    timeSeriesFormatedValuesArr[i][j][k] = dataValues[k][j-1];
//                }
//            }
//        }
//
//        if(timeSeriesOp.getTimeSeriesJobSpec().isCalcSpaceStats()){
//            DataSetControllerImpl.SpatialStatsInfo spatialStatsInfo = new DataSetControllerImpl.SpatialStatsInfo();
//            spatialStatsInfo.bWeightsValid = false;
//            timeSeriesJobResults =
//                    DataSetControllerImpl.calculateStatisticsFromWhole(timeSeriesOp.getTimeSeriesJobSpec(), timeSeriesFormatedValuesArr, timePointHelper.getTimePoints(), spatialStatsInfo);
//        }else{
//            timeSeriesJobResults =
//                    new TSJobResultsNoStats(
//                            variableNames,
//                            new int[][] {dataIndexHelper.getDataIndexes()},
//                            timePointHelper.getTimePoints(),
//                            timeSeriesFormatedValuesArr);
//        }
//        return new DataOperationResults.DataProcessingOutputTimeSeriesValues(timeSeriesOp.getVCDataIdentifier(), timeSeriesJobResults);

    }

//    public DataOperationResults getDataProcessingOutput_internal(DataOperation dataOperation, File dataProcessingOutputFileHDF5) throws Exception {
//        DataOperationResults dataProcessingOutputResults = null;
//        FileFormat hdf5FileFormat = null;
//        try{
//            if (dataProcessingOutputFileHDF5.exists()) {
//                // retrieve an instance of H5File
//                FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
//                if (fileFormat == null){
//                    throw new Exception("Cannot find HDF5 FileFormat.");
//                }
//                // open the file with read-only access
//                hdf5FileFormat = fileFormat.open(dataProcessingOutputFileHDF5.getAbsolutePath(), FileFormat.READ);
//                hdf5FileFormat.setMaxMembers(Simulation.MAX_LIMIT_SPATIAL_TIMEPOINTS);
//                // open the file and retrieve the file structure
//                hdf5FileFormat.open();
//                Group root = (Group)((javax.swing.tree.DefaultMutableTreeNode)hdf5FileFormat.getRootNode()).getUserObject();
//                if(dataOperation instanceof DataOperation.DataProcessingOutputInfoOP){
//                    DataSetControllerImpl.DataProcessingHelper dataProcessingHelper = new DataSetControllerImpl.DataProcessingHelper();
//                    iterateHDF5(root,"",dataProcessingHelper);
//                    dataProcessingOutputResults = new DataOperationResults.DataProcessingOutputInfo(dataOperation.getVCDataIdentifier(),
//                            dataProcessingHelper.getVarNames(),
//                            dataProcessingHelper.getVarISizes(),
//                            dataProcessingHelper.times,
//                            dataProcessingHelper.getVarUnits(),
//                            dataProcessingHelper.getPostProcessDataTypes(),
//                            dataProcessingHelper.getVarOrigins(),
//                            dataProcessingHelper.getVarExtents(),
//                            dataProcessingHelper.getVarStatValues());
//                    //map function names to PostProcess state variable name
//                    ArrayList<String> postProcessImageVarNames = new ArrayList<String>();
//                    for (int i = 0; i < ((DataOperationResults.DataProcessingOutputInfo)dataProcessingOutputResults).getVariableNames().length; i++) {
//                        String variableName = ((DataOperationResults.DataProcessingOutputInfo)dataProcessingOutputResults).getVariableNames()[i];
//                        if(((DataOperationResults.DataProcessingOutputInfo)dataProcessingOutputResults).getPostProcessDataType(variableName).equals(DataOperationResults.DataProcessingOutputInfo.PostProcessDataType.image)){
//                            postProcessImageVarNames.add(variableName);
//                        }
//                    }
//                    HashMap<String, String> mapFunctionNameToStateVarName = null;
//                    if(((DataOperation.DataProcessingOutputInfoOP)dataOperation).getOutputContext() != null){
//                        mapFunctionNameToStateVarName = new HashMap<String, String>();
//                        for (int i = 0; i < ((DataOperation.DataProcessingOutputInfoOP)dataOperation).getOutputContext().getOutputFunctions().length; i++) {
//                            AnnotatedFunction annotatedFunction = ((DataOperation.DataProcessingOutputInfoOP)dataOperation).getOutputContext().getOutputFunctions()[i];
//                            if(annotatedFunction.getFunctionType().equals(VariableType.POSTPROCESSING)){
//                                String[] symbols = annotatedFunction.getExpression().flatten().getSymbols();
//                                //Find any PostProcess state var that matches a symbol in the function
//                                for (int j = 0; j < symbols.length; j++) {
//                                    if(postProcessImageVarNames.contains(symbols[j])){
//                                        mapFunctionNameToStateVarName.put(annotatedFunction.getName(), symbols[j]);
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    if(mapFunctionNameToStateVarName != null && mapFunctionNameToStateVarName.size() > 0){
//                        dataProcessingOutputResults = new DataOperationResults.DataProcessingOutputInfo(((DataOperationResults.DataProcessingOutputInfo)dataProcessingOutputResults),mapFunctionNameToStateVarName);
//                    }
//                }else{
//                    OutputContext outputContext = dataOperation.getOutputContext();
//                    String[] variableNames = null;
//                    DataOperation.DataProcessingOutputDataValuesOP.DataIndexHelper dataIndexHelper = null;
//                    DataOperation.DataProcessingOutputDataValuesOP.TimePointHelper timePointHelper = null;
//                    if(dataOperation instanceof DataOperation.DataProcessingOutputDataValuesOP){
//                        variableNames = new String[] {((DataOperation.DataProcessingOutputDataValuesOP)dataOperation).getVariableName()};
//                        dataIndexHelper = ((DataOperation.DataProcessingOutputDataValuesOP)dataOperation).getDataIndexHelper();
//                        timePointHelper = ((DataOperation.DataProcessingOutputDataValuesOP)dataOperation).getTimePointHelper();
//                    }else if(dataOperation instanceof DataOperation.DataProcessingOutputTimeSeriesOP){
//                        variableNames = ((DataOperation.DataProcessingOutputTimeSeriesOP)dataOperation).getTimeSeriesJobSpec().getVariableNames();
//                        TimeSeriesJobSpec timeSeriesJobSpec = ((DataOperation.DataProcessingOutputTimeSeriesOP)dataOperation).getTimeSeriesJobSpec();
//                        double[] specificTimepoints = extractTimeRange(((DataOperation.DataProcessingOutputTimeSeriesOP)dataOperation).getAllDatasetTimes(), timeSeriesJobSpec.getStartTime(), timeSeriesJobSpec.getEndTime());
//                        timePointHelper = DataOperation.DataProcessingOutputDataValuesOP.TimePointHelper.createSpecificTimePointHelper(specificTimepoints);
//                        timeSeriesJobSpec.initIndices();
//                        dataIndexHelper = DataOperation.DataProcessingOutputDataValuesOP.DataIndexHelper.createSpecificDataIndexHelper(timeSeriesJobSpec.getIndices()[0]);
//                    }else{
//                        throw new Exception("Unknown Dataoperation "+dataOperation.getClass().getName());
//                    }
//                    if(variableNames.length != 1){
//                        throw new Exception("Only 1 variable request at a time");
//                    }
//                    AnnotatedFunction[] annotatedFunctions = (outputContext==null?null:outputContext.getOutputFunctions());
//                    AnnotatedFunction foundFunction = null;
//                    if(annotatedFunctions != null){
//                        for (int i = 0; i < annotatedFunctions.length; i++) {
//                            if(annotatedFunctions[i].getName().equals(variableNames[0])){
//                                foundFunction = annotatedFunctions[i];
//                                break;
//                            }
//                        }
//                    }
//                    double[] alltimes = null;
//                    if(foundFunction != null){
//                        DataOperationResults.DataProcessingOutputInfo dataProcessingOutputInfo =
//                            (DataOperationResults.DataProcessingOutputInfo)getDataProcessingOutput(new DataOperation.DataProcessingOutputInfoOP(dataOperation.getVCDataIdentifier(),false,dataOperation.getOutputContext()), dataProcessingOutputFileHDF5);
//                        alltimes = dataProcessingOutputInfo.getVariableTimePoints();
//                        DataSetControllerImpl.FunctionHelper functionHelper = DataSetControllerImpl.getPostProcessStateVariables(foundFunction, dataProcessingOutputInfo);
//                        DataSetControllerImpl.DataProcessingHelper dataProcessingHelper = new DataSetControllerImpl.DataProcessingHelper(functionHelper.postProcessStateVars,timePointHelper,dataIndexHelper);
//                        iterateHDF5(root,"",dataProcessingHelper);
//                        dataProcessingOutputResults =
//                            DataSetControllerImpl.evaluatePostProcessFunction(dataProcessingOutputInfo, functionHelper.postProcessStateVars, dataProcessingHelper.specificDataValues,
//                                    dataIndexHelper, timePointHelper, functionHelper.flattenedBoundExpression,variableNames[0]);
//                    }else{
//                        DataSetControllerImpl.DataProcessingHelper dataProcessingHelper =
//                            new DataSetControllerImpl.DataProcessingHelper(new String[] {variableNames[0]},timePointHelper,dataIndexHelper);
//                        iterateHDF5(root,"",dataProcessingHelper);
//                        alltimes = dataProcessingHelper.times;
//                        if(dataProcessingHelper.specificDataValues == null){
//                            throw new Exception("Couldn't find postprocess data as specified for var="+variableNames[0]);
//                        }
//                        dataProcessingOutputResults = new DataOperationResults.DataProcessingOutputDataValues(dataOperation.getVCDataIdentifier(),
//                            variableNames[0],timePointHelper,dataIndexHelper, dataProcessingHelper.specificDataValues[0]);
//                    }
//                    if(dataOperation instanceof DataOperation.DataProcessingOutputTimeSeriesOP){
//                        TimeSeriesJobResults timeSeriesJobResults = null;
//                        DataOperation.DataProcessingOutputTimeSeriesOP dataProcessingOutputTimeSeriesOP = (DataOperation.DataProcessingOutputTimeSeriesOP)dataOperation;
//                        double[][] dataValues = ((DataOperationResults.DataProcessingOutputDataValues)dataProcessingOutputResults).getDataValues();//[time][data]
//                        double[] desiredTimes = (timePointHelper.isAllTimePoints()?alltimes:timePointHelper.getTimePoints());
//                        double[][][] timeSeriesFormatedValuesArr = new double[variableNames.length][dataIndexHelper.getDataIndexes().length+1][desiredTimes.length];
//                        for (int i = 0; i < timeSeriesFormatedValuesArr.length; i++) {//var
//                            for (int j = 0; j < timeSeriesFormatedValuesArr[i].length; j++) {//index
//                                if(j==0){
//                                    timeSeriesFormatedValuesArr[i][j] = desiredTimes;
//                                    continue;
//                                }
//                                for (int k = 0; k < timeSeriesFormatedValuesArr[i][j].length; k++) {//time
//                                    //assume 1 variable for now
//                                    timeSeriesFormatedValuesArr[i][j][k] = dataValues[k][j-1];
//                                }
//                            }
//                        }
//
//                        if(dataProcessingOutputTimeSeriesOP.getTimeSeriesJobSpec().isCalcSpaceStats()){
//                            DataSetControllerImpl.SpatialStatsInfo spatialStatsInfo = new DataSetControllerImpl.SpatialStatsInfo();
//                            spatialStatsInfo.bWeightsValid = false;
//                            timeSeriesJobResults =
//                                DataSetControllerImpl.calculateStatisticsFromWhole(dataProcessingOutputTimeSeriesOP.getTimeSeriesJobSpec(), timeSeriesFormatedValuesArr, timePointHelper.getTimePoints(), spatialStatsInfo);
//                        }else{
//                            timeSeriesJobResults =
//                                new TSJobResultsNoStats(
//                                    variableNames,
//                                    new int[][] {dataIndexHelper.getDataIndexes()},
//                                    timePointHelper.getTimePoints(),
//                                    timeSeriesFormatedValuesArr);
//                        }
//                        dataProcessingOutputResults = new DataOperationResults.DataProcessingOutputTimeSeriesValues(dataOperation.getVCDataIdentifier(), timeSeriesJobResults);
//                    }
//                }
//            }else{
//                throw new FileNotFoundException("Data Processing Output file '"+dataProcessingOutputFileHDF5.getPath()+"' not found");
//            }
//        }catch(Exception e){
//            DataSetControllerImpl.lg.error(e.getMessage(), e);
//        }finally{
//            if(hdf5FileFormat != null){try{hdf5FileFormat.close();}catch(Exception e){
//                DataSetControllerImpl.lg.error(e.getMessage(), e);}}
//        }
//
//        return dataProcessingOutputResults;
//    }
//
//    private static double[] extractTimeRange(double[] alltimes, double startTime, double stoptime){
//        ArrayList<Double> selectedtimePointsList = new ArrayList<Double>();
//        for (int i = 0; i < alltimes.length; i++) {
//            if(alltimes[i] >= startTime && alltimes[i] <= stoptime){
//                selectedtimePointsList.add(alltimes[i]);
//            }
//        }
//        double[] selectedTimePoints = new double[selectedtimePointsList.size()];
//        for (int j = 0; j < selectedtimePointsList.size(); j++) {
//            selectedTimePoints[j] = selectedtimePointsList.get(j);
//        }
//        return selectedTimePoints;
//    }


//    private static void iterateHDF5(Hdf5PostProcessor.PostProcessing postProcessing, String indent, DataSetControllerImpl.DataProcessingHelper dataProcessingHelper) throws Exception{
//        if(hObject instanceof Group){
//            Group group = ((Group)hObject);
//            printInfo(group,indent);
//            if(group.getName().equals("/") || group.getName().equals(SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_POSTPROCESSING)){
//                List<HObject> postProcessMembers = ((Group)hObject).getMemberList();
//                for(HObject nextHObject:postProcessMembers){
//                    iterateHDF5(nextHObject, indent+" ", dataProcessingHelper);
//                }
//            }else if(group.getName().equals(SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_VARIABLESTATISTICS) && dataProcessingHelper.isInfoOnly()){
//                populateStatNamesAndUnits(hObject, dataProcessingHelper);
//                List<HObject> statDataAtEachTime = group.getMemberList();
//                dataProcessingHelper.statValues = new double[dataProcessingHelper.statVarNames.length][statDataAtEachTime.size()];
//                for(HObject nextStatData:statDataAtEachTime){
//                    printInfo(nextStatData,indent+" ");
//                    processDims(nextStatData, dataProcessingHelper,false);//always get stats data when ask for info
//                    double[] stats = (double[])dataProcessingHelper.tempData;
//                    int timeIndex = Integer.parseInt(nextStatData.getName().substring("time".length()));
//                    for (int j = 0; j < stats.length; j++) {
//                        dataProcessingHelper.statValues[j][timeIndex] = stats[j];
//                    }
//                }
//            }else{//must be image data
//                if(dataProcessingHelper.isInfoOnly()){
//                    dataProcessingHelper.imageNames = new ArrayList<String>();
//                    dataProcessingHelper.imageISize = new ArrayList<ISize>();
//                    dataProcessingHelper.imageOrigin = new ArrayList<Origin>();
//                    dataProcessingHelper.imageExtent = new ArrayList<Extent>();
//                    Origin imgDataOrigin;
//                    Extent imgDataExtent;
//                    HashMap<String, String> attrHashMap = getHDF5Attributes(group);
//                    if(attrHashMap.size() == 2){
//                        imgDataOrigin = new Origin(Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_ORIGINX)), 0, 0);
//                        imgDataExtent = new Extent(Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_EXTENTX)), 1, 1);//this is 1D, however the extentY, Z cannot take 0
//                    }
//                    else if(attrHashMap.size() == 4){
//                        imgDataOrigin = new Origin(Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_ORIGINX)), Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_ORIGINY)), 0);
//                        imgDataExtent = new Extent(Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_EXTENTX)), Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_EXTENTY)), 1);//this is 2D, however the extentZ cannot take 0
//                    }
//                    else if(attrHashMap.size() == 6){
//                        imgDataOrigin = new Origin(Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_ORIGINX)), Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_ORIGINY)), Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_ORIGINZ)));
//                        imgDataExtent = new Extent(Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_EXTENTX)), Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_EXTENTY)), Double.valueOf(attrHashMap.get(DATA_PROCESSING_OUTPUT_EXTENTZ)));
//                    }else{
//                        throw new Exception("Unexpected number of origin/extent values");
//                    }
//                    dataProcessingHelper.imageNames.add(hObject.getName());
//                    dataProcessingHelper.imageOrigin.add(imgDataOrigin);
//                    dataProcessingHelper.imageExtent.add(imgDataExtent);
//                    //get ISize
//                    processDims((H5ScalarDS)(((Group)hObject).getMemberList()).get(0), dataProcessingHelper,true);
//                    long[] dims = dataProcessingHelper.tempDims;
//                    ISize isize = new ISize((int)dims[0], (int)(dims.length>1?dims[1]:1), (int)(dims.length>2?dims[2]:1));
//                    dataProcessingHelper.imageISize.add(isize);
//                }else{
//                    int currentVarNameIndex = -1;
//                    for (int i = 0; i < dataProcessingHelper.specificVarNames.length; i++) {
//                        if(group.getName().equals(dataProcessingHelper.specificVarNames[i])){
//                            currentVarNameIndex = i;
//                            break;
//                        }
//                    }
//                    if(currentVarNameIndex == -1){
//                        return;//skip this group
//                    }
//                    dataProcessingHelper.specificDataValues[currentVarNameIndex] = new double[(dataProcessingHelper.specificTimePointHelper.isAllTimePoints()?dataProcessingHelper.times.length:dataProcessingHelper.specificTimePointHelper.getTimePoints().length)][];
//                    List<HObject> imageDataAtEachTime = ((Group)hObject).getMemberList();
//                    int foundTimePointIndex = 0;
//                    for(HObject nextImageData:imageDataAtEachTime){
////					if(dataProcessingHelper.isInfoOnly()){
////						printInfo(nextImageData,indent+" ");
////						processDims(nextImageData, dataProcessingHelper,true);
////						long[] dims = dataProcessingHelper.tempDims;
////						ISize isize = new ISize((int)dims[0], (int)(dims.length>1?dims[1]:1), (int)(dims.length>2?dims[2]:1));
////						dataProcessingHelper.imageISize.add(isize);
////						break;//only need 1st one for info
////					}else{
//                        int hdf5GroupTimeIndex = Integer.parseInt(nextImageData.getName().substring(SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_TIMEPREFIX.length()));
//                        if(dataProcessingHelper.specificTimePointHelper.isAllTimePoints() || dataProcessingHelper.specificTimePointHelper.getTimePoints()[foundTimePointIndex] == dataProcessingHelper.times[hdf5GroupTimeIndex]){
//
//                            int timeIndex = (dataProcessingHelper.specificTimePointHelper.isAllTimePoints()?hdf5GroupTimeIndex:foundTimePointIndex);
//                            processDims(nextImageData, dataProcessingHelper,false);
//                            long[] dims = dataProcessingHelper.tempDims;
//                            ISize isize = new ISize((int)dims[0], (int)(dims.length>1?dims[1]:1), (int)(dims.length>2?dims[2]:1));
//                            if(dataProcessingHelper.specificDataIndexHelper.isAllDataIndexes()){
//                                dataProcessingHelper.specificDataValues[currentVarNameIndex][timeIndex] = (double[])dataProcessingHelper.tempData;
//                            }else if(dataProcessingHelper.specificDataIndexHelper.isSingleSlice()){
//                                dataProcessingHelper.specificDataValues[currentVarNameIndex][timeIndex] = new double[isize.getX()*isize.getY()];
//                                System.arraycopy(
//                                        (double[])dataProcessingHelper.tempData,dataProcessingHelper.specificDataIndexHelper.getSliceIndex()*(isize.getX()*isize.getY()),
//                                        dataProcessingHelper.specificDataValues[currentVarNameIndex][timeIndex], 0, isize.getX()*isize.getY());
//                            }else{
//                                dataProcessingHelper.specificDataValues[currentVarNameIndex][timeIndex] = new double[dataProcessingHelper.specificDataIndexHelper.getDataIndexes().length];
//                                for (int i = 0; i < dataProcessingHelper.specificDataIndexHelper.getDataIndexes().length; i++) {
//                                    dataProcessingHelper.specificDataValues[currentVarNameIndex][timeIndex][i] = ((double[])dataProcessingHelper.tempData)[dataProcessingHelper.specificDataIndexHelper.getDataIndexes()[i]];
//                                }
//                            }
//                            foundTimePointIndex++;
//                            if(!dataProcessingHelper.specificTimePointHelper.isAllTimePoints() && foundTimePointIndex == dataProcessingHelper.specificTimePointHelper.getTimePoints().length){
//                                //break out after we get our data
//                                break;
//                            }
//                        }
//
////					}
//                    }
//                }
//            }
//        }else if(hObject instanceof Dataset){
//            Dataset dataset = (Dataset)hObject;
//            printInfo(dataset,indent);
//            if(dataset.getName().equals(SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_TIMES)){
//                processDims(hObject, dataProcessingHelper,false);
//                dataProcessingHelper.times = (double[])dataProcessingHelper.tempData;
//            }
//        }else if(hObject instanceof Datatype){
//            printInfo(hObject, indent);
//        }else{
//            printInfo(hObject, indent);
//        }
//    }
//    private static HashMap<String, String> getHDF5Attributes(HObject hObject) throws Exception{
//        HashMap<String, String> attrHashMap = new HashMap<String, String>();
//        List<Metadata> metaDataL = hObject.getMetadata();
//        if(metaDataL != null){
//            for (int j = 0; j < metaDataL.size(); j++) {
//                Attribute attr = (Attribute)metaDataL.get(j);
//                String attrValue = attr.toString(",");
//                //System.out.print(" "+attr.getName()+"='"+attrValue+"'");
//                attrHashMap.put(attr.getName(),attr.toString(","));
//            }
//        }
//        return attrHashMap;
//    }
//
//    private static void printInfo(HObject hObject,String indent) throws Exception{
//        if(true){return;}
//        System.out.println(indent+hObject.getName()+":"+hObject.getClass().getName());
//        List metaDatas = hObject.getMetadata();
//        for(Object metaData:metaDatas){
//            if(metaData instanceof Attribute){
//                Attribute attribute = (Attribute)metaData;
//                System.out.println(indent+"metadata="+attribute.getName()+" "+attribute.getType().getDatatypeDescription());
//            }else{
//                System.out.println(indent+"metadata="+metaData.getClass().getName());
//            }
//        }
//    }
//    private static void processDims(HObject hObject, DataSetControllerImpl.DataProcessingHelper dataProcessingHelper, boolean bInfoOnly) throws Exception{
//        H5ScalarDS h5ScalarDS = (H5ScalarDS)hObject;
//        h5ScalarDS.init();
//        dataProcessingHelper.tempDims = h5ScalarDS.getDims();
//
//        //make sure all dimensions are selected for loading if 3D
//        //note: for 3D, only 1st slice selected by default
//        long[] selectedDims = h5ScalarDS.getSelectedDims();
//        if(selectedDims != null && selectedDims.length > 2){
//            //changes internal class variable used during read
//            selectedDims[2] = dataProcessingHelper.tempDims[2];
//        }
//        if(!bInfoOnly){
//            //load all data
//            dataProcessingHelper.tempData = h5ScalarDS.read();
//        }
//
//        if(dataProcessingHelper.tempDims != null){
//            if(dataProcessingHelper.tempDims.length > 1){
//                //For HDF5View (x stored in index 1) and (y stored in index 0) so must switch back to normal assumption
//                long dimsY = dataProcessingHelper.tempDims[0];
//                dataProcessingHelper.tempDims[0] = dataProcessingHelper.tempDims[1];
//                dataProcessingHelper.tempDims[1] = dimsY;
//            }
////		//uncomment for Debug
////    	System.out.print(" dims=(");
////    	for (int j = 0; j < dataProcessingHelper.tempDims.length; j++) {
////			System.out.print((j>0?"x":"")+dataProcessingHelper.tempDims[j]);
////		}
////    	System.out.print(")");
//        }
//    }
//    private static void populateStatNamesAndUnits(HObject hObject, DataSetControllerImpl.DataProcessingHelper dataProcessingHelper) throws Exception{
//        if(!hObject.getName().equals(SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_VARIABLESTATISTICS)){
//            throw new Exception("expecting obejct name "+SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_VARIABLESTATISTICS);
//        }
//        final String NAME_ATTR = "_name";
//        final String UNIT_ATTR = "_unit";
//        final String STAT_PREFIX = "comp_";
//
//        List<Metadata> metaDataL = hObject.getMetadata();
//        if(metaDataL != null){
//            HashMap<String, String> attrHashMap = getHDF5Attributes(hObject);//map contains the same number of names and attributes
//            String[] variableStatNames = null;
//            String[] variableUnits = null;
//            Iterator<String> attrIterTemp = attrHashMap.keySet().iterator();
//            boolean bHasUnit = false;
//            for (int j = 0; j < attrHashMap.size(); j++) {
//                String compVal = attrIterTemp.next();
//                if(compVal.contains(NAME_ATTR) || compVal.contains(UNIT_ATTR)){
//                    bHasUnit = true;
//                    break;
//                }
//            }
//            if(bHasUnit){
//                variableStatNames = new String[attrHashMap.size()/2];
//                variableUnits = new String[attrHashMap.size()/2];
//            }else{
//                variableStatNames = new String[attrHashMap.size()]; // old way
//            }
//            Iterator<String> attrIter = attrHashMap.keySet().iterator();
//            for (int j = 0; j < attrHashMap.size(); j++) {
//                String compVal = attrIter.next();
//                if(compVal.contains(NAME_ATTR)){
//                    int compVarIdx = Integer.parseInt(compVal.substring(STAT_PREFIX.length(), compVal.indexOf('_', STAT_PREFIX.length())));
//                    variableStatNames[compVarIdx] = attrHashMap.get(compVal);
//                }else if(compVal.contains(UNIT_ATTR)){
//                    int compVarIdx = Integer.parseInt(compVal.substring(STAT_PREFIX.length(), compVal.indexOf('_', STAT_PREFIX.length())));
//                    variableUnits[compVarIdx] = attrHashMap.get(compVal);
//                }else{//old way for var names(e.g. comp_0 = abc) with no "_name" or "_unit"
//                    int compVarIdx = Integer.parseInt(compVal.substring(STAT_PREFIX.length()));
//                    variableStatNames[compVarIdx] = attrHashMap.get(compVal);
//                }
//            }
//            dataProcessingHelper.statVarNames = variableStatNames;
//            dataProcessingHelper.statVarUnits = variableUnits;
//        }
//    }
}
