package cbit.vcell.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Guards {@code vcell-legacy-env-names.properties}, the mapping from a VCell property to the
 * short historical name the deployment supplies it under.
 *
 * <h2>What can no longer be checked, and why</h2>
 *
 * This table was extracted from the {@code -D} flags in {@code docker/build/Dockerfile-*-dev},
 * and those flags were the only in-repository record of the mapping. They have since been
 * removed — that was their purpose, and removing them is what the provider made possible — so
 * the Dockerfiles cannot be diffed against this file any more.
 *
 * The other end of the mapping now lives entirely in vcell-fluxcd
 * ({@code kustomize/config/<overlay>/*.env}), which this repository cannot see. So <b>nothing
 * here can confirm that a name is still the one the deployment actually sets</b>. That is a real
 * loss of coverage and worth stating plainly rather than leaving as a gap someone discovers.
 *
 * <h2>What is still worth checking</h2>
 *
 * The failure modes that remain are the ones internal to this repository: an alias naming a
 * property that no longer exists, an alias that duplicates what the standard rules already
 * produce, and two properties quietly claiming the same environment variable.
 */
@Tag("Fast")
public class LegacyEnvironmentNamesTest {

	/**
	 * An alias for a property nobody declares configures nothing. It would most likely arrive by
	 * renaming a property and forgetting this file, which fails silently: the property resolves
	 * from its own defaults and the deployment's value is ignored.
	 */
	@Test
	public void everyAliasedPropertyIsDeclaredInPropertyLoader() {
		List<String> unknown = new ArrayList<>();
		for (String property : EnvironmentConfigProvider.legacyNames().keySet()) {
			if (!PropertyLoader.declaredPropertyNames().contains(property)) {
				unknown.add(property);
			}
		}
		java.util.Collections.sort(unknown);
		assertEquals(List.of(), unknown,
				"these are aliased but not declared via PropertyLoader.record(); either the"
						+ " property was renamed and this file was not, or the entry is dead");
	}

	/** An alias only earns its place by being one the standard rules would not have found. */
	@Test
	public void everyAliasIsOneTheStandardRulesWouldMiss() {
		List<String> redundant = new ArrayList<>();
		for (Map.Entry<String, String> entry : EnvironmentConfigProvider.legacyNames().entrySet()) {
			if (EnvironmentConfigProvider.environmentNamesFor(entry.getKey()).contains(entry.getValue())) {
				redundant.add(entry.getKey() + " -> " + entry.getValue());
			}
		}
		java.util.Collections.sort(redundant);
		assertEquals(List.of(), redundant,
				"these are already resolved by the exact/sanitised/upper-case rules");
	}

	/**
	 * Two properties sharing one environment variable means one of them silently takes the
	 * other's value — the kind of thing that looks fine until a deployment sets it.
	 */
	@Test
	public void noTwoPropertiesClaimTheSameEnvironmentVariable() {
		Map<String, String> byEnvironmentName = new LinkedHashMap<>();
		List<String> collisions = new ArrayList<>();
		for (Map.Entry<String, String> entry : EnvironmentConfigProvider.legacyNames().entrySet()) {
			String previous = byEnvironmentName.put(entry.getValue(), entry.getKey());
			if (previous != null) {
				collisions.add(entry.getValue() + " claimed by both " + previous + " and " + entry.getKey());
			}
		}
		java.util.Collections.sort(collisions);
		assertEquals(List.of(), collisions, "one environment variable cannot feed two properties");
	}

	/**
	 * The table carries what was a 157-flag wall across five Dockerfiles. A near-empty one means
	 * it failed to load, and every service would then start on its defaults — which is a far
	 * quieter failure than it deserves to be.
	 */
	@Test
	public void theTableLoaded() {
		assertTrue(EnvironmentConfigProvider.legacyNames().size() > 50,
				"expected the full legacy mapping, got "
						+ EnvironmentConfigProvider.legacyNames().size() + " entries");
		assertEquals("dburl", EnvironmentConfigProvider.legacyNameFor("vcell.server.dbConnectURL"));
		assertEquals("serverid", EnvironmentConfigProvider.legacyNameFor("vcell.server.id"));
	}
}
