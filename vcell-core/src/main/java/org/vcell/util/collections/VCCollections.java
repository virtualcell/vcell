package org.vcell.util.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public class VCCollections {
	
	/**
	 * return value for {@link VCCollections#equal(Collection, Collection, Comparator, Collection)}
	 *
	 * @param <T>
	 */
	public static class Delta <T> {
		final T a;
		final T b;
		public Delta(T a, T b) {
			this.a = a;
			this.b = b;
		}
		
		public String toString( ) {
			return String.valueOf(a)  + " != " + String.valueOf(b);
		}
	}
	
	/**
	 * compare Collections using comparator. Order does not have to be the same.
	 * @param a non null Collection 
	 * @param b non null Collection 
	 * @param cmp how to compare
	 * @return false if collections different
	 */
	public static <T> boolean equal(Collection<T> a, Collection<T> b, Comparator<T> cmp) {
		return equal(a,b,cmp,null);
	}
	
	/**
	 * compare Collections using comparator. Order does not have to be the same
	 * @param a non null Collection 
	 * @param b non null Collection 
	 * @param cmp how to compare
	 * @param diffs optional; out parameter with list of different elements, must be modifiable. Existing entries will be removed.
	 * @return false if collections different; note "diffs" is not populated if sizes are different 
	 */
	public static <T> boolean equal(Collection<T> a, Collection<T> b, Comparator<T> cmp , Collection<Delta<T> > diffs) {
		if (a.size( ) != b.size( )) {
			return false;
		}
		@SuppressWarnings("unchecked")
		T one[] = (T[]) a.toArray( );
		@SuppressWarnings("unchecked")
		T two[] = (T[]) b.toArray( );
		Arrays.sort(one,cmp);
		Arrays.sort(two,cmp);
		if (diffs == null) {
			return compareImpl(one,two,cmp);
		}
		return compareImpl(one,two,cmp,diffs);
	}
	
	private static <T> boolean compareImpl(T one[], T two[], Comparator<T> cmp) {
		for (int i = 0 ; i < one.length ; i++) {
			if (cmp.compare(one[i], two[i]) != 0) {
				return false;
			}
		}
		return true;
	}
	
	private static <T> boolean compareImpl(T one[], T two[], Comparator<T> cmp, Collection<Delta<T> > diffs) {
		diffs.clear();
		for (int i = 0 ; i < one.length ; i++) {
			T a = one[i];
			T b = two[i];
			if (cmp.compare(a,b) != 0) {
				diffs.add(new Delta<T>(a, b));
			}
		}
		return diffs.isEmpty();
	}
	
	

}
