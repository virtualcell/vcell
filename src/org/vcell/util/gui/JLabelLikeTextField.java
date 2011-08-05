/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class JLabelLikeTextField extends JTextField {
	public JLabelLikeTextField() {
		this(null);
	}
	public JLabelLikeTextField(String text) {
		super(text);
		setEditable(false);
		setBorder(null);
		setForeground(UIManager.getColor("Label.foreground"));
		setBackground(Color.white);
		setFont(UIManager.getFont("Label.font"));
	}
}
