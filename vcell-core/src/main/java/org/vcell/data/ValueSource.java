package org.vcell.data;

import org.vcell.data.array.DoubleArrayND;

import java.util.List;

/**
 * Strategy for where a variable's values come from: already in memory, read on demand from solver output, computed
 * from an expression, fetched from a remote data server. A {@link Dataset} holds one per variable and only ever asks
 * for values through it, so how and when they are produced is invisible to everything downstream.
 */
@FunctionalInterface
public interface ValueSource {

	/**
	 * @param dimensions the variable's dimension names, outermost first
	 * @param selection  narrows those dimensions; picks for dimensions not in {@code dimensions} are ignored.
	 *                   A source that can read less when narrowed (a file reader, say) should push this down.
	 * @return the selected values, shaped like {@code dimensions} after the selection (dimensions picked with
	 * {@link Selection.At} removed)
	 * @throws DataReadException if the values cannot be produced
	 */
	DoubleArrayND read(List<String> dimensions, Selection selection);

	/** @return a source over values already in memory; reads are views, not copies */
	static ValueSource of(DoubleArrayND values) {
		return (dimensions, selection) -> Selections.apply(values, dimensions, selection);
	}
}
