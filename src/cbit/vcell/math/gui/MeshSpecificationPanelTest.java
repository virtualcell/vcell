/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math.gui;

import cbit.vcell.math.*;
import cbit.vcell.solver.*;
/**
 * This type was created in VisualAge.
 */
public class MeshSpecificationPanelTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		MeshSpecificationPanel aSpatialSpecificationPanel;
		aSpatialSpecificationPanel = new MeshSpecificationPanel();
		frame.add("Center", aSpatialSpecificationPanel);
		frame.setSize(aSpatialSpecificationPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		MathDescription mathDesc = MathDescriptionTest.getExample();
		Simulation sim = new Simulation(mathDesc);
		aSpatialSpecificationPanel.setMeshSpecification(sim.getMeshSpecification());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Panel");
		exception.printStackTrace(System.out);
	}
}
}
