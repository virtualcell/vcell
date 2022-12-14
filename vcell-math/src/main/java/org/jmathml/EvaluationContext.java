package org.jmathml;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Supplies substituted variable values for evaluation of ASTNode results. There
 * are various ways to populate the variable values, either through a
 * constructor or various overloaded setValueFor() methods.
 * 
 */
public class EvaluationContext implements IEvaluationContext {

	private Map<String, Iterable<Double>> context = new HashMap<String, Iterable<Double>>();

	/**
	 * This constructor makes a defensive copy of the map passed in. However the
	 * value itself is not copied.
	 * 
	 * @param vars
	 *            A <code>Map</code> of identifier-value pairs.
	 */
	public EvaluationContext(Map<String, Iterable<Double>> vars) {
		for (Map.Entry<String, Iterable<Double>> entry : vars.entrySet()) {
			context.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Default constructor.
	 */
	public EvaluationContext() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Sets the value for a scalar. This overwrites any previous value.
	 * 
	 * @param identifier
	 *            A non-null <code>String</code>.
	 * @param value
	 *            A <code>double</code>.
	 */
	public void setValueFor(String identifier, double value) {
		context.put(identifier, Arrays.asList(value));
	}

	/**
	 * Sets the value for a vector value. This method does not make a defensive
	 * copy of the <code>value</code>, so external changes to this array will
	 * affect this object.
	 * @param identifier  A non-null <code>String</code>
	 * @param value  A non-null {@link Iterable} vector of values
	 */
	public void setValueFor(String identifier, Iterable<Double> value) {
		context.put(identifier, value);
	}

	/**
	 * Sets the value for a vector value. This method makes a defensive copy of
	 * the input array. I.e, changes to the <code>data</code> object will not
	 * affect this object. 
	 * @param identifier A non-null <code>String</code>
	 * @param data A non-null double [] data
	 */
	public void setValueFor(String identifier, final double[] data) {
		Double[] dtaDbl = new Double[data.length];
		for (int i = 0; i < data.length; i++) {
			dtaDbl[i] = data[i];
		}
		context.put(identifier, Arrays.asList(dtaDbl));
	}

	/**
	 * This returns the value of
	 */
	public Iterable<Double> getValueFor(String identifier) {
		return context.get(identifier);
	}

	public boolean hasValueFor(String identifier) {
		return context.get(identifier) != null;
	}

}
