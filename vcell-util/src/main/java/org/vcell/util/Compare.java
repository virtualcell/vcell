/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiPredicate;

/**
 * This type was created in VisualAge.
 */
public class Compare {
	private static final Logger lg = LogManager.getLogger(Compare.class);

	public interface CompareLogger {
		void compareFailed();
	}

	public static CompareLogger logger = null;//User sets logger implementation
	public static boolean loggingEnabled = false;//User sets logging on/off
	private static Object logInternalDisable = null;//logging off for intermediate comparisons

	private static Object getLogInternalDisableLock() {
		return (loggingEnabled ? new Object() : null);
	}

	private static void logInternalDisable(Object lock) {
		//Turns off logging internally.  Needed when unordered lists are being compared so
		//inappropriate 'false' logging doesn't occur.
		//'lock' ensures that nested Compare.xxx comparisons can't re-enable logging before finishing
		//a temporarily logging-disabled compare operation
		if (logInternalDisable == null) {
			logInternalDisable = lock;
		}
	}

	private static void logInternalEnable(Object lock) {
		//Only the lock owner can turn logging back on after
		//being internally disabled
		if (lock != null && lock.equals(logInternalDisable)) {
			logInternalDisable = null;
		}
	}

	public static boolean logFailure() {
		return Compare.logFailure(null);
	}

	public static boolean logFailure(Object internalDisableLock) {
		if (internalDisableLock != null) {
			//try our lock to re-enable logging
			logInternalEnable(internalDisableLock);
		}
		if ((logInternalDisable == null) && Compare.logger != null && Compare.loggingEnabled) {
			logger.compareFailed();
		}
		return false;
	}


	/**
	 * compatibility method for enums
	 * calls {@link Enum#equals(Object)}
	 *
	 * @param e1 one enum (not null)
	 * @param e2 another enum (not null)
	 * @return true if they're the same
	 * @throws IllegalArgumentException if either null
	 */
	public static <T extends Enum<?>> boolean isEqual(T e1, T e2) {
		if (e1 == null || e2 == null) {
			throw new IllegalArgumentException("null argument " + e1 + " and/or " + e2);
		}
		return e1.equals(e2);
	}

	public static <T extends Enum<?>> boolean isEqualOrNullStrict(T[] e1, T[] e2) {
		if (e1 == null && e2 == null) return true;
		if (e1 == null) return e2.length == 0;
		if (e2 == null) return e1.length == 0;

		if (e1.length != e2.length) return false;
		if (e1.length == 0) return true;
		List<String> elements1 = Arrays.stream(e1).map(T::name).toList();
		List<String> elements2 = Arrays.stream(e2).map(T::name).toList();
		return isEqual(elements1, elements2, String::equals);
	}

	public static boolean isEqual(int[] v1, int[] v2) {
		if (v1 == null || v2 == null) throw new RuntimeException("Compare.isEqual(int[],int[]) received null argument(s)");
		if (v1.length != v2.length) return logFailure();

		// check that every int is the same in v1[] and v2[]
		for (int i = 0; i < v1.length; i++) {
			if (v1[i] == v2[i]) continue;
			return logFailure();
		}
		return true;
	}

	public static boolean isEqual(short[] v1, short[] v2) {
		if (v1 == null || v2 == null) throw new RuntimeException("Compare.isEqual(short[],short[]) received null argument(s)");
		if (v1.length != v2.length) return logFailure();

		// check that every short is the same in v1[] and v2[]
		for (int i = 0; i < v1.length; i++) {
			if (v1[i] == v2[i]) continue;
			return logFailure();
		}
		return true;
	}

	public static boolean isEqual(float[] v1, float[] v2) {
		if (v1 == null || v2 == null) throw new RuntimeException("Compare.isEqual(float[],float[]) received null argument(s)");
		if (v1.length != v2.length) return logFailure();

		// check that every float is the same in v1[] and v2[]
		for (int i = 0; i < v1.length; i++) {
			if (v1[i] == v2[i]) continue;
			return logFailure();
		}
		return true;
	}

