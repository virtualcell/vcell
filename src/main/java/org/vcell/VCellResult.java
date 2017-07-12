package org.vcell;

import net.imagej.Dataset;

/**
 * Created by kevingaffney on 7/11/17.
 */
public class VCellResult {

    private Dataset membraneResult;
    private Dataset volumeResult;

    public VCellResult(Dataset membraneResult, Dataset volumeResult) {
        this.membraneResult = membraneResult;
        this.volumeResult = volumeResult;
    }

    public Dataset getMembraneResult() {
        return membraneResult;
    }

    public Dataset getVolumeResult() {
        return volumeResult;
    }
}
