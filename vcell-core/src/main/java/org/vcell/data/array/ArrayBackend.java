package org.vcell.data.array;

/**
 * Strategy for the storage behind a {@link DoubleArrayND}. Backends are interchangeable: arrays from different
 * backends compare equal through {@link DoubleArrayND#contentEquals} and can be mixed freely.
 */
public interface ArrayBackend {

	/** The backend used when a caller does not ask for one. */
	ArrayBackend DEFAULT = new StridedArrayBackend();

	String name();

	/** @return a new zero-filled array of the given shape */
	DoubleArrayND zeros(long... shape);

	/**
	 * @return an array of the given shape over {@code values} (row-major). The backend may share {@code values}
	 * rather than copy it, so the caller must not modify it afterwards.
	 */
	DoubleArrayND wrap(double[] values, long... shape);

	static long sizeOf(long[] shape) {
		long size = 1;
		for (long length : shape) {
			if (length < 0) throw new IllegalArgumentException("Negative dimension length " + length);
			size *= length;
		}
		return size;
	}

	static void checkWrap(double[] values, long[] shape) {
		if (values == null) throw new IllegalArgumentException("values cannot be null");
		long size = ArrayBackend.sizeOf(shape);
		if (size != values.length)
			throw new IllegalArgumentException("Shape holds " + size + " elements but " + values.length + " values were given");
	}
}
