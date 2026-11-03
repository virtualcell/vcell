package org.vcell.cli.run.results;

import cbit.vcell.math.Constant;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.MathOverrides;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.SedMLDataContainer;
import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.components.output.DataSet;
import org.jlibsedml.components.output.Report;
import org.vcell.data.DataReadException;
import org.vcell.data.Dataset;
import org.vcell.data.Dimension;
import org.vcell.data.Selections;
import org.vcell.data.ValueSource;
import org.vcell.data.array.ArrayBackend;
import org.vcell.data.sedml.SedmlReports;
import org.vcell.sbml.vcell.lazy.LazySBMLDataAccessor;
import org.vcell.sbml.vcell.lazy.LazySBMLNonSpatialDataAccessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builds one {@link Dataset} per SED-ML report (shaped as {@link SedmlReports} describes) from the CLI's nonspatial
 * results, organized by data generator. For nonspatial results this is the new system's replacement for
 * {@link NonSpatialResultsConverter#prepareNonSpatialDataForHdf5} and everything between it and the HDF5 file
 * ({@code Hdf5DataPreparer} and the {@code Hdf5Sedml*} holders): what used to be assembled for one file format is now a
 * dataset any writer can take.
 * <p>
 * Ids are kept as the SED-ML has them; writers decide how to present them. Values stay lazy: a data set's values are
 * only computed when a writer reads them.
 */
public final class SedmlReportDatasets {
    private SedmlReportDatasets() { }

    private final static Logger logger = LogManager.getLogger(SedmlReportDatasets.class);

    /**
     * @param sedml                  the executed SED-ML document
     * @param results                the nonspatial results, as {@link NonSpatialResultsConverter#organizeNonSpatialResultsBySedmlDataGenerator} organizes them
     * @param allValidDataGenerators data generators that produced results of any kind; a data set whose generator is
     *                               valid but missing from {@code results} is spatial and left out, not an error
     * @param document               the SED-ML document's path within its archive
     * @param includePlots           whether 2D plots are reported too, as BioSimulators expects
     */
    public static List<Dataset> fromNonSpatialResults(SedMLDataContainer sedml, Map<DataGenerator, ValueHolder<LazySBMLNonSpatialDataAccessor>> results,
                                                      Set<DataGenerator> allValidDataGenerators, String document, boolean includePlots) {
        List<Dataset> reports = new ArrayList<>();
        if (results.isEmpty()) return reports;

        List<Report> outputs = new ArrayList<>(sedml.getSedML().getOutputs().stream().filter(Report.class::isInstance).map(Report.class::cast).toList());
        int numberOfReports = outputs.size(); // any further outputs are plots, reported as if they were reports
        if (includePlots) {
            Map<DataGenerator, ValueHolder<LazySBMLDataAccessor>> generalized = new LinkedHashMap<>();
            for (var entry : results.entrySet()) generalized.put(entry.getKey(), new ValueHolder<>(entry.getValue()));
            ResultsConverter.add2DPlotsAsReports(sedml, generalized, outputs);
        }

        for (int i = 0; i < outputs.size(); i++) {
            Report report = outputs.get(i);
            Map<DataSet, DataGenerator> dataGenerators = new LinkedHashMap<>();
            List<String> omitted = new ArrayList<>();
            for (DataSet dataSet : report.getDataSets()) {
                DataGenerator dataGenerator = sedml.findDataGeneratorById(dataSet.getDataReference());
                if (dataGenerator == null || !results.containsKey(dataGenerator)) {
                    if (dataGenerator != null && allValidDataGenerators.contains(dataGenerator)) {
                        omitted.add(dataSet.getIdAsString());
                        continue;
                    }
                    throw new RuntimeException("No data for Data Generator `" + dataSet.getDataReference() + "` can be found!");
                }
                dataGenerators.put(dataSet, dataGenerator);
            }
            Map<DataSet, ValueHolder<LazySBMLNonSpatialDataAccessor>> dataSetValues = new LinkedHashMap<>();
            for (var entry : dataGenerators.entrySet()) dataSetValues.put(entry.getKey(), results.get(entry.getValue()));
            if (dataSetValues.isEmpty()) {
                logger.warn("Report `{}` has no nonspatial data sets to report", report.getIdAsString());
                continue;
            }
            if (dataSetValues.values().iterator().next().isEmpty()) continue;
            SedmlReports.OutputType type = i < numberOfReports ? SedmlReports.OutputType.REPORT : SedmlReports.OutputType.PLOT_2D;
            reports.add(SedmlReportDatasets.reportDataset(report, type, dataGenerators, dataSetValues, omitted, document));
        }
        return reports;
    }

    private static Dataset reportDataset(Report report, SedmlReports.OutputType type, Map<DataSet, DataGenerator> dataGenerators,
                                         Map<DataSet, ValueHolder<LazySBMLNonSpatialDataAccessor>> dataSetValues, List<String> omitted, String document) {
        String prefixedId = report.getIdAsString();
        Dataset.Builder builder = SedmlReports.report(document, prefixedId, report.getName(), type);
        if (!omitted.isEmpty()) builder.attribute(SedmlReports.OMITTED_DATA_SETS, String.join(",", omitted));

        ValueHolder<LazySBMLNonSpatialDataAccessor> first = dataSetValues.values().iterator().next();
        List<Dimension.Numeric> scans = SedmlReportDatasets.scanDimensions(first.getSimulation().getMathOverrides());
        for (ValueHolder<LazySBMLNonSpatialDataAccessor> holder : dataSetValues.values())
            if (!scans.equals(SedmlReportDatasets.scanDimensions(holder.getSimulation().getMathOverrides())))
                throw new IllegalArgumentException("Data sets of report `" + prefixedId + "` come from differently scanned simulations");

        List<String> dimensions = new ArrayList<>();
        int jobs = 1;
        for (Dimension.Numeric scan : scans) {
            builder.dimension(scan);
            dimensions.add(scan.name());
            jobs *= scan.size();
        }

        int points = first.listOfResultSets.get(0).getFlatSize();
        for (ValueHolder<LazySBMLNonSpatialDataAccessor> holder : dataSetValues.values()) {
            if (holder.listOfResultSets.size() != jobs)
                throw new IllegalArgumentException("Report `" + prefixedId + "` expects " + jobs + " jobs but a data set has " + holder.listOfResultSets.size());
            for (LazySBMLNonSpatialDataAccessor accessor : holder.listOfResultSets)
                if (accessor.getFlatSize() != points)
                    throw new IllegalArgumentException("Shape of data across DataSets in report `" + prefixedId + "` are not equal! Jagged data not supported!");
        }
        builder.dimension(new Dimension.Indexed(Dimension.TIME, points));
        dimensions.add(Dimension.TIME);

        long[] shape = new long[dimensions.size()];
        for (int i = 0; i < scans.size(); i++) shape[i] = scans.get(i).size();
        shape[shape.length - 1] = points;
        for (var entry : dataSetValues.entrySet()) {
            DataSet dataSet = entry.getKey();
            DataGenerator dataGenerator = dataGenerators.get(dataSet);
            builder.variable(SedmlReports.dataSet(dataSet.getIdAsString(), dataSet.getLabel(), dataSet.getName(),
                            dataGenerator.getIdAsString(), dataGenerator.getName(), dimensions),
                    SedmlReportDatasets.jobsSource(entry.getValue().listOfResultSets, points, shape));
        }
        return builder.build();
    }

    /**
     * @return one dimension per scanned constant, holding the values it takes. The jobs of a scan run over the scanned
     * constants in sorted order, the first outermost (see {@link MathOverrides#parameterScanCoordinateToScanIndex}), so
     * that is the order here.
     * <p>
     * Unlike the legacy converter, which looked each constant's values up by job index and named the constants in hash
     * order, this is also right when more than one constant is scanned.
     */
    static List<Dimension.Numeric> scanDimensions(MathOverrides overrides) {
        String[] scanned = overrides.getScannedConstantNames();
        Arrays.sort(scanned);
        List<Dimension.Numeric> scans = new ArrayList<>();
        for (String constant : scanned) {
            Constant[] values = overrides.getConstantArraySpec(constant).getConstants();
            double[] parameterValues = new double[values.length];
            for (int i = 0; i < values.length; i++) {
                try {
                    parameterValues[i] = values[i].getExpression().evaluateConstant();
                } catch (ExpressionException e) {
                    throw new RuntimeException("Scan value " + i + " of `" + constant + "` is not a constant", e);
                }
            }
            scans.add(new Dimension.Numeric(constant, parameterValues));
        }
        return scans;
    }

    /** Reads every job's values in job order, each NaN-padded to {@code points} as the time courses may be shorter. */
    private static ValueSource jobsSource(List<LazySBMLNonSpatialDataAccessor> jobs, int points, long[] shape) {
        return (dimensions, selection) -> {
            double[] values = new double[Math.multiplyExact(jobs.size(), points)];
            Arrays.fill(values, Double.NaN);
            for (int job = 0; job < jobs.size(); job++) {
                double[] data;
                try {
                    data = jobs.get(job).getData().data();
                } catch (Exception e) {
                    throw new DataReadException("Unable to compute job " + job + " of a data set", e);
                }
                if (data.length > points) throw new DataReadException("Job " + job + " produced " + data.length + " points, expected at most " + points);
                System.arraycopy(data, 0, values, job * points, data.length);
            }
            return Selections.apply(ArrayBackend.DEFAULT.wrap(values, shape), dimensions, selection);
        };
    }
}
