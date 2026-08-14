package cbit.vcell.client;

import java.net.URISyntaxException;
import java.net.URL;

import org.apache.logging.log4j.core.config.Configurator;

/**
 * Points log4j at the desktop client's own configuration, explicitly.
 * <p>
 * Two problems made every client-side stack trace invisible, and this addresses the one that
 * cannot be fixed from a configuration file (issue #1954).
 * <p>
 * <b>Classpath order.</b> {@code jsbml-core} and {@code biojava-ontology} each ship a
 * {@code log4j2.xml}, and log4j uses the first one the classloader returns. {@code vcell.sh} places
 * {@code maven-jars/*} ahead of {@code target/*}, so a file named {@code log4j2.xml} in this module
 * would lose to jsbml's - which configures a console appender that does not follow, and a file
 * appender writing {@code jsbml.log} into the launch directory. Loading our configuration by URL
 * removes the ambiguity: whatever the classpath order, this is the configuration in force.
 * <p>
 * <b>Ordering against the stream redirect.</b> The client redirects {@code System.out}/{@code
 * System.err} into {@code <vcellHome>/logs/vcellrun_<site>.log} after log4j is already initialized,
 * so an appender that resolved {@code System.out} at configuration time keeps writing to the
 * pre-redirect console - nowhere reachable from an installed macOS {@code .app}.
 * {@code log4j2-vcell-client.xml} sets {@code follow="true"} so the appender resolves the stream per
 * write instead, which makes this call order-independent: it is correct before or after the
 * redirect.
 * <p>
 * {@link Configurator#reconfigure(java.net.URI)} rather than the {@code log4j2.configurationFile}
 * system property, because setting a property only works if log4j has not configured itself yet.
 * Reconfiguring is correct either way, which is what lets a test call this and get the real thing.
 */
public final class VCellClientLogging {

	/** Deliberately not "log4j2.xml" - see the class comment. */
	static final String CONFIG_RESOURCE = "log4j2-vcell-client.xml";

	private VCellClientLogging() {
	}

	/**
	 * Applies the client's logging configuration. Safe to call more than once.
	 * <p>
	 * Complains to {@code System.err} and carries on if the configuration cannot be applied, rather
	 * than throwing. Only a packaging fault can get here - the file lives in this module's own jar -
	 * and refusing to start a client because its logging is misconfigured would be a worse outcome
	 * for the person trying to use it than logging to the wrong place. The regression test is what
	 * keeps that fault from reaching a build: it asserts this configuration is the one in force, so
	 * losing the resource fails CI rather than quietly restoring the old behaviour.
	 */
	public static void configure() {
		URL url = VCellClientLogging.class.getClassLoader().getResource(CONFIG_RESOURCE);
		if (url == null) {
			// cannot log this: the logging is what is broken
			System.err.println("WARNING: " + CONFIG_RESOURCE + " is not on the classpath, so this "
					+ "client will log through whichever configuration a dependency happens to ship, "
					+ "and its errors may not reach the log file. This is a packaging fault.");
			return;
		}
		try {
			Configurator.reconfigure(url.toURI());
		} catch (URISyntaxException e) {
			System.err.println("WARNING: cannot read logging configuration from " + url + ": " + e);
		}
	}
}
