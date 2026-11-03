package org.vcell.data;

import org.vcell.data.array.DoubleArrayND;

import java.util.Arrays;
import java.util.List;

/**
 * Applies a {@link Selection} to an array whose dimensions are named.
 */
public final class Selections {
	private Selections() { }

	/**
	 * @param values     an array with one dimension per entry of {@code dimensions}
	 * @param dimensions the name of each of {@code values}' dimensions, outermost first
	 * @return a view (or, for {@link Selection.Take}, possibly a copy) of the selected values
	 */
	public static DoubleArrayND apply(DoubleArrayND values, List<String> dimensions, Selection selection) {
		if (values.rank() != dimensions.size())
			throw new IllegalArgumentException("Array of rank " + values.rank() + " does not match dimensions " + dimensions);
		DoubleArrayND selected = values;
		// innermost first, so removing a dimension with At never shifts one still to be processed
		for (int dimension = dimensions.size() - 1; dimension >= 0; dimension--) {
			Selection.Pick pick = selection.pick(dimensions.get(dimension));
			if (pick instanceof Selection.Range range) selected = selected.slice(dimension, range.start(), range.end());
			else if (pick instanceof Selection.Take take) selected = selected.take(dimension, Arrays.stream(take.indices()).asLongStream().toArray());
			else if (pick instanceof Selection.At at) selected = selected.at(dimension, at.index());
		}
		return selected;
	}

	/** @return {@code dimensions} without those the selection fixes with {@link Selection.At} */
	public static List<String> remainingDimensions(List<String> dimensions, Selection selection) {
		return dimensions.stream().filter(name -> !(selection.pick(name) instanceof Selection.At)).toList();
	}
}
