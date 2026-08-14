package cbit.vcell.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

/**
 * The point of this provider is to let a service take its configuration from the environment it
 * is already given, instead of from a wall of {@code -D} flags that exist only to rename those
 * same values — 98 of them across five services, 74 in submit alone.
 *
 * Two properties have to hold for that to be safe to adopt gradually, and both are pinned here:
 * a {@code -D} flag still wins, so a service that has not been migrated behaves exactly as
 * before; and the environment variable names match the ones vcell-rest already answers to
 * through MicroProfile Config, so a value does not need two names depending on which service
 * reads it.
 */
@Tag("Fast")
// Sets and clears system properties, which the Fast group runs class-parallel against.
@ResourceLock("vcellGlobalConfig")
public class EnvironmentConfigProviderTest {

	private final List<String> touched = new ArrayList<>();
	private final EnvironmentConfigProvider provider = new EnvironmentConfigProvider();

	@AfterEach
	public void restore() {
		for (String key : touched) {
			System.clearProperty(key);
		}
		touched.clear();
	}

	private void systemProperty(String key, String value) {
		System.setProperty(key, value);
		touched.add(key);
	}

	/** The naming rules, which must match MicroProfile's so both stacks read the same variable. */
	@Test
	public void aPropertyIsLookedUpUnderThreeEnvironmentNames() {
		assertIterableEquals(
				List.of("vcell.server.id", "vcell_server_id", "VCELL_SERVER_ID"),
				EnvironmentConfigProvider.environmentNamesFor("vcell.server.id"),
				"exact, sanitised, then upper-cased -- the order MicroProfile resolves in");
	}

	/**
	 * UPPER_SNAKE is the only form that works for every property, and the deployment should use
	 * it exclusively.
	 *
	 * The middle candidate preserves the property's case rather than lowering it, so for a
	 * property with a camelCase segment the snake form is itself mixed-case:
	 * {@code vcell_server_dbConnectURL}, not {@code vcell_server_dbconnecturl}. 25 of the 84
	 * env-backed properties contain an uppercase letter, so an all-lowercase variable silently
	 * resolves to nothing for roughly a third of them.
	 *
	 * <b>Do not "fix" this by lower-casing as a fourth candidate.</b> MicroProfile searches
	 * exactly these three, so vcell-rest would not accept a lowercased name, and the two stacks
	 * would disagree about the very thing matching MicroProfile's rules exists to guarantee.
	 */
	@Test
	public void anAllLowercaseNameDoesNotResolveACamelCaseProperty() {
		Set<String> names = EnvironmentConfigProvider.environmentNamesFor("vcell.server.dbConnectURL");

		assertTrue(names.contains("VCELL_SERVER_DBCONNECTURL"), names.toString());
		assertTrue(names.contains("vcell_server_dbConnectURL"),
				"the sanitised form preserves case; " + names);
		assertTrue(!names.contains("vcell_server_dbconnecturl"),
				"an all-lowercase name must NOT resolve -- MicroProfile does not search it, and"
						+ " vcell-rest would disagree with us if we did: " + names);
	}

	/** Every character outside [A-Za-z0-9] becomes an underscore, not just the dots. */
	@Test
	public void nonAlphanumericCharactersAllBecomeUnderscores() {
		Set<String> names = EnvironmentConfigProvider.environmentNamesFor("vcell.htc-memory.min/mb");
		assertTrue(names.contains("vcell_htc_memory_min_mb"), names.toString());
		assertTrue(names.contains("VCELL_HTC_MEMORY_MIN_MB"), names.toString());
	}

	/**
	 * An environment variable this test can assert on: present, non-empty, and already trimmed
	 * (PropertyLoader.getProperty trims what it returns, so a padded value would fail for a
	 * reason that has nothing to do with this provider).
	 */
	private static Map.Entry<String, String> anEnvironmentVariable() {
		Map.Entry<String, String> found = System.getenv().entrySet().stream()
				.filter(e -> e.getValue() != null && !e.getValue().isEmpty())
				.filter(e -> e.getValue().equals(e.getValue().trim()))
				.findFirst().orElse(null);
		Assumptions.assumeTrue(found != null, "needs at least one usable environment variable");
		return found;
	}

	/**
	 * The property that makes this adoptable one service at a time: a -D flag still wins, so a
	 * container that has not dropped its flags is unaffected by installing this.
	 */
	@Test
	public void aSystemPropertyBeatsTheEnvironment() {
		// a variable the environment actually has, so the precedence test is a real contest
		Map.Entry<String, String> env = anEnvironmentVariable();

		assertEquals(env.getValue(), provider.getConfigValue(env.getKey()),
				"with no system property set, the environment answers");

		systemProperty(env.getKey(), "from-the-command-line");
		assertEquals("from-the-command-line", provider.getConfigValue(env.getKey()),
				"a -D flag must win, or migrating one service at a time would change behaviour");
	}

