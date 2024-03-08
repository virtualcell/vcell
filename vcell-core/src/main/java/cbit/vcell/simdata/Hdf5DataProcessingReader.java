package cbit.vcell.simdata;

import java.io.File;

public interface Hdf5DataProcessingReader {
    DataOperationResults.DataProcessingOutputInfo getDataProcessingOutput(DataOperation.DataProcessingOutputInfoOP infoOP, File dataProcessingOutputFileHDF5) throws Exception;
    DataOperationResults.DataProcessingOutputDataValues getDataProcessingOutput(DataOperation.DataProcessingOutputDataValuesOP dataValuesOp, File dataProcessingOutputFileHDF5) throws Exception;
    DataOperationResults.DataProcessingOutputTimeSeriesValues getDataProcessingOutput(DataOperation.DataProcessingOutputTimeSeriesOP timeSeriesOp, File dataProcessingOutputFileHDF5) throws Exception;
}
