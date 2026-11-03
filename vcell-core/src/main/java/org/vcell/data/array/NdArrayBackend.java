package org.vcell.data.array;

import org.tensorflow.ndarray.DoubleNdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.buffer.DataBuffers;
import org.tensorflow.ndarray.index.Index;
import org.tensorflow.ndarray.index.Indices;

import java.util.Arrays;

/**
 * Backend over TensorFlow's java-ndarray {@link DoubleNdArray}. Kept alongside {@link StridedArrayBackend} so the
 * two can be compared on speed and maintainability before one is chosen.
 */
public final class NdArrayBackend implements ArrayBackend {

	@Override
	public String name() {
		return "java-ndarray";
	}

	@Override
	public DoubleArrayND zeros(long... shape) {
		ArrayBackend.sizeOf(shape);
		return new Array(NdArrays.ofDoubles(Shape.of(shape)));
	}

	@Override
	public DoubleArrayND wrap(double[] values, long... shape) {
		ArrayBackend.checkWrap(values, shape);
		return new Array(NdArrays.wrap(Shape.of(shape), DataBuffers.of(values, false, false)));
	}

	private record Array(DoubleNdArray ndArray) implements DoubleArrayND {

		@Override
		public long[] shape() {
			return this.ndArray.shape().asArray();
		}

		@Override
		public int rank() {
			return this.ndArray.rank();
		}

		@Override
		public long size() {
			return this.ndArray.size();
		}

		@Override
		public double get(long... index) {
			this.checkIndex(index);
			return this.ndArray.getDouble(index);
		}

		@Override
		public void set(double value, long... index) {
			this.checkIndex(index);
			this.ndArray.setDouble(value, index);
		}

		// java-ndarray accepts fewer coordinates than the rank (returning a sub-array), which would hide caller bugs
		private void checkIndex(long[] index) {
			long[] shape = this.shape();
			if (index.length != shape.length)
				throw new IllegalArgumentException("Expected " + shape.length + " indices, got " + index.length);
			for (int dimension = 0; dimension < index.length; dimension++) DoubleArrayND.checkIndex(index[dimension], shape[dimension]);
		}

		@Override
		public DoubleArrayND slice(int dimension, long start, long end) {
			DoubleArrayND.checkDimension(dimension, this.rank());
			DoubleArrayND.checkRange(start, end, this.shape()[dimension]);
			return new Array(this.ndArray.slice(this.indicesWith(dimension, Indices.slice(start, end))));
		}

		@Override
		public DoubleArrayND at(int dimension, long index) {
			DoubleArrayND.checkDimension(dimension, this.rank());
			DoubleArrayND.checkIndex(index, this.shape()[dimension]);
			return new Array(this.ndArray.slice(this.indicesWith(dimension, Indices.at(index))));
		}

		@Override
		public DoubleArrayND take(int dimension, long[] indices) {
			DoubleArrayND.checkDimension(dimension, this.rank());
			long length = this.shape()[dimension];
			for (long index : indices) DoubleArrayND.checkIndex(index, length);
			return new Array(this.ndArray.slice(this.indicesWith(dimension, Indices.seq(indices))));
		}

		private Index[] indicesWith(int dimension, Index index) {
			Index[] indices = new Index[this.rank()];
			Arrays.fill(indices, Indices.all());
			indices[dimension] = index;
			return indices;
		}

		@Override
		public void copyTo(double[] destination, int offset) {
			long size = this.size();
			if (offset < 0 || offset + size > destination.length)
				throw new IndexOutOfBoundsException("Destination too small: need " + size + " from offset " + offset);
			this.ndArray.copyTo(DataBuffers.of(destination, false, false).slice(offset, size));
		}

		@Override
		public String toString() {
			return "NdArrayBackend.Array" + this.ndArray.shape();
		}
	}
}
