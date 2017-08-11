package org.vcell.util;

import java.util.List;

/**
 * generic conversion utilities
 */
public class GenericUtils {
	
	/**
	 * safely convert List<?> to List<T> 
	 * @param list
	 * @param clzz T to convert
	 * @return same list, checked for type safety
	 * @throws RuntimeException if list contains element which is not T
	 */
	@SuppressWarnings("unchecked")
	public static <T> java.util.List<T> convert(java.util.List<?> list, Class<T> clzz) {
		for (Object o : list) {
			if (!clzz.isAssignableFrom(o.getClass()) ) {
				throw new RuntimeException("invalid list conversion, " + clzz.getName() 
						+  " list contains " + o.getClass());
			}
		}
		return (List<T>) list;
	}
}
