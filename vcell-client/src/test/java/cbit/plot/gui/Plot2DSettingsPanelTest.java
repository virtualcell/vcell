package cbit.plot.gui;

import cbit.plot.Plot2DSettings;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class Plot2DSettingsPanelTest {

	@Test
	public void checkboxTextAndTwoWayBinding() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			Plot2DSettings settings = new Plot2DSettings();
			settings.setVaryLineStyles(false);
			Plot2DSettingsPanel panel = new Plot2DSettingsPanel();
			panel.setPlot2DSettings(settings);
			JCheckBox checkbox = find(panel, "JCheckBoxVaryLineStyles");
			assertNotNull(checkbox);
			assertEquals("Vary line styles", checkbox.getText());
			assertFalse(checkbox.isSelected());
			checkbox.setSelected(true);
			assertTrue(settings.getVaryLineStyles());
			settings.setVaryLineStyles(false);
			assertFalse(checkbox.isSelected());
		});
	}

	private static JCheckBox find(Container container, String name) {
		for (Component component : container.getComponents()) {
			if (component instanceof JCheckBox && name.equals(component.getName())) {
				return (JCheckBox) component;
			}
			if (component instanceof Container) {
				JCheckBox nested = find((Container) component, name);
				if (nested != null) {
					return nested;
				}
			}
		}
		return null;
	}
}
