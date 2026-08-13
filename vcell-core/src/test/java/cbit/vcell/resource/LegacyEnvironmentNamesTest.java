package cbit.vcell.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Keeps {@code vcell-legacy-env-names.properties} honest against the Dockerfiles it was derived
 * from.
 *
 * The table exists because the deployment supplies configuration under short legacy names
 * ({@code dburl}, {@code serverid}) while the code asks for {@code vcell.server.dbConnectURL} —
 * a renaming that today lives, duplicated, inside eight Dockerfiles as {@code -D} flags. Moving
 * it into one file only helps if the two cannot drift: a {@code -D} mapping added to a
 * Dockerfile without a matching entry here would silently resolve to nothing once that
 * Dockerfile's flag is removed.
 *
 * So this test re-derives the mapping from the Dockerfiles and compares. <b>If it fails, the fix
 * is to update the properties file to match the Dockerfile, not to relax the test.</b>
 */
@Tag("Fast")
public class LegacyEnvironmentNamesTest {

	/** {@code -Dvcell.server.dbConnectURL="${dburl}"} — the flag whose only job is renaming. */
	private static final Pattern ENV_BACKED_FLAG = Pattern.compile(
			"-D([A-Za-z0-9_.\\-]+)\\s*=\\s*\"?\\$\\{([A-Za-z0-9_]+)(?::-[^}]*)?}\"?");

	private static List<Path> serviceDockerfiles() throws IOException {
		// Surefire runs with the module directory as the working directory.
		Path dockerBuild = Path.of("..", "docker", "build");
		Assumptions.assumeTrue(Files.isDirectory(dockerBuild),
				"docker/build not reachable from " + Path.of("").toAbsolutePath() + "; skipping");
		try (var paths = Files.list(dockerBuild)) {
			List<Path> found = paths
					.filter(p -> p.getFileName().toString().matches("Dockerfile-.*-dev"))
					.sorted()
					.toList();
			assertTrue(found.size() >= 5, "expected the service Dockerfiles, found " + found);
			return found;
		}
	}

	/** Every property a Dockerfile renames an environment variable into. */
	private static Map<String, String> renamingsInDockerfiles() throws IOException {
		Map<String, String> mappings = new TreeMap<>();
		for (Path dockerfile : serviceDockerfiles()) {
			for (String line : Files.readAllLines(dockerfile, StandardCharsets.UTF_8)) {
				Matcher m = ENV_BACKED_FLAG.matcher(line);
				while (m.find()) {
					String property = m.group(1);
					String environmentVariable = m.group(2);
					String existing = mappings.put(property, environmentVariable);
					assertTrue(existing == null || existing.equals(environmentVariable),
							property + " is fed by two different environment variables across the"
									+ " Dockerfiles (" + existing + " and " + environmentVariable
									+ "); the table can only hold one, so reconcile them first");
				}
			}
		}
		return mappings;
	}

	private static String sanitised(String propertyName) {
		StringBuilder sb = new StringBuilder(propertyName.length());
		for (char c : propertyName.toCharArray()) {
			sb.append(Character.isLetterOrDigit(c) ? c : '_');
		}
		return sb.toString();
	}

	/** True when the standard MicroProfile rules already produce this name, so no alias is needed. */
	private static boolean coveredByStandardNaming(String property, String environmentVariable) {
		return environmentVariable.equals(property)
				|| environmentVariable.equals(sanitised(property))
				|| environmentVariable.equals(sanitised(property).toUpperCase());
	}

	/**
	 * The one that matters: every renaming a Dockerfile performs must be in the table, or the
	 * value becomes unreachable the moment that Dockerfile's -D flag is deleted.
	 */
	@Test
	public void everyDockerfileRenamingIsInTheTable() throws IOException {
		Map<String, String> table = EnvironmentConfigProvider.legacyNames();
		Map<String, String> missing = new LinkedHashMap<>();
		Map<String, String> disagreeing = new LinkedHashMap<>();

		for (Map.Entry<String, String> renaming : renamingsInDockerfiles().entrySet()) {
			String property = renaming.getKey();
			String environmentVariable = renaming.getValue();
			if (coveredByStandardNaming(property, environmentVariable)) {
				continue;
			}
			String recorded = table.get(property);
			if (recorded == null) {
				missing.put(property, environmentVariable);
			} else if (!recorded.equals(environmentVariable)) {
				disagreeing.put(property, recorded + " but the Dockerfile uses " + environmentVariable);
			}
		}

		assertEquals(Map.of(), missing,
				"these properties are renamed in a Dockerfile but absent from"
						+ EnvironmentConfigProvider.LEGACY_NAMES_RESOURCE
						+ "; add them, or the value is lost when the -D flag is removed");
		assertEquals(Map.of(), disagreeing,
				"the table and the Dockerfiles disagree about which environment variable feeds these");
	}

	/**
	 * And nothing in the table that no Dockerfile justifies — a stale alias is a name the
	 * deployment is silently trusted to keep setting.
	 */
	@Test
	public void theTableHasNoEntriesTheDockerfilesDoNotJustify() throws IOException {
		Map<String, String> renamings = renamingsInDockerfiles();
		List<String> unjustified = new ArrayList<>();
		for (String property : EnvironmentConfigProvider.legacyNames().keySet()) {
			if (!renamings.containsKey(property)) {
				unjustified.add(property);
			}
		}
		assertEquals(List.of(), unjustified,
				"no Dockerfile renames these any more; drop them from the table");
	}

	/** An alias only earns its place by being one the standard rules would not have found. */
	@Test
	public void everyAliasIsOneTheStandardRulesWouldMiss() {
		List<String> redundant = new ArrayList<>();
		for (Map.Entry<String, String> entry : EnvironmentConfigProvider.legacyNames().entrySet()) {
			if (coveredByStandardNaming(entry.getKey(), entry.getValue())) {
				redundant.add(entry.getKey() + " -> " + entry.getValue());
			}
		}
		assertEquals(List.of(), redundant,
				"these are already resolved by the exact/sanitised/upper-case rules");
	}

	/** The table is meant to carry the whole -D wall; a near-empty one means it failed to load. */
	@Test
	public void theTableLoaded() {
		assertTrue(EnvironmentConfigProvider.legacyNames().size() > 50,
				"expected the full legacy mapping, got "
						+ EnvironmentConfigProvider.legacyNames().size() + " entries");
		assertEquals("dburl", EnvironmentConfigProvider.legacyNameFor("vcell.server.dbConnectURL"));
		assertEquals("serverid", EnvironmentConfigProvider.legacyNameFor("vcell.server.id"));
	}
}
