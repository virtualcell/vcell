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

	// ------------------------------------------------- legacy deployment names

	/** A provider reading a fixed environment, so precedence can be tested deterministically. */
	private static EnvironmentConfigProvider readingEnvironment(Map<String, String> environment) {
		return new EnvironmentConfigProvider(environment);
	}

	/**
	 * The whole point of the legacy table. The deployment sets {@code dburl}; nothing sets
	 * {@code VCELL_SERVER_DBCONNECTURL}. Without this the -D flag could not be deleted from the
	 * Dockerfile without a coordinated rename in the vcell-fluxcd repository first.
	 */
	@Test
	public void aLegacyDeploymentNameResolves() {
		EnvironmentConfigProvider p = readingEnvironment(Map.of("dburl", "jdbc:oracle:thin:@//h:1521/db"));

		assertEquals("jdbc:oracle:thin:@//h:1521/db", p.getConfigValue("vcell.server.dbConnectURL"),
				"the deployment supplies this as dburl and always has");
	}

	/**
	 * The modern name wins, which is what makes the migration incremental: vcell-fluxcd can
	 * rename one variable at a time and the new name takes effect immediately, with the legacy
	 * one still in place as a fallback until it is removed.
	 */
	@Test
	public void theModernNameBeatsTheLegacyOne() {
		EnvironmentConfigProvider p = readingEnvironment(Map.of(
				"dburl", "the-old-name",
				"VCELL_SERVER_DBCONNECTURL", "the-new-name"));

		assertEquals("the-new-name", p.getConfigValue("vcell.server.dbConnectURL"),
				"otherwise a renamed variable would be ignored until the old one was deleted");
	}

	/** And a -D flag still beats both, so an unmigrated container is untouched. */
	@Test
	public void aSystemPropertyBeatsTheLegacyNameToo() {
		EnvironmentConfigProvider p = readingEnvironment(Map.of("dburl", "from-the-environment"));
		systemProperty("vcell.server.dbConnectURL", "from-the-command-line");

		assertEquals("from-the-command-line", p.getConfigValue("vcell.server.dbConnectURL"));
	}

	/** A property with no legacy alias and nothing in the environment is simply absent. */
	@Test
	public void aPropertyWithNoAliasAndNoValueStaysNull() {
		EnvironmentConfigProvider p = readingEnvironment(Map.of("dburl", "irrelevant"));

		assertNull(p.getConfigValue("vcell.server.dbUserid"),
				"dbuser is not set in this environment, so the property is absent");
	}
}
