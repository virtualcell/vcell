package cbit.vcell.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Resolves VCell configuration from system properties first, then the environment.
 *
 * <h2>Why</h2>
 *
 * Every standalone service container hands its configuration to the JVM as a wall of
 * {@code -D} flags — 98 distinct ones across api, data, db, sched and submit, of which only 14
 * are common; submit alone carries 74. Of those, 84 are the same shape: a flag whose only job is
 * to rename a container environment variable into a system property.
 *
 * <pre>
 *   -Dvcell.server.dbConnectURL="${dburl}"
 *   -Dvcell.slurm.cmd.sbatch="${slurm_cmd_sbatch}"
 * </pre>
 *
 * The value is already in the environment; the flag exists only to rename it. That mapping is
 * what makes each service's Dockerfile look bespoke when the images barely differ, and it is
 * what stands in the way of running several services from one image.
 *
 * <h2>Naming follows MicroProfile, deliberately</h2>
 *
 * vcell-rest already resolves configuration through MicroProfile Config (see
 * {@code CDIVCellConfigProvider}), which reads environment variables using a defined mapping.
 * This provider implements the same rules so both stacks answer to the same variable names —
 * otherwise the same value would need two names depending on which service read it.
 *
 * For a property {@code vcell.server.id} the environment is searched for, in order:
 * <ol>
 * <li>{@code vcell.server.id} — exact</li>
 * <li>{@code vcell_server_id} — each character outside [A-Za-z0-9] replaced by underscore</li>
 * <li>{@code VCELL_SERVER_ID} — the same, upper-cased</li>
 * </ol>
 *
 * <h2>Legacy names</h2>
 *
 * The deployment does not use those names today — it sets {@code dburl}, {@code serverid},
 * {@code jmshost_int_internal}, and the Dockerfile renames each one. Requiring the deployment to
 * be renamed first would make this a flag day across a separate repository, so the 81 legacy
 * names are listed in {@code vcell-legacy-env-names.properties} and consulted <em>last</em>.
 * A variable can then be migrated singly by adding its modern name, which wins; when nothing
 * sets the legacy names any more, that file goes away.
 *
 * <h2>System properties still win</h2>
 *
 * A {@code -D} flag takes precedence over the environment, so installing this changes nothing
 * for a service that still passes them. That is the point: the flags can be removed one service
 * at a time, and a half-migrated deployment behaves the same as a fully migrated one.
 *
 * <h2>Services only — this is not a default</h2>
 *
 * Each standalone service installs this in its own {@code main}, and {@code PropertyLoader}
 * keeps its system-properties-only provider. Reading the environment is right for a service in a
 * container VCell defines, and wrong for the desktop client, the CLI and the admin tools: those
 * run on machines whose environment VCell does not control, and legacy names like
 * {@code keystore} and {@code workingDir} are generic enough to collide there. {@code
 * VCellClientMain} calls {@code loadProperties} exactly as a service does — the installed
 * provider is the only thing distinguishing the two.
 */
public class EnvironmentConfigProvider implements PropertyLoader.VCellConfigProvider {

	private static final Logger lg = LogManager.getLogger(EnvironmentConfigProvider.class);

	static final String LEGACY_NAMES_RESOURCE = "/vcell-legacy-env-names.properties";

	/** property name -> the environment variable the current deployment sets it under. */
	private static final Map<String, String> legacyNames = loadLegacyNames();

	private final Map<String, String> environment;

	public EnvironmentConfigProvider() {
		this(System.getenv());
	}

	/**
	 * A JVM cannot alter its own environment, so precedence between the name forms could
	 * otherwise only be tested by launching a subprocess. Package-visible for that.
	 */
	EnvironmentConfigProvider(Map<String, String> environment) {
		this.environment = environment;
	}

	/** Every name the environment defines. */
	private Set<String> environmentNames() {
		return environment.keySet();
	}

