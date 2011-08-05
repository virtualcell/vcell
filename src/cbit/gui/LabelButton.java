/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class LabelButton extends JButton {

	public LabelButton(Icon icon) {
		super(icon);
		initialize();
	}

	public LabelButton(String text, Icon icon) {
		super(text, icon);
		initialize();
	}

	public LabelButton(String text) {
		super(text);
		initialize();
	}
	
	private void initialize() {
		setContentAreaFilled(false);
		setBorder(BorderFactory.createEtchedBorder());
		setFocusable(false);
		setBorderPainted(false);
		setRolloverEnabled(true);		
	}
}
