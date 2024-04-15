package org.vcell.cli.run.hdf5;

import org.jlibsedml.DataSet;
import org.jlibsedml.Report;
import org.jlibsedml.Variable;

import java.util.LinkedHashMap;
import java.util.Map;

public class SpatialDataMapping {
    private final Map<Report, Map<DataSet, Hdf5DataSourceSpatialVarDataItem>> dataMapping;

    public SpatialDataMapping(){
        this.dataMapping = new LinkedHashMap<>();
    }

    public Hdf5DataSourceSpatialVarDataItem put(Report report, DataSet dataset, Hdf5DataSourceSpatialVarDataItem data){
        if (report == null || dataset == null) throw new IllegalArgumentException("No null values allowed!");
        Map<DataSet, Hdf5DataSourceSpatialVarDataItem> reportMap;
        if (!this.dataMapping.containsKey(report) ) {
            this.dataMapping.put(report, new LinkedHashMap<>());
        }
        reportMap = this.dataMapping.get(report);
        if (!reportMap.containsKey(dataset)) return reportMap.put(dataset, data);
        String errorMessage = String.format("Data already recorded for Report={%s}, DataSet={%s}", report.getName(), dataset.getLabel());
        throw new IllegalArgumentException(errorMessage);
    }

    public Hdf5DataSourceSpatialVarDataItem get(Report report, DataSet dataset){
        Map<DataSet, Hdf5DataSourceSpatialVarDataItem> reportMap = this.getDataSetMappings(report);
        Hdf5DataSourceSpatialVarDataItem result = reportMap.get(dataset);
        if (result != null) return result;
        String errorMessage = String.format("No data can be found for Report={%s}, DataSet={%s}", report.getName(), dataset.getLabel());
        throw new IllegalArgumentException(errorMessage);
    }

    public Map<DataSet, Hdf5DataSourceSpatialVarDataItem> getDataSetMappings(Report report){
        if (this.dataMapping.containsKey(report)) return this.dataMapping.get(report);
        throw new IllegalArgumentException(String.format("No data for Report={%s} was found", report.getName()));
    }

    public boolean isEmpty(){
        return this.dataMapping.isEmpty();
    }
}
