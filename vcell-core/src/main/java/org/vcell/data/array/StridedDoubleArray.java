package org.vcell.data.array;

import java.util.Arrays;

/**
 * A view of a {@code double[]}: element {@code (i0, i1, ...)} lives at {@code offset + i0*strides[0] + i1*strides[1] + ...}.
 */
final class StridedDoubleArray implements DoubleArrayND {
	private final double[] data;
	private final long offset;
	private final long[] shape;
	private final long[] strides;

	StridedDoubleArray(double[] data, long offset, long[] shape, long[] strides) {
		this.data = data;
		this.offset = offset;
		this.shape = shape;
		this.strides = strides;
	}

	static long[] rowMajorStrides(long[] shape) {
		long[] strides = new long[shape.length];
		long stride = 1;
		for (int dimension = shape.length - 1; dimension >= 0; dimension--) {
			strides[dimension] = stride;
			stride *= shape[dimension];
		}
		return strides;
	}

	@Override
	public long[] shape() {
		return this.shape.clone();
	}

	@Override
	public int rank() {
		return this.shape.length;
	}

	@Override
	public double get(long... index) {
		return this.data[this.position(index)];
	}

	@Override
	public void set(double value, long... index) {
		this.data[this.position(index)] = value;
	}

	private int position(long[] index) {
		if (index.length != this.shape.length)
			throw new IllegalArgumentException("Expected " + this.shape.length + " indices, got " + index.length);
		long position = this.offset;
		for (int dimension = 0; dimension < index.length; dimension++) {
			DoubleArrayND.checkIndex(index[dimension], this.shape[dimension]);
			position += index[dimension] * this.strides[dimension];
		}
		return (int) position;
	}

	@Override
	public DoubleArrayND slice(int dimension, long start, long end) {
		DoubleArrayND.checkDimension(dimension, this.shape.length);
		DoubleArrayND.checkRange(start, end, this.shape[dimension]);
		long[] newShape = this.shape.clone();
		newShape[dimension] = end - start;
		return new StridedDoubleArray(this.data, this.offset + start * this.strides[dimension], newShape, this.strides);
	}

	@Override
	public DoubleArrayND at(int dimension, long index) {
		DoubleArrayND.checkDimension(dimension, this.shape.length);
		DoubleArrayND.checkIndex(index, this.shape[dimension]);
		return new StridedDoubleArray(this.data, this.offset + index * this.strides[dimension],
				StridedDoubleArray.without(this.shape, dimension), StridedDoubleArray.without(this.strides, dimension));
	}

	@Override
	public DoubleArrayND take(int dimension, long[] indices) {
		DoubleArrayND.checkDimension(dimension, this.shape.length);
		for (long index : indices) DoubleArrayND.checkIndex(index, this.shape[dimension]);
		long[] newShape = this.shape.clone();
		newShape[dimension] = indices.length;
		double[] gathered = new double[(int) ArrayBackend.sizeOf(newShape)];
		this.gather(0, dimension, indices, this.offset, gathered, 0);
		return new StridedDoubleArray(gathered, 0, newShape, StridedDoubleArray.rowMajorStrides(newShape));
	}

	@Override
	public void copyTo(double[] destination, int offset) {
		if (offset < 0 || offset + this.size() > destination.length)
			throw new IndexOutOfBoundsException("Destination too small: need " + this.size() + " from offset " + offset);
		this.gather(0, -1, null, this.offset, destination, offset);
	}

	/**
	 * Copies, in row-major order, every element of this view into {@code destination}, except that along
	 * {@code takeDimension} only {@code takeIndices} are visited (pass -1 to visit everything).
	 *
	 * @return the next free position in {@code destination}
	 */
	private int gather(int dimension, int takeDimension, long[] takeIndices, long sourcePosition, double[] destination, int destinationPosition) {
		if (dimension == this.shape.length) {
			destination[destinationPosition] = this.data[(int) sourcePosition];
			return destinationPosition + 1;
		}
		long stride = this.strides[dimension];
		if (dimension == takeDimension) {
			for (long index : takeIndices)
				destinationPosition = this.gather(dimension + 1, takeDimension, takeIndices, sourcePosition + index * stride, destination, destinationPosition);
			return destinationPosition;
		}
		long length = this.shape[dimension];
		if (dimension == this.shape.length - 1) {
			// innermost dimension: a plain strided loop, or a straight array copy when contiguous
			if (stride == 1) {
				System.arraycopy(this.data, (int) sourcePosition, destination, destinationPosition, (int) length);
				return destinationPosition + (int) length;
			}
			for (long index = 0; index < length; index++) destination[destinationPosition++] = this.data[(int) (sourcePosition + index * stride)];
			return destinationPosition;
		}
		for (long index = 0; index < length; index++)
			destinationPosition = this.gather(dimension + 1, takeDimension, takeIndices, sourcePosition + index * stride, destination, destinationPosition);
		return destinationPosition;
	}

	private static long[] without(long[] values, int removed) {
		long[] result = new long[values.length - 1];
		System.arraycopy(values, 0, result, 0, removed);
		System.arraycopy(values, removed + 1, result, removed, values.length - removed - 1);
		return result;
	}

	@Override
	public String toString() {
		return "StridedDoubleArray" + Arrays.toString(this.shape);
	}
}
