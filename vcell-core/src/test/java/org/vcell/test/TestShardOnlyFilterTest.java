package org.vcell.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** {@link TestShard#ONLY_PROPERTY}, which narrows a long sharded suite to named cases. */
@Tag("Fast")
public class TestShardOnlyFilterTest {

	private static final List<String> CASES = List.of(
			"biomodel_59361239.vcml:full_model",
			"biomodel_59361239.vcml:receptor_density",
			"biomodel_147699816.vcml:-sorafenib",
			"biomodel_28625786.vcml:simple_1");

	@AfterEach
	public void clearProperties() {
		System.clearProperty(TestShard.ONLY_PROPERTY);
		System.clearProperty(TestShard.SHARD_COUNT_PROPERTY);
		System.clearProperty(TestShard.SHARD_INDEX_PROPERTY);
	}

	@Test
	public void unsetPropertyRunsEverything() {
		assertEquals(CASES, TestShard.shard(CASES));
	}

	@Test
	public void blankPropertyRunsEverything() {
		System.setProperty(TestShard.ONLY_PROPERTY, "   ");
		assertEquals(CASES, TestShard.shard(CASES));
	}

	@Test
	public void selectsBySubstring() {
		System.setProperty(TestShard.ONLY_PROPERTY, "biomodel_59361239");
		assertEquals(List.of("biomodel_59361239.vcml:full_model", "biomodel_59361239.vcml:receptor_density"),
				TestShard.shard(CASES));
	}

	@Test
	public void selectsASingleApplication() {
		System.setProperty(TestShard.ONLY_PROPERTY, "biomodel_59361239.vcml:receptor_density");
		assertEquals(List.of("biomodel_59361239.vcml:receptor_density"), TestShard.shard(CASES));
	}

	@Test
	public void acceptsACommaSeparatedList() {
		System.setProperty(TestShard.ONLY_PROPERTY, "biomodel_147699816, biomodel_28625786");
		assertEquals(List.of("biomodel_147699816.vcml:-sorafenib", "biomodel_28625786.vcml:simple_1"),
				TestShard.shard(CASES));
	}

	/**
	 * The point of failing loudly: surefire reports a zero-test run as BUILD SUCCESS, so a typo in
	 * the pattern would otherwise look like everything passed.
	 */
	@Test
	public void aPatternMatchingNothingIsAnError() {
		System.setProperty(TestShard.ONLY_PROPERTY, "biomodel_does_not_exist");
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> TestShard.shard(CASES));
		assertTrue(e.getMessage().contains(TestShard.INCLUDE_SLOW_PROPERTY),
				"should point at the usual cause - the case is excluded as slow: " + e.getMessage());
	}

	/** Narrowing happens before sharding, so a selected case is not then sharded away. */
	@Test
	public void selectionIsNotLostToSharding() {
		System.setProperty(TestShard.ONLY_PROPERTY, "biomodel_28625786");
		System.setProperty(TestShard.SHARD_COUNT_PROPERTY, "4");
		System.setProperty(TestShard.SHARD_INDEX_PROPERTY, "0");
		assertEquals(List.of("biomodel_28625786.vcml:simple_1"), TestShard.shard(CASES));
	}

	@Test
	public void shardingStillWorksWithoutTheFilter() {
		System.setProperty(TestShard.SHARD_COUNT_PROPERTY, "2");
		System.setProperty(TestShard.SHARD_INDEX_PROPERTY, "1");
		assertEquals(List.of("biomodel_59361239.vcml:receptor_density", "biomodel_28625786.vcml:simple_1"),
				TestShard.shard(CASES));
	}
}
