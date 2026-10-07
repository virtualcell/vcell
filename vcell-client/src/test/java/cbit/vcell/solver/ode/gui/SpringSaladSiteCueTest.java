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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
