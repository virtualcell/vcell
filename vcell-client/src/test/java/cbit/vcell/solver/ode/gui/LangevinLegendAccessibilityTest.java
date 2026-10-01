package cbit.vcell.solver.ode.gui;

import cbit.plot.gui.MoleculePlotPanel;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class LangevinLegendAccessibilityTest {

	@Test
	public void legendIconDrawsSeriesDash() {
		Harness harness = new Harness();
		Icon solid = harness.new LineIcon(Color.BLACK, 0);
		Icon dashed = harness.new LineIcon(Color.BLACK, 1);
		assertEquals(80, dashed.getIconWidth());
		assertTrue(dashed.getIconHeight() >= 12);
		assertEquals(0, longestGap(paint(solid)));
		int gap = longestGap(paint(dashed));
		assertTrue(gap > 0 && gap <= 12);
	}

	@Test
	public void legendKeyboardSelectsSeriesByName() {
		Harness harness = new Harness();
		double[] time = {0, 1, 2, 3, 4};
		harness.plot.addAvgRenderer(time, new double[] {1, 1, 1, 1, 1}, Color.BLACK, "s0", "AVG", 0);
		harness.plot.addAvgRenderer(time, new double[] {2, 2, 2, 2, 2}, Color.BLACK, "s2", "AVG", 2);
		JLabel seriesTwo = new JLabel("s2");
		harness.bindLegendSeries(seriesTwo, "s2", () -> harness.plot.selectSeries("s2"));
		assertEquals("s2", seriesTwo.getAccessibleContext().getAccessibleName());
		assertTrue(seriesTwo.isFocusable());
		seriesTwo.getKeyListeners()[0].keyPressed(new KeyEvent(seriesTwo, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER, '\n'));
		assertEquals("s2", harness.plot.seriesStatusText());
		harness.plot.selectSeries("s0");
		seriesTwo.getKeyListeners()[0].keyPressed(new KeyEvent(seriesTwo, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_SPACE, ' '));
		assertEquals("s2", harness.plot.seriesStatusText());
	}

	private static BufferedImage paint(Icon icon) {
		BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		icon.paintIcon(null, graphics, 0, 0);
		graphics.dispose();
		return image;
	}

	private static int longestGap(BufferedImage image) {
		int best = 0;
		for (int y = 0; y < image.getHeight(); y++) {
			int run = 0;
			boolean seen = false;
			for (int x = 0; x < image.getWidth(); x++) {
				int rgb = image.getRGB(x, y);
				int r = (rgb >> 16) & 0xff;
				int g = (rgb >> 8) & 0xff;
				int b = rgb & 0xff;
				boolean dark = r < 80 && g < 80 && b < 80;
				boolean light = r > 220 && g > 220 && b > 220;
				if (dark) {
					if (seen) {
						best = Math.max(best, run);
					}
					seen = true;
					run = 0;
				} else if (seen && light) {
					run++;
				}
			}
		}
		return best;
	}

	private static final class Harness extends AbstractVisualizationPanel {
		final MoleculePlotPanel plot = new MoleculePlotPanel();

		Harness() {
			initialize();
		}

		@Override
		protected JPanel createPlotPanel() {
			return plot;
		}

		@Override
		protected JPanel createDataPanel() {
			return new JPanel();
		}
	}
}
