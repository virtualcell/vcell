package org.vcell.cli.run.hdf5;

import org.jlibsedml.DataSet;
import org.jlibsedml.Report;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NonspatialDataMapping {
    private final Map<Report, Map<DataSet, List<double[]>>> dataMapping;

    public NonspatialDataMapping(){
        this.dataMapping = new LinkedHashMap<>();
    }

    public List<double[]> put(Report report, DataSet dataset, List<double[]> data){
        if (report == null || dataset == null) throw new IllegalArgumentException("No null values allowed!");
        Map<DataSet, List<double[]>> reportMap;
        if (!this.dataMapping.containsKey(report) ) {
            this.dataMapping.put(report, new LinkedHashMap<>());
        }
        reportMap = this.dataMapping.get(report);
        if (!reportMap.containsKey(dataset)) return reportMap.put(dataset, data);
        String errorMessage = String.format("Data already recorded for Report={%s}, DataSet={%s}", report.getName(), dataset.getLabel());
        throw new IllegalArgumentException(errorMessage);
    }

    public List<double[]> get(Report report, DataSet dataset){
        Map<DataSet, List<double[]>> reportMap = this.getDataSetMappings(report);
        List<double[]> result = reportMap.get(dataset);
        if (result != null) return result;
        String errorMessage = String.format("No data can be found for Report={%s}, DataSet={%s}", report.getName(), dataset.getLabel());
        throw new IllegalArgumentException(errorMessage);
    }

    public Map<DataSet, List<double[]>> getDataSetMappings(Report report){
        if (this.dataMapping.containsKey(report)) return this.dataMapping.get(report);
        throw new IllegalArgumentException(String.format("No data for Report={%s} was found", report.getName()));
    }

    public boolean isEmpty(){
        return this.dataMapping.isEmpty();
    }
}