	public static boolean isEqual(double[] v1, double[] v2) {
		if (v1 == null || v2 == null) throw new RuntimeException("Compare.isEqual(double[],double[]) received null argument(s)");
		if (v1.length != v2.length) return logFailure();

		// check that every double is the same in v1[] and v2[]
		for (int i = 0; i < v1.length; i++) {
			if (v1[i] == v2[i]) continue;
			return logFailure();
		}
		return true;
	}

	public static boolean isEqual(String[] v1, String[] v2){
		return isEqual(v1, v2, String::equals);
	}

	@Contract(pure = true)
	public static <T extends Matchable> boolean isEqual(T[] e1, T[] e2) {
		return isEqual(Arrays.asList(e1), Arrays.asList(e2));
	}

	@Contract(pure = true)
	public static <T> boolean isEqual(T[] e1, T[] e2, BiPredicate<T, T> predicate) {
		return isEqual(Arrays.asList(e1), Arrays.asList(e2), predicate);
	}

	//TODO: Rename to `isEqualOrNull`
	public static boolean isEqual(Matchable obj1, Matchable obj2) {
		return isEqual(obj1, obj2, Matchable::compareEqual);
	}

	//TODO: Rename to `isEqualOrNull`
	public static boolean isEqual(Matchable obj1, Matchable obj2, BiPredicate<Matchable, Matchable> predicate) {
		if (obj1 == null && obj2 == null) return true;
		if (obj1 == null || obj2 == null) return logFailure();
		return predicate.test(obj1, obj2);
	}

	//TODO: Rename to `isEqualOrNull`
	public static boolean isEqual(java.lang.Number obj1, java.lang.Number obj2) {
		return isEqual(obj1, obj2, Object::equals);
	}

	public static boolean isEqual(String obj1, String obj2) {
		return isEqual(obj1, obj2, Object::equals);
	}

	public static boolean isEqual(java.util.Date obj1, java.util.Date obj2) {
		return isEqual(obj1, obj2, Object::equals);
	}

	private static <T> boolean isEqual(T obj1, T obj2, BiPredicate<T, T> predicate) {
		if (obj1 == null && obj2 == null) return true;
		if (obj1 == null || obj2 == null) return logFailure();
		if (!predicate.test(obj1, obj2)) return logFailure();
		return true;
	}

	public static <T extends Matchable> boolean isEqualOrNull(List<T> v1, List<T> v2) {
		if (v1 == null && v2 == null) return true;
		if (v1 == null || v2 == null) return logFailure();
		return isEqual(v1, v2);
	}

	public static <T> boolean isEqualOrNull(List<T> v1, List<T> v2, BiPredicate<T, T> predicate) {
		if (v1 == null && v2 == null) return true;
		if (v1 == null || v2 == null) return logFailure();
		return isEqual(v1, v2, predicate);
	}

	public static <T extends Matchable> boolean isEqual(List<T> v1, List<T> v2) {
		return isEqual(v1, v2, Matchable::compareEqual);
	}

	@Contract(value = "null, _, _ -> fail; !null, null, _ -> fail",  pure = true)
	public static <T> boolean isEqual(List<T> v1, List<T> v2, BiPredicate<T, T> equalityTest) {
		if (v1 == null || v2 == null) throw new IllegalArgumentException("Compare.isEqual(List<T>,List<T>) received null argument(s)");
		if (v1.size() != v2.size()) return logFailure();

		List<T> unmatchedV1 = new ArrayList<>();
		List<T> unmatchedV2 = new ArrayList<>();

		// assume everything is ordered similarly first, and check that every element v1[i] == v2[i]
		Object internalDisableLock = getLogInternalDisableLock();
		Compare.logInternalDisable(internalDisableLock);

		for (int i = 0; i < v1.size(); i++) {
			if (equalityTest.test(v2.get(i), v1.get(i))) continue;
			unmatchedV1.add(v1.get(i));
			unmatchedV2.add(v2.get(i));
		}

		if (unmatchedV1.isEmpty()) {
			Compare.logInternalEnable(internalDisableLock);
			return true;
		}

		VCAssert.assertTrue(unmatchedV1.size() == unmatchedV2.size(), "if starting sizes are the same, then unmatches sizes should too.");

		// Find matches across different indexes, iterating one-way through "unmatched1", and removing from "unmatched2"
		for (T c1 : unmatchedV1) {
			boolean matchFound = false;
			for (int i2 = 0; i2 < unmatchedV2.size(); i2++) {
				if (!equalityTest.test(unmatchedV2.get(i2), c1)) continue;
				matchFound = true;
				unmatchedV2.remove(i2);
				break;
			}
			if (!matchFound) return logFailure(internalDisableLock);
		}

		VCAssert.assertTrue(unmatchedV2.isEmpty(), "sizes matched, but despite removing pairs and successfully finding matches, somehow something is left over??");
		Compare.logInternalEnable(internalDisableLock);
		return true;
	}

