package cbit.vcell.solver.ode.gui;

import cbit.vcell.math.LangevinParticleMolecularComponent;
import cbit.vcell.math.LangevinParticleMolecularType;
import cbit.vcell.math.MathDescription;
import cbit.vcell.parser.Expression;
import cbit.vcell.simdata.SpringSaladTrajectory;
import org.vcell.util.springsalad.NamedColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.springsalad.Colors;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The species selector groups a trajectory by the only thing its file distinguishes sites by —
 * (color, radius) — and the canvas hides the sites of a deselected group.
 */
@Tag("Fast")
public class SpringSaladSpeciesLegendTest {

	@BeforeAll
	static void headless() { System.setProperty("java.awt.headless", "true"); }

	@Test
	public void groupsTrajectorySitesByColorAndRadius() {
		SpringSaladSpeciesLegend legend =
				SpringSaladSpeciesLegend.build(SpringSaladViewerPanel.makeDemoTrajectory(), null);
		// the demo cycles 12 colors over 5 radii
		assertFalse(legend.isEmpty());
		assertEquals(60, legend.getSiteTypes().stream().mapToInt(SpringSaladSpeciesLegend.SiteType::getSiteCount).sum());
		assertFalse(legend.isNamed(), "no math description was supplied, so nothing should be named");
		// with no names every entry falls into the single unidentified bucket
		assertEquals(List.of(SpringSaladSpeciesLegend.UNNAMED_SPECIES),
				new ArrayList<>(legend.bySpecies().keySet()));
	}

	@Test
	public void sameColorDifferentRadiusAreDistinctEntries() {
		SpringSaladTrajectory traj = singleTypeTrajectory();
		assertNotEquals(traj.siteTypeKey(site(0, 1.0, "LIME")), traj.siteTypeKey(site(0, 2.0, "LIME")));
		assertEquals(traj.siteTypeKey(site(0, 1.0, "LIME")), traj.siteTypeKey(site(0, 1.000001, "lime")));
		assertNotEquals(traj.siteTypeKey(site(0, 1.0, "LIME")), traj.siteTypeKey(site(0, 1.0, "LIME_GREEN")));
	}

	/**
	 * When the run supplied SiteIDs.csv the entries are real molecules and site types, and molecules
	 * whose sites share a color and radius no longer collapse together.
	 */
	@Test
	public void namedTrajectoryGroupsBySpecies() {
		SpringSaladTrajectory traj = twoLookalikeMoleculesTrajectory();
		SpringSaladSpeciesLegend legend = SpringSaladSpeciesLegend.build(traj, null);

		assertTrue(legend.isNamed());
		assertEquals(List.of("MolA", "MolB"), new ArrayList<>(legend.bySpecies().keySet()));
		assertEquals(4, legend.getSiteTypes().size(), "2 molecules x 2 site types");
		assertEquals(List.of("Head", "Tail"),
				legend.bySpecies().get("MolA").stream().map(SpringSaladSpeciesLegend.SiteType::getSiteLabel).toList());
	}

	/** Without the file, the same trajectory can only be split by appearance — so it merges. */
	@Test
	public void unnamedTrajectoryMergesLookalikeMolecules() {
		SpringSaladTrajectory traj = twoLookalikeMoleculesTrajectory().withSiteIdentities(Map.of());
		SpringSaladSpeciesLegend legend = SpringSaladSpeciesLegend.build(traj, null);

		assertFalse(legend.isNamed());
		assertEquals(2, legend.getSiteTypes().size(), "4 site types collapse to 2 distinct appearances");
	}

	/** Site types only present in a later frame (created mid-run) must still be listed. */
	@Test
	public void includesSiteTypesThatAppearOnlyInLaterFrames() {
		List<SpringSaladTrajectory.Frame> frames = List.of(
				new SpringSaladTrajectory.Frame(0, 0.0, List.of(site(10000, 1.0, "RED")), List.of()),
				new SpringSaladTrajectory.Frame(1, 1.0,
						List.of(site(10000, 1.0, "RED"), site(20000, 1.0, "TEAL")), List.of()));
		SpringSaladTrajectory traj = new SpringSaladTrajectory(1, 1, 10, 10, 5, 5, frames);
		SpringSaladSpeciesLegend legend = SpringSaladSpeciesLegend.build(traj, null);
		assertEquals(2, legend.getSiteTypes().size(), "the late-appearing TEAL type was dropped");
	}

	private static SpringSaladTrajectory.Site site(int id, double radius, String color) {
		return new SpringSaladTrajectory.Site(id, radius, color, 0, 0, 0);
	}

	/** Two molecules whose sites are identical in color and radius — the awkward real-world case. */
	private static SpringSaladTrajectory twoLookalikeMoleculesTrajectory() {
		List<SpringSaladTrajectory.Site> sites = List.of(
				site(100000000, 1.0, "DARK_GRAY"), site(100000001, 1.0, "BLUE"),   // MolA
				site(100500000, 1.0, "DARK_GRAY"), site(100500001, 1.0, "BLUE"));  // MolB
		SpringSaladTrajectory traj = new SpringSaladTrajectory(1, 1, 10, 10, 5, 5,
				List.of(new SpringSaladTrajectory.Frame(0, 0.0, sites, List.of())));
		return traj.withSiteIdentities(Map.of(
				100000000, new SpringSaladTrajectory.SiteIdentity("MolA", 0, "Head"),
				100000001, new SpringSaladTrajectory.SiteIdentity("MolA", 1, "Tail"),
				100500000, new SpringSaladTrajectory.SiteIdentity("MolB", 0, "Head"),
				100500001, new SpringSaladTrajectory.SiteIdentity("MolB", 1, "Tail")));
	}

