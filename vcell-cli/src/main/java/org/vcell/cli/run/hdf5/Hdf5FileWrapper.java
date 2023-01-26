package org.vcell.cli.run.hdf5;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Hdf5FileWrapper {
    public String uri; // name of sedml file
    public Map<String, Integer> pathToGroupIDTranslator;
    public List<Hdf5DatasetWrapper> datasetWrappers = new ArrayList<>();
}
