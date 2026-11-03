package org.vcell.data;

import java.util.List;
import java.util.Map;

/**
 * Describes one variable of a {@link Dataset}: which dimensions its values run over, and what they mean. The values
 * themselves come from the variable's {@link ValueSource}.
 *
 * @param name       unique within the dataset
 * @param dimensions names of the dataset dimensions the values run over, outermost first
 * @param unit       unit symbol, or null when unknown
 * @param role       what kind of quantity this is
 * @param expression for a {@link Role#FUNCTION}, the expression that defines it; otherwise null
 * @param domain     the subdomain (compartment, membrane) the variable is defined on, or null when it has none
 * @param attributes anything else worth carrying to a writer (display name, VCell variable type, ...)
 */
public record Variable(String name, List<String> dimensions, String unit, Role role, String expression,
					   String domain, Map<String, String> attributes) {

	public enum Role {
		/** A value the solver produced. */
		DATA,
		/** A value computed from other variables by {@link Variable#expression()}. */
		FUNCTION,
		/** A value that describes positions along a dimension rather than a result (e.g. a scan parameter per job). */
		COORDINATE
	}

	public Variable {
		if (name == null || name.isBlank()) throw new IllegalArgumentException("Variable name cannot be blank");
		dimensions = List.copyOf(dimensions);
		if (dimensions.stream().distinct().count() != dimensions.size())
			throw new IllegalArgumentException("Variable '" + name + "' repeats a dimension: " + dimensions);
		if (role == null) role = Role.DATA;
		attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
	}

	public static Variable data(String name, List<String> dimensions) {
		return new Variable(name, dimensions, null, Role.DATA, null, null, null);
	}

	public Variable withDimensions(List<String> newDimensions) {
		return new Variable(this.name, newDimensions, this.unit, this.role, this.expression, this.domain, this.attributes);
	}
}
