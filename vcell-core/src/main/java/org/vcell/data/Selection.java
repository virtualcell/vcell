package org.vcell.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * What part of a {@link Dataset} to keep: an optional subset of variables, and per dimension a range, a list of
 * positions, or a single position (which removes that dimension). Dimensions not mentioned are kept whole.
 * <p>
 * Selections are immutable; each builder method returns a new one. Every spatial query (slice, points, curve,
 * region of interest) resolves to positions along a spatial dimension, so it is expressed with {@link #take}.
 */
public final class Selection {

	/** How one dimension is narrowed. */
	public sealed interface Pick permits Range, Take, At { }

	/** Positions {@code [start, end)}; the dimension is kept. */
	public record Range(int start, int end) implements Pick {
		public Range {
			if (start < 0 || end < start) throw new IllegalArgumentException("Invalid range [" + start + ", " + end + ")");
		}
	}

	/** The given positions, in order; the dimension is kept. */
	public record Take(int[] indices) implements Pick {
		public Take {
			indices = indices.clone();
		}

		@Override
		public int[] indices() {
			return this.indices.clone();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof Take other && Arrays.equals(this.indices, other.indices);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(this.indices);
		}
	}

	/** One position; the dimension is removed. */
	public record At(int index) implements Pick {
		public At {
			if (index < 0) throw new IllegalArgumentException("Negative index " + index);
		}
	}

	private static final Selection ALL = new Selection(null, Map.of());

	private final Set<String> variables; // null means all
	private final Map<String, Pick> picks;

	private Selection(Set<String> variables, Map<String, Pick> picks) {
		this.variables = variables;
		this.picks = picks;
	}

	public static Selection all() {
		return ALL;
	}

	public Selection variables(String... names) {
		return this.variables(Arrays.asList(names));
	}

	public Selection variables(Collection<String> names) {
		return new Selection(new LinkedHashSet<>(names), this.picks);
	}

	public Selection range(String dimension, int start, int end) {
		return this.with(dimension, new Range(start, end));
	}

	public Selection take(String dimension, int... indices) {
		return this.with(dimension, new Take(indices));
	}

	public Selection at(String dimension, int index) {
		return this.with(dimension, new At(index));
	}

	private Selection with(String dimension, Pick pick) {
		Objects.requireNonNull(dimension, "dimension");
		Map<String, Pick> picks = new LinkedHashMap<>(this.picks);
		picks.put(dimension, pick);
		return new Selection(this.variables, Map.copyOf(picks));
	}

	/** @return the variables to keep, in the order the selected dataset lists them, or null for all of them */
	public Set<String> variables() {
		return this.variables == null ? null : Collections.unmodifiableSet(new LinkedHashSet<>(this.variables));
	}

	public boolean keepsVariable(String name) {
		return this.variables == null || this.variables.contains(name);
	}

	/** @return how {@code dimension} is narrowed, or null if it is kept whole */
	public Pick pick(String dimension) {
		return this.picks.get(dimension);
	}

	public Map<String, Pick> picks() {
		return this.picks;
	}

	public boolean isAll() {
		return this.variables == null && this.picks.isEmpty();
	}

	@Override
	public String toString() {
		return "Selection[variables=" + (this.variables == null ? "all" : this.variables) + ", picks=" + this.picks + "]";
	}
}
