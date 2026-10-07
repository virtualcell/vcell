package cbit.vcell.solver.ode.gui;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.JTextField;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class SolverOptionsValidationTest {

	@Test
	public void outputOptionsMessageStaysUntilTheFieldIsValid() {
		OutputOptionsPanel panel = new OutputOptionsPanel();
		JTextField field = new JTextField("1, 9");
		panel.reportFieldError(field, "Output times should be within [0.0,1.0].");
		assertTrue(panel.validationMessage().getText().contains("Output times"), panel.validationMessage().getText());
		assertEquals(panel.validationMessage().getText(), field.getAccessibleContext().getAccessibleDescription());
		assertTrue(panel.validationMessage().getParent() != null);

		panel.reportFieldError(field, null);
		assertEquals(" ", panel.validationMessage().getText());
		assertNull(field.getAccessibleContext().getAccessibleDescription());
	}

	@Test
	public void stochasticOptionsMessageStaysUntilTheFieldIsValid() {
		StochSimOptionsPanel panel = new StochSimOptionsPanel();
		JTextField field = new JTextField("nope");
		panel.reportFieldError(field, "Wrong number format for input string: \"nope\"");
		assertTrue(panel.validationMessage().getText().contains("Wrong number format"), panel.validationMessage().getText());
		assertTrue(panel.validationMessage().getParent() != null);

		panel.reportFieldError(field, null);
		assertEquals(" ", panel.validationMessage().getText());
		assertNull(field.getAccessibleContext().getAccessibleDescription());
	}
}
