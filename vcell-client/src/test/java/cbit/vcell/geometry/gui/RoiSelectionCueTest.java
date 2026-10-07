package cbit.vcell.geometry.gui;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.KeyStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A region is named from the list. The edge is a black/white pattern and does not rewrite ROI pixels.
 */
@Tag("Fast")
public class RoiSelectionCueTest {

	@BeforeAll
	static void headless() {
		System.setProperty("java.awt.headless", "true");
	}

	@Test
	public void boundaryIsTheOutlineAndDoesNotChangeTheMask() {
		int width = 5;
		int height = 5;
		byte[] mask = new byte[width * height];
		byte[] original = new byte[mask.length];
		for (int y = 1; y <= 3; y++) {
			for (int x = 1; x <= 3; x++) {
				mask[y * width + x] = 1;
			}
		}
		System.arraycopy(mask, 0, original, 0, mask.length);

		assertTrue(RoiSelectionCue.isBoundary(mask, width, height, 1, 1));
		assertFalse(RoiSelectionCue.isBoundary(mask, width, height, 2, 2));
		assertFalse(RoiSelectionCue.isBoundary(mask, width, height, 0, 0));
		assertEquals(0x000000, RoiSelectionCue.boundaryRgb(0, 0));
		assertEquals(0xFFFFFF, RoiSelectionCue.boundaryRgb(1, 0));
		assertArrayEquals(original, mask);
	}

	@Test
	public void selectionSentenceNamesTheRegion() {
		assertEquals("Selected: none", RoiSelectionCue.selectedRegionSentence(Collections.<String>emptyList()));
		assertEquals("Selected: cytosol", RoiSelectionCue.selectedRegionSentence(List.of("cytosol")));
		assertEquals("Domain Regions. Selected: cytosol, nucleus",
				RoiSelectionCue.withListTitle("Domain Regions", Arrays.asList("cytosol", "nucleus")));
	}

	@Test
	public void editorNamesTheActiveDomainAndKeepsKeyboardEdit() {
		OverlayEditorPanelJAI panel = new OverlayEditorPanelJAI();
		assertEquals("Domain Regions. Selected: none", panel.regionSelectionReadout());
		panel.addROIName("cytosol", false, "cytosol", true, 1);
		assertEquals("cytosol", panel.getCurrentROIInfo().getROIName());

		JList list = (JList) findNamed(panel, "DomainRegionsList");
		assertNotNull(list);
		assertEquals("Domain Regions", list.getAccessibleContext().getAccessibleName());
		assertNotNull(list.getInputMap(JComponent.WHEN_FOCUSED).get(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0)));

		Component image = findByAccessibleName(panel, "Domain image");
		assertNotNull(image);
		assertTrue(image.isFocusable());
		assertNotNull(((JComponent) image).getInputMap(JComponent.WHEN_FOCUSED)
				.get(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0)));
		assertNotNull(findByAccessibleName(panel, "Paint"));
		assertNotNull(findNamed(panel, "roiAddBtn"));
	}

	private static Component findNamed(Container parent, String name) {
		for (Component child : parent.getComponents()) {
			if (name.equals(child.getName())) {
				return child;
			}
			if (child instanceof Container) {
				Component found = findNamed((Container) child, name);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
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
