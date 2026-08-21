package cbit.vcell.simdata;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Naming trajectory sites from the solver's {@code SiteIDs.csv}.
 * <p>
 * The fixtures are trimmed from a real {@code LangevinNoVis01} run (its {@code all_reactions} test
 * case), chosen because it is the awkward case: molecules MT0 and MT1 have sites of identical color
 * and radius, and MT0 has a creation reaction, so one of its molecules only appears part-way
 * through the run.
 */
@Tag("Fast")
public class SpringSaladSiteIdentityTest {

	@Test
	public void namesEverySiteInTheTrajectory() throws IOException {
		SpringSaladTrajectory traj = load();
		assertTrue(traj.hasSiteIdentities());

		for (SpringSaladTrajectory.Frame frame : traj.getFrames()) {
			for (SpringSaladTrajectory.Site site : frame.getSites()) {
				assertNotNull(traj.getSiteIdentity(site.getId()), "unnamed site " + site.getId());
			}
		}
	}

	/**
	 * The reason this file is worth serving: MT0 and MT1 are indistinguishable by color and radius,
	 * so without it they would collapse into one entry in the viewer's species list.
	 */
	@Test
	public void distinguishesMoleculesWhoseSitesLookIdentical() throws IOException {
		SpringSaladTrajectory traj = load();
		SpringSaladTrajectory.Site mt0Site0 = siteById(traj, 100000000);
		SpringSaladTrajectory.Site mt1Site0 = siteById(traj, 100500000);

		// same appearance ...
		assertEquals(mt0Site0.getColor(), mt1Site0.getColor());
		assertEquals(mt0Site0.getRadius(), mt1Site0.getRadius());
		// ... different molecules, and the viewer groups them apart
		assertEquals("MT0", traj.getSiteIdentity(mt0Site0.getId()).getMoleculeName());
		assertEquals("MT1", traj.getSiteIdentity(mt1Site0.getId()).getMoleculeName());
		assertNotEquals(traj.siteTypeKey(mt0Site0), traj.siteTypeKey(mt1Site0));
	}

	/**
	 * The key's exact shape, pinned because the separator is invisible and looks like a mistake.
	 * <p>
	 * It is NUL rather than a space on purpose: both halves come from {@code SiteIDs.csv} as
	 * {@code (.+?)} and may contain spaces themselves, so molecule {@code "A B"} with type
	 * {@code "C"} and molecule {@code "A"} with type {@code "B C"} would key alike. A test that
	 * only checks keys are distinct passes either way, which is how a raw NUL byte once ended up
	 * in the source file.
	 */
	@Test
	public void siteTypeKeyHasAStableShape() throws IOException {
		SpringSaladTrajectory traj = load();
		assertEquals("site:MT0" + '\0' + "Site0", traj.siteTypeKey(siteById(traj, 100000000)));
		assertEquals("site:MT0" + '\0' + "Site1", traj.siteTypeKey(siteById(traj, 100000001)));
	}

	@Test
	public void namesMoleculesCreatedPartWayThroughTheRun() throws IOException {
		SpringSaladTrajectory traj = load();
		// molecule 10110 is absent at t=0 and created later by MT0's creation reaction
		assertFalse(idsInFrame(traj, 0).contains(101100000));
		assertTrue(idsInFrame(traj, traj.getFrameCount() - 1).contains(101100000));

		SpringSaladTrajectory.SiteIdentity identity = traj.getSiteIdentity(101100000);
		assertNotNull(identity);
		assertEquals("MT0", identity.getMoleculeName());
		assertEquals("Site0", identity.getSiteTypeName());
	}

	/** The site index is the index into the model's component list, so it must survive intact. */
	@Test
	public void carriesTheSiteIndexWithinTheMolecule() throws IOException {
		SpringSaladTrajectory traj = load();
		assertEquals(0, traj.getSiteIdentity(100000000).getSiteIndex());
		assertEquals("Site0", traj.getSiteIdentity(100000000).getSiteTypeName());
		assertEquals(1, traj.getSiteIdentity(100000001).getSiteIndex());
		assertEquals("Site1", traj.getSiteIdentity(100000001).getSiteTypeName());
	}

