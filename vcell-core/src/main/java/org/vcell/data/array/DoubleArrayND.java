package org.vcell.data.array;

import java.util.Arrays;

/**
 * A dense, n-dimensional array of doubles, laid out row-major (the last dimension varies fastest).
 * <p>
 * This is the only array type the rest of {@code org.vcell.data} sees, so the storage behind it is a choice made
 * by an {@link ArrayBackend}, not something callers depend on. Every operation that narrows the array
 * ({@link #slice}, {@link #at}) returns a view that shares storage with this array; {@link #take} may copy.
 * <p>
 * Bulk access ({@link #copyTo}, {@link #toArray}) is in primitive {@code double}s so callers moving whole blocks
 * never box a value.
 */
public interface DoubleArrayND {

	/** @return the length of every dimension; a fresh copy the caller may modify */
	long[] shape();

	default int rank() {
		return this.shape().length;
	}

	/** @return the number of elements, the product of {@link #shape()}; 1 for a rank-0 (scalar) array */
	default long size() {
		long size = 1;
		for (long length : this.shape()) size *= length;
		return size;
	}

	double get(long... index);

	void set(double value, long... index);

	/**
	 * @return a view restricted to {@code [start, end)} along {@code dimension}; the rank does not change
	 */
	DoubleArrayND slice(int dimension, long start, long end);

	/**
	 * @return a view fixed at {@code index} along {@code dimension}; that dimension is removed, so the rank drops by one
	 */
	DoubleArrayND at(int dimension, long index);

	/**
	 * @return the elements at the given {@code indices} along {@code dimension}, in that order (repeats allowed);
	 * the rank does not change. Implementations may copy.
	 */
	DoubleArrayND take(int dimension, long[] indices);

	/**
	 * Copies every element, in row-major order, into {@code destination} starting at {@code offset}.
	 */
	void copyTo(double[] destination, int offset);

	default double[] toArray() {
		long size = this.size();
		if (size > Integer.MAX_VALUE) throw new IllegalStateException("Array of " + size + " elements is too large for a double[]");
		double[] values = new double[(int) size];
		this.copyTo(values, 0);
		return values;
	}

	/**
	 * Element-wise equality of shape and values, independent of which backend holds either array.
	 */
	static boolean contentEquals(DoubleArrayND a, DoubleArrayND b) {
		if (a == b) return true;
		if (a == null || b == null) return false;
		return Arrays.equals(a.shape(), b.shape()) && Arrays.equals(a.toArray(), b.toArray());
	}

	static void checkDimension(int dimension, int rank) {
		if (dimension < 0 || dimension >= rank)
			throw new IndexOutOfBoundsException("Dimension " + dimension + " out of range for rank " + rank);
	}

	static void checkRange(long start, long end, long length) {
		if (start < 0 || end > length || start > end)
			throw new IndexOutOfBoundsException("Range [" + start + ", " + end + ") out of bounds for length " + length);
	}

	static void checkIndex(long index, long length) {
		if (index < 0 || index >= length)
			throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + length);
	}
}
