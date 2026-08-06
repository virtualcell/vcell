package org.vcell.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic sharding of a parameterized test's case list, for splitting a
 * slow {@code @MethodSource} across parallel CI runners.
 *
 * <p>Reads two system properties:
 * <ul>
 *   <li>{@code test.shard.count} - number of shards (default 1 = no sharding)</li>
 *   <li>{@code test.shard.index} - which shard this run is, 0-based (default 0)</li>
 * </ul>
 *
 * <p>Partitioning is by stride ({@code i % count == index}) rather than
 * contiguous chunks, so adjacent-and-similar cases are spread evenly across
 * shards and each shard gets a comparable amount of work. The order within a
 * shard is preserved, and with {@code count <= 1} the full list is returned
 * unchanged, so tests behave identically when run without sharding.
 */
public final class TestShard {

	public static final String SHARD_COUNT_PROPERTY = "test.shard.count";
	public static final String SHARD_INDEX_PROPERTY = "test.shard.index";

	/**
	 * When {@code -Dtest.include.slow=true}, tests that normally exclude a
	 * "slow" set of cases (to keep the sharded regression fast) run the full
	 * set instead. Used by the nightly / on-demand regression to keep covering
	 * the slow cases that the merge-gate skips.
	 */
	public static final String INCLUDE_SLOW_PROPERTY = "test.include.slow";

	/**
	 * Narrow a sharded test to the cases whose name contains any of a comma-separated list of
	 * substrings, e.g. {@code -Dtest.only=biomodel_59361239}.
	 * <p>
	 * These suites iterate hundreds of models and take hours; reproducing one reported failure
	 * otherwise means running the lot, or hand-editing the parameter source — which is what the
	 * commented-out {@code return Arrays.asList("biomodel_12522025.vcml:purkinje9")} lines in
	 * MathGenCompareTest and MathOverrideApplyTest were for.
	 * <p>
	 * Matching is a plain substring test against each case's string form, so it works both for
	 * suites whose cases are filenames ({@code biomodel_123.vcml:appName}) and for those whose
	 * cases are objects naming a file.
	 */
	public static final String ONLY_PROPERTY = "test.only";

	private TestShard() {
	}

	/**
	 * Apply {@link #ONLY_PROPERTY}, if set.
	 * <p>
	 * A pattern matching nothing is an error rather than an empty run: surefire reports "Tests run:
	 * 0" as BUILD SUCCESS, so a typo would otherwise look like a pass.
	 */
	private static <T> List<T> applyOnlyFilter(List<T> all) {
		final String only = System.getProperty(ONLY_PROPERTY);
		if (only == null || only.isBlank()) {
			return all;
		}
		final List<String> patterns = new ArrayList<>();
		for (String pattern : only.split(",")) {
			if (!pattern.isBlank()) {
				patterns.add(pattern.trim());
			}
		}
		final List<T> kept = new ArrayList<>();
		for (T t : all) {
			final String name = String.valueOf(t);
			if (patterns.stream().anyMatch(name::contains)) {
				kept.add(t);
			}
		}
		if (kept.isEmpty()) {
			throw new IllegalArgumentException(ONLY_PROPERTY + "=" + only + " matched none of the "
					+ all.size() + " cases this test would otherwise run. Note that cases excluded as"
					+ " slow stay excluded - add -D" + INCLUDE_SLOW_PROPERTY + "=true to reach those.");
		}
		return kept;
	}

	/**
	 * @param all every case the test would run unsharded
	 * @return the subset belonging to this shard (all of them if unsharded)
	 */
	public static <T> List<T> shard(Iterable<T> all) {
		final List<T> everything = new ArrayList<>();
		for (T t : all) {
			everything.add(t);
		}
		// narrow before sharding, so -Dtest.only picks up its matches whichever shard they land in
		final List<T> list = applyOnlyFilter(everything);
		final int count = Integer.getInteger(SHARD_COUNT_PROPERTY, 1);
		final int index = Integer.getInteger(SHARD_INDEX_PROPERTY, 0);
		if (count <= 1) {
			return list;
		}
		if (index < 0 || index >= count) {
			throw new IllegalArgumentException(
					SHARD_INDEX_PROPERTY + "=" + index + " is out of range for " + SHARD_COUNT_PROPERTY + "=" + count);
		}
		final List<T> out = new ArrayList<>();
		for (int i = index; i < list.size(); i += count) {
			out.add(list.get(i));
		}
		return out;
	}
}
