package cbit.vcell.solver.ode.gui;

import cbit.vcell.simdata.SpringSaladTrajectory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.springsalad.Colors;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

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
		assertNotEquals(SpringSaladSpeciesLegend.keyOf("LIME", 1.0), SpringSaladSpeciesLegend.keyOf("LIME", 2.0));
		assertEquals(SpringSaladSpeciesLegend.keyOf("LIME", 1.0), SpringSaladSpeciesLegend.keyOf("lime", 1.000001));
		assertNotEquals(SpringSaladSpeciesLegend.keyOf("LIME", 1.0),
				SpringSaladSpeciesLegend.keyOf("LIME_GREEN", 1.0));
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

		String key = SpringSaladSpeciesLegend.keyOf(Colors.LIMESTRING, 3.0);
		assertTrue(canvas.isSiteTypeVisible(key));
		canvas.setSiteTypeVisible(key, false);
		assertFalse(canvas.isSiteTypeVisible(key));
		assertEquals(0, countGlyphPixels(canvas.renderToImage(300, 300)), "hidden site type still drew");

		canvas.showAllSiteTypes();
		assertEquals(visiblePixels, countGlyphPixels(canvas.renderToImage(300, 300)));
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