	@Contract(pure = true)
	public static boolean isEqualOrNull(double[] v1, double[] v2) {
		if (v1 == null && v2 == null) return true;
		return isEqual(v1, v2);
	}

	@Contract(pure = true)
	public static boolean isEqualOrNull(int[] v1, int[] v2) {
		if (v1 == null && v2 == null) return true;
		return isEqual(v1, v2);
	}

	@Contract(pure = true)
	public static boolean isEqualOrNull(Matchable[] v1, Matchable[] v2) {
		if (v1 == null && v2 == null) return true;
		if (v1 == null || v2 == null) return logFailure();
		return isEqual(v1, v2);
	}

	@Contract(pure = true)
	public static boolean isEqualOrNull(Matchable obj1, Matchable obj2, BiPredicate<Matchable, Matchable> predicate) {
		if (obj1 == null && obj2 == null) return true;
		if (obj1 == null || obj2 == null) return logFailure();
		if (!predicate.test(obj1, obj2)) return logFailure();
		return true;
	}

	@Contract(pure = true)
	public static boolean isEqualOrNull(Matchable obj1, Matchable obj2) {
		BiPredicate<Matchable, Matchable> predicate = Matchable::compareEqual;
		return isEqualOrNull(obj1, obj2, predicate);
	}

	@Contract(pure = true)
	public static boolean isEqualOrNull(java.lang.Number obj1, java.lang.Number obj2) {
		return isEqualOrNull(obj1, obj2, Object::equals);
	}

	@Contract(pure = true)
	public static boolean isEqualOrNull(String obj1, String obj2) {
		return isEqualOrNull(obj1, obj2, Object::equals);
	}

	@Contract(pure = true)
	private static <T> boolean isEqualOrNull(T obj1, T obj2, BiPredicate<T, T> predicate) {
		if (obj1 == null && obj2 == null) return true;
		if (obj1 == null || obj2 == null) return logFailure();
		if (!predicate.test(obj1, obj2)) return logFailure();
		return true;
	}

	@Contract(pure = true)
	public static boolean isEqualOrNullStrict(Matchable[] v1, Matchable[] v2) {
		if (v1 == null && v2 == null) return true;
		return isEqualStrict(v1, v2);
	}

	@Contract(pure = true)
	public static boolean isEqualStrict(Matchable[] v1, Matchable[] v2) {
		if (v1 == null || v2 == null) throw new RuntimeException("Compare.isEqualStrict(Matchable[],Matchable[]) received null argument(s)");
		if (v1.length != v2.length) return logFailure();

		// check that each array is the same (and elements are in the same order)
		for (int i = 0; i < v1.length; i++) {
			if (v1[i].compareEqual(v2[i])) continue;
			return logFailure();
		}

		return true;
	}

	@Contract(pure = true)
	public static boolean isEqualStrict(List<? extends Matchable> v1, List<? extends Matchable> v2) {
		if (v1 == null || v2 == null) throw new RuntimeException("Compare.isEqualStrict(List<Matchable>,List<Matchable>) received null argument(s)");
		if (v1.size() != v2.size()) return logFailure();

		// check that each array is the same (and elements are in the same order)
		for (int i = 0; i < v1.size(); i++) {
			if (v1.get(i).compareEqual(v2.get(i))) continue;
			return logFailure();
		}

		return true;
	}

