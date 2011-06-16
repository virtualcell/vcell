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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

public class ButtonFormatter {

	public static JButton format(JButton button) {
		button.setContentAreaFilled(true);
		button.setBorderPainted(true);
		button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EtchedBorder()), new EmptyBorder(3,5,3,5)));
		return button;
	}
	
}
