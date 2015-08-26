package org.vcell.util;

/**
 * adapt Java language to local Virtual Cell policy
 */
public interface ObjectToMatchable extends Matchable {
	/**
	 * calls {@link Object#equals(Object)}
	 */
	@Override
	default boolean compareEqual(Matchable obj) {
		return equals(obj);
	}
}
