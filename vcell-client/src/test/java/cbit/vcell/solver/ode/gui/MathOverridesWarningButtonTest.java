package cbit.vcell.solver.ode.gui;

import cbit.image.DisplayAdapterService;
import cbit.vcell.client.constants.GuiConstants;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class MathOverridesWarningButtonTest {

	@Test
	public void unusedOverrideButtonCarriesAWarningWord() {
		MathOverridesPanel panel = new MathOverridesPanel();
		JButton button = panel.unusedOverrideButton();
		assertTrue(button.getText().toLowerCase().contains("warning"), button.getText());
		assertEquals(GuiConstants.WARNING_TEXT_COLOR, button.getForeground());
		double ratio = DisplayAdapterService.contrastRatio(button.getForeground().getRGB(), button.getBackground().getRGB());
		assertTrue(ratio >= 4.5, ratio + ":1");
	}
}
