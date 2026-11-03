package org.vcell.data.sedml;

import org.vcell.data.Dataset;
import org.vcell.data.Dimension;
import org.vcell.data.Variable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The vocabulary for SED-ML outputs held as {@link Dataset}s, shared by whatever builds them (the CLI, from executed
 * SED-ML) and whatever writes them (e.g. {@link org.vcell.data.io.BiosimulationsHdf5DatasetWriter}).
 * <p>
 * One report is one dataset:
 * <ul>
 *     <li>the dataset's name is the report's SED-ML id;</li>
 *     <li>each SED-ML data set is a variable, named by its id, in the report's order;</li>
 *     <li>each scanned parameter of a repeated task is a {@link Dimension.Numeric} named after the parameter, holding
 *     the values it takes, outermost first in the order the jobs were run;</li>
 *     <li>the data generator's own shape comes last: {@link Dimension#TIME} (an index over the time course's points)
 *     for nonspatial results, plus the topology's spatial dimensions for spatial ones.</li>
 * </ul>
 * Where the report sits (its SED-ML document), what kind of output it came from and which of its data sets could not
 * be included are dataset attributes; a data set's label, name and data generator are variable attributes.
 * <p>
 * Ids are kept exactly as the SED-ML has them, VCell's own prefixes included ({@link #PLOT_PREFIX},
 * {@link #RESERVED_DATA_SET_PREFIX}). How an id is presented, like everything else about the file, is the writer's
 * business, and different formats have long presented them differently.
 */
public final class SedmlReports {
	private SedmlReports() { }

	/** Dataset attribute: path of the SED-ML document within its archive, e.g. {@code simulation.sedml} or {@code dir/sim.sedml}. */
	public static final String DOCUMENT = "sedml.document";
	/** Dataset attribute: the output's SED-ML name, when it has one. */
	public static final String NAME = "sedml.name";
	/** Dataset attribute: the kind of SED-ML output, one of {@link OutputType}'s SED-ML names. */
	public static final String OUTPUT_TYPE = "sedml.outputType";
	/**
	 * Dataset attribute: ids of the report's data sets that are not variables of the dataset (spatial ones, when the
	 * dataset holds nonspatial results), comma separated; absent when the report is complete.
	 */
	public static final String OMITTED_DATA_SETS = "sedml.omittedDataSets";
	/** Variable attribute: the data set's label. */
	public static final String DATA_SET_LABEL = "sedml.label";
	/** Variable attribute: the data set's name, when it has one. */
	public static final String DATA_SET_NAME = "sedml.name";
	/** Variable attribute: id of the data generator the data set reports. */
	public static final String DATA_GENERATOR = "sedml.dataGenerator";
	/** Variable attribute: name of the data generator the data set reports, when it has one. */
	public static final String DATA_GENERATOR_NAME = "sedml.dataGeneratorName";

	/** Prefix VCell's SED-ML export gives the ids of outputs it made from plots. */
	public static final String PLOT_PREFIX = "__plot__";
	/** Prefix VCell's SED-ML export gives the ids of data sets it adds itself. */
	public static final String RESERVED_DATA_SET_PREFIX = "__vcell_reserved_data_set_prefix__";

	public enum OutputType {
		REPORT("SedReport"),
		/** A 2D plot whose curves' data generators are reported as if the plot were a report. */
		PLOT_2D("SedPlot2D");

		private final String sedmlName;

		OutputType(String sedmlName) {
			this.sedmlName = sedmlName;
		}

		public String sedmlName() {
			return this.sedmlName;
		}

		public static OutputType fromSedmlName(String sedmlName) {
			for (OutputType type : OutputType.values()) if (type.sedmlName.equals(sedmlName)) return type;
			throw new IllegalArgumentException("Unknown SED-ML output type '" + sedmlName + "'");
		}
	}

	/** @return a builder for a report dataset with its identifying attributes already set */
	public static Dataset.Builder report(String document, String id, String name, OutputType type) {
		if (document == null) throw new IllegalArgumentException("Report '" + id + "' needs the SED-ML document it belongs to");
		Dataset.Builder builder = Dataset.builder(id).attribute(DOCUMENT, document).attribute(OUTPUT_TYPE, type.sedmlName());
		if (name != null) builder.attribute(NAME, name);
		return builder;
	}

	/** @return the variable for one SED-ML data set of a report */
	public static Variable dataSet(String id, String label, String name, String dataGenerator, String dataGeneratorName, List<String> dimensions) {
		Map<String, String> attributes = new HashMap<>();
		if (label != null) attributes.put(DATA_SET_LABEL, label);
		if (name != null) attributes.put(DATA_SET_NAME, name);
		if (dataGenerator != null) attributes.put(DATA_GENERATOR, dataGenerator);
		if (dataGeneratorName != null) attributes.put(DATA_GENERATOR_NAME, dataGeneratorName);
		return new Variable(id, dimensions, null, Variable.Role.DATA, null, null, attributes);
	}

	/** @return whether every data set of the report is a variable of the dataset */
	public static boolean isComplete(Dataset report) {
		return !report.attributes().containsKey(OMITTED_DATA_SETS);
	}

	public static String document(Dataset report) {
		String document = report.attributes().get(DOCUMENT);
		if (document == null) throw new IllegalArgumentException("Dataset '" + report.name() + "' is not a SED-ML output: no '" + DOCUMENT + "' attribute");
		return document;
	}

	public static OutputType outputType(Dataset report) {
		String type = report.attributes().get(OUTPUT_TYPE);
		return type == null ? OutputType.REPORT : OutputType.fromSedmlName(type);
	}

	/** @return a report's id without VCell's {@link #PLOT_PREFIX} */
	public static String reportIdWithoutVCellPrefixes(String reportId) {
		return SedmlReports.withoutVCellPrefixes(reportId, reportId);
	}

	/**
	 * @return a data set's id without VCell's {@link #PLOT_PREFIX} or {@link #RESERVED_DATA_SET_PREFIX}, and without
	 * its report's (unprefixed) id in front
	 */
	public static String dataSetIdWithoutVCellPrefixes(String dataSetId, String reportId) {
		return SedmlReports.withoutVCellPrefixes(dataSetId, SedmlReports.reportIdWithoutVCellPrefixes(reportId));
	}

	// as the CLI has always done it, so ids written by either system match
	private static String withoutVCellPrefixes(String id, String reportId) {
		String unprefixedReportId = reportId.startsWith(PLOT_PREFIX) ? reportId.replace(PLOT_PREFIX, "") : reportId;
		if (reportId.equals(id)) return unprefixedReportId;
		if (id.startsWith(PLOT_PREFIX)) id = id.replace(PLOT_PREFIX, "");
		if (id.startsWith(RESERVED_DATA_SET_PREFIX)) id = id.replace(RESERVED_DATA_SET_PREFIX, "");
		if (id.startsWith(unprefixedReportId + "_")) id = id.replace(unprefixedReportId + "_", "");
		if (id.startsWith(unprefixedReportId)) id = id.replace(unprefixedReportId, "");
		return id;
	}
}
