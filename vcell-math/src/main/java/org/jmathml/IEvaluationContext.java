package org.jmathml;

import java.util.Arrays;

/**
 * Interface for providing variable resolution in MathML expressions
 * @author radams
 */
public interface IEvaluationContext {

	/**
	 * Gets a value for a given identifier, or null if can't be found. 
	 * @param identifier The identifier
	 * @return The <code>Iterable</code> value or <code>null</code> if
	 *         not found.
	 */
	public Iterable<Double> getValueFor(String identifier);

	/**
	 * Boolean test for whether a variable (identified by the argument) can be
	 * resolved to a value.
	 * @param identifier A String identifier
	 * @return <code>true</code> if this value can be resolved,
	 *         <code>false</code> otherwise.
	 */
	public boolean hasValueFor(String identifier);

	/**
	 * Default null object implementation
	 */
	public final static IEvaluationContext NULL_CONTEXT = new IEvaluationContext() {

		/**
		 * 
		 */
		public Iterable<Double> getValueFor(String identifier) {
			return Arrays.asList(0d);
		}

		/**
		 * Always returns <code>false</code>.
		 */
		public boolean hasValueFor(String identifier) {
			// TODO Auto-generated method stub
			return false;
		}
	};
}
