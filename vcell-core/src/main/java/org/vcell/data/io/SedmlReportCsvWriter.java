package org.vcell.data.io;

import org.vcell.data.Dataset;
import org.vcell.data.Dimension;
import org.vcell.data.Variable;
import org.vcell.data.sedml.SedmlReports;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes one SED-ML report (a dataset shaped as {@link SedmlReports} describes) as the CSV the VCell CLI has long
 * produced for it: a row per data set per job, each row
 * <pre>
 * &lt;data set id&gt;_&lt;job&gt;_,&lt;label&gt;(&lt;job&gt;),&lt;data generator name or id&gt;(&lt;job&gt;),value,value,...
 * </pre>
 * with jobs numbered in the order they were run (the scan dimensions, row-major). An id with VCell's reserved data set
 * prefix is written {@code VCell::<rest>}; an entry containing a comma is quoted before its job suffix is added.
 * <p>
 * A data set whose id contains {@code time_} is taken to be the time course and written for the first job only, since
 * it is the same for every job. That is how the CLI has always told time apart, so it stays until changed deliberately.
 */
public final class SedmlReportCsvWriter implements DatasetWriter {

	@Override
	public String extension() {
		return "csv";
	}

	@Override
	public void write(Dataset report, Path destination) throws IOException {
		Files.writeString(destination, this.toCsv(report));
	}

	public String toCsv(Dataset report) {
		if (!report.topology().spatialDimensions().isEmpty())
			throw new UnsupportedOperationException("Spatial reports are not supported yet (report '" + report.name() + "')");
		StringBuilder csv = new StringBuilder();
		for (Variable dataSet : report.variables()) {
			if (!dataSet.dimensions().contains(Dimension.TIME))
				throw new IllegalArgumentException("Data set '" + dataSet.name() + "' of report '" + report.name() + "' has no " + Dimension.TIME + " dimension");
			int jobs = 1;
			for (String dimension : dataSet.dimensions()) if (!dimension.equals(Dimension.TIME)) jobs *= report.dimension(dimension).size();
			int points = report.dimension(Dimension.TIME).size();
			if (!dataSet.dimensions().get(dataSet.dimensions().size() - 1).equals(Dimension.TIME))
				throw new IllegalArgumentException("Data set '" + dataSet.name() + "' of report '" + report.name() + "' must end with its " + Dimension.TIME + " dimension");
			double[] values = report.values(dataSet.name()).toArray();

			String id = dataSet.name().startsWith(SedmlReports.RESERVED_DATA_SET_PREFIX)
					? "VCell::" + dataSet.name().substring(SedmlReports.RESERVED_DATA_SET_PREFIX.length()) : dataSet.name();
			String label = dataSet.attributes().getOrDefault(SedmlReports.DATA_SET_LABEL, "");
			String dataGenerator = dataSet.attributes().getOrDefault(SedmlReports.DATA_GENERATOR_NAME, "");
			if (dataGenerator.isEmpty()) dataGenerator = dataSet.attributes().getOrDefault(SedmlReports.DATA_GENERATOR, "");
			int rows = dataSet.name().contains("time_") ? Math.min(1, jobs) : jobs;
			for (int job = 0; job < rows; job++) {
				csv.append(SedmlReportCsvWriter.quoted(id)).append('_').append(job).append("_,");
				csv.append(SedmlReportCsvWriter.quoted(label)).append('(').append(job).append("),");
				csv.append(SedmlReportCsvWriter.quoted(dataGenerator)).append('(').append(job).append("),");
				for (int point = 0; point < points; point++) {
					if (point > 0) csv.append(',');
					csv.append(values[job * points + point]);
				}
				csv.append('\n');
			}
		}
		return csv.toString();
	}

	private static String quoted(String entry) {
		return entry.contains(",") ? '"' + entry + '"' : entry;
	}
}
