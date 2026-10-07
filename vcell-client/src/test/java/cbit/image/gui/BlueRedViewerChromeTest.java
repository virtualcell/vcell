package cbit.image.gui;

import cbit.image.DisplayAdapterService;
import cbit.image.SourceDataInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.Extent;
import org.vcell.util.Origin;
import org.vcell.util.Range;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class BlueRedViewerChromeTest {

	private static final double MIN_TEXT = 4.5;
	private static final double MIN_INDICATOR = 3.0;

	@Test
	public void blueRedLegendLabelsAndScaleTextClearContrast() throws Exception {
		DisplayAdapterService service = new DisplayAdapterService();
		DisplayAdapterService.addStandardColorModels(service);
		service.setActiveColorModelID(DisplayAdapterService.BLUERED);
		service.setValueDomain(new Range(0, 10));
		service.setActiveScaleRange(new Range(0, 10));
		assertEquals(DisplayAdapterService.BLUERED, service.getActiveColorModelID());

		DisplayAdapterServicePanel panel = new DisplayAdapterServicePanel();
		panel.setDisplayAdapterService(service);

		int[] legend = new int[] {
				DisplayAdapterService.BELOW_MIN_COLOR_OFFSET,
				DisplayAdapterService.ABOVE_MAX_COLOR_OFFSET,
				DisplayAdapterService.NAN_COLOR_OFFSET,
				DisplayAdapterService.NOT_IN_DOMAIN_COLOR_OFFSET,
				DisplayAdapterService.NO_RANGE_COLOR_OFFSET
		};
		String[] codes = new String[] { "BM", "AM", "NN", "ND", "NR" };
		Color gap = panel.legendGapColor();
		for (int i = 0; i < legend.length; i++) {
			JLabel swatch = panel.specialStateSwatch(legend[i]);
			String longName = DisplayAdapterService.specialStateLabel(legend[i]);
			assertTrue(swatch.getText().startsWith(codes[i] + " "), swatch.getText());
			assertTrue(swatch.getText().contains(longName), swatch.getText());
			assertTrue(contrast(swatch.getForeground(), swatch.getBackground()) >= MIN_TEXT, swatch.getText());
			assertTrue(contrast(swatch.getBackground(), gap) >= MIN_INDICATOR, swatch.getText());
		}
		for (JComponent readout : panel.scaleReadouts()) {
			assertTrue(contrast(readout.getForeground(), readout.getBackground()) >= MIN_TEXT, readout.getName());
		}
		assertTrue(containsText(panel, "BlueRed selected"));
		writePng(panel, "bluered-legend.png");
		writeSwatches(panel, "special-states.png");
	}

	@Test
	public void sliceSelectionAndFocusAreMarkedWithoutHue() {
		ImagePlanePanel panel = new ImagePlanePanel();
		JRadioButton axis = (JRadioButton) find(panel, "ZAxisCheckbox");
		axis.setEnabled(true);
		axis.doClick();

		assertTrue(axis.getText().endsWith(" selected"), axis.getText());
		assertEquals(Color.BLACK, axis.getForeground());
		assertEquals(Color.WHITE, axis.getBackground());
		LineBorder border = assertInstanceOf(LineBorder.class, axis.getBorder());
		assertEquals(Color.BLACK, border.getLineColor());
		assertEquals(2, border.getThickness());

		JLabel slice = (JLabel) find(panel, "SliceLabel");
		assertEquals(Color.BLACK, slice.getForeground());
		assertEquals(Color.WHITE, slice.getBackground());
		assertTrue(contrast(slice.getForeground(), slice.getBackground()) >= MIN_TEXT);

		JSlider slider = (JSlider) find(panel, "SliceSlider");
		for (FocusListener listener : slider.getFocusListeners()) {
			listener.focusGained(new FocusEvent(slider, FocusEvent.FOCUS_GAINED));
		}
		LineBorder focus = assertInstanceOf(LineBorder.class, slider.getBorder());
		assertEquals(Color.BLACK, focus.getLineColor());
		assertEquals(3, focus.getThickness());
		enableTree(panel);
		writePng(panel, "slice-controls.png");
	}

	@Test
	public void keyboardReadsTheSameNumericValueAsThePointer() {
		ImagePlaneManagerPanel panel = new ImagePlaneManagerPanel();
		byte[] pixels = new byte[4 * 4];
		Arrays.fill(pixels, (byte) 7);
		pixels[1] = 9;
		SourceDataInfo info = new SourceDataInfo(SourceDataInfo.INDEX_TYPE, pixels,
				new Extent(4, 4, 1), new Origin(0, 0, 0), null,
				0, 4, 1, 4, 4, 1, 0);
		panel.setSourceDataInfo(info);

		assertTrue(panel.sampleArrowKeysInstalled());
		panel.moveKeyboardSample(1, 0);
		String keyboard = panel.getInfoJlabel().getText();
		assertTrue(keyboard.contains(info.getDataValueAsString(1, 0, 0)), keyboard);

		panel.setSourceDataInfo(indexPlane((byte) 7));
		panel.updateInfo(new java.awt.event.MouseEvent(panel, java.awt.event.MouseEvent.MOUSE_MOVED, 0L, 0, 2, 2, 1, false));
		String pointer = panel.getInfoJlabel().getText();
		panel.moveKeyboardSample(0, 0);
		String sameCell = panel.getInfoJlabel().getText();
		assertTrue(pointer.contains("Index = 7"), pointer);
		assertTrue(sameCell.contains("Index = 7"), sameCell);
	}

	private static SourceDataInfo indexPlane(byte handle) {
		byte[] pixels = new byte[4 * 4];
		Arrays.fill(pixels, handle);
		return new SourceDataInfo(SourceDataInfo.INDEX_TYPE, pixels,
				new Extent(4, 4, 1), new Origin(0, 0, 0), null,
				0, 4, 1, 4, 4, 1, 0);
	}

	private static double contrast(Color a, Color b) {
		return DisplayAdapterService.contrastRatio(a.getRGB(), b.getRGB());
	}

	private static void writeSwatches(DisplayAdapterServicePanel panel, String name) {
		javax.swing.JPanel row = new javax.swing.JPanel(new java.awt.GridLayout(1, 5, 6, 0));
		row.setBackground(Color.BLACK);
		row.setOpaque(true);
		int[] legend = new int[] {
				DisplayAdapterService.BELOW_MIN_COLOR_OFFSET,
				DisplayAdapterService.ABOVE_MAX_COLOR_OFFSET,
				DisplayAdapterService.NAN_COLOR_OFFSET,
				DisplayAdapterService.NOT_IN_DOMAIN_COLOR_OFFSET,
				DisplayAdapterService.NO_RANGE_COLOR_OFFSET
		};
		for (int offset : legend) {
			JLabel source = panel.specialStateSwatch(offset);
			JLabel copy = new JLabel(source.getText());
			copy.setOpaque(true);
			copy.setBackground(source.getBackground());
			copy.setForeground(source.getForeground());
			copy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			row.add(copy);
		}
		row.setSize(980, 52);
		row.doLayout();
		writePng(row, name);
	}

	private static void enableTree(Component component) {
		component.setEnabled(true);
		if (component instanceof Container) {
			for (Component child : ((Container) component).getComponents()) {
				enableTree(child);
			}
		}
	}

	private static void writePng(JComponent component, String name) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			frame.getContentPane().add(component);
			frame.pack();
			int width = Math.max(component.getWidth(), component.getPreferredSize().width);
			int height = Math.max(component.getHeight(), component.getPreferredSize().height);
			if (width < 40) {
				width = 360;
			}
			if (height < 40) {
				height = 200;
			}
			component.setSize(width, height);
			component.validate();
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = image.createGraphics();
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, width, height);
			component.printAll(graphics);
			graphics.dispose();
			frame.dispose();
			File dir = new File("target/r1-bluered");
			if (!dir.isDirectory() && !dir.mkdirs()) {
				throw new IllegalStateException("could not create " + dir);
			}
			ImageIO.write(image, "png", new File(dir, name));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static boolean containsText(Component component, String text) {
		if (component instanceof javax.swing.AbstractButton && text.equals(((javax.swing.AbstractButton) component).getText())) {
			return true;
		}
		if (component instanceof Container) {
			for (Component child : ((Container) component).getComponents()) {
				if (containsText(child, text)) {
					return true;
				}
			}
		}
		return false;
	}

	private static Component find(Container root, String name) {
		if (name.equals(root.getName())) {
			return root;
		}
		for (Component child : root.getComponents()) {
			if (child instanceof Container) {
				Component found = find((Container) child, name);
				if (found != null) {
					return found;
				}
			} else if (name.equals(child.getName())) {
				return child;
			}
		}
		return null;
	}
}
