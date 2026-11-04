package org.vcell.cli.run.results;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.SedMLDataContainer;
import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.components.output.DataSet;
import org.jlibsedml.components.output.Report;
import org.vcell.data.Dataset;
import org.vcell.data.Dimension;
import org.vcell.data.sedml.SedmlOutputs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builds one {@link Dataset} per SED-ML report (shaped as {@link SedmlOutputs} describes) from data generator datasets
 * (see {@link SedmlDataGenerators}). For nonspatial results this is the new system's replacement for
 * {@link NonSpatialResultsConverter#prepareNonSpatialDataForHdf5} and everything between it and the HDF5 file
 * ({@code Hdf5DataPreparer} and the {@code Hdf5Sedml*} holders), and for what {@code RunUtils.generateReportsAsCSV}
 * gathers: what used to be assembled for one file format is now a dataset any writer can take.
 * <p>
 * Ids are kept as the SED-ML has them; writers decide how to present them. Values stay lazy.
 */
public final class SedmlReportDatasets {
    private SedmlReportDatasets() { }

    private final static Logger logger = LogManager.getLogger(SedmlReportDatasets.class);

    /**
     * @param sedml                 the executed SED-ML document
     * @param dataGenerators        the document's data generators with nonspatial values
     * @param spatialDataGenerators data generators with spatial values; a data set using one is left out of its
     *                              report (and recorded as omitted) rather than being an error
     * @param document              the SED-ML document's path within its archive
     */
    public static List<Dataset> fromDataGenerators(SedMLDataContainer sedml, Map<DataGenerator, Dataset> dataGenerators,
                                                   Set<DataGenerator> spatialDataGenerators, String document) {
        List<Dataset> reports = new ArrayList<>();
        if (dataGenerators.isEmpty()) return reports;
        for (Report report : sedml.getSedML().getOutputs().stream().filter(Report.class::isInstance).map(Report.class::cast).toList()) {
            Map<DataSet, DataGenerator> used = new LinkedHashMap<>();
            List<String> omitted = new ArrayList<>();
            for (DataSet dataSet : report.getDataSets()) {
                DataGenerator dataGenerator = sedml.findDataGeneratorById(dataSet.getDataReference());
                if (dataGenerator != null && spatialDataGenerators.contains(dataGenerator) && !dataGenerators.containsKey(dataGenerator)) {
                    omitted.add(dataSet.getIdAsString());
                    continue;
                }
                if (dataGenerator == null || !dataGenerators.containsKey(dataGenerator))
                    throw new RuntimeException("No data for Data Generator `" + dataSet.getDataReference() + "` can be found!");
                used.put(dataSet, dataGenerator);
            }
            if (used.isEmpty()) {
                logger.warn("Report `{}` has no nonspatial data sets to report", report.getIdAsString());
                continue;
            }
            reports.add(SedmlReportDatasets.reportDataset(report, used, dataGenerators, omitted, document));
        }
        return reports;
    }

    private static Dataset reportDataset(Report report, Map<DataSet, DataGenerator> used, Map<DataGenerator, Dataset> dataGenerators,
                                         List<String> omitted, String document) {
        String id = report.getIdAsString();
        Dataset.Builder builder = SedmlOutputs.report(document, id, report.getName(), SedmlOutputs.OutputType.REPORT);
        if (!omitted.isEmpty()) builder.attribute(SedmlOutputs.OMITTED_DATA_SETS, String.join(",", omitted));

        // every data set is reported over the same jobs, padded to the longest time course
        List<Dimension> jobs = SedmlDataGenerators.jobDimensions(dataGenerators.get(used.values().iterator().next()));
        int points = 0;
        for (DataGenerator dataGenerator : used.values()) {
            Dataset values = dataGenerators.get(dataGenerator);
            if (!SedmlDataGenerators.jobDimensions(values).equals(jobs))
                throw new IllegalArgumentException("Data sets of report `" + id + "` come from differently scanned simulations");
            points = Math.max(points, values.dimension(Dimension.TIME).size());
        }
        jobs.forEach(builder::dimension);
        builder.dimension(new Dimension.Indexed(Dimension.TIME, points));
        List<String> dimensions = new ArrayList<>(jobs.stream().map(Dimension::name).toList());
        dimensions.add(Dimension.TIME);

        for (var entry : used.entrySet()) {
            DataSet dataSet = entry.getKey();
            DataGenerator dataGenerator = entry.getValue();
            builder.variable(SedmlOutputs.dataSet(dataSet.getIdAsString(), dataSet.getLabel(), dataSet.getName(),
                            dataGenerator.getIdAsString(), dataGenerator.getName(), dimensions),
                    SedmlDataGenerators.padded(dataGenerators.get(dataGenerator), points));
        }
        return builder.build();
    }
}
