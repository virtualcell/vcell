package cbit.gui;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class TableCellEditorAutoCompletionTest {

	@Test
	public void invalidExpressionLeavesTheWordErrorUntilEditingStartsAgain() {
		JTable table = new JTable();
		TableCellEditorAutoCompletion editor = new TableCellEditorAutoCompletion(new TextFieldAutoCompletion(), table);
		String message = "unknown function foo\n\nUse 'Ctrl-Space' to see a list of available names in your model or 'Esc' to revert to the original expression.";
		editor.showExpressionError(message);

		JTextField component = (JTextField) editor.getComponent();
		assertTrue(component.getBorder() instanceof TitledBorder);
		assertEquals("error", ((TitledBorder) component.getBorder()).getTitle());
		assertEquals(message, component.getAccessibleContext().getAccessibleDescription());
		assertEquals(message, component.getToolTipText());
		assertEquals(message, editor.expressionError());

		editor.getTableCellEditorComponent(table, "1", false, 0, 0);
		assertNull(component.getBorder());
		assertNull(component.getAccessibleContext().getAccessibleDescription());
		assertNull(editor.expressionError());
	}
}
