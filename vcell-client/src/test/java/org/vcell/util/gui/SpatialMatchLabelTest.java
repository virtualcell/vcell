package org.vcell.util.gui;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class SpatialMatchLabelTest {

	@Test
	public void matchRowKeepsTheWordWhenSelectedAndWhenYellowIsAbsent() {
		DefaultScrollTableCellRenderer renderer = new DefaultScrollTableCellRenderer() {
			@Override
			protected boolean isSpatialMatchRow(JTable table, int row) {
				return row == 0;
			}
		};
		JTable table = new JTable(new DefaultTableModel(
				new Object[][]{{"nucleus", "1"}, {"cytosol", "2"}},
				new Object[]{"Name", "Value"}));
		table.setSelectionBackground(new Color(8, 74, 217));
		table.setSelectionForeground(Color.WHITE);

		Component selected = renderer.getTableCellRendererComponent(table, "nucleus", true, false, 0, 0);
		assertTrue(((JLabel) selected).getText().endsWith(" match"), ((JLabel) selected).getText());
		assertEquals(table.getSelectionBackground(), selected.getBackground());
		assertEquals(table.getSelectionForeground(), selected.getForeground());

		Component selectedValue = renderer.getTableCellRendererComponent(table, "1", true, false, 0, 1);
		assertFalse(((JLabel) selectedValue).getText().endsWith(" match"), ((JLabel) selectedValue).getText());

		Component plain = renderer.getTableCellRendererComponent(table, "nucleus", false, false, 0, 0);
		assertTrue(((JLabel) plain).getText().endsWith(" match"), ((JLabel) plain).getText());
		assertEquals(Color.yellow, plain.getBackground());

		DefaultScrollTableCellRenderer quiet = new DefaultScrollTableCellRenderer();
		Component other = quiet.getTableCellRendererComponent(table, "cytosol", false, false, 1, 0);
		assertEquals("cytosol", ((JLabel) other).getText());
		assertFalse(Color.yellow.equals(other.getBackground()));
	}
}
