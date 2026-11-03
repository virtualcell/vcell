package org.vcell.data.io;

import org.vcell.data.Dataset;
import org.vcell.data.Dimension;
import org.vcell.data.Selection;
import org.vcell.data.Selections;
import org.vcell.data.Variable;
import org.vcell.data.array.DoubleArrayND;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Writes a dataset as a table: one row per position of a chosen row dimension (normally time), one column per
 * variable. A variable with further dimensions (a statistic, a job) gets one column per combination of their
 * positions, headed {@code name[dimension=label,...]}. {@link Variable.Role#COORDINATE} variables are not written.
 */
public final class CsvDatasetWriter implements DatasetWriter {

	public enum Layout {
		/** A header row of dimension and column names, then one line per row. */
		TABLE,
		/**
		 * Byte-for-byte the body of the CSV the existing {@code ASCIIExporter} writes for nonspatial data (everything
		 * after its simulation description header), so the two can be compared directly. Requires the row dimension
		 * to be {@code time} or {@code trial}.
		 */
		VCELL_LEGACY
	}

	private record Column(String header, double[] values) { }

	private final Layout layout;
	private final boolean transpose;
	private final String rowDimension;

	/**
	 * @param transpose    put the row dimension across the top and the variables down the side
	 * @param rowDimension the dimension whose positions are rows; null picks {@code time}, else {@code trial}, else
	 *                     the innermost dimension of the first variable
	 */
	public CsvDatasetWriter(Layout layout, boolean transpose, String rowDimension) {
		this.layout = layout;
		this.transpose = transpose;
		this.rowDimension = rowDimension;
	}

	public CsvDatasetWriter() {
		this(Layout.TABLE, false, null);
	}

	@Override
	public String extension() {
		return "csv";
	}

	@Override
	public void write(Dataset dataset, Path destination) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(destination, StandardCharsets.UTF_8)) {
			this.write(dataset, writer);
		}
	}

	public void write(Dataset dataset, Appendable out) throws IOException {
		Dimension rows = dataset.dimension(this.chooseRowDimension(dataset));
		List<Column> columns = CsvDatasetWriter.columns(dataset, rows.name());
		if (this.layout == Layout.VCELL_LEGACY) this.writeLegacy(rows, columns, out);
		else this.writeTable(rows, columns, out);
	}

	public String toCsv(Dataset dataset) {
		StringBuilder csv = new StringBuilder();
		try {
			this.write(dataset, csv);
		} catch (IOException e) {
			throw new UncheckedIOException(e); // a StringBuilder does not throw
		}
		return csv.toString();
	}

	private String chooseRowDimension(Dataset dataset) {
		if (this.rowDimension != null) return this.rowDimension;
		if (dataset.hasDimension(Dimension.TIME)) return Dimension.TIME;
		if (dataset.hasDimension(Dimension.TRIAL)) return Dimension.TRIAL;
		List<Variable> written = CsvDatasetWriter.writtenVariables(dataset);
		if (written.isEmpty() || written.get(0).dimensions().isEmpty())
			throw new IllegalArgumentException("Dataset '" + dataset.name() + "' has no dimension to use for rows");
		List<String> dimensions = written.get(0).dimensions();
		return dimensions.get(dimensions.size() - 1);
	}

	private static List<Variable> writtenVariables(Dataset dataset) {
		return dataset.variables().stream().filter(variable -> variable.role() != Variable.Role.COORDINATE).toList();
	}

	/** Splits every written variable into one column per combination of its non-row dimensions. */
	private static List<Column> columns(Dataset dataset, String rowDimension) {
		List<Column> columns = new ArrayList<>();
		for (Variable variable : CsvDatasetWriter.writtenVariables(dataset)) {
			if (!variable.dimensions().contains(rowDimension))
				throw new IllegalArgumentException("Variable '" + variable.name() + "' does not run over the row dimension '" + rowDimension + "'");
			List<Dimension> others = variable.dimensions().stream().filter(d -> !d.equals(rowDimension)).map(dataset::dimension).toList();
			DoubleArrayND values = dataset.values(variable.name());
			int[] position = new int[others.size()];
			do {
				Selection fixOthers = Selection.all();
				StringBuilder header = new StringBuilder(variable.name());
				for (int i = 0; i < others.size(); i++) {
					fixOthers = fixOthers.at(others.get(i).name(), position[i]);
					header.append(i == 0 ? "[" : ",").append(others.get(i).name()).append('=').append(others.get(i).label(position[i]));
				}
				if (!others.isEmpty()) header.append(']');
				columns.add(new Column(header.toString(), Selections.apply(values, variable.dimensions(), fixOthers).toArray()));
			} while (CsvDatasetWriter.advance(position, others));
		}
		return columns;
	}

	/** Steps {@code position} to the next combination in row-major order; false once every combination is done. */
	private static boolean advance(int[] position, List<Dimension> dimensions) {
		for (int i = position.length - 1; i >= 0; i--) {
			if (++position[i] < dimensions.get(i).size()) return true;
			position[i] = 0;
		}
		return false;
	}

	private void writeTable(Dimension rows, List<Column> columns, Appendable out) throws IOException {
		if (this.transpose) {
			out.append(CsvDatasetWriter.quoted(rows.name()));
			for (int row = 0; row < rows.size(); row++) out.append(',').append(CsvDatasetWriter.quoted(rows.label(row)));
			out.append('\n');
			for (Column column : columns) {
				out.append(CsvDatasetWriter.quoted(column.header()));
				for (double value : column.values()) out.append(',').append(String.valueOf(value));
				out.append('\n');
			}
			return;
		}
		out.append(CsvDatasetWriter.quoted(rows.name()));
		for (Column column : columns) out.append(',').append(CsvDatasetWriter.quoted(column.header()));
		out.append('\n');
		for (int row = 0; row < rows.size(); row++) {
			out.append(CsvDatasetWriter.quoted(rows.label(row)));
			for (Column column : columns) out.append(',').append(String.valueOf(column.values()[row]));
			out.append('\n');
		}
	}

	/** Mirrors {@code ASCIIExporter.getODEDataValues}, including its leading commas and blank line. */
	private void writeLegacy(Dimension rows, List<Column> columns, Appendable out) throws IOException {
		boolean isTime = Dimension.TIME.equals(rows.name());
		if (!isTime && !Dimension.TRIAL.equals(rows.name()))
			throw new IllegalArgumentException("The legacy layout needs a '" + Dimension.TIME + "' or '" + Dimension.TRIAL + "' row dimension, not '" + rows.name() + "'");
		if (rows.size() == 0) throw new IllegalArgumentException("The legacy layout needs at least one row");
		String rowTitle = isTime ? "Time" : "Trial No";

		out.append(isTime ? "Variable values over the time range " : "Variable values over the Trials ")
				.append(rows.label(0)).append(" to ").append(rows.label(rows.size() - 1)).append("\n\n");
		if (this.transpose) {
			out.append(",Variable\n");
			out.append(rowTitle).append(',');
			for (int row = 0; row < rows.size(); row++) out.append(',').append(rows.label(row));
			out.append('\n');
			for (Column column : columns) {
				out.append(',').append(column.header());
				for (double value : column.values()) out.append(',').append(String.valueOf(value));
				out.append('\n');
			}
			return;
		}
		out.append(',').append(rowTitle).append('\n');
		out.append("Variable,");
		for (Column column : columns) out.append(',').append(column.header());
		out.append('\n');
		for (int row = 0; row < rows.size(); row++) {
			out.append(',').append(rows.label(row));
			for (Column column : columns) out.append(',').append(String.valueOf(column.values()[row]));
			out.append('\n');
		}
	}

	private static String quoted(String field) {
		if (field.indexOf(',') < 0 && field.indexOf('"') < 0 && field.indexOf('\n') < 0) return field;
		return '"' + field.replace("\"", "\"\"") + '"';
	}
}
