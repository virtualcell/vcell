package cbit.vcell.message.server.htc.slurm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import com.fasterxml.jackson.databind.ObjectMapper;

import cbit.vcell.resource.PropertyLoader;

import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy.HtcJobInfo;

/**
 * Parses captured slurm output.
 *
 * Everything VCell knows about a cluster job arrives as the text of an `sacct` or `squeue`
 * command, so the parsers are where that knowledge is either preserved or quietly lost — and
 * they cannot be exercised against a real cluster from a build. The fixtures in
 * {@code src/test/resources/slurm-outputs} are verbatim captures from mantis, produced by the
 * exact commands {@link SlurmProxy} issues.
 *
 * <b>When slurm output turns up that does not parse correctly, capture it into that directory
 * and add a case here.</b> That is what the corpus is for; see its README.
 */
@Tag("Fast")
// Mutates vcell.server.id, which SlurmProxyTest also depends on. The Fast group runs
// class-parallel in one JVM, so without this lock the @AfterAll here clears the property out
// from under that class -- observed as "required System property vcell.server.id not defined"
// in a run where this class passed on its own. Repo convention, documented in ci.yml.
@ResourceLock("vcellGlobalConfig")
public class SlurmOutputParsingTest {

	private static String previousServerId;

	/**
	 * The parsers are static and pure, but loading the class is not: HtcProxy's static
	 * initialiser calls simulationJobNamePrefix(), which requires vcell.server.id. Nothing about
	 * parsing text needs a server id -- it is just what sits between the test and the method.
	 */
	@BeforeAll
	public static void giveTheClassLoaderWhatItNeeds() {
		previousServerId = System.getProperty(PropertyLoader.vcellServerIDProperty);
		System.setProperty(PropertyLoader.vcellServerIDProperty, "TEST");
	}

	@AfterAll
	public static void restoreServerId() {
		if (previousServerId == null) {
			System.clearProperty(PropertyLoader.vcellServerIDProperty);
		} else {
			System.setProperty(PropertyLoader.vcellServerIDProperty, previousServerId);
		}
	}

	private static String fixture(String name) {
		String path = "/slurm-outputs/" + name;
		try (InputStream in = SlurmOutputParsingTest.class.getResourceAsStream(path)) {
			assertNotNull(in, "missing fixture " + path);
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("could not read " + path, e);
		}
	}

	private static Set<String> jobIds(Map<HtcJobInfo, HtcJobStatus> statusMap) {
		return statusMap.keySet().stream()
				.map(info -> info.getHtcJobID().getJobNumber() + "")
				.collect(Collectors.toSet());
	}

	// ---------------------------------------------------------------- sacct

	/**
	 * A SpringSaLaD multirun. sacct reports the allocation plus `.batch`, `.extern` and one
	 * numbered step per concurrent task — 23 lines for one job. Only the allocation is a job
	 * VCell tracks; the steps share its id with a suffix and belong to nothing in the caller's
	 * map, which is how they caused a NullPointerException downstream.
	 */
	@Test
	public void aMultistepJobYieldsOnlyTheParentAllocation() throws IOException {
		Map<HtcJobInfo, HtcJobStatus> statusMap =
				SlurmProxy.extractJobIds(fixture("sacct-multistep-running.txt"));

		assertEquals(Set.of("2710593"), jobIds(statusMap),
				"steps and .batch/.extern are not jobs; only the allocation is");
		HtcJobInfo only = statusMap.keySet().iterator().next();
		assertEquals("V_ALPHA_321860411_0_0", only.getJobName());
		assertTrue(statusMap.get(only).isRunning());
	}

	@Test
	public void anOrdinaryFinishedJobYieldsOneEntry() throws IOException {
		Map<HtcJobInfo, HtcJobStatus> statusMap =
				SlurmProxy.extractJobIds(fixture("sacct-simple-completed.txt"));

		assertEquals(Set.of("2711367"), jobIds(statusMap));
		assertTrue(statusMap.values().iterator().next().isComplete());
	}

	/** sacct prints its header even when nothing matches; that is not an error. */
	@Test
	public void noMatchingJobsYieldsAnEmptyMap() throws IOException {
		assertTrue(SlurmProxy.extractJobIds(fixture("sacct-no-matching-jobs.txt")).isEmpty());
	}

	/**
	 * Genuinely empty output — no header at all. This is what an accounting outage can look
	 * like, and it must not throw: the caller decides what to do about knowing nothing, and a
	 * NullPointerException from the parser tells it the wrong thing.
	 */
	@Test
	public void emptyOutputYieldsAnEmptyMapRatherThanThrowing() throws IOException {
		assertTrue(SlurmProxy.extractJobIds(fixture("sacct-empty-output.txt")).isEmpty());
	}

	/**
	 * Slurm writes the canceller into the state: "CANCELLED by 54321". The state field
	 * therefore contains spaces, which is why this output has to be parsed by delimiter.
	 */
	@Test
	public void aCancelledStateCarryingAUserIdStillParses() throws IOException {
		Map<HtcJobInfo, HtcJobStatus> statusMap =
				SlurmProxy.extractJobIds(fixture("sacct-cancelled-by-user.txt"));

		assertEquals(Set.of("2699001"), jobIds(statusMap));
		HtcJobStatus status = statusMap.values().iterator().next();

		// A cancelled job is NOT a failure. The user (or an administrator) stopped it on purpose,
		// and VCell has a distinct notion for that -- "User aborted simulation". So isFailed()
		// being false here is correct, and must stay correct: VCell aborts a simulation by
		// issuing scancel itself, so reporting CANCELLED as a failure would turn a deliberate
		// stop into a spurious error for the user who asked for it.
		assertFalse(status.isFailed(), "a user-cancelled job is stopped, not failed");

		// It is finished, though. Terminal is terminal; only the reason differs, and a caller
		// asking "is this still going" must be told no.
		assertTrue(status.isStopped(), "cancelled is a deliberate stop");
		assertTrue(status.isDone(), "and a stop is an ending");
	}

