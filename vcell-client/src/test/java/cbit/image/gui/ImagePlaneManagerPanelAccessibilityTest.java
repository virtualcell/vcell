package cbit.image.gui;

import cbit.image.DisplayAdapterService;
import cbit.image.SourceDataInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.Extent;
import org.vcell.util.Origin;

import javax.swing.JList;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class ImagePlaneManagerPanelAccessibilityTest {

	@Test
	public void indexHoverNamesTheSubvolume_8_3_g() {
		ImagePlaneManagerPanel panel = new ImagePlaneManagerPanel();
		int handle = 3;
		panel.setSourceDataInfo(indexPlane(handle));
		panel.setIndexLabelProvider(pixelHandle -> pixelHandle == handle ? "cytosol" : null);

		panel.updateInfo(movedAt(panel, 2, 2));

		String text = panel.getInfoJlabel().getText();
		assertTrue(text.contains("\"cytosol\""), text);
	}

	@Test
	public void regionListNamesEverySubvolumeWithoutThePointer() {
		ImagePlaneManagerPanel panel = new ImagePlaneManagerPanel();
		List<String> names = List.of("extracellular", "cytosol", "nucleus");
		panel.setRegionNames(names);

		JList<String> regions = panel.getRegionList();
		assertEquals(names.size(), regions.getModel().getSize());
		for (int i = 0; i < names.size(); i++) {
			regions.setSelectedIndex(i);
			assertEquals(names.get(i), regions.getSelectedValue());
			assertTrue(panel.getInfoJlabel().getText().contains(names.get(i)), panel.getInfoJlabel().getText());
		}
	}

	@Test
	public void adjacentRegionFillsStayBelowNonTextContrastSoTheNameIsTheCue() {
		int[] colors = DisplayAdapterService.createContrastColorModel();
		double firstPair = DisplayAdapterService.contrastRatio(colors[0], colors[1]);
		double secondPair = DisplayAdapterService.contrastRatio(colors[1], colors[2]);
		assertEquals(1.51, firstPair, 0.05);
		assertEquals(1.71, secondPair, 0.05);
	}

	@Test
	public void missingProviderLeavesTheIndexReadoutUnnamed() {
		ImagePlaneManagerPanel panel = new ImagePlaneManagerPanel();
		panel.setSourceDataInfo(indexPlane(3));

		panel.updateInfo(movedAt(panel, 2, 2));

		assertFalse(panel.getInfoJlabel().getText().contains("\"cytosol\""));
	}

	private static SourceDataInfo indexPlane(int handle) {
		byte[] pixels = new byte[4 * 4];
		Arrays.fill(pixels, (byte) handle);
		return new SourceDataInfo(SourceDataInfo.INDEX_TYPE, pixels,
				new Extent(4, 4, 1), new Origin(0, 0, 0), null,
				0, 4, 1, 4, 4, 1, 0);
	}

	private static MouseEvent movedAt(ImagePlaneManagerPanel panel, int x, int y) {
		return new MouseEvent(panel, MouseEvent.MOUSE_MOVED, 0L, 0, x, y, 1, false);
	}
}
