package org.vcell.data.io;

import cbit.vcell.export.server.JhdfUtils;
import io.jhdf.HdfFile;
import io.jhdf.WritableHdfFile;
import io.jhdf.api.WritableDataset;
import io.jhdf.api.WritableGroup;
import org.vcell.data.Dataset;
import org.vcell.data.Dimension;
import org.vcell.data.Variable;
import org.vcell.data.array.DoubleArrayND;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * Writes a dataset to HDF5 in a self-describing, NetCDF-like layout:
 * <pre>
 * /                      attributes: name, topology, and the dataset's own attributes
 * /dimensions/&lt;name&gt;     coordinate values: doubles (numeric), strings (labeled) or source indices (indexed);
 *                        attribute "kind", and "unit" when known
 * /variables/&lt;name&gt;      values, one HDF5 dimension per dataset dimension; attributes "dimensions" (their names,
 *                        in order), "role", and "unit", "expression", "domain" when known, plus the variable's own
 * </pre>
 * Every variable is written in full, coordinate variables included. A rank-0 variable is written as a one-element
 * array with an empty "dimensions" attribute.
 */
public final class Hdf5DatasetWriter implements DatasetWriter {

	public static final String DIMENSIONS_GROUP = "dimensions";
	public static final String VARIABLES_GROUP = "variables";

	private static final Set<String> RESERVED_VARIABLE_ATTRIBUTES = Set.of("dimensions", "role", "unit", "expression", "domain");
	private static final Set<String> RESERVED_ROOT_ATTRIBUTES = Set.of("name", "topology");

	@Override
	public String extension() {
		return "h5";
	}

	@Override
	public void write(Dataset dataset, Path destination) throws IOException {
		try (WritableHdfFile file = HdfFile.write(destination)) {
			file.putAttribute("name", dataset.name());
			file.putAttribute("topology", dataset.topology().kind());
			Hdf5DatasetWriter.putAttributes(file, dataset.attributes(), RESERVED_ROOT_ATTRIBUTES);

			WritableGroup dimensions = file.putGroup(DIMENSIONS_GROUP);
			for (Dimension dimension : dataset.dimensions()) Hdf5DatasetWriter.writeDimension(dimensions, dimension);

			WritableGroup variables = file.putGroup(VARIABLES_GROUP);
			for (Variable variable : dataset.variables()) {
				DoubleArrayND values = dataset.values(variable.name());
				long[] shape = values.rank() == 0 ? new long[]{1} : values.shape();
				WritableDataset written = variables.putDataset(Hdf5DatasetWriter.checkedName(variable.name()),
						JhdfUtils.createMultidimensionalArray(shape, values.toArray()));
				written.putAttribute("dimensions", variable.dimensions().toArray(new String[0]));
				written.putAttribute("role", variable.role().name());
				if (variable.unit() != null) written.putAttribute("unit", variable.unit());
				if (variable.expression() != null) written.putAttribute("expression", variable.expression());
				if (variable.domain() != null) written.putAttribute("domain", variable.domain());
				Hdf5DatasetWriter.putAttributes(written, variable.attributes(), RESERVED_VARIABLE_ATTRIBUTES);
			}
		}
	}

	private static void writeDimension(WritableGroup group, Dimension dimension) {
		String name = Hdf5DatasetWriter.checkedName(dimension.name());
		WritableDataset written;
		if (dimension instanceof Dimension.Numeric numeric) {
			written = group.putDataset(name, numeric.values());
			written.putAttribute("kind", "numeric");
			if (numeric.unit() != null) written.putAttribute("unit", numeric.unit());
		} else if (dimension instanceof Dimension.Labeled labeled) {
			written = group.putDataset(name, labeled.labels().toArray(new String[0]));
			written.putAttribute("kind", "labeled");
		} else if (dimension instanceof Dimension.Indexed indexed) {
			written = group.putDataset(name, indexed.sourceIndices());
			written.putAttribute("kind", "indexed");
		} else {
			throw new IllegalArgumentException("Unknown dimension type " + dimension.getClass().getName());
		}
	}

	private static void putAttributes(io.jhdf.api.WritableNode node, Map<String, String> attributes, Set<String> reserved) {
		for (Map.Entry<String, String> attribute : attributes.entrySet()) {
			if (reserved.contains(attribute.getKey()))
				throw new IllegalArgumentException("Attribute name '" + attribute.getKey() + "' is reserved by the HDF5 layout");
			node.putAttribute(attribute.getKey(), attribute.getValue());
		}
	}

	private static String checkedName(String name) {
		if (name.contains("/")) throw new IllegalArgumentException("'" + name + "' cannot be an HDF5 name because it contains '/'");
		return name;
	}
}
