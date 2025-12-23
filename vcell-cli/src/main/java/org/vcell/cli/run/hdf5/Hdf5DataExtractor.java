package org.vcell.cli.run.hdf5;

import cbit.vcell.solver.TempSimulation;

import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.components.output.Report;
import org.jlibsedml.SedMLDataClass;
import org.jlibsedml.components.task.AbstractTask;
import org.vcell.cli.run.results.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.sbml.vcell.lazy.LazySBMLNonSpatialDataAccessor;
import org.vcell.sbml.vcell.lazy.LazySBMLSpatialDataAccessor;

import java.nio.file.Paths;
import java.util.*;

/**
 * Factory class to create Hdf5DataWrappers from a sedml object and simulation data.
 */
public class Hdf5DataExtractor {
    private final SedMLDataClass sedml;
    private final Map<AbstractTask, TempSimulation> taskToSimulationMap;
    private final String sedmlLocation;

    private final static Logger logger = LogManager.getLogger(Hdf5DataExtractor.class);

    /**
     * Constructor to initialize the factory for a given set of simulation and model data.
     * 
     * @param sedml the sedml object to get outputs, datasets, and data generators from.
     * @param taskToSimulationMap mapping of task to its simulation data
     */
    public Hdf5DataExtractor(SedMLDataClass sedml, Map<AbstractTask, TempSimulation> taskToSimulationMap){
        this.sedml = sedml;
        this.taskToSimulationMap = taskToSimulationMap;
        this.sedmlLocation = Paths.get(sedml.getPathForURI(), sedml.getFileName()).toString();
    }

    /**
     * 
     * @param organizedNonSpatialResults the nonspatial results set of a sedml execution
     * @param organizedSpatialResults the spatial results set of a sedml execution
     * @return a wrapper for hdf5 relevant data
     * @see NonSpatialResultsConverter ::convertNonspatialResultsToSedmlFormat
     * @see SpatialResultsConverter ::collectSpatialDatasets
     */
    public Hdf5DataContainer extractHdf5RelevantData(Map<DataGenerator, ValueHolder<LazySBMLNonSpatialDataAccessor>> organizedNonSpatialResults, Map<DataGenerator, ValueHolder<LazySBMLSpatialDataAccessor>> organizedSpatialResults, boolean isBioSimMode) {
        Map<Report, List<Hdf5SedmlResults>> wrappers = new LinkedHashMap<>();
        Hdf5DataContainer hdf5FileWrapper = new Hdf5DataContainer(isBioSimMode);
        Exception nonSpatialException = null, spatialException = null;
        Set<DataGenerator> allValidDataGenerators = new HashSet<>(organizedNonSpatialResults.keySet());
        allValidDataGenerators.addAll(organizedSpatialResults.keySet());

        if (!organizedNonSpatialResults.isEmpty()){
            try {
                Map<Report, List<Hdf5SedmlResults>> nonSpatialWrappers = NonSpatialResultsConverter.prepareNonSpatialDataForHdf5(
                        this.sedml, organizedNonSpatialResults, allValidDataGenerators, this.sedmlLocation, isBioSimMode);
                Hdf5DataExtractor.addWrappers(wrappers, nonSpatialWrappers);
            } catch (Exception e){
                logger.warn("Collection of non-spatial datasets failed for " + this.sedml.getFileName(), e);
                nonSpatialException = e;
            }
        }

        try {
            Map<Report, List<Hdf5SedmlResults>> spatialWrappers = SpatialResultsConverter.prepareSpatialDataForHdf5(
                    this.sedml, organizedSpatialResults, allValidDataGenerators, this.sedmlLocation, isBioSimMode);
            Hdf5DataExtractor.addWrappers(wrappers, spatialWrappers);

        } catch (Exception e){
            logger.warn("Collection of spatial datasets failed for " + this.sedml.getFileName(), e);
            spatialException = e;
        }

        if (nonSpatialException != null && spatialException != null){
            throw new RuntimeException("Encountered complete dataset collection failure;\nNonSpatial Reported:\n" + nonSpatialException.getMessage()
                + "\nSpatial Reported:\n" + spatialException.getMessage());
        } else if (nonSpatialException != null || spatialException != null){
            Exception exception = nonSpatialException == null ? spatialException : nonSpatialException;
            throw new RuntimeException("Encountered " + (nonSpatialException == null ? "spatial" : "non-spatial")
                + " dataset collection failure.", exception);
        } // else no problem

        Hdf5DataExtractor.addWrappers(hdf5FileWrapper.reportToResultsMap, wrappers);
        for (Report report : wrappers.keySet()){
            hdf5FileWrapper.reportToUriMap.put(report, this.sedmlLocation);
        }

        return hdf5FileWrapper;
    }

    private static void addWrappers(Map<Report, List<Hdf5SedmlResults>> wrappers, Map<Report, List<Hdf5SedmlResults>> wrappersToAdd){
        for (Report potentiallyNewReport : wrappersToAdd.keySet()){
            if (!wrappers.containsKey(potentiallyNewReport)) wrappers.put(potentiallyNewReport, new LinkedList<>());
            wrappers.get(potentiallyNewReport).addAll(wrappersToAdd.get(potentiallyNewReport));
        }
    }
}
