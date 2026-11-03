package org.vcell.data.array;

/**
 * Backend over a plain {@code double[]} with per-dimension strides, the same layout NumPy uses. Views are a new
 * offset, shape and stride table over the same {@code double[]}; nothing is copied until {@link DoubleArrayND#take}.
 */
public final class StridedArrayBackend implements ArrayBackend {

	@Override
	public String name() {
		return "strided";
	}

	@Override
	public DoubleArrayND zeros(long... shape) {
		long size = ArrayBackend.sizeOf(shape);
		if (size > Integer.MAX_VALUE) throw new IllegalArgumentException("Array of " + size + " elements is too large for a double[]");
		return new StridedDoubleArray(new double[(int) size], 0, shape.clone(), StridedDoubleArray.rowMajorStrides(shape));
	}

	@Override
	public DoubleArrayND wrap(double[] values, long... shape) {
		ArrayBackend.checkWrap(values, shape);
		return new StridedDoubleArray(values, 0, shape.clone(), StridedDoubleArray.rowMajorStrides(shape));
	}
}
