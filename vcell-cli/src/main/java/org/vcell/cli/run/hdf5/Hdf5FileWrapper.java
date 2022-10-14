package org.vcell.cli.run.hdf5;

import java.util.ArrayList;
import java.util.List;

public class Hdf5FileWrapper {
    public String combineArchiveLocation; // name of sedml file
    public String uri; // name of sedml file
    public List<Hdf5DatasetWrapper> datasetWrappers = new ArrayList<>();
}
