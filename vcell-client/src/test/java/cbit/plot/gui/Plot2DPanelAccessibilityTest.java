package cbit.plot.gui;

import cbit.plot.Plot2D;
import cbit.plot.PlotData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.ColorUtil;
import org.vcell.util.Range;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class Plot2DPanelAccessibilityTest {

	@Test
	public void sixCurveDashAcceptance_8_3_a() throws Exception {
		BufferedImage image = paint(panelWithCurves(6, false, Plot2D.RENDERHINT_DRAWLINE, false));
		int seriesOneRow = rowWithMost(image, Plot2DPanelAccessibilityTest::isOlive);
		assertTrue(seriesOneRow >= 0, "series 1 olive stroke was not painted");
		int[] gaps = interiorGaps(image, seriesOneRow, Plot2DPanelAccessibilityTest::isOlive);
		int longestGap = 0;
		for (int gap : gaps) {
			longestGap = Math.max(longestGap, gap);
		}
		assertTrue(longestGap > 0, "series 1 should contain a background gap");
		assertTrue(longestGap <= 12, "series 1 gaps " + java.util.Arrays.toString(gaps));
	}

	@Test
	public void denseDashAcceptance_8_3_b() throws Exception {
		BufferedImage image = paint(panelWithCurves(6, false, Plot2D.RENDERHINT_DRAWLINE, false, 5000));
		int seriesOneRow = rowWithMost(image, Plot2DPanelAccessibilityTest::isOlive);
		int[] gaps = interiorGaps(image, seriesOneRow, Plot2DPanelAccessibilityTest::isOlive);
		assertTrue(gaps.length >= 2, java.util.Arrays.toString(gaps));
		for (int gap : gaps) {
			assertTrue(gap <= 12);
		}
	}

	@Test
	public void sharedContractDashContinuity() throws Exception {
		Plot2DPanel panel = panelWithCurves(2, false, Plot2D.RENDERHINT_DRAWLINE, false);
		assertNull(((BasicStroke) panel.getVisiblePlotStroke(0)).getDashArray());
		assertEquals(ColorUtil.seriesDash(1)[0], ((BasicStroke) panel.getVisiblePlotStroke(1)).getDashArray()[0], 0.01f);
		panel.setVaryLineStyles(false);
		assertTrue(new BasicStroke(1.5f).equals(panel.getVisiblePlotStroke(1)));
	}

	@Test
	public void customColorsStayAuthoritative() throws Exception {
		Plot2DPanel panel = panelWithCurves(2, false, Plot2D.RENDERHINT_DRAWLINE, false);
		panel.setUserDefinedColors(new Color[]{Color.red});
		assertEquals(Color.red, panel.getVisiblePlotPaint(0));
		assertEquals(ColorUtil.seriesColor(1), panel.getVisiblePlotPaint(1));
	}

	@Test
	public void hiddenSeriesDoesNotConsumeAStyle() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
		});
		Plot2DPanel panel = new Plot2DPanel();
		PlotData[] data = new PlotData[2];
		data[0] = new PlotData(new double[]{0, 1}, new double[]{1, 1});
		data[1] = new PlotData(new double[]{0, 1}, new double[]{2, 2});
		Plot2D plot = new Plot2D(null, null, new String[]{"hidden", "shown"}, data,
				new String[]{"t", "x", "y"}, new boolean[]{false, true},
				new int[]{Plot2D.RENDERHINT_DRAWLINE, Plot2D.RENDERHINT_DRAWLINE});
		panel.setPlot2D(plot);
		assertEquals(ColorUtil.seriesColor(0), panel.getVisiblePlotPaint(0));
		assertNull(panel.getVisiblePlotStroke(0).getDashArray());
	}

	@Test
	public void pointerStatusNamesTheSeriesWithoutGraphics() throws Exception {
		Plot2DPanel panel = panelWithCurves(2, false, Plot2D.RENDERHINT_DRAWLINE, false);
		paint(panel);
		JLabel status = new JLabel();
		panel.setStatusLabel(status);
		panel.setShowCrosshair(true);
		panel.setCurrentPlot("s1");
		panel.dispatchEvent(new MouseEvent(panel, MouseEvent.MOUSE_MOVED, 0, 0, 200, 150, 0, false));
		assertNotNull(status.getText());
		assertTrue(status.getText().startsWith("s1: "), status.getText());
	}

	@Test
	public void emptyPlotPointerDoesNotFail() {
		Plot2DPanel panel = new Plot2DPanel();
		panel.setShowCrosshair(true);
		panel.setStatusLabel(new JLabel());
		panel.dispatchEvent(new MouseEvent(panel, MouseEvent.MOUSE_MOVED, 0, 0, 10, 10, 0, false));
		String text = panel.getStatusLabel().getText();
		assertTrue(text == null || text.isBlank());
	}

	@Test
	public void histogramAndLargeCurvePaint() throws Exception {
		BufferedImage histogram = paint(panelWithCurves(1, true, Plot2D.RENDERHINT_DRAWLINE | Plot2D.RENDERHINT_DRAWPOINT, true));
		assertTrue(countInk(histogram) > 20);
		long started = System.nanoTime();
		paint(panelWithCurves(1, false, Plot2D.RENDERHINT_DRAWLINE, false, 100000));
		long elapsedMs = (System.nanoTime() - started) / 1_000_000L;
		assertTrue(elapsedMs < 5000, "100k-point paint took " + elapsedMs + " ms");
	}

	private static Plot2DPanel panelWithCurves(int count, boolean histogram, int hints, boolean nodes) throws Exception {
		return panelWithCurves(count, histogram, hints, nodes, 8);
	}

	private static Plot2DPanel panelWithCurves(int count, boolean histogram, int hints, boolean nodes, int samples) throws Exception {
		final Plot2DPanel[] holder = new Plot2DPanel[1];
		SwingUtilities.invokeAndWait(() -> {
			Plot2DPanel panel = new Plot2DPanel();
			panel.setBackground(Color.white);
			panel.setOpaque(true);
			panel.setSize(400, 300);
			panel.setShowNodes(nodes);
			panel.setIsHistogram(histogram);
			PlotData[] data = new PlotData[count];
			String[] names = new String[count];
			boolean[] visible = new boolean[count];
			int[] renderHints = new int[count];
			double[] x = new double[samples];
			for (int sample = 0; sample < samples; sample++) {
				x[sample] = sample;
			}
			for (int series = 0; series < count; series++) {
				double[] y = new double[samples];
				for (int sample = 0; sample < samples; sample++) {
					y[sample] = series + 1;
				}
				data[series] = new PlotData(x, y);
				names[series] = "s" + series;
				visible[series] = true;
				renderHints[series] = hints;
			}
			panel.setPlot2D(new Plot2D(null, null, names, data, new String[]{"t", "x", "y"}, visible, renderHints));
			panel.setXAuto(false);
			panel.setYAuto(false);
			panel.setXManualRange(new Range(0, Math.max(1, samples - 1)));
			panel.setYManualRange(new Range(0, count + 1));
			holder[0] = panel;
		});
		return holder[0];
	}

	private static BufferedImage paint(Plot2DPanel panel) throws Exception {
		final BufferedImage[] image = new BufferedImage[1];
		SwingUtilities.invokeAndWait(() -> {
			image[0] = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = image[0].createGraphics();
			graphics.setColor(Color.white);
			graphics.fillRect(0, 0, image[0].getWidth(), image[0].getHeight());
			panel.paint(graphics);
			graphics.dispose();
		});
		return image[0];
	}

	private interface PixelTest {
		boolean matches(int rgb);
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
		return best >= 8 ? bestRow : -1;
	}

	private static int longestInteriorGap(BufferedImage image, int row, PixelTest ink) {
		int[] gaps = interiorGaps(image, row, ink);
		int longest = 0;
		for (int gap : gaps) {
			longest = Math.max(longest, gap);
		}
		return longest;
	}

	private static int[] interiorGaps(BufferedImage image, int row, PixelTest ink) {
		int first = -1;
		int last = -1;
		for (int x = 0; x < image.getWidth(); x++) {
			if (bandMatches(image, row, x, ink)) {
				if (first < 0) {
					first = x;
				}
				last = x;
			}
		}
		if (first < 0) {
			return new int[0];
		}
		int[] found = new int[256];
		int n = 0;
		int gap = 0;
		for (int x = first; x <= last; x++) {
			if (bandMatches(image, row, x, ink)) {
				if (gap > 0 && n < found.length) {
					found[n++] = gap;
				}
				gap = 0;
			} else if (isBackground(image.getRGB(x, row))) {
				gap++;
			} else if (gap > 0 && n < found.length) {
				found[n++] = gap;
				gap = 0;
			}
		}
		int[] result = new int[n];
		System.arraycopy(found, 0, result, 0, n);
		return result;
	}

	private static boolean isBackground(int rgb) {
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		return r > 245 && g > 245 && b > 245;
	}

	private static boolean bandMatches(BufferedImage image, int row, int x, PixelTest ink) {
		int y0 = Math.max(0, row - 2);
		int y1 = Math.min(image.getHeight() - 1, row + 2);
		for (int y = y0; y <= y1; y++) {
			if (ink.matches(image.getRGB(x, y))) {
				return true;
			}
		}
		return false;
	}

	private static int countInk(BufferedImage image) {
		int count = 0;
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				if ((image.getRGB(x, y) & 0xffffff) != 0xffffff) {
					count++;
				}
			}
		}
		return count;
	}
}
