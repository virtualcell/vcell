package org.vcell.cli.run.results;

import cbit.vcell.math.Constant;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.TempSimulation;
import org.jlibsedml.SedMLDataContainer;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.Variable;
import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.components.output.AbstractCurve;
import org.jlibsedml.components.output.Curve;
import org.jlibsedml.components.output.DataSet;
import org.jlibsedml.components.output.Output;
import org.jlibsedml.components.output.Plot2D;
import org.jlibsedml.components.output.Report;
import org.jlibsedml.components.simulation.Simulation;
import org.jlibsedml.components.simulation.UniformTimeCourse;
import org.jlibsedml.components.task.AbstractTask;
import org.jlibsedml.components.task.RepeatedTask;
import org.jlibsedml.components.task.Task;
import org.vcell.cli.run.TaskJob;
import org.vcell.data.DataReadException;
import org.vcell.data.Dataset;
import org.vcell.data.Dimension;
import org.vcell.data.Expressions;
import org.vcell.data.Selection;
import org.vcell.data.Selections;
import org.vcell.data.ValueSource;
import org.vcell.data.array.ArrayBackend;
import org.vcell.data.array.DoubleArrayND;
import org.vcell.data.sedml.SedmlOutputs;
import org.vcell.sbml.vcell.NonSpatialSBMLSimResults;
import org.vcell.sbml.vcell.lazy.LazySBMLNonSpatialDataAccessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Evaluates SED-ML data generators over the CLI's nonspatial solver results, giving each data generator a
 * {@link Dataset} of its own: one variable, named by the data generator's id, over its task's scan dimensions (see
 * {@link #scanDimensions}) and {@link Dimension#TIME}, as long as its time course. Reports and plots are assembled from
 * these (see {@link SedmlReportDatasets}, {@link SedmlPlotDatasets}), each padding to its own length.
 * <p>
 * For the new data system this replaces {@link NonSpatialResultsConverter#organizeNonSpatialResultsBySedmlDataGenerator}
 * and {@link SimpleDataGenCalculator}, evaluating each data generator's math the same way, point by point. Values stay
 * lazy: nothing is computed until a writer reads it.
 */
public final class SedmlDataGenerators {
    private SedmlDataGenerators() { }

    /**
     * @return a dataset for every data generator an output of {@code sedml} uses whose values are nonspatial results,
     * keyed by data generator, in the order the outputs first use them
     */
    public static Map<DataGenerator, Dataset> fromNonSpatialResults(SedMLDataContainer sedml, Map<TaskJob, NonSpatialSBMLSimResults> results,
                                                                    Map<AbstractTask, TempSimulation> taskToSimulation) {
        Map<DataGenerator, Dataset> dataGenerators = new LinkedHashMap<>();
        if (results.isEmpty()) return dataGenerators;
        for (DataGenerator dataGenerator : SedmlDataGenerators.usedDataGenerators(sedml)) {
            Dataset values = SedmlDataGenerators.dataGenerator(sedml, dataGenerator, results, taskToSimulation);
            if (values != null) dataGenerators.put(dataGenerator, values);
        }
        return dataGenerators;
    }

    private static Set<DataGenerator> usedDataGenerators(SedMLDataContainer sedml) {
        Set<DataGenerator> used = new LinkedHashSet<>();
        for (Output output : ResultsConverter.getValidOutputs(sedml)) {
            if (output instanceof Report report) {
                for (DataSet dataSet : report.getDataSets())
                    used.add(SedmlDataGenerators.required(sedml, dataSet.getDataReference()));
            } else if (output instanceof Plot2D plot) {
                for (AbstractCurve abstractCurve : plot.getCurves()) {
                    if (!(abstractCurve instanceof Curve curve)) continue;
                    used.add(SedmlDataGenerators.required(sedml, curve.getXDataReference()));
                    used.add(SedmlDataGenerators.required(sedml, curve.getYDataReference()));
                }
            }
        }
        return used;
    }

    private static DataGenerator required(SedMLDataContainer sedml, SId id) {
        DataGenerator dataGenerator = sedml.findDataGeneratorById(id);
        if (dataGenerator == null) throw new RuntimeException("Requested data generator `" + id + "` does not exist");
        return dataGenerator;
    }

    /** @return the data generator's dataset, or null when its values are not nonspatial results */
    private static Dataset dataGenerator(SedMLDataContainer sedml, DataGenerator dataGenerator, Map<TaskJob, NonSpatialSBMLSimResults> results,
                                         Map<AbstractTask, TempSimulation> taskToSimulation) {
        List<String> symbols = new ArrayList<>();
        List<Supplier<DoubleArrayND>> arguments = new ArrayList<>();
        AbstractTask scannedTask = null;
        int jobs = 0, points = 0;
        for (Variable variable : dataGenerator.getVariables()) {
            AbstractTask task = sedml.findAbstractTaskById(variable.getTaskReference());
            if (task == null) throw new RuntimeException("Task referenced by variable `" + variable.getIdAsString() + "` could not be found!");
            Task baseTask = sedml.findBaseTaskByAbstractTaskId(variable.getTaskReference());
            Simulation simulation = baseTask == null ? null : sedml.findSimulationById(baseTask.getSimulationReference());
            if (!(simulation instanceof UniformTimeCourse timeCourse))
                throw new IllegalArgumentException("Variable `" + variable.getIdAsString() + "`: only uniform time course simulations are supported");
            if (results.keySet().stream().noneMatch(taskJob -> taskJob.getTaskId().equals(task.getId()))) return null; // e.g. spatial

            // a repeated task has a job per scan point, in the order they ran; a plain task has one
            List<NonSpatialSBMLSimResults> taskJobs = new ArrayList<>();
            for (var entry : results.entrySet()) {
                if (entry.getValue() == null || !entry.getKey().getTaskId().equals(task.getId())) continue;
                taskJobs.add(entry.getValue());
                if (!(task instanceof RepeatedTask)) break;
            }
            symbols.add(variable.getIdAsString());
            if (taskJobs.isEmpty()) { // the task ran but failed: the variable has no values
                arguments.add(null);
                continue;
            }
            if (scannedTask == null) scannedTask = task;
            int length = timeCourse.getNumberOfSteps() + 1;
            String vcellSymbol = ResultsConverter.convertToVCellSymbol(variable);
            List<LazySBMLNonSpatialDataAccessor> accessors = taskJobs.stream().map(r -> r.getSBMLDataAccessor(vcellSymbol, timeCourse, length)).toList();
            arguments.add(() -> SedmlDataGenerators.read(accessors, length));
            jobs = Math.max(jobs, accessors.size());
            points = Math.max(points, length);
        }
        if (scannedTask == null) return null;

        List<Dimension> dimensions = new ArrayList<>();
        List<Dimension.Numeric> scans = SedmlDataGenerators.scanDimensions(taskToSimulation.get(scannedTask).getMathOverrides());
        long scanPoints = scans.stream().mapToLong(Dimension::size).reduce(1, Math::multiplyExact);
        if (scanPoints == jobs) dimensions.addAll(scans);
        else dimensions.add(new Dimension.Indexed(Dimension.JOB, jobs)); // jobs that are not a VCell scan
        dimensions.add(new Dimension.Indexed(Dimension.TIME, points));

        String id = dataGenerator.getIdAsString();
        Dataset.Builder builder = Dataset.builder(id);
        dimensions.forEach(builder::dimension);
        long[] shape = dimensions.stream().mapToLong(Dimension::size).toArray();
        ValueSource jobsByPoints = Expressions.evaluate(dataGenerator.getMathAsString(), symbols, arguments, new long[]{jobs, points});
        ValueSource values = (names, selection) -> Selections.apply(
                ArrayBackend.DEFAULT.wrap(jobsByPoints.read(List.of(Dimension.JOB, Dimension.TIME), Selection.all()).toArray(), shape), names, selection);
        builder.variable(org.vcell.data.Variable.data(id, dimensions.stream().map(Dimension::name).toList()), values);
        return builder.build();
    }

    /** @return one job per row, each {@code length} points of the variable's time course */
    private static DoubleArrayND read(List<LazySBMLNonSpatialDataAccessor> jobs, int length) {
        double[] values = new double[Math.multiplyExact(jobs.size(), length)];
        Arrays.fill(values, Double.NaN);
        for (int job = 0; job < jobs.size(); job++) {
            double[] data;
            try {
                data = jobs.get(job).getData().data();
            } catch (Exception e) {
                throw new DataReadException("Unable to read job " + job + " of a variable", e);
            }
            System.arraycopy(data, 0, values, job * length, Math.min(length, data.length));
        }
        return ArrayBackend.DEFAULT.wrap(values, jobs.size(), length);
    }

    /**
     * @return the values of a data generator's dataset padded with NaN along {@link Dimension#TIME} to {@code points},
     * so outputs can line up data generators with time courses of different lengths
     */
    static ValueSource padded(Dataset dataGenerator, int points) {
        List<Dimension> dimensions = dataGenerator.dimensions();
        int length = dataGenerator.dimension(Dimension.TIME).size();
        long[] shape = dimensions.stream().mapToLong(d -> d.name().equals(Dimension.TIME) ? points : d.size()).toArray();
        return (names, selection) -> {
            double[] values = dataGenerator.values(dataGenerator.name()).toArray();
            double[] paddedValues = new double[Math.toIntExact(values.length / length * (long) points)];
            Arrays.fill(paddedValues, Double.NaN);
            for (int row = 0; row < values.length / length; row++) System.arraycopy(values, row * length, paddedValues, row * points, length);
            return Selections.apply(ArrayBackend.DEFAULT.wrap(paddedValues, shape), names, selection);
        };
    }

    /** @return the dimensions of a data generator's dataset other than {@link Dimension#TIME}: its scan, or jobs */
    static List<Dimension> jobDimensions(Dataset dataGenerator) {
        return dataGenerator.dimensions().stream().filter(d -> !d.name().equals(Dimension.TIME)).toList();
    }

    /**
     * @return one dimension per scanned constant, holding the values it takes. The jobs of a scan run over the scanned
     * constants in sorted order, the first outermost (see {@link MathOverrides#parameterScanCoordinateToScanIndex}), so
     * that is the order here.
     * <p>
     * Unlike the legacy converter, which looked each constant's values up by job index and named the constants in hash
     * order, this is also right when more than one constant is scanned (https://github.com/virtualcell/vcell/issues/2190).
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

    /** @return the SED-ML symbol the data generator stands for, when it is a single symbol variable */
    static String symbol(DataGenerator dataGenerator) {
        if (dataGenerator.getVariables().size() != 1) return null;
        Variable variable = dataGenerator.getVariables().get(0);
        return variable.isSymbol() ? variable.getSymbol().getUrn() : null;
    }
}