	/** The scheduler-side endings the job monitor exists to report. */
	@Test
	public void everyTerminalStateParses() throws IOException {
		Map<HtcJobInfo, HtcJobStatus> statusMap =
				SlurmProxy.extractJobIds(fixture("sacct-terminal-states.txt"));

		// Every one of these is a way the scheduler ended a job that did not finish, so each must
		// read as failed -- not merely as "parsed". Before this, isFailed() was true only for the
		// state literally named FAILED, so a job killed by TIMEOUT or OUT_OF_MEMORY was neither
		// failed nor complete and read as though it were still running.
		for (Map.Entry<HtcJobInfo, HtcJobStatus> entry : statusMap.entrySet()) {
			assertTrue(entry.getValue().isFailed(),
					entry.getKey().getJobName() + " should be failed: " + entry.getValue());
			assertTrue(entry.getValue().isDone(), entry.getKey().getJobName() + " should be done");
		}

		// The point of this case is that every one of these states PARSES. OUT_OF_MEMORY was
		// missing from SlurmJobStatus entirely, so parseStatus() threw IllegalArgumentException
		// on any job the scheduler killed for memory -- while HtcSimulationWorker's private list
		// handled it by name. The two disagreed, and routing the monitor through SlurmProxy
		// would have turned a reported failure into an exception.
		assertEquals(Set.of("2699101", "2699102", "2699103", "2699104"), jobIds(statusMap));
	}

	// --------------------------------------------------------------- squeue

	/** squeue lists allocations only — no step lines, unlike sacct. */
	@Test
	public void squeueYieldsTheLiveAllocations() throws IOException {
		Map<HtcJobInfo, HtcJobStatus> statusMap =
				SlurmProxy.extractJobIdsFromSqueue(fixture("squeue-running.txt"));

		assertEquals(Set.of("2714763", "2710594", "2710593"), jobIds(statusMap));
		assertTrue(statusMap.values().stream().allMatch(HtcJobStatus::isRunning));
	}

	@Test
	public void anEmptyQueueYieldsAnEmptyMap() throws IOException {
		assertTrue(SlurmProxy.extractJobIdsFromSqueue(fixture("squeue-empty.txt")).isEmpty());
	}

	// ----------------------------------------------------------------- json

	/**
	 * The reason for using --json on this path: a 20-task SpringSaLaD multirun is ONE job with its
	 * steps nested inside it, not 23 sibling rows. The step filtering the text form needs is not a
	 * problem that exists in this shape.
	 */
	@Test
	public void sacctJsonReportsOneJobForAMultistepAllocation() throws IOException {
		Map<HtcJobInfo, HtcJobStatus> statusMap =
				SlurmProxy.extractJobIdsFromSacctJson(fixture("sacct-json-multistep.json"));

		assertEquals(Set.of("2710593"), jobIds(statusMap), "steps are nested, not siblings");
		HtcJobInfo only = statusMap.keySet().iterator().next();
		assertEquals("V_ALPHA_321860411_0_0", only.getJobName());
		assertTrue(statusMap.get(only).isRunning());
	}

    /** No matching job is an answer, not an error. */
	@Test
	public void sacctJsonWithNoMatchingJobsYieldsAnEmptyMap() throws IOException {
		assertTrue(SlurmProxy.extractJobIdsFromSacctJson(fixture("sacct-json-no-matching-jobs.json")).isEmpty());
		assertTrue(SlurmProxy.extractJobIdsFromSacctJson("").isEmpty());
		assertTrue(SlurmProxy.extractJobIdsFromSacctJson(null).isEmpty());
	}

	/**
	 * scontrol puts the state somewhere else than sacct does -- job_state rather than
	 * state.current -- which is exactly the kind of difference that makes hand-written parsing
	 * per command a liability.
	 */
	@Test
	public void scontrolJsonReportsTheJobAndItsState() throws IOException {
		Map<HtcJobInfo, HtcJobStatus> statusMap =
				SlurmProxy.extractJobIdsFromScontrolJson(fixture("scontrol-json-running.json"));

		assertEquals(Set.of("2710594"), jobIds(statusMap));
		HtcJobInfo only = statusMap.keySet().iterator().next();
		assertEquals("V_ALPHA_321860413_0_0", only.getJobName(),
				"the name matters -- it is what guards against a reused slurm job id");
		assertTrue(statusMap.get(only).isRunning());
	}

	/**
	 * The captures come from one slurm, and the field paths depend on its schema. Compare the
	 * PARSED value, not the raw text: slurm escapes the slash, so the bytes read
	 * "data_parser\\/v0.0.40" while the value is "data_parser/v0.0.40".
	 */
	@Test
	public void theFixturesMatchTheDataParserVersionTheCodeExpects() throws IOException {
		for (String name : new String[] {"sacct-json-multistep.json", "scontrol-json-running.json"}) {
			String reported = new ObjectMapper().readTree(fixture(name))
					.path("meta").path("plugin").path("data_parser").asText();
			assertEquals(SlurmProxy.EXPECTED_DATA_PARSER, reported,
					name + " reports a different schema version; re-capture the fixtures and check"
							+ " that job_id / name / state paths still hold");
		}
	}
}
