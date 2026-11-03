package org.vcell.cli.run.hdf5;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.SedMLDataContainer;
import org.vcell.data.Dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HDF5ExecutionResults implements Iterable<SedMLDataContainer>{
    private final static Logger logger = LogManager.getLogger(HDF5ExecutionResults.class);
    private final Map<SedMLDataContainer, Hdf5DataContainer> executionResultsMapping;
    public boolean isBioSimHdf5;
    /**
     * The same reports as the new system's datasets, collected alongside the legacy containers so both can be written
     * and compared; null when not collecting. See {@link org.vcell.cli.run.ExecutionJob#UNIFIED_REPORTS_PROPERTY}.
     */
    public final List<Dataset> reportDatasets;

    public HDF5ExecutionResults(boolean isBioSimHdf5){
        this(isBioSimHdf5, false);
    }

    public HDF5ExecutionResults(boolean isBioSimHdf5, boolean collectReportDatasets){
        this.executionResultsMapping = new HashMap<>();
        this.isBioSimHdf5 = isBioSimHdf5;
        this.reportDatasets = collectReportDatasets ? new ArrayList<>() : null;
    }

    public void addResults(SedMLDataContainer sedml, Hdf5DataContainer dataContainer){
        if (this.executionResultsMapping.containsKey(sedml)) logger.warn("Overwriting Results...");
        this.executionResultsMapping.put(sedml, dataContainer);
    }

    public Hdf5DataContainer getData(SedMLDataContainer sedml){
        if (!this.executionResultsMapping.containsKey(sedml)) throw new RuntimeException("No data for requested SED-ML!");
        return this.executionResultsMapping.get(sedml);
    }

    @Override
    public Iterator<SedMLDataContainer> iterator() {
        return this.executionResultsMapping.keySet().iterator();
    }
}
