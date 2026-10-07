package cbit.vcell.mapping.gui;

import cbit.image.DisplayAdapterService;
import cbit.vcell.client.constants.GuiConstants;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.JTable;
import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class StructureMappingUnmappedTextTest {

	@Test
	public void unmappedKeepsTheWordAndTheSelectionInk() {
		StructureMappingTableRenderer renderer = new StructureMappingTableRenderer();
		JTable table = new JTable();
		renderer.getTableCellRendererComponent(table, "x", false, false, 0, 0);
		renderer.showUnmappedSubdomain(false);
		assertEquals("Unmapped", renderer.getText());
		assertEquals(GuiConstants.ERROR_TEXT_COLOR, renderer.getForeground());
		assertContrast(renderer.getForeground(), Color.WHITE);

		renderer.getTableCellRendererComponent(table, "x", true, false, 0, 0);
		Color selectionInk = renderer.getForeground();
		Color selectionPaper = renderer.getBackground();
		renderer.showUnmappedSubdomain(true);
		assertEquals("Unmapped", renderer.getText());
		assertEquals(selectionInk, renderer.getForeground());
		assertContrast(renderer.getForeground(), selectionPaper);
	}

	private static void assertContrast(Color text, Color background) {
		double ratio = DisplayAdapterService.contrastRatio(text.getRGB(), background.getRGB());
		assertTrue(ratio >= 4.5, ratio + ":1");
	}
}
