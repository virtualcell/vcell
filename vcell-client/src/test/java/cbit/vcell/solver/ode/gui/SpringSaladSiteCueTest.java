package cbit.vcell.solver.ode.gui;

import cbit.vcell.simdata.SpringSaladTrajectory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.springsalad.Colors;
import org.vcell.util.springsalad.NamedColor;

import javax.swing.JCheckBox;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Site marks are presentation. The palette colors the solver file stores stay as they are.
 */
@Tag("Fast")
public class SpringSaladSiteCueTest {

	@BeforeAll
	static void headless() { System.setProperty("java.awt.headless", "true"); }

	@Test
	public void sameFillDifferentPatternDiffersInGrayscale() {
		int[] ring = grayscale(0, null);
		int[] cross = grayscale(2, null);
		assertFalse(Arrays.equals(ring, cross));
	}

	@Test
	public void isolatedCaptionIsDrawn() {
		assertFalse(Arrays.equals(grayscale(0, null), grayscale(0, "Head")));
	}

	@Test
	public void namesAppearWhenMarksRunOut() {
		assertNull(SpringSaladViewerCanvas.captionFor(2, "Head"));
		assertNull(SpringSaladViewerCanvas.captionFor(4, "Head"));
		assertEquals("Head", SpringSaladViewerCanvas.captionFor(1, "Head")); // Alt+I isolation
		assertEquals("Head", SpringSaladViewerCanvas.captionFor(5, "Head")); // a fifth type repeats a mark
	}

	/**
	 * Five co-visible types on the same fill and the same mark: the name each sprite wears is
	 * what tells them apart, and the five pictures stay different in grayscale.
	 */
	@Test
	public void fiveCoVisibleTypesShareFillButKeepFiveNames() {
		String[] names = { "Alpha", "Beta", "Gamma", "Delta", "Epsilon" };
		int[][] rasters = new int[names.length][];
		for (int i = 0; i < names.length; i++) {
			rasters[i] = grayscale(0, names[i]); // same pattern slot, same fill
		}
		for (int i = 0; i < names.length; i++) {
			for (int j = i + 1; j < names.length; j++) {
				assertFalse(Arrays.equals(rasters[i], rasters[j]),
						names[i] + " and " + names[j] + " look alike in grayscale");
			}
		}
	}

	/**
	 * The rendered scene: with five co-visible types every sprite carries its name, so the strip
	 * past the last sprite holds caption ink; with four types the marks suffice and it does not.
	 */
	@Test
	public void fiveCoVisibleTypesPaintTheirNames() {
		SpringSaladTrajectory traj = fiveTypeTrajectory();
		SpringSaladViewerCanvas canvas = new SpringSaladViewerCanvas();
		canvas.setTrajectory(traj);
		BufferedImage allFive = canvas.renderToImage(400, 400);

		String hiddenKey = traj.siteTypeKey(traj.getFrames().get(0).getSites().get(0));
		canvas.setSiteTypeVisible(hiddenKey, false); // four types left: marks unique, names off
		BufferedImage fourTypes = canvas.renderToImage(400, 400);

		int spriteRight = rightmostFillPixel(fourTypes);
		assertTrue(spriteRight > 0, "no sprite found in the render");
		int namedInk = inkRightOf(allFive, spriteRight + 2);
		int unnamedInk = inkRightOf(fourTypes, spriteRight + 2);
		assertTrue(namedInk >= 20, "five co-visible types painted " + namedInk + " caption pixels");
		assertTrue(unnamedInk <= 2, "four types still painted " + unnamedInk + " caption pixels");
	}

