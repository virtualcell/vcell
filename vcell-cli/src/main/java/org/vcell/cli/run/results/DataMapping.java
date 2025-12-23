package org.vcell.cli.run.results;

import org.jlibsedml.components.output.DataSet;
import org.jlibsedml.components.output.Report;
import org.vcell.sbml.vcell.lazy.LazySBMLDataAccessor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataMapping <T extends LazySBMLDataAccessor> {
    private final Map<Report, Map<DataSet, List<T>>> dataMapping;
    public DataMapping(){
        this.dataMapping = new LinkedHashMap<>();
    }

    public List<T> put(Report report, DataSet dataset, List<? extends T> data){
        if (report == null || dataset == null) throw new IllegalArgumentException("No null values allowed!");
        Map<DataSet, List<T>> reportMap;
        if (!this.dataMapping.containsKey(report) ) {
            this.dataMapping.put(report, new LinkedHashMap<>());
        }
        reportMap = this.dataMapping.get(report);
        if (!reportMap.containsKey(dataset)) return reportMap.put(dataset, new ArrayList<>(data));
        String errorMessage = String.format("Data already recorded for Report={%s}, DataSet={%s}", report.getName(), dataset.getLabel());
        throw new IllegalArgumentException(errorMessage);
    }

    public List<T> get(Report report, DataSet dataset){
        Map<DataSet, List<T>> reportMap = this.getDataSetMappings(report);
        List<T> result = reportMap.get(dataset);
        if (result != null) return result;
        String errorMessage = String.format("No data can be found for Report={%s}, DataSet={%s}", report.getName(), dataset.getLabel());
        throw new IllegalArgumentException(errorMessage);
    }

    public Map<DataSet, List<T>> getDataSetMappings(Report report){
        if (this.dataMapping.containsKey(report)) return this.dataMapping.get(report);
        throw new IllegalArgumentException(String.format("No data for Report={%s} was found", report.getName()));
    }

    public boolean isEmpty(){
        return this.dataMapping.isEmpty();
    }
}
