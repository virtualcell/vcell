package org.vcell.data.io;

import cbit.vcell.export.server.JhdfUtils;
import io.jhdf.HdfFile;
import io.jhdf.WritableHdfFile;
import io.jhdf.api.WritableDataset;
import io.jhdf.api.WritableGroup;
import org.vcell.data.Dataset;
import org.vcell.data.Dimension;
import org.vcell.data.Variable;
import org.vcell.data.sedml.SedmlReports;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Writes SED-ML reports (datasets shaped as {@link SedmlReports} describes) to the HDF5 layout BioSimulators expects
 * of a simulator, as the VCell CLI has long produced it:
 * <pre>
 * /&lt;dir&gt;/&lt;doc&gt;.sedml          a group per path segment of each SED-ML document;
 *                              attributes "combineArchiveLocation" and "uri", both the path so far
 * /&lt;dir&gt;/&lt;doc&gt;.sedml/&lt;id&gt;     one HDF5 dataset per report, shaped
 *                              [data set, (scan parameter, [sub-task])..., point...]
 * </pre>
 * Each report dataset carries the attributes {@code _type}, {@code sedmlDataSetDataTypes}, {@code sedmlDataSetIds},
 * {@code sedmlDataSetNames}, {@code sedmlDataSetLabels}, {@code sedmlDataSetShapes}, {@code sedmlRepeatedTaskValues}
 * and {@code sedmlRepeatedTaskParameterNames} (for scans only), {@code sedmlId}, {@code sedmlName} and {@code uri}.
 * <p>
 * The data sets of a report are stacked along the leading HDF5 dimension, so every one of them must run over the same
 * dimensions. Dimensions that are neither {@link Dimension#TIME} nor spatial are scan parameters and must be numeric.
 */
public final class BiosimulationsHdf5DatasetWriter implements DatasetCollectionWriter {

	private final boolean subTaskDimensions;

	/** A writer that includes the size-1 sub-task dimension after each scan dimension, as BioSimulators expects. */
	public BiosimulationsHdf5DatasetWriter() {
		this(true);
	}

	/**
	 * @param subTaskDimensions whether to follow each scan dimension with a size-1 dimension for the repeated task's
	 *                          sub-task (VCell runs one sub-task per repeated task)
	 */
	public BiosimulationsHdf5DatasetWriter(boolean subTaskDimensions) {
		this.subTaskDimensions = subTaskDimensions;
	}

	@Override
	public String extension() {
		return "h5";
	}

	@Override
	public void write(List<Dataset> reports, Path destination) throws IOException {
		try (WritableHdfFile file = HdfFile.write(destination)) {
			Map<String, WritableGroup> documentGroups = new HashMap<>();
			for (Dataset report : reports)
				this.writeReport(BiosimulationsHdf5DatasetWriter.documentGroup(file, documentGroups, SedmlReports.document(report)), report);
		}
	}

	/** @return the group for a SED-ML document, creating it and any missing parent groups */
	private static WritableGroup documentGroup(WritableHdfFile file, Map<String, WritableGroup> groups, String document) {
		WritableGroup parent = file;
		String path = "";
		for (String segment : document.split("/")) {
			path = path.isEmpty() ? segment : path + "/" + segment;
			WritableGroup group = groups.get(path);
			if (group == null) {
				group = parent.putGroup(segment);
				group.putAttribute("combineArchiveLocation", path);
				group.putAttribute("uri", path);
				groups.put(path, group);
			}
			parent = group;
		}
		return parent;
	}

	private void writeReport(WritableGroup documentGroup, Dataset report) {
		List<Variable> dataSets = report.variables();
		if (dataSets.isEmpty()) throw new IllegalArgumentException("Report '" + report.name() + "' has no data sets");
		if (!report.topology().spatialDimensions().isEmpty())
			throw new UnsupportedOperationException("Spatial reports are not supported yet (report '" + report.name() + "')");
		List<String> dimensions = dataSets.get(0).dimensions();
		for (Variable dataSet : dataSets)
			if (!dataSet.dimensions().equals(dimensions))
				throw new IllegalArgumentException("Data sets of report '" + report.name() + "' run over different dimensions: "
						+ dimensions + " and " + dataSet.dimensions() + " ('" + dataSet.name() + "')");

		// scan dimensions first, then the data generator's own shape
		Set<String> pointDimensions = Set.of(Dimension.TIME);
		List<Dimension.Numeric> scans = new ArrayList<>();
		List<Long> shape = new ArrayList<>(List.of((long) dataSets.size()));
		long points = 1;
		boolean inPoints = false;
		for (String name : dimensions) {
			Dimension dimension = report.dimension(name);
			if (pointDimensions.contains(name)) {
				inPoints = true;
				points *= dimension.size();
				continue;
			}
			if (inPoints) throw new IllegalArgumentException("Report '" + report.name() + "' has scan dimension '" + name + "' inside its point dimensions");
			if (!(dimension instanceof Dimension.Numeric numeric))
				throw new IllegalArgumentException("Scan dimension '" + name + "' of report '" + report.name() + "' needs parameter values");
			scans.add(numeric);
			shape.add((long) numeric.size());
			if (this.subTaskDimensions) shape.add(1L);
		}
		for (String name : dimensions) if (pointDimensions.contains(name)) shape.add((long) report.dimension(name).size());
		long jobs = 1;
		for (Dimension.Numeric scan : scans) jobs *= scan.size();

		int perDataSet = Math.toIntExact(jobs * points);
		double[] buffer = new double[Math.multiplyExact(perDataSet, dataSets.size())];
		for (int i = 0; i < dataSets.size(); i++) report.values(dataSets.get(i).name()).copyTo(buffer, i * perDataSet);
		WritableDataset written = documentGroup.putDataset(report.name(),
				JhdfUtils.createMultidimensionalArray(shape.stream().mapToLong(Long::longValue).toArray(), buffer));

		List<String> ids = new ArrayList<>(), names = new ArrayList<>(), labels = new ArrayList<>(), shapes = new ArrayList<>();
		for (Variable dataSet : dataSets) {
			ids.add(dataSet.name());
			names.add(dataSet.attributes().getOrDefault(SedmlReports.DATA_SET_NAME, ""));
			labels.add(dataSet.attributes().getOrDefault(SedmlReports.DATA_SET_LABEL, ""));
			// One entry per data set per job, each the flat size of one job's data. This is how the VCell CLI has always
			// written it, not one shape per data set; it stays until the change is made deliberately, so outputs match.
			for (long job = 0; job < jobs; job++) shapes.add(Long.toString(points));
		}
		written.putAttribute("_type", SedmlReports.outputType(report).sedmlName());
		written.putAttribute("sedmlDataSetDataTypes", ids.stream().map(id -> "float64").toArray(String[]::new));
		written.putAttribute("sedmlDataSetIds", ids.toArray(new String[0]));
		written.putAttribute("sedmlDataSetNames", names.toArray(new String[0]));
		written.putAttribute("sedmlDataSetLabels", labels.toArray(new String[0]));
		written.putAttribute("sedmlDataSetShapes", shapes.toArray(new String[0]));
		if (!scans.isEmpty()) {
			written.putAttribute("sedmlRepeatedTaskValues", scans.stream().map(scan -> Arrays.toString(scan.values())).toArray(String[]::new));
			written.putAttribute("sedmlRepeatedTaskParameterNames", scans.stream().map(Dimension::name).toArray(String[]::new));
		}
		written.putAttribute("sedmlId", report.name());
		written.putAttribute("sedmlName", report.attributes().getOrDefault(SedmlReports.NAME, report.name()));
		written.putAttribute("uri", SedmlReports.document(report) + "/" + report.name());
	}
}