	@Override
	public String getConfigValue(String propertyName) {
		if (propertyName == null) {
			return null;
		}
		String fromSystemProperty = System.getProperty(propertyName);
		if (fromSystemProperty != null) {
			return fromSystemProperty;
		}
		for (String candidate : environmentNamesFor(propertyName)) {
			String value = environment.get(candidate);
			if (value != null) {
				return value;
			}
		}
		String legacyName = legacyNames.get(propertyName);
		return legacyName == null ? null : environment.get(legacyName);
	}

	/**
	 * Set {@code -Dvcell.config.strictEnvironmentNames=true} to turn the report below into a
	 * startup failure. Off by default: an unrecognised name is usually harmless, and refusing to
	 * start a production service over one would be a worse failure than the one being prevented.
	 */
	public static final String STRICT_ENVIRONMENT_NAMES = "vcell.config.strictEnvironmentNames";

	/**
	 * {@code VCELL_*} variables that are deliberately not configuration properties, so the report
	 * does not cry wolf. Most are build, deploy or CI variables that never reach a running
	 * service; {@code VCELL_DEBUG_OPTS} is the exception — every service Dockerfile sets it, and
	 * treating it as a misspelling would condemn every pod.
	 */
	private static final Set<String> NOT_PROPERTIES = Set.of(
			"VCELL_DEBUG_OPTS", "VCELL_CONFIG_FILE_NAME", "VCELL_MANAGER_NODE",
			"VCELL_SITE", "VCELL_SITE_CAMEL", "VCELL_VERSION", "VCELL_BUILD", "VCELL_TAG",
			"VCELL_SHA", "VCELL_SWVERSION", "VCELL_REPO_NAMESPACE",
			"VCELL_DEPLOY_REMOTE_DIR", "VCELL_INSTALLER_REMOTE_DIR", "VCELL_WEBHELP_REMOTE_DIR");

	/**
	 * Reports {@code VCELL_*} environment variables that do not correspond to any declared
	 * property.
	 *
	 * Without this a misspelling is perfectly silent: {@code VCELL_SERVER_DBCONNECTUR} resolves
	 * nothing, the property quietly takes its default, and the service runs on configuration
	 * nobody intended. The whole point of moving configuration into the environment is that the
	 * environment becomes the interface, and an interface that accepts typos without complaint is
	 * how a deployment drifts from what its operator believes it says.
	 *
	 * Reported at ERROR when the name is close to a real property — that is a misspelling and
	 * somebody meant something by it — and at WARN otherwise, since an unknown variable may
	 * simply belong to something else. {@link #STRICT_ENVIRONMENT_NAMES} escalates either to a
	 * startup failure.
	 *
	 * @param declaredProperties every property name known to {@code PropertyLoader}
	 */
	void reportUnrecognisedEnvironmentNames(Collection<String> declaredProperties) {
		Set<String> recognised = new HashSet<>();
		for (String property : declaredProperties) {
			recognised.addAll(environmentNamesFor(property));
		}
		recognised.addAll(legacyNames.values());

		Map<String, String> suspicious = new TreeMap<>();
		for (String name : environmentNames()) {
			if (!name.startsWith("VCELL") || recognised.contains(name) || NOT_PROPERTIES.contains(name)) {
				continue;
			}
			suspicious.put(name, closestProperty(name, declaredProperties));
		}
		if (suspicious.isEmpty()) {
			return;
		}

		StringBuilder report = new StringBuilder("unrecognised VCELL_* environment variable(s);"
				+ " these match no property known to PropertyLoader and are being ignored:");
		boolean anyLooksLikeATypo = false;
		for (Map.Entry<String, String> entry : suspicious.entrySet()) {
			report.append("\n    ").append(entry.getKey());
			if (entry.getValue() != null) {
				anyLooksLikeATypo = true;
				report.append("  -- did you mean ").append(entry.getValue())
						.append(" (").append(upperCaseNameFor(entry.getValue())).append(")?");
			}
		}
		if (Boolean.parseBoolean(getConfigValue(STRICT_ENVIRONMENT_NAMES))) {
			throw new IllegalStateException(report.toString());
		}
		if (anyLooksLikeATypo) {
			lg.error(report.toString());
		} else {
			lg.warn(report.toString());
		}
	}