	/** A property nothing supplies is absent, not empty — callers distinguish those. */
	@Test
	public void anUnsuppliedPropertyIsNull() {
		assertNull(provider.getConfigValue("vcell.no.such.property.at.all"));
		assertNull(provider.getConfigValue(null));
	}

	/**
	 * PropertyLoader must see values that only the environment supplies, which is why its
	 * startup validation resolves each declared property rather than enumerating config names:
	 * an environment variable cannot be reversed into a property name.
	 */
	@Test
	public void propertyLoaderReadsThroughToTheEnvironment() {
		Map.Entry<String, String> env = anEnvironmentVariable();

		PropertyLoader.VCellConfigProvider previous = PropertyLoader.getConfigProvider();
		try {
			PropertyLoader.setConfigProvider(provider);
			assertEquals(env.getValue(), PropertyLoader.getProperty(env.getKey(), null),
					"PropertyLoader should resolve what the provider resolves");
		} finally {
			PropertyLoader.setConfigProvider(previous);
		}
	}

	/**
	 * Reading the environment is <em>not</em> the default, and must not become one.
	 *
	 * It is right for a service running in a container VCell defines, and wrong for the desktop
	 * client, the CLI and the admin tools: those run on machines whose environment VCell does not
	 * control, and several legacy names are generic enough to collide there — a user with
	 * {@code keystore} or {@code workingDir} set would silently feed a VCell property.
	 * {@code VCellClientMain} calls {@code loadProperties} like every service does, so the only
	 * thing separating the two cases is which provider is installed.
	 */
	@Test
	public void readingTheEnvironmentIsOptedIntoNotDefaulted() {
		assertTrue(!(PropertyLoader.getConfigProvider() instanceof EnvironmentConfigProvider),
				"the desktop client and CLI must not pick up configuration from a user's"
						+ " environment; each standalone service installs this provider itself");
	}

	/**
	 * The other half of that trade: opting in must not be forgotten. These five are the mains the
	 * service Dockerfiles launch, and each one's configuration arrives as container environment.
	 * A service that stopped installing the provider would lose it silently — the properties would
	 * simply read as unset, and the failure would surface as a missing-configuration error at
	 * startup rather than as anything pointing here.
	 */
	@Test
	public void everyServiceMainInstallsTheProvider() throws java.io.IOException {
		String[] mains = {
				"../vcell-api/src/main/java/org/vcell/rest/VCellApiMain.java",
				"../vcell-server/src/main/java/cbit/vcell/message/server/data/SimDataServerMain.java",
				"../vcell-server/src/main/java/cbit/vcell/message/server/db/DatabaseServer.java",
				"../vcell-server/src/main/java/cbit/vcell/message/server/dispatcher/SimulationDispatcherMain.java",
				"../vcell-server/src/main/java/cbit/vcell/message/server/batch/sim/HtcSimulationWorker.java",
		};
		List<String> notInstalling = new ArrayList<>();
		for (String main : mains) {
			java.nio.file.Path path = java.nio.file.Path.of(main);
			Assumptions.assumeTrue(java.nio.file.Files.isRegularFile(path),
					main + " not reachable from " + java.nio.file.Path.of("").toAbsolutePath());
			String source = java.nio.file.Files.readString(path);
			if (!source.contains("setConfigProvider(new EnvironmentConfigProvider())")) {
				notInstalling.add(main);
			}
		}
		assertEquals(List.of(), notInstalling,
				"these services read their configuration from the container environment, so each"
						+ " must install EnvironmentConfigProvider before loadProperties");
	}

	// ------------------------------------- unrecognised VCELL_* variables

