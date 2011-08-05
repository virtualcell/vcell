/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JFrame;

import cbit.vcell.client.data.PDEDataViewer;

@SuppressWarnings("serial")
public class VFrapPDEDataViewer extends PDEDataViewer {
	public VFrapPDEDataViewer()
	{
		super();
	}

	@Override
	protected void showComponentInFrame(Component comp, String title) {
		final JFrame frame = new JFrame(title);
		frame.setLocation(new Point(300,300));
		frame.getContentPane().add(comp);
		frame.setPreferredSize(new Dimension(700,550));
		frame.pack();
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
	}
}
