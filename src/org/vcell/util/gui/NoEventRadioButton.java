package org.vcell.util.gui;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;

/**
 * JRadioButton which does not send event on {@link #setSelected(boolean)} to existing state
 * if in part of {@link ButtonGroupCivilized}
 */
@SuppressWarnings("serial")
public class NoEventRadioButton extends JRadioButton {

	public NoEventRadioButton() {
	}

	public NoEventRadioButton(Icon icon) {
		super(icon);
	}

	public NoEventRadioButton(Action a) {
		super(a);
	}

	public NoEventRadioButton(String text) {
		super(text);
	}

	public NoEventRadioButton(Icon icon, boolean selected) {
		super(icon, selected);
	}

	public NoEventRadioButton(String text, boolean selected) {
		super(text, selected);
	}

	public NoEventRadioButton(String text, Icon icon) {
		super(text, icon);
	}

	public NoEventRadioButton(String text, Icon icon, boolean selected) {
		super(text, icon, selected);
	}

	/**
	 * set without sending event if in {@link ButtonGroupCivilized} if
	 * button already in specified state
	 */
	@Override
	public void setSelected(boolean b) {
		if (b != isSelected()) {
			super.setSelected(b);
		}
	}
}
