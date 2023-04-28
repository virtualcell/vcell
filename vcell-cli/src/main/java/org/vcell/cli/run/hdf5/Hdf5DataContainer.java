package org.vcell.cli.run.hdf5;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data wrapper class containing the relevant data for Hdf5 
 */
public class Hdf5DataContainer {
    /**
     *  Uri to results map
     */
    public Map<String, List<Hdf5SedmlResults>> uriToResultsMap;

    /**
     * Basic constructor
     */
    public Hdf5DataContainer(){
        this.uriToResultsMap = new HashMap<>();
    }

    /**
     * Take the data of another container and fold it into this wrapper
     * 
     * @param moreData
     */
    public void incorporate(Hdf5DataContainer moreData){
        uriToResultsMap.putAll(moreData.uriToResultsMap);
    }
}
