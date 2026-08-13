package cbit.vcell.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.Property;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

/**
 * Pins what {@code "PROPERTY: x not marked required"} actually means, because for a long time it
 * was simply noise nobody could act on — on the dev site it was over 90% of all ERROR volume, and
 * it drowned the errors that mattered.
 *
 * It is a real signal: {@code loadProperties(required[])} marks exactly the listed properties as
 * required, and {@code getRequiredProperty} complains when it is asked for one that is not on that
 * list. So the message says "this service demands a property it never declared" — the fix is to
 * declare it, not to lower the level.
 */
@Tag("Fast")
// PropertyLoader keeps its property map and checkRequired flag in static state, and these tests
// set system properties; the Fast group runs class-parallel in one JVM.
@ResourceLock("vcellGlobalConfig")
public class RequiredPropertyDeclarationTest {

	private ListAppender captured;
	private final List<String> setProperties = new ArrayList<>();

	@AfterEach
	public void cleanUp() {
		if (captured != null) {
			captured.detach();
			captured = null;
		}
		for (String key : setProperties) {
			System.clearProperty(key);
		}
		setProperties.clear();
	}

	private void give(String key, String value) {
		System.setProperty(key, value);
		setProperties.add(key);
	}

	private List<String> complaintsWhileFetching(String propertyName, String... declaredAsRequired) throws java.io.IOException {
		captured = ListAppender.attachTo(PropertyLoader.class);
		PropertyLoader.loadProperties(declaredAsRequired);
		PropertyLoader.getRequiredProperty(propertyName);
		// Scoped to this property: "not marked required" alone would also match a complaint
		// about some other property that a parallel test happened to fetch.
		return captured.messagesContaining("not marked required").stream()
				.filter(m -> m.contains(propertyName))
				.collect(java.util.stream.Collectors.toList());
	}

	/** Declared: no complaint. This is what adding the missing entries achieves. */
	@Test
	public void aDeclaredRequiredPropertyIsFetchedSilently() throws java.io.IOException {
		give(PropertyLoader.htcHosts, "some-cluster.example");

		List<String> complaints = complaintsWhileFetching(PropertyLoader.htcHosts, PropertyLoader.htcHosts);

		assertTrue(complaints.isEmpty(), "a declared property should be fetchable without complaint, got: " + complaints);
	}

	/**
	 * Undeclared: it complains, and it is right to. This is the case the noisy log was reporting
	 * all along — the service was demanding a property its own required list never mentioned.
	 */
	@Test
	public void fetchingAnUndeclaredPropertyIsReported() throws java.io.IOException {
		give(PropertyLoader.htcHosts, "some-cluster.example");

		List<String> complaints = complaintsWhileFetching(PropertyLoader.htcHosts /* declaring nothing */);

		assertEquals(1, complaints.size(), "an undeclared property should be reported exactly once per fetch");
		assertTrue(complaints.get(0).contains(PropertyLoader.htcHosts), complaints.get(0));
	}

	/** Captures events from one logger so a test can assert on them. */
	private static final class ListAppender extends AbstractAppender {
		private final List<LogEvent> events = Collections.synchronizedList(new ArrayList<>());
		private final Logger logger;
		private final Level previousLevel;

		/**
		 * Only events from the thread that attached the appender are kept.
		 *
		 * The Fast group runs class-parallel in one JVM, and this captures a logger shared by
		 * every test in it. Without the filter, any other class fetching an undeclared property
		 * at the same moment lands in this capture, and an assertion about "exactly once" fails
		 * for a reason that has nothing to do with the code under test. Observed in CI, where the
		 * shard mix differs from a local run: expected 1 complaint, got 2.
		 */
		private final String owningThread;

		private ListAppender(Logger logger) {
			super("required-property-test-capture", null, null, true, Property.EMPTY_ARRAY);
			this.logger = logger;
			this.previousLevel = logger.getLevel();
			this.owningThread = Thread.currentThread().getName();
		}

		static ListAppender attachTo(Class<?> cls) {
			LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
			Logger logger = ctx.getLogger(cls.getName());
			ListAppender appender = new ListAppender(logger);
			appender.start();
			logger.addAppender(appender);
			// Configurator rather than logger.setLevel(): the latter moves only this Logger
			// instance, and the class under test holds a different instance of the same name.
			Configurator.setLevel(cls.getName(), Level.DEBUG);
			return appender;
		}

		void detach() {
			logger.removeAppender(this);
			Configurator.setLevel(logger.getName(), previousLevel);
			stop();
		}

		/** Messages mentioning this fragment; callers pass the property they just fetched. */
		List<String> messagesContaining(String fragment) {
			List<String> out = new ArrayList<>();
			synchronized (events) {
				for (LogEvent event : events) {
					String message = event.getMessage().getFormattedMessage();
					if (message.contains(fragment)) {
						out.add(message);
					}
				}
			}
			return out;
		}

		@Override
		public void append(LogEvent event) {
			if (!owningThread.equals(event.getThreadName())) {
				return;
			}
			events.add(event.toImmutable());
		}
	}

	/**
	 * A file-valued property is checked at startup, which GEN never was.
	 *
	 * This exists because of a concrete near-miss: the deployment overrides the image's default
	 * secret paths under the legacy environment names only, so while migrating to VCELL_* names
	 * the modern form of vcell.db.pswdfile would have resolved to the image default
	 * (/run/secrets/dbpswd) rather than the deployment's (/run/secrets/api-secrets/dbpswd). 13
	 * such divergences existed, all of them secret paths. Declared GEN, every one resolves
	 * "successfully" to a file that is not there, and the failure surfaces much later as an
	 * unreadable password.
	 */
	@Test
	public void aFileValuedPropertyPointingAtNothingFailsValidation() throws java.io.IOException {
		give(PropertyLoader.dbPasswordFile, "/no/such/secret/dbpswd");

		IllegalStateException e = assertThrows(IllegalStateException.class,
				() -> PropertyLoader.loadProperties(new String[] { PropertyLoader.dbPasswordFile }));

		assertTrue(e.getMessage().contains(PropertyLoader.dbPasswordFile), e.getMessage());
		assertTrue(e.getMessage().contains("is not an existing file"), e.getMessage());
	}

	/** And one that does exist passes, so the check is about the file rather than the name. */
	@Test
	public void aFileValuedPropertyPointingAtARealFilePasses() throws java.io.IOException {
		java.io.File real = java.io.File.createTempFile("vcell-pswd", ".tmp");
		real.deleteOnExit();
		give(PropertyLoader.dbPasswordFile, real.getAbsolutePath());

		PropertyLoader.loadProperties(new String[] { PropertyLoader.dbPasswordFile });
	}

	/**
	 * Proves the isolation the fix relies on: a complaint raised by another thread, as happens
	 * when the Fast group runs class-parallel in one JVM, must not land in this test's capture.
	 * That leakage is what made fetchingAnUndeclaredPropertyIsReported fail in CI with 2
	 * complaints where it expects 1, while passing every local run.
	 */
	@Test
	public void aComplaintFromAnotherThreadIsNotCaptured() throws Exception {
		give(PropertyLoader.htcHosts, "some-cluster.example");
		captured = ListAppender.attachTo(PropertyLoader.class);
		PropertyLoader.loadProperties(new String[0]);

		Thread other = new Thread(() -> PropertyLoader.getRequiredProperty(PropertyLoader.htcHosts),
				"a-parallel-test-class");
		other.start();
		other.join();

		assertTrue(captured.messagesContaining("not marked required").isEmpty(),
				"another thread's complaint leaked into this test's capture: "
						+ captured.messagesContaining("not marked required"));
	}
}
