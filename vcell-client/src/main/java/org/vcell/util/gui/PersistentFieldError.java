package org.vcell.util.gui;

import javax.swing.JComponent;
import javax.swing.JLabel;

import cbit.vcell.client.constants.GuiConstants;

/**
 * A validation message that stays on the panel after the error dialog closes.
 * The words are the cue; a red border on the field is extra.
 */
public final class PersistentFieldError {

	private PersistentFieldError() {
	}

	public static void show(JLabel message, JComponent field, String text) {
		message.setText(text);
		message.setForeground(GuiConstants.ERROR_TEXT_COLOR);
		message.getAccessibleContext().setAccessibleName("Validation error");
		message.getAccessibleContext().setAccessibleDescription(text);
		if (field != null) {
			field.getAccessibleContext().setAccessibleDescription(text);
		}
	}

	public static void clear(JLabel message, JComponent field) {
		message.setText(" ");
		message.getAccessibleContext().setAccessibleDescription(null);
		if (field != null) {
			field.getAccessibleContext().setAccessibleDescription(null);
		}
	}
}
