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

	private TestShard() {
	}

	/**
	 * @param all every case the test would run unsharded
	 * @return the subset belonging to this shard (all of them if unsharded)
	 */
	public static <T> List<T> shard(Iterable<T> all) {
		final List<T> list = new ArrayList<>();
		for (T t : all) {
			list.add(t);
		}
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