	@Test
	public void hidingASiteTypeRemovesItsSitesFromTheRender() {
		// one site type only, so hiding it must empty the scene of glyphs
		SpringSaladTrajectory traj = singleTypeTrajectory();
		SpringSaladViewerCanvas canvas = new SpringSaladViewerCanvas();
		canvas.setTrajectory(traj);
		canvas.setShowBox(false);
		canvas.setShowMembrane(false);
		canvas.setShowLinks(false);

		int visiblePixels = countGlyphPixels(canvas.renderToImage(300, 300));
		assertTrue(visiblePixels > 0, "expected glyphs before hiding");

		String key = traj.siteTypeKey(traj.getFrames().get(0).getSites().get(0));
		assertTrue(canvas.isSiteTypeVisible(key));
		canvas.setSiteTypeVisible(key, false);
		assertFalse(canvas.isSiteTypeVisible(key));
		assertEquals(0, countGlyphPixels(canvas.renderToImage(300, 300)), "hidden site type still drew");

		canvas.showAllSiteTypes();
		assertEquals(visiblePixels, countGlyphPixels(canvas.renderToImage(300, 300)));
	}


	/**
	 * The fallback naming path -- a run with no SiteIDs.csv, named by joining against the math.
	 *
	 * Every other test in this class passed null for the math description, so this path had no
	 * coverage at all, and it threw IllegalFormatConversionException the moment it ran: the site
	 * radius is an Expression in the math and a double on the trajectory side, and the Expression
	 * was being handed straight to "%.5f". That took down the whole simulation results viewer,
	 * reported as "DataAccessException-f != cbit.vcell.parser.Expression".
	 */
	@Test
	public void namesSitesFromTheMathWhenTheRunHasNoSiteIds() throws Exception {
		SpringSaladTrajectory traj = singleTypeTrajectory();   // radius 3.0, colour LIME
		MathDescription math = mathWithSite("MolA", "SiteA", 3.0, Colors.LIME);

		SpringSaladSpeciesLegend legend = SpringSaladSpeciesLegend.build(traj, math);

		assertTrue(legend.isNamed(), "the model should have supplied a name");
		assertEquals(List.of("MolA"), new ArrayList<>(legend.bySpecies().keySet()));
		assertEquals("SiteA", legend.getSiteTypes().get(0).getSiteLabel());
	}

	/** A radius that does not match any trajectory entry simply names nothing -- and never throws. */
	@Test
	public void aRadiusThatMatchesNothingLeavesTheLegendUnnamed() throws Exception {
		SpringSaladTrajectory traj = singleTypeTrajectory();   // radius 3.0
		MathDescription math = mathWithSite("MolA", "SiteA", 99.0, Colors.LIME);

		SpringSaladSpeciesLegend legend = SpringSaladSpeciesLegend.build(traj, math);

		assertFalse(legend.isNamed());
		assertEquals(List.of(SpringSaladSpeciesLegend.UNNAMED_SPECIES),
				new ArrayList<>(legend.bySpecies().keySet()));
	}

	/**
	 * A non-constant radius cannot be matched against a numeric key, but naming is a convenience:
	 * it must leave the site unnamed rather than abort the legend and take the viewer with it.
	 */
	@Test
	public void aNonConstantRadiusIsSkippedRatherThanFatal() throws Exception {
		SpringSaladTrajectory traj = singleTypeTrajectory();
		MathDescription math = mathWithSiteRadius("MolA", "SiteA", new Expression("someParameter * 2"), Colors.LIME);

		SpringSaladSpeciesLegend legend = SpringSaladSpeciesLegend.build(traj, math);

		assertFalse(legend.isNamed(), "an unevaluable radius names nothing");
		assertFalse(legend.isEmpty(), "but the legend itself must still be built");
	}

	private static MathDescription mathWithSite(String molecule, String site, double radius, NamedColor color)
			throws Exception {
		return mathWithSiteRadius(molecule, site, new Expression(radius), color);
	}

	private static MathDescription mathWithSiteRadius(String molecule, String site, Expression radius,
			NamedColor color) throws Exception {
		LangevinParticleMolecularComponent component =
				new LangevinParticleMolecularComponent(molecule + "_" + site, site);
		component.setRadius(radius);
		component.setColor(color);
		LangevinParticleMolecularType type = new LangevinParticleMolecularType(molecule);
		type.addMolecularComponent(component);

		MathDescription math = new MathDescription("test");
		math.addParticleMolecularType(type);
		return math;
	}

	private static SpringSaladTrajectory singleTypeTrajectory() {
		List<SpringSaladTrajectory.Site> sites = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			sites.add(new SpringSaladTrajectory.Site(i, 3.0, Colors.LIMESTRING, i * 4 - 16, i * 3 - 12, i - 4));
		}
		SpringSaladTrajectory.Frame frame =
				new SpringSaladTrajectory.Frame(0, 0.0, sites, new ArrayList<>());
		return new SpringSaladTrajectory(1e-3, 1e-3, 40, 40, 20, 20, List.of(frame));
	}

	/** Pixels meaningfully brighter than the black background. */
	private static int countGlyphPixels(BufferedImage img) {
		int n = 0;
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				int rgb = img.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
				if (r + g + b > 60) n++;
			}
		}
		return n;
	}
}
