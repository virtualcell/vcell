package cbit.plot.gui;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.ColorUtil;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class PlotRenderersAccessibilityTest {

	@Test
	public void avgRendererSeriesStrokesDiffer_8_3_e() throws Exception {
		MoleculePlotPanel panel = panelWithSeries(2, true);
		BasicStroke first = panel.strokeForSeries("s0");
		BasicStroke second = panel.strokeForSeries("s1");
		assertNotNull(first);
		assertNotNull(second);
		assertFalse(Arrays.equals(first.getDashArray(), second.getDashArray()));
		assertNull(first.getDashArray());
		assertArrayEquals(ColorUtil.seriesDash(1), second.getDashArray());

		panel.setShowNodes(false);
		BufferedImage image = paint(panel);
		int solidGaps = longestGapOnBusiestRows(image, 0);
		int dashedGaps = longestGapOnBusiestRows(image, 1);
		assertEquals(0, Math.min(solidGaps, dashedGaps));
		assertTrue(Math.max(solidGaps, dashedGaps) > 0);
		assertTrue(Math.max(solidGaps, dashedGaps) <= 12);
	}

	@Test
	public void dashUsesFullSeriesIndexNotPaletteSlot() {
		MoleculePlotPanel panel = panelWithSeries(9, true);
		assertArrayEquals(ColorUtil.seriesDash(6), panel.strokeForSeries("s6").getDashArray());
		assertFalse(Arrays.equals(ColorUtil.seriesDash(0), panel.strokeForSeries("s6").getDashArray()));
		assertEquals(ColorUtil.seriesColor(6), ColorUtil.seriesColor(0));
	}

	@Test
	public void seriesCountsKeepKeyboardIdentityWhenPatternsRepeat() {
		for (int count : new int[] {1, 6, 8, 24, 25, 30}) {
			MoleculePlotPanel panel = panelWithSeries(count, true);
			for (int i = 0; i < count; i++) {
				assertArrayEquals(ColorUtil.seriesDash(i), panel.strokeForSeries("s" + i).getDashArray(),
						"count " + count + " series " + i);
			}
			fire(panel, "ctrl N");
			assertEquals("s0", panel.seriesStatusText());
			fire(panel, "ctrl P");
			assertEquals("s" + (count - 1), panel.seriesStatusText());
		}
		MoleculePlotPanel repeated = panelWithSeries(25, true);
		assertArrayEquals(repeated.strokeForSeries("s0").getDashArray(), repeated.strokeForSeries("s24").getDashArray());
		repeated.selectSeries("s0");
		assertEquals("s0", repeated.seriesStatusText());
		repeated.selectSeries("s24");
		assertEquals("s24", repeated.seriesStatusText());
		assertNotEquals(repeated.seriesStatusText(), "s0");
	}

	@Test
	public void stylesOffAndCustomColorKeepKeyboardIdentity() {
		MoleculePlotPanel stylesOff = panelWithSeries(8, false);
		stylesOff.setShowLines(false);
		assertNull(stylesOff.strokeForSeries("s0").getDashArray());
		assertNull(stylesOff.strokeForSeries("s1").getDashArray());
		fire(stylesOff, "ctrl N");
		assertEquals("s0", stylesOff.seriesStatusText());
		fire(stylesOff, "ctrl N");
		assertEquals("s1", stylesOff.seriesStatusText());
		assertArrayEquals(new double[] {2, 2, 2, 2, 2}, stylesOff.valuesForSeries("s1"));

		MoleculePlotPanel custom = new MoleculePlotPanel();
		custom.setVaryLineStyles(true);
		double[] time = {0, 1, 2, 3, 4};
		double[] overlapped = {1, 1, 1, 1, 1};
		custom.addAvgRenderer(time, overlapped, Color.RED, "red-a", "AVG", 0);
		custom.addAvgRenderer(time, overlapped, Color.RED, "red-b", "AVG", 1);
		assertFalse(Arrays.equals(custom.strokeForSeries("red-a").getDashArray(), custom.strokeForSeries("red-b").getDashArray()));
		custom.setVaryLineStyles(false);
		fire(custom, "ctrl N");
		assertEquals("red-a", custom.seriesStatusText());
		fire(custom, "ctrl I");
		assertEquals("red-a (only this series)", custom.seriesStatusText());
		assertFalse(custom.isSeriesVisible("red-b"));
		assertTrue(custom.isSeriesVisible("red-a"));
		fire(custom, "ctrl I");
		assertEquals("red-a", custom.seriesStatusText());
		assertTrue(custom.isSeriesVisible("red-b"));
	}

	@Test
	public void bandAndBubbleExposeNameAndValuesFromTheKeyboard() {
		MoleculePlotPanel bands = new MoleculePlotPanel();
		double[] time = {0, 1, 2, 3, 4};
		double[] low = {0, 0, 0, 0, 0};
		double[] high = {2, 2, 2, 2, 2};
		bands.addMinMaxRenderer(time, low, high, Color.BLACK, "envelope", "MIN_MAX");
		bands.addAvgRenderer(time, new double[] {1, 1, 1, 1, 1}, Color.BLACK, "mean", "AVG", 3);
		fire(bands, "ctrl N");
		assertEquals("envelope", bands.seriesStatusText());
		assertArrayEquals(high, bands.valuesForSeries("envelope"));
		fire(bands, "ctrl N");
		assertEquals("mean", bands.seriesStatusText());
		assertArrayEquals(new double[] {1, 1, 1, 1, 1}, bands.valuesForSeries("mean"));

		ClusterPlotPanel bubbles = new ClusterPlotPanel();
		bubbles.addBubbleRenderer(time, new double[] {4, 0, 9, 1, 2}, new Color(220, 30, 30), "3", 4);
		bubbles.addBubbleRenderer(time, new double[] {1, 5, 0, 7, 3}, new Color(220, 30, 30), "8", 5);
		bubbles.setShowBubbleSingleColor(true);
		fire(bubbles, "ctrl N");
		assertEquals("3", bubbles.seriesStatusText());
		assertArrayEquals(new double[] {4, 0, 9, 1, 2}, bubbles.valuesForSeries("3"));
		fire(bubbles, "ctrl N");
		assertEquals("8", bubbles.seriesStatusText());
		fire(bubbles, "ctrl I");
		assertEquals("8 (only this series)", bubbles.seriesStatusText());
		assertFalse(bubbles.isSeriesVisible("3"));
	}

	@Test
	public void grayscaleKeepsSeriesOneDashGaps() throws Exception {
		MoleculePlotPanel panel = new MoleculePlotPanel();
		panel.setShowNodes(false);
		panel.setVaryLineStyles(true);
		double[] time = {0, 1, 2, 3, 4};
		panel.addAvgRenderer(time, new double[] {1, 1, 1, 1, 1}, ColorUtil.seriesColor(0), "s0", "AVG", 0);
		panel.addAvgRenderer(time, new double[] {2, 2, 2, 2, 2}, ColorUtil.seriesColor(1), "s1", "AVG", 1);
		panel.setGlobalMinMax(0, 3);
		panel.setDt(1);
		BufferedImage image = paint(panel);
		int seriesOneRow = rowWithMost(image, PlotRenderersAccessibilityTest::isOlive);
		assertTrue(seriesOneRow >= 0);
		int longestGap = longestGapOnRow(grayscale(image), seriesOneRow, PlotRenderersAccessibilityTest::isMidGray);
		assertTrue(longestGap > 0 && longestGap <= 12, "grayscale gaps " + longestGap);
	}

	@Test
	public void dataViewNamesSeriesValues() {
		DataView data = new DataView();
		assertEquals("Series data", data.table().getAccessibleContext().getAccessibleName());
		assertTrue(data.table().getAccessibleContext().getAccessibleDescription().contains("series names"));
		assertTrue(data.table().getCellSelectionEnabled());
	}

	private static MoleculePlotPanel panelWithSeries(int count, boolean varyLineStyles) {
		MoleculePlotPanel panel = new MoleculePlotPanel();
		panel.setVaryLineStyles(varyLineStyles);
		panel.setShowNodes(true);
		double[] time = {0, 1, 2, 3, 4};
		for (int i = 0; i < count; i++) {
			double y = i + 1.0;
			panel.addAvgRenderer(time, new double[] {y, y, y, y, y}, Color.BLACK, "s" + i, "AVG", i);
		}
		panel.setGlobalMinMax(0, count + 1.0);
		panel.setDt(1);
		return panel;
	}

	private static void fire(AbstractPlotPanel panel, String keyStroke) {
		KeyStroke stroke = KeyStroke.getKeyStroke(keyStroke);
		assertNotNull(panel.getInputMap(JComponent.WHEN_FOCUSED).get(stroke), keyStroke);
		assertNull(panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(stroke), keyStroke);
		ActionListener action = panel.getActionForKeyStroke(stroke);
		assertNotNull(action, keyStroke);
		action.actionPerformed(new ActionEvent(panel, ActionEvent.ACTION_PERFORMED, keyStroke));
	}

	private static BufferedImage paint(MoleculePlotPanel panel) throws Exception {
		panel.setSize(480, 320);
		BufferedImage image = new BufferedImage(480, 320, BufferedImage.TYPE_INT_RGB);
		SwingUtilities.invokeAndWait(() -> {
			Graphics2D graphics = image.createGraphics();
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
			panel.paint(graphics);
			graphics.dispose();
		});
		return image;
	}

	private static int longestGapOnBusiestRows(BufferedImage image, int rank) {
		int[] gaps = new int[image.getHeight()];
		int[] ink = new int[image.getHeight()];
		for (int y = 20; y < image.getHeight() - 30; y++) {
			int run = 0;
			int longest = 0;
			boolean seen = false;
			for (int x = 60; x < image.getWidth() - 30; x++) {
				if (isDark(image.getRGB(x, y))) {
					ink[y]++;
					if (seen) {
						longest = Math.max(longest, run);
					}
					seen = true;
					run = 0;
				} else if (seen && isLight(image.getRGB(x, y))) {
					run++;
				}
			}
			gaps[y] = longest;
		}
		int first = -1;
		int second = -1;
		for (int y = 0; y < ink.length; y++) {
			if (ink[y] < 20) {
				continue;
			}
			if (first < 0 || ink[y] > ink[first]) {
				second = first;
				first = y;
			} else if (second < 0 || ink[y] > ink[second]) {
				second = y;
			}
		}
		assertTrue(first >= 0 && second >= 0);
		return rank == 0 ? gaps[first] : gaps[second];
	}

	private static BufferedImage grayscale(BufferedImage source) {
		BufferedImage gray = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < source.getHeight(); y++) {
			for (int x = 0; x < source.getWidth(); x++) {
				int rgb = source.getRGB(x, y);
				int r = (rgb >> 16) & 0xff;
				int g = (rgb >> 8) & 0xff;
				int b = rgb & 0xff;
				int lum = (int) Math.round(0.2126 * r + 0.7152 * g + 0.0722 * b);
				int pixel = (lum << 16) | (lum << 8) | lum;
				gray.setRGB(x, y, pixel);
			}
		}
		return gray;
	}

	private static int rowWithMost(BufferedImage image, PixelTest test) {
		int bestRow = -1;
		int best = 0;
		for (int y = 0; y < image.getHeight(); y++) {
			int count = 0;
			for (int x = 0; x < image.getWidth(); x++) {
				if (test.matches(image.getRGB(x, y))) {
					count++;
				}
			}
			if (count > best) {
				best = count;
				bestRow = y;
			}
		}
		return best > 0 ? bestRow : -1;
	}

	private static int longestGapOnRow(BufferedImage image, int y, PixelTest ink) {
		int run = 0;
		int longest = 0;
		boolean seen = false;
		for (int x = 60; x < image.getWidth() - 30; x++) {
			if (ink.matches(image.getRGB(x, y))) {
				if (seen) {
					longest = Math.max(longest, run);
				}
				seen = true;
				run = 0;
			} else if (seen) {
				run++;
			}
		}
		return longest;
	}

	private static boolean isOlive(int rgb) {
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		int dr = r - 0x99;
		int dg = g - 0x99;
		int db = b - 0x33;
		return dr * dr + dg * dg + db * db < 140 * 140 && (r + g) > b + 40;
	}

	private static boolean isMidGray(int rgb) {
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		int lum = (r * 2126 + g * 7152 + b * 722) / 10000;
		return lum > 70 && lum < 210;
	}

	private interface PixelTest {
		boolean matches(int rgb);
	}

	private static boolean isDark(int rgb) {
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		return r < 80 && g < 80 && b < 80;
	}

	private static boolean isLight(int rgb) {
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		return r > 220 && g > 220 && b > 220;
	}

	private static final class DataView extends AbstractDataPanel {
		javax.swing.JTable table() {
			return getScrollPaneTable();
		}
	}
}
