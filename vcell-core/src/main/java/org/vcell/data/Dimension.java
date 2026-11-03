package org.vcell.data;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * A named axis of a {@link Dataset}, with its length and, where they exist, the coordinate value of each position.
 * <p>
 * Every axis VCell results have is one of these: time, trial, parameter-scan job, statistic, channel, and each
 * spatial index space (volume elements, membrane elements, regions...). What a spatial dimension means
 * geometrically is the {@link Topology}'s business, not the dimension's.
 */
public sealed interface Dimension permits Dimension.Numeric, Dimension.Labeled, Dimension.Indexed {

	/** Well-known dimension names, so producers and consumers agree without string literals. */
	String TIME = "time";
	String TRIAL = "trial";
	String JOB = "job";
	String STATISTIC = "statistic";

	String name();

	int size();

	/** @return a printable label for the coordinate at {@code index} */
	String label(int index);

	/** @return the same dimension restricted to {@code indices}, in that order */
	Dimension subset(int[] indices);

	/** A dimension whose positions carry numeric coordinates, e.g. simulation times. */
	record Numeric(String name, double[] values, String unit) implements Dimension {
		public Numeric {
			Dimension.checkName(name);
			Objects.requireNonNull(values, "values");
			values = values.clone();
		}

		public Numeric(String name, double[] values) {
			this(name, values, null);
		}

		@Override
		public double[] values() {
			return this.values.clone();
		}

		public double value(int index) {
			return this.values[index];
		}

		@Override
		public int size() {
			return this.values.length;
		}

		@Override
		public String label(int index) {
			return String.valueOf(this.values[index]);
		}

		@Override
		public Numeric subset(int[] indices) {
			return new Numeric(this.name, Arrays.stream(indices).mapToDouble(i -> this.values[i]).toArray(), this.unit);
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof Numeric other && this.name.equals(other.name)
					&& Arrays.equals(this.values, other.values) && Objects.equals(this.unit, other.unit);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.name, Arrays.hashCode(this.values), this.unit);
		}

		@Override
		public String toString() {
			return "Numeric[" + this.name + ", size=" + this.values.length + (this.unit == null ? "" : ", unit=" + this.unit) + "]";
		}
	}

	/** A dimension whose positions are named, e.g. statistics ("mean", "min") or channels. */
	record Labeled(String name, List<String> labels) implements Dimension {
		public Labeled {
			Dimension.checkName(name);
			labels = List.copyOf(labels);
		}

		@Override
		public int size() {
			return this.labels.size();
		}

		@Override
		public String label(int index) {
			return this.labels.get(index);
		}

		public int indexOf(String label) {
			return this.labels.indexOf(label);
		}

		@Override
		public Labeled subset(int[] indices) {
			return new Labeled(this.name, Arrays.stream(indices).mapToObj(this.labels::get).toList());
		}
	}

	/**
	 * A dimension with no coordinate values of its own, e.g. mesh element indices or parameter-scan jobs. Values that
	 * describe its positions (a scan's parameter values, say) are {@link Variable.Role#COORDINATE} variables over it.
	 */
	record Indexed(String name, int size, int[] sourceIndices) implements Dimension {
		/**
		 * @param sourceIndices for a subset, the position each element had in the full dimension; null when this is
		 *                      the full dimension, so position {@code i} is source index {@code i}
		 */
		public Indexed {
			Dimension.checkName(name);
			if (size < 0) throw new IllegalArgumentException("size cannot be negative");
			if (sourceIndices != null) {
				if (sourceIndices.length != size) throw new IllegalArgumentException("sourceIndices must have one entry per position");
				sourceIndices = sourceIndices.clone();
			}
		}

		public Indexed(String name, int size) {
			this(name, size, null);
		}

		@Override
		public int[] sourceIndices() {
			return this.sourceIndices == null ? Dimension.range(0, this.size) : this.sourceIndices.clone();
		}

		/** @return the index position {@code index} had in the full, unsubsetted dimension */
		public int sourceIndex(int index) {
			Objects.checkIndex(index, this.size);
			return this.sourceIndices == null ? index : this.sourceIndices[index];
		}

		@Override
		public String label(int index) {
			return String.valueOf(this.sourceIndex(index));
		}

		@Override
		public Indexed subset(int[] indices) {
			return new Indexed(this.name, indices.length, Arrays.stream(indices).map(this::sourceIndex).toArray());
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof Indexed other && this.name.equals(other.name) && this.size == other.size
					&& Arrays.equals(this.sourceIndices(), other.sourceIndices());
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.name, this.size, Arrays.hashCode(this.sourceIndices()));
		}

		@Override
		public String toString() {
			return "Indexed[" + this.name + ", size=" + this.size + (this.sourceIndices == null ? "" : ", subset") + "]";
		}
	}

	private static void checkName(String name) {
		if (name == null || name.isBlank()) throw new IllegalArgumentException("Dimension name cannot be blank");
	}

	/** @return {@code [start, end)} as an index array, for {@link #subset} */
	static int[] range(int start, int end) {
		return IntStream.range(start, end).toArray();
	}
}
