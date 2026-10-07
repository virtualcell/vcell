package cbit.vcell.microscopy.gui.defineROIwizard;

import cbit.image.DisplayAdapterService;
import cbit.vcell.client.constants.GuiConstants;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class DefineRoiStatusTextTest {

	@Test
	public void recoveryIndexMessageUsesErrorInkOnThePanel() {
		DefineROI_SummaryPanel panel = new DefineROI_SummaryPanel();
		JLabel label = panel.startIndexStatusLabel();
		assertEquals(GuiConstants.ERROR_TEXT_COLOR, label.getForeground());
		assertTrue(DefineROI_SummaryPanel.START_IDX_UNAVAILABLE_STR.toLowerCase().contains("required"));
		double ratio = DisplayAdapterService.contrastRatio(label.getForeground().getRGB(), label.getParent().getBackground().getRGB());
		assertTrue(ratio >= 4.5, ratio + ":1");
	}
}
