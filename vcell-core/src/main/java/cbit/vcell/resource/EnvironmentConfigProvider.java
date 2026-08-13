package cbit.vcell.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

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
 */
public class EnvironmentConfigProvider implements PropertyLoader.VCellConfigProvider {

	private static final Logger lg = LogManager.getLogger(EnvironmentConfigProvider.class);

	static final String LEGACY_NAMES_RESOURCE = "/vcell-legacy-env-names.properties";

	/** property name -> the environment variable the current deployment sets it under. */
	private static final Map<String, String> legacyNames = loadLegacyNames();

	private final Function<String, String> environment;

	public EnvironmentConfigProvider() {
		this(System::getenv);
	}

	/**
	 * A JVM cannot alter its own environment, so precedence between the four name forms could
	 * otherwise only be tested by launching a subprocess. Package-visible for that.
	 */
	EnvironmentConfigProvider(Function<String, String> environment) {
		this.environment = environment;
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
			String value = environment.apply(candidate);
			if (value != null) {
				return value;
			}
		}
		String legacyName = legacyNames.get(propertyName);
		return legacyName == null ? null : environment.apply(legacyName);
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
