package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.mapping.TaskCallbackMessage;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 8.3-h. Console severity lines carry a textual tag ([Error], [Warning], [Stopped]) and
 * the shared accessible severity colors, so severity never rests on hue alone
 * (SC 1.4.1 / 1.3.3). Notifications stay untagged plain text.
 */
@Tag("Fast")
public class SimulationConsolePanelAccessibilityTest {

	@Test
	public void severityLinesCarryTagAndAccessibleColor_8_3_h() throws Exception {
		AtomicReference<SimulationConsolePanel> panelRef = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() -> panelRef.set(new SimulationConsolePanel()));
		SimulationConsolePanel panel = panelRef.get();
		AtomicReference<String> textRef = new AtomicReference<>();
		AtomicReference<Color> errorColorRef = new AtomicReference<>();
		AtomicReference<Color> warningColorRef = new AtomicReference<>();
		AtomicReference<Color> stoppedColorRef = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() -> {
			panel.appendToConsole(new TaskCallbackMessage(TaskCallbackStatus.Error, "boom"));
			panel.appendToConsole(new TaskCallbackMessage(TaskCallbackStatus.Warning, "careful"));
			panel.appendToConsole(new TaskCallbackMessage(TaskCallbackStatus.TaskStopped, "halted"));
			panel.appendToConsole(new TaskCallbackMessage(TaskCallbackStatus.Notification, "plain notice"));
			StyledDocument doc = panel.accessibleConsoleText().getStyledDocument();
			try {
				textRef.set(doc.getText(0, doc.getLength()));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			String text = textRef.get();
			errorColorRef.set(foregroundAt(doc, text.indexOf("[Error]")));
			warningColorRef.set(foregroundAt(doc, text.indexOf("[Warning]")));
			stoppedColorRef.set(foregroundAt(doc, text.indexOf("[Stopped]")));
		});
		String text = textRef.get();
		assertTrue(text.startsWith("[Error] boom\n"), text);
		assertTrue(text.contains("[Warning] careful\n"), text);
		assertTrue(text.contains("[Stopped] halted\n"), text);
		assertTrue(text.contains("plain notice\n"), text);
		assertFalse(text.contains("[Notification]"), text);
		assertEquals(GuiConstants.ERROR_TEXT_COLOR, errorColorRef.get());
		assertEquals(GuiConstants.WARNING_TEXT_COLOR, warningColorRef.get());
		assertEquals(GuiConstants.ERROR_TEXT_COLOR, stoppedColorRef.get());
	}

	private static Color foregroundAt(StyledDocument doc, int offset) {
		Element element = doc.getCharacterElement(offset);
		return StyleConstants.getForeground(element.getAttributes());
	}
}