	/** Five site types that share one fill and one radius; only their names differ. */
	private static SpringSaladTrajectory fiveTypeTrajectory() {
		String[] names = { "Alpha", "Beta", "Gamma", "Delta", "Epsilon" };
		List<SpringSaladTrajectory.Site> sites = new ArrayList<>();
		Map<Integer, SpringSaladTrajectory.SiteIdentity> identities = new HashMap<>();
		for (int i = 0; i < names.length; i++) {
			sites.add(new SpringSaladTrajectory.Site(i, 4.0, "RED", -30 + 15 * i, 0, 5));
			identities.put(i, new SpringSaladTrajectory.SiteIdentity("Kinase", i, names[i]));
		}
		SpringSaladTrajectory.Frame frame =
				new SpringSaladTrajectory.Frame(0, 0.0, sites, new ArrayList<int[]>());
		return new SpringSaladTrajectory(1e-4, 1e-4, 40, 40, 10, 10,
				new ArrayList<>(List.of(frame)), identities);
	}

	/** Rightmost x of a red sprite pixel: where the last sprite ends and its caption begins. */
	private static int rightmostFillPixel(BufferedImage img) {
		int right = -1;
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				int rgb = img.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
				if (r > 120 && g < r / 2 && b < r / 2) {
					right = Math.max(right, x);
				}
			}
		}
		return right;
	}

	/** Near-white pixels right of {@code x0}: caption ink is white on a red fill (cueInk). */
	private static int inkRightOf(BufferedImage img, int x0) {
		int count = 0;
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = Math.max(0, x0); x < img.getWidth(); x++) {
				int rgb = img.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
				if (r > 200 && g > 200 && b > 200) {
					count++;
				}
			}
		}
		return count;
	}

	@Test
	public void paletteColorsStillResolveToTheStoredColor() {
		for (NamedColor nc : Colors.COLORARRAY) {
			if (nc == Colors.BLACK) {
				continue;
			}
			assertEquals(nc.getColor(), SpringSaladViewerCanvas.colorForName(nc.getName()));
		}
	}

	@Test
	public void altIsolateLeavesOneSiteTypeVisible() {
		SpringSaladTrajectory traj = SpringSaladViewerPanel.makeDemoTrajectory();
		SpringSaladSpeciesLegend legend = SpringSaladSpeciesLegend.build(traj, null);
		List<String> shown = new ArrayList<>();
		SpringSaladSpeciesPanel panel = new SpringSaladSpeciesPanel((key, visible) -> {
			if (visible) {
				shown.add(key);
			}
		});
		panel.setLegend(legend);
		String only = legend.getSiteTypes().get(0).getKey();
		shown.clear();
		panel.isolateSiteType(only);
		assertEquals(List.of(only), shown);
		JCheckBox onlyBox = findBox(panel, "SpringSaladSite_" + only);
		assertNotNull(onlyBox);
		assertEquals(true, onlyBox.isSelected());
		int selected = 0;
		for (SpringSaladSpeciesLegend.SiteType siteType : legend.getSiteTypes()) {
			if (findBox(panel, "SpringSaladSite_" + siteType.getKey()).isSelected()) {
				selected++;
			}
		}
		assertEquals(1, selected);
	}

	private static JCheckBox findBox(java.awt.Container parent, String name) {
		for (java.awt.Component child : parent.getComponents()) {
			if (child instanceof JCheckBox && name.equals(child.getName())) {
				return (JCheckBox) child;
			}
			if (child instanceof java.awt.Container) {
				JCheckBox found = findBox((java.awt.Container) child, name);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}

	private static int[] grayscale(int pattern, String caption) {
		Color fill = new Color(180, 40, 40);
		BufferedImage img = new BufferedImage(80, 48, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setColor(fill);
		g.fillRect(0, 0, 80, 48);
		SpringSaladViewerCanvas.paintSiteCue(g, pattern, 4, 4, 40, fill, caption);
		g.dispose();
		int[] out = new int[img.getWidth() * img.getHeight()];
		for (int i = 0; i < out.length; i++) {
			int rgb = img.getRGB(i % img.getWidth(), i / img.getWidth());
			int r = (rgb >> 16) & 0xFF, gg = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
			out[i] = (r * 299 + gg * 587 + b * 114) / 1000;
		}
		return out;
	}
}
