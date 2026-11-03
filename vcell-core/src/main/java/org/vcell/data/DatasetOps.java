package org.vcell.data;

import org.vcell.data.array.ArrayBackend;
import org.vcell.data.array.DoubleArrayND;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Operations that build one {@link Dataset} from others.
 */
public final class DatasetOps {
	private DatasetOps() { }

	/**
	 * Stacks datasets of identical structure along a new, outermost dimension: parameter-scan jobs into a
	 * {@link Dimension#JOB} dimension, or per-statistic results into a {@link Dimension#STATISTIC} dimension.
	 * <p>
	 * Every part must have the same dimensions (including coordinate values, so e.g. jobs must share time points),
	 * the same variables, and the same topology. Values are read and copied now. Attributes all parts agree on are kept.
	 *
	 * @param newDimension one position per part, in order
	 */
	public static Dataset stack(String name, Dimension newDimension, List<Dataset> parts, ArrayBackend backend) {
		Objects.requireNonNull(newDimension, "newDimension");
		if (parts == null || parts.isEmpty()) throw new IllegalArgumentException("Nothing to stack");
		if (parts.size() != newDimension.size())
			throw new IllegalArgumentException("Dimension '" + newDimension.name() + "' has " + newDimension.size() + " positions for " + parts.size() + " datasets");
		Dataset template = parts.get(0);
		if (template.hasDimension(newDimension.name()))
			throw new IllegalArgumentException("Datasets already have a dimension '" + newDimension.name() + "'");
		for (Dataset part : parts) DatasetOps.checkSameStructure(template, part);

		Dataset.Builder builder = Dataset.builder(name).topology(template.topology()).dimension(newDimension);
		template.dimensions().forEach(builder::dimension);
		DatasetOps.commonAttributes(parts).forEach(builder::attribute);

		for (Variable variable : template.variables()) {
			long[] partShape = template.shapeOf(variable.name());
			long partSize = ArrayBackend.sizeOf(partShape);
			long[] stackedShape = new long[partShape.length + 1];
			stackedShape[0] = parts.size();
			System.arraycopy(partShape, 0, stackedShape, 1, partShape.length);
			long stackedSize = ArrayBackend.sizeOf(stackedShape);
			if (stackedSize > Integer.MAX_VALUE) throw new IllegalArgumentException("Stacked '" + variable.name() + "' is too large to hold in memory");

			double[] stacked = new double[(int) stackedSize];
			for (int i = 0; i < parts.size(); i++) parts.get(i).values(variable.name()).copyTo(stacked, (int) (i * partSize));

			List<String> dimensions = new ArrayList<>();
			dimensions.add(newDimension.name());
			dimensions.addAll(variable.dimensions());
			builder.variable(variable.withDimensions(dimensions), backend.wrap(stacked, stackedShape));
		}
		return builder.build();
	}

	public static Dataset stack(String name, Dimension newDimension, List<Dataset> parts) {
		return DatasetOps.stack(name, newDimension, parts, ArrayBackend.DEFAULT);
	}

	private static void checkSameStructure(Dataset template, Dataset part) {
		if (!template.dimensions().equals(part.dimensions()))
			throw new IllegalArgumentException("Dataset '" + part.name() + "' has dimensions " + part.dimensions()
					+ ", unlike '" + template.name() + "' with " + template.dimensions());
		if (!template.variables().equals(part.variables()))
			throw new IllegalArgumentException("Dataset '" + part.name() + "' has different variables from '" + template.name() + "'");
		if (!template.topology().equals(part.topology()))
			throw new IllegalArgumentException("Dataset '" + part.name() + "' has a different topology from '" + template.name() + "'");
	}

	private static Map<String, String> commonAttributes(List<Dataset> parts) {
		Map<String, String> common = new LinkedHashMap<>(parts.get(0).attributes());
		for (Dataset part : parts) common.entrySet().removeIf(entry -> !entry.getValue().equals(part.attributes().get(entry.getKey())));
		return common;
	}
}