	/**
	 * Note that because there's no expectation that compareEquals and hashcode are aligned in a class, each key-value pair is compared against every key-value pair in the
	 */
	@Contract(pure = true)
	public static <KeyType extends Matchable, ValueType extends Matchable> boolean isEqual(Map<KeyType, ValueType> map1, Map<KeyType, ValueType> map2) {
		// WARNING! This algorithm has a blind spot. Should `hashcode` and `isEqual` of `KeyCode` not match 1:1, the
		// algorithm can mistakenly call a map of differing sizes and/or elements as equal.
		// Let's call whatever subset of a class' fields are revelant for `compareEqual` the "virtual image";
		// so long as at least one map contains a key set larger than the set of the equivalent `virtual images`,
		// and both maps contain the same mappings from virtual images to values, there's an opportunity for "duplicates"
		// to fill up the map, and for `isEqual` to still return true.
		// it can be fixed by removing "matching pairs" from comparison as matches are made.
		return Compare.isEqualOneWay(map1, map2) && Compare.isEqualOneWay(map2, map1);
	}

	@Contract(pure = true)
	private static <KeyType extends Matchable, ValueType extends Matchable> boolean isEqualOneWay(Map<KeyType, ValueType> primary, Map<KeyType, ValueType> comparison){
		// compare entries from primary to comparison
		for (KeyType primaryKey : primary.keySet()) {
			boolean bFoundOtherKey = false;
			for (KeyType comparisonKey : comparison.keySet()) {
				if (!Compare.isEqual(comparisonKey, primaryKey)) continue;
				bFoundOtherKey = true;
				if (!Compare.isEqual(comparison.get(comparisonKey), primary.get(primaryKey))) return false; // keys are the same but values are different.
				break;
			}
			if (!bFoundOtherKey) return false; // can't find other key
		}
		return true;
	}

	/**
	 * compares list for same numbers of items and elements that {@link Matchable#compareEqual(Matchable)}, in any order
	 *
	 * @return true if criteria met
	 */
	@Contract(pure = true)
	public static <T extends Matchable> boolean areEquivalent(Collection<T> lhs, Collection<T> rhs) {
		if (lhs.size() != rhs.size()) return false;
		List<Matchable> punchList = new LinkedList<>(rhs);
		for (Matchable m : lhs) {
			boolean found = false;
			Iterator<T> iter = rhs.iterator();
			while (iter.hasNext() && !found) {
				if (m.compareEqual(iter.next())) {
					iter.remove();
					found = true;
				}
			}
			if (!found) {
				return false;
			}
		}
		VCAssert.assertTrue(punchList.isEmpty(), "logic error");

		return true;
	}

	/**
	 * see if any fields within an object that implements {@link Matchable} are unequal (both null or {@link Matchable#compareEqual(Matchable)} is false)
	 *
	 * @param lhs null is okay
	 * @param rhs null is okay
	 * @return false if both null or all fields equal
	 */
	@Contract(pure = true)
	public static boolean hasNonEqualFields(Matchable lhs, Matchable rhs) {
		if (null == lhs && null == rhs) return false;
		if (null == lhs || null == rhs) return true;
		Class<? extends Matchable> lClass = lhs.getClass();
		for (Field f : lClass.getDeclaredFields()) {
			try {
				f.setAccessible(true);
				Matchable lMatch = CastingUtils.downcast(Matchable.class, f.get(lhs));
				if (null == lMatch) continue;
				Matchable rMatch = CastingUtils.downcast(Matchable.class, f.get(rhs));
				if (isEqual(lMatch, rMatch)) continue;
				return true;
			} catch (Exception e) {
				lg.error(e);
			}
		}
		return false;
	}

	/**
	 * whether two objects are null or not
	 */
	public enum Nullness {
		BOTH_NULL,
		NEITHER_NULL,
		FIRST_NULL,
		SECOND_NULL;

		public static Nullness determineNullState(Object first, Object second) {
			if (first == null) return second == null ? Nullness.BOTH_NULL : Nullness.FIRST_NULL;
			return second == null ? Nullness.SECOND_NULL : Nullness.NEITHER_NULL;
		}
	}
}