package cbit.plot.gui;

import cbit.plot.Plot2D;
import cbit.plot.PlotData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class PlotPaneAccessibilityTest {

	@Test
	public void legendIconUsesSeriesStrokeAndClickUsesRawName() throws Exception {
		AtomicReference<PlotPane> paneRef = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() -> paneRef.set(new PlotPane()));
		PlotPane pane = paneRef.get();
		SwingUtilities.invokeAndWait(() -> pane.setPlot2D(samplePlot()));
		SwingUtilities.invokeAndWait(() -> {
			List<JLabel> labels = new ArrayList<>();
			collectLabels(pane, labels);
			JLabel seriesTwo = null;
			boolean sawStyledIcon = false;
			for (JLabel label : labels) {
				if ("s2".equals(label.getClientProperty("plotName"))) {
					seriesTwo = label;
				}
				if (label.getIcon() instanceof PlotPane.LineIcon) {
					PlotPane.LineIcon icon = (PlotPane.LineIcon) label.getIcon();
					assertTrue(icon.getIconWidth() >= 50);
					assertTrue(icon.getIconHeight() >= 12);
					assertEquals(pane.accessiblePlotPanel().getVisiblePlotStroke(icon.markerIndex()), icon.stroke());
					sawStyledIcon = true;
				}
			}
			assertTrue(sawStyledIcon);
			assertTrue(seriesTwo != null);
			seriesTwo.dispatchEvent(new MouseEvent(seriesTwo, MouseEvent.MOUSE_CLICKED, 0, 0, 1, 1, 1, false));
			assertEquals(2, pane.accessiblePlotPanel().getCurrentPlotIndex());
		});
	}

	@Test
	public void seriesStyleEventFiresWhenStylesChange() throws Exception {
		AtomicReference<PlotPane> paneRef = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() -> paneRef.set(new PlotPane()));
		PlotPane pane = paneRef.get();
		SwingUtilities.invokeAndWait(() -> pane.setPlot2D(samplePlot()));
		AtomicReference<Boolean> fired = new AtomicReference<>(false);
		pane.addPropertyChangeListener("seriesStyle", event -> fired.set(true));
		SwingUtilities.invokeAndWait(() -> pane.accessiblePlotPanel().setVaryLineStyles(false));
		assertTrue(fired.get());
	}

	private static Plot2D samplePlot() {
		PlotData[] data = new PlotData[3];
		String[] names = new String[3];
		boolean[] visible = new boolean[3];
		int[] hints = new int[3];
		for (int i = 0; i < 3; i++) {
			data[i] = new PlotData(new double[]{0, 1}, new double[]{i, i});
			names[i] = "s" + i;
			visible[i] = true;
			hints[i] = Plot2D.RENDERHINT_DRAWLINE;
		}
		return new Plot2D(null, null, names, data, new String[]{"title", "x", "y"}, visible, hints);
	}

	private static void collectLabels(Container container, List<JLabel> labels) {
		for (Component component : container.getComponents()) {
			if (component instanceof JLabel) {
				labels.add((JLabel) component);
			}
			if (component instanceof Container) {
				collectLabels((Container) component, labels);
			}
		}
	}
}
