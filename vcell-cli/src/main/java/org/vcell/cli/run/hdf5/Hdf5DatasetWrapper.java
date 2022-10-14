package org.vcell.cli.run.hdf5;

import java.util.ArrayList;
import java.util.List;

public class Hdf5DatasetWrapper {
    // could be a plot or a report

    public Hdf5DatasetMetadata datasetMetadata;
    public List<Hdf5JobData> jobData = new ArrayList<>();
}