	/**
	 * A misspelling used to be perfectly silent — {@code VCELL_SERVER_DBCONNECTUR} resolves
	 * nothing, the property takes its default, and the service runs on configuration nobody
	 * intended. Once configuration comes from the environment, the environment is the interface,
	 * and an interface that accepts typos without complaint is how a deployment drifts from what
	 * its operator believes it says.
	 */
	@Test
	public void aMisspeltVariableIsReportedAndSuggestsWhatWasMeant() {
		systemProperty(EnvironmentConfigProvider.STRICT_ENVIRONMENT_NAMES, "true");
		EnvironmentConfigProvider p = readingEnvironment(Map.of("VCELL_SERVER_DBCONNECTUR", "oops"));

		IllegalStateException e = assertThrows(IllegalStateException.class,
				() -> p.reportUnrecognisedEnvironmentNames(List.of("vcell.server.dbConnectURL")));

		assertTrue(e.getMessage().contains("VCELL_SERVER_DBCONNECTUR"), e.getMessage());
		assertTrue(e.getMessage().contains("vcell.server.dbConnectURL"),
				"should name the property it was probably meant to be: " + e.getMessage());
		assertTrue(e.getMessage().contains("VCELL_SERVER_DBCONNECTURL"),
				"and spell out the variable to use: " + e.getMessage());
	}

	/** The correctly spelled one is silent, or the report would be worthless. */
	@Test
	public void acorrectlyNamedVariableIsNotReported() {
		systemProperty(EnvironmentConfigProvider.STRICT_ENVIRONMENT_NAMES, "true");
		EnvironmentConfigProvider p = readingEnvironment(Map.of("VCELL_SERVER_DBCONNECTURL", "fine"));

		p.reportUnrecognisedEnvironmentNames(List.of("vcell.server.dbConnectURL"));
	}

	/**
	 * {@code VCELL_DEBUG_OPTS} is set by every service Dockerfile and is not a property. Treating
	 * it as a misspelling would, in strict mode, refuse to start every service — the check has to
	 * be worth trusting before anyone turns strict mode on.
	 */
	@Test
	public void aKnownNonPropertyVariableIsNotReported() {
		systemProperty(EnvironmentConfigProvider.STRICT_ENVIRONMENT_NAMES, "true");
		EnvironmentConfigProvider p = readingEnvironment(Map.of(
				"VCELL_DEBUG_OPTS", "", "VCELL_SITE", "alpha", "VCELL_VERSION", "8.0.14"));

		p.reportUnrecognisedEnvironmentNames(List.of("vcell.server.id"));
	}

	/** Not everything unknown is a typo, and a non-VCELL variable is none of our business. */
	@Test
	public void variablesBelongingToSomethingElseAreIgnored() {
		systemProperty(EnvironmentConfigProvider.STRICT_ENVIRONMENT_NAMES, "true");
		EnvironmentConfigProvider p = readingEnvironment(Map.of(
				"PATH", "/usr/bin", "JAVA_HOME", "/opt/java", "dburl", "jdbc:x"));

		p.reportUnrecognisedEnvironmentNames(List.of("vcell.server.id"));
	}

	/**
	 * Off by default. An unrecognised variable is usually harmless, and refusing to start a
	 * production service over one would be a worse failure than the one being prevented — so the
	 * default is a log, and strict mode is something an operator opts into.
	 */
	@Test
	public void byDefaultAnUnrecognisedVariableIsLoggedRatherThanFatal() {
		EnvironmentConfigProvider p = readingEnvironment(Map.of("VCELL_UTTERLY_UNKNOWN_THING", "x"));

		p.reportUnrecognisedEnvironmentNames(List.of("vcell.server.id"));
	}

	/**
	 * The exemption list is a hand-written claim that certain names are deliberately not
	 * configuration. Nothing stops a property being declared later that lands on one of them —
	 * {@code vcell.site} would become {@code VCELL_SITE}, which the deploy tooling already sets
	 * for something else entirely. At that point a build variable silently becomes configuration,
	 * which is exactly the failure this whole check exists to prevent, and the exemption would be
	 * the reason nobody noticed.
	 *
	 * This is the build-time half; the provider makes the same check at startup, against whatever
	 * PropertyLoader declares at runtime.
	 */
	@Test
	public void noExemptedNameIsAlsoADeclaredProperty() {
		List<String> collisions = new ArrayList<>();
		for (String property : PropertyLoader.declaredPropertyNames()) {
			String upper = EnvironmentConfigProvider.upperCaseNameFor(property);
			if (EnvironmentConfigProvider.NOT_PROPERTIES.contains(upper)) {
				collisions.add(upper + " exempted, but is the environment name of " + property);
			}
		}
		assertEquals(List.of(), collisions,
				"drop the exemption if the property should be configurable from the environment,"
						+ " or rename the property if the variable belongs to the deploy tooling");
	}

