package org.vcell.data;

import org.vcell.data.array.DoubleArrayND;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * The one container for simulation results in {@code org.vcell.data}: named {@link Dimension}s, {@link Variable}s
 * over them, and the {@link Topology} that says what the spatial dimensions mean.
 * <p>
 * Variables over different index spaces live side by side (a volume variable over {@code volume}, a membrane
 * variable over {@code membrane}, a scan parameter over {@code job}), which is why this is a set of arrays sharing
 * dimensions rather than one array.
 * <p>
 * A dataset never holds values directly: each variable has a {@link ValueSource}, and {@link #select} composes a
 * narrowed view without reading anything. Values are produced only when {@link #values} is called.
 */
public final class Dataset {

	private record Entry(Variable variable, ValueSource source) { }

	private final String name;
	private final Map<String, Dimension> dimensions;
	private final Map<String, Entry> variables;
	private final Topology topology;
	private final Map<String, String> attributes;

	private Dataset(String name, Map<String, Dimension> dimensions, Map<String, Entry> variables, Topology topology, Map<String, String> attributes) {
		this.name = name;
		this.dimensions = dimensions;
		this.variables = variables;
		this.topology = topology;
		this.attributes = attributes;
	}

	public static Builder builder(String name) {
		return new Builder(name);
	}

	public String name() {
		return this.name;
	}

	public List<Dimension> dimensions() {
		return List.copyOf(this.dimensions.values());
	}

	public Dimension dimension(String dimensionName) {
		Dimension dimension = this.dimensions.get(dimensionName);
		if (dimension == null) throw new NoSuchElementException("No dimension '" + dimensionName + "' in dataset '" + this.name + "'");
		return dimension;
	}

	public boolean hasDimension(String dimensionName) {
		return this.dimensions.containsKey(dimensionName);
	}

	public List<Variable> variables() {
		return this.variables.values().stream().map(Entry::variable).toList();
	}

	/** @return the variables with the given role, in dataset order */
	public List<Variable> variables(Variable.Role role) {
		return this.variables().stream().filter(variable -> variable.role() == role).toList();
	}

	public Variable variable(String variableName) {
		return this.entry(variableName).variable();
	}

	public boolean hasVariable(String variableName) {
		return this.variables.containsKey(variableName);
	}

	public Topology topology() {
		return this.topology;
	}

	public Map<String, String> attributes() {
		return this.attributes;
	}

	/** @return the shape a variable's values have: the size of each of its dimensions, in order */
	public long[] shapeOf(String variableName) {
		return this.variable(variableName).dimensions().stream().mapToLong(d -> this.dimension(d).size()).toArray();
	}

	/**
	 * Reads a variable's values.
	 *
	 * @throws DataReadException if its source cannot produce them
	 */
	public DoubleArrayND values(String variableName) {
		Entry entry = this.entry(variableName);
		DoubleArrayND values = entry.source().read(entry.variable().dimensions(), Selection.all());
		long[] expected = this.shapeOf(variableName);
		if (!Arrays.equals(values.shape(), expected))
			throw new DataReadException("Source for '" + variableName + "' produced shape " + Arrays.toString(values.shape())
					+ " but its dimensions have shape " + Arrays.toString(expected));
		return values;
	}

	/**
	 * @return a dataset narrowed by {@code selection}. Nothing is read: the new dataset's variables read through
	 * this one's sources, passing the selection down so lazy sources can read only what is kept.
	 */
	public Dataset select(Selection selection) {
		Objects.requireNonNull(selection, "selection");
		if (selection.isAll()) return this;
		for (String dimensionName : selection.picks().keySet()) this.dimension(dimensionName);
		if (selection.variables() != null) for (String variableName : selection.variables()) this.entry(variableName);

		Map<String, Dimension> narrowedDimensions = new LinkedHashMap<>();
		for (Dimension dimension : this.dimensions.values()) {
			Selection.Pick pick = selection.pick(dimension.name());
			if (pick == null) {
				narrowedDimensions.put(dimension.name(), dimension);
			} else if (pick instanceof Selection.Range range) {
				if (range.end() > dimension.size())
					throw new IndexOutOfBoundsException("Range end " + range.end() + " beyond '" + dimension.name() + "' of size " + dimension.size());
				narrowedDimensions.put(dimension.name(), dimension.subset(Dimension.range(range.start(), range.end())));
			} else if (pick instanceof Selection.Take take) {
				for (int index : take.indices()) Objects.checkIndex(index, dimension.size());
				narrowedDimensions.put(dimension.name(), dimension.subset(take.indices()));
			} else if (pick instanceof Selection.At at) {
				Objects.checkIndex(at.index(), dimension.size()); // the dimension is removed
			}
		}

		// when variables are named, the selection's order becomes the dataset's order
		List<Entry> kept = selection.variables() == null ? List.copyOf(this.variables.values())
				: selection.variables().stream().map(this::entry).toList();
		Map<String, Entry> narrowedVariables = new LinkedHashMap<>();
		for (Entry entry : kept) {
			Variable variable = entry.variable();
			List<String> remaining = Selections.remainingDimensions(variable.dimensions(), selection);
			ValueSource source = entry.source();
			List<String> sourceDimensions = variable.dimensions();
			ValueSource narrowed = (dimensionNames, furtherSelection) ->
					Selections.apply(source.read(sourceDimensions, selection), dimensionNames, furtherSelection);
			narrowedVariables.put(variable.name(), new Entry(variable.withDimensions(remaining), narrowed));
		}
		return new Dataset(this.name, narrowedDimensions, narrowedVariables, this.topology, this.attributes);
	}

	/** @return a builder seeded with this dataset's contents, for deriving a modified copy */
	public Builder toBuilder() {
		Builder builder = new Builder(this.name).topology(this.topology).attributes(this.attributes);
		this.dimensions.values().forEach(builder::dimension);
		this.variables.values().forEach(entry -> builder.variable(entry.variable(), entry.source()));
		return builder;
	}

	private Entry entry(String variableName) {
		Entry entry = this.variables.get(variableName);
		if (entry == null) throw new NoSuchElementException("No variable '" + variableName + "' in dataset '" + this.name + "'");
		return entry;
	}

	@Override
	public String toString() {
		return "Dataset[" + this.name + ", dimensions=" + this.dimensions.keySet() + ", variables=" + this.variables.keySet()
				+ ", topology=" + this.topology.kind() + "]";
	}

	public static final class Builder {
		private String name;
		private final Map<String, Dimension> dimensions = new LinkedHashMap<>();
		private final Map<String, Entry> variables = new LinkedHashMap<>();
		private Topology topology = Topology.NONSPATIAL;
		private final Map<String, String> attributes = new LinkedHashMap<>();

		private Builder(String name) {
			this.name(name);
		}

		public Builder name(String name) {
			if (name == null || name.isBlank()) throw new IllegalArgumentException("Dataset name cannot be blank");
			this.name = name;
			return this;
		}

		public Builder dimension(Dimension dimension) {
			Dimension existing = this.dimensions.putIfAbsent(dimension.name(), dimension);
			if (existing != null && !existing.equals(dimension))
				throw new IllegalArgumentException("Dimension '" + dimension.name() + "' already defined differently");
			return this;
		}

		/** Adds a variable whose values are already in memory; the shape is checked against its dimensions now. */
		public Builder variable(Variable variable, DoubleArrayND values) {
			this.checkDimensionsDefined(variable);
			long[] expected = variable.dimensions().stream().mapToLong(d -> this.dimensions.get(d).size()).toArray();
			if (!Arrays.equals(values.shape(), expected))
				throw new IllegalArgumentException("Values for '" + variable.name() + "' have shape " + Arrays.toString(values.shape())
						+ " but its dimensions " + variable.dimensions() + " have shape " + Arrays.toString(expected));
			return this.variable(variable, ValueSource.of(values));
		}

		/** Adds a variable whose values come from {@code source}; its shape is checked when the values are read. */
		public Builder variable(Variable variable, ValueSource source) {
			this.checkDimensionsDefined(variable);
			Objects.requireNonNull(source, "source");
			if (this.variables.putIfAbsent(variable.name(), new Entry(variable, source)) != null)
				throw new IllegalArgumentException("Variable '" + variable.name() + "' already added");
			return this;
		}

		public Builder topology(Topology topology) {
			this.topology = Objects.requireNonNull(topology, "topology");
			return this;
		}

		public Builder attribute(String key, String value) {
			this.attributes.put(key, value);
			return this;
		}

		public Builder attributes(Map<String, String> attributes) {
			this.attributes.putAll(attributes);
			return this;
		}

		private void checkDimensionsDefined(Variable variable) {
			for (String dimensionName : variable.dimensions())
				if (!this.dimensions.containsKey(dimensionName))
					throw new IllegalArgumentException("Variable '" + variable.name() + "' uses undefined dimension '" + dimensionName + "'");
		}

		public Dataset build() {
			for (String spatial : this.topology.spatialDimensions())
				if (!this.dimensions.containsKey(spatial))
					throw new IllegalStateException("Topology names dimension '" + spatial + "' that the dataset does not define");
			return new Dataset(this.name, new LinkedHashMap<>(this.dimensions), new LinkedHashMap<>(this.variables),
					this.topology, Map.copyOf(this.attributes));
		}
	}
}
