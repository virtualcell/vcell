package cbit.vcell.client;

import cbit.gui.MultiPurposeTextPanel;
import cbit.image.DisplayAdapterService;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.testingframework.TestingFrmwkTreeModel;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.numericstest.gui.NumericsTestCellRenderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.awt.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * R4. Status text uses {@link GuiConstants#ERROR_TEXT_COLOR} or {@link GuiConstants#WARNING_TEXT_COLOR}
 * on white, the alternate row, and the hover row. A selected row keeps the look-and-feel
 * selection foreground, which is the ink that clears 4.5:1 on that background, and the
 * severity stays in the words.
 */
@Tag("Fast")
public class StatusTextContrastTest {

	private static final double MIN_TEXT = 4.5;

	@Test
	public void errorAndWarningInkClearTheUnselectedRowBackgrounds() {
		assertContrast(GuiConstants.ERROR_TEXT_COLOR, Color.WHITE, "error on white");
		assertContrast(GuiConstants.ERROR_TEXT_COLOR, DefaultScrollTableCellRenderer.everyOtherRowColor, "error on alternate row");
		assertContrast(GuiConstants.ERROR_TEXT_COLOR, DefaultScrollTableCellRenderer.hoverColor, "error on hover row");
		assertContrast(GuiConstants.WARNING_TEXT_COLOR, Color.WHITE, "warning on white");
		assertContrast(GuiConstants.WARNING_TEXT_COLOR, DefaultScrollTableCellRenderer.everyOtherRowColor, "warning on alternate row");
		assertContrast(GuiConstants.WARNING_TEXT_COLOR, DefaultScrollTableCellRenderer.hoverColor, "warning on hover row");
	}

	@Test
	public void changedNetworkConstraintSaysChangedOnEveryRowState() {
		DefaultScrollTableCellRenderer renderer = new DefaultScrollTableCellRenderer() {
			@Override
			protected boolean valueDiffersFromDefault(TableModel tableModel, Object value, int row, int column) {
				return true;
			}
		};
		DefaultTableModel model = new DefaultTableModel(new Object[][]{{"4", "3"}, {"4", "3"}}, new Object[]{"Value", "Default"});
		JTable table = new JTable(model);
		table.setEnabled(false);

		Component even = renderer.getTableCellRendererComponent(table, "4", false, false, 0, 0);
		assertEquals("4 changed", ((JLabel) even).getText());
		assertEquals(GuiConstants.WARNING_TEXT_COLOR, even.getForeground());
		assertContrast(even.getForeground(), even.getBackground(), "changed constraint even row");

		Component alternate = renderer.getTableCellRendererComponent(table, "4", false, false, 1, 0);
		assertEquals("4 changed", ((JLabel) alternate).getText());
		assertEquals(DefaultScrollTableCellRenderer.everyOtherRowColor, alternate.getBackground());
		assertContrast(alternate.getForeground(), alternate.getBackground(), "changed constraint alternate row");

		Component selected = renderer.getTableCellRendererComponent(table, "4", true, false, 0, 0);
		assertEquals("4 changed", ((JLabel) selected).getText());
		assertContrast(selected.getForeground(), selected.getBackground(), "changed constraint selected row");
	}

	@Test
	public void errorLineIsNamedOnAWhiteChip() {
		MultiPurposeTextPanel panel = new MultiPurposeTextPanel();
		MultiPurposeTextPanel.LineNumberPanel gutter = panel.getLineNumberPanel();
		gutter.setErrorLine(11);
		assertEquals("error 12", gutter.errorLineLabel());
		assertContrast(GuiConstants.ERROR_TEXT_COLOR, Color.WHITE, "error line on white chip");
	}

	@Test
	public void failedVariableNodeSaysFailed() {
		BioModelNode node = new BioModelNode("Ca_cyt");
		node.setRenderHint(TestingFrmwkTreeModel.FAILED_VARIABLE_MAE_MRE, Boolean.TRUE);
		NumericsTestCellRenderer renderer = new NumericsTestCellRenderer();
		JTree tree = new JTree();

		JLabel plain = (JLabel) renderer.getTreeCellRendererComponent(tree, node, false, false, true, 0, false);
		assertTrue(plain.getText().toLowerCase().contains("failed"), plain.getText());
		assertEquals(GuiConstants.ERROR_TEXT_COLOR, plain.getForeground());
		Color plainPaper = renderer.getBackgroundNonSelectionColor() == null
				? plain.getBackground() : renderer.getBackgroundNonSelectionColor();
		assertContrast(plain.getForeground(), plainPaper, "failed variable");

		JLabel selected = (JLabel) renderer.getTreeCellRendererComponent(tree, node, true, false, true, 0, false);
		assertTrue(selected.getText().toLowerCase().contains("failed"), selected.getText());
		assertContrast(selected.getForeground(), renderer.getBackgroundSelectionColor(), "failed variable selected");
	}

	private static void assertContrast(Color text, Color background, String label) {
		double ratio = DisplayAdapterService.contrastRatio(text.getRGB(), background.getRGB());
		assertTrue(ratio >= MIN_TEXT, label + " is " + String.format("%.2f", ratio) + ":1");
	}
}
