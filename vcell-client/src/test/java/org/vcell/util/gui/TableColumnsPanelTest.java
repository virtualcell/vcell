package org.vcell.util.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Fast")
public class TableColumnsPanelTest {

	// model columns: 0 Path, 1 Name, 2 Description, 3 Expression, 4 Units
	private static JTable table() {
		return new JTable(new DefaultTableModel(new Object[] { "Path", "Name", "Description", "Expression", "Units" }, 2));
	}

	private static List<String> headers(JTable table) {
		List<String> headers = new ArrayList<>();
		for (int i = 0; i < table.getColumnCount(); i++) headers.add(table.getColumnName(i));
		return headers;
	}

	@Test
	public void startsInTheDefaultOrder() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			JTable table = TableColumnsPanelTest.table();
			new TableColumnsPanel(table, List.of(1, 3, 0, 2, 4));
			assertEquals(List.of("Name", "Expression", "Path", "Description", "Units"), TableColumnsPanelTest.headers(table));
			assertEquals("Name", table.getModel().getColumnName(table.convertColumnIndexToModel(0)), "the model keeps its own order");
		});
	}

	@Test
	public void hidesMovesAndResets() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			JTable table = TableColumnsPanelTest.table();
			TableColumnsPanel columns = new TableColumnsPanel(table, List.of(1, 3, 0, 2, 4));
			columns.setShown(2, false);
			assertEquals(List.of("Name", "Expression", "Path", "Units"), TableColumnsPanelTest.headers(table));
			columns.move(4, -1); // Units past the hidden Description, and before Path
			columns.move(4, -1);
			assertEquals(List.of("Name", "Expression", "Units", "Path"), TableColumnsPanelTest.headers(table));
			columns.setShown(2, true);
			assertEquals(List.of("Name", "Expression", "Units", "Path", "Description"), TableColumnsPanelTest.headers(table), "back in its place");
			columns.reset();
			assertEquals(List.of("Name", "Expression", "Path", "Description", "Units"), TableColumnsPanelTest.headers(table));
		});
	}

	@Test
	public void oneColumnAlwaysShows() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			JTable table = TableColumnsPanelTest.table();
			TableColumnsPanel columns = new TableColumnsPanel(table, List.of(0, 1, 2, 3, 4));
			for (int i = 0; i < 5; i++) columns.setShown(i, false);
			assertEquals(List.of("Units"), TableColumnsPanelTest.headers(table));
		});
	}

	@Test
	public void draggingAHeaderIsMovingTheColumn() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			JTable table = TableColumnsPanelTest.table();
			TableColumnsPanel columns = new TableColumnsPanel(table, List.of(1, 3, 0, 2, 4));
			columns.setShown(0, false); // Path hidden: Name, Expression, Description, Units
			table.getColumnModel().moveColumn(3, 0); // Units dragged to the front
			assertEquals(List.of(4, 1, 3, 2), columns.getShownOrder());
			columns.setShown(0, true);
			assertEquals(List.of("Units", "Name", "Expression", "Path", "Description"), TableColumnsPanelTest.headers(table), "Path where it was among them");
		});
	}
}
