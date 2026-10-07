package cbit.vcell.microscopy.gui.estparamwizard;

import cbit.image.DisplayAdapterService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import java.awt.Color;
import java.awt.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The FRAP analysis table already says NOT IDENTIFIABLE. The ink on the pink row has to clear 4.5:1.
 */
@Tag("Fast")
public class AnalysisTableContrastTest {

	private static final Color MAC_SELECTION = new Color(8, 74, 217);
	private static final Color HOVER_PAPER = new Color(0xFD, 0xFC, 0xDC);

	@Test
	public void notIdentifiableTextClearsPinkWhiteAndTheSelectionPair() {
		JTable table = tableWithUnidentifiableDiffOne();
		AnalysisTableRenderer renderer = new AnalysisTableRenderer(2);
		int ink = AnalysisTableRenderer.NOT_IDENTIFIABLE_TEXT.getRGB();
		int pink = AnalysisTableRenderer.NOT_IDENTIFIABLE_PINK.getRGB();

		JLabel resting = (JLabel) renderer.getTableCellRendererComponent(
				table, AnalysisTableModel.STR_NOT_SIGNIFICANT, false, false,
				AnalysisTableModel.INDEX_MODEL_SIGNIFICANCE, AnalysisTableModel.COLUMN_DIFF_ONE_PARAMETER_VAL);
		assertEquals(AnalysisTableModel.STR_NOT_SIGNIFICANT, resting.getText());
		assertEquals(AnalysisTableRenderer.NOT_IDENTIFIABLE_TEXT, resting.getForeground());
		assertEquals(AnalysisTableRenderer.NOT_IDENTIFIABLE_PINK, resting.getBackground());
		assertTrue(DisplayAdapterService.contrastRatio(ink, pink) >= 4.5, "pink " + DisplayAdapterService.contrastRatio(ink, pink));
		assertTrue(DisplayAdapterService.contrastRatio(ink, Color.WHITE.getRGB()) >= 4.5);
		assertTrue(DisplayAdapterService.contrastRatio(ink, HOVER_PAPER.getRGB()) >= 4.5,
				"hover paper " + DisplayAdapterService.contrastRatio(ink, HOVER_PAPER.getRGB()));

		JLabel onWhite = (JLabel) renderer.getTableCellRendererComponent(
				table, AnalysisTableModel.STR_NOT_SIGNIFICANT, false, false, 0, AnalysisTableModel.COLUMN_PARAM_NAME);
		assertEquals(AnalysisTableModel.STR_NOT_SIGNIFICANT, onWhite.getText());
		assertEquals(AnalysisTableRenderer.NOT_IDENTIFIABLE_TEXT, onWhite.getForeground());
		assertTrue(DisplayAdapterService.contrastRatio(onWhite.getForeground().getRGB(), onWhite.getBackground().getRGB()) >= 4.5);

		JLabel selected = (JLabel) renderer.getTableCellRendererComponent(
				table, AnalysisTableModel.STR_NOT_SIGNIFICANT, true, false,
				AnalysisTableModel.INDEX_MODEL_SIGNIFICANCE, AnalysisTableModel.COLUMN_DIFF_ONE_PARAMETER_VAL);
		assertEquals(AnalysisTableModel.STR_NOT_SIGNIFICANT, selected.getText());
		assertEquals(Color.WHITE, selected.getForeground());
		assertEquals(MAC_SELECTION, selected.getBackground());
		assertNotEquals(AnalysisTableRenderer.NOT_IDENTIFIABLE_TEXT, selected.getForeground());
		assertTrue(DisplayAdapterService.contrastRatio(selected.getForeground().getRGB(), selected.getBackground().getRGB()) >= 4.5);
	}

	@Test
	public void parameterCellOnANotIdentifiableModelKeepsItsNumberAndThePink() {
		JTable table = tableWithUnidentifiableDiffOne();
		AnalysisTableRenderer renderer = new AnalysisTableRenderer(2);
		Component cell = renderer.getTableCellRendererComponent(table, 1.25, false, false, 0,
				AnalysisTableModel.COLUMN_DIFF_ONE_PARAMETER_VAL);
		assertTrue(cell instanceof JLabel);
		JLabel label = (JLabel) cell;
		assertEquals("1.25", label.getText());
		assertEquals(AnalysisTableRenderer.NOT_IDENTIFIABLE_PINK, label.getBackground());
		assertTrue(DisplayAdapterService.contrastRatio(label.getForeground().getRGB(), label.getBackground().getRGB()) >= 4.5);
	}

	private static JTable tableWithUnidentifiableDiffOne() {
		Object[][] data = new Object[AnalysisTableModel.NUM_ROWS][AnalysisTableModel.NUM_COLUMNS];
		data[AnalysisTableModel.INDEX_MODEL_SIGNIFICANCE][AnalysisTableModel.COLUMN_DIFF_ONE_PARAMETER_VAL] =
				AnalysisTableModel.STR_NOT_SIGNIFICANT;
		data[0][AnalysisTableModel.COLUMN_PARAM_NAME] = AnalysisTableModel.STR_NOT_SIGNIFICANT;
		JTable table = new JTable(new AbstractTableModel() {
			@Override public int getRowCount() { return data.length; }
			@Override public int getColumnCount() { return data[0].length; }
			@Override public Object getValueAt(int row, int column) { return data[row][column]; }
		});
		table.setBackground(Color.WHITE);
		table.setSelectionBackground(MAC_SELECTION);
		table.setSelectionForeground(Color.WHITE);
		return table;
	}
}
