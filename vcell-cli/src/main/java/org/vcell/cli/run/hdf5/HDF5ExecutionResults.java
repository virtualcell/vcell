package org.vcell.cli.run.hdf5;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.SedML;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HDF5ExecutionResults implements Iterable<SedML>{
    private final static Logger logger = LogManager.getLogger(HDF5ExecutionResults.class);
    private final Map<SedML, Hdf5DataContainer> executionResultsMapping;
    public boolean isBioSimHdf5;

    public HDF5ExecutionResults(boolean isBioSimHdf5){
        this.executionResultsMapping = new HashMap<>();
        this.isBioSimHdf5 = isBioSimHdf5;
    }

    public void addResults(SedML sedml, Hdf5DataContainer dataContainer){
        if (this.executionResultsMapping.containsKey(sedml)) logger.warn("Overwriting Results...");
        this.executionResultsMapping.put(sedml, dataContainer);
    }

    public Hdf5DataContainer getData(SedML sedml){
        if (!this.executionResultsMapping.containsKey(sedml)) throw new RuntimeException("No data for requested SED-ML!");
        return this.executionResultsMapping.get(sedml);
    }

    @Override
    public Iterator<SedML> iterator() {
        return this.executionResultsMapping.keySet().iterator();
    }
}