	/**
	 * Each exemption is annotated with where the variable is set. Those annotations are claims
	 * about the rest of the repository, and a claim nothing checks is a comment that quietly stops
	 * being true — an exemption for a variable nobody sets any more is dead weight that can only
	 * silence a future misspelling.
	 */
	@Test
	public void everyExemptedVariableIsStillSetSomewhere() throws java.io.IOException {
		java.nio.file.Path repo = java.nio.file.Path.of("..");
		Assumptions.assumeTrue(java.nio.file.Files.isDirectory(repo.resolve("docker")),
				"repository not reachable from " + repo.toAbsolutePath());

		StringBuilder haystack = new StringBuilder();
		for (String dir : new String[] {".github/workflows", "docker"}) {
			java.nio.file.Path root = repo.resolve(dir);
			if (!java.nio.file.Files.isDirectory(root)) {
				continue;
			}
			try (var paths = java.nio.file.Files.walk(root)) {
				for (java.nio.file.Path p : paths.filter(java.nio.file.Files::isRegularFile).toList()) {
					try {
						haystack.append(java.nio.file.Files.readString(p)).append('\n');
					} catch (java.io.IOException | RuntimeException ignored) {
						// binary or unreadable; nothing here sets environment variables
					}
				}
			}
		}
		List<String> unused = new ArrayList<>();
		for (String exempted : EnvironmentConfigProvider.NOT_PROPERTIES) {
			if (!haystack.toString().contains(exempted)) {
				unused.add(exempted);
			}
		}
		java.util.Collections.sort(unused);
		assertEquals(List.of(), unused,
				"nothing under .github/workflows or docker/ sets these any more; drop them from"
						+ " NOT_PROPERTIES so a misspelling of the same shape is reported again");
	}

	/** And the runtime half fires when the drift is real. */
	@Test
	public void driftBetweenTheExemptionsAndTheDeclaredPropertiesIsReported() {
		systemProperty(EnvironmentConfigProvider.STRICT_ENVIRONMENT_NAMES, "true");
		EnvironmentConfigProvider p = readingEnvironment(Map.of());

		// "vcell.site" would be VCELL_SITE, which is exempted as a deploy variable
		IllegalStateException e = assertThrows(IllegalStateException.class,
				() -> p.reportUnrecognisedEnvironmentNames(List.of("vcell.site")));

		assertTrue(e.getMessage().contains("VCELL_SITE"), e.getMessage());
		assertTrue(e.getMessage().contains("vcell.site"), e.getMessage());
		assertTrue(e.getMessage().contains("NOT_PROPERTIES"),
				"should name the list that needs editing: " + e.getMessage());
	}

	// ------------------------------------------- no legacy fallback

	/** A provider reading a fixed environment, so resolution can be tested deterministically. */
	private static EnvironmentConfigProvider readingEnvironment(Map<String, String> environment) {
		return new EnvironmentConfigProvider(environment);
	}

	/**
	 * The historical short names are no longer resolved, and that is the point of having removed
	 * them: one name per setting, the same one vcell-rest answers to.
	 *
	 * The deployment supplied {@code dburl} for years and this class carried a table of 80 such
	 * names consulted after the standard forms. Every one now has an UPPER_SNAKE twin in the
	 * ConfigMaps, the deployment manifests and the images -- verified resolving in running
	 * containers before the table was deleted.
	 */
	@Test
	public void aLegacyDeploymentNameNoLongerResolves() {
		EnvironmentConfigProvider p = readingEnvironment(Map.of("dburl", "jdbc:oracle:thin:@//h:1521/db"));

		assertNull(p.getConfigValue("vcell.server.dbConnectURL"),
				"dburl is history; only the MicroProfile forms resolve now");
	}

	/** And the modern name does. */
	@Test
	public void theModernNameResolves() {
		EnvironmentConfigProvider p = readingEnvironment(
				Map.of("VCELL_SERVER_DBCONNECTURL", "jdbc:oracle:thin:@//h:1521/db"));

		assertEquals("jdbc:oracle:thin:@//h:1521/db", p.getConfigValue("vcell.server.dbConnectURL"));
	}

	/** A -D flag still wins over the environment, which is unchanged by any of this. */
	@Test
	public void aSystemPropertyStillBeatsTheEnvironment() {
		EnvironmentConfigProvider p = readingEnvironment(
				Map.of("VCELL_SERVER_DBCONNECTURL", "from-the-environment"));
		systemProperty("vcell.server.dbConnectURL", "from-the-command-line");

		assertEquals("from-the-command-line", p.getConfigValue("vcell.server.dbConnectURL"));
	}

	/** A property nothing supplies is absent. */
	@Test
	public void anUnsuppliedPropertyStaysNull() {
		EnvironmentConfigProvider p = readingEnvironment(Map.of("VCELL_SERVER_ID", "TEST"));

		assertNull(p.getConfigValue("vcell.server.dbUserid"));
	}
}
