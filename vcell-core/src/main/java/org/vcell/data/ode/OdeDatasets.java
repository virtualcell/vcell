package org.vcell.data.ode;

import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.LangevinBatchResultSet;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import org.vcell.data.DataReadException;
import org.vcell.data.Dataset;
import org.vcell.data.DatasetOps;
import org.vcell.data.Dimension;
import org.vcell.data.Variable;
import org.vcell.data.array.ArrayBackend;
import org.vcell.data.array.DoubleArrayND;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts between nonspatial results in their existing forms ({@link RowColumnResultSet} and its subclasses
 * {@link ODESolverResultSet} and {@link ODESimData}, plus {@link LangevinBatchResultSet}) and {@link Dataset}.
 * <p>
 * The existing forms encode their axes implicitly: time is a column named {@code t}, trials replace it with a
 * column named {@code TrialNo}, and Langevin statistics are four separate result sets. Here each becomes a real
 * dimension: {@link Dimension#TIME}, {@link Dimension#TRIAL} and {@link Dimension#STATISTIC}.
 */
public final class OdeDatasets {
	private OdeDatasets() { }

	public static final String TIME_COLUMN = "t";
	public static final String TRIAL_COLUMN = SimDataConstants.HISTOGRAM_INDEX_NAME;

	/** Attribute keys carried over from the result set's column descriptions and {@link ODESimData}. */
	public static final String DISPLAY_NAME = "displayName";
	public static final String PARAMETER_NAME = "parameterName";
	public static final String MATH_NAME = "mathName";
	public static final String FORMAT_ID = "formatID";

	/** Langevin statistics, in the order {@link #fromLangevinBatch} stacks them. */
	public static final List<String> LANGEVIN_STATISTICS = List.of("mean", "min", "max", "std");

	public static Dataset fromResultSet(String name, RowColumnResultSet resultSet) {
		return OdeDatasets.fromResultSet(name, resultSet, ArrayBackend.DEFAULT);
	}

	/**
	 * Every column other than the index column becomes a variable over the index dimension. Function columns are
	 * evaluated now and keep their expression; their values are what {@link RowColumnResultSet#extractColumn} gives.
	 *
	 * @throws IllegalArgumentException if the result set has neither a {@code t} nor a {@code TrialNo} column
	 * @throws DataReadException        if a function column cannot be evaluated
	 */
	public static Dataset fromResultSet(String name, RowColumnResultSet resultSet, ArrayBackend backend) {
		int timeColumn = resultSet.findColumn(TIME_COLUMN);
		int trialColumn = resultSet.findColumn(TRIAL_COLUMN);
		int indexColumn = timeColumn >= 0 ? timeColumn : trialColumn;
		if (indexColumn < 0)
			throw new IllegalArgumentException("Result set has neither a '" + TIME_COLUMN + "' nor a '" + TRIAL_COLUMN + "' column");
		String indexDimension = timeColumn >= 0 ? Dimension.TIME : Dimension.TRIAL;

		Dataset.Builder builder = Dataset.builder(name)
				.dimension(new Dimension.Numeric(indexDimension, OdeDatasets.extract(resultSet, indexColumn)));
		if (resultSet instanceof ODESimData odeSimData) {
			if (odeSimData.getMathName() != null) builder.attribute(MATH_NAME, odeSimData.getMathName());
			if (odeSimData.getFormatID() != null) builder.attribute(FORMAT_ID, odeSimData.getFormatID());
		}

		long rows = resultSet.getRowCount();
		ColumnDescription[] columns = resultSet.getColumnDescriptions();
		for (int column = 0; column < columns.length; column++) {
			if (column == indexColumn) continue;
			ColumnDescription description = columns[column];
			Variable variable = OdeDatasets.variableFor(description, indexDimension);
			builder.variable(variable, backend.wrap(OdeDatasets.extract(resultSet, column), rows));
		}
		return builder.build();
	}

	/**
	 * Stacks the result sets of a parameter scan, one per job, along a {@link Dimension#JOB} dimension. All jobs must
	 * share columns and index values. Parameter values per job can be added afterwards as
	 * {@link Variable.Role#COORDINATE} variables over {@code job}.
	 */
	public static Dataset fromJobs(String name, List<? extends RowColumnResultSet> jobs) {
		List<Dataset> parts = new ArrayList<>();
		for (int job = 0; job < jobs.size(); job++) parts.add(OdeDatasets.fromResultSet(name + "_job" + job, jobs.get(job)));
		return DatasetOps.stack(name, new Dimension.Indexed(Dimension.JOB, jobs.size()), parts);
	}

	/**
	 * The Langevin average, minimum, maximum and standard deviation result sets as one dataset with a
	 * {@link Dimension#STATISTIC} dimension labelled {@link #LANGEVIN_STATISTICS}. Cluster results have different
	 * columns and are not included.
	 */
	public static Dataset fromLangevinBatch(String name, LangevinBatchResultSet batch) {
		List<ODESimData> statistics = List.of(batch.getOdeSimDataAvg(), batch.getOdeSimDataMin(), batch.getOdeSimDataMax(), batch.getOdeSimDataStd());
		List<Dataset> parts = new ArrayList<>();
		for (int i = 0; i < statistics.size(); i++) {
			if (statistics.get(i) == null) throw new IllegalArgumentException("Langevin batch is missing its " + LANGEVIN_STATISTICS.get(i) + " results");
			parts.add(OdeDatasets.fromResultSet(name + "_" + LANGEVIN_STATISTICS.get(i), statistics.get(i)));
		}
		return DatasetOps.stack(name, new Dimension.Labeled(Dimension.STATISTIC, LANGEVIN_STATISTICS), parts);
	}

	/**
	 * Converts back to an {@link ODESolverResultSet} for code that still consumes one. The dataset must have exactly
	 * one dimension, {@code time} or {@code trial}, shared by every variable. Function variables become plain data
	 * columns holding their evaluated values, since their expressions would need a symbol table to re-evaluate.
	 */
	public static ODESolverResultSet toResultSet(Dataset dataset) {
		if (dataset.dimensions().size() != 1)
			throw new IllegalArgumentException("Only one-dimensional datasets convert to a result set; got " + dataset.dimensions());
		Dimension index = dataset.dimensions().get(0);
		if (!(index instanceof Dimension.Numeric numericIndex))
			throw new IllegalArgumentException("The index dimension '" + index.name() + "' must carry numeric values");
		String indexColumn = switch (index.name()) {
			case Dimension.TIME -> TIME_COLUMN;
			case Dimension.TRIAL -> TRIAL_COLUMN;
			default -> throw new IllegalArgumentException("The index dimension must be '" + Dimension.TIME + "' or '" + Dimension.TRIAL + "', not '" + index.name() + "'");
		};

		ODESolverResultSet resultSet = new ODESolverResultSet();
		resultSet.addDataColumn(new ODESolverResultSetColumnDescription(indexColumn));
		List<double[]> columns = new ArrayList<>();
		columns.add(numericIndex.values());
		for (Variable variable : dataset.variables()) {
			Map<String, String> attributes = variable.attributes();
			resultSet.addDataColumn(new ODESolverResultSetColumnDescription(variable.name(),
					attributes.get(PARAMETER_NAME), attributes.getOrDefault(DISPLAY_NAME, variable.name())));
			columns.add(dataset.values(variable.name()).toArray());
		}
		for (int row = 0; row < numericIndex.size(); row++) {
			double[] values = new double[columns.size()];
			for (int column = 0; column < values.length; column++) values[column] = columns.get(column)[row];
			resultSet.addRow(values);
		}
		return resultSet;
	}

	private static Variable variableFor(ColumnDescription description, String indexDimension) {
		Map<String, String> attributes = new LinkedHashMap<>();
		if (description instanceof FunctionColumnDescription function) {
			OdeDatasets.putIfPresent(attributes, DISPLAY_NAME, function.getDisplayName());
			OdeDatasets.putIfPresent(attributes, PARAMETER_NAME, function.getParameterName());
			String expression = function.getExpression() == null ? null : function.getExpression().infix();
			return new Variable(description.getName(), List.of(indexDimension), null, Variable.Role.FUNCTION, expression, null, attributes);
		}
		if (description instanceof ODESolverResultSetColumnDescription column) {
			OdeDatasets.putIfPresent(attributes, DISPLAY_NAME, column.getDisplayName());
			OdeDatasets.putIfPresent(attributes, PARAMETER_NAME, column.getParameterName());
		}
		return new Variable(description.getName(), List.of(indexDimension), null, Variable.Role.DATA, null, null, attributes);
	}

	private static void putIfPresent(Map<String, String> attributes, String key, String value) {
		if (value != null) attributes.put(key, value);
	}

	private static double[] extract(RowColumnResultSet resultSet, int column) {
		try {
			return resultSet.extractColumn(column);
		} catch (ExpressionException e) {
			throw new DataReadException("Could not evaluate column '" + resultSet.getColumnDescriptions(column).getName() + "': " + e.getMessage(), e);
		}
	}

	/** @return the values of a one-dimensional variable as a plain array, for callers that still want one */
	public static double[] column(Dataset dataset, String variableName) {
		DoubleArrayND values = dataset.values(variableName);
		if (values.rank() != 1) throw new IllegalArgumentException("'" + variableName + "' is not one-dimensional");
		return values.toArray();
	}
}
