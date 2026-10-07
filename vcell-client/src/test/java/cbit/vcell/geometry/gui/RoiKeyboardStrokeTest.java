package cbit.vcell.geometry.gui;

import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * One paint stroke through the keyboard path and the same stroke through the mouse path leave
 * the same ROI pixel buffer: the arrow keys call the same drawHighlight/drawPaint the mouse
 * calls, with the same brush size, so the encoding written back to the region cannot differ.
 */
@Tag("Fast")
public class RoiKeyboardStrokeTest {

	private static final int SIZE = 64;
	// BrushToolHelper's default radius is 10 and the test runs at zoom 1, so both paths paint
	// with (int)(10 * 2 / 1.0); the keyboard step is the same call the mouse drag makes.
	private static final int STEP = 20;

	@BeforeAll
	static void headless() {
		System.setProperty("java.awt.headless", "true");
	}

	@Test
	public void keyboardStrokeMatchesMouseStroke() throws Exception {
		OverlayEditorPanelJAI keyboardPanel = newEditor();
		OverlayEditorPanelJAI mousePanel = newEditor();

		// Keyboard: two RIGHT arrows from the default origin — a dot, then a one-step segment.
		JComponent keyboardPane = imagePane(keyboardPanel);
		AbstractAction right =
				(AbstractAction) keyboardPane.getActionMap().get("roiKey" + KeyEvent.VK_RIGHT);
		assertNotNull(right, "no keyboard paint binding on the domain image");
		right.actionPerformed(null);
		right.actionPerformed(null);

		// Mouse: the same stroke — the press paints the dot, the drag paints the segment.
		JComponent mousePane = imagePane(mousePanel);
		mousePane.dispatchEvent(new MouseEvent(mousePane, MouseEvent.MOUSE_PRESSED,
				System.currentTimeMillis(), 0, STEP, STEP, 1, false, MouseEvent.BUTTON1));
		mousePane.dispatchEvent(new MouseEvent(mousePane, MouseEvent.MOUSE_DRAGGED,
				System.currentTimeMillis(), InputEvent.BUTTON1_DOWN_MASK,
				2 * STEP, STEP, 1, false, MouseEvent.NOBUTTON));

		byte[] keyboardBytes = roiBytes(keyboardPanel);
		byte[] mouseBytes = roiBytes(mousePanel);
		assertTrue(anyNonZero(keyboardBytes), "keyboard stroke painted nothing");
		assertTrue(anyNonZero(mouseBytes), "mouse stroke painted nothing");
		assertArrayEquals(mouseBytes, keyboardBytes,
				"keyboard and mouse strokes wrote different ROI pixels");
	}

	@Test
	public void regionIsCreatedAndSelectedByName() throws Exception {
		OverlayEditorPanelJAI panel = newEditor();
		assertEquals("cytosol", panel.getCurrentROIInfo().getROIName());
		assertTrue(panel.regionSelectionReadout().startsWith("Domain Regions"));
	}

	private static OverlayEditorPanelJAI newEditor() throws Exception {
		UShortImage image = new UShortImage(new short[SIZE * SIZE], null, null, SIZE, SIZE, 1);
		ImageDataset dataset = new ImageDataset(new UShortImage[] { image }, new double[] { 0.0 }, 1);
		OverlayEditorPanelJAI panel = new OverlayEditorPanelJAI();
		panel.setImages(dataset, 1.0, 0.0, new OverlayEditorPanelJAI.AllPixelValuesRange(0, 65535));
		BufferedImage composite = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_BYTE_INDEXED,
				ROIMultiPaintManager.getContrastIndexColorModel());
		panel.setAllROICompositeImage(new BufferedImage[] { composite }, "test");
		panel.addROIName("cytosol", false, "cytosol", true, 1);
		return panel;
	}

	private static JComponent imagePane(OverlayEditorPanelJAI panel) {
		return (JComponent) findByAccessibleName(panel, "Domain image");
	}

	private static byte[] roiBytes(OverlayEditorPanelJAI panel) {
		OverlayImageDisplayJAI pane = (OverlayImageDisplayJAI) imagePane(panel);
		return ((DataBufferByte) pane.getAllROICompositeImage().getData().getDataBuffer()).getData();
	}

	private static boolean anyNonZero(byte[] bytes) {
		for (byte b : bytes) {
			if (b != 0) {
				return true;
			}
		}
		return false;
	}

	private static Component findByAccessibleName(Container parent, String accessibleName) {
		for (Component child : parent.getComponents()) {
			if (child.getAccessibleContext() != null
					&& accessibleName.equals(child.getAccessibleContext().getAccessibleName())) {
				return child;
			}
			if (child instanceof Container) {
				Component found = findByAccessibleName((Container) child, accessibleName);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}
}
