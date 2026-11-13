package org.vcell.util.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.GraphicsEnvironment;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Fast")
public class ScrollTableSizingTest {

	@Test
	public void widthsAreCappedAndMinimumsKeptReasonable() {
		assertEquals(120, ScrollTable.measuredWidth(120, false));
		assertEquals(ScrollTable.MAX_MEASURED_WIDTH, ScrollTable.measuredWidth(5000, false), "one long value doesn't take the table");
		assertEquals(ScrollTable.MAX_MEASURED_EXPRESSION_WIDTH, ScrollTable.measuredWidth(5000, true));
		assertEquals(ScrollTable.MIN_COLUMN_WIDTH, ScrollTable.minimumWidth(10));
		assertEquals(90, ScrollTable.minimumWidth(90), "about its header's width");
		assertEquals(ScrollTable.MAX_MIN_COLUMN_WIDTH, ScrollTable.minimumWidth(900), "a long header doesn't make it unreasonable");
	}

	private ScrollTable table() {
		ScrollTable table = new ScrollTable();
		DefaultTableModel model = new DefaultTableModel(new Object[] { "Name", "A rather long description header", "Units" }, 0);
		model.addRow(new Object[] { "Kf", "forward rate constant", "s-1" });
		table.setModel(model);
		return table;
	}

	@Test
	public void fillsTheWidthUntilColumnsWouldBeSqueezed() throws Exception {
		Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "needs a display to measure");
		SwingUtilities.invokeAndWait(() -> {
			ScrollTable table = this.table();
			assertEquals(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS, table.getAutoResizeMode(), "fills the width by default");
			table.fitColumnsAndRows();
			int minimum = 0;
			for (int i = 0; i < table.getColumnCount(); i++) minimum += table.getColumnModel().getColumn(i).getMinWidth();
			table.getParent().setSize(minimum + 100, 200); // the viewport, as the scroll pane would size it on screen
			assertTrue(table.getScrollableTracksViewportWidth(), "wide enough: the columns fill it");
			table.getParent().setSize(minimum / 2, 200);
			assertFalse(table.getScrollableTracksViewportWidth(), "too narrow: it scrolls sideways instead");
		});
	}

	@Test
	public void aColumnIsMeasuredOnceAndKeepsItsWidthAfter() throws Exception {
		Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "needs a display to measure");
		SwingUtilities.invokeAndWait(() -> {
			ScrollTable table = this.table();
			table.fitColumnsAndRows();
			TableColumn name = table.getColumnModel().getColumn(0);
			int measured = name.getPreferredWidth();
			assertTrue(measured >= name.getMinWidth());
			name.setPreferredWidth(measured + 77); // as if chosen
			((DefaultTableModel) table.getModel()).addRow(new Object[] { "a much longer parameter name than before", "x", "y" });
			table.fitColumnsAndRows();
			assertEquals(measured + 77, name.getPreferredWidth(), "not measured again when rows change");
		});
	}
}
