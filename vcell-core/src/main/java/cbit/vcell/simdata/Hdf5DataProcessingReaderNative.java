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

import static cbit.vcell.simdata.SimDataConstants.*;


public class Hdf5DataProcessingReaderNative implements Hdf5DataProcessingReader {


    @Override
    public DataOperationResults.DataProcessingOutputInfo getDataProcessingOutput(DataOperation.DataProcessingOutputInfoOP infoOP, File dataProcessingOutputFileHDF5) throws Exception {
        return (DataOperationResults.DataProcessingOutputInfo)getDataProcessingOutput_internal(infoOP, dataProcessingOutputFileHDF5);
    }

    @Override
    public DataOperationResults.DataProcessingOutputDataValues getDataProcessingOutput(DataOperation.DataProcessingOutputDataValuesOP dataValuesOp, File dataProcessingOutputFileHDF5) throws Exception {
        return (DataOperationResults.DataProcessingOutputDataValues)getDataProcessingOutput_internal(dataValuesOp, dataProcessingOutputFileHDF5);
    }

    @Override
    public DataOperationResults.DataProcessingOutputTimeSeriesValues getDataProcessingOutput(DataOperation.DataProcessingOutputTimeSeriesOP timeSeriesOp, File dataProcessingOutputFileHDF5) throws Exception {
        return (DataOperationResults.DataProcessingOutputTimeSeriesValues)getDataProcessingOutput_internal(timeSeriesOp, dataProcessingOutputFileHDF5);
    }

    private DataOperationResults getDataProcessingOutput_internal(DataOperation dataOperation, File dataProcessingOutputFileHDF5) throws Exception {
        NativeLib.HDF5.load();
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
                if(dataOperation instanceof DataOperation.DataProcessingOutputInfoOP){
                    DataSetControllerImpl.DataProcessingHelper dataProcessingHelper = new DataSetControllerImpl.DataProcessingHelper();
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
                    if(((DataOperation.DataProcessingOutputInfoOP)dataOperation).getOutputContext() != null){
                        mapFunctionNameToStateVarName = new HashMap<String, String>();
                        for (int i = 0; i < ((DataOperation.DataProcessingOutputInfoOP)dataOperation).getOutputContext().getOutputFunctions().length; i++) {
                            AnnotatedFunction annotatedFunction = ((DataOperation.DataProcessingOutputInfoOP)dataOperation).getOutputContext().getOutputFunctions()[i];
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
                    DataOperation.DataProcessingOutputDataValuesOP.DataIndexHelper dataIndexHelper = null;
                    DataOperation.DataProcessingOutputDataValuesOP.TimePointHelper timePointHelper = null;
                    if(dataOperation instanceof DataOperation.DataProcessingOutputDataValuesOP){
                        variableNames = new String[] {((DataOperation.DataProcessingOutputDataValuesOP)dataOperation).getVariableName()};
                        dataIndexHelper = ((DataOperation.DataProcessingOutputDataValuesOP)dataOperation).getDataIndexHelper();
                        timePointHelper = ((DataOperation.DataProcessingOutputDataValuesOP)dataOperation).getTimePointHelper();
                    }else if(dataOperation instanceof DataOperation.DataProcessingOutputTimeSeriesOP){
                        variableNames = ((DataOperation.DataProcessingOutputTimeSeriesOP)dataOperation).getTimeSeriesJobSpec().getVariableNames();
                        TimeSeriesJobSpec timeSeriesJobSpec = ((DataOperation.DataProcessingOutputTimeSeriesOP)dataOperation).getTimeSeriesJobSpec();
                        double[] specificTimepoints = extractTimeRange(((DataOperation.DataProcessingOutputTimeSeriesOP)dataOperation).getAllDatasetTimes(), timeSeriesJobSpec.getStartTime(), timeSeriesJobSpec.getEndTime());
                        timePointHelper = DataOperation.DataProcessingOutputDataValuesOP.TimePointHelper.createSpecificTimePointHelper(specificTimepoints);
                        timeSeriesJobSpec.initIndices();
                        dataIndexHelper = DataOperation.DataProcessingOutputDataValuesOP.DataIndexHelper.createSpecificDataIndexHelper(timeSeriesJobSpec.getIndices()[0]);
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
                        DataSetControllerImpl.FunctionHelper functionHelper = DataSetControllerImpl.getPostProcessStateVariables(foundFunction, dataProcessingOutputInfo);
                        DataSetControllerImpl.DataProcessingHelper dataProcessingHelper = new DataSetControllerImpl.DataProcessingHelper(functionHelper.postProcessStateVars,timePointHelper,dataIndexHelper);
                        iterateHDF5(root,"",dataProcessingHelper);
                        dataProcessingOutputResults =
                            DataSetControllerImpl.evaluatePostProcessFunction(dataProcessingOutputInfo, functionHelper.postProcessStateVars, dataProcessingHelper.specificDataValues,
                                    dataIndexHelper, timePointHelper, functionHelper.flattenedBoundExpression,variableNames[0]);
                    }else{
                        DataSetControllerImpl.DataProcessingHelper dataProcessingHelper =
                            new DataSetControllerImpl.DataProcessingHelper(new String[] {variableNames[0]},timePointHelper,dataIndexHelper);
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
                        DataOperation.DataProcessingOutputTimeSeriesOP dataProcessingOutputTimeSeriesOP = (DataOperation.DataProcessingOutputTimeSeriesOP)dataOperation;
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
                            DataSetControllerImpl.SpatialStatsInfo spatialStatsInfo = new DataSetControllerImpl.SpatialStatsInfo();
                            spatialStatsInfo.bWeightsValid = false;
                            timeSeriesJobResults =
                                DataSetControllerImpl.calculateStatisticsFromWhole(dataProcessingOutputTimeSeriesOP.getTimeSeriesJobSpec(), timeSeriesFormatedValuesArr, timePointHelper.getTimePoints(), spatialStatsInfo);
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
            DataSetControllerImpl.lg.error(e.getMessage(), e);
        }finally{
            if(hdf5FileFormat != null){try{hdf5FileFormat.close();}catch(Exception e){
                DataSetControllerImpl.lg.error(e.getMessage(), e);}}
        }

        return dataProcessingOutputResults;
    }

    private static double[] extractTimeRange(double[] alltimes, double startTime, double stoptime){
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


    private static void iterateHDF5(HObject hObject, String indent, DataSetControllerImpl.DataProcessingHelper dataProcessingHelper) throws Exception{
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
                    processDims((H5ScalarDS)(((Group)hObject).getMemberList()).get(0), dataProcessingHelper,true);
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
    private static void processDims(HObject hObject, DataSetControllerImpl.DataProcessingHelper dataProcessingHelper, boolean bInfoOnly) throws Exception{
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
    private static void populateStatNamesAndUnits(HObject hObject, DataSetControllerImpl.DataProcessingHelper dataProcessingHelper) throws Exception{
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
//		lg.error(e);
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
//		lg.error(e);
//	}
//
//	// Close the file.
//	try {
//		if (file_id >= 0)
//			H5.H5Fclose(file_id);
//	}
//	catch (Exception e) {
//		lg.error(e);
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

}
