package cbit.vcell.simdata;

import cbit.vcell.solver.ode.ODESimData;
import com.google.common.io.Files;
import hdf.hdf5lib.exceptions.HDF5Exception;
import io.jhdf.HdfFile;
import io.jhdf.api.Dataset;
import org.vcell.util.ObjectNotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * This class provides methods to extract data from the HDF5 file of a Stochastic MultiTrial Nonspatial simulation.
 * The data is stored in the HDF5 file in the following format:
 * <pre>
 *     Dataset "VarNames": string[n_vars]
 *     Dataset "SimTimes": double[n_times]
 *     Dataset "StatMean": double[n_times][n_vars]
 *     Dataset "StatMin": double[n_times][n_vars]
 *     Dataset "StatMax": double[n_times][n_vars]
 *     Dataset "StatStdDev": double[n_times][n_vars]
 * </pre>
 */

public class MultiTrialNonspatialStochSimDataReader {

    public static double[] extractColumn(ODESimData odeSimData, String columnName, SummaryStatisticType summaryStatisticType) throws HDF5Exception, ObjectNotFoundException {
        File localHdf5File = null;
        try {
            byte[] hdf5FileBytes = odeSimData.getHdf5FileBytes();
            if (hdf5FileBytes != null) {
                localHdf5File = File.createTempFile("multitrial_nonspatial_stats_", ".hdf5");
                Files.write(hdf5FileBytes, localHdf5File);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp file", e);
        }
        try (HdfFile hdfFile = new HdfFile(localHdf5File)) {

            // get the variable index from the dataset["VarNames"]
            Dataset varNamesDataset = (Dataset) hdfFile.getChild("VarNames");
            String[] varNames = (String[]) varNamesDataset.getData();
            int varIndex = Arrays.stream(varNames).toList().indexOf(columnName);
            if (varIndex == -1) {
                throw new ObjectNotFoundException("Variable name not found: " + columnName);
            }

            // get the column data from the
            // dataset["StatMean"], dataset["StatMin"], dataset["StatMax"], or dataset["StatStdDev"]
            // and extract data for the specified variable index
            String statisticsDatasetName = switch (summaryStatisticType) {
                case Mean -> "StatMean";
                case Min -> "StatMin";
                case Max -> "StatMax";
                case Std -> "StatStdDev";
            };
            Dataset statisticDataset = (Dataset) hdfFile.getChild(statisticsDatasetName);
            double[][] data = (double[][]) statisticDataset.getData();
            double[] column = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                column[i] = data[i][varIndex];
            }
            return column;
        }
    }
}
