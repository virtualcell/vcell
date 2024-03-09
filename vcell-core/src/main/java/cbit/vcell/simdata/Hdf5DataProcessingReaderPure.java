package cbit.vcell.simdata;

import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.DataOperationResults.DataProcessingOutputInfo.PostProcessDataType;
import cbit.vcell.solver.AnnotatedFunction;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.document.TSJobResultsNoStats;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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
        String[] varNames = timeSeriesOp.getTimeSeriesJobSpec().getVariableNames();
        int[][] meshIndices = timeSeriesOp.getTimeSeriesJobSpec().getIndices(); // shape is (n_variables, n_indices)
        double startTime = timeSeriesOp.getTimeSeriesJobSpec().getStartTime();
        double endTime = timeSeriesOp.getTimeSeriesJobSpec().getEndTime();
        int step = timeSeriesOp.getTimeSeriesJobSpec().getStep();
        double[] allTimes = postProcessing.times;
        // find indices of allTimes which are in the range [startTime, endTime]
        List<Integer> timeIndices = new ArrayList<>();
        for (int i = 0; i < allTimes.length; i++) {
            if (allTimes[i] >= startTime && allTimes[i] <= endTime) {
                timeIndices.add(i);
            }
        }
        // if step is not one, trim timeIndices to only include every step-th index
        if (step != 1) {
            List<Integer> newTimeIndices = new ArrayList<>();
            for (int i = 0; i < timeIndices.size(); i += step) {
                newTimeIndices.add(timeIndices.get(i));
            }
            timeIndices = newTimeIndices;
        }
        // extract the time points
        double[] timePoints = timeIndices.stream().map(i -> allTimes[i]).mapToDouble(d -> d).toArray();
        // loop over varNames and timeIndices to extract the data
        double[][][] timeSeriesFormatedValuesArr = new double[varNames.length][meshIndices[0].length + 1][timePoints.length];
        for (int varIndex = 0; varIndex < varNames.length; varIndex++) {
            String varName = varNames[varIndex];
            Hdf5PostProcessor.ImageMetadata imageMetadata = postProcessing.imageMetadata.stream().filter(im -> im.name.equals(varName)).findFirst().orElse(null);

            // add time array as the first element of the values array for this variable
            timeSeriesFormatedValuesArr[varIndex][0] = timePoints;

            // loop over time indices to extract the images for each time point
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
                // loop over the 1D indices to set the values for this time point and variable
                for (int i=0; i < meshIndices[varIndex].length; i++){
                    int meshIndex = meshIndices[varIndex][i];
                    timeSeriesFormatedValuesArr[varIndex][i + 1][timeIndex] = values1D[meshIndex];
                }
            }
        }
        TSJobResultsNoStats timeSeriesJobResults = new TSJobResultsNoStats(varNames, meshIndices, timePoints, timeSeriesFormatedValuesArr);
        return new DataOperationResults.DataProcessingOutputTimeSeriesValues(timeSeriesOp.getVCDataIdentifier(), timeSeriesJobResults);
    }
}
