package cbit.vcell.message.server.batch.sim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import cbit.vcell.solver.VCSimulationIdentifier;

import cbit.vcell.message.server.batch.sim.HtcSimulationWorker.MonitorJobInfo;

/**
 * The record of which slurm jobs are being monitored is persisted as text and read back on
 * startup, so `toString` and `fromString` have to be exact inverses — and they were not.
 *
 * The file was rewritten from the map's {@code keySet()}, which holds bare slurm job ids, so a
 * restart with live jobs wrote lines like {@code "12345"}. Reading one of those threw
 * {@code NoSuchElementException} at the second token, and because the read loop caught around
 * the whole file rather than per line, every job after it was dropped — VCell lost track of
 * running simulations.
 */
@Tag("Fast")
public class MonitorJobInfoTest {

	private static MonitorJobInfo aJob() {
		VCSimulationIdentifier simId = new VCSimulationIdentifier(
				new KeyValue("321868189"), new User("vcellNagios", new KeyValue("90825526")));
		return new MonitorJobInfo(2711367L, simId, 0, 0);
	}

	/** The property that was broken: what is written must read back as what was written. */
	@Test
	public void aJobRoundTripsThroughItsTextForm() {
		MonitorJobInfo original = aJob();
		MonitorJobInfo parsed = MonitorJobInfo.fromString(original.toString());

		assertEquals(original.toString(), parsed.toString(),
				"a round trip must be stable, or every rewrite of the file mutates the entry");
	}

	/** The username is written quoted; parsing must strip it, or the quotes compound. */
	@Test
	public void theUsernameDoesNotAccumulateQuotes() {
		String once = aJob().toString();
		String twice = MonitorJobInfo.fromString(once).toString();
		String thrice = MonitorJobInfo.fromString(twice).toString();

		assertTrue(once.contains("'vcellNagios'"), once);
		assertEquals(once, thrice, "repeated read/write cycles must not change the entry");
	}

	/** A delete marker carries only the job id, and must still parse. */
	@Test
	public void aDeleteMarkerParses() {
		MonitorJobInfo parsed = MonitorJobInfo.fromString("- 2711367 ");
		assertNotNull(parsed);
		assertEquals("- 2711367 ", parsed.toString());
	}

	/**
	 * The exact line the old rewrite produced. It must fail with something that names the line,
	 * not a bare NoSuchElementException whose getMessage() is null — that is what logged as "null".
	 */
	@Test
	public void aBareJobIdIsRejectedWithAUsefulMessage() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> MonitorJobInfo.fromString("2711367"));
		assertTrue(e.getMessage().contains("2711367"), "the message must quote the bad line: " + e.getMessage());
		assertTrue(e.getMessage().contains("slurmJobID"), "and say which field is missing: " + e.getMessage());
	}

	/** A truncated record — the shape an interrupted append leaves behind. */
	@Test
	public void aTruncatedRecordIsRejectedRatherThanHalfParsed() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> MonitorJobInfo.fromString("+ 2711367 321868189 'vcellNagios'"));
		assertTrue(e.getMessage().contains("userKey"), e.getMessage());
	}
}
