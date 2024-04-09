package org.vcell.cli.run.hdf5;

import org.jlibsedml.Report;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data wrapper class containing the relevant data for Hdf5 
 */
public class Hdf5DataContainer {
    /**
     *  Uri to results map
     */

    public Map<Report, List<Hdf5SedmlResults>> reportToResultsMap;
    public Map<Report, String> reportToUriMap;
    public boolean trackSubSetsInReports;

    /**
     * Basic constructor
     */
    public Hdf5DataContainer(){
        this(false);
    }

    public Hdf5DataContainer(boolean shouldTrackSedmlSubSetsInReports){
        this.reportToResultsMap = new LinkedHashMap<>();
        this.reportToUriMap = new LinkedHashMap<>();
        this.trackSubSetsInReports = shouldTrackSedmlSubSetsInReports;
    }

    /**
     * Take the data of another container and fold it into this wrapper
     * 
     * @param moreData
     */
    public void incorporate(Hdf5DataContainer moreData){
        this.reportToResultsMap.putAll(moreData.reportToResultsMap);
        this.reportToUriMap.putAll(moreData.reportToUriMap);
    }
}
