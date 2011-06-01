/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model.gui;

import cbit.vcell.model.*;
/**
 * This type was created in VisualAge.
 */
public class FeatureDialogTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		javax.swing.JDesktopPane desktop = new org.vcell.util.gui.JDesktopPaneEnhanced();
		frame.setSize(500,500);
		frame.setContentPane(desktop);

		
		cbit.vcell.model.Model model = cbit.vcell.model.ModelTest.getExample();
		cbit.vcell.model.Feature feature = (Feature)model.getStructure("Cytosol");

		FeatureDialog aFeatureDialog = new FeatureDialog(frame);
		aFeatureDialog.setModel(model);
		aFeatureDialog.setChildFeature(feature);
		aFeatureDialog.setVisible(true);

		desktop.add(aFeatureDialog, aFeatureDialog.getName());
		frame.getContentPane().setLayout(new java.awt.BorderLayout());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);

	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of cbit.vcell.model.FeatureDialog");
		exception.printStackTrace(System.out);
	}
}
}
