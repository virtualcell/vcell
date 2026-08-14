package org.vcell.util.logging;

import cbit.vcell.client.VCellClientLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A logged error must reach whatever {@code System.out} points at when it is logged, not the stream
 * that was current when log4j configured itself.
 * <p>
 * The desktop client redirects {@code System.out}/{@code System.err} into
 * {@code <vcellHome>/logs/vcellrun_<site>.log} (ConsoleCapture, called from VCellClientMain), and it
 * does so <em>after</em> log4j has already initialized. With log4j's DefaultConfiguration - which is
 * what the client had, since it shipped no {@code log4j2.xml} - the console appender resolves
 * {@code System.out} once at configuration time and keeps that reference, so every logged message
 * went to the pre-redirect console while only bare {@code println} calls reached the file. On an
 * installed macOS {@code .app} that console goes nowhere, so client-side stack traces were invisible:
 * a browser console was the only way to see that the field viewer had returned a 500. Issue #1954.
 * <p>
 * This test fails against the DefaultConfiguration in both directions: {@code follow} would be false,
 * and the root level would be ERROR rather than WARN.
 */
@Tag("Fast")
public class ClientLoggingReachesRedirectedStreamTest {

	private static final Logger lg = LogManager.getLogger(ClientLoggingReachesRedirectedStreamTest.class);

	/**
	 * The same call VCellClientMain makes, rather than a copy of it: the ordering hazard is the
	 * subject of the test, so the test must not arrange logging in a way the client does not.
	 * <p>
	 * Note this runs AFTER the static logger above has already initialized log4j - deliberately, and
	 * exactly as it does in the client, where ConsoleCapture's own logger forces initialization
	 * before the streams are swapped.
	 */
	@BeforeAll
	public static void configureLoggingTheWayTheClientDoes() {
		VCellClientLogging.configure();
	}

	@Test
	public void logged_error_follows_a_later_System_out_redirect() {
		PrintStream original = System.out;
		ByteArrayOutputStream captured = new ByteArrayOutputStream();
		// the marker must be unique, since this stream also receives whatever else the JVM prints
		String marker = "vcell-1954-marker-" + System.identityHashCode(captured);
		try {
			// exactly what ConsoleCapture does, and - as in the client - after log4j is already
			// configured, which the static logger above guarantees
			System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
			lg.error(marker);
			System.out.flush();
		} finally {
			System.setOut(original);
		}

		String written = captured.toString(StandardCharsets.UTF_8);
		assertTrue(written.contains(marker),
				"a logged ERROR did not reach the redirected System.out, so it would not reach the "
						+ "client's log file either. Check that vcell-client's log4j2.xml is on the "
						+ "classpath and that its console appender still has follow=\"true\". Captured: '"
						+ written + "'");
	}

	@Test
	public void warnings_are_logged_too_not_only_errors() {
		PrintStream original = System.out;
		ByteArrayOutputStream captured = new ByteArrayOutputStream();
		String marker = "vcell-1954-warn-" + System.identityHashCode(captured);
		try {
			System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
			lg.warn(marker);
			System.out.flush();
		} finally {
			System.setOut(original);
		}

		assertTrue(captured.toString(StandardCharsets.UTF_8).contains(marker),
				"a logged WARN did not reach the log. log4j's default root level is ERROR; "
						+ "vcell-client's log4j2.xml sets WARN so that a warning explaining a later "
						+ "failure is present too.");
	}

	/**
	 * The behavioural tests above would also pass if some other log4j2.xml on the classpath happened
	 * to follow. Pin the configuration the client actually ships.
	 */
	@Test
	public void the_client_configuration_is_the_one_in_use() {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		Configuration configuration = context.getConfiguration();
		assertNotNull(configuration.getAppender("console"),
				"vcell-client's log4j2.xml defines an appender named 'console'; the active "
						+ "configuration has none, so something else is configuring log4j: "
						+ configuration.getAppenders().keySet());
		assertEquals(org.apache.logging.log4j.Level.WARN,
				configuration.getRootLogger().getLevel(),
				"root level should be WARN - log4j's ERROR default is what hid the warnings");
	}
}
