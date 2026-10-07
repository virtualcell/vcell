package cbit.vcell.math.gui;

import cbit.image.DisplayAdapterService;
import cbit.vcell.client.constants.GuiConstants;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import javax.swing.JTextField;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class MeshSpecificationValidationTest {

	@Test
	public void invalidMeshSizeLeavesAMessageThatClears() {
		MeshSpecificationPanel panel = new MeshSpecificationPanel();
		JTextField field = new JTextField("abc");
		panel.reportFieldError(field, "Wrong number format for input string: \"abc\"");

		JLabel message = panel.validationMessage();
		assertTrue(message.getText().contains("Wrong number format"), message.getText());
		assertEquals(message.getText(), field.getAccessibleContext().getAccessibleDescription());
		assertTrue(message.getParent() != null);
		double ratio = DisplayAdapterService.contrastRatio(message.getForeground().getRGB(), message.getBackground().getRGB());
		assertTrue(ratio >= 4.5, ratio + ":1");
		assertEquals(GuiConstants.ERROR_TEXT_COLOR, message.getForeground());

		panel.reportFieldError(field, null);
		assertEquals(" ", message.getText());
		assertNull(field.getAccessibleContext().getAccessibleDescription());
	}
}