	/** Old runs have no SiteIDs.csv; they must still load, just without names. */
	@Test
	public void trajectoryWithoutSiteIdsFallsBackToColorAndRadius() throws IOException {
		SpringSaladTrajectory traj;
		try (Reader reader = resource("sim_VIEW_Run0.txt")) {
			traj = SpringSaladTrajectory.parse(reader);
		}
		assertFalse(traj.hasSiteIdentities());
		assertTrue(traj.getSiteIdentities().isEmpty());

		// MT0 and MT1 now collapse together, which is the documented limit of the fallback
		assertEquals(traj.siteTypeKey(siteById(traj, 100000000)), traj.siteTypeKey(siteById(traj, 100500000)));
		// but distinct colors still separate
		assertNotEquals(traj.siteTypeKey(siteById(traj, 100000000)), traj.siteTypeKey(siteById(traj, 100000001)));
	}

	/**
	 * The flat name the solver's {@code ConsolidationPostprocessor.canonicalizeSiteIdsFile()} writes
	 * for archived runs. It is agreed across two repositories, so pin it: if the solver's name and
	 * this one drift apart the file is simply never found, and sites go quietly unnamed.
	 */
	@Test
	public void flatFileNameMatchesTheOneTheSolverWrites() {
		// solver: simulationName + "_SiteIDs_Run0.csv", where simulationName is the input file name
		// without its extension, e.g. SimID_301441123_0_.langevinInput
		assertEquals("SimID_301441123_0__SiteIDs_Run0.csv",
				LangevinBatchResultSet.LangevinFileType.SiteIds.buildFilename("SimID_301441123_0_"));
		// same convention as the trajectory file it sits beside
		assertEquals("SimID_301441123_0__VIEW_Run0.txt",
				LangevinBatchResultSet.LangevinFileType.Viewer.buildFilename("SimID_301441123_0_"));
	}

	@Test
	public void malformedLinesAreSkippedRatherThanFailingTheRun() throws IOException {
		String text = "100000000,MT0 Site 0 SiteType Site0\n"
				+ "\n"
				+ "garbage that is not a site line\n"
				+ "100000001,MT0 Site 1 SiteType Site1\n";
		Map<Integer, SpringSaladTrajectory.SiteIdentity> identities =
				SpringSaladTrajectory.parseSiteIdentities(new java.io.StringReader(text));
		assertEquals(2, identities.size());
		assertEquals("Site1", identities.get(100000001).getSiteTypeName());
	}

	// ---- helpers ----

	private static SpringSaladTrajectory load() throws IOException {
		SpringSaladTrajectory traj;
		try (Reader reader = resource("sim_VIEW_Run0.txt")) {
			traj = SpringSaladTrajectory.parse(reader);
		}
		try (Reader reader = resource("SiteIDs.csv")) {
			traj = traj.withSiteIdentities(SpringSaladTrajectory.parseSiteIdentities(reader));
		}
		return traj;
	}

	private static Reader resource(String name) {
		InputStream in = SpringSaladSiteIdentityTest.class.getResourceAsStream("/springsalad/" + name);
		assertNotNull(in, "missing test resource " + name);
		return new InputStreamReader(in, StandardCharsets.UTF_8);
	}

	private static SpringSaladTrajectory.Site siteById(SpringSaladTrajectory traj, int id) {
		for (SpringSaladTrajectory.Frame frame : traj.getFrames()) {
			for (SpringSaladTrajectory.Site site : frame.getSites()) {
				if (site.getId() == id) {
					return site;
				}
			}
		}
		throw new AssertionError("no site " + id);
	}

	private static Set<Integer> idsInFrame(SpringSaladTrajectory traj, int frameIndex) {
		Set<Integer> ids = new HashSet<>();
		for (SpringSaladTrajectory.Site site : traj.getFrames().get(frameIndex).getSites()) {
			ids.add(site.getId());
		}
		return ids;
	}
}