	/** The UPPER_SNAKE form, which is the one a deployment should be using. */
	static String upperCaseNameFor(String propertyName) {
		StringBuilder sb = new StringBuilder(propertyName.length());
		for (char c : propertyName.toCharArray()) {
			sb.append(Character.isLetterOrDigit(c) ? c : '_');
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * The declared property whose UPPER_SNAKE name is nearest to this variable, or null when
	 * nothing is close enough to be worth suggesting. A typo is usually one or two characters,
	 * so the threshold scales with length rather than being a flat number.
	 */
	private static String closestProperty(String environmentName, Collection<String> declaredProperties) {
		String best = null;
		int bestDistance = Integer.MAX_VALUE;
		for (String property : declaredProperties) {
			int distance = editDistance(environmentName, upperCaseNameFor(property));
			if (distance < bestDistance) {
				bestDistance = distance;
				best = property;
			}
		}
		int threshold = Math.max(1, Math.min(3, environmentName.length() / 6));
		return bestDistance <= threshold ? best : null;
	}

	private static int editDistance(String a, String b) {
		int[] previous = new int[b.length() + 1];
		int[] current = new int[b.length() + 1];
		for (int j = 0; j <= b.length(); j++) {
			previous[j] = j;
		}
		for (int i = 1; i <= a.length(); i++) {
			current[0] = i;
			for (int j = 1; j <= b.length(); j++) {
				int substitute = previous[j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1);
				current[j] = Math.min(substitute, Math.min(previous[j] + 1, current[j - 1] + 1));
			}
			int[] swap = previous;
			previous = current;
			current = swap;
		}
		return previous[b.length()];
	}

	/** The environment variable the current deployment supplies this property under, if any. */
	static String legacyNameFor(String propertyName) {
		return legacyNames.get(propertyName);
	}

	static Map<String, String> legacyNames() {
		return legacyNames;
	}

	private static Map<String, String> loadLegacyNames() {
		Properties properties = new Properties();
		try (InputStream in = EnvironmentConfigProvider.class.getResourceAsStream(LEGACY_NAMES_RESOURCE)) {
			if (in == null) {
				// Not fatal: without the table only the modern names resolve, which is exactly the
				// state this is migrating towards. Failing startup here would be worse than the
				// missing-property report the caller is about to produce anyway.
				lg.warn("no " + LEGACY_NAMES_RESOURCE + " on the classpath;"
						+ " configuration supplied under legacy environment names will not be found");
				return Map.of();
			}
			properties.load(in);
		} catch (IOException e) {
			lg.warn("could not read " + LEGACY_NAMES_RESOURCE, e);
			return Map.of();
		}
		Map<String, String> map = new HashMap<>();
		for (String name : properties.stringPropertyNames()) {
			map.put(name, properties.getProperty(name).trim());
		}
		return Collections.unmodifiableMap(map);
	}

	/**
	 * The environment variable names a property may be supplied under, most specific first.
	 * Package-visible so the naming rules can be tested without reaching into the environment.
	 */
	static Set<String> environmentNamesFor(String propertyName) {
		Set<String> names = new LinkedHashSet<>();
		names.add(propertyName);
		StringBuilder sanitised = new StringBuilder(propertyName.length());
		for (char c : propertyName.toCharArray()) {
			sanitised.append(Character.isLetterOrDigit(c) ? c : '_');
		}
		names.add(sanitised.toString());
		names.add(sanitised.toString().toUpperCase());
		return names;
	}

	/**
	 * Names that carry a value.
	 *
	 * Environment variables cannot be reversed into property names — {@code VCELL_SERVER_ID}
	 * could be {@code vcell.server.id} or {@code vcell.server_id} — so only the system-property
	 * names are enumerable here. Callers that need to know whether a specific property is set
	 * should ask {@link #getConfigValue}, which resolves both sources; {@code PropertyLoader}
	 * does exactly that.
	 */
	@Override
	public Set<String> getConfigNames() {
		return System.getProperties().stringPropertyNames();
	}

	@Override
	public void setConfigValue(String propertyName, String value) {
		System.setProperty(propertyName, value);
	}
}
