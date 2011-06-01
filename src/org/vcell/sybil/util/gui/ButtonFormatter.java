/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.gui;

import java.awt.Insets;

import javax.swing.JButton;

public class ButtonFormatter {

	public static JButton format(JButton button) {
		button.setBorderPainted(true);
		button.setMargin(new Insets(2,2,2,2));
		return button;
	}
	
}
